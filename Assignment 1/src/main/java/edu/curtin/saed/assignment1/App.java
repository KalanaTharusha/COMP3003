package edu.curtin.saed.assignment1;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App extends Application 
{
    public static void main(String[] args) 
    {
        launch();        
    }

    @Override
    public void start(Stage stage) 
    {
        stage.setTitle("Example App (JavaFX)");
        JFXArena arena = new JFXArena();

        arena.start();
        arena.buildWall();
        arena.addListener((x, y) ->
        {
            System.out.println("Arena click at (" + x + "," + y + ")");
        });

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        ToolBar toolbar = new ToolBar();
        Label label = new Label("SCORE: " + 0);
        Label label2 = new Label("WALLS: " + 0 + "/10");

        toolbar.getItems().addAll(label, label2);

        TextArea logger = new TextArea();
        logger.appendText("Game Started\n");

        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                label.setText("SCORE: " + arena.score);
                label2.setText("WALLS: " + arena.wallCount + "/10");
            });
        }, 0, 1, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                try {
                    if(!arena.logBlockingQueue.isEmpty()){
                        String message = arena.logBlockingQueue.take();
                        logger.appendText(message + "\n");
                    }
                } catch (InterruptedException e) {
                    throw new UnsupportedOperationException(e);
                }
            });
        }, 0, 40, TimeUnit.MILLISECONDS);
        
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(arena, logger);
        arena.setMinWidth(300.0);
        
        BorderPane contentPane = new BorderPane();
        contentPane.setTop(toolbar);
        contentPane.setCenter(splitPane);

        Duration duration = Duration.millis(40.0);
        KeyFrame keyFrame = new KeyFrame(duration, event -> arena.setRobotPosition());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        Scene scene = new Scene(contentPane, 800, 800);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            arena.setGameOver();
            scheduler.shutdown();
        });
    }
}
