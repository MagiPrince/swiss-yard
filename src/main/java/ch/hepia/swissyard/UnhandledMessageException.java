package ch.hepia.swissyard;

public class UnhandledMessageException extends Exception{
    public UnhandledMessageException(){
        super("The message format is unhandled !");
    }
}