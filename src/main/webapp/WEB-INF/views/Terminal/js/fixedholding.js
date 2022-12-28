const type = document.getElementById('type1');
const currency = document.getElementById('Currency');
const percentage = document.getElementById('percentage');
const term = document.getElementById('term');
const radios = document.querySelectorAll('div.form_radio_btn > .r2 > input');
const forma = document.getElementById('fixed1');  // our form to send
const paybut = document.getElementById('b');
const cashpayment = document.getElementById('fixcash');
const cardpayment = document.getElementById('fixcard');
const currfrom = document.getElementById('Currencyfrom');
const cashsumma = document.getElementById('dep1');
const cardsumma = document.getElementById('dep2');
const billfrom = document.getElementById('billfrom');
const pin = document.getElementById('pin');


currency.onchange = async function(){
    getprecentage(currency.value.trim(), term.value.trim()).then(response => response.text())
    .then(responseText => {
        var dat = JSON.parse(responseText);
        percentage.value = dat.percentage;
    });
}


term.onchange = async function(){
    getprecentage(currency.value.trim(), term.value.trim()).then(response => response.text())
    .then(responseText => {
        var dat = JSON.parse(responseText);
        percentage.value = dat.percentage;
    });
}


cashsumma.onkeydown = function(e) {
if (!((e.keyCode > 95 && e.keyCode < 106) || (e.keyCode > 47 && e.keyCode < 58) || e.keyCode == 8 || e.keyCode == 190)) {
  return false; }
}

cardsumma.onkeydown = function(e) {
if (!((e.keyCode > 95 && e.keyCode < 106) || (e.keyCode > 47 && e.keyCode < 58) || e.keyCode == 8 || e.keyCode == 190)) {
  return false; }
}


function checkParamsFixed() {

    var typevalue = type.value.trim();
    var currencyvalue = currency.value.trim();
    var percentage = percentage.value.trim();
    var termvalue = term.value.trim();

    if(typevalue.length != 0 && currencyvalue.length != 0 && percentage.length != 0 && termvalue.length != 0) {
        $('#b').removeAttr('disabled');
    } else {
        $('#b').attr('disabled', 'disabled');
    }

}


function checkParamsFixedPaymentCash() {

    var summavalue = cashsumma.value.trim();
    var currencyvalue = currfrom.value.trim();

    if(summavalue.length != 0 && currencyvalue.length != 0) {
        $('#fixinv1').removeAttr('disabled');
    } else {
        $('#fixinv1').attr('disabled', 'disabled');
    }

}


function checkParamsFixedPaymentCard() {

    var billvalue = billfrom.value.trim();
    var summavalue = cardsumma.value.trim();
    var pinvalue = pin.value.trim();

    if(summavalue.length != 0 && billvalue.length != 0 || pinvalue.length !=0) {
        $('#fixinv2').removeAttr('disabled');
    } else {
        $('#fixinv2').attr('disabled', 'disabled');
    }

}


function check(){

    let flag = true;

    var typevalue = type.value.trim();
    var currencyvalue = currency.value.trim();
    var percentagevalue = percentage.value.trim();
    var termvalue = term.value.trim();


    if(typevalue === ''){
        flag = false;
        setErrorFor(type, "can't be blank");
    } else{
        setSuccessFor(type);
    }

    if(currencyvalue === ''){
        flag = false;
        setErrorFor(currency, "can't be blank");
    } else{
        setSuccessFor(currency);
    }

    if(termvalue === ''){
        flag = false;
        setErrorFor(term, "can't be blank");
    } else{
        setSuccessFor(term);
    }

    if(percentagevalue === ''){
        flag = false;
        setErrorFor(percentage, "can't be blank");
    }else{
        setSuccessFor(percentage);
    }

    return flag;
}


function setErrorFor(input, message){
    const r = input.parentElement;  //.r1
    const small = r.querySelector('small');
    small.innerText = message;
    r.className = 'r2 error';
}


function setSuccessFor(input){
    const r = input.parentElement;  //.r1
    r.className = 'r2 success';
}



async function getprecentage(data, data1){
const response = fetch("http://localhost:8080/Barclays/bill/PercentageForFixed?currency=" + data + "&term=" + data1);
return await response;
// we return promise but 'response' will be our response.json()
}


paybut.addEventListener('click', function(){

    if(check()){

        for(let i = 0; i < radios.length; i++){
            if(radios[i].checked){

                if(radios[i].value === 'cash'){
                    wins[0].style.opacity = '0%';
                    cashpayment.style.zIndex = '11';
                    cashpayment.style.opacity = '100%';
                }else{
                    wins[0].style.opacity = '0%';
                    cardpayment.style.zIndex = '11';
                    cardpayment.style.opacity = '100%';
                }

            }
        }
    }

});

function check1(){

    let flag = true;

    var summareg = /^(\d*\.\d{2})|(\d*\,\d{2})|(\d*)$/;

    if(radios[0].checked){

        if(currfrom.value.trim() === ''){
          flag = false;
          setErrorFor(currfrom, "Can't be blank");
        }else{
          setSuccessFor(currfrom);
        }

        if(cashsumma.value.trim() === ''){
               flag = false;
               setErrorFor(cashsumma, "Summa can't be blank");
           }else if(!summareg.test(cashsumma.value.trim())){
               flag = false;
               setErrorFor(cashsumma, "Not valid format");
           }else{
               setSuccessFor(cashsumma);
           }

       return flag;

    }else{  // indicates that client has chosen card payment

        var pinreg = /^\d{4}$/;

        if(billfrom.value.trim() === ''){
            flag = false;
              setErrorFor(billfrom, "You don't wield any card");
        }else{
              setSuccessFor(billfrom);
        }

         if(cardsumma.value.trim() === ''){
                flag = false;
                setErrorFor(cardsumma, "Summa can't be blank");
         }else if(!summareg.test(cardsumma.value.trim())){
             flag = false;
             setErrorFor(cardsumma, "Not valid format");
         }else{
             setSuccessFor(cardsumma);
         }

         if(pin.value.trim() === ''){
             setErrorFor(pin,"Confirm transaction");
             flag = false;
         }else if(!pinreg.test(pin.value.trim())){
             setErrorFor(pin, "Not valid format");
             flag = false;
         } else {
                setSuccessFor(pin);
         }

         return flag;
    }

}


forma.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check1()){

        $('.wrap').css({'opacity':'100%', 'z-index':'12'});

        if(radios[0].checked){

                var typevalue = $("#type1").val();
                var currencyvalue = $("#Currency").val();
                var percentage = $("#percentage").val();
                var termvalue = $("#term").val();
                var currencyfromvalue = $("#Currencyfrom").val();
                var summavalue = $("#dep1").val();

            investment = {
                "type":"",
                "percentage":"",
                "currency":"",
                "term":""
            };

            $.ajax({
                url: '/Barclays/bill/HoldCash',
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify({investment, "deposit":summavalue, "currencyFrom":currencyfromvalue}),
                processData: false,
                success: function(data, textStatus, jqXHR){

                   $('div.message').text(data.message);
                   $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
                   $('div.message1').text('');
                   $('.loader').css({'opacity':'0%', 'z-index':'0'});
                   $('.bl').css({'opacity':'100%', 'z-index':'12'});

                },
                error: function( jqXHR, textStatus, errorThrown ){

                    var msg = '';
                    var dat = JSON.parse(jqXHR.responseText);

                    if (jqXHR.status === 200) {
                        msg = dat.message;
                    } else if (jqXHR.status === 0) {
                        msg = 'Not connect. Verify Network.';
                    } else if (jqXHR.status == 404) {
                        msg = 'Requested page not found. [404]';
                    } else if (jqXHR.status == 500) {
                        msg = 'Internal Server Error [500].';
                    }else if (jqXHR.status == 400) {
                        msg = dat.message;
                    }else if (exception === 'parsererror') {
                        msg = 'Requested JSON parse failed.';
                    } else if (exception === 'timeout') {
                        msg = 'Time out error.';
                    } else if (exception === 'abort') {
                        msg = 'Ajax request aborted.';
                    } else {
                        msg = 'Uncaught Error. ' + jqXHR.responseText;
                    }

                    $('div.message').text(msg);
                    $('.loader').css('opacity','0%');
                    $('.bl').css({'opacity':'100%', 'z-index':'12'});

                    const type = document.getElementById('type1');
                    const currency = document.getElementById('Currency');
                    const percentage = document.getElementById('percentage');
                    const term = document.getElementById('term');

                    if('summa' in dat) {
                        const cashDep = document.getElementById('dep1');
                        setErrorFor(cashDep, dat.summa);
                    }

                    if('currencyOfDep' in dat) {
                        const currFrom = document.getElementById('Currencyfrom');
                        setErrorFor(currFrom, dat.currencyOfDep);
                    }

                    if('term' in dat){
                        const tr = document.getElementById('term');
                        setErrorFor(tr, dat.term);
                    }

                    if('type' in dat){
                        const tp = document.getElementById('type1');
                        setErrorFor(tp, dat.type);
                    }

                    if('currency' in dat) {
                        const summa = document.getElementById('Currency');
                        setErrorFor(curr, dat.currency);
                    }
                }
            });

        } else {

            var typevalue = $("#type1").val().trim();
            var currencyvalue = $("#Currency").val().trim();
            var percentage = $("#percentage").val().trim();
            var termvalue = $("#term").val().trim();
            var billvalue = $('#billfrom').val().trim().slice(0, -6);
            var summavalue = $("#dep2").val().trim();
            var pinvalue = $("#pin").val().trim();

            investment = {
                "type":"typevalue",
                "percentage":percentage,
                "currency":"",
                "term":termvalue
            };

            $.ajax({
                url: '/Barclays/bill/HoldCard',
                type: 'post',
                contentType: 'application/json',
                data: JSON.stringify({investment, "deposit":summavalue, "pin":pinvalue, "bill":billvalue}),
                processData: false,
                success: function(data, textStatus, jqXHR){

                   $('div.message').text(data.message);
                   $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
                   $('.loader').css({'opacity':'0%', 'z-index':'0'});
                   $('.bl').css({'opacity':'100%', 'z-index':'12'});

                },
                error: function(jqXHR, textStatus, errorThrown){

                    var msg = '';
                    var dat = JSON.parse(jqXHR.responseText);

                    if (jqXHR.status === 200) {
                        msg = dat.message;
                    } else if (jqXHR.status === 0) {
                        msg = 'Not connect. Verify Network.';
                    } else if (jqXHR.status === 404) {
                        msg = 'Requested page not found. [404]';
                    } else if (jqXHR.status === 500) {
                        msg = 'Internal Server Error [500].';
                    }else if (jqXHR.status === 400) {
                        msg = dat.message;
                    }else if (exception === 'parsererror') {
                        msg = 'Requested JSON parse failed.';
                    } else if (exception === 'timeout') {
                        msg = 'Time out error.';
                    } else if (exception === 'abort') {
                        msg = 'Ajax request aborted.';
                    } else {
                        msg = 'Uncaught Error. ' + jqXHR.responseText;
                    }

                    if('summa' in dat) {
                        const cardDep = document.getElementById('dep2');
                        setErrorFor(cardDep, dat.summa);
                    }

                    if('currencyOfDep' in dat) {
                        const currFrom = document.getElementById('Currencyfrom');
                        setErrorFor(currFrom, dat.currencyOfDep);
                    }
                    if('currency' in dat) {
                        const curr = document.getElementById('Currency');
                        setErrorFor(curr, dat.currency);
                    }

                    if('term' in dat){
                        const tr = document.getElementById('term');
                        setErrorFor(tr, dat.term);
                    }

                    if('type' in dat){
                        const tp = document.getElementById('type1');
                        setErrorFor(tp, dat.type);
                    }


                    $('div.message').text(msg);
                    $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
                    $('.loader').css('opacity','0%');
                    $('.bl').css({'opacity':'100%', 'z-index':'12'});
                }

            });

        }

    }

});

