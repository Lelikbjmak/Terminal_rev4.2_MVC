function checkParams(){
    var usernamevalue = document.getElementById("login").value.trim();
    var passwordvalue = document.getElementById("password").value.trim();


    if(usernamevalue.length != 0 &&  passwordvalue.length != 0) {
        $('#loggin').removeAttr('disabled');
    } else {
        $('#loggin').attr('disabled', 'disabled');
    }

}