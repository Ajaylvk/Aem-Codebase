import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class RegionChanger extends AEM.Component {

    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.$regionButton = $('.region__button', this.element);
        this.$regionButtonClose = $('.region__options-close', this.element);
        this.$regionItemHolder = $('.region_items-holder', this.element);

        this.selectors = {
            button: '.region-items__button',
            content: '.region-items__content-container',
        };
        this.states = {
            open: 'region-items--open',
            active: 'region__holder--active',
        };
        this.$regionItemButton = null;
        this.$regionItemContent = null;
    }

    init() {
        if (this.$regionButton) {
            this.$regionButton.unbind().on('click', () => this.showRegionList());
        }

        if (this.$regionButtonClose) {
            this.$regionButtonClose.unbind().on('click', () => this.closeRegionList());
        }

        this.$regionItemButton = $(this.element).find(this.selectors.button);
        this.$regionItemContent = $(this.element).find(this.selectors.content);

        this.$regionItemButton.unbind().on('click', event => this._toggle(event));
    }

    _toggle(event) {
        const clickedButton = $(event.currentTarget);
        const clickedContainer = clickedButton.closest('.region-items__container');

        if (clickedContainer.hasClass(this.states.open)) {
            clickedContainer.removeClass(this.states.open);
        } else {
            $(`.${this.states.open}`).removeClass(this.states.open);
            clickedContainer.addClass(this.states.open);
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

AEM.registerComponent('.regionchanger', RegionChanger);
