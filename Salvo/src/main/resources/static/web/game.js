$.getScript("public/particles.min.js", function(){
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


// Create class for handling all methods in a Object Oriented approach
function DataHandler() {

    //Initialize variables
    this.hits = [];
    this.gameState;
    this.shipsLeft;
    this.playerId;
    this.playerData;
    this.gamePlayerID;
    this.shipsLocations;
    this.mySalvoes;
    this.stats;

    this.myGrid = {
        0: "",
        1: "A",
        2: "B",
        3: "C",
        4: "D",
        5: "E",
        6: "F",
        7: "G",
        8: "H",
        9: "I",
        10: "J"
    }

    //Init function which initializes the first methods which will chain
    //on to the rest of the methods at the appropriate times.

    this.init = function() {
        this.createGrid("#container1", "grid1");
        this.createGrid("#container2", "grid2");
        this.getGameData();
    };

    //Method for retrieving data, and then manipulating accordingly in the CallBack function.

    this.getGameData = function() {
        var that = this;

        //Acquire my GamePlayer ID via the getParameterByName method, which uses Regular Expressions to sort out URL

        this.gamePlayerID = this.getParameterByName('gp');
        $.getJSON('/api/game_view/' + this.gamePlayerID, function(data) {
        // Saving data to variables
            that.gameState = data.gameState;
            that.shipsLeft = data.opponentSunk;
            that.mySalvoes = data.salvoes;
            that.shipsLocations = data.ships;
            that.playerData = data.gamePlayers;
            that.stats = data.stats;
        // Running other methods once data has been retrieved.
            that.getPlayerId();
            that.displayPlayerData();
            that.appendShips();
            that.checkHits();
            gameHandler.checkGameState();

            setTimeout(function() { that.getGameData() } , 3000);
        });
    };

    //Loop through hits and push to array, in order to map out HITS model on DOM correctly via appendHits().

    this.checkHits = function() {
        var hits = {
            Battleship : [],
            Destroyer : [],
            Submarine : [],
            PatrolBoat : [],
            Carrier : []
        }
        $('#shipsLeft').text("Enemy ships: " +this.shipsLeft);
        console.log(this.stats.You);
        $.each(this.stats.You, function(i,e){
            $.each(e, function(index,element){
                for (var x = 0; x < element; x++){
                    hits[index].push(index);
                };
            });
        });
        this.appendHits(hits.Battleship);
        this.appendHits(hits.Destroyer);
        this.appendHits(hits.Submarine);
        this.appendHits(hits.Carrier);
        this.appendHits(hits.PatrolBoat);
    };

    //Empty out DOM model, and reappend hits

    this.appendHits = function(hits){
        $.each($("#"+hits[0]+"Hit div"), function(index, div){
            div.textContent = "";
        });
        $.each(hits, function(i,e){
            $.each($("#"+e+"Hit div"), function(index, div){
                var hit = $(div);
                var text  = hit.text();
                if (text == "X"){
                    return true;
                }else {
                    hit.text("X");
                    return false;
                }
            });
        });
    };

    //Loop through GamePlayers, and compare GP ID to the URL param, if true, enter object and
    //assign player.id to variable, which will be later used to assign Salvoes to correct Player.

    this.getPlayerId = function(){
        var that = this;
        $.each(this.playerData, function(gpIndex, gp){
            if(gp.id == that.gamePlayerID) {
                that.playerId = gp.player.id;
            };
        });
    };

    //Loop through ships array and color grid cells accordingly

    this.appendShips = function(){
        $.each(this.shipsLocations, function(i, ship) {
            $.each(ship.locations, function(index, loc){
                $('#container1 .grid1.' +loc).addClass('ship'+ship.type).addClass('ship');
            });
        });
        this.colorSalvoes();
    };


    //Loop through Salvoes  and color grid cells accordingly with Turn number.
    //Inception shit going on here :O

    this.colorSalvoes = function(){
        var that = this;
        $.each(this.mySalvoes, function(index,object){
            if (that.playerId == index) {
                $.each(object, function(i,element){
                    $.each(element, function(number,salvo){
                        $("#container2 ." + salvo).css("background", "red").text(i);
                    });
                });
            } else {
                $.each(object, function(i,element){
                    $.each(element, function(number,salvo){
                        if  ($("#container1 ." + salvo).hasClass("ship")) {
                            $("#container1 ." + salvo).css("background", "red").text(i);
                        };
                    });
                });
            };
        });
    };

    //Method for getting GP ID

    this.getParameterByName = function(name, url) {
        if (!url) url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

    //Create nested for loops for creating columns and rows with divs, while attaching
    // classes needed for later identification of single Grid cells.

    this.createGrid = function(container, grid) {
        for (var rows = 0; rows < 11; rows++) {
            var div = $('<div>').addClass('column ' + rows)
            for (var columns = 0; columns < 11; columns++) {
                div.append($("<div>").addClass(grid).addClass('droppable'));
                $(container).append(div);
                if (rows == 0) {
                    $("." + grid).addClass('header').removeClass('droppable');
                };
            };
        };
        $("." + grid).width(500 / 11);
        $("." + grid).height(500 / 11);
        this.addLetters(container);
        this.addNumbers(container);

    };

    //Via the container parameter given in createGrid(), I insert numbers into the correct
    // grid cells for organizing the Grid, by taking each First() of each Child() of a column,
    //therefor the top row of every grid.

    this.addNumbers = function(container) {
        $(container + ' .column').each(function(i) {
            $(this).children().first().text(i);
        });
        $(container + ' .column').children().first().text("");
        this.giveParametersToMapClasses(container);
    }

    //Same idea as previous method, except using myGrid which is a object containing the needed letters
    //for the grid.

    this.addLetters = function(container) {
        var that = this;
        $(container + ' .header').each(function(i) {
            $(this).append($('<span>').text(that.myGrid[i]));
        });

    };

    //Simple for loop which uses the index number of the iteration and the container parameter
    //from the initial createGrid() method to run the mapID() method with correct arguments
    //for assigning classes to single grid cells, which will allows matching salvoes and ships later on.

    this.giveParametersToMapClasses = function(container) {
        var that = this;
        for (var i = 1; i <= 10; i++) {
            that.mapGridCellsClasses(container, i);
        };
    };

    //By taking the index number and container parameter from above method, I loop through all grid cells
    //and assign classes based on position.

    this.mapGridCellsClasses = function(container, columnClass) {
        var that = this;
        $(container + " ." + columnClass).children().each(function(i, e) {
            $(this).addClass(that.myGrid[i] + columnClass);
        });
    };

    //I loop through my two players, recognize via the URL param which is the one
    //playing, and append usernames to HTML.

    this.displayPlayerData = function() {
    var that = this;
        $.each(this.playerData, function(i, e) {
                        if (e.id == that.gamePlayerID) {
                            $('#player1').text(e.player.username + "(YOU)");
                        } else {
                            $('#player2').text(e.player.username);
                        };
        });
    };

};

var dataHandler = new DataHandler();
dataHandler.init();



//Initialize a new class for handling all game related.

function GameHandler () {

    //Init method for called appropriate methods in beginning.

    this.init = () => {
        this.placeShipsClickEvent();
        this.rotateShip();
        this.sendSalvoes();
        this.dragAndDrop();
    };

    //Local variables needed for gameplay
    this.turn;
    this.salvoes = [];
    this.Carrier = [];
    this.Battleship = [];
    this.Destroyer = [];
    this.Submarine = [];
    this.PatrolBoat = [];

    //Click event listener for sending ships to server.

    this.placeShipsClickEvent = () => {
        $('#postShips').on("click", () => {
            this.placeShips(this.Destroyer,this.Carrier,this.Battleship,this.Submarine,this.PatrolBoat);
        });
    };

    //Method for POST request to server, sending data of ship locations.

    this.placeShips = (destroyer,carrier,battleship,submarine,patrolboat) => {
        $.post({
           url: "/api/games/players/"+dataHandler.gamePlayerID+"/ships",
           data: JSON.stringify([ { "type": "Destroyer", "locations": destroyer },
                                  { "type": "Carrier", "locations": carrier },
                                  { "type": "Battleship", "locations": battleship },
                                  { "type": "Submarine", "locations": submarine },
                                  { "type": "PatrolBoat", "locations": patrolboat }
                                ]),
           dataType: "text",
           contentType: "application/json"
         }).done((response, status, jqXHR) => {
            dataHandler.getGameData();
            $('#myShips').hide();
            $('#postShips').hide();
        }).fail((jqXHR, status, httpError) => {
            alertify.error(jqXHR.responseText)
        });
    };

    //Method for modifying ship CSS in order to rotate it according to users choice.

    this.rotateShip = () => {
        $('.rotate').on('click', function() {
            var height = $(this).parent().css('height');
            var width = $(this).parent().css('width');
            $(this).parent().css('width', height);
            $(this).parent().css('height', width);
        });
    };

    //Method for calculating and saving locations of ships.

    this.calculateLocation = function(cell, shipId){
        var myLocations = [];
        var letter = cell[0];
        var number = cell[1];
        var height = $('#'+shipId).css('height');
        var position =  height.split("px")[0] < 50 ? "horizontal" : "vertical";
        var ship = shipId;
        var length = 0;
        var letterIndex = 0;
        var allLetters = ["A","B","C","D","E","F","G","H","I","J"];

        if (cell[1] == 0) {
            return alertify.error("Get your ship in a correct position!");
        };

        if (position == "horizontal" && cell[2] == 0){
            return alertify.error("Get your ship in a correct position!");
        };

        $.each(allLetters, function(i,e){
            if(e == letter) {
                letterIndex = i;
            };
        });

        switch (shipId) {
            case "Carrier":
                length = 5;
                break;
            case "Battleship":
                length = 4;
                break;
            case "Destroyer":
                length = 3;
                break;
            case "Submarine":
                length = 3;
                break;
            case "PatrolBoat":
                length = 2;
                break;
        }
            if (position == "vertical"  && cell[2] == 0) {
                for (var i = 0; i < length; i++) {
                    myLocations.push(allLetters[letterIndex] + number + 0)
                    letterIndex++;
                };
            } else  if (position == "horizontal") {
                var x = 0;
                for (var i = 0; i < length; i++) {
                   myLocations.push(letter + number)
                   number++;
                };
            } else {
                for (var i = 0; i < length; i++) {
                   myLocations.push(allLetters[letterIndex] + number)
                   letterIndex++;
                };
            };

           this[shipId] = myLocations;
    };

    //On click of different cells in salvo grid, mark them white and save cell location to local variable.
    //+Safe guards against faulty placements.

    this.setSalvoes = function(){
        var that = this;
            $('.grid2').on('click', function(){
                var location = $(this)[0].classList[2];
                var color = $(this).css('background-color');
                if (color == 'rgb(255, 0, 0)') {
                    alertify.error('Youve already tried there!');
                    return;
                } else if (color == "rgb(255, 255, 255)"){
                    $(this).toggleClass('toggleSalvo');
                    that.salvoes.pop();
                    return;
                } else if (that.salvoes.length == 2) {
                    alertify.error("That's it! 2 per turn!");
                    return;
                } else {
                    that.salvoes.push(location);
                    $(this).toggleClass('toggleSalvo');
                };
            });
        };

    //Send POST request to server with salvo locations.
    //On done: empty out array and reload game data;

    this.sendSalvoes = function(){
        var that = this;
        $('#sendSalvoes').on('click', function(){
            if(that.salvoes.length !== 2){
              return alertify.error("You must place both salvoes before shooting!");
            };
            that.calculateTurn();
            $.post({
            url: "/api/games/players/"+dataHandler.gamePlayerID+"/salvos",
            data: JSON.stringify( { "salvoLocations": that.salvoes, "turn": that.turn}
                                   ),
            dataType: "text",
            contentType: "application/json"
            }).done((response, status, jqXHR) => {
               that.salvoes = [];
               if (dataHandler.salvoes == undefined){
                   that.turn = 2;
               }
               dataHandler.getGameData();
            }).fail((jqXHR, status, httpError) => {
               alertify.error(jqXHR.responseText)
            });
        });
    };

    //Check length of salvoes object, and since the key is a turn, add +1 to that for turn progression.

    this.calculateTurn = function(){
        if (!$.isEmptyObject(dataHandler.mySalvoes[dataHandler.playerId])){
            var turns = Object.keys(dataHandler.mySalvoes[dataHandler.playerId]).length;
            this.turn = turns + 1;
        } else {
            this.turn = 1;
        }
    };

    //Check gamestate from JSON, and modify display accordingly.

    this.stateConfig = {
        noOpponent : {
            text : "Waiting for other player to join..",
            extra : () => {
                if ($('.ship')){
                    $('#postShips').hide();
                }
            }
        },

        placeShips : {
            text : "Place your ships!"
        },

        opponentPlaceShips : {
            text : "Waiting for other player to place ships..",
            extra : () => {
                $('#postShips').hide();
                $('#myShips').hide();
            }
        },

        myTurn : {
            text : "Your turn!",
            extra : () => {
                $('#shipsLeft').show();
                $('.grid2').unbind('click');
                this.setSalvoes();
                $('#salvoesDiv').fadeIn();
                $('#sendSalvoes').show();
                $('#hitShips').show();
                $('#myShips').hide();
                $('#postShips').hide();
            }
        },

        opponentTurn : {
            text : "Waiting for opponent to shoot...",
            extra : () => {
                $('.grid2').unbind('click');
                $('#shipsLeft').show();
                $('#hitShips').show();
                $('#sendSalvoes').hide();
                $('#myShips').hide();
                $('#salvoesDiv').fadeIn();
                $('#postShips').hide();
            }
        },

        gameOver : {
            text : "",
            extra : () => {
                $('#salvoesDiv').hide();
                $('#shipsDiv').hide();
                $('#setSalvoes').hide();
                $('#myShips').hide();
                $('#sendSalvoes').hide();
                var text = dataHandler.shipsLeft == 0 ? "You won!" : "You lost!";
                $('#gameState').text(text);
            }
        }
    };

    //According to stateConfig, show and hide game controls.

    this.checkGameState = function(){
        var conf = this.stateConfig[dataHandler.gameState];
        $('#gameState').text(conf.text)
        if(conf.extra){
            conf.extra();
        };
    };

    //Drag and Drop by JQuery UI.

    this.dragAndDrop = function() {
        var that = this;
        $('.draggable').draggable({
            snap: '.grid1'
        });
        $("#container1").droppable({
        drop: function(event, ui) {
            var grid = $('#container1').offset();
            var x = ui.offset.left;
            var y = ui.offset.top;
            var row = Math.floor((y - grid.top) / 45.45);
            var col = Math.floor((x - grid.left) / 45.45);
            if (row == 0){
            return alertify.error("Get your ship in correct position!");
            }
            var letter = String.fromCharCode(row + 64);
            var shipId = ui.draggable.attr('id');
            var cell = letter+col.toString();
            that.calculateLocation(cell, shipId);
            }
        });
    };
};

var gameHandler = new GameHandler();
gameHandler.init();



//Initialize a new class for handling Navigation

function Navigation() {

    this.init = function(){
        this.exitGame();
        this.logOut();
    }

    //Method for logging out while inside a specific game.
    //Hiding and showing relevant items.

    this.logOut = function(){
    $("#logOutGame").on("click", function(){
                $.post("/api/logout")
                .done(function() {
                window.location.href = "/web/public/index.html";
                    $("#currentUser").hide();
                    $("#logOut").hide();
                    $("#username").val("");
                    $("#password").val("");
                    $("#loginContainer").fadeIn();
                });
            });
    }

    //Return to main menu via changing of HREF

    this.exitGame = () => {
        $('#backToMenu').on('click', () => {
            console.log('hey')
            location.href = "/web/public/index.html";
        });
    };
};

var navigation = new Navigation();
navigation.init();

