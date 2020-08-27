package model;

import java.util.Objects;

import model.interfaces.Player;

public class SimplePlayer implements model.interfaces.Player {

    String  id;
    String  name;
    int     result;
    int     points;
    int     bet;

    public SimplePlayer(String id, String name, int points) {
        this.id = id;
        this.name = name;
        this.points = points;
	}

	@Override
    public String getPlayerName() {
        return name;
    }

    @Override
    public void setPlayerName(String playerName) {
        this.name = playerName;
    }

    @Override
    public int getPoints() {
        return this.points;
    }

    @Override
    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String getPlayerId() {
        return this.id;
    }

    @Override
    public boolean setBet(int bet) {
        if (bet > 0 && bet <= this.points) this.bet = bet;
        return (bet > 0 && bet <= this.points);
    }

    @Override
    public int getBet() {
        return this.bet;
    }

    @Override
    public void resetBet() {
        this.bet = 0;
    }

    @Override
    public int getResult() {
        return result;
    }

    @Override
    public void setResult(int result) {
        this.result = result;

    }

    @Override
    public boolean equals(Object player) {
        if (player instanceof Player)
            return this.equals((Player) player);

        return false;
    }


    @Override
    public boolean equals(Player player) {
        return this.hashCode() == player.hashCode();
    }

    @Override
    public int compareTo(Player player) {
        // i'm not 100% sure if player ID's are numerical only, 
        // or also contain letters, so i'm going with 
        // lexicographical ordering, as implementing 'natural' ordering
        // (i.e A1, A2, A3, A10, A11) will be unwieldly given the
        // scope of the project.

        return this.id.compareTo(player.getPlayerId());
    }

    @Override
    public String toString() {
        // will always show results if result is valid (i.e not zero)
        return String.format(
            "Player: id=%s, name=%s, bet=%d, points=%d%s",
            id,
            name,
            bet,
            points,
            (result > 0) ? String.format(", RESULT .. %d", result) : ""
        );
    }

    @Override 
    public int hashCode(){
        return Objects.hash(id, name, result, points, bet);
    }
    
}