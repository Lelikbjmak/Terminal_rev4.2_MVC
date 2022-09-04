
var wins = document.querySelectorAll(".form-pop");

var ov = document.getElementById('ov');

var buts = document.querySelectorAll('.opopen');

var header = document.querySelectorAll('.menuaut1');

var bl = document.getElementById('prev');

var wrap = document.getElementById('wrap');

var form = document.querySelectorAll('.con');

var again = document.getElementById('again');

ov.addEventListener('click', function(){

const rr = document.getElementsByClassName('r2 success');

const rrr = document.getElementsByClassName('r2 error');

for (let r of rrr) {
      r.classList.remove('error');
    }

for (let r of rrr) {
      r.classList.remove('error');
    }

for (let r of rr) {
      r.classList.remove('success');
    }

for (let r of rr) {
      r.classList.remove('success');
    }


ov.style.opacity = '0%';
ov.style.zIndex = '0';

for(var i = 0; i < wins.length; i++){
wins[i].style.opacity = '0%';
wins[i].style.zIndex = '0';
}

for(var i = 0; i< buts.length; i++){
buts[i].style.zIndex = '3';
}

for(var i = 0; i < header.length; i++){
header[i].style.zIndex = '3';
}

for(var i = 0; i < form.length; i++){
form[i].reset();
}

});

bl.addEventListener('click', function(){

const rr = document.getElementsByClassName('r2 success');

const rrr = document.getElementsByClassName('r2 error');

for (let r of rrr) {
      r.classList.remove('error');
    }

for (let r of rrr) {
      r.classList.remove('error');
    }

for (let r of rr) {
      r.classList.remove('success');
    }

for (let r of rr) {
      r.classList.remove('success');
    }


ov.style.opacity = '0%';
ov.style.zIndex = '0';

wrap.style.opacity = '0%';
wrap.style.zIndex = '0';

var msg = document.getElementById('message');
msg.innerHTML = "";

var ccc = document.querySelectorAll('.bl');
for(var i = 0; i< ccc.length; i++){
ccc[i].style.opacity = '0%';
}

var loader = document.querySelectorAll('.loader');
for(var i = 0; i< loader.length; i++){
loader[i].style.opacity = '100%';
}

for(var i = 0; i < wins.length; i++){
wins[i].style.opacity = '0%';
wins[i].style.zIndex = '0';
}

for(var i = 0; i< buts.length; i++){
buts[i].style.zIndex = '3';
}

for(var i = 0; i < header.length; i++){
header[i].style.zIndex = '3';
}

for(var i = 0; i < form.length; i++){
form[i].reset();
}

});



again.addEventListener('click', function(){

wrap.style.opacity = '0%';
wrap.style.zIndex = '0';

var msg = document.getElementById('message');
msg.innerHTML = "";

var ccc = document.querySelectorAll('.bl');
for(var i = 0; i< ccc.length; i++){
ccc[i].style.opacity = '0%';
}

var loader = document.querySelectorAll('.loader');
for(var i = 0; i< loader.length; i++){
loader[i].style.opacity = '100%';
}

});
