import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';
/* stylelint-disable */
class LanguageChanger extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.$regionButton = $('.region__button', this.element);
        this.$regionButtonClose = $('.region__options-close', this.element);

        this.states = {
            open: 'region-items--open',
            active: 'region__holder--active',
        };
    }

    init() {
        if (this.$regionButton) {
            this.$regionButton.unbind().on('click', () => this.showRegionList());
        }

        if (this.$regionButtonClose) {
            this.$regionButtonClose.unbind().on('click', () => this.closeRegionList());
        }
    }

    showRegionList() {
        $(this.element).find('.region__holder').toggleClass(this.states.active);
        $(document.body).toggleClass('static-region');
    }

    closeRegionList() {
        $(this.element).find('.region__holder').removeClass(this.states.active);
        $(document.body).removeClass('static-region');
    }
}
/* stylelint-enable */
AEM.registerComponent('.languagechanger', LanguageChanger);
