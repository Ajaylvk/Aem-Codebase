import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';

class ArticleBanner extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Article Banner';

        this.digitalDataTrackable = true;
    }

    init() {}
  // eslint-disable-next-line class-methods-use-this
}

AEM.registerComponent('.articlebanner', ArticleBanner);
