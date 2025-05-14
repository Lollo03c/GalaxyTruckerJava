package org.mio.progettoingsoft.view;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.ClientController;

import java.util.Scanner;

public class Tui extends View{
    private Scanner scanner = new Scanner(System.in);
    private final ClientController controller;

    public Tui(){
        controller = ClientController.getInstance();
    }

    @Override
    public void run(){
        while (true) {
            GameState state = controller.getState();
            switch (state) {
                case START -> printConnectionMenu();
                case NICKNAME -> askNickname();
                case WAITING -> {}

                case GAME_MODE -> printGameModeMenu();
                case GAME_START -> System.out.println("partita iniziata");

                case ERROR_NICKNAME -> {
                    System.out.println("Nickname already taken. Try Something else\n");
                    controller.setState(GameState.NICKNAME);
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

        controller.setNickname(nickname);
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
}
