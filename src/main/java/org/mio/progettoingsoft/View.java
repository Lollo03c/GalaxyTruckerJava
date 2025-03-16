package org.mio.progettoingsoft;

import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadInputException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class View {

    private final Player player;
    private final Scanner scanner;
    private final ShipBoard shipBoard;

    public View(Player player){
        this.player = player;
        this.shipBoard = player.getShipBoard();

        scanner = new Scanner(System.in);
    }

    public Player getPlayer(){
        return player;
    }

    public ShipBoard getShipBoard(){
        return  player.getShipBoard();
    }

    public int askDoubleEngine(){
        int maxAvailable = Integer.min(
                player.getShipBoard().getQuantBatteries(),
                player.getShipBoard().getDoubleEngine().size()
        );

        System.out.println("How many double engine to activate (max " + maxAvailable + ")? : " );
        return scanner.nextInt();
    }

    public boolean askForEffect(AdvCardType card) {
        System.out.println("Player : " + player.getUsername() + ". Apply the effect of " + card + " (y/s) : ");
        String ans = scanner.nextLine().toLowerCase();

        while (!(ans.equals("y") || ans.equals("n"))){
            System.out.println(ans + "in not accepted. Only 'y' or 'n'");
            System.out.println("");
            System.out.println("Apply the effect of " + card + " (y/s) : ");
            ans = scanner.nextLine().toLowerCase();
        }

        if (ans.equals("y"))
            return true;

        return false;
    }

    private Component askForComponentFromList(List<Component> components ){
        boolean toRepeat = true;
        int selected = 0;

        for (int  i = 0; i < components.size(); i++){
            System.out.println((i+1) + ": component in position " + shipBoard.printPosition(components.get(i)));
        }

        while (toRepeat) {
            System.out.println("Select the number of the component : ");
            try {
                selected = scanner.nextInt();
                if (selected < 1 || selected > components.size())
                    throw new BadInputException("No component associated with this number. Try Again");
                toRepeat = false;

            } catch (InputMismatchException | BadInputException e) {

            }
        }

        return components.get(selected - 1);
    }

    public Component askForHousingToRemoveGuest(String message){
        System.out.println(message);

        List<Component> housingAvailable = shipBoard.getComponentsStream()
                .filter(comp -> comp.getQuantityGuests() > 0)
                .toList();

        return askForComponentFromList(housingAvailable);
    }

    public Component askForDepotToAdd(GoodType type){
        System.out.println("Select the depot in which add the " + type + " good.");

        List<Component> availableDepot = shipBoard.getComponentsStream()
                .filter(comp -> comp.canContainsGood(type))
                .toList();

        return askForComponentFromList(availableDepot);
    }

    public List<Component> askDoubleDrill(){
        System.out.println("Player " + player.getUsername() + ". Select the double drills to activate");

        List<Component> activated = new ArrayList<>();

        List<Component> possible = shipBoard.getComponentsStream()
                .filter(comp -> comp.getFirePower() == 2.0f)
                .toList();

        boolean ended = shipBoard.getQuantBatteries() <= 0;

        while(!ended) {
            for (int i = 0; i < possible.size(); i++) {
                System.out.println((i + 1) + ": Component in position " + shipBoard.printPosition(possible.get(i)));
            }
            System.out.println("0: to exit");

            try {
                int selected = scanner.nextInt();
                if (selected == 0){
                    ended = true;
                }
                else if (selected < 1 || selected > possible.size()){
                    throw new BadInputException("");
                }
                else {
                    activated.add(possible.get(selected - 1));
                    possible.remove(possible.get(selected - 1));
                }
            }
            catch (InputMismatchException e){

            }
            catch (BadInputException e){

            }

            if (activated.size() >= shipBoard.getQuantBatteries())
                ended = true;
        }

        return activated;

    }
}
