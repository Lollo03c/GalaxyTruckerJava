package org.mio.scheletroprogetto;

import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.components.GoodType;

import java.util.*;

public class FlyBoard {
//    private  HourGlass hourGlass;

    private  List<AdventureCard> selectionDeckPrivate;
    private  List<AdventureCard> selectionDeck1;
    private  List<AdventureCard> selectionDeck2;
    private  List<AdventureCard> selectionDeck3;

    private  List<AdventureCard> deck;

    private  List<Optional<Player>> circuit;

    private  List<Player> scoryBoard;
    private  Stack<Component> coveredComponents;
    private  List<Component> uncoverdeComponents;

    private  Map<GoodType, Integer> remainingGoods;

    public FlyBoard(){}

    public void StartGame(){

    }

    public void addGood(GoodType type, Integer quant){

    }

    public Boolean removeGood(GoodType type, Integer quant){
        return false;
    }

    public void moveRocket(Player player, int nPos){

    }

    public Player getPosition(int position){
        return new Player("");
    }

    public List<Player> getScoryBoard(){
        return new LinkedList<>();
    }



}
