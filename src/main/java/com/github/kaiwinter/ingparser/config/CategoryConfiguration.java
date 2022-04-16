package com.github.kaiwinter.ingparser.config;

import java.util.ArrayList;
import java.util.List;

/**
 * One instance represents one object from the configuration file. This object contains one List of Strings which match
 * against the Auftraggeber of a booking. If a booking matches this rule it is grouped into a category which name is
 * defined by this object. The second List of Strings match against the Verwendungszweck. The method
 * {@link #getCombinedFilterCriteria()} creates one List of {@link FilterCriterion} objects from these two Lists.
 */
public class CategoryConfiguration {

   private String categoryName;
   private List<String> auftraggeberPattern = new ArrayList<>();
   private List<String> verwendungszweckPattern = new ArrayList<>();
   private List<CategoryConfiguration> subCategories = new ArrayList<>();

   public List<String> getAuftraggeberPattern() {
      return auftraggeberPattern;
   }

   public List<String> getVerwendungszweckPattern() {
      return verwendungszweckPattern;
   }

   public List<CategoryConfiguration> getSubCategories() {
      return subCategories;
   }

   public String getCategoryName() {
      return categoryName;
   }

   public void setCategoryName(String categoryName) {
      this.categoryName = categoryName;
   }

   private List<FilterCriterion> getAuftraggeberAsFilterCriteria() {
      return FilterCriterion.byAuftraggeber(categoryName, auftraggeberPattern.toArray(new String[0]));
   }

   private List<FilterCriterion> getVerwendungszweckAsFilterCriteria() {
      return FilterCriterion.byVerwendungszweck(categoryName, verwendungszweckPattern.toArray(new String[0]));
   }

   /**
    * @return List of {@link FilterCriterion} which is a combination of filter criteria for Auftraggeber and
    *         Verwendungszweck
    */
   public List<FilterCriterion> getCombinedFilterCriteria() {
      List<FilterCriterion> filterCriteria = getAuftraggeberAsFilterCriteria();
      filterCriteria.addAll(getVerwendungszweckAsFilterCriteria());

      for (CategoryConfiguration subCategory : subCategories) {
         List<FilterCriterion> combinedSubFilterCriteria = subCategory.getCombinedFilterCriteria();
         combinedSubFilterCriteria.forEach(crit -> crit.getCategory().setParentCategoryName(categoryName));

         // Add sub-categories to categories
         for (FilterCriterion f : filterCriteria) {
            f.getCategory().getSubCategories()
                  .addAll(combinedSubFilterCriteria.stream().map(FilterCriterion::getCategory).toList());
         }

         filterCriteria.addAll(combinedSubFilterCriteria);
      }

      return filterCriteria;
   }
}
