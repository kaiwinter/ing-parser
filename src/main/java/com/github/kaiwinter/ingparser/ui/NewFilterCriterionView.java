package com.github.kaiwinter.ingparser.ui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.config.FilterCriterion.MatchingCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
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

   private Dialog<List<FilterCriterion>> dialog;

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
      dialog.setTitle("Neues Filterkriterium");
      dialog.setDialogPane(dialogPane);

      categories.itemsProperty().bind(viewModel.categoriesProperty());
      criteria.itemsProperty().bind(viewModel.matchingCriteriaProperty());
      categories.setCellFactory(param -> new CategoryModelListCell());
      categories.setButtonCell(new CategoryModelListCell());

      pattern.disableProperty().bind(criteria.valueProperty().isEqualTo(MatchingCriterion.IDENTITY));

      Node okButton = dialogPane.lookupButton(ButtonType.OK);
      okButton.disableProperty().bind(categories.getSelectionModel().selectedItemProperty().isNull() //
            .or(criteria.getSelectionModel().selectedItemProperty().isNull()) //
            .or(pattern.textProperty().isEmpty()) //
            .or(pattern.textProperty().isEqualTo(FilterCriterion.UNMATCHED_CRITERION.getCategory().getName())));

      // Suggest pattern of selected booking
      criteria.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
               if (viewModel.bookingsProperty().isEmpty()) {
                  return;
               }

               Booking firstBooking = viewModel.bookingsProperty().getFirst();
               if (newValue == MatchingCriterion.AUFTRAGGEBER) {
                  pattern.setText(firstBooking.getAuftraggeber());
               } else if (newValue == MatchingCriterion.VERWENDUNGSZWECK) {
                  pattern.setText(firstBooking.getVerwendungszweck());
               } else if (newValue == MatchingCriterion.NOTIZ) {
                  pattern.setText(firstBooking.getNotiz());
               } else if (newValue == MatchingCriterion.IDENTITY) {
                  String identities = viewModel.bookingsProperty().stream().map(Booking::calculateIdentity).collect(Collectors.joining(", "));
                  pattern.setText(identities);
               } else {
                  throw new IllegalArgumentException("Unknown type: " + newValue);
               }

            });

      dialog.setResultConverter(buttonType -> {

         if (buttonType == ButtonType.CANCEL) {
            return null;
         }

         if (criteria.getValue() == MatchingCriterion.AUFTRAGGEBER) {
            return FilterCriterion.byAuftraggeber(categories.getValue(), pattern.getText());
         } else if (criteria.getValue() == MatchingCriterion.VERWENDUNGSZWECK) {
            return FilterCriterion.byVerwendungszweck(categories.getValue(), pattern.getText());
         } else if (criteria.getValue() == MatchingCriterion.NOTIZ) {
            return FilterCriterion.byNotiz(categories.getValue(), pattern.getText());
         } else if (criteria.getValue() == MatchingCriterion.IDENTITY) {
            return viewModel.bookingsProperty().stream().map(booking -> FilterCriterion.byIdentity(categories.getValue(), booking.calculateIdentity()).getFirst()).toList();
         } else {
            throw new IllegalArgumentException("Unknown type: " + criteria.getValue());
         }
      });

      Platform.runLater(() -> criteria.getSelectionModel().select(MatchingCriterion.IDENTITY));

      Platform.runLater(() -> categories.requestFocus());
   }

   /**
    * Shows the dialog and returns the result when the user confirms the dialog.
    *
    * @return a valid {@link FilterCriterion} or an empty optional
    */
   public Optional<List<FilterCriterion>> showAndWait() {
      return dialog.showAndWait();
   }
}
