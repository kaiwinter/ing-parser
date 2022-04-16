package com.github.kaiwinter.ingparser.ui.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterCriterion {

   private final CategoryName category;
   private final Type type;
   private final String pattern;

   public static final FilterCriterion NULL_CRITERION = new FilterCriterion(new CategoryName("unmatched"), null, null);

   public enum Type {
      BY_AUFTRAGGEBER, BY_VERWENDUNGSZWECK
   }

   private FilterCriterion(CategoryName category, Type type, String pattern) {
      this.category = category;
      this.type = type;
      this.pattern = pattern;
   }

   public static List<FilterCriterion> byAuftraggeber(String category, String... pattern) {
      return byAuftraggeber(new CategoryName(category), pattern);
   }

   public static List<FilterCriterion> byAuftraggeber(CategoryName category, String... pattern) {
      return Arrays.asList(pattern).stream()
            .map(string -> new FilterCriterion(category, Type.BY_AUFTRAGGEBER, string.trim()))
            .collect(Collectors.toList());
   }

   public static List<FilterCriterion> byVerwendungszweck(String category, String... pattern) {
      return byVerwendungszweck(new CategoryName(category), pattern);
   }

   public static List<FilterCriterion> byVerwendungszweck(CategoryName category, String... pattern) {
      return Arrays.asList(pattern).stream()
            .map(string -> new FilterCriterion(category, Type.BY_VERWENDUNGSZWECK, string.trim()))
            .collect(Collectors.toList());
   }

   public CategoryName getCategory() {
      return category;
   }

   public Type getType() {
      return type;
   }

   public String getPattern() {
      return pattern;
   }

   @Override
   public String toString() {
      return "FilterCriterion [category=" + category + ", type=" + type + ", pattern=" + pattern + "]";
   }

}
