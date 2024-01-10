if (window.Granite) {
    (function rte(window, $) {
    /**
    * Rich-Text Editor Max Length Validation
    *
    * @class RichTextMaxLengthValidation
    * @classdesc registers a new validator to the foundation-registry service focused on the
    * cq/gui/components/authoring/dialog/richtext component.
    *
    * Usage: the attribute maxlength to the richtext component, example: maxlength="100"
    */
        const RichTextMaxLengthValidation = (function rteMaxlength() {
            const CONST = {
                TARGET_GRANITE_UI: '.coral-RichText-editable',
                ERROR_MESSAGE: 'Your text length is {0} but character limit is {1}!',
            };
        /**
         * Initializes the RichTextMaxLengthValidation
         */
            function init() {
            // register the validator which includes the validate algorithm
                $(window).adaptTo('foundation-registry').register('foundation.validation.validator', {
                    selector: CONST.TARGET_GRANITE_UI,
                    validate(el) {
                        const $rteField = $(el);
                        const $field = $rteField.closest('.richtext-container').find('input.coral-Form-field');
                        const maxLength = $field.data('maxlength');
                        const textLength = $rteField.text().trim().length;
                        if (maxLength && textLength > maxLength) {
                            return Granite.I18n.get(CONST.ERROR_MESSAGE, [textLength, maxLength]);
                        }
                        return null;
                    },
                });
            // execute Jquery Validation onKeyUp
                $(document).on('keyup', CONST.TARGET_GRANITE_UI, function jqueyValidationForText() {
                    executeJqueryValidation($(this));
                });
            }
        /**
         * Execute foundation.validation.validator's validate algorithm.
         */
            function executeJqueryValidation(el) {
                const validationApi = el.adaptTo('foundation-validation');
                if (validationApi) {
                    validationApi.checkValidity();
                    validationApi.updateUI();
                }
            }
            return {
                init,
            };
        }());
        RichTextMaxLengthValidation.init();
    }(window, Granite.$));
    (function wordlimit($) {
        $(window).adaptTo('foundation-registry').register('foundation.validation.validator', {
            selector: '.custom-word-limit',
            validate(element) {
                const elementValue = element.value;
                const wordLimit = element.dataset.wordLimit;
                if (elementValue && wordLimit && elementValue.split(' ').filter(n => n !== '').length > wordLimit) {
                    return `Word limit for the field is ${wordLimit} !`;
                }
                return null;
            },
        });
    }(Granite.$));
}
