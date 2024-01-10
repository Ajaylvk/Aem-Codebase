import $ from "jquery";
import AEM from "../../../../core/js/AemComponent";

class RegionChangerV1 extends AEM.Component {
  /* eslint-disable */
  constructor(element) {
    super(element);
    this.compName = this.constructor.name;
    this.$regionButton = $(".region-v1__button", this.element);
    this.$regionButtonClose = $(".region-v1__options-close", this.element);
    this.$regionItemHolder = $(".region-v1_items-holder", this.element);
    this.$regionItems = $(".region-v1-items", this.element);
    this.selectors = {
      button: ".region-v1-items__button",
      content: ".region-v1-items__content-container",
    };
    this.states = {
      open: "region-v1-items--open",
      active: "region-v1__holder--active",
      globe: "activeGlobe",
      navigationbarActive: "navigationV1_Activate",
    };
    this.$regionItemButton = null;
    this.$regionItemContent = null;
  }

  init() {
    if (window.digitalData) {
      let regionLangList = [];
      let activeRegionLang;
      var regionLanguageList = $(".region-v1-items");
      for (let i = 0; i < regionLanguageList.length; i++) {
        if ($(".region-v1-items")[i].dataset["regionlanguage"]) {
          regionLangList.push(
            $(".region-v1-items")[i].dataset["regionlanguage"]
          );
        } else {
          regionLangList.push("");
        }
      }
      regionLangList.forEach((ele) => {
        if (ele) {
          if (
            ele.trim() ==
            window.digitalData["page"]["attributes"]["language"].trim()
          ) {
            activeRegionLang = ele;
          }
        }
      });
      if (this.$regionItems) {
        const activeTab = activeRegionLang;
        if (activeTab) {
          this.$regionItems.toArray().forEach((ele) => {
            if (ele.dataset["regionlanguage"]) {
              if (
                ele.dataset["regionlanguage"].trim().toString() ===
                activeTab.trim().toString()
              ) {
                const currentTab = ele;
                currentTab.style.borderColor = "#E7EBEE";
              } else ele.style.border = "";
            }
          });
        }
      }
    }
    if (this.$regionButton) {
      this.$regionButton.off().on("click", () => this.showRegionList());
    }
    if (this.$regionButtonClose) {
      this.$regionButtonClose.off().on("click", () => this.closeRegionList());
    }
    this.$regionItemButton = $(this.element).find(this.selectors.button);
    this.$regionItemContent = $(this.element).find(this.selectors.content);
    $(document).on("mouseup", (e) => {
      {
        const containerRegion = $("#region-container");
        const regionHolder = $(".region-v1__holder");
        const regionGlobe = $(".region-v1__button");
        // if the target of the click isn't the container nor a descendant of the container
        if (
          !containerRegion.is(e.target) &&
          containerRegion.has(e.target).length === 0 &&
          !regionGlobe.is(e.target) &&
          regionGlobe.has(e.target).length === 0
        ) {
          regionHolder.removeClass(this.states.active);
          $(".region-v1__button").removeClass(this.states.globe);
          if (document.getElementById("header-v1-parent-Id")) {
            $(".headerV1__icons-menu-regionChanger").removeClass(
              this.states.navigationbarActive
            );
          }
          if (document.getElementById("header-v2-parent-Id")) {
            $(".headerV2__icons-menu-regionChanger").removeClass(
              this.states.navigationbarActive
            );
          }
          if (document.getElementsByClassName("header-v1-parent")[0]) {
            if (
              document.getElementsByClassName("header-v1-parent")[0]
                .clientWidth <= 414 &&
              $(".child-div-2").hasClass(this.states.navigationbarActive)
            ) {
              $(".headerV1__icons-menu-close")[0].style.display = "block";
            }
          }
          if (document.getElementsByClassName("header-v2-parent")[0]) {
            if (
              document.getElementsByClassName("header-v2-parent")[0]
                .clientWidth <= 414 &&
              $(".child-div-2").hasClass(this.states.navigationbarActive)
            ) {
              $(".headerV2__icons-menu-close")[0].style.display = "block";
            }
          }
        }
      }
    });
    $(window).on("resize", () => this._handleWindowResizeRegionChangerV1());
    $(".region-v1-list a").on("click", (event) => {
      event.preventDefault();
    });
    $(".region-v1-list li")
      .off()
      .on("click", function(e) {
        //Get region locale for clicked items
        const regional_language = $(this).parent();
        const regional_countryurl = $(regional_language).attr("href");
        const regional_pathname = new URL(regional_countryurl).pathname;
        // const regional_locale = regional_pathname.substring(
        //   regional_pathname.indexOf("/") + 1,
        //   regional_pathname.lastIndexOf(".")
        // );

        let temp_locale_arr = [];
        temp_locale_arr = regional_pathname.split("/");
        let temp_locale = temp_locale_arr[1];
        if (temp_locale.includes(".")) {
          temp_locale = temp_locale.split(".")[0];
        }
        const regional_locale = temp_locale;

        const regionChanger_pathname = window.location.pathname;
        const regionchanger_domain = window.location.origin;
        const myRequestObj = {
          country: regional_locale, //en_us
          current_page: regionChanger_pathname,
          current_domain: regionchanger_domain,
          current_authored_url: regional_countryurl,
          regionlist: [],
        };

        //get region locales for all items in region changer

        $(".region-v1-list a").each(function() {
          const region_url = $(this).attr("href");
          const region_path = new URL(region_url).pathname;
          let temp_locale_arr = [];
          temp_locale_arr = region_path.split("/");
          let temp_locale = temp_locale_arr[1];
          if (temp_locale.includes(".")) {
            temp_locale = temp_locale.split(".")[0];
          }
          const region_locale = temp_locale;

          // const region_locale = region_path.substring(
          //   region_path.indexOf("/") + 1,
          //   region_path.lastIndexOf(".")
          // );

          // const regex = /^\/(\w+)_(\w+)\//;
          // const match = region_path.match(regex);
          // const region_locale = match ? match[1] + "_" + match[2] : "";
          myRequestObj.regionlist.push(region_locale);
        });

        window.history.pushState(
          {
            path: window.location.href,
          },
          "",
          window.location.href
        );

        const urlpath = window.location.pathname;
        const refinedurlpath = urlpath.split(".");
        const requestUrl = `${window.location.protocol}//${window.location.host}${refinedurlpath[0]}/jcr:content.regionchanger.html`;
        const sendRequestObject = JSON.stringify(myRequestObj);
        $.ajax({
          method: "POST",
          url: requestUrl,
          data: { reqValue: sendRequestObject },
          success(myresponse) {
            //alert(myresponse);
            const redirect_url = myresponse;
            location.replace(redirect_url);
          },
          error: function() {
            const homepageredirect = regional_countryurl;
            location.replace(homepageredirect);
          },
        });
      });
  }

  _handleWindowResizeRegionChangerV1() {
    $(".region-v1__holder").removeClass(this.states.active);
    $(".region-v1__button").removeClass(this.states.globe);
  }

  _toggle(event) {
    const clickedButton = $(event.currentTarget);
    const clickedContainer = clickedButton.closest(
      ".region-v1-items__container"
    );

    if (clickedContainer.hasClass(this.states.open)) {
      clickedContainer.removeClass(this.states.open);
    } else {
      $(`.${this.states.open}`).removeClass(this.states.open);
      clickedContainer.addClass(this.states.open);
    }
  }

  showRegionList() {
    if ($(".region-v1__holder").hasClass(this.states.active)) {
      $(".region-v1__holder").removeClass(this.states.active);
      $(".region-v1__button").removeClass(this.states.globe);
    } else {
      let topValue;
      let topRibInView;
      let isInViewportTopRib;
      const topRib = document.getElementById("topribbon");
      if (topRib) {
        topRibInView = topRib.getBoundingClientRect();
        isInViewportTopRib =
          topRibInView.top >= 0 &&
          topRibInView.left >= 0 &&
          topRibInView.bottom <=
            (window.innerHeight || document.documentElement.clientHeight) &&
          topRibInView.right <=
            (window.innerWidth || document.documentElement.clientWidth);
      }
      if (document.getElementById("at_promo_banner")) {
        if (document.getElementById("header-v1-parent-Id")) {
          topValue =
            document.getElementById("at_promo_banner").clientHeight +
            document.getElementById("header-v1-parent-Id").clientHeight;
        }
        if (document.getElementById("header-v2-parent-Id")) {
          topValue =
            document.getElementById("at_promo_banner").clientHeight +
            document.getElementById("header-v2-parent-Id").clientHeight;
        }
      } else if (document.getElementById("topribbon") && isInViewportTopRib) {
        if (document.getElementById("header-v1-parent-Id")) {
          topValue =
            document.getElementById("topribbon").clientHeight +
            document.getElementById("header-v1-parent-Id").clientHeight;
        }
        if (document.getElementById("header-v2-parent-Id")) {
          topValue =
            document.getElementById("topribbon").clientHeight +
            document.getElementById("header-v2-parent-Id").clientHeight;
        }
      } else {
        if (document.getElementById("header-v1-parent-Id")) {
          topValue = document.getElementById("header-v1-parent-Id")
            .clientHeight;
        }
        if (document.getElementById("header-v2-parent-Id")) {
          topValue = document.getElementById("header-v2-parent-Id")
            .clientHeight;
        }
      }
      $(".region-v1__holder").css("top", topValue);

      $(".region-v1__holder").addClass(this.states.active);
      $(".region-v1__button").addClass(this.states.globe);
    }
  }

  closeRegionList() {
    $(this.element)
      .find(".region-v1__holder")
      .removeClass(this.states.active);
    $(".region-v1__button").removeClass(this.states.globe);
  }
}
/* eslint-enable */
AEM.registerComponent(".regionchanger-v1", RegionChangerV1);
