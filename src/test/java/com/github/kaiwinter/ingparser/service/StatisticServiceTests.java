package com.github.kaiwinter.ingparser.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.kaiwinter.ingparser.model.Booking;
import com.github.kaiwinter.ingparser.model.FilterCriterion;

/**
 * Tests for {@link StatisticService}.
 */
class StatisticServiceTests {

   StatisticService statisticService = new StatisticService();

   @Test
   void groupByCategory() {
      List<FilterCriterion> byAuftraggeber = FilterCriterion.byAuftraggeber("Autraggeber-Criterion", "shop");
      List<FilterCriterion> byVerwendungszweck = FilterCriterion.byVerwendungszweck("Verwendungszweck-Criterion",
            "stuff");

      Booking booking1 = new Booking();
      booking1.setAuftraggeber("shop");
      booking1.setMatchedCriteria(byAuftraggeber);

      Booking booking2 = new Booking();
      booking2.setVerwendungszweck("stuff");
      booking2.setMatchedCriteria(byVerwendungszweck);
      List<Booking> bookings = List.of(booking1, booking2);

      Map<String, List<Booking>> groupByCategory = statisticService.groupByCategory(bookings);

      assertThat(groupByCategory) //
            .hasSize(2) //
            .containsEntry("Autraggeber-Criterion", List.of(booking1)) //
            .containsEntry("Verwendungszweck-Criterion", List.of(booking2));
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
