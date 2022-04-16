package com.github.kaiwinter.ingparser;

import java.util.List;

import com.github.kaiwinter.ingparser.config.ConfigurationService;
import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
import com.github.kaiwinter.ingparser.csv.ImportService;
import com.github.kaiwinter.ingparser.statistic.StatisticService;
import com.github.kaiwinter.ingparser.ui.MainView;
import com.github.kaiwinter.ingparser.ui.MainViewModel;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

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
      ConfigurationService configurationService = new ConfigurationService();

      List<Booking> bookings = importService.importFromFile(CSV_FILE);

      List<FilterCriterion> filterCriteria = configurationService.readConfiguration(CONFIG_FILE);

      importService.matchBookingsAgainstFilterCriteria(bookings, filterCriteria);
      bookings.removeIf(booking -> booking.getMatchedCriteria().stream()
            .anyMatch(crit -> crit.getCategory().getName().equals("ignore")));

      List<String> categories = filterCriteria.stream().map(FilterCriterion::getCategory) //
            .distinct() //
            .filter(category -> !"ignore".equals(category.getName())) //
            .filter(category -> category.getParentCategoryName() == null) // SubCategories nicht separat aufführen
            .map(CategoryModel::getName) //
            .sorted() //
            .toList();

      new StatisticService().groupByMonthAndCategory(bookings, categories);

      launch();
   }

}