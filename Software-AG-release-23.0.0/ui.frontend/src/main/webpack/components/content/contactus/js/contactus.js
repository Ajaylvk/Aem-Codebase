import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';

class Contactus extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Contactus form';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {
        if ($('.contactus').length) {
            $('.footer-v1').addClass('form-footer');
        }
    }
}

AEM.registerComponent('.contactus', Contactus);
