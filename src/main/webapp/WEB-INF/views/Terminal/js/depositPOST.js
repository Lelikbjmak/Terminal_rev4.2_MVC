$(document).ready(
    function($) {

        $("#bb2").click(function(event) {

               event.preventDefault();

                var billfrom = $("#billfrom2").val();
                var currency = $("#currency2").val();
                var summa = $("#summa2").val();


                $.post("/Barclays/bill/deposit", {
                    billfrom: billfrom,  //1st in Java | 2nd here
                    currency: currency,
                    summa: summa,

                }, function(data) {
                    var json = JSON.parse(data);
                }).done(function(){
                }).fail(function(xhr, textStatus, errorThrown) {
                }).complete(function(data){
                     $form.each(function(){
                          this.reset();  // или так очищается форма методом .reset()
                        });
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
    
    
    