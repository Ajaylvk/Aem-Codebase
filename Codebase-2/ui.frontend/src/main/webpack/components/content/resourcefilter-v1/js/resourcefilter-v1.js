import $ from 'jquery';
import AEM from '../../../../core/js/AemComponent';
import { mobilewidth } from '../../../../core/js/utils/global-variables';

/*eslint-disable */
class ResourceFilterV1 extends AEM.Component {
  constructor(element) {
    super(element);
    this.compName = this.constructor.name;
    this.mobilewidth = mobilewidth;
    this.name = "Resource Filter New";
    this.digitalDataTrackable = true;
    this.title = null;
    this.$titleComponent = null;
  }

  init() {
    var rbItemCount = 0;
    var responseObject = [];
    var isSearchActive = false;
    const assetpath = $(".filter-resources-container").data("assetpath");
    // Global Request Object
    const myRequestObj = {
      requesttype: "",
      names: [],
      values: [],
      searchterm: "",
      assetpath,
      urlparams: [],
    };

    const CONST = {
      BUBBLE_MESSAGE: 'Clear Filters',
      SEARCH_MESSAGE: 'Clear Search',
      NO_SEARCH_RESULT: 'No Search Results Found',
      NORESULTS_MESSAGE : 'No Results Found'
     };

    $(() => {
      populatedatatags();
      formatOptions();
      renderMobileView();
      const url_queryparams = window.location.search;
      const urlParams = new URLSearchParams(url_queryparams);
      const url_searchflag = urlParams.has("searchtext");
      const wcmmode = url_queryparams.includes("wcmmode");
     
      // Extract the value of the data-language attribute
      let languageValue = $(".filter-resources-container").attr("data-language");
      // Set the locale using Granite.I18n.setLocale() for conversion of strings
      if(languageValue){
        Granite.I18n.setLocale(languageValue);
      }
      
      //Static URL search with params, else condition covers first time load
      if (!url_searchflag && !wcmmode) {
        const searchParams = new URLSearchParams(window.location.search);
        const trueUtmParams = $("ul.select")
          .map(function() {
            return this.id;
          })
          .get();

        const dropdownsselected = [];

        const valueschecked = [];

        // Display the key/value pairs

        for (const [key, value] of searchParams.entries()) {
          if (trueUtmParams.includes(key)) {
            dropdownsselected.push(key);

            valueschecked.push(value);
          }
        }
        let modifiedarr1 = [];
        let modifiedarr2 = [];
        valueschecked.forEach(function(el) {
          if (el.includes("$")) {
            const subarr = el.split("$");
            modifiedarr1 = modifiedarr1.concat(subarr);
          }
        });

        valueschecked.forEach(function(el) {
          if (!el.includes("$")) {
            modifiedarr2.push(el);
          }
        });

        const finalvalueschecked = modifiedarr1.concat(modifiedarr2);

        for (let i = 0; i < finalvalueschecked.length; i++) {
          const presentvalue = finalvalueschecked[i];
          const requestvalue = {
            name: "",
            value: "",
            dropdown: "",
          };

          //filter UI interaction for static URL search
          
        let translated_text_bubble= Granite.I18n.get(CONST.BUBBLE_MESSAGE);
        $(".bubble-clear-text").text(translated_text_bubble);
          //$(".bubble-clear-text").text("Clear Filters");
          $("[data-tag='" + presentvalue + "']").addClass("checked");
          const bubblename = $("[data-tag='" + presentvalue + "']").data(
            "name"
          );

          populatebubbles(bubblename, "filter");
          //constructing request object for static URL
          myRequestObj.names.push(
            $("[data-tag='" + presentvalue + "']").data("name")
          );

          requestvalue.name = $("[data-tag='" + presentvalue + "']").data(
            "name"
          );
          requestvalue.value = $("[data-tag='" + presentvalue + "']").data(
            "value"
          );
          requestvalue.dropdown = $("[data-tag='" + presentvalue + "']")
            .parent()
            .attr("name");
          myRequestObj.values.push(requestvalue);
          updateParamsObject(myRequestObj, requestvalue.dropdown, presentvalue);
        }
        myRequestObj.requesttype = "filter";
        //ajax call to fetch response
        fetchResponse(myRequestObj, "", []);
      } else if (url_searchflag && !wcmmode) {
        //if someone wants to do a static keyword search
        //const url_searchkey = window.location.search.split("=")[1];
        const url_searchkey = urlParams.get("searchtext");
        myRequestObj.searchterm = url_searchkey;
        myRequestObj.requesttype = "resourcesearch";
        myRequestObj.names = [];
        myRequestObj.values = [];
        myRequestObj.urlparams = [
          { dropdowntype: "searchtext", optionselected: url_searchkey },
        ];
        isSearchActive = true;
        fetchSearchResponse(myRequestObj);
        updateParams(myRequestObj);
        populatebubbles(url_searchkey, "search");
        let translated_text_searchbubble= Granite.I18n.get(CONST.SEARCH_MESSAGE);
        $(".bubble-clear-text").text(translated_text_searchbubble);
        //$(".bubble-clear-text").text("Clear Search");
        $(".checked").removeClass("checked");
        $(".dropdownoption").removeClass("disableoption");
      } else {
        //on first time load if it is not a static URL search
        fetchInitialResponse(myRequestObj);
      }

      //*****Funtion to link data-values from backend and frontend***********//
      function populatedatatags() {
        var datavalues = [];
        $("li.dropdownoption").each(function() {
          var data = $(this).data("value");
          datavalues.push(data);
        });

        var datavaluesfinal = datavalues.map(function(item) {
          var tag = item.split("/");
          return tag[1];
        });

        $("li.dropdownoption").each(function(i) {
          $(this).attr("data-tag", datavaluesfinal[i]);
        });
      }

      //*************Functions to handle Visual Aspects*************//
      function formatOptions() {
        $("li.dropdownoption").each(function() {
          const length = $(this)
            .find(".checkbox-text")
            .text().length;
          if (length > 27) {
            $(this)
              .find(".checkbox-text")
              .css("top", "unset");
          }
        });
      }

      function renderMobileView() {
        if (isMobile()) {
          $(".filter-resources-container").addClass("render-mobile-filters");
        } else {
          $(".filter-resources-container").removeClass("render-mobile-filters");
        }
      }
      $(window).on("resize", renderMobileView);

      //***************AJAX calls*****************//

      //function to fetch repsonse on 1st load, and clear all bubbles and all dropdowns unchecked
      function fetchInitialResponse(myRequestObj) {
        rbItemCount = 0;
        responseObject = [];
        myRequestObj.searchterm = "";
        myRequestObj.requesttype = "initial_render";
        myRequestObj.names = [];
        myRequestObj.values = [];
        myRequestObj.urlparams = [];
        const urlpath = window.location.pathname;
        const refinedurlpath = urlpath.split(".");
        const sendRequestObject = JSON.stringify(myRequestObj);
        const requestUrl = `${window.location.protocol}//${window.location.host}${refinedurlpath[0]}/jcr:content.assetdata.json`;

        $.ajax({
          method: "POST",

          url: requestUrl,

          data: { inputJson: sendRequestObject },

          success(myJsonresponse) {
            responseObject = myJsonresponse.results;

            renderData(myJsonresponse);
          },
        });

        updateParams(myRequestObj);
      }
      //function to fetch response based on filter interactions
      function fetchResponse(
        myRequestObj,
        dropdown_selected,
        dropdownToPopulate
      ) {
        const requestUrl = constructRequestURL();
        rbItemCount = 0;
        const sendRequestObject = JSON.stringify(myRequestObj);
        responseObject = [];
        $.ajax({
          type: "POST",
          url: requestUrl,
          data: { inputJson: sendRequestObject },

          success(myJsonresponse) {
            responseObject = myJsonresponse.results;
            const dataname = getvaluestopopulate(
              myJsonresponse,
              dropdown_selected,
              dropdownToPopulate
            );

            updatedropdowns(dataname, dropdownToPopulate);
            renderData(myJsonresponse);
          },
        });
      }

      //Constructs Request URL used in AJAX calls
      function constructRequestURL() {
        const urlpath = window.location.pathname;
        const refinedurlpath = urlpath.split(".");
        return `${window.location.protocol}//${window.location.host}${refinedurlpath[0]}/jcr:content.assetdata.json`;
      }

      //*************URL params modifications******************//

      //function to update URL based on filter and Search box interaction.
      function updateParams(myRequestObj) {
        const searchParams = new URLSearchParams(window.location.search);
        const keysToDelete = $("ul.select")
          .map(function() {
            return this.id;
          })
          .get();
        keysToDelete.forEach((key) => searchParams.delete(key));
        searchParams.delete("searchtext");

        const newurl = `${window.location.protocol}//${window.location.host}${window.location.pathname}`;
        window.history.pushState(
          {
            path: newurl,
          },
          "",
          newurl
        );
        myRequestObj.urlparams.forEach((item) => {
          if (history.pushState) {
            if (searchParams.has(item.dropdowntype)) {
              const currentval = searchParams.get(item.dropdowntype); //category's current value in URL
              const append_delimiter = "$";
              let url_val = currentval + append_delimiter;
              let finalval = url_val.concat(item.optionselected);
              searchParams.set(item.dropdowntype, finalval);
            } else {
              searchParams.set(item.dropdowntype, item.optionselected);
            }

            const newurl = `${window.location.protocol}//${
              window.location.host
            }${window.location.pathname}?${searchParams.toString()}`;
            window.history.pushState(
              {
                path: newurl,
              },
              "",
              newurl
            );
          }
        });
      }

      //construct URL params object
      function updateParamsObject(myRequestObj, dkey, option) {
        const urlParamObj = {
          dropdowntype: "",
          optionselected: "",
        };

        urlParamObj.dropdowntype = dkey;
        urlParamObj.optionselected = option;
        myRequestObj.urlparams.push(urlParamObj);
        updateParams(myRequestObj);
      }

      //************Bubbles Functionality**************//

      //Populate bubbles on option select
      function populatebubbles(optionselected, flag) {
        //if previous action was search and next action is filter
        if (flag == "filter" && isSearchActive == true) {
          isSearchActive = false;

          const searchbubble = $(".filter-bubble").data("bubblename");
          removebubbles(searchbubble); //remove search bubble
        }

        //if 1st bubble is populated show bubbles container
        if ($(".filter-bubbles-container").hasClass("hide")) {
          $(".filter-bubbles-container").removeClass("hide");
        }

        //removes all filter bubbles if search bubble has to be populated
        if (flag == "search" && $(".clearall-bubble").siblings().length > 0) {
          $(".clearall-bubble")
            .siblings()
            .remove();
        }

        // creating a bubble
        $(".filter-bubbles-container").append(
          '<div class="filter-bubble" data-bubblename=""><span class="bubble-text"></span><span class="remove-bubble"><svg class="a-icon a-icon--close"><use xlink:href="#close"></use></svg></span></div> '
        );

        $(".filter-bubbles-container")
          .children(".filter-bubble")
          .last()
          .children(".bubble-text")
          .text(optionselected);
        //adding data-bubblename for identification of bubble
        $(".filter-bubbles-container")
          .children(".filter-bubble")
          .last()
          .attr("data-bubblename", optionselected);

        //removing bubble on click of bubble
        $(".filter-bubble")
          .off()
          .on("click", function(e) {
            let bubbleremoved = "";
            let urlparamremoved = "";
            if (flag == "filter") {
              const uncheckoption = $(this)
                .children(".bubble-text")
                .text();
              bubbleremoved = uncheckoption;

              const elem2 = $(".checked ").filter(
                `[data-name='${uncheckoption}']`
              );

              const elem3 = $(".dropdownoption").filter(
                `[data-name='${uncheckoption}']`
              );

              const dropdown_selected = "bubble";
              const dropdownToPopulate = [];

              urlparamremoved = elem3.attr("data-tag");

              $(elem2).removeClass("checked"); //update dropdown option on removing bubble
              e.currentTarget.remove(); //removes bubble from DOM

              //if clicked bubble is last bubble ,hide clearall section
              if ($(".clearall-bubble").siblings().length == 0) {
                $(".filter-bubbles-container").addClass("hide");
                $(".checked").removeClass("checked");
                $(".dropdownoption").removeClass("disableoption"); //unhide all dropdown option
              }

              //update request object for filter bubble
              myRequestObj.names = myRequestObj.names.filter(
                (item) => item !== bubbleremoved
              );

              myRequestObj.values = myRequestObj.values.filter(
                (item) => item.name !== bubbleremoved
              );

              myRequestObj.urlparams = myRequestObj.urlparams.filter(
                (item) => item.optionselected !== urlparamremoved
              );
              var sibblingLength = $(".clearall-bubble").siblings().length;
              if (sibblingLength == 0) {
                fetchInitialResponse(myRequestObj);
              } else {
                fetchResponse(
                  myRequestObj,
                  dropdown_selected,
                  dropdownToPopulate
                );
              }
            }

            //update request object if bubble is searchtext bubble
            if (flag == "search") {
              e.currentTarget.remove();

              if ($(".clearall-bubble").siblings().length == 0) {
                $(".filter-bubbles-container").addClass("hide");
                $(".checked").removeClass("checked");
                $(".dropdownoption").removeClass("disableoption");
                fetchInitialResponse(myRequestObj);
              }
            }

            updateParams(myRequestObj);
          });
      }

      //clear all bubbles
      $(".clearall-bubble").on("click", function(e) {
        $(".filter-bubble").remove();
        $(".filter-bubbles-container").addClass("hide");
        $(".checked").removeClass("checked");
        $(".dropdownoption").removeClass("disableoption");
        myRequestObj.names = [];

        myRequestObj.values = [];

        myRequestObj.urlparams = [];
        myRequestObj.requesttype = "";
        fetchInitialResponse(myRequestObj);

        updateParams(myRequestObj);
      });

      function removebubbles(optionremoved) {
        $(".filter-bubble")
          .filter(function() {
            return $(this).data("bubblename") === optionremoved;
          })
          .remove();
        if ($(".clearall-bubble").siblings().length == 0) {
          $(".filter-bubbles-container").addClass("hide");
        }
      }

      //**************Filters UI interaction functionality*********************//

      $(".dropdownoption").on("click", function(e) {
       
       let translated_text_bubble= Granite.I18n.get(CONST.BUBBLE_MESSAGE);
        $(".bubble-clear-text").text(translated_text_bubble);
        if (isSearchActive) {
          myRequestObj.urlparams = [];
          myRequestObj.searchterm = "";
          updateParams(myRequestObj);
        }
        const dkey = $(this)
          .parent()
          .attr("id");
        const dropdown_key = $(this).data("name");
        const dropdown_value = $(this).data("value");
        const dropdown_id = $(this)
          .parent()
          .attr("id");
        const dropdown_selected = $(this).parent();
        const dropdownToPopulate = [];
        $(e.currentTarget).toggleClass("checked");
        if ($(this).hasClass("checked")) {
          const optionselected = $(this).data("name");
          const tagselected = $(this).data("tag");
          const optionvalue = {
            name: "",
            value: "",
            dropdown: "",
          };

          optionvalue.name = optionselected;
          optionvalue.value = $(this).data("value");
          optionvalue.dropdown = dkey;
          myRequestObj.requesttype = "filter";

          myRequestObj.names.push(optionselected);
          myRequestObj.values.push(optionvalue);
          myRequestObj.searchterm == "";

          updateParamsObject(myRequestObj, dkey, tagselected);

          populatebubbles(optionselected, "filter");

          fetchResponse(myRequestObj, dropdown_selected, dropdownToPopulate);
        } else {
          const optionremoved = $(this).data("name");
          const dropdownParamremoved = $(this).data("tag");
          const optionvalueremoved = $(this).data("value");

          if (!$(".dropdownoption").hasClass("checked")) {
            removebubbles(optionremoved);
            $(".dropdownoption").removeClass("disableoption");
            fetchInitialResponse(myRequestObj);
            updateParams(myRequestObj);
          } else {
            myRequestObj.requesttype = "filter";
            myRequestObj.names = myRequestObj.names.filter(
              (item) => item !== optionremoved
            );

            myRequestObj.values = myRequestObj.values.filter(
              (item) => item.name !== optionremoved
            );

            myRequestObj.urlparams = myRequestObj.urlparams.filter(
              (item) => item.optionselected !== dropdownParamremoved
            );

            myRequestObj.searchterm == "";
            updateParams(myRequestObj);
            removebubbles(optionremoved);
            fetchResponse(myRequestObj, dropdown_selected, dropdownToPopulate);
          }
        }
      });

      /* Menulist Functionality open and close*/

      $(".menulist").on("click", function() {
        $(this).toggleClass("menulist--open");

        if (
          !$(".input-wrapper")
            .find(".menulist--open")
            .find(".list-to-top")
            .hasClass("angle__down--rotate")
        ) {
          $(".input-wrapper")
            .find(".menulist--open")
            .find(".list-to-top")
            .addClass("angle__down--rotate");
        }
        $(this)
          .find(".menulist--close")
          .toggleClass("menulist--close");

        $(this)
          .parent()
          .siblings()
          .find(".menulist--open")
          .toggleClass("menulist--open")
          .siblings(".select")
          .toggleClass("menulist--close");

        $(".input-wrapper")
          .find(".menulist--close")
          .prev()
          .find(".angle__down--rotate")
          .removeClass(".angle__down--rotate");

        $(".menulist--open")
          .siblings(".menulist--close")
          .removeClass("menulist--close");

        $(this)
          .siblings(".select")
          .slideToggle(200);

        $(this)
          .parent()
          .siblings()
          .find(".select")
          .css("display", "none");
      });

      //collapse dropdown on click outside dropdowns
      $(document).on("mouseup", (e) => {
        const container = $(".menulist");

        const selected = $(".select");

        if (
          !container.is(e.target) &&
          container.has(e.target).length === 0 &&
          !selected.is(e.target) &&
          selected.has(e.target).length === 0
        ) {
          $(".select").hide();
          $(".list-to-top").removeClass("angle__down--rotate");
          $(".menulist").removeClass("menulist--open");
        }
      });

      //*****************************Search UI interaction Functionality************//

      // initiate search functionality on mouse click
      $(".search-resources-btn").on("mousedown", (e) => {
        searchresults();
      });

      // initiate search functionality on keypress
      $("#resource-searchbox").on("keypress", function(e) {
        if (e.key == "Enter") {
          searchresults();
        }
      });

      // clear search on cross button click
      $(".clear-search").on("mousedown", (e) => {
        $("#resource-searchbox").val("");
        // fetchInitialResponse(myRequestObj);
        isSearchActive = false;
      });

      // clear search on focus out of input
      $("#resource-searchbox").focusout((e) => {
        $("#resource-searchbox").val("");
      });

      // search UI functionality skeleton
      function searchresults() {
        const resource_searchtext = $("#resource-searchbox").val();
        if (resource_searchtext) {
          myRequestObj.searchterm = resource_searchtext;
          myRequestObj.requesttype = "resourcesearch";
          myRequestObj.names = [];
          myRequestObj.values = [];
          myRequestObj.urlparams = [
            { dropdowntype: "searchtext", optionselected: resource_searchtext },
          ];
          isSearchActive = true;
          fetchSearchResponse(myRequestObj);
          updateParams(myRequestObj);
          populatebubbles(resource_searchtext, "search");
          let translated_text_searchbubble= Granite.I18n.get(CONST.SEARCH_MESSAGE);
          $(".bubble-clear-text").text(translated_text_searchbubble);
          //$(".bubble-clear-text").text("Clear Search");
          $(".checked").removeClass("checked");
          $(".dropdownoption").removeClass("disableoption");
        } else {
          $(".search-tooltip").css("display", "block");
          setTimeout(function removetooltip() {
            $(".search-tooltip").css("display", "none");
          }, 3000);
        }
      }
      //function to render content based on filter interactions
      function fetchSearchResponse(myRequestObj) {
        const requestUrl = constructRequestURL();
        rbItemCount = 0;
        const sendRequestObject = JSON.stringify(myRequestObj);
        responseObject = [];
        $.ajax({
          type: "POST",
          url: requestUrl,
          data: { inputJson: sendRequestObject },

          success(myJsonresponse) {
            responseObject = myJsonresponse.results;
            renderData(myJsonresponse);
          },
        });
      }

      //****************All Response handling helper functions****************//

      //Extracts value from response object
      function getvaluestopopulate(
        myJsonresponse,
        dropdown_selected,
        dropdownToPopulate
      ) {
        const label = $(dropdown_selected).attr("name");

        // iterate over type,content and industry as keys

        // value is an array objects with final key value pairs,we need to display this final key in dropdown option
        var tagobj = myJsonresponse.tags;
        let valuesToPopulate = [];

        for (var key in tagobj) {
          if (key != label) {
            dropdownToPopulate.push(key);
            let dropdownNewValue = tagobj[key];

            const finalarray = dropdownNewValue.map((obj) => {
              let myobj = Object.keys(obj).toString();
              return myobj;
            });

            valuesToPopulate = [...valuesToPopulate, ...finalarray];
          }
        }

        return valuesToPopulate;
      }

      // Update other 2 dropdowns based on extracted values from above function
      function updatedropdowns(dataname, dropdownToPopulate) {
        for (i = 0; i < dropdownToPopulate.length; i++) {
          $(document.getElementById(dropdownToPopulate[i]))
            .children()
            .addClass("disableoption");
        }

        dataname.forEach((item, index) => {
          const elem = $(".input-wrapper")
            .find(`[data-name='${item}']`)
            .removeClass("disableoption");
        });
      }

      //********************Display data functions****************//

      function renderData(data_results) {
        if (data_results.message) {
          $("#loadmore").hide();
          document.getElementById("first-items-set").innerHTML = "";
          document.getElementById("second-items-set").innerHTML = "";
          document.getElementById("popular-data").innerHTML = "";
          document.getElementById("most-recent").innerHTML = "";
          $("#error-message").show();
          $(".recently-created").hide();
          $(".recently-popular").hide();
          $("#first-items-set").hide();
          $("#second-items-set").hide();
          document.getElementById("error-message").innerHTML = "";
          let noresultsfoundmessage = Granite.I18n.get(data_results.message);
          $("#error-message").text(noresultsfoundmessage);

        } else if (
          data_results.promoted.length == 0 &&
          data_results.popular.length == 0 &&
          data_results.results.length == 0
        ) {
          $("#loadmore").hide();
          document.getElementById("first-items-set").innerHTML = "";
          document.getElementById("second-items-set").innerHTML = "";
          document.getElementById("popular-data").innerHTML = "";
          document.getElementById("most-recent").innerHTML = "";
          $("#error-message").show();
          $(".recently-created").hide();
          $(".recently-popular").hide();
          $("#first-items-set").hide();
          $("#second-items-set").hide();
          document.getElementById("error-message").innerHTML = "";
          let noresultsmessage = Granite.I18n.get(CONST.NORESULTS_MESSAGE);
          $("#error-message").text(noresultsmessage);
        } else {
          $("#error-message").hide();
          document.getElementById("error-message").innerHTML = "";
          const parent = document.getElementById("most-recent");
          loadPromoted_PopularResultsFromAPI(
            parent,
            data_results.promoted,
            false
          );
          if (data_results.promoted && data_results.promoted.length > 0) {
            $(".recently-created").show();
          } else {
            $(".recently-created").hide();
          }

          if (data_results.popular && data_results.popular.length > 0) {
            $(".recently-popular").show();
          } else {
            $(".recently-popular").hide();
          }
          const parent2 = document.getElementById("first-items-set");
          loadRestResultsFromAPI(parent2, data_results.results, false);
          const parent4 = document.getElementById("popular-data");
          loadPromoted_PopularResultsFromAPI(
            parent4,
            data_results.popular,
            true
          );
          const parent3 = document.getElementById("second-items-set");
          if (data_results.results && data_results.results.length > 0) {
            $(parent2).show();
          } else {
            $(parent2).hide();
          }
          if (data_results.results && data_results.results.length > 6) {
            $(parent3).show();
          } else {
            $(parent3).hide();
          }
          loadRestResultsFromAPI(parent3, data_results.results, false);
          if (data_results.results && data_results.results.length > 12) {
            document.querySelector("#loadmore").style.display = "block";
            document.querySelector("#loadmore").classList.add("loadmoreItems");
            const loadMoreBtn = document.querySelector("#loadmore");
            loadMoreBtn.addEventListener("click", (e) =>
              loadMoreItemInTheView(e)
            );
          } else {
            document.querySelector("#loadmore").style.display = "none";
          }
        }
      }

      function loadPromoted_PopularResultsFromAPI(parent, data, isPopular) {
        if (data && data.length > 0) {
          let count = 0;
          parent.innerHTML = "";
          let isPopularCount = 0;
          if (isPopular) {
            isPopularCount = 3;
          } else {
            isPopularCount = 2;
          }
          for (count = 0; count < isPopularCount; count++) {
            if (data[count]) {
              const element = document.createElement("div");
              if (isPopular) {
                element.classList.add("nonrecent-element-width");
              } else {
                element.classList.add("recent-element-width");
              }
              element.innerHTML = `<div><picture><img loading="lazy" src="${data[count].imagesrc}" alt="${data[count].alttext}"></picture></div>
  <div class="descriptive-section">
  <div class="sub-heading">${data[count].caption}</div>
  <div class="heading">${data[count].title}</div>
  <div class="paragraph">${data[count].description}</div>
  <div class="link-padding"><a class="text-link" href="${data[count].ctalinkdestination}" 
  target="_blank"> <span class="linkLabel">${data[count].ctalinklabel}</span><i class="arrow-right"></i></a></div></div>`;
              parent.appendChild(element);
            }
          }
        }

        if (data && data.length == 0) {
          parent.innerHTML = "";
        }
      }

      function loadMoreItemInTheView(e) {
        const parent = document.getElementById("second-items-set");
        loadRestResultsFromAPI(parent, responseObject, true);
        if (rbItemCount > responseObject.length) {
          e.target.style.display = "none";
        }
      }

      function loadRestResultsFromAPI(parent, data, loadBtnClick) {
        if (data && data.length > 0) {
          let showItemCount = 0;
          if (loadBtnClick) {
            showItemCount = 11;
          } else {
            showItemCount = 5;
            parent.innerHTML = "";
          }
          const displayItem = rbItemCount;
          for (
            rbItemCount;
            rbItemCount <= displayItem + showItemCount;
            rbItemCount++
          ) {
            if (data[rbItemCount]) {
              const element = document.createElement("div");
              element.classList.add("nonrecent-element-width");
              element.innerHTML = `<div><picture><img loading="lazy" src="${data[rbItemCount].imagesrc}" alt="${data[rbItemCount].alttext}"></picture></div>
          <div class="descriptive-section">
          <div class="sub-heading">${data[rbItemCount].caption}</div>
          <div class="heading">${data[rbItemCount].title}</div>
          <div class="paragraph">${data[rbItemCount].description}</div>
          <div class="link-padding"><a class="text-link" href="${data[rbItemCount].ctalinkdestination}" 
          target="_blank">
          <span class="linkLabel">${data[rbItemCount].ctalinklabel}</span><i class="arrow-right"></i></a></div></div>`;
              parent.appendChild(element);
            }
          }
        }
        if (data && data.length == 0) {
          parent.innerHTML = "";
        }
      }

      //**********Functions for mobile view****************//
      function isMobile() {
        return window.screen.width <= mobilewidth;
      }

      if (window.screen.width <= 416) {
        $(window).on("resize", renderMobileView);
      }

      $(".render-mobile-filters .filter-link").on("click", function(e) {
        $(".filter-form").addClass("filter-mobile");
        $(".filter-back-container").css("display", "block");
      });

      $(".filter-back").on("click", function() {
        $(".filter-form").removeClass("filter-mobile");
        $(".filter-back-container").css("display", "none");
      });
    });
  }
}

/* eslint-enable */
AEM.registerComponent('.resourcefilter-v1', ResourceFilterV1);
