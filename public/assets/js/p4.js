var player = 0;
var advers = "";
var winner = "";
var enCours = false;

$(document).ready(function () {
    $('title').html("puissance 4");

    $("#disconnect").click(function (e) {
        deleteGame(nomPartie);
        e.preventDefault();
    });
    $(".spa-body").append("<div class=\"alert alert-warning\" id=\"game-info-alert\" style=\"z-index: 9996;\" role=\"alert\">\n" +
        "  En attente d'un adversaire...\n" +
        "</div>");
    wait("player");
});

function ready() {
    enCours = true;
    if (me == 1) wait();
    $('#game table tr td').click(function (e) {
        if (player !== me || winner !== "") return;
        var col = this.getAttribute("data-column");
        if (set(col)) {
            player = (player === 1) ? 0 : 1;
            push(col);
        }
    });
}

function set(colonne) {
    var isOk = false;
    for (var i = 5; i >= 0; i--) {
        var row = $('#game table tr[data-row=' + i + '] td[data-column=' + colonne + ']');
        if (row.css("background-color") === "rgb(255, 255, 255)") {
            row.css("background-color", (player == 1) ? "yellow" : "red");
            isOk = true;
            var win = verifWin();

            if (win !== false)
                gotWinner(win);
            break;
        }
    }
    if (!isOk)
        alert("Choisissez une autre colonne");
    return isOk;
}

function gotWinner(player) {
    enCours = false;
    winner = (player === me) ? localStorage.getItem("user.pseudo") : advers;
    if (winner === localStorage.getItem("user.pseudo")) {
        var data = "gagnant=" + winner + "&token=" + localStorage.getItem("token") + "&perdant=" + advers;
        $.ajax({
            url : '/api/historic',
            type : 'POST',
            data : data,
            dataType : 'json',
            timeout: 5000
        });
    } else {
        deleteGame(nomPartie);
    }
    $(".spa-body").prepend('<canvas id="canvas" style="display: block; position: fixed; width: 100%; height: 100%; z-index: 9997; background: rgba(0, 0, 0, 0.7);"></canvas><script src="./assets/js/firework.min.js"></script>');
    $(".spa-body").prepend("<button type=\"button\" class=\"btn btn-outline-primary\" id=\"winner-user-button\" data-toggle=\"modal\" data-target=\"#winner-user-modal\" hidden></button>\n" +
        "<div class=\"modal\" data-backdrop=\"static\" id=\"winner-user-modal\" style=\"z-index: 9998;\" tabindex=\"-1\" role=\"dialog\">\n" +
        "  <div class=\"modal-dialog\" role=\"document\">\n" +
        "    <div class=\"modal-content\">\n" +
        "      <div class=\"modal-header\">\n" +
        "        <h5 class=\"modal-title\">Nous avons un gagnant</h5>\n" +
        "      </div>\n" +
        "      <div class=\"modal-body\">\n" +
        "        <p>" + winner + " a gagné !</p>\n" +
        "      </div>\n" +
        "      <div class=\"modal-footer\">\n" +
        "        <button type=\"button\" id=\"exit-winner\" class=\"btn btn-primary\" data-dismiss=\"modal\">Retour</button>\n" +
        "      </div>\n" +
        "    </div>\n" +
        "  </div>\n" +
        "</div>");
    $( "#winner-user-button" ).trigger( "click" );

    $('#exit-winner').click(function () {
        activeLoadDisconnected();
    });
}

function push(colonne) {
    var data = "nomPartie=" + nomPartie + "&token=" + localStorage.getItem("token") + "&pseudo=" + localStorage.getItem("user.pseudo") + "&colonne=" + colonne;
    $.ajax({
        url : '/api/playingGame',
        type : 'POST',
        data : data,
        dataType : 'json',
        timeout: 5000,
        success : function(response, statut) {
            wait();
        },
        error : function () {
            disconnectedAdvers();
        }
    });
}

function wait(type) {
    var data = "nomPartie=" + nomPartie + "&token=" + localStorage.getItem("token");
    if (type !== "player")
        $('#game-info-alert').html("C'est à l'adversaire de jouer...").removeClass("alert-success").addClass("alert-warning");
    $.ajax({
        url : '/api/playingGame',
        type : 'GET',
        data : data,
        dataType : 'json',
        timeout: 5000,
        success : function(response, statut) {
            if (response.success === "true" && statut === "success") {
                if (type === "player") {
                    if (response.data.joueur2 !== "") {
                        advers = (me == 0) ? response.data.joueur2 : response.data.joueur1;
                        $('#game-info-alert').html("C'est à vous de jouer...").removeClass("alert-warning").addClass("alert-success");
                        ready();
                    }
                } else {
                    if (response.data.dernierJoueur !== "" && localStorage.getItem("user.pseudo") !== response.data.dernierJoueur) {
                        set(response.data.colonne);
                        $('#game-info-alert').html("C'est à vous de jouer...").removeClass("alert-warning").addClass("alert-success");
                        player = me;
                    }
                }
            } else {
                disconnectedAdvers();
            }
        },
        error : function () {
            disconnectedAdvers();
        }
    });
    setTimeout(function() {
        if (winner !== "" || localStorage.getItem("token") == null || localStorage.getItem("token") === "null") return;
        if (advers === "") {
            wait("player");
            return;
        }
        if (type !== "player" && me !== player) wait();
    },5000);
}

function verifWin() {
    var verif;
    verif = verifC();
    if (verif !== false) return verif;

    verif = verifR();
    if (verif !== false) return verif;

    verif = verifDD();
    if (verif !== false) return verif;

    verif = verifDG();
    if (verif !== false) return verif;

    return false;
}

function verifC() {
    for (var i = 0; i <= 6; i++) {
        var count = 0;
        var color = "";
        for (var j = 0; j <= 5; j++) {
            var row = $('#game table tr[data-row=' + j + '] td[data-column=' + i + ']');
            if (row.css("background-color") !== "rgb(255, 255, 255)") {
                if (color !== row.css("background-color")) {
                    count = 1;
                    color = row.css("background-color");
                } else {
                    count++;
                }
            }
            if (count >= 4)
                return (color === "rgb(255, 0, 0)") ? 0 : 1;
        }
    }
    return false;
}

function verifR() {
    for (var i = 0; i <= 5; i++) {
        var count = 0;
        var color = "";
        for (var j = 0; j <= 6; j++) {
            var row = $('#game table tr[data-row=' + i + '] td[data-column=' + j + ']');
            if (row.css("background-color") !== "rgb(255, 255, 255)") {
                if (color !== row.css("background-color")) {
                    count = 1;
                    color = row.css("background-color");
                } else {
                    count++;
                }
            }
            if (count >= 4)
                return (color === "rgb(255, 0, 0)") ? 0 : 1;
        }
    }
    return false;
}

function verifDD() {
    var i, j, k, l, count, color, row;
    for (i = 0; i <= 3; i++) {
        if (i === 0) {
            for (j = 3; j <= 5; j++) {
                l = 0;
                count = 0;
                color = "";
                for (k = j; k >= 0; k--) {
                    row = $('#game table tr[data-row=' + k + '] td[data-column=' + l + ']');
                    if (row.css("background-color") !== "rgb(255, 255, 255)") {
                        if (color !== row.css("background-color")) {
                            count = 1;
                            color = row.css("background-color");
                        } else
                            count++;
                    }
                    if (count >= 4)
                        return (color === "rgb(255, 0, 0)") ? 0 : 1;
                    l++;
                }
            }
        } else {
            k = 5;
            count = 0;
            color = "";
            for (j = i; j <= 6; j++) {
                row = $('#game table tr[data-row=' + k + '] td[data-column=' + j + ']');
                if (row.css("background-color") !== "rgb(255, 255, 255)") {
                    if (color !== row.css("background-color")) {
                        count = 1;
                        color = row.css("background-color");
                    } else
                        count++;
                }
                if (count >= 4)
                    return (color === "rgb(255, 0, 0)") ? 0 : 1;
                k--;
            }
        }
    }
    return false;
}

function verifDG() {
    var i, j, k, l, count, color, row;
    for (i = 0; i <= 3; i++) {
        if (i === 0) {
            for (j = 3; j > 0; j--) {
                l = 0;
                count = 0;
                color = "";
                for (k = j; k <= 6; k++) {
                    row = $('#game table tr[data-row=' + l + '] td[data-column=' + k + ']');
                    if (row.css("background-color") !== "rgb(255, 255, 255)") {
                        if (color !== row.css("background-color")) {
                            count = 1;
                            color = row.css("background-color");
                        } else
                            count++;
                    }
                    if (count >= 4)
                        return (color === "rgb(255, 0, 0)") ? 0 : 1;
                    l++;
                }
            }
        } else {
            k = 5;
            count = 0;
            color = "";
            for (j = k-i+1; j >= 0; j--) {
                row = $('#game table tr[data-row=' + k + '] td[data-column=' + j + ']');
                if (row.css("background-color") !== "rgb(255, 255, 255)") {
                    if (color !== row.css("background-color")) {
                        count = 1;
                        color = row.css("background-color");
                    } else
                        count++;
                }
                if (count >= 4)
                    return (color === "rgb(255, 0, 0)") ? 0 : 1;
                k--;
            }
        }
    }
    return false;
}

window.onbeforeunload = function(){
    deleteGame(nomPartie);
};

function deleteGame(nomPartieD) {
    $.ajax({
        url : '/api/playingGame?nomPartie=' + nomPartieD,
        type : 'DELETE',
        dataType : 'json',
        timeout: 5000
    });
}

function disconnectedAdvers() {
    winner = ".";
    $(".spa-body").prepend("<button type=\"button\" class=\"btn btn-outline-primary\" id=\"disconnected-user-button\" data-toggle=\"modal\" data-target=\"#disconnected-user-modal\" hidden></button>\n" +
        "<div class=\"modal\" data-backdrop=\"static\" id=\"disconnected-user-modal\" tabindex=\"-1\" role=\"dialog\">\n" +
        "  <div class=\"modal-dialog\" role=\"document\">\n" +
        "    <div class=\"modal-content\">\n" +
        "      <div class=\"modal-header\">\n" +
        "        <h5 class=\"modal-title\">Erreur de partie</h5>\n" +
        "      </div>\n" +
        "      <div class=\"modal-body\">\n" +
        "        <p>L'adversaire s'est déconnecté.</p>\n" +
        "      </div>\n" +
        "      <div class=\"modal-footer\">\n" +
        "        <button type=\"button\" id=\"exit-disconnected-error\" class=\"btn btn-primary\" data-dismiss=\"modal\">Retour</button>\n" +
        "      </div>\n" +
        "    </div>\n" +
        "  </div>\n" +
        "</div>");
    $( "#disconnected-user-button" ).trigger( "click" );

    $('#exit-disconnected-error').click(function () {
        activeLoadDisconnected();
    });
}

function activeLoadDisconnected() {
    activeLoading();
    setTimeout(function() {
        loadDashboard();
    },500);
}