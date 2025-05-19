package org.mio.progettoingsoft.views.tui;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.exceptions.InvalidCordinate;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.views.tui.ShipCell;
import org.mio.progettoingsoft.views.tui.VisualShipboard;
import org.mio.progettoingsoft.views.View;

import java.beans.PropertyChangeEvent;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Tui implements View {
    private Scanner scanner = new Scanner(System.in);
    private final ClientController controller;

    private final Object lockView = new Object();

    private final BlockingQueue<GameState> statesQueue = new LinkedBlockingQueue<>();

    public Tui() {
        controller = ClientController.getInstance();
        controller.addPropertyChangeListener(this);
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

            case VIEW_SHIP_BUILDING -> viewShipBuilding();

            case ERROR_NICKNAME -> {
                System.out.println("Nickname already taken. Try Something else\n");
                controller.setState(GameState.NICKNAME);
            }

            case ERROR_PLACEMENT -> {
                System.out.println("Invalid Position. Try again.\n\n");
                controller.setState(GameState.ADD_COMPONENT);
            }
        }
    }


    private void printConnectionMenu() {
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
            } catch (NumberFormatException e) {
                chosen = -1;
                System.out.println("Your action is not valid. Try again\n");
            }
        }


        boolean isRmi = chosen == 1;
        controller.connectToServer(isRmi);
    }

    private void askNickname() {
        System.out.print("Insert your nickname : ");
        String nickname = scanner.nextLine();

        controller.handleNickname(nickname);
    }

    private void printGameModeMenu() {
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
        synchronized (controller.getFlyboardLock()) {
            FlyBoard flyBoard = controller.getFlyBoard();
            int count = 1;

            for (Player player : flyBoard.getPlayers()) {
                System.out.println((count++) + " " + player.getNickname() + " : " + player.getColor());
            }
        }
    }

    private void buildingShipMenu() {
        System.out.println("1 : pick covered component");
        System.out.println("2 : pick uncovered component");
        System.out.println("3 : view other player's ship");

        int chosen = Integer.parseInt(scanner.nextLine());
        controller.handleBuildingShip(chosen);
    }

    private void addComponent() {
        System.out.println("Insert row : ");
        int row = Integer.parseInt(scanner.nextLine());

        System.out.println("insert column : ");
        int column = Integer.parseInt(scanner.nextLine());

        System.out.println("insert rotation : ");
        int rotations = Integer.parseInt(scanner.nextLine());

        try {
            controller.addComponent(Cordinate.convertWithOffset(row, column), rotations);
        } catch (InvalidCordinate e) {
            controller.setState(GameState.ERROR_PLACEMENT);
        }
    }

    private void viewShipBuilding() {
        printPlayersName();
        System.out.print("Insert nickname to look at :");
        String chosenPlayer = scanner.nextLine();
        controller.getFlyBoard().getPlayerByUsername(chosenPlayer).getShipBoard().drawShipboard();
        controller.setState(GameState.BUILDING_SHIP);
    }

    private void componentMenu() {
        new ShipCell(controller.getFlyBoard().getComponentById(controller.getInHandComponent())).drawCell();
        ;
        controller.getShipBoard().drawShipboard();

        System.out.println("1 : Insert in the shipboard");
        System.out.println("2 : Put back in the deck");
        System.out.println("3 : Save for later");

        int chosenAction = Integer.parseInt(scanner.nextLine());
        if (chosenAction == 1) {
            controller.setState(GameState.ADD_COMPONENT);
        } else if (chosenAction == 2) {
            controller.discardComponent();
        } else if (chosenAction == 3) {

        }
    }
}
