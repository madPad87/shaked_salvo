package Salvo.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private Date date;

    //OneToMany relationship with ships, as in ONE GamePlayer can have several Ships.
    //EAGER meaning no waiting for requests, but immediately loading data.

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    private Set<Ship> ships = new HashSet<Ship>();

    //OneToMany relationship with salvoes, as in ONE GamePlayer can have several Salvoes.
    //EAGER meaning no waiting for requests, but immediately loading data.

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salvoes = new HashSet<Salvo>();

    //ManyToOne relationship with Player, ONE Player having many GamePlayers.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    //ManyToOne relationship with Player, ONE Game having many GamePlayers.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    private boolean isFirst;

    //Constructor

    public GamePlayer () { }

    public GamePlayer (Player player, Game game, Date date) {
        this.player = player;
        this.game = game;
        this.date = date;
    }

    public GamePlayer (Player player, Game game, Date date, boolean isFirst) {
        this.player = player;
        this.game = game;
        this.date = date;
        this.isFirst = isFirst;
    }

    //Getters and Setters, must be provided for spring to load instance specific data.

    public  long  getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate(Date date) {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    //Json Ignore is telling SPRING to ignore this method while rendering JSON data on my browser.
    //This is being done in order to prevent loops.

    @JsonIgnore
    public Set<Ship> getShips() {
        return ships;
    }

    public void setShips(Set<Ship> ships) {
        this.ships = ships;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        this.salvoes = salvoes;
    }

    public void addSalvo (Salvo salvo) {
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public Score getScore(Game game){
        return this.getPlayer().getScore(game);
    }

    @JsonIgnore
    public GamePlayer getOpponent(){
        return game.getGameplayers().stream().filter(gamePlayer -> gamePlayer != this ).findFirst().orElse(null);
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public Date getDate() {
        return date;
    }

}
