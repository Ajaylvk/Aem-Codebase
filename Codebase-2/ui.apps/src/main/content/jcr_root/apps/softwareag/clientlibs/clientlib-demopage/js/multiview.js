// Prototyping code.
var Helper = {
    defaultHeight: 400,
    isFirstLoaded: false,
    init: function () {
        this.attachHandlers();
        this.loadFirst();
        //this.storeDimensionsOnNode();
        this.setDefaults();
    },

    setDefaults: function () {
        $('select#scale-all').val('percent75').trigger('change');
    },
    attachHandlers: function () {

        var _this = this;
        $('.components-selection').on('click', 'a', function (e) {
            e.preventDefault();
            e.stopPropagation();
            var url = this.href;
            url += '?wcmmode=disabled';
            $('.link-active').removeClass('link-active');
            $(this).addClass('link-active');
            // if ($(this).attr('data-iframeheight')) {
            //     var height = $(this).attr('data-iframeheight');
            //     $('.iframe, .component-categories').height(height);
            // } else {
            //     $('.iframe, .component-categories').height(_this.defaultHeight);
            // }

            var name = $(this).text();
            var path = this.href;


            $('#selected').text(name);

            // update URL for deeplinking when link is clicked
            var updatedUrl = new URL(location.href);
            updatedUrl.searchParams.set('path', path);
            window.history.pushState("", "", updatedUrl);

            if (console) {
                console.log('clicked');
            }

            $('#components-toggle').toggleClass('open');
            $('#categories-container').toggleClass('open');

            _this.loadSource(url);
        });

        $('select.scale').change(function () {
            console.log('select.scale');
            const iframe = $(this).parents('.demo-page-viewport:first').find(
                'iframe'),
                $parent = $(this).parents('.demo-page-viewport:first');

            iframe.removeClass('percent25 percent50 percent75 percent100');
            iframe.addClass($(this).val());

            setTimeout(function () {
                var newWidth = iframe[0].getBoundingClientRect();

                $parent.width(newWidth.width);
                $parent.height(newWidth.height + $parent.find(
                    '.viewport-menu:first').height() + parseInt(
                    $parent.css('padding-bottom')));

            }, 750);

            //$(this).parents('.demo-helper-viewport:first').width(iframe)
        });

        $('select#scale-all').change(function () {
            var globalValue = $(this).val();

            if (console) {
                console.log('global valiue chanhge', globalValue);
            }

            $.each($('select.scale'), function () {
                $(this).val(globalValue);
                $(this).trigger('change');
            });

            //$(this).parents('.demo-helper-viewport:first').width(iframe)
        });

        this.calcDimensions();

        $('.rotate-button').on('click', function () {
            var iframe = $(this).parents('.demo-page-viewport:first').find(
                'iframe'),
                $parent = $(this).parents('.demo-page-viewport:first');

            iframe.css({
                width: iframe.height(),
                height: iframe.width()
            });

            setTimeout(function () {
                var newWidth = iframe[0].getBoundingClientRect();
                $parent.width(newWidth.width);
                $parent.height(newWidth.height + $parent.find(
                    '.viewport-menu:first').height() + parseInt(
                    $parent.css('padding-bottom')));

            }, 500);

            _this.calcDimensions();
            return false;
        });
    },
    loadSource: function (url) {
        $('.iframe').attr('src', url);

        //$('#components-toggle').trigger('click');

    },
    loadFirst: function () {
        var _this = this;
        var link = $('.categories').find('a:first');

        var url = link[0].href;
        //url = '/content/softwareag/test/test-page-generic-model.html';
        url += '?wcmmode=disabled';
        $('.link-active').removeClass('link-active');
        $(link).addClass('link-active');
        // if ($(this).attr('data-iframeheight')) {
        //     var height = $(this).attr('data-iframeheight');
        //     $('.iframe, .component-categories').height(height);
        // } else {
        //     $('.iframe, .component-categories').height(_this.defaultHeight);
        // }


        var updatedUrl = new URL(location.href);
        var paramUrl = updatedUrl.searchParams.get('path');
        if(paramUrl.length > 0) {
            url = paramUrl;
        }
        _this.loadSource(url);

        Helper.isFirstLoaded = true;
    },
    calcDimensions: function () {
        console.log('calcDimensions');
        $.each($('.demo-page-viewport'), function (node, counter) {
            const iframe = $(this).find('iframe');
            const $parent = $(this);
            //$(this).find('span.dimensionsInfo').text(iframe.width().toFixed() + 'x' + iframe.height().toFixed());

            setTimeout(function () {
                var newWidth = iframe[0].getBoundingClientRect();

                $parent.width(newWidth.width);
                $parent.height(newWidth.height + $parent.find(
                    '.viewport-menu:first').height() + parseInt(
                    $parent.css('padding-bottom')));

            }, 750);

        });

    },
    storeDimensionsOnNode: function () {
        $.each($('iframe'), function () {
            $(this).attr({
                'data-width': $(this).width(),
                'data-height': $(this).height()
            });
        });
    }
};
$(function () {
    Helper.init();
});
