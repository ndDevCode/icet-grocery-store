import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        Class.forName("db.DBConnection");
        Class.forName("controller.CustomerViewController");
        Class.forName("controller.ItemViewController");
        Class.forName("controller.PlaceOrderController");

        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/DashboardForm.fxml"))));
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Grocery Store");
        primaryStage.getIcons().add(new Image("assets/appLogo.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
