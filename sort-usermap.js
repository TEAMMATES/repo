const fs = require('fs');
const _ = require('lodash');
const userMapData = require('./src/web/data/userMapData.json');

const userMapPath = './src/web/data/userMapData.json';

// sort institute names in each country case insensitively, and remove duplicates
const countries = Object.keys(userMapData.institutes);
for (let i = 0; i < countries.length; i += 1) {
    const country = countries[i];
    userMapData.institutes[country].sort((a, b) => a.toLowerCase().localeCompare(b.toLowerCase()));
    userMapData.institutes[country] = _.sortedUniq(userMapData.institutes[country]);
}

// write back to the file
const sortedUserMapData = `${JSON.stringify(userMapData, null, 2)}\n`;
fs.writeFile(userMapPath, sortedUserMapData, (err) => {
    if (err) throw err;
});
