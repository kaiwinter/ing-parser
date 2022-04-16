package com.github.kaiwinter.ingparser;

import java.util.List;
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

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
      stage.setTitle("Hello World Application");

      ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();

      Parent root = viewTuple.getView();
      stage.setScene(new Scene(root));
      stage.show();
   }

   public static void main(String[] args) {

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
      bookings.removeIf(
            booking -> booking.getMatchedCriteria().stream().anyMatch(crit -> crit.getCategory().equals("ignore")));

      List<String> categories = filterCriteria.stream().map(FilterCriterion::getCategory) //
            .distinct() //
            .filter(category -> !"ignore".equals(category)) //
            .filter(category -> category.getParentCategoryName() == null) // SubCategories nicht separat aufführen
            .map(CategoryName::getName) //
            .sorted().collect(Collectors.toList());

      new StatisticService().groupByCategoryAndMonth(bookings);
      new StatisticService().groupByMonthAndCategory(bookings, categories);

//      System.exit(0);
      launch();
   }

}