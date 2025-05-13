package org.mio.progettoingsoft.views;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.components.*;
import org.mio.progettoingsoft.model.enums.GameMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisualShipboard {
    private List<Optional<Component>> components;
    private List<Optional<ShipCell>> shipCells;


    public VisualShipboard(ShipBoard shipBoard) {

        components = shipBoard.getComponents();
        //lista di componenti da shipBoard[5][4]
        this.components = components;
        this.shipCells = new ArrayList<>();
        for (Optional<Component> component : components) {
            if (component.isPresent()) {
                ShipCell cell = new ShipCell(component.get());
                cell.computeCell();
                this.shipCells.add(Optional.of(cell));
            }
            else {
                this.shipCells.add(Optional.empty());
            }
        }
    }
    public VisualShipboard(List<Optional<Component>> components) {

        this.components = components;
        this.shipCells = new ArrayList<>();
        for (Optional<Component> component : components) {
            if (component.isPresent()) {
                ShipCell cell = new ShipCell(component.get());
                cell.computeCell();
                this.shipCells.add(Optional.of(cell));
            }
            else {
                this.shipCells.add(Optional.empty());
            }
        }
    }

    public void drawShipboard() {
        int cols = 7;
        int rows = 5;
        int cellHeight = 5;
        int cellWidth = 9;
        int offrow = 5;
        int offcol = 4;

        System.out.print("    "); // spazio per l’indice riga
        for (int col = offcol; col < cols + offcol; col++) {
            String colStr = String.valueOf(col);
            int padLeft = (cellWidth - colStr.length()) / 2;
            int padRight = cellWidth - colStr.length() - padLeft;
            System.out.print(" ".repeat(padLeft) + colStr + " ".repeat(padRight));
        }
        System.out.println();

        for (int row = 0; row < rows; row++) {
            for (int line = 0; line < cellHeight; line++) {
                if (line == cellHeight / 2) {
                    int disp = row+offrow;
                    System.out.printf("%-3d ", disp);
                } else {
                    System.out.print("    ");
                }

                for (int col = 0; col < cols; col++) {
                    int index = row * cols + col;
                    if (index < shipCells.size() && shipCells.get(index).isPresent()) {
                        ColoredChar[][] matrix = shipCells.get(index).get().getMatrix();
                        for (int j = 0; j < cellWidth; j++) {
                            System.out.print(matrix[line][j]);
                        }
                    } else {
                        System.out.print(" ".repeat(cellWidth));
                    }
                }
                System.out.println();
            }
        }
    }



    public static void main(String[] args) throws IOException {
        Component depotReal = new Depot(68,true,true,Connector.TRIPLE,Connector.TRIPLE,Connector.TRIPLE,Connector.SINGLE);
        Component energyDepot = new EnergyDepot(16,true,Connector.FLAT,Connector.DOUBLE,Connector.TRIPLE,Connector.SINGLE);
        Component doubleEngine = new DoubleEngine(92,Connector.DOUBLE, Connector.SINGLE,Connector.TRIPLE, Connector.FLAT);
        Component drill = new DoubleDrill(128,Connector.DOUBLE, Connector.SINGLE,Connector.TRIPLE, Connector.FLAT);
        Component fullDepot = new Depot(18,true,false,Connector.FLAT,Connector.DOUBLE,Connector.TRIPLE,Connector.SINGLE);
        fullDepot.addGood(GoodType.BLUE);
        fullDepot.addGood(GoodType.GREEN);
        fullDepot.addGood(GoodType.YELLOW);
        Component shield = new Shield(149,Connector.DOUBLE, Connector.SINGLE,Connector.TRIPLE, Connector.FLAT);
        Component firstHouse = new Housing(34,Connector.TRIPLE,Connector.TRIPLE,Connector.TRIPLE,Connector.TRIPLE);
        Component house = new Housing(40,Connector.TRIPLE,Connector.SINGLE,Connector.DOUBLE,Connector.FLAT);
        Component house2 = new Housing(41,Connector.TRIPLE,Connector.FLAT,Connector.DOUBLE,Connector.FLAT);
        house2.addAllowedGuest(GuestType.BROWN);
        house2.addGuest(GuestType.BROWN);

        house.addAllowedGuest(GuestType.PURPLE);
        house.addGuest(GuestType.PURPLE);

        firstHouse.addHumanMember();
        firstHouse.addHumanMember();
        firstHouse.addHumanMember();
        Component alienHouse = new AlienHousing(137,GuestType.BROWN,Connector.FLAT,Connector.TRIPLE,Connector.DOUBLE,Connector.SINGLE);
        Component alienHouse12 = new AlienHousing(143,GuestType.PURPLE,Connector.FLAT,Connector.TRIPLE,Connector.DOUBLE,Connector.SINGLE);


        List<Optional<Component>> components = new ArrayList<>();
        components.add(Optional.empty());
        components.add(Optional.empty());
        components.add(Optional.of(energyDepot));
        components.add(Optional.empty());
        components.add(Optional.of(drill));
        components.add(Optional.empty());
        components.add(Optional.empty());
        components.add(Optional.empty());
        components.add(Optional.of(doubleEngine));
        components.add(Optional.of(depotReal));
        components.add(Optional.of(house2));
        components.add(Optional.of(depotReal));
        components.add(Optional.of(depotReal));
        components.add(Optional.empty());
        components.add(Optional.of(doubleEngine));
        components.add(Optional.of(drill));
        components.add(Optional.of(alienHouse));
        components.add(Optional.of(firstHouse));
        components.add(Optional.of(shield));
        components.add(Optional.of(doubleEngine));
        components.add(Optional.of(fullDepot));
        components.add(Optional.of(depotReal));
        components.add(Optional.of(fullDepot));
        components.add(Optional.of(depotReal));
        components.add(Optional.of(depotReal));
        components.add(Optional.of(drill));
        components.add(Optional.of(depotReal));
        components.add(Optional.of(doubleEngine));
        components.add(Optional.of(shield));
        components.add(Optional.of(depotReal));
        components.add(Optional.of(house));
        components.add(Optional.empty());
        components.add(Optional.of(depotReal));
        components.add(Optional.of(house2));
        components.add(Optional.of(alienHouse12));

        VisualShipboard shipboard = new VisualShipboard(components);
        shipboard.drawShipboard();
    }
}
