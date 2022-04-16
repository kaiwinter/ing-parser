package com.github.kaiwinter.ingparser.config;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.github.kaiwinter.ingparser.App;
import com.google.gson.Gson;

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
   public List<FilterCriterion> readConfiguration(String configFile) {
      Reader fileReader = new InputStreamReader(App.class.getResourceAsStream(configFile));
      CategoryConfiguration[] categories = new Gson().fromJson(fileReader, CategoryConfiguration[].class);

      List<FilterCriterion> filterCriteria = new ArrayList<>();

      for (CategoryConfiguration category : categories) {
         filterCriteria.addAll(category.getCombinedFilterCriteria());
      }

      return filterCriteria;
   }
}