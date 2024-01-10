import $ from 'jquery';

import AEM from '../../../../core/js/AemComponent';
import { mobilewidth } from '../../../../core/js/utils/global-variables';

class FastTrackServices extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor

    constructor(element) {
        super(element);

        this.name = 'FastTrack Services';

        this.digitalDataTrackable = true;
    }
  // eslint-disable-next-line class-methods-use-this

    init() {
        let rbItemCount = 0;
        let responseObject = [];
        let isSearchActive = false;
        const assetpath = $('.filter-resources-container').data('assetpath');
        const errorMsg = $('.filter-results-population').data('errormessage');
    // Global Request Object
        const myRequestObj = {
            requesttype: '',
            names: [],
            values: [],
            searchterm: '',
            assetpath,
            urlparams: [],
            dropdown_order: [],
        };

        $(() => {
            populatedatatags();
            formatOptions();
            renderMobileView();
            getdropdownOrder();
            function getdropdownOrder() {
                $('ul.select').map(function () {
                    myRequestObj.dropdown_order.push(this.id);
                });
            }
      // Dropdown options functionality
      /* Toggle class Checked and create request object*/
            $(window).on('resize', renderMobileView);

      /* Menulist Functionality open and close*/
            $('.menulist').on('click', function () {
                $(this).toggleClass('menulist--open');

                if (
                    !$('.input-wrapper')
            .find('.menulist--open')
            .find('.list-to-top')
            .hasClass('angle__down--rotate')
                ) {
                    $('.input-wrapper')
            .find('.menulist--open')
            .find('.list-to-top')
            .addClass('angle__down--rotate');
                }
                $(this)
          .find('.menulist--close')
          .toggleClass('menulist--close');

                $(this)
          .parent()
          .siblings()
          .find('.menulist--open')
          .toggleClass('menulist--open')
          .siblings('.select')
          .toggleClass('menulist--close');

                $('.input-wrapper')
          .find('.menulist--close')
          .prev()
          .find('.angle__down--rotate')
          .removeClass('.angle__down--rotate');

                $('.menulist--open')
          .siblings('.menulist--close')
          .removeClass('menulist--close');

                $(this)
          .siblings('.select')
          .slideToggle(200);

                $(this)
          .parent()
          .siblings()
          .find('.select')
          .css('display', 'none');
            });

      // collapse dropdown on click outside dropdowns
            $(document).on('mouseup', (e) => {
                const container = $('.menulist');

                const selected = $('.select');

                if (
                    !container.is(e.target) &&
          container.has(e.target).length === 0 &&
          !selected.is(e.target) &&
          selected.has(e.target).length === 0
                ) {
                    $('.select').hide();
                    $('.list-to-top').removeClass('angle__down--rotate');
                    $('.menulist').removeClass('menulist--open');
                }
            });

      // Dropdown option Click
            $('.dropdownoption').on('click', function (e) {
                $('.bubble-clear-text').text($('.bubble-filter-placeholder').text());
                if (isSearchActive) {
                    myRequestObj.urlparams = [];
                    myRequestObj.searchterm = '';
                    updateParams(myRequestObj);
                }
                const dkey = $(this)
          .parent()
          .attr('id');
                const dropdown_key = $(this).data('name');
                const dropdown_value = $(this).data('value');
                const dropdown_id = $(this)
          .parent()
          .attr('id');
                const dropdown_selected = $(this).parent();
                const dropdownToPopulate = [];
                $(e.currentTarget).toggleClass('checked');
                if ($(this).hasClass('checked')) {
                    const optionselected = $(this).data('name');
                    const tagselected = $(this).data('tag');
                    const optionvalue = {
                        name: '',
                        value: '',
                        dropdown: '',
                    };

                    optionvalue.name = optionselected;
                    optionvalue.value = $(this).data('value');
                    optionvalue.dropdown = dkey;
                    myRequestObj.requesttype = 'filter';

                    myRequestObj.names.push(optionselected);
                    myRequestObj.values.push(optionvalue);
          // myRequestObj.urlparams.push(urlParamObj);
                    myRequestObj.searchterm == '';

                    updateParamsObject(myRequestObj, dkey, tagselected);

                    populatebubbles(optionselected, 'filter');

                    fetchResponse(myRequestObj, dropdown_selected, dropdownToPopulate);
                } else {
                    const optionremoved = $(this).data('name');
                    const dropdownParamremoved = $(this).data('tag');
                    const optionvalueremoved = $(this).data('value');

                    if (!$('.dropdownoption').hasClass('checked')) {
                        removebubbles(optionremoved);
                        $('.dropdownoption').removeClass('disableoption');
                        fetchInitialResponse(myRequestObj);
                        updateParams(myRequestObj);
                    } else {
                        myRequestObj.requesttype = 'filter';
                        myRequestObj.names = myRequestObj.names.filter(
                            item => item !== optionremoved
                        );

                        myRequestObj.values = myRequestObj.values.filter(
                            item => item.name !== optionremoved
                        );

                        myRequestObj.urlparams = myRequestObj.urlparams.filter(
                            item => item.optionselected !== dropdownParamremoved
                        );

                        myRequestObj.searchterm == '';
                        updateParams(myRequestObj);
                        removebubbles(optionremoved);
                        fetchResponse(myRequestObj, dropdown_selected, dropdownToPopulate);
                    }
                }
            });

      // Search interaction
      // Create request object for search
            $('.search-resources-btn').on('mousedown', (e) => {
                searchresults();
            });

            $('#resource-searchbox').on('keypress', (e) => {
                if (e.key == 'Enter') {
                    searchresults();
                }
            });

      // clear search on cross button click
            $('.clear-search').on('mousedown', (e) => {
                $('#resource-searchbox').val('');
        // fetchInitialResponse(myRequestObj);
                isSearchActive = false;
            });

      // clear search on focus out of input
            $('#resource-searchbox').focusout((e) => {
                $('#resource-searchbox').val('');
            });

      // Clear Bubble
            $('.clearall-bubble').on('click', (e) => {
                $('.filter-bubble').remove();
                $('.filter-bubbles-container').addClass('hide');
                $('.checked').removeClass('checked');
                $('.dropdownoption').removeClass('disableoption');
                myRequestObj.names = [];

                myRequestObj.values = [];

                myRequestObj.urlparams = [];
                myRequestObj.requesttype = '';
                fetchInitialResponse(myRequestObj);

                updateParams(myRequestObj);
            });

            if (window.screen.width <= 416) {
                $(window).on('resize', renderMobileView);
            }

            $('.render-mobile-filters .filter-link').on('click', (e) => {
                $('.filter-form').addClass('filter-mobile');
                $('.filter-back-container').css('display', 'block');
            });

            $('.filter-back').on('click', () => {
                $('.filter-form').removeClass('filter-mobile');
                $('.filter-back-container').css('display', 'none');
            });

            const url_queryparams = window.location.search;
            const urlParams = new URLSearchParams(url_queryparams);
            const url_searchflag = urlParams.has('searchtext');
            const wcmmode = url_queryparams.includes('wcmmode');

      // Static URL resource library implentation URL params
            if (!url_searchflag && !wcmmode) {
                const searchParams = new URLSearchParams(window.location.search);

                const trueUtmParams = $('ul.select')
          .map(function () {
              return this.id;
          })
          .get();
        // Display the key/value pairs
                const dropdownsselected = [];

                const valueschecked = [];

                for (const [key, value] of searchParams.entries()) {
                    if (trueUtmParams.includes(key)) {
                        dropdownsselected.push(key);

                        valueschecked.push(value);
                    }
                }
                let modifiedarr1 = [];
                const modifiedarr2 = [];
                valueschecked.forEach((el) => {
                    if (el.includes('$')) {
                        const subarr = el.split('$');
                        modifiedarr1 = modifiedarr1.concat(subarr);
                    }
                });

                valueschecked.forEach((el) => {
                    if (!el.includes('$')) {
                        modifiedarr2.push(el);
                    }
                });

                const finalvalueschecked = modifiedarr1.concat(modifiedarr2);

                for (let i = 0; i < finalvalueschecked.length; i++) {
                    const presentvalue = finalvalueschecked[i];
                    const requestvalue = {
                        name: '',
                        value: '',
                        dropdown: '',
                    };

          // filter UI interaction
                    $('.bubble-clear-text').text($('.bubble-filter-placeholder').text());
                    $(`[data-tag='${presentvalue}']`).addClass('checked');
                    const bubblename = $(`[data-tag='${presentvalue}']`).data('name');

                    populatebubbles(bubblename, 'filter');

                    myRequestObj.names.push(
                        $(`[data-tag='${presentvalue}']`).data('name')
                    );

                    requestvalue.name = $(`[data-tag='${presentvalue}']`).data('name');
                    requestvalue.value = $(`[data-tag='${presentvalue}']`).data('value');
                    requestvalue.dropdown = $(`[data-tag='${presentvalue}']`)
            .parent()
            .attr('name');
                    myRequestObj.values.push(requestvalue);

                    updateParamsObject(myRequestObj, requestvalue.dropdown, presentvalue);
                }
                myRequestObj.requesttype = 'filter';
                fetchResponse(myRequestObj, '', []);
            } else if (url_searchflag && !wcmmode) {
        // const url_searchkey = window.location.search.split('=')[1];
                const url_searchkey = urlParams.get('searchtext');
                myRequestObj.searchterm = url_searchkey;
                myRequestObj.requesttype = 'resourcesearch';
                myRequestObj.names = [];
                myRequestObj.values = [];
                myRequestObj.urlparams = [
                    { dropdowntype: 'searchtext', optionselected: url_searchkey },
                ];
                isSearchActive = true;
                fetchSearchResponse(myRequestObj);
                updateParams(myRequestObj);
                populatebubbles(url_searchkey, 'search');
                $('.bubble-clear-text').text($('.bubble-search-placeholder').text());
                $('.checked').removeClass('checked');
                $('.dropdownoption').removeClass('disableoption');
            } else {
                fetchInitialResponse(myRequestObj);
            }

            function populatedatatags() {
                const datavalues = [];
                $('li.dropdownoption').each(function () {
                    const data = $(this).data('value');
                    datavalues.push(data);
                });

                const datavaluesfinal = datavalues.map((item) => {
                    const tag = item.split('/');
                    return tag[1];
                });

                $('li.dropdownoption').each(function (i) {
                    $(this).attr('data-tag', datavaluesfinal[i]);
                });
            }

            function formatOptions() {
                $('li.dropdownoption').each(function () {
                    const length = $(this)
            .find('.checkbox-text')
            .text().length;
                    if (length > 25) {
                        $(this)
              .find('.checkbox-text')
              .css('top', 'unset');
                    }
                });
            }

      // Populate bubbles on option select
            function populatebubbles(optionselected, flag) {
        // if previous action was search and next action is filter
                if (flag == 'filter' && isSearchActive == true) {
                    isSearchActive = false;

                    const searchbubble = $('.filter-bubble').data('bubblename');
                    removebubbles(searchbubble); // remove search bubble
                }

        // if 1st bubble is populated show bubbles container
                if ($('.filter-bubbles-container').hasClass('hide')) {
                    $('.filter-bubbles-container').removeClass('hide');
                }

        // removes all filter bubbles if search bubble has to be populated
                if (flag == 'search' && $('.clearall-bubble').siblings().length > 0) {
                    $('.clearall-bubble')
            .siblings()
            .remove();
                }

        // creating a bubble
                $('.filter-bubbles-container').append(
                    '<div class="filter-bubble" data-bubblename=""><span class="bubble-text"></span><span class="remove-bubble"><svg class="a-icon a-icon--close"><use xlink:href="#close"></use></svg></span></div> '
                );

                $('.filter-bubbles-container')
          .children('.filter-bubble')
          .last()
          .children('.bubble-text')
          .text(optionselected);
        // adding data-bubblename for identification of bubble
                $('.filter-bubbles-container')
          .children('.filter-bubble')
          .last()
          .attr('data-bubblename', optionselected);

        // removing bubble on click of bubble
                $('.filter-bubble')
          .off()
          .on('click', function (e) {
              let bubbleremoved = '';
              let urlparamremoved = '';
              if (flag == 'filter') {
                  const uncheckoption = $(this)
                .children('.bubble-text')
                .text();
                  bubbleremoved = uncheckoption;

                  const elem2 = $('.checked ').filter(
                      `[data-name='${uncheckoption}']`
                  );

                  const elem3 = $('.dropdownoption').filter(
                      `[data-name='${uncheckoption}']`
                  );

                  const dropdown_selected = 'bubble';
                  const dropdownToPopulate = [];

                  urlparamremoved = elem3.attr('data-tag');

                  $(elem2).removeClass('checked'); // update dropdown option on removing bubble
                  e.currentTarget.remove(); // removes bubble from DOM

              // if clicked bubble is last bubble ,hide clearall section
                  if ($('.clearall-bubble').siblings().length == 0) {
                      $('.filter-bubbles-container').addClass('hide');
                      $('.checked').removeClass('checked');
                      $('.dropdownoption').removeClass('disableoption'); // unhide all dropdown option
                  }

              // update request object for filter bubble
                  myRequestObj.names = myRequestObj.names.filter(
                      item => item !== bubbleremoved
                  );

                  myRequestObj.values = myRequestObj.values.filter(
                      item => item.name !== bubbleremoved
                  );

                  myRequestObj.urlparams = myRequestObj.urlparams.filter(
                      item => item.optionselected !== urlparamremoved
                  );
                  const sibblingLength = $('.clearall-bubble').siblings().length;
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

            // update request object if bubble is searchtext bubble
              if (flag == 'search') {
                  e.currentTarget.remove();

                  if ($('.clearall-bubble').siblings().length == 0) {
                      $('.filter-bubbles-container').addClass('hide');
                      $('.checked').removeClass('checked');
                      $('.dropdownoption').removeClass('disableoption');
                      fetchInitialResponse(myRequestObj);
                  }
              }

              updateParams(myRequestObj);
          });
            }

      // function to render initial contents

            function fetchInitialResponse(myRequestObj) {
                rbItemCount = 0;
                responseObject = [];
                myRequestObj.searchterm = '';
                myRequestObj.requesttype = 'initial_render';
                myRequestObj.names = [];
                myRequestObj.values = [];
                myRequestObj.urlparams = [];
                const urlpath = window.location.pathname;
                const refinedurlpath = urlpath.split('.');
                const sendRequestObject = JSON.stringify(myRequestObj);
                const requestUrl = `${window.location.protocol}//${window.location.host}${refinedurlpath[0]}/jcr:content.fasttrackservicedata.json`;

                $.ajax({
                    method: 'POST',
                    url: requestUrl,
                    data: { inputJson: sendRequestObject },

                    success(myJsonresponse) {
                        responseObject = myJsonresponse.results;
                        renderData(myJsonresponse);
                    },
                });

                updateParams(myRequestObj);
            }

      // AJAX call
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
                    type: 'POST',
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

            function fetchSearchResponse(myRequestObj) {
                const requestUrl = constructRequestURL();
                rbItemCount = 0;
                const sendRequestObject = JSON.stringify(myRequestObj);
                responseObject = [];
                $.ajax({
                    type: 'POST',
                    url: requestUrl,
                    data: { inputJson: sendRequestObject },

                    success(myJsonresponse) {
                        responseObject = myJsonresponse.results;
                        renderData(myJsonresponse);
                    },
                });
            }

            function constructRequestURL() {
                const urlpath = window.location.pathname;
                const refinedurlpath = urlpath.split('.');
                return `${window.location.protocol}//${window.location.host}${refinedurlpath[0]}/jcr:content.fasttrackservicedata.json`;
            }

            function removebubbles(optionremoved) {
                $('.filter-bubble')
          .filter(function () {
              return $(this).data('bubblename') === optionremoved;
          })
          .remove();
                if ($('.clearall-bubble').siblings().length == 0) {
                    $('.filter-bubbles-container').addClass('hide');
                }
            }

            function updateParamsObject(myRequestObj, dkey, option) {
                const urlParamObj = {
                    dropdowntype: '',
                    optionselected: '',
                };

                urlParamObj.dropdowntype = dkey;
                urlParamObj.optionselected = option;
                myRequestObj.urlparams.push(urlParamObj);
                updateParams(myRequestObj);
            }

            function updateParams(myRequestObj) {
                const searchParams = new URLSearchParams(window.location.search);
                const keysToDelete = $('ul.select')
          .map(function () {
              return this.id;
          })
          .get();
                keysToDelete.forEach(key => searchParams.delete(key));
                searchParams.delete('searchtext');

                const newurl = `${window.location.protocol}//${window.location.host}${window.location.pathname}`;
                window.history.pushState(
                    {
                        path: newurl,
                    },
                    '',
                    newurl
                );
                myRequestObj.urlparams.forEach((item) => {
                    if (history.pushState) {
                        if (searchParams.has(item.dropdowntype)) {
                            const currentval = searchParams.get(item.dropdowntype); // category's current value in URL
                            const append_delimiter = '$';
                            const url_val = currentval + append_delimiter;
                            const finalval = url_val.concat(item.optionselected);
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
                            '',
                            newurl
                        );
                    }
                });
            }

      // search functionality

            function searchresults() {
                const resource_searchtext = $('#resource-searchbox').val();
                if (resource_searchtext) {
                    myRequestObj.searchterm = resource_searchtext;
                    myRequestObj.requesttype = 'resourcesearch';
                    myRequestObj.names = [];
                    myRequestObj.values = [];
                    myRequestObj.urlparams = [
                        { dropdowntype: 'searchtext', optionselected: resource_searchtext },
                    ];
                    isSearchActive = true;
                    fetchSearchResponse(myRequestObj);
                    updateParams(myRequestObj);
                    populatebubbles(resource_searchtext, 'search');
                    $('.bubble-clear-text').text($('.bubble-search-placeholder').text());
                    $('.checked').removeClass('checked');
                    $('.dropdownoption').removeClass('disableoption');
                } else {
                    $('.search-tooltip').css('display', 'block');
                    setTimeout(() => {
                        $('.search-tooltip').css('display', 'none');
                    }, 3000);
                }
            }

      // Extracts value from response object
            function getvaluestopopulate(
                myJsonresponse,
                dropdown_selected,
                dropdownToPopulate
            ) {
                const label = $(dropdown_selected).attr('name');

        // iterate over type,content and industry as keys

        // value is an array objects with final key value pairs,we need to display this final key in dropdown option
                const tagobj = myJsonresponse.tags;
                let valuesToPopulate = [];

                for (const key in tagobj) {
                    if (key != label) {
                        dropdownToPopulate.push(key);
                        const dropdownNewValue = tagobj[key];

                        const finalarray = dropdownNewValue.map((obj) => {
                            const myobj = Object.keys(obj).toString();
              //   myobj.trim();
              //   myobj.replace(/['"]+/g, "");

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
            .addClass('disableoption');
                }

                dataname.forEach((item, index) => {
                    const elem = $('.input-wrapper')
            .find(`[data-name='${item}']`)
            .removeClass('disableoption');
                });
            }

      // Pranita's renderdata function signature

            function renderData(data_results) {
                if (data_results.message && data_results.statuscode) {
                    $('#loadmore').hide();
                    document.getElementById('second-items-set').innerHTML = '';
                    $('#error-message').show();
                    $('#second-items-set').hide();
                    document.getElementById('error-message').innerHTML = '';
                    document.getElementById('error-message').innerHTML =
            data_results.message;
                } else if (data_results.message) {
                    $('#loadmore').hide();
                    document.getElementById('second-items-set').innerHTML = '';
                    $('#error-message').show();
                    $('#second-items-set').hide();
                    document.getElementById('error-message').innerHTML = '';
                    document.getElementById('error-message').innerHTML = errorMsg;
                } else if (data_results.results.length == 0) {
                    $('#loadmore').hide();
                    document.getElementById('second-items-set').innerHTML = '';
                    $('#error-message').show();
                    $('#second-items-set').hide();
                    document.getElementById('error-message').innerHTML = '';
                    document.getElementById('error-message').innerHTML = errorMsg;
                } else {
                    $('#error-message').hide();
                    document.getElementById('error-message').innerHTML = '';
                    const parent3 = document.getElementById('second-items-set');
                    if (data_results.results && data_results.results.length > 0) {
                        $(parent3).show();
                    } else {
                        $(parent3).hide();
                    }
                    loadRestResultsFromAPI(parent3, data_results.results, false);
                    if (data_results.results && data_results.results.length > 14) {
                        document
              .getElementsByClassName('loadmore-container')[0]
              .classList.add('loadmore-button-spacing');
                        document.querySelector('#loadmore').style.display = 'block';
                        document.querySelector('#loadmore').classList.add('loadmoreItems');
                        const loadMoreBtn = document.querySelector('#loadmore');
                        loadMoreBtn.addEventListener('click', e => loadMoreItemInTheView(e));
                    } else {
                        document.querySelector('#loadmore').style.display = 'none';
                    }
                }
            }

            function loadMoreItemInTheView(e) {
                const parent = document.getElementById('second-items-set');
                loadRestResultsFromAPI(parent, responseObject, true);
                if (rbItemCount > responseObject.length) {
                    e.target.style.display = 'none';
                    document
            .getElementsByClassName('loadmore-container')[0]
            .classList.remove('loadmore-button-spacing');
                }
            }

            function loadRestResultsFromAPI(parent, data, loadBtnClick) {
                if (data && data.length > 0) {
                    let showItemCount = 0;
                    if (loadBtnClick) {
                        showItemCount = 8;
                    } else {
                        showItemCount = 14;
                        parent.innerHTML = '';
                    }
                    const displayItem = rbItemCount;
                    for (
                        rbItemCount;
                        rbItemCount <= displayItem + showItemCount;
                        rbItemCount++
                    ) {
                        if (data[rbItemCount]) {
                            const fasttrackCounter = data[rbItemCount].fsiconcolor;
                            let creditvalue = '';
                            if (data[rbItemCount].credits == '1') {
                                creditvalue = 'Credit';
                            } else if (
                                data[rbItemCount].credits == '0' ||
                data[rbItemCount].credits == '' ||
                data[rbItemCount].credits == undefined
                            ) {
                                creditvalue = '';
                            } else {
                                creditvalue = 'Credits';
                            }
                            const element = document.createElement('div');
                            element.classList.add('nonrecent-element-width');
                            element.innerHTML = `
                            <a class="descriptive-section" target="_blank" href="${
    data[rbItemCount].ctalinkdestination
}">
                            <div class="fasttrack-icon">
                                 <picture>
                                      <img class="svgicon ${fasttrackCounter}" src="${
    data[rbItemCount].imagesrc
}" alt="${data[rbItemCount].alttext}"/>
                                 </picture>
                            </div>
                            <div class="sub-heading ${
    data[rbItemCount].imagesrc
        ? 'fasttrack-icon-padding'
        : ''
}">
                                 ${data[rbItemCount].caption}
                            </div>
                            <div class="heading">${
    data[rbItemCount].title
}</div>
                            <div class="paragraph">${
    data[rbItemCount].description
}</div>
                            <div class="link-padding"><span class="text-link">
                                      <span class="linkLabel">${
    data[rbItemCount].ctalinklabel
}
                                      </span>
                                      <i class="arrow-right"></i>
                                      </span>
                            </div>
                       </a>`;
                            parent.appendChild(element);
                        }
                    }
                }
                if (data && data.length == 0) {
                    parent.innerHTML = '';
                }
            }

            function renderMobileView() {
                if (isMobile()) {
                    $('.filter-resources-container').addClass('render-mobile-filters');
                } else {
                    $('.filter-resources-container').removeClass('render-mobile-filters');
                }
            }

      //   check if width is mobile
            function isMobile() {
                return window.screen.width <= mobilewidth;
            }
        });
    }
}
{
  /* <span class="creditLabel">${
    data[rbItemCount].credits == undefined
        ? ''
        : data[rbItemCount].credits
}
                                      <span id="credit">${creditvalue}</span>
                                      </span> */
}

AEM.registerComponent('.fasttrackservicesfilter', FastTrackServices);
