# Frontend Build

## Features

* Full ES6 and ES5 support (with applicable Webpack wrappers).
* JavaScript linting (using a TSLint ruleset – to be refined).
* ES5 output, for legacy browser support.
* Globbing
    * No need to add imports anywhere.
    * All JS and CSS files can now be added to each component (best practice is under /clientlib/js or /clientlib/(s)css)
    * No .content.xml or js.txt/css.txt files needed as everything is run through Webpack
    * The globber pulls in all JS files under the /component/ folder. Webpack allows CSS/SCSS files to be chained in via JS files. They are pulled in through the two entry points, sites.js and vendors.js.
    * The only files consumed by AEM are the output files site.js and site.css, the resources folder in /clientlib-site as well as dependencies.js and dependencies.css in /clientlib-dependencies
* Chunks
    * Main (site js/css)
    * Vendors (dependencies js/css)
* Full Sass/Scss support (Sass is compiled to CSS via Webpack).
* Static webpack development server with built in proxy to a local instance of AEM

## Installation

1. Install [NodeJS](https://nodejs.org/en/download/) (v10+), globally. This will also install `npm`.
2. Navigate to `ui.frontend` in your project and run `npm install`. (You must have run the archetype with `-DoptionIncludeFrontendModule=y` to populate the ui.frontend folder)

## Usage

The following npm scripts drive the frontend workflow:

* `npm run build:dev` - Full build of client libraries with JS optimization disabled (tree shaking, etc) and source maps enabled and CSS optimization disabled.
* `npm run build:prod` - Full build of client libraries build with JS optimization enabled (tree shaking, etc), source maps disabled and CSS optimization enabled.
* `npm run start` - Starts a static webpack development server for local development with minimal dependencies on AEM.

## Visual regression tests

The project uses backstopjs to run visual regression tests. The tests can be run by entering `npm run visual-test` in the ui.frontend folder. Make sure you have docker running in the background.

Screenshots of [components in the demo content](demo/content/softwareag/test/components) will be compared to the [reference screenshots](ui.frontend/backstop_data/bitmaps_reference). New components can be added in [backstop.json](ui.frontend/backstop.json) by copying one of the existing scenarios und adjusting the label and the url.

If the visual changes are intended the screenshots can be approved by entering `npm run visual-approve`. The screenshots can then be added to the commit and the code-reviewer has the additional advantage to visually see how the code-changes will impact the appearance of the component.

### General

The ui.frontend module compiles the code under the `ui.frontend/src` folder and outputs the compiled CSS and JS, and any resources beneath a folder named `ui.frontend/dist`.
The FE build chain generates JS/CSS files for the actual site as well as JS/CSS files with AEM Authoring-specific code. 

* **Site** - `core.js` & `components.js` are created in a `dist/clientlib-site` (site.bundle.js, site.css) folder.
* **Authoring** - `authoring.js` & `authoring.css` are created in a `dist/clientlib-site` (authoring.css, authoring.merged.js) folder.

### JavaScript

* **Optimization** - for production builds, all JS that is not being used or
called is removed.

### CSS

* **Autoprefixing** - all CSS is run through a prefixer and any properties that require prefixing will automatically have those added in the CSS.
* **Optimization** - at post, all CSS is run through an optimizer (cssnano) which normalizes it according to the following default rules:
    * Reduces CSS calc expression wherever possible, ensuring both browser compatibility and compression.
    * Converts between equivalent length, time and angle values. Note that by default, length values are not converted.
    * Removes comments in and around rules, selectors & declarations.
    * Removes duplicated rules, at-rules and declarations. Note that this only works for exact duplicates.
    * Removes empty rules, media queries and rules with empty selectors, as they do not affect the output.
    * Merges adjacent rules by selectors and overlapping property/value pairs.
    * Ensures that only a single `@charset` is present in the CSS file and moves it to the top of the document.
    * Replaces the CSS initial keyword with the actual value, when the resulting output is smaller.
    * Compresses inline SVG definitions with SVGO.
* **Cleaning** - explicit clean task for wiping out the generated CSS, JS and Map files on demand.
* **Source Mapping** - development build only.

#### Notes

* Utilizes dev-only and prod-only webpack config files that share a common config file. This way the development and production settings can be tweaked independently.

###  (NOT USED CURRENTLY) Client Library Generation

The second part of the ui.frontend module build process leverages the [aem-clientlib-generator](https://www.npmjs.com/package/aem-clientlib-generator) plugin to move the compiled CSS, JS and any resources into the `ui.apps` module. The aem-clientlib-generator configuration is defined in `clientlib.config.js`. The following client libraries are generated:


* **clientlib-site** - `ui.apps/src/main/content/jcr_root/apps/<app>/clientlibs/clientlib-site`
* **clientlib-dependencies** - `ui.apps/src/main/content/jcr_root/apps/<app>/clientlibs/clientlib-dependencies`

###  Page Inclusion

`clientlib-site` and `clientlib-dependencies` categories are included on pages via the Page Policy configuration as part of the default template. To view the policy, edit the **Content Page Template**  > **Page Information** > **Page Policy**.

The final inclusion of client libraries on the sites page is as follows:

```html

<HTML>
    <head>
        <link rel="stylesheet" href="clientlib-base.css" type="text/css">
        <script type="text/javascript" src="clientlib-dependencies.js"></script>
        <link rel="stylesheet" href="clientlib-dependencies.css" type="text/css">
        <link rel="stylesheet" href="clientlib-site.css" type="text/css">
    </head>
    <body>
        ....
        <script type="text/javascript" src="clientlib-site.js"></script>
        <script type="text/javascript" src="clientlib-base.js"></script>
    </body>
</HTML>
```

The above inclusion can of course be modified by updating the Page Policy and/or modifying the categories and embed properties of respective client libraries.

#### Important files

* `ui.frontend/webpack.dev.js` - This contains the configuration for the webpack-dev-serve and points to the html template to use. It also contains a proxy configuration to an AEM instance running on `localhost:4502`.

#### Using

1. From within the root of the project run the command `mvn -PautoInstallSinglePackage clean install` to install the entire project to an AEM instance running at `localhost:4502`
2. Navigate inside the `ui.frontend` folder.
3. Run the following command `npm run start` to start the webpack dev server. Once started it should open a browser (localhost:8080 or the next available port).
4. You can now modify CSS, JS, SCSS files and see the changes immediately reflected in the webpack dev server.
