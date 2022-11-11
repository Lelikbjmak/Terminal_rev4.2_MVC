var form = document.getElementById("resetPassword");
var email = document.getElementById("mailtoreset");

form.addEventListener('submit', (e) => {

    e.preventDefault();

    $(document).ready(
        function($) {

            $("#message").text('');
            $('.fa-lg').css({'opacity':'100%'});

            var mail = $("#mailtoreset").val().trim();

            $.post("/Barclays/client/ForgotPassword", {
                mail: mail,  //1st in Java | 2nd here

            }, function(data) {

            }).done(function(data, textStatus, jqXHR){


                setTimeout(() => {
                 $("#message").text(jqXHR.responseText);
                 $("#message").css("color", "rgba(0, 0, 0, 0.6)");
                 $('.fa-lg').css({'opacity':'0%'});
                }, 3000);


            }).fail(function(jqXHR, exception, textStatus, errorThrown) {

                var msg = '';
                if (jqXHR.status === 0) {
                    msg = 'Not connect. Verify Network.';
                } else if (jqXHR.status == 404) {
                    msg = 'Requested page not found. [404]';
                } else if (jqXHR.status == 500) {
                    msg = 'Internal Server Error [500].';
                }else if (jqXHR.status == 400) {  // bad request
                    msg = jqXHR.responseText;
                    $("#message").css("color", "rgba(255, 0, 0, 0.37)");
                }else if (exception === 'parsererror') {
                    msg = 'Requested JSON parse failed.';
                } else if (exception === 'timeout') {
                    msg = 'Time out error.';
                } else if (exception === 'abort') {
                    msg = 'Ajax request aborted.';
                } else {
                    msg = jqXHR.responseText;
                }

                setTimeout(() => {
                $("#message").text(jqXHR.responseText);
                $('.fa-lg').css({'opacity':'0%'});
                }, 3000);

            });

        });

    });




function checkParams() {

    var emailvalue = email.value.trim();
    var b = document.querySelector("#resetPassword input[type='submit']");

    if(emailvalue.length != 0) {
        b.removeAttribute('disabled');
    } else {
        b.setAttribute('disabled', 'disabled');
    }

}