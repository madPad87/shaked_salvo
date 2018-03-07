package Salvo.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

@Entity
public class Ship {

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ElementCollection
    @Column(name="location")
    private List<String> locations = new ArrayList<String>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public boolean isSunk() {
        return isSunk;
    }

    public void setSunk(boolean sunk) {
        isSunk = sunk;
    }

    private boolean isSunk;

    public Ship ( ) { }

    public Ship (Type type, List<String> locations) {
        this.type = type;
        this.locations = locations;
    }

    public enum Type {
        Carrier,
        Battleship,
        Destroyer,
        Submarine,
        PatrolBoat
    }

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @JsonIgnore
    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public int getLength (Ship.Type type) {
        int length = 0;

        switch (type){
            case PatrolBoat:
                length = 2;
                break;
            case Battleship:
                length = 4;
                break;
            case Submarine:
                length = 3;
                break;
            case Destroyer:
                length = 3;
                break;
            case Carrier:
                length = 5;
                break;
        }
        return length;
    }

}
