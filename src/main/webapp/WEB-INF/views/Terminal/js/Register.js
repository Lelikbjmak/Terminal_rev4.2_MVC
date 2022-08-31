
$(document).ready(
    function($) {

        $("#reg").click(function(event) {

                event.preventDefault();
                var username = $("#username").val().trim();
                var password = $("#password").val().trim();
                var confirmedpassword = $("#confirmpassword").val().trim();
                var name = $("#name").val().trim();
                var passport = $("#passport").val().trim();
                var birth = $("#bday").val();
                var phone = $("#phone").val().trim();


                $.post("/Barclays/client/add", {
                    username: username,  //1st in Java | 2nd here
                    password: password,
                    confirmedpassword: confirmedpassword,
                    name: name,
                    passport: passport,
                    birth: birth,
                    phone: phone
                }, function(data) {

                 }).done(function(data, textStatus, jqXHR){
                       alert(jqXHR.responseText);
                }).fail(function(jqXHR, exception, errorThrown) {
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
                    alert(msg);

                });


        });

    });


