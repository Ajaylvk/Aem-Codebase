$(document).ready(function(){

    var productVersions = [];
    var productFamilies = [];
    var productLines = [];
    var searchContains = "";
    var deliveryTypes = [];

    var selector = getSelector();
    var searchTerm = getParameterValues('q');

    var mobMediaQuery = window.matchMedia("(max-width: 991px)");
    var desktopMediaQuery = window.matchMedia("(min-width: 992px)");

    if (selector != null) {
        // apply source facet for search API
    } else if (searchTerm != null) {
        searchContains = searchTerm;
    }

    $('.emp-input-searchbar').on('click', function(){
        $(this).parents('.emp-search-inner-wrap').css('outline', '2px solid #000');
    })

    $('.emp-input-searchbar').on('focus', function(){
        $(this).parents('.emp-search-inner-wrap').css('outline', '2px solid #000');
    })

    $('.emp-input-searchbar').on('blur', function(){
        $(this).parents('.emp-search-inner-wrap').css('outline', 'none');
    })

    // sort by filter
    $('.sort-by span svg').on('click', function(){
        $('.sort-by-filters').toggleClass('block-active');
        $(this).toggleClass('rotate');
    })

    // facet filter section - mobile view
    $('.mob-filter-wrap-btn').on('click', function(){
        $('.filterSidenav').css('width', '100%');
    })

    $('.closeFilterBtn svg').on('click', function() {
        $('.filterSidenav').css('width', '0');
    })

    // filter block section
    $('.emp-expand-icon, .emp-filter-results.mob .emp-expand-icon').on('click', function(){
        $(this).addClass('block-inactive');
        $(this).siblings('.emp-collapse-icon').addClass('block-active');
        $(this).parents('.emp-filter-results').children('.emp-filter-options').addClass('block-inactive');
        $(this).parents('.emp-filter-results').children('.search-filter-wrapper').addClass('block-inactive');
    })

    $('.emp-collapse-icon, .emp-filter-results.mob .emp-collapse-icon').on('click', function(){
        $(this).removeClass('block-active');
        $(this).siblings('.emp-expand-icon').removeClass('block-inactive');
        $(this).parents('.emp-filter-results').children('.emp-filter-options').removeClass('block-inactive');
        $(this).parents('.emp-filter-results').children('.search-filter-wrapper').removeClass('block-inactive');
    })

    $('.emp-filter-results, .emp-filter-results.mob').on('keyup','.search-filter-wrapper.block-active input', function(){
        $(this).siblings('.clear-input').addClass('block-active');

        var value = $(this).val().toLowerCase();
        if (value !== '') {
            $(this).parents('.emp-filter-results').find('.view-more-less-wrapper').hide();
        } else {
            $(this).parents('.emp-filter-results').find('.view-more-less-wrapper').show();
        }
        $(this).parent().siblings(".emp-filter-options").find(".emp-filter-options-parent li").filter(function() {
            $(this).toggle($(this).find('label').text().toLowerCase().indexOf(value) > -1)
        });
    })

    $('.emp-filter-results, .emp-filter-results.mob').on('click','.clear-input.block-active', function(){
        var value = '';
        $(this).siblings('input').val(value);
        $(this).removeClass('block-active');
        $(this).parent().siblings(".emp-filter-options").find(".emp-filter-options-parent li").filter(function() {
            $(this).toggle($(this).find('label').text().toLowerCase().indexOf(value) > -1)
        });
        $(this).parents('.emp-filter-results').find('.view-more-less-wrapper').show();
    })

    $('.emp-filter-results, .emp-filter-results.mob').on('click','.clear-all', function(){
        $(this).hide();
        $(this).parents(".emp-filter-tag").siblings(".emp-filter-options").find("li.emp-filter-options-child input").each(function() {
            this.checked = false;
            var filterValue = $(this).val();
            if(this.checked == false){
                $('.emp-filter-results .clear-all').hide();
                if (desktopMediaQuery.matches) {
                    $(this).parents('.emp-facets').parents('.emp-search-results').siblings('.emp-searchResultsWrapper').find('.emp-facetsSelectedFiltersList span[data-filter-value = "' + filterValue + '"]').parents('.emp-facetsSelectedFiltersList').addClass('block-inactive');
                }
                if (mobMediaQuery.matches) {
                    $(this).parents('.emp-facets-filter.mobile').parents('.emp-counts').siblings('.emp-searchResultsWrapper.mob').find('.emp-facetsSelectedFiltersList span[data-filter-value = "' + filterValue + '"]').parents('.emp-facetsSelectedFiltersList').addClass('block-inactive');
                }
            }
        });
    })

    $('.emp-filter-results, .emp-filter-results.mob').on('click','.emp-filter-options-child input', function(){
        $(this).parents(".emp-filter-results").find("button.clear-all").show();
    })

    $('.emp-facetsSelectedFiltersWrap').on('click', '.emp-facetsSelectedFiltersList span' , function(){
        $(this).parents('.emp-facetsSelectedFiltersList').addClass('block-inactive');
        var facetGroup = $(this).data('filter-key');
        var facetValue = $(this).data('filter-value');
        if (desktopMediaQuery.matches) {
            $('.emp-facets').find('.emp-filter-tag:contains("' + facetGroup + '")').siblings('.emp-filter-options').find('li.emp-filter-options-child input[value="' + facetValue + '"]').prop('checked', false);
        }
        if (mobMediaQuery.matches) {
            $('.emp-facets-filter.mobile').find('.emp-filter-tag:contains("' + facetGroup + '")').siblings('.emp-filter-options').find('li.emp-filter-options-child input[value="' + facetValue + '"]').prop('checked', false);
        }
        if ($(this).parents(".emp-facetsSelectedFiltersWrap").find(".emp-facetsSelectedFiltersList").length == $(this).parents(".emp-facetsSelectedFiltersWrap").find(".emp-facetsSelectedFiltersList.block-inactive").length) {
            $(this).parents(".emp-searchResultsWrapper").addClass('block-inactive');
        }
        if ($('.emp-search-results.variation2').length > 0) {
            productLines = getProductLines();
            productFamilies = getProductFamilies();
            productVersions = getVersions();
            deliveryTypes = getDeliveryTypes();
            getSDCProducts(searchContains, productVersions, productFamilies, productLines, deliveryTypes);
        }

        // if ($('.emp-search-results.variation3').length > 0) {
        //     searchDocsResults();
        // }
    })

    $('.emp-clear-filter').on('click', function(){
        $(this).parents('.emp-searchResultsWrapper').addClass('block-inactive');
        //$(this).parents('.emp-searchResultsWrapper').hide();
        $(".emp-filter-results .emp-filter-tag").siblings(".emp-filter-options").find("li.emp-filter-options-child input").each(function() {
            this.checked = false;
            var filterValue = $(this).val();
            if(this.checked == false){
                if (desktopMediaQuery.matches) {
                    $(this).parents('.emp-facets').parents('.emp-search-results').siblings('.emp-searchResultsWrapper').find('.emp-facetsSelectedFiltersList span[data-filter-value = "' + filterValue + '"]').parents('.emp-facetsSelectedFiltersList').addClass('block-inactive');
                }
                if (mobMediaQuery.matches) {
                    $(this).parents('.emp-facets-filter.mobile').parents('.emp-counts').siblings('.emp-searchResultsWrapper.mob').find('.emp-facetsSelectedFiltersList span[data-filter-value = "' + filterValue + '"]').parents('.emp-facetsSelectedFiltersList').addClass('block-inactive');
                }
            }
        });
        if ($('.emp-search-results.variation2').length > 0) {
            getSDCProducts(searchContains, [], [], []);
        }

        // if ($('.emp-search-results.variation3').length > 0) {
        //     searchDocsResults();
        // }
        
    })

    $('.emp-input-searchbar').on('focusout', function(){
        searchContains =  $('.emp-input-searchbar').val();
        if ($('.emp-search-results.variation2').length > 0) {
            getSDCProducts(searchContains, productVersions, productFamilies, productLines, deliveryTypes);
        }
    
        if ($('.emp-search-results.variation3').length > 0) {
            searchDocsResults();
        }
    });

    $('.emp-search-results').on('change', '.emp-filter-options-child input:checkbox', function () {
        var filterValue = $(this).val();
        var filterKey = $(this).parents('.emp-filter-results').find('.emp-filter-title').text();
        if(this.checked) {
            populateFilters(filterValue, filterKey);
        } 
        if (this.checked == false) {
            if (desktopMediaQuery.matches) {
                $(this).parents('.emp-facets').parents('.emp-search-results').siblings('.emp-searchResultsWrapper').find('.emp-facetsSelectedFiltersList span[data-filter-value = "' + filterValue + '"]').parents('.emp-facetsSelectedFiltersList').addClass('block-inactive');
            }
            if (mobMediaQuery.matches) {
                $(this).parents('.emp-facets-filter.mobile').parents('.emp-counts').siblings('.emp-searchResultsWrapper.mob').find('.emp-facetsSelectedFiltersList span[data-filter-value = "' + filterValue + '"]').parents('.emp-facetsSelectedFiltersList').addClass('block-inactive');
            }
        }

        if ($('.emp-search-results.variation2').length > 0) {
            productLines = getProductLines();
            productFamilies = getProductFamilies();
            productVersions = getVersions();
            deliveryTypes = getDeliveryTypes();
            getSDCProducts(searchContains, productVersions, productFamilies, productLines, deliveryTypes);
        }
        
    });


    // check variations
    if ($('.emp-search-results.variation2').length > 0) {
        getSDCProducts(searchContains, productVersions, productFamilies, productLines, deliveryTypes);
    }

    if ($('.emp-search-results.variation3').length > 0) {
        searchDocsResults();
    }
})

function getVersions() {
    productVersions = $('.product-version-checkbox:checked').map(function () {
        return this.value;
    }).get();
    console.log(productVersions);
    return productVersions;
}
function getProductFamilies(){
    productFamilies = $('.product-family-checkbox:checked').map(function () {
        return this.value;
    }).get();
    return productFamilies;
}
function getProductLines(){
    productLines = $('.product-line-checkbox:checked').map(function () {
        return this.value;
    }).get();
    return productLines;
}
function getDeliveryTypes(){
    deliveryTypes = $('.download-option-checkbox:checked').map(function () {
        return this.value;
    }).get();
    return deliveryTypes;
}

function populateFilters(filterValue, filterKey) {

    $('.emp-searchResultsWrapper').removeClass('block-inactive');

    var filterItem = $('<div class="emp-facetsSelectedFiltersList"> <p>' + filterKey + ' : ' + filterValue + '</p> ' +
    '<span data-filter-key="' + filterKey + '" data-filter-value="' + filterValue + '"> ' +
    '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">' +
        '<g clip-path="url(#clip0_4338_5519)">' +
            '<path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="#011F3D"/>' +
        '</g>' +
        '<defs>' +
            '<clipPath id="clip0_4338_5519">' +
            '<rect width="24" height="24" fill="white"/>' +
            ' </clipPath> ' +
        '</defs>' +
        '</svg>' +
    '</span> </div>')

    $('.emp-searchResultsWrapper .emp-facetsSelectedFilters .emp-facetsSelectedFiltersWrap').append(filterItem);  
}

function getCookie(cookieName) {
    var name = cookieName + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var cookieArray = decodedCookie.split(';');

    for (var i = 0; i < cookieArray.length; i++) {
        var cookie = cookieArray[i].trim();
        if (cookie.indexOf(name) == 0) {
            return cookie.substring(name.length, cookie.length);
        }
    }
    return null; // Return null if the cookie is not found
}

function getSDCProducts(searchContains, productVersions, productFamilies, productLines, deliveryTypes) {
    var userEmail = localStorage.getItem("username");
    var total = 0;
    $.ajax({
        url: "https://sagdaasdev.apigw-aw-eu.webmethods.io/gateway/SDCPortal/1.0/searchSDC",
        type:"POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("isLoggedIn")
        },

        "data": JSON.stringify({
            "filters": {
              "nameContains": searchContains,
              "version": productVersions,
              "productFamily": productFamilies,
              "productLine": productLines,
              "deliveryType": deliveryTypes
            }
          }),

        success: function(data) {
            
            var searchResults = data.results;
            $(".emp-search-totalResults-wrapper.variation2").empty();
            if (searchResults.length == 0) {
                $('.emp-totalCounts').html('0 Results');
                $('.load-more-btn, .emp-icons-wrapper .clear-all, .emp-filter-options, .search-filter-wrapper, .view-more-less-wrapper').hide();
                $('.emp-icons-wrapper .emp-collapse-icon').addClass('block-inactive').removeClass('block-active');
                $('.emp-icons-wrapper .emp-expand-icon').addClass('block-active').removeClass('block-inactive');
                return;
            }
            //$('.load-more-btn').show();
            //$('.emp-search-results-wrapper').show();
            $.each(searchResults, function(i, item) {
                        total += 1;
                        var itemName = item.name;
                        var itemType = item.deliverytype;
                        var itemId = item.product_id;
                        var itemVersion = item.version;
                        var itemCode = item.productcode;
                        var itemLine = item.productline;
                        var itemFamily = item.productfamily;
                        var itemProductNameAlt = item.name_alt;
                        var itemCreateDate = item.create_date;

                        var createDate = new Date(itemCreateDate);
                        var itemCreate_Date = '';

                        // Check if the date is valid
                        if (!isNaN(createDate.getTime())) {
                            var options = {
                                year: "numeric",
                                month: "short",
                                day: "2-digit"
                            };

                            // Format the date
                            itemCreate_Date = new Intl.DateTimeFormat("en", options).format(createDate);

                        } else {
                            console.error("Invalid date format from the API:", itemCreateDate);
                        }

                        var detailsPagePath = $('.emp-search-totalResults-wrapper').data('detailspagepath');
                        var detailsUrl = detailsPagePath + '?id=' + itemId;

                        var productSDCHtml=$('<div class="emp-search-totalResults">'
                            + '<div class="emp-searchResultsIcons">'
                            + '<a target="_blank" href="' + detailsUrl + '" data-productid="' + itemId + '"><svg xmlns="http://www.w3.org/2000/svg" width="30" height="22" viewBox="0 0 30 22" fill="none">'
                            + '<path d="M7.66683 21.6667C5.64461 21.6667 3.91683 20.9667 2.4835 19.5667C1.05016 18.1667 0.333496 16.4556 0.333496 14.4333C0.333496 12.7 0.855718 11.1556 1.90016 9.80001C2.94461 8.44445 4.31127 7.57779 6.00016 7.20001C6.37794 5.60001 7.32239 4.07779 8.8335 2.63334C10.3446 1.1889 11.9557 0.466675 13.6668 0.466675C14.4002 0.466675 15.0279 0.727786 15.5502 1.25001C16.0724 1.77223 16.3335 2.40001 16.3335 3.13334V11.2L18.4668 9.13334L20.3335 11L15.0002 16.3333L9.66683 11L11.5335 9.13334L13.6668 11.2V3.13334C11.9779 3.44445 10.6668 4.26112 9.7335 5.58334C8.80016 6.90556 8.3335 8.26667 8.3335 9.66668H7.66683C6.37794 9.66668 5.27794 10.1222 4.36683 11.0333C3.45572 11.9445 3.00016 13.0445 3.00016 14.3333C3.00016 15.6222 3.45572 16.7222 4.36683 17.6333C5.27794 18.5445 6.37794 19 7.66683 19H23.6668C24.6002 19 25.3891 18.6778 26.0335 18.0333C26.6779 17.3889 27.0002 16.6 27.0002 15.6667C27.0002 14.7333 26.6779 13.9445 26.0335 13.3C25.3891 12.6556 24.6002 12.3333 23.6668 12.3333H21.6668V9.66668C21.6668 8.60001 21.4224 7.60556 20.9335 6.68334C20.4446 5.76112 19.8002 4.97779 19.0002 4.33334V1.23334C20.6446 2.01112 21.9446 3.16112 22.9002 4.68334C23.8557 6.20556 24.3335 7.86668 24.3335 9.66668C25.8668 9.84445 27.1391 10.5056 28.1502 11.65C29.1613 12.7945 29.6668 14.1333 29.6668 15.6667C29.6668 17.3333 29.0835 18.75 27.9168 19.9167C26.7502 21.0833 25.3335 21.6667 23.6668 21.6667H7.66683Z" fill="#36597D"/>'
                            + '</svg></a>'
                            + '</div>'
                            + '<div class="emp-resultsInfo-wrap">'
                            + '<div class="emp-type-title">'
                            + '<div class="emp-type">' + itemType + '</div>'
                            + '<div class="emp-title">' + itemName + '</div>'
                            + '</div>'
                            + '<div class="metaData">'
                            + '<div class="metaData-label">MetaData : </div>'
                            + '<div class="emp-productLine">' + itemLine + '</div>'
                            + '<div class="emp-productNameAlt">' + itemProductNameAlt + '</div>'
                            //+ '<div class="emp-productCode">' + itemCode + '</div>'
                            + '<div class="emp-productVersion">' + itemVersion + '</div>'
                            + '<div class="emp-productFamily">' + itemFamily + '</div>'
                            + '</div>'
                            + '<div class="emp-date">'
                            + '<div class="emp-productDate">' + itemCreate_Date + '</div>'
                            + '</div>'
                            + '</div>'
                            + '</div>');

                        $('.emp-search-totalResults-wrapper.variation2').append(productSDCHtml);
            });

            var resultsCount = total + ' Results';
            $(".emp-totalCounts").html(resultsCount);

            //Download options Mapping
            var downloadOptions = data.deliveryTypes;
            $.each(downloadOptions, function(i, type) {
                var productDeliveryType = type.deliveryType;
                var productDeliveryCount = type.amount;

                var productDownloadOptions = $('<li class="emp-filter-options-child">' +
                        '<input class="download-option-checkbox" type="checkbox" id="' + productDeliveryType +  '" name="' + productDeliveryType + '" value="' + productDeliveryType + '">' +
                        '<label class="download-option-checkbox" for="' + productDeliveryType + '">' + productDeliveryType + '</label>' +'<span>' + '(' + productDeliveryCount + ')' + '</span>' +
                        '</li>')
                $('.emp-search-results.variation2 .emp-filter-results.emp-download-options .emp-filter-options-parent').append(productDownloadOptions);
            });

            //Product Version Results Mapping
            var versionResults = data.versions;
            $.each(versionResults, function(i, versions) {
                //total += 1;
                var productVersion = versions.version;
                var productAmount = versions.amount;

                var productVersionFilter = $('<li class="emp-filter-options-child">' +
                        '<input class="product-version-checkbox" type="checkbox" id="' + productVersion +  '" name="' + productVersion + '" value="' + productVersion + '">' +
                        '<label class="product-version-checkbox" for="' + productVersion + '">' + productVersion + '</label>' + '<span>' + '(' + productAmount + ')' + '</span>' +
                        '</li>')
                $('.emp-search-results.variation2 .emp-filter-results.emp-version-filter .emp-filter-options-parent').append(productVersionFilter);
            });

            //Product Family Results Mapping
            var productFamily = data.families;
            $.each(productFamily, function(i, families) {
                var productLabel = families.productfamily;
                var productCount = families.amount;

                var productFamilieFilter = $('<li class="emp-filter-options-child">' +
                        '<input class="product-family-checkbox" type="checkbox" id="' + productLabel + '" name="' + productLabel + '" value="' + productLabel + '">' +
                        '<label class="product-family-checkbox" for="' + productLabel + '">' + productLabel + '</label>' + '<span>' + '(' + productCount + ')' + '</span>' +
                        '</li>')
                $('.emp-search-results.variation2 .emp-filter-results.emp-product-filter .emp-filter-options-parent').append(productFamilieFilter);
            });

            //Product Line Results Mapping
            var productLine = data.lines;
            $.each(productLine, function(i, lines) {
                var productLineLabel = lines.productline;
                var productLineCount = lines.amount;

                var facetProductLineFilter = $('<li class="emp-filter-options-child">' +
                        '<input class="product-line-checkbox" type="checkbox" id="' + productLineLabel + '" name="' + productLineLabel + '" value="' + productLineLabel + '">' +
                        '<label class="product-line-checkbox" for="' + productLineLabel + '">' + productLineLabel + '</label>' + '<span>' + '(' + productLineCount + ')' + '</span>' +
                        '</li>')
                $('.emp-search-results.variation2 .emp-filter-results.emp-product-line .emp-filter-options-parent').append(facetProductLineFilter);
            });

            //Product Version Result Filters
            var versionFilterCount = $('.emp-filter-results.emp-version-filter .emp-filter-options-child');
                if(versionFilterCount.length >= 6) {
                        $(versionFilterCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
                        $(versionFilterCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
                        //$(versionFilterCount).closest('.emp-filter-options').siblings('.emp-filter-tag').children('.emp-icons-wrapper').children('.clear-all').addClass('block-active');

                        size_li = versionFilterCount.length;
                        x=5;
                        $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(0, x).show();
                        $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(5).hide();

                        $('.emp-version-filter .view-more.block-active').on('click', function () {
                            x = x+5;
                            $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(0, x).slideDown();
                            $(this).removeClass('block-active');
                            $('.emp-version-filter .view-less').addClass('block-active');
                        });
                        $('.emp-version-filter').on('click', '.view-less.block-active', function () {
                            $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(0, x).show();
                            $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(5).hide();
                            $(this).removeClass('block-active');
                            $('.emp-version-filter .view-more').addClass('block-active');
                        });
                }

            //Product Familie Results Filters
            var productFilterCount = $('.emp-filter-results.emp-product-filter .emp-filter-options-child');
                if(productFilterCount.length >= 6) {
                        $(productFilterCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
                        $(productFilterCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
                        size_li = productFilterCount.length;
                        x=5;

                        $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(0, x).show();
                        $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(5).hide();

                        $('.emp-product-filter .view-more.block-active').on('click', function () {
                            x = x+5;
                            $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(0, x).slideDown();
                            $(this).removeClass('block-active');
                            $('.emp-product-filter .view-less').addClass('block-active');
                        });
                        $('.emp-product-filter').on('click', '.view-less.block-active', function () {
                            $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(0, x).show();
                            $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(5).hide();
                            $(this).removeClass('block-active');
                            $('.emp-product-filter .view-more').addClass('block-active');
                        });
                    }

            //Product Line Results Filters
            var productLineCount = $('.emp-filter-results.emp-product-line .emp-filter-options-child');
            if(productLineCount.length >= 6) {
                    $(productLineCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
                    $(productLineCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');

                    size_li = productLineCount.length;
                    x=5;

                    $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(0, x).show();
                    $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(5).hide();

                    $('.emp-product-line .view-more.block-active').on('click', function () {
                        x = x+5;
                        $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(0, x).slideDown();
                        $(this).removeClass('block-active');
                        $('.emp-product-line .view-less').addClass('block-active');
                    });
                    $('.emp-product-line').on('click', '.view-less.block-active', function () {
                        $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(0, x).show();
                        $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(5).hide();
                        $(this).removeClass('block-active');
                        $('.emp-product-line .view-more').addClass('block-active');
                    });
                }

            var filterResults = data.families;
            console.log(filterResults);
            //$('.emp-searchResultsWrapper.variation2 .emp-facetsSelectedFilters .emp-facetsSelectedFiltersWrap').empty();

                /* $.each(filterResults, function(i, item) {

                var filterLabel = item.productfamily;
                var filterLabelKey = item.amount;

                    var filterLabelName = $('<div class="emp-facetsSelectedFiltersList"> <p>' + filterLabel + ' : ' + filterLabelKey + '</p> ' +
                    '<span> ' +
                    '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">' +
                        '<g clip-path="url(#clip0_4338_5519)">' +
                            '<path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="#011F3D"/>' +
                        '</g>' +
                        '<defs>' +
                            '<clipPath id="clip0_4338_5519">' +
                            '<rect width="24" height="24" fill="white"/>' +
                            ' </clipPath> ' +
                        '</defs>' +
                        '</svg>' +
                '</span> </div>')

                    $('.emp-searchResultsWrapper.variation2 .emp-facetsSelectedFilters .emp-facetsSelectedFiltersWrap').append(filterLabelName);
            }); */

            var $parent = $(".emp-search-totalResults-wrapper.variation2"),
                            $items = $parent.find(".emp-search-totalResults"),
                            $loadMoreBtn = $(".load-more-btn"),
                            maxItems = $('.emp-search-results').data('results-count');

            if (total > maxItems) {
                $loadMoreBtn.show();
            }

            // Hide items beyond maxItems initially
            $items.slice(maxItems).hide();

            // On click of "load more" button, show more = maxItems
            // If there are none left to show, hide the "load more" button
            $loadMoreBtn.on("click", function (e) {
                $items.slice(0, maxItems).show();

                // Update maxItems for the next "load more" click
                maxItems += maxItems;

                // Hide the button if no more items to show
                if ($items.slice(maxItems).length === 0) {
                    $loadMoreBtn.hide();
                }
            });
        },
        error: function(data) {

            $('.emp-totalCounts').html('0 Results');
            $('.load-more-btn, .emp-icons-wrapper .clear-all, .emp-filter-options, .search-filter-wrapper, .view-more-less-wrapper').hide();
            $('.emp-icons-wrapper .emp-collapse-icon').addClass('block-inactive').removeClass('block-active');
            $('.emp-icons-wrapper .emp-expand-icon').addClass('block-active').removeClass('block-inactive');

            $('.emp-filter-options').empty();
            var errorHtml=$('<p id="error-msg">Error happened while fetching results. Please try after some time.</p>');
            $('.emp-search-totalResults-wrapper.variation2').html(errorHtml);
        }
    });

}

function searchDocsResults() {
    var total = 0;
    $.ajax({
        url: "/content/dam/empower-sag/json/search.json", 
        async: false, 
        success: function(data) {
            var searchTotalResultsCount = data.meta.total;
            var searchTotalResults = data.results;
            // var searchTotalResultsCount = searchTotalResults.length;

            //var countText = searchTotalResultsCount + ' Results';
            var countText = searchTotalResultsCount + ' Results';
            $(".emp-totalCounts").html(countText);

            $.each(searchTotalResults, function(i, item) {
                total += 1;
                var itemType = item.type;
                var itemTitle = item.title;
                var itemProductLine = item.facet_customfield_productline;
                var itemProductName = item.facet_customfield_product;
                var itemProductVersion = item.facet_customfield_productversion;
                var itemPlatform = item.facet_customfield_os;
                var itemPreview = item.content;
                var rawItemDate = item.date;

                var date = new Date(parseInt(rawItemDate));
                var options = {
                    year: "numeric",
                    month: "short",
                    day: "2-digit"
                };
                var itemDate = new Intl.DateTimeFormat("en", options).format(date);

                var productSearchHtml=$('<div class="emp-search-totalResults">'
                    + '<div class="emp-searchResultsIcons">'
                    +  '<svg xmlns="http://www.w3.org/2000/svg" width="30" height="22" viewBox="0 0 30 22" fill="none">'
                    +  '<path d="M7.66683 21.6667C5.64461 21.6667 3.91683 20.9667 2.4835 19.5667C1.05016 18.1667 0.333496 16.4556 0.333496 14.4333C0.333496 12.7 0.855718 11.1556 1.90016 9.80001C2.94461 8.44445 4.31127 7.57779 6.00016 7.20001C6.37794 5.60001 7.32239 4.07779 8.8335 2.63334C10.3446 1.1889 11.9557 0.466675 13.6668 0.466675C14.4002 0.466675 15.0279 0.727786 15.5502 1.25001C16.0724 1.77223 16.3335 2.40001 16.3335 3.13334V11.2L18.4668 9.13334L20.3335 11L15.0002 16.3333L9.66683 11L11.5335 9.13334L13.6668 11.2V3.13334C11.9779 3.44445 10.6668 4.26112 9.7335 5.58334C8.80016 6.90556 8.3335 8.26667 8.3335 9.66668H7.66683C6.37794 9.66668 5.27794 10.1222 4.36683 11.0333C3.45572 11.9445 3.00016 13.0445 3.00016 14.3333C3.00016 15.6222 3.45572 16.7222 4.36683 17.6333C5.27794 18.5445 6.37794 19 7.66683 19H23.6668C24.6002 19 25.3891 18.6778 26.0335 18.0333C26.6779 17.3889 27.0002 16.6 27.0002 15.6667C27.0002 14.7333 26.6779 13.9445 26.0335 13.3C25.3891 12.6556 24.6002 12.3333 23.6668 12.3333H21.6668V9.66668C21.6668 8.60001 21.4224 7.60556 20.9335 6.68334C20.4446 5.76112 19.8002 4.97779 19.0002 4.33334V1.23334C20.6446 2.01112 21.9446 3.16112 22.9002 4.68334C23.8557 6.20556 24.3335 7.86668 24.3335 9.66668C25.8668 9.84445 27.1391 10.5056 28.1502 11.65C29.1613 12.7945 29.6668 14.1333 29.6668 15.6667C29.6668 17.3333 29.0835 18.75 27.9168 19.9167C26.7502 21.0833 25.3335 21.6667 23.6668 21.6667H7.66683Z" fill="#36597D"/>'
                    +  '</svg>'
                    +  '</div>'
                    +  '<div class="emp-resultsInfo-wrap">'
                    + '<div class="emp-type-title">'
                    + '<div class="emp-type">' + itemType + '</div>'
                    + '<div class="emp-title">' + itemTitle + '</div>'
                    + '</div>'
                    + '<div class="metaData">'
                    + '<div class="metaData-label">MetaData : </div>'
                    + '<div class="emp-productLine">' + itemProductLine + '</div>'
                    + '<div class="emp-product">' + itemProductName + '</div>'
                    + '<div class="emp-version">' + itemProductVersion + '</div>'
                    + '<div class="emp-platform">' + itemPlatform + '</div>'
                    + '</div>'
                    + '<div class="emp-preview">Preview: ' + itemPreview + '</div>'
                    + '<div class="emp-date">Date: ' + itemDate + '</div>'
                    + '</div>'
                    + '</div>');
                
                $('.emp-search-totalResults-wrapper.variation3').append(productSearchHtml);
            });

            var facetsResults = data.facets;

            var facetDateRange = facetsResults.facet_date_range.list;
            var facetExt = facetsResults.facet_ext.list;
            var facetType = facetsResults.facet_sag_kc_type.list;
            var facetDocType = facetsResults.facet_customfield_doctype;
            var facetProductLine = facetsResults.facet_customfield_productline.list;
            var facetProductFamily = facetsResults.facet_customfield_productfamily;
            var facetProduct = facetsResults.facet_customfield_product.list;
            var facetProductVersion = facetsResults.facet_customfield_productversion.list;
            var facetPlatform = facetsResults.facet_customfield_os.list;
            var facetDate = facetsResults.facet_date;

            // $('.emp-search-results.variation3 .emp-filter-results.emp-source .emp-filter-options-parent').empty();
            // $('.emp-search-results.variation3 .emp-filter-results.emp-date-range .emp-filter-options-parent').empty();
            // $('.emp-search-results.variation3 .emp-filter-results.emp-product-line .emp-filter-options-parent').empty();
            // $('.emp-search-results.variation3 .emp-filter-results.emp-product-filter .emp-filter-options-parent').empty();
            // $('.emp-search-results.variation3 .emp-filter-results.emp-version-filter .emp-filter-options-parent').empty();
            // $('.emp-search-results.variation3 .emp-filter-results.emp-platform-filter .emp-filter-options-parent').empty();
            // $('.emp-search-results.variation3 .emp-filter-results.emp-fileType-filter .emp-filter-options-parent').empty();

            $.each(facetType, function(i, item) {
                total += 1;

                var facetSourceLabel = item.l;
                var facetSourceCount = item.c;

                var facetSource =  $('<li class="emp-filter-options-child">' +
                '<input class="" type="checkbox" id="' + facetSourceLabel + '" name="' + facetSourceLabel + '" value="' + facetSourceLabel + '">' +
                '<label class="" for="' + facetSourceLabel + '">' + facetSourceLabel + '</label>' + '<span>' + '(' + facetSourceCount + ')' + '</span>' +
                '</li>')  

                $('.emp-search-results.variation3 .emp-filter-results.emp-source .emp-filter-options-parent').append(facetSource);                                    

            });

            $.each(facetDateRange, function(i, item) {
                total += 1;

                var facetDateRangeLabel = item.l;
                var facetDateStartEnd = item.n;

                var facetDateRangeFilter = $('<li class="emp-filter-options-child">' +
                '<input class="" type="checkbox" id="' + facetDateStartEnd + '" name="' + facetDateStartEnd + '" value="' + facetDateStartEnd + '">' +
                '<label class="" for="' + facetDateStartEnd + '">' + facetDateRangeLabel + '</label>' + 
                '</li>')  

                $('.emp-search-results.variation3 .emp-filter-results.emp-date-range .emp-filter-options-parent').append(facetDateRangeFilter);                                    

            });

            $.each(facetProductLine, function(i, item) {
                total += 1;

                var facetProductLineLabel = item.l;
                var facetProductLineCount = item.c;

                var facetProductLineFilter = $('<li class="emp-filter-options-child">' +
                '<input class="" type="checkbox" id="' + facetProductLineLabel + '" name="' + facetProductLineLabel + '" value="' + facetProductLineLabel + '">' +
                '<label class="" for="' + facetProductLineLabel + '">' + facetProductLineLabel + '</label>' + '<span>' + '(' + facetProductLineCount + ')' + '</span>' +
                '</li>')  

                $('.emp-search-results.variation3 .emp-filter-results.emp-product-line .emp-filter-options-parent').append(facetProductLineFilter);                                    

            });

            $.each(facetProduct, function(i, item) {
                total += 1;

                var facetProductLabel = item.l;
                var facetProductCount = item.c;

                var facetProductFilter = $('<li class="emp-filter-options-child">' +
                                        '<input class="" type="checkbox" id="' + facetProductLabel + '" name="' + facetProductLabel + '" value="' + facetProductLabel + '">' +
                                        '<label class="" for="' + facetProductLabel + '">' + facetProductLabel + '</label>' + '<span>' + '(' + facetProductCount + ')' + '</span>' +
                                    '</li>')

                $('.emp-search-results.variation3 .emp-filter-results.emp-product-filter .emp-filter-options-parent').append(facetProductFilter);                                    

            });

            $.each(facetProductVersion, function(i, item) {
                total += 1;

                var facetProductVersionLabel = item.l;
                var facetProductVersionCount = item.c;

                var facetProductVersionFilter = $('<li class="emp-filter-options-child">' +
                '<input class="" type="checkbox" id="' + facetProductVersionLabel + '" name="' + facetProductVersionLabel + '" value="' + facetProductVersionLabel + '">' +
                '<label class="" for="' + facetProductVersionLabel + '">' + facetProductVersionLabel + '</label>' + '<span>' + '(' + facetProductVersionCount + ')' + '</span>' +
                '</li>')  

                $('.emp-search-results.variation3 .emp-filter-results.emp-version-filter .emp-filter-options-parent').append(facetProductVersionFilter);                                    

            });

            $.each(facetPlatform, function(i, item) {
                total += 1;

                var facetPlatformLabel = item.l;
                var facetPlatformCount = item.c;

                var facetPlatformFilter = $('<li class="emp-filter-options-child">' +
                '<input class="" type="checkbox" id="' + facetPlatformLabel + '" name="' + facetPlatformLabel + '" value="' + facetPlatformLabel + '">' +
                '<label class="" for="' + facetPlatformLabel + '">' + facetPlatformLabel + '</label>' + '<span>' + '(' + facetPlatformCount + ')' + '</span>' +
                '</li>')  

                $('.emp-search-results.variation3 .emp-filter-results.emp-platform-filter .emp-filter-options-parent').append(facetPlatformFilter);                                    

            });

            $.each(facetExt, function(i, item) {
                total += 1;

                var facetExtLabel = item.l;
                var facetExtCount = item.c;

                var facetExtFilter = $('<li class="emp-filter-options-child">' +
                '<input class="" type="checkbox" id="' + facetExtLabel + '" name="' + facetExtLabel + '" value="' + facetExtLabel + '">' +
                '<label class="" for="' + facetExtLabel + '">' + facetExtLabel + '</label>' + '<span>' + '(' + facetExtCount + ')' + '</span>' +
                '</li>')  

                $('.emp-search-results.variation3 .emp-filter-results.emp-fileType-filter .emp-filter-options-parent').append(facetExtFilter);                                    

            });

            var filterResults = data.filter;
            //$('.emp-searchResultsWrapper.variation3 .emp-facetsSelectedFilters .emp-facetsSelectedFiltersWrap').empty();  

            $.each(filterResults, function(i, item) {
                total += 1;

                var filterLabel = item.l;
                var filterLabelKey = item.vi;
                
                $.each(filterLabelKey, function(i, item) {
                    total += 1;
    
                    var filterLabelValue = item.l;
    
                    var filterLabelName = $('<div class="emp-facetsSelectedFiltersList"> <p>' + filterLabel + ' : ' + filterLabelValue + '</p> ' +
                    '<span> ' +
                    '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">' +
                        '<g clip-path="url(#clip0_4338_5519)">' +
                          '<path d="M19 6.41L17.59 5L12 10.59L6.41 5L5 6.41L10.59 12L5 17.59L6.41 19L12 13.41L17.59 19L19 17.59L13.41 12L19 6.41Z" fill="#011F3D"/>' +
                        '</g>' +
                        '<defs>' +
                          '<clipPath id="clip0_4338_5519">' +
                            '<rect width="24" height="24" fill="white"/>' +
                         ' </clipPath> ' +
                        '</defs>' +
                      '</svg>' +
                '</span> </div>')                    
    
                    $('.emp-searchResultsWrapper.variation3 .emp-facetsSelectedFilters .emp-facetsSelectedFiltersWrap').append(filterLabelName);                                    
    
                });

            });
        }
    });

    var sourceCount = $('.emp-filter-results.emp-source .emp-filter-options-child');
    var dateRangeCount = $('.emp-filter-results.emp-date-range .emp-filter-options-child');
    var productLineCount = $('.emp-filter-results.emp-product-line .emp-filter-options-child');
    var productFilterCount = $('.emp-filter-results.emp-product-filter .emp-filter-options-child');
    var versionFilterCount = $('.emp-filter-results.emp-version-filter .emp-filter-options-child');
    var platformFilterCount = $('.emp-filter-results.emp-platform-filter .emp-filter-options-child');
    var fileTypeFilterCount = $('.emp-filter-results.emp-fileType-filter .emp-filter-options-child');

    if(productFilterCount.length >= 6) {
        $(productFilterCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
        $(productFilterCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
        //$(productFilterCount).closest('.emp-filter-options').siblings('.emp-filter-tag').children('.emp-icons-wrapper').children('.clear-all').addClass('block-active');

        size_li = productFilterCount.length;
        x=5;
        // $('.emp-filter-results.emp-product-filter li.emp-filter-options-child:lt('+x+')').show();
        $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(0, x).show();
        $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(5).hide();
        
        $('.emp-product-filter .view-more.block-active').on('click', function () {
            x = x+5;
            $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(0, x).slideDown();
            $(this).removeClass('block-active');
            $('.emp-product-filter .view-less').addClass('block-active');
        });
        $('.emp-product-filter').on('click', '.view-less.block-active', function () {
            $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(0, x).show();
            $('.emp-filter-results.emp-product-filter li.emp-filter-options-child').slice(5).hide();
            $(this).removeClass('block-active');
            $('.emp-product-filter .view-more').addClass('block-active');
        });
    } 
    if(sourceCount.length >= 6) {
        $(sourceCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
        $(sourceCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
        //$(sourceCount).closest('.emp-filter-options').siblings('.emp-filter-tag').children('.emp-icons-wrapper').children('.clear-all').addClass('block-active');

        size_li = sourceCount.length;
        x=5;
        // $('.emp-filter-results.emp-product-filter li.emp-filter-options-child:lt('+x+')').show();
        $('.emp-filter-results.emp-source li.emp-filter-options-child').slice(0, x).show();
        $('.emp-filter-results.emp-source li.emp-filter-options-child').slice(5).hide();
        
        $('.emp-source .view-more.block-active').on('click', function () {
            x = x+5;
            $('.emp-filter-results.emp-source li.emp-filter-options-child').slice(0, x).slideDown();
            $(this).removeClass('block-active');
            $('.emp-source .view-less').addClass('block-active');
        });
        $('.emp-source').on('click', '.view-less.block-active', function () {
            $('.emp-filter-results.emp-source li.emp-filter-options-child').slice(0, x).show();
            $('.emp-filter-results.emp-source li.emp-filter-options-child').slice(5).hide();
            $(this).removeClass('block-active');
            $('.emp-source .view-more').addClass('block-active');
        });
    } 
    if(dateRangeCount.length >= 6) {
        $(dateRangeCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
        $(dateRangeCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
       //$(dateRangeCount).closest('.emp-filter-options').siblings('.emp-filter-tag').children('.emp-icons-wrapper').children('.clear-all').addClass('block-active');

       size_li = dateRangeCount.length;
        x=5;
        // $('.emp-filter-results.emp-product-filter li.emp-filter-options-child:lt('+x+')').show();
        $('.emp-filter-results.emp-date-range li.emp-filter-options-child').slice(0, x).show();
        $('.emp-filter-results.emp-date-range li.emp-filter-options-child').slice(5).hide();
        
        $('.emp-date-range .view-more.block-active').on('click', function () {
            x = x+5;
            $('.emp-filter-results.emp-date-range li.emp-filter-options-child').slice(0, x).slideDown();
            $(this).removeClass('block-active');
            $('.emp-date-range .view-less').addClass('block-active');
        });
        $('.emp-date-range').on('click', '.view-less.block-active', function () {
            $('.emp-filter-results.emp-date-range li.emp-filter-options-child').slice(0, x).show();
            $('.emp-filter-results.emp-date-range li.emp-filter-options-child').slice(5).hide();
            $(this).removeClass('block-active');
            $('.emp-date-range .view-more').addClass('block-active');
        });
    } 
    if(productLineCount.length >= 6) {
        $(productLineCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
        $(productLineCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
        //$(productLineCount).closest('.emp-filter-options').siblings('.emp-filter-tag').children('.emp-icons-wrapper').children('.clear-all').addClass('block-active');

        size_li = productLineCount.length;
        x=5;
        // $('.emp-filter-results.emp-product-filter li.emp-filter-options-child:lt('+x+')').show();
        $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(0, x).show();
        $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(5).hide();
        
        $('.emp-product-line .view-more.block-active').on('click', function () {
            x = x+5;
            $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(0, x).slideDown();
            $(this).removeClass('block-active');
            $('.emp-product-line .view-less').addClass('block-active');
        });
        $('.emp-product-line').on('click', '.view-less.block-active', function () {
            $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(0, x).show();
            $('.emp-filter-results.emp-product-line li.emp-filter-options-child').slice(5).hide();
            $(this).removeClass('block-active');
            $('.emp-product-line .view-more').addClass('block-active');
        });
    } 
    if(versionFilterCount.length >= 6) {
        $(versionFilterCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
        $(versionFilterCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
        //$(versionFilterCount).closest('.emp-filter-options').siblings('.emp-filter-tag').children('.emp-icons-wrapper').children('.clear-all').addClass('block-active');

        size_li = versionFilterCount.length;
        x=5;
        // $('.emp-filter-results.emp-product-filter li.emp-filter-options-child:lt('+x+')').show();
        $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(0, x).show();
        $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(5).hide();
        
        $('.emp-version-filter .view-more.block-active').on('click', function () {
            x = x+5;
            $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(0, x).slideDown();
            $(this).removeClass('block-active');
            $('.emp-version-filter .view-less').addClass('block-active');
        });
        $('.emp-version-filter').on('click', '.view-less.block-active', function () {
            $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(0, x).show();
            $('.emp-filter-results.emp-version-filter li.emp-filter-options-child').slice(5).hide();
            $(this).removeClass('block-active');
            $('.emp-version-filter .view-more').addClass('block-active');
        });
    } 
    if(platformFilterCount.length >= 6) {
        $(platformFilterCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
        $(platformFilterCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
        //$(platformFilterCount).closest('.emp-filter-options').siblings('.emp-filter-tag').children('.emp-icons-wrapper').children('.clear-all').addClass('block-active');

        size_li = platformFilterCount.length;
        x=5;
        // $('.emp-filter-results.emp-product-filter li.emp-filter-options-child:lt('+x+')').show();
        $('.emp-filter-results.emp-platform-filter li.emp-filter-options-child').slice(0, x).show();
        $('.emp-filter-results.emp-platform-filter li.emp-filter-options-child').slice(5).hide();
        
        $('.emp-platform-filter .view-more.block-active').on('click', function () {
            x = x+5;
            $('.emp-filter-results.emp-platform-filter li.emp-filter-options-child').slice(0, x).slideDown();
            $(this).removeClass('block-active');
            $('.emp-platform-filter .view-less').addClass('block-active');
        });
        $('.emp-platform-filter').on('click', '.view-less.block-active', function () {
            $('.emp-filter-results.emp-platform-filter li.emp-filter-options-child').slice(0, x).show();
            $('.emp-filter-results.emp-platform-filter li.emp-filter-options-child').slice(5).hide();
            $(this).removeClass('block-active');
            $('.emp-platform-filter .view-more').addClass('block-active');
        });
    } 
    if(fileTypeFilterCount.length >= 6) {
        $(fileTypeFilterCount).closest('.emp-filter-options').siblings('.view-more-less-wrapper').children('.view-more').addClass('block-active');
        $(fileTypeFilterCount).closest('.emp-filter-options').siblings('.search-filter-wrapper').addClass('block-active');
        //$(fileTypeFilterCount).closest('.emp-filter-options').siblings('.emp-filter-tag').children('.emp-icons-wrapper').children('.clear-all').addClass('block-active');

        size_li = fileTypeFilterCount.length;
        x=5;
        // $('.emp-filter-results.emp-product-filter li.emp-filter-options-child:lt('+x+')').show();
        $('.emp-filter-results.emp-fileType-filter li.emp-filter-options-child').slice(0, x).show();
        $('.emp-filter-results.emp-fileType-filter li.emp-filter-options-child').slice(5).hide();
        
        $('.emp-fileType-filter .view-more.block-active').on('click', function () {
            x = x+5;
            $('.emp-filter-results.emp-fileType-filter li.emp-filter-options-child').slice(0, x).slideDown();
            $(this).removeClass('block-active');
            $('.emp-fileType-filter .view-less').addClass('block-active');
        });
        $('.emp-fileType-filter').on('click', '.view-less.block-active', function () {
            $('.emp-filter-results.emp-fileType-filter li.emp-filter-options-child').slice(0, x).show();
            $('.emp-filter-results.emp-fileType-filter li.emp-filter-options-child').slice(5).hide();
            $(this).removeClass('block-active');
            $('.emp-fileType-filter .view-more').addClass('block-active');
        });
    } 
}

// Utility method - needs to be moved to common JS
function getParameterValues(param) {
    var url = decodeURIComponent(window.location.href);
    var params = url.slice(url.indexOf('?') + 1).split('&');
    for (var i = 0; i < params.length; i++) {
        var urlparam = params[i].split('=');
        if (urlparam[0] == param) {
            return urlparam[1];
        }
    }
}

function getSelector() {
    var path = window.location.pathname;
    if (path.indexOf('.') < path.lastIndexOf('.html')) {
        return path.substring(path.indexOf('.') + 1, path.indexOf('.html'));
    }
    return null;
}