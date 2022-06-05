package com.github.kaiwinter.ingparser;

import com.github.kaiwinter.ingparser.config.ConfigurationService;
import com.github.kaiwinter.ingparser.csv.ImportService;
import com.github.kaiwinter.ingparser.ui.MainView;
import com.github.kaiwinter.ingparser.ui.MainViewModel;

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

   @Override
   public void start(Stage stage) {
      stage.setTitle("ING CSV Parser (DE)");

      ViewTuple<MainView, MainViewModel> viewTuple = FluentViewLoader.fxmlView(MainView.class).load();

      Parent root = viewTuple.getView();
      stage.setScene(new Scene(root));
      stage.show();
   }

   public static void main(String[] args) {

      ImportService importService = new ImportService();
      ConfigurationService configurationService = new ConfigurationService();

      // List<Booking> bookings = importService.importFromFile(CSV_FILE);

      // List<FilterCriterion> filterCriteria = configurationService.readConfiguration(CONFIG_FILE);

//      importService.matchBookingsAgainstFilterCriteria(bookings, filterCriteria);
//      bookings.removeIf(booking -> booking.getMatchedCriteria().stream()
//            .anyMatch(crit -> crit.getCategory().getName().equals("ignore")));
//
//      List<String> categories = filterCriteria.stream().map(FilterCriterion::getCategory) //
//            .distinct() //
//            .filter(category -> !"ignore".equals(category.getName())) //
//            .filter(category -> category.getParentCategoryName() == null) // SubCategories nicht separat aufführen
//            .map(CategoryModel::getName) //
//            .sorted() //
//            .toList();
//
//      new StatisticService().groupByMonthAndCategory(bookings, categories);

      launch();
   }

}