package com.github.kaiwinter.ingparser.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.config.FilterCriterion.MatchingCriterion;

class ImportServiceTest {

   ImportService importService = new ImportService();

   /**
    * Tests if bookings with negative amounts remain in the list.
    */
   @Test
   void testFilterNegativeInplace_lessThanZero() {
      List<Booking> list = new ArrayList<>();
      Booking bookingLessThanZero = new Booking();
      bookingLessThanZero.setBetrag(BigDecimal.valueOf(-12.34));
      list.add(bookingLessThanZero);
      importService.filterNegativeInplace(list);

      assertEquals(1, list.size());
   }

   /**
    * Tests if bookings with positive amounts are removed from the list.
    */
   @Test
   void testFilterNegativeInplace_moreThanZero() {
      List<Booking> list = new ArrayList<>();
      Booking bookingLessThanZero = new Booking();
      bookingLessThanZero.setBetrag(BigDecimal.valueOf(12.34));
      list.add(bookingLessThanZero);
      importService.filterNegativeInplace(list);

      assertEquals(0, list.size());
   }

   /**
    * Tests if bookings are matched correctly by Auftraggeber.
    *
    * @param filterPattern the Auftraggeber which is used to create a {@link MatchingCriterion#AUFTRAGGEBER} criterion
    */
   @ParameterizedTest
   @ValueSource(strings = { "e-mart", "E-MART", "e-Mart" })
   void moveToMapByCriteriaAuftraggeber(String filterPattern) {
      var booking = new Booking();
      booking.setAuftraggeber("quick-e-mart");
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byAuftraggeber("Markets", filterPattern);

      importService.matchBookingsAgainstFilterCriteria(bookings, moveDesciption);
      assertEquals(1, booking.getMatchedCriteria().size());
   }

   /**
    * Tests if bookings are matched correctly by Verwendungszweck.
    *
    * @param filterPattern the Auftraggeber which is used to create a {@link MatchingCriterion#VERWENDUNGSZWECK} criterion
    */
   @ParameterizedTest
   @ValueSource(strings = { "e-mart", "E-MART", "e-Mart" })
   void moveToMapByCriteriaVerwendungszweck(String filterPattern) {
      var booking = new Booking();
      booking.setVerwendungszweck("quick-e-mart");
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byVerwendungszweck("Markets", filterPattern);

      importService.matchBookingsAgainstFilterCriteria(bookings, moveDesciption);
      assertEquals(1, booking.getMatchedCriteria().size());
   }

   /**
    * Tests if a booking which matches multiple FilterCriteria are recognized.
    */
   @Test
   void warnAboutMultipleMatches_Auftraggeber() {
      var booking = new Booking();
      booking.setAuftraggeber("quick-e-mart");
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byAuftraggeber("Quicks", "quick");
      moveDesciption.addAll(FilterCriterion.byAuftraggeber("Markets", "mart"));

      importService.matchBookingsAgainstFilterCriteria(bookings, moveDesciption);
      assertEquals(2, booking.getMatchedCriteria().size());
   }

   /**
    * Tests if a booking which matches multiple FilterCriteria are recognized.
    */
   @Test
   void warnAboutMultipleMatches_Verwendungszweck() {
      var booking = new Booking();
      booking.setVerwendungszweck("quick-e-mart");
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byVerwendungszweck("Quicks", "quick");
      moveDesciption.addAll(FilterCriterion.byVerwendungszweck("Markets", "mart"));

      importService.matchBookingsAgainstFilterCriteria(bookings, moveDesciption);
      assertEquals(2, booking.getMatchedCriteria().size());
   }
}
