const form = document.getElementById("cardapply"); // form

const type = document.getElementById("type");
const currency = document.getElementById("Currency");


form.addEventListener('submit', (e) => {

    e.preventDefault();


//    $(document).ready(
//        function($) {
//
//            $('.wrap').css({'opacity':'100%', 'z-index':'12'});
//
//            var type = $("#type").val();
//            var currency = $("#Currency").val();
//
//            bill = {
//            "currency": currency,
//            "type": type
//            };
//
//            $.post("/Barclays/bill/add", {
//                type:type,
//                currency: currency
//            }, function(data) {
//
//            }).done(function(data, textStatus, jqXHR){
//                    setTimeout(() => {
//                 $('div.message').text(jqXHR.responseText);
//                 $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
//                 $('.loader').css({'opacity':'0%', 'z-index':'0'});
//                 $('.bl').css({'opacity':'100%', 'z-index':'12'});
//                 alert(document.querySelector('div.message').innerText);
//                 datafromcardreg = Array.from(document.querySelector('div.message').innerText.matchAll(/(\d{4}\s{1}){4}/gi))[0][0];
//                 alert(datafromcardreg);
//                 }, 3000);
//
//            }).fail(function(jqXHR, exception, textStatus, errorThrown) {
//                var msg = '';
//                if (jqXHR.status === 0) {
//                    msg = 'Not connect. Verify Network.';
//                } else if (jqXHR.status == 404) {
//                    msg = 'Requested page not found. [404]';
//                } else if (jqXHR.status == 500) {
//                    msg = 'Internal Server Error [500].';
//                }else if (jqXHR.status == 400) {
//                    msg = jqXHR.responseText;
//                }else if (exception === 'parsererror') {
//                    msg = 'Requested JSON parse failed.';
//                } else if (exception === 'timeout') {
//                    msg = 'Time out error.';
//                } else if (exception === 'abort') {
//                    msg = 'Ajax request aborted.';
//                } else {
//                    msg = 'Uncaught Error. ' + jqXHR.responseText;
//                }
//
//                setTimeout(() => {
//                $('div.message').text(msg);
//                $('.loader').css('opacity','0%');
//                $('.bl').css({'opacity':'100%', 'z-index':'12'});
//                }, 3000);
//            });
//        });

    if(checkBeforeRegisterBill()){

        var type = $("#type").val();
        var currency = $("#Currency").val();

        bill = {
        "currency": type,
        "type": currency
        };

        document.querySelector('#cardreg button[type="submit"]').setAttribute('disabled', 'disabled');
        $('.wrap').css({'opacity':'100%', 'z-index':'12'});

        $.ajax({
            url: '/Barclays/bill/add',
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify(bill),
            processData: false,

            success: function(data, textStatus, jqXHR){
               setTimeout(() => {
               $('div.message').text(data.message);
               $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
               $('.loader').css({'opacity':'0%', 'z-index':'0'});
               $('.bl').css({'opacity':'100%', 'z-index':'12'});
               document.querySelector('#cardreg button[type="submit"]').removeAttribute('disabled');
               }, 2500);

            },
            error: function(jqXHR, exception, textStatus){

                document.querySelector('#cardreg button[type="submit"]').removeAttribute('disabled');
                var data = JSON.parse(jqXHR.responseText);

                var msg = '';

                if (jqXHR.status === 0) {
                    msg = 'Not connect. Verify Network.';
                } else if (jqXHR.status === 404) {
                    msg = 'Requested page not found. [404]';
                } else if (jqXHR.status === 500) {
                    msg = 'Internal Server Error [500].';
                }else if (jqXHR.status === 400) {
                    msg = data.message;
                }else if (exception === 'parsererror') {
                    msg = 'Requested JSON parse failed.';
                } else if (exception === 'timeout') {
                    msg = 'Time out error.';
                } else if (exception === 'abort') {
                    msg = 'Ajax request aborted.';
                } else {
                    msg = 'Uncaught Error. ' + data.message;
                }

                if('currency' in data){
                us = document.getElementById("Currency");
                us.parentElement.classList.remove('success');
                setErrorForR1(us, data.currency);
                }

                if('type' in data){
                us = document.getElementById("type");
                us.parentElement.classList.remove('success');
                setErrorForR1(us, data.type);
                }

                setTimeout(() => {
                $('div.message').text(msg);
                $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
                $('.loader').css('opacity','0%');
                $('.bl').css({'opacity':'100%', 'z-index':'12'});
                }, 2500);

            }
        });

    }

});



function setErrorForR1(input, message){
    const r = input.parentElement;  //.r1
    const small = r.querySelector('small');
    small.innerText = message;
    r.className = 'r1 error';
}


function setSuccessForR1(input){
    const r = input.parentElement;  //.r1
    r.className = 'r1 success';
}


function checkBeforeRegisterBill(){

var flag = true;

const currencyEnum =  { USD: 'USD', EUR: 'EUR', BYN: 'BYN', RUB: 'RUB' };
const typeEnum =  { Salary: 'Salary', Personal: 'Personal', Corporate: 'Corporate' };

const typeValue = type.value.trim();
const currencyValue = currency.value.trim();


if(currencyValue in currencyEnum){
    setSuccessForR1(currency);
} else {
    setErrorForR1(currency, "Choose correct currency. Can't be blank.");
    flag = false;
}

if(typeValue in typeEnum){
    setSuccessForR1(type);
} else {
    setErrorForR1(type, "Choose correct type. Can't be blank.");
    flag = false;
}

return flag;

}
