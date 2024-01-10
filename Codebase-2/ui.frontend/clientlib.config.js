const APP_ID = 'softwareag';
const clientlibsPath = './../ui.apps/src/main/content/jcr_root/apps/' + APP_ID + '/clientlibs';

module.exports = {
    // default working directory (can be changed per 'cwd' in every asset option)
    context: __dirname,

    // path to the clientlib root folder (output)
    clientLibRoot: clientlibsPath,

    libs: [
        {
            name: "clientlib-dependencies",
            allowProxy: true,
            categories: ["softwareag.dependencies"],
            serializationFormat: "xml",
            cssProcessor : ["default:none", "min:none"],
            jsProcessor: ["default:none", "min:none"],
            assets: {
                js: [
                    "dist/clientlib-dependencies/*.js",
                ],
                css: [
                    "dist/clientlib-dependencies/*.css"
                ]
            }
        },
        {
            name: "clientlib-site",
            allowProxy: true,
            categories: ["softwareag.site"],
            dependencies: ["softwareag.dependencies"],
            serializationFormat: "xml",
            cssProcessor : ["default:none", "min:none"],
            jsProcessor: ["default:none", "min:none"],
            assets: {
                js: [
                    "dist/clientlib-site/*.js",
                ],
                css: [
                    "dist/clientlib-site/*.css"
                ],
                resources: [
                    {src: "dist/clientlib-site/resources/images/*.*", dest: "images/"},
                    {src: "dist/clientlib-site/resources/fonts/*.*", dest: "fonts/"},
                ]
            }
        }
    ]
};
