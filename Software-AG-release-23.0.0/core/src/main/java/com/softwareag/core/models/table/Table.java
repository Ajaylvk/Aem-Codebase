package com.softwareag.core.models.table;

import com.day.cq.wcm.api.WCMMode;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Table {

    public static final String RESOURCE_TYPE = "softwareag/components/content/table";

    public static final String HEAD_NN = "head";
    public static final String BODY_NN = "body";

    public static final String ROW_NAME_PREFIX = "row_";
    public static final String COL_NAME_PREFIX = "cell_";

    @Self
    @ScriptVariable
    protected Resource resource;

    @ScriptVariable
    private SlingHttpServletRequest request;

    @ValueMapValue
    private String design;

    @ValueMapValue
    private String columnWidth;

    @ValueMapValue
    private String anchorName;

    private WCMMode wcmMode;
    private String path;

    private TableRow headRow;
    private List<TableRow> bodyRows;

    @PostConstruct
    void init() {
        if (request != null) {
            this.wcmMode = WCMMode.fromRequest(request);
            if (resource == null) {
                resource = request.getResource();
            }
        }
        this.path = resource.getPath();

        initHeadRow();
        initBodyRows();
    }

    public Resource getResource() {
        return resource;
    }

    public String getDesign() {
        return design;
    }

    public String getColumnWidth() {
        return columnWidth;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public String getPath() {
        return path;
    }

    public TableRow getHeadRow() {
        return headRow;
    }

    public List<TableRow> getBodyRows() {
        if (CollectionUtils.isEmpty(bodyRows)) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(bodyRows);
        }
    }

    public String getBodyPath() {
        return PathUtils.concat(path, BODY_NN);
    }

    public String getNewRowPath(final String rowSuffix) {
        return PathUtils.concat(getBodyPath(), ROW_NAME_PREFIX + rowSuffix);
    }

    public String getNewCellPath(final TableRow tableRow, final String suffix) {
        return PathUtils.concat(tableRow.getPath(), COL_NAME_PREFIX + suffix);
    }

    public List<String> getUniqueColNames() {
        if (headRow == null && CollectionUtils.isEmpty(bodyRows)) {
            return Collections.emptyList();
        }

        final TableRow row;
        if (headRow != null) {
            row = headRow;
        } else {
            row = bodyRows.get(0);
        }

        final List<String> orderedUniqueColNames = new ArrayList<>();
        for (final TableCell cell : row.getRowCells()) {
            final String colName = cell.getColName();
            if (!orderedUniqueColNames.contains(colName)) {
                orderedUniqueColNames.add(colName);
            }
        }
        return orderedUniqueColNames;
    }

    private void initHeadRow() {
        headRow = initTableRow(resource.getChild(HEAD_NN));
    }

    private void initBodyRows() {
        final Resource bodyResource = resource.getChild(BODY_NN);
        if (bodyResource != null && bodyResource.hasChildren()) {
            bodyRows = new ArrayList<>();
            bodyResource.getChildren().forEach(childResource -> {
                final TableRow tableRow = initTableRow(childResource);
                if (tableRow != null) {
                    bodyRows.add(tableRow);
                }
            });
        } else {
            bodyRows = Collections.emptyList();
        }
    }

    private TableRow initTableRow(final Resource rowResource) {
        if (rowResource == null) {
            return null;
        }

        final TableRow row = rowResource.adaptTo(TableRow.class);
        if (row != null) {
            row.setTable(this);
            row.setWcmMode(wcmMode);

            assignColumnWidths(row);
        }
        return row;
    }

    private void assignColumnWidths(final TableRow tableRow) {
        final String[] colWidths;
        if (StringUtils.isBlank(columnWidth)) {
            colWidths = new String[0];
        } else {
            colWidths = columnWidth.split(",");
        }

        final List<TableCell> rowCells = tableRow.getRowCells();
        int totalWidth = 0;
        for (int i = 0; i < rowCells.size(); i++) {
            final TableCell rowCell = rowCells.get(i);
            if (i < colWidths.length && isPositiveInteger(colWidths[i])) {
                rowCell.setColWidth(colWidths[i]);
            } else {
                rowCell.setColWidth(TableCell.DEFAULT_COL_WIDTH);
            }
            totalWidth += Integer.parseInt(rowCell.getColWidth());
        }

        // assign percentage values on column widths
        for (int i = 0; i < rowCells.size(); i++) {
            final TableCell rowCell = rowCells.get(i);
            final int colWidth = Integer.parseInt(rowCell.getColWidth());
            // rounding is done by int casting
            final int colWidthPercentage;

            if (totalWidth != 0) {
                colWidthPercentage = (int) ((100.0 * colWidth) / totalWidth);
            } else {
                colWidthPercentage = (int) ((100.0 * colWidth) / rowCells.size());
            }
            rowCell.setColWidthPercentage(colWidthPercentage);
        }
    }

    private static boolean isPositiveInteger(final String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }

        boolean positiveInteger = true;
        for (char c : str.trim().toCharArray()) {
            if (!Character.isDigit(c)) {
                positiveInteger = false;
                break;
            }
        }
        return positiveInteger;
    }

}

