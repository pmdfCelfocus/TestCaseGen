let requirements;
let onlyScenarios = [];
let $;
let actDiagram;
let nodesArrays = [];
let formData = new FormData();
let nodeDataArray;

function getContent(content) {
  requirements = content;
  processNodes();
}

function addForm() {
  if (nodeDataArray != 'undefined' && nodesArrays !=0) {
    formData.append("diagram", JSON.stringify(nodeDataArray));
    draw();
  }
}

function processNodes() {
  let value;
  let obj;
  Object.keys(requirements).forEach(function (key) {
    value = requirements[key];
    Object.keys(value).forEach(function (index) {
      obj = value[index];
      Object.keys(obj).forEach(function (attribute) {
        if (attribute === 'desc') {
          if (obj[attribute]['hasScenario']) {
            let scenarios = obj[attribute]['scenarios'];
            let base = String(obj[attribute]['base']);
            let result = base.split(":");
            base = result[1];
            result = base.split('\n');
            base = '';
            for(let i = 0; i < result.length; i++){
              if(i === 0){
                base += result[i] + ": "
              }else{
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

function createNodes() {
  Object.keys(onlyScenarios).forEach(function (index) {
    let scenarioArray = onlyScenarios[index].scenarios;
    Object.keys(scenarioArray).forEach(function (title) {
      console.log(title);
      let desc = onlyScenarios[index].base;
      console.log(desc);
      let nodeDataArray = [];
      let steps = scenarioArray[title];
      let parent = -1;
      let key = 0;
      nodeDataArray.push(buildJSON('INIT', parent, key));
      parent++;
      key++;
      for (var i = 0; i < steps.length; i++) {
        let json;
        let current = steps[String(i)];
        let string = String(current);
        if(string === '')
          continue;
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
      nodeDataArray.push(buildJSON('END', parent, key));
      let temp = new Test(title, desc , nodeDataArray);
      nodesArrays.push(temp);
    });

  });
  draw();
}

function buildJSON(string, parent, key) {
  if(string.includes('{')){
    console.log(string);
    console.log('Dafuq is going on!?!?');
  }
  let result = {
    key: key,
    name: string,
    parent: parent
  };

  return result;
}

function getGO(model) {
  $ = model;
  actDiagram = $(go.Diagram, "act", {
    contentAlignment: go.Spot.Center,
    layout: $(go.TreeLayout,
      { angle: 90, layerSpacing: 35 })
  });
}

function draw() {
  if(nodesArrays.length == 0){
    sendSelected();
    alert('No more diagrams!');
    actDiagram.clear();
  }else{
    nodeDataArray = nodesArrays.pop();
    actDiagram.nodeTemplate =
      $(go.Node, "Horizontal",
        { background: "#000" },
        $(go.TextBlock, "Default Text",
          { margin: 8, stroke: "white", font: "bold 16px sans-serif" },
          new go.Binding("text", "name"))
      );
  
    var model = $(go.TreeModel);
    model.nodeDataArray = nodeDataArray.steps;
    actDiagram.model = model;
    showName(nodeDataArray.name);
  }
}

function showName(name){
  let diagName = document.getElementById('diagName');
  diagName.innerHTML = name;
}

function sendSelected() {
  var xhr = new XMLHttpRequest();
  xhr.open('POST', 'http://localhost:8080/generate', true);
  xhr.send(formData);
}

function Test(name, desc,steps) {
  this.name = name;
  this.desc = desc;
  this.steps = steps;
}

Test.prototype.getName = function () {
  return this.name;
}

Test.prototype.getDesc = function() {
 return this.desc;
}

Test.prototype.getSteps = function () {
  return this.steps;
}

//----------------------------------------

function Scenario(base, scenarios){
  this.base = base;
  this.scenarios = scenarios;
}

Scenario.prototype.getBase = function(){
  return this.base;
}

Scenario.prototype.getScenarios = function(){
  return this.scenarios;
}
