package lab2.MainGUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lab2.Helpers.Helper;
import lab2.Helpers.MedianCutQuantization;
import lab2.Helpers.OrderedDithering;
import lab2.Helpers.PopularityAlgQuantization;
import lab2.ImageChooserGUI.ImageChooserGUI;
import lab2.Main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import static javafx.embed.swing.SwingFXUtils.fromFXImage;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

public class Controller {
    public static Image image;
    public static BufferedImage workingImage;

    @FXML
    public ImageView mainImageView;
    @FXML
    public TextField matrixSize;
    @FXML
    public TextField levelsOfGray;
    @FXML
    public TextField sizeColPalette;

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

    public void transformOrdDithering(ActionEvent actionEvent) {
        int mSize = 2;
        int lvlGray = 4;
        try {
            mSize = Integer.parseInt(matrixSize.getText());
            lvlGray = Integer.parseInt(levelsOfGray.getText());
        } catch (NumberFormatException ex) {
            Alert alr = new Alert(Alert.AlertType.ERROR, "Wrong value!", ButtonType.OK);
            alr.show();
            return;
        }

        if (image == null) {
            Alert alr = new Alert(Alert.AlertType.ERROR, "There is no image to process!", ButtonType.OK);
            alr.show();
        } else {
            BufferedImage workCpy = Helper.copyBufferedImage(workingImage);
            int[][] ditherMatrix = OrderedDithering.prepareMatrix(mSize);
            workingImage = OrderedDithering.orderedDithering(ditherMatrix, workCpy, Helper.genGrayLevels(lvlGray));
            image = toFXImage(workingImage, null);
            mainImageView.setImage(image);
        }

    }

    public void transformColorQuant(ActionEvent actionEvent) {
        int sizeCol = 8;
        try {
            sizeCol = Integer.parseInt(sizeColPalette.getText());
        } catch (NumberFormatException ex) {
            Alert alr = new Alert(Alert.AlertType.ERROR, "Wrong value!", ButtonType.OK);
            alr.show();
            return;
        }

        if (image == null) {
            Alert alr = new Alert(Alert.AlertType.ERROR, "There is no image to process!", ButtonType.OK);
            alr.show();
        } else {
            BufferedImage workCpy = Helper.copyBufferedImage(workingImage);
            workingImage = PopularityAlgQuantization.performQuantization(workCpy, PopularityAlgQuantization.prepareColorArray(workCpy, sizeCol));
            image = toFXImage(workingImage, null);
            mainImageView.setImage(image);
        }
    }
}