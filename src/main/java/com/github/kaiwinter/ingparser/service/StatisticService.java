package com.github.kaiwinter.ingparser.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.bethecoder.ascii_table.ASCIITable;
import com.github.kaiwinter.ingparser.model.Booking;

public class StatisticService {

   /**
    * Groups the bookings in the following order by category and month:
    * <ol>
    * <li>category</li>
    * <li>month</li>
    * </ol>
    * 
    * @param bookings
    */
   public void groupByCategoryAndMonth(List<Booking> bookings) {
      Map<String, List<Booking>> category2Bookings = bookings.stream()
            .filter(booking -> !booking.matchedCriteria.isEmpty())
            .collect(Collectors.groupingBy(booking -> booking.matchedCriteria.get(0).getCategory()));

      category2Bookings.remove("ignore");
      List<Booking> values = category2Bookings.values().stream().flatMap(Collection::stream)
            .collect(Collectors.toList());
      values.sort(Comparator.comparingDouble(booking -> booking.betrag.doubleValue()));

      for (Entry<String, List<Booking>> entry : category2Bookings.entrySet()) {
         System.out.println(entry.getKey() + ", " + entry.getValue().size() + " Buchungen, Summe: "
               + entry.getValue().stream().map(booking -> booking.betrag).reduce(BigDecimal.ZERO, BigDecimal::add));

         Map<LocalDate, List<Booking>> byMonth = entry.getValue().stream()
               .collect(Collectors.groupingBy(date -> date.date.withDayOfMonth(1)));

         TreeMap<LocalDate, List<Booking>> sortedMap = new TreeMap<>(byMonth);
         for (Entry<LocalDate, List<Booking>> monthEntry : sortedMap.entrySet()) {
            System.out.println(
                  monthEntry.getKey() + ", " + monthEntry.getValue().size() + " Buchungen, Summe: " + monthEntry
                        .getValue().stream().map(booking -> booking.betrag).reduce(BigDecimal.ZERO, BigDecimal::add));
         }

         System.out.println();
         System.out.println();
      }
   }

   public void groupByMonthAndCategory(List<Booking> bookings) {
      Map<LocalDate, List<Booking>> byMonth = bookings.stream()
            .collect(Collectors.groupingBy(date -> date.date.withDayOfMonth(1)));

      // Group 1. by date, 2. by category
      List<String[]> datas = new ArrayList<>();
      for (Entry<LocalDate, List<Booking>> entry : new TreeMap<>(byMonth).entrySet()) {

         BigDecimal freizeitSum = entry.getValue().stream()
               .filter(booking -> booking.matchedCriteria.get(0).getCategory().equals("Freizeit"))
               .map(Booking::getBetrag) //
               .reduce(BigDecimal.ZERO, BigDecimal::add);

         BigDecimal einkaufeSum = entry.getValue().stream()
               .filter(booking -> booking.matchedCriteria.get(0).getCategory().equals("Einkäufe"))
               .map(Booking::getBetrag) //
               .reduce(BigDecimal.ZERO, BigDecimal::add);

         BigDecimal lebensmittelSum = entry.getValue().stream()
               .filter(booking -> booking.matchedCriteria.get(0).getCategory().equals("Lebensmittel"))
               .map(Booking::getBetrag) //
               .reduce(BigDecimal.ZERO, BigDecimal::add);

         datas.add(new String[] { entry.getKey().toString(), freizeitSum.toString(), einkaufeSum.toString(),
               lebensmittelSum.toString(), freizeitSum.add(einkaufeSum).add(lebensmittelSum).toString() });
      }

      String[] header = { "Monat", "Freizeit", "Einkäufe", "Lebensmittel", "Summe" };

      String[][] data = datas.toArray(new String[0][0]);

      ASCIITable.getInstance().printTable(header, data);
   }
}
