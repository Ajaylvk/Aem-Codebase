import $ from 'jquery';
import wait from '../../../../core/js/utils/wait';
import AEM from '../../../../core/js/AemComponent';

class BottomRibbon extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor
  /* eslint-disable */
  constructor(element) {
    super(element);

    this.name = "Bottom Ribbon";

    this.digitalDataTrackable = true;
  }

  init() {
    $(".btmRibbon-label").on("mouseover", function() {
      $(this).css("cursor", "pointer");
    });
    const accordion = $(".btmRibbon-label");
    for (let i = 0; i < accordion.length; i++) {
      accordion.off().on("click", (e) => {
        this._toggleAccordionClass(e);
      });
    }
  }

  _toggleAccordionClass(e) {
    const accordion = $(".btmRibbon-label");
    const index = jQuery.inArray(e.currentTarget, accordion);
    if (!e.currentTarget.classList.contains("active")) {
      this._openAccordion(index);
    } else {
      this._closeAccordion(index);
    }
  }

  _openAccordion(accordion_index) {
    this._setHeightOnAccordionItemContent(
      this._getAccordionContentHeight(accordion_index),
      accordion_index
    );
    document
      .getElementsByClassName("btmRibbon-accordion-content")
      [accordion_index].classList.add("btmRibbon-margin-active-content");
    document
      .getElementsByClassName("btmRibbon-contentBx")
      [accordion_index].classList.add("active-content");
    document
      .getElementsByClassName("btmRibbon-label")
      [accordion_index].classList.add("active");
    document
      .getElementsByClassName("bottomRibbon-desc")
      [accordion_index].classList.add("hide-cloned-description");
    document
      .getElementsByClassName("bottomRibbon-button")
      [accordion_index].classList.add("hide-cloned-description");
  }
  _closeAccordion(accordion_index) {
    this._setHeightOnAccordionItemContent("0px", accordion_index);
    document
      .getElementsByClassName("btmRibbon-accordion-content")
      [accordion_index].classList.remove("btmRibbon-margin-active-content");

    wait(150).then(() => {
      document
        .getElementsByClassName("btmRibbon-label")
        [accordion_index].classList.remove("active");
    });
    wait(300).then(() => {
      const flag_phone = window.screen.width <= 414;
      document
        .getElementsByClassName("btmRibbon-contentBx")
        [accordion_index].classList.remove("active-content");
      const bottomClonedButton = document.getElementsByClassName(
        "bottomRibbon-cloned-button"
      )[accordion_index];
      const bottomClonedButton2 = document.getElementsByClassName(
        "bottomRibbon-Description"
      )[accordion_index];

      while (bottomClonedButton.hasChildNodes()) {
        bottomClonedButton.removeChild(bottomClonedButton.firstChild);
      }
      if (flag_phone) {
        while (
          document.getElementsByClassName("bottomRibbon-Description")[
            accordion_index
          ].children.length == 3
        ) {
          document
            .getElementsByClassName("bottomRibbon-Description")
            [accordion_index].lastChild.remove();
        }
      }
      document
        .getElementsByClassName("bottomRibbon-desc")
        [accordion_index].classList.remove("hide-cloned-description");
      document
        .getElementsByClassName("bottomRibbon-button")
        [accordion_index].classList.remove("hide-cloned-description");
    });
  }
  _setHeightOnAccordionItemContent(height, acc_index) {
    if (
      document.getElementsByClassName("btmRibbon-accordion-content").length > 0
    ) {
      document
        .getElementsByClassName("btmRibbon-accordion-content")
        [acc_index].setAttribute("style", `height: ${height};`);
    }
  }

  _getAccordionContentHeight(acc_index) {
    const flag_phone = window.screen.width <= 414;
    const node = document.getElementsByClassName("bottomRibbon-button")[
      acc_index
    ];
    const clone = node.cloneNode(true);
    document
      .getElementsByClassName("bottomRibbon-cloned-button")
      [acc_index].appendChild(clone);
    if (flag_phone) {
      const node2 = document.getElementsByClassName("bottomRibbon-image")[
        acc_index
      ];
      const clone2 = node2.cloneNode(true);

      document
        .getElementsByClassName("bottomRibbon-Description")
        [acc_index].appendChild(clone2);
    }
    document
      .getElementsByClassName("btmRibbon-accordion-content")
      [acc_index].setAttribute("style", "display: flex;");

    // if (flag_phone) {
    //   const height = `${document.getElementsByClassName(
    //     "bottomRibbon-Description"
    //   )[acc_index].scrollHeight +
    //     document.getElementsByClassName("bottomRibbon-image")[acc_index]
    //       .scrollHeight}px`;
    //   document
    //     .getElementsByClassName("btmRibbon-accordion-content")
    //     [acc_index].setAttribute("style", "display: none;");
    //   return height;
    // }
    //  else {
    const height = `${
      document.getElementsByClassName("bottomRibbon-Description")[acc_index]
        .scrollHeight
    }px`;
    document
      .getElementsByClassName("btmRibbon-accordion-content")
      [acc_index].setAttribute("style", "display: none;");
    return height;
    // }
  }
  // eslint-disable-next-line class-methods-use-this
}

AEM.registerComponent(".bottomribbon", BottomRibbon);
