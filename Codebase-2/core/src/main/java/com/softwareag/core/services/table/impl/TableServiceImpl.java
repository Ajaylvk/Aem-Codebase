package com.softwareag.core.services.table.impl;

import com.softwareag.core.models.table.Table;
import com.softwareag.core.models.table.TableCell;
import com.softwareag.core.models.table.TableRow;
import com.softwareag.core.services.table.TableAction;
import com.softwareag.core.services.table.TableActionResult;
import com.softwareag.core.services.table.TableActionType;
import com.softwareag.core.services.table.TableService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;
import java.util.Objects;

import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;


@Component(service = TableService.class, immediate = true)
public class TableServiceImpl implements TableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableServiceImpl.class);

    @Override
    public void handleAction(final TableActionResult actionResult, final TableAction action) {
        if (action == null) {
            actionResult.setFailureMessage("The argument 'action' must not be null.");
            return;
        }

        final TableActionType tableActionType = TableActionType.of(action.getActionType());
        if (tableActionType == null) {
            actionResult.setFailureMessage("Missing or Unknown action type.");
        } else if (action.getCurrentRowCell() == null) {
            final String errorMsg = String.format("TableCell could not be found for the provided currentCellPath '%s'.", action.getCellPath());
            actionResult.setFailureMessage(errorMsg);
        } else {
            LOGGER.info("handleAction attempting to invoke '{}'.", action.getActionType());

            try {
                actionResult.setMessage(String.format("Action '%s' handled succesfully.", action.getActionType()));
                switch (tableActionType) {
                    case ACTION_TYPE_ADD_LEFT_COLUMN:
                        addLeftColumn(actionResult, action);
                        break;
                    case ACTION_TYPE_ADD_RIGHT_COLUMN:
                        addRightColumn(actionResult, action);
                        break;
                    case ACTION_TYPE_DELETE_COLUMN:
                        deleteColumn(actionResult, action);
                        break;
                    case ACTION_TYPE_ADD_ROW_BELOW:
                        addRowBelow(actionResult, action);
                        break;
                    case ACTION_TYPE_ADD_ROW_ABOVE:
                        addRowAbove(actionResult, action);
                        break;
                    case ACTION_TYPE_DELETE_ROW:
                        deleteRow(actionResult, action);
                        break;
                    case ACTION_TYPE_MERGE_WITH_RIGHT:
                        mergeWithRight(actionResult, action);
                        break;
                    case ACTION_TYPE_MERGE_WITH_BELOW:
                        mergeWithBelow(actionResult, action);
                        break;
                    case ACTION_TYPE_SPLIT_HORIZONTALLY:
                        splitHorizontally(actionResult, action);
                        break;
                    case ACTION_TYPE_SPLIT_VERTICALLY:
                        splitVertically(actionResult, action);
                        break;
                    default:
                        final String errorMsg = String.format("Action type '%s' is not being handled yet.", action.getActionType());
                        actionResult.setFailureMessage(errorMsg);
                }

                // commit changes
                action.getResourceResolver().commit();
            } catch (final RepositoryException | PersistenceException e) {
                final String errorMsg = String.format("Action '%s' failed on '%s'.", action.getActionType(), action.getCurrentRow().getPath());

                LOGGER.error(errorMsg, e);
                actionResult.setFailureMessage(errorMsg);
            }
        }
    }

    @Override
    public void addLeftColumn(final TableActionResult actionResult, final TableAction action)
        throws PersistenceException, RepositoryException {
        final TableCell currentCell = action.getCurrentRowCell();
        final String currentRightColName = currentCell.getColName();

        addNewColumn(action, currentRightColName);
    }

    @Override
    public void addRightColumn(final TableActionResult actionResult, final TableAction action)
        throws PersistenceException, RepositoryException {
        final Table table = action.getTable();

        final List<String> uniqueColNames = table.getUniqueColNames();
        final TableCell currentCell = action.getCurrentRowCell();
        final int currentColIndex = uniqueColNames.indexOf(currentCell.getColName());
        final String currentRightColName;
        if (currentColIndex + 1 == uniqueColNames.size()) {
            currentRightColName = null;
        } else {
            currentRightColName = uniqueColNames.get(currentColIndex + 1);
        }

        addNewColumn(action, currentRightColName);
    }

    @Override
    public void deleteColumn(final TableActionResult actionResult, final TableAction action)
        throws PersistenceException {
        final Table table = action.getTable();
        final TableRow headRow = table.getHeadRow();

        // Don't allow deleting the last column
        if (headRow.getRowCells().size() == 1) {
            actionResult.setFailureMessage("The last column can not be deleted.");
            return;
        }

        final ResourceResolver resourceResolver = action.getResourceResolver();

        final TableCell currentCell = action.getCurrentRowCell();
        final String colNameToDelete = currentCell.getColName();

        deleteCell(resourceResolver, headRow, colNameToDelete);
        if (CollectionUtils.isNotEmpty(table.getBodyRows())) {
            for (final TableRow bodyRow : table.getBodyRows()) {
                deleteCell(resourceResolver, bodyRow, colNameToDelete);
            }
        }
    }

    @Override
    public void addRowAbove(final TableActionResult actionResult, final TableAction action)
        throws RepositoryException, PersistenceException {
        final Table table = action.getTable();
        final TableRow headRow = table.getHeadRow();

        // Don't allow adding row above header
        final TableRow currentRow = action.getCurrentRow();
        if (currentRow == headRow) {
            actionResult.setFailureMessage("No row can be added above the header.");
            return;
        }

        final ResourceResolver resourceResolver = action.getResourceResolver();
        final Session session = resourceResolver.adaptTo(Session.class);

        final String rowSuffix = String.valueOf(System.currentTimeMillis());
        final String newRowPath = table.getNewRowPath(rowSuffix);
        if (!Objects.requireNonNull(session).itemExists(newRowPath)) {
            final Node newRowNode = JcrUtils.getOrCreateByPath(newRowPath, NT_UNSTRUCTURED, session);

            // order new row node before the desired row node
            final Node bodyNode = newRowNode.getParent();
            bodyNode.orderBefore(newRowNode.getName(), currentRow.getName());

            addCells(resourceResolver, table, newRowPath);
        }
    }

    @Override
    public void addRowBelow(final TableActionResult actionResult, final TableAction action)
        throws RepositoryException, PersistenceException {
        final Table table = action.getTable();
        final TableRow headRow = table.getHeadRow();
        final List<TableRow> bodyRows = table.getBodyRows();

        final TableRow currentRow = action.getCurrentRow();

        final ResourceResolver resourceResolver = action.getResourceResolver();
        final Session session = resourceResolver.adaptTo(Session.class);

        final String rowSuffix = String.valueOf(System.currentTimeMillis());
        final String newRowPath = table.getNewRowPath(rowSuffix);
        if (!Objects.requireNonNull(session).itemExists(newRowPath)) {
            final Node newRowNode = JcrUtils.getOrCreateByPath(newRowPath, NT_UNSTRUCTURED, session);

            // order new row node before the desired row node
            final Node bodyNode = newRowNode.getParent();
            if (currentRow == headRow) {
                if (CollectionUtils.isNotEmpty(bodyRows)) {
                    bodyNode.orderBefore(newRowNode.getName(), bodyRows.get(0).getName());
                }
            } else {
                final int currentRowIndex = bodyRows.indexOf(currentRow);
                if ((currentRowIndex + 1) < bodyRows.size()) {
                    final TableRow beforeRow = bodyRows.get(currentRowIndex + 1);
                    bodyNode.orderBefore(newRowNode.getName(), beforeRow.getName());
                }
            }

            addCells(resourceResolver, table, newRowPath);
        }
    }

    @Override
    public void deleteRow(final TableActionResult actionResult, final TableAction action)
        throws PersistenceException {
        final TableRow currentRow = action.getCurrentRow();

        // Don't allow deleting the header row
        if (currentRow == action.getTable().getHeadRow()) {
            actionResult.setFailureMessage("Header row can not be deleted.");
            return;
        }

        final ResourceResolver resourceResolver = action.getResourceResolver();
        final Resource currentRowResource = currentRow.getResource();
        resourceResolver.delete(currentRowResource);
    }

    @Override
    public void mergeWithRight(final TableActionResult actionResult, final TableAction action) {
        // not implemented yet
    }

    @Override
    public void mergeWithBelow(final TableActionResult actionResult, final TableAction action) {
        // not implemented yet
    }

    @Override
    public void splitHorizontally(final TableActionResult actionResult, final TableAction action) {
        // not implemented yet
    }

    @Override
    public void splitVertically(final TableActionResult actionResult, final TableAction action) {
        // not implemented yet
    }

    private Resource createTableCell(final ResourceResolver resourceResolver, final String newCellPath)
        throws PersistenceException {
        return ResourceUtil.getOrCreateResource(resourceResolver, newCellPath,
            TableCell.getResourceType(), NT_UNSTRUCTURED, false);
    }

    private void addCells(final ResourceResolver resourceResolver, final Table table, final String newRowPath)
        throws PersistenceException {
        final List<String> uniqueColNames = table.getUniqueColNames();
        if (CollectionUtils.isNotEmpty(uniqueColNames)) {
            for (final String uniqueColName : uniqueColNames) {
                final String newCellPath = PathUtils.concat(newRowPath, uniqueColName);
                createTableCell(resourceResolver, newCellPath);
            }
        }
    }

    private void deleteCell(final ResourceResolver resourceResolver, final TableRow row, final String colName)
        throws PersistenceException {
        if (row == null || CollectionUtils.isEmpty(row.getRowCells()) || StringUtils.isBlank(colName)) {
            return;
        }

        for (final TableCell cell : row.getRowCells()) {
            if (colName.equals(cell.getColName())) {
                resourceResolver.delete(cell.getResource());
            }
        }
    }

    private void addNewColumn(final TableAction action, final String currentRightColName)
        throws RepositoryException, PersistenceException {
        final Table table = action.getTable();
        final ResourceResolver resourceResolver = action.getResourceResolver();

        final String cellSuffix = String.valueOf(System.currentTimeMillis());
        addCell(resourceResolver, table.getHeadRow(), currentRightColName, cellSuffix);
        if (CollectionUtils.isNotEmpty(table.getBodyRows())) {
            for (final TableRow bodyRow : table.getBodyRows()) {
                addCell(resourceResolver, bodyRow, currentRightColName, cellSuffix);
            }
        }
    }

    private void addCell(final ResourceResolver resourceResolver, final TableRow row,
                         final String currentRightColName, final String cellSuffix)
        throws PersistenceException, RepositoryException {
        if (row == null || CollectionUtils.isEmpty(row.getRowCells())) {
            return;
        }

        final Table table = row.getTable();
        final String newCellPath = table.getNewCellPath(row, cellSuffix);
        final Resource newCellResource = createTableCell(resourceResolver, newCellPath);
        if (StringUtils.isNotBlank(currentRightColName)) {
            final Node newCellNode = newCellResource.adaptTo(Node.class);
            for (final TableCell cell : row.getRowCells()) {
                if (currentRightColName.equals(cell.getColName())) {
                    Objects.requireNonNull(newCellNode).getParent().orderBefore(newCellNode.getName(), cell.getName());
                    break;
                }
            }
        }
    }

}
