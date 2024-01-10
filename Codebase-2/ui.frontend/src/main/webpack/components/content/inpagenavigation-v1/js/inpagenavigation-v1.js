import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';
import {
    desktop,
    headerTablet,
} from '../../../../core/js/utils/global-variables';

class Inpagenavigationv1 extends AEM.Component {
    constructor(element) {
        super(element);

        this.compName = this.constructor.name;
        this.name = 'Inpage navigation v1';
        this.desktop = desktop;
        this.header_tablet = headerTablet;
        this.count = 0;
        this.lastScrollTop = 0;

        this.selectors = {
            inNavPrevBtn: '.prev-item',
            inNavNextBtn: '.next-item',
            iNavBox: '.inpagenavigation-v1__container',
            inNavItem: '.inpagenavigation-v1-item',
            inNavItemLink: '.inpagenavigation-v1-item a',
            headerElement: '.header-v1',
        };
    }

    init() {
        this.$inNavItem = $(this.selectors.inNavItem, this.element);
        this.$iNavBox = $(this.element);
        this.$iNavBoxWidth = $(this.element).innerWidth();
        this.$inNavItemLink = $(this.selectors.inNavItemLink, this.element);
        this.$headerElement = $(this.selectors.headerElement);

        this._attachEvents();
    }

    _attachEvents() {
        this.$inNavItem.first().addClass('default-active');
        const firstlink_phone = $('.default-active a').text();
        $('.link_active').text(firstlink_phone);
        this.$inNavItemLink.on('click', event => this._jumpToSection(event));
        this._populateid();

    // Scan the links and store their positions

    // $(window).on('scroll', event => this._renderStickyInpage(event));
        $(window).on('scroll', event => this._changeActiveLink(event));
        $(window).on('resize', () => {
            if (this._isTablet()) {
                this.$iNavBox.addClass('dropdown-list');
            } else {
                this.$iNavBox.removeClass('dropdown-list');
                this.$iNavBox.removeClass('dropdown-active');
            }
        });

        $(window).on('load', () => {
            if (this._isTablet()) {
                this.$iNavBox.addClass('dropdown-list');
            } else {
                this.$iNavBox.removeClass('dropdown-list');
                this.$iNavBox.removeClass('dropdown-active');
            }
        });

        this.$iNavBox.on('click', () => {
            $('.inpagenavigation-v1__container').toggleClass('dropdown-active');
        });
        this.$inNavItemLink.on('click', () => {
            $('.inpagenavigation-v1__container').toggleClass('dropdown-active');
        });

    // calculate if it is scroll up or down and update stickiness in above and below tablet view
        $(window).on('scroll', () => {
            const st = $(window).scrollTop();
            if (st > this.lastScrollTop) {
                $('.inpagenavigation_v1').css('top', -2);
            } else {
                $('.inpagenavigation_v1').css({
                    transition: 'top 0.5s ease',
                    top: this.$headerElement.height(),
                });
            }
            this.lastScrollTop = st;
        });
    }

    _renderStickyInpage() {
        const herobanner_pos = $('.herobannercontainer').offset().top;
        if (herobanner_pos == 0) {
            alert('hi');
            $('.inpagenavigation_v1').addClass('inpage--sticky');
        } else if (herobanner_pos > 0) {
            alert('hello');
            $('.inpagenavigation_v1').removeClass('inpage--sticky');
        }
    }

    _populateid() {
        const idList = [];
        $('.inpagenavigation_v1 ~ div').each((index, elem) => {
            const randLetter = String.fromCharCode(
                97 + Math.floor(Math.random() * 26)
            );
            const uniqid = randLetter + Math.floor(Math.random() * 10000);

            elem.id = uniqid;
            if (elem.id) {
                idList.push(elem.id);
            }
        });

        $('.inpagenavigation-v1-item > a').each((index, elem) => {
            elem.href = `#${idList[index]}`;
        });
    }

    _isTablet() {
        return window.screen.width <= this.header_tablet;
    }

    _jumpToSection(event) {
        event.preventDefault();
        event.stopPropagation();

        const elementSelector = $(event.target).attr('href');
        const elementTarget = $(elementSelector);
        const headerHeight = this.$headerElement.height();

        if (elementTarget.length) {
            const positionParent = elementTarget;
            const positionParentTop = positionParent.offset().top;
            const inpagenav_height = $('.inpagenavigation-v1__container').height();
            $('html, body').animate(
                {
                    scrollTop: positionParentTop - headerHeight - inpagenav_height,
                },
                '300'
            );
        }
    }

    _changeActiveLink() {
        const gutterspace = 2;
        const scrollbarLocationFromTop = $(window).scrollTop();

        $('.inpagenavigation-v1-item a').each(function () {
            const hashLink = $(this).attr('href');

            const linkOffset =
        $(hashLink).offset().top -
        $('.header-v1').height() -
        $('.inpagenavigation-v1__container').height() -
        gutterspace;

      // distance of link from inpagenav after inpagenav becomes sticky
            if (linkOffset <= scrollbarLocationFromTop) {
                $(this)
          .parent()
          .addClass('default-active');
                $(this)
          .parent()
          .siblings()
          .removeClass('default-active');

                const link_active_text = $(
                    '.inpagenavigation-v1-item.default-active a'
                ).text();
                $('.link_active').text(link_active_text);
            }
        });
    }
}

AEM.registerComponent('.inpagenavigation_v1', Inpagenavigationv1);
