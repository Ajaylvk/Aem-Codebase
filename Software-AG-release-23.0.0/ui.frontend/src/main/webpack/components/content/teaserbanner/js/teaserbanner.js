import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';


class Teaserbanner extends AEM.Component {

    constructor(element) {
        super(element);
        this.name = 'Teaserbanner';
        this.compName = this.constructor.name;
        this.digitalDataTrackable = true;
        this.hasSingleCampaign = true;

        this.campaign1 = null;
        this.title = null;
        this.$titleComponent = null;
    }

    init() {
        if (this.element.hasAttribute('data-attrib-campaign')) {
            this.campaign1 = this.element.getAttributeNode('data-attrib-campaign').value;
        }

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
    }
}

AEM.registerComponent('.teaserbanner', Teaserbanner);
