/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package largefilepreviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

/**
 *
 * @author emiewag
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField textFieldFileBox;

    @FXML
    private TextArea textAreaFileText;

    @FXML
    private TableView tableViewFileText;

    @FXML
    private TextField textFieldCsvSeparator;

    public FXMLDocumentController() {
        this.textFieldFileBox = new TextField();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        System.out.print("init");
        textFieldCsvSeparator.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {

                System.out.print("TextField Text Changed (newValue: " + newValue + ")\n");

                if (textFieldFileBox.getText() != null && !textFieldFileBox.getText().isEmpty()) {
                    readFile();
                    readCSVFile();
                }
            }
        });
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

        readFile();
        readCSVFile();

    }

    private TableColumn<ObservableList<StringProperty>, String> createColumn(
            final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        String title;
        if (columnTitle == null || columnTitle.trim().length() == 0) {
            title = "Column " + (columnIndex + 1);
        } else {
            title = columnTitle;
        }
        column.setText(title);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(
                    CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures) {
                ObservableList<StringProperty> values = cellDataFeatures.getValue();
                if (columnIndex >= values.size()) {
                    return new SimpleStringProperty("");
                } else {
                    return cellDataFeatures.getValue().get(columnIndex);
                }
            }
        });
        return column;
    }

    private void readCSVFile() {

        if (textFieldCsvSeparator.getText().isEmpty()) {
            return;
        }

        try {
            FileInputStream fstream = new FileInputStream(textFieldFileBox.getText());
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            int lineCount = 0;

            tableViewFileText.getItems().clear();
            tableViewFileText.getColumns().clear();

            String strLine;
            //Read File Line By Line
            if ((strLine = br.readLine()) != null) {
                String[] headerValues = strLine.split(textFieldCsvSeparator.getText());
                for (int column = 0; column < headerValues.length; column++) {
                    tableViewFileText.getColumns().add(createColumn(column, headerValues[column]));
                }
            }

            while ((strLine = br.readLine()) != null) {
                // Add data to table:
                ObservableList<StringProperty> data = FXCollections.observableArrayList();

                String[] dataValues = strLine.split(textFieldCsvSeparator.getText());
                for (String value : dataValues) {
                    data.add(new SimpleStringProperty(value));
                }
                tableViewFileText.getItems().add(data);
                lineCount++;
                if (lineCount >= 50) {
                    break;
                }

            }

            //Close the input stream
            br.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void readFile() {
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
                fileText = fileText + strLine + "\n";
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

}
