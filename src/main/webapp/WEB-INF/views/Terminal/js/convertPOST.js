const form3 = document.getElementById("convert"); // form

const billfrom3 = document.getElementById("billfrom3");
const currency3 = document.getElementById("currency3");
const summa3 = document.getElementById("summa3");
const pin3 = document.getElementById("pin3");

summa3.onkeydown = function(e) {
if (!((e.keyCode > 95 && e.keyCode < 106) || (e.keyCode > 47 && e.keyCode < 58) || e.keyCode == 8 || e.keyCode == 190)) {
  return false; }
}


form3.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check3()){

    $(document).ready(
        function($) {

            $('.wrap').css({'opacity':'100%', 'z-index':'12'});


            var billfrom = $("#billfrom3").val().slice(0, -6);
            var currency = $("#currency3").val();
            var summa = $("#summa3").val();
            var pin = $("#pin3").val();


            $.post("/Barclays/bill/convert", {
                billFrom: billfrom,  //1st in Java | 2nd here
                currency: currency,
                summa: summa,
                pin: pin
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

                if('bill' in dat) {
                    const bF = document.getElementById("billfrom3");
                    setErrorFor(bF, dat.bill);
                }

                if('pin' in dat){
                    const pn = document.getElementById("pin3");
                    pn.parentElement.classList.remove('success');
                    setErrorFor(pn, dat.pin);
                }

                if('convert.billFrom' in dat){
                    const bF = document.getElementById("billfrom3");
                    bF.parentElement.classList.remove('success');
                    setErrorFor(bF, dat["convert.billFrom"]);

                }

                if('convert.pin' in dat){
                    const pn = document.getElementById("pin3");
                    setErrorFor(pn, dat["convert.pin"]);
                }

                if('convert.summa' in dat){
                    const ledger = document.getElementById("summa3");
                    setErrorFor(ledger, dat["convert.summa"]);
                }

                if('convert.currency' in dat){
                   const cur = document.getElementById("currency3");
                   setErrorFor(cur, dat["convert.currency"]);
                }

                if('ledger' in dat){
                   const ledger = document.getElementById("summa3");
                   setErrorFor(ledger, dat.ledger);
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



function check3(){
    let flag = true;

    var billfromvalue = billfrom3.value.trim();
    var currencyvalue = currency3.value.trim();
    var summavalue = summa3.value.trim();
    var pinvalue = pin3.value.trim();

    var summareg = /^(\d*\.\d{2})|(\d*\,\d{2})|(\d*)$/;
    var pinreg = /^\d{4}$/;

    if(billfromvalue === ''){
        setErrorFor(billfrom3, "You don't wield any card")
    }else{
        setSuccessFor(billfrom3);
    }

    if(currencyvalue === ''){
        setErrorFor(currency3, "Currency can't be blank");
        flag = false;
    }else{
        setSuccessFor(currency3);
    }

    if(summavalue === ''){
        flag = false;
        setErrorFor(summa3, "Summa can't be blank");
    }else if(!summareg.test(summavalue)){
        flag = false;
        setErrorFor(summa3, "Not valid format");
    }
//else{
//        var url = "/Barclays/bill/checkLedger?card=" + billfromvalue;
//        const xhr = new XMLHttpRequest();
//        xhr.open("GET", url, false);
//        xhr.send();
//        var ledger = xhr.response;
//
//        if(ledger < summavalue){ // indicates that this bill isn't exist
//           setErrorFor(summa3, "Scarcity of money on your ledger");
//           flag = false;
//        }
        else{
           setSuccessFor(summa3);
        }
//    }


   if(pinvalue === ''){
       setErrorFor(pin3,"Confirm transaction");
       flag = false;
   }else if(!pinreg.test(pinvalue)){
       setErrorFor(pin3, "Not valid format")
       flag = false;
   }else{
       setSuccessFor(pin3);
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


function checkParams3() {

     var billfromvalue = billfrom3.value.trim();
     var currencyvalue = currency3.value.trim();
     var summavalue = summa3.value.trim();
     var pinvalue = pin3.value.trim();

    if(billfromvalue.length != 0 && currencyvalue.length != 0 && summavalue.length != 0 && pinvalue.length != 0) {
        $('#bb3').removeAttr('disabled');
    } else {
        $('#bb3').attr('disabled', 'disabled');
    }
}