//form product table dropdowns
$(document).ready(function () {

    $.ajax({
        url: "/content/dam/empower-sag/json/forms.json",
        dataType: 'json',
        success: function (data) {
          //Product
          var productsDropdown = $('#products');
          var productsMobDropdown = $('#products-mob');
         
          var selectProduct = data.products;
          // Append options to the dropdown
          $.each(selectProduct, function(i, item) {
            var selectProductLabel = item.label;
            // Create an option with label
            var option = $('<option>').val(selectProductLabel).text(selectProductLabel);
            // Append the option to the dropdown
            productsDropdown.append(option.clone());
            productsMobDropdown.append(option);
          });

          //Product Version
          var versionDropdown = $('#products-version');
          var versionMobDropdown = $('#products-version-mob');
          var selectVersion = data.productVersions;
          $.each(selectVersion, function(i, item) {
                var selectVersionLabel = item.label;
                      // Create an option with label
                var option = $('<option>').val(selectVersionLabel).text(selectVersionLabel);
                      // Append the option to the dropdown
                versionDropdown.append(option.clone());
                versionMobDropdown.append(option);
          });

          //Operating System
          var operatingSystemDropdown = $('#operating-system');
          var operatingSystemMobDropdown = $('#operating-system-mob');
          var selectoperatingSystem = data.operatingSystems;
          $.each(selectoperatingSystem, function(i, item) {
                var selectoperatingSystemLabel = item.label;

                var option = $('<option>').val(selectoperatingSystemLabel).text(selectoperatingSystemLabel);

                operatingSystemDropdown.append(option.clone());
                operatingSystemMobDropdown.append(option);
          });
        },
        error: function (error) {
          console.error('Error fetching JSON data:', error);
        }
    });
})