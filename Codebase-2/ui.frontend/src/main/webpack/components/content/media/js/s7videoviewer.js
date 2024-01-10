import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class videoViewer extends AEM.Component {
    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Dynamic Media Video Player';
        this.$el = $(element).parent('.s7dm__component');
        this.videoElClass = 's7videoplayer';
        this.overlayClass = 's7dm--overlay';
        this.noScrollClass = 's7dm--no-scroll';
        this.fullScreenMode = 'fullscreen';
        this.fullScreenEleClass = 's7container';
        this.playPauseBtnClass = 's7playpausebutton';
        this.fullScreenBtnClass = 's7fullscreenbutton';
        this.pausedEleAtrr = '[state="play"]';
        this.containerEl = this.$el.find('.s7dm__container');
        this.titleEle = this.$el.find('.s7dm__title');
        this.overlayCloseBtn = this.$el.find('.s7dm__close-popup');
        this.blackAreaEle = this.$el.find('.s7dm__black-area');
        this.placeHolderEle = this.$el.prev('.s7dm__placeholder');
    }

    init() {
        this._attachEvents();
    }

    _attachEvents() {
        // Play Click then show overlay
        this.$el.on('click', `.${this.videoElClass},.${this.playPauseBtnClass}`, () => {
            // check if overlayClass is present
            const overlayClassExist = this._overlayClassExist();
            const isFullScreenMode = this._isFullScreenMode();
            const containerHeight = this.containerEl.outerHeight();
            const isMobile = ($(window).width() <= 768);

            // If mobile screen(width less than 768px) then show as full screen
            if (isMobile) {
                this.$el.find(`.${this.fullScreenBtnClass}`).trigger('click');
                return;
            }

            if (!overlayClassExist && !isFullScreenMode) {
                // show black-area if it is hidden
                this.blackAreaEle.removeClass('hide');

                // add overlay class
                this.$el.addClass(this.overlayClass);

                // show title in overlay
                this.titleEle.removeClass('hide');

                // show overlay close button
                this.overlayCloseBtn.removeClass('hide');

                // placeholder element: remove hide class and add container height
                this.placeHolderEle.removeClass('hide').css({ height: containerHeight });

                // add no-scroll class to body
                $('html, body').addClass(this.noScrollClass);
            }
        });

        // Close button click to hide overlay if already present
        this.overlayCloseBtn.on('click', this._closePopup.bind(this));
        this.blackAreaEle.on('click', this._closePopup.bind(this));
    }

    _closePopup(e) {
        const currTarget = e.currentTarget;

        // check if overlayClass is present
        const overlayClassExist = this._overlayClassExist();

        if (overlayClassExist) {
            const isVideoPlaying = this.$el.find(this.pausedEleAtrr).length < 1;

            // placeholder element: remove hide class and add container height
            this.placeHolderEle.addClass('hide').css({ height: 'auto' });

            // pause video if its playing
            if (isVideoPlaying) {
                this.$el.find(`.${this.videoElClass}`).trigger('click');
            }

            // remove overlay class
            this.$el.removeClass(this.overlayClass);

            // hide title in overlay
            this.titleEle.addClass('hide');

            // hide overlay close button
            $(currTarget).addClass('hide');

            // remove no-scroll class to body
            $('html, body').removeClass(this.noScrollClass);
        }
    }

    _overlayClassExist() {
        return (this.$el.attr('class') || '').indexOf(this.overlayClass) > -1;
    }

    _isFullScreenMode() {
        return (this.$el.find(`.${this.fullScreenEleClass}`).attr('mode') || '').indexOf(this.fullScreenMode) > -1;
    }
}

AEM.registerComponent('.s7dm__component', videoViewer);
