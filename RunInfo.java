/**
 * RunInfo is an object that hold information of
 * each run of the run file. It provides starting index
 * ending index, current index of the run and few more
 * methods
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0/**
 */
class RunInfo {

    private int startingIndex;
    private int endingIndex;
    private int currIndex;
    private int increment;


    /**
     * Default constructor
     */
    public RunInfo() {
        startingIndex = 0;
        endingIndex = 0;
        currIndex = 0;
    }


    /**
     * Parameterize constructor for run info
     * 
     * @param start
     *            stating index of a run
     * @param end
     *            ending index of a run
     * @param increment
     *            a run increment
     */

    public RunInfo(int start, int end, int increment) {
        startingIndex = start;
        endingIndex = end;
        currIndex = startingIndex;
        this.increment = increment;
    }


    /**
     * Increase current index of a run
     */
    public void incIndex() {
        currIndex += increment;
    }


    /**
     * Get a run starting index
     * 
     * @return run starting index
     */
    public int getStartIndex() {
        return startingIndex;
    }


    /**
     * Get a run ending index
     * 
     * @return run ending index
     */
    public int getEndIndex() {
        return endingIndex;
    }


    /**
     * Get the current index of a run
     * 
     * @return current index
     */
    public int getCurrIndex() {
        if (currIndex > endingIndex) {
            return -1;
        }
        return currIndex;
    }


    /**
     * Check to see if the run is empty
     * 
     * @return true if the run is empty
     */
    public boolean isEmpty() {
        return currIndex > endingIndex;
    }


    /**
     * reset the current index of a run
     */
    public void reset() {
        currIndex = startingIndex;
    }


    /**
     * set the ending index of a run
     * 
     * @param num
     *            ending index
     */
    public void setEndIndex(int num) {
        endingIndex = num;
    }
}
