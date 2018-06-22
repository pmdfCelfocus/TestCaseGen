var PdfReader = require("pdfreader").PdfReader;

var rows = {}; // indexed by y-position

function printRows() {
  Object.keys(rows) // => array of y-positions (type: float)
    .sort((y1, y2) => parseFloat(y1) - parseFloat(y2)) // sort float positions
    .forEach((y) => console.log((rows[y] || []).join('')));
}

module.exports =
  function (dir) {
    new PdfReader().parseFileItems(dir, function (err, item) {
      if (item && item.text)
        console.log(item.text);
    });
  }