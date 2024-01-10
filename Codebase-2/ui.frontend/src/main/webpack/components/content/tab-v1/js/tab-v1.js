import PubSub from '../../../../core/js/utils/pubsub';
import AEM from '../../../../core/js/AemComponent';

class TabV1 extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.name = 'Tab V1';
        this.digitalDataTrackable = true;

        this.selectors = {
            container: '.tab-V1__container',
            itemList: '.tab__item-list',
            button: '.tab__button',
            contentList: '.tab__content-list',
            content: '.tab__content',
            arrowLeft: '.tab__arrow--left',
            arrowRight: '.tab__arrow--right',
        };
        this.states = {
            buttonActive: 'tab__button--active',
            contentActive: 'tab__content--active',
        };
    }

    init() {
        this.$container = $(this.element).parent().find(this.selectors.container);
        this.$itemList = $(this.element).find(this.selectors.itemList);
        this.$buttons = $(this.element).find(this.selectors.button);
        this.$contentList = $(this.element).find(this.selectors.contentList);
        this.$arrowLeft = $(this.element).find(this.selectors.arrowLeft);
        this.$arrowRight = $(this.element).find(this.selectors.arrowRight);

        this.$buttons.on('click', event => this._handleTabClick(event));
        this.$arrowLeft.on('click', () => this._handleLeftArrowClick());
        this.$arrowRight.on('click', () => this._handleRightArrowClick());

        this.isScrolling = undefined;
        this.$itemList.on('scroll', () => this._handleScroll());
        $(window).on('resize', () => this._handleWindowResize());

        // if "scroll-to-tab" event is triggered then call
        PubSub.subscribe('scroll-to-tab', this.__captureTabId.bind(this));

        // necessary since backend can't provide the markup structure we need
        this._moveContentMarkup();
        if ($('#sag__content').attr('data-wcmmode') == 'wcmmode') {
            const activeItem = localStorage.getItem('active-tab');
            if (activeItem === null || activeItem === '') {
                this._setFirstTabActive();
            } else {
                let idIndex_1 = -1;
                if (
                    this.$buttons[0].innerText.toLowerCase().trim() !=
          activeItem.toLowerCase().trim()
                ) {
                    const countChildren_1 = $('.tab__item-list').children().length;
                    for (let i = 0; i < countChildren_1; i++) {
                        if (
                            this.$buttons[i].innerText.toLowerCase().trim() ===
              activeItem.toLowerCase().trim()
                        ) {
                            idIndex_1 = i;
                        }
                    }
                    if (idIndex_1 == -1) {
                        idIndex_1 = 0;
                    }
                    const buttonIndex_1 = idIndex_1;
                    if (buttonIndex_1 > -1) {
                        const $item_1 = this.$buttons.eq(buttonIndex_1);
                        if ($item_1.length > 0) {
                            this._setActiveButton($item_1);
                            this._setActiveContent($item_1);

                            $('html, body').animate(
                                {
                                    scrollTop: $item_1.offset().top - 90,
                  //  scrollTop: (scrollEl.offset().top - 100),
                                },
                                500
                            );
                        }
                        localStorage.setItem(
                            'active-tab',
                            this.$buttons[idIndex_1].innerText
                        );
                    }
                } else {
                    this._setInitialTab();
                }
            }
        } else {
            if (!document.location.hash) {
                localStorage.setItem('active-tab', this.$buttons[0].innerText);
            }
            this._setInitialTab();
        }
        window.addEventListener('hashchange', () => {
            this._checkUrlHasId();
        });
        this.$arrowLeft.hide();
        if (this._hasMoreSpaceToScroll()) {
            this.$arrowRight.hide();
        }
    }

    __captureTabId(id) {
        this._checkUrlHasId(id);
    }

    _handleTabClick(event) {
        const $item = $(event.target);
        this._setActiveButton($item);
        this._setActiveContent($item);
        document.location.hash = $item[0].id;
        if ($item[0].id === null || $item[0].id === undefined || $item[0].id === '') {
            $('html, body').animate({
                scrollTop: ($item.offset().top - 90),
            }, 500);
        }
        localStorage.setItem('active-tab', event.target.textContent);
    }

    _handleScroll() {
        // Clear our timeout throughout the scroll
        window.clearTimeout(this.isScrolling);

        // Set a timeout to run after scrolling ends
        this.isScrolling = setTimeout(() => this._toggleArrowVisibility(), 66);
    }

    _handleWindowResize() {
        this._toggleArrowVisibility();
    }

    _handleLeftArrowClick() {
        this.$itemList.animate({
            scrollLeft: '-=100px',
        });
    }

    _handleRightArrowClick() {
        this.$itemList.animate({
            scrollLeft: '+=100px',
        });
    }

    _moveContentMarkup() {
        this.$contentList.append($(this.element).find(this.selectors.content));
    }

    _toggleArrowVisibility() {
        const showArrowLeft = this.$itemList.scrollLeft() > 0;
        const showArrowRight = !this._hasMoreSpaceToScroll();
        this.$arrowLeft.toggle(showArrowLeft);
        this.$arrowRight.toggle(showArrowRight);
    }

    _setActiveButton($item) {
        this.$buttons.removeClass(this.states.buttonActive);
        $item.addClass(this.states.buttonActive);
    }

    _setActiveContent($item) {
        $(this.element).find(this.selectors.content).removeClass(this.states.contentActive);
        this.$contentList.children().eq($item.index()).addClass(this.states.contentActive);
    }

    _setInitialTab() {
        const activeItem = localStorage.getItem('active-tab');
        const hasHash = this._checkUrlHasId();

        if (hasHash) return;

        if (activeItem === null || activeItem === '') {
            this._setFirstTabActive();
            return;
        }
        const $item = this.$buttons.filter(`:contains(${activeItem.toLowerCase().trim()})`);
        if ($item.length > 0) {
            this._setActiveButton($item);
            this._setActiveContent($item);
            return;
        }
        this._setFirstTabActive();
    }

    _setFirstTabActive() {
        const $firstTab = this.$buttons.first();
        this._setActiveButton($firstTab);
        this._setActiveContent($firstTab);
        localStorage.setItem('active-tab', $firstTab.text());
    }

    _hasMoreSpaceToScroll() {
        const scrollPosition = this.$itemList.width() + this.$itemList.scrollLeft();
        return Math.floor(this._getWidthOfItems()) <= Math.floor(scrollPosition);
    }

    _getWidthOfItems() {
        const itemsArray = this.$buttons.toArray();
        return itemsArray.reduce((acc, item) => acc + $(item).outerWidth(true), 0);
    }

    _checkUrlHasId(id) {
        const urlHash = id || document.location.hash.substring(1, document.location.hash.length);
        let outOfloop = false;
        let idIndex;
        const countChildren = $('.tab__item-list').children().length;
        if (urlHash !== '' && urlHash !== null && document.location.hash !== '') {
            for (let i = 0; i < countChildren; i++) {
                if (this.$buttons[i].id.toLowerCase() === urlHash.toLowerCase()) idIndex = i;
            }
            const buttonIndex = idIndex;
            if (buttonIndex > -1) {
                const $item = this.$buttons.eq(buttonIndex);
                if ($item.length > 0) {
                    this._setActiveButton($item);
                    this._setActiveContent($item);
                    outOfloop = true;

                    $('html, body').animate({
                        scrollTop: ($item.offset().top - 90),
                        //  scrollTop: (scrollEl.offset().top - 100),
                    }, 500);
                }
                localStorage.setItem('active-tab', this.$buttons[idIndex].innerText);
            }
        }

        return outOfloop;
    }
}

AEM.registerComponent('.tab-V1', TabV1);
