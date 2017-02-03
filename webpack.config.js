const path = require('path');
const glob = require('glob');
const webpack = require('webpack');

const ROOT = './src/main/webapp/js';
const BUILD = 'build/exploded-app/js';

const entry = {};
const files = glob.sync(`${ROOT}/**/*.js`, {
    ignore: `${ROOT}/lib/**`    // ignore /lib/
});
files.forEach((file) => {
    const { dir, name } = path.parse(file);
    const filePath = path.join(dir.replace(ROOT, ''), name);
    entry[filePath] = file;
});

module.exports = {
    entry,
    output: {
        filename: '[name].js',
        path: path.resolve(__dirname, BUILD)
    },
    plugins: [
        new webpack.optimize.UglifyJsPlugin()
    ],
    stats: 'errors-only'
};