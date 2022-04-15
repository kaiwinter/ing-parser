package com.github.kaiwinter.ingparser.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FilterCriterion {

   private final String category;
   private final Type type;
   private final String pattern;

   public static final FilterCriterion NULL_CRITERION = new FilterCriterion("unmatched", null, null);

   public static enum Type {
      BY_AUFTRAGGEBER, BY_VERWENDUNGSZWECK
   }

   private FilterCriterion(String category, Type type, String pattern) {
      this.category = category;
      this.type = type;
      this.pattern = pattern;
   }

   public static List<FilterCriterion> byAuftraggeber(String category, String... pattern) {
      return Arrays.asList(pattern).stream()
            .map(string -> new FilterCriterion(category, Type.BY_AUFTRAGGEBER, string.trim()))
            .collect(Collectors.toList());
   }

   public static List<FilterCriterion> byVerwendungszweck(String category, String... pattern) {
      return Arrays.asList(pattern).stream()
            .map(string -> new FilterCriterion(category, Type.BY_VERWENDUNGSZWECK, string.trim()))
            .collect(Collectors.toList());
   }

   public String getCategory() {
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
