package com.github.kaiwinter.ingparser.statistic;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Util class to test the console output.
 */
public class TestHelper {

   public static void captureOutput(CaptureTest test) {
      ByteArrayOutputStream outContent = new ByteArrayOutputStream();
      ByteArrayOutputStream errContent = new ByteArrayOutputStream();

      System.setOut(new PrintStream(outContent));
      System.setErr(new PrintStream(errContent));

      test.test(outContent, errContent);

      System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
      System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.out)));

   }
}

@FunctionalInterface
interface CaptureTest {
   void test(ByteArrayOutputStream outContent, ByteArrayOutputStream errContent);
}