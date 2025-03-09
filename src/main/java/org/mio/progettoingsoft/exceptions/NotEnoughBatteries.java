package org.mio.progettoingsoft.exceptions;

public class NotEnoughBatteries extends RuntimeException {
  public NotEnoughBatteries(String message) {
    super(message);
  }

  public NotEnoughBatteries(){
    super ("Not enough batteries");
  }
}
