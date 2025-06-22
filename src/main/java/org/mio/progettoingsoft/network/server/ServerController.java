package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.CannonPenalty;
import org.mio.progettoingsoft.advCards.Meteor;
import org.mio.progettoingsoft.advCards.sealed.*;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadParameterException;
import org.mio.progettoingsoft.exceptions.IncorrectFlyBoardException;
import org.mio.progettoingsoft.exceptions.NotYourTurnException;
import org.mio.progettoingsoft.advCards.sealed.SldAbandonedShip;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.advCards.sealed.SldOpenSpace;
import org.mio.progettoingsoft.advCards.sealed.SldStardust;
import org.mio.progettoingsoft.exceptions.*;
import org.mio.progettoingsoft.model.enums.CannonType;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.MeteorType;
import org.mio.progettoingsoft.model.interfaces.GameServer;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.utils.Logger;

import java.util.List;
import java.util.Map;

public class ServerController {
    /**
     * SINGLETON IMPLEMENTATION
     */
    private static ServerController instance;

    public static ServerController getInstance() {
        if (instance == null) {
            instance = new ServerController();
        }
        return instance;
    }

    public int addClientToAccept(VirtualClient client) {
        GameManager gameManager = GameManager.getInstance();
        return gameManager.addClientToAccept(client);
    }

    public void handleNickname(int idClient, String nickname) {
        GameManager gameManager = GameManager.getInstance();

        gameManager.addPlayerToGame(idClient, nickname);
    }

    public void handleGameInfo(GameInfo gameInfo, String nickname) {
        GameManager gameManager = GameManager.getInstance();
        GameServer game = gameManager.getWaitingGame();
        game.setupGame(gameInfo.mode(), gameInfo.nPlayers());
        try {
            game.getClients().get(nickname).setState(GameState.WAITING_PLAYERS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations) {
        GameManager gameManager = GameManager.getInstance();
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);

        ShipBoard shipBoard = game.getFlyboard().getPlayerByUsername(nickname).getShipBoard();
        shipBoard.addComponentToPosition(idComp, cordinate, rotations);

        Logger.debug(nickname + " added component " + idComp + " " + cordinate + " " + rotations);

        for (Player player : game.getFlyboard().getPlayers()) {
            if (!player.getNickname().equals(nickname)) {
                VirtualClient client = game.getClients().get(player.getNickname());

                try {
                    client.addComponent(nickname, idComp, cordinate, rotations);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void getCoveredComponent(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        VirtualClient client = game.getClients().get(nickname);

        try {
            client.setInHandComponent(flyBoard.getCoveredComponents().removeLast());
            client.setState(GameState.COMPONENT_MENU);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void discardComponent(int idGame, int idComponent) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        flyBoard.addUncoveredComponent(idComponent);

        for (VirtualClient client : game.getClients().values()) {
            try {
                client.addUncoveredComponent(idComponent);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void applyStardust(int idGame, SldStardust card) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyboard = game.getFlyboard();
        card.applyEffect(flyboard);
        /*List<Player> reversedScoreboard = flyboard.getScoreBoard().reversed();
        for (Player p : reversedScoreboard){
            int exposedConnectors = p.getShipBoard().getExposedConnectors();
            flyboard.moveDays(p, -exposedConnectors);
        }*/

        //SldAdvCard nextCard = flyboard.drawSldAdvCard();
        //String type = nextCard.getCardName().toUpperCase();
        //GameState next = GameState.stringToGameState(type);

        //TODO settare il Gamestate allo stato della carta pescata
        //a chi devo settarlo il nuovo stato? A tutti o basta settarlo a uno solo ?
        //game.getClients().get(nickname).setState(GameState.COMPONENT_MENU);

    }

    public void drawUncovered(int idGame, String nickname, Integer idComponent) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        boolean removed = flyBoard.getUncoveredComponents().remove(idComponent);

        if (removed) {
            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.removeUncovered(idComponent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                game.getClients().get(nickname).setInHandComponent(idComponent);
                game.getClients().get(nickname).setState(GameState.COMPONENT_MENU);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                game.getClients().get(nickname).setState(GameState.UNABLE_UNCOVERED_COMPONENT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void bookDeck(int idGame, String nickname, Integer deckNumber) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        synchronized (flyBoard.getAvailableDecks()) {
            List<Integer> availableDecks = flyBoard.getAvailableDecks();

            boolean removed = availableDecks.remove(deckNumber);

            if (removed) {
                for (VirtualClient client : game.getClients().values()) {
                    try {
                        client.removeDeck(deckNumber);
                        Logger.debug("removed deck " + deckNumber + " from client " + client);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    game.getClients().get(nickname).setInHandDeck(deckNumber);
                    game.getClients().get(nickname).setState(GameState.VIEW_DECK);
                    Logger.debug("Set deck " + deckNumber + " to " + nickname);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    game.getClients().get(nickname).setState(GameState.UNABLE_DECK);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void freeDeck(int idGame, String nickname, Integer deckNumber) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        synchronized (flyBoard.getAvailableDecks()) {
            List<Integer> availableDecks = flyBoard.getAvailableDecks();
            availableDecks.add(deckNumber);
            Logger.debug("Free deck " + deckNumber + ".");

            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.addAvailableDeck(deckNumber);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                game.getClients().get(nickname).setState(GameState.BUILDING_SHIP);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void takeBuild(int idGame, String nickname) {

        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        Player player = flyBoard.getPlayerByUsername(nickname);

        Logger.debug(nickname + " assigned to ship " + player.getColor());
        try {
            flyBoard.takeCostructedShip(player);

            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.setBuiltShip(nickname);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                game.getClients().get(nickname).setState(GameState.BUILDING_SHIP);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (IncorrectFlyBoardException e) {
            try {
                game.getClients().get(nickname).setState(GameState.INVALID_SHIP_CHOICE);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }


    }

    public void endBuild(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        List<Integer> availablePlaces = flyBoard.getAvailableStartingPositions();
        VirtualClient client = game.getClients().get(nickname);
        try {
            client.setAvailablePlaces(availablePlaces);
            client.setState(GameState.CHOOSE_POSITION);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void choosePlace(int idGame, String nickname, int place) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        GameState state;
        VirtualClient client = game.getClients().get(nickname);
        try {
            flyBoard.addPlayerToCircuit(nickname, place);
            for (VirtualClient c : game.getClients().values()) {
                try {
                    c.addOtherPlayerToCircuit(nickname, place);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            state = GameState.END_BUILDING;
        } catch (BadParameterException e) {
            try {
                client.setAvailablePlaces(flyBoard.getAvailableStartingPositions());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            state = GameState.WRONG_POSITION;
        }

        try {
            client.setState(state);
            if (flyBoard.isReadyToAdventure()) {
                VirtualClient c2 = game.getClients().get(flyBoard.getScoreBoard().getFirst().getNickname());
                c2.setState(GameState.YOU_CAN_DRAW_CARD);
                for (VirtualClient c : game.getClients().values()) {
                    if (!c.equals(c2))
                        c.setState(GameState.DRAW_CARD);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void drawCard(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        //controllo per vedere se il giocatore è il Leader
        if (!flyBoard.getScoreBoard().getFirst().equals(flyBoard.getPlayerByUsername(nickname))) {
            throw new NotYourTurnException();
        }
//        SldAdvCard card = flyBoard.drawSldAdvCard();
        SldAdvCard card = flyBoard.getSldAdvCardByID(1);
        Logger.debug(nickname + " draws card " + card.getCardName());
        flyBoard.setPlayedCard(card);

        card.disegnaCard();
        for (VirtualClient client : game.getClients().values()) {
            try {
                client.setPlayedCard(card.getId());
                //setto a tutti i client lo stato NEW_CARD, così la mostra a tutti poi lo switch in base al tipo
                //di carta e al player lo fa il ClientController
                client.setState(GameState.NEW_CARD);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        card.init(game);

        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                meteorSwarm.setNextMeteor();
            }

            default -> card.setNextPlayer();
        }


    }

    public void activateDoubleEngine(int idGame, String nickname, int number) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        SldAdvCard card = flyBoard.getPlayedCard();
        Player player = flyBoard.getPlayerByUsername(nickname);


        switch (card) {
            case SldOpenSpace openSpace -> {

                try {
                    openSpace.applyEffect(player, number);
                    openSpace.setNextPlayer();
                } catch (IllegalStateException | BadParameterException | BadPlayerException |
                         NotEnoughBatteriesException e) {
                    VirtualClient client = game.getClients().get(nickname);
                    try {
                        client.genericChoiceError(e.getMessage());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            default -> {
                Logger.error("carta non valida per effetto activeDoubleEngine");
            }
        }
    }

    public void leaveFlight(int idGame, String nickname) {
        //TODO: this is only for testing of the circuit update, this must be replaced with the actual functionality
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        flyBoard.moveDays(flyBoard.getPlayerByUsername(nickname), 4);

        for (String nick : game.getClients().keySet()) {
            VirtualClient client = game.getClients().get(nick);
            try {
                client.advancePlayer(nickname, 4, 0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void skipEffect(int idGame, String nickname, int idCard) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();
        SldAdvCard card = flyBoard.getPlayedCard();

        if (idCard == card.getId() && nickname.equals(card.getActualPlayer().getNickname())) {
            Logger.debug("Salto effetto carta " + idCard);
            switch (card) {
                case SldAbandonedShip abandonedShip -> {
                    abandonedShip.applyEffect(nickname, false, null);
                    card.setNextPlayer();
                }

                case SldAbandonedStation abandonedStation -> {
                    abandonedStation.setNextPlayer();
                }

                case SldSmugglers sldSmugglers -> {
                    sldSmugglers.setNextPlayer();
                }

                case SldPlanets sldPlanets -> {
                    card.notifyGoodsPlacementFinished(game.getFlyboard().getPlayerByUsername(nickname));
                    Logger.debug("ReadyToProceed: " + sldPlanets.getReadyToProceed());
                    Logger.debug("AllPlayersPlacedGoods: " + sldPlanets.allPlayersPlacedGoods());
                    if (sldPlanets.getReadyToProceed() && sldPlanets.allPlayersPlacedGoods()) {
                        card.applyEffect();
                    }
                }

                default -> Logger.error("carta non implementata - per salto effetto");
            }
            //if(!(card instanceof SldPlanets))
            //card.setNextPlayer();
        }
    }

    public void applyEffect(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();
        Player player = game.getFlyboard().getPlayerByUsername(nickname);

        switch (card) {
            case SldAbandonedStation abandonedStation -> abandonedStation.applyEffect(player, true);

            default -> Logger.error("effetto carta non applicabile");
        }
    }

    public void removeCrew(int idGame, String nickname, List<Cordinate> cordToRemove) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        SldAdvCard card = flyBoard.getPlayedCard();

        switch (card) {
            case SldAbandonedShip abandonedShip -> {
                abandonedShip.applyEffect(nickname, true, cordToRemove);

                for (VirtualClient client : game.getClients().values()) {
                    try {
                        client.removeCrew(nickname, cordToRemove);
                        //commento perchè ho modificato con i listener il model di add credits
                        //client.addCredits(nickname, card.getCredits());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                abandonedShip.setNextPlayer();

            }

            case SldSlavers sldSlavers -> {
                Player player = flyBoard.getPlayerByUsername(nickname);
                sldSlavers.removeCrew(player, cordToRemove);
                for (VirtualClient client : game.getClients().values()) {
                    try {
                        client.removeCrew(nickname, cordToRemove);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            default -> Logger.error("Effetto carta non consentito");
        }
    }

    public void addGood(int idGame, String nickname, int idComp, GoodType type) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        try {
            game.getFlyboard().getComponentById(idComp).addGood(type);
            for (VirtualClient client : game.getClients().values()) {

                try {
                    client.addGood(idComp, type);
                    client.removeGoodPendingList(nickname, type);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IncorrectShipBoardException e) {
            try {
                game.getClients().get(nickname).genericChoiceError(e.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }


    }

    public void removeGood(int idGame, String nickname, int idComp, GoodType type) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        game.getFlyboard().getComponentById(idComp).removeGood(type);

        for (VirtualClient client : game.getClients().values()) {
            try {
                client.removeGood(idComp, type);
                client.addGoodPendingList(nickname, type);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            game.getClients().get(nickname).setCardState(CardState.GOODS_PLACEMENT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void landOnPlanet(int idGame, String nickname, int choice) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        Player player = game.getFlyboard().getPlayerByUsername(nickname);
        Logger.debug("player " + nickname + " land on planet number " + choice);
        SldAdvCard card = game.getFlyboard().getPlayedCard();
        card.land(player, choice);
        int passedPlayers = card.getPassedPlayers();

        VirtualClient c = game.getClients().get(nickname);

        if (choice != -1) {
            for (VirtualClient client : game.getClients().values()) {
                try {
                    client.setPlayerOnPlanet(nickname, choice);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (choice != -1) {
            try {
                c.setCardState(CardState.GOODS_PLACEMENT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Logger.debug("numero giocatori passati " + passedPlayers);
        if (passedPlayers == game.getNumPlayers() || card.getLandedPlayers().size() == card.getPlanets().size()) {
            card.setReadyToProceed(true);
        } else {
            card.setNextPlayer();
        }
        if (game.getFlyboard().getScoreBoard().getLast().equals(player) && choice == -1 && card.allPlayersPlacedGoods()) {
            card.applyEffect();
        }

    }

    public void activateDoubleDrills(int idGame, String nickname, List<Cordinate> drillCordinates) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        SldAdvCard card = game.getFlyboard().getPlayedCard();

        switch (card) {
            case SldSmugglers sldSmugglers -> {
                Logger.debug(nickname + drillCordinates);
                Player player = game.getFlyboard().getPlayerByUsername(nickname);
                sldSmugglers.applyEffect(player, true, drillCordinates);

                if (sldSmugglers.isStealGoods()) {
                    stealGoods(game, player, sldSmugglers.getStolenGoods());
                    sldSmugglers.setNextPlayer();
                } else if (sldSmugglers.isGiverReward()) {
                    VirtualClient client = game.getClients().get(nickname);
                    try {

                        client.setCardState(CardState.GOODS_PLACEMENT);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    sldSmugglers.setNextPlayer();
                }
            }

            case SldPirates sldPirates -> {
                Logger.debug(nickname + drillCordinates);
                Player player = flyBoard.getPlayerByUsername(nickname);

                sldPirates.loadPower(player, drillCordinates);
            }

            default -> Logger.error("effetto carta non consentito");
        }
    }

    public void activateSlaver(int idGame, String nickname, List<Cordinate> activatedDrills, boolean wantsToActivate) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();
        Logger.debug("player: " + nickname + " ha attivato " + activatedDrills + " doppicannoni");
        Player player = game.getFlyboard().getPlayerByUsername(nickname);
        switch (card) {
            case SldSlavers sldSlavers -> {
                sldSlavers.applyEffect(player, wantsToActivate, activatedDrills);
            }
            default -> Logger.error("effetto carta non consentito");
        }
    }


    private void stealGoods(GameServer game, Player player, int numberStolenGoods) {
        Logger.debug(player.getNickname() + "steal goods");
        Map<Integer, List<GoodType>> stolenGoods = player.getShipBoard().stoleGood(numberStolenGoods);

        for (Integer idComp : stolenGoods.keySet()) {
            for (GoodType type : stolenGoods.get(idComp)) {
                for (VirtualClient client : game.getClients().values()) {
                    try {
                        client.removeGood(idComp, type);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void setRollResult(int idGame, String nickname, int number) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();

        if (!nickname.equals(game.getFlyboard().getScoreBoard().getFirst().getNickname()))
            throw new IncorrectFlyBoardException("Not the leader");

        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                Logger.info("e' uscito " + number);

                Meteor meteor = meteorSwarm.getActualMeteor();
                meteor.setNumber(number);
                Direction direction = meteor.getDirection();
                MeteorType type = meteor.getType();

                for (VirtualClient client : game.getClients().values()) {
                    try {
                        client.meteorHit(type, direction, number);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            case SldPirates sldPirates -> {
                CannonPenalty cannon = sldPirates.getActualCannon();
                cannon.setNumber(number);
                Direction direction = cannon.getDirection();
                CannonType type = cannon.getCannonType();
                List<String> nicknameToHit = sldPirates.getPenaltyPlayers().stream().map(Player::getNickname).toList();

                for (String nick : game.getClients().keySet()){
                    if (nicknameToHit.contains(nick)){
                        try{
                            game.getClients().get(nick).cannonHit(type, direction, number);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            default -> Logger.error("No effect for setRollResult");

        }
    }

    public void removeBattery(int idGame, String nickname, int quantity) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);

        ShipBoard shipBoard = game.getFlyboard().getPlayerByUsername(nickname).getShipBoard();
        if (shipBoard.getQuantBatteries() < quantity) {
            throw new IncorrectShipBoardException("not enough batteries");
        }

        List<Integer> removedId = shipBoard.removeEnergy(quantity);

        for (VirtualClient client : game.getClients().values()) {
            try {
                client.removeBatteries(removedId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void advanceMeteor(int idGame, String nickname) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();

        switch (card) {
            case SldMeteorSwarm meteorSwarm -> {
                meteorSwarm.setNextMeteor(nickname);
            }

            default -> Logger.error("Effect not taken");
        }
    }

    public void advanceCannon(int idGame, String nickname){
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        SldAdvCard card = game.getFlyboard().getPlayedCard();

        switch (card){
            case SldPirates pirates ->{
                pirates.setNextCannon();
            }

            default -> Logger.error("Effect not taken");
        }
    }

    public void startHourglass(int idGame) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard fly = game.getFlyboard();
        fly.startHourglass(idGame);
        for(VirtualClient client : game.getClients().values()){
            try {
                client.startedHourglass(idGame);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    public void removeComponent(int idGame, String nickname, Cordinate cord) {
        GameServer game = GameManager.getInstance().getOngoingGames().get(idGame);
        FlyBoard flyBoard = game.getFlyboard();

        if (!flyBoard.getNicknameList().contains(nickname)) {
            throw new IncorrectFlyBoardException("Not player wit hthis nick");
        }

        ShipBoard shipBoard = flyBoard.getPlayerByUsername(nickname).getShipBoard();
        shipBoard.removeComponent(cord);

        for (VirtualClient client : game.getClients().values()) {
            try {
                client.removeComponent(nickname, cord);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}

