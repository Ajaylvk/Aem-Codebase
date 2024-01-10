import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class HeroBannerV2 extends AEM.Component {
    constructor(element) {
        super(element);
        this.name = 'Hero Banner V2';
        this.compName = this.constructor.name;
        this.digitalDataTrackable = true;
        this.hasSingleCampaign = true;

        this.campaign1 = null;
        this.title = null;
        this.$titleComponent = null;
    }

    init() {
        this.hideImageContainer_v2();
    }

    hideImageContainer_v2() {
        const flag_phone = window.screen.width <= 414;
        const flag_desktop = window.screen.width > 768;
        const flag_tablet = window.screen.width > 414 && window.screen.width <= 768;
        if (flag_tablet) {
            $('.herobannerb-container .herobannerb-image').insertAfter(
                '.herobannerb-content'
            );
        }
        if (flag_phone) {
            $('.herobannerb-container .herobannerb-image').insertAfter(
                '.herobannerb-content'
            );
        }

        if (flag_phone) {
            if ($('div').hasClass('herobannera-container')) {
        // herobanner A
                const herotext = document.getElementById('herobannera-content')
          .clientHeight;
                const heroContentHeight =
          600 - document.getElementById('herobannera-content').clientHeight;
                $('.herobannera-image').css('height', heroContentHeight);
                $('.herobannera-image img').css('height', heroContentHeight);
                $('.herobannera-background').css('height', herotext);
            }
        }
        if (flag_phone) {
      // herobanner B
            if ($('div').hasClass('herobannerb-container')) {
                const herobtext = document.getElementById('herobannerb-content')
          .clientHeight;
                const herobContentHeight =
          600 - document.getElementById('herobannerb-content').clientHeight;
                // $('.herobannerb-image').css('height', herobContentHeight);
                // $('.herobannerb-image img').css('height', herobContentHeight);
            }
        }
        if (flag_tablet) {
      // herobanner B
            if ($('div').hasClass('herobannerb-container')) {
                const herobtext = document.getElementById('herobannerb-content')
          .clientHeight;
                const herobContentHeight =
          600 - document.getElementById('herobannerb-content').clientHeight;
                // $('.herobannerb-image').css('height', herobContentHeight);
                // $('.herobannerb-image img').css('height', herobContentHeight);
            }
        }
        if (flag_phone) {
            if ($('div').hasClass('herobannera-container')) {
        // herobanner A
                $('.herobannera-container #heroa-phone').removeClass(
                    'herobannera-viewports'
                );
                $('.herobannera-container #heroa-tablet').addClass(
                    'herobannera-viewports'
                );
                $('.herobannera-container #heroa-desktop').addClass(
                    'herobannera-viewports'
                );
            }
      // herobanner B
            if ($('div').hasClass('herobannerb-container')) {
                $('.herobannerb-container #herob-phone').removeClass(
                    'herobannerb-viewports'
                );
                $('.herobannerb-container #herob-tablet').addClass(
                    'herobannerb-viewports'
                );
                $('.herobannerb-container #bannerb-desktop').addClass(
                    'herobannerb-viewports'
                );
            }
            if ($('div').hasClass('herobannerc-container')) {
        // herobanner C
                $('.herobannerc-container #heroc-phone').removeClass(
                    'herobannerc-viewports'
                );
                $('.herobannerc-container #heroc-tablet').addClass(
                    'herobannerc-viewports'
                );
                $('.herobannerc-container #heroc-desktop').addClass(
                    'herobannerc-viewports'
                );
            }
        }
        if (flag_tablet) {
      // herobanner A
            if ($('div').hasClass('herobannera-container')) {
                $('.herobannera-container #heroa-phone').addClass(
                    'herobannera-viewports'
                );
                $('.herobannera-container #heroa-desktop').addClass(
                    'herobannera-viewports'
                );
                $('.herobannera-container #heroa-tablet').removeClass(
                    'herobannera-viewports'
                );
            }
      // herobanner B
            if ($('div').hasClass('herobannerb-container')) {
                $('.herobannerb-container #herob-phone').addClass(
                    'herobannerb-viewports'
                );
                $('.herobannerb-container #bannerb-desktop').addClass(
                    'herobannerb-viewports'
                );
                $('.herobannerb-container #herob-tablet').removeClass(
                    'herobannerb-viewports'
                );
            }
      // herobanner C
            if ($('div').hasClass('herobannerc-container')) {
                $('.herobannerc-container #heroc-phone').addClass(
                    'herobannerc-viewports'
                );
                $('.herobannera-container #heroc-desktop').addClass(
                    'herobannerc-viewports'
                );
                $('.herobannerc-container #heroc-tablet').removeClass(
                    'herobannerc-viewports'
                );
            }
        }
        if (flag_desktop) {
      // herobanner A
            if ($('div').hasClass('herobannera-container')) {
                $('.herobannera-container #heroa-phone').addClass(
                    'herobannera-viewports'
                );
                $('.herobannera-container #heroa-tablet').addClass(
                    'herobannera-viewports'
                );
                $('.herobannera-container #heroa-desktop').removeClass(
                    'herobannera-viewports'
                );
            }
      // herobanner B

            if ($('div').hasClass('herobannerb-container')) {
                $('.herobannerb-container #herob-phone').addClass(
                    'herobannerb-viewports'
                );
                $('.herobannerb-container #herob-tablet').addClass(
                    'herobannerb-viewports'
                );
                $('.herobannerb-container #bannerb-desktop').removeClass(
                    'herobannerb-viewports'
                );
            }
      // herobanner C
            if ($('div').hasClass('herobannerc-container')) {
                $('.herobannerc-container #heroc-phone').addClass(
                    'herobannerc-viewports'
                );
                $('.herobannerc-container #heroc-tablet').addClass(
                    'herobannerc-viewports'
                );
                $('.herobannerc-container #heroc-desktop').removeClass(
                    'herobannerc-viewports'
                );
            }
        }
    }
}

AEM.registerComponent('.herobanner_v2', HeroBannerV2);
