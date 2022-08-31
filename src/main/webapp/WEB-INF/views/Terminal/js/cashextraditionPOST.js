$(document).ready(
    function($) {

        $("#bb4").click(function(event) {

                event.preventDefault();

                $('.wrap').css({'opacity':'100%', 'z-index':'12'});


                var billfrom = $("#billfrom4").val();
                var summa = $("#summa4").val();
                var pin = $("#pin4").val();


                $.post("/Barclays/bill/cashextradition", {
                    billf: billfrom,  //1st in Java | 2nd here
                    summa: summa,
                    pin: pin
                }, function(data) {

                }).done(function(data, textStatus, jqXHR){
                        setTimeout(() => {
                     $('div.message').text(jqXHR.responseText);
                     $('.loader').css({'opacity':'0%', 'z-index':'0'});
                     $('.bl').css({'opacity':'100%', 'z-index':'12'})
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
                    $('.bl').css({'opacity':'100%', 'z-index':'12'})
                    }, 3000);
                });


        });

    });


