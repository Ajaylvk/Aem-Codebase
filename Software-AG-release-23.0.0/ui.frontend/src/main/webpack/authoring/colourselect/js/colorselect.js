/* eslint-disable */
if (window.Granite) {
    (function ($, $gAuthor, $document) {

        $(document).on('dialog-ready', function (event) {

            $('.cq-dialog-background-color-select').each(function () {
                updateColorsHandler(this);
            });

            $('.cq-dialog-background-color-select').change(function () {
                updateColorsHandler(this);
            });
        });

        function updateColorsHandler(element) {
            if ($(element).is("coral-select")) {
                Coral.commons.ready(element, function (component) {
                    updateColors(component);
                    updateSelected(component);
                })
            }
        }

        function updateColors(element) {
            $(element).each(function(idx, select){
                select.items.getAll().forEach(function(item, idx){
                    $(item).find('coral-icon').addClass(item.value);
                    $($(element).find('coral-selectlist-item').get(idx)).find('coral-icon').addClass(item.value);
                    if(item.value === element.selectedItem.value){
                        item.selected = true;
                    }
                });
            });
        }

        function updateSelected(element) {
            $(element).find('.coral3-Select-label').find('coral-icon').addClass(element.selectedItem.value);
        }
    }
    (jQuery, Granite.author, jQuery(document)));
}
/* eslint-enable */
