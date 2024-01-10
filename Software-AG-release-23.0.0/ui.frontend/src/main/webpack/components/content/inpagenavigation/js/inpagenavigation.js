import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Inpagenavigation extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.name = 'Inpage navigation';

        this.selectors = {
            inNavPrevBtn: '.prev-item',
            inNavNextBtn: '.next-item',
            iNavBox: '.inpagenavigation__container',
            inNavItem: '.inpagenavigation-item',
            inNavItemLink: '.inpagenavigation-item a',
            headerElement: '.header',
        };
    }

    init() {
        this.$inNavPrevBtn = $(this.selectors.inNavPrevBtn, this.element);
        this.$inNavNextBtn = $(this.selectors.inNavNextBtn, this.element);
        this.$iNavBox = $(this.element);
        this.$iNavBoxWidth = $(this.element).innerWidth();
        this.$inNavItemLink = $(this.selectors.inNavItemLink, this.element);
        this.$headerElement = $(this.selectors.headerElement);

        let itemsWidth = 0;// total width of all items
        const allItems = $(this.selectors.inNavItem, this.element);
        let countItem = 0;

        for (countItem = 0; countItem < allItems.length; countItem++) {
            itemsWidth += $(allItems[countItem]).width();
        }

        if (itemsWidth > this.$iNavBox.width()) {
            this.$iNavBox.addClass('scroll-right');
        } else {
            this.$iNavBox.removeClass('scroll-right');
        }

        this._attachEvents();
    }

    _attachEvents() {
        this.$iNavBox.on('scroll', event => this._handleNavScroll(event));
        this.$inNavNextBtn.on('click', event => this._inNavMoveNext(event));
        this.$inNavPrevBtn.on('click', event => this._inNavMovePrev(event));
        this.$inNavItemLink.on('click', event => this._jumpToSection(event));
    }

    _handleNavScroll() {
        if (this.$iNavBox.scrollLeft() > 1) {
            this.$iNavBox.addClass('scroll-left');
        } else {
            this.$iNavBox.removeClass('scroll-left');
        }

        if (this.$iNavBox.scrollLeft() + this.$iNavBoxWidth + 2 >= this.$iNavBox[0].scrollWidth) {
            this.$iNavBox.removeClass('scroll-right');
        } else {
            this.$iNavBox.addClass('scroll-right');
        }
    }

    _inNavMoveNext() {
        this.$iNavBox.animate({
            scrollLeft: '+=75px',
        }, '700');
    }

    _inNavMovePrev() {
        this.$iNavBox.animate({
            scrollLeft: '-=75px',
        }, '700');
    }

    _jumpToSection(event) {
        event.preventDefault();
        event.stopPropagation();

        const elementSelector = $(event.target).attr('href');
        const elementTarget = $(elementSelector);
        const headerHeight = this.$headerElement.height();

        if (elementTarget.length) {
            const positionParent = elementTarget.parent();
            const positionParentTop = positionParent.offset().top;

            $('html, body').animate({
                scrollTop: positionParentTop - headerHeight,
            }, '300');
        }
    }

}

AEM.registerComponent('.inpagenavigation', Inpagenavigation);
