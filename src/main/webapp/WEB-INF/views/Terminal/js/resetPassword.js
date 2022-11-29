var password = document.getElementById("pass");
var confirmedPassword = document.getElementById("confPass");

function checkParams() {

    var passwordValue = password.value.trim();
    var confirmedPasswordValue = confirmedPassword.value.trim();

    var b = document.querySelector("#resetPassword1 input[type='submit']");

    if(passwordValue.length != 0 && confirmedPasswordValue.length != 0) {
        b.removeAttribute('disabled');
    } else {
        b.setAttribute('disabled', 'disabled');
    }

}


function check(){
let flag = true;

  var passwordValue = password.value.trim();
  var confirmedPasswordValue = confirmedPassword.value.trim();

  if(passwordValue === ""){
          // show error
          flag = false;
          setErrorFor(password, "Password can't be blank");
      }else if(passwordValue.length < 3){
          flag = false;
          setErrorFor(password, "Password must contain at least 8 symbols");
      }else{
          setSuccessFor(password);
      }

      if(confirmedPasswordValue === ""){
          setErrorFor(confirmedPassword, "Confirm password");
          flag = false;
      }else if(confirmedPasswordValue != passwordValue) {
          setErrorFor(confirmedPassword, "Password doesn't match");
          flag = false;
      }else if(confirmedPasswordValue.length < 3){
           flag = false;
           setErrorFor(confirmedPassword, "Password must contain at least 8 symbols");
      }
      else{
          setSuccessFor(confirmedPassword);
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
    const r = input.parentElement;  //.r1
    r.className = 'r2 success';
}


var form = document.getElementById("resetPassword1");

form.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check()){

        $(document).ready(

            function($) {

                 $("#message").text('');
                 $('.fa-lg').css({'opacity':'100%'});
                 document.querySelector("#resetPassword1 input[type='submit']").setAttribute('disabled', 'disabled');

                var password = $("#pass").val().trim();
                var confirmedPassword = $("#confPass").val().trim();

                $.post("/Barclays/client/resetPassword", {

                    password: password,
                    confirmedPassword: confirmedPassword,
                    token: getToken()

                }, function(data) {

                }).done(function(data, textStatus, jqXHR){

                   $("#message").text(data.message);
                   $("#message").css("color", "rgba(0, 0, 0, 0.6)");
                   $('.fa-lg').css({'opacity':'0%'});
                   document.querySelector("#resetPassword1 input[type='submit']").removeAttribute('disabled');

                }).fail(function(jqXHR, exception, errorThrown) {

                    var data = JSON.parse(jqXHR.responseText);
                    var msg = '';
                    if (jqXHR.status === 0) {
                        msg = 'Not connect. Verify Network.';
                    } else if (jqXHR.status === 404) {
                        msg = 'Requested page not found. [404]';
                    } else if (jqXHR.status === 500) {
                        msg = 'Internal Server Error [500].';
                    }else if (jqXHR.status === 400) {
                        msg = data.message;
                    }else if (exception === 'parsererror') {
                        msg = 'Requested JSON parse failed.';
                    } else if (exception === 'timeout') {
                        msg = 'Time out error.';
                    } else if (exception === 'abort') {
                        msg = 'Ajax request aborted.';
                    } else {
                        msg = 'Uncaught Error. ' + data.message;
                    }

                    if('resetPassword.confirmedPassword' in data){
                    us = document.getElementById("confPass");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data["resetPassword.confirmedPassword"]);
                    }

                    if('confirmedPassword' in data){
                    us = document.getElementById("confPass");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.confirmedPassword);
                    }


                    if('resetPassword.password' in data){
                    us = document.getElementById("pass");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data["resetPassword.password"]);
                    }

                    if('resetPassword.token' in data){
                    msg = msg + " " + data["resetPassword.token"];
                    }

                    setTimeout(() => {
                        $("#message").text(msg);
                        $("#message").css("color", "rgba(255, 0, 0, 0.37)");
                        $('.fa-lg').css({'opacity':'0%'});
                        document.querySelector("#resetPassword1 input[type='submit']").removeAttribute('disabled');
                    }, 3000);

                });

        });

    }

});



function getToken (url = window.location) {
    let params = {};

    new URL(url).searchParams.forEach(function (val, key) {
        params[key] = val; // Пушим пары ключ / значение (key / value) в объект
    });

    return params.token;
}


const togglePassword1 = document.querySelector("#togglePassword1");
const togglePassword2 = document.querySelector("#togglePassword2");


togglePassword1.addEventListener("click", function () {
    // toggle the type attribute
    const type = password.getAttribute("type") === "password" ? "text" : "password";
    password.setAttribute("type", type);

    // toggle the icon
    this.classList.toggle("fa-eye");

});

togglePassword2.addEventListener("click", function () {
    // toggle the type attribute
    const type = confirmedPassword.getAttribute("type") === "password" ? "text" : "password";
    confirmedPassword.setAttribute("type", type);

    // toggle the icon
    this.classList.toggle("fa-eye");

});