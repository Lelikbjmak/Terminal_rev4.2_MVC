const changeUsernameForm = document.getElementById("updateLogin");
const changeUsernameLink = document.querySelector(".sidebar-menu li:nth-child(2) ul li:nth-child(1) a");

changeUsernameLink.addEventListener('click', function(){
    closeOtherOpenTabs();
    $('#updateLogin').css({'opacity':'100%', 'zIndex':'3'});
});

function closeChangeUsernameForm(){
    $("#message2").text('');
    $('#updateLogin')[0].reset();
    $('#updateLogin').css({'opacity':'0%', 'zIndex':'0'});
    removeSuccessAndErrorAttributes();
}

function checkUsernameParams() {
    var usernameValue = document.getElementById('username').value.trim();
    var b = document.querySelector("#updateLogin input[type='submit']");
    if(usernameValue.length != 0) {
        b.removeAttribute('disabled');
    } else {
        b.setAttribute('disabled', 'disabled');
    }
}


function checkUsername(){   // before sending request
    let flag = true;

    var username = document.getElementById('username');
    var usernameValue = username.value.trim();
    var usernameValueRegex = /\w{4,20}/;

    if(usernameValue === ""){
        flag = false;
        setErrorFor(username, "Username can't be blank");
    }else if(usernameValue.length < 4){
        flag = false;
        setErrorFor(username, "Username must contain at least 4 characters");
    }else if(!usernameValueRegex.test(usernameValue)){
        flag = false;
        setErrorFor(username, "Username contains at least 4 chars (A-z, 0-9, _)")
    }else {
        setSuccessFor(username);
    }
    return flag;
}


changeUsernameForm.addEventListener('submit', (e) => {
    e.preventDefault();
    if(checkUsername()){
         $("#message2").text('');
         $('#updateLogin .fa-lg').css({'opacity':'100%'});
         document.querySelector("#updateLogin input[type='submit']").setAttribute('disabled', 'disabled');

         var newUsername = $("#username").val().trim();

         var logins = {
            "newLogin": newUsername
         }

         $.ajax({
             url: '/Barclays/user/changeUsername',
             type: 'post',
             contentType: 'application/json',
             data: JSON.stringify(logins),
             processData: false,
             success: function(data, textStatus, jqXHR){

                 $("#message2").text(data.message);
                 $("#message2").css("color", "rgba(0, 0, 0, 0.6)");
                 $('#updateLogin .fa-lg').css({'opacity':'0%'});

                 setTimeout(() => {
                     closeChangeUsernameForm();
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

                 if('username' in data){
                     us = document.getElementById("username");
                     us.parentElement.classList.remove('success');
                     setErrorFor(us, data.password);
                 }

                 $('#updateLogin .fa-lg').css({'opacity':'0%'});
                     document.querySelector("#updateLogin input[type='submit']").removeAttribute('disabled');
                 }, 2000);
             }
         });

    }
});
