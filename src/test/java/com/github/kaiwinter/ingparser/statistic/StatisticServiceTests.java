package com.github.kaiwinter.ingparser.statistic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

/**
 * Tests for {@link StatisticService}.
 */
class StatisticServiceTests {

   StatisticService statisticService = new StatisticService();

   @Test
   void groupByCategory() {
      CategoryModel categoryName1 = new CategoryModel("Autraggeber-Criterion");
      CategoryModel categoryName2 = new CategoryModel("Verwendungszweck-Criterion");
      List<FilterCriterion> byAuftraggeber = FilterCriterion.byAuftraggeber(categoryName1, "shop");
      List<FilterCriterion> byVerwendungszweck = FilterCriterion.byVerwendungszweck(categoryName2, "stuff");

      Booking booking1 = new Booking();
      booking1.setAuftraggeber("shop");
      booking1.setMatchedCriteria(byAuftraggeber);

      Booking booking2 = new Booking();
      booking2.setVerwendungszweck("stuff");
      booking2.setMatchedCriteria(byVerwendungszweck);
      List<Booking> bookings = List.of(booking1, booking2);

      Map<CategoryModel, List<Booking>> groupByCategory = statisticService.groupByCategory(bookings);

      assertThat(groupByCategory) //
            .hasSize(2) //
            .containsEntry(categoryName1, List.of(booking1)) //
            .containsEntry(categoryName2, List.of(booking2));
   }

   @Test
   void groupByMonthAndCategory_1() {
      List<FilterCriterion> byAuftraggeber = FilterCriterion.byAuftraggeber("Main category", "shop");
      List<FilterCriterion> byVerwendungszweck = FilterCriterion.byVerwendungszweck("Main category", "stuff");

      Booking booking1 = createBooking(LocalDate.of(2022, 4, 10), 10, "shop", byAuftraggeber);
      Booking booking2 = createBooking(LocalDate.of(2022, 4, 15), 6, "stuff", byVerwendungszweck);

      List<Booking> bookings = List.of(booking1, booking2);
      List<String> categories = List.of("Main category");
      TestHelper.captureOutput((outContent, errContent) -> {

         statisticService.groupByMonthAndCategory(bookings, categories);

         assertEquals("""
               +------------+---------------+-------+
               |    Monat   | Main category | Summe |
               +------------+---------------+-------+
               | 2022-04-01 |            16 |    16 |
               +------------+---------------+-------+
               """.trim(), outContent.toString().trim());
      });
   }

   @Test
   void groupByMonthAndCategory_2() {
      List<FilterCriterion> byAuftraggeber = FilterCriterion.byAuftraggeber("Main category", "shop");
      List<FilterCriterion> byVerwendungszweck = FilterCriterion.byVerwendungszweck("Main category", "stuff");

      Booking booking1 = createBooking(LocalDate.of(2022, 4, 10), 10, "shop", byAuftraggeber);
      Booking booking2 = createBooking(LocalDate.of(2022, 6, 15), 6, "stuff", byVerwendungszweck);

      List<Booking> bookings = List.of(booking1, booking2);
      List<String> categories = List.of("Main category");
      TestHelper.captureOutput((outContent, errContent) -> {

         statisticService.groupByMonthAndCategory(bookings, categories);

         assertEquals("""
               +------------+---------------+-------+
               |    Monat   | Main category | Summe |
               +------------+---------------+-------+
               | 2022-04-01 |            10 |    10 |
               | 2022-06-01 |             6 |     6 |
               +------------+---------------+-------+
               """.trim(), outContent.toString().trim());
      });
   }

   @Test
   void groupByMonthAndCategory_3() {
      List<FilterCriterion> byAuftraggeber = FilterCriterion.byAuftraggeber("Main category", "shop");
      List<FilterCriterion> byVerwendungszweck = FilterCriterion.byVerwendungszweck("Another category", "stuff");

      Booking booking1 = createBooking(LocalDate.of(2022, 4, 10), 10, "shop", byAuftraggeber);
      Booking booking2 = createBooking(LocalDate.of(2022, 4, 15), 6, "stuff", byVerwendungszweck);

      List<Booking> bookings = List.of(booking1, booking2);
      List<String> categories = List.of("Main category", "Another category");
      TestHelper.captureOutput((outContent, errContent) -> {

         statisticService.groupByMonthAndCategory(bookings, categories);

         assertEquals("""
               +------------+---------------+------------------+-------+
               |    Monat   | Main category | Another category | Summe |
               +------------+---------------+------------------+-------+
               | 2022-04-01 |            10 |                6 |    16 |
               +------------+---------------+------------------+-------+
               """.trim(), outContent.toString().trim());
      });
   }

   @Test
   void groupByMonthAndCategory_4() {
      List<FilterCriterion> byAuftraggeber = FilterCriterion.byAuftraggeber("Main category", "shop");
      List<FilterCriterion> byVerwendungszweck = FilterCriterion.byVerwendungszweck("Another category", "stuff");

      Booking booking1 = createBooking(LocalDate.of(2022, 4, 10), 10, "shop", byAuftraggeber);
      Booking booking2 = createBooking(LocalDate.of(2022, 7, 15), 6, "stuff", byVerwendungszweck);

      List<Booking> bookings = List.of(booking1, booking2);
      List<String> categories = List.of("Main category", "Another category");
      TestHelper.captureOutput((outContent, errContent) -> {

         statisticService.groupByMonthAndCategory(bookings, categories);

         assertEquals("""
               +------------+---------------+------------------+-------+
               |    Monat   | Main category | Another category | Summe |
               +------------+---------------+------------------+-------+
               | 2022-04-01 |            10 |                0 |    10 |
               | 2022-07-01 |             0 |                6 |     6 |
               +------------+---------------+------------------+-------+
               """.trim(), outContent.toString().trim());
      });
   }

   private Booking createBooking(LocalDate date, int betrag, String auftraggeber,
         List<FilterCriterion> matchedCriteria) {
      Booking booking = new Booking();
      booking.setDate(date);
      booking.setBetrag(BigDecimal.valueOf(betrag));
      booking.setAuftraggeber(auftraggeber);
      booking.setMatchedCriteria(matchedCriteria);

      return booking;
   }
}
