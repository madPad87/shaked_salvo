package Salvo.salvo;
//Import needed classes/modules/libraries/dependencies

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.*;
import static java.util.stream.Collectors.toList;

//Entity annotation for spring to recognize - DB
@Entity

//Declaring class Player
public class Game {

    //ID annotation for auto generation of unique ID
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private Date date;

    //OnetoMany relationship. EAGER meaning no waiting for requests, but immediately loading data.
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    private Set<GamePlayer> gameplayers;

    @JsonIgnore
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    List<Score> scores = new ArrayList<Score>();

    public boolean gameOver;

    //Constructor function
    public Game ( ) {}

    public Game(Date date) {
        this.date = date;
    }

    //Getter and setter for all variables

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    //Method for adding specific game to to GamePlayer class, noting time of adding and adding to gameplayers list.

    public void addGamePlayer(GamePlayer gameplayer) {
        gameplayer.setGame(this);
        gameplayer.setDate(new Date());
        gameplayers.add(gameplayer);
    }

    public void setGameplayers(Set<GamePlayer> gameplayers) {
        this.gameplayers = gameplayers;
    }

    @JsonIgnore
    public Set<GamePlayer> getGameplayers() {
        return gameplayers;
    }

    @JsonIgnore
    public List<Player> getPlayers() {
    return gameplayers.stream().map(gamePlayer -> gamePlayer.getPlayer()).collect(toList());
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

}