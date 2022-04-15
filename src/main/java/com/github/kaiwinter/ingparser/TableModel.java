package com.github.kaiwinter.ingparser;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TableModel {

   private LocalDate date;
   private BigDecimal betrag;
   private String auftraggeber;
   private String verwendungszweck;

   public LocalDate getDate() {
      return date;
   }

   public void setDate(LocalDate date) {
      this.date = date;
   }

   public BigDecimal getBetrag() {
      return betrag;
   }

   public void setBetrag(BigDecimal betrag) {
      this.betrag = betrag;
   }

   public String getAuftraggeber() {
      return auftraggeber;
   }

   public void setAuftraggeber(String auftraggeber) {
      this.auftraggeber = auftraggeber;
   }

   public String getVerwendungszweck() {
      return verwendungszweck;
   }

   public void setVerwendungszweck(String verwendungszweck) {
      this.verwendungszweck = verwendungszweck;
   }

}