package org.mio.progettoingsoft.network.server;

import org.mio.progettoingsoft.GameManager;
import org.mio.progettoingsoft.exceptions.IncorrectClientException;
import org.mio.progettoingsoft.exceptions.IncorrectNameException;
import org.mio.progettoingsoft.exceptions.SetGameModeException;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.client.ClientSocket;
import org.mio.progettoingsoft.network.server.messages.GameInfoMessage;
import org.mio.progettoingsoft.network.server.messages.Message;
import org.mio.progettoingsoft.network.client.VirtualClient;
import org.mio.progettoingsoft.network.server.messages.SetNicknameMessage;
import org.mio.progettoingsoft.network.server.messages.WelcomeMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketClientHandler extends Server implements Runnable, VirtualServer{
    private final Socket socket;

    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public SocketClientHandler(Socket socket) throws RemoteException {
        super();
        this.socket =  socket;
    }

    @Override
    public void run(){
        try{
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            VirtualClient client = new ClientSocket();
            registerClient(client);

            ServerMessageHandler messageHandler = new ServerMessageHandler(in, receivedMessages);
            new Thread(messageHandler::run).start();

            while (true){
                try{
                    Message message = receivedMessages.take();

                    handleMessage(message);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message){
        switch (message){
            case WelcomeMessage welcomeMessage -> setNickname(welcomeMessage.getIdClient(), welcomeMessage.getNickname());
            case GameInfoMessage gameInfoMessage -> setGameInfo(gameInfoMessage.getGameInfo());

            default -> {}
        }
    }

    private void sendMessage(Message message){
        try {
            out.writeObject(message);
            out.flush();
            out.reset();
        }
        catch (IOException e){
            System.out.println("Impossible to send " + message);
        }

    }


    @Override
    public int registerClient(VirtualClient client){
        int idClient = GameManager.getInstance().addClientToAccept(client);
        sendMessage(new WelcomeMessage(0, "", idClient));
        return idClient;
    }

    @Override
    public void setNickname(int clientId, String nickname){
        try{
            GameManager.getInstance().addPlayerToGame(clientId, nickname);
            sendMessage(new SetNicknameMessage(GameManager.getInstance().getWaitingGame().getIdGame(), nickname, true, false));
        }
        catch (IncorrectNameException e){
            sendMessage(new SetNicknameMessage(GameManager.getInstance().getWaitingGame().getIdGame(), nickname, false, false));
        } catch (IncorrectClientException e) {
            throw new RuntimeException(e);
        }
        catch (SetGameModeException e){
            sendMessage(new SetNicknameMessage(GameManager.getInstance().getWaitingGame().getIdGame(), nickname, true, true));
        }
    }

    @Override
    public void setGameInfo(GameInfo gameInfo){
        GameManager.getInstance().getWaitingGame().setupGame(gameInfo.mode(), gameInfo.nPlayers());
    }


}
