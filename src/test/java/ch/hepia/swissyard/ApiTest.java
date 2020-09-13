package ch.hepia.swissyard;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

public class ApiTest{

    static ArrayList<Stop> allStops;
    @BeforeAll
    public static void init(){
        allStops = ApiParser.getStations();
    }

    @Test
    void TestAppel(){
        SwissTransportApi r = new SwissTransportApi("http://transport.opendata.ch/v1/");
        r.locationQuery("8587057");
        Stop s = ApiParser.getStation("8592988");
        System.out.println(s.toString());
    }

    @Test
    void testStationBoard(){
        SwissTransportApi r = new SwissTransportApi("http://transport.opendata.ch/v1/");

        ArrayList<String> arret = new ArrayList<>();
        arret.add("8587061");
        arret.add("8592726");
        arret.add("8592728");
        arret.add("8592727");
        arret.add("8587045");
        arret.add("8592724");
        arret.add("8592725");

        for (String id : arret){

            Optional<Stop> instStop = allStops.stream()
                               .filter(s -> s.getId().equals(id))
                               .findFirst();

            System.out.println(instStop.get().toString());
        }

        LinkedHashMap<Transport, Integer> ts = ApiParser.getNextDeparture("8592988", "5");
    }
}