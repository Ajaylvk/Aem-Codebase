import $ from 'jquery';
import 'slick-carousel';
import AEM from '../../../../core/js/AemComponent';

class Carouselcontainer extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'Carousel-container';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {
        this.$titleComponent = $('h2', this.element);
        this.title =
      this.$titleComponent != null ? this.$titleComponent.text().trim() : '';
        if ($('.carousel-a-v1__container').data('wcmmode') != 'edit') {
            this.initCarouselAv1();
        }
    }

    initCarouselAv1() {
        $('.slider-bullet').slick({
            cssEase: 'linear',
            pauseOnHover: false,
            pauseOnDotsHover: true,
        });
        $('.slider-for').slick({
            slidesToShow: 1,
            slidesToScroll: 1,
            arrows: false,
            fade: true,
            asNavFor: '.slider-nav',
        });

        $('.slider-nav').slick({
            slidesToShow: 5,
            slidesToScroll: 1,
            asNavFor: '.slider-for',
            dots: false,
            centerMode: false,
            focusOnSelect: true,
            arrows: true,
            nextArrow:
        "<span class='slick-customarrowright'><svg class='a-icon a-icon--to-top next_arrow'><use xlink:href='#to-top'></use></svg></span>",
            prevArrow:
        "<span class='slick-customarrowleft'><svg class='a-icon a-icon--to-top previous_arrow'><use xlink:href='#to-top'></use></svg></span>",
            responsive: [
                {
                    breakpoint: 900,
                    settings: {
                        slidesToShow: 3,
                    },
                },
                {
                    breakpoint: 700,
                    settings: {
                        slidesToShow: 3,
                        centerMode: false,
                    },
                },
            ],
        });

        if ($('.carousel-left').length || $('.carousel-center').length) {
            $('.slider-nav').slick('slickSetOption', { slidesToShow: 4 }, true);
        }

        if ($('.carousel-scroll-identifier').data('autoplay') == 'istrue') {
            window.addEventListener('scroll', checkcarouselpos);
        }
        $('.carousel-a-v1__container').removeClass('hidecarouselonload');
    }
}

function checkcarouselpos() {
    const carouselpos = document.querySelector('.carouselcontainer');
    const rect = carouselpos.getBoundingClientRect();

    const isInViewport =
    rect.top >= 0 &&
    rect.bottom <=
      (window.innerHeight || document.documentElement.clientHeight);

    if (isInViewport) {
        $('.slider-bullet').slick('slickSetOption', { autoplay: true }, true);
        $('.slider-nav').slick('slickSetOption', { autoplay: true }, true);
        $('.slider-for').slick('slickSetOption', { autoplay: true }, true);
    }
}

AEM.registerComponent('.carouselcontainer', Carouselcontainer);
