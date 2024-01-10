$(document).ready(function(){
    checkLoginCookie();

    //get referrer from URL parameter
    var state = getParameterValues('referrer');
    if (state === undefined) {
        state = window.location.pathname;
        if (state === '/' || state === '/login.html') {
            state = '/dashboard.html';
        }
    }

    //login
    authURL = 'https://myaccount-dev.softwareag.com:443/am/oauth2/authorize?response_type=code&client_id=SAG_InternalPartners&scope=openid%20customer&redirect_uri=https://empower.dev.sag.adobecqms.net/bin/logincallback' + '&state=' + state;
    $('.login').on('click',function(){
        $('.login a').attr('href',authURL);
        // loggedInView();
    })

    if($('#login-section').length > 0) {
        configureLogout();
    }
    

    //logout
    $('#logout').on('click', function(){
        $.ajax({url: "/bin/logout", 
        success: function() {
            location.reload();
        },
        error: function() {
            location.reload();
        }
    });
        // logoutView();
    })

    /*  header search */
    //$('#header-v2-parent-Id .emp-search-icon').removeClass('headerV1__search-form-submit');
    $('#header-v2-parent-Id div.emp-search-icon').removeClass('block-inactive');
    //$('#header-v2-parent-Id button.emp-search-icon').addClass('block-inactive');
    $('#header-v2-parent-Id button.emp-search-icon').css('display', 'none');

    // header search icon click
     $('#header-v2-parent-Id div.emp-search-icon .search-core-dark120').on('click', function(){
        $('#header-v2-parent-Id .searchIcon.emp-search .emp-searchbar input').addClass('block-active').focus();
        $('#header-v2-parent-Id #child-div-2-Id').addClass('block-inactive');
        $('#header-v2-parent-Id .emp-close-btn').addClass('block-active');
        $(this).addClass('block-active');
        $('#header-v2-parent-Id .emp-input-parent').addClass('block-active');
        //$('#header-v2-parent-Id .emp-search-icon').addClass('headerV1__search-form-submit');
        $('#header-v2-parent-Id .searchIcon.emp-search .emp-searchbar .emp-search-inner-wrap').addClass('block-active').css('outline', 'none');

        $('#header-v2-parent-Id div.emp-search-icon').addClass('block-inactive');
        //$('#header-v2-parent-Id button.emp-search-icon').removeClass('block-inactive');
        $('#header-v2-parent-Id button.emp-search-icon').css('display', 'block');
    }) 

    // header input field cancel icon click
    $('#header-v2-parent-Id .emp-close-btn').on('click', function(){
        $('#header-v2-parent-Id .searchIcon.emp-search .emp-searchbar input').removeClass('block-active');
        $('#header-v2-parent-Id #child-div-2-Id').removeClass('block-inactive');
        $(this).removeClass('block-active');
        $('#header-v2-parent-Id .search-core-dark120').removeClass('block-active');
        $('#header-v2-parent-Id .emp-input-parent').removeClass('block-active');
        //$('#header-v2-parent-Id .emp-search-icon').removeClass('headerV1__search-form-submit');
        $('#header-v2-parent-Id .searchIcon.emp-search .emp-searchbar .emp-search-inner-wrap').removeClass('block-active');

        $('#header-v2-parent-Id div.emp-search-icon').removeClass('block-inactive');
        //$('#header-v2-parent-Id button.emp-search-icon').addClass('block-inactive');
        $('#header-v2-parent-Id button.emp-search-icon').css('display', 'none');
    }) 

    // header input field should close on click of anywhere except the input field
    $('#header-v2-parent-Id .emp-search-inner-wrap #emp-input-searchbar').on('blur', function(){
        $('#header-v2-parent-Id .searchIcon.emp-search .emp-searchbar input').removeClass('block-active');
        $('#header-v2-parent-Id #child-div-2-Id').removeClass('block-inactive');
        $('#header-v2-parent-Id .emp-close-btn').removeClass('block-active');
        $('#header-v2-parent-Id .search-core-dark120').removeClass('block-active');
        $('#header-v2-parent-Id .emp-input-parent').removeClass('block-active');
        //$('#header-v2-parent-Id .emp-search-icon').removeClass('headerV1__search-form-submit');
        $('#header-v2-parent-Id .searchIcon.emp-search .emp-searchbar .emp-search-inner-wrap').removeClass('block-active');

        $('#header-v2-parent-Id div.emp-search-icon').removeClass('block-inactive');
        // $('#header-v2-parent-Id button.emp-search-icon').addClass('block-inactive');
        $('#header-v2-parent-Id button.emp-search-icon').css('display', 'none');
    }) 

    $('#header-v2-parent-Id .emp-input-searchbar').on('click', function(){
        $(this).parents('.emp-search-inner-wrap').css('outline', 'none');
    })

    $('#header-v2-parent-Id .emp-input-searchbar').on('focus', function(){
        $(this).parents('.emp-search-inner-wrap').css('outline', 'none');
    })
})

function checkLoginCookie() {
    $getCookie = document.cookie;
    if ($getCookie.includes('isLoggedIn')) {
        loggedInView();
        getUserInfo();
    } else {
        logoutView();
        invalidateUserInfo();
    }
}

function loggedInView() {
    $('.request-access').addClass('block-inactive').removeClass('block-active');
    $('.login').addClass('block-inactive').removeClass('block-active');
    $('.secondary-button-profile').addClass('block-active').removeClass('block-inactive');
}

function logoutView() {
    $('.secondary-button-profile').removeClass('block-active').addClass('block-inactive');
    $('.request-access').removeClass('block-inactive').addClass('block-active');
    $('.login').removeClass('block-inactive').addClass('block-active');
}

function invalidateUserInfo() {
    localStorage.removeItem("username");
}

function getUserInfo() {
    if (localStorage.getItem("username") === null) {
        $.ajax({
            url: "/bin/userinfo",
            success: function(data) {
                var username = data.username;
                localStorage.setItem("username", username);
            },
            error: function(data) {
                console.log(data.responseText);
            }
        });
    }
}

function configureLogout() {
    //get the list of profile submenu
    $navArr = $('.secondary-button-profile .navlist');

    //get the last item from profile submenu
    $navArrLast = $navArr[$navArr.length-1];

    //select the immediate parent of profile submenu
    $navArrList = $navArrLast.parentElement;

    //duplicate the last child of profile submenu
    var getVal = $('.profile').attr('data-logout-label');
    var getIcon = $('.profile').attr('data-logout-icon');
    if (getVal){
        $navArrList.innerHTML += $navArrLast.outerHTML;
    }

    // get the new list of profile submenu
    $navNewArr = $('.secondary-button-profile .navlist');

    //get the updated list od profile submenu
    $navArrNewLast = $navNewArr[$navNewArr.length-1];

    //update the text of last profile submenu, get the value from getAttributeVal()
    $navNewVal = $($navArrNewLast).children().find('a').text(getVal);

    //give the id to the logout (last profile submenu)
    $($navNewVal).attr('id', 'logout');

    // remove the attribute href from logout
    $($navNewVal).removeAttr("href");

    $($navArrNewLast).children().find('img').attr('src', getIcon);
}

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