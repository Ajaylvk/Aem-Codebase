function aemArrowTabs() {
    this.selectors = {
        inNavPrevBtn: '#searchResultPrev',
        inNavNextBtn: '#searchResultNext',
        iNavBox: '.perspectives',
        inNavItem: '.perspectives > a',
        inNavWrapper: '.ig_facets'
    };

    const init = function() {
        this.$inNavPrevBtn = $(this.selectors.inNavPrevBtn);
        this.$inNavNextBtn = $(this.selectors.inNavNextBtn);
        this.$iNavBox = $(this.selectors.iNavBox);
        this.$iNavBoxWidth = $(this.selectors.inNavWrapper).innerWidth();
        this.$inNavItem = $(this.selectors.inNavItem);
        let itemsWidth = 0;// total width of all items
        const allItems = $(this.selectors.inNavItem, this.element);
        let countItem = 0;

        for (countItem; countItem < allItems.length; countItem++) {
            itemsWidth += $(allItems[countItem]).width();
        }

        if (itemsWidth > this.$iNavBox.width()) {
            this.$inNavNextBtn.addClass('scroll-right');
        } else {
            this.$inNavNextBtn.removeClass('scroll-right');
        }
        _attachEvents();
    }

    const _attachEvents = function() {
        this.$iNavBox.on('scroll', event => _handleNavScroll(event));
        this.$inNavNextBtn.on('click', event => _inNavMoveNext(event));
        this.$inNavPrevBtn.on('click', event => _inNavMovePrev(event));
    }

    const _handleNavScroll = function(event) {
        if (this.$iNavBox.scrollLeft() > 1) {
            this.$inNavPrevBtn.addClass('scroll-left');
        } else {
            this.$inNavPrevBtn.removeClass('scroll-left');
        }

        if (this.$iNavBox.scrollLeft() + this.$iNavBoxWidth + 2 >= this.$iNavBox[0].scrollWidth) {
            this.$inNavNextBtn.removeClass('scroll-right');
        } else {
            this.$inNavNextBtn.addClass('scroll-right');
        }
    }

    const _inNavMoveNext = function(event) {
        this.$iNavBox.animate({
            scrollLeft: '+=75px',
        }, '700');
    }

    const _inNavMovePrev = function(event) {
        this.$iNavBox.animate({
            scrollLeft: '-=75px',
        }, '700');
    }

    init();
}
