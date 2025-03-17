package org.mio.progettoingsoft;

import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
import org.mio.progettoingsoft.advCards.Planet;
import org.mio.progettoingsoft.advCards.Planets;
import org.mio.progettoingsoft.components.DoubleDrill;
import org.mio.progettoingsoft.components.GoodType;
import org.mio.progettoingsoft.exceptions.BadInputException;

import javax.sound.midi.Soundbank;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        boolean ended = false;
        int number = 0;
        while (!ended) {
            System.out.println("How many double engine to activate (max " + maxAvailable + ")? : ");
            try {
                number = scanner.nextInt();

                if (number > maxAvailable || number < 0) {
                    throw  new BadInputException("");
                }
                ended = true;
            } catch (InputMismatchException e) {

            }
            catch (BadInputException e){

            }
        }
        return number;

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

    public int askForPlanet(List<Planet> planets) {
        System.out.println("Player : " + player.getUsername() + " do you want to land somewhere (y/n) : ");
        String ans = scanner.nextLine().trim().toLowerCase();

        while (!(ans.equals("y") || ans.equals("n"))) {
            System.out.println(ans + "in not accepted. Only 'y' or 'n'");
            System.out.println("");
            System.out.println("do you want to land somewhere  (y/n) : ");
            ans = scanner.nextLine().trim().toLowerCase();
        }
        if (ans.equals("y")) {
            System.out.println("Select the planet (index from 1 to " + planets.size() + "): ");
            int choice = -1;

            while (true) {
                String input = scanner.nextLine().trim();
                try {
                    choice = Integer.parseInt(input);
                    if (choice >= 1 && choice <= planets.size()) {
                        if(planets.get(choice-1).getPlayer().isPresent()){
                            System.out.println("planet already taken, please choose another one");
                        }
                        else{
                            break;
                        }
                    } else {
                        System.out.println("Invalid number, insert a number between 1 and " + planets.size());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            }

            return choice;
        }
        return 0;
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
                .filter(comp -> comp.getType().equals(ComponentType.DOUBLE_DRILL))
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

    public int rollDicesAndSum(){
        Random random = new Random();

        int a = 1 + random.nextInt(6);
        int b = 1 + random.nextInt(6);

        return a + b;
    }

    public boolean askShield(Direction direction){
        List<Component> shields = shipBoard.getComponentsStream()
                .filter(component -> component.getShieldDirections() != null)
                .filter(component -> component.getShieldDirections().contains(direction))
                .toList();

        if (shields.isEmpty())
            return false;

        String ans;
        boolean toContinue = shipBoard.getQuantBatteries() > 0;
        boolean answer = false;

        while (toContinue) {
            System.out.print(player.getUsername() + "activate a shield (y/n) : ");
            ans = scanner.nextLine().toLowerCase();

            if (ans.equals("y") || ans.equals("n")){
                toContinue = false;

                answer = ans.equals("y");
            }
            else{
                System.out.println("Answer not valid. Try again.");
            }
        }

        return answer;
    }

    public boolean askOneDoubleDrill(){
        String ans;
        boolean valid = !(shipBoard.getQuantBatteries() > 0);
        boolean answer = false;

        while (!valid) {
            System.out.print(player.getUsername() + "activate a shield (y/n) : ");
            ans = scanner.nextLine().toLowerCase();

            if (ans.equals("y") || ans.equals("n")){
                valid = true;

                answer = ans.equals("y");
            }
            else{
                System.out.println("Answer not valid. Try again.");
            }
        }

        return answer;
    }
}
