package org.mio.progettoingsoft.views.gui;

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
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.model.enums.GameMode;
import org.mio.progettoingsoft.network.client.ClientController;
import org.mio.progettoingsoft.utils.Logger;
import org.mio.progettoingsoft.views.View;

import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GUI class: we decided to use the JavaFX framework
 * the GUI (as well as the TUI) is implemented as a property change listener
 * it has been made to make the GUI change with the game state, probably there will be more properties
 * (for example a change in the circuit)
 */
public class Gui extends Application implements View {

    private final ClientController controller;
    private final BlockingQueue<GameState> statesQueue = new LinkedBlockingQueue<>();

    private static final String IMG_PATH = "/images/";
    private static final String TILES_REL_PATH = "tiles/GT-new_tiles_16_for web";
    private static final String CARDBOARDS_REL_PATH = "cardboards/";
    private static final String ADC_CARD_REL_PATH = "advCards/GT-cards_";
    private static final String IMG_JPG_EXTENSION = ".jpg";
    private static final String IMG_PNG_EXTENSION = ".png";

    /*
     * utility fields used for the scene building
     */

    // true -> first time in the BUILDING_SHIP view
    private boolean firstBuilding = true;
    private double screenHeight;
    private double screenWidth;
    // height given to the tiles in the shipboard grid
    private double tilesSideLength;
    private Insets shipPadding = new Insets(28, 33, 28, 33);
    // maps used to identifie stackPane e imageView in a specific grid cell
    private final Map<Cordinate, StackPane> cordToStackPanes = new HashMap<>();
    private final Map<Cordinate, ImageView> cordToImageViews = new HashMap<>();
    // indicates if the click on a component is enabled (for placement or for being removed from booked)
    private boolean isComponentBoxClickable = false;

    private Stage stage;
    private StackPane root;
    // used only for the nickname choice
    private VBox nicknameBox;
    private Label errorNicknameLabel;
    // view components for the BUILDING_SHIP view
    private BorderPane shipViewBorderPane;
    private HBox shipTopBox;
    private HBox shipTilesDeckBox;
    private HBox shipAdvDeckBox;
    private VBox shipRightColumn;
    private VBox inHandBox;
    private ImageView inHandImageView;
    private VBox viewOtherPlayersBox;
    // modal stage and view components for the uncovered components "gallery"
    private Stage uncoveredComponentModalStage;
    private TilePane uncoveredComponentsTilePane;
    // view components for the actual ship visualization
    private BorderPane shipboardBorderPane;
    private VBox shipboardGridContainer;
    private GridPane shipboardGrid;
    private Button backButton;
    private Label hintLabel;
    // modal stage for other player ship displaying
    private Stage otherPlayerShipStage;
    // modal stage for position choosing
    private Stage choosePositionStage;

    public Gui() {
        controller = ClientController.getInstance();
        controller.addPropertyChangeListener(this);
    }

    /**
     * method called when a property changes, at the moment is used only in response to state changes
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("gameState")) {
            statesQueue.add((GameState) evt.getNewValue());
        }
    }

    /**
     * method inherited by the View interface.
     * as needed in the framework, it calls the launch method
     */
    @Override
    public void run() {
        Application.launch(Gui.class);
    }

    /**
     * starting method, it initializes the main stage and starts the thread that updates the view in response of game state changes
     *
     * @param stage: the main stage of the application
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.root = new StackPane();
        this.stage.setTitle("Galaxy Trucker");
        this.stage.setScene(new Scene(root));
        this.stage.setMaximized(true);
        shipViewBorderPane = new BorderPane();
        this.stage.show();
        screenHeight = Screen.getPrimary().getBounds().getHeight();
        screenWidth = Screen.getPrimary().getBounds().getWidth();
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

    /**
     * depending on the game state, it calls a method to build the UI
     *
     * @param state: game state taken from the state queue
     */
    private void updateGui(GameState state) {
        switch (state) {
            case START -> firstView() /*buildingShipView()*/;
            case WAITING -> loadingView();
            case NICKNAME -> nicknameRequestView(false);
            case ERROR_NICKNAME -> nicknameRequestView(true);
            case GAME_MODE -> askForSettingsView();
            case WAITING_PLAYERS -> waitingForPlayersView();
            case BUILDING_SHIP -> buildingShipView();
            case COMPONENT_MENU -> newComponentView();
            case UNABLE_UNCOVERED_COMPONENT -> unableUncoveredView();
            case ADD_COMPONENT -> placeComponentView();
            case SWITCH_BOOKED -> switchBookedComponentsView();
            case VIEW_DECK -> inspectDeckView(true);
            case UNABLE_DECK -> inspectDeckView(false);
            case CHOOSE_POSITION -> choosePositionView(false);
            case WRONG_POSITION -> choosePositionView(true);
            case END_BUILDING -> waitingForAdventureStartView();

            default -> genericErrorView(state);
        }
        stage.show();
    }

    /*
     * Scene building methods
     */

    /**
     * view showed when the app opens: the view asks the protocol to use to communicate with the server,
     * the "connect" button calls the method to connect to the server with the chosen protocol
     */
    private void firstView() {
        root.getChildren().clear();
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        VBox box = new VBox(10);

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

        box.getChildren().addAll(selectionLabel, selectionBox, connectButton);
        root.getChildren().add(box);
    }

    /**
     * generic loading view
     */
    private void loadingView() {
        root.getChildren().clear();
        Label loadingLabel = new Label("Loading...");
        HBox loadingBox = new HBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.getChildren().addAll(loadingLabel);
        root.getChildren().add(loadingBox);
    }

    /**
     * second view shown to the user: he can choose the nickname,
     * refreshed if the nickname is wrong
     *
     * @param isWrong: if true, the previously sent nickname was wrong, it shows an additional error label
     */
    private void nicknameRequestView(boolean isWrong) {
        root.getChildren().clear();
        nicknameBox = new VBox(10);
        nicknameBox.setAlignment(Pos.CENTER);

        if (isWrong) {
            errorNicknameLabel = new Label("Nickname already take! Try again");
            nicknameBox.getChildren().addAll(errorNicknameLabel);
        }

        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);
        Label label = new Label("Insert nickname:");
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Insert your nickname");
        Button sendButton = new Button("Confirm");
        sendButton.setOnAction(event -> handleNickname(nicknameField.getText()));
        box.getChildren().addAll(label, nicknameField, sendButton);
        nicknameBox.getChildren().add(box);

        root.getChildren().add(nicknameBox);
    }

    /**
     * third view shown to the user (if he is the first joining a new match): he can shoose the game settings
     */
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

    /**
     * mainly used for debug or not already implemented funcionalities
     *
     * @param state: the game state in which the server is
     */
    private void genericErrorView(GameState state) {
        root.getChildren().clear();
        Label errorLabel = new Label("Generic error! State: " + state);
        root.getChildren().add(errorLabel);
    }

    /**
     * simple view to notify the waiting for players situation,
     * maybe it will show some information about the joined match
     */
    private void waitingForPlayersView() {
        root.getChildren().clear();
        VBox box = new VBox(10);
        Label waitingLabel = new Label("Waiting for players!");
        box.getChildren().add(waitingLabel);
        root.getChildren().add(box);
    }

    /**
     * first game view: the ship building view.
     * more details in comments in the code
     */
    private void buildingShipView() {
        // if it's the first time showing this view, it is needed to initialize all the view components
        if (firstBuilding) {
            root.getChildren().clear();

            // used for rendering the tiles (semi) dynamically with different screen dimensions
            tilesSideLength = Math.min(screenHeight / 9, screenWidth / 16);

            /* ----------------------- TOP BOX ----------------------- */
            shipTopBox = new HBox();
            HBox.setHgrow(shipTopBox, Priority.ALWAYS);

            /* Tiles decks */
            shipTilesDeckBox = new HBox(15);
            shipTilesDeckBox.setPadding(new Insets(20));
            shipTilesDeckBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(shipTilesDeckBox, Priority.ALWAYS);
            Button coveredDeck = new Button("Covered Deck");
            Button uncoveredDeck = new Button("Uncovered Deck");
            List<Button> tilesDeckButtons = new ArrayList<>();
            tilesDeckButtons.add(coveredDeck);
            tilesDeckButtons.add(uncoveredDeck);
            for (Button button : tilesDeckButtons) {
                button.setPrefWidth(tilesSideLength * 1.1);
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
            HBox.setHgrow(shipAdvDeckBox, Priority.ALWAYS);

            List<Integer> decks;
            synchronized (controller.getFlyboardLock()) {
                decks = new ArrayList<>(controller.getFlyBoard().getAvailableDecks());
            }
            for (Integer i : decks) {
                Button advDeckButton = new Button("Adventure card deck " + (i + 1));
                advDeckButton.setOnAction(event -> {
                    controller.bookDeck(i);
                });
                shipAdvDeckBox.getChildren().add(advDeckButton);
            }

            /* Time management commands */
            // this functionality is still to be implemented (both in TUI and GUI)
            HBox hourglassBox = new HBox(10);
            hourglassBox.setPadding(new Insets(20));
            hourglassBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(hourglassBox, Priority.ALWAYS);
            Button endBuildBtn = new Button("Ship Ready!");
            endBuildBtn.setOnAction(event -> endBuild());
            hourglassBox.getChildren().addAll(endBuildBtn);

            shipTopBox.getChildren().addAll(shipTilesDeckBox, shipAdvDeckBox, hourglassBox);
            shipViewBorderPane.setTop(shipTopBox);

            /* ----------------------- RIGHT COLUMN ----------------------- */
            shipRightColumn = new VBox(10);
            shipRightColumn.setPadding(new Insets(20));
            shipRightColumn.setAlignment(Pos.CENTER);

            /* List of button with other player's name */
            viewOtherPlayersBox = new VBox(5);
            viewOtherPlayersBox.setPadding(new Insets(10));
            viewOtherPlayersBox.setAlignment(Pos.CENTER);
            List<Player> playerList;
            synchronized (controller.getFlyboardLock()) {
                playerList = new ArrayList<>(controller.getFlyBoard().getPlayers());
            }
            for (Player player : playerList) {
                if (!player.getNickname().equals(controller.getNickname())) {
                    Button butt = new Button("Show " + player.getNickname() + "'s shipboard");
                    butt.setTextAlignment(TextAlignment.CENTER);
                    butt.setWrapText(true);
                    butt.setOnAction(event -> lookAtOtherShipboardView(player.getNickname()));
                    viewOtherPlayersBox.getChildren().add(butt);
                }
            }

            /* Box with commands for the in hand component (and the component itself) */
            controller.resetTmpRotation(); // reset the rotation of the component in hand (because there's no in hand component)
            inHandBox = new VBox(5);
            inHandBox.setPadding(new Insets(10));
            inHandBox.setAlignment(Pos.CENTER);

            /* Component */
            String tmpResourcePath = IMG_PATH + TILES_REL_PATH + "157" + IMG_JPG_EXTENSION;
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
            placeButton.setOnAction(event -> controller.setState(GameState.ADD_COMPONENT));
            Button discardButton = new Button("Discard component");
            discardButton.setOnAction(event -> controller.discardComponent());
            Button bookButton = new Button("Book component");
            bookButton.setOnAction(event -> controller.bookComponent());
            Button rotateButton = new Button("Rotate component");
            rotateButton.setOnAction(event -> {
                controller.increaseTmpRotation();
                inHandImageView.setRotate(controller.getTmpRotation() * 90);
            });
            buttonBox.getChildren().addAll(placeButton, discardButton, bookButton, rotateButton);
            inHandBox.getChildren().addAll(buttonBox);
            inHandBox.setDisable(true); // disabled by default, they will be enabled only with a component in hand (drawn or chosen or swapped from booked)

            shipRightColumn.getChildren().addAll(viewOtherPlayersBox, inHandBox);
            shipViewBorderPane.setRight(shipRightColumn);

            /* ------------ SHIPBOARD CONTAINER ------------*/

            shipboardBorderPane = new BorderPane();

            /* shipboard tiles grid */
            shipboardGridContainer = new VBox(10);
            shipboardGridContainer.setAlignment(Pos.CENTER);
            VBox.setVgrow(shipboardGridContainer, Priority.NEVER);
            shipboardGridContainer.setPadding(new Insets(10, 0, 10, 0));
            shipboardGrid = new GridPane();
            shipboardGrid.setAlignment(Pos.CENTER);
            shipboardGrid.setPadding(shipPadding);
            tmpResourcePath = IMG_PATH + CARDBOARDS_REL_PATH + "cardboard-1b" + IMG_JPG_EXTENSION;
            Image shipboardImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
            shipboardGrid.setBackground(
                    new Background(
                            new BackgroundImage(
                                    shipboardImage,
                                    BackgroundRepeat.NO_REPEAT,
                                    BackgroundRepeat.NO_REPEAT,
                                    BackgroundPosition.CENTER,
                                    new BackgroundSize(100, 100, true, true, true, false)
                            )
                    )
            );

            int cabinId;
            synchronized (controller.getShipboardLock()) {
                ShipBoard ship = controller.getShipBoard();
                Optional<Component>[][] matrix = ship.getComponentsMatrix();
                cabinId = 157;
                if (matrix[2][3].isPresent())
                    cabinId = matrix[2][3].get().getId();
            }
            tmpResourcePath = IMG_PATH + TILES_REL_PATH + cabinId + IMG_JPG_EXTENSION;
            /*
             * design of cells: every enabled cell (both ship slots and book slots) has a stackPane which contains an
             * imageView where the component image will be placed
             */
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    //checks if the current cell is in the shipboard
                    if (!((i == 0 && j == 0) || (i == 0 && j == 1) || (i == 1 && j == 0) ||
                            (i == 0 && j == 3) || (i == 1 && j == 6) || (i == 4 && j == 3))) {
                        StackPane sp = new StackPane();
                        final int r = i, c = j;
                        // load the starter cabin image
                        ImageView imgView = new ImageView();
                        if (i == 2 && j == 3) {
                            imgView.setImage(new Image(getClass().getResource(tmpResourcePath).toExternalForm()));
                        }
                        // sets the side length of the stack pane and bind the image to be 90% of the stack pane
                        sp.setMaxHeight(tilesSideLength);
                        sp.setMaxWidth(tilesSideLength);
                        sp.setMinHeight(tilesSideLength);
                        sp.setMinWidth(tilesSideLength);
                        imgView.fitWidthProperty().bind(sp.widthProperty().multiply(0.9));
                        imgView.fitHeightProperty().bind(sp.heightProperty().multiply(0.9));
                        imgView.setPreserveRatio(true);
                        sp.setOnMouseClicked(event -> spComponentAction(r, c));
                        sp.getChildren().add(imgView);
                        sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        shipboardGrid.add(sp, j, i);
                        // adds the imageViee and the stack pane to the map to trace them with their grid position
                        cordToStackPanes.put(new Cordinate(i, j), sp);
                        cordToImageViews.put(new Cordinate(i, j), imgView);
                    }
                }
            }

            /* creating 5 rows and 7 columns with specified size */
            setGridConstraints(shipboardGrid, tilesSideLength);

            shipboardGridContainer.getChildren().addAll(shipboardGrid);
            shipboardBorderPane.setCenter(shipboardGridContainer);
            shipViewBorderPane.setCenter(shipboardBorderPane);

            /* Button to put back in hand the component (after trying to place or book) */
            backButton = new Button("Put back in hand");
            backButton.setOnAction(event -> controller.setState(GameState.COMPONENT_MENU));
            backButton.setVisible(false);
            backButton.setWrapText(true);
            backButton.setTextAlignment(TextAlignment.CENTER);
            VBox box = new VBox();
            box.setPrefWidth(130);
            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(0, 10, 0, 0));
            box.getChildren().addAll(backButton);
            shipboardBorderPane.setRight(box);

            /* Label to give hint for placement */
            HBox topShipBox = new HBox();
            topShipBox.setAlignment(Pos.CENTER);
            topShipBox.setPadding(new Insets(10, 10, 10, 10));
            topShipBox.setPrefHeight(50);
            hintLabel = new Label();
            hintLabel.setVisible(false);
            topShipBox.getChildren().add(hintLabel);
            shipboardBorderPane.setTop(topShipBox);

            shipViewBorderPane.setDisable(false);

            root.getChildren().add(shipViewBorderPane);

            uncoveredComponentModalStage = new Stage();

            firstBuilding = false;
        } else {
            // else it is necessary only to reinitialize some view components
            String tmpResourcePath = IMG_PATH + TILES_REL_PATH + "157" + IMG_JPG_EXTENSION;
            Image inHandImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
            inHandImageView.setImage(inHandImage);
            inHandImageView.setRotate(0);

            shipViewBorderPane.setDisable(false);
            shipTilesDeckBox.setDisable(false);
            shipAdvDeckBox.setDisable(false);
            inHandBox.setDisable(true);
            isComponentBoxClickable = false;
            backButton.setVisible(false);
            hintLabel.setVisible(false);

            // fill the grid of the shipboard with the components from the model
            Optional<Integer>[][] idMatrix;
            Optional<Integer>[][] rotationsMatrix;
            List<Optional<Integer>> bookedComponents;
            synchronized (controller.getShipboardLock()) {
                idMatrix = controller.getShipBoard().getComponentIdsMatrix();
                rotationsMatrix = controller.getShipBoard().getComponentRotationsMatrix();
                bookedComponents = new ArrayList<>(controller.getShipBoard().getBookedComponents());
            }
            fillShipboard(idMatrix, rotationsMatrix, bookedComponents, cordToImageViews);
        }
    }

    /**
     * sets the image of the drawn/chosen/unbooked components and enables the buttons for the placing
     * it also fill the shipboard (useful only if it is called after a swap of booked components)
     */
    private void newComponentView() {
        shipViewBorderPane.setDisable(true);
        Component inHand = controller.getInHandComponentObject();
        int idComponent = inHand.getId();
        String tmpResourcePath = IMG_PATH + TILES_REL_PATH + (idComponent != 1 ? idComponent : "") + IMG_JPG_EXTENSION;
        Image image = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
        shipViewBorderPane.setDisable(false);
        shipTilesDeckBox.setDisable(true);
        shipAdvDeckBox.setDisable(true);
        inHandBox.setDisable(false);
        inHandImageView.setImage(image);
        backButton.setVisible(false);
        hintLabel.setVisible(false);
        Optional<Integer>[][] idMatrix;
        Optional<Integer>[][] rotationsMatrix;
        List<Optional<Integer>> bookedComponents;
        synchronized (controller.getShipboardLock()) {
            idMatrix = controller.getShipBoard().getComponentIdsMatrix();
            rotationsMatrix = controller.getShipBoard().getComponentRotationsMatrix();
            bookedComponents = new ArrayList<>(controller.getShipBoard().getBookedComponents());
        }
        fillShipboard(idMatrix, rotationsMatrix, bookedComponents, cordToImageViews);
    }

    /**
     * displays (in a modal window) a gallery of the available uncovered components
     * adds the callback method chooseComponentFromUncovered to every component, binding its id
     */
    private void showUncoveredComponent() {
        FlyBoard fly;
        List<Integer> componentIdList;
        synchronized (controller.getFlyboardLock()) {
            fly = controller.getFlyBoard();
            componentIdList = new ArrayList<>(fly.getUncoveredComponents());
        }

        uncoveredComponentsTilePane = new TilePane();
        if (!componentIdList.isEmpty()) {
            for (Integer componentId : componentIdList) {
                String tmpResourcePath = IMG_PATH + TILES_REL_PATH + (componentId != 1 ? componentId : "") + IMG_JPG_EXTENSION;
                Image tileImage = new Image(this.getClass().getResource(tmpResourcePath).toExternalForm());
                ImageView tileImageView = new ImageView(tileImage);
                tileImageView.setFitHeight(100);
                tileImageView.setPreserveRatio(true);
                tileImageView.setOnMouseClicked(event -> chooseComponentFromUncovered(componentId));
                uncoveredComponentsTilePane.getChildren().add(tileImageView);
            }
        } else {
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

    /**
     * simple modal window to notify that the component chosen is no more available
     */
    private void unableUncoveredView() {
        StackPane sp = new StackPane();
        sp.getChildren().add(new Label("This component is no more available"));
        sp.setAlignment(Pos.CENTER);
        sp.setPadding(new Insets(10, 20, 10, 20));
        uncoveredComponentModalStage = new Stage();
        uncoveredComponentModalStage.setScene(new Scene(sp));
        uncoveredComponentModalStage.show();
        uncoveredComponentModalStage.setResizable(false);
        uncoveredComponentModalStage.setOnCloseRequest(event -> controller.setState(GameState.BUILDING_SHIP));
    }

    /**
     * called when the user click the "place" button, it shows a hint label and enable the click on the components slots
     */
    private void placeComponentView() {
        backButton.setVisible(true);
        inHandBox.setDisable(true);
        isComponentBoxClickable = true;
        hintLabel.setText("Click on the empty cell where you want to insert the component");
        hintLabel.setVisible(true);
    }

    /**
     * called when the user try to book a component while having two booked components, the view asks which component
     * to swap with and enable the click on the booked components
     */
    private void switchBookedComponentsView() {
        backButton.setVisible(true);
        inHandBox.setDisable(true);
        isComponentBoxClickable = true;
        hintLabel.setText("Click on the booked component you want to replace");
        hintLabel.setVisible(true);
    }

    /**
     * view shown when the user tries to inspect an adv card deck.
     * if available the deck is shown in a modal window, else it is written a message to wait
     * !!attention!! client-side the deck management is not already implemented (the visualized decks are different for every client)
     * @param isAvailable: indicates if show the deck or the error message
     */
    private void inspectDeckView(boolean isAvailable) {
        Stage advCardDeckModalStage = new Stage();
        advCardDeckModalStage.initModality(Modality.APPLICATION_MODAL);
        if (isAvailable) {
            List<Integer> advList;
            VBox vBox = new VBox();
            HBox cardBox = new HBox(20);
            cardBox.setPadding(new Insets(10, 20, 10, 20));
            cardBox.setAlignment(Pos.CENTER);
            synchronized (controller.getFlyboardLock()) {
                advList = new ArrayList<>(controller.getFlyBoard().getAdvDeckByIndex(controller.getInHandDeck()));
            }
            for (Integer advCardId : advList) {
                String tmpResourcePath = IMG_PATH + ADC_CARD_REL_PATH + advCardId + IMG_JPG_EXTENSION;
                ImageView imgView = new ImageView(new Image(this.getClass().getResource(tmpResourcePath).toExternalForm()));
                imgView.setFitHeight(250);
                imgView.setPreserveRatio(true);
                cardBox.getChildren().add(imgView);
            }
            HBox btnBox = new HBox();
            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(event -> {
                advCardDeckModalStage.close();
                controller.freeDeck();
            });
            btnBox.setAlignment(Pos.BASELINE_RIGHT);
            btnBox.getChildren().addAll(closeBtn);
            vBox.getChildren().addAll(cardBox, btnBox);
            advCardDeckModalStage.setScene(new Scene(vBox));
            advCardDeckModalStage.setOnCloseRequest(event -> controller.freeDeck());
        } else {
            VBox box = new VBox();
            box.setPadding(new Insets(10, 20, 10, 20));
            box.setAlignment(Pos.CENTER);
            box.getChildren().add(new Label("This deck isn't available at the moment"));
            advCardDeckModalStage.setScene(new Scene(box));
        }
        advCardDeckModalStage.show();
    }

    /**
     * displays a modal window that shows the requested player's shipboard, the view is rendered the same way as the user's one
     * @param nickname: nickname of the chosen player
     */
    private void lookAtOtherShipboardView(String nickname) {
        Map<Cordinate, ImageView> otherPlayerShipCordToImg = new HashMap<>();
        otherPlayerShipStage = new Stage();
        otherPlayerShipStage.initModality(Modality.APPLICATION_MODAL);
        otherPlayerShipStage.setTitle(nickname + "'s shipboard");
        VBox otherPlayerShipContainer = new VBox();
        otherPlayerShipStage.setScene(new Scene(otherPlayerShipContainer, 1200, 800));
        otherPlayerShipStage.show();
        double stageHeight = otherPlayerShipStage.getHeight(), stageWidth = otherPlayerShipStage.getWidth();
        otherPlayerShipContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(otherPlayerShipContainer, Priority.ALWAYS);
        otherPlayerShipContainer.setPadding(new Insets(10, 0, 10, 0));
        GridPane otherPlayerShipGrid = new GridPane();
        otherPlayerShipGrid.setAlignment(Pos.CENTER);
        otherPlayerShipGrid.setPadding(shipPadding);
        String tmpResourcePath = IMG_PATH + CARDBOARDS_REL_PATH + "cardboard-1b" + IMG_JPG_EXTENSION;
        Image shipboardImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
        otherPlayerShipGrid.setBackground(
                new Background(
                        new BackgroundImage(
                                shipboardImage,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER,
                                new BackgroundSize(100, 100, true, true, true, false)
                        )
                )
        );
        tilesSideLength = Math.min(stageHeight / 6, stageWidth / 10);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (!(
                        (i == 0 && j == 0) ||
                                (i == 0 && j == 1) ||
                                (i == 1 && j == 0) ||
                                (i == 0 && j == 3) ||
                                (i == 1 && j == 6) ||
                                (i == 4 && j == 3)
                )) {
                    StackPane sp = new StackPane();
                    ImageView imgView = new ImageView();
                    sp.setMaxHeight(tilesSideLength);
                    sp.setMaxWidth(tilesSideLength);
                    sp.setMinHeight(tilesSideLength);
                    sp.setMinWidth(tilesSideLength);
                    imgView.fitWidthProperty().bind(sp.widthProperty().multiply(0.9));
                    imgView.fitHeightProperty().bind(sp.heightProperty().multiply(0.9));
                    imgView.setPreserveRatio(true);
                    sp.getChildren().add(imgView);
                    sp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    otherPlayerShipGrid.add(sp, j, i);
                    otherPlayerShipCordToImg.put(new Cordinate(i, j), imgView);
                }
            }
        }
        setGridConstraints(otherPlayerShipGrid, tilesSideLength);
        otherPlayerShipContainer.getChildren().add(otherPlayerShipGrid);
        Optional<Integer>[][] idMatrix;
        Optional<Integer>[][] rotationsMatrix;
        List<Optional<Integer>> bookedComponents;
        synchronized (controller.getFlyboardLock()) {
            idMatrix = controller.getFlyBoard().getPlayerByUsername(nickname).getShipBoard().getComponentIdsMatrix();
            rotationsMatrix = controller.getFlyBoard().getPlayerByUsername(nickname).getShipBoard().getComponentRotationsMatrix();
            bookedComponents = new ArrayList<>(controller.getFlyBoard().getPlayerByUsername(nickname).getShipBoard().getBookedComponents());
        }
        fillShipboard(idMatrix, rotationsMatrix, bookedComponents, otherPlayerShipCordToImg);
    }

    private void choosePositionView(boolean isError){
        choosePositionStage = new Stage();
        choosePositionStage.initModality(Modality.APPLICATION_MODAL);

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 10, 10, 10));

        if(isError){
            Label errorLabel = new Label("Previously chosen position is no more available!");
            box.getChildren().add(errorLabel);
        }

        String tmpResourcePath = IMG_PATH + CARDBOARDS_REL_PATH + "cardboard-5" + IMG_PNG_EXTENSION;
        Logger.debug(tmpResourcePath);
        Image circuitImage = new Image(this.getClass().getResource(tmpResourcePath).toExternalForm());
        ImageView circuitImageView = new ImageView(circuitImage);

        Label choosePlaceLabel = new Label("Choose the position you want to occupy");

        HBox choosePlaceBox = new HBox();
        choosePlaceBox.setAlignment(Pos.CENTER);
        choosePlaceBox.setPadding(new Insets(10, 10, 10, 10));
        List<Integer> avlPlaces = controller.getAvailablePlacesOnCircuit();
        for(Integer i : avlPlaces){
            String text = "Position n. ";
            switch(i){
                case 0 -> text += 4;
                case 1 -> text += 3;
                case 3 -> text += 2;
                case 6 -> text += 1;
            }
            Button btn = new Button(text);
            btn.setOnAction(e -> choosePlace(i));
            choosePlaceBox.getChildren().add(btn);
        }

        box.getChildren().addAll(circuitImageView, choosePlaceLabel, choosePlaceBox);
        choosePositionStage.setScene(new Scene(box));
        choosePositionStage.show();
    }

    private void waitingForAdventureStartView(){
        root.getChildren().clear();
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Waiting for adventure start"));
    }

    /*
     * Callback methods section
     */

    /**
     * forwards the request of connection to the controller
     * @param isRmi: true: rmi connetcion, false: socket connection
     */
    private void connectToServer(boolean isRmi) {
        controller.connectToServer(isRmi);
    }

    /**
     * verify if the input is ok, then forwards the nicnkame input to the controller
     * @param nickname: nickname chosen by the user
     */
    private void handleNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            nicknameBox.getChildren().remove(errorNicknameLabel);
            errorNicknameLabel = new Label("Nickname cannot be empty");
            nicknameBox.getChildren().addFirst(errorNicknameLabel);
        } else {
            controller.setState(GameState.WAITING);
            controller.handleNickname(nickname);
        }
    }

    /**
     * forwards the input of the game settings
     * @param nPlayers: numbero of plater chosen
     * @param isNormal: true: normal game mode, false: easy game mode
     */
    private void handleGameInfo(int nPlayers, boolean isNormal) {
        GameInfo info = new GameInfo(-1, isNormal ? GameMode.NORMAL : GameMode.EASY, nPlayers);
        controller.handleGameInfo(info);
    }

    /**
     * forward to the controller the request to draw a covered component
     */
    private void drawCoveredComponent() {
        controller.handleBuildingShip(1);
    }

    /**
     * forward to the controller the request to choose an uncovered component
     * @param componentId: id of the chosen component
     */
    private void chooseComponentFromUncovered(int componentId) {
        controller.drawUncovered(componentId);
        uncoveredComponentModalStage.close();
        uncoveredComponentsTilePane.setVisible(false);
    }

    /// if the click on the component is enabled, execute different instructions based on the state.
    /// - ADD_COMPONENT: forwards to the controller the request to add a component in the clicked position
    /// - SWITCH_BOOKED: forwards to the controller the request to swap a component with a booked one
    /// @param i: row index of the clicked cell
    /// @param j: column of the clicked cell
    private void spComponentAction(int i, int j) {
        if (isComponentBoxClickable) {
            GameState state = controller.getState();
            switch (state) {
                case ADD_COMPONENT -> {
                    if (!((i == 0) && ((j == 5) || (j == 6))))
                        controller.addComponent(new Cordinate(i, j), controller.getTmpRotation());
                }
                case SWITCH_BOOKED -> {
                    if ((i == 0) && ((j == 5) || (j == 6)))
                        controller.bookComponent(j == 5 ? 0 : 1);
                }
                default -> {
                }
            }
        }
    }

    private void endBuild(){
        controller.endBuild();
    }

    private void choosePlace(int place){
        choosePositionStage.close();
        controller.choosePlace(place);
    }

    /*
     * utility methods
     */

    /**
     * place the images of the components in the shipboard grid based on the parameters given
     * @param idMatrix: matrix of component ids
     * @param rotationsMatrix: matrix of rotations of the components
     * @param bookedComponents: booked components to show
     * @param map: map that link coordinates with the related imageView
     */
    private void fillShipboard(Optional<Integer>[][] idMatrix, Optional<Integer>[][] rotationsMatrix, List<Optional<Integer>> bookedComponents, Map<Cordinate, ImageView> map) {

        for (Cordinate cord : map.keySet()) {
            if (!cord.equals(new Cordinate(0, 5)) && !cord.equals(new Cordinate(0, 6))) {
                if (idMatrix[cord.getRow()][cord.getColumn()].isPresent() && rotationsMatrix[cord.getRow()][cord.getColumn()].isPresent()) {
                    String tmpResourcePath = IMG_PATH + TILES_REL_PATH + idMatrix[cord.getRow()][cord.getColumn()].get() + IMG_JPG_EXTENSION;
                    map.get(cord).setImage(new Image(getClass().getResource(tmpResourcePath).toExternalForm()));
                    map.get(cord).setRotate(rotationsMatrix[cord.getRow()][cord.getColumn()].get() * 90);
                }
            } else {
                if (cord.equals(new Cordinate(0, 5)) && bookedComponents.get(0).isPresent()) {
                    String tmpResourcePath = IMG_PATH + TILES_REL_PATH + bookedComponents.get(0).get() + IMG_JPG_EXTENSION;
                    Image img = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
                    map.get(cord).setImage(img);
                } else if (cord.equals(new Cordinate(0, 6)) && bookedComponents.get(1).isPresent()) {
                    String tmpResourcePath = IMG_PATH + TILES_REL_PATH + bookedComponents.get(1).get() + IMG_JPG_EXTENSION;
                    Image img = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
                    map.get(cord).setImage(img);
                }
            }
        }
    }

    /**
     * set the passed tilesHeight to the rows and columns of the grid
     * @param grid: shipboard grid to apply the constraints to
     * @param tilesHeight: side length of the cells
     */
    private void setGridConstraints(GridPane grid, double tilesHeight) {
        for (int i = 0; i < 5; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(tilesHeight);
            grid.getRowConstraints().add(rowConstraints);
        }
        for (int j = 0; j < 7; j++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(tilesHeight);
            grid.getColumnConstraints().add(columnConstraints);
        }
    }
}
