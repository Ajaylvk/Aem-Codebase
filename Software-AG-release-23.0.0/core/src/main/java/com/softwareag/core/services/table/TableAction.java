package com.softwareag.core.services.table;

import com.softwareag.core.models.table.Table;
import com.softwareag.core.models.table.TableCell;
import com.softwareag.core.models.table.TableRow;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;

public class TableAction {
    private Resource tableResource;
    private String actionType;
    private String cellPath;

    // Resolved params
    private Table table;
    private TableRow currentRow;
    private TableCell currentRowCell;

    public Resource getTableResource() {
        return tableResource;
    }

    public void setTableResource(final Resource tableResource) {
        this.tableResource = tableResource;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(final String actionType) {
        this.actionType = actionType;
    }

    public String getCellPath() {
        return cellPath;
    }

    public void setCellPath(final String cellPath) {
        this.cellPath = cellPath;
    }

    public TableRow getCurrentRow() {
        return currentRow;
    }

    public TableCell getCurrentRowCell() {
        return currentRowCell;
    }

    public ResourceResolver getResourceResolver() {
        return tableResource.getResourceResolver();
    }

    public void resolveTableProperties() {
        if (table != null || tableResource == null) {
            return;
        }
        table = tableResource.adaptTo(Table.class);
        if (table != null) {
            resolveCurrentCell(table.getHeadRow());
            if (currentRowCell == null) {
                resolveCurrentCell(table.getBodyRows());
            }
        }
    }

    public Table getTable() {
        if (table != null) {
            return table;
        }
        resolveTableProperties();
        return table;
    }

    private boolean resolveCurrentCell(final TableRow tableRow) {
        if (tableRow == null || CollectionUtils.isEmpty(tableRow.getRowCells()) || StringUtils.isBlank(cellPath)) {
            return false;
        }

        for (final TableCell rowCell : tableRow.getRowCells()) {
            if (cellPath.equals(rowCell.getCellPath()) || cellPath.equals(rowCell.getPath())) {
                currentRow = tableRow;
                currentRowCell = rowCell;
                return true;
            }
        }
        return false;
    }

    private boolean resolveCurrentCell(final List<TableRow> tableRows) {
        if (CollectionUtils.isNotEmpty(tableRows)) {
            for (final TableRow tableRow : tableRows) {
                if (resolveCurrentCell(tableRow)) {
                    return true;
                }
            }
        }
        return false;
    }

}
