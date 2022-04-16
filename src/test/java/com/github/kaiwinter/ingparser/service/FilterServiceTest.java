package com.github.kaiwinter.ingparser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.github.kaiwinter.ingparser.model.Booking;
import com.github.kaiwinter.ingparser.model.FilterCriterion;
import com.github.kaiwinter.ingparser.model.FilterCriterion.Type;

class FilterServiceTest {

   /**
    * Tests if bookings with negative amounts remain in the list.
    */
   @Test
   void testFilterNegativeInplace_lessThanZero() {
      FilterService filterService = new FilterService();

      List<Booking> list = new ArrayList<>();
      Booking bookingLessThanZero = new Booking();
      bookingLessThanZero.setBetrag(BigDecimal.valueOf(-12.34));
      list.add(bookingLessThanZero);
      filterService.filterNegativeInplace(list);

      assertEquals(1, list.size());
   }

   /**
    * Tests if bookings with positive amounts are removed from the list.
    */
   @Test
   void testFilterNegativeInplace_moreThanZero() {
      FilterService filterService = new FilterService();

      List<Booking> list = new ArrayList<>();
      Booking bookingLessThanZero = new Booking();
      bookingLessThanZero.setBetrag(BigDecimal.valueOf(12.34));
      list.add(bookingLessThanZero);
      filterService.filterNegativeInplace(list);

      assertEquals(0, list.size());
   }

   /**
    * Tests if bookings are matched correctly by Auftraggeber.
    * 
    * @param filterPattern the Auftraggeber which is used to create a {@link Type#BY_AUFTRAGGEBER} criterion
    */
   @ParameterizedTest
   @ValueSource(strings = { "e-mart", "E-MART", "e-Mart" })
   void moveToMapByCriteriaAuftraggeber(String filterPattern) {
      FilterService filterService = new FilterService();
      var booking = new Booking();
      booking.setAuftraggeber("quick-e-mart");
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byAuftraggeber("Markets", filterPattern);

      filterService.matchBookingsAgainstFilterCriteria(bookings, moveDesciption);
      assertEquals(1, booking.getMatchedCriteria().size());
   }

   /**
    * Tests if bookings are matched correctly by Verwendungszweck.
    * 
    * @param filterPattern the Auftraggeber which is used to create a {@link Type#BY_VERWENDUNGSZWECK} criterion
    */
   @ParameterizedTest
   @ValueSource(strings = { "e-mart", "E-MART", "e-Mart" })
   void moveToMapByCriteriaVerwendungszweck(String filterPattern) {
      FilterService filterService = new FilterService();
      var booking = new Booking();
      booking.setVerwendungszweck("quick-e-mart");
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byVerwendungszweck("Markets", filterPattern);

      filterService.matchBookingsAgainstFilterCriteria(bookings, moveDesciption);
      assertEquals(1, booking.getMatchedCriteria().size());
   }

   /**
    * Tests if a booking which matches multiple FilterCriteria are recognized.
    */
   @Test
   void warnAboutMultipleMatches_Auftraggeber() {
      FilterService filterService = new FilterService();

      var booking = new Booking();
      booking.setAuftraggeber("quick-e-mart");
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byAuftraggeber("Quicks", "quick");
      moveDesciption.addAll(FilterCriterion.byAuftraggeber("Markets", "mart"));

      filterService.matchBookingsAgainstFilterCriteria(bookings, moveDesciption);
      assertEquals(2, booking.getMatchedCriteria().size());
   }

   /**
    * Tests if a booking which matches multiple FilterCriteria are recognized.
    */
   @Test
   void warnAboutMultipleMatches_Verwendungszweck() {
      FilterService filterService = new FilterService();

      var booking = new Booking();
      booking.setVerwendungszweck("quick-e-mart");
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byVerwendungszweck("Quicks", "quick");
      moveDesciption.addAll(FilterCriterion.byVerwendungszweck("Markets", "mart"));

      filterService.matchBookingsAgainstFilterCriteria(bookings, moveDesciption);
      assertEquals(2, booking.getMatchedCriteria().size());
   }
}
