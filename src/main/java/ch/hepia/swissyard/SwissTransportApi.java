package ch.hepia.swissyard;

/**
 * Classe Api executant les demandes de l'Api utilisée afin d'accèder aux informations nécéssaires
 * @author David Nogueiras Blanco
 * @author Matthias Boutay
 * @author Nicolas Paschoud
 * @author Jérôme Chételât
 * @version 0.1
 */

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

import java.util.Optional;

public class SwissTransportApi{

    String url = "http://transport.opendata.ch/v1/";

    public SwissTransportApi(String url){
        this.url = url;
    }
    
    /**
     * This function will try to get informations about the departures of a station
     * @param station Station to query
     * @param limit nb max elements of the query
     * @return An optional JsonArray if the query succeed
     */
    public Optional<JSONArray> stationboardQuery(String station, String limit){

        Optional<JSONArray> departures = Optional.empty();


        try {
            String stationBoardRoute = url + "stationboard";
            HttpResponse<JsonNode> stationBoard = Unirest.get(stationBoardRoute)
                    .queryString("station", station)
                    .queryString("limit", limit)
                    .asJson();

             departures = Optional.of(stationBoard.getBody().getObject().getJSONArray("stationboard"));

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return departures;

    }

    /**
     * This function will try to get informations about the location of a station (latitude and longitude)
     * @param station Station to query
     * @return An optional JsonArray if the query succeed
     */
    public Optional<JSONArray> locationQuery(String station){

        Optional<JSONArray> resultQuery = Optional.empty();

        try {
            String locationBoardRoute = url + "locations";
            HttpResponse<JsonNode> locationBoard = Unirest.get(locationBoardRoute)
                    .queryString("query", station)
                    .asJson();

             resultQuery = Optional.of(locationBoard.getBody().getObject().getJSONArray("stations"));


        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return resultQuery;

    }

}