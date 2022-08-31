$(document).ready(
    function($) {

        $("#bb1").click(function(event) {

                event.preventDefault();

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


