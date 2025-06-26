package org.mio.progettoingsoft.network.client.socket;

import org.mio.progettoingsoft.Cordinate;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.components.GuestType;
import org.mio.progettoingsoft.model.enums.GameInfo;
import org.mio.progettoingsoft.network.messages.*;
import org.mio.progettoingsoft.network.server.socket.VirtualServerSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

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

    @Override
    public void takeBuild(int idGame, String nickname) throws IOException{
        Message message = new BuildShipMessage(idGame, nickname);
        sendMessage(message);
    }

//    @Override
//    public void applyStardust(int idGame, String nickname, SldStardust card) {
//        Message message = new StardustMessage(idGame, nickname, card);
//        try{
//            sendMessage(message);
//        }
//        catch(IOException e){
//            e.printStackTrace();
//        }
//    }

    @Override
    public void endBuild(int idGame, String nickname) throws IOException{
        Message message = new EndBuildMessage(idGame, nickname);
        sendMessage(message);
    }

    @Override
    public void choosePlace(int idGame, String nickname, int place) throws IOException {
        Message message = new ChoosePlacementMessage( idGame, nickname,  place);
        sendMessage(message);
    }

    @Override
    public void endValidation(int idGame, String nickname, boolean usedBattery) throws Exception{
        Message message = new EndValidationMessage(idGame, nickname, usedBattery);
        sendMessage(message);
    }

    @Override
    public void activateDoubleEngine(int idGame, String nickname, int number) throws IOException{
        Message message = new DoubleEngineMessage(idGame, nickname, number);
        sendMessage(message);
    }

    @Override
    public void leaveFlight(int idGame, String nickname, boolean leave) throws IOException {
        Message message = new LeaveMessage(idGame, nickname, leave);
        sendMessage(message);
    }

    @Override
    public void drawCard(int idGame, String nickname) throws IOException {
        // idCard = 1 perchè non mi interessa l'idCard quando mando l'input da client a Server di pescare : mi servirà al ritorno
        Message message = new DrawCardMessage(idGame,nickname,1);
        sendMessage(message);
    }

    @Override
    public void skipEffect(int idGame, String nickname, int idCard) throws IOException{
        Message message = new SkipEffectMessage(idGame, nickname, idCard);
        sendMessage(message);
    }

    @Override
    public void applyEffect(int idGame, String nickname) throws IOException{
        Message message = new ApplyEffectMessage(idGame, nickname);
        sendMessage(message);
    }

    @Override
    public void crewRemove(int idGame, String nickname, List<Cordinate> cordinateList) throws IOException{
        Message message = new CrewRemoveMessage(idGame, nickname, cordinateList);
        sendMessage(message);
    }

    @Override
    public void addGood(int idGame, String nickname, int compId, GoodType type) throws IOException{
        Message message = new GoodMessage(idGame, nickname, GoodMessage.GoodMessageType.ADD_GOOD, compId, type);
        sendMessage(message);
    }

    @Override
    public void removeGood(int idGame, String nickaname, int compId, GoodType type) throws IOException{
        Message message = new GoodMessage(idGame, nickaname, GoodMessage.GoodMessageType.REMOVE_GOOD, compId, type);
        sendMessage(message);
    }

    @Override
    public void activateDoubleDrills(int idGame, String nickname, List<Cordinate> drillCordintes) throws IOException{
        Message message = new DoubleDrillMessage(idGame, nickname, drillCordintes);
        sendMessage(message);
    }

    @Override
    public void landOnPlanet(int idGame, String nickname, int choice)throws IOException{
        Message message = new LandOnPlanetMessage(idGame,nickname,choice);
        sendMessage(message);
    }

//    @Override
//    public void activateSlaver(int idGame,String nickname,List<Cordinate> activatedDrills,boolean wantsToActivate)throws IOException{
//        Message message = new ActivateSlaversMessage(idGame,nickname,activatedDrills,wantsToActivate);
//        sendMessage(message);
//    }

    @Override
    public void setRollResult(int idGame, String nickname, int first, int second) throws IOException{
        Message message = new RollDiceMessage(idGame, nickname, first, second);
        sendMessage(message);
    }

//    @Override
//    public void removeBattery(int idGame, String nickname, int quantity) throws IOException{
//        Message message = new BatteryMessage(idGame, nickname, quantity);
//        sendMessage(message);
//    }

    @Override
    public void advanceMeteor(int idGame, String nickname, boolean destroyed, boolean energy) throws IOException{
        Message message = new AdvanceMeteorMessage(idGame, nickname, destroyed, energy);
        sendMessage(message);
    }

    @Override
    public void advanceCannon(int idGame, String nickname, boolean destroyed, boolean energy) throws IOException{
        Message message = new AdvanceCannonMessage(idGame, nickname, destroyed, energy);
        sendMessage(message);
    }

    @Override
    public void removeComponent(int idGame, String nickname, Cordinate cordinate, boolean toAllClient) throws IOException{
        Message message = new ComponentMessage(idGame, nickname, ComponentMessage.Action.REMOVE, -1, cordinate, -1, toAllClient);
        sendMessage(message);
    }

    @Override
    public void startHourglass(int idGame) throws IOException{
        Message message = new StartHourglassMessage(idGame, " wesh ");
        sendMessage(message);
    }

    @Override
    public void addCrew(int idGame, String nickname, Map<Cordinate, List<GuestType>> addedCrew) throws IOException{
        Message message = new AddCrewMessage(idGame, nickname, addedCrew);
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
