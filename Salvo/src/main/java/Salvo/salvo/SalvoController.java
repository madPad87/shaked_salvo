package Salvo.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;


//Request mapping allows for me to map certain URLs to certain methods
@CrossOrigin
@RequestMapping("/api")

//Rest controller is a JSON web service which returns DATA

@RestController
public class SalvoController {

    //Autowired - import repositories im going to use

    @Autowired
    ScoreRepository scoreRepository;

    @Autowired
    SalvoRepository salvoRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GamePlayerRepository gamePlayerRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    ShipRepository shipRepository;

    @RequestMapping("/games")
    public Map<String, Object> getAllGames() {
        Map<String, Object> playerAndGames = new LinkedHashMap<>();
        Map<String, Object> currentPlayerDTO = new LinkedHashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Player currentPlayer = getCurrentPlayer(authentication);
        currentPlayerDTO.put("id", currentPlayer.getId());
        currentPlayerDTO.put("name", currentPlayer.getUserName());

        List<Object> allGames = gameRepository.findAll().stream().map(game -> this.makeGamesDTO(game)).collect(Collectors.toList());

        playerAndGames.put("player", currentPlayerDTO);
        playerAndGames.put("games", allGames);
        return playerAndGames;
    }

    //Via DTO's - Data Transfer Objects, i can structure the way i want my data returned to me, via either
    //Maps, which are shown as Objects in JSON, or Lists, which are shown as arrays.

    //In this DTO im a using a @PathVariable, which is a SPRING annotation which allows me to
    //to use a variable as a parameter in my URL, in this case a GamePlayer ID.

    @RequestMapping("/game_view/{gamePlayerId}")
    private ResponseEntity<Object> findGamePlayer(@PathVariable Long gamePlayerId, Authentication authentication) {

        //By putting in a ID in the URL, i can access the specific INSTANCE of gameplayer
        //which i want to see, by using findOne in the game player repository.

        GamePlayer gameplayer = gamePlayerRepository.findOne(gamePlayerId);

        //Accessing this INSTANCE of GamePlayer related Game INSTANCE.

        Game game = gameplayer.getGame();

        //Creating a LinkedHashMap for structuring my DTO.

        Map<String, Object> gameViewDTO = new LinkedHashMap<>();

        //Inserting keys and values

        gameViewDTO.put("id", game.getId());
        gameViewDTO.put("created", new Date());

        //Creating a new List to be represented as an Array in my JSON.

        List<Map> gamePlayerList = new ArrayList<>();

        //Retrieving a Set of GamePlayers by using my Game instance's getGamePlayers method,
        //which return a Set of the two GamePlayers that are related.

        Set<GamePlayer> gameplayers = game.getGameplayers();

        //Looping  through my two GamePlayers, and for each one creating a Map with the needed information.
        //In this case, another method is being referenced as a Value (makePlayerDTO).

        gameplayers.forEach(gamePlayer -> {
            HashMap<String, Object> gamePlayerMap = new HashMap<>();
            gamePlayerMap.put("id", gamePlayer.getId());
            gamePlayerMap.put("isFirst", gamePlayer.isFirst());
            gamePlayerMap.put("player", makePlayersDTO(gamePlayer.getPlayer()));
            gamePlayerList.add(gamePlayerMap);
        });

        //I continue to put the remaining needed info, this time referencing a different method for creating
        //my SalvoDTO.

        gameViewDTO.put("gamePlayers", gamePlayerList);
        gameViewDTO.put("ships", gameplayer.getShips());
        gameViewDTO.put("salvoes", makeSalvoDTO(game));
        gameViewDTO.put("stats", buildStats(gameplayers, gamePlayerId));
        gameViewDTO.put("gameState", getGameState(gameplayer));
        gameViewDTO.put("opponentSunk", checkForSunken(gameplayer));

        boolean isLoggedIn = false;
        HttpStatus status = HttpStatus.OK;

        Player currentPlayer = getCurrentPlayer(authentication);
        List<GamePlayer> currentGamePlayers = currentPlayer.getGameplayers();
        List<Long> gamePlayersIds = new ArrayList<>();

        currentGamePlayers.forEach(gamePlayer -> {
            gamePlayersIds.add(gamePlayer.getId());
        });

        for (Long gamePlayersId : gamePlayersIds) {
            if (gamePlayersId == gamePlayerId) {
                isLoggedIn = true;
                break;
            }
        }

        if (!isLoggedIn) {
            gameViewDTO.clear();
            gameViewDTO.put("status", "Not your game! Stop cheating!");
            status = HttpStatus.FORBIDDEN;
        }

        return new ResponseEntity<>(gameViewDTO, status);
    }

    //Check stats of all players, sort into a DTO and return for.

    @RequestMapping("/leaderboard")
    private Map<String, Object> getAllPlayers() {
        List<Player> players = playerRepository.findAll();
        Map<String, Object> allPlayersDTO = new LinkedHashMap<>();
        List<Map> mappedPlayers = new ArrayList<>();
        players.forEach(player -> {
            Map<String, Object> myPlayer = new LinkedHashMap<>();
                List<Score> scores = player.getScores();
            double total = 0;
            int wins = 0;
            int losses = 0;
            double ties = 0;
            for (Score score : scores) {
                total += score.getScore();
                if (score.getScore() == 0) {
                    losses += 1;
                } else if (score.getScore() == 0.5) {
                    ties += 1;
                } else {
                    wins += 1;
                }
                myPlayer.put("total", total);
                myPlayer.put("wins", wins);
                myPlayer.put("losses", losses);
                myPlayer.put("ties", ties);
            }
            myPlayer.put("username", player.getUserName());
            mappedPlayers.add(myPlayer);
        });
        allPlayersDTO.put("players", mappedPlayers);
        return allPlayersDTO;
    }

    //DTO for specific player

    private Map<String, Object> makePlayersDTO(Player player) {
        Map<String, Object> playersDTO = new LinkedHashMap<String, Object>();
        playersDTO.put("id", player.getId());
        playersDTO.put("username", player.getUserName());
        return playersDTO;
    }

    //Comments inside -------------------------

    private Map<String, Object> makeGamesDTO(Game game) {

        //Instantiate a new LinkedHashMAp with the name gameDTO

        Map<String, Object> gameDTO = new LinkedHashMap<String, Object>();

        //LinkedHashMap allows me to structure how i want my response DATA
        //I now "PUT" two keys with two values

        gameDTO.put("id", game.getId());
        gameDTO.put("created", game.getDate());

        //Creating a new List to be represented as an Array in my JSON.

        List<Map> gamePlayerList = new ArrayList<>();

        //Save gameplayers of SPECIFIC game to local variable

        Set<GamePlayer> gameplayers = game.getGameplayers();


        //Loop through my gamePlayers , and for each one restructure it with HashMap into object notation
        //With the values i choose. In this case, one of my values - makePlayersDTO, is a method
        //creating another DTO. This is done this way purely for structural reasons.

        gameplayers.forEach(gamePlayer -> {
            HashMap<String, Object> gamePlayerMap = new HashMap<>();
            gamePlayerMap.put("id", gamePlayer.getId());
            gamePlayerMap.put("player", makePlayersDTO(gamePlayer.getPlayer()));
            gamePlayerMap.put("score", gamePlayer.getScore(gamePlayer.getGame()));
            //Add my structured data into the previously created List.

            gamePlayerList.add(gamePlayerMap);
        });


        //Add that data into my Original LinkedHashMap - gameDTO.
        gameDTO.put("gamePlayers", gamePlayerList);
        gameDTO.put("gameOver", gameOver(game));
        return gameDTO;
    }

    //Comments inside ---------------------------

    private Map<String, Object> makeSalvoDTO(Game game) {

        //In this method for creating a DTO, im using a nested loop.
        //First, im accesing my two GamePlayers via the specifc Game INSTANCE.
        //I then loop through each one, and while inside, i loop through its Salvoes, and construct them
        //into a Map. I later use this map as a value in my Game_View DTO.

        Map<String, Object> playerMap = new LinkedHashMap<>();
        Set<GamePlayer> gameplayers = game.getGameplayers();

        gameplayers.forEach(gameplayer -> {
            String playerID = Long.toString(gameplayer.getPlayer().getId());
            Set<Salvo> salvoes = gameplayer.getSalvoes();
            Map<String, Object> salvoMap = new LinkedHashMap<>();

            salvoes.forEach(salvo -> {
                String salvoTurn = Long.toString(salvo.getTurn());
                salvoMap.put(salvoTurn, salvo.getSalvoLocations());
                playerMap.put(playerID, salvoMap);
            });
        });
        return playerMap;
    }

    //Save new user to the database

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    private ResponseEntity<Map<String, Object>> signUpPlayer(@RequestParam String username, @RequestParam String password) {
        if (username.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name"), HttpStatus.FORBIDDEN);
        }
        Player player = playerRepository.findOneByUserName(username);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Name is already used"), HttpStatus.CONFLICT);
        }
        player = playerRepository.save(new Player(username, password));
        return new ResponseEntity<>(makeMap("name", player.getUserName()), HttpStatus.CREATED);
    }

    //Create a new GAME instance , and connect the currentUser to it.

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    private ResponseEntity<Object> createGame(Authentication authentication) {
        Player currentPlayer = getCurrentPlayer(authentication);
        Game newGame = new Game(new Date());
        gameRepository.save(newGame);
        GamePlayer newGamePlayer = new GamePlayer(currentPlayer, newGame, new Date(), true);
        gamePlayerRepository.save(newGamePlayer);
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put("succes", "New game created!");
        responseMap.put("GPID", newGamePlayer.getId());
        return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
    }

    //On click of joinGame in the frontend, join the currentUser into a preexisting game in the database.

    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    private ResponseEntity<Object> joinGame(@PathVariable Long gameId, Authentication authentication) {
        Player currentPlayer = getCurrentPlayer(authentication);

        if (currentPlayer.getUserName() == null) {
            return new ResponseEntity<>("No User", HttpStatus.UNAUTHORIZED);
        }

        if (gameRepository.findOne(gameId) == null) {
            return new ResponseEntity<>("No Game", HttpStatus.FORBIDDEN);
        }
        Game game = gameRepository.findOne(gameId);

        List<Player> players = game.getPlayers();
        if (players.size() == 2) {
            return new ResponseEntity<>("Game is full", HttpStatus.FORBIDDEN);
        }


        GamePlayer gameplayer = new GamePlayer(currentPlayer, game, new Date(), false);
        gamePlayerRepository.save(gameplayer);
        Map<String, Object> gpIdMap = new LinkedHashMap<>();
        gpIdMap.put("gpid", gameplayer.getId());
        return new ResponseEntity<>(gpIdMap, HttpStatus.CREATED);
    }

    //Parse body of request and create instances of SHIP to be saved into the DATABASE.

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    private ResponseEntity<Object> placedShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {
        GamePlayer gameplayer = gamePlayerRepository.findOne(gamePlayerId);
        errorCatcherMain(gamePlayerId, authentication);
        return errorCatcherShips(ships, gameplayer);
    }

    //Method for catching illegal actions related to ship placement.

    private ResponseEntity<Object> errorCatcherShips(List<Ship> ships, GamePlayer gameplayer) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        String response = "";
        List<String> locations = new ArrayList<>();

        for (Ship ship : ships) {
            List<String> locationsShip = ship.getLocations();
            for (String location : locationsShip) {
                locations.add(location);
            }
        }

        Set<String> uniqueLocations = new HashSet<>(locations);
        if (uniqueLocations.size() < 17 || uniqueLocations.size() > 17) {
            response = "Either you have not placed all your ships, you already placed them, or your ships are overlapping!";
            return new ResponseEntity<>(response, status);
        }

        boolean shipPlacement = true;

        for (String uniqueLocation : uniqueLocations) {
            shipPlacement = uniqueLocation.matches(("^[a-zA-Z][1-9]|[a-zA-Z][1-9][0]$"));
            if (!shipPlacement) {
                response = "One of your ships is out of the border!";
                return new ResponseEntity<>(response, status);
            }
        }

        status = HttpStatus.CREATED;
        response = "Succes!";


        for (Ship ship : ships) {
            gameplayer.addShip(ship);
            shipRepository.save(ship);
        }


        return new ResponseEntity<>(response, status);
    }

    //Method for catching illegal actions

    private ResponseEntity<Object> errorCatcherMain(Long gamePlayerId, Authentication authentication) {
        GamePlayer gameplayer = gamePlayerRepository.findOne(gamePlayerId);
        Player player = getCurrentPlayer(authentication);
        HttpStatus status = HttpStatus.CREATED;
        String response = "success";
        List<GamePlayer> gamePlayers = player.getGameplayers();
        boolean matchedPlayerToGamePlayer = false;

        for (GamePlayer gamePlayer : gamePlayers) {
            if (gameplayer.getId() == gamePlayerId) {
                matchedPlayerToGamePlayer = true;
            }
        }

        if (player == null) {
            status = HttpStatus.UNAUTHORIZED;
            response = "No User!";
        } else if (gameplayer == null) {
            status = HttpStatus.UNAUTHORIZED;
            response = "No GamePlayer!";
            return new ResponseEntity<>(response, status);
        } else if (!matchedPlayerToGamePlayer) {
            status = HttpStatus.UNAUTHORIZED;
            response = "Wrong GamePlayer Id!";
            return new ResponseEntity<>(response, status);
        }

        return new ResponseEntity<>(response, status);
    }

    //shootSalvo() uses a SPRING annotation called @RequestBody, which tells JACKSON to deserialize the body of the request into a
    //matching instance of the Java class which it represents, therefor allowing me to save a new SALVO into the database.
    //--Several failsafes for faulty//illegal salvoes.

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    private ResponseEntity<Object> shootSalvo(@PathVariable Long gamePlayerId, @RequestBody Salvo salvo, Authentication authentication) {

        GamePlayer gameplayer = gamePlayerRepository.findOne(gamePlayerId);
        Player player = getCurrentPlayer(authentication);
        Set<Salvo> salvoes = gameplayer.getSalvoes();
        GamePlayer opponent = gameplayer.getOpponent();

        errorCatcherMain(gamePlayerId, authentication);

        HttpStatus status = HttpStatus.CREATED;
        String response = "success";

        if (opponent != null) {
            if (opponent.getShips().size() != 5) {
                status = HttpStatus.FORBIDDEN;
                response = "Other player must place his ships before you can shoot..";
                return new ResponseEntity<>(response, status);
            }
        } else {
            status = HttpStatus.FORBIDDEN;
            response = "Another player must join the game..";
            return new ResponseEntity<>(response, status);
        }

        for (Salvo salvoe : salvoes) {
            if (salvoe.getTurn() == salvo.getTurn()) {
                status = HttpStatus.FORBIDDEN;
                response = "You have already used your salvoes this turn!";
                return new ResponseEntity<>(response, status);
            }
        }

        gameplayer.addSalvo(salvo);
        salvoRepository.save(salvo);

        return new ResponseEntity<>(response, status);
    }

    //Method for returning the JSON data of the stats for BOTH players, based on getPlayerStats().

    private Map<String,Object> buildStats (Set<GamePlayer> gameplayers, Long gamePlayerId) {

        Map<String, Object> statsDTO = new LinkedHashMap<>();

        gameplayers.forEach(gamePlayer -> {
            String name = gamePlayer.getId() == gamePlayerId ? "You" : "Opponent";
            statsDTO.put(name,getPlayerStats(gamePlayer));
        });

        return statsDTO;
    }

    //Comments inside -------------------------

    private Map<String,Object> getPlayerStats(GamePlayer gamePlayer) {

        Game game = gamePlayer.getGame();
        Player player = gamePlayer.getPlayer();
        Map<String,Object> playerStatsDTO = new LinkedHashMap<>();
        Map<Ship,Integer> overAllHits = new LinkedHashMap<>();
        GamePlayer opponent = gamePlayer.getOpponent();

        //Make sure an opponent exists to avoid NullPointerException.

        if (opponent != null){

            //For every one of "MY" salvoes , i loop through enemy ships.
            //I compare the locations of the ONE salvo to EACH enemy ship locations
            //and keep the "HITS" in a new array.

            //In order to always keep track of PREVIOUS hits, i created overAllHits,
            //which keeps building up through out the different salvoes.
            //I can then check if a ship is SUNKEN by the overall hits on it, and if so,
            //set its boolean isSunk to true.

            //Finally, i place the hits in a map with the ship type, and check to see if the
            //game is over with the last If statement.

            for (Salvo salvo : gamePlayer.getSalvoes()) {
                Map<Object,Object> turnDTO = new LinkedHashMap<>();
                String turn = Integer.toString(salvo.getTurn());

                for (Ship ship : opponent.getShips()) {
                    Ship.Type type = ship.getType();
                    String shipName = ship.getType().toString();
                    List<String> shipLocations = new ArrayList<>(ship.getLocations());
                    List<String> salvoLocations = new ArrayList<>(salvo.getSalvoLocations());

                    salvoLocations.retainAll(shipLocations);

                    if (salvoLocations.size() != 0){

                        if (overAllHits.containsKey(ship)){
                            overAllHits.put( ship, overAllHits.get(ship) + salvoLocations.size() );
                        } else {
                            overAllHits.put( ship, salvoLocations.size() );
                        }

                        if (ship.getLength(type) == overAllHits.get(ship)){
                          ship.setSunk(true);
                        }

                        turnDTO.put(shipName, salvoLocations.size());
                    }
                }
                playerStatsDTO.put(turn, turnDTO);
            }

            if (gamePlayer.getScore(game) == null && checkForSunken(gamePlayer) == 0 && opponent.getSalvoes().size() != 0){
              getScores(gamePlayer);
            }

            return playerStatsDTO;
        }

        return null;
    }

    private String getGameState (GamePlayer gamePlayer) {
        GamePlayer opponent = gamePlayer.getOpponent();
        String state = "";

        if (gamePlayer.getShips().size() != 5){
            state = "placeShips";
            return state;
        }

        if (opponent != null) {

            if (gamePlayer.getGame().isGameOver()){
                state = "gameOver";
                return state;
            }

            if (opponent.getShips().size() != 5){
                state = "opponentPlaceShips";
                return state;
            }

            List<Integer> myTurns = new ArrayList<>();
            List<Integer> opponentTurns = new ArrayList<>();

            gamePlayer.getSalvoes().forEach(salvo -> {
                myTurns.add(salvo.getTurn());
            });

            opponent.getSalvoes().forEach(salvo -> {
                opponentTurns.add(salvo.getTurn());
            });

            boolean first = gamePlayer.isFirst();

            if (myTurns.size() < opponentTurns.size()) {
                state = "myTurn";
                return state;
            } else if (myTurns.size() > opponentTurns.size()){
                state = "opponentTurn";
                return state;
            } else {
                if (first){
                    state = "myTurn";
                    return state;
                } else {
                   state = "opponentTurn";
                   return state;
                }
            }
        }

        state = "noOpponent";
        return state;
    }

    //Check if game is over by the existence of SCORES or not.

    private boolean gameOver(Game game) {
        List<Score> scores = game.getScores();
        if (scores.size() != 0) {
            game.setGameOver(true);
        }
        return game.isGameOver();
    }

    //Small simple method for quickly making a MAP.

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    //Find player from repo by using the Authentication getName(),
    //create a new Player instance, and if there is a user logged in,
    //set that instance to the user, and return him.

    private Player getCurrentPlayer(Authentication authentication) {

        List<Player> players = playerRepository.findByUserName(authentication.getName());
        Player myPlayer = new Player();
        if (!players.isEmpty()) {
            Player player = players.get(0);
            myPlayer = player;
        } else {
            isGuest(authentication);
        }
        return myPlayer;
    }

    //If there is no authenticated user, create instane of AnonymousUser.

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    //Method for saving new Scores to DB according to GAME results.

    private void getScores (GamePlayer gamePlayer) {

        Player opponent = gamePlayer.getOpponent().getPlayer();
        Game game = gamePlayer.getGame();
        Player player = gamePlayer.getPlayer();
        game.setGameOver(true);

        //Check for ties

        if (checkForSunken(gamePlayer) == 0 && checkForSunken(gamePlayer.getOpponent()) == 0){
            Score myScore = new Score(0.5, game, player );
            Score opponentScore = new Score(0.5, game, opponent);
            scoreRepository.save(myScore);
            scoreRepository.save(opponentScore);
            return;
        }

        //Save new scores into repo.

        Score myScore = new Score(1.0, game, player );
        Score opponentScore = new Score(0.0, game, opponent);
        scoreRepository.save(myScore);
        scoreRepository.save(opponentScore);
    }

    //Check that opponent exists to avoid nullPointerException.
    //Calculate how many sunken ships by checking class boolean isSunk.

    private Integer checkForSunken (GamePlayer gamePlayer){

        GamePlayer opponent = gamePlayer.getOpponent();

        if (opponent != null ) {
            int sunken = 5;
            for (Ship ship : opponent.getShips()) {
                if (ship.isSunk()) {
                    sunken--;
                }
            }
            return sunken;
        }
        return null;
    }
}























