function populateDigitalData(componentName, componentTitle) {
    const componentInfo = {
        name: componentName,
        title: componentTitle,
    };

    window.digitalData.page.components.push(componentInfo);
}

function populateDigitalDataWithCampaign(componentName, componentTitle,
    campaign1, campaign2, campaign3) {
    const componentInfo = {
        name: componentName,
        title: componentTitle,
        campaign: [
            {
                boxType: 'TeaserBox',
                campaign: campaign1 != null ? campaign1 : '',
            },
            {
                boxType: 'SolutionBox1',
                campaign: campaign2 != null ? campaign2 : '',
            },
            {
                boxType: 'SolutionBox2',
                campaign: campaign3 != null ? campaign3 : '',
            },
        ],
    };

    window.digitalData.page.components.push(componentInfo);
}

function populateDigitalDataWithSingleCampaign(componentName, componentTitle, campaign1) {
    const componentInfo = {
        name: componentName,
        title: componentTitle,
        campaign: campaign1 != null ? campaign1 : '',
    };

    window.digitalData.page.components.push(componentInfo);
}

export {
    // eslint-disable-next-line import/prefer-default-export
    populateDigitalData,
    populateDigitalDataWithCampaign,
    populateDigitalDataWithSingleCampaign,
};
