var conv = document.getElementById('convert');
var win = document.getElementById('convertor');
var buts = document.querySelectorAll('.dws-menu');
var header = document.querySelectorAll('.menuaut1');
var ov = document.getElementById('ov');

conv.addEventListener('click', function(){

for(var i = 0; i < header.length; i++){
header[i].style.zIndex = '0';
}

for(var i = 0; i < buts.length; i++){
buts[i].style.zIndex = '0';

}

ov.style.opacity = '100%';
ov.style.zIndex = '8';


win.style.opacity = "100%";
win.style.zIndex = '10';

});