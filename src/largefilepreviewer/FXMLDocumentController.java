/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package largefilepreviewer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;
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
    
    @FXML
    private Label labelCsvTotalColumns;

    public FXMLDocumentController() {
        this.textFieldFileBox = new TextField();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        System.out.println("init");
        textFieldCsvSeparator.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {

                System.out.println("TextField Text Changed (newValue: " + newValue + ")");

                if (textFieldFileBox.getText() != null && !textFieldFileBox.getText().isEmpty()) {
                    readFile();
                    readCSVFile();
                }
            }
        });
    }

    private static final boolean debug_printEventTriger = false;
    
    // a char represents a character in Java. It is 2 bytes large
    // 1024*1024/2 will read 0.5 MB data
    private static final int BUFFER_SIZE = 1024*1024/2;

    @FXML
    public void handleFileBoxOnDragOver(DragEvent event) {

        if (debug_printEventTriger) {
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

        if (debug_printEventTriger) {
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

        if (debug_printEventTriger) {
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

    private String getFileExtension(String path) {
    try {
        return path.substring(path.lastIndexOf(".") + 1);
    } catch (Exception e) {
        return "";
    }
}
    
    private InputStream getInputStream() throws Exception{
        
        String path = textFieldFileBox.getText();
        String ext = getFileExtension(path);
        
        if(ext.equals("gz")){
            FileInputStream fis = new FileInputStream(path);
            GZIPInputStream gis = new GZIPInputStream(fis);
            return gis;
        }else{
            FileInputStream fis = new FileInputStream(path);
            return fis;
        }
        
    }
    
    
    /**
     * read CSV file for table viewer
     */
    private void readCSVFile() {

        if (textFieldCsvSeparator.getText().isEmpty()) {
            return;
        }
        
        try {
        
            InputStream is = getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int lineCount = 0;

            tableViewFileText.getItems().clear();
            tableViewFileText.getColumns().clear();
            
            // pre check the file
            // if one line is too long , just exit the function
            br.mark(BUFFER_SIZE);
            // max read buffer
            char[] buffer = new char[BUFFER_SIZE];
            br.read(buffer);
            String fileText = new String(buffer);
            if(!fileText.contains("\n")){
                return;
            }
            
            // reset the mark and read from begining
            br.reset();

            String strLine;
            //Read File Line By Line
            if ((strLine = br.readLine()) != null) {
                String[] headerValues = strLine.split(textFieldCsvSeparator.getText());
                for (int column = 0; column < headerValues.length; column++) {
                    tableViewFileText.getColumns().add(createColumn(column, headerValues[column]));
                }
                labelCsvTotalColumns.setText(headerValues.length+"");
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

    /**
     *  read text file for text area viewer
     */
    private void readFile() {
        // Open the file
        try {
            InputStream is = getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // max read buffer
            char[] buffer = new char[BUFFER_SIZE];
            br.read(buffer);
            String fileText = new String(buffer);

            //Close the input stream
            br.close();
            textAreaFileText.setText(fileText);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
