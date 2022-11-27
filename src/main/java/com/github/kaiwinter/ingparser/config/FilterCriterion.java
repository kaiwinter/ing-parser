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

   public static final FilterCriterion UNMATCHED_CRITERION = new FilterCriterion(CategoryModel.UNMATCHED_CATEGORY, null,
         null);

   /**
    * Defines on which criterion the pattern will be tested.
    */
   public enum MatchingCriterion {
      AUFTRAGGEBER, VERWENDUNGSZWECK, NOTIZ
   }

   private FilterCriterion(CategoryModel category, MatchingCriterion matchingCriterion, String pattern) {
      this.category = category;
      this.matchingCriterion = matchingCriterion;
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

   public static List<FilterCriterion> byNotiz(String category, String... pattern) {
      return byNotiz(new CategoryModel(category), pattern);
   }

   public static List<FilterCriterion> byNotiz(CategoryModel category, String... pattern) {
      return Arrays.asList(pattern).stream()
            .map(string -> new FilterCriterion(category, MatchingCriterion.NOTIZ, string.trim()))
            .collect(Collectors.toList());
   }

   public boolean isIgnoreFilterCriterion() {
      return category.isIgnoreCategory();
   }

   public boolean isUnmatchedFilterCriterion() {
      return category.isUnmatchedCategory();
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
      return "FilterCriterion [pattern=" + pattern + ", matchingCriterion=" + matchingCriterion + ", category="
            + category + "]";
   }

}
