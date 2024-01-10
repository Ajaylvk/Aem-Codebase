$.fn.igSuggestions = function(options) {
    var field = jQuery(this);

    var url = options.suggestUrl;
    var lang = options.searchLang;

    var extraParams = {
        "indexKey"    : "multiplex",
        "fieldLimit"  : 5,
        "resultLimit" : 10,
        "fields"      : [ "keywords","anno.phrase","intergator.suggest.tags","intergator.suggest.logs","intergator.suggest.dym"],
        "parameters"  : {
            "query"   : "*",
            "filterQuery"  : "facet_language:" + lang
        }
    };

    var autoposition = {
        of: field,
        my: "left top",
        at: "left bottom",
        collision: "none",
        offset: "0 -2"
    };

    field.bind("autocompleteopen", function(event, ui) {
        var widget = jQuery(this).autocomplete("widget");
        var w = field.outerWidth();
        widget.width(w - 2 - 4); // border :1px * 2 = 2; padding:2px * 2 = 4
    });

    field.autocomplete({
        position: autoposition,
        source: function(request, response) {
            $.ajax({
                method  : 'POST',
                dataType: 'json',
                contentType: 'application/json',
                url     : url,
                data    : JSON.stringify(jQuery.extend({ phrase: request.term }, extraParams)),
                success : function(data) {
                    if ( data && data[0] ) {
                        response(jQuery.map(data, function(item) {
                            var regex = new RegExp("(?![^&;]+;)(?!<[^<>]*)(" + request.term.replace(/([\^\$\(\)\[\]\{\}\*\.\+\?\|\\])/gi, "\\$1") + ")(?![^<>]*>)(?![^&;]+;)", "gi");
                            return {
                                label: item.value.raw.replace(regex, "<strong>$1</strong>"),
                                value: item.value.raw
                            }
                        }));
                    } else {
                        response([]);
                    }
                }
            });
        }
    }).data('ui-autocomplete')._renderItem = function(ul, item) {
        return jQuery("<li></li>")
            .data("item.autocomplete", item)
            .append('<a>' + item.label + '</a>')
            .appendTo(ul);
    };

    // Wenn man die Suche zu schnell auslöst, während Suggestions noch gerendert werden,
    // bleibt sie Auswahlbox fälschlicherweise stehen
    jQuery(document).on('click', 'body', function(){
        jQuery(".ui-autocomplete").hide();
    })
}
