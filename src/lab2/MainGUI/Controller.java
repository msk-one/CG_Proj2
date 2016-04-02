package lab2.MainGUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lab2.ImageChooserGUI.ImageChooserGUI;
import lab2.Main;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

public class Controller {
    public static Image image;
    public static BufferedImage workingImage;

    @FXML
    public ImageView mainImageView;

    @FXML
    public void initialize() {

    }

    public void showAbout(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../AboutGUI/AboutGUI.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("About app");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root1, 300, 300));
        stage.show();
    }

    public void quitApp(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void openImage(ActionEvent actionEvent) throws IOException {
        ImageChooserGUI imgCh = new ImageChooserGUI(Main.mainStage);
        if (imgCh.file != null) {
            image = new Image(imgCh.file.toURI().toString());
            workingImage = fromFXImage(image, null);
            mainImageView.setImage(image);
        }
    }

    public void closeImage(ActionEvent actionEvent) {
        workingImage = null;
        image = null;
        mainImageView.setImage(null);
    }

    public void showCustomFilterDialog(ActionEvent actionEvent) throws IOException {
    }

    public void transformUsingConvFilter(ActionEvent actionEvent) {
    }

    public void transformUsingFuncFilter(ActionEvent actionEvent) {
    }
}