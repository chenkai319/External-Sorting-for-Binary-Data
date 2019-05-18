/**
 * RunNode is STRICTLY used for 8 records heap ONLY
 * RunNode is simply a node with 1 extra element pointing
 * which run that node belong to
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 */
public class RunNode implements Comparable<RunNode> {
    private Comparable data;
    private int runNum;


    /**
     * Default constructor of RunNode
     */
    public RunNode() {
        runNum = 0;
        data = new Node<Long, Double>();
    }


    /**
     * parameterize constructor
     * 
     * @param runNumber
     *            run number of the node
     * @param item
     *            a node
     */
    public RunNode(int runNumber, Comparable item) {
        this.runNum = runNumber;
        data = item;
    }


    /**
     * get the node of the node run
     * 
     * @return a node
     */
    public Comparable getData() {
        return data;
    }


    /**
     * Override the compare to method
     */
    @Override
    public int compareTo(RunNode rhs) {
        return this.data.compareTo(rhs.getData());
    }


    /**
     * get the run number of the node
     * 
     * @return run number of node
     */
    public int getRunNum() {
        return runNum;
    }

}