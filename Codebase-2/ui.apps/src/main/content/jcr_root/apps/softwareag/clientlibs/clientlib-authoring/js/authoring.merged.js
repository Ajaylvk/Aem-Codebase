/* eslint-disable */
if (window.Granite) {
    (function ($, $gAuthor, $document) {

        let registry = $(window).adaptTo("foundation-registry");

        registry.register("foundation.validation.validator", {
            selector: "[data-validation=imageAlternativeText]",
            validate: function (element) {

                let valid = true;
                let errorMessage = "";

                switch ($(element).attr('name')) {

                    case './additional/altText':
                        valid = validateImageFields("./additional/image", "./additional/altText");
                        break;

                    case './solutionBox1/altText':
                        valid = validateImageFields("./solutionBox1/image", "./solutionBox1/altText");
                        break;

                    case './solutionBox2/altText':
                        valid = validateImageFields("./solutionBox2/image", "./solutionBox2/altText");
                        break;

                    case './logoAltText':
                        valid = validateImageFields("./image", "./logoAltText");
                        break;

                    case './iconAltText':
                        valid = validateImageFields("./icon", "./iconAltText");
                        break;

                    case './blogAltText':
                        valid = validateImageFields("./image", "./blogAltText");
                        break;

                    default:
                        if ($(element).is(":visible")) {
                            valid = validateImageFields("./fileReference", "./altText");
                        }
                        break;
                }

                if (!valid) {
                    errorMessage = "Please fill out this field.";
                }

                return errorMessage;
            }
        });

        // Use this if you want to make another field mandatory after the current field is filled.
        registry.register("foundation.validation.validator", {
            selector: "[data-validation=siblingDependence]",
            validate: function (element) {

                let valid = true;
                let errorMessage = "";

                switch ($(element).attr('name')) {

                    case './link/href':
                        valid = validateImageFields("./link/text", "./link/href");
                        break;

                    default:
                        break;
                }

                if (!valid) {
                    errorMessage = "Please fill out this field.";
                }

                return errorMessage;
            }
        });

        registry.register("foundation.validation.validator", {
            selector: "[data-foundation-validation=imageLinkDestinationPath]",
            validate: function (element) {

                let errorMessage = "";
                let valid = validateImageFields("./additional/image", "./additional/link/href");

                if (!valid) {
                    errorMessage = "Please fill out this field.";
                }

                // Run Granite field validation methods to
                // update field and dialog UI accordingly.
                setTimeout(function() {
                    $(element).checkValidity();
                    $(element).updateErrorUI();
                }, 1000);

                return errorMessage;
            }
        });

        function validateImageFields(imageField, altTextfield) {

            let valid = true;
            const imageValid = validateField(imageField);

            if (imageValid) {
                valid = validateField(altTextfield);
            }

            return valid;
        }

        function validateField(fieldName) {
            return !!getFieldValue(fieldName);
        }

        function getFieldValue(fieldName) {
            let $node = $('input[name*="' + fieldName + '"]');
            if ($node.length > 0) {
                return $node.val();
            }
        }
    }
    (jQuery, Granite.author, jQuery(document)));
}
/* eslint-enable */

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

/* eslint-disable */
if (window.Granite) {
    (function ($, $gAuthor, $document) {
        $(document).on('foundation-contentloaded', () => {
            /*console.log($gAuthor);
            console.log($document);
            console.log($document);*/
        });
    }(jQuery, Granite.author, jQuery(document)));
}
/* eslint-enable */

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
