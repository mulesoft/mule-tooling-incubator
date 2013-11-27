var invokeCallbacks = new Array();
var listenerCallbacks = new Array();
function callCallback(id,response){
    if(invokeCallbacks[id] != null){
        invokeCallbacks[id](response);
        delete invokeCallbacks[id];
    }
}

function invoke(message,callback){
    var id = guid();
    invokeCallbacks[id] = callback;
    window.status = "message:"+id+":"+message;
    //
    // setTimeout(function () { callback('HELLO') }, 500);
    //
}

function notifyListener(topic,message){
  if(listenerCallbacks[topic]!=null){
    listenerCallbacks[topic](message);
  }
}

function registerListener(topic,callback){
  //todo add it to a list
  listenerCallbacks[topic] = callback;
  window.status = "listener:"+topic;
}

function unRegisterListener(topic){
  //todo add it to a list
  delete listenerCallbacks[topic];
}

function show(message){
    alert(message);
}

function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
    .toString(16)
    .substring(1);
}

function guid() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
    s4() + '-' + s4() + s4() + s4();
}