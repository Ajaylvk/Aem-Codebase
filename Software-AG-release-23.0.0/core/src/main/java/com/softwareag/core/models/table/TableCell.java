package com.softwareag.core.models.table;

import com.day.cq.wcm.api.WCMMode;
import com.softwareag.core.models.tableitem.TableItem;
import com.softwareag.core.services.table.TableActionType;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TableCell {

    private static final String DATA_ATTRIBUTE_CELL_PATH = "data-cellpath";
    private static final String DATA_ATTRIBUTE_ACTION_TYPES = "data-actiontypes";

    public static final String RESOURCE_TYPE = TableItem.RESOURCE_TYPE;
    public static final String DEFAULT_COL_WIDTH = "1";
    public static final int DEFAULT_COL_WIDTH_PERCENTAGE = 25;

    @Self
    protected Resource resource;

    private String path;
    private String name;
    private String cellPath;
    private String colWidth = DEFAULT_COL_WIDTH;
    private int colWidthPercentage = DEFAULT_COL_WIDTH_PERCENTAGE;
    private WCMMode wcmMode;
    private TableRow tableRow;
    private String backgroundStyle;

    @PostConstruct
    void init() {
        this.path = resource.getPath();
        this.name = resource.getName();
        this.backgroundStyle = Objects.requireNonNull(resource.adaptTo(TableItem.class)).getBackgroundStyle();

        final Resource tableContainerResource = getTableContainerResource();
        if (tableContainerResource != null) {
            final String tableResourcePath = tableContainerResource.getPath();
            this.cellPath = this.path.substring(tableResourcePath.length() + 1);
        }
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public static String getResourceType() {
        return RESOURCE_TYPE;
    }

    public Resource getResource() {
        return resource;
    }

    public String getPath() {
        return path;
    }

    /**
     * Returns node name.
     *
     * @return Node name of the cell.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns extracted col name.
     *
     * @return Coll name of the cell by extracting the {@link Table#COL_NAME_PREFIX}_milliseconds part.
     */
    public String getColName() {
        if (name.indexOf('_') == name.lastIndexOf('_')) {
            return name;
        }

        // additional suffix might have been added as a result of split or merge operations.
        final String[] nameParts = name.split("_");
        return nameParts[0] + "_" + nameParts[1];
    }

    public String getCellPath() {
        return cellPath;
    }

    public String getColWidth() {
        return colWidth;
    }

    public void setColWidth(final String colWidth) {
        this.colWidth = colWidth;
    }

    public int getColWidthPercentage() {
        return colWidthPercentage;
    }

    public void setColWidthPercentage(final int colWidthPercentage) {
        this.colWidthPercentage = colWidthPercentage;
    }

    public String getColWidthPercentageStr() {
        return colWidthPercentage + "%";
    }

    public void setWcmMode(final WCMMode wcmMode) {
        this.wcmMode = wcmMode;
    }

    public Map<String, Object> getDataAttributes() {
        if (wcmMode != WCMMode.EDIT) {
            return Collections.emptyMap();
        }

        final Map<String, Object> attrMap = new HashMap<>();
        attrMap.put(DATA_ATTRIBUTE_CELL_PATH, cellPath);
        attrMap.put(DATA_ATTRIBUTE_ACTION_TYPES, TableActionType.getActionTypes());

        return Collections.unmodifiableMap(attrMap);
    }

    public TableRow getTableRow() {
        if (tableRow == null) {
            tableRow = Objects.requireNonNull(resource.getParent()).adaptTo(TableRow.class);
        }
        return tableRow;
    }

    public void setTableRow(final TableRow tableRow) {
        this.tableRow = tableRow;
    }

    private Resource getTableContainerResource() {
        Resource tmpResource = resource;
        while (tmpResource != null) {
            if (tmpResource.isResourceType(Table.RESOURCE_TYPE)) {
                return tmpResource;
            }
            tmpResource = tmpResource.getParent();
        }
        return null;
    }
}
