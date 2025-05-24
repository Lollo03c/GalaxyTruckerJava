package org.mio.progettoingsoft.views.gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.mio.progettoingsoft.Component;
import org.mio.progettoingsoft.FlyBoard;
import org.mio.progettoingsoft.GameState;
import org.mio.progettoingsoft.Player;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.views.View;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Gui extends Application implements View {

    private final ClientController controller;
    private final BlockingQueue<GameState> statesQueue = new LinkedBlockingQueue<>();
    private boolean firstBuilding = true;

    private final String imgPath = "/images/";
    private final String tilesRelPath = "tiles/GT-new_tiles_16_for web";
    private final String imgExtension = ".jpg";

    private int tmpRotation;

    private Stage stage;
    private StackPane root;
    private VBox nicknameBox;
    private Label errorNicknameLabel;
    private BorderPane shipViewBorderPane;
    private HBox shipTopBox;
    private HBox shipTilesDeckBox;
    private HBox shipAdvDeckBox;
    private VBox shipRightColumn;
    private VBox inHandBox;
    private ImageView inHandImageView;
    private VBox viewOtherPlayersBox;
    private Stage uncoveredComponentModalStage;
    private TilePane uncoveredComponentsTilePane;

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
        this.stage.setMaximized(true);
        shipViewBorderPane = new BorderPane();
        this.stage.show();
        this.updateGui(GameState.START);
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
            case START -> firstView()/*buildingShipView()*/;
            case WAITING -> loadingView();
            case NICKNAME -> nicknameRequestView(false);
            case ERROR_NICKNAME -> nicknameRequestView(true);
            case GAME_MODE -> askForSettingsView();
            case WAITING_PLAYERS -> waitingForPlayersView();
            case BUILDING_SHIP -> buildingShipView();
            case COMPONENT_MENU -> addComponentView();
            case UNABLE_UNCOVERED_COMPONENT -> unableUncoveredView();
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

    private void nicknameRequestView(boolean isWrong) {
        root.getChildren().clear();
        nicknameBox = new VBox(10);
        nicknameBox.setAlignment(Pos.CENTER);
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        if(isWrong){
            errorNicknameLabel = new Label("Nickname already take! Try again");
            nicknameBox.getChildren().addAll(errorNicknameLabel);
        }
        Label label = new Label("Insert nickname:");
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Insert your nickname");
        Button sendButton = new Button("Confirm");
        sendButton.setOnAction(event -> handleNickname(nicknameField.getText()));
        box.getChildren().addAll(label, nicknameField, sendButton);
        nicknameBox.getChildren().add(box);
        root.getChildren().add(nicknameBox);
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
//        Label gameInfoLabel = new Label("Game info:");
//        box.getChildren().addAll(waitingLabel, gameInfoLabel);
//        HBox gameInfoBox = new HBox(10);
//        gameInfoBox.setAlignment(Pos.CENTER);
//        GameInfo info = controller.getGameInfo();
//        Label nPlayersLabel = new Label("Number of players: " + info.nPlayers());
//        Label gameModeLabel = new Label("Game mode: " + info.mode());
//        gameInfoBox.getChildren().addAll(nPlayersLabel, gameModeLabel);
//        box.getChildren().add(gameInfoBox);
        box.getChildren().add(waitingLabel);
        root.getChildren().add(box);
    }

    private void buildingShipView() {
        if (firstBuilding) {
            root.getChildren().clear();
            /* ----------------------- TOP BOX ----------------------- */
            shipTopBox = new HBox();
            /* Tiles decks */
            shipTilesDeckBox = new HBox(15);
            shipTilesDeckBox.setPadding(new Insets(20));
            shipTilesDeckBox.setAlignment(Pos.CENTER);
            shipTilesDeckBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
            HBox.setHgrow(shipTilesDeckBox, Priority.ALWAYS);
            // tiles buttons may be replaced by the tile back
            Button coveredDeck = new Button("Covered Deck");
            Button uncoveredDeck = new Button("Uncovered Deck");
            List<Button> tilesDeckButtons = new ArrayList<>();
            tilesDeckButtons.add(coveredDeck);
            tilesDeckButtons.add(uncoveredDeck);
            for (Button button : tilesDeckButtons) {
                button.setPrefWidth(100);
                button.prefHeightProperty().bind(coveredDeck.widthProperty());
                button.setWrapText(true);
                button.setTextAlignment(TextAlignment.CENTER);
            }
            coveredDeck.setOnAction(event -> drawCoveredComponent());
            uncoveredDeck.setOnAction(event -> showUncoveredComponent());
            shipTilesDeckBox.getChildren().addAll(coveredDeck, uncoveredDeck);

            /* Adventure cards decks */
            shipAdvDeckBox = new HBox(10);
            shipAdvDeckBox.setPadding(new Insets(20));
            shipAdvDeckBox.setAlignment(Pos.CENTER);
            shipAdvDeckBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
            HBox.setHgrow(shipAdvDeckBox, Priority.ALWAYS);
            Button advDeck1 = new Button("Adventure card deck 1");
            Button advDeck2 = new Button("Adventure card deck 2");
            Button advDeck3 = new Button("Adventure card deck 3");
            List<Button> advDeckButtons = new ArrayList<>();
            advDeckButtons.add(advDeck1);
            advDeckButtons.add(advDeck2);
            advDeckButtons.add(advDeck3);
            for (Button button : advDeckButtons) {
                /* buttons style */
            }
            shipAdvDeckBox.getChildren().addAll(advDeck1, advDeck2, advDeck3);

            /* Time management commands */
            HBox hourglassBox = new HBox(10);
            hourglassBox.setPadding(new Insets(20));
            hourglassBox.setAlignment(Pos.CENTER);
            hourglassBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
            HBox.setHgrow(hourglassBox, Priority.ALWAYS);
            Label hourglassLabel = new Label("Hourglass");
            hourglassBox.getChildren().addAll(hourglassLabel);

            shipTopBox.getChildren().addAll(shipTilesDeckBox, shipAdvDeckBox, hourglassBox);
            shipTopBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
            shipViewBorderPane.setTop(shipTopBox);

            /* ----------------------- RIGHT COLUMN ----------------------- */
            shipRightColumn = new VBox(10);
            shipRightColumn.setPadding(new Insets(20));
            shipRightColumn.setAlignment(Pos.CENTER);
            shipRightColumn.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));

            /* List of button with other player's name */
            viewOtherPlayersBox = new VBox(5);
            viewOtherPlayersBox.setPadding(new Insets(10));

            List<Player> playerList;
            synchronized (controller.getFlyboardLock()) {
                playerList = controller.getFlyBoard().getPlayers();
            }
            for (Player player : playerList) {
                if (!player.getNickname().equals(controller.getNickname())) {
                    Button butt = new Button("Show " + player.getNickname() + "'s shipboard");
                    butt.setTextAlignment(TextAlignment.CENTER);
                    butt.setWrapText(true);
                    butt.setOnAction(event -> lookAtOtherShipboard(player.getNickname()));
                    viewOtherPlayersBox.getChildren().add(butt);
                }
            }

            /* Box with commands for the in hand component (and the component itself) */
            controller.resetTmpRotation();
            inHandBox = new VBox(5);
            inHandBox.setPadding(new Insets(10));
            inHandBox.setAlignment(Pos.CENTER);
            /* Component */
            String tmpResourcePath = imgPath + tilesRelPath + "157" + imgExtension;
            Image inHandImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
            inHandImageView = new ImageView(inHandImage);
            inHandBox.getChildren().addAll(inHandImageView);
            /* Commands */
            TilePane buttonBox = new TilePane();
            buttonBox.setPrefColumns(2);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10));
            buttonBox.setHgap(5);
            buttonBox.setVgap(10);
            Button placeButton = new Button("Place component");
            Button discardButton = new Button("Discard component");
            discardButton.setOnAction(event -> {
                controller.discardComponent();
            });
            Button bookButton = new Button("Book component");
            Button rotateButton = new Button("Rotate component");
            rotateButton.setOnAction(event -> {
                controller.increaseTmpRotation();
                inHandImageView.setRotate(controller.getTmpRotation() * 90);
            });
            buttonBox.getChildren().addAll(placeButton, discardButton, bookButton, rotateButton);
            inHandBox.getChildren().addAll(buttonBox);
            inHandBox.setDisable(true);

            shipRightColumn.getChildren().addAll(viewOtherPlayersBox, inHandBox);
            shipViewBorderPane.setRight(shipRightColumn);
            shipViewBorderPane.setDisable(false);

            root.getChildren().add(shipViewBorderPane);

            uncoveredComponentModalStage = new Stage();

            firstBuilding = false;
        }else{
            String tmpResourcePath = imgPath + tilesRelPath + "157" + imgExtension;
            Image inHandImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
            inHandImageView.setImage(inHandImage);
            shipViewBorderPane.setDisable(false);
            shipTilesDeckBox.setDisable(false);
            shipAdvDeckBox.setDisable(false);
        }
    }

    private void addComponentView() {
        shipViewBorderPane.setDisable(true);
        Component inHand = controller.getInHandComponentObject();
        int idComponent = inHand.getId();
        VBox vBox = new VBox(15);
        vBox.setAlignment(Pos.CENTER);
        vBox.setBackground(new Background(new BackgroundFill(Color.GREY, null, null)));
        vBox.setMaxWidth(300);
        vBox.setMaxHeight(350);
        Label label = new Label("You got this component:");
        String tmpResourcePath = imgPath + tilesRelPath + (idComponent != 1 ? idComponent : "") + imgExtension;
        Image image = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
        ImageView imageView = new ImageView(image);
        Button okButton = new Button("Ok");
        okButton.setOnAction(event -> {
            root.getChildren().remove(vBox);
            shipViewBorderPane.setDisable(false);
            shipTilesDeckBox.setDisable(true);
            shipAdvDeckBox.setDisable(true);
            inHandBox.setDisable(false);
            inHandImageView.setImage(image);
        });
        vBox.getChildren().addAll(label, imageView, okButton);
        root.getChildren().add(vBox);
    }

    private void unableUncoveredView(){
        StackPane sp = new StackPane();
        sp.getChildren().add(new Label("This component is no more available"));
        sp.setAlignment(Pos.CENTER);
        sp.setPadding(new Insets(10,20,10,20));
        uncoveredComponentModalStage = new Stage();
        uncoveredComponentModalStage.setScene(new Scene(sp));
        uncoveredComponentModalStage.show();
        uncoveredComponentModalStage.setResizable(false);
        System.out.println("test");
        uncoveredComponentModalStage.setOnCloseRequest(event -> controller.setState(GameState.BUILDING_SHIP));
    }
    /*
     * Callback routines section
     */

    private void connectToServer(boolean isRmi) {
        controller.connectToServer(isRmi);
    }

    private void handleNickname(String nickname) {
        if (nickname.isBlank()) {
            nicknameBox.getChildren().remove(errorNicknameLabel);
            errorNicknameLabel = new Label("Nickname cannot be empty");
            nicknameBox.getChildren().addFirst(errorNicknameLabel);
        } else {
            controller.setState(GameState.WAITING);
            controller.handleNickname(nickname);
        }
    }

    private void handleGameInfo(int nPlayers, boolean isNormal) {
        GameInfo info = new GameInfo(-1, isNormal ? GameMode.NORMAL : GameMode.EASY, nPlayers);
        controller.handleGameInfo(info);
    }

    private void drawCoveredComponent() {
        controller.handleBuildingShip(1);
    }

    private void showUncoveredComponent() {
        FlyBoard fly;
        synchronized (controller.getFlyboardLock()) {
            fly = controller.getFlyBoard();
        }
        List<Integer> componentIdList = fly.getUncoveredComponents();

        uncoveredComponentsTilePane = new TilePane();
        if(!componentIdList.isEmpty()) {
            for (Integer componentId : componentIdList) {
                String tmpResourcePath = imgPath + tilesRelPath + (componentId != 1 ? componentId : "") + imgExtension;
                Image tileImage = new Image(this.getClass().getResource(tmpResourcePath).toExternalForm());
                ImageView tileImageView = new ImageView(tileImage);
                tileImageView.setFitHeight(100);
                tileImageView.setPreserveRatio(true);
                tileImageView.setOnMouseClicked(event -> chooseComponentFromUncovered(componentId));
                uncoveredComponentsTilePane.getChildren().add(tileImageView);
            }
        }else{
            uncoveredComponentsTilePane.getChildren().add(new Label("No uncovered components"));
        }
        /* ------------- UNCOVERED COMPONENT CONTAINER ----------- */
        uncoveredComponentModalStage = new Stage();
        uncoveredComponentModalStage.initModality(Modality.APPLICATION_MODAL);
        uncoveredComponentModalStage.setTitle("Uncovered Components");
        uncoveredComponentModalStage.setResizable(false);
        uncoveredComponentsTilePane.setVisible(false);
        uncoveredComponentsTilePane.setAlignment(Pos.CENTER);
        uncoveredComponentsTilePane.setHgap(15);
        uncoveredComponentsTilePane.setVgap(15);
        uncoveredComponentsTilePane.setPadding(new Insets(5));
        uncoveredComponentsTilePane.setPrefColumns(10);
        ScrollPane uncoveredComponentsScrollPane = new ScrollPane(uncoveredComponentsTilePane);
        uncoveredComponentsScrollPane.setFitToWidth(true);
        uncoveredComponentsScrollPane.setPannable(true);
        uncoveredComponentModalStage.setScene(new Scene(uncoveredComponentsScrollPane, 900, 550));
        uncoveredComponentsTilePane.setVisible(true);
        uncoveredComponentModalStage.show();
        uncoveredComponentModalStage.setOnCloseRequest(event -> uncoveredComponentModalStage.close());
    }

    private void chooseComponentFromUncovered(int componentId) {
        controller.drawUncovered(componentId);
        uncoveredComponentModalStage.close();
        uncoveredComponentsTilePane.setVisible(false);
    }

    private void lookAtOtherShipboard(String nickname) {
        Label lab = new Label("Ok: " + nickname);
        root.getChildren().add(lab);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            root.getChildren().remove(lab);
        });
        pause.play();
    }
}
