const personalInfoForm = document.getElementById("personalInfo");
const personalInfoLink = document.querySelector(".sidebar-menu li:nth-child(2) ul li:nth-child(3) a");

const editPersonalInfoButton = document.querySelector('#personalInfo button');

personalInfoLink.addEventListener('click', function(){
    closeOtherOpenTabs();
    setCurrentPersonalData();
    $('#personalInfo').css({'opacity':'100%', 'zIndex':'3'});
});

async function setCurrentPersonalData(){
    getPersonalInfo().then((data) => {
        var inps = document.querySelectorAll("#personalInfo div.r2 input");
        inps[0].value = data.name;
        inps[1].value = data.passport;
        inps[2].value = data.phone;
        inps[3].value = data.birthday;
    });
}

function closePersonalInfoForm(){
    var inps = document.querySelectorAll("#personalInfo div.r2 input");
    for(var i = 0; i < inps.length; i++){  // except button submit
        inps[i].setAttribute('disabled', '');
    }
    $("#message3").text('');
    $('#personalInfo').css({'opacity':'0%', 'zIndex':'0'});
    removeSuccessAndErrorAttributes();
}

async function getPersonalInfo(){
    var requestOptions = {
      method: 'GET',
      redirect: 'follow'
    };
    const response = await fetch("/Barclays/user/getPersonalInfo", requestOptions);
    return await response.json();
    // we return promise but 'response' will be our response.json()
}

editPersonalInfoButton.addEventListener('click', function(){
    var inps = document.querySelectorAll("#personalInfo div.r2 input");

    if(inps[0].disabled){
        this.firstChild.style.color = 'rgba(255, 0, 0, 0.4)';
    } else {
        this.firstChild.style.color = '#0096f4';
    }

    for(var i = 0; i < inps.length; i++){  // except button submit
        if(inps[i].disabled && i % 2 !=  1){
            inps[i].removeAttribute('disabled');
        } else {
            inps[i].setAttribute('disabled', '');
        }
    }
});

function checkPersonalDataParams() {
    var inps = document.querySelectorAll("#personalInfo div.r2 input");
    var nameValue = inps[0].value.trim();
    var phoneValue = inps[2].value.trim();

    var b = document.querySelector("#personalInfo input[type='submit']");
    if(nameValue.length != 0 && phoneValue.length != 0) {
        b.removeAttribute('disabled');
    } else {
        b.setAttribute('disabled', 'disabled');
    }
}

function checkInfoBeforeUpdate(){
     let flag = true;

     var inps = document.querySelectorAll("#personalInfo div.r2 input");
     var nameValue = inps[0].value.trim();
     var phoneValue = inps[2].value.trim();
     var nameValueRegex = /([A-Z]{1}[a-z]*\s?){2,3}|([А-Я]{1}[а-я]*\s?){2,3}/;
     var phoneValueRegex = /\+?\d{7,16}/;

     if(nameValue === ""){
        flag = false;
        setErrorFor(inps[0], "Full name can't be blank");
     }else if(!nameValueRegex.test(nameValue)){
         flag = false;
         setErrorFor(inps[0], "Not valid. Pattern [Ivan Inanov Ivanovich]")
     }else {
         setSuccessFor(inps[0]);
     }

     if(phoneValue === ""){
        flag = false;
        setErrorFor(inps[2], "Phone can't be blank");
     }else if(!phoneValueRegex.test(phoneValue)){
         flag = false;
         setErrorFor(inps[2], "Must contain 7-16 digits only")
     }else {
         setSuccessFor(inps[2]);
     }

     return flag;
}


personalInfoForm.addEventListener('submit', (e) => {
    e.preventDefault();
    if(checkInfoBeforeUpdate()){

        $("#message3").text('');
        $('#personalInfo .fa-lg').css({'opacity':'100%'});
        document.querySelector("#personalInfo input[type='submit']").setAttribute('disabled', 'disabled');

         var inps = document.querySelectorAll("#personalInfo div.r2 input");
         var nameValue = inps[0].value.trim();
         var phoneValue = inps[2].value.trim();

         $.ajax({
              url: '/Barclays/user/updatePersonalInfo',
              type: 'post',
              data: jQuery.param({name: nameValue, phone: phoneValue}),
              processData: false,
              success: function(data, textStatus, jqXHR){

                  $("#message3").text(data.message);
                  $("#message3").css("color", "rgba(0, 0, 0, 0.6)");
                  $('#personalInfo .fa-lg').css({'opacity':'0%'});
                  setCurrentPersonalData();
                  setTimeout(() => {
                      closePersonalInfoForm();
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


                  if('updatePersonalInfo.name' in data){
                      var us = document.querySelectorAll("#personalInfo div.r2 input");
                      setErrorFor(us[0], data['updatePersonalInfo.name']);
                  }

                  if('updatePersonalInfo.phone' in data){
                     var us = document.querySelectorAll("#personalInfo div.r2 input");
                     setErrorFor(us[2], data['updatePersonalInfo.phone']);
                 }

                 $('#personalInfo .fa-lg').css({'opacity':'0%'});
                 document.querySelector("#personalInfo input[type='submit']").removeAttribute('disabled');
              }
         });
    }
});