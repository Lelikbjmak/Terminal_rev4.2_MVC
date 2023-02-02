var password = document.getElementById("pass");
var confirmedPassword = document.getElementById("confPass");
var currentPassword = document.getElementById("currentPassword");

var changePasswordLink = document.querySelector(".sidebar-menu li:nth-child(2) ul li:nth-child(2) a");

var checkPasswordForm = document.querySelector("#resetPassword");

var form = document.getElementById("resetPassword1");

changePasswordLink.addEventListener('click', function(){
$('#resetPassword').css({'opacity':'100%', 'zIndex':'10', 'margin-top': '-23%'});
});

function closeCheckPasswordForm(){
$('#resetPassword')[0].reset();
$('#resetPassword').css({'opacity':'0%', 'zIndex':'0'});
}

function closeChangePasswordForm(){
$('#resetPassword1')[0].reset();
$('#resetPassword1').css({'opacity':'0%', 'zIndex':'0'});
}

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

function checkParams1() {

    var currentPasswordValue = currentPassword.value.trim();
    var b = document.querySelector("#resetPassword input[type='submit']");

    if(currentPasswordValue.length != 0) {
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


function check1(){
    let flag = true;
    var currentPasswordValue = currentPassword.value.trim();

    if(currentPasswordValue === ""){
      flag = false;
      setErrorFor(currentPassword, "Password can't be blank");
    }else if(currentPasswordValue.length < 3){
      flag = false;
      setErrorFor(currentPassword, "Password must contain at least 8 symbols");
    }else{
      setSuccessFor(currentPassword);
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


checkPasswordForm.addEventListener('submit', (e) => {

    e.preventDefault();
    if(check1()){
        $("#message1").text('');
        $('.fa-lg').css({'opacity':'100%'});
        document.querySelector("#resetPassword input[type='submit']").setAttribute('disabled', 'disabled');

        var currPassword = $("#currentPassword").val().trim();

        var passwords = {
            "oldPassword": currPassword
        }

        $.ajax({
            url: '/Barclays/user/checkPassword',
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify(passwords),
            processData: false,
            success: function(data, textStatus, jqXHR){

                setTimeout(() => {
                $("#message1").text(data.message);
                $("#message1").css("color", "rgba(0, 0, 0, 0.6)");
                $('.fa-lg').css({'opacity':'0%'});
                document.querySelector("#resetPassword1 input[type='submit']").removeAttribute('disabled');
                form.reset();
                }, 2000);

                setTimeout(() => {
                  $('#resetPassword').css({'opacity':'0%', 'zIndex':'0', 'margin-top': '-23%'});
                  $('#resetPassword1').css({'opacity':'100%', 'zIndex':'3', 'margin-top': '-37%'});
                }, 3500);

            },
            error: function( jqXHR, textStatus, errorThrown ){
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

                setTimeout(() => {

                if('password' in data){
                    us = document.getElementById("currentPassword");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.password);
                }

                $('.fa-lg').css({'opacity':'0%'});
                document.querySelector("#resetPassword1 input[type='submit']").removeAttribute('disabled');
                }, 2000);
            }
        });
    }
});


form.addEventListener('submit', (e) => {

    e.preventDefault();

    if(check()){

       $("#message").text('');
       $('.fa-lg').css({'opacity':'100%'});
       document.querySelector("#resetPassword1 input[type='submit']").setAttribute('disabled', 'disabled');

       var password = $("#pass").val().trim();
       var confirmedPassword = $("#confPass").val().trim();

       var passwords = {
           "newPassword": password,
           "confirmedNewPassword": confirmedPassword
       }

        $.ajax({
            url: '/Barclays/user/changePassword',
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify(passwords),
            processData: false,
            success: function(data, textStatus, jqXHR){
                setTimeout(() => {
                $("#message").text(data.message);
                $("#message").css("color", "rgba(0, 0, 0, 0.6)");
                $('.fa-lg').css({'opacity':'0%'});
                document.querySelector("#resetPassword1 input[type='submit']").removeAttribute('disabled');
                form.reset();
                }, 2000);
            },
            error: function( jqXHR, textStatus, errorThrown ){
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

                setTimeout(() => {
                if('confirmedPassword' in data){
                    us = document.getElementById("confPass");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.confirmedPassword);
                }

                if('password' in data){
                    us = document.getElementById("pass");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.password);
                }

                $("#message").text(msg);
                $("#message").css("color", "rgba(255, 0, 0, 0.37)");
                $('.fa-lg').css({'opacity':'0%'});
                document.querySelector("#resetPassword1 input[type='submit']").removeAttribute('disabled');
                }, 2000);
            }
        });
    }


});



const togglePassword1 = document.querySelector("#togglePassword1");
const togglePassword2 = document.querySelector("#togglePassword2");
const togglePassword3 = document.querySelector("#togglePassword3");

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

togglePassword3.addEventListener("click", function () {
    // toggle the type attribute
    const type = currentPassword.getAttribute("type") === "password" ? "text" : "password";
    currentPassword.setAttribute("type", type);

    // toggle the icon
    this.classList.toggle("fa-eye");

});


$(document).ready(function(){

  $(".sidebar-menu > li.have-children a").on("click", function(i){
      i.preventDefault();
    if( ! $(this).parent().hasClass("active") ){
      $(".sidebar-menu li ul").slideUp();
      $(this).next().slideToggle();
      $(".sidebar-menu li").removeClass("active");
      $(this).parent().addClass("active");
    }
    else{
      $(this).next().slideToggle();
      $(".sidebar-menu li").removeClass("active");
        }
    });
});