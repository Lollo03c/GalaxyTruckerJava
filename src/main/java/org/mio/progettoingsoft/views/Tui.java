package org.mio.progettoingsoft.views;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.Client;
import org.mio.progettoingsoft.network.ClientController;
import org.mio.progettoingsoft.network.input.EmptyInput;
import org.mio.progettoingsoft.network.input.Input;
import org.mio.progettoingsoft.network.input.SetupInput;
import org.mio.progettoingsoft.network.input.StringInput;

import java.util.Scanner;

public class Tui implements VirtualView {
    private final Scanner scan;

    public Tui() {
        scan = new Scanner(System.in);
    }

    @Override
    public Input gameMenu(GameState gameState) {
        return switch (gameState) {
            case START -> printConnectionType();
            case NICKNAME_REQUEST -> printNicknameRequest();
            case SETUP_GAME -> printSetupGameReguest();
            case PRINT_GAME_INFO -> printGameInfo();
            case WAITING_PLAYERS -> new EmptyInput();

            case BUILDING_SHIP -> new EmptyInput();
            default -> null;
        };
    }

    private Input printConnectionType() {
        System.out.println("Select connection type: ");
        System.out.println("1: RMI");
        System.out.println("2: Socket");
        System.out.print("Make your choice: ");
        return new StringInput(scan.nextLine());
    }

    private Input printNicknameRequest() {
        System.out.print("Select nickname: ");
        return new StringInput(scan.nextLine());
    }

    private Input printSetupGameReguest(){
        System.out.print("Select number of players (2-4) : ");
        int nPlayers = Integer.parseInt(scan.nextLine());

        System.out.println("Select Game Mode : ");
        System.out.println("1 : Easy Mode ");
        System.out.println("2 : Normal Mode");
        System.out.print("Make your choice : ");
        int chosenMode = Integer.parseInt(scan.nextLine());
        GameMode mode = chosenMode == 1 ? GameMode.EASY : GameMode.NORMAL;

        return new SetupInput(nPlayers, mode);
    }

    private Input printGameInfo(){
        ClientController controller = ClientController.get();

        System.out.println("Add to game with id # " + controller.getGame().getIdGame());
        System.out.println("Game Mode " + controller.getGame().getGameMode());
        System.out.println("Number of players : " + controller.getGame().getNumPlayers());

        controller.setGameState(GameState.WAITING_PLAYERS);

        return new EmptyInput();
    }
}
