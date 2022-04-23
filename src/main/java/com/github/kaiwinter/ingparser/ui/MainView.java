package com.github.kaiwinter.ingparser.ui;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.github.kaiwinter.ingparser.config.ConfigurationService;
import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class MainView implements FxmlView<MainViewModel>, Initializable {

   @InjectViewModel
   private MainViewModel viewModel;

   @FXML
   private ListView<CategoryModel> categoryList;

   @FXML
   private TableView<Booking> bookingsTable;
   @FXML
   private TableColumn<Booking, BigDecimal> betragColumn;
   @FXML
   private TableColumn<Booking, LocalDate> dateColumn;
   @FXML
   private TableColumn<Booking, String> auftraggeberColumn;
   @FXML
   private TableColumn<Booking, String> verwendungszweckColumn;

   @FXML
   private ListView<FilterCriterion> criteriaList;

   @FXML
   private Label leftStatusLabel;
   @FXML
   private Label rightStatusLabel;

   @FXML
   private Button newMainCategoryButton;
   @FXML
   private Button newSubCategoryButton;

   @FXML
   private Button newFilterCriterionButton;
   @FXML
   private Button removeFilterCriterionButton;

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      newFilterCriterionButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {

         CategoryModel selectedItem = categoryList.getSelectionModel().getSelectedItem();
         if (selectedItem == null) {
            return true;
         }
         return !FilterCriterion.NULL_CRITERION.getCategory().getName().equals(selectedItem.getName());
      }, categoryList.getSelectionModel().selectedItemProperty()));

      removeFilterCriterionButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
         ObservableList<Booking> selectedItems = bookingsTable.getSelectionModel().getSelectedItems();
         if (selectedItems.isEmpty()) {
            return true;
         }
         return selectedItems.stream().anyMatch(booking -> {
            if (booking.getMatchedCriteria().isEmpty()) {
               return true;
            }
            return booking.getMatchedCriteria().stream()
                  .anyMatch(filterCriterion -> filterCriterion == FilterCriterion.NULL_CRITERION);

         });
      }, bookingsTable.getSelectionModel().selectedItemProperty()));

      // click listener
      newMainCategoryButton.setOnAction(__ -> {
         CategoryModel newCategory = new CategoryModel("NEU");
         viewModel.categoriesProperty().add(newCategory);
      });

      newFilterCriterionButton.setOnAction(__ -> {
         ViewTuple<NewFilterCriterionView, NewFilterCriterionViewModel> viewTuple = FluentViewLoader
               .fxmlView(NewFilterCriterionView.class).load();

         // don't show "unmatched" category in dialog
         List<CategoryModel> my = new ArrayList<>(viewModel.categoriesProperty().getValue());
         my.remove(FilterCriterion.NULL_CRITERION.getCategory());
         viewTuple.getViewModel().categoriesProperty().setValue(FXCollections.observableArrayList(my));
         Optional<FilterCriterion> newFilterCriterion = viewTuple.getCodeBehind().showAndWait();
         if (newFilterCriterion.isEmpty()) {
            return;
         }
         CategoryModel selected = categoryList.getSelectionModel().getSelectedItem();
         viewModel.getFilterCriteriaFromFile().add(newFilterCriterion.get());
         new ConfigurationService().saveFilterCriteriaToFile(viewModel.getFilterCriteriaFromFile());
         viewModel.applyFilterCriteriaOnBookings();

         categoryList.getSelectionModel().select(selected);
      });

      removeFilterCriterionButton.setOnAction(__ -> {
         CategoryModel selected = categoryList.getSelectionModel().getSelectedItem();

         viewModel.getFilterCriteriaFromFile().removeAll(criteriaList.getItems());
         viewModel.applyFilterCriteriaOnBookings();

         categoryList.getSelectionModel().select(selected);
      });

      // List click listener
      categoryList.getSelectionModel().selectedItemProperty().addListener((__1, __2, newValue) -> {

         viewModel.refreshBookingTable(newValue);
         if (newValue == null) {
            newSubCategoryButton.setDisable(true);
         } else {
            newSubCategoryButton.setDisable(newValue.getParentCategoryName() != null);
         }
      });

      // Table click listener
      bookingsTable.getSelectionModel().selectedItemProperty().addListener((__1, __2, newValue) -> {
         ObservableList<Booking> selected = bookingsTable.getSelectionModel().getSelectedItems();
         viewModel.refreshFilterCriteriaList(selected);
      });

      bookingsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      // Model of List and Table
      viewModel.categoriesProperty().bind(categoryList.itemsProperty());
      viewModel.bookingsOfSelectedCategoryProperty().bind(bookingsTable.itemsProperty());
      viewModel.filterCriteriaOfSelectedBookingProperty().bind(criteriaList.itemsProperty());

      // List
      categoryList.setCellFactory(param -> new CategoryModelListCell());

      criteriaList.setCellFactory(param -> new LambdaListCell<FilterCriterion>(
            crit -> crit.getMatchingCriterion().toString() + ": " + crit.getPattern()));

      // Table cells
      betragColumn.setCellValueFactory(column -> getValue(column.getValue().getBetrag()));
      dateColumn.setCellValueFactory(column -> getValue(column.getValue().getDate()));
      auftraggeberColumn.setCellValueFactory(column -> getValue(column.getValue().getAuftraggeber()));
      verwendungszweckColumn.setCellValueFactory(column -> getValue(column.getValue().getVerwendungszweck()));

      bookingsTable.setRowFactory(__ -> new TableRow<>() {
         @Override
         protected void updateItem(Booking item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && item.getMatchedCriteria().size() > 1) {
               setStyle("-fx-background-color: #ffcccb;");
            } else {
               setStyle("");
            }
         }
      });

      leftStatusLabel.textProperty().bind(viewModel.leftStatusLabelProperty());
      rightStatusLabel.textProperty().bind(viewModel.rightStatusLabelProperty());
   }

   private <T> ObservableValueBase<T> getValue(T localDate) {
      return new ObservableValueBase<>() {
         @Override
         public T getValue() {
            return localDate;
         }
      };
   }
}