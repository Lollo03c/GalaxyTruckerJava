package org.mio.progettoingsoft.advCards.sealed;

import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.Planet;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.BadPlayerException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SldPlanets extends SldAdvCard {
    private final int daysLost;
    private final List<Planet> planets;
    private List<Player> landedPlayers;

    public SldPlanets(int id, int level, int daysLost, List<Planet> planets) {
        super(id, level);
        this.daysLost = daysLost;
        this.planets = planets;
    }

    @Override
    public List<Planet> getPlanets(){
        return planets;
    }

    @Override
    public int getDaysLost(){return daysLost;}

    @Override
    public String getCardName() {
        return "Planets";
    }

    @Override
    public void init(FlyBoard board) {
//        if (board.getState() != GameState.DRAW_CARD) {
//            throw new IllegalStateException("Illegal state: " + board.getState());
//        }
        this.state = CardState.PLANET_CHOICE;
//        board.setState(GameState.CARD_EFFECT);
        this.allowedPlayers = new ArrayList<>(board.getScoreBoard());
        this.playerIterator = allowedPlayers.iterator();
        if (playerIterator.hasNext()) {
            actualPlayer = playerIterator.next();
        } else {
            throw new RuntimeException("No players allowed");
        }
    }

    // if the planetIndex parameter is -1, the player doesn't want to land
    public void land(FlyBoard board, Player player, int planetIndex) {
        if (this.state != CardState.PLANET_CHOICE) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (this.actualPlayer.equals(player)) {
            if (planetIndex == -1) {
                nextPlayer(board);
            } else {
                if (planetIndex >= this.planets.size() || planetIndex < 0) {
                    throw new BadParameterException("Index out of list bounds");
                }
                if (this.planets.get(planetIndex).getPlayer().isPresent()) {
                    throw new BadParameterException("This planet is already taken");
                }
                this.planets.get(planetIndex).land(actualPlayer);
                landedPlayers.add(actualPlayer);
                boolean allTaken = true;
                for (Planet planet : this.planets) {
                    if (planet.getPlayer().isEmpty()) {
                        allTaken = false;
                        break;
                    }
                }
                if (allTaken) {
                    applyEffect(board);
                } else {
                    nextPlayer(board);
                }
            }
        } else {
            throw new BadPlayerException("The player " + actualPlayer.getNickname() + " cannot play " + this.getCardName() + " at the moment");
        }
    }

    private void applyEffect(FlyBoard board) {
        boolean atLeastOneGiven = false;
        for (Planet planet : this.planets) {
            if (planet.getPlayer().isPresent()) {
                planet.getPlayer().get().giveGoods(planet.getGoods());
                board.moveDays(planet.getPlayer().get(), -daysLost);
                atLeastOneGiven = true;
            }
        }
        if (atLeastOneGiven) {
            this.state = CardState.GOODS_PLACEMENT;
        } else {
            this.state = CardState.FINALIZED;
        }
    }

    private void nextPlayer(FlyBoard board) {
        if (playerIterator.hasNext()) {
            actualPlayer = playerIterator.next();
            this.state = CardState.PLANET_CHOICE;
        } else {
            applyEffect(board);
        }
    }

    public void goodPlaced(Player player) {
        if (this.state != CardState.GOODS_PLACEMENT) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
        if (this.landedPlayers.remove(actualPlayer)) {
            if (this.landedPlayers.isEmpty()) {
                this.state = CardState.FINALIZED;
            }
        } else {
            throw new BadPlayerException("The player " + player.getNickname() + " can't confirm goods placement");
        }
    }

    @Override
    public void finish(FlyBoard board) {
        if (this.state != CardState.FINALIZED) {
            throw new IllegalStateException("Illegal state: " + this.state);
        }
//        board.setState(GameState.DRAW_CARD);
    }
}
