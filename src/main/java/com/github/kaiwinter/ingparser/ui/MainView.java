package com.github.kaiwinter.ingparser.ui;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.github.kaiwinter.ingparser.ui.model.BookingModel;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ObservableValueBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class MainView implements FxmlView<MainViewModel>, Initializable {

   @InjectViewModel
   private MainViewModel viewModel;

   @FXML
   private ListView<CategoryModel> categoryList;

   @FXML
   private TableView<BookingModel> bookingsTable;
   @FXML
   private TableColumn<BookingModel, BigDecimal> betragColumn;
   @FXML
   private TableColumn<BookingModel, LocalDate> dateColumn;
   @FXML
   private TableColumn<BookingModel, String> auftraggeberColumn;
   @FXML
   private TableColumn<BookingModel, String> verwendungszweckColumn;

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      // List click listener
      categoryList.getSelectionModel().selectedItemProperty()
            .addListener((__1, __2, newValue) -> viewModel.refreshBookingTable(newValue));

      // Model of List and Table
      viewModel.categoriesProperty().bind(categoryList.itemsProperty());
      viewModel.bookingsProperty().bind(bookingsTable.itemsProperty());

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
         protected void updateItem(BookingModel item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && item.getMatchedCriteria() > 1) {
               setStyle("-fx-background-color: #ffcccb;");
            } else {
               setStyle("");
            }
         }
      });
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