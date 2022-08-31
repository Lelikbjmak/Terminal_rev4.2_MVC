const username = document.getElementById("username");
const password = document.getElementById("password");
const confirmpassword = document.getElementById("confirmpassword");

const form = document.getElementById("go");

var win = document.getElementById('client');

var ov = document.getElementById('ovvv');

var userwin = document.getElementById('user');


function check(){
    let flag = true;
    // get values from inputs
    var usernamevalue = username.value.trim();
    var passwordvalue = password.value.trim();
    var confirmpasswordvalue = confirmpassword.value.trim();


    if(usernamevalue === ""){
    // show error
    flag = false;
    setErrorFor(username, "Username can't be blank");
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


    if(passwordvalue === ""){
     // show error
     flag = false;
     setErrorFor(password, "Username can't be blank");
     }else{
        setSuccessFor(password);
     }

    if(confirmpasswordvalue === ""){
     setErrorFor(confirmpassword, "Confirm password");
     flag = false;
     }else if(confirmpasswordvalue != passwordvalue){
     setErrorFor(confirmpassword, "Password doesn't match");
     flag = false;
     }else{
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


function checkusername(username){
var url = "/Barclays/client/checkUsername?username=" + username;
fetch(url).then(function(response){
    response.text().then(function(text){
    alert("text:" + text);
  });
});
}

form.addEventListener('click', (e) =>{

    if(check() === true){
        openclientregform();
    }

    var reg = /^([a-zа-яё]+[\s]{0,1}[a-zа-яё]+[\s]{0,1}[a-zа-яё])$/ig;
     // ig any substring in any register
    var str = "Иван Иванов";
    var str_en = "Petya Petrovich Pupkin";
    var str_not_work = "Petya Petrovich Pupkin sfsdfsdf";

    console.log(reg.test(str));
    console.log(reg.test(str_en));
    console.log(reg.test(str_not_working));

});

const forma = document.getElementById("regg");

regg.addEventListener('submit', (e) => {
    e.preventDefault();
    check1();
});

const name = document.getElementById("name");
const passport = document.getElementById("passport");
const birth = document.getElementById("bday");
const phone = document.getElementById("phone");


function check1(){
const namevalue = name.value.trim();
const passportvalue = passport.value.trim();
const birth = birth.value;
const phonevalue = phone.value.trim();

if(namevalue === ''){
setErrorFor(name, "Name can't be blank")
}


}
