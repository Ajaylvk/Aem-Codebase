import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';

class MediaV1 extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Media V1';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {}
}

AEM.registerComponent('.media-v1', MediaV1);
