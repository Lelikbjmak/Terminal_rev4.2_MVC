const form1 = document.getElementById("cashtransfer"); // form

const billfrom = document.getElementById("billfrom");
const billto = document.getElementById("billto");
const summa = document.getElementById("summa");
const pin = document.getElementById("pin");

summa.onkeydown = function(e) {
if (!((e.keyCode > 95 && e.keyCode < 106) || (e.keyCode > 47 && e.keyCode < 58) || e.keyCode == 8 || e.keyCode == 190)) {
  return false; }
}

$('#billto').on('keypress change', function () {
  $(this).val(function (index, value) {
    return value.replace(/\W/gi, '').replace(/(.{4})/g, '$1 ');
  });
});

form1.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check1()){

    $(document).ready(
        function($) {

            $('.wrap').css({'opacity':'100%', 'z-index':'12'});

            var billfrom = $("#billfrom").val().trim().slice(0, -6);
            var billto = $("#billto").val().trim().slice(0, -6);
            var summa = $("#summa").val().trim();
            var pin = $("#pin").val().trim();


            $.post("/Barclays/bill/cashTransfer", {
                billFrom: billfrom,
                billTo: billto,
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

                if('bill' in dat){

                    const bF = document.getElementById("billfrom");
                    const bT = document.getElementById("billto");

                    if(dat.card === bF.value) {
                        bF.parentElement.classList.remove('success');
                        setErrorFor(bF, dat.bill);
                    } else {
                        bT.parentElement.classList.remove('success');
                        setErrorFor(bT, dat.bill);
                    }
                }

                if('pin' in dat){
                    const pn = document.getElementById("pin");
                    setErrorFor(pn, dat.pin);
                }

                if('cashTransfer.billFrom' in dat){
                    const bF = document.getElementById("billfrom");
                    bF.parentElement.classList.remove('success');
                    setErrorFor(bF, dat["cashTransfer.billFrom"]);

                }

                if('cashTransfer.billTo' in dat){
                    const bT = document.getElementById("billto");
                    bT.parentElement.classList.remove('success');
                    setErrorFor(bT, dat["cashTransfer.billFrom"]);
                }

                if('cashTransfer.pin' in dat){
                    const pn = document.getElementById("pin");
                    setErrorFor(pn, dat["cashTransfer.pin"]);
                }

                if('cashTransfer.summa' in dat){
                    const ledger = document.getElementById("summa");
                    setErrorFor(ledger, dat["cashTransfer.summa"]);
                }

                if('ledger' in dat){
                   const ledger = document.getElementById("summa");
                   setErrorFor(ledger, dat.ledger);
                }

                if('billTo' in dat){
                    const bT = document.getElementById("billto");
                    setErrorFor(bT, dat.billTo);
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

function check1(){
    let flag = true;

    var billfromvalue = billfrom.value.trim();
    var billtovalue = billto.value.trim();
    var summavalue = summa.value.trim();
    var pinvalue = pin.value.trim();

    var billtoreg = /^(\d{4}\s{1}){3}\d{4}$/;
    var summareg = /^(\d*\.\d{2})|(\d*\,\d{2})|(\d*)$/;
    var pinreg = /^\d{4}$/;

    if(billfromvalue === ''){
        setErrorFor(billfrom, "You don't wield any card")
    }else{
        setSuccessFor(billfrom);
    }

    if(billtovalue === ''){
        setErrorFor(billto, "Recipient can't be blank");
        flag = false;
    }else if(!billtoreg.test(billtovalue)){
        setErrorFor(billtovalue, "Not valid format");
        flag = false;
    } else{
       setSuccessFor(billto);
    }


    if(summavalue === ''){
        flag = false;
        setErrorFor(summa, "Summa can't be blank");
    }else if(!summareg.test(summavalue)){
        flag = false;
        setErrorFor(summa, "Not valid format");
    } else{
       setSuccessFor(summa);
    }

   if(pinvalue === ''){
       setErrorFor(pin,"Confirm transaction");
       flag = false;
   }else if(!pinreg.test(pinvalue)){
       setErrorFor(pin, "Not valid format")
       flag = false;

   }else{
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


function checkParams1() {

       var billfromvalue = billfrom.value.trim();
       var billtovalue = billto.value.trim();
       var summavalue = summa.value.trim();
       var pinvalue = pin.value.trim();

    if(billfromvalue.length != 0 && summavalue.length != 0 && pinvalue.length != 0 && billtovalue.length != 0){
        $('#bb1').removeAttr('disabled');
    } else {
        $('#bb1').attr('disabled', 'disabled');

    }

}