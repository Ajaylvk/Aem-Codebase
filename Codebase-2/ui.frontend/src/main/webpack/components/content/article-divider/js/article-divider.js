import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';


class ArticleDivider extends AEM.Component {


    // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Article Divider';

        this.digitalDataTrackable = true;
    }
    // eslint-disable-next-line class-methods-use-this

    init() {

    }

}


AEM.registerComponent('.articledivider', ArticleDivider);
