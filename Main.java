// ***************************************
// Kaichun Zhong
// ***************************************


package Sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

            Parent root = FXMLLoader.load(getClass().getResource("MainStage.fxml"));
            primaryStage.setScene(new Scene(root));

            primaryStage.show();

    }
}
