package com.github.kaiwinter.ingparser.ui.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class BookingModel {

   private LocalDate date;
   private BigDecimal betrag;
   private String auftraggeber;
   private String verwendungszweck;
   private int matchedCriteria;

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

   public int getMatchedCriteria() {
      return matchedCriteria;
   }

   public void setMatchedCriteria(int matchedCriteria) {
      this.matchedCriteria = matchedCriteria;
   }

}