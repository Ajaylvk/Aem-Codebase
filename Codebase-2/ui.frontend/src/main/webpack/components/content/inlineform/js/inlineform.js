import $ from "jquery";
import AEM from "../../../../core/js/AemComponent";

class Inlineform extends AEM.Component {
  constructor(element) {
    super(element);
    this.name = "inlineform";
    this.compName = this.constructor.name;
    this.digitalDataTrackable = true;
    this.hasSingleCampaign = true;

    this.campaign1 = null;
    this.title = null;
    this.$titleComponent = null;
  }

  init() {
    this.$titleComponent = $("h2", this.element);
    this.title =
      this.$titleComponent != null ? this.$titleComponent.text().trim() : "";
  }
}

AEM.registerComponent(".inlineform", Inlineform);
