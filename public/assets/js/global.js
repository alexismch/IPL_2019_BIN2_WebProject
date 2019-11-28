let token=undefined;

const initialRenderOfComponents = function (){
    token = localStorage.getItem("token");
    if (token) {
        return token;
    }
    else if (window.location.pathname !== "/login") {
        var data = "type=json";
        var url = '/login';
        $.ajax({
            url : url,
            type : 'GET',
            data : data,
            dataType : 'html',
            timeout: 5000,
            success : function(response, statut) {
                appendPage(url, response);
            },
            error : function() {
                initialRenderOfComponents();
            }
        });
    } else {
        exitLoading();
    }
};

function failed(message) {
    $('.alert span.message').html(message);
    $('.alert').fadeIn();
}

function appendPage(url, response) {
    $("body").append(response);
    window.history.pushState({"pageUrl":window.location.hostname + "/login", "pageTitle":"Connexion"},"", url);
    exitLoading();
}

function exitLoading() {
    $("rect").css("transition", "1s ease").css("transform", "translateY(50px)");
    $(".body-loader").delay(600).fadeOut();
}

function activeLoading() {
    $("rect").attr("style", "");
    $(".body-loader").fadeIn();
}

$(document).ready(function () {
    token = initialRenderOfComponents();
    $(".alert button.close").click(function () {
        $('.alert').fadeOut();
    });
});