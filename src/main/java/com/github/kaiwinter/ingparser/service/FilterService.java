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
    * Matches all bookings against all filter criteria. As a result each booking which matches one
    * {@link FilterCriterion} will have it set in its list of matched criteria.
    * 
    * @param bookings       the list of all bookings
    * @param filterCriteria the list of all filter criteria
    */
   public void matchBookingsAgainstFilterCriteria(List<Booking> bookings, List<FilterCriterion> filterCriteria) {

      for (Booking booking : bookings) {
         matchBookingAgainstFilterCriteria(booking, filterCriteria);
      }

      bookings.stream().filter(booking -> booking.matchedCriteria.isEmpty())
            .map(booking -> booking.auftraggeber + " < -- > " + booking.verwendungszweck).distinct().sorted()
            .forEach(System.out::println);
   }

   /**
    * Matches one booking against all available filter criteria.
    * 
    * @param booking        the booking to match
    * @param filterCriteria the list of all filter criteria
    */
   private void matchBookingAgainstFilterCriteria(Booking booking, List<FilterCriterion> filterCriteria) {

      // Iterate each filter criterion (in context of each booking)
      for (FilterCriterion filterCriterion : filterCriteria) {
         if (filterCriterion.getType() == FilterCriterion.Type.BY_AUFTRAGGEBER) {
            matchBookingValueAgainstCriterion(booking, filterCriterion, booking.auftraggeber);

         } else if (filterCriterion.getType() == FilterCriterion.Type.BY_VERWENDUNGSZWECK) {
            matchBookingValueAgainstCriterion(booking, filterCriterion, booking.verwendungszweck);

         } else {
            throw new IllegalArgumentException("Unknown: " + filterCriterion.getType());
         }
      }

   }

   /**
    * Matches the value of one booking against one filter criterion. If the value matches the criterion the criterion is
    * added to the list of matched criteria in the booking.
    * 
    * @param booking         the booking
    * @param filterCriterion the filter criterion
    * @param value           the value which is matched against the filter criterion
    */
   private void matchBookingValueAgainstCriterion(Booking booking, FilterCriterion filterCriterion, String value) {
      if (StringUtils.containsIgnoreCase(value, filterCriterion.getPattern())) {
         booking.matchedCriteria.add(filterCriterion);
         if (booking.matchedCriteria.size() > 1) {
            LOGGER.warn("{} matching multiple Criteria: {}", booking, booking.matchedCriteria);
         }

      }
   }

}
