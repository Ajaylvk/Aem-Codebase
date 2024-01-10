/* eslint-disable */
import $ from 'jquery';
import Swiper from 'swiper';
import AEM from '../../../../core/js/AemComponent';
import wait from '../../../../core/js/utils/wait';

class Carousel extends AEM.Component {

    constructor(element) {
        super(element);

        this.name = 'Carousel';
        this.digitalDataTrackable = true;

        this.title = null;
        this.$titleComponent = null;

        /**
         * @private
         * @type {Number}
         */
        this.slideTransitionSpeed = 750;

        /**
         * @private
         * @type {Object}
         */
        this.$carouselLoaderNode = null;

        /**
         * @private
         * @type {Object}
         */
        this.$sliderNode = null;

        this.$mediaContainers = null;

        this.selectors = {
            'swiper-container': '.swiper-container',
            media__container: '.media__container, .textmedia__container, .textmediabanner__container, .teaserbanner__container',
            'swiper-slide': '.swiper-slide',
            'swiper-slide-duplicate': '.swiper-slide-duplicate',
            s7videoviewer: '.s7videoviewer',
            's7dm-dynamic-media': '.s7dm-dynamic-media',
            'carousel-loader': '.carousel-loader',
        };
    }

    init() {
        const self = this;

        // Get DOM Nodes
        this.$sliderNode = $(self.selectors['swiper-container'], this.element);
        this.$carouselLoaderNode = $(self.selectors['carousel-loader'], this.element);
        this.$mediaContainers = this.$sliderNode.find(self.selectors.media__container);
        this.$carouselItems = $('.carousel .swiper-container');
        this.$carouselId = `Carousel: ${this.$carouselItems.index(self.$sliderNode)}`;

        console.log(`${this.$carouselId}: Found ${this.$mediaContainers.length} Media items to be resolved`);

        this.$carouselLoaderNode.addClass('slide-in');

        wait(1000).then(() => {
            self.checkAssetsLoaded();
        });

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
    }

    /*
    * Recheck loaded images/videos until fully resolved (has valid src attribute
    * starting with 'http') loaded, then fire Slider code.
    * */
    checkAssetsLoaded() {
        const self = this;

        let assetUrlsNotResolvedYet = 0;

        this.$carouselLoaderNode.addClass('slide-in');

        // eslint-disable-next-line func-names
        self.$mediaContainers.each(function () {
            // See if DM has a video, if not it has an image.
            const $videoNode = $(this).find(self.selectors.s7videoviewer);

            // If video node is attached then video is init.
            if ($videoNode.length !== 0) {
                if ($videoNode.find('video').length === 0) {
                    assetUrlsNotResolvedYet += 1;
                }
            } else {
                const $dmNode = $(this).find(self.selectors['s7dm-dynamic-media']);

                // Unless img src is not containing http, track it.
                if ($dmNode.length !== 0) {
                    if ($dmNode.find('img').length === 0) {
                        assetUrlsNotResolvedYet += 1;
                    }
                }
            }
        });

        console.log(`${self.$carouselId}: ${assetUrlsNotResolvedYet} / ${self.$mediaContainers.length} not loaded yet. ${assetUrlsNotResolvedYet === 0 ? '' : 'Retrying...'}`);

        // Recall method in case of unresolved images
        // Otherwise init slider
        if (assetUrlsNotResolvedYet > 0) {
            wait(500).then(() => {
                self.checkAssetsLoaded();
            });
        } else {
            console.log(`${self.$carouselId}: All media items loaded. Init slider...`);
            setTimeout(() => {
                self.initSlider();
            }, 1);

            self.$sliderNode.addClass('is-visible');
            self.$carouselLoaderNode.fadeOut(function () { $(this).remove(); });
        }
    }

    initSlider() {
        const wcmmode = this.props.wcmmode;

        // Dont run any slick code in Edit mode.
        if (wcmmode === 'edit') return;

        let slideDelay;
        let autoPauseDisabled;

        const sliderConfig = {
            slidesPerView: 1,
            loop: true,
            pagination: {
                el: '.swiper-pagination',
                clickable: true,
            },
            speed: this.slideTransitionSpeed,
            autoHeight: false,
            grabCursor: true,
        };

        const hasAutoplayEnabled = this.props.cmpAutoplay !== undefined;

        if (hasAutoplayEnabled) {
            slideDelay = this.props.cmpDelay;
            autoPauseDisabled = this.props.cmpAutopauseDisabled !== undefined;

            // Extend initial config
            sliderConfig.autoplay = { delay: slideDelay };
        }

        // Init slider
        const swiper = new Swiper(this.$sliderNode, sliderConfig);

        // Optionally attach event handlers to pause rotation on hover
        if (autoPauseDisabled) {
            $(this.element).on({
                mouseenter() {
                    if (swiper.autoplay && swiper.autoplay.running) {
                        swiper.autoplay.stop();
                    }
                },
                mouseleave() {
                    if (swiper.autoplay && !swiper.autoplay.running) {
                        swiper.autoplay.start();
                    }
                },
            });
        }
    }
}

AEM.registerComponent('.carousel', Carousel);
/* eslint-enable */
