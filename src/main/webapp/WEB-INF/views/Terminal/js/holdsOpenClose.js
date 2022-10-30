const fixh = document.getElementById('fixedbut');
const cplb = document.getElementById('cplb');   // all buttons to open forms to commit investments and holdings
const floath = document.getElementById('floatbut');

const messages = document.querySelectorAll(".fixed");
var wins = document.querySelectorAll(".form-pop");

const ov = document.getElementById('ov');

var forms = document.querySelectorAll('.con');

var header = document.querySelectorAll('.menu');

const win1 = document.getElementById('fixinvest');

const closebut = document.getElementById('prev');

var wrap = document.getElementById('wrap');

// fixed hold
fixh.addEventListener('click', function(){

fetch("http://localhost:8080/Barclays/bill/PercentageForFixed?currency=" + currency.value.trim() + "&term=" + term.value.trim()).then(response => response.text())
.then(responseText => {
   percentage.value = responseText;
});

    win1.style.marginTop = '-1280px';
    type.value = "Fixed";
    document.querySelector('div#fixinvest > h2 > u').textContent = '#Fixed Hold#';

    for(var i = 0; i < header.length; i++){
    header[i].style.zIndex = '1';
    }

    for(var i = 0; i < messages.length; i++){
        messages[i].style.zIndex = '0';
    }

    ov.style.opacity = '100%';
    ov.style.zIndex = '8';

    wins[0].style.opacity = "100%";
    wins[0].style.zIndex = '10';

});


// capitalisation of interests
cplb.addEventListener('click', function(){

fetch("http://localhost:8080/Barclays/bill/PercentageForFixed?currency=" + currency.value.trim() + "&term=" + term.value.trim()).then(response => response.text())
.then(responseText => {
   percentage.value = responseText;
});

    win1.style.marginTop = '-730px';
    type.value = "Capitalisation";
    document.querySelector('div#fixinvest > h2 > u').textContent = '#Capitalisation#';


    for(var i = 0; i < header.length; i++){
    header[i].style.zIndex = '1';
    }

    for(var i = 0; i < messages.length; i++){
        messages[i].style.zIndex = '0';
    }

    ov.style.opacity = '100%';
    ov.style.zIndex = '8';

    wins[0].style.opacity = "100%";
    wins[0].style.zIndex = '10';

});


ov.addEventListener('click', function(){

const rr = document.getElementsByClassName('r2 success');

const rrr = document.getElementsByClassName('r2 error');


for(let i = 0; i < 3; i++){
    for (let r of rr) {
          r.classList.remove('success');
    }

    for (let r of rrr) {
          r.classList.remove('error');
    }
}


ov.style.opacity = '0%';
ov.style.zIndex = '0';

for(var i = 0; i < messages.length; i++){
    messages[i].style.zIndex = '1';
}

for(var i = 0; i < wins.length; i++){
wins[i].style.opacity = '0%';
wins[i].style.zIndex = '0';
}

for(var i = 0; i < header.length; i++){
header[i].style.zIndex = '3';
}

for(var i = 0; i < forms.length; i++){
forms[i].reset();
}

});

closebut.addEventListener('click', function(){

const rr = document.getElementsByClassName('r2 success');

const rrr = document.getElementsByClassName('r2 error');


for(let i = 0; i < 3; i++){
    for (let r of rr) {
          r.classList.remove('success');
    }

    for (let r of rrr) {
          r.classList.remove('error');
    }
}


ov.style.opacity = '0%';
ov.style.zIndex = '0';

wrap.style.opacity = '0%';
wrap.style.zIndex = '0';


for(var i = 0; i < messages.length; i++){
    messages[i].style.zIndex = '1';
}


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

for(var i = 0; i < header.length; i++){
header[i].style.zIndex = '3';
}

for(var i = 0; i < forms.length; i++){
forms[i].reset();
}

});