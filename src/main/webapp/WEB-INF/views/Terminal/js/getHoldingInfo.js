const holdRef = document.getElementById('holdingCondition');
const currentHold = document.getElementById('currHold');

holdRef.addEventListener('click', function() {

var time1 = document.getElementById('currtime1');
var date1 = document.getElementById('currdate1');

function getcurrtime(){
return new Date().toTimeString().replace(/ .*/, '');
}

function getCurrentDate(){
return new Date().toDateString();
}

    date1.innerHTML = getCurrentDate();
    time1.innerHTML = getcurrtime();

    setInterval(
          () => time1.innerHTML = getcurrtime(),
          1000
    );

    setInterval(
          () => date1.innerHTML = getCurrentDate(),
          1000 * 60 * 60 * 24
    );

var holdId = currentHold.value.slice(0,-6);


    $.post("/Barclays/service/holdings/holdingInfo", {
        holdingId: holdId
    }, function(data) {
     }).done(function(data, textStatus, jqXHR){

         $('#holdInfo').text(data.message);
         $('#holdInfo').html($('#holdInfo').html().replace(/\n/g,'<br/>'));

    }).fail(function(jqXHR, exception, textStatus, errorThrown) {
        });

var buts = document.querySelectorAll('.dws-menu');
var header = document.querySelectorAll('.menuaut1');
const ov = document.getElementById('ov');
const hld = document.getElementById('hld');


    for(var i = 0; i < header.length; i++){
    header[i].style.zIndex = '0';
    }


    for(var i = 0; i < buts.length; i++){
    buts[i].style.zIndex = '0';

    }

    ov.style.opacity = '100%';
    ov.style.zIndex = '8';

    hld.style.opacity = '100%';
    hld.style.zIndex = '10';

});


function getInfAboutHold(){

var holdId = currentHold.value.slice(0,-6);


    $.post("/Barclays/service/holdings/holdingInfo", {
        holdingId: holdId
    }, function(data) {
     }).done(function(data, textStatus, jqXHR){

         $('#holdInfo').text(data.message);
         $('#holdInfo').html($('#holdInfo').html().replace(/\n/g,'<br/>'));

    }).fail(function(jqXHR, exception, textStatus, errorThrown) {
        });
}