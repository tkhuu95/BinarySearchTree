
package BST;

import java.util.Random;

/**
 * Generates a BST built from N random key inserts. Provides various 
 * representations and properties for a BST.
 * 
 * More Info: 
 *   Algorithms 4th Edition by Sedgewick and Wayne (Chapter 3)
 *   An Introduction to the Analysis of Algorithms by Sedgewick and Flajolet (Chapter 6)
 * 
 * @author Thomas Khuu
 */
public class RandomBST {
    
    private class Node {
        
        private int key;
        private int value;
        private int size;
        private int depth;
        private Node left, right;
        
        public Node(int key, int value, int depth) {
            this.key   = key;
            this.value = value;
            this.depth = depth;
        }
    }
    
    private Node root;
    private int n;
    private Random rand;
    private int height;
    
    /**
     * Constructs a random BST non-recursively given the number of random keys
     * to generate.
     * 
     * @param n  the number of keys to generate.
     * 
     * @throws IllegalArgumentException if n is less than 0
     */
    public RandomBST(int n) {
        if (n < 0) 
            throw new IllegalArgumentException("Can't generate less than 0.");
        
        rand = new Random(System.currentTimeMillis());
        this.n = n;
        root = new Node(rand.nextInt(this.n) + 1, 1, 0);
        //System.out.println(root.key + " root");
        generate();
    }
    
    private void generate() {
        int count = n;
        int tempHeight;
        --count;
        while (count > 0) {
            Node parent = null;
            Node temp = root;
            int k = rand.nextInt(n) + 1;
            while (temp != null) {
                parent = temp;
                if (k < temp.key) {
                    temp = temp.left;
                } else if (k > temp.key) {
                    temp = temp.right;
                } else {
                    temp.value++;
                    break;
                }
            }
            if (temp == null) {
                if (k < parent.key) 
                    parent.left = new Node(k, 1, parent.depth + 1);
                else
                    parent.right = new Node(k, 1, parent.depth + 1);
                // Calculate height along the way
                tempHeight = parent.depth + 1;
                if (tempHeight > height) height = tempHeight;
                count--;
                
            }
        }
    }
    
    private int size(Node x) {
        if (x == null) return 0;
        return size(x.left) + size(x.right) + 1;
    }
    
    /**
     * The parenthesis systems representation corresponding to this randomly 
     * generated BST.
     * 
     * @return the parenthesis systems
     */
    public String parenSystems() {
        StringBuilder sb = new StringBuilder(2*n);
        parenSystems(sb, root);
        return sb.toString();
    }
    
    private void parenSystems(StringBuilder sb, Node x) {
        if (x != null) {
            sb.append("(");
            parenSystems(sb, x.left);
            parenSystems(sb, x.right);
            sb.append(")");
        }
    }
    
    /**
     * The gambler's ruin path representation corresponding to this randomly 
     * generated BST.
     * 
     * @return the gambler's ruin sequence
     */
    public String gamblerRuinSeq() {
        StringBuilder sb = new StringBuilder(2*n + 1);
        gamblerRuinSeq(sb, root);
        return sb.toString();
    }
    
    private void gamblerRuinSeq(StringBuilder sb, Node x) {
        if (x == null) { sb.append("-"); }
        else {
            sb.append("+");
            gamblerRuinSeq(sb, x.left);
            gamblerRuinSeq(sb, x.right);
        }
    }
    
    /************************
     * BST properties below
     ***********************/
    
    /**
     * Height of this generated BST. More formally as, the maximum level of all
     * the nodes plus 1.
     * 
     * @return the height
     */
    public int height() {
        return height;
    }
    
    /**
     * The external path length of this BST.
     * 
     * @return the external path
     */
    public int externalPathLen() {
        //System.out.println(externalPathLen(root) + " version 2");
        return externalPathLen(root, n);
    }
    
    // Version 1
    private int externalPathLen(Node x, int count) {
        if (x == null) return 0;
        return externalPathLen(x.left, size(x.left)) +
               externalPathLen(x.right, size(x.right)) + count + 1;
    }
    
    // Version 2
    private int externalPathLen(Node x) {
        if (x == null) return 1;
        if (x.left == null && x.right == null)
            return (externalPathLen(x.left) + externalPathLen(x.right))*(x.depth + 1);
        else if (x.left == null)
            return externalPathLen(x.left)*(x.depth + 1) + externalPathLen(x.right);
        else if (x.right == null)
            return externalPathLen(x.left) + externalPathLen(x.right)*(x.depth + 1);
        else
            return externalPathLen(x.left) + externalPathLen(x.right);
    }
    
    /**
     * The internal path length for this BST.
     * 
     * @return the internal path length
     */
    public int internalPathLen() {
        return internalPathLen(root, n);
    }
    
    private int internalPathLen(Node x, int count) {
        if (x == null || count == 1) return 0;
        return internalPathLen(x.left, size(x.left)) + 
               internalPathLen(x.right, size(x.right)) + count - 1;
    }
    
    
    
    /**
     * The number of compares of a successful search for this BST.
     * (internal path length / size of tree) + 1
     * 
     * @return the number of compares of a successful search
     */
    public double successfulSearchCost() {
        return 1.0*internalPathLen()/size(root) + 1;
    }
    
    /**
     * The number of compares of an unsuccessful search of this BST.
     * (external path length / (size of tree + 1))
     * 
     * @return number of compares of an unsuccessful search
     */
    public double unsuccessfulSearchCost() {
        return 1.0*externalPathLen() / (size(root) + 1);
    }
    
    /**
     * Number of leaves (internal nodes with no children) for this random BST.
     * 
     * @return the number of leaves
     */
    public int leaves() {
        return leaves(root);
    }
    
    private int leaves(Node x) {
        if (x == null) return 0;
        if (x.left == null && x.right == null) return 1;
        return leaves(x.left) + leaves(x.right);
    }
    
    /**
     * Number of external nodes for this random BST.
     * 
     * @return the number of external nodes
     */
    public int externalNodeCount() {
        return externalNodeCount(root);
    }
    
    private int externalNodeCount(Node x) {
        if (x == null) return 1;
        return externalNodeCount(x.left) + externalNodeCount(x.right);
    }
    
    /********************************
     * Helper methods to verify BST
     *******************************/
    
    private void verify() {
        int size = size(root);
        int height = height();
        int epl = externalPathLen();
        int ipl = internalPathLen();
        if (!isBST(root)) {
            System.err.println("BST not in symmetric order");
            return;
        }
        if (height < Math.floor(Math.log(size)/Math.log(2))) {
            System.err.println("BST not at least floor(log(N))");
        }
        if (epl != ipl + 2*size) {
            System.err.println("Error with path lengths.");
        }
    }
    
    private boolean isBST() {
        return isBST(root);
    }
    
    private boolean isBST(Node x) {
        if (x.left != null && x.right != null){
            if (x.key <= x.left.key || x.key >= x.right.key) return false;
            else                                             return isBST(x.left) 
                                                                 && isBST(x.right);
        } else if (x.right != null) {
            if (x.key >= x.right.key) return false;
            else                      return isBST(x.right);
        } else if (x.left != null) {
            if (x.key <= x.left.key) return false;
            else                     return isBST(x.left);
        } else {
            return true;  // internal nodes with no children always true
        }
    }
    /* Used for debugging
    public StringBuilder x() {
        StringBuilder s = new StringBuilder();
        x(root, s);
        return s;
    }
    
    private void x(Node x, StringBuilder s) {
        if (x == null) return;
        s.append(x.key).append(" ").append(x.depth).append(" <-| ");
        x(x.left, s);
        x(x.right, s);
    }
    */
    public static void main(String[] args) {
        RandomBST rbst = new RandomBST(23143);
        System.out.println(rbst.height());
        //System.out.println(rbst.parenSystems());
        //System.out.println(rbst.gamblerRuinSeq());
        //System.out.println(rbst.leaves());
        //System.out.println(rbst.externalNodeCount());
        System.out.println(rbst.externalPathLen());
        rbst.verify();
        System.out.println(rbst.internalPathLen());
    }
}