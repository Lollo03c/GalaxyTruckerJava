package org.mio.progettoingsoft;

import java.util.Scanner;

public class AdvCardController {

    public void OpenSpaceController(FlyBoard board){
        for (Player player : board.getScoreBoard()){
            int maxAllowedDoubleEngines = Integer.min(
                    player.getShipBoard().getQuantBatteries(),(int) player.getShipBoard().getComponentsStream()
                            .filter(x->x.getEnginePower() == 2 ).count());
            Scanner sc = new Scanner(System.in);
            int choice;
            do{
                System.out.println("Player "+player+ "how many Double Engine do you want to activate?");
                choice = sc.nextInt();
            }while(choice <= maxAllowedDoubleEngines);
            board.moveDays(player,2*choice+player.getShipBoard().getBaseEnginePower());
            player.getShipBoard().setQuantBatteries(player.getShipBoard().getQuantBatteries()-choice);
        }
    }
}
