import AEM from '../../../../core/js/AemComponent';
import $ from 'jquery';

class Textv3 extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'Text v3';
        this.digitalDataTrackable = true;
    }

  // eslint-disable-next-line class-methods-use-this
    init() {
        if ($('.text_V3-container').data('wcmmode') != 'edit') {
            $('.text_V3-link').each(function (index) {
                const childlength = $(this).children().length;
                const linkArray = $(this).children('.links');
                let count = 0;
                for (let i = 0; i < linkArray.length; i++) {
                    if (linkArray[i].children.length == 0) {
                        $(linkArray[i]).hide();
                        count++;
                    }
                }
                if (count == childlength) {
                    $(this).hide();
                }
            });
        }
    }
}

AEM.registerComponent('.text_v3', Textv3);
