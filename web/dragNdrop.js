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
const ejs = require('ejs');
//const expressLayouts = require('express-ejs-layouts');
const { JSDOM } = jsdom;
const dom = new JSDOM(getHTML(HTML_DIR));
const window = dom.window;
const document = window.document;

app.set('view engine', 'ejs');
//app.use(expressLayouts);

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Cache-Control");
    next();
  });

app.use(express.static(path.join(__dirname, '/public')));
app.use(busboy());
console.log("Server started!");

let json;

/**app.get('/', function (req, res) {
    //uploadHTML();
    //res.set('Content-type', 'text/html');
    //res.send(getHTML(TEMP_HTML));
    res.render('pages/index');
});
**/
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
            getJson();
            //postFunc(dir,filename);
            res.redirect('back');
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
}

function getHTML(file){
    let buffer = new Buffer (fs.readFileSync(file,null));
    return buffer;
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

function generateDiv(res) {
    var para = document.createElement("p");
    
    var node = document.createTextNode('YES');

    var element = document.getElementById("instructions");

    para.appendChild(node);
    var result = element.appendChild(para);
   // uploadHTML();
   res.render('pages/index', result);
   res.redirect('back');
}

function uploadHTML(){
    fs.writeFileSync(TEMP_HTML,dom.serialize(),null);
}

function test(filename,data,options, res){
    ejs.renderFile(filename, data, options, function(err, str){
        if(err)
            console.log(err);

        res.render(filename);
    });
}






