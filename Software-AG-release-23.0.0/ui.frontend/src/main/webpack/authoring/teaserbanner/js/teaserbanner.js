/* eslint-disable */
/*
* Enable/Disable drop-down menu for choosing boxColor background color.
*
* */
if (window.Granite) {
    (function ($, $gAuthor, $document) {
        $(document).on('dialog-ready', () => {

            function updateBoxColorStatus() {
                var $_checkbox = $('input[name="./enableBoxColor"]');
                var $_select = $('coral-select[name="./boxColor"]');

                if($_checkbox.prop("checked") == true){
                    $_select.prop("disabled", false);
                }
                else {
                    $_select.prop("disabled", true);
                }
            }

            $('input[name="./enableBoxColor"]').on('click', function(){
               updateBoxColorStatus();
            });

            setTimeout(function () {updateBoxColorStatus()},500);
        });
    }(jQuery, Granite.author, jQuery(document)));
}
/* eslint-enable */
