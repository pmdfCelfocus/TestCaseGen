let requirements;
let onlyScenarios = [];
let $;
let actDiagram;
let seqDiagram;
let nodesArrays = [];

function getContent(content) {
  requirements = content;
  //console.log(requirements);
  processNodes();
}

function processNodes() {
  let value;
  let obj;
  Object.keys(requirements).forEach(function (key) {
    console.log('Key -> ' + key);
    value = requirements[key];
    Object.keys(value).forEach(function (index) {
      console.log('Index -> ' + index);
      obj = value[index];
      Object.keys(obj).forEach(function (attribute) {
        console.log('Attribute-> ' + attribute);
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
  Object.keys(onlyScenarios).forEach(function (index) {
    let scenarioArray = onlyScenarios[index];
    Object.keys(scenarioArray).forEach(function (title) {
      let nodeDataArray = [];
      let start = false;
      let steps = scenarioArray[title];
      let json;
      let parent = -1;
      let key = 0;
      nodeDataArray.push(buildJSON('INIT', 0, parent, key));
      parent++;
      key++;
      for (var i = 0; i < steps.length; i++) {
        let current = steps[String(i)];
        let string = String(current);
        if (string.includes('When')) {
          start = true;
          json = buildJSON(string, 0, parent, key);
        }
        else if (string.includes('Then')) {
          if (!start)
            continue;
          json = buildJSON(string, 1, parent, key);
        }
        else if ((string.includes('Given') || string === '')) {
          continue;
        }
        else {
          if (!start)
            continue;
          json = buildJSON(string, 2, parent, key);
        }
        key++;
        parent++;
        nodeDataArray.push(json);

      }
      nodeDataArray.push(buildJSON('END', 0, parent++, key++));
      nodesArrays.push(nodeDataArray);
    });
    
  });
  draw();
}

function buildJSON(string, config, parent, key) {
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
  //init();
let nodeDataArray = nodesArrays.pop();
console.log(nodesArrays);
  actDiagram.nodeTemplate =
    $(go.Node, "Vertical",
      { background: "#000" },
      $(go.TextBlock, "Default Text",
        { margin: 8, stroke: "white", font: "bold 16px sans-serif" },
        new go.Binding("text", "name"))
    );

  var model = $(go.TreeModel);
  model.nodeDataArray = nodeDataArray;
  actDiagram.model = model;
  //actDiagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);
}