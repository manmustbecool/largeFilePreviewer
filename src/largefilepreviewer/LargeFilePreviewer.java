/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package largefilepreviewer;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 */
public class LargeFilePreviewer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        
        
        // Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        FXMLLoader loader = new FXMLLoader(LargeFilePreviewer.class.getResource("FXMLDocument.fxml"));
			Parent root = (Parent) loader.load();
        
        
        Scene scene = new Scene(root);
       
        
        stage.setScene(scene);
        stage.setTitle("Large File Previewer");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
