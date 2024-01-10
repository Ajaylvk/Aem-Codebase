module.exports = async (page) => {
    console.log("AEM LOGIN > Start");
    await page.focus("#username");
    await page.keyboard.type("admin");
    await page.focus("#password");
    await page.keyboard.type("admin");
    await page.click("#submit-button");
    console.log("AEM LOGIN > Done");
}
