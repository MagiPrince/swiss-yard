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

public class MessageToComTest{
    
    Coordinate coordonnees = new Coordinate("Arret", 46.05, 70.34);
    Place place = new Stop("PlaceTest", "2", coordonnees);
    Stop stop = new Stop("StopTest", "1", coordonnees);
    Player player = new Policeman("PolicemanTest", stop);

    LinkedHashMap<Stop,Integer> passLine = new LinkedHashMap<>();

    Transport transport = new Transport("Tram 1", "Tram", "14", passLine);

    MessageToCom messageToCom = new MessageToCom("Player", "TestClass", player, place);

    MessageToCom messageToComSecond = new MessageToCom(Stop.class.getName(), "TestClass", stop, transport, MessageType.TERMINUS);

    @Test
    void getAuteurClassTest(){
        assertEquals(messageToCom.getAuteurClass(), "Player");
    }

    @Test
    void getMessageClassTest(){
        assertEquals(messageToCom.getMessageClass(), "TestClass");
    }

    @Test
    void safeGetPlayerTest(){
        assertEquals(messageToCom.safeGetPlayer().get(), player);
    }

    @Test
    void safeGetPlaceTest(){
        assertEquals(messageToCom.safeGetPlace().get(), place);
    }

    @Test
    void safeGetTypeTest(){
        assertEquals(messageToComSecond.safeGetType().get(), MessageType.TERMINUS);
    }

    @Test
    void toStringTest(){
        assertEquals(messageToComSecond.toString(), "Event : Tram 14 is at his terminus StopTest(Stop)");
        assertEquals(messageToCom.toString(), "Event : Message unknown");
    }

}