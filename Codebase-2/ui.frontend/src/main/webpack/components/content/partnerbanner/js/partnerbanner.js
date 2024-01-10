// import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class PartnerBanner extends AEM.Component {
    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.$partnerbanner = undefined;
        this.$document = undefined;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
    }
}

AEM.registerComponent('.partnerbanner', PartnerBanner);
