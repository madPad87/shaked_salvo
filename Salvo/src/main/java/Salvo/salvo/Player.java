package Salvo.salvo;
//Import needed classes/modules/libraries/dependencies
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

//Entity annotation for spring to recognize - DB
@Entity
//Declaring class Player
public class Player {

    //ID annotation for auto generation of unique ID
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    //Declaring variables.
    private long id;
    private String userName;

    //OnetoMany relationship with gameplayers, as in ONE Player can have several GamePlayers.
    //EAGER meaning no waiting for requests, but immediately loading data.

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    List<GamePlayer> gameplayers = new ArrayList<GamePlayer>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    List<Score> scores = new ArrayList<Score>();

    private String password;

    //Constructor function

    public Player () { }

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    //Getter and setter for all variables


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //Method for adding specific player to GamePlayer class, noting time of adding and adding to gameplayers list.
    public void addGamePlayer(GamePlayer gameplayer) {
        gameplayer.setPlayer(this);
        gameplayer.setDate(new Date());
        gameplayers.add(gameplayer);
    }

    //Method for retrieving list of games which specific player played in
    @JsonIgnore
    public List<Game> getGames() {
        return gameplayers.stream().map(gamePlayer -> gamePlayer.getGame()).collect(toList());
    }

    @JsonIgnore
    public List<GamePlayer> getGameplayers() {
        return gameplayers;
    }

    public void setGameplayers(List<GamePlayer> gameplayers) {
        this.gameplayers = gameplayers;
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public Score getScore(Game game) {
        return this.scores.stream()
                .filter(score -> score.getGame().equals(game)).findFirst().orElse(null);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}