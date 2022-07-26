package com.github.kaiwinter.ingparser.csv;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
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
   private LocalDate date;

   @CsvBindByPosition(position = 2)
   private String auftraggeber;

   @CsvBindByPosition(position = 3)
   private String buchungstext;

   @CsvBindByPosition(position = 4)
   private String notiz;

   @CsvBindByPosition(position = 5)
   private String verwendungszweck;

   @CsvBindByPosition(position = 6)
   @CsvNumber(value = "#0.00")
   private BigDecimal saldo;

   @CsvBindByPosition(position = 8)
   @CsvNumber(value = "#0.00")
   private BigDecimal betrag;

   private List<FilterCriterion> matchedCriteria = new ArrayList<>();

   public LocalDate getDate() {
      return date;
   }

   public void setDate(LocalDate date) {
      this.date = date;
   }

   public String getAuftraggeber() {
      return auftraggeber;
   }

   public void setAuftraggeber(String auftraggeber) {
      this.auftraggeber = auftraggeber;
   }

   public String getBuchungstext() {
      return buchungstext;
   }

   public void setBuchungstext(String buchungstext) {
      this.buchungstext = buchungstext;
   }

   public String getVerwendungszweck() {
      return verwendungszweck;
   }

   public String getNotiz() {
      return notiz;
   }

   public void setNotiz(String notiz) {
      this.notiz = notiz;
   }

   public void setVerwendungszweck(String verwendungszweck) {
      this.verwendungszweck = verwendungszweck;
   }

   public BigDecimal getSaldo() {
      return saldo;
   }

   public void setSaldo(BigDecimal saldo) {
      this.saldo = saldo;
   }

   public BigDecimal getBetrag() {
      return betrag;
   }

   public void setBetrag(BigDecimal betrag) {
      this.betrag = betrag;
   }

   public List<FilterCriterion> getMatchedCriteria() {
      return matchedCriteria;
   }

   public void setMatchedCriteria(List<FilterCriterion> matchedCriteria) {
      this.matchedCriteria = matchedCriteria;
   }

   @Override
   public String toString() {
      return "Booking [date=" + date + ", auftraggeber=" + auftraggeber + ", buchungstext=" + buchungstext
            + ", verwendungszweck=" + verwendungszweck + ", saldo=" + saldo + ", betrag=" + betrag + "]";
   }
}
