package com.softwareag.core.services.table;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum TableActionType {

    ACTION_TYPE_ADD_LEFT_COLUMN("addLeftColumn"),
    ACTION_TYPE_ADD_RIGHT_COLUMN("addRightColumn"),
    ACTION_TYPE_DELETE_COLUMN("deleteColumn"),
    ACTION_TYPE_ADD_ROW_ABOVE("addRowAbove"),
    ACTION_TYPE_ADD_ROW_BELOW("addRowBelow"),
    ACTION_TYPE_DELETE_ROW("deleteRow"),
    ACTION_TYPE_MERGE_WITH_RIGHT("mergeWithRight"),
    ACTION_TYPE_MERGE_WITH_BELOW("mergeWithBelow"),
    ACTION_TYPE_SPLIT_HORIZONTALLY("splitHorizontally"),
    ACTION_TYPE_SPLIT_VERTICALLY("splitVertically");

    private final String actionType;
    private static List<String> actionTypes;

    TableActionType(final String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

    public static TableActionType of(final String actionType) {
        if (StringUtils.isBlank(actionType)) {
            return null;
        }

        for (final TableActionType tableActionType : TableActionType.values()) {
            if (actionType.equals(tableActionType.getActionType())) {
                return tableActionType;
            }
        }

        return null;
    }

    public static List<String> getActionTypes() {
        if (actionTypes == null) {
            actionTypes = new ArrayList<>();
            for (final TableActionType tableActionType : TableActionType.values()) {
                actionTypes.add(tableActionType.getActionType());
            }
        }
        return Collections.unmodifiableList(actionTypes);
    }

}
