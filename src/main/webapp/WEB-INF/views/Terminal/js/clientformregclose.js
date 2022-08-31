
var win = document.getElementById('client');

var ov = document.getElementById('ovvv');

var userwin = document.getElementById('user');

ov.addEventListener('click', function(){

userwin.style.opacity = '100%';

win.style.opacity = '0%';
win.style.zIndex = '2';

ov.style.opacity = '0%';
ov.style.zIndex = '0';
});

