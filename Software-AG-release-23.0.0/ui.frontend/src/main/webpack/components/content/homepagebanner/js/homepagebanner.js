import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Homepagebanner extends AEM.Component {

    // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.name = 'Homepage Banner';
        this.digitalDataTrackable = true;
        this.hasCampaign = true;

        this.$boxMain = null;
        this.$solutionBox1 = null;
        this.$solutionBox2 = null;

        this.campaign1 = null;
        this.campaign2 = null;
        this.campaign3 = null;

        this.title = null;
        this.$titleComponent = null;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
        this.$boxMain = $('.box__main', this.element);
        this.$solutionBox1 = $('.homepagebanner__solutionbox1', this.element);
        this.$solutionBox2 = $('.homepagebanner__solutionbox2', this.element);

        this.campaign1 = this.$boxMain.data('attrib-campaign');
        this.campaign2 = this.$solutionBox1.data('attrib-campaign');
        this.campaign3 = this.$solutionBox2.data('attrib-campaign');

        this.$titleComponent = $('h1', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
    }
}

AEM.registerComponent('.homepagebanner', Homepagebanner);
