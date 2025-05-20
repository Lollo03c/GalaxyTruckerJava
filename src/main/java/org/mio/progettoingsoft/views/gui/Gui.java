package org.mio.progettoingsoft.views.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.mio.progettoingsoft.Game;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.views.View;

import java.beans.PropertyChangeEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Gui extends Application implements View {

    private final ClientController controller;
    private Stage stage;
    private StackPane root;
    private final BlockingQueue<GameState> statesQueue = new LinkedBlockingQueue<>();

    public Gui() {
        controller = ClientController.getInstance();
        controller.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("gameState")) {
            statesQueue.add((GameState) evt.getNewValue());
        }
    }

    @Override
    public void run() {
        Application.launch(Gui.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.root = new StackPane();
        this.stage.setTitle("Galaxy Trucker");
        this.stage.setScene(new Scene(root, 1000, 600));
        this.updateGui(GameState.START);
        this.stage.show();
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    GameState state = statesQueue.take();
                    Platform.runLater(() -> updateGui(state));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void updateGui(GameState state) {
        switch (state) {
            case START -> firstView();
            case WAITING -> loadingView();
            case NICKNAME -> nicknameRequestView();
            case GAME_MODE -> askForSettingsView();
            case WAITING_PLAYERS -> waitingForPlayersView();
            case BUILDING_SHIP -> buildingShipView();
            default -> genericErrorView(state);
        }
        stage.show();
    }

    /*
     * Scene building section
     */

    private void firstView() {
        root.getChildren().clear();
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        Label selectionLabel = new Label("Choose the type of connection:");
        HBox selectionBox = new HBox(10);
        ToggleGroup group = new ToggleGroup();
        RadioButton rmiRadio = new RadioButton("RMI connection");
        rmiRadio.setToggleGroup(group);
        rmiRadio.setSelected(true);
        RadioButton socketRadio = new RadioButton("Socket connection");
        socketRadio.setToggleGroup(group);
        selectionBox.getChildren().addAll(rmiRadio, socketRadio);
        selectionBox.setAlignment(Pos.CENTER);
        Button connectButton = new Button("Connect");
        connectButton.setOnAction(event -> connectToServer(rmiRadio.isSelected()));
        VBox box = new VBox(10);
        box.getChildren().addAll(selectionLabel, selectionBox, connectButton);
        root.getChildren().add(box);
    }

    private void loadingView() {
        root.getChildren().clear();
        Label loadingLabel = new Label("Loading...");
        HBox loadingBox = new HBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.getChildren().addAll(loadingLabel);
        root.getChildren().add(loadingBox);
    }

    private void nicknameRequestView() {
        root.getChildren().clear();
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        Label label = new Label("Insert nickname:");
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Insert your nickname");
        Button sendButton = new Button("Confirm");
        sendButton.setOnAction(event -> handleNickname(nicknameField.getText()));
        box.getChildren().addAll(label, nicknameField, sendButton);
        root.getChildren().add(box);
    }

    private void askForSettingsView() {
        root.getChildren().clear();
        VBox box = new VBox(25);

        VBox playersBox = new VBox(10);
        playersBox.setAlignment(Pos.CENTER);
        Label playersLabel = new Label("Select number of players:");
        ToggleGroup playersGroup = new ToggleGroup();
        RadioButton twoPlayersRadio = new RadioButton("Two players");
        twoPlayersRadio.setToggleGroup(playersGroup);
        twoPlayersRadio.setSelected(true);
        RadioButton threePlayersRadio = new RadioButton("Three players");
        threePlayersRadio.setToggleGroup(playersGroup);
        RadioButton fourPlayersRadio = new RadioButton("Four players");
        fourPlayersRadio.setToggleGroup(playersGroup);
        playersBox.getChildren().addAll(playersLabel, twoPlayersRadio, threePlayersRadio, fourPlayersRadio);
        box.getChildren().add(playersBox);

        VBox gameModeBox = new VBox(10);
        gameModeBox.setAlignment(Pos.CENTER);
        Label gameModeLabel = new Label("Select game mode:");
        ToggleGroup gameModeGroup = new ToggleGroup();
        RadioButton normalRadio = new RadioButton("Normal");
        normalRadio.setToggleGroup(gameModeGroup);
        normalRadio.setSelected(true);
        RadioButton easyRadio = new RadioButton("Easy");
        easyRadio.setToggleGroup(gameModeGroup);
        gameModeBox.getChildren().addAll(gameModeLabel, normalRadio, easyRadio);
        box.getChildren().add(gameModeBox);

        Button sendButton = new Button("Confirm");
        sendButton.setOnAction(event -> {
            int nPlayers = fourPlayersRadio.isSelected() ? 4
                            : threePlayersRadio.isSelected() ? 3
                            : 2;
            handleGameInfo(nPlayers, normalRadio.isSelected());
        });
        box.getChildren().add(sendButton);

        root.getChildren().add(box);
    }

    private void genericErrorView(GameState state) {
        root.getChildren().clear();
        Label errorLabel = new Label("Generic error! State: " + state);
        root.getChildren().add(errorLabel);
    }

    private void waitingForPlayersView() {
        root.getChildren().clear();
        VBox box = new VBox(10);
        Label waitingLabel = new Label("Waiting for players!");
        Label gameInfoLabel = new Label("Game info:");
        box.getChildren().addAll(waitingLabel, gameInfoLabel);
//        HBox gameInfoBox = new HBox(10);
//        gameInfoBox.setAlignment(Pos.CENTER);
//        GameInfo info = controller.getGameInfo();
//        Label nPlayersLabel = new Label("Number of players: " + info.nPlayers());
//        Label gameModeLabel = new Label("Game mode: " + info.mode());
//        gameInfoBox.getChildren().addAll(nPlayersLabel, gameModeLabel);
//        box.getChildren().add(gameInfoBox);
        root.getChildren().add(box);
    }

    private void buildingShipView(){
        root.getChildren().clear();
        Label shipLabel = new Label("Qua dovremmo costruire la nave");
    }

    /*
     * Callback routines section
     */

    // TODO: dovremmo poter lasciar scegliere al client l'ip e la porta del server (lo facciamo lato UI oppure in fase di lancio dell'applicazione?)
    private void connectToServer(boolean isRmi) {
        controller.connectToServer(isRmi);
    }

    private void handleNickname(String nickname) {
        if (nickname.isBlank()) {
            Label nickError = new Label("Nickname cannot be empty");
            root.getChildren().add(nickError);
        } else {
            controller.handleNickname(nickname);
        }
    }

    private void handleGameInfo(int nPlayers, boolean isNormal){
        GameInfo info = new GameInfo(-1, isNormal ? GameMode.NORMAL : GameMode.EASY, nPlayers);
        controller.setGameInfo(info);
    }
}
