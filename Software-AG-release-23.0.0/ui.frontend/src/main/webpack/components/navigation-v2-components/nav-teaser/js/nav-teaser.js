import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';

class NavTeaser extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'NavTeaser';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {}
}

AEM.registerComponent('.navteaser', NavTeaser);
