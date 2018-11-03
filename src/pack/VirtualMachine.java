package pack;

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.util.Scanner;

import enigma.console.Console;
import enigma.console.TextAttributes;
import enigma.core.Enigma;

public class VirtualMachine {

  private static final TextAttributeibutes TEXT_CYAN = new TextAttributes(Color.CYAN);
  private static final TextAttributes TEXT_GREEN = new TextAttributes(Color.GREEN);
  private static final TextAttributes TEXT_LIGHT_GRAY = new TextAttributes(Color.LIGHT_GRAY);
  private static final TextAttributes TEXT_RED = new TextAttributes(Color.RED);
  private static final TextAttributes TEXT_WHITE = new TextAttributes(Color.WHITE);
  private static final TextAttributes TEXT_YELLOW = new TextAttributes(Color.YELLOW);

  private static final int WIDTH = 110, HEIGHT = 30;
  private final Console console =
      Enigma.getConsole("DeuCeng Virtual Machine", WIDTH, HEIGHT, 18, 2);
  private final Scanner scanner = new Scanner(System.in);

  private Cpu cpu;
  private Ram ram;
  private Vdisk vdisk;

  public void start() {
    // Initializing components
    cpu = new Cpu(this);
    ram = new Ram(this);
    vdisk = new Vdisk(this);

    // Header
    setColor(TEXT_YELLOW);
    outln("DeuCeng Virtual Machine | V1.0 | Akif Cakar, Dilara Goral, Omar Othman");
    setColor(TEXT_LIGHT_GRAY);
    outln("Dokuz Eylul University, Faculty of Engineering, Department of Computer Engineering");
    outln("CME1102 Project-Based Learning | 2016-2017");
    setColor(TEXT_CYAN);
    outln("> Enter \"help\" for a list of available commands.");
    resetColor();
    printHorizontal();

    // Main Loop
    String input;
    while (true) {
      /* Get input. */
      setColor(TEXT_LIGHT_GRAY);
      out("\nCommand > ");
      setColor(TEXT_CYAN);
      input = scanner.nextLine();
      resetColor();
      cpu.parseInput(input);
    }
  }

  public Cpu getCpu() {
    return cpu;
  }

  public Ram getRam() {
    return ram;
  }

  public Vdisk getVdisk() {
    return vdisk;
  }

  public void outErr(final String msg) {
    setColor(TEXT_RED);
    outln("Error - " + msg);
    resetColor();
  }

  public void outSuccess() {
    setColor(TEXT_GREEN);
    outln("OK");
    resetColor();
  }


  public void outWarning(final String msg) {
    setColor(TEXT_YELLOW);
    outln("Warning - " + msg);
    resetColor();
  }

  public void outEmphasis(final String msg) {
    setColor(TEXT_YELLOW);
    out(msg);
    resetColor();
  }

  // Screen Methods

  /** Clears the screen. */
  public void clearScreen() {
    /* Print space characters all over the screen. */
    for (int i = 0; i < WIDTH; i++) {
      for (int j = 0; j < HEIGHT - 1; j++) {
        out(" ", i, j);
      }
    }
    /* Set the cursor at the beginning of the console. */
    setCursor(0, 0);
  }

  /**
   * Returns the current x-position of the cursor.
   * 
   * @return a non-negative integer starting at 0 on the left-side of the console.
   */
  public int getCursorX() {
    return console.getTextWindow().getCursorX();
  }

  /**
   * Returns the current y-position of the cursor.
   * 
   * @return a non-negative integer starting at 0 at the top of the console.
   */
  public int getCursorY() {
    return console.getTextWindow().getCursorY();
  }

  /**
   * Prints text on the console at the current cursor location.
   * 
   * @param text the text to print.
   */
  public void out(final String text) {
    System.out.print(text);
  }

  /**
   * Prints text on the console at a specific location.
   * 
   * @param text the text to print.
   * @param x the x-position (starting at 0 on the left).
   * @param y the y-position (starting at 0 at the top).
   */
  public void out(final String text, final int x, final int y) {
    setCursor(x, y);
    out(text);
  }

  /**
   * Prints text on the console centered horizontally with respect to the whole width of the console
   * at the current y-location.
   * 
   * @param text the text to print.
   */
  public void outCenter(final String text) {
    final int textLength = text.length();
    if (textLength < WIDTH)
      setCursor((WIDTH - textLength) / 2, getCursorY());
    outln(text);
  }

  /**
   * Prints text on the console centered horizontally with respect to the whole width of the console
   * at a specific y-position.
   * 
   * @param text the text to print.
   * @param y the y-position (starting at 0 at the top).
   */
  public void outCenterAt(final String text, final int y) {
    setCursor(0, y);
    outCenter(text);
  }

  /** Skips a line. */
  public void outln() {
    outln("");
  }

  /**
   * Prints text on the console at the current cursor location followed by a line feed (a new line).
   * 
   * @param text the text to print.
   */
  public void outln(final String text) {
    out(text + "\n");
  }

  /**
   * Prints text on the console at a specific location followed by a line feed (a new line).
   * 
   * @param text the text to print.
   * @param x the x-position (starting at 0 on the left).
   * @param y the y-position (starting at 0 at the top).
   */
  public void outln(final String text, final int x, final int y) {
    setCursor(x, y);
    outln(text);
  }

  public void printHorizontal() {
    for (int i = 0; i < WIDTH; i++)
      out("-");
  }

  public void resetColor() {
    setColor(TEXT_WHITE);
  }

  /**
   * Sets the foreground and backgrounds colors of the output.
   * 
   * @param color the {@code TextAttributes} object.
   */
  public void setColor(final TextAttributes color) {
    console.setTextAttributes(color);
  }

  /**
   * Sets the position of the cursor.
   * 
   * @param x the x-position starting at 0 on the left.
   * @param y the y-position starting at 0 at the top.
   */
  public void setCursor(final int x, final int y) {
    console.getTextWindow().setCursorPosition(x, y);
  }
}
