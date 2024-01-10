(function(global, hb) {
    'use strict';

    // Container für Helper
    var helpers   = {};

    //Translation
    helpers.i18n = function(key, args) {
        var translated = Granite.I18n.get(key);
        if (args && args.hash) {
            for(var hashKey in args.hash) {
                translated = translated.replace("{{" + hashKey + "}}", args.hash[hashKey]);
            }
        }
        return translated;
    };

    // Einfache math. Operationen
    helpers.calc = function(left, operator, right) {
        left  = parseFloat(left);
        right = parseFloat(right);

        var result;

        switch (operator) {
            case "+": result = left + right; break;
            case "-": result = left - right; break;
            case "*": result = left * right; break;
            case "/": result = left / right; break;
            case "%": result = left % right; break;
            default : result = null;         break;
        }

        return result;
    };

    // Highlighting
    helpers.marked = function(value, spanClass) {
        if (value && value[0].tokens) {
            var tokens = value[0].tokens;
            var string = '';
            for (var i = 0; i < tokens.length; i++) {
                if (tokens[i].marked) {
                    string += '<span class="' + spanClass + '">' + tokens[i].value + '</span>';
                } else {
                    string += tokens[i].value;
                }
            }
            return string;
        } else {
            if (value) return value[0].formatted;
            else return '';
        }
    };

    // Pfad splitten
    helpers.splitPath = function(path) {
        var folders = path.split('/');
        var kum     = "";
        var result  = new Array();
        for (var i = 0; i < folders.length; i++) {
            kum += folders[i];
            result.push({
                folder : folders[i],
                path   : kum
            });
            kum += '/';

        }
        return result;
    };

    // IF-Anweisung
    helpers.ifcond = function(v1, operator, v2) {
        var opts    = arguments[arguments.length - 1];
        var logical = false;

        switch (operator)
        {
            case '<'  : if (v1 <   v2) { logical = true; } break;
            case '<=' : if (v1 <=  v2) { logical = true; } break;
            case '>'  : if (v1 >   v2) { logical = true; } break;
            case '>=' : if (v1 >=  v2) { logical = true; } break;
            case '==' : if (v1 ==  v2) { logical = true; } break;
            case '!=' : if (v1 !=  v2) { logical = true; } break;
            case '===': if (v1 === v2) { logical = true; } break;
            case '!==': if (v1 !== v2) { logical = true; } break;
            case '&&' : if (v1 &&  v2) { logical = true; } break;
            case '||' : if (v1 ||  v2) { logical = true; } break;
        }

        return (logical)? opts.fn(this) : opts.inverse(this);
    };

    // Prüft, ob Wert in Array enthalten ist
    helpers.contains = function(value, array) {
        var opts = arguments[arguments.length - 1];
        if (Array.isArray(array) && (array.indexOf(value) !== -1)) {
            return opts.fn(this);
        } else {
            return opts.inverse(this);
        }
    };

    // Prüft, ob Facetten-Kategorie aktiv ist
    helpers.isFilterActive = function(value, array = []) {
        var opts = arguments[arguments.length - 1];
        var isActive = false;

        for (var i = 0; i < array.length; i++) {
            for (var j = 0; j < array[i].categories.length; j++) {
                if (array[i].categories[j] == value) {
                    isActive = true;
                    break;
                }
            }
        }
        return (isActive) ? opts.fn(this) : opts.inverse(this);
    };

    // Holt Facetten-Label anhand eines Kategorie-Namens
    helpers.getLabel = function(type, name, facets = []) {
        var label = "";
        var arr   = name.split(':');
        for (var i = 0; i < facets.length; i++) {
            if (facets[i].name == arr[0]) {
                if (type=='facet') {
                    label = facets[i].label;
                    break;
                }

                for (var j = 0; j < facets[i].categories.length; j++) {
                    if (facets[i].categories[j].name == name) {
                        label = facets[i].categories[j].label;
                        break;
                    }
                }
            }
        }
        return label;
    };

    // Kategorie-Gewicht berechnen
    helpers.getKeywordWeight = function(wordCount, allCount) {
        return Math.round(wordCount/allCount*100 + 100);
    };

    // Sortierung der Facetten-Kategorien für die hierachische Ausgabe
    // Hinweis: Funktioniert nur, wenn Pfad im Label steckt und die Trennung mittels Slash erfolgt
    helpers.getSortedTree = function(categories, name, categoryFilters) {

        var sorted = [];

        if (categories.length > 0) {
            // Sortierung nach Anzahl und Kategoriename
            sorted = categories.sort(function (a, b) {
                if (a.matchingDocumentCount > b.matchingDocumentCount) return -1;
                if (a.matchingDocumentCount < b.matchingDocumentCount) return 1;
                if (a.name < b.name) return -1;
                if (a.name > b.name) return 1;
                return 0;
            });

            // max. gewähltes Level ermitteln
            var selectedLevel = 0;
            if (categoryFilters) {
                for (var i = 0; i < categoryFilters.length; i++) {
                    for (var j = 0; j < categoryFilters[i].categories.length; j++) {
                        if (categoryFilters[i].categories[j].lastIndexOf(name) === 0){
                            var levels = categoryFilters[i].categories[j].split('/').length;
                            if (levels > selectedLevel) selectedLevel = categoryFilters[i].categories[j].split('/').length;
                        }
                    }
                }
            }

            // Tiefe der tiefsten Ebene
            // var maxLevels = sorted[sorted.length-1].label.split('/').length;

            // Labels der Unterverzeichnisse kürzen
            for (var number = 0; number < sorted.length; number++) {
                var arr = sorted[number].label.split('/');

                //Nur aktuell gewähltes Verzeichnis anzeigen, Eltern nicht
                if (selectedLevel == arr.length) {
                    sorted[number].display = true;
                }

                // Tiefste anzuzeigende Ebene kürzen
                if (selectedLevel == arr.length-1) {
                    sorted[number].label   = arr[arr.length-1];
                    sorted[number].display = true;
                }

                sorted[number].marginLeft = 0;
                // Einrücken
                if (selectedLevel == arr.length-1 && selectedLevel != 0) {
                    sorted[number].marginLeft = 10;
                }
            }

            return sorted;
        }
    };

    // Alle Facettenkategorien ausgeben inkl. unfilteredCategories
    helpers.getPerspectives = function(root, facet) {
        var categories = [];

        var sorted;

        var catNames   = [];

        for (var i = 0; i < root.searchFacets.facets.length; i++) {
            if (facet == root.searchFacets.facets[i].name) {
                if (root.searchFacets.facets[i].categories) {
                    for (var j = 0; j < root.searchFacets.facets[i].categories.length; j++) {
                        categories.push({
                            "name"                  : root.searchFacets.facets[i].categories[j].name,
                            "label"                 : root.searchFacets.facets[i].categories[j].label,
                            "ordinal"               : root.searchFacets.facets[i].categories[j].ordinal,
                            "matchingDocumentCount" : root.searchFacets.facets[i].categories[j].matchingDocumentCount
                        });
                        catNames.push(root.searchFacets.facets[i].categories[j].name);
                    }
                }
            }
        }

        for (var k = 0; k < root.allFacets.facets.length; k++) {
            if (facet == root.allFacets.facets[k].name) {
                if (root.allFacets.facets[k].categories) {
                    for (var l = 0; l < root.allFacets.facets[k].categories.length; l++) {
                        if (catNames.indexOf(root.allFacets.facets[k].categories[l].name) == -1) {
                            categories.push({
                                "name"                  : root.allFacets.facets[k].categories[l].name,
                                "label"                 : root.allFacets.facets[k].categories[l].label,
                                "ordinal"               : root.allFacets.facets[k].categories[l].ordinal,
                                "matchingDocumentCount" : 0
                            });
                        }
                    }
                }
            }
        }

        sorted = categories.sort(function (a, b) {
            if (a.ordinal < b.ordinal) return -1;
            if (a.ordinal > b.ordinal) return 1;
            return 0;
        });

        return sorted;

    };

    // Gibt den Wert categoryLimit eines Objects aus
    helpers.getInitCategoryLimit = function(arr) {
        return arr && arr.categoryLimit ? arr.categoryLimit : 0;
    };

    // Gibt den Wert categoryLimit eines Objects für eine bestimmte Facette aus
    helpers.getActCategoryLimit = function(arr, name) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i].facetName == name) {
                return arr[i].categoryLimit;
            }
        }
        return 0;
    };

    // Debug Ausgabe
    helpers.debug = function(value) {
        console.log(value);
    };

    // format date field for results
    // (to remove time for SAG search)
    helpers.cutTimestamp = function(locale, value) {
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        if(value) {
            var cdate = new Date(value);
            return cdate.toLocaleDateString( locale, options);
        }
    }

    hb.registerHelper(helpers);

})(this, Handlebars);


// function getparam(opts, name, defv)
// {
//     if (opts && opts.hash) {
//         return opts.hash[name] || defv;
//     }
//     return defv;
// }
