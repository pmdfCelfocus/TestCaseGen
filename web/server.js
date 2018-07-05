const POST_URL = "http://172.18.191.105:9999/";
const express = require("express");
const path = require("path");
const busboy = require('connect-busboy');
const fs = require('fs');
const request = require('request');
const app = express();

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Cache-Control");
    next();
  });

app.use(express.static(path.join(__dirname, '/public')));
app.use(busboy());
console.log("Server started!");

let json;

app.get('/', function(req, res){
    res.sendFile(__dirname + '/server.html');
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
            res.set('Content-type', 'application/json');
            res.send(getJson());
            //res.send(postFunc(dir,filename));
            res.status(200).end('SUCESS');
        });
    }
    );
});

app.listen(8080,'localhost');

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
        json = JSON.parse(body);
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

//------------------------------FOR TESTS---------------------------------------------


function getJson() {
    json = JSON.parse(fs.readFileSync(__dirname + "/test.json", 'utf8'));
    return json;
}






