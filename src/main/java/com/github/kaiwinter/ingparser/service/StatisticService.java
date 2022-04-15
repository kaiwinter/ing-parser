package com.github.kaiwinter.ingparser.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
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

   public void groupByMonthAndCategory(List<Booking> bookings, List<String> categories) {

      // Group by month
      Map<LocalDate, List<Booking>> byMonth = bookings.stream()
            .collect(Collectors.groupingBy(date -> date.date.withDayOfMonth(1)));

      List<String> headerRow = new ArrayList<>();
      headerRow.add("Monat");
      headerRow.addAll(categories);
      headerRow.add("Summe");

      List<String[]> dataRows = new ArrayList<>();
      for (Entry<LocalDate, List<Booking>> entry : new TreeMap<>(byMonth).entrySet()) {

         List<String> dataRow = new ArrayList<>();
         dataRow.add(entry.getKey().toString());
         BigDecimal grandTotal = BigDecimal.ZERO;
         for (String category : categories) {
            BigDecimal total = entry.getValue().stream() //
                  .filter(booking -> filterByCategory(booking, category)) //
                  .map(Booking::getBetrag) //
                  .reduce(BigDecimal.ZERO, BigDecimal::add);
            dataRow.add(total.toString());
            grandTotal = grandTotal.add(total);
         }
         dataRow.add(grandTotal.toString());
         dataRows.add(dataRow.toArray(new String[0]));
      }

      printTable(headerRow, dataRows);
   }

   private void printTable(List<String> headerList, List<String[]> datas) {
      String[] header = headerList.toArray(new String[0]);
      String[][] data = datas.toArray(new String[0][0]);

      ASCIITable.getInstance().printTable(header, data);
   }

   private boolean filterByCategory(Booking booking, String category) {
      return booking.matchedCriteria.stream().findFirst()
            .orElseThrow(() -> new NoSuchElementException("No matched filter criteria: " + booking)).getCategory()
            .equals(category);
   }

}
