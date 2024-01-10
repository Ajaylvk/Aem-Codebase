import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Title extends AEM.Component {

    // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Title';
        this.digitalDataTrackable = true;

        this.title = null;
        this.$titleComponent = null;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
        this.$titleComponent = $('h1', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
    }
}

AEM.registerComponent('.title', Title);
