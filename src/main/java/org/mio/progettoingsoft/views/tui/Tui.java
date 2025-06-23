package org.mio.progettoingsoft.views.tui;

import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.*;
import org.mio.progettoingsoft.advCards.sealed.*;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.exceptions.CannotRotateHourglassException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.InvalidCordinate;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.model.events.Event;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.utils.Logger;
import org.mio.progettoingsoft.views.View;

import javax.security.auth.login.LoginException;
import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static org.mio.progettoingsoft.views.tui.CircuitCell.GREEN;

public class Tui implements View {
    private Scanner scanner = new Scanner(System.in);
    private final ClientController controller;

    private final Object lockView = new Object();

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";

    private boolean firstBuilding;

    private final BlockingQueue<GameState> statesQueue = new LinkedBlockingQueue<>();

    public Tui() {
        controller = ClientController.getInstance();
        controller.addPropertyChangeListener(this);
        this.firstBuilding = true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "gameState" -> {
                statesQueue.add((GameState) evt.getNewValue());
            }
        }
    }

    @Override
    public void run() {
        this.updateTui(GameState.START);
        try {
            while (true) {
                GameState state = statesQueue.take();
                updateTui(state);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTui(GameState state) {
        switch (state) {
            case START -> printConnectionMenu();
            case NICKNAME -> askNickname();
            case WAITING -> checkEndBuilding();

            case GAME_MODE -> printGameModeMenu();
            case GAME_START -> {
                System.out.println("game started");
                printPlayersName();
                controller.setState(GameState.WAITING);
            }
            case WRONG_POSITION -> {
                System.out.println(RED + "Current position is already occupied" + RESET);
                controller.setState(GameState.CHOOSE_POSITION);
            }

            case BUILDING_SHIP -> {
                buildingShipMenu();
            }

            case COMPONENT_MENU -> {
                componentMenu();
            }

            case ADD_COMPONENT -> addComponent();

            case VIEW_SHIP_BUILDING -> viewShipBuilding();

            case DRAW_UNCOVERED_COMPONENTS -> drawUncoveredComponents();

            case UNABLE_DECK -> {
                System.out.println("Deck is already been taken");
                controller.setState(GameState.BUILDING_SHIP);
            }

            case CHOICE_BUILT -> {
                chooseBuiltShip();
            }

            case FINISH_HOURGLASS -> {
                controller.setPendingHourglass(false);
                System.out.println(GREEN + "Hourglass has finished its cycle number : " + controller.getHourglassCounter() +RESET);
                if(controller.getFinishedBuilding()){
                    controller.setState(GameState.END_BUILDING);
                }
            }

            case FINISH_LAST_HOURGLASS -> {
                //todo : problema quando setto lo stato a choose_position dato che prima lo stato era a ship_building o qualcosa
                //di simile e deve processare l'input mi genera un'eccezione
                if(!controller.getFinishedBuilding()){
                    System.out.println(GREEN + "Last Hourglass is terminated : the time to build your shipBoard is over!"+ RESET);
                    if(controller.getFlyBoard().getMode().equals(GameMode.NORMAL)){
                        controller.handleBuildingShip(6);
                    }
                }
                else{
                    controller.setState(GameState.END_BUILDING);
                }
            }

            case INVALID_SHIP_CHOICE -> {
                System.out.println("Invalid ship choice");
                controller.setState(GameState.CHOICE_BUILT);
            }

            case VIEW_DECK -> viewDeck();

            case VIEW_DECKS_LIST -> viewDecksList();

            case VIEW_BOOKED -> viewBookedComponents();
            case SWITCH_BOOKED -> switchBookedComponents();
            case CHOOSE_POSITION -> {
                System.out.println("Choose position");
                printChoosePosition();
            }
            case END_BUILDING -> {
                endBuildingMenu();
            }

            case UNABLE_UNCOVERED_COMPONENT -> {
                System.out.println("Component is already been taken");
                controller.setState(GameState.BUILDING_SHIP);
            }

            case ERROR_NICKNAME -> {
                System.out.println("Nickname already taken. Try Something else\n");
                controller.setState(GameState.NICKNAME);
            }

            case ERROR_PLACEMENT -> {
                System.out.println("Invalid Position. Try again.\n\n");
                controller.setState(GameState.ADD_COMPONENT);
            }

            case DRAW_CARD -> {
                printWaitingTheLeader();
            }

            case YOU_CAN_DRAW_CARD -> printDrawCardMenu();

            case NEW_CARD -> printNewCard();

            case CARD_EFFECT -> cardEffect();
        }
    }

    private void endBuildingMenu(){
        System.out.println("Waiting for other players" + RESET);
        if (!controller.getFinishedLastHourglass() && !controller.getPendingHourglass()) {
            System.out.println("Type \"r\" to rotate hourglass");
            String input = " ";
            while(!input.equalsIgnoreCase("r")){
                input = scanner.nextLine();
            }
            try{
                controller.rotateHourglass();
            } catch (CannotRotateHourglassException e) {
                System.out.println(RED + e.getMessage() + RESET);
                controller.setState(GameState.END_BUILDING);
            } catch (RuntimeException e) {
                Throwable cause = e.getCause();
                if (cause instanceof CannotRotateHourglassException) {
                    System.out.println(RED + cause.getMessage() + RESET);
                } else {
                    throw e;
                }
                controller.setState(GameState.END_BUILDING);
            }
        }
    }

    /**
     * First message showed to the client when he starts.
     * The method asks and waits for the choice (1: RMI, 2: socket)
     */
    private void printConnectionMenu() {
        int choice = -1;
        String input = "";

        while (choice < 1 || choice > 2) {
            System.out.println("Select connection type: ");
            System.out.println("1 : RMI");
            System.out.println("2 : Socket");
            System.out.print("Make your choice: ");

            input = scanner.nextLine();

            try {
                choice = Integer.parseInt(input);

                if (choice < 1 || choice > 2) {
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid choice!" + RESET);
            }
        }

        boolean isRmi = choice == 1;
        controller.connectToServer(isRmi);
        clearConsole();
    }

    private void printWaitingTheLeader() {
        System.out.println("The effect of the card is over, this is the actual circuit");
        synchronized (controller.getFlyboardLock()) {
            controller.getFlyBoard().drawScoreboard();
            controller.getFlyBoard().drawCircuit();
        }
        System.out.println("Waiting for the leader to draw a new card");
    }

    private void printNewCard() {
        System.out.println("A new card has been drawn");
        controller.getPlayedCard().disegnaCard();
    }

    private void printChoosePosition() {
        List<Integer> availablePlaces = controller.getAvailablePlacesOnCircuit();
        String input = "";
        int choice = -1;
        int k = 0;
        while (!availablePlaces.contains(choice)) {
            controller.getFlyBoard().drawCircuit();
            System.out.println("In which of these available position do you want to start ?");
            for (Integer i : availablePlaces) {
                k = FlyBoardNormal.indexToPosition(i);
                System.out.println(k);
            }
            input = scanner.nextLine();
            try {
                choice = Integer.parseInt(input);
                choice = FlyBoardNormal.positionToIndex(choice);
                if (!availablePlaces.contains(choice)) {
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid choice!" + RESET);
            }
        }
        controller.choosePlace(choice);
    }

    private void printDrawCardMenu() {
        System.out.println("The effect of the card is over, this is the actual circuit");
        synchronized (controller.getFlyboardLock()) {
            controller.getFlyBoard().drawScoreboard();
            controller.getFlyBoard().drawCircuit();
        }
        String input = "";
        while (!input.equalsIgnoreCase("d")) {
            System.out.println("You are the leader! You can draw a Card, type \"d\" to draw a Card");
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("d")) {
                controller.drawNewAdvCard();
            }
        }
    }

    public void cardEffect() {
        Logger.debug("[cardEffect] chiamato con stato: " + controller.getCardState());
        switch (controller.getCardState()) {
            case ENGINE_CHOICE -> {
                engineChoice();
            }

            case ACCEPTATION_CHOICE -> askAcceptEffect();

            case CREW_REMOVE_CHOICE -> crewRemove();

            case DRILL_CHOICE -> drillChoice();

            case PLANET_CHOICE -> planetChoice();

            case COMPARING -> {
                SldAdvCard card = controller.getPlayedCard();
                switch (card){
                    default -> System.out.println(" COMPARING NOT IMPLEMENTED FOR " + card.getCardName());
                }
            }

            case DICE_ROLL -> rollDice();

            case WAITING_ROLL -> waitingRoll();

            case SHIELD_SELECTION -> shieldSelection();

            case ASK_ONE_DOUBLE_DRILL -> askOneDoubleDrill();

            case GOODS_PLACEMENT -> goodPlacement();

            case STARDUST_END -> stardustEndInfo();

            case EPIDEMIC_END -> epidemicEndInfo();

            case FINALIZED -> {

            }
        }
    }

    public void compareFirePower(){
        SldAdvCard card = controller.getPlayedCard();
        ShipBoard shipBoard = controller.getShipBoard();
        FlyBoard flyBoard = controller.getFlyBoard();
//        int slaverStrength = card.getStrength();
        double baseStrength = shipBoard.getBaseFirePower();
//        List<Cordinate> drillCords = shipBoard.getDoubleDrills();
        int ris = card.comparePower(flyBoard, flyBoard.getPlayerByUsername(controller.getNickname()));
        if (ris > 0) {
            System.out.println("your baseStrength is:" +baseStrength + " and is bigger than the card strength");
            System.out.println("do you want to activate any double drill  ? y/n ");
        }else if(ris == 0){
            System.out.println("Your baseStrength is the same of the card's one, do you want to activate any double drill  ? y/n");
        }
        else{
            System.out.println("Your baseStrength is lower than the card's one,do you want to activate any double drill  ? y/n ");
        }
        String input = "";
        while (!input.equals("y") && !input.equals("n")) {
            input = scanner.nextLine();
            if(input.equals("y")){
                drillChoice();
            }else if (input.equals("n")){
                drillChoice();
            }
        }
    }

    public void planetChoice() {
        System.out.println("Do you want to land on any planet? y/n");
        String input = "";
        while(!input.equals("y") && !input.equals("n")) {
            input = scanner.nextLine();
            if(!input.equals("y") && !input.equals("n")) {
                System.out.println(RED + "Invalid choice!" + RESET);
            }
        }
        if(input.equals("n")){
            controller.landOnPlanet(-1);
        }
        else {
            SldAdvCard card = controller.getPlayedCard();
            List<Planet> planets = card.getPlanets();
            List<Planet> availablePlanets = new ArrayList<>();
            availablePlanets = planets.stream().filter(x->!x.getPlayer().isPresent()).collect(Collectors.toList());
            System.out.println("Which planet do you want to land on?");
            for (Planet planet : availablePlanets) {
                System.out.println(planets.indexOf(planet)+1);
            }
            int numPlanet = 0;
            while (true) {
                try {
                    numPlanet = scanner.nextInt();
                    scanner.nextLine();
                    if (numPlanet >= 1 && numPlanet <= planets.size() && availablePlanets.contains(planets.get(numPlanet - 1))) {
                        break;
                    } else {
                        System.out.println(RED + "Invalid choice!" + RESET);
                    }
                } catch (InputMismatchException e) {
                    System.out.println(RED + "Please enter a number!" + RESET);
                    scanner.next();
                }
            }
            int choice = numPlanet - 1;
            controller.landOnPlanet(choice);
            // ho messo qua la consegna delle merci come ha fatto toni su abandonedStation, non so se va bene
        }

    }

    /**
     * Message shown to the client after the registration success, asks for the nickname
     */
    private void askNickname() {
        String nickname = "";

        while (true) {
            System.out.print("Insert your nickname: ");
            nickname = scanner.nextLine();

            if (!nickname.isEmpty()) {
                break;
            }

            System.out.println(RED + "Nickname cannot be empty!" + RESET);
        }

        controller.handleNickname(nickname);
        clearConsole();
    }

    /**
     * Message to ask the client for game settings (only if the player is the first joining that match)
     */
    private void printGameModeMenu() {
        int nPlayers = -1;
        int choice = -1;
        String input = "";

        while (nPlayers < 2 || nPlayers > 4) {
            System.out.print("Insert number of players of the game (2-4): ");

            input = scanner.nextLine();

            try {
                nPlayers = Integer.parseInt(input);

                if (nPlayers < 2 || nPlayers > 4) {
                    System.out.println(RED + "Invalid number of players!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid number of players!" + RESET);
            }
        }

        while (choice < 1 || choice > 2) {
            System.out.println("Select game mode: ");
            System.out.println("1 : Easy Mode");
            System.out.println("2 : Normal Mode");
            System.out.print("Make your choice: ");

            input = scanner.nextLine();

            try {
                choice = Integer.parseInt(input);

                if (choice < 1 || choice > 2) {
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid choice!" + RESET);
            }
        }

        GameInfo gameInfo = new GameInfo(-1, choice == 1 ? GameMode.EASY : GameMode.NORMAL, nPlayers);
        controller.handleGameInfo(gameInfo);
        clearConsole();
    }

    private void checkEndBuilding() {

    }

    /**
     * Message to notify the clients the start of the game
     */
    private void printStartGameInfo() {
        System.out.println(BLUE + "The game has started!" + RESET);
        System.out.println("Players:");

        printPlayersName();

        //controller.setState(GameState.WAITING);
    }

    /**
     * Message to show building ship menu
     */
    private void buildingShipMenu() {
        GameMode mode = controller.getFlyBoard().getMode();
        if (firstBuilding) {
            printStartGameInfo();
            System.out.println(BLUE + "It's time to build your ship!" + RESET);
            firstBuilding = false;
            //decido di far partire la clessidra dal client con la firstHousing blu che c'è in ogni partitaAdd commentMore actions
            if(controller.getShipBoard().getHousingColor().equals(HousingColor.BLUE)  && mode.equals(GameMode.NORMAL)){
                controller.startHourglass();
            }
        }
        int choice = -1;
        String input = "";

        System.out.println("1 : Pick covered component");
        System.out.println("2 : Pick uncovered component");
        System.out.println("3 : Pick booked component");
        System.out.println("4 : View other player's ship");

        if (mode.equals(GameMode.NORMAL)) {
            System.out.println("5 : Look at decks");
            System.out.println("6 : End ship building");
            System.out.println("7 : Load automatic shipboard");
            System.out.println("8 : Rotate hourglass");
        } else {
            System.out.println("5 : end building ship");
        }
        System.out.print("Make your choice: ");

        input = scanner.nextLine();

        try {
            choice = Integer.parseInt(input);

            if (choice < 1) {
                throw new Exception("");
            } else if (mode.equals(GameMode.NORMAL) && choice > 8) {
                throw new Exception("");
            } else if (mode.equals(GameMode.EASY) && choice > 5) {
                throw new Exception("");
            }
        } catch (Exception e) {
            System.out.println(RED + "Invalid choice!" + RESET);
            controller.setState(GameState.BUILDING_SHIP);
            return;
        }
        try {
            if(!controller.getFinishedLastHourglass()){
                controller.handleBuildingShip(choice);
            }
        } catch (CannotRotateHourglassException e) {
            System.out.println(RED + e.getMessage() + RESET);
            controller.setState(GameState.BUILDING_SHIP);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof CannotRotateHourglassException) {
                System.out.println(RED + cause.getMessage() + RESET);
            } else {
                throw e;
            }
            controller.setState(GameState.BUILDING_SHIP);
        }
        clearConsole();
    }

    /**
     * Prints the menu to add component, asks for row, column and rotation
     */
    private void addComponent() {
        String input = "";

        int row = 0;
        while (row < 5 || row > 9) {
            System.out.print("Insert row: ");

            input = scanner.nextLine();

            try {
                row = Integer.parseInt(input);

                if (row < 5 || row > 9) {
                    System.out.println(RED + "Invalid row!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid row!" + RESET);
            }
        }

        int column = 0;
        while (column < 4 || column > 10) {
            System.out.print("Insert column: ");

            input = scanner.nextLine();

            try {
                column = Integer.parseInt(input);

                if (column < 4 || column > 10) {
                    System.out.println(RED + "Invalid column!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid column!" + RESET);
            }
        }

        int rotation = -1;
        while (rotation < 0 || rotation > 3) {
            System.out.print("Insert rotation: ");

            input = scanner.nextLine();

            try {
                rotation = Integer.parseInt(input);

                if (rotation < 0 || rotation > 3) {
                    System.out.println(RED + "Invalid rotation!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid rotation!" + RESET);
            }
        }

        ShipBoard ship = controller.getShipBoard();
        Cordinate cord = new Cordinate(row - ship.getOffsetRow(), column - ship.getOffsetCol());
        try {
            controller.addComponent(cord, rotation);
        } catch (InvalidCordinate e) {
            controller.setState(GameState.ERROR_PLACEMENT);
        }
        clearConsole();
    }

    private void viewShipBuilding() {
        System.out.println("These are the players: ");
        printPlayersName();

        String chosenPlayer = "";

        while (true) {
            System.out.print("Insert nickname to look at: ");
            chosenPlayer = scanner.nextLine();

            try {
                if (chosenPlayer.isEmpty()) {
                    System.out.println(RED + "Invalid nickname!" + RESET);
                } else {
                    controller.getFlyBoard().getPlayerByUsername(chosenPlayer).getShipBoard().drawShipboard();
                    break;
                }
            } catch (IncorrectFlyBoardException e) {
                System.out.println(RED + "Invalid nickname!" + RESET);
            }
        }


        controller.setState(GameState.BUILDING_SHIP);
    }

    private void componentMenu() {
        System.out.println("This is the component you've drawn:");
        new ShipCell(controller.getFlyBoard().getComponentById(controller.getInHandComponent())).drawCell();
        controller.getShipBoard().drawShipboard();

        int choice = -1;
        String input = "";

        while (choice < 1 || choice > 3) {
            System.out.println("1 : Insert in the shipboard");
            System.out.println("2 : Put back in the deck");
            System.out.println("3 : Save for later");
            System.out.print("Make your choice: ");

            input = scanner.nextLine();

            try {
                choice = Integer.parseInt(input);

                if (choice < 1 || choice > 3) {
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid choice!" + RESET);
            }
        }

        if (choice == 1) {
            controller.setState(GameState.ADD_COMPONENT);
        } else if (choice == 2) {
            controller.discardComponent();
        } else if (choice == 3) {
            controller.bookComponent();
        }
    }

    private void drawUncoveredComponents() {
        int count = 1;
        if (controller.getFlyBoard().getUncoveredComponents().isEmpty()) {
            System.out.println("No uncovered Components");
            controller.setState(GameState.BUILDING_SHIP);
            return;
        }

        for (int idComp : controller.getFlyBoard().getUncoveredComponents()) {
            System.out.println("Component #" + idComp);
            Component component = controller.getFlyBoard().getComponentById(idComp);
            new ShipCell(component).drawCell();
        }

        System.out.print("Select component to draw (-1 to null) : ");
        int chosen = Integer.parseInt(scanner.nextLine());

        //todo controllo dell'input

        if (chosen == -1)
            controller.setState(GameState.BUILDING_SHIP);
        else
            controller.drawUncovered(chosen);
    }

    private void viewDecksList() {
        System.out.println("Available decks : ");
        for (int numberDeck : controller.getFlyBoard().getAvailableDecks()) {
            System.out.println("Deck #" + numberDeck);
        }
        System.out.print("Choose deck number : ");
        int chosen = Integer.parseInt(scanner.nextLine());

        controller.bookDeck(chosen);
    }

    private void viewDeck() {
        System.out.println("hai in mano il deck #" + controller.getInHandDeck());
        System.out.println("premere invio per continuare");

        String buffer = scanner.nextLine();
        controller.freeDeck();
    }

    private void printPlayersName() {
        synchronized (controller.getFlyboardLock()) {
            FlyBoard flyBoard = controller.getFlyBoard();
            int count = 1;

            for (Player player : flyBoard.getPlayers()) {
                System.out.println((count++) + " " + player.getNickname() + " : " + player.getColor());
            }
        }
    }

    private void viewBookedComponents() {
        List<Optional<Integer>> bookedComponents = controller.getShipBoard().getBookedComponents();
        int count = 1;

        for (Optional<Integer> booked : bookedComponents) {
            if (booked.isEmpty())
                continue;

            System.out.println("Component # " + count++);
            Component component = controller.getFlyBoard().getComponentById(booked.get());
            new ShipCell(component).drawCell();
        }

        System.out.print("Select component to draw (-1 to null) : ");
        int chosen = Integer.parseInt(scanner.nextLine());

        //todo controllo dell'input
        controller.choseBookedComponent(chosen);
    }

    private void switchBookedComponents() {
        List<Integer> possibles = new ArrayList<>();

        List<Optional<Integer>> bookedComponents = controller.getShipBoard().getBookedComponents();
        for (Optional<Integer> optComp : bookedComponents) {
            if (optComp.isEmpty())
                continue;

            possibles.add(optComp.get());
            System.out.println("Component #" + optComp.get());
            new ShipCell(controller.getFlyBoard().getComponentById(optComp.get())).drawCell();
        }

        int chosenComp;
        while (true) {
            System.out.println("Select the component to switch (enter to escape) : ");
            String string = scanner.nextLine();

            if (string.equals("")) {
                controller.setState(GameState.COMPONENT_MENU);
                return;
            } else {
                try {
                    chosenComp = Integer.parseInt(string);
                    if (!possibles.contains(chosenComp))
                        throw new NumberFormatException("");

                    break;
                } catch (NumberFormatException e) {
                    System.out.println(RED + "Invalid chosen component" + RESET);
                    throw new RuntimeException(e);
                }
            }
        }

        controller.bookComponent(possibles.indexOf(chosenComp));
    }

    private void chooseBuiltShip() {
        controller.builtDefault();
    }

    private void engineChoice() {
        int maxAvailable;
        synchronized (controller.getShipboardLock()) {
            ShipBoard shipBoard = controller.getShipBoard();
            maxAvailable = Integer.min(shipBoard.getQuantBatteries(), shipBoard.getDoubleEngine().size());
        }
        int activated = -1;
        while (activated < 0 || activated > maxAvailable) {
            System.out.println("Select the number of double engines to activate (max " + maxAvailable + " : )");
            try {
                activated = scanner.nextInt();
            }catch(InputMismatchException e){
                scanner.next();
                activated = -1;
            }
            if (activated >= 0 && activated <= maxAvailable)
                controller.activateDoubleEngine(activated);
            else
                System.out.println(RED + "Invalid activated engine" + RESET);
        }
    }

    private void drawCard() {
        controller.drawNewAdvCard();
    }

    private void askAcceptEffect(){
        SldAdvCard card = controller.getPlayedCard();
        String choice = "";
        while (!choice.equals("y") && !choice.equals("n")) {
            System.out.println("Do you want to accept the card effect (y/n) : ");

            choice = scanner.nextLine().trim().toLowerCase();
        }

        if (choice.equals("n")){
            controller.skipEffect();
            return;
        }

        switch (card){
            case SldAbandonedShip abandonedShip -> {
                controller.setCardState(CardState.CREW_REMOVE_CHOICE);
            }

            case SldAbandonedStation abandonedStation -> {
                controller.applyEffect();
            }

            case SldSmugglers sldSmugglers ->{
                controller.setState(GameState.DRILL_CHOICE);
            }

            case SldSlavers sldSlavers  -> {
                controller.applyEffect();
            }

            default -> Logger.error("Effetto non permesso dalla carta");
        }
    }

    private void crewRemove() {
        SldAdvCard card = controller.getPlayedCard();
        Logger.debug("sono in crewRemove");
        int toRemove = -1;

        switch (controller.getPlayedCard()) {
            case SldCombatZone combatZone ->{
                for (CombatLine line : combatZone.getLines()){
                    if (line.getPenalties().getFirst().getType().equals(PenaltyType.CREW))
                        toRemove = line.getPenalties().getFirst().getAmount();
                }
            }
            default -> {
                toRemove = card.getCrewLost();
            }
        }
        List<Cordinate> crewPositionsToRemove = new ArrayList<>();

        System.out.println("Now you have to select " + toRemove + " guest, the selected ones will be deleted from your shipBoard");


        System.out.println("To select a guest enter the associated row and column");
        controller.getShipBoard().drawShipboard();
        ShipBoard shipBoard = controller.getShipBoard();
        int removed = 0;

        while (removed < toRemove){
            System.out.println("\nCrew member #" + removed + 1);
            String input = "";
            int row = 0;
            while (row < 5 || row > 9) {
                System.out.print("Insert row: ");

                input = scanner.nextLine();

                try {
                    row = Integer.parseInt(input);

                    if (row < 5 || row > 9) {
                        System.out.println(RED + "Invalid row!" + RESET);
                    }
                } catch (Exception e) {
                    System.out.println(RED + "Invalid row!" + RESET);
                }
            }

            int column = 0;
            while (column < 4 || column > 10) {
                System.out.print("Insert column: ");

                input = scanner.nextLine();

                try {
                    column = Integer.parseInt(input);

                    if (column < 4 || column > 10) {
                        System.out.println(RED + "Invalid column!" + RESET);
                    }
                } catch (Exception e) {
                    System.out.println(RED + "Invalid column!" + RESET);
                    column = 0;
                }
            }

            int offsetRow = controller.getShipBoard().getOffsetRow();
            int offsetCol = controller.getShipBoard().getOffsetCol();

            Cordinate cord = new Cordinate(row - offsetRow, column - offsetCol);
            if (shipBoard.getOptComponentByCord(cord).isEmpty()) {
                System.out.println("Invalid cordinate");
                continue;
            }
            if (shipBoard.getOptComponentByCord(cord).get().getGuests().isEmpty()){
                System.out.println("This component has no guest to eliminate");
                continue;
            }
            if (shipBoard.getOptComponentByCord(cord).get().getGuests().size() == (int)crewPositionsToRemove.stream().filter(c -> c.equals(cord)).count()){
                System.out.println("This component has no guests reamining to eliminate");
                continue;
            }

            crewPositionsToRemove.add(new Cordinate(row - offsetRow, column - offsetCol));
            removed++;
        }

        controller.removeCrew(crewPositionsToRemove);
    }

    private void goodPlacement(){
        SldAdvCard card = controller.getPlayedCard();
        List<GoodType> toInsert = new ArrayList<>();
        switch (card){
            case SldPlanets planets -> {
                toInsert = controller.getPlanetGoods();
            }
            default -> toInsert = controller.getGoodsToInsert();
        }
        ShipBoard shipBoard = controller.getShipBoard();

        String choice = "";

        Logger.info("GOOD_PLACEMENT menu");

        shipBoard.drawShipboard();

        System.out.println("The following goods have to be placed");
        for (GoodType type : toInsert)
            System.out.println(type);

        System.out.println("\n1. Insert a good in the shipboard");
        System.out.println("2. Remove a good in the shipboard");
        System.out.println("3, End placement (the good yet to placed will be discarded");

        choice = scanner.nextLine();
        if (choice.equals("1")){
            System.out.println("Available goods in your hand");
            for (GoodType type : toInsert){
                System.out.print(type + "\t");
            }
            System.out.println("Select the type :");
            String chosenType = scanner.nextLine().trim().toUpperCase();

            Logger.debug("good selezionato");
            GoodType type = GoodType.stringToGoodType(chosenType);

            List<Cordinate> availableDepots = shipBoard.getAvailableDepots(type);

            if (! availableDepots.isEmpty()) {
                for (int i = 0; i < availableDepots.size(); i++) {
                    System.out.println(i + 1 + ". " + availableDepots.get(i));
                }
                System.out.println("Select the depot by its number : ");
                int chosenDepot = Integer.parseInt(scanner.nextLine().trim());

                Component comp = shipBoard.getOptComponentByCord(availableDepots.get(chosenDepot - 1)).get();

                controller.addGood(comp.getId(), type);
            }
            else{
                System.out.println("not enough space for the good chosen. Try to remove one first");
                controller.setCardState(CardState.GOODS_PLACEMENT);
            }
        }
        else if (choice.equals("2")){
            List<Cordinate> depotCords = new ArrayList<>();
            Iterator<Cordinate> cordinateIterator = Cordinate.getIterator();

            while (cordinateIterator.hasNext()){
                Cordinate cord = cordinateIterator.next();
                if (shipBoard.getOptComponentByCord(cord).isEmpty())
                    continue;

                List<GoodType> goods = shipBoard.getOptComponentByCord(cord).get().getStoredGoods();
                if (! goods.isEmpty())
                    depotCords.add(cord);
            }

            for (int i = 0; i < depotCords.size(); i++){
                System.out.println(i+1 + ". " + depotCords.get(i));
            }
            System.out.print("Select a housing by its number :");
            int chosenHousing = Integer.parseInt(scanner.nextLine().trim());
            Component depot = shipBoard.getOptComponentByCord(depotCords.get(chosenHousing - 1)).get();
            System.out.println("Available goods to remove");
            for (GoodType type : depot.getStoredGoods()){
                System.out.println(type);
            }
            System.out.print("Select a good to remove : ");
            GoodType type = GoodType.stringToGoodType(scanner.nextLine().trim().toUpperCase());

            controller.removeGood(depot.getId(), type);

            System.out.println();
        }
        else if (choice.equals("3")){
            controller.skipEffect();
        }

    }

    private void drillChoice(){
        ShipBoard ship = controller.getShipBoard();
        ship.drawShipboard();
        SldAdvCard card = controller.getPlayedCard();
        int energyAvailable = ship.getQuantBatteries();
        double power = ship.getBaseFirePower();

        List<Cordinate> drillCords = ship.getDoubleDrills();
        System.out.println("These are the available double drills in your ShipBoard:");
        for (int i = 0; i < drillCords.size(); i++){
            System.out.println(i+1 + ". " + drillCords.get(i));
        }
        List<Cordinate> activatedDrills = new ArrayList<>();

        boolean stopAsking = false;
        while ( activatedDrills.size() < energyAvailable && activatedDrills.size() < drillCords.size() && !stopAsking) {
            System.out.println("You currently have " + energyAvailable + " batteries");
            System.out.println("Actual fire power : " + power);

            System.out.print("Select double drill to activate (0 to exit) : ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (activatedDrills.contains(choice))
                    throw new IllegalArgumentException("");
                if (choice < 0 || choice > drillCords.size())
                    throw new IllegalArgumentException("");

                if (choice == 0)
                    stopAsking = true;
                else {
                    activatedDrills.add(drillCords.get(choice - 1));
                    power += ship.getOptComponentByCord(drillCords.get(choice - 1)).get().getFirePower(true);
                    energyAvailable--;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Try again.");
            }
        }

        controller.activateDoubleDrills(activatedDrills);

    }

    private void rollDice(){
        System.out.println("You are the leader. Press enter to roll the dices");
        String pressed = scanner.nextLine();

        Random random = new Random();
        int first = random.nextInt(6) + 1;
        int second = random.nextInt(6) + 1;


//        controller.setRollResult(first + second);

//        //todo da cancellare questa riga
//        int first = 3;
//        int second = 3;
        controller.setRollResult(first, second);
    }

    private void waitingRoll(){
        System.out.println("Waiting for the leader to roll the dices");
    }

    private void shieldSelection(){
        SldAdvCard card = controller.getPlayedCard();
        ShipBoard shipBoard = controller.getShipBoard();
        Direction direction = null;

        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                Meteor meteor = controller.getMeteor();
                direction = meteor.getDirection();
            }

            case SldPirates pirates -> {
                CannonPenalty cannon = controller.getCannon();
                direction = cannon.getDirection();
            }

            default -> Logger.error("caso non previsto");
        }

        boolean possibleToActivate = shipBoard.getQuantBatteries() > 0 && shipBoard.coveredByShield(direction);

        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                Meteor meteor = controller.getMeteor();
                System.out.println("Expoxed connector on " + meteor.getCordinateHit());

            }

            case SldPirates sldPirates -> {
                CannonPenalty cannon = controller.getCannon();
                System.out.println("Light cannot hit component in " + cannon.getCordinateHit());
            }

            default -> Logger.error("caso non previsto");
        }

        String choice = "";
        if (possibleToActivate){
            while (choice.equals("")) {
                System.out.println("Activate a shield to protect (y/n) : ");
                choice = scanner.nextLine().trim().toLowerCase();

                if (!(choice.equals("y") || choice.equals("n"))){
                    choice = "";
                    continue;
                }


            }
        }

        boolean destroyedComp = !choice.equals("y");


        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                Meteor meteor = controller.getMeteor();
                controller.advanceMeteor(destroyedComp, !destroyedComp);
            }

            case SldPirates pirates -> {
                CannonPenalty cannon = controller.getCannon();
                controller.advanceCannon(destroyedComp, !destroyedComp);


            }

            default -> Logger.error("caso non previsto");
        }


    }

    private void askOneDoubleDrill(){
        String choice = "";
        ShipBoard shipBoard = controller.getShipBoard();
        boolean activate = false;

        if (shipBoard.getQuantBatteries() > 0){
            switch (controller.getPlayedCard()) {
                case SldMeteorSwarm meteorSwarm -> {
                    System.out.println("hit on " + controller.getMeteor().getCordinateHit());
                    Meteor meteor = controller.getMeteor();
                    List<Cordinate> possibleDrills = shipBoard.possibleDrills(meteor.getDirection(), meteor.getNumber());


                    if (possibleDrills.isEmpty()){
                        System.out.println("No possibile drill to cover");
                        controller.advanceMeteor(true, false);
                        return;
                    }

                    for (Cordinate cord : possibleDrills){
                        int idComp = shipBoard.getOptComponentByCord(cord).get().getId();
                        if (controller.getFlyBoard().getComponentById(idComp).getFirePower(false) > 0) {
                            controller.advanceMeteor(false, false);
                            return;
                        }

                    }

                    while (choice.equals("")) {
                        System.out.print("Activate one double drill (y/n) : ");
                        choice = scanner.nextLine().trim().toLowerCase();

                        if (!(choice.equals("y") || choice.equals("n"))) {
                            choice = "";
                            continue;
                        }
                        boolean destroyed = choice.equals("y");
                        controller.advanceMeteor(destroyed, !destroyed);
                    }
                }

                default -> Logger.error("errore meteoirit");
            }
        }

        if (!activate){
            controller.removeComponent(controller.getMeteor().getCordinateHit());
        }
//        controller.advanceMeteor();
    }

    private void stardustEndInfo(){
        int exposedConn;
        synchronized (controller.getShipboardLock()){
            exposedConn = controller.getShipBoard().getExposedConnectors();
        }
        System.out.println("Stardust has been applied:");
        System.out.println("You have " + exposedConn + " exposed connectors, so you lost " + exposedConn + " positions");
    }

    private void epidemicEndInfo(){
        System.out.println("Epidemic has been applied:");
        System.out.println("Look at your shipboard and check your crew!");
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
    }
}
