import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';
import wait from '../../../../core/js/utils/wait';
import { desktop, headerTablet } from '../../../../core/js/utils/global-variables';


class Linklist extends AEM.Component {

    constructor(element) {
        super(element);

        this.name = 'Link list';

        this.desktop = desktop;
        this.headerTablet = headerTablet;
        this.compName = this.constructor.name;
        this._animationTime = 300; // [in milliseconds] keep in sync with css animation-time
        this.selectors = {
            head: '.list__head',
            items: '.list__linklist',
        };
        this.states = {
            isSingleItem: 'list--single-item',
            isMobileOnly: 'list--mobile-only',
            open: 'list__container--open',
            closing: 'list__container--closing',
        };
        this.$listHead = null;
    }

    init() {
        this.$listHead = $(this.element).find(this.selectors.head);
        this.$listItems = $(this.element).find(this.selectors.items);

        this._attachHandlers();
    }

    _attachHandlers() {
        this.$listHead.on('click', () => this._toggle());
    }

    _toggle() {
    // on click of 2nd level list item in mobile view..toggle gets triggered

        if ($(this.element).parent().hasClass(this.states.isMobileOnly) && !this._isTablet()) { // changed isMobile to isTablet
            return;
        }


         // for new header mobile view
        if (this._isTablet()) {
            if ($(this.element).closest('ul').hasClass('headerv1__nav-2-level')) {
                this._hideOtherLists(this.element);
            }
        }


        if ($(this.element).parent().hasClass(this.states.isSingleItem)) {
            this._closeOtherLists();
        }
        if (!$(this.element).hasClass(this.states.open)) {
            this._open($(this.element));
        } else {
            this._close($(this.element));
        }
    }

    // adding header__nav-3-list--active will make other list hidden (nvaigation scss)
    _hideOtherLists(item) {
        item.closest(this.states.isSingleItem).addClass('headerv1__nav-3-list--active');
    }

    _close(item) {
        item.addClass(this.states.closing);
        this._setHeightOnItemContent(item, '0px');
        wait(this._animationTime).then(() => {
            item.removeClass([this.states.open, this.states.closing]);
        });
    }

    _open(item) {
        this._setHeightOnItemContent(item, this._getHeight(item));
        item.addClass(this.states.open);
    }

    _closeOtherLists() {
        const openLists = $(this.element).parent().siblings().find(`.${this.states.open}`);
        if (openLists.length > 0) {
            openLists.each((_, item) => this._close($(item)));
        }
    }

    _setHeightOnItemContent(item, height) {
        if (item.find(this.selectors.items).length > 0) {
            item.find(this.selectors.items).get(0).setAttribute('style', `height: ${height};`);
        }
    }

    _getHeight(item) {
        if (!item.find(this.selectors.items).get(0)) {
            return '0px';
        }
        item.find(this.selectors.items).get(0).setAttribute('style', 'display: block;');
        const height = `${item.find(this.selectors.items).get(0).scrollHeight}px`;
        item.find(this.selectors.items).get(0).setAttribute('style', 'display: none;');
        return height;
    }

    _isMobile() {
        return window.screen.width <= this.desktop;
    }

    // Added this to check screen width less than tablet
    _isTablet() {
        return window.screen.width <= this.headerTablet;
    }
}

AEM.registerComponent('.list', Linklist);
