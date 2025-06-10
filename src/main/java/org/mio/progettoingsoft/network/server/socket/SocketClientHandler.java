package org.mio.progettoingsoft.network.server.socket;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.components.HousingColor;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class SocketClientHandler implements VirtualClient, Runnable {
    private final Socket clientSocket;
    private final BlockingQueue<Message> receivedMessages;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public SocketClientHandler(Socket socket, BlockingQueue<Message> receivedMessages) throws IOException {
        this.clientSocket = socket;
        this.receivedMessages = receivedMessages;
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    @Override
    public void run(){
        try{
            WelcomeMessage welcomeMessage = (WelcomeMessage) in.readObject();
            int idClient = GameManager.getInstance().addClientToAccept(this);
            out.writeObject(new WelcomeMessage(-1, "", idClient));
            out.flush();
            out.reset();


            while (true) {
                try {
                    receivedMessages.add((Message) in.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Client crashed - " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
        out.reset();
    }

    /**
     * Metodi derivati da VirtualClient, ovvero i metodi che vengono chiamati dal server
     */

    @Override
    public void ping(String msg) throws IOException {

    }

    @Override
    public void setNickname(String nickname) throws IOException {
        Message message = new NicknameMessage(-1, nickname, -1);
        sendMessage(message);
    }

    @Override
    public void askGameSettings(String nickname) throws IOException {
        Message message = new StateMessage(0, "", GameState.GAME_MODE);
        sendMessage(message);
    }

    @Override
    public void wrongNickname() throws IOException {
        Message message = new StateMessage(0, "", GameState.ERROR_NICKNAME);
        sendMessage(message);
    }

    @Override
    public void setGameId(int gameId) throws IOException {
        Message message = new GameIdMessage(gameId, "");
        sendMessage(message);
    }

    @Override
    public void setState(GameState state) throws IOException {
        Message message = new StateMessage(0, "", state);
        sendMessage(message);
    }

    @Override
    public void setCardState(CardState state) throws IOException{
        Message message = new CardStateMessage(0, "", state);
        sendMessage(message);
    }

    @Override
    public void setFlyBoard(GameMode mode, Map<String, HousingColor> players, List<List<Integer>> decks) throws IOException {
        Message message = new FlyBoardMessage(-1, "", mode, players, decks);
        sendMessage(message);
    }

    @Override
    public void addComponent(String nickname, int idComp, Cordinate cordinate, int rotations) throws IOException {
        Message message = new ComponentMessage(0, nickname, ComponentMessage.Action.ADD, idComp, cordinate, rotations );
        sendMessage(message);
    }

    @Override
    public void setInHandComponent(int idComp) throws IOException {
        Message message = new ComponentMessage(0, "", ComponentMessage.Action.COVERED, idComp, null, 0);
        sendMessage(message);
    }

    @Override
    public void addUncoveredComponent(int idComp) throws IOException {
        Message message = new ComponentMessage(0, "", ComponentMessage.Action.ADD_UNCOVERED, idComp, null, 0);
        sendMessage(message);
    }

    @Override
    public void removeUncovered(int idComp) throws IOException {
        Message message = new ComponentMessage(0, "", ComponentMessage.Action.DRAW_UNCOVERED, idComp, null, 0);
        sendMessage(message);
    }

    @Override
    public void removeDeck(Integer idDeck) throws IOException {
        Message message = new DeckMessage(0, "", DeckMessage.Action.REMOVE_FROM_CLIENT, idDeck);
        sendMessage(message);
    }

    @Override
    public void setInHandDeck(int deckNumber) throws IOException {
        Message message = new DeckMessage(0, "", DeckMessage.Action.BOOK, deckNumber);
        sendMessage(message);
    }

    @Override
    public void addAvailableDeck(int deckNumber) throws IOException {
        Message message = new DeckMessage(0, "", DeckMessage.Action.UNBOOK, deckNumber);
        sendMessage(message);
    }

    @Override
    public void setBuiltShip(String nickname, int indexBuild) throws IOException{
        Message message = new BuildShipMessage(0, nickname, indexBuild);
        sendMessage(message);
    }

    @Override
    public void setAvailablePlaces(List<Integer> availablePlaces) throws Exception{
        Message message = new AvailablePlacesMessage(0, "", availablePlaces);
        sendMessage(message);
    }

    @Override
    public void addOtherPlayerToCircuit(String nickname, int place) throws Exception {
        Message message = new AddPlayerMessage(0, "", nickname, place);
        sendMessage(message);
    }

    @Override
    public void advancePlayer(String nickname, int steps) throws IOException{
        Message message = new AdvancePlayerMessage(0, nickname, steps);
        sendMessage(message);
    }

    @Override
    public void setPlayedCard(int idCard) throws IOException{
        Message message = new DrawCardMessage(0, "", idCard);
        sendMessage(message);
    }

    @Override
    public void addCredits(int credits) throws IOException{
        Message message = new AddCreditsMessage(0, "", credits);
        sendMessage(message);
    }

    @Override
    public void crewLost(String nickname, List<Cordinate> housingCordinates)throws IOException{
        Message message = new CrewLostMessage(0,nickname,housingCordinates);
        sendMessage(message);
    }


}
