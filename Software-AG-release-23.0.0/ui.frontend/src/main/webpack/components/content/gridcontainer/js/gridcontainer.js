import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';


class GridContainer extends AEM.Component {

    // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);

        this.name = 'GridContainer';
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
        if (this.$campaign != null) {
            this.campaign1 = this.$campaign.data('attrib-campaign');
        }

        this.$titleComponent = $('h2', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
    }
}

AEM.registerComponent('.gridcontainer', GridContainer);
