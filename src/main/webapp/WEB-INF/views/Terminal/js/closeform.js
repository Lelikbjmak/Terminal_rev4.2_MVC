
var wins = document.querySelectorAll(".form-pop");

var ov = document.getElementById('ov');

ov.addEventListener('click', function(){
for(var i = 0; i < wins.length; i++){
wins[i].style.opacity = '0%';
wins[i].style.zIndex = '2';
}
ov.style.opacity = '0%';
ov.style.zIndex = '0';
});

