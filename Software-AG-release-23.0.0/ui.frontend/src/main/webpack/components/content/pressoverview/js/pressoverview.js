import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Pressoverview extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.name = 'Press Overview';
        this.digitalDataTrackable = true;

        this.amountOfLoadMoreItems = null;
        this.$list = null;
        this.$items = null;
        this.$loadMoreButton = null;

        this.title = null;
        this.$titleComponent = null;
    }

    init() {
        this.$list = $('.pressoverview__list', this.element);
        this.$items = $('.pressoverview__list-item', this.element);
        this.$loadMoreButton = $('.pressoverview__load-more', this.element);
        this.$currentlyShownItems = $('.pressoverview__currently-shown-items', this.element);
        this.amountOfLoadMoreItems = this.$list.data('load-more-items');

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';

        if (this.$loadMoreButton) {
            this.$loadMoreButton.on('click', () => this._handleShowMoreClick());
        }

        // restore filters from browser-back
        $(document).ready(() => this._filterResults());
    }

    _filterResults() {
        this._unhidePressItems();
        this._toggleShowMoreButtonVisibility();
    }

    _handleShowMoreClick() {
        this._updateNumberOfShownItems();
        this._unhidePressItems();
        this._toggleShowMoreButtonVisibility();
    }

    _unhidePressItems() {
        const $itemsInDOM = $('.pressoverview__list-item', this.element);
        for (let i = 0; i < $itemsInDOM.length; i++) {
            const isVisible = !(i < parseInt(this.$currentlyShownItems.val(), 10));
            $($itemsInDOM[i]).toggleClass('pressoverview__list-item--hidden', isVisible);
        }
    }

    _toggleShowMoreButtonVisibility() {
        const $hiddenItems = $('.pressoverview__list-item--hidden', this.element);
        const isVisible = $hiddenItems.length <= 0;
        this.$loadMoreButton.toggleClass('pressoverview__load-more--hidden', isVisible);
    }

    _updateNumberOfShownItems() {
        const newNumberOfShownItems = Math.min(
            this.$items.length,
            parseInt(this.$currentlyShownItems.val(), 10) + this.amountOfLoadMoreItems
        );
        this.$currentlyShownItems.val(newNumberOfShownItems);
    }
}

AEM.registerComponent('.pressoverview', Pressoverview);
