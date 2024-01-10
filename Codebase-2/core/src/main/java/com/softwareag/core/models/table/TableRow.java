package com.softwareag.core.models.table;

import com.day.cq.wcm.api.WCMMode;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TableRow {

    @Self
    protected Resource resource;

    private Table table;
    private String path;
    private String name;
    private List<TableCell> rowCells;
    private WCMMode wcmMode;

    @PostConstruct
    void init() {
        this.path = resource.getPath();
        this.name = resource.getName();

        if (resource.hasChildren()) {
            rowCells = new ArrayList<>();
            resource.getChildren().forEach(childResource -> {
                final TableCell tableCell = initTableCell(childResource);
                if (tableCell != null) {
                    rowCells.add(tableCell);
                }
            });
        } else {
            this.rowCells = Collections.emptyList();
        }
    }

    private TableCell initTableCell(final Resource cellResource) {
        if (cellResource == null) {
            return null;
        }

        final TableCell tableCell = cellResource.adaptTo(TableCell.class);
        if (tableCell != null) {
            tableCell.setTableRow(this);
        }
        return tableCell;
    }

    public Resource getResource() {
        return resource;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public List<TableCell> getRowCells() {
        if (CollectionUtils.isEmpty(rowCells)) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(rowCells);
        }
    }

    public WCMMode getWcmMode() {
        return wcmMode;
    }

    public void setWcmMode(final WCMMode wcmMode) {
        this.wcmMode = wcmMode;
        if (CollectionUtils.isNotEmpty(rowCells)) {
            rowCells.forEach(c -> c.setWcmMode(wcmMode));
        }
    }

    public Table getTable() {
        if (table == null) {
            final Resource parentResource = resource.getParent();
            if (Table.HEAD_NN.equals(resource.getName())) {
                table = Objects.requireNonNull(parentResource).adaptTo(Table.class);
            } else if (Table.BODY_NN.equals(Objects.requireNonNull(parentResource).getName())) {
                table = Objects.requireNonNull(parentResource.getParent()).adaptTo(Table.class);
            }
        }

        return table;
    }

    public void setTable(final Table table) {
        this.table = table;
    }

}
