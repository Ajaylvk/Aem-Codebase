(function ($, $document) {
    "use strict";
    var COMPONENTS_LIMIT = 1;
    var CUSTOM_COLUMN_COMPONENT_1 = "/configuration/jcr:content/parsys/footer/regionorlanguagechangerparsys";
    var CUSTOM_COLUMN_COMPONENT_2 = "/jcr:content/assetcomponent/mediaorpdfparsys";
    var CUSTOM_COLUMN_COMPONENT_3 = "/configuration/jcr:content/parsys/footer/textparsys"
    //TO DO: Once Pdf component is ready, please add it to  ALLOWED_COMPONENTS list
    var ALLOWED_COMPONENTS = ["/apps/softwareag/components/content/regionchanger",
        "/apps/softwareag/components/content/languagechanger",
        "/apps/softwareag/components/content/media",
        "/apps/softwareag/components/content/pdfviewer",
        "/apps/softwareag/components/content/text"];
    function getDesignPath(editable) {
        var parsys = editable.getParent(),
            designSrc = parsys.config.designDialogSrc,
            result = {}, param;
        if (designSrc === undefined) {
            return undefined;
        }
        designSrc = designSrc.substring(designSrc.indexOf("?") + 1);
        designSrc.split(/&/).forEach(function (it) {
            if (_.isEmpty(it)) {
                return;
            }
            param = it.split("=");
            result[param[0]] = param[1];
        });
        if (result.referrer === undefined) {
            return undefined;
        }
        return decodeURIComponent(result.referrer);
    }
    function showErrorAlert(message, title) {
        var fui = $(window).adaptTo("foundation-ui"),
            options = [{
                text: "OK",
                warning: true
            }];
        message = message || "Unknown Error";
        title = title || "Error";
        fui.prompt(title, message, "error", options);
    }
    function getChildEditables(parsys) {
        var editables = Granite.author.edit.findEditables(),
            children = [], parent;
        _.each(editables, function(editable){
            parent = editable.getParent();
            if(parent && (parent.path === parsys.path)){
                children.push(editable);
            }
        });
        return children;
    }
    function isWithinLimit(editable){
        var path = getDesignPath(editable),
            children = getChildEditables(editable.getParent()),
            isWithin = true, currentLimit = "";
        isWithin = children.length <= COMPONENTS_LIMIT;
        if(!isWithin){
            showErrorAlert("Max Limit exceeded, allowed " + COMPONENTS_LIMIT + " component only");
        }
        return isWithin;
    }
    function isWithinList(comp_path){
        var isWithin = true;
        if ( jQuery.inArray(comp_path, ALLOWED_COMPONENTS ) == -1){
            isWithin = false;
        }
        if(!isWithin){
            showErrorAlert("Component <b>" + comp_path + "</b> is not allowed");
        }
        return isWithin;
    }
    function extendComponentDrop() {
        var dropController = Granite.author.ui.dropController,
            compDragDrop;
        if (dropController !== undefined) {
            compDragDrop = dropController.get(Granite.author.Component.prototype.getTypeName());
            if (compDragDrop !== undefined) {
                compDragDrop.handleDrop = function(dropFn){
                    return function (event) {
                        var targetEditable = event.currentDropTarget.targetEditable;
                        var parPath = targetEditable.path;
                        console.log("parPath" + parPath);
                        if (parPath && (parPath.indexOf(CUSTOM_COLUMN_COMPONENT_1) != -1
                            || (parPath.indexOf(CUSTOM_COLUMN_COMPONENT_2) != -1)
                            || (parPath.indexOf(CUSTOM_COLUMN_COMPONENT_3) != -1))) {
                            if (!isWithinLimit(targetEditable) || !isWithinList(event.path)) {
                                return;
                            }
                        }
                        return dropFn.call(this, event);
                    };
                }(compDragDrop.handleDrop);
            }
            //------------------------------------------------
            Granite.author.Component.prototype.beforeInsert = function(openDlgFn){
                return function (defaultInsertFunction, editable) {
                    var parsysPath = editable.path;
                    var compPath = this.componentConfig.path;
                    if (parsysPath &&
                        (parsysPath.indexOf(CUSTOM_COLUMN_COMPONENT_1) != -1
                            || parsysPath.indexOf(CUSTOM_COLUMN_COMPONENT_2) != -1
                            || parsysPath.indexOf(CUSTOM_COLUMN_COMPONENT_3) != -1)
                        && !(isWithinLimit(editable.getChildren()[0]) && isWithinList(compPath))) {
                        return false;
                    }
                };
            }( Granite.author.Component.prototype.beforeInsert)
            //-------------------------------------------------
            //handle insert action
            Granite.author.edit.actions.openInsertDialog = function(openDlgFn){
                return function (editable) {
                    if(!isWithinLimit(editable)){
                        return;
                    }
                    return openDlgFn.call(this, editable);
                };
            }(Granite.author.edit.actions.openInsertDialog);
            //handle paste action
            var insertAction = Granite.author.edit.Toolbar.defaultActions.INSERT;
            insertAction.handler = function(insertHandlerFn){
                return function(editableBefore, param, target) {
                    var parsysPath = editableBefore.path;
                    if (parsysPath &&
                        (parsysPath.indexOf(CUSTOM_COLUMN_COMPONENT_1) != -1
                            || parsysPath.indexOf(CUSTOM_COLUMN_COMPONENT_2) != -1
                            || parsysPath.indexOf(CUSTOM_COLUMN_COMPONENT_3) != -1) &&
                        !isWithinLimit(editableBefore)) {
                        return;
                    }
                    return insertHandlerFn.call(this, editableBefore, param, target);
                };
            }(insertAction.handler);
        }
    }
    $(function() {
        if (Granite && Granite.author && Granite.author.edit && Granite.author.Component &&
            Granite.author.ui && Granite.author.ui.dropController) {
            extendComponentDrop();
        }
    });
}(jQuery, jQuery(document)));
