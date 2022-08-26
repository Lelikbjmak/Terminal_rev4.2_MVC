$(document).ready(
    function($) {

        $("#bb1").click(function(event) {

                event.preventDefault();

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
                    var json = JSON.parse(data);
                }).done(function(){
                }).fail(function(xhr, textStatus, errorThrown) {
                }).complete(function(){

                   var wins = document.querySelectorAll(".form-pop");

                   var ov = document.getElementById('ov');

                   for(var i = 0; i < wins.length; i++){
                   wins[i].style.opacity = '0%';
                   wins[i].style.zIndex = '0';
                   }
                   ov.style.opacity = '0%';
                   ov.style.zIndex = '0';


                });

        });

    });


