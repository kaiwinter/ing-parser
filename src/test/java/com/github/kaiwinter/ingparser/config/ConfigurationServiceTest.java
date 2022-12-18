package com.github.kaiwinter.ingparser.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
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
      List<FilterCriterion> filterCriteria = configurationService
            .readConfiguration(ConfigurationServiceTest.class.getResourceAsStream("/config_subcriteria.json"));

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

   /**
    * Tests if loading and saving results in the same JSON string.
    */
   @Test
   void saveFilterCriteriaToFile() throws IOException {
      ConfigurationService configurationService = new ConfigurationService();
      List<FilterCriterion> filterCriteria = configurationService
            .readConfiguration(ConfigurationServiceTest.class.getResourceAsStream("/config_subcriteria.json"));

      StringWriter writer = new StringWriter();
      configurationService.saveFilterCriteriaToFile(filterCriteria, writer);
      String saveFilterCriteriaToFile = writer.toString();

      assertEquals("""
            [
              {
                "categoryName": "Lebensmittel",
                "auftraggeberPattern": [],
                "verwendungszweckPattern": [
                  "Brötchen"
                ],
                "notizPattern": [],
                "identityPattern": [],
                "subCategories": [
                  {
                    "categoryName": "Supermarkt",
                    "auftraggeberPattern": [
                      "Supermarkt"
                    ],
                    "verwendungszweckPattern": [],
                    "notizPattern": [],
                    "identityPattern": [],
                    "subCategories": []
                  },
                  {
                    "categoryName": "Restaurant",
                    "auftraggeberPattern": [
                      "Restaurant"
                    ],
                    "verwendungszweckPattern": [],
                    "notizPattern": [],
                    "identityPattern": [],
                    "subCategories": []
                  }
                ]
              },
              {
                "categoryName": "Handwerker",
                "auftraggeberPattern": [
                  "Bäckermann"
                ],
                "verwendungszweckPattern": [],
                "notizPattern": [],
                "identityPattern": [],
                "subCategories": []
              }
            ]""", saveFilterCriteriaToFile);
   }
}
