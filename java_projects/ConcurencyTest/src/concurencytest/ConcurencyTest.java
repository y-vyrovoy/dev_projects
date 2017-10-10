/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concurencytest;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Yura Vyrovoy
 */
public class ConcurencyTest extends Application {
    
    private InterfaceController _controller; 
    
    @Override
    public void start(Stage primaryStage) {
        try
        {
            //Parent p = FXMLLoader.load(getClass().getResource("interface.fxml"));
            //Scene scene = new Scene(p);
            
            FXMLLoader fxmlLoader = new FXMLLoader();
            Pane pane = fxmlLoader.load(getClass().getResource("interface.fxml").openStream());
            Scene scene1 = new Scene(pane);

            primaryStage.setTitle("Conurency test");
            primaryStage.sizeToScene();
            primaryStage.setScene(scene1);

            _controller = (InterfaceController) fxmlLoader.getController();
            
            primaryStage.setOnCloseRequest((event) -> {
                if( _controller != null) {
                    _controller.finilize();
                }
            });
            
            primaryStage.show();
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
