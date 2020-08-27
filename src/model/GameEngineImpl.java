package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

import jdk.jfr.internal.Logger;
import model.interfaces.GameEngine;
import model.interfaces.Player;
import model.interfaces.PlayingCard;
import model.interfaces.PlayingCard.Suit;
import model.interfaces.PlayingCard.Value;
import view.interfaces.GameEngineCallback;

public class GameEngineImpl implements GameEngine {

    // ordered arraylist of callbacks so that execution of callbacks can have a
    // sense of 'order'. (i.e callback 2 will always come after callback 1)
    ArrayList<GameEngineCallback> callbacks = new ArrayList<GameEngineCallback>();
    
    // self sorting set to avoid the hassle of sorting players manually
    TreeSet<Player> players = new TreeSet<Player>();

    // not sure how we want decks to be handled between deals with
    // players and the house, keeping one deck here for persistence.
    Deque<PlayingCard> deck = getShuffledHalfDeck();
    // decks are reset after applyWinLoss, though may need to be revised
    // given only 28 cards and no limit was stated on players 
    // (more than 5 players can cause issues with card availability)
 
    @Override
    public void dealPlayer(Player player, int delay) throws IllegalArgumentException {
        if (delay > 1000 || delay < 0)
            throw new IllegalArgumentException("Delay must be between 0 and 1000ms!");

        // as the function is being generalised, we must check for null players
        // here rather than in the generalised function to ensure no null objects
        // are unknowingly processed as house results
        if (player == null)
            throw new IllegalArgumentException("Player cannot be a null object!");
        
        // delegate to generalised function
        deal(player, delay);
            
    }

    @Override
    public void dealHouse(int delay) throws IllegalArgumentException {
        if (delay > 1000 || delay < 0)
            throw new IllegalArgumentException("Delay must be between 0 and 1000ms!");

        // delegate to generalised function and use return val
        // to apply winloss
        int houseResult = deal(null, delay);

        for (Player p : players)
            applyWinLoss(p, houseResult);

        for (GameEngineCallback gec : callbacks)
            gec.houseResult(houseResult, this);
        
        // reset the deck of cards for the next round
        deck = getShuffledHalfDeck();
    }

    // the specification states eliminating 'rigorous code re-use'
    // so i am generalizing both deal methods to this one method here
    // otherwise a lot of code would be reused

    // returns the result so that win/loss can be handled after 
    // house result
    private int deal(Player player, int delay){
        
        // if P is null it is assumed to be the house deal
        // player is checked to be null already
        boolean isHouse = (player == null);

        PlayingCard c = deck.pop();
        int sum = c.getScore();

        while (sum < GameEngine.BUST_LEVEL) {
            for (GameEngineCallback gec : callbacks)
                if (isHouse) gec.nextHouseCard(c, this);
                else gec.nextCard(player, c, this);
            

            c = deck.pop();
            sum += c.getScore();

            try { Thread.sleep(delay); }
            catch(InterruptedException ie) { ie.printStackTrace(); }
        }

        if (sum == 42) c = null;
        else sum -= c.getScore();

        if (!isHouse) player.setResult(sum);

        for (GameEngineCallback gec : callbacks){
            if (isHouse){
                if (c != null) 
                    gec.houseBustCard(c, this);
                    // house result callback is not called here as 
                    // they rely on player bets to be processed which are
                    // done in a separate method
            }            
            else {
                if (c != null) gec.bustCard(player, c, this);
                gec.result(player, sum, this);
            }
        }

        // return sum so it can be used by other functions
        // if it's ever needed
        return sum;


    }

    @Override
    public void applyWinLoss(Player player, int houseResult) {
        // if player wins, score = score + bet
        // if player loses, score = score - bet
        if (player.getResult() < houseResult) player.setPoints(player.getPoints() - player.getBet());
        else if (player.getResult() > houseResult) player.setPoints(player.getPoints() + player.getBet());
              
        // not sure if bets should be reset at the end of a houseResult or 
        // if they're persistent unless changed

        // for now, they'll remain persistent
        // player.resetBet()
    }

    @Override
    public void addPlayer(Player player) {
        if (player == null) return;
        players.add(player);
    }

    @Override
    public Player getPlayer(String id) {
        for (Player p : players)
            if (p.getPlayerId() == id)
                return p;

        return null;
    }

    @Override
    public boolean removePlayer(Player player) {
        return players.remove(player);
    }

    @Override
    public boolean placeBet(Player player, int bet) {
        return player.setBet(bet);
    }

    @Override
    public void addGameEngineCallback(GameEngineCallback gameEngineCallback) {
        if (gameEngineCallback == null) return;
        callbacks.add(gameEngineCallback);
    }

    @Override
    public boolean removeGameEngineCallback(GameEngineCallback gameEngineCallback) {
        return callbacks.remove(gameEngineCallback);
    }

    @Override
    public Collection<Player> getAllPlayers() {
        // treesets are sorted automatically upon element insertion
        // so no sorting is needed here.
        return players;
    }

    @Override
    public Deque<PlayingCard> getShuffledHalfDeck() {
        LinkedList<PlayingCard> queue = new LinkedList<PlayingCard>();
        for (Suit s : Suit.values())
            for (Value v : Value.values())
                queue.add(new PlayingCardImpl(s, v));

        Collections.shuffle(queue);
        return queue;
    }

}