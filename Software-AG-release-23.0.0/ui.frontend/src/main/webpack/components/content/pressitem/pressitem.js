import AEM from '../../../core/js/AemComponent';

class PressItem extends AEM.Component {

    constructor(element) {
        super(element);
        this.name = 'Press item';
        this.digitalDataTrackable = true;
    }

    // eslint-disable-next-line class-methods-use-this
    init() {
    }
}

AEM.registerComponent('.pressitem', PressItem);
