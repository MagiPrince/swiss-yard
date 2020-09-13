package ch.hepia.swissyard;

public class NonExistantPlaceException extends Exception{
    public NonExistantPlaceException(){
        super("There is no Stop !");
    }
}