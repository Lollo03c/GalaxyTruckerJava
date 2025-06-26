package org.mio.progettoingsoft.views.tui;

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
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.utils.Logger;
import org.mio.progettoingsoft.views.View;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mio.progettoingsoft.views.tui.CircuitCell.GREEN;

public class Tui implements View {
    private Scanner scanner = new Scanner(System.in);
    private final ClientController controller;

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public boolean continueAsking = true;
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
                if ((GameState) evt.getNewValue() == GameState.YOU_CAN_ROTATE_HOURGLASS) {
                    if(statesQueue.contains(GameState.YOU_CAN_ROTATE_HOURGLASS)) {
                       return;
                    }
                }
                statesQueue.add((GameState) evt.getNewValue());
            }
        }
    }

    @Override
    public void run() {
        Logger.setMinLevel(Logger.Level.WARNING);
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
            case ERROR_NICKNAME -> {
                System.out.println(RED + "Nickname already taken. Try Something else!" + RESET);
                controller.setState(GameState.NICKNAME);
            }
            case WAITING -> System.out.println(BLUE + "Waiting..." + RESET);
            case GAME_MODE -> printGameModeMenu();
            case WAITING_PLAYERS -> {
                clearConsole();
                System.out.println(BLUE + "Waiting for other players..." + RESET);
            }
            case GAME_START -> printStartGameInfo();
            case BUILDING_SHIP -> buildingShipMenu();
            case COMPONENT_MENU -> componentMenu();
            case ADD_COMPONENT -> addComponent();
            case ERROR_PLACEMENT -> {
                clearConsole();
                System.out.println(RED + "Invalid position. Try again.\n" + RESET);
                controller.setState(GameState.ADD_COMPONENT);
            }
            case DRAW_UNCOVERED_COMPONENTS -> drawUncoveredComponents();
            case UNABLE_UNCOVERED_COMPONENT -> {
                System.out.println(RED + "You can't take this component." + RESET);
                controller.setState(GameState.DRAW_UNCOVERED_COMPONENTS);
            }
            case VIEW_BOOKED -> viewBookedComponents();
            case SWITCH_BOOKED -> switchBookedComponents();
            case VIEW_SHIP_BUILDING -> {
                viewShipBuilding();
                controller.setState(GameState.BUILDING_SHIP);
            }
            case VIEW_DECKS_LIST -> viewDecksList();
            case VIEW_DECK -> viewDeck();
            case UNABLE_DECK -> {
                clearConsole();
                System.out.println(RED + "You can't take this deck." + RESET);
                controller.setState(GameState.BUILDING_SHIP);
            }

            case WRONG_POSITION -> {
                clearConsole();
                System.out.println(RED + "Current position is already occupied." + RESET);
                controller.setState(GameState.CHOOSE_POSITION);
            }

            case CHOICE_BUILT -> {
                chooseBuiltShip();
            }

            case FINISH_HOURGLASS -> {
                controller.setPendingHourglass(false);
                System.out.println(GREEN + "Hourglass has finished its cycle number : " + controller.getHourglassCounter() +RESET);
                if(controller.getFinishedBuilding()){
                    controller.setState(GameState.END_BUILDING);
                    //controller.setState(GameState.YOU_CAN_ROTATE_HOURGLASS);
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

            case END_BUILDING -> {
                if(!controller.getPendingHourglass() && controller.getHourglassCounter() < 3 && continueAsking)
                    controller.setState(GameState.YOU_CAN_ROTATE_HOURGLASS);
            }

            case CHOOSE_POSITION -> printChoosePosition();

            case VALIDATION -> printValidationMenu();

            case DRAW_CARD -> {
                printWaitingTheLeader();
            }

            case YOU_CAN_DRAW_CARD -> printDrawCardMenu();

            case NEW_CARD -> printNewCard();

            case CARD_EFFECT -> cardEffect();

            case IDLE -> {

            }
            case YOU_CAN_ROTATE_HOURGLASS -> printYouCanRotateHourglass();

            case ENDGAME -> endgame();

            case ADD_CREW -> addCrewMenu();

            case REMOVED_FROM_FLYBOARD -> System.out.println("You have been removed from ScoreBoard now you are just a spectator till the end of the flight, please wait");
        }
    }

private void endgame() {
    int index = 1;
    Logger.debug(controller.getNickname() + " has ended game.");
    List<Player> players = controller.getFlyBoard().getPlayers();
    List<Player> playersSorted = players.stream().sorted(Comparator.comparing(Player::getCredits).reversed()).toList();

    clearConsole();
    System.out.println(BLUE + "THE GAME HAS ENDED" + RESET);
    System.out.println("These are the final credits for each player: ");

    for (Player p  : playersSorted) {
        HousingColor color = p.getColor();
        System.out.println(color.colorToString() + index + " : "+ p.getNickname() + " finished with " + p.getCredits()+ " credits "+ RESET);
        index++;
    }

    String winner = playersSorted.stream().max(Comparator.comparing(Player::getCredits)).get().getNickname();

    if (controller.getNickname().equals(winner)) {
        System.out.println(GREEN + "\nYOU WON THE GAME" + RESET);
    } else {
        System.out.println(RED + "\nYOU LOST THE GAME" + RESET);
        System.out.println("The winner is " + winner);
    }

    System.out.println("\n\nPress enter to exit...");
    String buffer = scanner.nextLine();
    System.exit(0);
}

    private void printYouCanRotateHourglass() {
        if(controller.getPendingHourglass()) {
            //controller.setState(GameState.END_BUILDING);
            return;
        }
        String input = "";
        while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
            System.out.println("Do you want to rotate hourglass? y/n");
            input = scanner.nextLine().trim().toLowerCase();
        }

        if (input.equalsIgnoreCase("y")) {
            if (!controller.getFinishedLastHourglass() && !controller.getPendingHourglass()) {
                System.out.println("Type \"r\" to rotate hourglass");
                String input2 = "";
                while (!input2.equalsIgnoreCase("r")) {
                    input2 = scanner.nextLine().trim().toLowerCase();
                }

                try {
                    controller.rotateHourglass();
                } catch (CannotRotateHourglassException e) {
                    System.out.println(RED + e.getMessage() + RESET);
                    controller.setState(GameState.END_BUILDING);
                    return;
                } catch (RuntimeException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof CannotRotateHourglassException) {
                        System.out.println(RED + cause.getMessage() + RESET);
                    } else {
                        throw e;
                    }
                    controller.setState(GameState.END_BUILDING);
                    return;
                }
            }

        }
        else if (input.equalsIgnoreCase("n")) {
            continueAsking = false;
        }

        controller.setState(GameState.END_BUILDING);
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
     * Message to notify the clients the start of the game
     */
    private void printStartGameInfo() {
        System.out.println(BLUE + "THE GAME HAS STARTED!" + RESET);
        System.out.println("Game id: " + controller.getGameInfo().gameId());
        System.out.println("Game mode: " + controller.getGameInfo().mode());
        System.out.println("Players:");
        printPlayersName();
        System.out.println("Press enter to continue...");
        String buffer = scanner.nextLine();
        clearConsole();
    }

    /**
     * Message to show building ship menu
     */
    private void buildingShipMenu() {
        GameMode mode = controller.getFlyBoard().getMode();
        if (firstBuilding) {
            System.out.println(BLUE + "IT'S TIME TO BUILD YOUR SHIP!" + RESET);
            firstBuilding = false;
            //decido di far partire la clessidra dal client con la firstHousing blu che c'è in ogni partita Add commentMore actions
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
            System.out.println("5 : End building ship");
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
            if(!controller.getFinishedLastHourglass())
                controller.handleBuildingShip(choice);
        } catch (CannotRotateHourglassException e) {
            System.out.println(RED + "You can't rotate hourglass!" + RESET);
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
     * Message to show component menu
     */
    private void componentMenu() {
        int choice = -1;
        String input = "";

        while (choice < 1 || choice > 3) {
            printShipAndDrewComponent();

            System.out.println("1 : Insert in the shipboard");
            System.out.println("2 : Put back in the deck");
            System.out.println("3 : Book for later");
            System.out.print("Make your choice: ");
            input = scanner.nextLine();

            try {
                choice = Integer.parseInt(input);

                if (choice < 1 || choice > 3) {
                    clearConsole();
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            } catch (Exception e) {
                clearConsole();
                System.out.println(RED + "Invalid choice!" + RESET);
            }
        }

        if (choice == 1) {
            controller.setState(GameState.ADD_COMPONENT);
        } else if (choice == 2) {
            clearConsole();
            System.out.println(GREEN + "Component discarded." + RESET);
            controller.discardComponent();
        } else if (choice == 3) {
            clearConsole();
            System.out.println(GREEN + "Component booked." + RESET);
            controller.bookComponent();
        }
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
                    clearConsole();
                    System.out.println(RED + "Invalid row!" + RESET);
                    printShipAndDrewComponent();
                }
            } catch (Exception e) {
                clearConsole();
                System.out.println(RED + "Invalid row!" + RESET);
                printShipAndDrewComponent();
            }
        }

        int column = 0;
        while (column < 4 || column > 10) {
            System.out.print("Insert column: ");
            input = scanner.nextLine();

            try {
                column = Integer.parseInt(input);
                if (column < 4 || column > 10) {
                    clearConsole();
                    System.out.println(RED + "Invalid column!" + RESET);
                    printShipAndDrewComponent();
                    System.out.println("Selected row: " + row);
                }
            } catch (Exception e) {
                clearConsole();
                System.out.println(RED + "Invalid column!" + RESET);
                printShipAndDrewComponent();
                System.out.println("Selected row: " + row);
            }
        }

        int rotation = -1;
        while (rotation < 0 || rotation > 3) {
            System.out.print("Insert rotation: ");
            input = scanner.nextLine();

            try {
                rotation = Integer.parseInt(input);
                if (rotation < 0 || rotation > 3) {
                    clearConsole();
                    System.out.println(RED + "Invalid rotation!" + RESET);
                    printShipAndDrewComponent();
                    System.out.println("Selected row: " + row);
                    System.out.println("Selected column: " + column);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid rotation!" + RESET);
                printShipAndDrewComponent();
                System.out.println("Selected row: " + row);
                System.out.println("Selected column: " + column);
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
        System.out.println(GREEN + "Component added to ship." + RESET);
    }

    /**
     * Prints uncovered components
     */
    private void drawUncoveredComponents() {
        int count = 1;
        if (controller.getFlyBoard().getUncoveredComponents().isEmpty()) {
            clearConsole();
            System.out.println(RED + "No uncovered Components" + RESET);
            controller.setState(GameState.BUILDING_SHIP);
            return;
        }

        String input = "";
        int chosen = -2;
        while (chosen < -1) {
            printUncoveredComponent();
            System.out.print("Insert id component to draw (-1 to cancel): ");
            input = scanner.nextLine();

            try {
                chosen = Integer.parseInt(input);
            } catch (Exception e) {
                clearConsole();
                System.out.println(RED + "Invalid choice." + RESET);
            }
        }

        if (chosen == -1) {
            clearConsole();
            System.out.println(GREEN + "None uncovered component selected." + RESET);
            controller.setState(GameState.BUILDING_SHIP);
        } else {
            clearConsole();
            controller.drawUncovered(chosen);
        }
    }

    /**
     * Prints menu to switch components
     */
    private void viewBookedComponents() {
        List<Integer> bookedComponentIds = controller.getShipBoard().getBookedComponents().stream()
                .flatMap(Optional::stream)
                .toList();

        int count = 1;
        for (Integer id : bookedComponentIds) {
            System.out.println("Component # " + count++);
            Component component = controller.getFlyBoard().getComponentById(id);
            new ShipCell(component).drawCell();
        }

        int chosen;
        while (true) {
            System.out.print("Select component to draw (-1 to null): ");
            String input = scanner.nextLine();

            try {
                chosen = Integer.parseInt(input);

                if (chosen == -1) {
                    clearConsole();
                    System.out.println(GREEN + "No component selected." + RESET);
                    controller.setState(GameState.BUILDING_SHIP);
                    return;
                }

                if (chosen > 0 && chosen < count) {
                    controller.choseBookedComponent(chosen);
                    break;
                } else {
                    clearConsole();
                    System.out.println(RED + "Invalid component index." + RESET);
                }

            } catch (NumberFormatException e) {
                clearConsole();
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
            }
        }

    }

    /**
     * Prints menu to switch components
     */
    private void switchBookedComponents() {
        List<Integer> bookedComponentIds = controller.getShipBoard().getBookedComponents().stream()
                .flatMap(Optional::stream)
                .toList();

        int count = 1;
        for (Integer id : bookedComponentIds) {
            System.out.println("Component # " + id);
            Component component = controller.getFlyBoard().getComponentById(id);
            new ShipCell(component).drawCell();
        }

        int chosenComp;
        while (true) {
            System.out.print("Select the component to switch (-1 to cancel): ");
            String input = scanner.nextLine();

            try {
                chosenComp = Integer.parseInt(input);

                if (chosenComp == -1) {
                    clearConsole();
                    System.out.println(GREEN + "No switch component." + RESET);
                    controller.setState(GameState.COMPONENT_MENU);
                    return;
                }

                if (bookedComponentIds.contains(chosenComp)) {
                    controller.bookComponent(bookedComponentIds.indexOf(chosenComp));
                    break;
                } else {
                    clearConsole();
                    System.out.println(RED + "Invalid chosen component." + RESET);
                }

            } catch (NumberFormatException e) {
                clearConsole();
                System.out.println(RED + "Invalid input. Please enter a number." + RESET);
            }
        }
    }

    /**
     * Prints other players ship
     */
    private void viewShipBuilding() {
        clearConsole();

        String chosenPlayer = "";
        while (true) {
            System.out.println("These are the players: ");
            printPlayersName();
            System.out.print("Insert nickname to look at: ");
            chosenPlayer = scanner.nextLine();

            try {
                if (chosenPlayer.isEmpty()) {
                    clearConsole();
                    System.out.println(RED + "Invalid nickname!" + RESET);
                } else {
                    controller.getFlyBoard().getPlayerByUsername(chosenPlayer).getShipBoard().drawShipboard();
                    System.out.println("Press enter to continue...");
                    String buffer = scanner.nextLine();
                    clearConsole();
                    break;
                }
            } catch (IncorrectFlyBoardException e) {
                clearConsole();
                System.out.println(RED + "Invalid nickname!" + RESET);
            }
        }
    }

    /**
     * Prints decks list
     */
    private void viewDecksList() {
        String input = "";
        int chosen = -1;
        while (chosen < 0 || chosen > controller.getFlyBoard().getAvailableDecks().getLast()) {
            System.out.println("Available decks: ");
            for (int numberDeck : controller.getFlyBoard().getAvailableDecks()) {
                System.out.println("Deck #" + numberDeck);
            }
            System.out.print("Choose deck number: ");
            input = scanner.nextLine();
            try {
                chosen = Integer.parseInt(input);

                if (chosen < -1 || chosen > controller.getFlyBoard().getAvailableDecks().getLast()) {
                    clearConsole();
                    System.out.println(RED + "Invalid choice." + RESET);
                }
            } catch (Exception e) {
                clearConsole();
                System.out.println(RED + "Invalid choice." + RESET);
            }
        }
        controller.bookDeck(chosen);
    }

    /**
     * Prints selected deck
     */
    private void viewDeck() {
        System.out.println("Deck #" + controller.getInHandDeck() + " in hand.");
        printDeck();
        System.out.println("Press enter to continue...");
        String buffer = scanner.nextLine();
        clearConsole();
        controller.freeDeck();
    }

    /**
     * Prints end of building menu
     */
    private void endBuildingMenu(){
        System.out.println(BLUE + "Waiting for other players" + RESET);
    }

    /**
     * Prints choose position menu
     */
    private void printChoosePosition() {
        clearConsole();
        List<Integer> availablePlaces = controller.getAvailablePlacesOnCircuit();
        String input = "";
        int choice = -1;
        while (!availablePlaces.contains(choice)) {
            System.out.println("Choose position: ");
            controller.getFlyBoard().drawCircuit();
            System.out.println("In which of these available position do you want to start?");
            for (Integer i : availablePlaces)
                System.out.println(FlyBoardNormal.indexToPosition(i));

            System.out.print("Make a choice: ");
            input = scanner.nextLine();
            try {
                choice = Integer.parseInt(input);
                // se la choice è fuori dal possibile range
                if (choice < 1 || choice > 4) {
                    clearConsole();
                    System.out.println(RED + "Invalid choice!" + RESET);
                    choice = FlyBoardNormal.positionToIndex(choice);
                } else {
                    choice = FlyBoardNormal.positionToIndex(choice);
                    if (!availablePlaces.contains(choice)) {
                        clearConsole();
                        System.out.println(RED + "Invalid choice!" + RESET);
                    }
                }
            } catch (Exception e) {
                clearConsole();
                System.out.println(RED + "Invalid choice!" + RESET);
            }
        }

        controller.setState(GameState.WAITING_PLAYERS);
        controller.choosePlace(choice);
    }

    /**
     * Prints validation menu
     */
    private void printValidationMenu() {
        clearConsole();
        System.out.println(BLUE + "END BUILDING PHASE, IT'S NOW TIME TO VALIDATE YOUR SHIP:" + RESET);

        List<Cordinate> incorrectComponents = controller.getIncorrectComponents();

        String input = "";
        while (!incorrectComponents.isEmpty()) {
            int choice = -1;
            while(choice < 1 || choice > incorrectComponents.size()) {
                controller.getShipBoard().drawShipboard();
                System.out.println("Following components are not properly connected: " + incorrectComponents);
                System.out.print("Select a incorrect component to remove from the list (1 - " + incorrectComponents.size() + "): ");
                input = scanner.nextLine().trim().toLowerCase();

                try {
                    choice = Integer.parseInt(input);

                    if (choice < 1 || choice > incorrectComponents.size()) {
                        clearConsole();
                        System.out.println(RED + "Invalid choice!" + RESET);
                    }
                } catch (Exception e) {
                    clearConsole();
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            }

            controller.removeComponentImmediate(incorrectComponents.get(choice - 1));
            incorrectComponents = controller.getIncorrectComponents();
        }

        List<Set<Component>> standAloneBlocks = controller.getStandAloneBlocks();
        if (standAloneBlocks.size() > 1) {
            int choice = -1;
            while(choice < 0){
                System.out.println("There are blocks of components that are not connected to each other. These are the stand alone blocks:");
                for (Set<Component> standAloneBlock : standAloneBlocks) {
                    System.out.println("Block: " + standAloneBlock.stream().map(Component::getCordinate).toList());
                }

                System.out.print("Which one do you want to keep? Select between 1 - " + standAloneBlocks.size() + ": ");
                input = scanner.nextLine().trim().toLowerCase();

                try {
                    choice = Integer.parseInt(input);

                    if (choice < 1 || choice > standAloneBlocks.size()) {
                        choice = -1;
                        clearConsole();
                        System.out.println(RED + "Invalid choice!" + RESET);
                    }
                } catch (Exception e) {
                    clearConsole();
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            }

            controller.removeStandAloneBlocks(choice - 1);
            System.out.println(GREEN + "Components removed!" + RESET);
        }

        controller.endValidation(controller.isUsedBattery());

        System.out.println(BLUE + "End of validation phase." + RESET);
    }

    /**
     * Prints initial circuit
     */
    private void printWaitingTheLeader() {
        clearConsole();
        System.out.println("The effect of the card is over, this is the actual circuit:");
        synchronized (controller.getFlyboardLock()) {
            controller.getFlyBoard().drawScoreboard();
            controller.getFlyBoard().drawCircuit();
        }
        System.out.println("Waiting for the leader to draw a new card...");
    }

    /**
     * Prints initial circuit for leader
     */
    private void printDrawCardMenu() {
        clearConsole();
        System.out.println("The effect of the card is over, this is the actual circuit: ");
        synchronized (controller.getFlyboardLock()) {
            controller.getFlyBoard().drawScoreboard();
            controller.getFlyBoard().drawCircuit();
        }
        String input = "";
        while (!input.equalsIgnoreCase("d")) {
            System.out.println("You are the leader! You can draw a Card, type \"d\" to draw a Card: ");
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("d")) {
                controller.drawNewAdvCard();
            }
        }
    }

    private void printNewCard() {
        clearConsole();
        System.out.println("A new card has been drawn");
        controller.getPlayedCard().disegnaCard();
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

            case ASK_LEAVE ->  askLeave();
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
            availablePlanets = planets.stream().filter(x->!x.getPlayer().isPresent()).toList();
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
        controller.setState(GameState.WAITING_PLAYERS);
    }

    private void chooseBuiltShip() {
        controller.builtDefault();
    }

    private void engineChoice() {

        int maxAvailable;
        synchronized (controller.getShipboardLock()) {
            ShipBoard shipBoard = controller.getShipBoard();
            shipBoard.drawShipboard();
            maxAvailable = Integer.min(shipBoard.getQuantBatteries(), shipBoard.getDoubleEngine().size());
        }

        if (maxAvailable > 0) {
            int activated = -1;
            while (activated < 0 || activated > maxAvailable) {
                System.out.println("Select the number of double engines to activate (max " + maxAvailable + " : )");
                try {
                    activated = scanner.nextInt();
                } catch (InputMismatchException e) {
                    scanner.next();
                    activated = -1;
                }
                if (activated >= 0 && activated <= maxAvailable)
                    controller.activateDoubleEngine(activated);
                else
                    System.out.println(RED + "Invalid activated engine" + RESET);
            }
        }
        else{
            controller.activateDoubleEngine(0);
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
        int toRemove = card.getCrewLost();

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

        toInsert = controller.getGoodsToInsert();
        ShipBoard shipBoard = controller.getShipBoard();

        String choice = "";

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

            case SldCombatZone combatZone -> {
                CannonPenalty cannon =controller.getCannon();
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

            case SldCombatZone combatZone -> {
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
        System.out.println("Waiting other player to make a decision.");


        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                Meteor meteor = controller.getMeteor();
                controller.advanceMeteor(destroyedComp, !destroyedComp);
            }

            case SldPirates pirates -> {
                CannonPenalty cannon = controller.getCannon();
                controller.advanceCannon(destroyedComp, !destroyedComp);
            }

            case SldCombatZone combatZone -> {
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
                        if (controller.getShipBoard().getQuantBatteries() > 0) {
                            System.out.print("Activate one double drill (y/n) : ");
                            choice = scanner.nextLine().trim().toLowerCase();

                            if (!(choice.equals("y") || choice.equals("n"))) {
                                choice = "";
                                continue;
                            }
                            boolean destroyed = choice.equals("y");
                            controller.advanceMeteor(destroyed, !destroyed);
                        }
                        else{
                            controller.advanceMeteor(true, false);
                        }
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
    private void askLeave(){
        int chose = 0;

        while (chose == 0) {
            System.out.println("1. Leave flight");
            System.out.println("2. View flyboard");
            System.out.println("3. View other player's shipboard");
            System.out.println("4. Keep flying");

            System.out.print("Make your decision : ");
            try{
                chose = Integer.parseInt(scanner.nextLine());

                if (chose < 0 || chose > 4){
                    System.out.println(RED + "invalid option. Try again.");
                    controller.setCardState(CardState.ASK_LEAVE);
                }

                switch (chose){
                    case 1 -> controller.leaveFlight(true);
                    case 2 -> {
                        controller.getFlyBoard().drawCircuit();
                        controller.setCardState(CardState.ASK_LEAVE);
                    }
                    case 3 -> {
                        viewShipBuilding();
                        controller.setCardState(CardState.ASK_LEAVE);
                    }
                    case 4 -> controller.leaveFlight(false);
                }
            } catch (NumberFormatException e) {
                chose = 0;
            }
        }

    }

//        try{
//            controller.removeCrew(crewPositionsToRemove);
//        } catch (Exception e) {
//            System.out.println(RED + "One of the coordindate you insert is not associated with a guest" + RESET);
//            controller.setState(GameState.CARD_EFFECT);
//            return ;
//        }

    private void epidemicEndInfo(){
        System.out.println("Epidemic has been applied:");
        System.out.println("Look at your shipboard and check your crew!");
    }

    /** SECONDARY METHOD */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
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

    private void printShipAndDrewComponent() {
        System.out.println("This is the component you've drawn:");
        new ShipCell(controller.getFlyBoard().getComponentById(controller.getInHandComponent())).drawCell();
        controller.getShipBoard().drawShipboard();
    }

    private void printUncoveredComponent() {
        for (int idComp : controller.getFlyBoard().getUncoveredComponents()) {
            System.out.println("Component #" + idComp);
            Component component = controller.getFlyBoard().getComponentById(idComp);
            new ShipCell(component).drawCell();
        }
    }

    private void printDeck() {
        for (int cardId : controller.getFlyBoard().getLittleDecks().get(controller.getInHandDeck()))
            controller.getFlyBoard().getSldAdvCardByID(cardId).disegnaCard();
    }

    private void addCrewMenu(){
        clearConsole();

        Map<Cordinate, List<GuestType>> addedCrew = new HashMap<>();
        ShipBoard shipBoard = controller.getShipBoard();

        Set<GuestType> alreadyInserted = new HashSet<>();

        String input = "";
        int choice = -1;
        while (true) {
            System.out.println(BLUE + "ADD YOUR CREW!" + RESET);
            shipBoard.drawShipboard();

            System.out.println("1. Human");
            System.out.println("2. Purple Alien");
            System.out.println("3. Brown Alien");
            System.out.print("Select the crew member to add (0 to exit): ");
            input = scanner.nextLine();

            try {
                choice = Integer.parseInt(input);

                if (choice < 0 || choice > 3) {
                    choice = -1;
                    clearConsole();
                    System.out.println(RED + "Choice not valid. Try Again." + RESET);
                    continue;
                }
            } catch (NumberFormatException e) {
                choice = -1;
                clearConsole();
                System.out.println(RED + "Choice not valid. Try Again." + RESET);
                continue;
            }


            GuestType guestSelected = null;
            if (choice == 0) {
                break;
            } else if (choice == 1) {
                guestSelected = GuestType.HUMAN;
            } else if (choice == 2) {
                guestSelected = GuestType.PURPLE;
            } else if (choice == 3) {
                guestSelected = GuestType.BROWN;
            }

            List<Cordinate> availableCord = shipBoard.getAvailableHousing(guestSelected);
            if (availableCord.isEmpty()) {
                clearConsole();
                System.out.println(RED + "No available housing to guest this crew member." + RESET);
                continue;
            }

            clearConsole();

            int secondChoice = -1;
            while (secondChoice == -1) {
                System.out.println("You are adding a " + guestSelected + ":");
                shipBoard.drawShipboard();
                System.out.println("These are the available housing for " + guestSelected + ":");
                for (int i = 0; i < availableCord.size(); i++)
                    System.out.println((i + 1) + ". " + availableCord.get(i));
                System.out.print("Select where to add a " + guestSelected + " (0 to exit): ");
                try {
                    secondChoice = Integer.parseInt(scanner.nextLine());

                    if (secondChoice < 0 || secondChoice > availableCord.size()) {
                        secondChoice = -1;
                        clearConsole();
                        System.out.println(RED + "Invalid Selection. Try Again." + RESET);
                    }
                } catch (NumberFormatException e) {
                    secondChoice = -1;
                    clearConsole();
                    System.out.println(RED + "Invalid Selection. Try Again." + RESET);
                }
                clearConsole();
            }

            if (secondChoice == 0){
                clearConsole();
                continue;
            }

            secondChoice -= 1;
            Cordinate chosenCord = availableCord.get(secondChoice);

            if (guestSelected.equals(GuestType.BROWN) || guestSelected.equals(GuestType.PURPLE)){
                if (alreadyInserted.contains(guestSelected)){
                    clearConsole();
                    System.out.println(RED + "It is possible to insert only a alien for kind." + RESET);
                    continue;
                }
            }

            shipBoard.getOptComponentByCord(chosenCord).get().addGuest(guestSelected);
            alreadyInserted.add(guestSelected);

            if (addedCrew.containsKey(chosenCord)){
                addedCrew.get(chosenCord).add(guestSelected);
            }
            else{
                addedCrew.put(chosenCord, new ArrayList<>());
                addedCrew.get(chosenCord).add(guestSelected);
            }
        }

        controller.setState(GameState.WAITING_PLAYERS);
        controller.addCrew(addedCrew);
    }
}