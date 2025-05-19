
package org.mio.progettoingsoft.view;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.Component;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ShipCell {
    public static final String RESET = "\u001B[0m";
    public static final String BROWN = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    private ColoredChar[][] cell = new ColoredChar[5][9];
    private Component component;

    public ShipCell(Component component) {
        this.component = component;
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 9; j++)
                cell[i][j] = new ColoredChar(' ');
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ColoredChar[] row : cell) {
            for (ColoredChar cc : row) {
                if (cc != null) {
                    sb.append(cc); // usa il suo toString() che include il colore
                } else {
                    sb.append(" "); // spazio vuoto se null
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void computeCell(){
        computeConnector();
        computeComponent();
        computeBorder();
        //System.out.println(this);
    }

    public void drawCell(){
        this.computeCell();
        System.out.println(this);
    }

    private void computeConnector(){
        String topConn = component.getConnector(Direction.FRONT).toString();
        switch (topConn){
            case "flat" : {
                cell[0][4].setChar('─');
                cell[0][3].setChar('─');
                cell[0][5].setChar('─');
            }
            break;
            case "single" : {
                cell[0][4].setChar('╩');
                cell[0][3].setChar('─');
                cell[0][5].setChar('─');
            }
            break;
            case "double" : cell[0][3].setChar('╩');
                cell[0][4].setChar('─');
                cell[0][5].setChar('╩');
                break;
            case "triple" : {cell[0][3].setChar('╩'); cell[0][4].setChar('╩'); cell[0][5].setChar('╩');}
            break;
            default : cell[0][4].setChar(' ');
                break;
        }

        String rightConn = component.getConnector(Direction.RIGHT).toString();
        switch (rightConn){
            case "flat" : {
                cell[2][8].setChar('│');
                cell[1][8].setChar('│');
                cell[3][8].setChar('│');
            }
            break;
            case "single" : {
                cell[2][8].setChar('╟');
                cell[1][8].setChar('│');
                cell[3][8].setChar('│');}
            break;
            case "double" : {cell[1][8].setChar('╟');
                cell[3][8].setChar('╟');
                cell[2][8].setChar('│');
            }
            break;

            case "triple" : {
                cell[1][8].setChar('╟');
                cell[2][8].setChar('╟');
                cell[3][8].setChar('╟');}
            break;
            default : cell[2][8].setChar(' ');
                break;
        }

        String leftConn = component.getConnector(Direction.LEFT).toString();
        switch (leftConn){
            case "flat" : {
                cell[2][0].setChar('│');
                cell[1][0].setChar('│');
                cell[3][0].setChar('│');
            }
            break;
            case "single" : {
                cell[2][0].setChar('╢');
                cell[1][0].setChar('│');
                cell[3][0].setChar('│');
            }
            break;
            case "double" :{
                cell[1][0].setChar('╢');
                cell[3][0].setChar('╢');
                cell[2][0].setChar('│');}
            break;
            case "triple" : {cell[1][0].setChar('╢'); cell[2][0].setChar('╢'); cell[3][0].setChar('╢');}
            break;
            default : cell[2][0].setChar(' ');
                break;
        }

        String bottomConn = component.getConnector(Direction.BACK).toString();
        switch (bottomConn){
            case "flat" : {
                cell[4][4].setChar('─');
                cell[4][3].setChar('─');
                cell[4][5].setChar('─');
            }
            break;
            case "single" : {
                cell[4][4].setChar('╦');
                cell[4][3].setChar('─');
                cell[4][5].setChar('─');
            }
            break;
            case "double" : {
                cell[4][3].setChar('╦');
                cell[4][5].setChar('╦');
                cell[4][4].setChar('─');
            }
            break;
            case "triple" : {cell[4][3].setChar('╦'); cell[4][4].setChar('╦'); cell[4][5].setChar('╦');}
            break;
            default : cell[2][0].setChar(' ');
                break;
        }
    }



    public void computeComponent(){

        //PIPE, DRILL,SH
        // IELD, DOUBLE_DRILL, ENGINE, DOUBLE_ENGINE,DEPOT,  ENERGY_DEPOT, HOUSING, ALIEN_HOUSING
        ComponentType type = component.getType();
        switch (type){
            case PIPE: {
                cell[1][2].setChar('p');
                cell[1][3].setChar('i');
                cell[1][4].setChar('p');
                cell[1][5].setChar('e');
            }
            break;
            case DRILL :{
                cell[1][2].setChar('d');
                cell[1][3].setChar('r');
                cell[1][4].setChar('i');
                cell[1][5].setChar('l');
                cell[1][6].setChar('l');
                drawDirection();}
            break;
            case DOUBLE_DRILL:{cell[1][1].setChar('d');
                cell[1][2].setChar('o');
                cell[1][3].setChar('u');
                cell[1][4].setChar('b');
                cell[1][5].setChar('l');
                cell[1][6].setChar('e');
                cell[2][2].setChar('d');
                cell[2][3].setChar('r');
                cell[2][4].setChar('i');
                cell[2][5].setChar('l');
                cell[2][6].setChar('l');
                drawDirection();}
            break;
            case SHIELD: {cell[1][1].setChar('s');
                cell[1][2].setChar('h');
                cell[1][3].setChar('i');
                cell[1][4].setChar('e');
                cell[1][5].setChar('l');
                cell[1][6].setChar('d');
                drawDoubleDirection();}
            break;
            case ENGINE: {cell[1][1].setChar('e');
                cell[1][2].setChar('n');
                cell[1][3].setChar('g');
                cell[1][4].setChar('i');
                cell[1][5].setChar('n');
                cell[1][6].setChar('e');
                drawDirection();}
            break;
            case DOUBLE_ENGINE: {
                cell[1][1].setChar('d');
                cell[1][2].setChar('o');
                cell[1][3].setChar('u');
                cell[1][4].setChar('b');
                cell[1][5].setChar('l');
                cell[1][6].setChar('e');
                cell[2][1].setChar('e');
                cell[2][2].setChar('n');
                cell[2][3].setChar('g');
                cell[2][4].setChar('i');
                cell[2][5].setChar('n');
                cell[2][6].setChar('e');
                drawDirection();}
            break;
            case DEPOT: {
                boolean hazard = component.getHazard();
                String color = " ";
                if(hazard){  color = RED;}
                else{  color = BLUE;}
                for (ColoredChar[] row : cell) {
                    for (ColoredChar cc : row) {
                        cc.setColor(color);
                    }
                }

                boolean isBig = component.getBig();
                cell[2][2].setChar('d');
                cell[2][3].setChar('e');
                cell[2][4].setChar('p');
                cell[2][5].setChar('o');
                cell[2][6].setChar('t');
                switch (color){
                    case RED -> {
                        if (isBig) {
                            cell[1][1].setChar('d');
                            cell[1][2].setChar('o');
                            cell[1][3].setChar('u');
                            cell[1][4].setChar('b');
                            cell[1][5].setChar('l');
                            cell[1][6].setChar('e');
                        } else {
                            cell[1][1].setChar('s');
                            cell[1][2].setChar('i');
                            cell[1][3].setChar('n');
                            cell[1][4].setChar('g');
                            cell[1][5].setChar('l');
                            cell[1][6].setChar('e');
                        }
                    }

                    case BLUE -> {
                        if(isBig){
                            cell[1][1].setChar('t');
                            cell[1][2].setChar('r');
                            cell[1][3].setChar('i');
                            cell[1][4].setChar('p');
                            cell[1][5].setChar('l');
                            cell[1][6].setChar('e');
                        }
                        else{
                            cell[1][1].setChar('d');
                            cell[1][2].setChar('o');
                            cell[1][3].setChar('u');
                            cell[1][4].setChar('b');
                            cell[1][5].setChar('l');
                            cell[1][6].setChar('e');
                        }
                    }

                    default -> {
                        cell[1][1].setChar('d');
                        cell[1][2].setChar('e');}
                }

                List<GoodType> storedGoods = component.getStoredGoods();
                drawGoods(storedGoods);
                //Map<GoodType, Integer> storedGoods =component.getStoredGoods();
                //drawGoods(storedGoods);
            }
            break;
            case ENERGY_DEPOT: {
                boolean isTriple = component.getTriple();
                cell[1][1].setChar('e');
                cell[1][2].setChar('n');
                cell[1][3].setChar('e');
                cell[1][4].setChar('r');
                cell[1][5].setChar('g');
                cell[1][6].setChar('y');
                cell[2][1].setChar('d');
                cell[2][2].setChar('e');
                cell[2][3].setChar('p');
                cell[2][4].setChar('o');
                cell[2][5].setChar('t');
                if(isTriple){
                    cell[2][6].setChar('3');
                }
                else {cell[2][6].setChar('2');}
                int storedQuantity = component.getEnergyQuantity();
//                for(int i : storedQuantity)
            }
            break;
            //housing e alien housing da fare perchè non mi è chiara la logica
            case HOUSING: {
                List<GuestType> guests = component.getGuests();
                int humans = (int) guests.stream()
                        .filter(x -> x == GuestType.HUMAN)
                        .count();
                boolean isFirst = component.getId() == 33
                        || component.getId() == 34
                        || component.getId() == 52
                        || component.getId() == 61;
                //int humans = component.getGuestedHuman();
                if (humans == 1){
                    cell[3][4].setChar('o');
                } else if (humans == 2) {
                    cell[3][3].setChar('o');
                    cell[3][5].setChar('o');
                } else {
                    Optional<GuestType> purpleAlien = guests.stream().filter(x -> x == GuestType.PURPLE).findFirst();
                    Optional<GuestType> brownAlien = guests.stream().filter(x -> x == GuestType.BROWN).findFirst();
                    if(purpleAlien.isPresent()){
                        cell[3][4].setChar('●');
                        cell[3][4].setColor(PURPLE);
                    }
                    else if(brownAlien.isPresent()){
                        cell[3][4].setChar('●');
                        cell[3][4].setColor(BROWN);
                    }
                }


                //Map<AlienType, Boolean> guestedAlien = component.getGuestedAlien();

                if (isFirst) {
                    String color = component.getHousingColorById(component.getId()).colorToString();
                    for(ColoredChar[] row : cell){
                        for(ColoredChar cc : row){
                            cc.setColor(color);
                        }
                    }
                    cell[1][1].setChar('s');
                    cell[1][2].setChar('t');
                    cell[1][3].setChar('a');
                    cell[1][4].setChar('r');
                    cell[1][5].setChar('t');
                    cell[1][6].setChar('e');
                    cell[1][7].setChar('r');
                    cell[2][1].setChar('c');
                    cell[2][2].setChar('a');
                    cell[2][3].setChar('b');
                    cell[2][4].setChar('i');
                    cell[2][5].setChar('n');
                }
                else{
                    cell[1][1].setChar('h');
                    cell[1][2].setChar('o');
                    cell[1][3].setChar('u');
                    cell[1][4].setChar('s');
                    cell[1][5].setChar('i');
                    cell[1][6].setChar('n');
                    cell[1][7].setChar('g');
                }
            }
            break;
            case ALIEN_HOUSING:{
                String color = component.getColorAlien().guestToColor();
                for(ColoredChar[] row : cell){
                    for(ColoredChar cc : row){
                        cc.setColor(color);
                    }
                }
                cell[1][1].setChar('a');
                cell[1][2].setChar('l');
                cell[1][3].setChar('i');
                cell[1][4].setChar('e');
                cell[1][5].setChar('n');
                cell[2][1].setChar('m');
                cell[2][2].setChar('o');
                cell[2][3].setChar('d');
                cell[2][4].setChar('u');
                cell[2][5].setChar('l');
                cell[2][6].setChar('e');
            }
            break;
        }
    }
    private void drawGoods(List<GoodType> storedGoods){
        int index = 0;
        for(GoodType goodType : storedGoods){
            cell[3][3+index].setChar('□');
            cell[3][3+index].setColor(goodType.toColor());
            index += 1;
        }
    }

    /*private void drawGoods(Map<GoodType,Integer> storedGoods){
        int index = 0;
        for(GoodType goodType : storedGoods.keySet()){
            if(storedGoods.get(goodType) > 0){
                cell[3][3+index].setChar('□');
                cell[3][3+index].setColor(goodType.toColor());
                index += 1;
            }
        }
    }*/
    private void computeBorder() {
        cell[0][0].setChar('┌');
        cell[0][8].setChar('┐');
        cell[4][0].setChar('└');
        cell[4][8].setChar('┘');
        cell[0][1].setChar('─');
        cell[0][2].setChar('─');
        cell[0][6].setChar('─');
        cell[0][7].setChar('─');
        cell[4][1].setChar('─');
        cell[4][2].setChar('─');
        cell[4][6].setChar('─');
        cell[4][7].setChar('─');
    }


    public ColoredChar[][] getMatrix() {
        return cell;
    }

    private void drawDirection(){
        String direction = component.getDirection().toString();
        switch (direction){
            case "front" : cell[3][4].setChar('↑');
                break;
            case "right" : cell[3][4].setChar('→');
                break;
            case "left" : cell[3][4].setChar('←');
                break;
            case "back" : cell[3][4].setChar('↓');
                break;
            default : cell[3][4].setChar(' ');
                break;
        }
    }

    private void drawDoubleDirection(){
        List<Direction> directions = component.getShieldDirections();
        String direction1 = directions.get(0).toString();
        String direction2 = directions.get(1).toString();
        switch (direction1){
            case "front" : cell[3][3].setChar('↑');
                break;
            case "right" : cell[3][3].setChar('→');
                break;
            case "left" : cell[3][3].setChar('←');
                break;
            case "back" : cell[3][3].setChar('↓');
                break;
            default : cell[3][3].setChar(' ');
                break;
        }
        switch (direction2){
            case "front" : cell[3][5].setChar('↑');
                break;
            case "right" : cell[3][5].setChar('→');
                break;
            case "left" : cell[3][5].setChar('←');
                break;
            case "back" : cell[3][5].setChar('↓');
                break;
            default : cell[3][5].setChar(' ');
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        /*Component depot = new Pipe(1, Connector.DOUBLE, Connector.SINGLE,Connector.TRIPLE, Connector.FLAT);
        Component drill = new DoubleDrill(2,Connector.DOUBLE, Connector.SINGLE,Connector.TRIPLE, Connector.FLAT);
        Component shield = new Shield(3,Connector.DOUBLE, Connector.SINGLE,Connector.TRIPLE, Connector.FLAT);
        Component doubleEngine = new DoubleEngine(3,Connector.DOUBLE, Connector.SINGLE,Connector.TRIPLE, Connector.FLAT);
        Component eng = new Engine(3,Connector.DOUBLE, Connector.SINGLE,Connector.TRIPLE, Connector.FLAT);
        Component depotReal = new Depot(2,true,true,Connector.FLAT,Connector.DOUBLE,Connector.TRIPLE,Connector.SINGLE);
        Component energyDepot = new EnergyDepot(2,true,Connector.FLAT,Connector.DOUBLE,Connector.TRIPLE,Connector.SINGLE);
        Component fullDepot = new Depot(1,false,false,Connector.FLAT,Connector.DOUBLE,Connector.TRIPLE,Connector.SINGLE);
        fullDepot.addGood(GoodType.BLUE);
        fullDepot.addGood(GoodType.GREEN);
        fullDepot.addGood(GoodType.YELLOW);
        Component firstHouse = new Housing(33,Connector.TRIPLE,Connector.TRIPLE,Connector.TRIPLE,Connector.TRIPLE);
        Component house = new Housing(1,Connector.TRIPLE,Connector.SINGLE,Connector.DOUBLE,Connector.FLAT);
        house.addGuest(GuestType.BROWN);

        //house.addAlienType(AlienType.BROWN);
        firstHouse.addHumanMember();
        firstHouse.addHumanMember();
        firstHouse.addHumanMember();
        Component alienHouse = new AlienHousing(4,GuestType.BROWN,Connector.FLAT,Connector.TRIPLE,Connector.DOUBLE,Connector.SINGLE);
        */

        Component fullDepot = new Depot(30,true,false,Connector.FLAT,Connector.DOUBLE,Connector.TRIPLE,Connector.SINGLE);
        fullDepot.addGood(GoodType.BLUE);
        fullDepot.addGood(GoodType.YELLOW);
        fullDepot.addGood(GoodType.GREEN);

        ShipCell cella = new ShipCell(fullDepot);
        cella.drawCell();
        //cella.computeCell();
        //System.out.println(cella);
    }
}