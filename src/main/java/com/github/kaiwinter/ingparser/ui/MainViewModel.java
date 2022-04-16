package com.github.kaiwinter.ingparser.ui;

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

public class MainViewModel implements ViewModel {

   private final ListProperty<CategoryModel> categories = new SimpleListProperty<>();
   private final ListProperty<Booking> bookings = new SimpleListProperty<>();
   private final ListProperty<FilterCriterion> filterCriteria = new SimpleListProperty<>();

   private Map<CategoryModel, List<Booking>> category2Booking;

   public ListProperty<CategoryModel> categoriesProperty() {
      return this.categories;
   }

   public ListProperty<Booking> bookingsProperty() {
      return this.bookings;
   }

   public ListProperty<FilterCriterion> filterCriteriaProperty() {
      return this.filterCriteria;
   }

   public void loadData(String csvFile, String configFile) {
      ImportService importService = new ImportService();
      ConfigurationService configurationService = new ConfigurationService();

      List<Booking> importedBookings = importService.importFromFile(csvFile);

      List<FilterCriterion> filterCriteria = configurationService.readConfiguration(configFile);

      importService.matchBookingsAgainstFilterCriteria(importedBookings, filterCriteria);

      List<CategoryModel> configuredCategories = filterCriteria.stream() //
            .filter(crit -> crit.getCategory().getParentCategoryName() == null) // Don't list sub-categories separately
            .map(FilterCriterion::getCategory) //
            .distinct() //
            .sorted(Comparator.comparing(CategoryModel::getName)) //
            .collect(Collectors.toList());

      importedBookings.stream() //
            .filter(booking -> booking.getMatchedCriteria().isEmpty())
            .forEach(booking -> booking.getMatchedCriteria().add(FilterCriterion.NULL_CRITERION));

      configuredCategories.add(FilterCriterion.NULL_CRITERION.getCategory());

      configurationService.saveFilterCriteriaToFile(filterCriteria);

      this.categories.addAll(configuredCategories);
      this.category2Booking = new StatisticService().groupByCategory(importedBookings);
   }

   public void refreshBookingTable(CategoryModel selectedValue) {
      if (selectedValue == null) {
         return;
      }
      bookings.clear();

      List<Booking> bookingsToDisplay = new ArrayList<>(category2Booking.getOrDefault(selectedValue, List.of()));

      // Add bookings of sub-categories
      for (CategoryModel sub : selectedValue.getSubCategories()) {
         bookingsToDisplay.addAll(category2Booking.getOrDefault(sub, List.of()));
      }
      bookings.addAll(bookingsToDisplay);
   }

   public void refreshFilterCriteriaList(Booking newValue) {
      filterCriteria.clear();
      if (newValue == null) {
         return;
      }
      filterCriteria.addAll(newValue.getMatchedCriteria());
   }
}