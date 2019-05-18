/**
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 *         The node class takes in 2 data type. T is actual object
 *         the node should hold and E is the data type of the
 *         node's ID.
 * 
 * @param <T>
 *            real object
 * @param <E>
 *            ID of the node
 * @version 1.0
 */
public class Node<E extends Comparable<E>, T extends Comparable<T>>
    implements Comparable<Node<E, T>> {
    private T data;
    private E id;


    /**
     * Default constructor
     */
    public Node() {
        this.data = null;
        this.id = null;
    }


    /**
     * paramertize constructor
     * 
     * @param item
     *            The item
     */
    public Node(T item) {
        this.data = item;
        this.id = null;
    }



    /**
     * Paraterize constructor
     * @param id ID of the value
     * @param item the actual double value
     */
    public Node(E id, T item) {
        this.data = item;
        this.id = id;
    }


    /**
     * get the data
     * 
     * @return data
     */
    public T getData() {
        return data;
    }


    /**
     * get the id of node
     * 
     * @return Node id
     */
    public E getID() {
        return id;
    }


    /**
     * compare 2 nodes
     * return the result of comparison
     */
    @Override
    public int compareTo(Node<E, T> input) {
        return (data.compareTo(input.data));
    }


    /**
     * swap data of 2 node
     * 
     * @param rhs
     *            the data on the right hand side
     */
    public void swapData(Node<E, T> rhs) {
        T tempD = rhs.data;
        E tempK = rhs.id;
        this.data = rhs.data;
        this.id = rhs.id;
        rhs.data = tempD;
        rhs.id = tempK;
    }
}