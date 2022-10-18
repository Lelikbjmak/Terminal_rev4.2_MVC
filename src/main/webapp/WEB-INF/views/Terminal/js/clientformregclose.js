
var win = document.getElementById('client');

var ov = document.getElementById('ovvv');

var userwin = document.getElementById('user');

const backButton = document.getElementById('prev');


ov.addEventListener('click', function(){

userwin.style.opacity = '100%';

win.style.opacity = '0%';
win.style.zIndex = '2';

ov.style.opacity = '0%';
ov.style.zIndex = '0';
});


backButton.addEventListener('click', function(){

const rr = document.getElementsByClassName('r2 success');

const rrr = document.getElementsByClassName('r2 error');

for (let r of rrr) {
      r.classList.remove('error');
    }

for (let r of rrr) {
      r.classList.remove('error');
    }

for (let r of rr) {
      r.classList.remove('success');
    }

for (let r of rr) {
      r.classList.remove('success');
    }


userwin.style.opacity = '100%';

win.style.opacity = '0%';
win.style.zIndex = '2';

ov.style.opacity = '0%';
ov.style.zIndex = '0';

$('div.message').text('');
$('div.message1.small').text('');

$('.wrap').css({'opacity':'0%', 'z-index':'0'});
$('.loader').css('opacity','100%');
$("#regg").trigger('reset');

});
