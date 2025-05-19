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

    public Tui(){
        controller = ClientController.getInstance();
    }

    @Override
    public void run(){
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
                    case WAITING -> {
                    }

                    case GAME_MODE -> printGameModeMenu();
                    case GAME_START -> {
                        System.out.println("partita iniziata");
                        printPlayersName();
                        controller.setState(GameState.WAITING);
                    }

                    case BUILDING_SHIP -> {
                        buildingShipMenu();
                    }

                    case COMPONENT_MENU -> {
                        componentMenu();
                    }

                    case ADD_COMPONENT -> addComponent();

                    case DRAW_UNCOVERED_COMPONENTS ->
                        drawUncoveredComponents();

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


    private void printConnectionMenu(){
        int chosen = -1;


        while (chosen == -1) {
            System.out.println("Select connection type: ");
            System.out.println("1: RMI");
            System.out.println("2: Socket");
            System.out.print("Make your choice: ");

            try {
                chosen = Integer.parseInt(scanner.nextLine());

                if (chosen < 1 || chosen > 2)
                    throw new NumberFormatException("");
            }
            catch (NumberFormatException e){
                chosen = -1;
                System.out.println("Your action is not valid. Try again\n");
            }
        }


        boolean isRmi = chosen == 1;
        controller.connectToServer(isRmi);
    }

    private void askNickname(){
        System.out.print("Insert your nickname : ");
        String nickname = scanner.nextLine();

        controller.handleNickname(nickname);
    }

    private void printGameModeMenu(){
        System.out.print("Insert number of players of the game : ");
        int nPlayers = Integer.parseInt(scanner.nextLine());

        System.out.println("Decide game mode");
        System.out.println("1 : Easy Mode");
        System.out.println("2 : Normal Mode");
        System.out.print("Select game mode : ");
        int choice = Integer.parseInt(scanner.nextLine());

        GameInfo gameInfo = new GameInfo(-1, choice == 1 ? GameMode.EASY : GameMode.NORMAL, nPlayers);
        controller.setGameInfo(gameInfo);
    }

    private void printPlayersName() {
        synchronized (controller.getFlyboardLock()){
            FlyBoard flyBoard = controller.getFlyBoard();
            int count = 1;

            for (Player player : flyBoard.getPlayers()) {
                System.out.println((count++) + " " + player.getNickname() + " : " + player.getColor());
            }
        }
    }

    private void buildingShipMenu(){
        System.out.println("1 : pick covered component");
        System.out.println("2 : pick uncovered component");
        System.out.println("3 : view other player's ship");

        if (controller.getFlyBoard().getMode().equals(GameMode.NORMAL)) {
            System.out.println("4 : look at decks");
        }

        int chosen = Integer.parseInt(scanner.nextLine());
        controller.handleBuildingShip(chosen);
    }

    private void addComponent(){
        System.out.println("Insert row : ");
        int row = Integer.parseInt(scanner.nextLine());

        System.out.println("insert column : ");
        int column= Integer.parseInt(scanner.nextLine());

        System.out.println("insert rotation : ");
        int rotations = Integer.parseInt(scanner.nextLine());

        try {
            controller.addComponent(Cordinate.convertWithOffset(row, column), rotations);
        } catch (InvalidCordinate e) {
            controller.setState(GameState.ERROR_PLACEMENT);
        }
    }

    private void viewShipBuilding(){
        printPlayersName();
        System.out.print("Insert nickname to look at :");
        String chosenPlayer = scanner.nextLine();
        controller.getFlyBoard().getPlayerByUsername(chosenPlayer).getShipBoard().drawShipboard();
        controller.setState(GameState.BUILDING_SHIP);
    }

    private void componentMenu(){
        new ShipCell(controller.getFlyBoard().getComponentById(controller.getInHandComponent())).drawCell();;
        controller.getShipBoard().drawShipboard();

        System.out.println("1 : Insert in the shipboard");
        System.out.println("2 : Put back in the deck");
        System.out.println("3 : Save for later");

        int chosenAction = Integer.parseInt(scanner.nextLine());
        if (chosenAction == 1){
            controller.setState(GameState.ADD_COMPONENT);
        }
        else if (chosenAction == 2){
            controller.discardComponent();
        }
        else if (chosenAction == 3){

        }
    }

    private void drawUncoveredComponents(){
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
}
