package com.github.kaiwinter.ingparser.statistic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.github.freva.asciitable.AsciiTable;
import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

public class StatisticService {

   /**
    * Creates a Map with the category as key and a List of Bookings as value. If a Booking matches two categories it is
    * listed two times.
    *
    * @param bookings
    * @return
    */
   public Map<CategoryModel, List<Booking>> groupByCategory(List<Booking> bookings) {

      bookings.forEach(booking -> {
         if (booking.getMatchedCriteria().stream().anyMatch(filterCriterion -> FilterCriterion.IGNORE_CATEGORY_CAPTION
               .equals(filterCriterion.getCategory().getName()))) {
            for (Iterator<FilterCriterion> iterator = booking.getMatchedCriteria().iterator(); iterator.hasNext();) {
               FilterCriterion fc = iterator.next();
               if (!FilterCriterion.IGNORE_CATEGORY_CAPTION.equals(fc.getCategory().getName())) {
                  iterator.remove();
               }
            }
         }
      });

      // TODO: if there is more than one sub-level, this fails:
      Map<CategoryModel, List<Booking>> groupToProductMapping = bookings.stream()
            .flatMap(booking -> booking.getMatchedCriteria().stream()
                  .map(filterCriterion -> new AbstractMap.SimpleEntry<>(filterCriterion.getCategory(), booking)))
            .collect(Collectors.groupingBy(Map.Entry::getKey,
                  Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

      return groupToProductMapping;
   }

   public void groupByMonthAndCategory(List<Booking> bookings, List<String> categories) {

      // Group by month
      Map<LocalDate, List<Booking>> byMonth = bookings.stream()
            .collect(Collectors.groupingBy(booking -> booking.getDate().withDayOfMonth(1)));

      List<String> headerRow = new ArrayList<>();
      headerRow.add("Monat");
      headerRow.addAll(categories);
      headerRow.add("Summe");

      List<String[]> dataRows = new ArrayList<>();
      for (Entry<LocalDate, List<Booking>> entry : new TreeMap<>(byMonth).entrySet()) {

         List<String> dataRow = new ArrayList<>();
         dataRow.add(entry.getKey().format(DateTimeFormatter.ofPattern("yyyy-MM")));
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

      System.out.println(AsciiTable.getTable(AsciiTable.BASIC_ASCII_NO_DATA_SEPARATORS, header, new String[] {}, data));

   }

   private boolean filterByCategory(Booking booking, String category) {
      boolean includeSubCategories = false;
      if (includeSubCategories) {
         return booking.getMatchedCriteria().stream()
               .anyMatch(booking2 -> category.equals(booking2.getCategory().getName()));
      } else {
         return booking.getMatchedCriteria().stream()
               .anyMatch(booking2 -> (category.equals(booking2.getCategory().getName()))
                     || category.equals(booking2.getCategory().getParentCategoryName()));
      }

   }

}
