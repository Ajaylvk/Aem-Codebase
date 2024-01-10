/*
* intergator-Api 1.1 Web Search
* events are triggered using hashChangeHandler
*/
var _messages   = {};  // i18n translation table
var _templates  = {};  // handlebars templates

$.fn.igGlobalSearch = function(options) {
    var settings = $.extend({   //default settings
        filterQuery : '*',
        limit       : 10,
        sort        : "result.ranking:DESC",
        selectors   : {
            queryPlaceholder  : 'div.ig_query',
            searchPlaceholder : 'div.ig_search',
            facetsPlaceholder : 'div.ig_facets',
            searchForm        : 'form.header__search-form', // search input field name *has to be* "q"!
            actions           : 'a[data-action]',
            rsmax             : 'div.rsmax select',
            order             : 'div.order select',
            resultContainer   : 'div.searchresult' //selector added by sag, targeting jump to top scroll
        },
        facetsConfig : { // facets to be displayed. view = tree, cloud, list (default)
            "facet_sag_perspectives" : {
                'categoryLimit': 5,
                'view'         : 'perspective'
            }
        },
        suggestParams: {
            "indexKey"    : "multiplex",
            "fieldLimit"  : 5,
            "resultLimit" : 10,
            "fields"      : ["keywords","anno.phrase", "intergator.suggest.tags","intergator.suggest.logs","intergator.suggest.dym","intergator.suggest.syns"],
            "parameters"  : {
                "query"   : "*",
                "filterQuery"  : "facet_language:" + options.searchLang,
                "synonymsEnabled" : "true"
            }
        }
    }, options);

    /*
    * search params, set via URL hash:
    * q  - query
    * li - limit
    * of - offset
    * ff - fieldFilters
    * fq - filterQuery
    * cf - categoryFilters
    * f  - facets to query, Array
    */
    var searchParams    = generateSearchParams();

    // set initial parameters
    if (!searchParams.fq) searchParams.fq = settings.filterQuery;

    var result          = {};  // search results
    var facets          = {};  // facets

    // fields to request in search
    var resultAttributes = [
        {'name':'common.title', 'createAbstracts':true},
        {'name':'content', 'textLimit':100, 'abstractLimit':100},
        {'name':'url'},
        {'name':'path'},
        {'name':'common.date'}
    ];

    var loadTemplates = $('script[type="text/x-handlebars-template"]').each(function () {
        var name        = $(this).attr('name');
        var src         = $(this).html();
        var partialName = $(this).attr('partial-name');

        if(partialName) Handlebars.registerPartial(partialName,src);

        _templates[name] = Handlebars.compile(src);
    });

    // perform search (Ajax)
    var getResults = function(apiSearchParams) {
        return $.ajax({
            method  : 'POST',
            dataType: 'json',
            url  : settings.url,
            contentType: 'application/json',
            cache: true,
            data : JSON.stringify(apiSearchParams),
            success: function(data) {
                result              = data;
                result.searchParams = apiSearchParams;
                result.pager        = generatePager(data.availableDocumentCount, apiSearchParams.parameters.limit, apiSearchParams.parameters.offset);
                result.pageSize     = generatePSOptions(apiSearchParams.parameters.limit);
                result.lang			= settings.uiLang;

                <!-- adobe analytics data added by Andre Urban -->
                // preset variables for adobe analytics
                digitalData = digitalData || {}; // init data layer if NOT set
                digitalData.internalSearch = {   //  set search layer
                    searchInfo: {
                        keywords: apiSearchParams.parameters.query,
                        totalResults: data.availableDocumentCount,
                        totalResultPages: Math.ceil(data.availableDocumentCount/apiSearchParams.parameters.limit)
                    }
                }
                <!-- /adobe analytics data -->
            },
            // TODO: more user-friendly error handling.
            error : function() {
                //console.log('search failed.');
            }
        });
    }

    // get facets (Ajax)
    var getFacets = function(apiSearchParams, apiFacetsParam) {

        var facetParams = JSON.parse(JSON.stringify(apiSearchParams)); // do not create a reference
        var tmpFacetCategories = facetParams.parameters.categoryFilters;

        delete facetParams.parameters.limit;
        delete facetParams.parameters.offset;
        delete facetParams.parameters.attributes;
        delete facetParams.parameters.categoryFilters;
        delete facetParams.parameters.logSearch;
        facetParams.parameters.facetGrouping = apiFacetsParam;

        return $.ajax({
            method  : 'POST',
            dataType: 'json',
            url  : settings.url,
            contentType: 'application/json',
            cache: true,
            data : JSON.stringify(facetParams),
            success: function(data) {
                facets.searchFacets = data;
                facets.facetsConfig = settings.facetsConfig;
                facets.searchParams = facetParams;
                facets.searchParams.parameters.categoryFilters = tmpFacetCategories;
            },
            // TODO: more user-friendly error handling.
            error : function() {
                //console.log('search failed.');
            }
        });
    }

    // get similar search terms
    var getSimilarResults = function(apiSearchParams) {
        return $.ajax({
            method  : 'POST',
            dataType: 'json',
            url  : settings.suggestUrl,
            contentType: 'application/json',
            cache: true,
            data: JSON.stringify(jQuery.extend({ phrase: apiSearchParams.parameters.query }, settings.suggestParams)),
            success: function(data) {
                if ( data && data[0] ) {
                    result.suggestions = jQuery.map(data, function(item) {
                        var regex = new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + apiSearchParams.parameters.query.replace(/([\^\$\(\)\[\]\{\}\*\.\+\?\|\\])/gi, "\\$1") + ")(?![^<>]*>)(?![^&;]+;)", "gi");
                        return {
                            label: item.value.raw.replace(regex, "<strong>$1</strong>"),
                            value: item.value.raw
                        }
                    });
                } else {
                    result.suggestions = [];
                }
                //console.log("result.suggestions: "+result.suggestions)
            },
            // TODO: more user-friendly error handling.
            error : function() {
                //console.log('search failed.');
            }
        });
    }

    // get facets, ignoring search term and currently set filters (Ajax)
    var getAllFacets = function(apiSearchParams, apiFacetsParam) {

        var facetParams = JSON.parse(JSON.stringify(apiSearchParams)); // do not create a reference
        delete facetParams.parameters.limit;
        delete facetParams.parameters.offset;
        delete facetParams.parameters.attributes;
        delete facetParams.parameters.categoryFilters;
        delete facetParams.parameters.logSearch;
        facetParams.parameters.query = "*";
        facetParams.parameters.facetGrouping = apiFacetsParam;

        return $.ajax({
            method  : 'POST',
            dataType: 'json',
            url  : settings.url,
            contentType: 'application/json',
            cache: true,
            data : JSON.stringify(facetParams),
            success: function(data) {
                facets.allFacets    = data;
            },
            // TODO: more user-friendly error handling.
            error : function() {
                //console.log('search failed.');
            }
        });
    }

    // render results to page
    var renderResult = function() {
        $(settings.selectors.queryPlaceholder).empty().append(
            _templates['ig_query'](result)
        );

        $(settings.selectors.searchPlaceholder).empty().append(
            _templates['ig_search'](result)
        );
    }

    // render facets to page
    var renderFacets = function() {
        $(settings.selectors.facetsPlaceholder).empty().append(
            _templates['search_facets'](facets)
        );
        aemArrowTabs();
    }

    // TODO: Validate, Limit, CategoryLimit and DocumentLimit
    var doTheTrick = function(searchParams) {

        Granite.I18n.setLocale(settings.uiLang);

        var apiSearchParams = {
            'i18n':{
                'localeId'   : settings.uiLang,
                'timeZoneId' : settings.timeZoneId
            },
            'indexKey'  : 'multiplex',
            'parameters': {
                'query'        : searchParams.q,
                'logSearch'    : 'true',
                'filterQuery'  : searchParams.fq + " facet_language:" + settings.searchLang,
                'limit'        : parseInt(searchParams.li),
                'offset'       : parseInt(searchParams.of),
                'correctQuery' : true,
                'attributes'   : resultAttributes,
                'synonymsEnabled' : 'true'
            }
        };

        // set fieldFilters
        if (searchParams.ff) {
            apiSearchParams.parameters.fieldFilters = [];
            for (var i = 0; i < searchParams.ff.length; i++) {
                apiSearchParams.parameters.fieldFilters.push({
                    'inclusive':true,
                    'fields':[searchParams.ff[i]]
                });
            }
        }

        // set categoryFilters
        if (searchParams.cf) {
            apiSearchParams.parameters.categoryFilters = [];
            for (var k = 0; k < searchParams.cf.length; k++) {
                apiSearchParams.parameters.categoryFilters.push({
                    'inclusive':true,
                    'categories':[searchParams.cf[k]]
                });
            }
        }

        // make sure facet_sag_perspectives:all is selected by default
        // if no other perspective has been chosen manually
        if(apiSearchParams.parameters.categoryFilters == undefined || apiSearchParams.parameters.categoryFilters.length < 1) {
            apiSearchParams.parameters.categoryFilters = [];
            apiSearchParams.parameters.categoryFilters.push({
                'inclusive':true,
                'categories':['facet_sag_perspectives:all']
            });
        }

        //facets to load
        var apiFacetsParam = [];

        // prepare facetsConfig for apiSearchParams
        for (var key in settings.facetsConfig) {
            apiFacetsParam.push({
                'facetName'     : key,
                'categoryLimit' : settings.facetsConfig[key].categoryLimit,
                'documentLimit' : 0
            });
        }

        if (searchParams.f) {
            for (var l = 0; l < searchParams.f.length; l++) {
                var facetValues = searchParams.f[l].split(':');
                for (var j = 0; j < apiFacetsParam.length; j++) {
                    if (apiFacetsParam[j].facetName == facetValues[0]) {
                        apiFacetsParam[j].categoryLimit = parseInt(facetValues[1]);
                        apiFacetsParam[j].documentLimit = parseInt(facetValues[2]);
                        break;
                    }
                }
            }
        }

        if(searchParams.q) {
            $.when(getResults(apiSearchParams), getFacets(apiSearchParams, apiFacetsParam), getAllFacets(apiSearchParams, apiFacetsParam), getSimilarResults(apiSearchParams)).done(renderResult, renderFacets);
        }
        else{
            // console.log('empty search query, doing nothing.');
        }
    }

    // search, including events
    var doSearch = function(){

        doTheTrick(searchParams);

        // Submit Query
        $(document).on('submit', settings.selectors['searchForm'] , function(event) {
            event.preventDefault();
            var query = $(settings.selectors['searchForm'] + ' input[name=q]').val();

            searchParams.of = 0;
            searchParams.q  = query;

            if (query) {
                changeURL(searchParams);
            } else {
                result.values = [];
                renderResult();
                renderFacets();
            }
        });

        // Link Events using "data-action", "data-name", "data-value"
        // TODO: reset "offset" etc. (i.e. while filtering)
        $(document).on('click', settings.selectors['actions'], function(event) {
            event.preventDefault();
            var action = $(this).data('action');
            var name   = $(this).data('name');
            var value  = $(this).data('value');

            var doNewSearch = false;

            switch (action) {
                case "add":
                    if (name && value != undefined) {
                        if (name == "cf") {
                            setCategoryFilters(value);
                        } else {
                            searchParams[name] = value;
                        }
                        doNewSearch = true;
                    }
                    break;
                case "change":
                    if (name && value != undefined) {
                        if (name == "cf") {
                            var facetName = value.slice(0,value.lastIndexOf(':'));

                            // remove all occurrences of facet
                            if (searchParams.cf) {
                                // backward-iteration due to splice
                                for (var i = searchParams.cf.length - 1; i >= 0 ; i--) {
                                    // TODO: respect comma-separated order?
                                    var arr = searchParams.cf[i].split(':');
                                    if (arr[0] == facetName) {
                                        searchParams.cf.splice(i,1);
                                    }
                                }
                            }
                            setCategoryFilters(value);
                        } else if (name == "f") { // reload facet categories
                            // value structure is"Facettenname:CategoryLimit:DocumentLimit"
                            if (searchParams.f) {
                                // remove existing entry
                                for (var m = searchParams.f.length - 1; m >= 0 ; m--) {
                                    if (value.lastIndexOf(searchParams.f[m].split(':')[0] + ":", 0) === 0) {
                                        searchParams.f.splice(m,1);
                                    }
                                }
                                searchParams.f.push(value);
                            } else {
                                searchParams.f = [value];
                            }
                        } else {
                            searchParams[name] = value;
                        }
                    }
                    doNewSearch = true;
                    break;
                case "remove":
                    if (name != undefined) {
                        if (name == "cf") {
                            // remove single filter
                            if (value != undefined) {
                                // TODO: respect comma-separated order?
                                if (searchParams.cf.indexOf(value) != -1) {
                                    searchParams.cf.splice(searchParams.cf.indexOf(value), 1);
                                }
                            } else {
                                // completely clear entry if no special filter is set for removal.
                                delete searchParams[name];
                            }
                        } else {
                            delete searchParams[name];
                        }

                        doNewSearch = true;
                    }
                    break;
            }

            if (doNewSearch) {
                changeURL(searchParams);
                var scrollPos = $(document).scrollTop();
                var queryPos  = $(settings.selectors['resultContainer']).offset().top; //resultContainer selector added by sag

                if (scrollPos > queryPos) {
                    $('html, body').animate({
                        scrollTop: queryPos,
                        speed: 400
                    });
                }
            }

        });

        // search results count
        $(document).on('change', settings.selectors['rsmax'], function(event) {
            event.preventDefault();
            searchParams.li = parseInt($(this).find('option:selected').text());
            changeURL(searchParams);
        });


        //HashChangeHandler, perform new search after user interaction in client
        $(window).on('hashchange', function() {
            searchParams = generateSearchParams();
            doTheTrick(searchParams);
        });
    }

    // load search
    $.when(loadTemplates)
        .done(doSearch)
        .fail(function(){
            // TODO: more user friendly error handling
            //console.log('Search cannot be rendered.');
        });

    /////////////////////////////////////////////////////////////////////
    // ************************** functions  **************************//
    /////////////////////////////////////////////////////////////////////

    // re-set URL
    function changeURL(searchParams) {
        window.location.href = settings.searchResultUrl + "#" + $.param(searchParams);
    }

    // set categoryFilters
    function setCategoryFilters(value) {

        //reset pager
        searchParams.of = 0;

        if (!searchParams.cf) {
            searchParams.cf = [value];
        } else {
            if (searchParams.cf.indexOf(value) == -1) searchParams.cf.push(value);
        }
    }

    //get URL params
    //URL-params like param[] are written to an array
    function getUrlVars(sign) {
        var vars = [], hash;
        var url  = window.location.href;
        var occ  = url.indexOf(sign);
        if (occ != -1) {
            var hashes        = url.slice(occ + 1).split('&');
            for(var i = 0; i < hashes.length; i++) {
                hash = hashes[i].split('=');
                if (hash[0]) {
                    var key = decodeURIComponent(hash[0]);
                    var val = decodeURIComponent(hash[1]);

                    if (key.indexOf('[]') != -1){
                        key = key.substring(0, key.length - 2);
                        if (vars[key]) {
                            vars[key].push(val);
                        } else {
                            vars[key] = [val];
                        }
                    } else {
                        vars[key] = val;
                    }
                }
            }
        }
        return vars;
    }

    //compile searchParams
    function generateSearchParams() {
        var urlVars = getUrlVars('?');
        if (Object.keys(urlVars).length == 0) urlVars = getUrlVars('#');

        var searchParams = {
            "q"  : urlVars['q']  ? decodeURIComponent(urlVars['q']).replace(/\+/g, ' ') : '',
            "li" : urlVars['li'] ? urlVars['li']                    : settings.limit,
            "of" : urlVars['of'] ? urlVars['of']                    : 0
        };

        if (urlVars['ff'])  searchParams.ff  = urlVars['ff'];
        if (urlVars['fq'])  searchParams.fq  = urlVars['fq'];
        if (urlVars['f'])   searchParams.f   = urlVars['f'];
        if (urlVars['cf'])  searchParams.cf  = urlVars['cf'];

        return searchParams;
    }

    // build paging
    function generatePager(max, limit, offset) {

        // "max_pages" needs to be odd, indicates range of pages displayed
        //  example: << 1 ... 7 8 [9] 10 11 ... 20 >>

        var max_pages   = 9;

        var anz_pages   = Math.ceil(max/limit);
        var actual_page = Math.ceil(offset/limit + 1);

        // First page in range
        var first_page  = 1;
        if (actual_page > ((max_pages+1)/2)) {
            first_page = actual_page - ((max_pages-1)/2);
        }

        // Last page in range
        var last_page   = first_page + (max_pages-1);
        if (anz_pages < last_page) {
            last_page  = anz_pages;
            first_page = anz_pages - (max_pages-1);
            if (first_page < 1) first_page = 1;
        }

        var pages = [];

        pages.firstPageHit = offset+1;
        pages.lastPageHit  = max > offset+limit ? offset+limit : max;

        //previous
        var prev_active = false;
        var prev_offset = (actual_page-1-1)*limit;
        if (actual_page > 1) prev_active = true;

        if (prev_offset >= 0) {
            pages.push({
                'type'     : 'prev',
                'title'    : 'prev',
                'offset'   : prev_offset,
                'is_active': prev_active
            })
        }

        // pages
        var prev_type = '';
        for(var i = 1; i <= anz_pages; i++) {
            if (i == 1 || i == anz_pages || (i >= first_page && i <= last_page)) {
                var new_offset = (i-1)*limit;
                var is_active  = false;
                if (i == actual_page)  is_active = true;
                var type = 'page';

                var page  = {
                    'type'        : type,
                    'title'       : i,
                    'offset'      : new_offset,
                    'is_active'   : is_active,
                    'hasPrevPage' : prev_type == 'page' ? true : false
                };
                pages.push(page);

                prev_type = type;
            } else {
                var type = 'separator';
                if (prev_type != type) {
                    pages.push({
                        'type'     : type,
                        'title'    : '...'
                    });
                    prev_type = type;
                }
            }
        }

        //next
        var next_active = false;
        var next_offset = (actual_page+1-1)*limit;
        if (actual_page < anz_pages) next_active = true;

        if (next_offset < max) {
            pages.push({
                'type'     : 'next',
                'title'    : 'next',
                'offset'   : next_offset,
                'is_active': next_active
            });
        } else {
            pages.lastPage = true;
        }

        return pages;
    }

    //select: count of search results to be displayed
    function generatePSOptions(limit){
        var options = [
            {label : 7},
            {label : 10},
            {label : 20},
            {label : 30},
            {label : 40},
        ];
        for(var i = 0; i < options.length; i++) {
            if (options[i].label == limit) options[i].selected = true;
        }
        return options;
    }
}
