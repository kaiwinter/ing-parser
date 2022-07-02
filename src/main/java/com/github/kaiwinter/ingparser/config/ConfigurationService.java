package com.github.kaiwinter.ingparser.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.kaiwinter.ingparser.config.FilterCriterion.MatchingCriterion;
import com.github.kaiwinter.ingparser.ui.model.CategoryModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Reads the configuration from a file and returns the parsed {@link FilterCriterion} object.
 */
public class ConfigurationService {

   /**
    * Reads the configuration which defines how groups should be built from the CSV file.
    *
    * @param configFile file name of the configuration file
    * @return List of filter criteria
    */
   public List<FilterCriterion> readConfiguration(InputStream inputStream) {
      Reader fileReader = new InputStreamReader(inputStream);
      CategoryConfiguration[] categories = new Gson().fromJson(fileReader, CategoryConfiguration[].class);

      List<FilterCriterion> filterCriteria = new ArrayList<>();

      for (CategoryConfiguration category : categories) {
         filterCriteria.addAll(category.getCombinedFilterCriteria());
      }

      return filterCriteria;
   }

   /**
    * Transforms {@link FilterCriterion} objects back to {@link CategoryConfiguration} in order to store them in a file.
    * 
    * @throws IOException
    */
   public void saveFilterCriteriaToFile(List<FilterCriterion> filterCriteria, Writer writer) throws IOException {
      String json = filterCriteriaToString(filterCriteria);
      writer.write(json);
   }

   private String filterCriteriaToString(List<FilterCriterion> filterCriteria) {
      Map<String, CategoryConfiguration> categories = new HashMap<>();

      Map<String, CategoryConfiguration> subcategories = new HashMap<>();

      for (FilterCriterion filterCriterion : filterCriteria) {

         String parentCategoryName = filterCriterion.getCategory().getParentCategoryName();
         if (parentCategoryName == null) {
            // Create top level criterion (if not already exists)
            CategoryConfiguration categoryConfiguration = categories
                  .computeIfAbsent(filterCriterion.getCategory().getName(), name -> {
                     CategoryConfiguration newCategoryConfiguration = new CategoryConfiguration();
                     newCategoryConfiguration.setCategoryName(name);
                     return newCategoryConfiguration;
                  });

            // Add pattern to criterion
            if (filterCriterion.getMatchingCriterion() == MatchingCriterion.AUFTRAGGEBER) {
               categoryConfiguration.getAuftraggeberPattern().add(filterCriterion.getPattern());
            } else if (filterCriterion.getMatchingCriterion() == MatchingCriterion.VERWENDUNGSZWECK) {
               categoryConfiguration.getVerwendungszweckPattern().add(filterCriterion.getPattern());
            } else if (filterCriterion.getMatchingCriterion() == MatchingCriterion.NOTIZ) {
               categoryConfiguration.getNotizPattern().add(filterCriterion.getPattern());
            } else {
               throw new IllegalArgumentException("Unknown: " + filterCriterion.getMatchingCriterion());
            }

            // Create placeholder for sub-category if not already exists
            for (CategoryModel subCategoryModel : filterCriterion.getCategory().getSubCategories()) {
               if (categoryConfiguration.getSubCategories().stream().map(CategoryConfiguration::getCategoryName)
                     .noneMatch(name -> name.equals(subCategoryModel.getName()))) {
                  CategoryConfiguration categoryConfigurationSub = new CategoryConfiguration();
                  categoryConfigurationSub.setCategoryName(subCategoryModel.getName());
                  categoryConfiguration.getSubCategories().add(categoryConfigurationSub);
               }
            }

         } else {
            // Create top level criterion (if not already exists)
            CategoryConfiguration categoryConfiguration = subcategories
                  .computeIfAbsent(filterCriterion.getCategory().getName(), name -> {
                     CategoryConfiguration newCategoryConfiguration = new CategoryConfiguration();
                     newCategoryConfiguration.setCategoryName(name);
                     return newCategoryConfiguration;
                  });

            // Add pattern to criterion
            if (filterCriterion.getMatchingCriterion() == MatchingCriterion.AUFTRAGGEBER) {
               categoryConfiguration.getAuftraggeberPattern().add(filterCriterion.getPattern());
            } else if (filterCriterion.getMatchingCriterion() == MatchingCriterion.VERWENDUNGSZWECK) {
               categoryConfiguration.getVerwendungszweckPattern().add(filterCriterion.getPattern());
            } else if (filterCriterion.getMatchingCriterion() == MatchingCriterion.NOTIZ) {
               categoryConfiguration.getNotizPattern().add(filterCriterion.getPattern());
            } else {
               throw new IllegalArgumentException("Unknown: " + filterCriterion.getMatchingCriterion());
            }

            // Create placeholder for sub-category if not already exists
            for (CategoryModel subCategoryModel : filterCriterion.getCategory().getSubCategories()) {
               if (filterCriterion.getCategory().getSubCategories().stream().map(CategoryModel::getName)
                     .noneMatch(name -> name.equals(subCategoryModel.getName()))) {
                  CategoryConfiguration categoryConfigurationSub = new CategoryConfiguration();
                  categoryConfigurationSub.setCategoryName(subCategoryModel.getName());
                  categoryConfiguration.getSubCategories().add(categoryConfigurationSub);
               }
            }
         }
      }

      // Link sub-categories
      // TODO: implement this for more than one sub-level

      // Link sub-categories to top level categories
      for (CategoryConfiguration topcat : categories.values()) {
         topcat.getSubCategories().forEach(subcat -> {
            CategoryConfiguration categoryConfiguration = subcategories.get(subcat.getCategoryName());
            if (categoryConfiguration == null) {
               return;
            }
            subcat.getAuftraggeberPattern().addAll(categoryConfiguration.getAuftraggeberPattern());
            subcat.getVerwendungszweckPattern().addAll(categoryConfiguration.getVerwendungszweckPattern());
            subcat.getSubCategories().addAll(categoryConfiguration.getSubCategories());
         });
      }

      Collection<CategoryConfiguration> values = categories.values();

      String json = new GsonBuilder().setPrettyPrinting().create().toJson(values);
      System.out.println(json);
      return json;
   }
}
