import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';
import toggleNoScrollClass from '../../../../core/js/utils/no-scroll';
import {
    desktop,
    headerTablet,
} from '../../../../core/js/utils/global-variables';
/* eslint-disable */
class NavigationMenuV1 extends AEM.Component {
  constructor(element) {
    super(element);

    this.compName = this.constructor.name;
    this.desktop = desktop;
    this.header_tablet = headerTablet;

    this.selectors = {
      nav: ".headerv1__nav",
      firstLevel: ".headerv1__nav-1-level",
      firstLevelItem: ".headerv1__nav-1-level-item--has-children",
      secondLevel: ".menu-list-selectable",
      secondLevelItem: ".menu-list-selectable-item",
      secondLevelListActive: ".headerv1__nav-2-list--active",
      secondLevelItemTitle: ".headerv1__nav-2-level-item-title",
      thirdLevelList: ".list--single-item.list--mobile-only",
      thirdLevelListActive: "headerv1__nav-3-list--active",
      closeButton: ".headerv1__nav-2-close",
      backButton: ".headerv1__nav-2-back",
      levelThreebackButton: ".l3-menu-back",
    };
    this.states = {
      navActive: "headerv1__nav--active",
      firstLevelItemActive: "headerv1__nav-1-level-item--active",
      secondLevelItemNoChildren: "headerv1__nav-2-level-item--no-children",
    };
  }

  init() {
    this.animationTime = 150;

    this.$nav = $(this.selectors.nav, this.element);
    this.$firstLevel = $(this.selectors.firstLevel, this.element);
    this.$secondLevel = $(this.selectors.secondLevel, this.element);
    this.$secondLevelItem = $(this.selectors.secondLevelItem, this.element);
    this.$thirdLevelList = $(this.selectors.thirdLevelList, this.element);
    this.$thirdLevelListActive = $(
      this.selectors.thirdLevelListActive,
      this.element
    );
    this.$closeButton = $(this.selectors.closeButton, this.element);
    this.$backButton = $(this.selectors.backButton, this.element);
    this.$secondLevelItemTitle = $(
      this.selectors.secondLevelItemTitle,
      this.element
    );
    this.$levelThreebackButton = $(
      this.selectors.levelThreebackButton,
      this.element
    );

    if ($(".navlink-container").length) {
      $(".headerv1__nav").addClass("additionallinks");
    }

    this._attachHandlers();
  }

  _attachHandlers() {
    if (this.$firstLevel) {
      this.$firstLevel.on("click", (event) =>
        this._handleFirstLevelClick(event)
      );
      this.$firstLevel.on("keydown", (event) =>
        this._handleFirstLevelClick(event)
      );
    }

    if (this.$secondLevel) {
      this.$secondLevel.on("click", (event) =>
        this._handleSecondLevelClick(event)
      );
    }

    // if (this.$secondLevelItemTitle) {
    //     this.$secondLevelItemTitle.on('click', event => this._handleSecondLevelClick(event));
    //     this.$secondLevelItemTitle.on('click', event => this._handleSecondLevelItemClick(event));
    // }

    if (this.$closeButton) {
      this.$closeButton.on("click", () => this._handleCloseButtonClick());
    }
    if (this.$backButton) {
      this.$backButton.on("click", () => this._handleBackButtonClick());
    }

    if (this.$levelThreebackButton) {
      this.$levelThreebackButton.on("click", () =>
        this._handlelevelThreebackButtonClick()
      );
    }
  }

  _handleFirstLevelClick(event) {
    if (
      this._firstLevelHasNoChildren(event) ||
      this.constructor._isKeyboardControl(event)
    ) {
      return;
    }

    //if dev center site,then do nothing on first level click
    if ($(".headerv1__nav-1-level-item a").hasClass("dev-center-level0-link")) {
      return;
    }

    this.$firstLevel.children().each((_, firstLevelItem) => {
      if (firstLevelItem === event.target) {
        $(firstLevelItem, this.element).toggleClass(
          this.states.firstLevelItemActive
        );
      } else {
        $(firstLevelItem, this.element).removeClass(
          this.states.firstLevelItemActive
        );
      }
      // By default add to first li of 2nd level
      // setActiveThirdLevel's functionality is added in linklist.js for mobile view,
      // Css is being handled by navigation-menu.scss
    });

    if (!this._isTablet()) {
      $(".headerv1__nav-1-level-item--active").each(function(index) {
        $(this)
          .find("li")
          .removeAttr("data-selected");

        const linkAbsent = $(
          ".headerv1__nav-1-level-item--active .menu-list-selectable-item"
        ).find("a").length;
        if (linkAbsent === 0) {
          $(this)
            .find("li:eq(0)")
            .attr("data-selected", "true");
        }
      });

      $(".headerv1__nav-1-level-item--active").each(function(index) {
        $(this)
          .find(".thirdLevelList")
          .removeClass("headerv1__nav-3-list--active");
        $(this)
          .find(".thirdLevelList:eq(0)")
          .addClass("headerv1__nav-3-list--active");
      });
    }

    if (this._isTablet()) {
      const linkPresent = $(
        ".headerv1__nav-1-level-item--active .menu-list-selectable-item"
      ).find("a").length;
      if (linkPresent) {
        $(".headerv1__nav-1-level-item--active")
          .find(".headerv1__nav-2-level")
          .addClass("mobilePromo-levelthreeabsent");
      }
      const firstTabActive = $(".headerv1__nav-1-level-item").hasClass(
        "headerv1__nav-1-level-item--active"
      );
      if (firstTabActive) {
        $(".tablet-view-contact-button").removeClass("navigationV1_Activate");
        $(".tablet-view-contact-button").css("display", "none");
        if (
          document.getElementsByClassName("header-v1-parent")[0].clientWidth <=
          414
        ) {
          $(".regionChanger-spacing_mobile-view").css("display", "none");
        }
      }
    }

    toggleNoScrollClass(this._firstLevelHasActiveItem());
  }

  _handleSecondLevelClick(event) {
    this.$secondLevel.children().each((_, secondLevelItem) => {
      if (secondLevelItem === event.target && this._checkLevelThree()) {
        $(secondLevelItem, this.element).attr("data-selected", "true");
        this._setActiveThirdLevel();
      } else {
        $(secondLevelItem, this.element).removeAttr("data-selected");
      }
    });
  }

  _checkLevelThree() {
    const linkAbsent = $(
      ".headerv1__nav-1-level-item--active .menu-list-selectable-item"
    ).find("a").length;
    if (linkAbsent === 0) {
      return true;
    }

    return false;
  }

  _setActiveThirdLevel() {
    const indexSelected = $(".menu-list-selectable-item")
      .filter(function() {
        return $(this).attr("data-selected") === "true";
      })
      .index();

    if (this._isTablet()) {
      $(".headerv1__nav-1-level-item--active")
        .find(".headerv1__nav-2-level")
        .addClass("mobile-open");
      $(".headerv1__nav-1-level-item--active")
        .find(".menu-list-selectable")
        .css("display", "none");
      $(".headerv1__nav-1-level-item--active")
        .find(".headerv1__nav-2-back")
        .css("display", "none");
    }

    $(this.selectors.secondLevel)
      .siblings(".headerv1__nav-2-level")
      .find(".thirdLevelList")
      .removeClass(this.selectors.thirdLevelListActive);

    if (indexSelected === 0) {
      $(".headerv1__nav-1-level-item--active").each(function(index) {
        $(this)
          .find(".thirdLevelList")
          .eq(indexSelected)
          .addClass("headerv1__nav-3-list--active");
      });
      // $(this.selectors.secondLevel).siblings('.headerv1__nav-2-level').find('.thirdLevelList').eq(indexSelected)
      // .addClass(this.selectors.thirdLevelListActive);
    } else if (indexSelected > 0) {
      $(".headerv1__nav-1-level-item--active").each(function(index) {
        $(this)
          .find(".thirdLevelList")
          .removeClass("headerv1__nav-3-list--active");
      });

      $(".headerv1__nav-1-level-item--active").each(function(index) {
        $(this)
          .find(".thirdLevelList")
          .eq(indexSelected)
          .addClass("headerv1__nav-3-list--active");
      });

      // $(this.selectors.secondLevel).siblings('.headerv1__nav-2-level').find('.thirdLevelList')
      // .removeClass(this.selectors.thirdLevelListActive);
      //     $(this.selectors.secondLevel).siblings('.headerv1__nav-2-level').find('.thirdLevelList').eq(indexSelected)
      // .addClass(this.selectors.thirdLevelListActive);
    }
  }

  _handleSecondLevelItemClick(event) {
    // on mobile/tablet we don't want to follow the link if we have 3rd level items,
    // instead we just want to open the accordion (handled by linklist.js)
    if (this._isTablet() && this._hasThirdLevelItems(event)) {
      event.preventDefault();
    }
  }

  _handleBurgerMenuClick() {
    this.$nav.toggleClass(this.states.navActive);
    toggleNoScrollClass();
    this.$firstLevel
      .children()
      .first()
      .focus();
    this.$burgerMenuButton.children().toggle(this.animationTime);
    this.$searchIcon.toggle(this.animationTime);
  }

  _handleCloseButtonClick() {
    this._removeAllFirstLevelActiveClasses();
    this._removeAllSecondLevelActiveClasses();
    this._removeAllThirdLevelActiveClasses();
    toggleNoScrollClass(false);
  }

  _handleBackButtonClick() {
    this._removeAllFirstLevelActiveClasses();
    this._removeAllSecondLevelActiveClasses();
    this._removeAllThirdLevelActiveClasses();
    this.$firstLevel
      .children()
      .first()
      .focus();
    $(".tablet-view-contact-button").addClass("navigationV1_Activate");
    $(".tablet-view-contact-button").css("display", "block");
    if (
      document.getElementsByClassName("header-v1-parent")[0].clientWidth <= 414
    ) {
      $(".regionChanger-spacing_mobile-view").css("display", "block");
    }
  }

  _handlelevelThreebackButtonClick() {
    $(".headerv1__nav-2-level").removeClass("mobile-open");
    $(".headerv1__nav-1-level-item--active")
      .find(".headerv1__nav-2-back")
      .css("display", "flex");
    $(".headerv1__nav-1-level-item--active")
      .find(".menu-list-selectable")
      .css("display", "block");
  }

  _handleEscapePress(event) {
    if (event.key !== "Escape") {
      return;
    }
    this._removeAllFirstLevelActiveClasses();
    this._removeAllSecondLevelActiveClasses();
    this._removeAllThirdLevelActiveClasses();
    this.$firstLevel
      .children()
      .first()
      .focus();
  }

  _handleWindowResize() {
    this._removeAllFirstLevelActiveClasses();
    this._removeAllSecondLevelActiveClasses();
    this._removeAllThirdLevelActiveClasses();
    this.$searchForm.css("width", "");
  }

  _removeAllFirstLevelActiveClasses() {
    this.$firstLevel.children().each((_, firstLevelItem) => {
      $(firstLevelItem, this.element).removeClass(
        this.states.firstLevelItemActive
      );
    });
  }

  _removeAllSecondLevelActiveClasses() {
    $(".menu-list-selectable")
      .children()
      .each(() => {
        $(".menu-list-selectable-item").removeAttr("data-selected");
      });
  }

  _removeAllThirdLevelActiveClasses() {
    $(".menu-list-selectable")
      .siblings(".headerv1__nav-2-level")
      .find(".thirdLevelListActive")
      .removeClass(this.states.thirdLevelListActive);
  }

  _isTablet() {
    return window.screen.width <= this.header_tablet;
  }

  static _isKeyboardControl(event) {
    return (
      event.key !== undefined && event.key !== "Enter" && event.key !== " "
    );
  }

  _firstLevelHasActiveItem() {
    return (
      this.$firstLevel.children().filter(`.${this.states.firstLevelItemActive}`)
        .length >= 1
    );
  }

  _firstLevelHasNoChildren(event) {
    return !$(event.target).hasClass(this.selectors.firstLevelItem.slice(1));
  }

  _hasThirdLevelItems(event) {
    return !$(event.target)
      .parents()
      .hasClass(this.states.secondLevelItemNoChildren);
  }
}
/* eslint-enable */
AEM.registerComponent('.navigation_menu-v1', NavigationMenuV1);
