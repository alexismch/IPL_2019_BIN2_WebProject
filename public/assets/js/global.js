let token=undefined;

const initialRenderOfComponents = function (){
    token = localStorage.getItem("token");
    if (token) {
        if (window.location.pathname === "/login" || window.location.pathname === "/") {
            loadDashboard();
        } else exitLoading();
        return token;
    }
    else if (window.location.pathname !== "/login") {
        loadLogin()
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
    window.history.pushState({"pageUrl":window.location.hostname + url},"", url);
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

function loadDashboard() {
    if (token == null || window.location.pathname === "/dashboard")
        return;
    var data = "type=json&token=" + token;
    var url = '/dashboard';
    $.ajax({
        url : url,
        type : 'GET',
        data : data,
        dataType : 'html',
        timeout: 5000,
        success : function(response, statut) {
            $(".spa-body").remove();
            appendPage(url, response);
        },
        error : function(response) {
            console.log(response.responseText);
        }
    });
}

function disconnect() {
    activeLoading();
    localStorage.removeItem("token");
    localStorage.removeItem("user.fullname");
    localStorage.removeItem("user.pseudo");
    localStorage.removeItem("user.email");
    token = undefined;
    setTimeout(function() {
        loadLogin();
    },500);
}

function loadLogin() {
    if (token != null || window.location.pathname === "/login")
        return;
    $(".spa-body").remove();
    $(".navbar").remove();
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
}

$(document).ready(function () {
    token = initialRenderOfComponents();
    $(".alert button.close").click(function () {
        $('.alert').fadeOut();
    });
});