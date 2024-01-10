import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';
import redirect from '../../redirect-popup/js/redirect-popup';


const labels = redirect.classlabels;


/*eslint-disable */
class UtmParams extends AEM.Component {

    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Redirect Popup';
        this.digitalDataTrackable = true;
        this.eleClass = '.utmParams';
        this.$el = $(element).closest(this.eleClass);
        this.config = this.$el.data('config');
    }

    init() {
        if (!this.config.paramPrefix && this.config.paramPrefix == '') return;
        const utmParam = window.location.search.indexOf(this.config.paramPrefix) > -1;
        if (this.config.enable === 'true' && utmParam) {
            this._attachEvents();
        }

    }

    _attachEvents() {
        const self = this;
        $(document).on('click', 'a', (e) => {
            e.preventDefault();
            const currTarget = e.currentTarget;
            const currOrigin = window.location.origin;
            const currRegion = location.pathname.replace('.html', '').split('/').filter(v => v)[0];
            const target = $(currTarget).attr('target');
            let href = $(currTarget).attr('href');
            let redirectRegion = '';
            let currOriginCopy;
            let hrefOriginCopy;
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

            if (hrefOriginCopy === currOriginCopy && currRegion === redirectRegion) {
                const queryParams = getQueryParamAsObj();
                const filteredUTMParams = Object.keys(queryParams)
                                        .filter(v => v.indexOf(self.config.paramPrefix) > -1)
                                        .reduce((str, key) => {
                                            const separator = (str === '') ? '' : '&';
                                            str += `${separator}${key}=${queryParams[key]}`;
                                            return str;
                                        }, '');


                // If next url doesnot have search/query params then only append utm params to url
                if (href.search === '') {
                    href.search = filteredUTMParams;
                } else { // If next url has search/query params then merge with utm params
                    href.search += `&${filteredUTMParams}`;
                }
                _redirect(href, target);
            }

            if(labels.external.show === 'false' && labels.regional.show === 'true'){
               if(!(currRegion === redirectRegion)) {
                   return;
               }
               else{
                _redirect(href, target);
               } 
                
            }
            if(labels.external.show === 'true' && labels.regional.show === 'false'){
                if(!(hrefOriginCopy === currOriginCopy)) {
                    return;
                }
                else{
                 _redirect(href, target);}
            }

            if(labels.external.show === 'false' && labels.regional.show === 'false'){
                _redirect(href, target);
            }
        });
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

function getQueryParamAsObj() {
    const search = location.search.substring(1);
    const jsonString = `{"${decodeURI(search).replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g, '":"')}"}`;
    let finalJSON = {};

    if (IsJsonString(jsonString)) {
        finalJSON = JSON.parse(jsonString);
    }

    return finalJSON;
}

function IsJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

AEM.registerComponent('.utmParams', UtmParams);

