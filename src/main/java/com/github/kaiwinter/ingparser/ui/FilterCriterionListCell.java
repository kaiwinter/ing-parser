package com.github.kaiwinter.ingparser.ui;

import com.github.kaiwinter.ingparser.config.FilterCriterion;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class FilterCriterionListCell implements Callback<ListView<FilterCriterion>, ListCell<FilterCriterion>> {

   @Override
   public ListCell<FilterCriterion> call(ListView<FilterCriterion> param) {
      return new LambdaListCell<>(crit -> crit.getMatchingCriterion().toString() + ": " + crit.getPattern());
   }
}
