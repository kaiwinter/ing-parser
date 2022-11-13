package com.github.kaiwinter.ingparser.csv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.opencsv.bean.CsvToBeanBuilder;

public class ImportService {

   private static final Logger LOGGER = LoggerFactory.getLogger(ImportService.class);

   // Number of lines to skip from the CSV file.
   private static final int CSV_HEADER_LINES = 14;

   public List<Booking> importFromFile(InputStream inputStream) {
      List<Booking> bookings = new CsvToBeanBuilder<Booking>(new InputStreamReader(inputStream)) //
            .withSeparator(';') //
            .withType(Booking.class) //
            .withSkipLines(CSV_HEADER_LINES) //
            .build().parse();

      LOGGER.info("Imported {} bookings", bookings.size());
      filterNegativeInplace(bookings);
      LOGGER.info("Removed positive bookings, left: {} bookings", bookings.size());

      return bookings;
   }

   public void filterNegativeInplace(List<Booking> list) {
      list.removeIf(booking -> booking.getBetrag().compareTo(BigDecimal.ZERO) > 0);
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

      bookings.stream().filter(booking -> booking.getMatchedCriteria().isEmpty())
            .map(booking -> booking.getAuftraggeber() + " < -- > " + booking.getVerwendungszweck()).distinct().sorted()
            .forEach(booking -> LOGGER.info("Unmatched booking: {}", booking));
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
         if (filterCriterion.getMatchingCriterion() == FilterCriterion.MatchingCriterion.AUFTRAGGEBER) {
            matchBookingValueAgainstCriterion(booking, filterCriterion, booking.getAuftraggeber());

         } else if (filterCriterion.getMatchingCriterion() == FilterCriterion.MatchingCriterion.VERWENDUNGSZWECK) {
            matchBookingValueAgainstCriterion(booking, filterCriterion, booking.getVerwendungszweck());

         } else if (filterCriterion.getMatchingCriterion() == FilterCriterion.MatchingCriterion.NOTIZ) {
            matchBookingValueAgainstCriterion(booking, filterCriterion, booking.getNotiz());

         } else {
            throw new IllegalArgumentException("Unknown: " + filterCriterion.getMatchingCriterion());
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
         booking.getMatchedCriteria().add(filterCriterion);
         if (booking.getMatchedCriteria().size() > 1) {
            LOGGER.warn("{} matching multiple Criteria: {}", booking, booking.getMatchedCriteria());
         }

      }
   }

}
