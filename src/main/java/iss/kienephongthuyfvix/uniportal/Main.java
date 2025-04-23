package iss.kienephongthuyfvix.uniportal;

import iss.kienephongthuyfvix.uniportal.dao.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/iss/kienephongthuyfvix/uniportal/img/logo.png")));
        primaryStage.setResizable(false);
        primaryStage.setTitle("University Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        System.out.println("Application closing - performing cleanup");
        Database.closeDataSource();
    }
}