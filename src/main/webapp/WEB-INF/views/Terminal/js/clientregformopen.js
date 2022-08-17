function openclientregform(){
var ov = document.getElementById('ovvv');
var win = document.getElementById('client');
var userwin = document.getElementById('user');

userwin.style.opacity = '0%';

ov.style.opacity = '100%';
ov.style.zIndex = '8';

win.style.opacity = "100%";
win.style.zIndex = '10';
}