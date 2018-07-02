const POST_URL = "http://172.18.191.105:9999/";
const express = require("express");
const path = require("path");
const busboy = require('connect-busboy');
const fs = require('fs');
const request = require('request');
const app = express();

app.use(express.static(path.join(__dirname, '/public')));
app.use(busboy());

const http = require("http").Server(app).listen(80);
console.log("Server started!");

let collections = [];

app.get("/", function (req, res) {
    res.sendFile(__dirname + "/index.html");
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
            postFunc(dir,filename);
        });
    }
    );
});

//-------------------------------------------------------

function postFunc(dir, filename) {
    let extension = findExtension(filename);

    let formData = {
        data: fs.createReadStream(dir)
    };

    request.post({ url: POST_URL + extension + "/upload", formData: formData }, function optionalCallback(err, httpResponse, body) {
        if (err) {
            return console.error('upload failed:', err);
        }
        console.log('Upload successful! -> ' + body);
        return '<p>' + body + '<\p>';
        collections[extension] = JSON.parse(body);
    });

}

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

//-----------------------------------------------------------------------------

