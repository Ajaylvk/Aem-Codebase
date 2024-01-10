import AEM from '../../../../core/js/AemComponent';
import $ from 'jquery';

class KPI extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'KPI';
        this.digitalDataTrackable = true;
    }

  // eslint-disable-next-line class-methods-use-this
    init() {
        const variablenames = [];
        const spchChar = [];
        if ($('.herobanner_card-container')) {
            $('.herobannercontainer').addClass('herocardpresent');
        } else {
            $('.herobannercontainer').removeClass('herocardpresent');
        }
        $('.herocard-gradient-txt').each(function () {
            variablenames.push($(this).data('hero-figure'));
        });
        for (let i = 0; i <= variablenames.length; i++) {
            $('div.herocard-gradient-txt')
        .eq(i)
        .css('--hero-counter', variablenames[i]);
        }
        this._attachHandlers();
    }

    _attachHandlers() {
        this.hidecontainer();
    }

    hidecontainer() {
        const flag = window.screen.width <= 414;
    // const tab_flag = window.screen.width > 768;
        if (flag) {
      // const clonedelem = $('.desktop-herocardcontainer').clone();
      // clonedelem.removeClass('desktop-herocardcontainer');
      // clonedelem.addClass('phone-herocardcontainer');
      // $('.desktop-herocardcontainer').hide();
            $('.desktop-herocardcontainer').insertAfter('.herobannercontainer');
            $('.desktop-herocardacontainer').insertAfter('.herobannera-container');
            $('.desktop-herocardbcontainer').insertAfter('.herobannerb-container');
            $('.desktop-herocardccontainer').insertAfter('.herobannerc-container');

      // clonedelem.insertAfter($('.herobannercontainer'));
        }
    // if (tab_flag) {
    //     $('.herobannerb .hero-B-banner-image').insertAfter('.hero-text');
    //     $('.hero-B-banner-image').css('display', 'block');
    // }
    }
}

AEM.registerComponent('.kpi', KPI);
