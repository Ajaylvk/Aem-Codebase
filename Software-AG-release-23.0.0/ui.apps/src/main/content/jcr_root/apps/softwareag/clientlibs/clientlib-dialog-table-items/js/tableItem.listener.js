(function ($, Granite, ns, $document) {
    "use strict";

    function getCellType () {
        var $node = $(document).find('[data-type="Editable"].is-selected');
        var dataPath = $node.attr("data-path");

        var headIndex = dataPath.indexOf('/' + "head");
        var bodyIndex = dataPath.indexOf('/' + "body");

        return { isHeadCell: headIndex > -1, isBodyCell: bodyIndex > -1}
    }

    $document.on("foundation-contentloaded", function (e) {

        if(getCellType().isHeadCell){

              Coral.commons.ready($("coral-select[name='./backgroundStyle']"), function () {
                        $("coral-select[name='./backgroundStyle']").parent().hide();

               });

               Coral.commons.ready(e, function (component) {
                   $(".tabitems coral-tablist coral-tab")[1].hide();
               });
        }

    });
}(jQuery, Granite, Granite.author, jQuery(document)));
