package com.github.kaiwinter.ingparser.model;

import java.util.List;

/**
 * One instance represents one object from the configuration file. This object contains one List of Strings which match
 * against the Auftraggeber of a booking. If a booking matches this rule it is grouped into a category which name is
 * defined by this object. The second List of Strings match against the Verwendungszweck. The method
 * {@link #getCombinedFilterCriteria()} creates one List of {@link FilterCriterion} objects from these two Lists.
 */
public class Category {

   private String name;
   private List<String> auftraggeber;
   private List<String> verwendungszweck;

   public String getName() {
      return name;
   }

   private List<FilterCriterion> getAuftraggeberAsFilterCriteria() {
      return FilterCriterion.byAuftraggeber(name, auftraggeber.toArray(new String[0]));
   }

   private List<FilterCriterion> getVerwendungszweckAsFilterCriteria() {
      return FilterCriterion.byVerwendungszweck(name, verwendungszweck.toArray(new String[0]));
   }

   /**
    * @return List of {@link FilterCriterion} which is a combination of filter criteria for Auftraggeber and
    *         Verwendungszweck
    */
   public List<FilterCriterion> getCombinedFilterCriteria() {
      List<FilterCriterion> filterCriterria = getAuftraggeberAsFilterCriteria();
      filterCriterria.addAll(getVerwendungszweckAsFilterCriteria());
      return filterCriterria;
   }
}
