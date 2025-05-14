package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.server.messages.GameInfoMessage;
import org.mio.progettoingsoft.network.server.messages.Message;
import org.mio.progettoingsoft.network.server.messages.SetNicknameMessage;
import org.mio.progettoingsoft.network.server.messages.WelcomeMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSocket extends Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int port = 1050;

    private ClientController controller = ClientController.getInstance();

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final BlockingQueue<Message> receivedMessaged = new LinkedBlockingQueue<>();

    public ClientSocket() throws RemoteException {
        super();
    }

    @Override
    public void connect(){

        try {
            socket = new Socket(SERVER_ADDRESS, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registryClient(){
        try {
            WelcomeMessage message = (WelcomeMessage) in.readObject();
            controller.setIdClient(message.getIdClient());

            ClientMessageHandler messageHandler = new ClientMessageHandler(in, receivedMessaged);
            new Thread(messageHandler::run).start();
            new Thread(this::startHandling).start();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void startHandling() {
        while(true){
            try {
                Message message = receivedMessaged.take();

                switch (message){
                    case WelcomeMessage welcomeMessage-> receivedMessaged.add(welcomeMessage);
                    case SetNicknameMessage nicknameMessage -> {
                        if (nicknameMessage.isAccepted()){
                            controller.setConfirmedNickname(nicknameMessage.getNickname());
                            controller.setIdGame(nicknameMessage.getIdGame());

                            controller.setState(nicknameMessage.isToSetup() ? GameState.GAME_MODE : GameState.WAITING);
                        }
                        else {
                            controller.setState(GameState.ERROR_NICKNAME);
                        }
                    }

                    default -> controller.setState(GameState.WAITING);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendMessage(Message message){

        try {
            out.writeObject(message);
            out.flush();
            out.reset();

        } catch (IOException e) {
            System.out.println("Impossibile inviare il messaggio");
            e.printStackTrace();
        }

    }

    @Override
    public void setNickname(String nickname){
        Message message = new WelcomeMessage(0, nickname, controller.getIdClient());
        sendMessage(message);
    }

    @Override
    public void setGameInfo(GameInfo gameInfo){
        Message message = new GameInfoMessage(controller.getIdGame(), controller.getNickname(), gameInfo);
        sendMessage(message);
    }

    @Override
    public void setState(GameState newState) throws RemoteException{
        int a = 0;
    }
}
