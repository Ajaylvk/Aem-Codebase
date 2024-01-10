import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';
import toggleNoScrollClass from '../../../../core/js/utils/no-scroll';
import debounce from '../../../../core/js/utils/debounce';
import { desktop } from '../../../../core/js/utils/global-variables';

class Header extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.desktop = desktop;

        this.selectors = {
            nav: '.header__nav',
            firstLevel: '.header__nav-1-level',
            firstLevelItem: '.header__nav-1-level-item--has-children',
            secondLevelItemTitle: '.header__nav-2-level-item-title',
            closeButton: '.header__nav-2-close',
            backButton: '.header__nav-2-back',
            icons: '.header__icons',
            burgerMenuButton: '.header__icons-menu',
            searchIcon: '.header__icons-search',
            searchIconOpen: '.header__icons-search-open',
            searchForm: '.header__search-form',
            searchFormInput: '.header__search-form-input',
            searchFormSubmit: '.header__search-form-submit',
        };
        this.states = {
            sticky: 'header--sticky',
            navActive: 'header__nav--active',
            firstLevelItemActive: 'header__nav-1-level-item--active',
            secondLevelItemNoChildren: 'header__nav-2-level-item--no-children',
            searchFormActive: 'header__search-form--active',
            searchSubmitShow: 'header__search-form-submit--show',
        };
    }

    init() {
        this.animationTime = 150;

        this.$nav = $(this.selectors.nav, this.element);
        this.$firstLevel = $(this.selectors.firstLevel, this.element);
        this.$burgerMenuButton = $(this.selectors.burgerMenuButton, this.element);
        this.$closeButton = $(this.selectors.closeButton, this.element);
        this.$backButton = $(this.selectors.backButton, this.element);
        this.$secondLevelItemTitle = $(this.selectors.secondLevelItemTitle, this.element);
        this.$searchIcon = $(this.selectors.searchIcon, this.element);
        this.$searchIconOpen = $(this.selectors.searchIconOpen, this.element);
        this.$searchForm = $(this.selectors.searchForm, this.element);
        this.$searchFormInput = $(this.selectors.searchFormInput, this.element);
        this.$searchFormSubmit = $(this.selectors.searchFormSubmit, this.element);

        const vh = window.innerHeight * 0.01;
        document.documentElement.style.setProperty('--vh', `${vh}px`);

        window.addEventListener('resize', () => {
            document.documentElement.style.setProperty('--vh', `${vh}px`);
        });


        this._attachHandlers();
    }

    _attachHandlers() {
        if (this.$firstLevel) {
            this.$firstLevel.on('click', event => this._handleFirstLevelClick(event));
            this.$firstLevel.on('keydown', event => this._handleFirstLevelClick(event));
        }
        if (this.$secondLevelItemTitle) {
            this.$secondLevelItemTitle.on('click', event => this._handleSecondLevelItemClick(event));
        }
        if (this.$burgerMenuButton) {
            this.$burgerMenuButton.on('click', () => this._handleBurgerMenuClick());
        }
        if (this.$closeButton) {
            this.$closeButton.on('click', () => this._handleCloseButtonClick());
        }
        if (this.$backButton) {
            this.$backButton.on('click', () => this._handleBackButtonClick());
        }
        if (this.$searchIconOpen) {
            this.$searchIconOpen.on('click', () => this._handleSearchIconOpenClick());
        }
        if (this.$searchFormInput) {
            this.$searchFormInput.on('focusin', () => this._handleSearchFormInputFocusIn());
            this.$searchFormInput.on('focusout', event => this._handleSearchFormInputFocusOut(event));
            this.$searchFormInput.on('keyup', () => this._handleSearchFormInputKeypress());
        }
        if (this.$searchFormSubmit) {
            this.$searchFormSubmit.on('focusout', () => this._handleSearchFormSubmitFocusOut());
        }

        $(document).on('keydown', event => this._handleEscapePress(event));
        $(window).on('resize', () => this._handleWindowResize());

        const handleScroll = debounce(() => {
            this._toggleSticky();
        }, 10, true);
        window.addEventListener('scroll', handleScroll);
    }

    _toggleSticky() {
        $(this.element).parent().toggleClass(this.states.sticky, window.scrollY !== 0);
    }

    _handleFirstLevelClick(event) {
        if (this._firstLevelHasNoChildren(event) || this.constructor._isKeyboardControl(event)) {
            return;
        }
        this.$firstLevel.children().each((_, firstLevelItem) => {
            if (firstLevelItem === event.target) {
                $(firstLevelItem, this.element).toggleClass(this.states.firstLevelItemActive);
            } else {
                $(firstLevelItem, this.element).removeClass(this.states.firstLevelItemActive);
            }
        });
        toggleNoScrollClass(this._firstLevelHasActiveItem());
    }

    _handleSecondLevelItemClick(event) {
        // on mobile/tablet we don't want to follow the link if we have 3th level items,
        // instead we just want to open the accordion (handled by linklist.js)
        if (this._isMobile() && this._hasThirdLevelItems(event)) {
            event.preventDefault();
        }
    }

    _handleBurgerMenuClick() {
        this.$nav.toggleClass(this.states.navActive);
        toggleNoScrollClass();
        this.$firstLevel.children().first().focus();
        this.$burgerMenuButton.children().toggle(this.animationTime);
        this.$searchIcon.toggle(this.animationTime);
    }

    _handleCloseButtonClick() {
        this._removeAllFirstLevelActiveClasses();
        toggleNoScrollClass(false);
    }

    _handleBackButtonClick() {
        this._removeAllFirstLevelActiveClasses();
        this.$firstLevel.children().first().focus();
    }

    _handleSearchIconOpenClick() {
        this.$searchIcon.children().toggle(this.animationTime);
        this.$searchForm.addClass(this.states.searchFormActive);
        this._setWidthOfSearchForm();
        this.$searchFormInput.focus();
    }

    _handleSearchFormInputFocusIn() {
        this.$searchForm.addClass(this.states.searchFormActive);
        this.$searchFormSubmit.attr('tabindex', 0);
        this.$searchFormInput.attr('tabindex', 0);
    }

    _handleSearchFormInputFocusOut(event) {
        // we don't want to close the search-overlay if the focus changes
        // from input to submit-button because the website might be navigated with a keyboard
        if (this._isFocusChangeToSubmitButton(event)) {
            return;
        }
        this._closeSearchOverlay();
    }

    _handleSearchFormSubmitFocusOut() {
        this._closeSearchOverlay();
    }

    _handleSearchFormInputKeypress() {
        const showSearchFormSubmit = this.$searchFormInput.val().length > 0;
        this.$searchFormSubmit.toggleClass(this.states.searchSubmitShow, showSearchFormSubmit);
    }

    _handleEscapePress(event) {
        if (event.key !== 'Escape') {
            return;
        }
        this._removeAllFirstLevelActiveClasses();
        this.$firstLevel.children().first().focus();
    }

    _handleWindowResize() {
        this._removeAllFirstLevelActiveClasses();
        this.$searchForm.css('width', '');
    }

    _removeAllFirstLevelActiveClasses() {
        this.$firstLevel.children().each((_, firstLevelItem) => {
            $(firstLevelItem, this.element).removeClass(this.states.firstLevelItemActive);
        });
    }

    _closeSearchOverlay() {
        this.$searchIcon.children().toggle(this.animationTime);
        this.$searchFormInput.val('');
        this._handleSearchFormInputKeypress();
        this.$searchForm.removeClass(this.states.searchFormActive);
        this.$searchFormSubmit.attr('tabindex', -1);
        this.$searchFormInput.attr('tabindex', -1);
        this.$firstLevel.children().first().focus();
    }

    _setWidthOfSearchForm() {
        this.$searchForm.css('width', `${this._calcWidthOfHeader()}px`);
    }

    _calcWidthOfHeader() {
        return $(this.element).width() - $(this.selectors.icons, this.element).width();
    }

    _isMobile() {
        return window.screen.width <= this.desktop;
    }

    _isFocusChangeToSubmitButton(ev) {
        return ev.relatedTarget && ev.relatedTarget.isEqualNode(this.$searchFormSubmit.get(0));
    }

    static _isKeyboardControl(event) {
        return event.key !== undefined && event.key !== 'Enter' && event.key !== ' ';
    }

    _firstLevelHasActiveItem() {
        return this.$firstLevel.children().filter(`.${this.states.firstLevelItemActive}`).length >= 1;
    }

    _firstLevelHasNoChildren(event) {
        return !$(event.target).hasClass(this.selectors.firstLevelItem.slice(1));
    }

    _hasThirdLevelItems(event) {
        return !$(event.target).parents().hasClass(this.states.secondLevelItemNoChildren);
    }
}

AEM.registerComponent('.header', Header);
