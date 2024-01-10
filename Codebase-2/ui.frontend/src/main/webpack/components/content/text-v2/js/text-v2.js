import AEM from '../../../../core/js/AemComponent';

class Textv2 extends AEM.Component {

    // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'Text v2';
        this.digitalDataTrackable = true;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
    }
}

AEM.registerComponent('.text_v2', Textv2);

