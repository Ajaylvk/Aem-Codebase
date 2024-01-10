import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Productteaserv1 extends AEM.Component {

    // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'Product teaser_v1';
        this.digitalDataTrackable = true;
        this.title = null;
        this.$titleComponent = null;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
        this.$titleComponent = $('h4', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
        this._attachEvents();
        this.productTeasere = document.querySelectorAll('.productteasere');
    }

    _attachEvents() {
        $(window).on('scroll', event => this._handleScrollAndLoad(event));
        $(window).on('load', event => this._handleScrollAndLoad(event));
    }

    _handleScrollAndLoad() {
        this._checkElementsInViewport();
    }

    _checkElementsInViewport() {
        this.productTeasere.forEach((el) => {
            if (this._isElementInViewport(el)) {
                const prodvariablenames = [];

                $('.productteaserecard-gradient-txt').each(function () {
                    prodvariablenames.push($(this).data('productteasere-figure'));
                });

                for (let i = 0; i < prodvariablenames.length; i++) {
                    $('div.productteaserecard-gradient-txt')
                .eq(i)
                .css('--productteasere-counter', prodvariablenames[i]);
                }

                callanimation();
            }
        });
    }

    // _isElementInViewport(el) {
    //     const rect = el.getBoundingClientRect();
    //     return (
    //         rect.top >= 0 &&
    //       rect.left >= 0 &&
    //       rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
    //       rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    //     );
    // }

    _isElementInViewport(el) {
        const rect = el.getBoundingClientRect();
        const windowHeight = window.innerHeight || document.documentElement.clientHeight;

        // Calculate the visible height of the element
        const visibleHeight = Math.min(rect.bottom, windowHeight) - Math.max(rect.top, 0);

        // Check if at least 1/4th of the element is visible
        return visibleHeight >= (rect.height / 4);
    }
}

function callanimation() {
    $('.productteaserecard-gradient-txt').addClass('counter-active');
}


AEM.registerComponent('.productteaser_v1', Productteaserv1);
