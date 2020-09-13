package ch.hepia.swissyard;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Providing a way to get an API key from a file called ".environment.json" only using a singleton
 * @author J??r??me Ch??telat
 */
public class AppApiKeys {
    /**
     * Reads the file
     * @author J??r??me Ch??telat
     */
    private static class ApiKeys {
        private String gmapsApiKey;
        private String tpgApiKey;

        public ApiKeys() {
            readEnvironnment();
        }

        /**
         * Gets keys from the json
         */
        private void readEnvironnment() {
            String jsonString = "{}";
            try {
                jsonString = new String(Files.readAllBytes(Paths.get(getClass().getResource("/.environnment.json").toURI())));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
            JSONObject keys = new JSONObject(jsonString);
            this.gmapsApiKey = keys.getString("gmapApiKey");
            this.tpgApiKey = keys.getString("tpgApiKey");
        }

        String getTPGKey() {
            return this.tpgApiKey;
        }

        String getMapsKey() {
            return this.gmapsApiKey;
        }
    }

    private static ApiKeys singleton;

    private static ApiKeys getInstance() {
        if(AppApiKeys.singleton == null)
            AppApiKeys.singleton = new ApiKeys();
        return AppApiKeys.singleton;
    }

    public static String getTPGApiKey() {
        return AppApiKeys.getInstance().getTPGKey();
    }

    public static String getGMAPSApiKey() {
        return AppApiKeys.getInstance().getMapsKey();
    }
}