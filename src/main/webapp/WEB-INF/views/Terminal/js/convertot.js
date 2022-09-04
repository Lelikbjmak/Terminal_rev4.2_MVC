var usd = document.getElementById("usd");
var byn = document.getElementById("byn");
var eur = document.getElementById("eur");
var rub = document.getElementById("rub");


usd.oninput = async function(){
getrates('USD').then((data) => {
byn.value = (usd.value * data.rates.BYN).toFixed(2);
eur.value = (usd.value * data.rates.EUR).toFixed(2);
rub.value = (usd.value * data.rates.RUB).toFixed(2);
});
}


byn.oninput = async function(){
getrates('BYN').then((data) => {
usd.value = (byn.value * data.rates.USD).toFixed(2);
eur.value = (byn.value * data.rates.EUR).toFixed(2);
rub.value = (byn.value * data.rates.RUB).toFixed(2);
});
}


eur.oninput = async function(){
getrates('EUR').then((data) => {
byn.value = (eur.value * data.rates.BYN).toFixed(2);
usd.value = (eur.value * data.rates.USD).toFixed(2);
rub.value = (eur.value * data.rates.RUB).toFixed(2);
});
}


rub.oninput = async function(){
getrates('RUB').then((data) => {
byn.value = (usd.value * data.rates.BYN).toFixed(2);
eur.value = (usd.value * data.rates.EUR).toFixed(2);
rub.value = (usd.value * data.rates.RUB).toFixed(2);
});
}



async function getrates(currency){
var myHeaders = new Headers();
myHeaders.append("apikey", "iRObqt4llz1dG1BuUQSeTMd0VxLvc2AU");
myHeaders.append('Content-Type', 'application/json');

// set headers our api to get access to API  + content-type to work with JSON

var requestOptions = {
  method: 'GET',
  redirect: 'follow',
  headers: myHeaders
};

const response = await fetch("https://api.apilayer.com/fixer/latest?symbols=USD%2CEUR%2CRUB%2CBYN&base=" + currency, requestOptions);
return await response.json();

// we return promise but 'response' will be our response.json()
}