const express = require("express");
const fileUpload = require("express-fileupload");
const path = require("path");
const bodyParser = require('body-parser');
const app = express();
app.use(express.static(path.join(__dirname, '/public')));
const http = require("http").Server(app).listen(80);
console.log("Server started!");

//app.use(bodyParser.raw(({ type: 'application/octet-stream' })));

//app.use(bodyParser.urlencoded({
//    extended: true
//  }));

app.use(bodyParser.json());
app.get("/", function(req, res){
    res.sendFile(__dirname + "/TestCaseGen.html");
});

app.post('/file-upload', function(req, res) {
    console.log(req.body);
    res.status(200).send("GOOD JOB!");
  });