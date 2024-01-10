import AEM from '../../../../core/js/AemComponent';

class Embed extends AEM.Component {

    // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'Embed';
        this.digitalDataTrackable = true;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
    }
}

AEM.registerComponent('.embed', Embed);
