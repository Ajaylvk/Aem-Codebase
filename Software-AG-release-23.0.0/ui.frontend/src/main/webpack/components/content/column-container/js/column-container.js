import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';

class ColumnContainer extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Column Container';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {}
}

AEM.registerComponent('.coloum-container', ColumnContainer);
