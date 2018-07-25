//Rest Server base URL
const REST_URL = "http://172.18.191.105:9999/";
//All the npm used packages
const express = require("express");
const busboy = require('connect-busboy');
const fs = require('fs');
const request = require('request');
const app = express();
const multer = require('multer');
const upload = multer();
//Server local folder
const BASE_FOLDER = '/files/';
//Diagram JSON name
const SEND_JSON = 'toSend.json';

//For testing
let json;

//Server's headers configuration -------------------------------
app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Cache-Control");
    next();
});

app.use(busboy());
console.log("Server started!");
//--------------------------------------------------------------

//HTTP GET method configuration
app.get('/', function (req, res) {
    //When the server receives a GET request, it responds with a simple HTML file
    res.sendFile(__dirname + '/server.html');
});

/*Excel generation request : HTTP POST method configuration, multer package is used as upload.array()
  to parse the request body
*/
app.post('/generate', upload.array(), function (req, res) {
    //Transforms the request body into a JSON
    req.body = JSON.stringify(req.body);
    let json = req.body;
    //Path creation
    let dir = __dirname + BASE_FOLDER + SEND_JSON;
    //Write received file into the defined path
    fs.writeFileSync(dir, json, function (err) {
        if (err)
            console.error(err);
    });
    generatePost(dir, res);
});

//File Upload request : HTTP POST method configuration
app.post('/file-upload', function (req, res) {
    let fstream;
    //Busboy used to parse a received file
    
    req.pipe(req.busboy);
    req.busboy.on('file', function (fieldname, file, filename) {
        console.log("Uploading: " + filename);
        let dir = __dirname + BASE_FOLDER + filename;
        //Write stream creation and the file is written into the defined dir
        fstream = fs.createWriteStream(dir);
        file.pipe(fstream);
        //When the write is finished
        fstream.on('close', function () {
            uploadPost(dir,filename,res);
        });
    }
    );
});

//URL server configuration -> http://localhost:8080
app.listen(8080, 'localhost');

//-------------------------------------------------------

/**
 * Send a file to the Rest Server as a Form Data and transforms the response to JSON format
 * @param {String} dir 
 * @param {String} filename 
 * @param {Express Response} res 
 */
function uploadPost(dir, filename, res) {
    //Get the file extension
    let extension = findExtension(filename);

    //Form data creation
    let formData = {
        data: fs.createReadStream(dir)
    };

    //Post request to the Rest Server with the defined form data
    request.post({ url: REST_URL + extension + "/upload", formData: formData }, function optionalCallback(err, httpResponse, body) {
        if (err) {
            return console.error('upload failed:', err);
        }
        //console.log('Upload successful! -> ' + body);
        //Send a JSON as response
        let response = String (JSON.parse(JSON.stringify(body)));
        //console.log(response);
        res.send(response);
    }
);

}

/**
 * Send a diagram file to the Rest Server and then, receives as response, a generated Excel file that is written to
 * the local server folder. The file path is sent as response 
 * @param {String} dir 
 * @param {Express Response} res 
 */
function generatePost(dir, res) {
    //Form data creation
    let formData = {
        data: fs.createReadStream(dir)
    }
    //Post request to the Rest Server
    request.post(
        {
            url: REST_URL + 'generate',
            formData: formData
        }).on('response', function (response) {
            /* On http response header, the file name is found and the file is written directly into a path. Then,
               this path is sent as Express response 
            */
            let filename;
            filename = new String(findFileName(response.headers['content-disposition']));
            //Inserting the path to a variable creates a bug on Express's send 
            response.pipe(fs.createWriteStream(__dirname + BASE_FOLDER + filename));
            res.send(__dirname + BASE_FOLDER + filename);
        });
}

/**
 * Search the content disposition http header from a HTTP response to found the attached file name
 * @param {String} header 
 */
function findFileName(header) {
    let string = String(header);
    string.replace("'", '');
    let firstSplit = header.split(';');
    string = firstSplit[1];
    let secondSplit = string.split('=');
    string = secondSplit[1];
    return string.substring(1, string.length - 1);
}

/**
 * Find a file extension (.pdf, .xlsl)
 * @param {String} filename 
 */
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






