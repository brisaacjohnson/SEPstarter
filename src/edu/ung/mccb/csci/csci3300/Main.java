package edu.ung.mccb.csci.csci3300;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/edu/ung/mccb/csci/csci3300/view/register.fxml"));
        primaryStage.setTitle("CSCI3300 Starter Project");
        primaryStage.setScene(new Scene(root, 425, 400));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
