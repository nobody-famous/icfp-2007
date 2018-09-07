package icfp.endo.rope;

public class Rope {
    /**
     * Default constructor
     */
    public Rope() {
        this((char[]) null);
    }

    public Rope(char[] data) {
        if (data != null) {
            RopeNode tmp = new RopeNode(data);
            this.head = tmp;
            this.length = data.length;
        } else {
            this.head = null;
            this.length = 0;
        }
    }

    /**
     * @param str String to wrap
     *
     * Wrap the given string as a rope.
     * Create a new node as the left child so that all ropes have an
     * empty head node. Makes things easier later on.
     */
    public Rope(String str) {
        this(str.toCharArray());
    }

    /**
     * Check if this rope is empty
     *
     * @return True - the length is 0
     *         False - otherwise
     */
    public boolean isEmpty() {
        return (this.length == 0);
    }

    /**
     * @param ndx The index of the character to get
     *
     * Return the character at the given index
     *
     * @return A char
     */
    public char get(int ndx) {
        return (this.head == null) ? '\0' : getRecur(this.head, ndx);
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length Length to cut off the front of the rope
     *
     * Remove the first length items from the rope
     */
    public void trunc(int length) {
        FoundNode fn = lookup(length);

        throw new RuntimeException("Rope.trunc not done");
        // Rope node = fn.node;
        // int count = 0;

        // if ((fn.index > 0) && (fn.index < node.str.length())) {
        //     node.str = node.str.substring(fn.index);
        //     count = fn.index;
        // } else {
        //     count = node.parent.leftCount;
        //     node.parent.left = null;
        // }

        // while ((node.parent != null) && (node == node.parent.left)) {
        //     node.parent.leftCount -= count;
        //     node = node.parent;
        // }
    }

    /**
     * Remove the first char in the rope and return it
     *
     * @return The first character
     */
    public char pop() {
        char ch = get(0);

        trunc(1);

        return ch;
    }

    /**
     * @param start The index of the beginning of the substring
     *         end The index just after the end of the substring
     *
     * Return a substring from start to end - 1
     *
     * @return A new rope with the substring
     */
    public Rope substring(int start, int end) {
        FoundNode startNode = lookup(start);
        FoundNode endNode = lookup(end);

        if (startNode.node == endNode.node) {
            char[] buf = startNode.node.substring(start, end);
            return new Rope(buf);
        }
        throw new RuntimeException("Rope.substring not done");
    }

    /**
     * @param start The index of the beginning of the substring
     *
     * Return a substring from the given index to the end of the rope
     *
     * @return A new rope with the substring
     */
    public Rope substring(int start) {
        return substring(start, this.length);
    }

    /**
     * @param rope An existing rope
     *
     * Add the given rope to the front of this one
     */
    public void prepend(Rope rope) {
        RopeNode tmp = new RopeNode();

        this.length += rope.length;

        tmp.setLeft(rope.head, rope.length);
        tmp.setRight(this.head);
        this.head = tmp;
    }

    /**
     * @param str The string to add
     *
     * Add the given string to the front of this one
     * 
     * @return The new head of the rope
     */
    public void prepend(String str) {
        prepend(new Rope(str));
    }

    /**
     * @param rope An existing rope
     *
     * Add the given rope to the end of this one
     */
    public void concat(Rope rope) {
        if (length == 0) {
            this.head = rope.head;
            this.length = rope.length;
            return;
        }

        RopeNode tmp = new RopeNode();

        tmp.setRight(rope.head);
        tmp.setLeft(this.head, this.length);

        this.length += rope.length;
        this.head = tmp;
    }

    /**
     * @param str The string to concatenate
     *
     * Add the given string to the end of this rope
     */
    public void concat(String str) {
        concat(new Rope(str));
    }

    /**
     * @param ndx The index to check
     *
     * Check if the given index is valid for this rope.
     * Assumes a head node and the entire rope is to the left.
     *
     * @return true If 0 <= ndx < length
     *         false otherwise
     */
    public boolean isValidIndex(int ndx) {
        return (ndx >= 0) && (ndx < this.length);
    }

    /**
     * @param srch The string to search for
     *
     * Locate the first occurence of the given string in this rope
     *
     * @return -1 If the string was not found
     *         otherwise, the index of the end of the string
     */
    public int search(String srch) {
        throw new RuntimeException("Rope.search not done");
    }

    /**
     * Get a Java string for this rope
     *
     * @return A string object
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();

        toString(builder, this.head);

        return builder.toString();
    }

    /**
     * @param builder The StringBuilder object to add to
     * @param node The current node
     *
     * Recursively walk the rope, constructing a String from this rope
     */
    private void toString(StringBuilder builder, RopeNode node) {
        if (node.getData() != null) {
            builder.append(node.getData());
            return;
        }

        if (node.getLeft() != null) {
            toString(builder, node.getLeft());
        }
        if (node.getRight() != null) {
            toString(builder, node.getRight());
        }
    }

    /**
     * @params ndx The index to lookup
     *
     * Find the node that contains the given index
     *
     * @return A FoundNode object with the node and index into that node
     *         of the requested index
     */
    private FoundNode lookup(int ndx) {
        RopeNode node = this.head;

        // Loop until we find a child node
        while (node.getData() == null) {
            while (node.getLeftCount() > ndx) {
                node = node.getLeft();
            }

            // If we hit the end of the left branch, but there's a right branch,
            // need to go down it.
            if (node.getRight() != null) {
                ndx -= node.getLeftCount();
                node = node.getRight();
            }
        }

        return new FoundNode(node, ndx);
    }

    private char getRecur(RopeNode node, int ndx) {
        char ch = '\0';

        if (ndx < node.getLeftCount()) {
            if (node.getLeft() == null) {
                throw new RuntimeException("Rope.get Invalid State no left child");
            }
            ch = getRecur(node.getLeft(), ndx);
        } else {
            if (node.getRight() == null) {
                throw new RuntimeException("Rope.get Invalid State no right child");
            }
            ch = getRecur(node.getRight(), ndx - node.getLeftCount());
        }

        return ch;
    }

    /***********************************************************************
     * When looking up an index, we need to find the node that holds the
     * index as well as the resulting index into that node. Can't return
     * multiple values, so need to wrap all that up in a class.
     ***********************************************************************/
    private class FoundNode {
        public FoundNode(RopeNode node, int index) {
            this.node = node;
            this.index = index;
        }

        RopeNode node;
        int index;
    }

    private RopeNode head;
    private int length;
}
