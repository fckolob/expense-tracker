package com.expensetracker;

import com.expensetracker.config.DatabaseConfig;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseConfig.initializeDatabase();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/dashboard.fxml")
        );

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
        getClass().getResource("/css/dark-theme.css").toExternalForm());
        stage.setTitle("Expense Tracker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println(new java.io.File("expenses.db").getAbsolutePath());
        launch();
    }
}