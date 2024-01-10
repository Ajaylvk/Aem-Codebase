import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';
import { desktop } from '../../../../core/js/utils/global-variables';
/* eslint-disable */
class logoComponent extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.desktop = desktop;

        this.selectors = {
            logoV1: '.logo-v1',
            logoImageV1: '.logoImage',
            logoSeparatorV1: '.logo-separator',
            logoTitleV1: '.logo-title',

        };
    }

    init() {
        this.animationTimeV1 = 150;
        this.$logoV1 = $(this.selectors.logoV1, this.element);
        this.$logoImageV1 = $(this.selectors.logoImageV1, this.element);
        this.$logoSeparatorV1 = $(this.selectors.logoSeparatorV1, this.element);
        this.$logoTitleV1 = $(this.selectors.logoTitleV1, this.element);
        this._attachLogoHandlersV1();
    }

    _attachLogoHandlersV1() {
        this._calLogoV1ComponentWidth();
    }

    _calLogoV1ComponentWidth() {
        $('.logo-v1')[0].width = `${this._calcWidthOfLogoV1()}px`;
    }

    _calcWidthOfLogoV1() {
        if (document.getElementsByClassName('logo-title')
        && document.getElementsByClassName('logo-title')[0]
        && document.getElementsByClassName('logo-title')[0].clientWidth > 0) {
            return document.getElementsByClassName('logoImage')[0].clientWidth
        + document.getElementsByClassName('logo-title')[0].clientWidth;
        }
        return document.getElementsByClassName('logoImage')[0].clientWidth;
    }

}
/* eslint-enable */
AEM.registerComponent('.logo-v1', logoComponent);
