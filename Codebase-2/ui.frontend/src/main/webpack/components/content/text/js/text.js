import AEM from '../../../../core/js/AemComponent';

class Text extends AEM.Component {

    // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'Text';
        this.digitalDataTrackable = true;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
    }
}

AEM.registerComponent('.text', Text);
