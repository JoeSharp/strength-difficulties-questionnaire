package uk.ratracejoe.sdq.exception;

public class SdqException extends RuntimeException {
  public SdqException(String msg) {
    super(msg);
  }

  public SdqException(String msg, Throwable e) {
    super(msg, e);
  }
}
