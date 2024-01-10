import { populateDigitalData, populateDigitalDataWithCampaign, populateDigitalDataWithSingleCampaign } from './DigitalData';

/*
* AEM super class for components.
* Will provide helper functions that are relevant for every component.
*
* */
class Component {
    constructor(element) {
        this.element = element;
        this.props = this.element.dataset;
        this.name = 'AEM Component';
        this.title = '';
        this.digitalDataTrackable = false;
        this.hasCampaign = false;
        this.hasSingleCampaign = false;

        this.campaign1 = null;
        this.campaign2 = null;
        this.campaign3 = null;
    }
}

/*
* Register/instantiates a new AEM component.
* The DOM node mapped is the first child of the AEM-wrapped component DIV,
* here 'sag-textimage__container'.
*
* Example:
*   <div class="textimage>
*       <div class="sag-textimage__container">
*       ...
*       </div>
* </div>
*
* */
function registerComponent(selector, ComponentClass) {
    const components = document.querySelectorAll(`${selector} >:first-child`);

    for (let i = 0; i < components.length; i++) {
        const component = new ComponentClass(components[i]);
        component.init();

        if (component.digitalDataTrackable) {
            // eslint-disable-next-line no-console
            if (component.hasSingleCampaign && component.campaign1 != null) {
                populateDigitalDataWithSingleCampaign(component.name,
                    component.title,
                    component.campaign1);
            } else if (component.hasCampaign &&
                (component.campaign1 != null
                    || component.campaign2 != null
                    || component.campaign3 != null)) {
                populateDigitalDataWithCampaign(component.name, component.title,
                    component.campaign1, component.campaign2, component.campaign3);
            } else {
                populateDigitalData(component.name, component.title);
            }
        }
    }
}

export default {
    Component,
    registerComponent,
};
