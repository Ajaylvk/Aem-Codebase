//Author : Anmol Agrawal

//Date :27/04/23

(function ($, window, document, Granite) {
  "use strict";

  $(document).on("foundation-contentloaded", function () {
    setTimeout(function () {
      var dropdown = $('.coral-Form-field[name="./backgroundColorC"]');

      var checkbox = $('.coral-Form-field[name="./checkboxGradient"]');

      var gradientdropdown = $('.coral-Form-field[name="./gradientPreset"]');

      var initialOption = $(dropdown).find(
        ".coral3-SelectList-item.is-selected"
      );
      var initialOptionText = initialOption.attr("value");
      var ischeckboxchecked = $(
        '.coral-Form-field[name="./checkboxGradient"]'
      ).prop("checked");

      if (initialOptionText.indexOf("gradient") == -1 && !ischeckboxchecked) {
        checkbox.prop("checked", false);
        checkbox.prop("disabled", false);
        checkbox.css("pointer-events", "auto");
        $(checkbox)
          .find(".coral3-Checkbox-checkmark")
          .css("background-color", "#fff");
        $('input[name="./checkboxGradient"]').prop("disabled", false);
      } else if (
        initialOptionText.indexOf("gradient") == -1 &&
        ischeckboxchecked
      ) {
        checkbox.prop("checked", true);
        checkbox.prop("disabled", false);
        checkbox.css("pointer-events", "auto");
        $(checkbox)
          .find(".coral3-Checkbox-checkmark")
          .css("background-color", "#fff");

        $('.coral-Form-field[name="./gradientPreset"]')
          .parent()
          .parent()
          .removeClass("hide");
      } else {
        checkbox.prop("checked", true);
        checkbox.css("pointer-events", "none");
        $(checkbox)
          .find(".coral3-Checkbox-checkmark")
          .css("background-color", "#ddd");
        $('.coral-Form-field[name="./gradientPreset"]')
          .parent()
          .parent()
          .removeClass("hide");
      }
    }, 100);

    var dropdown = $('.coral-Form-field[name="./backgroundColorC"]');
    var checkbox = $('.coral-Form-field[name="./checkboxGradient"]');

    var gradientdropdown = $('.coral-Form-field[name="./gradientPreset"]');

    dropdown.on("change", function () {
      setTimeout(function () {
        var selectedOption = $(dropdown).find(
          ".coral3-SelectList-item.is-selected"
        );
        var optionText = selectedOption.attr("value");

        if (optionText.indexOf("gradient") !== -1) {
          checkbox.prop("checked", true);
          checkbox.css("pointer-events", "none");
          $(checkbox)
            .find(".coral3-Checkbox-checkmark")
            .css("background-color", "#ddd");
          $('.coral-Form-field[name="./gradientPreset"]')
            .parent()

            .parent()

            .removeClass("hide");
        } else {
          checkbox.prop("checked", false);
          checkbox.prop("disabled", false);
          checkbox.css("pointer-events", "auto");
          $(checkbox)
            .find(".coral3-Checkbox-checkmark")
            .css("background-color", "#fff");

          $('input[name="./checkboxGradient"]').prop("disabled", false);

          $('.coral-Form-field[name="./gradientPreset"]')
            .parent()

            .parent()

            .addClass("hide");
        }
      }, 300);
    });
  });
})(jQuery, window, document, Granite);
