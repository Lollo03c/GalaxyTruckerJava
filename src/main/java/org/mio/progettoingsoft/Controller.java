package org.mio.progettoingsoft;

public class Controller {
    private final FlyBoard flyBoard;

    public Controller(FlyBoard board){
        this.flyBoard = board;
    }

    public void openSpaceController(FlyBoard board) {

        for (Player player : board.getScoreBoard()) {
            int activated = player.getView().askDoubleEngine();

            for (int i  =0; i<activated; i++)
                player.getShipBoard().removeEnergy();

            flyBoard.moveDays(player, player.getShipBoard().getBaseEnginePower() + 2 * activated);
        }
    }

}
