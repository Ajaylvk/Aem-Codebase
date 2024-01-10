import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class TopRibbon extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Top Ribbon';
        this.digitalDataTrackable = true;

        this.title = null;
        this.$titleComponent = null;
    }

  // eslint-disable-next-line class-methods-use-this
    init() {
        const flag_phone = window.screen.width <= 414;
        const flag_tablet = window.screen.width <= 768;
        if (flag_phone) {
            if (
                document.querySelectorAll('.ribbon-primary-link .cmp-link').length <= 0
            ) {
                $('.ribbon-primary-link ').css('width', '0');
                $('.ribbon-primary-link ').css('height', '0');
                $('.ribbon-primary-link ').css('padding-left', '0');
                $('.ribbon-primary-link ').css('padding-top', '0');
                $('.topRibbon-text ').css('width', '100%');
            }
        }
        if (flag_tablet) {
            if (
                document.querySelectorAll('.ribbon-primary-link .cmp-link').length <= 0
            ) {
                $('.ribbon-primary-link ').css('width', '0');
                $('.ribbon-primary-link ').css('height', '0');
                $('.topRibbon-text ').css('width', '100%');
                $('.ribbon-primary-link ').css('padding-left', '0');
                $('.ribbon-primary-link ').css('padding-top', '0');
            }
        }
        if (document.querySelectorAll('.topRibbon-text h6').length > 0) {
            $('.ribbon-primary-link .cmp-link ').css('font-size', '1.4rem');
            $('.ribbon-primary-link .cmp-link ').css('line-height', '2.2rem');
        }
        if (document.querySelectorAll('.topRibbon-text h5').length > 0) {
            $('.ribbon-primary-link .cmp-link ').css('font-size', '1.6rem');
            $('.ribbon-primary-link .cmp-link ').css('line-height', '2.4rem');
        }
    }
}

AEM.registerComponent('.topribbon', TopRibbon);
