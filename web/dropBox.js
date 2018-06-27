require('isomorphic-fetch');
const Dropbox = require('dropbox').Dropbox;
const request = require('request');
const dbx = new Dropbox({ accessToken: 'dHyZQCwlQWAAAAAAAAAACP-tYc6nlydtUXys5bWcXt7C4ZofI_-rcM6l0gixJMlj' });


module.exports.createFolder = function (name) {
    dbx.filesCreateFolderV2({
        path: name,
        autorename: false
    }).then(function (response) {
        console.log(response);
    })
        .catch(function (error) {
            console.error(error);
        });
}

module.exports.addFile = function(name, filename, file){
   dbx.filesUpload({
        path: name + "/" + filename,
        mode: "add",
        autorename: true,
        mute : false,
        contents: file
    }).then(function(response){
        console.log(response);
    })
    .catch(function(error){
        console.error(error);
    });

}