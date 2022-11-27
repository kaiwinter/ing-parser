package com.github.kaiwinter.ingparser.ui;

import com.github.kaiwinter.ingparser.config.FilterCriterion;
import com.github.kaiwinter.ingparser.config.FilterCriterion.MatchingCriterion;
import com.github.kaiwinter.ingparser.csv.Booking;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class NewFilterCriterionViewModel implements ViewModel {
   private final ListProperty<CategoryModel> categories = new SimpleListProperty<>();
   private final ListProperty<Booking> bookings = new SimpleListProperty<>();
   private final ListProperty<FilterCriterion> filterCriteria = new SimpleListProperty<>();

   private final ListProperty<MatchingCriterion> matchingCriteria = new SimpleListProperty<>(
         FXCollections.observableArrayList(MatchingCriterion.AUFTRAGGEBER, MatchingCriterion.VERWENDUNGSZWECK,
               MatchingCriterion.NOTIZ, MatchingCriterion.IDENTITY));

   public ListProperty<CategoryModel> categoriesProperty() {
      return this.categories;
   }

   public ListProperty<Booking> bookingsProperty() {
      return this.bookings;
   }

   public ListProperty<FilterCriterion> filterCriteriaProperty() {
      return this.filterCriteria;
   }

   public ListProperty<MatchingCriterion> matchingCriteriaProperty() {
      return this.matchingCriteria;
   }
}
