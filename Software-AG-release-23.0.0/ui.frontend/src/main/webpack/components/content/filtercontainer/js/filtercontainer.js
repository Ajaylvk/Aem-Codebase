import $ from 'jquery';
import Dropkick from 'dropkickjs';
import AEM from '../../../../core/js/AemComponent';

class FilterContainer extends AEM.Component {

    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Filter container';
        this.digitalDataTrackable = true;
        this.title = null;
        this.$titleComponent = null;
    }

    init() {
        this.$filter1 = $('.filtercontainer__filter-1', this.element);
        this.$filter2 = $('.filtercontainer__filter-2', this.element);
        this.$filter3 = $('.filtercontainer__filter-3', this.element);
        this.$filter1Input = $('.filtercontainer__filter1-input', this.element);
        this.$filter2Input = $('.filtercontainer__filter2-input', this.element);
        this.$filter3Input = $('.filtercontainer__filter3-input', this.element);
        this.$contentItemList = $('.filtercontainer__list', this.element);
        this.$contentItems = $('.contentitem__container', this.element);
        this.$contentItemsVideo = $('[data-asset-type="videoavs"]', this.element);
        this.$allContentItems = $('.contentitem__container', this.element).parent().parent();
        this.$loadMoreButton = $('.filtercontainer__load-more', this.element);
        this.$currentlyShownItems = $('.filtercontainer__currently-shown-items', this.element);
        this.loadMoreItems = $('.filtercontainer__container', this.element).data('load-more-items');

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';

        if (this.$filter1.length && this.$filter1.children().length) {
            this.filter1Dropkick = new Dropkick(this.$filter1.get(0), { mobile: false });
            this.$filter1.on('change', () => {
                this._filterResults();
                this._updateFilterOptions('filter1');
                this.$filter1Input.val(this.$filter1.val());
            });
            this.filterOptions1 = this.constructor._getOptionsForFilter('filter1', this.$contentItems);
            this.filterOptions1.sort().map((option, i) => this.filter1Dropkick.add(option, i + 1));
        }

        if (this.$filter2 && this.$filter2.children().length) {
            this.filter2Dropkick = new Dropkick(this.$filter2.get(0), { mobile: false });
            this.$filter2.on('change', () => {
                this._filterResults();
                this._updateFilterOptions('filter2');
                this.$filter2Input.val(this.$filter2.val());
            });
            this.filterOptions2 = this.constructor._getOptionsForFilter('filter2', this.$contentItems);
            this.filterOptions2.sort().map((option, i) => this.filter2Dropkick.add(option, i + 1));
        }

        if (this.$filter3 && this.$filter3.children().length) {
            this.filter3Dropkick = new Dropkick(this.$filter3.get(0), { mobile: false });
            this.$filter3.on('change', () => {
                this._filterResults();
                this._updateFilterOptions('filter3');
                this.$filter3Input.val(this.$filter3.val());
            });
            this.filterOptions3 = this.constructor._getOptionsForFilter('filter3', this.$contentItems);
            this.filterOptions3.sort().map((option, i) => this.filter3Dropkick.add(option, i + 1));
        }

        if (this.$loadMoreButton) {
            this.$loadMoreButton.on('click', () => this._handleShowMoreClick());
        }

        this._handleVideoContentItems();

        // restore filters/load-more e.g. from browser-back
        $(document).ready(() => {
            const isEditMode = this.props.wcmmode === 'edit';
            if (!isEditMode) {
                this._filterResults();
                this._restoreFilters();
            }
        });
    }

    static _getOptionsForFilter(filter, items) {
        const options = Array.from(items).reduce((acc, item) => {
            const tags = item.dataset[filter];
            if (tags) {
                tags.split(';').forEach(opt => acc.push(opt));
            }
            return acc;
        }, []);
        return options.filter((option, index) => options.indexOf(option) === index);
    }

    _updateFilterOptions(filter) {
        const $unfilteredItems = $(this.element).find('.parsys > div:not(.filtered)').children().children();
        if (this.$filter1.length && filter !== 'filter1') {
            const newFilterOptions1 = this.constructor._getOptionsForFilter('filter1', $unfilteredItems);
            this.filterOptions1.sort().forEach((filterOption, i) => {
                this.filter1Dropkick.hide(i + 1, newFilterOptions1.indexOf(filterOption) === -1);
            });
        }
        if (this.$filter2.length && filter !== 'filter2') {
            const newFilterOptions2 = this.constructor._getOptionsForFilter('filter2', $unfilteredItems);
            this.filterOptions2.sort().forEach((filterOption, i) => {
                this.filter2Dropkick.hide(i + 1, newFilterOptions2.indexOf(filterOption) === -1);
            });
        }
        if (this.$filter3.length && filter !== 'filter3') {
            const newFilterOptions3 = this.constructor._getOptionsForFilter('filter3', $unfilteredItems);
            this.filterOptions3.sort().forEach((filterOption, i) => {
                this.filter3Dropkick.hide(i + 1, newFilterOptions3.indexOf(filterOption) === -1);
            });
        }
    }

    _filterResults() {
        this.$allContentItems.removeClass('hidden');
        this.$allContentItems.removeClass('filtered');
        const $filteredItems = this.$contentItems.filter((_, item) => !this._matchesFilter(item));
        $filteredItems.parent().parent().addClass('filtered');

        this._toggleItemsVisibility();
        this._toggleShowMoreButtonVisibility();
    }

    _matchesFilter(item) {
        const filter1 = this.$filter1.val();
        const filter2 = this.$filter2.val();
        const filter3 = this.$filter3.val();
        const matchesFilter1 = (!filter1 || !item.dataset.filter1 || item.dataset.filter1.split(';').indexOf(filter1) !== -1);
        const matchesFilter2 = (!filter2 || !item.dataset.filter2 || item.dataset.filter2.split(';').indexOf(filter2) !== -1);
        const matchesFilter3 = (!filter3 || !item.dataset.filter3 || item.dataset.filter3.split(';').indexOf(filter3) !== -1);
        return matchesFilter1 && matchesFilter2 && matchesFilter3;
    }

    _toggleItemsVisibility() {
        const $unfilteredItems = $(this.element).find('.parsys > div:not(.filtered)');
        for (let i = 0; i < $unfilteredItems.length; i++) {
            const isVisible = i >= parseInt(this.$currentlyShownItems.val(), 10);
            $($unfilteredItems[i]).toggleClass('hidden', isVisible);
        }
    }

    _handleShowMoreClick() {
        this._updateNumberOfShownItems();
        this._toggleItemsVisibility();
        this._toggleShowMoreButtonVisibility();
    }

    _updateNumberOfShownItems() {
        const newNumberOfShownItems = Math.min(
            this.$contentItems.length,
            parseInt(this.$currentlyShownItems.val(), 10) + this.loadMoreItems
        );
        this.$currentlyShownItems.val(newNumberOfShownItems);
    }

    _toggleShowMoreButtonVisibility() {
        const $hiddenItems = $('.hidden', this.element).not('.newpar');
        const isVisible = $hiddenItems.length <= 0;
        this.$loadMoreButton.toggleClass('filtercontainer__load-more--hidden', isVisible);
    }

    _restoreFilters() {
        if (this.$filter1Input.val()) {
            this.filter1Dropkick.select(this.$filter1Input.val());
        }
        if (this.$filter2Input.val()) {
            this.filter2Dropkick.select(this.$filter2Input.val());
        }
        if (this.$filter3Input.val()) {
            this.filter3Dropkick.select(this.$filter3Input.val());
        }
    }

    _handleVideoContentItems() {
        const $videoContentWrapper = this.$contentItemsVideo.parent().find('.contentitem__content-wrapper');
        $videoContentWrapper.closest('.contentitem__link').on('click', e => e.preventDefault());
        $videoContentWrapper.on('click', (event) => {
            const $contentItem = $(event.target).closest('.contentitem__content');
            const video = $contentItem.find('video').get(0);
            if (video && video.paused) {
                video.play();
            } else if (video) {
                video.pause();
            }
        });
    }

}

AEM.registerComponent('.filtercontainer', FilterContainer);
