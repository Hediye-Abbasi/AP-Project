package com.shoppingmall;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the Shopping Mall desktop application.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Bootstrap JavaFX</li>
 *     <li>Load the initial view (login screen)</li>
 *     <li>Apply the global stylesheet</li>
 * </ul>
 * This class intentionally contains no business logic to respect the Single Responsibility Principle.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

        primaryStage.setTitle("Shopping Mall");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



