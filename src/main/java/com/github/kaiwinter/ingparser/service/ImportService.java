package com.github.kaiwinter.ingparser.service;

import java.io.InputStreamReader;
import java.util.List;

import com.github.kaiwinter.ingparser.App;
import com.github.kaiwinter.ingparser.model.Booking;
import com.opencsv.bean.CsvToBeanBuilder;

public class ImportService {

   // Number of lines to skip from the CSV file.
   private static final int CSV_HEADER_LINES = 14;

   public List<Booking> importFromFile(String filename) {
      List<Booking> bookings = new CsvToBeanBuilder<Booking>(
            new InputStreamReader(App.class.getResourceAsStream(filename))) //
            .withSeparator(';') //
            .withType(Booking.class) //
            .withSkipLines(CSV_HEADER_LINES) //
            .build().parse();

      return bookings;
   }
}
