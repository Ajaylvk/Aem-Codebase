//* ****************************country change text start*****************************************
let loadedXML = '';

function loadCountries() {
    console.log('Marketo-Form: loadCountries()')
    if (loadedXML == '') {
        $.ajax({
            type: 'GET',
            url: '/conf/softwareag/assets/marketo-form-countries.xml',
            dataType: 'xml',
            success(xml) {
                loadedXML = xml;
            },
            error() {
                loadedXML = '';
            },
        });
    }
}

function proceedXML(selectedCountry) {
    if (loadedXML == '') {
        loadCountries();
    }
    setTimeout(() => {
        if (loadedXML != '') {
            let defaultText = '';
            let selectedText = '';
            const defaultCountry = 'default';
            const currentCountry = selectedCountry != '' ? selectedCountry : defaultCountry;

            const cNodes = loadedXML.getElementsByTagName('country');
            for (let i = 0; i < cNodes.length; i++) {
                const cNames = cNodes[i].getElementsByTagName('name');
                for (let j = 0; j < cNames.length; j++) {
                    if (cNames[j].firstChild.nodeValue == defaultCountry) {
                        defaultText = cNodes[i].getElementsByTagName('value')[0].firstChild.wholeText;
                    } else if (cNames[j].firstChild.nodeValue == currentCountry) {
                        selectedText = cNodes[i].getElementsByTagName('value')[0].firstChild.wholeText;
                    }
                }
            }
            if (selectedText != '' || defaultText != '') {
                const $textHolder = $('#consent');
                if ($textHolder != null) {
                    $textHolder.html(selectedText != '' ? selectedText : defaultText);
                }
            }
        }
    }, 1);
}

$(document).one('focus', '#Address_Visit_Country__c', function () {
    loadCountries();
    const $country = $(this);
    $country.change(() => {
        proceedXML($country.val());
    });
});

//* ********************************country change end*******************************************

$(document).one('focus', '#Company', function () {
    const $company = $(this);
    const $country = $('#Address_Visit_Country__c');
    const $state = $('#Address_Visit_State__c');
    const $duns = $("[name='DUNS_Number__c']");
    const $dnbAddress = $("[name='dnbAddress']");
    const $dnbAnnualRevenueCurrency = $("[name='dnbAnnualRevenueCurrency']");
    const $dnbAnnualRevenue = $("[name='dnbAnnualRevenue']");
    const $dnbCity = $("[name='dnbCity']");
    const $dnbEmployeeCount = $("[name='dnbEmployeeCount']");
    const $dnbPostalCode = $("[name='dnbPostalCode']");
    const $dnbSICCode = $("[name='dnbSICCode']");
    const $dnbSICDescription = $("[name='dnbSICDescription']");
    let selectedCompany;

    $company.change(() => {
        if (selectedCompany !== $company.val()) {
            $duns.val('');
        }
    });

    $state.change(() => {
        $company.val('');
        $duns.val('');
    });

    $country.change(() => {
        $state.val('');
        //$company.val('');
        $duns.val('');
        proceedXML($country.val());
    });

    $company.autocomplete({
        minLength: 2,
        autoFocus: true,
        source(request, response) {
            if ($country.val() != '') {
                const $xhr = $.ajax({
                    type: 'get',
                    url: '/dnbservice',
                    data: {
                        searchTerm: request.term,
                        country: $country.val(),
                        stateCode: $state.val() || '',
                    },
                    success(data) {
                        if (data != null) {
                            response(data.searchCandidates);
                        } else {
                            response([]);
                        }
                    },
                    error() {
                        response([]);
                    },
                });
            }
        },
        select(event, ui) {
            $duns.val(ui.item.organization.duns);
            setTimeout(() => {
                selectedCompany = ui.item.organization.primaryName;
                $company.val(selectedCompany);
            }, 1);
            if (ui.item.organization.financials) {
                setTimeout(() => { $dnbAnnualRevenueCurrency.val(ui.item.organization.financials[0].yearlyRevenue[0].currency); }, 1);
                setTimeout(() => { $dnbAnnualRevenue.val(ui.item.organization.financials[0].yearlyRevenue[0].value); }, 1);
            }
            if (ui.item.organization.numberOfEmployees) {
                setTimeout(() => { $dnbEmployeeCount.val(ui.item.organization.numberOfEmployees[0].value); }, 1);
            }

            setTimeout(() => { $dnbAddress.val(ui.item.organization.primaryAddress.streetAddress.line1); }, 1);
            setTimeout(() => { $dnbCity.val(ui.item.organization.primaryAddress.addressLocality.name); }, 1);
            setTimeout(() => { $dnbPostalCode.val(ui.item.organization.primaryAddress.postalCode); }, 1);

            if (ui.item.organization.primaryIndustryCodes) {
                setTimeout(() => { $dnbSICCode.val(ui.item.organization.primaryIndustryCodes[0].usSicV4); }, 1);
                setTimeout(() => { $dnbSICDescription.val(ui.item.organization.primaryIndustryCodes[0].usSicV4Description); }, 1);
            }
        },
        open() { // needed for lightbox, show above lightbox
            $('.ui-autocomplete').css('z-index', 99999999999999);
        },
        change() {
            if ($('[name=DUNS_Number__c]').val()) {
                const requestDunsNumbers = $.ajax({
                    type: 'get',
                    url: '/dnbservice',
                    data: {
                        apiCall: 'additionalinformation',
                        dunsNumber: $('[name=DUNS_Number__c]').val(),
                    },
                    success(data) {
                        $('[name=Domestic_Ultimate_DUNS_Number__c]').val(data.organization.corporateLinkage.domesticUltimate.duns);
                        $('[name=Global_Ultimate_DUNS_Number__c]').val(data.organization.corporateLinkage.globalUltimate.duns);
                    },
                });
            }
        },
    });

    $company.data('ui-autocomplete')._renderItem = function ($ul, item) {
        // var $div = $("<div>").text(item.organization.primaryName).addClass("ui-item-wrapper");

        let strToDisplay = `<strong>${item.organization.primaryName}</strong><br><small>`;
        if (item.organization.primaryAddress.streetAddress) {
            strToDisplay += item.organization.primaryAddress.streetAddress.line1 || '';
        }
        if (item.organization.primaryAddress.addressLocality) {
            strToDisplay += `, ${item.organization.primaryAddress.addressLocality.name}` || '';
        }
        if (item.organization.addressRegion) {
            strToDisplay += `, ${item.organization.primaryAddress.addressRegion.name}` || '';
        }
        // if (item.organization.primaryAddress.addressCountry)
        // {
        //    strToDisplay += item.organization.primaryAddress.addressCountry.isoAlpha2Code || "";
        // }
        strToDisplay += '</small>';

        const $div = $('<div>').html(strToDisplay).addClass('ui-item-wrapper');


        const $li = $('<li>').append($div);
        return $li.appendTo($ul);
    };
});

