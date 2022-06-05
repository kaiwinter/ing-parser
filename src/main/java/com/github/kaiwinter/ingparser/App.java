package com.github.kaiwinter.ingparser;

import com.github.kaiwinter.ingparser.ui.MainView;
import com.github.kaiwinter.ingparser.ui.MainViewModel;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

   @Override
   public void start(Stage stage) {
      stage.setTitle("ING CSV Parser (DE)");

      ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();

      Parent root = viewTuple.getView();
      stage.setScene(new Scene(root));
      stage.show();
   }

   public static void main(String[] args) {
      launch();
   }

}