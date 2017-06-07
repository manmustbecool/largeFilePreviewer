/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package largefilepreviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 *
 * @author emiewag
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private TextField textFieldFileBox;

    @FXML
    private Label labelFileBox;

    @FXML
    private TextArea textAreaFileText;

    public FXMLDocumentController() {
        this.textFieldFileBox = new TextField();
        labelFileBox = new Label();
    }

    private final boolean printEventTriger = false;

    @FXML
    public void handleFileBoxOnDragOver(DragEvent event) {

        if (printEventTriger) {
            System.out.println("handleFileBoxOnDragOver");
        }

        Dragboard db = event.getDragboard();

        if (db.hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            // only get first file
            //File file = event.getDragboard().getFiles().get(0);
            for (File file : db.getFiles()) {
                String filePath = file.getAbsolutePath();
                // System.out.println(filePath);
                textFieldFileBox.setText(filePath);
            }
        }

        event.consume();
    }

    @FXML
    public void handleFileBoxOnDragExited(DragEvent event) {

        if (printEventTriger) {
            System.out.println("handleFileBoxOnDragExited");
        }

        // if did not drop the file, empty the FileBox.
        if (!event.isDropCompleted()) {
            textFieldFileBox.setText("");
        }

        event.consume();
    }

    @FXML
    public void handleFileBoxOnDragDropped(DragEvent event) {

        if (printEventTriger) {
            System.out.println("handleFileBoxOnDragDropped");
        }

        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            for (File file : db.getFiles()) {
                String filePath = file.getAbsolutePath();
                // System.out.println(filePath);
                textFieldFileBox.setText(filePath);
            }
            success = true;
        }
        // let the source know whether the file was successfully dropped
        event.setDropCompleted(success);
        event.consume();

        // Open the file
        try {
            FileInputStream fstream = new FileInputStream(textFieldFileBox.getText());
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            int lineCount = 0;
            String fileText = "";
            String strLine;
            //Read File Line By Line

            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                System.out.println(strLine);
                fileText = fileText + strLine;
                lineCount++;
                if (lineCount >= 50) {
                    break;
                }

            }

            //Close the input stream
            br.close();

            textAreaFileText.setText(fileText);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
