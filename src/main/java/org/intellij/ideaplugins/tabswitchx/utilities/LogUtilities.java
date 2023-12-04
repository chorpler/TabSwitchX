package org.intellij.ideaplugins.tabswitchx.utilities;

import static org.intellij.ideaplugins.tabswitchx.utilities.ConsoleColors.*;

/**
 * Utility logging functions with color support.
 *
 */
public class LogUtilities {
  public static boolean DEBUG_OUTPUT=false;

  public static boolean getDebug() {
    return DEBUG_OUTPUT;
  }

  public static boolean setDebug(boolean showDebugOutput) {
    DEBUG_OUTPUT = showDebugOutput;
    return DEBUG_OUTPUT;
  }

  /**
   * Print output on stderr if DEBUG_OUTPUT is true.
   * {@code ConsoleColors.DEBUG} will be used for color.
   *
   * @param args Variable number of strings to concatenate and display.
   */
  public static void logDbg(String... args) {
    if(DEBUG_OUTPUT) {
      String result = String.join(" ", args);
      String color = DEBUG;
      System.err.println(color + result + NC);
    }
  }

  /**
   * Print output on stderr if DEBUG_OUTPUT is true.
   * {@code ConsoleColors.DEBUG2} will be used for color.
   *
   * @param args Variable number of strings to concatenate and display.
   */
  public static void logDbg2(String... args) {
    if(DEBUG_OUTPUT) {
      String result = String.join(" ", args);
      String color = DEBUG2;
      System.err.println(color + result + NC);
    }
  }

  /**
   * Print output on stderr if DEBUG_OUTPUT is true.
   * {@code ConsoleColors.DEBUG3} will be used for color.
   *
   * @param args Variable number of strings to concatenate and display.
   */
  public static void logDbg3(String... args) {
    if(DEBUG_OUTPUT) {
      String result = String.join(" ", args);
      String color = DEBUG3;
      System.err.println(color + result + NC);
    }
  }

  /**
   * Print output on stderr if DEBUG_OUTPUT is true.
   * {@code ConsoleColors.DEBUG4} will be used for color.
   *
   * @param args Variable number of strings to concatenate and display.
   */
  public static void logDbg4(String... args) {
    if(DEBUG_OUTPUT) {
      String result = String.join(" ", args);
      String color = DEBUG4;
      System.err.println(color + result + NC);
    }
  }

  /**
   * Print output on stderr.
   * {@code ConsoleColors.ERROR} will be used for color.
   *
   * @param args Variable number of strings to concatenate and display.
   */
  public static void logErr(String... args) {
    String result = String.join(" ", args);
    String color = ERROR;
    System.err.println(color + result + NC);
  }

  /**
   * Print output on stderr.
   * {@code ConsoleColors.WARNING} will be used for color.
   *
   * @param args Variable number of strings to concatenate and display.
   */
  public static void logWarn(String... args) {
    String result = String.join(" ", args);
    String color = WARNING;
    System.err.println(color + result + NC);
  }

  /**
   * Print output on stderr.
   * {@code ConsoleColors.SUCCESS} will be used for color.
   *
   * @param args Variable number of strings to concatenate and display.
   */
  public static void logSuccess(String... args) {
    String result = String.join(" ", args);
    String color = SUCCESS;
    System.err.println(color + result + NC);
  }

  /**
   * Print output on stderr.
   * {@code ConsoleColors.INFO} will be used for color (default: no color)
   *
   * @param args Variable number of strings to concatenate and display.
   */
  public static void log(String... args) {
    String result = String.join(" ", args);
    String color = INFO;
    System.err.println(color + result + NC);
  }

}
