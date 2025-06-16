package org.mio.progettoingsoft.views.tui;

import org.mio.progettoingsoft.AdvCardType;
import org.mio.progettoingsoft.AdventureCard;
import org.mio.progettoingsoft.Direction;
import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.advCards.sealed.*;
import org.mio.progettoingsoft.components.GoodType;

import java.util.ArrayList;
import java.util.List;

public class VisualCard {
    public static final String RESET = "\u001B[0m";
    public static final String BROWN = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String WHITE = "\u001B[97m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String ORANGE = "\u001B[38;5;208m";

    private SldAdvCard card;
    private final static int numRows = 15;
    private final static int numCols = 30;
    private int level = 2;
    ColoredChar[][] mat;
    public VisualCard(SldAdvCard card) {
        this.card = card;
        mat = new ColoredChar[numRows][numCols];
        //AdvCardType type = card.getType();
        switch (card) {
            case SldOpenSpace o -> {
                paint(BLUE);
            }
            case SldStardust s -> {
                paint(YELLOW);
            }
            case SldPlanets  p-> {
                paint(GREEN);
                computePlanet();
            }
            case SldSlavers s-> {
                paint(PURPLE);
                computeSlaver();
            }
            case SldSmugglers s-> {
                paint(PURPLE);
                computeSmugglers();
            }
            case SldPirates p-> {
                paint(PURPLE);
                computePirate();
            }
            case SldMeteorSwarm m-> {
                paint(ORANGE);
                computeMetorSwarm();
            }
            case SldCombatZone c-> {
                paint(RED);
                computeCombateZone();
            }
            case SldEpidemic e-> paint(YELLOW);
            case SldAbandonedShip a->{
                paint(WHITE);
                computeAbandonedShip();
            }
            case SldAbandonedStation a-> {
                paint(WHITE);
                computeAbandonedStation();
            }

            default -> {}
        }
        computeBorders();
        computeName();
    }

    private void computeAbandonedStation() {
        daysLost();
        computeObtainedGoods();
        try {
            int crewNeeded = card.getCrewNeeded();
            String disp = "Crew needed: " + crewNeeded;
            for(int i = 0; i < disp.length(); i++){
                mat[4][2+i].setChar(disp.charAt(i));
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void computeAbandonedShip(){
        daysLost();
        credits();
        loseCrew();
    }

    private void computeCombateZone(){
        String disp1 = "Target";
        String disp2 = "Penalty";
        for(int i = 0; i< disp1.length(); i++){
            mat[3][2+i].setChar(disp1.charAt(i));
        }
        for(int i = 0; i< disp2.length(); i++){
            mat[3][17+i].setChar(disp2.charAt(i));
        }
        List<CombatLine> lines = card.getLines();
        int indexext = 0;
        for(CombatLine line : lines){
            String disp = line.getCriterion().criterionToString();
            for(int i = 0; i< disp.length(); i++){
                mat[4+3*indexext][2+i].setChar(disp.charAt(i));
            }
            List<Penalty> penalties = line.getPenalties();
            PenaltyType penType = penalties.getFirst().getType();
            switch (penType){
                case DAYS -> {
                    String disp3 = "Days: " + penalties.getFirst().getAmount();
                    for(int i = 0; i< disp3.length(); i++){
                        mat[4+3*indexext][17+i].setChar(disp3.charAt(i));
                    }
                }
                case CREW -> {
                    String disp4 = "Crew: " + penalties.getFirst().getAmount();
                    for(int i = 0; i< disp4.length(); i++){
                        mat[4+3*indexext][17+i].setChar(disp4.charAt(i));
                    }
                }
                case LIGHT_CANNON -> {
                    int indexInt = 0;
                    for(Penalty pen : penalties){
                        PenaltyType internalType = pen.getType();
                        String disp5;
                        char dir = dirToChar(pen.getDirection());
                        switch (internalType){
                            case LIGHT_CANNON -> {
                                disp5 = "Light "+dir;
                            }
                            case HEAVY_CANNON -> {
                                disp5 = "Heavy "+dir;
                            }
                            default -> disp5 = "Unknown";
                        }
                        for(int i = 0; i< disp5.length(); i++){
                            mat[4+indexInt][17+i].setChar(disp5.charAt(i));
                        }
                        indexInt++;
                    }
                }
                case GOODS ->{
                    String disp4 = "Goods: " + penalties.getFirst().getAmount();
                    for(int i = 0; i< disp4.length(); i++){
                        mat[4+3*indexext][17+i].setChar(disp4.charAt(i));
                    }
                }
            }
            indexext++;
        }
    }

    private void computeMetorSwarm(){
        List<Meteor> meteors = card.getMeteors();
        String disp = "Meteors:";
        for(int i = 0; i<disp.length(); i++){
            mat[4][2+i].setChar(disp.charAt(i));
        }
        int index = 0;
        for(Meteor meteor : meteors){
            char d = dirToChar(meteor.getDirection());
            String met = meteor.toString();
            for(int i = 0; i<met.length(); i++){
                mat[4+2*index][2+i].setChar(met.charAt(i));
            }
            mat[4+2*index][15].setChar(':');
            mat[4+2*index][17].setChar(d);
            index++;
        }
    }

    private void computePirate(){
        firePower();
        daysLost();
        credits();
        cannonPenalty();
    }

    private void cannonPenalty(){
        String disp = "Cannon penalties:";
        String light = "Light cannon ";
        String heavy = "Heavy cannon ";
        for(int i = 0 ; i<disp.length() ; i++){
            mat[6][2+i].setChar(disp.charAt(i));
        }
        int index = 0;
        List<CannonPenalty> cannonPenalties = card.getCannonPenalty();
        for (CannonPenalty c : cannonPenalties) {
            char direction = dirToChar(c.getDirection());
            PenaltyType p = c.getType();
            switch(p){
                case LIGHT_CANNON -> {
                    for(int k = 0; k <light.length();k++){
                        mat[7+index][2+k].setChar(light.charAt(k));
                    }
                }
                case HEAVY_CANNON -> {
                    for(int k = 0; k <heavy.length();k++){
                        mat[7+index][2+k].setChar(heavy.charAt(k));
                    }
                }
            }
            mat[7+index][15].setChar(direction);
            index++;
        }
    }
    private char dirToChar(Direction dir){
        return switch (dir) {
            case FRONT  -> '↓';
            case BACK   -> '↑';
            case LEFT   -> '→';
            case RIGHT  -> '←';
        };
    }

    private void computeSmugglers(){
        firePower();
        daysLost();
        computeObtainedGoods();
        computePenaltyGoods();
    }

    private void computePenaltyGoods(){
        int stolenGoods = card.getStolenGoods();
        String disp = "Goods to be lost: " + stolenGoods;
        for(int i = 0; i< disp.length(); i++){
            mat[6][2+i].setChar(disp.charAt(i));
        }
    }

    private void computeObtainedGoods(){
        List<GoodType> goods = card.getGoods();
        int index = 0;
        String disp = "Obtained goods: ";
        for (int i = 0; i<disp.length(); i++){
            mat[10][2+i].setChar(disp.charAt(i));
        }
        for (GoodType good : goods) {
            mat[10][disp.length()+2+index].setChar('□');
            mat[10][disp.length()+2+index].setColor(good.toColor());
            index++;
        }
    }

    private void computeSlaver() {
        firePower();
        daysLost();
        credits();
        loseCrew();
    }

    private void loseCrew(){
        int lose = card.getCrewLost();
        String pen = "lose crew members: " + lose;
        for(int i = 0; i < pen.length(); i++){
            mat[6][2+i].setChar(pen.charAt(i));
        }
    }

    private void credits(){
        int credits = card.getCredits();
        String cr = "gain credits: " + credits;
        for(int i = 0; i< cr.length(); i++){
            mat[10][2+i].setChar(cr.charAt(i));
        }
    }

    private void firePower() {
        int firePower = card.getStrength();
        String power = "fire power: " + firePower;
        for(int i = 0; i< power.length(); i++){
            mat[4][2+i].setChar(power.charAt(i));
        }
    }

    private void computePlanet() {
        List<Planet> planets = card.getPlanets();
        int indexPlanet = 0;
        int startRows = 4;
        int startCols = 2;
        int startGoods = 12;
        for (Planet planet : planets) {
            int indexGood = 0;
            List<GoodType> goods = planet.getGoods();
            for(int k = 0; k < "Planet".length(); k++) {
                mat[startRows+2*indexPlanet][startCols+k].setChar("Planet".charAt(k));
            }
            char digit = (char) ('0' + indexPlanet+1);
            mat[startRows+2*indexPlanet][startCols+7].setChar(digit);
            mat[startRows+2*indexPlanet][startCols+8].setChar(':');
            for (GoodType good : goods) {
                mat[startRows+2*indexPlanet][startGoods+indexGood].setChar('□');
                mat[startRows+2*indexPlanet][startGoods+indexGood].setColor(good.toColor());
                indexGood++;
            }
            indexPlanet++;
        }
        daysLost();
    }

    private void daysLost(){
        int startCols = 2;
        String daysLost = "days lost :";
        for(int f = 0 ; f < daysLost.length(); f++ ) {
            mat[12][startCols+f].setChar(daysLost.charAt(f));
        }
        char digit = (char) ('0'+card.getDaysLost());
        mat[12][startCols+daysLost.length()+1].setChar(digit);
    }

    private void paint(String color) {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                mat[i][j] = new ColoredChar(' ');
                mat[i][j].setColor(color);
            }
        }
    }

    public void drawCard() {
        for (ColoredChar[] riga : mat) {
            for (ColoredChar c : riga) {
                System.out.print(c);
            }
            System.out.println();
        }
    }

    public void computeName(){
        String name = card.getCardName();
        int usableCols = numCols - 2;
        int startCol = 1 + (usableCols - name.length()) / 2;

        for (int i = 0; i < name.length(); i++) {
            mat[1][startCol + i].setChar(name.charAt(i));
        }
    }

    public void computeBorders() {
        for (int i = 1; i < numRows -1; i++) {
            mat[i][0].setChar('║');
            mat[i][numCols -1].setChar('║');
        }
        for (int j = 1; j < numCols -1 ; j++){
            mat[0][j].setChar('═');
            mat[numRows -1][j].setChar('═');
            mat[2][j].setChar('═');
        }
        mat[0][0].setChar('╔');
        mat[0][numCols -1].setChar('╗');
        mat[numRows -1][0].setChar('╚');
        mat[numRows -1][numCols -1].setChar('╝');
        mat[numRows-1][numCols/2-1].setChar(card.getLevel() == 1 ? '1' : '2');

    }

    public static void main(String[] args){
        List<GoodType> goods = new ArrayList<GoodType>();
        goods.add(GoodType.RED);
        goods.add(GoodType.GREEN);
        Planet pl2 = new Planet(goods);
        goods.add(GoodType.YELLOW);
        Planet pl = new Planet(goods);
        List<Planet> list = new ArrayList<>();
        list.add(pl);
        list.add(pl2);
        list.add(pl);
        list.add(pl);
        AdventureCard planets = new Planets(1,2,2,list);
        AdventureCard card = new Stardust(2,2);
        AdventureCard card2 = new Slaver(2,2,6,3,2,4);
        AdventureCard card3 = new Smugglers(2,2,9,5,4,goods);
        SldAdvCard card89 = new SldSmugglers(2,2,4,goods,9,5);
        List<CannonPenalty> penalties = new ArrayList<>();
        penalties.add(new LightCannon(Direction.FRONT));
        penalties.add(new LightCannon(Direction.BACK));
        penalties.add(new HeavyCannon(Direction.LEFT));
        SldAdvCard sld = new SldPirates(1,2,7,5,5,penalties);
        VisualCard visualCard = new VisualCard(sld);
        visualCard.drawCard();
        List<Meteor> meteors = new ArrayList<>();
        meteors.add(new BigMeteor(Direction.FRONT));
        meteors.add(new BigMeteor(Direction.BACK));
        meteors.add(new SmallMeteor(Direction.LEFT));
        //AdventureCard card5 = new MeteorSwarm(1,1,meteors);
        SldAdvCard card5 = new SldMeteorSwarm(1,1,meteors);
        VisualCard visualCard2 = new VisualCard(card5);
        visualCard2.drawCard();
        List<Penalty> pen2 = new ArrayList<>();
        pen2.add(new LoseCrewPenalty(2));
        List<Penalty> pen  = new ArrayList<>();
        pen.add(new LightCannon(Direction.FRONT));
        pen.add(new LightCannon(Direction.BACK));
        pen.add(new HeavyCannon(Direction.LEFT));
        pen.add(new HeavyCannon(Direction.RIGHT));
        List<Penalty> pen3 = new ArrayList<>();
        pen3.add(new LoseGoodsPenalty(4));
        List<CombatLine> combatLines = new ArrayList<>();
        combatLines.add(new CombatLine(Criterion.CREW,pen));
        combatLines.add(new CombatLine(Criterion.FIRE_POWER,pen2));
        combatLines.add(new CombatLine(Criterion.ENGINE_POWER,pen3));
        SldAdvCard card9 = new SldCombatZone(1,1,combatLines);
        VisualCard visualCard9 = new VisualCard(card9);
        visualCard9.drawCard();
        SldAdvCard card10 = new SldAbandonedShip(1,1,4,5,2);
        VisualCard visualCard10 = new VisualCard(card10);
        visualCard10.drawCard();
        SldAdvCard card11 = new SldAbandonedStation(1,1,3,5,goods);
        VisualCard visualCard11 = new VisualCard(card11);
        visualCard11.drawCard();
        VisualCard card3v = new VisualCard(card89);
        card3v.drawCard();
    }
}