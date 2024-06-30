package com.github.kaiwinter.ingparser;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;

import com.github.kaiwinter.ingparser.preferences.PreferenceStore;
import com.github.kaiwinter.ingparser.ui.MainView;
import com.github.kaiwinter.ingparser.ui.MainViewModel;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

   @Override
   public void start(Stage stage) {
      ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();

      Parent root = viewTuple.getView();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      stage.show();

      MainViewModel viewModel = viewTuple.getViewModel();
      scene.getWindow().setOnCloseRequest(event -> {
         if (!viewModel.handleExit()) {
            event.consume();
         }
      });

      StringExpression titleExpression = StringExpression
            .stringExpression(new ReadOnlyStringWrapper("ING CSV Parser (DE) - "))
            .concat(viewModel.currentParserFileProperty())
            .concat(Bindings.createStringBinding(
                  () -> Boolean.TRUE.equals(viewModel.parserConfigurationChangedProperty().getValue()) ? "*" : "",
                  viewModel.parserConfigurationChangedProperty()));
      stage.titleProperty().bind(titleExpression);

      String lastUsedParserFile = PreferenceStore.loadLastUsedParserFile();
      if (!StringUtils.isBlank(lastUsedParserFile)) {
         try {
            viewModel.loadParserFile(new File(lastUsedParserFile));
         } catch (FileNotFoundException e) {
            PreferenceStore.saveLastUsedParserFile("");
            Alert alert = new Alert(AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Die zuletzt verwendete Parser-Konfiguration konnte nicht mehr geöffnet werden.");
            alert.show();
         }
      }

      String lastUsedCsvFile = PreferenceStore.loadLastUsedCsvFile();
      if (!StringUtils.isBlank(lastUsedCsvFile)) {
         try {
            viewModel.loadCsvFile(new File(lastUsedCsvFile));
         } catch (FileNotFoundException e) {
            PreferenceStore.saveLastUsedCsvFile("");
            Alert alert = new Alert(AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Die zuletzt verwendete CSV-Datei konnte nicht mehr geöffnet werden.");
            alert.show();
         }
      }
   }

   public static void main(String[] args) {
      launch();
   }

}