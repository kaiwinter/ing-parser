package com.github.kaiwinter.ingparser.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;

/**
 * Tests for {@link MainViewModel}.
 */
class MainViewModelTest {

   @Test
   void calculateMatchesOfFilterCriterion_One() {
      MainViewModel mainViewModel = new MainViewModel();
      Booking booking1 = new Booking();
      booking1.setAuftraggeber("Shop ABC");
      mainViewModel.getBookingsFromFile().add(booking1);

      Booking booking2 = new Booking();
      booking2.setAuftraggeber("Store ABC");
      mainViewModel.getBookingsFromFile().add(booking2);

      FilterCriterion filterCriterion = FilterCriterion.byAuftraggeber("Main category", "shop").getFirst();
      Long result = mainViewModel.calculateMatchesOfFilterCriterion(filterCriterion);

      assertEquals(1, result);
   }

   @Test
   void calculateMatchesOfFilterCriterion_Two() {
      MainViewModel mainViewModel = new MainViewModel();
      Booking booking1 = new Booking();
      booking1.setAuftraggeber("Shop ABC");
      mainViewModel.getBookingsFromFile().add(booking1);

      Booking booking2 = new Booking();
      booking2.setAuftraggeber("Shop DEF");
      mainViewModel.getBookingsFromFile().add(booking2);

      FilterCriterion filterCriterion = FilterCriterion.byAuftraggeber("Main category", "shop").getFirst();
      Long result = mainViewModel.calculateMatchesOfFilterCriterion(filterCriterion);

      assertEquals(2, result);
   }
}
