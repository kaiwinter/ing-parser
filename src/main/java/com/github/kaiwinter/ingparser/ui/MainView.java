package com.github.kaiwinter.ingparser.ui;

import java.io.File;
import java.io.FileNotFoundException;
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
import com.github.kaiwinter.ingparser.preferences.PreferenceStore;
import com.github.kaiwinter.ingparser.statistic.StatisticService;
import com.github.kaiwinter.ingparser.ui.FilterCriterionListCell.Type;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

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

   // Tab 2
   @FXML
   private ListView<FilterCriterion> criteriaList2;

   @FXML
   private TableView<Booking> bookingsTable2;
   @FXML
   private TableColumn<Booking, BigDecimal> betragColumn2;
   @FXML
   private TableColumn<Booking, LocalDate> dateColumn2;
   @FXML
   private TableColumn<Booking, String> auftraggeberColumn2;
   @FXML
   private TableColumn<Booking, String> verwendungszweckColumn2;

   @FXML
   private Button removeFilterCriterionButton2;

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

         List<String> categories = viewModel.getFilterCriteriaFromFile().stream().map(FilterCriterion::getCategory) //
               .distinct() //
               .filter(category -> !"ignore".equals(category.getName())) //
               .filter(category -> category.getParentCategoryName() == null) // SubCategories nicht separat aufführen
               .map(CategoryModel::getName) //
               .sorted() //
               .toList();
         new StatisticService().groupByMonthAndCategory(viewModel.bookingsFromFile, categories);
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

      criteriaList.setCellFactory(new FilterCriterionListCell(Type.SHORT));

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

      // Tab 2
      viewModel.filterCriteriaFromFilePProperty().bind(criteriaList2.itemsProperty());
      criteriaList2.setCellFactory(new FilterCriterionListCell(Type.LONG));
      criteriaList2.getSelectionModel().selectedItemProperty().addListener((__1, __2, newValue) -> {

         viewModel.showBookingsWithFilterCriterion(newValue);
      });
      viewModel.bookingsWithSelectedFilterCriterionProperty().bind(bookingsTable2.itemsProperty());
      betragColumn2.setCellValueFactory(column -> getValue(column.getValue().getBetrag()));
      dateColumn2.setCellValueFactory(column -> getValue(column.getValue().getDate()));
      auftraggeberColumn2.setCellValueFactory(column -> getValue(column.getValue().getAuftraggeber()));
      verwendungszweckColumn2.setCellValueFactory(column -> getValue(column.getValue().getVerwendungszweck()));

      removeFilterCriterionButton2.disableProperty()
            .bind(criteriaList2.getSelectionModel().selectedItemProperty().isNull());
   }

   public void openCsvFile() throws FileNotFoundException {
      FileChooser fileChooser = new FileChooser();
      FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Dateien (*.csv)", "*.csv");
      fileChooser.getExtensionFilters().add(extFilter);
      fileChooser.setTitle("Wähle die Datei");
      File file = fileChooser.showOpenDialog(Window.getWindows().iterator().next());
      if (file != null) {
         try {
            viewModel.loadCsvFile(file);
            PreferenceStore.saveLastUsedCsvFile(file.getAbsolutePath());

         } catch (RuntimeException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("Die Datei konnte nicht eingelesen werden.");
            alert.show();
         }
      }
   }

   public void openParserFile() throws FileNotFoundException {
      FileChooser fileChooser = new FileChooser();
      FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Parser-Konfigurationen (*.json)",
            "*.json");
      fileChooser.getExtensionFilters().add(extFilter);
      fileChooser.setTitle("Wähle die Datei");
      File file = fileChooser.showOpenDialog(Window.getWindows().iterator().next());
      if (file != null) {
         try {
            viewModel.loadParserFile(file);
            PreferenceStore.saveLastUsedParserFile(file.getAbsolutePath());

         } catch (RuntimeException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText("Die Datei konnte nicht eingelesen werden.");
            alert.show();
         }
      }
   }

   public void printStatistics() {
      List<Booking> bookings = viewModel.bookingsFromFile;
      List<FilterCriterion> filterCriteria = viewModel.getFilterCriteriaFromFile();

//      bookings.removeIf(booking -> booking.getMatchedCriteria().stream()
//            .anyMatch(crit -> crit.getCategory().getName().equals("ignore")));

      List<String> categories = filterCriteria.stream().map(FilterCriterion::getCategory) //
            .distinct() //
            .filter(category -> !"ignore".equals(category.getName())) //
            .filter(category -> category.getParentCategoryName() == null) // SubCategories nicht separat aufführen
            .map(CategoryModel::getName) //
            .sorted() //
            .toList();

      new StatisticService().groupByMonthAndCategory(bookings, categories);
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