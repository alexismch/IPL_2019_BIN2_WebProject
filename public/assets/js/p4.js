var player = 0;
const me = 0;
var advers = "";
var nomPartie = "Salut";
var nbCouts = 0;

$(document).ready(function () {
    ready();
});

function ready() {
    $('#game table tr td').click(function (e) {
        if (player !== me) return;
        var col = this.getAttribute("data-column");
        var isOk = set(col);
        if (isOk) {
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
            nbCouts++;
            isOk = true;
            break;
        }
    }
    if (!isOk)
        alert("Choisissez une autre colonne");
    return isOk;
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
            console.log(response);
            wait();
        }
    });
}

function wait() {
    var data = "nomPartie=" + nomPartie + "&token=" + localStorage.getItem("token");
    $.ajax({
        url : '/api/playingGame',
        type : 'GET',
        data : data,
        dataType : 'json',
        timeout: 5000,
        success : function(response, statut) {
            if (response.success === "true" && statut === "success") {
                if (localStorage.getItem("user.pseudo") !== response.data.dernierJoueur) {
                    set(response.data.colonne);
                    player = me;
                }
            }
        }
    });
    setTimeout(function() {
        if (me !== player && localStorage.getItem("token") != null && localStorage.getItem("token") !== "null") wait();
    },5000);
}