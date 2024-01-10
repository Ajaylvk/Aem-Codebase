$(document).ready(function(){
    // desktop delete functionality
    $('.desktop-tablet').on('click', '.delete-icon', function(){
        $(this).parents('tr').remove();
    })

    // mobile delete functionality
    $('.mobile').on('click', '.delete-icon', function(){
        $(this).parents('tbody').remove();
    })

    // Adding row on click to Add New Row button
    let prevProductVal = $('#products').val();
    $('.desktop-tablet').on('change', '#products', function(){
        selectedProductVal = $('#products').val();

        $('#products > option').each(function(i, item) {
            if(item.value === prevProductVal) {
                //$(item).removeAttr("selected");
                $(item).remove('attr', 'selected');
            }
            if(item.value === selectedProductVal) {
                $(item).attr("selected", "true");
                prevProductVal = $(item).val();
            }
        })
    })

    let prevVersionVal = $('#products-version').val();
    $('.desktop-tablet').on('change', '#products-version', function(){
        selectedVersionVal = $('#products-version').val();

        $('#products-version > option').each(function(i, item) {
            if(item.value === prevVersionVal) {
                //$(item).removeAttr("selected");
                $(item).remove('attr', 'selected');
            }
            if(item.value === selectedVersionVal) {
                $(item).attr("selected", "true");
                prevVersionVal = $(item).val();
            }
        })
    })

    let prevOSVal = $('#operating-system').val();
    $('.desktop-tablet').on('change', '#operating-system', function(){
        selectedOSVal = $('#operating-system').val();

        $('#operating-system > option').each(function(i, item) {
            if(item.value === prevOSVal) {
                //$(item).removeAttr("selected");
                $(item).remove('attr', 'selected');
            }
            if(item.value === selectedOSVal) {
                $(item).attr("selected", "true");
                prevOSVal = $(item).val();
            }
        })
    })

    // if (selectedProductVal && selectedVersionVal && selectedOSVal) {
    //     $('.desktop-tablet .add-btn button').remove('prop','disabled');
    // }

    var count=1;
    $('.desktop-tablet').on('click', '.add-btn button', function () {
        var dynamicRowHTML =
        '<tr class="row-added">' +
            '<td name="Product" class="value-selected product">' + selectedProductVal +'</td>' +
            '<td name="Product Version" class="value-selected version">' + selectedVersionVal + '</td>' +
            '<td name="Operating System" class="value-selected os">' + selectedOSVal + '</td>' +
            '<td class="delete-icon">' +
            '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">' +
                '<path d="M7 21C6.45 21 5.97917 20.8042 5.5875 20.4125C5.19583 20.0208 5 19.55 5 19V6H4V4H9V3H15V4H20V6H19V19C19 19.55 18.8042 20.0208 18.4125 20.4125C18.0208 20.8042 17.55 21 17 21H7ZM17 6H7V19H17V6ZM9 17H11V8H9V17ZM13 17H15V8H13V17Z" fill="#011F3D"/>' +
            '</svg>' +
            '</td>' +
        '</tr>';
        $('.desktop-tablet tbody').append(dynamicRowHTML);
        count++;
    });

    let prevMobProductVal = $('#products-mob').val();
    $('.mobile').on('change', '#products-mob', function(){
        selectedMobProductVal = $('#products-mob').val();

        $('#products-mob > option').each(function(i, item) {
            if(item.value === prevMobProductVal) {
                //$(item).removeAttr("selected");
                $(item).remove('attr', 'selected');
            }
            if(item.value === selectedMobProductVal) {
                $(item).attr("selected", "true");
                prevMobProductVal = $(item).val();
            }
        })
    })

    let prevMobVersionVal = $('#products-version-mob').val();
    $('.mobile').on('change', '#products-version-mob', function(){
        selectedMobVersionVal = $('#products-version-mob').val();

        $('#products-version-mob > option').each(function(i, item) {
            if(item.value === prevMobVersionVal) {
                //$(item).removeAttr("selected");
                $(item).remove('attr', 'selected');
            }
            if(item.value === selectedMobVersionVal) {
                $(item).attr("selected", "true");
                prevMobVersionVal = $(item).val();
            }
        })
    })

    let prevMobOSVal = $('#operating-system-mob').val();
    $('.mobile').on('change', '#operating-system-mob', function(){
        selectedMobOSVal = $('#operating-system-mob').val();

        $('#operating-system-mob > option').each(function(i, item) {
            if(item.value === prevMobOSVal) {
                //$(item).removeAttr("selected");
                $(item).remove('attr', 'selected');
            }
            if(item.value === selectedMobOSVal) {
                $(item).attr("selected", "true");
                prevMobOSVal = $(item).val();
            }
        })
    })

    // if (selectedMobProductVal && selectedMobVersionVal && selectedMobOSVal) {
    //     $('.mobile .add-btn button').remove('prop','disabled');
    // }

    $('.mobile').on('click', '.add-btn button', function () {
        var mobProduct =  '<tr>' +
            '<th>Product</th>' +
            '<td name="Product" class="value-selected product">' + selectedMobProductVal + '</td>'
            '</tr>' ;
        var mobVersion = '<tr>' +
            '<th>Product Version</th>' +
            '<td name="Version" class="value-selected version">' + selectedMobVersionVal + '</td>' +
            '</tr>' ;
        var mobOS = '<tr>' +
            '<th>Operating System</th>' +
            '<td name="Operating System" class="value-selected os">' + selectedMobOSVal + '</td>' +
            '</tr>';       
        var dynamicMobRowHTML = 
        '<tbody class="row-added">' + mobProduct + mobVersion + mobOS +
            '<tr>' +
              '<th></th>' +
              '<td class="delete-icon">' +
                '<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">' +
                  '<path d="M7 21C6.45 21 5.97917 20.8042 5.5875 20.4125C5.19583 20.0208 5 19.55 5 19V6H4V4H9V3H15V4H20V6H19V19C19 19.55 18.8042 20.0208 18.4125 20.4125C18.0208 20.8042 17.55 21 17 21H7ZM17 6H7V19H17V6ZM9 17H11V8H9V17ZM13 17H15V8H13V17Z" fill="#011F3D"/>' +
                '</svg>' +
              '</td>' +
            '</tr>' +
          '</tbody>';
        $('.mobile').append(dynamicMobRowHTML);
        count++;
    });

    // reset functionality
    $('.form-reset').on('click', function(){
        $('input').val('');
        $('.desktop-tablet .delete-icon').parents('tr.row-added').remove();
        $('.mobile .delete-icon').parents('tbody.row-added').remove();
    })

    // submit functionality
    var resultCount = 0;
    var postBody = '';
    $('.form-submit').on('click', function(){
        formBody = {};
        //console.log('Form submitted!');
        $form = $(this).parents('.emp-forms-wrapper');

        $productRows = $form.find('.product-table .row-added');
        if ($productRows.length > 0) {
            jsonObj = {};
            jsonArr = [];
            $.each($productRows, function(rowIndex, productRow) {
                //console.log(productRow);
                $productColumns = $(productRow).find('td.value-selected');
                item = {};
                $.each($productColumns, function(columnIndex, data) {
                    //console.log(data);
                    var key = $(data).attr('name');
                    //console.log(key);
                    var value = $(data).text();
                    //console.log(value);
                    item [key] = value;
                });
                jsonArr.push(item);
            });
            jsonObj ["Items"] = jsonArr;
            jsonString = JSON.stringify(jsonObj);
            //console.log(jsonString);
            formBody ["Product Details"] = jsonString;
        }

        $inputs = $form.find('input,textarea');
        if ($inputs.length > 0) {
            $.each($inputs, function(inputIndex, input) {
                var inputKey = $(input).attr('name');
                //console.log('Key:: ' + inputKey);
                var inputValue = $(input).val();
                //console.log('Value:: ' + inputValue);
                if (inputValue !== null && inputValue !== '') {
                    formBody [inputKey] = inputValue;
                }
            });
        }

        //var empFormsWrapper = document.querySelector('.emp-forms-wrapper');
        // Read the value of the 'data-recipientemail' attribute
        var supportEmailAddress = $('.emp-forms-wrapper').data('recipientemail');
        if (supportEmailAddress !== null && supportEmailAddress !== '') {
            formBody ['CC Email'] = supportEmailAddress;
        }
        
        var recipientEmailArray = [];
        // Read the logged in user name from localStorage
        var username = localStorage.getItem('username');
        if (username !== null && username !== '') {
            recipientEmailArray.push(username);
        }
        
        if (recipientEmailArray !== null && recipientEmailArray !== '') {
            formBody ['Recipient Email'] = recipientEmailArray;
        }

        // Read the value of the 'data-mailsubject' attribute
        var mailSubjectAttribute = $('.emp-forms-wrapper').data('mailsubject');

        if (mailSubjectAttribute !== null && mailSubjectAttribute !== '') {
                    formBody ['Mail Subject'] = mailSubjectAttribute;
        }

        if (!$.isEmptyObject(formBody)) {
            postBody = JSON.stringify(formBody);
        }
        console.log('Form POST Body:: ' + postBody);
        sendEmail(postBody, $form);
    })
})

function sendEmail(postBody, thisObj) {
    $.ajax({
        url: "/bin/sendemail",
        type:"POST",
        data: postBody,
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            thisObj.hide();
            thisObj.siblings('.emp-forms-succes-wrapper').show()
        },
        error: function(data) {
            console.log('Error while sending email!');
        }
    });
}