let requirements;
let onlyScenarios = [];
let $;
let actDiagram;
let seqDiagram;
let nodesArrays = [];
let dic = {};
let formData = new FormData();
let nodeDataArray;

function getContent(content) {
  requirements = content;
  processNodes();
}

function addForm() {
  if (nodeDataArray != 'undefined' && nodesArrays !=0) {
    console.log(nodeDataArray.name);
    formData.append("diagram", JSON.stringify(nodeDataArray));
    draw();
  }
}

function processNodes() {
  let value;
  let obj;
  Object.keys(requirements).forEach(function (key) {
    //console.log('Key -> ' + key);
    value = requirements[key];
    Object.keys(value).forEach(function (index) {
      //console.log('Index -> ' + index);
      obj = value[index];
      Object.keys(obj).forEach(function (attribute) {
        // console.log('Attribute-> ' + attribute);
        if (attribute === 'desc') {
          if (obj[attribute]['hasScenario']) {
            let scenarios = obj[attribute]['scenarios'];
            onlyScenarios.push(scenarios);
          }
        }
      });
    });
  });
  createNodes();
}

function createNodes() {
  //TODO -> Add desc to Test OBJ
  Object.keys(onlyScenarios).forEach(function (index) {
    let scenarioArray = onlyScenarios[index];
    Object.keys(scenarioArray).forEach(function (title) {
      let nodeDataArray = [];
      let start = false;
      let steps = scenarioArray[title];
      let json;
      let parent = -1;
      let key = 0;
      nodeDataArray.push(buildJSON('INIT', parent, key));
      parent++;
      key++;
      for (var i = 0; i < steps.length; i++) {
        let current = steps[String(i)];
        let string = String(current);
        if (string.includes('When')) {
          start = true;
          string = string.replace('When', '');
          json = buildJSON(string, parent, key);
        }
        else if (string.includes('Then')) {
          if (!start)
            continue;
          string = string.replace('Then', '');
          json = buildJSON(string, parent, key);
        }
        else if ((string.includes('Given') || string === '')) {
          continue;
        }
        else {
          if (!start)
            continue;
          string = string.replace('And', '');
          json = buildJSON(string, parent, key);
        }
        key++;
        parent++;
        nodeDataArray.push(json);

      }
      nodeDataArray.push(buildJSON('END', parent++, key++));
      let temp = new Test(title, nodeDataArray);
      nodesArrays.push(temp);
    });

  });
  draw();
}

function buildJSON(string, parent, key) {
  let result = {
    key: key,
    name: string,
    parent: parent
  };

  return result;
}

function getGO(model) {
  $ = model;
  actDiagram = $(go.Diagram, "act");
  seqDiagram = $(go.Diagram, "seq");
}

function draw() {
  if(nodesArrays.length == 0){
    sendSelected();
    alert('No more diagrams!');
    actDiagram.clear();
  }else{
    nodeDataArray = nodesArrays.pop();
    console.log(nodeDataArray.steps);
    actDiagram.nodeTemplate =
      $(go.Node, "Vertical",
        { background: "#000" },
        $(go.TextBlock, "Default Text",
          { margin: 8, stroke: "white", font: "bold 16px sans-serif" },
          new go.Binding("text", "name"))
      );
  
    var model = $(go.TreeModel);
    model.nodeDataArray = nodeDataArray.steps;
    actDiagram.model = model;
    //actDiagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);
  }
}

function sendSelected() {
  var xhr = new XMLHttpRequest();
  xhr.open('POST', 'http://localhost:8080/generate', true);
  //xhr.send(formData);
  //xhr.setRequestHeader('Content-type', 'application/json');
  xhr.send(formData);
}

function Test(name, steps) {
  this.name = name;
  //this.desc = desc;
  this.steps = steps;
}

Test.prototype.getName = function () {
  return this.name;
}

//Test.prototype.getDesc = function() {
 // return this.desc;
//}

Test.prototype.getSteps = function () {
  return this.steps;
}
