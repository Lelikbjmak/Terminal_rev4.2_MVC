
var ov = document.getElementById('ov');

var ww = document.getElementsByClassName('form-pop');

var formm = document.querySelectorAll('.con');

var win = document.getElementById('cardreg');

var wrap = document.getElementById('wrap');

var but = document.getElementById('prev');

var buts = document.getElementsByClassName('dws-menu');

var header = document.querySelectorAll('.menuaut1');

var rrt = document.getElementById('currencyrates');

var wrap = document.getElementById('wrap');


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

win.style.opacity = '0%';
win.style.zIndex = '0';

rrt.style.opacity = '0%';
rrt.style.zIndex = '0';

for(var i = 0; i < ww.length; i++){
ww[i].style.opacity = '0%';
ww[i].style.zIndex = '0';
}

for(var i = 0; i< buts.length; i++){
buts[i].style.zIndex = '3';
}

for(var i = 0; i < header.length; i++){
header[i].style.zIndex = '3';
}

for(var i = 0; i < formm.length; i++){
formm[i].reset();
}

});




but.addEventListener('click', function(){


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

win.style.opacity = '0%';
win.style.zIndex = '0';

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

for(var i = 0; i < ww.length; i++){
ww[i].style.opacity = '0%';
ww[i].style.zIndex = '0';
}

rrt.style.opacity = '0%';
rrt.style.zIndex = '0';


for(var i = 0; i< buts.length; i++){
buts[i].style.zIndex = '3';
}

for(var i = 0; i < header.length; i++){
header[i].style.zIndex = '3';
}

for(var i = 0; i < formm.length; i++){
formm[i].reset();
}

});


var recbutton = document.getElementById("rec");

//alert(recbutton.parentElement);



recbutton.addEventListener("click", function(){
    alert("rebuild card# " + datafromcardreg);
    savecardfile();
    but.removeAttribute("disabled");
});


function savecardfile(){
    let link = document.createElement("a");
    link.setAttribute("href", "http://localhost:8080/Barclays/bill/card?card=" + datafromcardreg);
    link.setAttribute("download", datafromcardreg);
    link.click();
}