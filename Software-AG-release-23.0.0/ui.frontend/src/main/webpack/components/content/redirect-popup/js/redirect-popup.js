import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';
import RenderTemplate from '../../../../core/js/utils/handlebar-renderer';

let classlabels;
class referenceFilters extends AEM.Component {

    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Redirect Popup';
        this.digitalDataTrackable = true;
        this.render = new RenderTemplate();
        this.eleClass = '.redirect-popup';
        this.$el = $(element).closest(this.eleClass);
        this.closeBtnClass = 'redirect-popup__close-btn';
        this.redirectBtnClass = 'redirect-popup__redirect-btn';
        this.labels = this.$el.data('labels');
        this.templateEle = this.$el.find('.sag-popup__container');
    }

    init() {
        if (this.labels.external.show === 'true' || this.labels.regional.show === 'true') {
            this._attachEvents();
            classlabels = this.labels;
        } else {
            classlabels = this.labels;
        }
    }


    _attachEvents() {
        const self = this;
        $(document).on('click', 'a', (e) => {
            e.preventDefault();
            const currTarget = e.currentTarget;
            const currOrigin = window.location.origin;
            const currRegion = location.pathname.replace('.html', '').split('/').filter(v => v)[0];// content
            let currOriginCopy;
            let hrefOriginCopy;
            const target = $(currTarget).attr('target');// _blank
            let href = $(currTarget).attr('href');
            let redirectRegion = '';
            // en_us,en_corporate,de_DE, zh_CN

            // if href is only a #hash then exit
            if (_isHash(href)) {
                _redirect(href, target);
                return;
            }

            // if its a relative url then merge with origin
            if (!_absoluteUrl(href)) {
                href = window.location.origin + href;
            }

            href = new URL(href);


            if (currOrigin.toLowerCase() === 'https://softwareag.com' || currOrigin.toLowerCase() === 'https://www.softwareag.com' || currOrigin.toLowerCase() === 'softwareag.com') {
                currOriginCopy = 'https://www.softwareag.com';
            } else {
                currOriginCopy = currOrigin;
            }

            if (href.origin.toLowerCase() === 'https://softwareag.com' || href.origin.toLowerCase() === 'https://www.softwareag.com' || href.origin.toLowerCase() === 'softwareag.com') {
                hrefOriginCopy = 'https://www.softwareag.com';
            } else {
                hrefOriginCopy = href.origin;
            }
            redirectRegion = href.pathname.replace('.html', '').split('/').filter(v => v)[0];
            if (hrefOriginCopy !== currOriginCopy && self.labels.external.show === 'true' && href.href.slice(href.href.length - 3, href.href.length) !== 'pdf') {
                self._showPopupContent(href, target);

                self.render.init(self.templateEle, self.labels.external);
            } else if (hrefOriginCopy === currOriginCopy && currRegion !== redirectRegion && self.labels.regional.show === 'true' && href.href.slice(href.href.length - 3, href.href.length) !== 'pdf') {
                self._showPopupContent(href, target);

                self.render.init(self.templateEle, self.labels.regional);
            } else {
                _redirect(href, target);
            }
        });

        this.$el.on('click', `.${this.redirectBtnClass}`, ((e) => {
            const currTarget = e.currentTarget;
            let trimmedUrl;
            const lengthString = JSON.parse(currTarget.closest(this.eleClass).getAttribute('data-url-details')).url.length;
            if (JSON.parse(currTarget.closest(this.eleClass).getAttribute('data-url-details')).url[lengthString - 1] === '/') {
                trimmedUrl = JSON.parse(currTarget.closest(this.eleClass).getAttribute('data-url-details')).url.substring(0, lengthString - 1);
            } else {
                trimmedUrl = JSON.parse(currTarget.closest(this.eleClass).getAttribute('data-url-details')).url;
            }
            const urlRedirectObj = {
                url: trimmedUrl,
                target: JSON.parse(currTarget.closest(this.eleClass).getAttribute('data-url-details')).target,
            };
            const redirectDetails = urlRedirectObj;

            if (redirectDetails && redirectDetails.url) {
                _redirect(redirectDetails.url, redirectDetails.target);
                // close popup once redirected
                this.$el.addClass('hide');
                $('html').removeClass('sag-popup--no-scroll');
                $('body').removeClass('sag-popup--no-scroll');
            }
        }));
    }

    _showPopupContent(href, target) {
        const urlObj = { url: href, target };
        this.$el.attr('data-url-details', JSON.stringify(urlObj));
        this.$el.removeClass('hide');
        $('html, body').addClass('sag-popup--no-scroll');
    }

}

function _absoluteUrl(urlString) {
    const isAbsolute = new RegExp('^([a-z]+://|//)', 'i');
    return isAbsolute.test(urlString);
}

function _isHash(urlString) {
    return urlString.indexOf('#') === 0 || urlString.indexOf('#') === 1;
}

function _redirect(url, target) {
    window.open(url, (target || '_self'));
}


AEM.registerComponent('.redirect-popup', referenceFilters);


export default {
    classlabels,
};
