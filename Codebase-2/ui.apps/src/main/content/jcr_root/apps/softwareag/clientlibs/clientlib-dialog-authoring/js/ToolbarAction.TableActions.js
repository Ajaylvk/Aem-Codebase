(function ($, ns, channel, window) {
    "use strict";
    var popover;
    var EditorFrame = ns.EditorFrame;
    var ACTION_ICON = "tableEdit";
    var ACTION_TITLE = "Table Action";
    var ACTION_NAME = "TABLE_ACTION";

    var HEAD_NN = "head";
    var BODY_NN = "body";

    var actions = {
        DELETE_COLUMN: 'deleteColumn',
        ADD_ROW_ABOVE: 'addRowAbove',
        DELETE_ROW: 'deleteRow',
    };

    var selectors = {
        'editableToolbar' : '#EditableToolbar',
        'tableActionButton': '.table-actionButton'
    }

    /**
     * Represents the TABLE_ACTION action (opens a popover relative to the toolbar of the selected editable) that could be performed on the current {@link -> Granite.author.Editable}
     *
     * @memberOf -> Granite.author.edit.ToolbarActions
     * @type -> Granite.author.ui.ToolbarAction
     * @alias -> TABLE_ACTION
     */

    var tableAction = new ns.ui.ToolbarAction({
        name: ACTION_NAME,
        text: Granite.I18n.get(ACTION_TITLE),
        icon: ACTION_ICON,
        render: function ($el) {
            if (popover) {
                popover.target = $el[0];
            }

            resetActions();

            // Hide actions when initializing. Hide deleteColumn when only 1 column left.
            if(getColsCount() === 1) {
                hideActions(actions.DELETE_COLUMN);
            }

            if(getCellType().isHeadCell){
                hideActions(actions.ADD_ROW_ABOVE);
                hideActions(actions.DELETE_ROW);
            }

            if(getCellType().isBodyCell){
                $(selectors.editableToolbar).find('button[data-action="EDIT"]').hide();
            }

            return $el;
        },
        execute: function () {
            // just ensure we don't close the toolbar
            return false;
        },
        condition: function (editable) {
            return editable.type === "softwareag/components/content/tableitem";
        },
        isNonMulti: true
    });

    channel.on('cq-layer-activated', function (event) {
        if (event.layer === "Edit") {
            EditorFrame.editableToolbar.registerAction(ACTION_NAME, tableAction);
        }
    });

    /**
     * bind event to table action button and popover
     *
     */
    (function(){
        popover = document.getElementById('tableActionButton');

        // Click handler on Table-related buttons
        $(document).on('click', selectors.tableActionButton, popover, function () {
            var actionString = $(this).attr('data-action');

            // Hide popover
            $(popover.target).trigger('click');

            // Init request to BE
            doRequest(actionString);
        });
    })();

    /**
     * Get the number of currently visible cells (horizontally).
     *
     */
    function getColsCount() {
        var $activeNode = getActiveNode();
        var $headNodes = $activeNode.parents('[data-type="Editable"]:first').find('[data-path*="head"]')
        return $headNodes.length;
    }

    /**
     * Hide the action that has the data-action attribute set as actionName param.
     * @para String actionName
     */
    function hideActions(actionName) {
        $(popover).find(selectors.tableActionButton + '[data-action="' + actionName + '"]').hide();
    }

    /**
     * Show all actions.
     */
    function resetActions() {
        $(selectors.editableToolbar).find('button[data-action="EDIT"]').show();
        $(popover).find(selectors.tableActionButton).show();
    }

    /**
     * Get currently selected cell (after click on action).
     *
     */
    function getActiveNode() {
        return $(document).find('[data-type="Editable"].is-selected');
    }


    /**
     * This returns an object to determine if the activeNode is a body or head cell.
     * Use getCellType().isHeadCell to check if its a Head Cell.
     *
     */
    function getCellType () {
        var $node = getActiveNode();
        var dataPath = $node.attr("data-path");
        var headIndex = dataPath.indexOf('/' + HEAD_NN);
        var bodyIndex = dataPath.indexOf('/' + BODY_NN);

        return { isHeadCell: headIndex > -1, isBodyCell: bodyIndex > -1}
    }
    /**
     * Resolve component related properties, e.g, table path, relative cell path, etc.
     *
     */
    function resolveComponentInfo() {
        var activeCell = getActiveNode();
        var dataPath = activeCell.attr("data-path");
        var headIndex = dataPath.indexOf('/' + HEAD_NN);
        var bodyIndex = dataPath.indexOf('/' + BODY_NN);

        var cellPath = dataPath;
        var componentPath = dataPath;
        if (headIndex > -1) {
            cellPath = dataPath.substring(headIndex + 1);
            componentPath = dataPath.substring(0, headIndex);
        } else if (bodyIndex > -1) {
            cellPath = dataPath.substring(bodyIndex + 1);
            componentPath = dataPath.substring(0, bodyIndex);
        }

        return {"cellPath": cellPath, "componentPath": componentPath};
    }

    /**
     * Get currently selected cell (after click on action).
     *
     */
    function doRequest(userAction) {
        var componentInfo = resolveComponentInfo();
        var cellPath = componentInfo.cellPath;
        var componentPath = componentInfo.componentPath;
        var endpointUrl = Granite.HTTP.externalize(componentPath + '.performAction.json');

        var data = {
            actionType: userAction,
            cellPath: cellPath,
        }

        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: endpointUrl,
            data: JSON.stringify(data),
            success() {
                // Reload component after successful response.
                location.reload();
            },
            error() {
            },
        });
    }

}(jQuery, Granite.author, jQuery(document), this));
