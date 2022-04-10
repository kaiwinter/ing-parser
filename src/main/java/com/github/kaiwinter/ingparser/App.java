package com.github.kaiwinter.ingparser;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.ingparser.model.Booking;
import com.github.kaiwinter.ingparser.model.FilterCriterion;
import com.github.kaiwinter.ingparser.service.ConfigurationService;
import com.github.kaiwinter.ingparser.service.FilterService;
import com.github.kaiwinter.ingparser.service.ImportService;
import com.github.kaiwinter.ingparser.service.StatisticService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

   private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
   private static final String CSV_FILE = "/ING_sample.csv";
   private static final String CONFIG_FILE = "/parser_sample.json";

   @Override
   public void start(Stage stage) {

      var label = new Label("Hello, JavaFX");
      var scene = new Scene(new StackPane(label), 640, 480);
      stage.setScene(scene);
      stage.show();
   }

   public static void main(String[] args) throws IOException {

      ImportService importService = new ImportService();
      FilterService filterService = new FilterService();
      ConfigurationService configurationService = new ConfigurationService();

      List<Booking> bookings = importService.importFromFile(CSV_FILE);

      LOGGER.info("SIZE initial: {}", bookings.size());
      filterService.filterNegativeInplace(bookings);

      LOGGER.info("SIZE initial (negative only): {}", bookings.size());

      List<FilterCriterion> filterCriteria = configurationService.readConfiguration(CONFIG_FILE);
      // bookings.sort(Comparator.comparingDouble(booking -> booking.betrag.doubleValue()));

      filterService.matchBookingsAgainstFilterCriteria(bookings, filterCriteria);
      // bookings.removeIf(booking -> !(booking.date.getYear() == 2021 && booking.date.getMonth() == Month.DECEMBER));

      new StatisticService().groupByCategoryAndMonth(bookings);
      new StatisticService().groupByMonthAndCategory(bookings);

      System.exit(0);
//      launch();
   }

}