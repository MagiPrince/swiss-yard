package ch.hepia.swissyard.communication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.hepia.swissyard.Coordinate;
import ch.hepia.swissyard.Place;
import ch.hepia.swissyard.Player;
import ch.hepia.swissyard.Policeman;
import ch.hepia.swissyard.Stop;
import ch.hepia.swissyard.Thief;
import ch.hepia.swissyard.Transport;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComTest{
    Broker b = new Broker("localhost");
    Client c = new Client("localhost");

    @Test
    void TestClientMessage(){
        c.send("COUCOU");
    }

    @Test
    void TestClientMessageToCom(){

        Coordinate coordonnees = new Coordinate("Arret", 46.05, 70.34);

        Stop stop = new Stop("StopTest", "1", coordonnees);

        Player player = new Policeman("PolicemanTest", stop);
        
        MessageToCom messageToCom = new MessageToCom("Test", "TestClass", player, stop);

        c.send(messageToCom);
    }
}