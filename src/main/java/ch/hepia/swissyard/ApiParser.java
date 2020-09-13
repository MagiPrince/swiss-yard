package ch.hepia.swissyard;

import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.*;

public class ApiParser{
    private static SwissTransportApi apiRequest = new SwissTransportApi("http://transport.opendata.ch/v1/");
    private static ArrayList<Stop> stopsLists;
    private static final String LIMIT = "5";

    private static String readBusLines(){
        String stations = "";

        try {
            stations = Files.readString(Path.of(ApiParser.class.getResource("/stop_info.txt").getPath()));
        } catch(IOException e){
            e.printStackTrace();
        }

        return stations;
    }

    public static ArrayList<Stop> getStations(){
        String stationsInfo = readBusLines();
        ArrayList<Stop> stations = new ArrayList<>();

        JSONArray allLines = new JSONObject(stationsInfo).getJSONArray("stations");

        for (int i = 0; i < allLines.length(); i++){
            stations.add(setStop(allLines.getJSONObject(i)));
        }

        stopsLists = stations;

        return stations;
    }

    public static Stop getStation(String stationName){
        JSONObject stationDetails = apiRequest.locationQuery(stationName).get().getJSONObject(0);
        return setStop(stationDetails);
    }

    /**
     * This function get all the details of a Bus stop from a JSONObject
     * @param details
     * @return new Stop
     */
    private static Stop setStop(JSONObject details){
        String name = details.getString("name");

        JSONObject coordJson = details.getJSONObject("coordinate");
        Coordinate coord = new Coordinate(coordJson.getString("type"),
                                          coordJson.getDouble("x"),
                                          coordJson.getDouble("y"));

        String id = details.getString("id");
        Stop s = new Stop(name, id, coord);

        return s;
    }

    private static JSONArray getStationboard(String busStop, String limit){
        return apiRequest.stationboardQuery(busStop, limit).get();
    }

    /**
     * Update the passline time of a transport
     * @param t
     */
    public static void updatePassLine(Transport t){
        
        try {
            Stop nextStop = (Stop) t.next();
            LinkedHashMap<Transport, Integer> departures = getNextDeparture(nextStop.getName(), LIMIT+5);
            
            for (Transport transport : departures.keySet()){
                if (transport.equals(t)){
                    t = transport;
                }
            }
        } catch (NonExistantPlaceException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * This function get all the stop from a line and create a list of Stop from it
     * @param line
     * @return
     */
    private static ArrayList<Stop> setStopsFromLine(JSONArray line){
        ArrayList<Stop> stopLine = new ArrayList<>();

        for (int i = 0; i < line.length(); i++){
            String idStop = line.getJSONObject(i).getJSONObject("station").getString("id");
            try{
                Optional<Stop> instStop = stopsLists.stream()
                               .filter(s -> s.getId().equals(idStop))
                               .findFirst();

                instStop.ifPresent(s -> stopLine.add(s));
            } catch (NoSuchElementException ex){
                System.out.println("Le numéro d'arrêt n'est pas présent dans la liste : " + idStop);
            }
        }

        return stopLine;
    }

    private static LinkedHashMap<Stop, Integer> getPassLineWithDeparture(JSONArray line){
        LinkedHashMap<Stop, Integer> passList = new LinkedHashMap<>();
        ArrayList<Stop> stops = setStopsFromLine(line);

        for (int i = 0; i < line.length(); i++){
            if (i != line.length()-1){
                passList.put(stops.get(i), line.getJSONObject(i).getInt("departureTimestamp"));
            } else {
                passList.put(stops.get(i), 0);
            }
        }

        return passList;
    }

    public static LinkedHashMap<Transport,Integer> getNextDeparture(String busStop, String limit){
        JSONArray stationBoard = getStationboard(busStop, limit);
        System.out.println(busStop);
        ArrayList<Transport> transports = new ArrayList<>();

        for (int i = 0; i < Integer.parseInt(limit); i++){
            JSONObject nextDeparture = stationBoard.getJSONObject(i);
            LinkedHashMap<Stop, Integer> line = getPassLineWithDeparture(nextDeparture.getJSONArray("passList"));

            String category = nextDeparture.getString("category");
            String number = nextDeparture.getString("number");
            String name = nextDeparture.getString("name");

            transports.add(new Transport(name, category, number, line));
        }

        LinkedHashMap<Transport, Integer> nextDeparture = new LinkedHashMap<>();
        for (Transport t: transports){
            nextDeparture.put(t, t.getNextDepartureTime().get());
        }

        return nextDeparture;
    }
}