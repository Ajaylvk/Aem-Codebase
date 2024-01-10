import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';
import { desktop } from '../../../../core/js/utils/global-variables';
import debounce from '../../../../core/js/utils/debounce';
/* eslint-disable */
class HeaderV1 extends AEM.Component {
  constructor(element) {
    super(element);

    this.compName = this.constructor.name;
    this.desktop = desktop;

    this.selectors = {
      childDiv1: ".child-div-1",
      childDiv2: ".child-div-2",
      childDiv3: ".child-div-3",
      childDiv4: ".child-div-4",
      parentDiv: ".header-v1-parent",
      burgerMenuButtonV1: ".headerV1__icons-menu",
      headerIconFirstV1: ".headerV1__icons-first",
      headerIconSecondV1: ".headerV1__icons-second",
      regionChangerText: "region-changer-text",
      regionChangerSpacing: "regionChanger-spacing_mobile-view",
      nav: ".headerv1__nav",
    };
    this.states = {
      headerV1Sticky: "headerv1--sticky",
      navigationbarActive: "navigationV1_Activate",
      active: "region-v1__holder--active",
      navActive: "headerv1__nav--active",
    };
  }

  init() {
    this.animationTimeV1 = 150;
    this.$nav = $(this.selectors.nav, this.element);
    this.$regionChangerText = $(this.selectors.regionChangerText, this.element);
    this.$burgerMenuButtonV1 = $(
      this.selectors.burgerMenuButtonV1,
      this.element
    );
    this.$headerIconFirstV1 = $(this.selectors.headerIconFirstV1, this.element);
    this.$headerIconSecondV1 = $(
      this.selectors.headerIconSecondV1,
      this.element
    );
    this.$childDiv2_Class = $(this.selectors.childDiv2, this.element);
    this.$parentDiv = $(this.selectors.parentDiv, this.element);
    this.$childDiv3 = $(this.selectors.childDiv3, this.element);
    this.$childDiv1 = $(this.selectors.childDiv1, this.element);
    this.$childDiv4 = $(this.selectors.childDiv4, this.element);
    this.$regionChangerSpacing = $(
      this.selectors.regionChangerSpacing,
      this.element
    );
    const vh = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh}px`);
    window.addEventListener("resize", () => {
      document.documentElement.style.setProperty("--vh", `${vh}px`);
      this._calcWidthOfChildDivV2();
      this._handleWindowResizeHeader();
    });

    if (
      $("#header-v1-parent-Id").hasClass(
        "input__colorselect-text-background-text-component-color3"
      )
    ) {
      $(".header-v1").addClass("header-dark-mode");
    }
    this._attachHandlersV1();
  }

  _attachHandlersV1() {
    // if (this.$childDiv2_Class) {
    //   this.$childDiv2_Class.css("width", `${this._calcWidthOfChildDivV2()}px`);
    // }
    $(".region-changer-text")
      .off()
      .on("click", () => this.showRegionList2());
    $(window).on("resize", () => this._handleWindowResizeHeader());
    const handleScrollV1 = debounce(
      () => {
        this._toggleStickyV1();
      },
      10,
      true
    );
    window.addEventListener("scroll", handleScrollV1);
    $(".headerV1__icons-menu-close").on("click", () =>
      this._handleBurgerMenuClose()
    );
    $(".headerV1__icons-menu-open").on("click", () =>
      this._handleBurgerMenuClickV1()
    );
    $(".headerV1__icons-menu-regionChanger").on("click", () =>
      this._closeRegion()
    );
  }

  showRegionList2() {
    $(".region-v1__holder").toggleClass(this.states.active);
    $(".headerV1__icons-menu-close").css("display", "none");
    $(".headerV1__icons-menu-regionChanger").addClass(
      this.states.navigationbarActive
    );
    $("html").addClass("no-scroll");
    $("body").addClass("no-scroll");
  }

  _closeRegion() {
    $(".headerV1__icons-menu-close").css("display", "block");
    $(".headerV1__icons-menu-regionChanger").removeClass(
      this.states.navigationbarActive
    );
    $(".region-v1__holder").removeClass(this.states.active);
  }

  _handleBurgerMenuClose() {
    $("html").removeClass("no-scroll");
    $("body").removeClass("no-scroll");
    this.$nav.removeClass(this.states.navActive);
    $(".headerV1__icons-menu-open")[0].style.display = "block";
    $(".headerV1__icons-menu-close")[0].style.display = "none";
    $(".child-div-2").removeClass(this.states.navigationbarActive);
    $(".tablet-view-contact-button").removeClass(
      this.states.navigationbarActive
    );
    $(".region-changer-text").removeClass(this.states.navigationbarActive);
    document.getElementsByClassName("tablet-view-contact-button")[0].innerHTML =
      " ";
    $(".regionchanger-v1").addClass("pull-right");
  }

  _handleWindowResizeHeader() {
    $(".headerV1__icons-menu-regionChanger").removeClass(
      this.states.navigationbarActive
    );
    $("html").removeClass("no-scroll");
    $("body").removeClass("no-scroll");
    $(".headerV1__icons-menu-open")[0].style.display = "block";
    $(".headerV1__icons-menu-close")[0].style.display = "none";
    $(".child-div-2").removeClass(this.states.navigationbarActive);
    $(".tablet-view-contact-button").removeClass(
      this.states.navigationbarActive
    );
    $(".region-changer-text").removeClass(this.states.navigationbarActive);
    document.getElementsByClassName("tablet-view-contact-button")[0].innerHTML =
      " ";
    $(".regionchanger-v1").addClass("pull-right");
  }

  _toggleStickyV1() {
    $(this.element)
      .parent()
      .toggleClass(this.states.headerV1Sticky, window.scrollY !== 0);
  }

  _calcWidthOfChildDivV2() {
    return (
      document.getElementsByClassName("header-v1-parent")[0].clientWidth -
      this.$childDiv1[0].clientWidth -
      this.$childDiv3[0].clientWidth
    );
  }

  _handleBurgerMenuClickV1() {
    $("html").addClass("no-scroll");
    $("body").addClass("no-scroll");
    this.$nav.addClass(this.states.navActive);
    $(".headerV1__icons-menu-open")[0].style.display = "none";
    $(".headerV1__icons-menu-close")[0].style.display = "block";
    $(".child-div-2").addClass(this.states.navigationbarActive);
    $(".tablet-view-contact-button").addClass(this.states.navigationbarActive);
    // if(document.getElementsByClassName('header-v1-parent')[0].clientWidth <= 414) {
    //     const headerNavLevelHeight = $('.header__nav-1-level').height() + 4;
    //     $('.tablet-view-contact-button').css('top', headerNavLevelHeight)
    // }
    // if(document.getElementsByClassName('header-v1-parent')[0].clientWidth > 414) {
    //     const headerNavLevelHeight = $('.header__nav-1-level').height() + 24;
    //     $('.tablet-view-contact-button').css('top', headerNavLevelHeight)
    // }
    if (
      document.getElementsByClassName("header-v1-parent")[0].clientWidth <= 414
    ) {
      const tryButton = document.getElementById("secondary-try-button");
      const cloneTryBtn = tryButton.cloneNode(true);
      document
        .getElementsByClassName("tablet-view-contact-button")[0]
        .appendChild(cloneTryBtn);
    }
    const contactButton = document.getElementById("primary-contact-button");
    const cloneContactBtn = contactButton.cloneNode(true);
    document
      .getElementsByClassName("tablet-view-contact-button")[0]
      .appendChild(cloneContactBtn);
    if (
      document.getElementsByClassName("header-v1-parent")[0].clientWidth <= 414
    ) {
      $(".regionchanger-v1").removeClass("pull-right");
    }
  }
}
/* eslint-enable */
AEM.registerComponent('.header-v1', HeaderV1);
