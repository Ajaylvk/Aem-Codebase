import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';

class NavItems extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'NavItems';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {}
}

AEM.registerComponent('.navitem', NavItems);
