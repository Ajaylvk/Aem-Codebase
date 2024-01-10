import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Productteaser extends AEM.Component {

    // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'Product teaser';
        this.digitalDataTrackable = true;
        this.title = null;
        this.$titleComponent = null;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
        this.$titleComponent = $('h4', this.element);
        this.title = this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
    }
}

AEM.registerComponent('.productteaser', Productteaser);
