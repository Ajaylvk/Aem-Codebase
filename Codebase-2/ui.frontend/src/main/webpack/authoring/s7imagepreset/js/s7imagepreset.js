/* eslint-disable */

/*
* Only display breakpoints checkbox if image preset is selected.
*
* */
if (window.Granite) {
    (function ($, $gAuthor, $document) {
        $(document).on('dialog-ready', () => {

            function toggle() {
                const $breakpointsNode = $('input[name="./disableBreakpoints"]').parents('.coral-Form-fieldwrapper');
                    $breakpointsNode.toggle($('[name="./s7PresetType"]:checked').val() === "image");
            }

            $('[name="./s7PresetType"]').on('click', toggle);

            setTimeout(function () {toggle()},500);
    });
    }(jQuery, Granite.author, jQuery(document)));
}
/* eslint-enable */
