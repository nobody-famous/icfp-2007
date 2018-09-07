package icfp.endo;

/***********************************************************************
 * DNA
 * 
 * Due to memory issues, the DNA cannot be kept in a single string. This class
 * keeps a list of DNA chunks, treating them as one big string.
 ***********************************************************************/
public class DNA {
  public DNA() {
  }

  /***********************************************************************
   * Add a chunk of DNA to the beginning
   ***********************************************************************/
  public void prepend(DNAChunk chunk) {
    ChunkNode newNode = new ChunkNode(chunk);

    if (active == null) {
      active = newNode;
    } else {
      newNode.next = active;
      active = newNode;
    }
  }

  /***********************************************************************
   * Get an array index for each of the possible characters
   ***********************************************************************/
  private int hash(char ch) {
    switch (ch) {
    case 'I':
      return 0;
    case 'C':
      return 1;
    case 'F':
      return 2;
    case 'P':
      return 3;
    }
    return -1;
  }

  /***********************************************************************
   * Search for the given string in the DNA
   * 
   * Uses Horspool's algorithm
   ***********************************************************************/
  public int search(String p) {
    int[] occ = new int[4];
    int n = this.length();
    int m = p.length();

    // Initialize occurences array
    for (int i = 0; i < 4; i += 1) {
      occ[i] = -1;
    }

    // For each character, mark the last place in the string that it appears
    for (int i = 0; i < p.length() - 1; i += 1) {
      char ch = p.charAt(i);
      int ndx = hash(ch);

      occ[ndx] = i;
    }

    // Horspool's algorithm.
    // Try to match characters, starting with the last one, and jump forward if no
    // match is found
    int i = 0;
    while (i <= n - m) {
      int j = m - 1;
      while (j >= 0 && p.charAt(j) == this.get(i + j)) {
        j -= 1;
      }

      if (j < 0) {
        return i + m;
      }
      i += m - 1;
      i -= occ[hash(this.get(i))];
    }

    return 0;
  }

  /***********************************************************************
   * Get a substring from the DNA
   ***********************************************************************/
  public DNAChunk substring(int start, int end) {
    int seen = 0;
    int length = end - start;

    end -= 1;

    ChunkNode startNode = null;
    ChunkNode endNode = null;
    ChunkNode n = active;

    // Find the starting chunk
    while (n != null) {
      if (start <= n.chunk.size()) {
        startNode = n;
        seen += n.chunk.size() - start;
        break;
      } else {
        start -= n.chunk.size();
        end -= n.chunk.size();
      }
      n = n.next;
    }

    if (startNode == null) {
      return null;
    }

    // Found the first chunk, now find the ending chunk
    if (end < n.chunk.size()) {
      // Start and end are both in the same chunk so don't need to copy any actual
      // characters. Instead, copy the chunk object and set the appropriate indices.
      return n.chunk.copy(start, end + 1);
    }

    // The substring spans chunks, so need to generate a new string for it.
    n = startNode.next;
    while (n != null) {
      // Count how many characters are in the substring
      if (seen + n.chunk.size() > length) {
        endNode = n;
        seen = length;
        break;
      } else {
        seen += n.chunk.size();
      }

      n = n.next;
    }

    // Now that we know how many we've seen, we know how many we need to copy
    StringBuilder builder = new StringBuilder();
    int remaining = seen;

    n = startNode;
    String s = n.chunk.substring(start);
    remaining -= s.length();
    builder.append(s);

    n = startNode.next;
    while (n != null) {
      s = (remaining > n.chunk.size()) ? n.chunk.substring(0) : n.chunk.substring(0, remaining);
      builder.append(s);
      remaining -= s.length();
      if (n == endNode) {
        break;
      }

      n = n.next;
    }

    return new StringChunk(builder.toString());
  }

  /***********************************************************************
   * Check if the given index is within the sequence
   ***********************************************************************/
  public boolean isValidIndex(int ndx) {
    if (ndx < 0) {
      return false;
    }

    return ndx <= length();
  }

  public boolean isEmpty() {
    return active == null;
  }

  /***********************************************************************
   * Get the entire length of the DNA
   * 
   * Keeping track of the length with a variable got a bit tedious and error
   * prone, so calculate it each time. If this becomes an issue will need to
   * revisit it.
   ***********************************************************************/
  public int length() {
    int length = 0;
    ChunkNode n = active;

    while (n != null) {
      length += n.chunk.size();
      n = n.next;
    }

    return length;
  }

  /***********************************************************************
   * Grab the first element and remove it
   ***********************************************************************/
  public char pop() {
    if (active == null) {
      return '\0';
    }

    if (active.chunk.size() == 0) {
      throw new RuntimeException("Popping from empty chunk");
    }

    char ch = active.chunk.pop();

    if (active.chunk.size() == 0) {
      removeNode();
    }

    return ch;
  }

  /***********************************************************************
   * Remove size elements from the fron of the DNA
   ***********************************************************************/
  public void trunc(int size) {
    ChunkNode n = active;

    while (n != null) {
      if (size == n.chunk.size()) {
        removeNode();
        break;
      } else if (size < n.chunk.size()) {
        n.chunk.trunc(size);
        break;
      } else {
        size -= n.chunk.size();
        n = removeNode();
      }
    }
  }

  /***********************************************************************
   * Grab the first element, without removal
   ***********************************************************************/
  public char get() {
    return get(0);
  }

  /***********************************************************************
   * Grab the element at the given index
   ***********************************************************************/
  public char get(int ndx) {
    if (active == null) {
      return '\0';
    }

    ChunkNode n = active;
    while (n != null) {
      if (ndx < n.chunk.size()) {
        return n.chunk.get(ndx);
      }

      ndx -= n.chunk.size();
      n = n.next;
    }

    return '\0';
  }

  public void printChunkNodes() {
    ChunkNode n = active;

    while (n != null) {
      System.out.println("  " + n.chunk.size());
      n = n.next;
    }
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();

    ChunkNode n = active;

    builder.append(active.chunk.substring(0));
    n = n.next;
    while (n != null) {
      builder.append(n.chunk.substring(0));
      n = n.next;
    }

    return builder.toString();
  }

  private ChunkNode active;

  private class ChunkNode {
    DNAChunk chunk;
    ChunkNode next;

    ChunkNode(DNAChunk c) {
      chunk = c;
    }
  }

  private ChunkNode removeNode() {
    ChunkNode tmp = active;

    active = active.next;

    tmp.next = null;

    return active;
  }
}
