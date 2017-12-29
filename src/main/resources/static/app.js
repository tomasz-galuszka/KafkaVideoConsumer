var stompClient = null;

var URL = '/gs-guide-websocket';
var TOPIC = '/topic/message';

function connect() {
    var socket = new SockJS(URL);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe(TOPIC, function (data) {
            updateImage(data.body);
        });

    });
}


function updateImage(base64Image) {
    $('#camera-image').attr('src', 'data:image/png;base64,' + base64Image);
}

$(document).ready(function() {
    connect();
});