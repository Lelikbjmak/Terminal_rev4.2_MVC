const forma = document.getElementById('ledgermenu');

const billF = document.getElementById('bill');

const pin = document.getElementById('pincode');

forma.addEventListener('submit', (e) => {

    e.preventDefault();

    if(ch()){

        $(document).ready(
            function($) {

                $('.wrap').css({'opacity':'100%', 'z-index':'12'});

                var bill = $("#bill").val();
                var pin = $('#pincode').val();

                $.post("/Barclays/bill/getLedger", {
                    bill: bill,  //1st in Java | 2nd here
                    pin: pin
                }, function(data) {

                 }).done(function(data, textStatus, jqXHR){
                        setTimeout(() => {
                     $('div.message').text(data.message);
                     $('.loader').css({'opacity':'0%', 'z-index':'0'});
                     }, 3000);

                }).fail(function(jqXHR, exception, textStatus, errorThrown) {

                    var data = JSON.parse(jqXHR.responseText);

                    var msg = '';
                    if (jqXHR.status === 0) {
                        msg = 'Not connect. Verify Network.';
                    } else if (jqXHR.status == 404) {
                        msg = 'Requested page not found. [404]';
                    } else if (jqXHR.status == 500) {
                        msg = 'Internal Server Error [500].';
                    }else if (jqXHR.status == 400) {
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



                    setTimeout(() => {
                    $('div.message').text(msg);
                    $('.loader').css('opacity','0%');
                    $('.bl').css({'opacity':'100%', 'z-index':'12'});
                    }, 3000);
                });

            });

    }

});


function ch(){

    let flag = true;

    var billvalue = billF.value.trim();
    var pinvalue = pin.value.trim();

    var pinreg = /^\d{4}$/;

    if(billvalue === ''){
        flag = false;
        setErrorFor(billF, "You don't wield any card")
    }else{
        setSuccessFor(billF);
    }

    if(pinvalue === ''){
        flag = false;
        setErrorFor(pin, "Confirm operation");
    }else if(!pinreg.test(pinvalue)){
        flag = false;
        setErrorFor(pin, 'Not valid format');
    } else{
           setSuccessFor(pin);
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