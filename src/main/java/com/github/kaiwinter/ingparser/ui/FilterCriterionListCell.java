package com.github.kaiwinter.ingparser.ui;

import com.github.kaiwinter.ingparser.config.FilterCriterion;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class FilterCriterionListCell implements Callback<ListView<FilterCriterion>, ListCell<FilterCriterion>> {

   private final Type type;

   enum Type {
      SHORT, LONG
   }

   public FilterCriterionListCell(Type type) {
      this.type = type;
   }

   @Override
   public ListCell<FilterCriterion> call(ListView<FilterCriterion> param) {
      if (type == Type.SHORT) {
         return new LambdaListCell<>(crit -> crit.getMatchingCriterion().toString() + ": " + crit.getPattern());
      } else if (type == Type.LONG) {
         return new LambdaListCell<>(crit -> crit.getMatchingCriterion().toString() + ": " + crit.getPattern() + " ("
               + crit.getCategory().getName() + ")");
      }
      throw new IllegalArgumentException("Unknown type: " + type);
   }
}
