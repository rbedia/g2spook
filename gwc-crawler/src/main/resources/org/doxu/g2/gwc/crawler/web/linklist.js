function init() {
  window.counter = 0;
  window.timer = false;
  addBatch();
}

function addBatch() {
  var div, a, text;
  window.services = document.getElementById('services');
  window.urls = services.getElementsByTagName('ul')[0].getElementsByTagName('li');
  div = document.createElement('div');
  a = document.createElement('a');
  a.setAttribute('href', '#');
  a.onclick = addAll;
  text = document.createTextNode('Add All GWebCaches');
  a.appendChild(text);
  div.appendChild(a);
  div.setAttribute('id', 'add');
  services.appendChild(div);
  return false;
}

function addAll() {
  var p, text
  window.counter = 0;
  clearInterval(timer);
  window.timer = setInterval('sendLink()', 50);
  p = window.services.getElementsByTagName('p');
  if(p.length == 0) {
    p = document.createElement('p');
    if(window.counter == 0) {
      text = document.createTextNode(urls.length + ' URLs added to Shareaza.');
    }
    else {
      text = document.createTextNode('URLs could not be added to Shareaza.');
    }
    p.appendChild(text);
    window.services.appendChild(p);
  }
  return false;
}

function sendLink() {
  if(window.counter >= window.urls.length) {
    clearInterval(timer);
  }
  else {
    try {
      document.location.href = window.urls[counter].getElementsByTagName('a')[0];
      window.counter++;
    }
    catch(e) {
      clearInterval(window.timer);
    }
  }
  return true;
}