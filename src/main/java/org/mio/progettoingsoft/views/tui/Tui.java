package org.mio.progettoingsoft.views.tui;

import ch.qos.logback.core.net.SyslogOutputStream;
import javafx.scene.control.Button;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.advCards.sealed.SldStardust;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.InvalidCordinate;
import org.mio.progettoingsoft.model.FlyBoardNormal;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.views.View;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
        if(evt.getPropertyName().equals("gameState")) {
            statesQueue.add((GameState) evt.getNewValue());
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
            case WAITING -> {}

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
            case STARDUST -> {
                // la riga successiva è da eliminare e passargli la carta pescata
                SldStardust card = new SldStardust(1,1);
                System.out.println("STARDUST was drown");
                controller.applyStardust(card);
            }

            case VIEW_DECK -> viewDeck();

            case VIEW_DECKS_LIST -> viewDecksList();

            case SWITCH_BOOKED -> switchBookedComponents();
            case CHOOSE_POSITION -> {
                System.out.println("Choose position");
                printChoosePosition();
            }
            case END_BUILDING -> {
                System.out.println("Waiting for other players" + RESET);

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

            case ENGINE_CHOICE -> engineChoice();
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


    private void printChoosePosition(){
        List<Integer> availablePlaces = controller.getAvailablePlacesOnCircuit();
        String input = "";
        int choice = -1;
        int k = 0;
        while (!availablePlaces.contains(choice)) {
            controller.getFlyBoard().drawCircuit();
            System.out.println("In which of these available position do you want to start ?" );
            for ( Integer i : availablePlaces){
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

    /**
     * Message to notify the clients the start of the game
     * */
    private void printStartGameInfo() {
        System.out.println(BLUE + "The game has started!" + RESET);
        System.out.println("Players:");

        printPlayersName();

        //controller.setState(GameState.WAITING);
    }

    /**
     * Message to show building ship menu
     * */
    private void buildingShipMenu() {
        if(firstBuilding){
            printStartGameInfo();
            System.out.println(BLUE + "It's time to build your ship!" + RESET);
            firstBuilding = false;
        }
        int choice = -1;
        String input = "";

        while (choice < 1 || choice > 5) {
            System.out.println("1 : Pick covered component");
            System.out.println("2 : Pick uncovered component");
            System.out.println("3 : View other player's ship");
            if (controller.getFlyBoard().getMode().equals(GameMode.NORMAL)) {
                System.out.println("4 : Look at decks");
                System.out.println("5 : End ship building");
            }
            else {
                System.out.println("4 : end building ship");
            }
            System.out.print("Make your choice: ");

            input = scanner.nextLine();

            try {
                choice = Integer.parseInt(input);

                if (choice < 1 || choice > 5) {
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid choice!" + RESET);
            }

        }
        controller.handleBuildingShip(choice);
        clearConsole();
    }

    /**
     * Prints the menu to add component, asks for row, column and rotation
     * */
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

        try {
            controller.addComponent(Cordinate.convertWithOffset(row, column), rotation);
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
            }
            catch (IncorrectFlyBoardException e){
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

        if (choice == 1){
            controller.setState(GameState.ADD_COMPONENT);
        }
        else if (choice == 2){
            controller.discardComponent();
        }
        else if (choice == 3){
            controller.bookComponent();
        }
    }

    private void drawUncoveredComponents() {
        int count = 1;
        if (controller.getFlyBoard().getUncoveredComponents().isEmpty()){
            System.out.println("No uncovered Components");
            controller.setState(GameState.BUILDING_SHIP);
            return;
        }

        for (int idComp : controller.getFlyBoard().getUncoveredComponents()){
            System.out.println("Component #" + idComp);
            Component component = controller.getFlyBoard().getComponentById(idComp);
            new ShipCell(component).drawCell();
        }

        System.out.print("Select component to draw (-1 to null) : ");
        int chosen = Integer.parseInt(scanner.nextLine());

        if (chosen == -1)
            controller.setState(GameState.BUILDING_SHIP);
        else
            controller.drawUncovered(chosen);
    }

    private void viewDecksList(){
        System.out.println("Available decks : ");
        for (int numberDeck : controller.getFlyBoard().getAvailableDecks()){
            System.out.println("Deck #" + numberDeck);
        }
        System.out.print("Choose deck number : ");
        int chosen = Integer.parseInt(scanner.nextLine());

        controller.bookDeck(chosen);
    }

    private void viewDeck(){
        System.out.println("hai in mano il deck #" + controller.getInHandDeck());
        System.out.println("premere invio per continuare");

        String buffer = scanner.nextLine();
        controller.freeDeck();
    }

    private void printPlayersName(){
        synchronized (controller.getFlyboardLock()){
            FlyBoard flyBoard = controller.getFlyBoard();
            int count = 1;

            for (Player player : flyBoard.getPlayers()) {
                System.out.println((count++) + " " + player.getNickname() + " : " + player.getColor());
            }
        }
    }

    private void switchBookedComponents(){
        List<Integer> possibles = new ArrayList<>();

        List<Optional<Integer>> bookedComponents = controller.getShipBoard().getBookedComponents();
        for (Optional<Integer> optComp : bookedComponents){
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

    private void engineChoice(){
        ShipBoard shipBoard = controller.getShipBoard();

        int maxAvailable = Integer.max(shipBoard.getQuantBatteries(), shipBoard.getDoubleEngine().size());
        System.out.println("Select the number of double engines to activate (max " + maxAvailable + " : ");
        int activated = scanner.nextInt();
        //todo da controllare l'input

        controller.activateDoubleEngine(activated);

    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
    }
}
