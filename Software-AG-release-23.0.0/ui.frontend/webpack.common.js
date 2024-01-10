'use strict';

const path = require('path');
const webpack = require('webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const TSConfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const FileManagerPlugin = require('filemanager-webpack-plugin');
const MergeIntoSingleFilePlugin = require('webpack-merge-and-include-globally');
const Stylish = require('webpack-stylish');
const APP_ID = 'softwareag';
const SOURCE_ROOT = __dirname + '/src/main/webpack';
const DIST_ROOT = __dirname + '/dist';
const CLIENTLIB_ROOT = __dirname
    + './../ui.apps/src/main/content/jcr_root/apps/' + APP_ID + '/clientlibs';

const IS_PROD = (process.env.NODE_ENV === 'production');

module.exports = {
    resolve: {
        extensions: ['.js'],
        plugins: [new TSConfigPathsPlugin({
            configFile: "./tsconfig.json"
        })]
    },
    entry: {
        'site': [
            path.resolve(SOURCE_ROOT, 'site/core.js'),
            path.resolve(SOURCE_ROOT, 'site/components.js'),
        ],
        'authoring': path.resolve(SOURCE_ROOT, 'site/authoring.js')
    },
    output: {
        filename: 'clientlib-[name]/[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: [/node_modules\/(?!(swiper|dom7)\/).*/, /\.test\.jsx?$/],
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env']
                    }
                }, {
                    loader: 'eslint-loader',
                    query: {
                        // This option makes ESLint automatically fix minor issues
                        fix: !IS_PROD,
                        formatter: function (results) {
                            if (!IS_PROD) {
                                const output = require(
                                    'eslint/lib/cli-engine/formatters/stylish')(results);
                                // WORKAROUND because webpack-command's formatter doesn't format ESLint & Stylelint
                                // errors well. Similar to https://github.com/webpack-contrib/webpack-stylish/issues/22
                                console.log(output);
                            }

                            return '';
                        },
                    },
                }],
            },
            {
                test: /\.(woff|woff2)$/,
                use: {
                    loader: 'url-loader',
                },
            },
            {
                test: /\.scss$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    {
                        loader: "css-loader",
                        options: {
                            url: false
                        }
                    },
                    {
                        loader: 'postcss-loader',
                        options: {
                            plugins() {
                                const plugins = [];
                                /*if (!IS_PROD) {
                                    plugins.push(require('stylelint')({
                                        configFile: path.resolve(__dirname,
                                            './stylelint.config.js'),
                                        fix: true,
                                    }));

                                    plugins.push(require('postcss-reporter'));
                                }*/

                                // Load Autoprefixer AFTER Stylelint to avoid failing on Stylelint's prefix rules
                                plugins.push(require('autoprefixer'));

                                return plugins;
                            }
                        }
                    },
                    {
                        loader: "sass-loader",
                        options: {
                            url: false
                        }
                    },
                    {
                        loader: "webpack-import-glob-loader",
                        options: {
                            url: false
                        }
                    }
                ]
            }
        ]
    },
    plugins: [
        new webpack.NamedModulesPlugin(),
        new Stylish(),
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery'
        }),
        new webpack.NoEmitOnErrorsPlugin(),
        new MiniCssExtractPlugin({
            filename: 'clientlib-[name]/[name].css'
        }),
        new MergeIntoSingleFilePlugin({
            files: {
                "clientlib-authoring/authoring.merged.js": [
                    path.resolve(SOURCE_ROOT, 'authoring/**/*.js'),
                ]
            }
        }),
        new FileManagerPlugin({
            onEnd: {
                copy: [
                    {
                        source: path.resolve(__dirname,
                            DIST_ROOT + '/clientlib-site/site.bundle.js'),
                        destination: path.resolve(__dirname,
                            CLIENTLIB_ROOT
                            + '/clientlib-site/js/site.bundle.js')
                    },
                    {
                        source: path.resolve(__dirname,
                            DIST_ROOT + '/clientlib-site/site.css'),
                        destination: path.resolve(__dirname,
                            CLIENTLIB_ROOT + '/clientlib-site/css/site.css')
                    },
                    {
                        source: path.resolve(__dirname,
                            DIST_ROOT
                            + '/clientlib-authoring/authoring.merged.js'),
                        destination: path.resolve(__dirname,
                            CLIENTLIB_ROOT
                            + '/clientlib-authoring/js/authoring.merged.js')
                    },
                    {
                        source: path.resolve(__dirname,
                            DIST_ROOT + '/clientlib-authoring/authoring.css'),
                        destination: path.resolve(__dirname,
                            CLIENTLIB_ROOT
                            + '/clientlib-authoring/css/authoring.css')
                    },

                ],
                delete: [
                    path.resolve(__dirname,
                        DIST_ROOT + '/clientlib-authoring/authoring.bundle.js'),
                ],

            }
        }),
    ],

    stats: {
        assetsSort: "chunks",
        builtAt: true,
        children: false,
        chunkGroups: true,
        chunkOrigins: true,
        colors: true,
        errors: true,
        errorDetails: true,
        env: true,
        modules: false,
        performance: false,
        providedExports: false,
        source: false,
        warnings: true
    },
};
