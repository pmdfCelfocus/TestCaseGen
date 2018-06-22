require('isomorphic-fetch');
const Dropbox = require('dropbox').Dropbox;
const dbx = new Dropbox({ accessToken: 'INekBYaEnNAAAAAAAAAAVEt_w8ATFNfIEzMzfDZ8Hd8ugGJnL6biXVewAmirA5fU' });


module.exports.getAcc = function() {
    dbx.usersGetCurrentAccount()
        .then(function (response) {
            console.log(response);
        })
        .catch(function (error) {
            console.error(error);
        });
    }

module.exports.createFolder = function(name){
    dbx.filesCreateFolderV2({
        path: name,
        autorename: false        
    }).then(function(response){
        console.log(response);
    })
    .catch(function(error){
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