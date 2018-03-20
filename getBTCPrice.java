// ***************************************
// Kaichun Zhong
// ***************************************

package Sample;

import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class getBTCPrice {

    public float current_price = 0;
    public float daily_open_price = 0;
    public float daily_close_price = 0;
    public float daily_high = 0;
    public float daily_low = 0;
    public JsonArray api_content;
    public JsonArray api_minute_content;
    public JsonArray api_hour_content;
    public JsonArray api_day_conent;

    // APIs to read from
    String input_url_minute =
            "https://min-api.cryptocompare.com/data/histominute?aggregate=1&e=CCCAGG&extraParams=CryptoCompare&fsym=BTC&limit=20&tryConversion=false&tsym=USD";
    String input_url_hour =
            "https://min-api.cryptocompare.com/data/histohour?aggregate=1&e=CCCAGG&extraParams=CryptoCompare&fsym=BTC&limit=24&tryConversion=false&tsym=USD";
    String input_url_day =
            "https://min-api.cryptocompare.com/data/histoday?aggregate=1&e=CCCAGG&extraParams=CryptoCompare&fsym=BTC&limit=20&tryConversion=false&tsym=USD";


    private JsonArray readFromAPI(String url_to_read) throws Exception{

        URL url = new URL(url_to_read);

        // Connect to API
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonObject jobj;
        JsonParser jp = new JsonParser();

        try{
            // Parse the input and transfer to JsonArray
            jobj = (JsonObject) jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray data = (JsonArray) jobj.getAsJsonArray("Data");
            api_content = data;

        }catch (Exception e){
            System.out.println("Error in reading API");
        }

        return api_content;
    }

    // This method returns daily price in the past 20 days and get the highest and lowest
    // price as of today
    public void get_Day_Price(){

        try{

            api_day_conent = readFromAPI(input_url_day);
            JsonObject current_obj = (JsonObject) api_day_conent.get(api_day_conent.size()-1);
            daily_open_price = current_obj.get("open").getAsFloat();
            daily_close_price = current_obj.get("close").getAsFloat();
            daily_high = current_obj.get("high").getAsFloat();
            daily_low = current_obj.get("low").getAsFloat();
        // long current_time = current_obj.get("time").getAsLong();
        }catch (Exception e){
            System.out.println("Error when Getting daily current price");
        }

    }

    // This method returns the hourly price in the past 24 hours
    public void get_Hour_Price(){
        try{
            api_hour_content = readFromAPI(input_url_hour);
        }catch (Exception e){
            System.out.println("Error when getting hourly price");
        }
    }

    // This method returns the price in minute resolution in the past 20 minutes and the
    // price as of current minute
    public void get_Minute_Price(){
        try{
            api_minute_content = readFromAPI(input_url_minute);
            JsonObject current_price_obj = (JsonObject) api_minute_content.get(api_minute_content.size()-1);
            current_price = current_price_obj.get("close").getAsFloat();
        }catch (Exception e){
            System.out.println("Error in getting minutely price");
        }
    }



}
