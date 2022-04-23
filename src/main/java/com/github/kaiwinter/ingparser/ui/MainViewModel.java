package com.github.kaiwinter.ingparser.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.kaiwinter.ingparser.config.ConfigurationService;
import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
import com.github.kaiwinter.ingparser.csv.ImportService;
import com.github.kaiwinter.ingparser.statistic.StatisticService;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class MainViewModel implements ViewModel {

   private final ImportService importService = new ImportService();
   private final ConfigurationService configurationService = new ConfigurationService();

   private final ListProperty<CategoryModel> categories = new SimpleListProperty<>();
   private final ListProperty<Booking> bookingsOfSelectedCategory = new SimpleListProperty<>();
   private final ListProperty<FilterCriterion> filterCriteriaOfSelectedBooking = new SimpleListProperty<>();

   private final StringProperty leftStatusLabel = new SimpleStringProperty();
   private final StringProperty rightStatusLabel = new SimpleStringProperty();

   private Map<CategoryModel, List<Booking>> category2Booking;

   private List<FilterCriterion> filterCriteriaFromFile;
   private List<Booking> bookingsFromFile;

   public ListProperty<CategoryModel> categoriesProperty() {
      return this.categories;
   }

   public ListProperty<Booking> bookingsOfSelectedCategoryProperty() {
      return this.bookingsOfSelectedCategory;
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

   public List<FilterCriterion> getFilterCriteriaFromFile() {
      return filterCriteriaFromFile;
   }

   public void loadData(String csvFile, String configFile) {
      bookingsFromFile = importService.importFromFile(csvFile);
      filterCriteriaFromFile = configurationService.readConfiguration(configFile);
      applyFilterCriteriaOnBookings();
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
            .forEach(booking -> booking.getMatchedCriteria().add(FilterCriterion.NULL_CRITERION));

      configuredCategories.add(FilterCriterion.NULL_CRITERION.getCategory());

      configurationService.saveFilterCriteriaToFile(filterCriteriaFromFile);

      this.categories.setAll(configuredCategories);
      this.category2Booking = new StatisticService().groupByCategory(bookingsFromFile);
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
            .flatMap(booking -> booking.getMatchedCriteria().stream()) //
            .distinct() //
            .toList());
   }
}