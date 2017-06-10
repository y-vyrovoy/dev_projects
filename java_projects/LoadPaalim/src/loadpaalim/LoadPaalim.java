/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadpaalim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author vyrovoy
 */
public class LoadPaalim extends Application {
    
    @Override
    public void start(Stage primaryStage) {

        try{
            Parent p = FXMLLoader.load(getClass().getResource("Interface.fxml"));
            Scene scene = new Scene(p);

            primaryStage.setTitle("Download verbs");
            primaryStage.sizeToScene();
            primaryStage.setScene(scene);

            primaryStage.show();
            
        }catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
            
    
}
