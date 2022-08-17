
var ov = document.getElementById('ovvv');

var win = document.getElementById('cardreg');

ov.addEventListener('click', function(){

win.style.opacity = '0%';
win.style.zIndex = '2';

ov.style.opacity = '0%';
ov.style.zIndex = '0';
});

