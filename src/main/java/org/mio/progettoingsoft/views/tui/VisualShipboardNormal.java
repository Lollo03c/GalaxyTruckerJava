package org.mio.progettoingsoft.views.tui;

import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.Connector;
import org.mio.progettoingsoft.ShipBoard;
import org.mio.progettoingsoft.components.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisualShipboardNormal extends VisualShipboard {

    public VisualShipboardNormal(ShipBoard shipBoard) {
        this(shipBoard.getComponents());
    }
    public VisualShipboardNormal(List<Optional<Component>> components) {
        super(components);
        this.rows = 5;
        this.cols = 7;
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
        energyDepot.removeOneEnergy();

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

        VisualShipboardNormal shipboard = new VisualShipboardNormal(components);
        shipboard.drawShipboard();
    }
}
