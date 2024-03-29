const username = document.getElementById("username");
const password = document.getElementById("password");
const confirmpassword = document.getElementById("confirmpassword");
const email = document.getElementById("email");
const form = document.getElementById("go");
const forma = document.getElementById("regg");  // our form
const resendButton = document.getElementById("rec");

var win = document.getElementById('client');

var ov = document.getElementById('ovvv');

var userwin = document.getElementById('user');



function check(){

    let flag = true;
    // get values from inputs
    var usernamevalue = username.value.trim();
    var passwordvalue = password.value.trim();
    var confirmpasswordvalue = confirmpassword.value.trim();
    var emailvalue = email.value.trim();

    if(usernamevalue === ""){
        // show error
        flag = false;
        setErrorFor(username, "Username can't be blank");
    }else if(usernamevalue.length < 4){
        flag = false;
        setErrorFor(username, "Username must contain at least 4 symbols")
    }else{
        var url = "/Barclays/client/checkUsername?username=" + usernamevalue;
        const xhr = new XMLHttpRequest();
        xhr.open("GET", url, false);
        xhr.send();

        if(xhr.response === "false"){
            setErrorFor(username, "Username already exist");
            flag = false;
        }else{
            setSuccessFor(username);
        }
    }


    if(emailvalue === ""){
            // show error
            flag = false;
            setErrorFor(email, "Email can't be blank");
    }else{
        var url = "/Barclays/client/checkMail?mail=" + emailvalue;
               const xhr = new XMLHttpRequest();
               xhr.open("GET", url, false);
               xhr.send();

               if(xhr.response === "false"){
                   setErrorFor(email, "Email already used");
                   flag = false;
               }else{
                   setSuccessFor(email);
               }
    }


    if(passwordvalue === ""){
        // show error
        flag = false;
        setErrorFor(password, "Password can't be blank");
    }else if(passwordvalue.length < 8){
              flag = false;
              setErrorFor(password, "Password must contain at least 8 symbols");
    }else{
        setSuccessFor(password);
    }

    if(confirmpasswordvalue === ""){
        setErrorFor(confirmpassword, "Confirm password");
        flag = false;
    }else if(confirmpasswordvalue != passwordvalue) {
        setErrorFor(confirmpassword, "Password doesn't match");
        flag = false;
    }else if(confirmpasswordvalue.length < 8){
         flag = false;
         setErrorFor(confirmpassword, "Password must contain at least 8 symbols");
    }
    else{
        setSuccessFor(confirmpassword);
    }

     return flag;  // if true -> 0 mistakes -> open client form
}

function setErrorFor(input, message){
    const r = input.parentElement;  //.r1
    const small = r.querySelector('small');
    small.innerText = message;
    r.className = 'r1 error';
}


function setSuccessFor(input){
    const r = input.parentElement;  //.r1
    r.className = 'r1 success';
}



function openclientregform(){
userwin.style.opacity = '0%';

ov.style.opacity = '100%';
ov.style.zIndex = '8';

win.style.opacity = "100%";
win.style.zIndex = '10';
}



form.addEventListener('click', (e) =>{

    if(check() === true){
        openclientregform();
    }

});






forma.addEventListener('submit', (e) => {
    e.preventDefault();

    if(check1()){

        $('.wrap').css({'opacity':'100%', 'z-index':'12'});

        var username = $("#username").val().trim();
        var email = $("#email").val().trim();
        var password = $("#password").val().trim();
        var confirmedPassword = $("#confirmpassword").val().trim();
        var name = $("#name").val().trim();
        var passport = $("#passport").val().trim();
        var birth = $("#bday").val();
        var phone = $("#phone").val().trim();

        user = {
        "username":username,
        "password":password,
        "confirmedpassword":confirmedPassword,
        "mail":email
        };

        client = {
        "name":name,
        "phone":phone,
        "passport":passport,
        "birth":birth
        };

        $.ajax({
            url: '/Barclays/client/add',
            type: 'post',
            contentType: 'application/json',
            data: JSON.stringify({user, client}),
            processData: false,

            success: function(data, textStatus, jqXHR){
               setTimeout(() => {
               $('div.message').text(data.message);
               $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
               $('div.message1').text('');
               $('.loader').css({'opacity':'0%', 'z-index':'0'});
               $('.bl').css({'opacity':'100%', 'z-index':'12'});
               }, 2500);

            },
            error: function(jqXHR, exception, textStatus){

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

                if('username' in data){
                    us = document.getElementById("username");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.username);
                }

                if('password' in data){
                    us = document.getElementById("password");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.password);
                }

                if('confirmedPassword' in data){
                    us = document.getElementById("confirmpassword");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.confirmedPassword);
                }

                if('mail' in data){
                    us = document.getElementById("email");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.mail);
                }

                if('name' in data){
                    us = document.getElementById("name");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.name);
                }

                if('passport' in data){
                    us = document.getElementById("passport");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.passport);
                }


                if('phone' in data){
                    us = document.getElementById("email");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data.mail);
                }

                setTimeout(() => {
                $('div.message').text(msg);
                $('div.message').html($('div.message').html().replace(/\n/g,'<br/>'));
                $('div.message1').text('');
                $('.loader').css({'opacity':'0%', 'z-index':'0'});
                $('.bl').css({'opacity':'100%', 'z-index':'12'});
                }, 2500);
            }
        });


    }
});


const name = document.getElementById("name");
const passport = document.getElementById("passport");
const birth = document.getElementById("bday");
const phone = document.getElementById("phone");


function check1(){

var flag = true;
const namevalue = name.value.trim();
const passportvalue = passport.value.trim();
const birthvalue = birth.value;
const phonevalue = phone.value.trim();

var namereg = /^([A-Z]{1}[a-z]*\s?){2}|([А-Я]{1}[а-я]*\s?){2}$/;
var passportreg = /^[A-Z]{2}\d{7}$/;

if(namevalue === ''){
    setErrorFor(name, "Name can't be blank");
    flag = false;
}else if(!namereg.test(namevalue)){
    setErrorFor(name, "Not valid format");
    flag = false;
}else{
    setSuccessFor(name);
}


if(passportvalue === ''){
    setErrorFor(passport, "Passport can't be blank")
    flag = false;
}else if(!passportreg.test(passportvalue)){
    setErrorFor(passport, "Not valid format");
    flag = false;
}else{
    setSuccessFor(passport);
}

if(birthvalue === ''){
    setErrorFor(birth, "Birth can't be blank")
    flag = false;
}else{
    setSuccessFor(birth);
}

if(phonevalue === ''){
    setErrorFor(phone, "Phone can't be blank")
    flag = false;
}else{
    setSuccessFor(phone);
}

return flag;

}



resendButton.addEventListener('click', (e) => {

    e.preventDefault();

    $(document).ready(
        function($) {

             $('.loader').css({'opacity':'100%', 'z-index':'13'});

            var username = $("#username").val().trim();

            $.post("/Barclays/client/resendConfirmation", {
                username: ""  //1st in Java | 2nd here
            }, function(data) {

             }).done(function(data, textStatus, jqXHR){
                var dat = JSON.parse(jqXHR.responseText);
                alert(data.message + " " + dat.message);
                setTimeout(() => {
                    $('.message1').text(data.message);
                    $('.loader').css({'opacity':'0%', 'z-index':'0'});
                }, 2500);

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

                if('resendConfirmationPostRequest.username' in data){
                    us = document.getElementById("username");
                    us.parentElement.classList.remove('success');
                    setErrorFor(us, data["resendConfirmationPostRequest.username"]);
                }

                setTimeout(() => {
                $('.message1').text(msg);
                $('.loader').css({'opacity':'0%', 'z-index':'0'});
                }, 2500);

            });




        });

    });


function checkParamsClient() {

    var namevalue = name.value.trim();
    var passportvalue = passport.value.trim();
    var birthvalue = birth.value;
    var phonevalue = phone.value.trim();

    if(phonevalue.length != 0 && namevalue.length != 0 && passportvalue.length != 0 && birthvalue.length != 0) {
        $('#reg').removeAttr('disabled');
    } else {
        $('#reg').attr('disabled', 'disabled');
    }
}


function checkParamsUser() {

    var usernamevalue = username.value.trim();
    var passwordvalue = password.value.trim();
    var confirmpasswordvalue = confirmpassword.value.trim();
    var emailvalue = email.value.trim();

    if(usernamevalue.length != 0 && emailvalue.length != 0 && passwordvalue.length != 0 && confirmpasswordvalue.length != 0 ) {
        $('#go').removeAttr('disabled');
    } else {
        $('#go').attr('disabled', 'disabled');
    }
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
    const type = confirmpassword.getAttribute("type") === "password" ? "text" : "password";
    confirmpassword.setAttribute("type", type);

    // toggle the icon
    this.classList.toggle("fa-eye");

});