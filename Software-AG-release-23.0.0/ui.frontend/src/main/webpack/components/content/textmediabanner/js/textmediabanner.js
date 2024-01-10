import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Textmediabanner extends AEM.Component {

    constructor(element) {
        super(element);
        this.name = 'Textmediabanner';
        this.compName = this.constructor.name;
        this.digitalDataTrackable = true;
        this.hasSingleCampaign = true;

        this.campaign1 = null;
        this.title = null;
        this.$titleComponent = null;
    }

    init() {
        this.$titleComponent = $('h3', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';

        if (this.element.hasAttribute('data-attrib-campaign')) {
            this.campaign1 = this.element.getAttributeNode('data-attrib-campaign').value;
        }
    }
}

AEM.registerComponent('.textmediamabber', Textmediabanner);
