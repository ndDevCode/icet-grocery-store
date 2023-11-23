package controller;

import com.jfoenix.controls.JFXButton;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DashboardFormController {

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Label lblTime;

    @FXML
    private JFXButton btnToggle;

    @FXML
    private JFXButton btnCustomerView;

    @FXML
    private JFXButton btnItemsView;

    @FXML
    private JFXButton btnPlaceOrder;

    @FXML
    private AnchorPane contentAnchorPane;

    @FXML
    private AnchorPane sidePane;

    @FXML
    private Circle circleClose;

    @FXML
    private Circle circleMinimize;

    @FXML
    private Circle circleMaximize;

    @FXML
    private AnchorPane paneHeader;
    private Stage stage;
    private double xOffset;
    private double yOffset;

    public void initialize() {

        //---- Setting up the date and timer on the app heading

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                lblTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EE, dd LLLL yyyy hh:mm a")));
            }
        };
        timer.start();

        //---- SidePane Slide in/out Animation
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.8), sidePane);
        sidePane.setTranslateX(-185);

        // Set action on toggle button
        btnToggle.setOnAction(event -> {
            if (sidePane.getTranslateX() < 0) {
                translateTransition.setToX(0);
                translateTransition.play();
            } else {
                translateTransition.setToX(-185);
                translateTransition.play();
            }
        });

        // Set action on Mouse move out >> slide out
        sidePane.setOnMouseExited(mouseEvent -> {
            translateTransition.setToX(-185);
            translateTransition.play();
        });

        // Load customer view pane on customer view button onAction event
        btnCustomerView.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    contentAnchorPane.
                            getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull
                                    (getClass().getResource("/view/CustomerView.fxml"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Load item view pane on item view button onAction event
        btnItemsView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    contentAnchorPane.
                            getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull
                                    (getClass().getResource("/view/ItemView.fxml"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnPlaceOrder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    contentAnchorPane.
                            getChildren().setAll((Node) FXMLLoader.load(Objects.requireNonNull
                                    (getClass().getResource("/view/PlaceOrderView.fxml"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        circleClose.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        circleMinimize.addEventHandler
                (MouseEvent.MOUSE_CLICKED, event -> ((Stage) mainAnchorPane.getScene().getWindow()).setIconified(true));

    }

    @FXML
    void paneOnMouseEntered(){
        stage = (Stage) mainAnchorPane.getScene().getWindow();
        paneHeader.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        paneHeader.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }

}

