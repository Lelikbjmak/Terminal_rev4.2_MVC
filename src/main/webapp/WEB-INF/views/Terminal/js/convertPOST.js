$(document).ready(
    function($) {

        $("#bb3").click(function(event) {

                event.preventDefault();

                var billfrom = $("#billfrom3").val();
                var currency = $("#currency3").val();
                var summa = $("#summa3").val();
                var pin = $("#pin3").val();



                $.post("/Barclays/bill/convert", {
                    billfrom: billfrom,  //1st in Java | 2nd here
                    currency: currency,
                    summa: summa,
                    pin: pin
                }, function(data) {
                    alert(data);
                }).done(function(data, textStatus, jqXHR){
                    $("#ov").css({'opacity':'0%', 'z-index':'0'});
                    $(".form-pop").css({'opacity':'0%', 'z-index':'0'});
                    $(".con").trigger('reset');
                }).fail(function(xhr, textStatus, errorThrown) {
                });


        });

    });


