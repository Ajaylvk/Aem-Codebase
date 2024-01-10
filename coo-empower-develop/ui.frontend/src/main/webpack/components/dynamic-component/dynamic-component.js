$(document).ready(function(){

    if ($('.dynamic-component .incidents').length > 0) {
        getOpenIncidents();
    }

    if ($('.dynamic-component .fixes').length > 0) {
        var fixId = getParameterValues('id');
        if (fixId != null) {
            getFixDetails(fixId);
        }
    }

    if ($('.dynamic-component .productDetails').length > 0) {
        var productId = getParameterValues('id');
        if (productId != null) {
            getProductDetails(productId);
        }
    }

    if ($('.dynamic-component .notifications').length > 0) {
        var iframeSrc = $('.notifications-wrapper iframe').attr('src');
        $('.notifications-wrapper iframe').attr('src', iframeSrc + getNotificationParams());
    }

    $('.fixes-files-wrapper').on('click', 'a.download-fix', function(e) {
        e.preventDefault();
        var fixId = $(this).data('fixid');
        downloadFix(fixId);
    })
})

function getOpenIncidents() {
    //getUserInfo();
    var email = localStorage.getItem("username");
    //var apiEndpoint = "/bin/jsm?startAt=0&maxResults=5&fields=id,key,summary,customfield_10430,customfield_11700,customfield_10425,customfield_10462,created,priority,updated&jql=reporter='" + email + "' AND status != Closed AND 'Customer Viewable' IS EMPTY ORDER BY created DESC";
    var apiEndpoint = "https://sagdaasdev.apigw-aw-eu.webmethods.io/gateway/SupportJSM/1.0/rest/api/2/search";
    var total = 0;
    var showing = 5;
    $.ajax({
        url: apiEndpoint,
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("isLoggedIn")
        },
        success: function(data) {
            $('table.open-incident-table tbody').children( 'tr:not(:first)' ).remove();
            total = data.total;
            if (total > 5) {
                $('.dynamic-component .incidents .count').text('Showing ' + showing + ' of ' + total);
                $('.dynamic-component .incidents .count').show();
            } else {
                $('.dynamic-component .incidents .count').hide();
            }
            var issues = data.issues;
            if (data.issues.length == 0) {
                $('.dynamic-component .incidents .open-incident-table').empty();
                $('.dynamic-component .incidents .text_V3-headline').text('No Open Incidents');
            } else {
                $.each(issues, function(i, issue) {
                    //total += 1;
                    var referenceId = issue.key;
                    var summary = issue.fields.summary;
                    var status = issue.fields.customfield_10430 == null ? '' : issue.fields.customfield_10430.value;
                    var requesterFUll = issue.fields.customfield_10462 == null ? '' : issue.fields.customfield_10462[0];
                    var requester = requesterFUll.substring(0, requesterFUll.indexOf('(PSUP'));
                    var priority = issue.fields.priority == null ? '' : issue.fields.priority.name;
                    var createDate = formatDate(issue.fields.created);
                    var updateDate = formatDate(issue.fields.updated);
                    var productName = issue.fields.customfield_11700 == null ? '' : issue.fields.customfield_11700.value;
                    var productVersionFull = issue.fields.customfield_10425 == null ? '' : issue.fields.customfield_10425[0];
                    var productVersion = productVersionFull.substring(0, productVersionFull.indexOf('(PSUP'));
                    var incidentUrl = 'https://gs-jsm-test.softwareag.com/servicedesk/customer/portal/2/' + referenceId;

                    var mobileTable=$('<table><tr>'
                        + '<th>Reference</th>'
                        + '<td><a target="_blank" href="' + incidentUrl + '">' + '<svg xmlns="http://www.w3.org/2000/svg" height="12" width="12" viewBox="0 0 512 512"><!--!Font Awesome Free 6.5.1 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2023 Fonticons, Inc.--><path d="M320 0c-17.7 0-32 14.3-32 32s14.3 32 32 32h82.7L201.4 265.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L448 109.3V192c0 17.7 14.3 32 32 32s32-14.3 32-32V32c0-17.7-14.3-32-32-32H320zM80 32C35.8 32 0 67.8 0 112V432c0 44.2 35.8 80 80 80H400c44.2 0 80-35.8 80-80V320c0-17.7-14.3-32-32-32s-32 14.3-32 32V432c0 8.8-7.2 16-16 16H80c-8.8 0-16-7.2-16-16V112c0-8.8 7.2-16 16-16H192c17.7 0 32-14.3 32-32s-14.3-32-32-32H80z"/></svg>' + referenceId + '</a></td>'
                        + '</tr><tr>'
                        + '<th>Summary</th>'
                        + '<td><a target="_blank" href="' + incidentUrl + '">' + summary + '</a></td>'
                        + '</tr><tr>'
                        + '<th>Prod &amp; Ver</th>'
                        + '<td>' + productName + ' ' + productVersion + '</td>'
                        + '</tr><tr>'
                        + '<th>Status</th>'
                        + '<td>' + status + '</td>'
                        + '</tr><tr>'
                        + '<th>Requester</th>'
                        + '<td>' + requester + '</td>'
                        + '</tr><tr>'
                        + '<th>Created date</th>'
                        + '<td>' + createDate + '</td>'
                        + '</tr><tr>'
                        + '<th>Updated date</th>'
                        + '<td>' + updateDate + '</td>'
                        + '</tr><tr>'
                        + '<th>Priority</th>'
                        + '<td>' + priority + '</td>'
                        + '</tr></table>');
    
                    var incidentRow=$('<tr>'
                        + '<td><a target="_blank" href="' + incidentUrl + '">' + '<svg xmlns="http://www.w3.org/2000/svg" height="12" width="12" viewBox="0 0 512 512"><!--!Font Awesome Free 6.5.1 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free Copyright 2023 Fonticons, Inc.--><path d="M320 0c-17.7 0-32 14.3-32 32s14.3 32 32 32h82.7L201.4 265.4c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L448 109.3V192c0 17.7 14.3 32 32 32s32-14.3 32-32V32c0-17.7-14.3-32-32-32H320zM80 32C35.8 32 0 67.8 0 112V432c0 44.2 35.8 80 80 80H400c44.2 0 80-35.8 80-80V320c0-17.7-14.3-32-32-32s-32 14.3-32 32V432c0 8.8-7.2 16-16 16H80c-8.8 0-16-7.2-16-16V112c0-8.8 7.2-16 16-16H192c17.7 0 32-14.3 32-32s-14.3-32-32-32H80z"/></svg>' + referenceId + '</a></td>'
                        + '<td><a target="_blank" href="' + incidentUrl + '">' + summary + '</a></td>'
                        + '<td>' + productName + ' ' + productVersion + '</td>'
                        + '<td>' + status + '</td>'
                        + '<td>' + requester + '</td>'
                        + '<td>' + createDate + '</td>'
                        + '<td>' + updateDate + '</td>'
                        + '<td>' + priority + '</td>'
                        + '</tr>');
                    
                    $('div.open-incident-table.mobile').append(mobileTable);  
                    $('table.open-incident-table tbody').append(incidentRow);
    
                });
            }
            
        },
        error: function(data) {
            $('.dynamic-component .incidents .open-incident-table').empty();
            $('.dynamic-component .incidents .text_V3-headline').text('No Open Incidents');
        }
    });
}

function formatDate(dateMetadata) {
    let parsedDate = Date.parse(dateMetadata);
    const date = new Date(parsedDate);
    let day = new Intl.DateTimeFormat('en', { day: '2-digit' }).format(date);
    let month = new Intl.DateTimeFormat('en', { month: 'short' }).format(date);
    let year = new Intl.DateTimeFormat('en', { year: 'numeric' }).format(date);
    let formattedDate = `${day}/${month}/${year}`;
    return formattedDate;
}

function getFixDetails(fixId) {
    var apiEndpoint = "https://sagdaasdev.apigw-aw-eu.webmethods.io/gateway/ProductFixesWarnings/1.0/fixes/" + fixId + "?embed=(product,files)";
    $.ajax({
        url: apiEndpoint,
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("isLoggedIn")
        },
        success: function(data) {  
            //console.log(data);
            var fixResponse = data.getFixResponse;
            if(fixResponse != null) {
                var fixName = fixResponse.fixName;
                var osFamily = fixResponse.operatingSystemFamily;
                var version = fixResponse.productVersion;
                var articleId = fixResponse.articleId;
                var rawUpdated = fixResponse.updatedDate;
                var rawReleaseDate = fixResponse.releaseDate;
                var product = fixResponse.embedded.product != null ? fixResponse.embedded.product.productName : '';
                var link=$('<a target="_blank" href="' + window.location.href + '">' + window.location.href + '</a></td>'
                            + '</tr>');
                console.log(link);

                //var u_parsedDate = Date.parse(parseInt(rawUpdated));
                var u_date = new Date(parseInt(rawUpdated));
                var u_options = {
                    year: "numeric",
                    month: "short",
                    day: "2-digit",
                    hour: "numeric",
                    minute: "numeric",
                    hour12: false
                };
                var updated = new Intl.DateTimeFormat("en", u_options).format(u_date);

                //var r_parsedDate = Date.parse(parseInt(rawReleaseDate));
                var r_date = new Date(parseInt(rawReleaseDate));
                var r_options = {
                    year: "numeric",
                    month: "short",
                    day: "2-digit"
                };
                var releaseDate = new Intl.DateTimeFormat("en", r_options).format(r_date);

                $('.fixes-table-wrapper').show();
                //$('.fixes-header .text_V3-headline').text(fixName);
                $('.fixes-header .text_V3-headline, table.fixes-details tbody tr.name td').text(fixName);
                $('table.fixes-details tbody tr.os td').text(osFamily);
                $('table.fixes-details tbody tr.product td').text(product);
                $('table.fixes-details tbody tr.version td').text(version);
                $('table.fixes-details tbody tr.articleId td').text(articleId);
                $('table.fixes-details tbody tr.updated td').text(updated);
                $('table.fixes-details tbody tr.releaseDate td').text(releaseDate);
                $('table.fixes-details tbody tr.link td').html(link);

                var description = fixResponse.description;
                if (description != null) {
                    $('.fixes-description-wrapper').show();
                    console.log(description);
                    var descSection=$('<p>' + description + '</p>');
                    $('.description-details').html(descSection);
                }

                var requires = fixResponse.requiresList;
                if (requires != null && requires.length > 0) {
                    $('table.fixes-installed tbody').children( 'tr:not(:first)' ).remove();
                    $('.fixes-installed-wrapper').show();
                    $.each(requires, function(i, requiredFix) {
                        console.log(requiredFix);
                        var requiredFixRow=$('<tr>'
                            + '<td><a target="_blank" href="?id=' + requiredFix.id + '">' + requiredFix.fixName + '</a></td>'
                            + '</tr>');
                        $('table.fixes-installed tbody').append(requiredFixRow);
                    });
                }
                var replaces = fixResponse.replaceList;
                if (replaces != null && replaces.length > 0) {
                    $('table.fixes-replaced tbody').children( 'tr:not(:first)' ).remove();
                    $('.fixes-replaced-wrapper').show();
                    $.each(replaces, function(i, replacedFix) {
                        console.log(replacedFix);
                        var replacedFixRow=$('<tr>'
                            + '<td><a target="_blank" href="?id=' + replacedFix.id + '">' + replacedFix.fixName + '</a></td>'
                            + '</tr>');
                        $('table.fixes-replaced tbody').append(replacedFixRow);
                    });
                }
                var files = fixResponse.embedded.files;
                if (files != null && files.length > 0) {
                    $('table.fixes-files.desktop tbody').children( 'tr:not(:first)' ).remove();
                    $('.fixes-files-wrapper').show();
                    $.each(files, function(i, file) {
                        var os = file.operatingSystem;
                        var type = file.type;
                        var name = file.fileName;
                        var fileId = file.id;

                        var downloadHtml = '<a class="download-fix" data-fixid="' + fileId + '" target="_blank" href>' 
                            + '<img src="/content/dam/empower-sag/icon/software-download-center.svg" alt="downloadFile" width="24" height="24"></a>';
                        var readmeHtml = '<a class="download-fix" data-fixid="' + fileId + '" target="_blank" href>' 
                            + '<img src="/content/dam/empower-sag/icon/readme.svg" alt="readMe" width="24" height="24"></a>';

                        if(type == 'ReadMe') {
                            downloadHtml = '';
                        } else {
                            readmeHtml = '';
                        }
                        var fileDesktopRow=$('<tr>'
                            + '<td>' + os + '</td>'
                            + '<td>' + downloadHtml + '</td>'
                            + '<td>' + readmeHtml + '</td>'
                            + '</tr>');
                        
                        $('table.fixes-files.desktop tbody').append(fileDesktopRow);
                        $('.fixes-files-wrapper table.fixes-files.mobile tbody tr.os').text('<td>' + os + '</td>');
                        $('.fixes-files-wrapper table.fixes-files.mobile tbody tr.download td').html('<td>' + downloadHtml + '</td>');
                        $('.fixes-files-wrapper table.fixes-files.mobile tbody tr.readme td').html('<td>' + readmeHtml + '</td>');
                    });
                }
            }

        },
        error: function(data) {
            var errorHtml=$('<p id="error-msg">Error happened while fetching results. Please try after some time.</p>');
            $('.fixes-wrap').html(errorHtml);
        }
    });
}

function getProductDetails(productId) {
    var apiEndpoint = "https://sagdaasdev.apigw-aw-eu.webmethods.io/gateway/SDCPortal/1.0/getItems";
    $.ajax({
        url: apiEndpoint,
        type: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("isLoggedIn")
        },
        data: JSON.stringify(
            {
              "product_id": productId
            }
        ),
        success: function(data) {  
            //console.log(data);
            var items = data.Items;
            if(items != null) {
                $('table.product-details-table tbody').children( 'tr:not(:first)' ).remove();
                $.each(items, function(i, item) {
                    var itemId = item.id;
                    var itemName = item.name;
                    var itemVersion = item.version;
                    var itemFileSize = item.file_size;
                    var itemDownloadUrl = item.file_name_download;
                    console.log(item.name);

                    var mobileTable=$('<table><tr>'
                        + '<th>Product Id</th>'
                        + '<td>' + itemId + '</td>'
                        + '</tr><tr>'
                        + '<th>Name</th>'
                        + '<td>' + itemName + '</td>'
                        + '</tr><tr>'
                        + '<th>Version</th>'
                        + '<td>' + itemVersion + '</td>'
                        + '</tr><tr>'
                        + '<th>File size</th>'
                        + '<td>' + itemFileSize + '</td>'
                        + '</tr><tr>'
                        + '<th>Download</th>'
                        + '<td><a target="_blank" href="' + itemDownloadUrl + '"><img src="/content/dam/empower-sag/icon/software-download-center.svg" alt="downloadFile" width="24" height="24"></a></td>'
                        + '</tr></table>');
    
                    var itemRow=$('<tr>'
                        + '<td>' + itemId + '</td>'
                        + '<td>' + itemName + '</td>'
                        + '<td>' + itemVersion + '</td>'
                        + '<td>' + itemFileSize + '</td>'
                        + '<td><a target="_blank" href="' + itemDownloadUrl + '"><img src="/content/dam/empower-sag/icon/software-download-center.svg" alt="downloadFile" width="24" height="24"></a></td>'
                        + '</tr>');
                    
                    $('div.product-details-table.mobile').append(mobileTable);
                    $('table.product-details-table.desktop tbody').append(itemRow);
            
                });
            }
        },
        error: function(data) {
            console.log('Error happened while fetching product details!');
        }
    });
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

function getNotificationParams() {
    var paramUrl = '';
    $.ajax({
        url: "/bin/userinfo",
        async: false,
        success: function(data) {
            var email = data.email;
            var customerNumber = data.customerNumber;
            paramUrl = '?userid=' + email + '&custno=' + customerNumber + '&memberof=CN=Extranet,CN=Partner,';
        },
        error: function(data) {
            console.log(data.responseText);
        }
    });
    return paramUrl;
}

function downloadFix(fixId) {
    var apiEndpoint = "https://sagdaasdev.apigw-aw-eu.webmethods.io/gateway/ProductFixesWarnings/1.0/files/" + fixId + "/download";
    $.ajax({
        url: apiEndpoint,
        type:"POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + getCookie("isLoggedIn")
        },
        success: function(data) {
            var downloadInfo = data.generateDownloadInformationResponse;
            if(downloadInfo != null) {
                var filePath = downloadInfo.location;
                if(filePath != null) {
                    window.open(filePath, '_blank').focus();
                }
            }
        },
        error: function(data) {
            console.log('Error happened while downloading fix!');
        }
    });
    return false;
}