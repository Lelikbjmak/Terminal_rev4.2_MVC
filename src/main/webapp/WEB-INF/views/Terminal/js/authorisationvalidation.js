const login = document.getElementById("login");
const password = document.getElementById("password");

const but = document.getElementById("loggin");

function checkk(){

alert("go");

const loginvalue = login.value.trim();
const passwordvalue = password.value.trim();


if(loginvalue === ''){
    setErrorFor(login, "Username can't be blank");
}else{
    var url = "/Barclays/client/checkUsername?username=" + loginvalue;
    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, false);
    xhr.send();

     if(xhr.response === "true"){
         setErrorFor(login, "Username doesn't exist");
     }else{
         setSuccessFor(login);
     }
}


if(passwordvalue === ''){
    setErrorFor(password, "Confirm authentication")
}else{

    var url = "/Barclays/client/checkPassword?username=" + loginvalue + "&password=" + passwordvalue;
    const xhr = new XMLHttpRequest();
    xhr.open("GET", url, false);
    xhr.send();

     if(xhr.response === "false"){
         setErrorFor(password, "Incorrect password");
     }else{
         setSuccessFor(password);
     }
}

alert("end");
}

