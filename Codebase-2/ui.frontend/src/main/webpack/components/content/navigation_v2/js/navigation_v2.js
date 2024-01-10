import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';

class Navigation_v2 extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'navigation_v2';
        this.digitalDataTrackable = true;
        this.title = null;
        this.$titleComponent = null;
    }

  // eslint-disable-next-line class-methods-use-this
    init() {
        let hoverIntentTimeout;

        $('.listitem-wrapper')
      .on('mouseenter', function (event) {
          const $currentElement = $(this);
          $('.listitem.active').removeClass('active');
        // Clear any existing hoverIntentTimeout
          clearTimeout(hoverIntentTimeout);

          if ($currentElement.find('.nav-directlink').length == 0) {
          // Set a new hoverIntentTimeout to open the panel after a delay
              hoverIntentTimeout = setTimeout(() => {
                  $currentElement.parent().addClass('active');
              }, 500); // 500 milliseconds = 0.5 seconds
          } else {
              $('.listitem.active').removeClass('active');
          }
      })
      .on('mouseleave', () => {
        // Clear the hoverIntentTimeout when the mouse leaves
          clearTimeout(hoverIntentTimeout);
      });

    // Remove active class when clicking outside .navigation_v2
        $(document).on('click', (event) => {
            if (!$(event.target).closest('.listitem').length) {
                $('.listitem.active').removeClass('active');
            }
        });

        $('.nav-back-mobile').on('click', () => {
            $('.listitem.active').removeClass('active');
        });
    }
}

AEM.registerComponent('.navigation_v2', Navigation_v2);
