package com.github.kaiwinter.ingparser.preferences;

import java.util.prefs.Preferences;

import com.github.kaiwinter.ingparser.App;

public class PreferenceStore {

   private static final String LAST_CSV_FILE = "last-csv-file";
   private static final String LAST_PARSER_FILE = "last-parser-file";

   private PreferenceStore() {
   }

   public static void saveLastUsedCsvFile(String csvFilename) {
      Preferences pref = Preferences.userNodeForPackage(App.class);
      pref.put(LAST_CSV_FILE, csvFilename);
   }

   public static String loadLastUsedCsvFile() {
      Preferences pref = Preferences.userNodeForPackage(App.class);
      return pref.get(LAST_CSV_FILE, "");
   }

   public static void saveLastUsedParserFile(String csvFilename) {
      Preferences pref = Preferences.userNodeForPackage(App.class);
      pref.put(LAST_PARSER_FILE, csvFilename);
   }

   public static String loadLastUsedParserFile() {
      Preferences pref = Preferences.userNodeForPackage(App.class);
      return pref.get(LAST_PARSER_FILE, "");
   }

}
