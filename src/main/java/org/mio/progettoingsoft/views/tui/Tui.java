package org.mio.progettoingsoft.views.tui;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.exceptions.InvalidCordinate;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.views.View;

import java.util.Scanner;

public class Tui extends View {
    private Scanner scanner = new Scanner(System.in);
    private final ClientController controller;

    private final Object lockView = new Object();

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";

    public Tui(){
        controller = ClientController.getInstance();
    }

    @Override
    public void run() {
        synchronized (controller.getStateLock()) {
            while (true) {

                GameState state = null;
                try {
                    state = controller.getStateQueue().take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                switch (state) {
                    case START -> printConnectionMenu();
                    case NICKNAME -> askNickname();
                    case WAITING -> {}
                    case GAME_MODE -> printGameModeMenu();
                    case GAME_START -> printStartGameInfo();
                    case BUILDING_SHIP -> buildingShipMenu();
                    case COMPONENT_MENU -> componentMenu();
                    case ADD_COMPONENT -> addComponent();
                    case DRAW_UNCOVERED_COMPONENTS -> drawUncoveredComponents();
                    case VIEW_SHIP_BUILDING -> viewShipBuilding();
                    case VIEW_DECKS_LIST -> viewDecksList();
                    case VIEW_DECK -> viewDeck();
                    case ERROR_NICKNAME -> {
                        System.out.println("Nickname already taken. Try Something else\n");
                        controller.setState(GameState.NICKNAME);
                    }
                    case UNABLE_UNCOVERED_COMPONENT -> {
                        System.out.println("This component has been already taken.");
                        controller.setState(GameState.BUILDING_SHIP);
                    }
                    case ERROR_PLACEMENT -> {
                        System.out.println("Invalid Position. Try again.\n\n");
                        controller.setState(GameState.ADD_COMPONENT);
                    }
                }
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
        controller.setGameInfo(gameInfo);
    }

    /**
     * Message to notify the clients the start of the game
     * */
    private void printStartGameInfo() {
        System.out.println(BLUE + "The game has started!" + RESET);
        System.out.println("Players:");

        printPlayersName();

        controller.setState(GameState.WAITING);
    }

    /**
     * Message to show building ship menu
     * */
    private void buildingShipMenu() {
        System.out.println(BLUE + "It's time to build your ship!" + RESET);

        int choice = -1;
        String input = "";

        while (choice < 1 || choice > 4) {
            System.out.println("1 : Pick covered component");
            System.out.println("2 : Pick uncovered component");
            System.out.println("3 : View other player's ship");
            if (controller.getFlyBoard().getMode().equals(GameMode.NORMAL))
                System.out.println("4 : Look at decks");
            System.out.print("Make your choice: ");

            input = scanner.nextLine();

            try {
                choice = Integer.parseInt(input);

                if (choice < 1 || choice > 4) {
                    System.out.println(RED + "Invalid choice!" + RESET);
                }
            } catch (Exception e) {
                System.out.println(RED + "Invalid choice!" + RESET);
            }
        }

        controller.handleBuildingShip(choice);
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
    }

    private void viewShipBuilding() {
        System.out.println("These are the players: ");
        printPlayersName();

        String chosenPlayer = "";

        while (true) {
            System.out.print("Insert nickname to look at: ");
            chosenPlayer = scanner.nextLine();

            if (chosenPlayer.isEmpty()) {
                System.out.println(RED + "Invalid nickname!" + RESET);
            } else {
                break;
            }
        }

        controller.getFlyBoard().getPlayerByUsername(chosenPlayer).getShipBoard().drawShipboard();
        controller.setState(GameState.BUILDING_SHIP);
    }

    private void componentMenu() {
        System.out.println("This is the component you've drawn:");
        new ShipCell(controller.getFlyBoard().getComponentById(controller.getInHandComponent())).drawCell();;
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

        }
    }

    private void drawUncoveredComponents() {
        int count = 1;
        for (int idComp : controller.getFlyBoard().getUncoveredComponents()){
            System.out.println("Component #" + idComp);
            Component component = controller.getFlyBoard().getComponentById(idComp);
            new ShipCell(component).drawCell();
        }

        System.out.print("Select component to draw : ");
        int chosen = Integer.parseInt(scanner.nextLine());

        controller.drawCovered(chosen);
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
}
