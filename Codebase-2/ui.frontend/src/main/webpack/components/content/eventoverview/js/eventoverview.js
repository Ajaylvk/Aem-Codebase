import $ from 'jquery';
import Dropkick from 'dropkickjs';
import AEM from '../../../../core/js/AemComponent';

class EventOverview extends AEM.Component {

    constructor(element) {
        super(element);

        this.name = 'Event Overview';
        this.compName = this.constructor.name;
        this.digitalDataTrackable = true;
        this.hasSingleCampaign = true;

        this.campaign1 = null;
        this.title = null;
        this.$titleComponent = null;

        this.amountOfLoadMoreEventItems = null;
        this.typeFilterDropkick = null;
        this.regionFilterDropkick = null;
        this.$eventList = null;
        this.$eventItems = null;
        this.$typeFilter = null;
        this.$regionFilter = null;
        this.$loadMoreButton = null;
    }

    init() {
        this.$typeFilter = $('.eventoverview__type-filter', this.element);
        this.$regionFilter = $('.eventoverview__region-filter', this.element);
        this.$eventList = $('.eventoverview__list', this.element);
        this.$eventItems = $('.eventoverview__list-item', this.element);
        this.$loadMoreButton = $('.eventoverview__load-more', this.element);
        this.$currentlyShownItems = $('.eventoverview__currently-shown-items', this.element);
        this.amountOfLoadMoreEventItems = this.$eventList.data('load-more-items');

        if (this.element.hasAttribute('data-attrib-campaign')) {
            this.campaign1 = this.element.getAttributeNode('data-attrib-campaign').value;
        }

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';

        this._attachEvents();

        if (this.$typeFilter.length && this.$typeFilter.children().length) {
            this.typeFilterDropkick = new Dropkick(this.$typeFilter.get(0), { mobile: false });
            this.$typeFilter.on('change', () => this._filterResults());
        }

        if (this.$regionFilter && this.$regionFilter.children().length) {
            this.regionFilterDropkick = new Dropkick(this.$regionFilter.get(0), { mobile: false });
            this.$regionFilter.on('change', () => this._filterResults());
        }

        if (this.$loadMoreButton) {
            this.$loadMoreButton.on('click', () => this._handleShowMoreClick());
        }

        // restore filters from browser-back
        $(document).ready(() => this._filterResults());
    }

    _attachEvents() {
        this.$eventItems.each((index, element) => {
            const $node = $(element);
            const $wrapper = $node.find('.eventoverview__list-item-link');
            const hiddenLinkNode = $node.find('.eventoverview__list-item-link-accessibility');

            // Attaches click event on content container to support nested Links (from inside RTE)
            $wrapper[0].addEventListener('click', () => { window.open(hiddenLinkNode.attr('href'), hiddenLinkNode.attr('target')); });
        });
    }

    _filterResults() {
        const $filteredEventItems = this.$eventItems.filter((_, item) => this._matchesFilter(item));
        this.$eventList.html($filteredEventItems);
        this._unhideEventItems();
        this._toggleShowMoreButtonVisibility();
    }

    _handleShowMoreClick() {
        this._updateNumberOfShownItems();
        this._unhideEventItems();
        this._toggleShowMoreButtonVisibility();
    }

    _unhideEventItems() {
        const $eventItemsInDOM = $('.eventoverview__list-item', this.element);
        for (let i = 0; i < $eventItemsInDOM.length; i++) {
            const isVisible = !(i < parseInt(this.$currentlyShownItems.val(), 10));
            $($eventItemsInDOM[i]).toggleClass('eventoverview__list-item--hidden', isVisible);
        }
    }

    _toggleShowMoreButtonVisibility() {
        const $hiddenItems = $('.eventoverview__list-item--hidden', this.element);
        const isVisible = $hiddenItems.length <= 0;
        this.$loadMoreButton.toggleClass('eventoverview__load-more--hidden', isVisible);
    }

    _updateNumberOfShownItems() {
        const newNumberOfShownItems = Math.min(
            this.$eventItems.length,
            parseInt(this.$currentlyShownItems.val(), 10) + this.amountOfLoadMoreEventItems
        );
        this.$currentlyShownItems.val(newNumberOfShownItems);
    }

    _matchesFilter(item) {
        const typeFilter = this.$typeFilter.val();
        const regionFilter = this.$regionFilter.val();
        const matchesTypeFilter = (!typeFilter || item.dataset.eventType === typeFilter);
        const matchesRegionFilter = (!regionFilter || item.dataset.eventCountry === regionFilter);
        return matchesTypeFilter && matchesRegionFilter;
    }
}

AEM.registerComponent('.eventoverview', EventOverview);
