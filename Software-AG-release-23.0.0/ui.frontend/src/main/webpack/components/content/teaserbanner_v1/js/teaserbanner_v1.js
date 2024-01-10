import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';


class TeaserBanner extends AEM.Component {


    // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Teaser Banner V1';

        this.digitalDataTrackable = true;
    }
    // eslint-disable-next-line class-methods-use-this

    init() {
        const flag_phone = window.screen.width <= 414;
        const flag_desktop = window.screen.width > 768;
        const flag_tablet = window.screen.width > 414 && window.screen.width <= 768;
        if (flag_phone) {
            if ($('div').hasClass('herobanner-teaser')) {
                $('.herobanner-teaser #teaser-phone').removeClass(
                    'teaser-viewports'
                );
                $('.herobannera-container #teaser-tablet').addClass(
                    'teaser-viewports'
                );
                $('.herobannera-container #teaser-desktop').addClass(
                    'teaser-viewports'
                );
            }
        }
        if (flag_tablet) {
            if ($('div').hasClass('herobanner-teaser')) {
                $('.herobanner-teaser #teaser-phone').addClass(
                    'teaser-viewports'
                );
                $('.herobanner-teaser #teaser-desktop').addClass(
                    'teaser-viewports'
                );
                $('.herobanner-teaser #teaser-tablet').removeClass(
                    'teaser-viewports'
                );
            }
        }

        if (flag_desktop) {
            if ($('div').hasClass('herobanner-teaser')) {
                $('.herobanner-teaser #teaser-phone').addClass(
                    'teaser-viewports'
                );
                $('.herobanner-teaser #teaser-tablet').addClass(
                    'teaser-viewports'
                );
                $('.herobanner-teaser #teaser-desktop').removeClass(
                    'teaser-viewports'
                );
            }
        }
    }

}


AEM.registerComponent('.teaserbanner_v1', TeaserBanner);
