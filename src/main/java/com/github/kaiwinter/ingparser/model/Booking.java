package com.github.kaiwinter.ingparser.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvNumber;

/**
 * One {@link Booking} represents one line from the CSV file after it was parsed. The List {@link #matchedCriteria}
 * contains one or multiple {@link FilterCriterion} which were defined by the configuration file. If the list contains
 * multiple {@link FilterCriterion} objects this indicates, that two matching rules match the same booking. Since this
 * messes up the statistics this should be considered as a faulty configuration.
 */
public class Booking {

   @CsvBindByPosition(position = 0)
   @CsvDate(value = "dd.MM.yyyy")
   public LocalDate date;

   @CsvBindByPosition(position = 2)
   public String auftraggeber;

   @CsvBindByPosition(position = 3)
   public String buchungstext;

   @CsvBindByPosition(position = 5)
   public String verwendungszweck;

   @CsvBindByPosition(position = 6)
   @CsvNumber(value = "#0.00")
   public BigDecimal saldo;

   @CsvBindByPosition(position = 8)
   @CsvNumber(value = "#0.00")
   public BigDecimal betrag;

   public List<FilterCriterion> matchedCriteria = new ArrayList<>();

   @Override
   public String toString() {
      return "Booking [date=" + date + ", auftraggeber=" + auftraggeber + ", buchungstext=" + buchungstext
            + ", verwendungszweck=" + verwendungszweck + ", saldo=" + saldo + ", betrag=" + betrag + "]";
   }
}
