package com.github.kaiwinter.ingparser.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.kaiwinter.ingparser.config.ConfigurationService;
import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
import com.github.kaiwinter.ingparser.csv.ImportService;
import com.github.kaiwinter.ingparser.preferences.PreferenceStore;
import com.github.kaiwinter.ingparser.statistic.StatisticService;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class MainViewModel implements ViewModel {

   private final StringProperty currentParserFile = new SimpleStringProperty();

   private final ImportService importService = new ImportService();
   private final ConfigurationService configurationService = new ConfigurationService();

   private final ListProperty<CategoryModel> categories = new SimpleListProperty<>();
   private final ListProperty<Booking> bookingsOfSelectedCategory = new SimpleListProperty<>();
   private final ListProperty<FilterCriterion> filterCriteriaOfSelectedBooking = new SimpleListProperty<>();

   private final StringProperty leftStatusLabel = new SimpleStringProperty();
   private final StringProperty rightStatusLabel = new SimpleStringProperty();

   private final SimpleBooleanProperty parserConfigurationChanged = new SimpleBooleanProperty(false);

   private Map<CategoryModel, List<Booking>> category2Booking;

   // Tab 2
   private final ListProperty<FilterCriterion> filterCriteriaFromFileP = new SimpleListProperty<>();
   private List<FilterCriterion> filterCriteriaFromFile = new ArrayList<>();
   private List<Booking> bookingsFromFile = new ArrayList<>();
   private final ListProperty<Booking> bookingsWithSelectedFilterCriterion = new SimpleListProperty<>();

   //
   public StringProperty currentParserFileProperty() {
      return currentParserFile;
   }

   public ListProperty<CategoryModel> categoriesProperty() {
      return this.categories;
   }

   public ListProperty<Booking> bookingsOfSelectedCategoryProperty() {
      return this.bookingsOfSelectedCategory;
   }

   public ListProperty<Booking> bookingsWithSelectedFilterCriterionProperty() {
      return this.bookingsWithSelectedFilterCriterion;
   }

   public ListProperty<FilterCriterion> filterCriteriaOfSelectedBookingProperty() {
      return this.filterCriteriaOfSelectedBooking;
   }

   public StringProperty leftStatusLabelProperty() {
      return this.leftStatusLabel;
   }

   public StringProperty rightStatusLabelProperty() {
      return this.rightStatusLabel;
   }

   public BooleanProperty parserConfigurationChangedProperty() {
      return this.parserConfigurationChanged;
   }

   public List<FilterCriterion> getFilterCriteriaFromFile() {
      return filterCriteriaFromFile;
   }

   public List<Booking> getBookingsFromFile() {
      return bookingsFromFile;
   }

   public ListProperty<FilterCriterion> filterCriteriaFromFilePProperty() {
      return filterCriteriaFromFileP;
   }

   public void loadCsvFile(File file) throws FileNotFoundException {
      bookingsFromFile = importService.importFromFile(new FileInputStream(file));
      applyFilterCriteriaOnBookings();
   }

   public void loadParserFile(File file) throws FileNotFoundException {
      filterCriteriaFromFile = configurationService.readConfiguration(new FileInputStream(file));
      applyFilterCriteriaOnBookings();
      currentParserFile.setValue(file.getAbsolutePath());
      PreferenceStore.saveLastUsedParserFile(file.getAbsolutePath());
   }

   public void applyFilterCriteriaOnBookings() {
      // on re-run: clean matchings!
      bookingsFromFile.forEach(booking -> booking.getMatchedCriteria().clear());

      importService.matchBookingsAgainstFilterCriteria(bookingsFromFile, filterCriteriaFromFile);

      List<CategoryModel> configuredCategories = filterCriteriaFromFile.stream() //
            .filter(crit -> crit.getCategory().getParentCategoryName() == null) // Don't list sub-categories separately
            .map(FilterCriterion::getCategory) //
            .distinct() //
            .sorted(Comparator.comparing(CategoryModel::getName)) //
            .collect(Collectors.toList());

      bookingsFromFile.stream() //
            .filter(booking -> booking.getMatchedCriteria().isEmpty())
            .forEach(booking -> booking.getMatchedCriteria().add(FilterCriterion.UNMATCHED_CRITERION));

      configuredCategories.add(FilterCriterion.UNMATCHED_CRITERION.getCategory());

      this.categories.setAll(configuredCategories);
      this.category2Booking = new StatisticService().groupByCategory(bookingsFromFile);
      this.filterCriteriaFromFileP.setAll(filterCriteriaFromFile);
   }

   public void refreshBookingTable(CategoryModel selectedValue) {
      if (selectedValue == null) {
         return;
      }
      bookingsOfSelectedCategory.clear();

      List<Booking> bookingsToDisplay = new ArrayList<>(category2Booking.getOrDefault(selectedValue, List.of()));

      // Add bookingsOfSelectedCategory of sub-categories
      for (CategoryModel sub : selectedValue.getSubCategories()) {
         bookingsToDisplay.addAll(category2Booking.getOrDefault(sub, List.of()));
      }
      bookingsOfSelectedCategory.addAll(bookingsToDisplay);

      BigDecimal total = bookingsToDisplay.stream() //
            .map(Booking::getBetrag) //
            .reduce(BigDecimal.ZERO, BigDecimal::add);
      leftStatusLabel.setValue("Bookings: " + bookingsToDisplay.size() + ", Sum: " + total.toString());
   }

   public void refreshFilterCriteriaList(ObservableList<Booking> selectedBookings) {
      filterCriteriaOfSelectedBooking.clear();
      rightStatusLabel.setValue("");
      if (selectedBookings.isEmpty()) {
         return;
      }
      BigDecimal total = selectedBookings.stream() //
            .map(Booking::getBetrag) //
            .reduce(BigDecimal.ZERO, BigDecimal::add);
      rightStatusLabel.setValue("Selected: " + selectedBookings.size() + ", sum: " + total.toString());
      filterCriteriaOfSelectedBooking.addAll(selectedBookings.stream() //
            .flatMap(booking -> booking.getMatchedCriteria().stream()
                  .filter(Predicate.not(FilterCriterion::isUnmatchedFilterCriterion))) // Filter Unmatched-Criterion
            .distinct() //
            .toList());
   }

   public void showBookingsWithFilterCriterion(FilterCriterion newValue) {
      List<Booking> bookings = bookingsFromFile.stream()
            .filter(booking -> booking.getMatchedCriteria().contains(newValue)).toList();

      bookingsWithSelectedFilterCriterion.setAll(bookings);
   }

   public Long calculateMatchesOfFilterCriterion(List<FilterCriterion> filterCriteria) {
      return filterCriteria.stream().mapToLong(this::calculateMatchesOfFilterCriterion).sum();
   }

   /**
    * Calculates the number of matched Bookings for a {@link FilterCriterion}.
    * 
    * @param filterCriterion the {@link FilterCriterion} to test
    * @return number of matching bookings
    */
   public Long calculateMatchesOfFilterCriterion(FilterCriterion filterCriterion) {
      // Make a copy of all Bookings
      List<Booking> copies = new ArrayList<>();
      bookingsFromFile.forEach(booking -> copies.add(new Booking(booking)));

      // Match Filter Criterion in copy of Bookings
      copies.forEach(booking -> booking.getMatchedCriteria().clear());
      importService.matchBookingsAgainstFilterCriteria(copies, List.of(filterCriterion));

      // Count matches
      return copies.stream().map(Booking::getMatchedCriteria).mapToLong(List::size).sum();
   }

   public boolean askForSave(Alert alert) {
      ButtonType overwrite = new ButtonType("Ja", ButtonBar.ButtonData.YES);
      ButtonType saveAs = new ButtonType("Speichern unter ...");
      ButtonType cancel = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);

      alert.getButtonTypes().setAll(overwrite, saveAs, cancel);

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isEmpty()) {
         return false;
      }
      if (result.get() == overwrite) {
         try (Writer writer = new FileWriter(currentParserFileProperty().getValue())) {
            new ConfigurationService().saveFilterCriteriaToFile(getFilterCriteriaFromFile(), writer);
            parserConfigurationChangedProperty().set(false);
            return true;
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      } else if (result.get() == saveAs) {
         FileChooser fileChooser = new FileChooser();
         FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Parser-Konfigurationen (*.json)",
                 "*.json");
         fileChooser.getExtensionFilters().add(extFilter);
         fileChooser.setTitle("Wähle die Datei");
         File selectedFile = fileChooser.showSaveDialog(Window.getWindows().getFirst());
         if (selectedFile == null) {
            return false;
         }
         try (Writer writer = new FileWriter(selectedFile)) {
            new ConfigurationService().saveFilterCriteriaToFile(getFilterCriteriaFromFile(), writer);
            parserConfigurationChangedProperty().set(false);
            writer.flush(); // flush to make loading work
            loadParserFile(selectedFile);
            return true;
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      return false;
   }

   /**
    * Checks for unsaved changes and asks the user to save them.
    * @return true if there are no unsaved changes, false otherwise
    */
   public boolean handleExit() {
      if (parserConfigurationChangedProperty().get()) {
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
         alert.setTitle("Ungesicherte Änderungen");
         alert.setHeaderText("""
               Es wurden Änderungen an der Parser-Datei vorgenommen die noch nicht gespeichert wurden.
               $file speichern?""".replace("$file", currentParserFileProperty().getValue()));

         return askForSave(alert);
      }
      return true;
   }
}