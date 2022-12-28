const form2 = document.getElementById("deposit");

const bill2 = document.getElementById("billfrom2");
const currency2 = document.getElementById("currency2");
const summa2 = document.getElementById("summa2");

summa2.onkeydown = function(e) {
if (!((e.keyCode > 95 && e.keyCode < 106) || (e.keyCode > 47 && e.keyCode < 58) || e.keyCode == 8 || e.keyCode == 190)) {
  return false; }
}


form2.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check2()){

    $(document).ready(
        function($) {

            $('.wrap').css({'opacity':'100%', 'z-index':'12'});


            var billfrom = $("#billfrom2").val().slice(0, -6);
            var currency = $("#currency2").val();
            var summa = $("#summa2").val();


            $.post("/Barclays/bill/deposit", {
                billFrom: billfrom,  //1st in Java | 2nd here
                currency: currency,
                summa: summa

            }, function(data) {

            }).done(function(data, textStatus, jqXHR){

                $('div.message').text(data.message);
                $('.loader').css({'opacity':'0%', 'z-index':'0'});
                $('.bl').css({'opacity':'100%', 'z-index':'12'});

            }).fail(function(jqXHR, exception, textStatus, errorThrown) {

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

                if('bill' in dat){
                    const bF = document.getElementById("billfrom2");
                    setErrorFor(bF, dat.bill);
                }

                if('deposit.cardFrom' in dat){
                    const bF = document.getElementById("billfrom2");
                    setErrorFor(bF, dat["deposit.cardFrom"]);
                }

                if('deposit.summa' in dat){
                    const ledger = document.getElementById("summa2");
                    setErrorFor(ledger, dat["deposit.summa"]);
                }

                if('deposit.currency' in dat){
                   const cur = document.getElementById("currency2");
                   setErrorFor(cur, dat["deposit.currency"]);
                }


                $('div.message').text(msg);
                $('.loader').css('opacity','0%');
                if(jqXHR.status === 400 || jqXHR.status === 404 || jqXHR.status === 500){
                    for(var i = 1; i < $('.bl').length; i++)
                    $('.bl').eq(i).css({'opacity':'100%', 'z-index':'12'});
                } else
                    $('.bl').css({'opacity':'100%', 'z-index':'12'});

            });

        });

        }
    });
    


function check2(){

    let flag = true;

    var billfromvalue = bill2.value.trim();
    var currencyvalue = currency2.value.trim();
    var summavalue = summa2.value.trim();


    var summareg = /^(\d*\.\d{2})|(\d*\,\d{2})|(\d*)$/;


    if(billfromvalue === ''){
        setErrorFor(bill2, "You don't wield any card")
    }else{
        setSuccessFor(bill2);
    }


    if(summavalue === ''){
        flag = false;
        setErrorFor(summa2, "Summa can't be blank");
    }else if(!summareg.test(summavalue)){
        flag = false;
        setErrorFor(summa2, "Not valid format");
    }else{
        setSuccessFor(summa2);
    }

    if(currencyvalue === ''){
        setErrorFor(currency2, "Currency can't be blank");
        flag = false;
    }else{
        setSuccessFor(currency2);
    }

  return flag;
}




function setErrorFor(input, message){
    const r = input.parentElement;  //.r2
    const small = r.querySelector('small');
    small.innerText = message;
    r.className = 'r2 error';
}


function setSuccessFor(input){
    const r = input.parentElement;  //.r2
    r.className = 'r2 success';
}


function checkParams2() {

    var billfromvalue = bill2.value.trim();
    var currencyvalue = currency2.value.trim();
    var summavalue = summa2.value.trim();


    if(billfromvalue.length != 0 && summavalue.length != 0 && currencyvalue.length != 0) {
        $('#bb2').removeAttr('disabled');
    } else {
        $('#bb2').attr('disabled', 'disabled');
    }
}