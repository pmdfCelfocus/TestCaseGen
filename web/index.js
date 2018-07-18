//URL to post the form data with diagrams data
const POST_URL = 'http://localhost:8080/generate';
//Download file name
const FILENAME = 'TestCase.xlsx';

//Used to save the Dropzone success response
let requirements = {};
//Used to save all the requirements with scenarios
let onlyScenarios = [];
//GOJS variable
let $;
//GOJS diagram name
let actDiagram;
//Used to save all the diagrams that will be shown in canvas
let nodesArrays = [];
// Used to save the selected diagrams for send into the Rest Server
let formData = new FormData();
// Represents one object from nodesArrays
let nodeDataArray;
// On load errors handlers
// Handle the multiple buttons insertion
let first = false;
// Handle the onload formdata post
let postHandler = true;

/**
 * When a file is dropped, the Dropzone makes a http request.
 * This function is used to save this http response
 * @param {JSON} content 
 */
function getContent(content) {
  requirements = JSON.parse(content);
  console.log(requirements);
  processNodes();
}

/**
 * Get the GOJS model and configure the diagram canvas and nodes
 * @param {GOJS object} model 
 */
function getGO(model) {
  $ = model;
  actDiagram = $(go.Diagram, "act", {
    contentAlignment: go.Spot.Center,
    layout: $(go.TreeLayout,
      { angle: 90, layerSpacing: 35 })
  });
  actDiagram.nodeTemplate =
      $(go.Node, "Horizontal",
        { background: "#000" },
        $(go.TextBlock, "Default Text",
          { margin: 8, stroke: "white", font: "bold 16px sans-serif" },
          new go.Binding("text", "name"))
      );
}

/**
 * Process the requirements and extract only those that has scenarios
 */
function processNodes() {
  let value;
  let obj;
  Object.keys(requirements).forEach(function (key) {
    //Search all requirements
    value = requirements[key];
    Object.keys(value).forEach(function (index) {
      //Get the current requirement object
      obj = value[index];
      Object.keys(obj).forEach(function (attribute) {
        /*Search for scenarios in the current requiremeny object. If it has it, this data will be 
          transformed into a Scenario object
        */
        if (attribute === 'desc') {
          if (obj[attribute]['hasScenario']) {
            let scenarios = obj[attribute]['scenarios'];
            let base = String(obj[attribute]['base']);
            let result = base.split(":");
            base = result[1];
            result = base.split('\n');
            base = '';
            for (let i = 0; i < result.length; i++) {
              if (i === 0) {
                base += result[i] + ": "
              } else {
                base += result[i].toLocaleLowerCase() + " "
              }
            }
            let scenario = new Scenario(base, scenarios);
            onlyScenarios.push(scenario);
          }
        }
      });
    });
  });
  createNodes();
}

/**
 * Creates all diagram nodes, to be displayed in GOJS canvas
 */
function createNodes() {
  //Search all requirements with scenarios
  Object.keys(onlyScenarios).forEach(function (index) {
    let scenarioArray = onlyScenarios[index].scenarios;
    //Search all scenarios from the current requirement
    Object.keys(scenarioArray).forEach(function (title) {
      let desc = onlyScenarios[index].base;
      let nodeDataArray = [];
      let steps = scenarioArray[title];
      let parent = -1;
      let key = 0;
      //Creates the init diagram node
      nodeDataArray.push(buildJSON('INIT', parent, key));
      parent++;
      key++;
      //Create step nodes
      for (var i = 0; i < steps.length; i++) {
        let json;
        let current = steps[String(i)];
        let string = String(current);
        if (string === '')
          continue;
        //Remove the string first word
        if (string.includes('When')) {
          string = string.replace('When', '');
        }
        else if (string.includes('Then')) {
          string = string.replace('Then', '');
        }
        else if ((string.includes('Given'))) {
          string = string.replace('Given', '');
        }
        else {
          string = string.replace('And', '');
        }
        json = buildJSON(string, parent, key);
        key++;
        parent++;
        nodeDataArray.push(json);
      }
      //Add the end diagram node
      nodeDataArray.push(buildJSON('END', parent, key));
      //Creates an object and inserted into an array
      let temp = new Test(title, desc, nodeDataArray);
      nodesArrays.push(temp);
      if(postHandler){
        postHandler = false;
      }
    });

  });
  draw();
}

/**
 * Build the GOJS's node object
 * @param {String} string 
 * @param {Number} parent 
 * @param {Number} key 
 */
function buildJSON(string, parent, key) {
  let result = {
    key: key,
    name: string,
    parent: parent
  };

  return result;
}

/**
 * Draw a diagram in canvas. A diagram is consumed from the diagrams array and it is displayed in canvas.
 * When the array is empty, the selected diagrams are sent to the Rest Server to be used
 */
function draw() {
  if (nodesArrays.length == 0) {
    sendSelected();
  } else {
    nodeDataArray = nodesArrays.pop();
    var model = $(go.TreeModel);
    model.nodeDataArray = nodeDataArray.steps;
    actDiagram.model = model;
    showName('diagName',nodeDataArray.name);
  }
}

/**
 * Add a diagram to the Form Data
 */
function addForm() {
  if (nodeDataArray != 'undefined' && nodesArrays != 0) {
    formData.append("diagram", JSON.stringify(nodeDataArray));
  }
}

/**
 * Button on click operation that add a diagram to the Form Data and draw another diagram
 */
function next() {
  addForm();
  draw();
}

/**
 * Shows the current diagram name
 * @param {String} id 
 * @param {String} name 
 */
function showName(id, name) {
  //Insert multiple buttons handler
  if(!first){
    first = true;
    insertButtons();
  }
  let el = document.getElementById(id);
  let parent = el.parentNode;
  parent.removeChild(el);
  el = document.createElement(id);
  el.id = id;
  parent.appendChild(el);
  el.innerHTML = name;
}

/**
 * Send the Form Data to the Rest Server to being used in the Excel file creation
 */
function sendSelected() {
  if(!postHandler){
    addForm();
    reset();
    //Creates a XMLHttpRequest
    var xhr = new XMLHttpRequest();
    xhr.open('POST', POST_URL, true);
    xhr.onreadystatechange = function () {
      if (xhr.readyState != 4) {
        //When the response is received, we check if it is not empty, and if not, we create the download button
        let response = xhr.responseText;
        console.log(response);
        if (response != '')
          createDownload(response);
        return;
      }
      if (xhr.status != 200) {
        return;
      }
    }
    xhr.send(formData);
  }
}

/**
 * Clean canvas and removes the diagram name DOM element
 */
function reset() {
  actDiagram.clear();
  clearDiagName();
}

/**
 * Removes diagram name DOM element from the HTML
 */
function clearDiagName() {
  let diagName = document.getElementById('diagName');
  diagName.parentNode.removeChild(diagName);
}

/**
 * Creates a download button in HTML with the url inserted
 * @param {Sting} url 
 */
function createDownload(url) {
  let div = document.getElementById('download');
  let newbutton = document.createElement('button');
  newbutton.id = 'btn';
  let newEl = document.createElement('a');
  newEl.id = 'dl';
  let text = document.createTextNode('Download Here')
  newEl.setAttribute('href', url);
  newEl.setAttribute('download', FILENAME);

  newEl.appendChild(text);
  newbutton.appendChild(newEl);
  div.appendChild(newbutton);

}

/**
 * Insert the buttons to select the diagrams.
 * These are inserted when the first diagram is drawn
 */
function insertButtons() {
  createButton('yes', 'Add Diagram', 'next()');
  createButton('no', 'Reject Diagram', 'draw()');
  showName('or','OR');
  createButton('ready', 'Send Now', 'sendSelected()');
}

/**
 * Creates a DOM button element with an custom id, node text and an onclick function
 * @param {String} id 
 * @param {String} text 
 * @param {String} funcName 
 */
function createButton(id, text, funcName) {
let buttonsDiv = document.getElementById('buttons');
let btn = document.createElement('button');
btn.id = id;
btn.setAttribute('onclick', funcName);
let node = document.createTextNode(text);

btn.appendChild(node);
buttonsDiv.appendChild(btn);

}

//--------------------------Classes---------------------------

/**
 * Class that is used to convert the received data into an JS Object
 * @param {String} name 
 * @param {String} desc 
 * @param {String[]} steps 
 */
function Test(name, desc, steps) {
  this.name = name;
  this.desc = desc;
  this.steps = steps;
}

Test.prototype.getName = function () {
  return this.name;
}

Test.prototype.getDesc = function () {
  return this.desc;
}

Test.prototype.getSteps = function () {
  return this.steps;
}

//----------------------------------------

/**
 * Class that is used to convert the scenarios into an JS Object
 * @param {String} base 
 * @param {String[]} scenarios 
 */
function Scenario(base, scenarios) {
  this.base = base;
  this.scenarios = scenarios;
}

Scenario.prototype.getBase = function () {
  return this.base;
}

Scenario.prototype.getScenarios = function () {
  return this.scenarios;
}