const POST_URL = "http://172.18.191.105:9999/";

const express = require("express");
const path = require("path");
const busboy = require('connect-busboy');
const fs = require('fs');
const FileAPI = require('file-api');
const request = require('request');
const rp = require('request-promise');
const dbx = require('./dropBox');
const app = express();
const FileReader = FileAPI.FileReader;
const File = FileAPI.File;
const reader = new FileReader();


app.use(express.static(path.join(__dirname, '/public')));
app.use(busboy());

const http = require("http").Server(app).listen(80);
console.log("Server started!");
app.get("/", function (req, res) {
    res.sendFile(__dirname + "/TestCaseGen.html");
});

app.post('/file-upload', function (req, res) {
    let fstream;
    req.pipe(req.busboy);
    req.busboy.on('file', function (fieldname, file, filename) {
        console.log("Uploading: " + filename);
        let dir = __dirname + '/files/' + filename;
        fstream = fs.createWriteStream(dir);
        file.pipe(fstream);

        fstream.on('close', function () {
            res.redirect('back');
        });
       // requestFunc(filename, dir);
        dbx.createFolder('/Share');
       // test(dir);
    });

});

//-------------------------------------------------------

/**function postFunc(filename, extension, data) {

   var options = {
    method: 'POST',
    uri:  POST_URL + extension + "/",
    body: {
        buffer: new Buffer(data, 'binary')
    },
    json: true // Automatically stringifies the body to JSON
};
 
rp(options)
    .then(function (parsedBody) {
        console.log('Upload successful!  Server responded with:', parsedBody);
    })
    .catch(function (err) {
        return console.error('upload failed:', err);
    });

}
**/

function findExtension(filename) {
    let charArray = Array.from(filename);
    let length = charArray.length;
    let result = length - 1;
    for (let i = length - 1; i >= 0; i--) {
        if (charArray[i] == '.') {
            return filename.substring(result + 1, length);
        } else {
            result--;
        }
    }
}

function requestFunc(filename, myFile) {
    var fileByteArray = [];
    //reader.readAsArrayBuffer(__dirname + "/files/" + myFile);
    reader.readAsArrayBuffer(new File(myFile));
    reader.onload = function (evt) {
        if (evt.target.readyState == FileReader.DONE) {
            var arrayBuffer = evt.target.result,
                array = new Uint8Array(arrayBuffer);
            for (var i = 0; i < array.length; i++) {
                fileByteArray.push(array[i]);
            }
            let extension = findExtension(filename);
            //postFunc(filename, extension, fileByteArray);
        }
    }
}