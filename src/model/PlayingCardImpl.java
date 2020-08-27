package model;

import java.util.Objects;

import model.interfaces.PlayingCard;

public class PlayingCardImpl implements PlayingCard {

    Suit suit;
    Value value;

    public PlayingCardImpl(Suit s, Value v){
        suit = s;
        value = v;
    }

    @Override
    public Suit getSuit() {
        return suit;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public int getScore() {
        switch(value){
            case EIGHT:
                return 8;

            case NINE:
                return 9;

            case TEN:
            case JACK:
            case QUEEN:
            case KING:
                return 10;

            case ACE:
                return 11;

            default:
                return -1;
        }
        
    }

    @Override
    public boolean equals(PlayingCard card) {
        return (this.hashCode() == card.hashCode());
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.suit, this.value);
    }

    @Override
    public String toString(){
        return String.format(
            "Suit: %s, Value: %s, Score: %d",
            suit,
            value,
            getScore()
        );
    }

}