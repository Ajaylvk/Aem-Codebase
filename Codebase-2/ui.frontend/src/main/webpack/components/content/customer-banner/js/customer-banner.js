import $ from 'jquery';
import 'slick-carousel';

import AEM from '../../../../core/js/AemComponent';

class CustomerBanner extends AEM.Component {

    constructor(element) {
        super(element);

        this.name = 'Customer banner';
        this.digitalDataTrackable = true;


        /**
         * @private
         * @type {jQuery}
         */
        this.compName = this.constructor.name;
        this.title = null;
        this.$titleComponent = null;

        /**
         * @private
         * @type {jQuery}
         */
        this.$sliderNode = null;
    }

    init() {
        // Get DOM Nodes
        this.$sliderNode = $('.slick-slider', this.element);

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';

        this.initSlider();
    }

    initSlider() {
        this.$sliderNode.slick({
            dots: false,
            arrows: true,
            infinite: true,
            speed: 500,
            slidesToShow: 6,
            slidesToScroll: 6,
            prevArrow: $('.slick-prev-custom'),
            nextArrow: $('.slick-next-custom'),
            responsive: [
                {
                    breakpoint: 1024,
                    settings: {
                        slidesToShow: 4,
                        slidesToScroll: 4,
                        infinite: true,
                    },
                },
                {
                    breakpoint: 600,
                    settings: {
                        slidesToShow: 4,
                        slidesToScroll: 4,
                        infinite: true,
                    },
                },
                {
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 4,
                        slidesToScroll: 4,
                        infinite: true,
                    },
                },
            ],
        });
    }
}

AEM.registerComponent('.customerbanner', CustomerBanner);
