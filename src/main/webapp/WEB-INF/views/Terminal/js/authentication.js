function checkParams(){
    var usernamevalue = document.getElementById("login").value.trim();
    var passwordvalue = document.getElementById("password").value.trim();


    if(usernamevalue.length != 0 &&  passwordvalue.length != 0) {
        $('#loggin').removeAttr('disabled');
    } else {
        $('#loggin').attr('disabled', 'disabled');
    }

}

const togglePassword = document.querySelector("#togglePassword");
const password = document.querySelector("#password");

togglePassword.addEventListener("click", function () {
    // toggle the type attribute
    const type = password.getAttribute("type") === "password" ? "text" : "password";
    password.setAttribute("type", type);

    // toggle the icon
    this.classList.toggle("fa-eye");

});

const loginButton = document.querySelector("#loggin");

loginButton.addEventListener("click", function(){
    $('.fa-lg').css({'opacity':'100%'});
});

