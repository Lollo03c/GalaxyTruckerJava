package org.mio.progettoingsoft.exceptions;

public class NotEnoughBatteriesException extends RuntimeException {
  public NotEnoughBatteriesException(String message) {
    super(message);
  }

  public NotEnoughBatteriesException(){
    super ("Not enough batteries");
  }
}
