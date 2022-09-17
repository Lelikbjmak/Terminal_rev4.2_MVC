
const rates = document.getElementById('rates');

rates.addEventListener('click', function(){

var time = document.getElementById('currtime');
var date = document.getElementById('currdate');

function getcurrtime(){
return new Date().toTimeString().replace(/ .*/, '');
}

date.innerHTML = getCurrentDate();
time.innerHTML = getcurrtime();

setInterval(
      () => time.innerHTML = getcurrtime(),
      1000
);

setInterval(
      () => date.innerHTML = getCurrentDate(),
      1000 * 60 * 60 * 24
);

function getCurrentDate(){
return new Date().toDateString();
}


var rt = document.getElementById('currencyrates');
var buts = document.querySelectorAll('.dws-menu');
var header = document.querySelectorAll('.menuaut1');
const ov = document.getElementById('ov');


for(var i = 0; i < header.length; i++){
header[i].style.zIndex = '0';
}


for(var i = 0; i < buts.length; i++){
buts[i].style.zIndex = '0';

}

ov.style.opacity = '100%';
ov.style.zIndex = '8';

rt.style.opacity = '100%';
rt.style.zIndex = '10';
});

async function getrate(){

var myHeaders = new Headers();
myHeaders.append("apikey", "iRObqt4llz1dG1BuUQSeTMd0VxLvc2AU");
myHeaders.append('Content-Type', 'application/json');

var requestOptions = {
  method: 'GET',
  redirect: 'follow',
  headers: myHeaders
};

const response = await fetch("https://api.apilayer.com/exchangerates_data/latest?symbols=BYN%2CEUR%2CRUB&base=USD", requestOptions);
return await response.json();

}

rates.addEventListener('click', async function(){

var buybyn = document.getElementById('buybyn');
var buyeur = document.getElementById('buyeur');
var buyrub = document.getElementById('buyrub');

var sellbyn = document.getElementById('sellbyn');
var selleur = document.getElementById('selleur');
var sellrub = document.getElementById('sellrub');

getrate().then((data) => {
buybyn.innerHTML = (data.rates.BYN).toFixed(4);
buyeur.innerHTML = (data.rates.EUR).toFixed(4);
buyrub.innerHTML = (data.rates.RUB).toFixed(4);

sellbyn.innerHTML = ((data.rates.BYN) + (data.rates.BYN)/100 * 1.5).toFixed(4);
selleur.innerHTML = ((data.rates.EUR) + (data.rates.EUR)/100 * 1.5).toFixed(4);
sellrub.innerHTML = ((data.rates.RUB) + (data.rates.RUB)/100 * 1.5).toFixed(4);

});


});
