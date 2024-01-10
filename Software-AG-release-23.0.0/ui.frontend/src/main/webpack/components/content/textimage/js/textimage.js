/*
  eslint lines-between-class-members: ["error", "never"]
*/
import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class TextImage extends AEM.Component {

    constructor(element) {
        super(element);

        this.name = 'Text image';
        this.digitalDataTrackable = true;
        this.compName = this.constructor.name;

        this.title = null;
        this.$titleComponent = null;

        /**
         * @private
         * @type {jQuery}
         */
        this.toggleButton = $(element).find('.js-button-toggle');

        /**
         * @private
         * @type {jQuery}
         */
        this.compName = this.constructor.name;
    }
    init() {
        this.$titleComponent = $('h3', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';

        this.attachHandlers();
    }
    attachHandlers() {
        if (this.toggleButton) {
            this.toggleButton.on('click', () => this.toggleImage());
        }
    }
    toggleImage() {
        $(this.element).find('.textimage__image-container').slideToggle();
    }
}

AEM.registerComponent('.textimage', TextImage);
