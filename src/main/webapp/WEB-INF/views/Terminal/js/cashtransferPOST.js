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

            var billfrom = $("#billfrom").val();
            var billto = $("#billto").val();
            var summa = $("#summa").val();
            var pin = $("#pin").val();


            $.post("/Barclays/bill/cashtransfer", {
                billfrom: billfrom,
                billto: billto,
                summa: summa,
                pin: pin
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
//   }else //{
//       var url = "/Barclays/bill/checkPin?card=" + billfromvalue + "&pin=" + pinvalue;
//       const xhr = new XMLHttpRequest();
//       xhr.open("GET", url, false);
//       xhr.send();
//
//       if(xhr.response === "false"){ // indicates that this bill isn't exist
//          setErrorFor(pin, "Incorrect pin");
//          flag = false;
       }else{
          setSuccessFor(pin);
       }
//   }

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