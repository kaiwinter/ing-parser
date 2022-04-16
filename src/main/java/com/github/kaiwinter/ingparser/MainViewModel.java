package com.github.kaiwinter.ingparser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.ingparser.model.Booking;
import com.github.kaiwinter.ingparser.model.CategoryName;
import com.github.kaiwinter.ingparser.model.FilterCriterion;
import com.github.kaiwinter.ingparser.service.ConfigurationService;
import com.github.kaiwinter.ingparser.service.FilterService;
import com.github.kaiwinter.ingparser.service.ImportService;
import com.github.kaiwinter.ingparser.service.StatisticService;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;

public class MainViewModel implements ViewModel {

   private static final Logger LOGGER = LoggerFactory.getLogger(MainViewModel.class);
   private static final String CSV_FILE = "/ING_sample.csv";
   private static final String CONFIG_FILE = "/parser_sample.json";

   private final ListProperty<CategoryName> categories = new SimpleListProperty<>();
   private final ListProperty<TableModel> bookings = new SimpleListProperty<>();

   private Map<CategoryName, List<Booking>> category2Booking;

   public ListProperty<CategoryName> categoriesProperty() {
      return this.categories;
   }

   public ListProperty<TableModel> bookingsProperty() {
      return this.bookings;
   }

   public void init() {
      ImportService importService = new ImportService();
      FilterService filterService = new FilterService();
      ConfigurationService configurationService = new ConfigurationService();

      List<Booking> importedBookings = importService.importFromFile(CSV_FILE);

      LOGGER.info("SIZE initial: {}", importedBookings.size());
      filterService.filterNegativeInplace(importedBookings);

      LOGGER.info("SIZE initial (negative only): {}", importedBookings.size());

      List<FilterCriterion> filterCriteria = configurationService.readConfiguration(CONFIG_FILE);

      filterService.matchBookingsAgainstFilterCriteria(importedBookings, filterCriteria);

      List<CategoryName> configuredCategories = filterCriteria.stream() //
            .filter(crit -> crit.getCategory().getParentCategoryName() == null) // Don't list sub-categories separately
            .map(FilterCriterion::getCategory) //
            .distinct() //
            .sorted(Comparator.comparing(CategoryName::getName)) //
            .collect(Collectors.toList());

      importedBookings.stream() //
            .filter(booking -> booking.getMatchedCriteria().isEmpty())
            .forEach(booking -> booking.getMatchedCriteria().add(FilterCriterion.NULL_CRITERION));

      configuredCategories.add(FilterCriterion.NULL_CRITERION.getCategory());

      this.categories.addAll(configuredCategories);
      this.category2Booking = new StatisticService().groupByCategory(importedBookings);
   }

   public void refreshBookingTable(CategoryName selectedValue) {
      if (selectedValue == null) {
         return;
      }
      bookings.clear();

      List<Booking> bookingsToDisplay = new ArrayList<>(category2Booking.getOrDefault(selectedValue, List.of()));

      // Add bookings of sub-categories
      for (CategoryName sub : selectedValue.getSubCategories()) {
         bookingsToDisplay.addAll(category2Booking.getOrDefault(sub, List.of()));
      }

      for (Booking booking : bookingsToDisplay) {
         TableModel tableModel = new TableModel();
         tableModel.setDate(booking.getDate());
         tableModel.setBetrag(booking.getBetrag());
         tableModel.setAuftraggeber(booking.getAuftraggeber());
         tableModel.setVerwendungszweck(booking.getVerwendungszweck());
         tableModel.setMatchedCriteria(booking.getMatchedCriteria().size());
         bookings.add(tableModel);
      }
   }
}