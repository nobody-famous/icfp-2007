package icfp.endo.rope;

/***********************************************************************
 * A node in a rope.
 * 
 * Can be either a connecting node, that has children and no data, or
 * a leaf node that has data and no children.
 ***********************************************************************/
class RopeNode {
    public RopeNode() {
    }

    /**
     * @param data initial data for this node
     *
     * Default constructor that wraps a character array
     */
    public RopeNode(char[] data) {
        this.setData(data);
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(RopeNode parent) {
        this.parent = parent;
    }

    /**
     * @param left the left to set
     */
    public void setLeft(RopeNode left, int count) {
        this.left = left;
        this.leftCount = count;
        left.parent = this;
    }

    /**
     * @param right the right to set
     */
    public void setRight(RopeNode right) {
        this.right = right;
        right.parent = this;
    }

    /**
     * @param data the data to set
     */
    public void setData(char[] data) {
        this.data = data;
        this.dataNdx = 0;
    }

    /**
     * @return the data
     */
    public char[] getData() {
        return data;
    }

    /**
     * @return the leftCount
     */
    public int getLeftCount() {
        return leftCount;
    }

    /**
     * @return the left
     */
    public RopeNode getLeft() {
        return left;
    }

    /**
     * @return the right
     */
    public RopeNode getRight() {
        return right;
    }

    public char[] substring(int start, int end) {
        char[] buf = new char[end - start];

        for (int inNdx = start, outNdx = 0; inNdx < end; inNdx += 1, outNdx += 1) {
            buf[outNdx] = this.data[inNdx];
        }

        return buf;
    }

    private int leftCount;
    private int dataNdx;
    private char[] data;
    private RopeNode parent;
    private RopeNode left;
    private RopeNode right;
}
