package com.github.kaiwinter.ingparser.ui;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.config.FilterCriterion.MatchingCriterion;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

public class NewFilterCriterionView implements FxmlView<NewFilterCriterionViewModel>, Initializable {

   @InjectViewModel
   private NewFilterCriterionViewModel viewModel;

   private Dialog<FilterCriterion> dialog;

   @FXML
   private DialogPane dialogPane;

   @FXML
   private ComboBox<CategoryModel> categories;

   @FXML
   private ComboBox<MatchingCriterion> criteria;

   @FXML
   private TextField pattern;

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      dialog = new Dialog<>();
      dialog.setTitle("Create new Filter Criterion");
      dialog.setDialogPane(dialogPane);

      categories.itemsProperty().bind(viewModel.categoriesProperty());
      criteria.itemsProperty().bind(viewModel.matchingCriteriaProperty());
      categories.setCellFactory(param -> new CategoryModelListCell());
      categories.setButtonCell(new CategoryModelListCell());

      Node okButton = dialogPane.lookupButton(ButtonType.OK);
      okButton.disableProperty().bind(categories.getSelectionModel().selectedItemProperty().isNull() //
            .or(criteria.getSelectionModel().selectedItemProperty().isNull()) //
            .or(pattern.textProperty().isEmpty()));

      dialog.setResultConverter(buttonType -> {

         if (buttonType == ButtonType.CANCEL) {
            return null;
         }

         if (criteria.getValue() == MatchingCriterion.AUFTRAGGEBER) {
            return FilterCriterion.byAuftraggeber(categories.getValue(), pattern.getText()).get(0);
         } else if (criteria.getValue() == MatchingCriterion.VERWENDUNGSZWECK) {
            return FilterCriterion.byVerwendungszweck(categories.getValue(), pattern.getText()).get(0);
         } else {
            throw new IllegalArgumentException("Unknown type");
         }
      });

      Platform.runLater(() -> categories.requestFocus());
   }

   /**
    * Shows the dialog and returns the result when the user confirms the dialog.
    *
    * @return a valid {@link FilterCriterion} or an empty optional
    */
   public Optional<FilterCriterion> showAndWait() {
      return dialog.showAndWait();
   }
}
