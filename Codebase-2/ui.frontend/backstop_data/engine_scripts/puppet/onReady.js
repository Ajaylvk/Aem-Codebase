module.exports = async (page, scenario, vp) => {
    console.log("SCENARIO > " + scenario.label);
    await require("./clickAndHoverHelper")(page, scenario);
    await require("./loginAEM")(page);
    await page.waitFor(3000);
};
