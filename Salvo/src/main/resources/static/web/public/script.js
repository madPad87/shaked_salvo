$.getScript("particles.min.js", function(){
    particlesJS('particles-js',
      {
        "particles": {
          "number": {
            "value": 80,
            "density": {
              "enable": true,
              "value_area": 800
            }
          },
          "color": {
            "value": "#ffffff"
          },
          "shape": {
            "type": "circle",
            "stroke": {
              "width": 0,
              "color": "#000000"
            },
            "polygon": {
              "nb_sides": 5
            },
            "image": {
              "width": 100,
              "height": 100
            }
          },
          "opacity": {
            "value": 0.5,
            "random": false,
            "anim": {
              "enable": false,
              "speed": 1,
              "opacity_min": 0.1,
              "sync": false
            }
          },
          "size": {
            "value": 5,
            "random": true,
            "anim": {
              "enable": false,
              "speed": 40,
              "size_min": 0.1,
              "sync": false
            }
          },
          "line_linked": {
            "enable": true,
            "distance": 150,
            "color": "#ffffff",
            "opacity": 0.4,
            "width": 1
          },
          "move": {
            "enable": true,
            "speed": 6,
            "direction": "none",
            "random": false,
            "straight": false,
            "out_mode": "out",
            "attract": {
              "enable": false,
              "rotateX": 600,
              "rotateY": 1200
            }
          }
        },
        "interactivity": {
          "detect_on": "canvas",
          "events": {
            "onhover": {
              "enable": true,
              "mode": "repulse"
            },
            "onclick": {
              "enable": true,
              "mode": "push"
            },
            "resize": true
          },
          "modes": {
            "grab": {
              "distance": 400,
              "line_linked": {
                "opacity": 1
              }
            },
            "bubble": {
              "distance": 400,
              "size": 40,
              "duration": 2,
              "opacity": 8,
              "speed": 3
            },
            "repulse": {
              "distance": 200
            },
            "push": {
              "particles_nb": 4
            },
            "remove": {
              "particles_nb": 2
            }
          }
        },
        "retina_detect": true,
        "config_demo": {
          "hide_card": false,
          "background_color": "#b61924",
          "background_image": "",
          "background_position": "50% 50%",
          "background_repeat": "no-repeat",
          "background_size": "cover"
        }
      }
    );

});

//Initialize new class for handling Authentication

function Authentication(){

    //Initialize variables

    this.playerId = 0;

    //Initiation method for running appropriate methods on start.

    this.init = function(){
        this.checkCurrentUser();
        this.loginViaSubmit();
        this.logOut();
        this.signUp();
    };

    //Get data from API and check user info, show it on DOM and show appropriate elements.

    this.checkCurrentUser = function(){
    var that = this;
        $.getJSON("/api/games", function(data){
          var playerName = data.player.name;
          that.playerId = data.player.id;
                $("#currentUser").text(playerName);
                $("#currentUser").show("slow");
                $("#logOut").show("slow");
                $("#menu").fadeIn();
                $('#header').fadeIn();
        }).fail(function(){
             $("#loginContainer").show("slow");
        });
    };

    //A pure login method via a HTTP POST request to the server.

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

    //Same method as before except it runs on click event from SUBMIT button.

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

    //Several actions to be taken once a user has authenticated (showing and hiding DOM elements).

    this.afterLoginActions = function(){
         $("#loginContainer").hide("slow");
         $("#logOut").show();
         $("#menu").fadeIn();
         $("#allGames").html("");
         this.checkCurrentUser();
         dataHandler.getData();
    };

    //POST request for logging out of server, and showing/hiding DOM elements.

    this.logOut = function(){
        $("#logOut").on("click", function(){
            $.post("/api/logout")
            .done(function() {
                $("#menu").hide("slow");
                navigation.hideAll();
                $("#username").val("");
                $("#password").val("");
                $("#loginContainer").show("slow");
                $('#error').text("");
                dataHandler.removeReturnToGameLink();
            });
        });
    };

    //POST request for registering new user to the DATABASE, and immediately logging him in.

    this.signUp = function(){
    var that = this;
        $("#signUp").on("click", function(){
            $.post("/api/players", {
                username: $("#username").val(),
                password: $("#password").val()
            })
            .done(function(){
                that.cleanLogin();
            })
        });
    };

};

var authentication = new Authentication();
authentication.init();


//Initialize new class for handling data

function DataHandler() {

    //Variables

    this.playersData;
    this.gamesData;

    //Init method for starting up all relevant methods.

    this.init = function(){
        this.getData();
    };

    //HTTP GET request to server for the data, and calllback function running appropriate methods.

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

    //With the data recieved, loop through it, check for scores (If there arent any no need to show on leaderboard),
    //and append to DOM element.

    this.appendPlayerStats = function(){
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

    //Method for building raw list of all games, later to be sorted to two different DOM elements.
    //Data attributes are given in order to make it possible to recognize which games are related to which players,
    //and to give controls to join/continue these games. User data is given from Authentication class.

    this.appendGames = function(){
    var that = this;
        var allGames = $("#allGames");
        allGames.html("");
        $.each(this.gamesData, function(i, game){
            var li = $("<li>");
            var gpId = 0;
            var player1 = game.gamePlayers[0] ? game.gamePlayers[0].player.username : "...";
            var player2 = game.gamePlayers[1] ? game.gamePlayers[1].player.username : "...";
            var winner = "";
                $.each(game.gamePlayers, function(index, gp){
                    if  (gp.player.id == authentication.playerId) {
                    gpId = gp.id;
                     }
            });
            allGames.append(li.text(player1 + " " + "VS" +  " " +player2).append("<br>"));
            if  (game.gameOver == false && game.gamePlayers.length == 1){
                li.attr("data-game", game.id);
            };
            if (gpId !== 0 && game.gameOver == false)  {
                li.attr("data-gp", gpId);
            };
        });
        this.createReturnToGameLink();
    };


    //Thanks to the previously given data attributes, i can now choose which button needs to be attached to which game,
    //according to which user is currently logged in.

    this.createReturnToGameLink = function(){
        if (authentication.playerId !== 0){
            var that = this;
            $("#allGames li").each(function(i, li){
                var myGpId = $(this).attr("data-gp");
                var myGameId = $(this).attr("data-game");
                if ($(this).attr("data-gp")) {
                    $(this).append($("<a>").attr("href", "/web/game.html?gp=" + myGpId).text("Continue").addClass("button gameLink"));
                }
                if  (!$(this).attr("data-gp")){
                    if ($(this).attr("data-game")) {
                        $(this).append($("<button>").text("Join").addClass("button gameLink").attr("id", "joinGame" + myGameId));
                        that.joinGameButton(myGameId);
                    };
                };
            });
        };
    };

    //POST request for telling the server that a player is connecting to a specific game,
    //also thanks to the data attributes.

    this.joinGameButton = function(gameid){
            $("#joinGame"+gameid).on("click", function(){
                $.post("/api/game/"+gameid+"/players")
                .done(function(data){
                    location.href = "/web/game.html?gp=" + data.gpid;
                }).fail(function(){
                    alert("Failed! Shit!");
                });
            });
        };

    //Remove button if user logs out.

    this.removeReturnToGameLink = function(){
        $(".gameLink").remove();
    };
};

var dataHandler = new DataHandler();
dataHandler.init();


//Initialize new class for handling navigation

function Navigation() {

    //Init method for running methods

    this.init = function(){
        this.newGame();
        this.returnGame();
        this.joinGame();
        this.leaderBoard();
        this.backToMenu();
    };

    //HTTP POST request for creating a new game in the backend, and the changing HREF to specific game.

    this.newGame = function(){
        $("#newGame").on("click", function(){
            $.post("/api/games")
            .done(function(data){
                location.href = "/web/game.html?gp=" + data.GPID;
                console.log(data);
            });
        });
    };

    //Show Leaderboard DOM element and hide others.

    this.leaderBoard = function(){
        $('#leaderBoard').on('click', function(){
            $('#menu').fadeOut();
            $('#statsContainer').fadeIn();
            $('#backToMenu').fadeIn();
        });
    };

    //Show menu DOM element and hide others.

    this.backToMenu = function(){
        $('#backToMenu').on('click', function(){
            $('#statsContainer').hide();
            $('#joinGameContainer').hide();
            $('#backToMenu').hide("slow");
            $('#returnGameContainer').hide("slow");
            $('#menu').fadeIn();
        });
    };

    //Loop through previously made list of all games, and according to data attributes i can sort to only show the games
    //which the currentUser can join.
    // ---Show list of games (If exist) which currentUser can join

    this.joinGame = function(){
        var that = this;
        $('#joinGame').on('click', function(){
            $.each($('#allGames li'), function(i,e){
                if(!$(this).attr('data-gp')){
                    if ($(this).attr('data-game')){
                        $('#joinGames').append($(this));
                    };
                };
            });
            $('#menu').hide();
            $('#joinGameContainer').fadeIn();
            $('#backToMenu').show("slow");
        });
    };

    //Same as previous method, except its for games which the player has already begun but
    //has'nt finished.

    this.returnGame = function(){
        var that = this;
        $('#continue').on('click', function(){
            $.each($('#allGames li'), function(i,e){
                if  ($(this).attr('data-gp')){
                    $('#returnGames').append($(this));
                }
            });
            $('#menu').hide();
            $('#returnGameContainer').fadeIn();
            $('#backToMenu').fadeIn();
        });
    };

    //Method for hiding serveral DOM elements.

    this.hideAll = function(){
        $('#joinGameContainer').hide();
        $('#statsContainer').hide();
        $('#header').hide();
        $('#joinGames').html("");
        $('#returnGames').html("");
    };

};

var navigation = new Navigation();
navigation.init();