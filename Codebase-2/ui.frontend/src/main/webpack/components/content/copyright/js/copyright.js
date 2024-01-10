import AEM from '../../../../core/js/AemComponent';

class Copyright extends AEM.Component {

    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.teconsent = this.element.querySelector('#teconsent');
        this.initalCookieConsent = this.teconsent.querySelector('.cookie-consent-initial');
        this.cookieConsentLabel = this.teconsent.dataset.cookieConsentLabel;
    }

    init() {
        // add class and change text as soon as the <a> node is added by teconsent
        const teconsentObserver = new MutationObserver(() => {
            this.updateCookieConsentLabel();
        });
        teconsentObserver.observe(this.teconsent, { childList: true });

        // update cookie disclaimer on init
        this.updateCookieConsentLabel();
        this.initalCookieConsent.innerText = this.cookieConsentLabel;
    }

    updateCookieConsentLabel() {
        const teconsentLink = this.teconsent.querySelector('a');
        if (teconsentLink) {
            teconsentLink.classList.add('a-anchor-text-footer');
            teconsentLink.innerText = this.cookieConsentLabel;
            this.initalCookieConsent.innerText = '';
        }
    }
}

AEM.registerComponent('.copyright', Copyright);
