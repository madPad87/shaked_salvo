
function Authentication(){

    this.playerId = 0;

    this.init = function(){
        this.checkCurrentUser();
        this.loginViaSubmit();
        this.logOut();
        this.signUp();
        this.createGame();
    };

    this.checkCurrentUser = function(){
    var that = this;
        $.getJSON("/api/games", function(data){
          var playerName = data.player.name;
          that.playerId = data.player.id;
            if (playerName == null) {
                $("#myForm").show();
                $("#signUp").show();
            } else {
                $("#playerName").text(playerName);
                $("#playerName").show();
                $("#logOut").show();
            };
        });
    };

    this.cleanLogin = function(){
        var that = this;
         $.post("/api/login",
         {
         username: $("#username").val(),
         password: $("#password").val()
         }).done(function() {
           that.afterLoginActions();
        });
    }

    this.loginViaSubmit = function() {
    var that = this;
       $('#submit').on("click", function() {
              $.post("/api/login",
              {
              username: $("#username").val(),
              password: $("#password").val()
              }).done(function(){
                 that.afterLoginActions();
              })
              .fail(function(jqXHR, textStatus, errorThrown) {
                    $("#error").text("Oops! Wrong username/password.")
                $("#username").val("");
                $("#password").val("");
              });
       });
    };

    this.afterLoginActions = function(){
         $("#myForm").fadeOut("slow");
         $("#logOut").show();
         $("#signUp").hide();
         $("#allGames").html("");
         this.checkCurrentUser();
         dataHandler.getData();
    };

    this.logOut = function(){

        $("#logOut").on("click", function(){
            $.post("/api/logout")
            .done(function() {
                $("#playerName").hide();
                $("#logOut").hide();
                $("#username").val("");
                $("#password").val("");
                $("#myForm").show();
                $("#signUp").show();
                dataHandler.removeReturnToGameLink();
            });
        });
    };

    this.signUp = function(){
    var that = this;
        $("#signUp").on("click", function(){
            $.post("/api/players", {
                username: $("#username").val(),
                password: $("#password").val()
            })
            .done(function(){
                alert("You have successfully signed up!");
                that.cleanLogin();
                $("#signUp").hide();
            })
        });
    };

    this.createGame = function(){
        $("#createGame").on("click", function(){
            $.post("/api/games"
            )
            .done(function(data){
               location.href = "/web/private/game.html?gp=" + data.GPID;
               console.log(data);
            });
        });
    };
};

var authentication = new Authentication();
authentication.init();


function DataHandler() {

    this.playersData;
    this.gamesData;

    this.init = function(){
        this.getData();
    };

    this.getData = function(){
    var that = this;
        $.getJSON('../../api/leaderboard', function(data){
            that.playersData = data.players;
            that.appendPlayerStats();
        })
        $.getJSON("../../api/games", function(data){
            that.gamesData = data.games;
            that.appendGames();
        });
    };

    this.appendPlayerStats = function(){
        console.log(this.playersData);
        var table = $('#myTable');
            table.html("");
            var rows = [];
            var players = this.playersData;
            var playersWithScore = [];
            $.each(players, function(i,e){
                if (e.total) {
                    playersWithScore.push(e);
                }
               });
            playersWithScore.sort(function(a, b){
                    return b.total-a.total;
            })
            $.each(playersWithScore, function(i, e){
            if  (!e.total == 0) {
            var row = $('<tr>');
            row.append($('<td>').text(e.username));
            row.append($('<td>').text(e.total));
            row.append($('<td>').text(e.wins));
            row.append($('<td>').text(e.losses));
            row.append($('<td>').text(e.ties));
            rows.push(row);
            }
            });
            table.append(rows);
    };

    this.appendGames = function(){
    var that = this;
        var allGames = $("#allGames");
        $.each(this.gamesData, function(i, game){
            var date = new Date(game.created).toLocaleString();
            var li = $("<li>");
            var gpId = 0;
            var player1 = game.gamePlayers[0] ? game.gamePlayers[0].player.username : "Waiting for other player..";
            var player2 = game.gamePlayers[1] ? game.gamePlayers[1].player.username : "Waiting for other player..";
            var winner = "";
                $.each(game.gamePlayers, function(index, gp){
                    if  (gp.player.id == authentication.playerId) {
                    gpId = gp.id;
                    };
                    if  (gp.score !== null){
                        if (gp.score.score == 1) {
                            winner = "The winner is " + gp.player.username;
                        } else if (gp.score.score == 0.5) {
                            winner = "The game is a tie!";
                        }
                    } else {
                        winner = "Game in progress..";
                    }
                });
            allGames.append(li.text(date + " " + player1 + " VS " + player2 +" "+" "+ winner).addClass("list-group-item"));
            if  (game.gameOver == false && game.gamePlayers.length == 1){
                li.attr("data-game", game.id);
            };
            if (gpId !== 0 && game.gameOver == false) {
                li.attr("data-gp", gpId);
            };
        });
        this.createReturnToGameLink();
    };

    this.joinGameButton = function(gameid){
        $("#joinGame"+gameid).on("click", function(){
            $.post("/api/game/"+gameid+"/players")
            .done(function(data){
                location.href = "/web/private/game.html?gp=" + data.gpid;
            }).fail(function(){
                alert("Failed! Shit!");
            });
        });
    };

    this.createReturnToGameLink = function(){
        if (authentication.playerId !== 0){
            var that = this;
            $("#allGames li").each(function(i, li){
                var myGpId = $(this).attr("data-gp");
                var myGameId = $(this).attr("data-game");
                if ($(this).attr("data-gp")) {
                    $(this).append($("<a>").attr("href", "/web/private/game.html?gp=" + myGpId).text("Continue Game").addClass("btn btn-primary gameLink"));
                };
                if  (!$(this).attr("data-gp")){
                    if ($(this).attr("data-game")) {
                        $(this).append($("<button>").text("Join Game").addClass("btn btn-primary gameLink").attr("id", "joinGame" + myGameId));
                        that.joinGameButton(myGameId);
                    };
                };
            });
        };
    };

    this.removeReturnToGameLink = function(){
        $(".gameLink").remove();
    };
};

var dataHandler = new DataHandler();
dataHandler.init();
