package ch.hepia.swissyard;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.hepia.swissyard.communication.Broker;
import ch.hepia.swissyard.view.View;

import java.util.*;

public class GameplayTest{
    
    @Test
    void TestNewPlayer(){
        Stop randomStop = new Stop("Cornavin","", new Coordinate("",15.00,30.00));
        Player policeman = new Policeman(randomStop);
        assertEquals(policeman.getName(), "Policeman");
        assertEquals(policeman.getPlace(), randomStop);

        Player thief = new Thief("Michel", randomStop);
        assertEquals(thief.getName(), "Michel");
        assertEquals(thief.getPlace(), randomStop);
    }

    @Test
    void TestCatchThief(){
        Stop randomStop = new Stop("Cornavin","", new Coordinate("",15.00,30.00));
        Player policeman = new Policeman(randomStop);
        Player thief = new Thief("Michel", randomStop);
        assertTrue(policeman.catchThief(thief));
    }

    @Disabled
    @Test
    void TestnbPlayers(){
        View v = new View();
        Controller c = new Controller(v);
        try{
            c.createPlayers(3);
        } catch (NonExistantPlaceException | IllegalArgumentException e) {
            System.err.println("Error : " + e.getMessage());
        }
        assertEquals(c.nbPlayers(), 3);
    }

    @Test 
    void TestTimePassed(){
        LinkedHashMap<Stop,Integer> passLine = new LinkedHashMap<Stop,Integer>();
        passLine.put(new Stop(), 30);
        passLine.put(new Stop(), 90);
        passLine.put(new Stop(), 150);
        passLine.put(new Stop(), 210);
        Place bus = new Transport("Bus 1", passLine);
    
        Iterator<Map.Entry<Stop,Integer>> i = passLine.entrySet().iterator();
        int n=0;
        while (i.hasNext()){
            bus.timePassed(30);
            assertEquals((int) i.next().getValue() , 30*n);
            n++;
        }
    }

    
    @Test
    void TestPlayerTakesAndLeavesTrasport(){
        Broker b = new Broker("localhost");
        final Integer time = 30;
        Stop poterie = new Stop("Poterie","1", new Coordinate());
        Stop servette = new Stop("Servette","2", new Coordinate());
        Stop vieusseux = new Stop("Vieusseux","3", new Coordinate());
        Player policeman = new Policeman(servette);
        
        LinkedHashMap<Stop,Integer> passLine = new LinkedHashMap<>();
        passLine.put(poterie, -60);        
        passLine.put(servette, 60);
        passLine.put(vieusseux, 180);
        Transport tram = new Transport("Tram 1", "Tram", "14", passLine);

        LinkedHashMap<Transport,Integer> nextDepartures = new LinkedHashMap<>();
        nextDepartures.put(tram,60);
        servette = new Stop("Servette","2", new Coordinate(), nextDepartures);

        try {
            assertFalse(tram.isTransportAtTheStop());
            
            tram.timePassed(time);
            servette.timePassed(time);

            assertTrue(tram.isTransportAtTheStop());
            
            tram.arrived();
            assertEquals(tram.next(), servette);
            assertEquals(servette, policeman.getPlace());
            assertEquals(servette.next(), tram);
            assertEquals(nextDepartures.entrySet().iterator().next().getKey(), tram);
            Place p = policeman.getPlace();
            policeman.takeTransport();
            
            assertEquals(policeman.getPlace(), tram);
            tram.timePassed(time);
            assertFalse(tram.isTransportAtTheStop());
            tram.left();

            tram.timePassed(time);
            tram.timePassed(time);
            tram.timePassed(time);

            assertTrue(tram.isTransportAtTheStop());
            tram.arrived();
            assertEquals(tram.next(), vieusseux);
            policeman.getOutOfTransport();
            assertEquals(policeman.getPlace(), vieusseux);
        } catch (NonExistantPlaceException e) {
            System.err.println("Error : " + e.getMessage());
        }
    }
}