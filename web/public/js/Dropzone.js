const myDropzone = new Dropzone("div#myId", { url: "/file/post"});

// "myAwesomeDropzone" is the camelized version of the HTML element's ID
Dropzone.options.myDropzone = {
    paramName: "file", // The name that will be used to transfer the file
    maxFilesize: 50, // MB
    accept: function(file, done) {
      done();
    }
  };

  // Prevent Dropzone from auto discovering this element:
Dropzone.options.myAwesomeDropzone = false;
// This is useful when you want to create the
// Dropzone programmatically later

// Disable auto discover for all elements:
Dropzone.autoDiscover = false;

myDropzone.on("complete", function(file) {
    myDropzone.removeFile(file);
  });

  Dropzone.confirm = function(question, accepted, rejected) {
    // Ask the question, and call accepted() or rejected() accordingly.
    // CAREFUL: rejected might not be defined. Do nothing in that case.
  };

// The recommended way from within the init configuration:
Dropzone.options.drop = {
    init: function() {
      this.on("addedfile", function(file) { alert("Added file."); });
    }
  };

  myDropzone.on("sending", function(file, xhr, formData) {
    // Will send the filesize along with the file as POST data.
    formData.append("filesize", file.size);
  });

  myDropzone.on("addedfile", function(file) {
    file.previewElement.addEventListener("click", function() {
      myDropzone.removeFile(file);
    });
  });

  new Dropzone(document.body, {
    previewsContainer: ".dropzone-previews",
    // You probably don't want the whole body
    // to be clickable to select files
    clickable: false
  });
