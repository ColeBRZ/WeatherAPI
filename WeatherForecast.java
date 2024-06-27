import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * WeatherForecast contacts the OpenMeteo weather API for weather data
 * and prints out a weather report for the next 7 days.
 */
class WeatherForecast {
    public static void main(String[] args) {
        try {
            // The webURL and data parameters that will be passed to the API
            String webURL = "https://api.open-meteo.com/v1/forecast?";
            Map<String, String> dataParams = new HashMap<>();
            dataParams.put("latitude", "39.1653");
            dataParams.put("longitude", "-86.5264");
            dataParams.put("hourly", "temperature_2m");
            dataParams.put("temperature_unit", "fahrenheit");
            dataParams.put("timezone", "EST");

            // Building the data parameters onto the web URL
            StringBuilder baseUrl = new StringBuilder(webURL);
            for (String s : dataParams.keySet()) {
                baseUrl.append(String.format("%s=%s&", s, dataParams.get(s)));
            }

            // deletes unnecessary ampersand(&)
            baseUrl = baseUrl.deleteCharAt(baseUrl.length() - 1);

            // establishing url, http connection, and the request method
            URL url = new URL(baseUrl.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // response code error
            int resp = conn.getResponseCode();
            if (resp != 200) {
                throw new RuntimeException("main: response code " + resp + " was not 200.");
            } else {
                // Get data from connection stream
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuffer response = new StringBuffer();
                    String inputLine = null;
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    // Parsing the received data
                    JsonElement jsonElement = JsonParser.parseString(response.toString());
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonArray times = jsonObject.get("hourly")
                            .getAsJsonObject()
                            .get("time")
                            .getAsJsonArray();
                    JsonArray temps = jsonObject.get("hourly")
                            .getAsJsonObject()
                            .get("temperature_2m")
                            .getAsJsonArray();

                    // Printing weather report for the next 7 days
                    System.out.println("Bloomington 7-Day Forecast in Fahrenheit:");
                    for (int i = 0; i < 150; i=i+24) {
                        String dateAndTime = String.valueOf(times.get(i));
                        System.out.println("Forecast for " + dateAndTime.substring(1,11) + ":");
                        for (int j = i; j < 24+i; j = j + 3) {
                            String dateAndTime2 =  String.valueOf(times.get(j));
                            String time = dateAndTime2.substring(12, dateAndTime.length()-1);
                            String temp = String.valueOf(temps.get(j));
                            System.out.println(time + ": " + temp + "°F");
                        }
                    }
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();;
        }
    }
}
