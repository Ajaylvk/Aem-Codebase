module.exports = async (page, scenario, vp) => {
    await require("./loadCookies")(page, scenario);

    // await page.evaluate(() => {
    //     console.log(document.getElementsByTagName('html')[0]);
    //     let cookieConsent = document.querySelector(".trustarc-cookie-consent");
    //     cookieConsent.innerHTML = "";
    // });
};
