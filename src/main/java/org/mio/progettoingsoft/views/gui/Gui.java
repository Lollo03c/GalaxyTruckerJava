package org.mio.progettoingsoft.views.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.mio.progettoingsoft.*;
import org.mio.progettoingsoft.advCards.sealed.CardState;
import org.mio.progettoingsoft.advCards.sealed.SldAbandonedShip;
import org.mio.progettoingsoft.advCards.sealed.SldAdvCard;
import org.mio.progettoingsoft.advCards.sealed.SldSlavers;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.HousingColor;
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
    private final BlockingQueue<Pair<Integer, Integer>> circuitMovesQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<CardState> cardStatesQueue = new LinkedBlockingQueue<>();

    private static final String IMG_PATH = "/images/";
    private static final String TILES_REL_PATH = "tiles/GT-new_tiles_16_for web";
    private static final String CARDBOARDS_REL_PATH = "cardboards/";
    private static final String ADV_CARD_REL_PATH = "advCards/GT-cards_";
    private static final String OTHERS_REL_PATH = "others/";
    private static final String IMG_JPG_EXTENSION = ".jpg";
    private static final String IMG_PNG_EXTENSION = ".png";

    /*
     * utility fields used for the scene building
     */

    // true -> first time in the BUILDING_SHIP view
    private boolean firstBuilding = true;
    // true -> the built-in button is invisible (the ship has already been loaded)
    private boolean willTestBuildDisappear = false;
    //true -> first time in adventure card view
    private boolean firstAdventureStart = true;
    private boolean acceptEffect;
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
    private String depotAction = "MOVE";
    private int depotId;
    private GoodType goodToBePlaced = null;
    // list of coordinates for placement of rocket on the circuit
    private final List<Point2D> coordList = new ArrayList<>(List.of(
            new Point2D(0.2448, 0.2375),
            new Point2D(0.3138, 0.1861),
            new Point2D(0.3886, 0.1569),
            new Point2D(0.4627, 0.1472),
            new Point2D(0.5359, 0.1458),
            new Point2D(0.6116, 0.1625),
            new Point2D(0.6856, 0.1944),
            new Point2D(0.7546, 0.2417),
            new Point2D(0.8210, 0.3181),
            new Point2D(0.8665, 0.4333),
            new Point2D(0.8597, 0.5806),
            new Point2D(0.8101, 0.6861),
            new Point2D(0.7453, 0.7583),
            new Point2D(0.6730, 0.8000),
            new Point2D(0.5998, 0.8264),
            new Point2D(0.5241, 0.8417),
            new Point2D(0.4484, 0.8417),
            new Point2D(0.3718, 0.8236),
            new Point2D(0.2995, 0.7931),
            new Point2D(0.2280, 0.7444),
            new Point2D(0.1649, 0.6694),
            new Point2D(0.1195, 0.5514),
            new Point2D(0.1279, 0.4083),
            new Point2D(0.1775, 0.3056)
    ));
    // map from Housing color to actual color
    private final Map<HousingColor, Color> housingColors = new HashMap<>(Map.of(
            HousingColor.BLUE, Color.BLUE,
            HousingColor.RED, Color.RED,
            HousingColor.GREEN, Color.GREEN,
            HousingColor.YELLOW, Color.YELLOW
    ));

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
    private VBox topBuildingShipBox;
    private VBox inHandBox;
    private ImageView inHandImageView;
    private Button loadShipBtn;
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
    private Label errorLabel;
    // modal stage for other player ship displaying
    private Stage modalShipStage;
    private Map<Cordinate, ImageView> modalShipCordToImg;
    private Map<Cordinate, StackPane> modalShipCordToStackPane;
    private String modalErrorLabelMessage;
    private VBox modalShipContainer;
    private Label modalHintTopLabel;
    private HBox goodsBox;
    // modal stage for position choosing
    private Stage choosePositionStage;
    // view components for the circuit and card activation
    private BorderPane adventureBorderPane;
    // circuit view components
    private Pane circlesLayer;
    private ImageView circuitImageView;
    // card management components
    private Button drawCardButton;
    private Label waitingForLeaderLabel;
    private ImageView cardImageView;
    private Label creditsLabel;

    public Gui() {
        controller = ClientController.getInstance();
        controller.addPropertyChangeListener(this);
    }

    /**
     * method called when a property changes, at the moment is used only in response to game state changes and circuit changes
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            // the game state changes: usually the view changes, sometimes has some small modifications
            case "gameState" -> statesQueue.add((GameState) evt.getNewValue());
            // the circuit changes (only in the right game states): the circuit view is updated
            case "circuit" -> {
                if (controller.getState() == GameState.DRAW_CARD || controller.getState() == GameState.CARD_EFFECT || controller.getState() == GameState.NEW_CARD || controller.getState() == GameState.YOU_CAN_DRAW_CARD) {
                    circuitMovesQueue.add(new Pair<>((int) evt.getOldValue(), (int) evt.getNewValue()));
                }
            }
            // the card state changes (only in CARD_EFFECT state): enable the user interaction based on the different state
            case "cardState" -> cardStatesQueue.add((CardState) evt.getNewValue());
            // the player's credits changes, it updates the label of credits
            case "credits" -> {
                Platform.runLater(() -> {
                    if (creditsLabel == null) {
                        Logger.debug("creditsLabel is null");
                        creditsLabel = new Label("Your credits: " + evt.getNewValue());
                    } else {
                        creditsLabel.setText("Your credits: " + evt.getNewValue());
                    }
                });
            }
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
        tilesSideLength = Math.min(screenHeight / 9, screenWidth / 16);
        creditsLabel = new Label();
        this.updateGui(GameState.START);
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    GameState state = statesQueue.take();
                    Platform.runLater(() -> updateGui(state));
                    if (statesQueue.isEmpty()) {
                        synchronized (statesQueue) {
                            statesQueue.notifyAll();
                        }
                    }
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
            case START -> firstView();
            case WAITING -> loadingView();
            case NICKNAME -> nicknameRequestView(false);
            case ERROR_NICKNAME -> nicknameRequestView(true);
            case GAME_MODE -> askForSettingsView();
            case WAITING_PLAYERS -> waitingForPlayersView();
            case BUILDING_SHIP -> buildingShipView();
            case COMPONENT_MENU -> newComponentView();
            case UNABLE_UNCOVERED_COMPONENT -> unableUncoveredView();
            case ADD_COMPONENT -> placeComponentView(false);
            case ERROR_PLACEMENT -> placeComponentView(true);
            case SWITCH_BOOKED -> switchBookedComponentsView();
            case VIEW_DECK -> inspectDeckView(true);
            case UNABLE_DECK -> inspectDeckView(false);
            case CHOOSE_POSITION -> choosePositionView(false);
            case WRONG_POSITION -> choosePositionView(true);
            case END_BUILDING -> waitingForAdventureStartView();
            case DRAW_CARD -> advStartedView(false);
            case YOU_CAN_DRAW_CARD -> advStartedView(true);
            case NEW_CARD -> loadNewCard();
            case CARD_EFFECT -> {
            }
            case IDLE -> {
            }
            case FINISH_HOURGLASS -> {
            }
            case FINISH_LAST_HOURGLASS -> {
            }

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
        nicknameField.setOnAction(event -> handleNickname(nicknameField.getText()));
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
     * mainly used for debug or not already implemented functionalities
     *
     * @param state: the game state in which the server is
     */
    private void genericErrorView(GameState state) {
        root.getChildren().clear();
        Label errorLabel = new Label("Generic error! State: " + state);
        root.getChildren().add(errorLabel);
    }

    /**
     * mainly used for debug or not already implemented funcionalities
     *
     * @param state: the game card state in which the played card is
     */
    private void genericErrorView(CardState state) {
        root.getChildren().clear();
        Label errorLabel = new Label("Generic error! Card state: " + state);
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
                    butt.setOnAction(event -> modalShipboardView(player.getNickname()));
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

            /* TESTING SECTION */
            loadShipBtn = new Button("Load built-in shipboard");
            loadShipBtn.setOnAction(evt -> {
                controller.builtDefault();
                willTestBuildDisappear = true;
            });
            shipRightColumn.getChildren().addAll(loadShipBtn);
            /* END TESTING SECTION */

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
            topBuildingShipBox = new VBox();
            topBuildingShipBox.setAlignment(Pos.CENTER);
            topBuildingShipBox.setPadding(new Insets(10, 10, 10, 10));
            topBuildingShipBox.setPrefHeight(50);
            hintLabel = new Label();
            errorLabel = new Label();
            hintLabel.setVisible(false);
            topBuildingShipBox.getChildren().addAll(hintLabel, errorLabel);
            shipboardBorderPane.setTop(topBuildingShipBox);

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
            backButton.setVisible(false);
            hintLabel.setVisible(false);
            loadShipBtn.setDisable(false);
            loadShipBtn.setVisible(!willTestBuildDisappear);

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
        isComponentBoxClickable = true;
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
        loadShipBtn.setDisable(false);
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
     *
     * @param isError: has to be set true if the user tries to place a component in an occupied slot
     */
    private void placeComponentView(boolean isError) {
        backButton.setVisible(true);
        inHandBox.setDisable(true);
        loadShipBtn.setDisable(false);
        isComponentBoxClickable = true;
        hintLabel.setText("Click on the empty cell where you want to insert the component");
        hintLabel.setVisible(true);
        errorLabel.setVisible(false);
        // if previously the user tried to place the component in an occupied slot, the view notifies it and re-show
        // the "new component" view that allow the user to place the component
        if (isError) {
            errorLabel.setVisible(true);
            errorLabel.setText("You cannot place a component in an occupied position!");
            newComponentView();
        }
    }

    /**
     * called when the user try to book a component while having two booked components, the view asks which component
     * to swap with and enable the click on the booked components
     */
    private void switchBookedComponentsView() {
        backButton.setVisible(true);
        inHandBox.setDisable(true);
        loadShipBtn.setDisable(false);
        isComponentBoxClickable = true;
        hintLabel.setText("Click on the booked component you want to replace");
        hintLabel.setVisible(true);
    }

    /**
     * view shown when the user tries to inspect an adv card deck.
     * if available the deck is shown in a modal window, else it is written a message to wait
     * !!attention!! client-side the deck management is not already implemented (the visualized decks are different for every client)
     *
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
                String tmpResourcePath = IMG_PATH + ADV_CARD_REL_PATH + advCardId + IMG_JPG_EXTENSION;
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
     *
     * @param nickname: nickname of the chosen player
     */
    private void modalShipboardView(String nickname) {
        modalShipCordToImg = new HashMap<>();
        modalShipCordToStackPane = new HashMap<>();
        modalShipStage = new Stage();
        modalShipStage.initModality(Modality.APPLICATION_MODAL);
        modalShipStage.setTitle(nickname + "'s shipboard");
        modalShipContainer = new VBox(15);
        modalShipStage.setScene(new Scene(modalShipContainer));
        modalShipStage.setMaximized(true);
        modalShipStage.show();
        double stageHeight = modalShipStage.getHeight(), stageWidth = modalShipStage.getWidth();
        modalShipContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(modalShipContainer, Priority.ALWAYS);
        modalShipContainer.setPadding(new Insets(10, 0, 10, 0));
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
                    modalShipCordToImg.put(new Cordinate(i, j), imgView);
                    modalShipCordToStackPane.put(new Cordinate(i, j), sp);
                }
            }
        }
        setGridConstraints(otherPlayerShipGrid, tilesSideLength);
        modalShipContainer.getChildren().add(otherPlayerShipGrid);
        Optional<Integer>[][] idMatrix;
        Optional<Integer>[][] rotationsMatrix;
        List<Optional<Integer>> bookedComponents;
        synchronized (controller.getFlyboardLock()) {
            idMatrix = controller.getFlyBoard().getPlayerByUsername(nickname).getShipBoard().getComponentIdsMatrix();
            rotationsMatrix = controller.getFlyBoard().getPlayerByUsername(nickname).getShipBoard().getComponentRotationsMatrix();
            bookedComponents = new ArrayList<>(controller.getFlyBoard().getPlayerByUsername(nickname).getShipBoard().getBookedComponents());
        }
        fillShipboard(idMatrix, rotationsMatrix, bookedComponents, modalShipCordToImg);
        synchronized (controller.getFlyboardLock()) {
            ShipBoard ship = controller.getFlyBoard().getPlayerByUsername(nickname).getShipBoard();
            loadComponentSpecificObjects(ship, modalShipCordToStackPane);
        }
    }

    /**
     * this method creates and shows a modal stage to ask the user in which position he wants to be placed on the circuit,
     * it is shown when the user decides to stop building or when the time ends
     *
     * @param isError: true if the previously chosen position was already occupied
     */
    private void choosePositionView(boolean isError) {
        choosePositionStage = new Stage();
        choosePositionStage.initModality(Modality.APPLICATION_MODAL);

        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 10, 10, 10));

        if (isError) {
            Label errorLabel = new Label("Previously chosen position is no more available!");
            box.getChildren().add(errorLabel);
        }

        String tmpResourcePath = IMG_PATH + CARDBOARDS_REL_PATH + "cardboard-5" + IMG_PNG_EXTENSION;
        Image circuitImage = new Image(this.getClass().getResource(tmpResourcePath).toExternalForm());
        ImageView circuitImageView = new ImageView(circuitImage);
        circuitImageView.setPreserveRatio(true);
        circuitImageView.setFitWidth(tilesSideLength * 8);

        Label choosePlaceLabel = new Label("Choose the position you want to occupy");

        HBox choosePlaceBox = new HBox();
        choosePlaceBox.setAlignment(Pos.CENTER);
        choosePlaceBox.setPadding(new Insets(10, 10, 10, 10));
        List<Integer> avlPlaces = controller.getAvailablePlacesOnCircuit();
        for (Integer i : avlPlaces) {
            String text = "Position n. ";
            switch (i) {
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

    /**
     * waiting view displayed when the player has finished to build his ship but other players are still building
     */
    private void waitingForAdventureStartView() {
        root.getChildren().clear();
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Waiting for adventure start"));
        root.getChildren().add(box);
    }

    /**
     * view that is shown when it's time to start the actual game, the view displays the circuit (with the players) and
     * some buttons: top buttons allow the user to look at other player's ship, bottom button allows the user to look at his ship,
     * the right column shows some details about the match and the playing card and allows the user to leave the flight
     */
    private void advStartedView(boolean isLeader) {
        if (firstAdventureStart) {
            root.getChildren().clear();
            adventureBorderPane = new BorderPane();
            adventureBorderPane.setPadding(new Insets(10, 10, 10, 10));

            /* top button container */
            HBox topBox = new HBox(50);
            topBox.setAlignment(Pos.CENTER);
            List<Player> playerList;
            synchronized (controller.getFlyboardLock()) {
                playerList = new ArrayList<>(controller.getFlyBoard().getPlayers());
            }
            for (Player player : playerList) {
                if (!player.getNickname().equals(controller.getNickname())) {
                    Button butt = new Button("Show " + player.getNickname() + "'s shipboard");
                    butt.setTextAlignment(TextAlignment.CENTER);
                    butt.setWrapText(true);
                    butt.setOnAction(event -> modalShipboardView(player.getNickname()));
                    topBox.getChildren().add(butt);
                }
            }
            adventureBorderPane.setTop(topBox);

            /* bottom button container */
            HBox bottomBox = new HBox(50);
            bottomBox.setAlignment(Pos.CENTER);
            Button showShipBtn = new Button("Show your shipboard");
            showShipBtn.setOnAction(evt -> modalShipboardView(controller.getNickname()));
            bottomBox.getChildren().addAll(showShipBtn);
            adventureBorderPane.setBottom(bottomBox);

            /* right column */
            VBox rightColumn = new VBox(20);
            rightColumn.setAlignment(Pos.CENTER);
            rightColumn.setPadding(new Insets(10, 10, 10, 10));

            VBox cardContainer = new VBox(10);
            cardContainer.setAlignment(Pos.CENTER);
            cardContainer.setPadding(new Insets(10, 10, 10, 10));
            String tmpResourcePath = IMG_PATH + ADV_CARD_REL_PATH + "back" + IMG_JPG_EXTENSION;
            Image cardBackImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
            cardImageView = new ImageView(cardBackImage);
            cardImageView.setFitHeight(tilesSideLength * 3);
            cardImageView.setPreserveRatio(true);

            drawCardButton = new Button("Draw new card");
            drawCardButton.setOnAction(evt -> {
                drawNewAdvCard();
            });
            drawCardButton.setVisible(isLeader);
            cardContainer.getChildren().addAll(cardImageView, drawCardButton);

            waitingForLeaderLabel = new Label("Wait for the leader to draw a card!");
            cardContainer.getChildren().addAll(waitingForLeaderLabel);
            waitingForLeaderLabel.setAlignment(Pos.CENTER);
            waitingForLeaderLabel.setVisible(!isLeader);

            Button leaveFlightBtn = new Button("Leave the flight");
            leaveFlightBtn.setOnAction(evt -> {
                leaveFlightConfirm();
            });
            int credits = 0;
            synchronized (controller.getFlyboardLock()) {
                credits = controller.getFlyBoard().getPlayerByUsername(controller.getNickname()).getCredits();
            }
            creditsLabel.setText("Your credits: " + credits);
            rightColumn.getChildren().addAll(cardContainer, leaveFlightBtn, creditsLabel);

            adventureBorderPane.setRight(rightColumn);

            /* circuit visualisation region */

            /*
             * the circuit is made of two components, layered in a stack pane:
             * - circuitImageView: contains the circuit image
             * - circlesLayer: contains the circles that represent the player's rocket on the circuit
             */
            VBox center = new VBox(20);
            center.setAlignment(Pos.CENTER);
            StackPane imgContainer = new StackPane();
            circlesLayer = new Pane();
            tmpResourcePath = IMG_PATH + CARDBOARDS_REL_PATH + "cardboard-5" + IMG_PNG_EXTENSION;
            Image circuitImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
            circuitImageView = new ImageView(circuitImage);
            circuitImageView.setFitHeight(tilesSideLength * 6);
            circuitImageView.setPreserveRatio(true);
            // the width of the circle layer is fixed to the width of the circuit image
            circlesLayer.setMinWidth(circuitImageView.getBoundsInParent().getWidth());
            circlesLayer.setPrefWidth(circuitImageView.getBoundsInParent().getWidth());
            circlesLayer.setMaxWidth(circuitImageView.getBoundsInParent().getWidth());
            imgContainer.getChildren().addAll(circuitImageView, circlesLayer);
            center.getChildren().add(imgContainer);

            //circuit initialization
            updateCircuit();

            adventureBorderPane.setCenter(center);

            root.getChildren().add(adventureBorderPane);
            firstAdventureStart = false;

            /*
             * thread that check the circuitMovesQueue for changes in the circuit and, if there isn't any pending game state,
             * updates the circuit view, else it waits until the state states are made
             */
            Thread circuitUpdater = new Thread(() -> {
                while (true) {
                    try {
                        Pair<Integer, Integer> p = circuitMovesQueue.take();
                        while (!statesQueue.isEmpty()) {
                            synchronized (statesQueue) {
                                statesQueue.wait();
                            }
                        }
                        Platform.runLater(this::updateCircuit);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            circuitUpdater.setDaemon(true);
            circuitUpdater.start();

            /*
             * thread that listens for changes in the card state and, if there isn't any pending game state,
             * update the view to manage the new card state
             */
            Thread cardStateManager = new Thread(() -> {
                while (true) {
                    try {
                        CardState cardState = cardStatesQueue.take();
                        while (!statesQueue.isEmpty()) {
                            synchronized (statesQueue) {
                                statesQueue.wait();
                            }
                        }
                        Platform.runLater(() -> updateCardGui(cardState));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            cardStateManager.setDaemon(true);
            cardStateManager.start();

        } else {
            waitingForLeaderLabel.setVisible(!isLeader);
            drawCardButton.setVisible(isLeader);
            String tmpResourcePath = IMG_PATH + ADV_CARD_REL_PATH + "back" + IMG_JPG_EXTENSION;
            Image cardBackImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
            cardImageView.setImage(cardBackImage);
        }
    }

    /**
     * called when a player draws a new card, it shows the card and make draw card commands invisible
     */
    private void loadNewCard() {
        String tmpResourcePath = IMG_PATH + ADV_CARD_REL_PATH + controller.getPlayedCard().getId() + IMG_JPG_EXTENSION;
        Image cardImage = new Image(getClass().getResource(tmpResourcePath).toExternalForm());
        cardImageView.setImage(cardImage);
        drawCardButton.setVisible(false);
        waitingForLeaderLabel.setVisible(false);
    }

    /*
     * card-specific view methods section
     */

    private void updateCardGui(CardState cardState) {
        switch (cardState) {
            case ENGINE_CHOICE -> engineChoiceView();
            case DRILL_CHOICE -> drillChoiceView();
            case CREW_REMOVE_CHOICE -> crewRemoveChoiceAccept();
            case ACCEPTATION_CHOICE -> {
                acceptEffectStage(() -> {
                    if (acceptEffect) {
                        controller.applyEffect();
                    } else {
                        controller.skipEffect();
                    }
                });
            }
            case GOODS_PLACEMENT -> goodsPlacementView();
            case ERROR_CHOICE -> {
                modalErrorLabelMessage = controller.getErrMessage();
                controller.resetErrMessage();
            }
            case IDLE -> {
            }
            default -> genericErrorView(cardState);
        }
    }

    private void engineChoiceView() {
        modalShipboardView(controller.getNickname());
        modalHintTopLabel = new Label();
        ShipBoard ship = controller.getShipBoard();
        Map<Cordinate, Boolean> cordToActive = new HashMap<>();
        int maxAvailable;
        synchronized (controller.getShipboardLock()) {
            maxAvailable = Integer.min(ship.getQuantBatteries(), ship.getDoubleEngine().size());
            modalHintTopLabel.setText("Click on the double engine you want to activate (or disable). Max available: " + maxAvailable + ". Now active: 0");
            for (Cordinate cord : modalShipCordToStackPane.keySet()) {
                StackPane sp = modalShipCordToStackPane.get(cord);
                if (ship.getOptComponentByCord(cord).isPresent()) {
                    if (ship.getOptComponentByCord(cord).get().getType() == ComponentType.DOUBLE_ENGINE) {
                        cordToActive.put(cord, false);
                        sp.setOnMouseClicked(evt -> {
                            if (!cordToActive.get(cord)) {
                                int sum = 0;
                                for (Boolean bool : cordToActive.values()) {
                                    if (bool)
                                        sum++;
                                }
                                if (sum < maxAvailable) {
                                    cordToActive.put(cord, true);
                                    modalHintTopLabel.setText("Click on the double engine you want to activate (or disable). Max available: " + maxAvailable + ". Now active: " + (sum + 1));
                                    sp.setStyle("-fx-border-color: green;" +
                                            "-fx-border-width: 4;");
                                }
                            } else {
                                cordToActive.put(cord, false);
                                int sum = 0;
                                for (Boolean bool : cordToActive.values()) {
                                    if (bool)
                                        sum++;
                                }
                                modalHintTopLabel.setText("Click on the double engine you want to activate (or disable). Max available: " + maxAvailable + ". Now active: " + (sum));
                                sp.setStyle("");
                            }
                        });
                    }
                }
            }
        }
        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(evt -> {
            int i = 0;
            for (Cordinate cord : cordToActive.keySet()) {
                if (cordToActive.get(cord)) {
                    i++;
                }
            }
            modalShipStage.close();
            controller.activateDoubleEngine(i);
        });
        modalShipContainer.getChildren().remove(modalHintTopLabel);
        modalShipContainer.getChildren().addFirst(modalHintTopLabel);
        modalShipContainer.getChildren().add(confirmBtn);
        if (modalErrorLabelMessage != null) {
            modalShipContainer.getChildren().addFirst(new Label(modalErrorLabelMessage));
            modalErrorLabelMessage = null;
        }
    }

    private void drillChoiceView() {
        modalShipboardView(controller.getNickname());
        modalHintTopLabel = new Label();
        ShipBoard ship = controller.getShipBoard();
        Map<Cordinate, Boolean> cordToActive = new HashMap<>();
        int maxAvailable;
        synchronized (controller.getShipboardLock()) {
            maxAvailable = Integer.min(ship.getQuantBatteries(), ship.getDoubleDrills().size());
            modalHintTopLabel.setText("Click on the double drills you want to activate (or disable). Max available: " + maxAvailable + ". Now active: 0");
            for (Cordinate cord : modalShipCordToStackPane.keySet()) {
                StackPane sp = modalShipCordToStackPane.get(cord);
                if (ship.getOptComponentByCord(cord).isPresent()) {
                    if (ship.getOptComponentByCord(cord).get().getType() == ComponentType.DOUBLE_DRILL) {
                        cordToActive.put(cord, false);
                        sp.setOnMouseClicked(evt -> {
                            if (!cordToActive.get(cord)) {
                                int sum = 0;
                                for (Boolean bool : cordToActive.values()) {
                                    if (bool)
                                        sum++;
                                }
                                if (sum < maxAvailable) {
                                    cordToActive.put(cord, true);
                                    modalHintTopLabel.setText("Click on the double drill you want to activate (or disable). Max available: " + maxAvailable + ". Now active: " + (sum + 1));
                                    sp.setStyle("-fx-border-color: green;" +
                                            "-fx-border-width: 4;");
                                }
                            } else {
                                cordToActive.put(cord, false);
                                int sum = 0;
                                for (Boolean bool : cordToActive.values()) {
                                    if (bool)
                                        sum++;
                                }
                                modalHintTopLabel.setText("Click on the double drill you want to activate (or disable). Max available: " + maxAvailable + ". Now active: " + (sum));
                                sp.setStyle("");
                            }
                        });
                    }
                }
            }
        }
        final SldAdvCard card = controller.getPlayedCard();
        Button confirmBtn = new Button("Confirm");
        List<Cordinate> drillsToActivate = new ArrayList<>();
        confirmBtn.setOnAction(evt -> {
            for (Cordinate cord : cordToActive.keySet()) {
                if (cordToActive.get(cord)) {
                    drillsToActivate.add(cord);
                }
            }
            modalShipStage.close();
            switch (card) {
                case SldSlavers slavers -> {
                    double playerStrenght;
                    synchronized (controller.getShipboardLock()) {
                        playerStrenght = controller.getShipBoard().getBaseFirePower();
                        for (Cordinate cordinate : drillsToActivate) {
                            if (controller.getShipBoard().getOptComponentByCord(cordinate).isPresent()) {
                                playerStrenght += controller.getShipBoard().getOptComponentByCord(cordinate).get().getFirePower(true);
                            }
                        }
                    }
                    acceptEffect = false;
                    if (playerStrenght > slavers.getStrength()) {
                        acceptEffectStage(() -> {
                            controller.activateSlaver(drillsToActivate, acceptEffect);
                        });
                    } else {
                        controller.activateSlaver(drillsToActivate, acceptEffect);
                    }

                }
                default -> controller.activateDoubleDrills(drillsToActivate);
            }
        });
        modalShipContainer.getChildren().remove(modalHintTopLabel);
        modalShipContainer.getChildren().addFirst(modalHintTopLabel);
        modalShipContainer.getChildren().add(confirmBtn);
        if (modalErrorLabelMessage != null) {
            modalShipContainer.getChildren().addFirst(new Label(modalErrorLabelMessage));
            modalErrorLabelMessage = null;
        }
    }

    private void crewRemoveChoiceAccept() {
        SldAdvCard card = controller.getPlayedCard();

        switch (card) {
            case SldAbandonedShip s -> {
                acceptEffectStage(this::crewRemoveChoiceView);
            }
            default -> {
                acceptEffect = true;
                crewRemoveChoiceView();
            }
        }
    }

    private void crewRemoveChoiceView() {
        if (acceptEffect) {
            SldAdvCard card = controller.getPlayedCard();
            int toRemove = card.getCrewLost();
            modalShipboardView(controller.getNickname());
            modalHintTopLabel = new Label();
            ShipBoard ship = controller.getShipBoard();
            Map<Cordinate, Integer> cordToRemoving = new HashMap<>();
            Button confirmBtn = new Button("Confirm");
            synchronized (controller.getShipboardLock()) {
                modalHintTopLabel.setText("Click on the housing you want to remove crew from, click twice if you want to remove two members.\nCrew to remove: " + toRemove + ". Removed: 0\nTo undo a selection, click again");
                for (Cordinate cord : modalShipCordToStackPane.keySet()) {
                    StackPane sp = modalShipCordToStackPane.get(cord);
                    if (ship.getOptComponentByCord(cord).isPresent()) {
                        if (ship.getOptComponentByCord(cord).get().getType() == ComponentType.HOUSING) {
                            cordToRemoving.put(cord, 0);
                            sp.setOnMouseClicked(evt -> {
                                int removed = cordToRemoving.values().stream().mapToInt(i -> i).sum();
                                int maxRemovable = ship.getOptComponentByCord(cord).get().getGuests().size();
                                if (cordToRemoving.get(cord) < maxRemovable && removed < toRemove) {
                                    if (cordToRemoving.get(cord) == 0) {
                                        cordToRemoving.put(cord, 1);
                                    } else if (cordToRemoving.get(cord) == 1) {
                                        cordToRemoving.put(cord, 2);
                                    }
                                } else {
                                    if (cordToRemoving.get(cord) == 2) {
                                        cordToRemoving.put(cord, 1);
                                    } else if (cordToRemoving.get(cord) == 1) {
                                        cordToRemoving.put(cord, 0);
                                    }
                                }
                                loadHousingSpecificObjects(ship.getOptComponentByCord(cord).get(), sp, cordToRemoving.get(cord));
                                removed = cordToRemoving.values().stream().mapToInt(i -> i).sum();
                                modalHintTopLabel.setText("Click on the housing you want to remove crew from, click twice if you want to remove two members.\nCrew to remove: " + toRemove + ". Removed: " + removed + "\nTo undo a selection, click again");
                                confirmBtn.setDisable(removed != toRemove);

                            });
                        }
                    }
                }
            }
            HBox btnBox = new HBox(10);
            btnBox.setAlignment(Pos.CENTER);

            confirmBtn.setOnAction(evt -> {
                modalShipStage.close();
                List<Cordinate> cordsToRemove = new ArrayList<>();
                for (Cordinate cord : cordToRemoving.keySet()) {
                    for (int i = 0; i < cordToRemoving.get(cord); i++) {
                        cordsToRemove.add(cord);
                    }
                }
                controller.removeCrew(cordsToRemove);
            });
            Button resetBtn = new Button("Reset");
            resetBtn.setOnAction(evt -> {
                modalShipStage.close();
                crewRemoveChoiceView();
            });
            modalShipContainer.getChildren().remove(modalHintTopLabel);
            modalShipContainer.getChildren().addFirst(modalHintTopLabel);
            btnBox.getChildren().addAll(resetBtn, confirmBtn);
            modalShipContainer.getChildren().add(btnBox);
            if (modalErrorLabelMessage != null) {
                modalShipContainer.getChildren().addFirst(new Label(modalErrorLabelMessage));
                modalErrorLabelMessage = null;
            }
        } else {
            controller.skipEffect();
        }
    }

    private void goodsPlacementView() {
        depotAction = "MOVE";
        goodToBePlaced = null;
        modalShipboardView(controller.getNickname());
        modalHintTopLabel = new Label();
        ShipBoard ship = controller.getShipBoard();
        modalHintTopLabel.setText("Use the buttons or click on the depot you want to modify");
        synchronized (controller.getShipboardLock()) {
            for (Cordinate cord : modalShipCordToStackPane.keySet()) {
                StackPane sp = modalShipCordToStackPane.get(cord);
                if (ship.getOptComponentByCord(cord).isPresent()) {
                    if (ship.getOptComponentByCord(cord).get().getType() == ComponentType.DEPOT) {
                        sp.setOnMouseClicked(evt -> {
                            switch (depotAction) {
                                case "PLACE" -> {
                                    controller.addGood(ship.getOptComponentByCord(cord).get().getId(), goodToBePlaced);
                                    modalShipStage.close();
                                }
                                case "MOVE" -> {
                                    depotId = ship.getOptComponentByCord(cord).get().getId();
                                    modalGoodBoxView(new ArrayList<>(ship.getOptComponentByCord(cord).get().getStoredGoods()));
                                }

                            }
                        });
                    }
                }
            }
        }

        List<GoodType> goods = controller.getGoodsToInsert();
        HBox goodsBox = buildGoodList(goods, tilesSideLength / 3, null);
        HBox btnBox = new HBox(10);
        Button placeGoodBtn = new Button("Place");
        placeGoodBtn.setOnAction(evt -> {
            if (goodToBePlaced != null) {
                depotAction = "PLACE";
                backButton.setDisable(false);
                placeGoodBtn.setDisable(true);
                goodsBox.setDisable(true);
                modalHintTopLabel.setText("Click on the depot in which you want to place the good");
            }
        });
        Button backBtn = new Button("Back");
        backBtn.setDisable(true);
        backBtn.setOnAction(evt -> {
            goodsBox.setDisable(false);
            backButton.setDisable(true);
            placeGoodBtn.setDisable(false);
            modalHintTopLabel.setText("Use the buttons or click on the depot you want to modify");
            goodToBePlaced = null;
            goodsBox.getChildren().forEach(n -> n.setStyle(""));
        });
        Button finishBtn = new Button("End placement");
        finishBtn.setOnAction(evt -> {
            goodToBePlaced = null;
            controller.skipEffect();
            modalShipStage.close();
        });
        btnBox.getChildren().addAll(placeGoodBtn, backBtn, finishBtn);
        modalShipStage.setOnCloseRequest(event -> {
            goodToBePlaced = null;
            controller.skipEffect();
        });

        modalShipContainer.getChildren().remove(modalHintTopLabel);
        modalShipContainer.getChildren().addFirst(modalHintTopLabel);
        modalShipContainer.getChildren().addAll(goodsBox, btnBox);
        if (modalErrorLabelMessage != null) {
            modalShipContainer.getChildren().addFirst(new Label(modalErrorLabelMessage));
            modalErrorLabelMessage = null;
        }
    }

    private void modalGoodBoxView(List<GoodType> goods) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Depot goods");
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        HBox btnBox = new HBox(10);
        HBox goodBox = buildGoodList(goods, tilesSideLength, btnBox);
        btnBox.setAlignment(Pos.CENTER);
        Button discardButton = new Button("Remove good");
        discardButton.setOnAction(evt -> {
            controller.removeGood(depotId, goodToBePlaced);
            goodToBePlaced = null;
            modal.close();
            modalShipStage.close();
        });
        btnBox.getChildren().addAll(discardButton);
        box.getChildren().addAll(goodBox, btnBox);
        btnBox.setDisable(true);
        modal.setScene(new Scene(box));
        modal.show();
    }

    private void acceptEffectStage(Runnable postConfirmation) {
        Stage acceptStage = new Stage();
        acceptStage.setTitle("Accept the effect");
        acceptStage.initModality(Modality.APPLICATION_MODAL);
        acceptStage.setResizable(false);
        VBox center = new VBox(10);
        center.setPadding(new Insets(20));
        center.setAlignment(Pos.CENTER);
        Label acceptLabel = new Label("Do you want to apply the effect?");
        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER);
        Button cancelBtn = new Button("No");
        cancelBtn.setOnAction(evt -> {
            acceptEffect = false;
            acceptStage.close();
            postConfirmation.run();
        });
        Button confirmBtn = new Button("Yes");
        confirmBtn.setOnAction(evt -> {
            acceptEffect = true;
            acceptStage.close();
            postConfirmation.run();
        });
        btnBox.getChildren().addAll(cancelBtn, confirmBtn);
        center.getChildren().addAll(acceptLabel, btnBox);
        acceptStage.setScene(new Scene(center));
        acceptStage.show();
    }

    /*
     * Callback methods section
     */

    /**
     * forwards the request of connection to the controller
     *
     * @param isRmi: true: rmi connection, false: socket connection
     */
    private void connectToServer(boolean isRmi) {
        controller.connectToServer(isRmi);
    }

    /**
     * verify if the input is ok, then forwards the nickname input to the controller
     *
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
     *
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
     *
     * @param componentId: id of the chosen component
     */
    private void chooseComponentFromUncovered(int componentId) {
        controller.drawUncovered(componentId);
        uncoveredComponentModalStage.close();
        uncoveredComponentsTilePane.setVisible(false);
    }

    /**
     * if the click on the component is enabled, execute different instructions based on the state.
     * - ADD_COMPONENT: forwards to the controller the request to add a component in the clicked position
     * - SWITCH_BOOKED: forwards to the controller the request to swap a component with a booked one
     * - BUILDING_SHIP: allows the user to take and place the clicked booked component
     *
     * @param i: row index of the clicked cell
     * @param j: column of the clicked cell
     */
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
                case BUILDING_SHIP -> {
                    if (i == 0) {
                        if (j == 5) {
                            controller.getBooked(0);
                        } else if (j == 6) {
                            controller.getBooked(1);
                        }
                    }
                }
                default -> {
                }
            }
        }
    }

    /**
     * forwards to the controller the request to end build
     */
    private void endBuild() {
        controller.endBuild();
    }

    /**
     * forwards to the controller the chosen place for the adventure starting
     *
     * @param place: the chosen place
     */
    private void choosePlace(int place) {
        choosePositionStage.close();
        controller.choosePlace(place);
    }

    /**
     * forwards the controller the request to draw the new adventure card
     */
    private void drawNewAdvCard() {
        controller.drawNewAdvCard();
    }

    /**
     * creates a modal stage that asks for a confirmation to leave the flight, if the user wants to, it calls the "leaveFlight" controller method
     */
    private void leaveFlightConfirm() {
        Stage alertStage = new Stage();
        alertStage.setTitle("Leave the flight");
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.setResizable(false);
        VBox center = new VBox(10);
        center.setAlignment(Pos.CENTER);
        Label alertLabel = new Label("Do you want to leave the flight?");
        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(evt -> {
            alertStage.close();
        });
        Button confirmBtn = new Button("Confirm");
        confirmBtn.setOnAction(evt -> {
            alertStage.close();
            controller.leaveFlight();
        });
        btnBox.getChildren().addAll(cancelBtn, confirmBtn);
        center.getChildren().addAll(alertLabel, btnBox);
        alertStage.setScene(new Scene(center));
        alertStage.show();
    }

    /*
     * utility methods
     */

    /**
     * place the images of the components in the shipboard grid based on the parameters given
     *
     * @param idMatrix:         matrix of component ids
     * @param rotationsMatrix:  matrix of rotations of the components
     * @param bookedComponents: booked components to show
     * @param map:              map that link coordinates with the related imageView
     */
    private void fillShipboard(Optional<Integer>[][] idMatrix, Optional<Integer>[][] rotationsMatrix, List<Optional<Integer>> bookedComponents, Map<Cordinate, ImageView> map) {

        for (Cordinate cord : map.keySet()) {
            if (!cord.equals(new Cordinate(0, 5)) && !cord.equals(new Cordinate(0, 6))) {
                if (idMatrix[cord.getRow()][cord.getColumn()].isPresent() && rotationsMatrix[cord.getRow()][cord.getColumn()].isPresent()) {
                    String tmpResourcePath = IMG_PATH + TILES_REL_PATH + (idMatrix[cord.getRow()][cord.getColumn()].get() == 1 ? "" : idMatrix[cord.getRow()][cord.getColumn()].get()) + IMG_JPG_EXTENSION;
                    map.get(cord).setImage(new Image(getClass().getResource(tmpResourcePath).toExternalForm()));
                    map.get(cord).setRotate(rotationsMatrix[cord.getRow()][cord.getColumn()].get() * 90);
                } else {
                    map.get(cord).setImage(null);
                }
            }
        }
        refreshBooked(bookedComponents, map);
    }

    /**
     * updates the booked components in the shipboard view
     *
     * @param bookedComponents: booked components to show
     * @param map:map           that link coordinates with the related imageView
     */
    private void refreshBooked(List<Optional<Integer>> bookedComponents, Map<Cordinate, ImageView> map) {
        String tmpResourcePath1 = "", tmpResourcePath2 = "";
        for (Cordinate cord : map.keySet()) {
            if (cord.equals(new Cordinate(0, 5))) {
                if (bookedComponents.get(0).isPresent()) {
                    tmpResourcePath1 = IMG_PATH + TILES_REL_PATH + (bookedComponents.get(0).get() == 1 ? "" : bookedComponents.get(0).get()) + IMG_JPG_EXTENSION;
                    Image img1 = new Image(getClass().getResource(tmpResourcePath1).toExternalForm());
                    map.get(cord).setImage(img1);
                } else {
                    map.get(cord).setImage(null);
                }
            }
            if (cord.equals(new Cordinate(0, 6))) {
                if (bookedComponents.get(1).isPresent()) {
                    tmpResourcePath2 = IMG_PATH + TILES_REL_PATH + (bookedComponents.get(1).get() == 1 ? "" : bookedComponents.get(1).get()) + IMG_JPG_EXTENSION;
                    Image img2 = new Image(getClass().getResource(tmpResourcePath2).toExternalForm());
                    map.get(cord).setImage(img2);
                } else {
                    map.get(cord).setImage(null);
                }
            }
        }

    }

    /**
     * show graphically the content of ENERGY_DEPOT, DEPOT and HOUSING
     *
     * @param ship: the shipboard to display the content of
     * @param map:  the map of the stack panes where add the icons
     */
    private void loadComponentSpecificObjects(ShipBoard ship, Map<Cordinate, StackPane> map) {
        for (Cordinate cord : map.keySet()) {
            if (!((cord.getRow() == 0) && (cord.getColumn() == 5 || cord.getColumn() == 6)) && ship.getOptComponentByCord(cord).isPresent()) {
                Component c = ship.getOptComponentByCord(cord).get();
                StackPane sp = map.get(cord);
                switch (c.getType()) {
                    case ComponentType.ENERGY_DEPOT -> loadEnergySpecificObjects(c, sp);
                    case ComponentType.HOUSING -> loadHousingSpecificObjects(c, sp, 0);
                    case ComponentType.DEPOT -> loadDepotSpecificObjects(c, sp);
                    default -> {
                    }
                }
            }
        }
    }

    private void loadHousingSpecificObjects(Component c, StackPane sp, int toCancel) {
        if (!c.getGuests().isEmpty()) {
            sp.getChildren().retainAll(sp.getChildren().getFirst());
            HBox hbox = new HBox(tilesSideLength / 12);
            hbox.setAlignment(Pos.CENTER);
            for (int i = 0; i < c.getGuests().size(); i++) {
                Color color;
                switch (c.getGuests().get(i)) {
                    case HUMAN -> color = Color.DARKSLATEGREY;
                    case PURPLE -> color = Color.FUCHSIA;
                    case BROWN -> color = Color.SANDYBROWN;
                    default -> color = Color.BLACK;
                }
                if (toCancel > 0) {
                    toCancel--;
                } else {
                    hbox.getChildren().add(new Circle(tilesSideLength / 12, color));
                }
            }
            sp.getChildren().add(hbox);
        }
    }

    private void loadDepotSpecificObjects(Component c, StackPane sp) {

        if (!c.getStoredGoods().isEmpty()) {
            HBox hbox = new HBox(tilesSideLength / 12);
            hbox.setAlignment(Pos.CENTER);
            for (int i = 0; i < c.getStoredGoods().size(); i++) {
                String goodName, tmpResourcePath;
                switch (c.getStoredGoods().get(i)) {
                    case RED -> goodName = "goodRed";
                    case YELLOW -> goodName = "goodYellow";
                    case GREEN -> goodName = "goodGreen";
                    case BLUE -> goodName = "goodBlue";
                    default -> goodName = "ERR";
                }
                tmpResourcePath = IMG_PATH + OTHERS_REL_PATH + goodName + IMG_PNG_EXTENSION;
                Image img = new Image(getClass().getResourceAsStream(tmpResourcePath));
                ImageView imgView = new ImageView(img);
                imgView.setFitHeight(tilesSideLength / 5);
                imgView.setPreserveRatio(true);
                hbox.getChildren().add(imgView);
            }
            sp.getChildren().add(hbox);
        }
    }

    private void loadEnergySpecificObjects(Component c, StackPane sp) {
        if (c.getEnergyQuantity() > 0) {
            VBox vbox = new VBox();
            vbox.setPadding(new Insets(tilesSideLength / 10));
            vbox.setAlignment(Pos.BOTTOM_CENTER);
            HBox hbox = new HBox(tilesSideLength / 14);
            hbox.setAlignment(Pos.CENTER);
            hbox.setBackground(new Background(new BackgroundFill(
                    Color.rgb(255, 255, 255, 0.5),
                    null,
                    null
            )));
            hbox.setPadding(new Insets(tilesSideLength / 18));
            vbox.getChildren().add(hbox);
            for (int i = 0; i < c.getEnergyQuantity(); i++) {
                hbox.getChildren().add(new Circle(tilesSideLength / 12, Color.GREEN));
            }
            sp.getChildren().add(vbox);
        }
    }

    /**
     * set the passed tilesHeight to the rows and columns of the grid
     *
     * @param grid:        shipboard grid to apply the constraints to
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

    /**
     * clear the circle layer for the circuit, then place the circle of the player in the right position
     */
    private void updateCircuit() {
        List<Optional<Player>> circuit = new ArrayList<>(controller.getCircuit());
        circlesLayer.getChildren().clear();
        for (int i = 0; i < coordList.size(); i++) {
            if (circuit.get(i).isPresent()) {
                double x = coordList.get(i).getX() * circuitImageView.getBoundsInParent().getWidth();
                double y = coordList.get(i).getY() * circuitImageView.getBoundsInParent().getHeight();
                Circle circle = new Circle(7, housingColors.get(circuit.get(i).get().getColor()));
                circle.setLayoutX(x);
                circle.setLayoutY(y);
                circlesLayer.getChildren().add(circle);
            }
        }
    }

    private HBox buildGoodList(List<GoodType> goods, double sideLength, HBox btnBox) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);

        for (int i = 0; i < goods.size(); i++) {
            final int index = i;
            String goodName, tmpResourcePath;
            switch (goods.get(i)) {
                case RED -> goodName = "goodRed";
                case YELLOW -> goodName = "goodYellow";
                case GREEN -> goodName = "goodGreen";
                case BLUE -> goodName = "goodBlue";
                default -> goodName = "ERR";
            }
            tmpResourcePath = IMG_PATH + OTHERS_REL_PATH + goodName + IMG_PNG_EXTENSION;
            Image img = new Image(getClass().getResourceAsStream(tmpResourcePath));
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(sideLength);
            imgView.setPreserveRatio(true);
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(imgView);
            box.getChildren().add(stackPane);
            stackPane.setOnMouseClicked(evt -> {
                goodToBePlaced = goods.get(index);
                box.setDisable(true);
                stackPane.setStyle("-fx-border-color: grey; -fx-border-width: 3");
                if (btnBox != null)
                    btnBox.setDisable(false);
            });
        }
        return box;
    }

}
