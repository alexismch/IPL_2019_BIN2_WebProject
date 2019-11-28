$(document).ready(function(){
    $('title').html("Connexion");

    var mailExpr = '^[a-zA-Z0-9._]+@[a-zA-Z0-9.]+\.[a-z]+$';
    var mailRegex = new RegExp(mailExpr);
    var passwdExpr = '^.{2,}$';
    var passwdRegex = new RegExp(passwdExpr);
    var pseudoExpr = '^[a-zA-Z0-9]{2,}';
    var pseudoRegex = new RegExp(pseudoExpr);
    var nameExpr = '^[A-Za-z\ ]{2,}';
    var nameRegex = new RegExp(nameExpr);

    $(".veen .rgstr-btn button").click(function(){
        $('.veen .wrapper').addClass('move');
        $('.body-login').css('background','#ff59cd');
        $(".veen .login-btn button").removeClass('active');
        $(this).addClass('active');
        resetForm();
    });

    $(".veen .login-btn button").click(function(){
        $('.veen .wrapper').removeClass('move');
        $('.body-login').css('background','#ff4931');
        $(".veen .rgstr-btn button").removeClass('active');
        $(this).addClass('active');
        resetForm();
    });

    function resetForm() {
        $("form input").css("border-color", "#999");
        $("form input").val("");
    }

    $("form#login").submit(function (e) {
        e.preventDefault();
        var nbrOk = 0;

        var email = $("#login .mail input");
        if (!mailRegex.test(email.val()))
            email.css("border-color", "#ff4931");
        else {
            email.css("border-color", "#50c355");
            nbrOk++;
        }

        var passwd = $("#login .passwd input");
        if (!passwdRegex.test(passwd.val()))
            passwd.css("border-color", "#ff4931");
        else {
            passwd.css("border-color", "#50c355");
            nbrOk++;
        }

        if (nbrOk == 2) loginRequest(email.val(), passwd.val());
    });

    function loginRequest(email, passwd) {
        var data = "email=" + email + "&passwd=" + passwd;
        $.ajax({
            url : '/api/login',
            type : 'POST',
            data : data,
            dataType : 'json',
            timeout: 5000,
            success : function(response, statut) {
                if (response.success === "true" && statut === "success") {
                    successLogin(response.data);
                } else failedLogin("form#login", response.message);
            },
            error : function() {
                failedLogin("form#login", "Erreur de chargement.");
            }
        });
    }

    $("form#register").submit(function (e) {
        e.preventDefault();
        var nbrOk = 0;

        var name = $("#register .name input");
        if (!nameRegex.test(name.val()))
            name.css("border-color", "#ff4931");
        else {
            name.css("border-color", "#50c355");
            nbrOk++;
        }

        var email = $("#register .mail input");
        if (!mailRegex.test(email.val()))
            email.css("border-color", "#ff4931");
        else {
            email.css("border-color", "#50c355");
            nbrOk++;
        }

        var pseudo = $("#register .uid input");
        if (!nameRegex.test(pseudo.val()))
            pseudo.css("border-color", "#ff4931");
        else {
            pseudo.css("border-color", "#50c355");
            nbrOk++;
        }

        var passwd = $("#register .passwd input");
        if (!passwdRegex.test(passwd.val()))
            passwd.css("border-color", "#ff4931");
        else {
            passwd.css("border-color", "#50c355");
            nbrOk++;
        }

        if (nbrOk == 4) registerRequest(name.val(), email.val(), pseudo.val(), passwd.val());
    });

    function registerRequest(name, email, pseudo, passwd) {
        var data = "fullname=" + name + "&email=" + email + "&pseudo=" + pseudo + "&passwd=" + passwd;
        $.ajax({
            url : '/api/register',
            type : 'POST',
            data : data,
            dataType : 'json',
            timeout: 5000,
            success : function(response, statut) {
                if (response.success === "true" && statut === "success") {
                    successLogin(response.data);
                } else failedLogin("form#register", response.message);
            },
            error : function() {
                failedLogin("form#register", "Erreur de chargement.");
            }
        });
    }

    function successLogin(response) {
        localStorage.setItem("token", response.token);
        localStorage.setItem("user.pseudo", response.user.pseudo);
        localStorage.setItem("user.fullname", response.user.fullname);
        localStorage.setItem("user.email", response.user.email);

        $('.alert').fadeOut();
        $('.body-login').css('background','#50c355');
        $(".veen").css('margin-top', '300vh');
        setTimeout(function() {
                activeLoading();
                loadDashboard();
            },500);
    }

    function failedLogin(form, message) {
        $(form + " input").css("border-color", "#ff4931");
        failed(message);
    }
});