(function ($, window, document) {
    "use strict";

    $(document).on("dialog-ready", function () {

        $('coral-multifield').each(function() {

            checkMultifieldValidity($(this));
        });

        $('coral-multifield').on('change', function() {

            checkMultifieldValidity($(this))
        });
    });

    function checkMultifieldValidity(multifield) {

        const maxItems = $(multifield).attr('data-maxitems');
        const itemsNumber = $(multifield).children('coral-multifield-item').length;
        const addButton = $(multifield).children('button');
        const maxItemsValid = checkMaxItems(maxItems, itemsNumber);

        if (!maxItemsValid) {

            $(addButton).attr('disabled','disabled')

        } else {

            $(addButton).removeAttr('disabled')
        }
    }

    function checkMaxItems(maxNumber, itemsNumber) {

        let valid = true;

        if ((itemsNumber && maxNumber) && (itemsNumber >= maxNumber)) {

            valid = false;
        }

        return valid;
    }

})($, window, document);