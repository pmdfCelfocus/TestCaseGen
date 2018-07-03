const POST_URL = "http://172.18.191.105:9999/";
const HTML_DIR = __dirname + "/index.html";
const TEMP_HTML = __dirname + "/temp.html";


const express = require("express");
const path = require("path");
const busboy = require('connect-busboy');
const fs = require('fs');
const request = require('request');
const app = express();
const jsdom = require("jsdom");
const { JSDOM } = jsdom;
const dom = new JSDOM(getHTML());
console.log(dom.serialize());
const document = dom.window.document;

app.use(express.static(path.join(__dirname, '/public')));
app.use(busboy());

const http = require("http").Server(app).listen(80);
console.log("Server started!");

let json;

app.get("/", function (req, res) {
    //res.sendFile(HTML_DIR);
    res.set('Content-Type', 'text/html');
    res.send(getHTML());
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
            getJson(dir);
            //postFunc(dir,filename);
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
        json = JSON.parse(body)
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

//------------------------------DIAGRAMS---------------------------------------------


function getJson() {
    json = JSON.parse(fs.readFileSync(__dirname + "/test.json", 'utf8'));
    generateDiv();
}

function getHTML(){
    return new Buffer (fs.readFileSync(HTML_DIR,null));
}

function processNodes() {
    let value;
    let obj;
    Object.keys(json).forEach(function (key) {
        console.log('Key -> ' + key);
        value = json[key];
        Object.keys(value).forEach(function (index) {
            console.log('Index -> ' + index);
            obj = value[index];
            Object.keys(obj).forEach(function (attribute) {
                console.log('Attribute-> ' + attribute);
                console.log(obj[attribute]);
            });
        });
    });
}

function generateDiv() {
    var para = document.createElement("p");
    
    var node = document.createTextNode('YES');

    var element = document.getElementById("instructions");

    para.appendChild(node);
    element.appendChild(para);
    updateHTML();
}

function updateHTML(){
    return fs.writeFileSync(TEMP_HTML,dom.serialize(),null);
}





