let token=undefined;
const initialRenderOfComponents = function (){
    token = localStorage.getItem("token");
    if (token) {
        return token;
    }
    else {
        return;
    }
};

function failed(message) {
    $('.alert span.message').html(message);
    $('.alert').fadeIn();
}

$(document).ready(function () {
    token = initialRenderOfComponents();
    $(".alert button.close").click(function () {
        $('.alert').fadeOut();
    });
});