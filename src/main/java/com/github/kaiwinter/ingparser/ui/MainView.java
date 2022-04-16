package com.github.kaiwinter.ingparser.ui;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      // List click listener
      categoryList.getSelectionModel().selectedItemProperty()
            .addListener((__1, __2, newValue) -> viewModel.refreshBookingTable(newValue));

      // Table click listener
      bookingsTable.getSelectionModel().selectedItemProperty().addListener((__1, __2, newValue) -> {
         ObservableList<Booking> selected = bookingsTable.getSelectionModel().getSelectedItems();
         viewModel.refreshFilterCriteriaList(selected);
      });

      bookingsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

      // Model of List and Table
      viewModel.categoriesProperty().bind(categoryList.itemsProperty());
      viewModel.bookingsProperty().bind(bookingsTable.itemsProperty());
      viewModel.filterCriteriaProperty().bind(criteriaList.itemsProperty());

      // List
      categoryList.setCellFactory(param -> new ListCell<>() {
         @Override
         public void updateItem(CategoryModel category, boolean empty) {
            super.updateItem(category, empty);
            if (empty || category == null) {
               setText(null);
            } else {
               setText(category.getName());
            }
         }
      });

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