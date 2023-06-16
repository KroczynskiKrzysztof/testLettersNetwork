import Basic.MultiLayerNetwork;
import Basic.Row;
import Basic.SigmoidPerceptron;
import Image.ImageTools;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PaintClone extends Application {
    private static final int CANVAS_SIZE = 100;
    private static final int CANVAS_SCALE = 1;
    private static int BRUSH_RADIUS = 4;
    private int[][] canvasData = new int[100][100];
    private double prevX = -1;
    private double prevY = -1;
    static MultiLayerNetwork multiLayerNetwork;

    static {
        try {
            multiLayerNetwork = MultiLayerNetwork.loadNetwork("SavedNetworks/network5002023-05-14 20-54-12.192.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        for (int[] canvasDatum : canvasData) {
            Arrays.fill(canvasDatum,-1);
        }
        Canvas canvas = new Canvas(CANVAS_SIZE * CANVAS_SCALE, CANVAS_SIZE * CANVAS_SCALE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_SIZE * CANVAS_SCALE, CANVAS_SIZE * CANVAS_SCALE);


        canvas.setOnMousePressed(event -> {
            double x = event.getX();
            double y = event.getY();
            double scaledX = x / CANVAS_SCALE;
            double scaledY = y / CANVAS_SCALE;
            if (scaledX >= 0 && scaledX < CANVAS_SIZE && scaledY >= 0 && scaledY < CANVAS_SIZE) {
                gc.beginPath();
                gc.setLineWidth(BRUSH_RADIUS);
                gc.moveTo(scaledX + BRUSH_RADIUS / 2.0, scaledY + BRUSH_RADIUS / 2.0);
                gc.setFill(Color.BLACK);
                gc.fillRect(scaledX, scaledY, BRUSH_RADIUS, BRUSH_RADIUS);
                for (int i = ((int)x-.5*BRUSH_RADIUS>=0)? (int) ( x - .5 * BRUSH_RADIUS) :0; i < (((int)x+.5*BRUSH_RADIUS<=990)? (int) ( x + .5 * BRUSH_RADIUS):99) ; i++) {
                    for (int j = ((int)y-.5*BRUSH_RADIUS>=0)? (int) ( y - .5 * BRUSH_RADIUS) :0; j < (((int)y+.5*BRUSH_RADIUS<=990)? (int) ( y + .5 * BRUSH_RADIUS):99) ; j++) {
                        canvasData[(int) j][(int) i] = 0;
                    }
                }
                prevX = scaledX;
                prevY = scaledY;
            }
        });


        canvas.setOnMouseDragged(event -> {
            double x = event.getX();
            double y = event.getY();

            if (x >= 0 && x < CANVAS_SIZE && y >= 0 && y < CANVAS_SIZE) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(BRUSH_RADIUS);
                gc.lineTo(x + BRUSH_RADIUS / 2.0, y + BRUSH_RADIUS / 2.0);
                gc.stroke();
                for (int i = ((int)x-.5*BRUSH_RADIUS>=0)? (int) ( x - .5 * BRUSH_RADIUS) :0; i < (((int)x+.5*BRUSH_RADIUS<=990)? (int) ( x + .5 * BRUSH_RADIUS):99) ; i++) {
                    for (int j = ((int)y-.5*BRUSH_RADIUS>=0)? (int) ( y - .5 * BRUSH_RADIUS) :0; j < (((int)y+.5*BRUSH_RADIUS<=990)? (int) ( y + .5 * BRUSH_RADIUS):99) ; j++) {
                        canvasData[(int) j][(int) i] = 0;
                    }
                }

            }
        });
        Label[] letterLabels = new Label[26];
        BorderPane canvasBox = new BorderPane(canvas);
        canvasBox.setBackground(Background.fill(Paint.valueOf("darkgrey")));
        BorderPane root = new BorderPane(canvasBox);

        Label resultLabel = new Label(Arrays.toString(new double[26]).replace(",","\n").replace("[","").replace("]","").replace(" ",""));



        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> {

            for (Label letterLabel : letterLabels) {
                letterLabel.setBackground(Background.fill(Paint.valueOf("white")));
            }


        Double[] result = multiLayerNetwork.askAllLayersRaw(ImageTools.getSamples(canvasData,10));
            letterLabels[MultiLayerNetwork.findMaxIndex(result)].setBackground(Background.fill(Paint.valueOf("lightgreen")));
        resultLabel.setText(Arrays.toString(result).replace(",","\n").replace("[","").replace("]","").replace(" ",""));
        primaryStage.sizeToScene();

        });





        Button increaseBrushButton = new Button("+");
        increaseBrushButton.setOnAction(event -> BRUSH_RADIUS++);
        Button decreaseBrushButton = new Button("-");
        decreaseBrushButton.setOnAction(event -> {
            if (BRUSH_RADIUS>1) BRUSH_RADIUS -= 1;
        });
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(confirmButton,increaseBrushButton,decreaseBrushButton);

        VBox buttonBoxBox = new VBox(buttonBox);
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> {
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, CANVAS_SIZE * CANVAS_SCALE, CANVAS_SIZE * CANVAS_SCALE);
            canvasData=new int[100][100];
            for (int[] canvasDatum : canvasData) {
                Arrays.fill(canvasDatum,-1);
            }
        });
        VBox alphabetBox = new VBox();
        String[] alphabet = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" ");

        for (int i = 0; i < alphabet.length; i++) {
            letterLabels[i]=new Label(alphabet[i]);
        }
        alphabetBox.getChildren().addAll(letterLabels);
        buttonBoxBox.getChildren().addAll(clearButton,new HBox(alphabetBox,resultLabel));
        root.setBottom(buttonBoxBox);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Paint Clone");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
