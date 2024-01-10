module.exports = async page => {
    console.log('COOKIE DISCLAIMER > Start');
    const frame = await waitForFrame(page);

    const acceptCookiesButton = await frame.waitForSelector('.call');
    acceptCookiesButton.click();

    const closeCookieOverlayButton = await frame.waitForSelector('.close');
    closeCookieOverlayButton.click();

    // wait for cookie disclaimer overlay to disappear
    await page.waitForSelector('.truste_box_overlay_inner', { hidden: true });
    await page.waitFor(500);

    console.log('COOKIE DISCLAIMER > Done');

    function waitForFrame(page) {
        let fulfill;
        const promise = new Promise(x => (fulfill = x));
        checkFrame();
        return promise;

        function checkFrame() {
            const frame = page.frames().find(f => f.name().includes('pop-frame'));
            if (frame) {
                fulfill(frame);
            } else {
                page.once('frameattached', checkFrame);
            }
        }
    }
};
