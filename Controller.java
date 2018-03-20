// ***************************************
// Kaichun Zhong
// ***************************************


package Sample;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class Controller {


    getBTCPrice my_price;
    ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);


    @FXML // fx:id = "currentprice_label";
            Label currentprice_label;
    @FXML // fx:id = "pricechange_label";
            Label pricechange_label;
    @FXML // fx:id = "highprice_label";
            Label highprice_label;
    @FXML // fx:id = "lowprice_label"
            Label lowprice_label;
    @FXML // fx:id = "openprice_label"
            Label openprice_label;
    @FXML // fx:id = "day_chart";
            LineChart<String, Number> day_chart;
    @FXML //fx:id = "minute_chart";
            LineChart<String, Number> minute_chart;
    @FXML // fx:id = "hour_chart"
            LineChart<String, Number> hour_chart;



    public Controller(){
        my_price = new getBTCPrice();
        Controller ctrl = this;
        my_price.get_Day_Price();

        // Refresh the price information every minute
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ctrl.refreshPrice();
                System.out.println("It's running");
            }
        },0,1, TimeUnit.MINUTES);

    }

    // Get current price from minute api content and refresh all data and charts
    public void refreshPrice(){

        CompletableFuture<Float> future = new CompletableFuture<Float>();
        future.supplyAsync(() -> {
            // Get day price for setting labels, get hourly price for the hour_chart
            my_price.get_Day_Price();
            my_price.get_Hour_Price();
            my_price.get_Minute_Price();
            Platform.runLater(()->{
                setLabel();
                load_Day_Chart();
                load_Hour_Chart();
                load_Minute_Chart();
            });
            System.out.println("refreshed");
            return my_price.current_price;
        });
    }


    private void setLabel(){
        float _change;

        // Calculate the price change within the past 24 hours
        _change = ((my_price.current_price - my_price.daily_open_price)/ my_price.daily_open_price)*100;
        String price_change = String.format("%.2f%%", _change);

        // Set the label to display price information
        currentprice_label.setText(String.format("%.2f", my_price.current_price));
        highprice_label.setText(String.format("%.2f", my_price.daily_high));
        lowprice_label.setText(String.format("%.2f", my_price.daily_low));
        openprice_label.setText(String.format("%.2f", my_price.daily_open_price));
        pricechange_label.setText(price_change);
    }


    // This method loads the line chart under different tabs accordingly
    private void load_chart(JsonArray api_content_to_load, LineChart chart){

        Date date;
        String date_to_use;
        long time_stamp;

        // Defining the series
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        ObservableList<String> time_series = FXCollections.observableArrayList();

        // Reset the chart
        chart.getData().clear();

        try {
            XYChart.Series<String, Number> xy_series = new XYChart.Series<String, Number>();
            ObservableList<String> xy_time_series = FXCollections.observableArrayList();
            for (JsonElement o : api_content_to_load) {
                JsonObject obj = (JsonObject) o;
                time_stamp = obj.get("time").getAsLong()*1000;
                SimpleDateFormat df = new SimpleDateFormat( "YYYY-MM-dd HH:mm");
                date = new Date(time_stamp);
                date_to_use = df.format(date);
                // date_to_use = date.toString();
                xy_time_series.add(date_to_use);
                Double price_axis = obj.get("close").getAsDouble();
                xy_series.getData().add(new XYChart.Data<String, Number>(date_to_use , price_axis));

            }
            series = xy_series;
            time_series = xy_time_series;
        }catch (Exception e){
            System.out.println("chart error");
        }

        // Config the axes
        series.setName("Price");
        NumberAxis y_axis =  (NumberAxis) chart.getYAxis();
        y_axis.setLabel("Price");
        y_axis.setForceZeroInRange(false);
        chart.getData().add(series);
        CategoryAxis x_axis = (CategoryAxis) chart.getXAxis();
        x_axis.setLabel("Time");
        x_axis.setAutoRanging(true);
        x_axis.setCategories(FXCollections.observableArrayList(time_series));
        x_axis.setTickLabelRotation(90);
    }

    public void load_Minute_Chart(){
        load_chart(my_price.api_minute_content, minute_chart);
    }

    public void load_Hour_Chart(){

        load_chart(my_price.api_hour_content, hour_chart);
    }

    public void load_Day_Chart(){
        load_chart(my_price.api_day_conent, day_chart);
    }

}
