import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class HerobannerContainer extends AEM.Component {
    constructor(element) {
        super(element);
        this.name = 'Hero Banner V1';
        this.compName = this.constructor.name;
        this.digitalDataTrackable = true;
        this.hasSingleCampaign = true;

        this.campaign1 = null;
        this.title = null;
        this.$titleComponent = null;
    }

    init() {
        this.hideImageContainer();
    }

    hideImageContainer() {
        const tab_flag = window.screen.width > 768;
        if (tab_flag) {
            $('.herobannerb .hero-B-banner-image').insertAfter('.hero-text');
            $('.hero-B-banner-image').css('display', 'block');
        }
        const flag_phone = window.screen.width <= 414;
        const flag_desktop = window.screen.width > 768;
        const flag_tablet = window.screen.width > 414 && window.screen.width <= 768;
        if (flag_phone) {
            $('.herobannera #heroa-phone').removeClass('herobannera-display');
            $('.herobannera #heroa-tablet').addClass('herobannera-display');
            $('.herobannera #heroa-desktop').addClass('herobannera-display');
        }
        if (flag_tablet) {
            $('.herobannera #heroa-phone').addClass('herobannera-display');
            $('.herobannera #heroa-desktop').addClass('herobannera-display');
            $('.herobannera #heroa-tablet').removeClass('herobannera-display');
        }
        if (flag_desktop) {
            $('.herobannera #heroa-phone').addClass('herobannera-display');
            $('.herobannera #heroa-tablet').addClass('herobannera-display');
            $('.herobannera #heroa-desktop').removeClass('herobannera-display');
        }
    }
}

AEM.registerComponent('.herobannercontainer', HerobannerContainer);
