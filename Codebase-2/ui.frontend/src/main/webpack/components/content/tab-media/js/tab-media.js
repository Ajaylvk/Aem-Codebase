import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';

class Tabmedia extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Tab Media';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {}
}

AEM.registerComponent('.tab-media', Tabmedia);
