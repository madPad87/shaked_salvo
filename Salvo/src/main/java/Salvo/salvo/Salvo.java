package Salvo.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ElementCollection
    @Column(name="salvoLocation")
    private List<String> salvoLocations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    private int turn;

    public Salvo ( ) { }

    public Salvo (int turn, List<String> salvoLocations) {
        this.turn = turn;
        this.salvoLocations = salvoLocations;
    }

    public Salvo (GamePlayer gamePlayer, int turn, List<String> salvoLocations) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocations = salvoLocations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    @JsonIgnore
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}
