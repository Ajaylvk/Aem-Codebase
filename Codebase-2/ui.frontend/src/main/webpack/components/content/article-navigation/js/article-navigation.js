import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';
import { headerTablet } from '../../../../core/js/utils/global-variables';

class ArticleNavigation extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'Article-Navigation';
        this.digitalDataTrackable = true;
        this.title = null;
        this.$titleComponent = null;
        this.header_tablet = headerTablet;
    }

  // eslint-disable-next-line class-methods-use-this
    init() {
        this.$titleComponent = $('h2', this.element);
        this.title =
      this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
        $(window).on('resize', () => this._articlenavmobileview());
        $(window).on('load', () => this._articlenavmobileview());
        $(() => {
      // initialize scrollable links and animate.
            const scrollLink = $('.scroll');

            scrollLink.on('click', function (e) {
                e.preventDefault();
                $('html').css('scroll-behavior', 'auto'); // change existing scroll behaviour on html tag to auto to avoid delay.
                $('body,html').animate(
                    {
                        scrollTop: $(this.hash).offset().top - 120, // set scroll position from top, (65px header height)
                    },
                    500
                );
            });

      //   Setting active link on scroll

            $(window).on('scroll', function () {
                const scrollbarLocationFromTop = $(this).scrollTop();
                scrollLink.each(function () {
                    const sectionOffset = $(this.hash).offset().top - 120;

                    if (sectionOffset - 120 <= scrollbarLocationFromTop) {
                        $(this)
              .parent()
              .addClass('active');
                        $(this)
              .parent()
              .siblings()
              .removeClass('active');
                    }
                });
            });
        });
    }

    _articlenavmobileview() {
        if (window.screen.width <= this.header_tablet) {
            if ($('.articleintro').siblings('.articleNavBlock').length == 0) {
                $('.articleNavBlock').css('display', 'none');
                $('.articleNavBlock').insertAfter($('.articleintro'));
                $('.articleNavBlock').css('display', 'block');
            }
        }
    }
}

AEM.registerComponent('.articlenavigation', ArticleNavigation);
