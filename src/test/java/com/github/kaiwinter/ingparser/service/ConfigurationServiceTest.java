package com.github.kaiwinter.ingparser.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.kaiwinter.ingparser.model.FilterCriterion;

/**
 * Tests for {@link ConfigurationService}.
 */
class ConfigurationServiceTest {

   /**
    * Tests if sub-categories are parsed correctly.
    */
   @Test
   void subCriteria() {
      ConfigurationService configurationService = new ConfigurationService();
      List<FilterCriterion> filterCriteria = configurationService.readConfiguration("/config_subcriteria.json");

      assertThat(filterCriteria).hasSize(4);

      assertThat(filterCriteria) //
            .extracting(FilterCriterion::getCategory) //
            .containsOnly("Lebensmittel", "Supermarkt", "Restaurant", "Handwerker");

      // Should have "Lebensmittel" as parent category
      assertThat(filterCriteria)//
            .filteredOn(crit -> crit.getCategory().equals("Supermarkt") || crit.getCategory().equals("Restaurant")) //
            .extracting(FilterCriterion::getParentCategory) //
            .containsOnly("Lebensmittel");

      // Shouldn't have a parent category
      assertThat(filterCriteria)//
            .filteredOn(crit -> crit.getCategory().equals("Lebensmittel") || crit.getCategory().equals("Handwerker")) //
            .extracting(FilterCriterion::getParentCategory) //
            .containsOnlyNulls();
   }
}
