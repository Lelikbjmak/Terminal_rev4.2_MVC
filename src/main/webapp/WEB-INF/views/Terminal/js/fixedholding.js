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
        percentage.value = responseText;
    });
}

term.onchange = async function(){
    getprecentage(currency.value.trim(), term.value.trim()).then(response => response.text())
    .then(responseText => {
        percentage.value = responseText;
    });
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
             var url = "/Barclays/bill/checkLedger?card=" + billfrom.value.trim();
             const xhr = new XMLHttpRequest();
             xhr.open("GET", url, false);
             xhr.send();
             var ledger = xhr.response;

             if(ledger < cardsumma.value.trim()){
                setErrorFor(cardsumma, "Scarcity of money on your ledger");
                flag = false;
             }else{
                setSuccessFor(cardsumma);
             }
         }

         if(pin.value.trim() === ''){
             setErrorFor(pin,"Confirm transaction");
             flag = false;
         }else if(!pinreg.test(pin.value.trim())){
             setErrorFor(pin, "Not valid format");
             flag = false;
         }else{
             var url = "/Barclays/bill/checkPin?card=" + billfrom.value.trim() + "&pin=" + pin.value.trim();
             const xhr = new XMLHttpRequest();
             xhr.open("GET", url, false);
             xhr.send();

             if(xhr.response === "false"){ // indicates that this bill isn't exist
                setErrorFor(pin, "Incorrect pin");
                flag = false;
             }else{
                setSuccessFor(pin);
             }
         }
         return flag;
    }

}


forma.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check1()){

        if(radios[0].checked){

             $(document).ready(
                    function($) {

                        $('.wrap').css({'opacity':'100%', 'z-index':'12'});

                        var typevalue = $("#type1").val();
                        var currencyvalue = $("#Currency").val();
                        var percentage = $("#percentage").val();
                        var termvalue = $("#term").val();
                        var currencyfromvalue = $("#Currencyfrom").val();
                        var summavalue = $("#dep1").val();


                        $.post("/Barclays/bill/HoldCash", {
                            type: typevalue,  //1st in Java | 2nd here
                            currency: currencyvalue,
                            precentage: percentage,
                            term: termvalue,
                            summa : summavalue,
                            currfrom: currencyfromvalue

                        }, function(data) {

                        }).done(function(data, textStatus, jqXHR){
                            setTimeout(() => {
                            $('div.message').text(jqXHR.responseText);
                            $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
                            $('.loader').css({'opacity':'0%', 'z-index':'0'});
                            $('.bl').css({'opacity':'100%', 'z-index':'12'});
                            }, 3000);
                        }).fail(function(jqXHR, exception, textStatus, errorThrown) {
                            var msg = '';
                            if (jqXHR.status === 0) {
                                msg = 'Not connect. Verify Network.';
                            } else if (jqXHR.status == 404) {
                                msg = 'Requested page not found. [404]';
                            } else if (jqXHR.status == 500) {
                                msg = 'Internal Server Error [500].';
                            }else if (jqXHR.status == 400) {
                                msg = jqXHR.responseText;
                            }else if (exception === 'parsererror') {
                                msg = 'Requested JSON parse failed.';
                            } else if (exception === 'timeout') {
                                msg = 'Time out error.';
                            } else if (exception === 'abort') {
                                msg = 'Ajax request aborted.';
                            } else {
                                msg = 'Uncaught Error. ' + jqXHR.responseText;
                            }

                            setTimeout(() => {
                            $('div.message').text(msg);
                            $('.loader').css('opacity','0%');
                            $('.bl').css({'opacity':'100%', 'z-index':'12'});
                            }, 3000);
                    });

             });

        }else{

            $(document).ready(
                function($) {

                    $('.wrap').css({'opacity':'100%', 'z-index':'12'});

                    var typevalue = $("#type1").val();
                    var currencyvalue = $("#Currency").val();
                    var percentage = $("#percentage").val();
                    var termvalue = $("#term").val();
                    var billvalue = $('#billfrom').val();
                    var summavalue = $("#dep2").val();


                    $.post("/Barclays/bill/HoldCard", {
                        type: typevalue,  //1st in Java | 2nd here
                        currency: currencyvalue,
                        precentage: percentage,
                        term: termvalue,
                        summa : summavalue,
                        bill: billvalue

                    }, function(data) {

                    }).done(function(data, textStatus, jqXHR){
                        setTimeout(() => {
                        $('div.message').text(jqXHR.responseText);
                        $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
                        $('.loader').css({'opacity':'0%', 'z-index':'0'});
                        $('.bl').css({'opacity':'100%', 'z-index':'12'});
                        }, 3000);
                    }).fail(function(jqXHR, exception, textStatus, errorThrown) {
                        var msg = '';
                        if (jqXHR.status === 0) {
                            msg = 'Not connect. Verify Network.';
                        } else if (jqXHR.status == 404) {
                            msg = 'Requested page not found. [404]';
                        } else if (jqXHR.status == 500) {
                            msg = 'Internal Server Error [500].';
                        }else if (jqXHR.status == 400) {
                            msg = jqXHR.responseText;
                        }else if (exception === 'parsererror') {
                            msg = 'Requested JSON parse failed.';
                        } else if (exception === 'timeout') {
                            msg = 'Time out error.';
                        } else if (exception === 'abort') {
                            msg = 'Ajax request aborted.';
                        } else {
                            msg = 'Uncaught Error. ' + jqXHR.responseText;
                        }

                        setTimeout(() => {
                        $('div.message').text(msg);
                        $('.loader').css('opacity','0%');
                        $('.bl').css({'opacity':'100%', 'z-index':'12'});
                        }, 3000);
                });

            });

        }

    }

});

