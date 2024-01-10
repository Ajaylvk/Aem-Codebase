import $ from 'jquery';
import wait from '../../../../core/js/utils/wait';
import AEM from '../../../../core/js/AemComponent';

class Accordion extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor
  /* eslint-disable */
  constructor(element) {
    super(element);

    this.name = "Accordion";

    this.digitalDataTrackable = true;
  }

  init() {
    $("accordion-label").on("mouseover", function() {
      $(this).css("cursor", "pointer");
    });
    const accordion = $(".accordion-label");
    const accordion_content = document.getElementsByClassName("content");
    for (let i = 0; i < accordion.length; i++) {
      accordion.off().on("click", (e) => {
        this._toggleAccordionClass(e);
      });
    }
  }

  _toggleAccordionClass(e) {
    const accordion = $(".accordion-label");
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
      .getElementsByClassName("accordion-content")
      [accordion_index].classList.add("margin-active-content");
    document
      .getElementsByClassName("contentBx")
      [accordion_index].classList.add("active-content");
    document
      .getElementsByClassName("accordion-label")
      [accordion_index].classList.add("active");
  }

  _closeAccordion(accordion_index) {
    this._setHeightOnAccordionItemContent("0px", accordion_index);
    document
      .getElementsByClassName("accordion-content")
      [accordion_index].classList.remove("margin-active-content");
    wait(150).then(() => {
      document
        .getElementsByClassName("accordion-label")
        [accordion_index].classList.remove("active");
    });
    wait(300).then(() => {
      document
        .getElementsByClassName("contentBx")
        [accordion_index].classList.remove("active-content");
    });
  }

  _setHeightOnAccordionItemContent(height, acc_index) {
    if (document.getElementsByClassName("accordion-content").length > 0) {
      document
        .getElementsByClassName("accordion-content")
        [acc_index].setAttribute("style", `height: ${height};`);
    }
  }

  _getAccordionContentHeight(acc_index) {
    document
      .getElementsByClassName("accordion-content")
      [acc_index].setAttribute("style", "display: block;");
    const height = `${
      document.getElementsByClassName("accordion-content")[acc_index]
        .scrollHeight
    }px`;
    document
      .getElementsByClassName("accordion-content")
      [acc_index].setAttribute("style", "display: none;");
    return height;
  }
  // eslint-disable-next-line class-methods-use-this
}

AEM.registerComponent(".accordion", Accordion);
