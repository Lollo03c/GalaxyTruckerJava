package org.mio.progettoingsoft.network.client;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSocket extends Client{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    @Override
    public void connect(){
        try{
            socket = new Socket("localhost", 1050);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            ClientMessageReceiver clientMessageReceiver = new ClientMessageReceiver(in, receivedMessages);
            new Thread(clientMessageReceiver).start();

            new Thread(this::handleMessage).start();


            sendMessage(new WelcomeMessage(-1, "", -1));


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessage(Message message){
        try{
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMessage(){
        while (true){
            try {
                Message message = receivedMessages.take();

                switch (message){
                    case WelcomeMessage welcomeMessage -> {
                        idClient = welcomeMessage.getClientId();
                    }
                    case StateMessage stateMessage -> {
                        controller.setState(stateMessage.getState());
                    }

                    case NicknameMessage nicknameMessage -> {
                        controller.setNickname(nicknameMessage.getNickname());
                    }

                    case GameIdMessage gameIdMessage -> {
                        controller.setGameId(gameIdMessage.getGameId());
                    }

                    default -> {
                        System.out.println("Received " + message);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ;
        }
    }

    @Override
    public void ping(String msg){
        return ;
    }

    @Override
    public void handleNickname(String nickname){
        Message message = new NicknameMessage(-1, nickname, idClient);
        sendMessage(message);

    }

    @Override
    public void handleGameInfo(GameInfo gameInfo){

    }

    @Override
    public int getCoveredComponent(int idGame){
        return -1;
    }

    @Override
    public void handleComponent(int idGame, String nickname, int idComp, Cordinate cordinate, int rotations){

    }
}
