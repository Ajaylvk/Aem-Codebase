const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const StylelintPlugin = require('stylelint-webpack-plugin');

module.exports = merge(common, {
    mode: 'development',
    devtool: 'inline-source-map',
    performance: {hints: false},
    plugins: [new StylelintPlugin()],
});
