package com.github.kaiwinter.ingparser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.ingparser.model.Booking;
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

   private final ListProperty<String> categories = new SimpleListProperty<>();
   private final ListProperty<TableModel> bookings = new SimpleListProperty<>();

   private Map<String, List<Booking>> category2Booking;

   public ListProperty<String> categoriesProperty() {
      return this.categories;
   }

   public ListProperty<TableModel> bookingsProperty() {
      return this.bookings;
   }

   public void init() {
      ImportService importService = new ImportService();
      FilterService filterService = new FilterService();
      ConfigurationService configurationService = new ConfigurationService();

      List<Booking> bookings = importService.importFromFile(CSV_FILE);

      LOGGER.info("SIZE initial: {}", bookings.size());
      filterService.filterNegativeInplace(bookings);

      LOGGER.info("SIZE initial (negative only): {}", bookings.size());

      List<FilterCriterion> filterCriteria = configurationService.readConfiguration(CONFIG_FILE);

      filterService.matchBookingsAgainstFilterCriteria(bookings, filterCriteria);

      List<String> categories = filterCriteria.stream().map(FilterCriterion::getCategory) //
            .distinct() //
            .sorted().collect(Collectors.toList());

      bookings.stream().filter(booking -> booking.matchedCriteria.isEmpty())
            .forEach(booking -> booking.matchedCriteria.add(FilterCriterion.NULL_CRITERION));

      categories.add(FilterCriterion.NULL_CRITERION.getCategory());

      this.categories.addAll(categories);
      this.category2Booking = new StatisticService().groupByCategory(bookings);
   }

   public void refreshBookingTable(String selectedValue) {
      if (selectedValue == null) {
         return;
      }
      bookings.clear();
      System.out.println("SELECTED: " + selectedValue);
      List<Booking> bookingsA = category2Booking.getOrDefault(selectedValue, List.of());
      for (Booking booking : bookingsA) {

         TableModel tableModel = new TableModel();
         tableModel.setDate(booking.date);
         tableModel.setBetrag(booking.betrag);
         tableModel.setAuftraggeber(booking.auftraggeber);
         tableModel.setVerwendungszweck(booking.verwendungszweck);
         tableModel.setMatchedCriteria(booking.matchedCriteria.size());
         bookings.add(tableModel);
      }
   }
}