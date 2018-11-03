package pack;

public class MultiLinked {

  private FileNode diskstartHead;
  private FileNode nameHead;

  public MultiLinked() {

  }

  public void addFile(String newFilename, int newDiskstart) {
    FileNode newFile = new FileNode(newFilename, newDiskstart);
    if (diskstartHead == null) { // Empty list
      diskstartHead = newFile;
      nameHead = newFile;
      
    } else { // More than one item
      
      FileNode temp = diskstartHead;
      
      //------------
      
      // Diskstart sorting
      
      if (newDiskstart >= diskstartHead.diskstart) {
        
        while (temp.nextDiskstart != null && newDiskstart >= temp.nextDiskstart.diskstart) {
          temp = temp.nextDiskstart;
        }
        newFile.nextDiskstart = temp.nextDiskstart;
        temp.nextDiskstart = newFile;
        
      } else {
        
        newFile.nextDiskstart = diskstartHead;
        diskstartHead = newFile;
      }
      
      // Filename sorting
      
      if (newFilename.compareTo(nameHead.filename) >= 0) {
        
        temp = nameHead;

        while (temp.nextName != null && newFilename.compareTo(temp.nextName.filename) >= 0) {
          temp = temp.nextName;
        }
        newFile.nextName = temp.nextName;
        temp.nextName = newFile;
        
      } else {
        
        newFile.nextName = nameHead;
        nameHead = newFile;
      }
    }

  }

  /**
   * Adds a block to the given file.
   * 
   * @param filename the name of the file to add the block to
   * @param content the content to add to the block
   */
  public void addBlock(String filename, String content) {
    addBlock(filename, content, -1);
  }

  /**
   * Adds a block to the given file at the specified place (0 for the very beginning).
   * 
   * @param filename the name of the file to add the block to.
   * @param block the block {@code String} to add. Its length <em>must</em> be less than or equal to
   *        10.
   * @param place the place (index) to add the block to, starting at 0 for the first place. A value
   *        of -1 adds the block to the end of the file.
   */
  public void addBlock(String filename, String block, int place) {
    FileNode file = search(filename);

    if (file != null) {

      /* Make sure the block is in the right size. */
      if (block.length() <= 10) {

        if (file.rightLinked == null) {
          /* If file is empty, add the block to the start of the file. */
          file.rightLinked = new Node(block);

        } else {
          /* Otherwise, add it at the specified location. */

          final Node newNode = new Node(block);

          Node traverse = file.rightLinked;

          if (file.size == 0) {

            newNode.link = traverse;
            file.rightLinked = newNode;

          } else {

            if (place == -1) {

              while (traverse.link != null) {
                traverse = traverse.link;
              }

            } else {
              for (int i = 0; i < place; i++) {
                traverse = traverse.link;
              }

              newNode.link = traverse.link;
            }

            traverse.link = newNode;
          }
        }

        file.size++;

      } else {
        System.err.println(
            "Error - Trying to add a block to the file in RAM which is larger than 10 characters");
      }
    }
  }

  public void deleteFile(String filename) {
    /* delete the file for the name linked list. */
    while (nameHead != null && nameHead.filename.equals(filename)) {
      if (nameHead.nextName != null)
        nameHead = nameHead.nextName;
      else
        nameHead = null;
    }
    FileNode temp = nameHead;
    FileNode prev = null;
    while (temp != null) {
      if (temp.filename.equals(filename)) {
        prev.nextName = temp.nextName;
        temp = prev;
      } else {
        prev = temp;
        temp = temp.nextName;
      }
    }
    
    while (diskstartHead != null && diskstartHead.filename.equals(filename)) {
      if (diskstartHead.nextDiskstart != null)
          diskstartHead = diskstartHead.nextDiskstart;
      else
          diskstartHead = null;
    }
    temp = diskstartHead;
    prev = null;
    while (temp != null) {
      if (temp.filename.equals(filename)) {
        prev.nextDiskstart = temp.nextDiskstart;
        temp = prev;
      } else {
        prev = temp;
        temp = temp.nextDiskstart;
      }
    }
  }


  public void deleteBlock(String filename, int startBlock, int endBlock) {
    FileNode temphead = nameHead;
    /* search which one the file equals the filename */
    while (temphead != null && !temphead.filename.equals(filename)) {
      temphead = temphead.nextDiskstart;
    }

    if (temphead != null) {
      Node blockStart = temphead.rightLinked;
      Node blockEnd = temphead.rightLinked;
      int counter = 0;
      /*
       * #1 search the block using the startBlock to delete in the blocks
       */
      while (blockStart != null && counter != startBlock - 1) {
        if (blockStart.link != null)
          blockStart = blockStart.link;
        else
          blockStart = null;
        counter++;
      }
      counter = 0;
      /* #2 search the block using the endBlock to delete in the blocks */
      while (blockEnd != null && counter != endBlock - 1) {
        if (blockEnd.link != null)
          blockEnd = blockEnd.link;
        else
          blockEnd = null;
        counter++;
      }
      /*
       * Change the rightLinked node for changing head(Node) in the FileNode
       */
      if (startBlock == 1) {
        startBlock += 1;
        temphead.rightLinked = blockEnd.link;
      }
      /* finally, place and change the node each other. */
      Node temp = temphead.rightLinked;
      Node prev = new Node(null);
      while (temp != null) {
        if (temp.data != null && ((String) (temp.data)).equals((String) blockStart.data)) {
          if (blockEnd.link != null)
            prev.link = blockEnd.link;
          else
            prev.link = null;
          temp = prev;
        } else {
          prev = temp;
          temp = temp.link;
        }
      }
    }
    temphead.size -= endBlock - startBlock + 1;
  }
  
  public String printAll(final boolean b) {
    
    String string = "";
    
    FileNode traverse;
    
    if (b) { // diskstart'a gore
      
      traverse = diskstartHead;
      do {
        string += traverse.filename + ": " + printFile(traverse.filename) + "\n";
        traverse = traverse.nextDiskstart;
      } while (traverse != null);
      
    } else { // filename'e gore
      
      traverse = nameHead;
      do {
        string += traverse.filename + ": " + printFile(traverse.filename) + "\n";
        traverse = traverse.nextName;
      } while (traverse != null);
    }
    
    return string;
  }

  /**
   * Reads the contents of a given file directly from RAM (if found).
   * 
   * @param filename the name of the file to print.
   * @return a {@code String} containing the file's contents, or {@code null} if either the file is
   *         not found or if found but empty.
   */
  public String printFile(String filename) {

    /* Search for the file. */
    FileNode file = search(filename);

    /* If found, read its contents. */
    if (file != null) {

      /* Prepare a String variable. */
      String text = "";

      /* Prepare a Node variable to traverse the contents of the file. */
      Node blockTraverse = file.rightLinked;

      if (blockTraverse == null) {
        return null;
      }

      /* Traverse all the contents and them to the String variable. */
      do {
        text += (String) blockTraverse.data;

        /* Get the next block of content. */
        blockTraverse = blockTraverse.link;
      } while (blockTraverse != null);

      return text;
    }

    /* If not found, return null. */
    return null;
  }

  /**
   * Searches the RAM for the given file and returns it if found.
   * 
   * @param filename the name of the file to search for.
   * @return the {@code FileNode} of the sought file, or {@code null} if not found.
   */
  public FileNode search(String filename) {

    /* Prepare a variable that traverses all the nodes starting from the first node (head). */
    FileNode traverse = nameHead;

    /* If the list is empty, return null. */
    if (traverse == null) {
      return null;
    }

    /* Traverse the whole list until the sought file is found. */
    do {

      /* If the file is found, return it. */
      if (traverse.filename.equalsIgnoreCase(filename)) {
        return traverse;
      }

      /* Otherwise, go to the next node. */
      traverse = traverse.nextName;
    } while (traverse != null);

    /* If the file is not found, return null. */
    return null;
  }
}
