package com.github.kaiwinter.ingparser.service;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.ingparser.model.Booking;
import com.github.kaiwinter.ingparser.model.FilterCriterion;

public class FilterService {

   private static final Logger LOGGER = LoggerFactory.getLogger(FilterService.class);

   public void filterNegativeInplace(List<Booking> list) {
      list.removeIf(booking -> booking.betrag.compareTo(BigDecimal.ZERO) > 0);
   }

   /**
    * Creates a Map with category name as key and a list of bookings which match to these categories.
    * 
    * @param filterCriteria the list of all filter criteria
    * @param bookings       the list of all bookings
    */
   public void moveToMapByCriteria(List<FilterCriterion> filterCriteria, List<Booking> bookings) {

      for (Booking booking : bookings) {
         matchBookingWithFilterCriteria(filterCriteria, booking);
      }

      bookings.stream().filter(booking -> booking.matchedCriteria.isEmpty())
            .map(booking -> booking.auftraggeber + " < -- > " + booking.verwendungszweck).distinct().sorted()
            .forEach(System.out::println);
   }

   /**
    * Iterates all filter criteria for one booking and puts it in a Map accordingly.
    * 
    * @param filterCriteria the list of all filter criteria
    * @param booking        the booking to match
    */
   private void matchBookingWithFilterCriteria(List<FilterCriterion> filterCriteria, Booking booking) {

      // Iterate each filter criterion (in context of each booking)
      for (FilterCriterion filterCriterion : filterCriteria) {
         if (filterCriterion.getType() == FilterCriterion.Type.BY_AUFTRAGGEBER) {
            addToMapIfMatches(filterCriterion, booking, booking.auftraggeber);

         } else if (filterCriterion.getType() == FilterCriterion.Type.BY_VERWENDUNGSZWECK) {
            addToMapIfMatches(filterCriterion, booking, booking.verwendungszweck);

         } else {
            throw new IllegalArgumentException("Unknown: " + filterCriterion.getType());
         }
      }

   }

   private void addToMapIfMatches(FilterCriterion filterCriterion, Booking booking, String value) {
      if (StringUtils.containsIgnoreCase(value, filterCriterion.getPattern())) {
         booking.matchedCriteria.add(filterCriterion);
         if (booking.matchedCriteria.size() > 1) {
            LOGGER.warn("{} matching multiple Criteria: {}", booking, booking.matchedCriteria);
         }

      }
   }

}
