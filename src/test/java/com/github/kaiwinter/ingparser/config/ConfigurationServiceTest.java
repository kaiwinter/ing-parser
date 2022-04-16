package com.github.kaiwinter.ingparser.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

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
            .extracting(CategoryModel::getName) //
            .containsOnly("Lebensmittel", "Supermarkt", "Restaurant", "Handwerker");

      // Should have "Lebensmittel" as parent category
      assertThat(filterCriteria)//
            .extracting(FilterCriterion::getCategory) //
            .filteredOn(category -> category.getName().equals("Supermarkt") || category.getName().equals("Restaurant")) //
            .extracting(CategoryModel::getParentCategoryName) //
            .containsOnly("Lebensmittel");

      // Shouldn't have a parent category
      assertThat(filterCriteria)//
            .extracting(FilterCriterion::getCategory) //
            .filteredOn(
                  category -> category.getName().equals("Lebensmittel") || category.getName().equals("Handwerker")) //
            .extracting(CategoryModel::getParentCategoryName) //
            .containsOnlyNulls();
   }
}
