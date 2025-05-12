package org.mio.progettoingsoft.views;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.TuiController;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.network.input.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tui implements VirtualView {
    private final Scanner scan;
    private final TuiController tuiController;

    public Tui(TuiController tuiController) {
        scan = new Scanner(System.in);
        this.tuiController = tuiController;
    }

    @Override
    public Input gameMenu(GameState gameState) {
        return switch (gameState) {
            case START -> printConnectionType();
            case NICKNAME_REQUEST -> printNicknameRequest();
            case SETUP_GAME -> printSetupGameReguest();
            case PRINT_GAME_INFO -> printGameInfo();
            case WAITING -> new EmptyInput();
            case BUILDING_SHIP -> printComponentMenu();
            default -> null;
        };
    }

    private Input printConnectionType()  {
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

    private Input printSetupGameReguest() {
        System.out.print("Select number of players (2-4): ");
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

        controller.setGameState(GameState.WAITING);

        return new EmptyInput();
    }

    /**
     *
     * @return a {@link ComponentInput} based on the chosen action
     */
    private Input printComponentMenu(){
        List<Integer> possibleOptions = new ArrayList<>();
        possibleOptions.addAll(List.of(1, 2, 3));

        System.out.println("1 : Draw a covered component");
        System.out.println("2 : Pick an uncovered component");
        System.out.println("3 : Look a player's shipboard");



        if (tuiController.getGame().getGameMode().equals(GameMode.NORMAL)) {
            System.out.println("4 : Look a deck of adventure cards");
            possibleOptions.add(4);
        }

        //TODO va controllato se la scelta e' valida
        int chosen = Integer.parseInt(scan.nextLine());
        return new IntInput(chosen);
    }

    public String readInput(){
        return scan.nextLine();
    }

}
