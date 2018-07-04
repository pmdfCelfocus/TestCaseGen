const JS_SERVER = 'http://localhost:8080/file-upload';

function createRequest() {
  var result = null;
  if (window.XMLHttpRequest) {
    // FireFox, Safari, etc.
    result = new XMLHttpRequest();
    if (typeof result.overrideMimeType != 'undefined') {
      result.overrideMimeType('text/xml'); // Or anything else
    }
  }
  else if (window.ActiveXObject) {
    // MSIE
    result = new ActiveXObject("Microsoft.XMLHTTP");
  }
  else {
    // No known mechanism -- consider aborting the application
  }
  return result;
}


function XMLPostRequest(file) {
  generateDiv(text, false);
  document.getElementById("input").value = '';
  var req = createRequest();
  req.open("POST", JS_SERVER, true);
  req.send(file);
  req.onreadystatechange = function () {
    if (req.readyState != 4) return;
    if (req.status != 200) {
      return;
    }
    //document.getElementById("response").innerHTML = resp;
    generateDiv(resp, true);
  }
}

/**document.getElementById('fileID').addEventListener('onchange', function(event){
  let files = document.getElementById('fileID').files;
  console.log(files);
});
**/

function generateDiv(resp, isBot) {
  var iDiv = document.createElement('div');
  var para = document.createElement("p");
  para.className = 'chat-message';
  var node = document.createTextNode(resp);
  var element = document.getElementById("logs");
  if (isBot == true) {
    iDiv.className = 'chat bot';
    iDiv.id = 'bot';
  } else {
    iDiv.className = 'chat self';
    iDiv.id = 'self';
    var order = document.createElement('div');
    order.className = 'blank';
    iDiv.appendChild(order);
  }

  para.appendChild(node);
  iDiv.appendChild(para);
  element.appendChild(iDiv);
}