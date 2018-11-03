package pack;

public class Cpu {

  private final VirtualMachine virtualMachine;

  public Cpu(final VirtualMachine virtualMachine) {
    this.virtualMachine = virtualMachine;
  }

  public void parseInput(final String input) {
    final String[] inputSplit = input.split(" ");
    if (inputSplit.length != 0) {
      switch (inputSplit[0]) {

        case "about":
          if (input.length() == 5) {
            virtualMachine.outEmphasis("DeuCeng Virtual Machine \n");
            virtualMachine.outln(
                "  The aim of the program is to implement a simplified virtual machine partially.");
            virtualMachine.outEmphasis("General Information \n");
            virtualMachine.outln(
                "  In our virtual machine there are three parts: CPU, Disk and RAM. CPU processes the command set.");
            virtualMachine.outln(
                "  The structures to process commands are in RAM. The function of the virtual Disk(vDisk) is to ");
            virtualMachine.outln("  provide permanency. Mainly, vdisk is simulated in the project.");
            virtualMachine.outEmphasis("RAM \n");
            virtualMachine.outln(
                "- In the RAM, file structures include name, size and diskstart. The diskstart indicates the start ");
            virtualMachine.outln("  of the file on the vdisk.");
            virtualMachine
                .outln("- Command and file to be worked on must be kept on RAM for quick access.");
            virtualMachine.outln("- All files must be synchronized in RAM and vdisk.");
            virtualMachine.outln(
                "- The memory structures of the files in the RAM must be sorted by diskstart address and the file ");
            virtualMachine.outln("  name.");
            virtualMachine.outEmphasis("VDISK \n");
            virtualMachine.outln("- The vdisk consists of 30 blocks(1-30).");
            virtualMachine.outln("- Each block contains 11 byte/character.");
            virtualMachine.outln("       * The first 10 byte/characters contains data.");
            virtualMachine.outln("       * The 11th byte indicates the next block.");
            virtualMachine.outln("                o 1-30 : Next block of the file");
            virtualMachine.outln("                o 0    : End of file");
            virtualMachine.outln("- Vdisk data character set: 0-9, a-z, A-Z, space");
            virtualMachine.outln("- Special characters: . and @");
            virtualMachine.outln("       * . : no character/data");
            virtualMachine.outln("       * @ : start of the file");
            virtualMachine.outln(
                "- File name should begin with @ character and the fist block is used for file name.");

          } else {
            virtualMachine.outErr("Wrong form. Syntax: about");
          }
          break;

        case "append":
          if (inputSplit.length > 2 && inputSplit[2].startsWith("\"")
              && inputSplit[inputSplit.length - 1].endsWith("\"")) {
            final String filename = inputSplit[1];
            if (virtualMachine.getRam().fileExists(filename)) {
              String append = "";
              for (int i = 2; i < inputSplit.length; i++) {
                append += inputSplit[i];
                if (i < inputSplit.length - 1) {
                  append += " ";
                }
              }
              append = append.substring(1, append.length() - 1);
              if (virtualMachine.getRam().appendToFile(filename, append)) {
                virtualMachine.outSuccess();
              } else {
                virtualMachine.outErr("Could not append text to " + filename);
              }
            } else {
              virtualMachine.outErr("File does not exist");
            }
          } else {
            virtualMachine.outErr("Wrong form. Syntax: append <filename> \"<text>\"");
          }
          break;
        case "create":
          if (inputSplit.length == 2) {
            final String filename = inputSplit[1];
            if (filename.length() < 10 && filename.length() > 0) {
              if (virtualMachine.getRam().fileExists(filename)) {
                virtualMachine.outErr("File exists");
              } else {
                if (virtualMachine.getRam().createFile(filename)) {
                  virtualMachine.outSuccess();
                } else {
                  virtualMachine.outErr("File could not be created");
                }
              }
            } else {
              virtualMachine.outErr("Invalid file name. File name must be 1-9 characters long");
            }
          } else {
            virtualMachine.outErr("Wrong form. Syntax: create <filename>");
          }
          break;
        case "defrag":
          virtualMachine.getVdisk().defragDisk();
          virtualMachine.outSuccess();
          break;
        case "delete":
          /*
           * The delete command can be either two, three or four words. The three-word version is a
           * special addition to our project.
           */
          switch (inputSplit.length) {
            case 2:
              /* The two-word version deletes a whole file. */
              if (virtualMachine.getRam().deleteFile(inputSplit[1])) {
                virtualMachine.outSuccess();
              } else {
                virtualMachine.outErr("File does not exist");
              }
              break;
            case 3:
            case 4:
              /*
               * The three-word version deletes a block from a file. The four-word version deletes a
               * range of blocks from a file. First, make sure the syntax is correct.
               */
              if (virtualMachine.getRam().fileExists(inputSplit[1])) {
                final int startBlock, endBlock;
                try {
                  startBlock = Integer.parseInt(inputSplit[2]);
                  endBlock = inputSplit.length == 4 ? Integer.parseInt(inputSplit[3]) : startBlock;
                  if (startBlock < 1 || startBlock >= Vdisk.NUM_BLOCKS || endBlock < startBlock
                      || endBlock >= Vdisk.NUM_BLOCKS) {
                    throw new IllegalArgumentException();
                  }
                  if (virtualMachine.getRam().deleteBlocks(inputSplit[1], startBlock, endBlock)) {
                    virtualMachine.outSuccess();
                  } else {
                    virtualMachine.outErr("Invalid block numbers.");
                  }
                } catch (NumberFormatException e) {
                  virtualMachine.outErr("The third (and fourth) words must be integers.");
                } catch (IllegalArgumentException e) {
                  virtualMachine
                      .outErr("The start and end blocks must be between 2 and " + Vdisk.NUM_BLOCKS
                          + ", and end block must be greater than or equal to start block.");
                }
              } else {
                virtualMachine.outErr("File does not exist.");
              }
              break;
            default:
              /* Anything else means an invalid input. */
              virtualMachine.outErr(
                  "Wrong form. Syntax: delete <filename> [<block number> [<block range end>]]");
              break;
          }
          break;
        case "help":
          if (input.length() == 4) {
            /* Print a list with commands and explanations. */
            virtualMachine.outEmphasis("about");
            virtualMachine.outln("                          Prints information about this program");
            virtualMachine.outEmphasis("append <fname> \"<append>\"");
            virtualMachine.outln("      Append data to the end of the file (as a block/blocks)");
            virtualMachine.outEmphasis("create <fname>");
            virtualMachine.outln("                 Create a file");
            virtualMachine.outEmphasis("defrag");
            virtualMachine.outln("                         Defragment Vdisk");
            virtualMachine.outEmphasis("delete <fname>");
            virtualMachine.outln("                 Delete file");
            virtualMachine.outEmphasis("delete <fname> <a> <b>");
            virtualMachine.outln("         Delete blocks from a to b");
            virtualMachine.outEmphasis("insert <fname> <i> \"<append>\"");
            virtualMachine.outln("  Insert data into file from the i'th block (as a block/blocks)");
            virtualMachine.outEmphasis("load <fname.txt>");
            virtualMachine
                .outln("               Load and run an executable batch file from harddisk");
            virtualMachine.outEmphasis("print <fname>");
            virtualMachine.outln("                  Print the content of a Vdisk file on screen");
            virtualMachine.outEmphasis("printdisk");
            virtualMachine.outln("                      Print all Vdisk on screen");
            virtualMachine.outEmphasis("printdisk -d");
            virtualMachine
                .outln("                   Print all files on screen sorted by diskstart");
            virtualMachine.outEmphasis("printdisk -f");
            virtualMachine.outln("                   Print all files on screen sorted by filename");
            virtualMachine.outEmphasis("restore");
            virtualMachine.outln("                        Restore Vdisk from harddisk");
            virtualMachine.outEmphasis("store");
            virtualMachine.outln("                          Save Vdisk to harddisk");

          } else {
            virtualMachine.outErr("Wrong form. Syntax: help");
          }
          break;
        case "insert":
          String filename = inputSplit[1];
          if (inputSplit.length > 3 && inputSplit[3].startsWith("\"")
              && inputSplit[inputSplit.length - 1].endsWith("\"")) {
            if (virtualMachine.getRam().fileExists(filename)) {

              String textToAdd = "";
              for (int i = 3; i < inputSplit.length; i++) {
                textToAdd += inputSplit[i];
                if (i < inputSplit.length - 1) {
                  textToAdd += " ";
                }
              }
              textToAdd = textToAdd.substring(1, textToAdd.length() - 1);

              if (virtualMachine.getRam().fileSize(filename) != 0) {

                int diskstart = virtualMachine.getVdisk().fileExists(filename);
                /* The index of the block to insert the text into. */
                int insertblock = Integer.valueOf(inputSplit[2]);
                /* For memorizing pointer */
                int insertIndex = virtualMachine.getVdisk().lastlocation();
                /* To get insertblock's next */
                virtualMachine.getVdisk().getInsertblockNext(insertblock, diskstart, insertIndex);

                if (insertblock > virtualMachine.getRam().fileSize(filename)) {
                  virtualMachine.outErr("Wrong block number. File hasn't got this block.");
                  break;
                } else {

                  /* Remove quotation marks. */
                  inputSplit[3] = inputSplit[3].substring(1, inputSplit[3].length());
                  inputSplit[inputSplit.length - 1] = inputSplit[inputSplit.length - 1].substring(0,
                      inputSplit[inputSplit.length - 1].length() - 1);

                  virtualMachine.getVdisk().insert(textToAdd, insertIndex);

                  /* Update RAM. */
                  virtualMachine.getRam().insert(filename, textToAdd, insertblock);
                  virtualMachine.outSuccess();
                }
              } else {
                parseInput("append " + filename + " \"" + textToAdd + "\"");
              }
            } else {
              virtualMachine.outErr("File does not exist");
            }
          } else {
            virtualMachine.outErr("Wrong form. Syntax: insert <filename.txt> <block> \"<append>\"");
          }
          break;
        case "load":
          if (inputSplit.length == 2) {
            virtualMachine.getRam().runBatch(inputSplit[1]);
          } else {
            virtualMachine.outErr("Wrong form. Syntax: load <filename.txt>");
          }
          break;
        case "print":
          if (inputSplit.length == 2) {
            final String filename2 = inputSplit[1];
            if (virtualMachine.getRam().fileExists(filename2)) {
              virtualMachine.getRam().printFile(filename2);
            } else {
              virtualMachine.outErr("File does not exist");
            }
          } else {
            virtualMachine.outErr("Wrong form. Syntax: print <filename>");
          }
          break;
        case "printdisk":
          if (inputSplit.length == 1) {
            virtualMachine.getVdisk().printDisk();
          } else if (inputSplit.length == 2) {
            if (inputSplit[1].equalsIgnoreCase("-d")) {
              virtualMachine.getRam().printDisk(true);
            } else if (inputSplit[1].equalsIgnoreCase("-f")) {
              virtualMachine.getRam().printDisk(false);
            } else {
              virtualMachine.outErr("Wrong form. Syntax: printdisk [-d/f]");
            }
          } else {
            virtualMachine.outErr("Wrong form. Syntax: printdisk [-d/f]");
          }
          break;
        case "restore":
          virtualMachine.getVdisk().reStore();
          virtualMachine.outSuccess();
          break;
        case "store":
          virtualMachine.getVdisk().store();
          virtualMachine.outSuccess();
          break;
        default:
          virtualMachine.outErr("Unknown command");
          break;
      }
    }
  }
}
