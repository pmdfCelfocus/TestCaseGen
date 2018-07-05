let requirements;

function getContent(content){
  requirements = content;
  console.log(requirements);
  generateDiv();
}

function generateDiv() {
  let para = document.createElement("p");
  para.className = 'test';
  let node = document.createTextNode('I GET IT NOW');
  let element = document.getElementById("instructions");

  para.appendChild(node);
  element.appendChild(para);
}

function processNodes() {
  let value;
  let obj;
  Object.keys(json).forEach(function (key) {
      console.log('Key -> ' + key);
      value = json[key];
      Object.keys(value).forEach(function (index) {
          console.log('Index -> ' + index);
          obj = value[index];
          Object.keys(obj).forEach(function (attribute) {
              console.log('Attribute-> ' + attribute);
              console.log(obj[attribute]);
          });
      });
  });
}