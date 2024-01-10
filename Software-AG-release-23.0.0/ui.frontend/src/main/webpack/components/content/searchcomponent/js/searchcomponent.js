import $ from "jquery";

import AEM from "../../../../core/js/AemComponent";
import debounce from "../../../../core/js/utils/debounce";
import { desktop } from "../../../../core/js/utils/global-variables";

class headerSearch extends AEM.Component {
  /* eslint-disable */
  constructor(element) {
    super(element);

    this.compName = this.constructor.name;
    this.desktop = desktop;

    this.selectors = {
      navigationMenu: ".child-div-2",
      iconsV1: ".headerV1__icons",
      searchIconV1button: ".headerV1_icon-button",
      searchIconV1: ".headerV1__icons-search",
      searchIconOpenV1: ".headerV1__icons-search-open",
      searchIconCloseV1: ".headerV1__icons-search-close",
      searchFormV1: ".headerV1__search-form",
      searchFormInputV1: ".headerV1__search-form-input",
      searchFormSubmitV1: ".headerV1__search-form-submit",
    };
    this.states = {
      searchFormActiveV1: "headerV1__search-form--active",
      searchSubmitShowV1: "headerV1__search-form-submit--show",
      deactiveSearch: "deactiveSearch",
      topPosition: "top__position-search",
    };
  }

  init() {
    this.animationTimeV1 = 150;
    this.$searchIconV1 = $(this.selectors.searchIconV1, this.element);
    this.$searchIconOpenV1 = $(this.selectors.searchIconOpenV1, this.element);
    this.$searchIconCloseV1 = $(this.selectors.searchIconCloseV1, this.element);
    this.$searchFormV1 = $(this.selectors.searchFormV1, this.element);
    this.$searchFormInputV1 = $(this.selectors.searchFormInputV1, this.element);
    this.$searchFormSubmitV1 = $(
      this.selectors.searchFormSubmitV1,
      this.element
    );

    const vh1 = window.innerHeight * 0.01;
    document.documentElement.style.setProperty("--vh", `${vh1}px`);

    window.addEventListener("resize", () => {
      document.documentElement.style.setProperty("--vh", `${vh1}px`);
    });
    this._attachHandlersV1();
  }

  _attachHandlersV1() {
    if (this.$searchIconOpenV1) {
      this.$searchIconOpenV1.on("click", () =>
        this._handleSearchIconOpenClickV1()
      );
    }
    $(".headerV1_Cross_icon").on("click", () => this._closeSearchOverlayV1());
    $(".headerV1__search-form-input").on("focusin", () =>
      this._handleSearchFormInputFocusInV1()
    );
    $(".headerV1__search-form-input").on("focusout", (event) =>
      this._handleSearchFormInputFocusOutV1(event)
    );
    $(".headerV1__search-form-input").on("keyup", () =>
      this._handleSearchFormInputKeypressV1()
    );
    $(".headerV1__search-form-submit").on("focusout", () =>
      this._handleSearchFormSubmitFocusOutV1()
    );
    $(".headerV1_Cross_icon").on("focusout", () =>
      this._handleSearchFormSubmitFocusOutV1()
    );
    // $('.headerV1__search-form').css('right', $('.child-div-3').width());
    $(document).on("keydown", (event) => this._handleEscapePressV1(event));
    $(window).on("resize", () => this._handleWindowResizeV1());

    const handleScrollV1 = debounce(
      () => {
        this._toggleStickyV1();
      },
      10,
      true
    );
    window.addEventListener("scroll", handleScrollV1);
  }

  _toggleStickyV1() {
    $(this.element)
      .parent()
      .toggleClass(this.states.sticky, window.scrollY !== 0);
  }

  _handleEscapePressV1(event) {
    if (event.key !== "Escape") {
    }
  }

  _handleSearchIconOpenClickV1() {
    $(".headerV1__icons-search").addClass(this.states.deactiveSearch);
    $(".headerV1__search-form-input").val("");
    $(".headerV1__search-form-input").focus();
    $(".headerV1__search-form-input")
      .get(0)
      .focus();
    setTimeout(function() {
      jQuery(".headerV1__search-form-input")
        .get(0)
        .focus();
    }, 10);
    $(".headerV1__search-form-input").attr("tabindex", 0);
    if (document.getElementById("header-v1-parent-Id")) {
      if (
        document.getElementsByClassName("header-v1-parent")[0].clientWidth > 414
      ) {
        $(".headerV1__search-form").css("right", $(".child-div-3").width());
      }
    }
    if (document.getElementById("header-v2-parent-Id")) {
      if (
        document.getElementsByClassName("header-v2-parent")[0].clientWidth > 414
      ) {
        $(".headerV1__search-form").css("right", $(".child-div-3").width());
      }
    }
    if (document.getElementById("header-v1-parent-Id")) {
      if (
        document.getElementsByClassName("header-v1-parent")[0].clientWidth <=
        414
      ) {
        $(".headerV1__search-form").css(
          "right",
          $(".child-div-3").width() - 12
        );
      }
    }
    if (document.getElementById("header-v2-parent-Id")) {
      if (
        document.getElementsByClassName("header-v2-parent")[0].clientWidth <=
        414
      ) {
        $(".headerV1__search-form").css(
          "right",
          $(".child-div-3").width() - 12
        );
      }
    }
    this.$searchIconV1.addClass("form-width-inactive");
    if (document.getElementById("header-v1-parent-Id")) {
      if (
        document.getElementsByClassName("header-v1-parent")[0].clientWidth > 768
      ) {
        $(".child-div-2").css("visibility", "hidden");
        $(".child-div-2").css("pointer-events", "none");
      }
    }
    if (document.getElementById("header-v2-parent-Id")) {
      if (
        document.getElementsByClassName("header-v2-parent")[0].clientWidth > 768
      ) {
        $(".child-div-2").css("visibility", "hidden");
        $(".child-div-2").css("pointer-events", "none");
      }
    }
    $(".headerV1__search-form").addClass(this.states.searchFormActiveV1);
    this._setWidthOfSearchFormV1();
  }

  _handleSearchFormInputFocusInV1() {
    $(".headerV1__search-form").addClass(this.states.searchFormActiveV1);
    $(".headerV1__search-form-input").attr("tabindex", 0);
    $("headerV1_Cross_icon").attr("tabindex", 0);
  }

  _handleSearchFormInputFocusOutV1(event) {
    // we don't want to close the search-overlay if the focus changes
    // from input to submit-button because the website might be navigated with a keyboard
    if (this._isFocusChangeToSubmitButtonV1(event)) {
      return;
    }
    this._closeSearchOverlayV1();
  }

  _handleSearchFormSubmitFocusOutV1() {
    this._closeSearchOverlayV1();
  }

  _handleSearchFormInputKeypressV1() {
    // const showSearchFormSubmitV1 = $('.headerV1__search-form-input').val().length > 0;
    // $('.headerV1_Cross_icon').toggleClass('headerV1_Cross_icon-submit--show', showSearchFormSubmitV1);
  }

  _handleWindowResizeV1() {
    if (document.getElementById("header-v1-parent-Id")) {
      if (
        document.getElementsByClassName("header-v1-parent")[0].clientWidth > 768
      ) {
        $(".headerV1__icons-search").removeClass(this.states.deactiveSearch);
        $(".headerV1__search-form").removeClass(this.states.searchFormActiveV1);
        this.$searchIconV1.removeClass("form-width-inactive");
        $(".child-div-2").css("visibility", "visible");
        $(".child-div-2").css("pointer-events", "auto");
      }
    }
    if (document.getElementById("header-v2-parent-Id")) {
      if (
        document.getElementsByClassName("header-v2-parent")[0].clientWidth > 768
      ) {
        $(".headerV1__icons-search").removeClass(this.states.deactiveSearch);
        $(".headerV1__search-form").removeClass(this.states.searchFormActiveV1);
        this.$searchIconV1.removeClass("form-width-inactive");
        $(".child-div-2").css("visibility", "visible");
        $(".child-div-2").css("pointer-events", "auto");
      }
    }
  }

  _closeSearchOverlayV1() {
    $(".headerV1__icons-search").removeClass(this.states.deactiveSearch);
    this.$searchIconV1.removeClass("form-width-inactive");
    if (document.getElementById("header-v1-parent-Id")) {
      if (
        document.getElementsByClassName("header-v1-parent")[0].clientWidth > 768
      ) {
        $(".child-div-2").css("visibility", "visible");
        $(".child-div-2").css("pointer-events", "auto");
      }
    }
    if (document.getElementById("header-v2-parent-Id")) {
      if (
        document.getElementsByClassName("header-v2-parent")[0].clientWidth > 768
      ) {
        $(".child-div-2").css("visibility", "visible");
        $(".child-div-2").css("pointer-events", "auto");
      }
    }
    $(".headerV1__search-form-input").val("");
    this._handleSearchFormInputKeypressV1();
    $(".headerV1__search-form").removeClass(this.states.searchFormActiveV1);
    $(".headerV1_Cross_icon").attr("tabindex", -1);
    $(".headerV1__search-form-input").attr("tabindex", -1);
  }

  _setWidthOfSearchFormV1() {
    $(".headerV1__search-form").css(
      "width",
      `${this._calcWidthOfHeaderV1()}px`
    );
  }

  _calcWidthOfHeaderV1() {
    if (document.getElementById("header-v1-parent-Id")) {
      if (
        document.getElementsByClassName("header-v1-parent")[0].clientWidth > 768
      ) {
        return (
          document.getElementsByClassName("header-v1-parent")[0].clientWidth -
          $(".child-div-1").width() -
          $(".child-div-3").width() -
          65
        );
      }
    }
    if (document.getElementById("header-v2-parent-Id")) {
      if (
        document.getElementsByClassName("header-v2-parent")[0].clientWidth > 768
      ) {
        return (
          document.getElementsByClassName("header-v2-parent")[0].clientWidth -
          $(".child-div-1").width() -
          $(".child-div-3").width() -
          65
        );
      }
    }
    if (document.getElementById("header-v1-parent-Id")) {
      if (
        document.getElementsByClassName("header-v1-parent")[0].clientWidth <=
          768 &&
        document.getElementsByClassName("header-v1-parent")[0].clientWidth >=
          414
      ) {
        return (
          document.getElementsByClassName("header-v1-parent")[0].clientWidth -
          $(".child-div-3").width() -
          19
        );
      }
    }
    if (document.getElementById("header-v2-parent-Id")) {
      if (
        document.getElementsByClassName("header-v2-parent")[0].clientWidth <=
          768 &&
        document.getElementsByClassName("header-v2-parent")[0].clientWidth >=
          414
      ) {
        return (
          document.getElementsByClassName("header-v2-parent")[0].clientWidth -
          $(".child-div-3").width() -
          19
        );
      }
    }
    if (document.getElementById("header-v1-parent-Id")) {
      if (
        document.getElementsByClassName("header-v1-parent")[0].clientWidth < 414
      ) {
        return (
          document.getElementsByClassName("header-v1-parent")[0].clientWidth -
          $(".child-div-3").width() -
          4
        );
      }
    }
    if (document.getElementById("header-v2-parent-Id")) {
      if (
        document.getElementsByClassName("header-v2-parent")[0].clientWidth < 414
      ) {
        return (
          document.getElementsByClassName("header-v2-parent")[0].clientWidth -
          $(".child-div-3").width() -
          4
        );
      }
    }
  }

  _isMobile() {
    return window.screen.width <= this.desktop;
  }

  _isFocusChangeToSubmitButtonV1(ev) {
    return (
      ev.relatedTarget &&
      ev.relatedTarget.isEqualNode($(".headerV1__search-form-submit").get(0))
    );
  }

  static _isKeyboardControlV1(event) {
    return (
      event.key !== undefined && event.key !== "Enter" && event.key !== " "
    );
  }
}
/* eslint-enable */
AEM.registerComponent(".headerSearch-v1", headerSearch);
