const form4 = document.getElementById("cashextradition"); // form

const billfrom4 = document.getElementById("billfrom4");
const summa4 = document.getElementById("summa4");
const pin4 = document.getElementById("pin4");


summa4.onkeydown = function(e) {
if (!((e.keyCode > 95 && e.keyCode < 106) || (e.keyCode > 47 && e.keyCode < 58) || e.keyCode == 8 || e.keyCode == 190)) {
  return false; }
}



form4.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check4()){

    $(document).ready(
        function($) {

            $('.wrap').css({'opacity':'100%', 'z-index':'12'});


            var billfrom = $("#billfrom4").val().slice(0, -6);
            var summa = $("#summa4").val();
            var pin = $("#pin4").val();


            $.post("/Barclays/bill/cashExtradition", {
                billFrom: billfrom,  //1st in Java | 2nd here
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
                    const bF = document.getElementById("billfrom4");
                    setErrorFor(bF, dat.bill);
                }

                if('pin' in dat){
                    const pn = document.getElementById("pin");
                    setErrorFor(pn, dat.pin);
                }

                if('cashExtradition.billFrom' in dat){
                    const bF = document.getElementById("billfrom4");
                    bF.parentElement.classList.remove('success');
                    setErrorFor(bF, dat["cashExtradition.billFrom"]);

                }

                if('cashExtradition.pin' in dat){
                    const pn = document.getElementById("pin4");
                    setErrorFor(pn, dat["cashExtradition.pin"]);
                }

                if('cashExtradition.summa' in dat){
                    const ledger = document.getElementById("summa4");
                    setErrorFor(ledger, dat["cashExtradition.summa"]);
                }

                if('ledger' in dat){
                   const ledger = document.getElementById("summa4");
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



function check4(){
    let flag = true;

    var billfromvalue = billfrom4.value.trim();
    var summavalue = summa4.value.trim();
    var pinvalue = pin4.value.trim();

    var summareg = /^(\d*\.\d{2})|(\d*\,\d{2})|(\d*)$/;
    var pinreg = /^\d{4}$/;

    if(billfromvalue === ''){
        setErrorFor(billfrom4, "You don't wield any card")
    }else{
        setSuccessFor(billfrom4);
    }

    if(summavalue === ''){
        flag = false;
        setErrorFor(summa4, "Summa can't be blank");
    }else if(!summareg.test(summavalue)){
        flag = false;
        setErrorFor(summa4, "Not valid format");
    }else{
        setSuccessFor(summa4);
    }


   if(pinvalue === ''){
       setErrorFor(pin4,"Confirm transaction");
       flag = false;
   }else if(!pinreg.test(pinvalue)){
       setErrorFor(pin4, "Not valid format")
       flag = false;
   }else{
//       var url = "/Barclays/bill/checkPin?card=" + billfromvalue + "&pin=" + pinvalue;
//       const xhr = new XMLHttpRequest();
//       xhr.open("GET", url, false);
//       xhr.send();
//
//       if(xhr.response === "false"){ // indicates that this bill isn't exist
//          setErrorFor(pin4, "Incorrect pin");
//          flag = false;
//       }else{
          setSuccessFor(pin4);
//       }
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

function checkParams4() {

      var billfromvalue = billfrom4.value.trim();
      var summavalue = summa4.value.trim();
      var pinvalue = pin4.value.trim();

    if(billfromvalue.length != 0 && summavalue.length != 0 && pinvalue.length != 0) {
        $('#bb4').removeAttr('disabled');
    } else {
        $('#bb4').attr('disabled', 'disabled');
    }
}