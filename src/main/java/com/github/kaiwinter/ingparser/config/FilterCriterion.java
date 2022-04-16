package com.github.kaiwinter.ingparser.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

/**
 * {@link FilterCriterion} objects are build on base of a {@link CategoryConfiguration}. While a
 * {@link CategoryConfiguration} contains one category name and a List of String-based criteria a
 * {@link FilterCriterion} stores exactly one category name ({@link #category} and also just one criterion
 * ({@link #pattern}.
 */
public class FilterCriterion {

   private final CategoryModel category;
   private final MatchingCriterion matchingCriterion;
   private final String pattern;

   public static final FilterCriterion NULL_CRITERION = new FilterCriterion(new CategoryModel("unmatched"), null, null);

   /**
    * Defines on which criterion the pattern will be tested.
    */
   public enum MatchingCriterion {
      AUFTRAGGEBER, VERWENDUNGSZWECK
   }

   private FilterCriterion(CategoryModel category, MatchingCriterion type, String pattern) {
      this.category = category;
      this.matchingCriterion = type;
      this.pattern = pattern;
   }

   public static List<FilterCriterion> byAuftraggeber(String category, String... pattern) {
      return byAuftraggeber(new CategoryModel(category), pattern);
   }

   public static List<FilterCriterion> byAuftraggeber(CategoryModel category, String... pattern) {
      return Arrays.asList(pattern).stream()
            .map(string -> new FilterCriterion(category, MatchingCriterion.AUFTRAGGEBER, string.trim()))
            .collect(Collectors.toList());
   }

   public static List<FilterCriterion> byVerwendungszweck(String category, String... pattern) {
      return byVerwendungszweck(new CategoryModel(category), pattern);
   }

   public static List<FilterCriterion> byVerwendungszweck(CategoryModel category, String... pattern) {
      return Arrays.asList(pattern).stream()
            .map(string -> new FilterCriterion(category, MatchingCriterion.VERWENDUNGSZWECK, string.trim()))
            .collect(Collectors.toList());
   }

   public CategoryModel getCategory() {
      return category;
   }

   public MatchingCriterion getMatchingCriterion() {
      return matchingCriterion;
   }

   public String getPattern() {
      return pattern;
   }

   @Override
   public String toString() {
      return "FilterCriterion [category=" + category + ", matchingCriterion=" + matchingCriterion + ", pattern="
            + pattern + "]";
   }

}
