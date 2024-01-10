import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class ContentItem extends AEM.Component {

    constructor(element) {
        super(element);
        this.name = 'Content Item';
        this.compName = this.constructor.name;
        this.digitalDataTrackable = true;
        this.hasSingleCampaign = true;

        this.$campaign = null;
        this.campaign1 = null;
        this.title = null;
        this.$titleComponent = null;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
        this.$campaign = $('[data-attrib-campaign]', this.element);
        this.campaign1 = this.$campaign != null ? this.$campaign.data('attrib-campaign') : null;

        this.$titleComponent = $('h4', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
    }
}

AEM.registerComponent('.contentitem', ContentItem);
