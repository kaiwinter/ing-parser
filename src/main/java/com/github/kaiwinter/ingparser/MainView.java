package com.github.kaiwinter.ingparser;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.github.kaiwinter.ingparser.model.CategoryName;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ObservableValueBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class MainView implements FxmlView<MainViewModel>, Initializable {

   @InjectViewModel
   private MainViewModel viewModel;

   @FXML
   ListView<CategoryName> categoryList;

   @FXML
   private TableView<TableModel> bookingsTable;
   @FXML
   private TableColumn<TableModel, BigDecimal> betragColumn;
   @FXML
   private TableColumn<TableModel, LocalDate> dateColumn;
   @FXML
   private TableColumn<TableModel, String> auftraggeberColumn;
   @FXML
   private TableColumn<TableModel, String> verwendungszweckColumn;

   @Override
   public void initialize(URL url, ResourceBundle resourceBundle) {
      // List click listener
      categoryList.getSelectionModel().selectedItemProperty()
            .addListener((__1, __2, newValue) -> viewModel.refreshBookingTable(newValue));

      // Model of List and Table
      viewModel.categoriesProperty().bind(categoryList.itemsProperty());
      viewModel.bookingsProperty().bind(bookingsTable.itemsProperty());

      // Table cells
      betragColumn.setCellValueFactory(column -> getValue(column.getValue().getBetrag()));
      dateColumn.setCellValueFactory(column -> getValue(column.getValue().getDate()));
      auftraggeberColumn.setCellValueFactory(column -> getValue(column.getValue().getAuftraggeber()));
      verwendungszweckColumn.setCellValueFactory(column -> getValue(column.getValue().getVerwendungszweck()));

      bookingsTable.setRowFactory(__ -> {
         return new TableRow<TableModel>() {
            @Override
            protected void updateItem(TableModel item, boolean empty) {
               super.updateItem(item, empty);
               if (item != null && item.getMatchedCriteria() > 1) {
                  setStyle("-fx-background-color: #ffcccb;");
               } else {
                  setStyle("");
               }
            }
         };
      });
      viewModel.init();
   }

   private <T> ObservableValueBase<T> getValue(T localDate) {
      return new ObservableValueBase<T>() {
         @Override
         public T getValue() {
            return localDate;
         }
      };
   }
}