import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class HerobannerV1 extends AEM.Component {
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

    }
}

AEM.registerComponent('.herobanner-v1', HerobannerV1);
