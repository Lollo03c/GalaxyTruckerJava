package org.mio.progettoingsoft.network.client.socket;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.messages.*;
import org.mio.progettoingsoft.network.server.socket.VirtualServerSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SocketServerHandler implements VirtualServerSocket {
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public SocketServerHandler(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    public void sendWelcome() throws IOException {
        sendMessage(new WelcomeMessage(-1, "", -1));
    }

    @Override
    public void handleNickname(int tempIdPlayer, String nickname) throws IOException {
        Message message = new NicknameMessage(-1, nickname, tempIdPlayer);
        sendMessage(message);
    }

    @Override
    public void handleGameInfo(GameInfo gameInfo, String nickname) throws IOException {
        Message message = new GameInfoMessage(-1, nickname, gameInfo);
        sendMessage(message);
    }

    @Override
    public void getCoveredComponent(int idGame, String nickname) throws IOException {
        Message message = new ComponentMessage(idGame, nickname, ComponentMessage.Action.COVERED, 0, null, 0);
        sendMessage(message);
    }

    @Override
    public void addComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations) throws IOException {
        Message message = new ComponentMessage(idGame, nickname, ComponentMessage.Action.ADD, idComp, cordinate, rotations);
        sendMessage(message);
    }

    @Override
    public void discardComponent(int idGame, int idComponent) throws IOException {
        Message message = new ComponentMessage(idGame, "", ComponentMessage.Action.DISCARD, idComponent, null, 0);
        sendMessage(message);
    }

    @Override
    public void drawUncovered(int idGame, String nickname, int idComponent) throws IOException {
        Message message = new ComponentMessage(idGame, nickname, ComponentMessage.Action.DRAW_UNCOVERED, idComponent, null, 0);
        sendMessage(message);
    }

    @Override
    public void bookDeck(int idGame, String nickname, int deckNumber) throws IOException {
        Message message = new DeckMessage(idGame, nickname, DeckMessage.Action.BOOK, deckNumber);
        sendMessage(message);
    }

    @Override
    public void freeDeck(int idGame, String nickname, int deckNumber) throws IOException {
        Message message = new DeckMessage(idGame, nickname, DeckMessage.Action.UNBOOK, deckNumber);
        sendMessage(message);
    }

    /**
     * utility method used to send messages to the server
     *
     * @param message
     */
    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
        out.reset();
    }
}
