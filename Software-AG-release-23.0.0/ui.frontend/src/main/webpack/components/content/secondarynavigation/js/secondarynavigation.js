import $, { event } from 'jquery';
import AEM from '../../../../core/js/AemComponent';
import {
    desktop,
    headerTablet,
} from '../../../../core/js/utils/global-variables';

class Secondarynavigation extends AEM.Component {
    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Secondary Navigation';
        this.desktop = desktop;
        this.header_tablet = headerTablet;

        this.selectors = {

            headerElement: '.header-v2',
            secondaryNavBox: '.secondarynav__container',
            secondaryNavItem: '.secondarynav-item',
            secondaryNavItemLink: '.secondarynav__link',
        };
    }

    init() {
        this._attachEvents();
        // this._handleNavitemActive();
        this._handleBackForwardNavigation();

              // Listen for the popstate event (back/forward button clicks)
    }

    _attachEvents() {
        const self = this;
        $(() => {
            if (this._isTablet()) {
                $('.secondarynav__container').addClass('secondarynav-mobileview');
            }
        });

        $(window).on('resize', () => {
            if (self._isTablet()) {
                $('.secondarynav__container').addClass('secondarynav-mobileview');
            }
        });

        $('.secondarynav-label-wrapper').on('click', (event) => {
            if (self._isTablet()) {
                event.preventDefault();
                $('.secondarynav__container').toggleClass('secondarynav-dropdown-active');
                $('.secondarynav-item').removeClass('secondarynav-dropdown-list-active');
                $('.secondarynav-item').removeClass('active');
            } else {
                $('.secondarynav-item').removeClass('active');
            }
        });


        $('.secondarynav-item').on('click', function () {
            $('.secondarynav-item').removeClass('active');
            // Check if the clicked element has the class
            if ($(this).hasClass('secondarynav-dropdown-list-active')) {
                // If it has the class, remove it
                $(this).removeClass('secondarynav-dropdown-list-active');
            } else {
                // If it doesn't have the class, remove the class from all .secondarynav-item elements
                $('.secondarynav-item').removeClass('secondarynav-dropdown-list-active');
                // Add the class to the clicked .secondarynav-item
                $(this).addClass('secondarynav-dropdown-list-active');
            }
        });


        $(document).on('click', (event) => {
            if (!$(event.target).closest('.secondarynav-item').length) {
                $('.secondarynav-item').removeClass('secondarynav-dropdown-list-active');
            }
        });
    }

    _isTablet() {
        return window.screen.width <= this.header_tablet;
    }

    _handleBackForwardNavigation() {
    // Retrieve the index from session storage
        $('.secondarynav-item').removeClass('active');

        // const latestClickedItemIndex = sessionStorage.getItem('latestClickedItemIndex');

        const url = window.location.pathname; // Get the current URL
        const secondaryNavItemLinks = $('.secondarynav__containerwrapper a');

        // Find the navigation item link that corresponds to the current URL
        const matchingLink = secondaryNavItemLinks.filter((index, link) => {
            const href = $(link).attr('href');
            const absoluteURL = new URL(href, window.location.origin).pathname;

            return absoluteURL === url || href === url; // Compare both absolute and relative paths
        });


        if (matchingLink.length > 0) {
            // Add the 'active' class to the corresponding navigation item

            if (matchingLink.hasClass('secondarynavtitle__link')) {
                const matchingNavtitle = matchingLink.closest('.secondarynav-title');
                matchingNavtitle.addClass('active');
            } else {
                const matchingNavItem = matchingLink.closest('.secondarynav-item');
                matchingNavItem.addClass('active');
            }
        }
    }


}

AEM.registerComponent('.secondarynavigation', Secondarynavigation);
