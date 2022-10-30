const form2 = document.getElementById("deposit");

const bill2 = document.getElementById("billfrom2");
const currency2 = document.getElementById("currency2");
const summa2 = document.getElementById("summa2");


form2.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check2()){

    $(document).ready(
        function($) {

            $('.wrap').css({'opacity':'100%', 'z-index':'12'});


            var billfrom = $("#billfrom2").val();
            var currency = $("#currency2").val();
            var summa = $("#summa2").val();


            $.post("/Barclays/bill/deposit", {
                billfrom: billfrom,  //1st in Java | 2nd here
                currency: currency,
                summa: summa

            }, function(data) {

            }).done(function(data, textStatus, jqXHR){
                setTimeout(() => {
                $('div.message').text(jqXHR.responseText);
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