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

public class FilterServiceTest {

   /**
    * Tests if bookings with negative amounts remain in the list.
    */
   @Test
   public void testFilterNegativeInplace_lessThanZero() {
      FilterService filterService = new FilterService();

      List<Booking> list = new ArrayList<>();
      Booking bookingLessThanZero = new Booking();
      bookingLessThanZero.betrag = BigDecimal.valueOf(-12.34);
      list.add(bookingLessThanZero);
      filterService.filterNegativeInplace(list);

      assertEquals(1, list.size());
   }

   /**
    * Tests if bookings with positive amounts are removed from the list.
    */
   @Test
   public void testFilterNegativeInplace_moreThanZero() {
      FilterService filterService = new FilterService();

      List<Booking> list = new ArrayList<>();
      Booking bookingLessThanZero = new Booking();
      bookingLessThanZero.betrag = BigDecimal.valueOf(12.34);
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
   public void moveToMapByCriteriaAuftraggeber(String filterPattern) {
      FilterService filterService = new FilterService();
      var booking = new Booking();
      booking.auftraggeber = "quick-e-mart";
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byAuftraggeber("Markets", filterPattern);

      filterService.moveToMapByCriteria(moveDesciption, bookings);
      assertEquals(1, booking.matchedCriteria.size());
   }

   /**
    * Tests if bookings are matched correctly by Verwendungszweck.
    * 
    * @param filterPattern the Auftraggeber which is used to create a {@link Type#BY_VERWENDUNGSZWECK} criterion
    */
   @ParameterizedTest
   @ValueSource(strings = { "e-mart", "E-MART", "e-Mart" })
   public void moveToMapByCriteriaVerwendungszweck(String filterPattern) {
      FilterService filterService = new FilterService();
      var booking = new Booking();
      booking.verwendungszweck = "quick-e-mart";
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byVerwendungszweck("Markets", filterPattern);

      filterService.moveToMapByCriteria(moveDesciption, bookings);
      assertEquals(1, booking.matchedCriteria.size());
   }

   /**
    * Tests if a booking which matches multiple FilterCriteria are recognized.
    */
   @Test
   public void warnAboutMultipleMatches_Auftraggeber() {
      FilterService filterService = new FilterService();

      var booking = new Booking();
      booking.auftraggeber = "quick-e-mart";
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byAuftraggeber("Quicks", "quick");
      moveDesciption.addAll(FilterCriterion.byAuftraggeber("Markets", "mart"));

      filterService.moveToMapByCriteria(moveDesciption, bookings);
      assertEquals(2, booking.matchedCriteria.size());
   }

   /**
    * Tests if a booking which matches multiple FilterCriteria are recognized.
    */
   @Test
   public void warnAboutMultipleMatches_Verwendungszweck() {
      FilterService filterService = new FilterService();

      var booking = new Booking();
      booking.verwendungszweck = "quick-e-mart";
      List<Booking> bookings = List.of(booking);

      List<FilterCriterion> moveDesciption = FilterCriterion.byVerwendungszweck("Quicks", "quick");
      moveDesciption.addAll(FilterCriterion.byVerwendungszweck("Markets", "mart"));

      filterService.moveToMapByCriteria(moveDesciption, bookings);
      assertEquals(2, booking.matchedCriteria.size());
   }
}
