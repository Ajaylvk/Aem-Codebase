import $ from 'jquery';
import 'slick-carousel';
import AEM from '../../../../core/js/AemComponent';

class Carouselav1 extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Carousel-a-v1';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {
        this.$titleComponent = $('h2', this.element);
        this.title =
      this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
    }
}

AEM.registerComponent('.carousel-a-v1', Carouselav1);
