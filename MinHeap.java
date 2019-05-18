import java.util.LinkedList;

/**
 * A min heap has the property that every node stores a value that is less than
 * or equal to that of its children. Because the root has a value less than or
 * equal to its children, which in turn have values less than or equal to their
 * children, the root stores the minimum of all values in the tree.
 * This mean heap also have some extra method in order to support external
 * sort function such as keep track of each run in the heap array and
 * modify run information
 * 
 * 
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 */

class MinHeap {
    private Comparable[] heap; // Pointer to the heap array
    private int maxSize; // Maximum size of the heap
    private int currSize; // Number of things now in heap
    private LinkedList<RunInfo> runs;


    /**
     * Default constructor for min heap
     */
    MinHeap() {
        currSize = 0;
        maxSize = 4096;
// maxSize = 8;
        heap = new Comparable[maxSize];
        runs = new LinkedList<RunInfo>();
    }


    /**
     * parameterize constructor for min heap
     * 
     * @param h
     *            the array of the heap
     */
    MinHeap(Comparable[] h) {
        heap = h;
        currSize = 0;
        maxSize = h.length;
        runs = new LinkedList<RunInfo>();
    }


    /**
     * check if the child is leaf
     * 
     * @param index
     *            child index
     * @return true if its leaf child
     */
    boolean isLeaf(int index) {
        return (index >= currSize / 2) && (index < currSize);
    }


    /**
     * The the left child position of an index
     * 
     * @param index
     *            parent index
     * @return left child of that index
     */
    int getLChildPos(int index) {
        if (this.isLeaf(index)) {
            return -1;
        }
        return 2 * index + 1;
    }


    /**
     * The the right child position of an index
     * 
     * @param index
     *            parent index
     * @return right child of that index
     */
    int getRChildPos(int index) {
        if (index >= (currSize - 1) / 2) {
            return -1;
        }
        return 2 * index + 2;
    }


    /**
     * get the parent position of an index
     * 
     * @param index
     *            child index
     * @return parent index
     */
    int getParentPos(int index) {
        if (index < 1) {
            return -1;
        }
        return (index - 1) / 2;
    }


    /**
     * swap position of 2 item
     * 
     * @param indexA
     *            position a
     * @param indexB
     *            position b
     */
    void swap(int indexA, int indexB) {
        Comparable temp = heap[indexA];
        heap[indexA] = heap[indexB];
        heap[indexB] = temp;
    }


    /**
     * insert item into min heap
     * 
     * @param input
     *            an item
     */
    public void insert(Comparable<?> input) {
        if (currSize >= maxSize) {
            System.out.println("heap is full");
            return;
        }

        int currPos = currSize++;
        heap[currPos] = input;
        int parentPos = this.getParentPos(currPos);

        while (currPos != 0 && heap[parentPos].compareTo(heap[currPos]) > 0) {
            this.swap(parentPos, currPos);
            currPos = parentPos;
            parentPos = this.getParentPos(currPos);
        }
    }


    /**
     * get the first element of the array
     * 
     * @return the root of the heap
     */
    public Comparable<?> getMinValue() {

        if (this.currSize == 0) {
            return null;
        }
        return heap[0];
    }


    /**
     * remove an item in the heap
     * 
     * @param position
     *            item position
     * @return true if the item exist
     */
    public boolean remove(int position) {
        if (invalid(position)) {
            return false;
        }

        currSize--;
        if (position == currSize) {
            return true;
        }

        this.swap(position, currSize);
        this.update(position);
        return true;
    }


    /**
     * remove minimum value
     * 
     * @return the min value after removed
     */
    public Comparable<?> removeMin() {

        if (currSize == 0) {
            return -1;
        }
        swap(0, --currSize);
        if (currSize != 0) {
            siftDown(0);
        }
        return heap[currSize];
    }


    /**
     * shift the item of current position down
     * to follow the heap order
     * 
     * @param position
     *            the item position
     */
    void siftDown(int position) {
        if (invalid(position)) {
            return;
        }

        while (!isLeaf(position)) {
            int minChildPos = getLChildPos(position);
            // Check outbound first then Compare left child with right child
            if (minChildPos < (currSize - 1) && heap[minChildPos].compareTo(
                heap[minChildPos + 1]) > 0) {
                minChildPos++;
            }
            if (heap[position].compareTo(heap[minChildPos]) <= 0) {
                return;
            }
            swap(position, minChildPos);
            position = minChildPos;
        }
    }


    /**
     * get the item position into the right place
     * 
     * @param position
     *            the position to update
     */
    public void update(int position) {
        int parentPos = this.getParentPos(position);
        while (position > 0 && heap[position].compareTo(heap[parentPos]) < 0) {
            swap(position, parentPos);
            position = parentPos;
        }
        if (currSize != 0) {
            this.siftDown(position);
        }
    }


    /**
     * modify a element inside the heap
     * then change its postition to correct
     * place
     * 
     * @param position
     *            the position to modify
     * @param input
     *            the item
     */
    public void modifyHeap(int position, Comparable<?> input) {
        if (invalid(position)) {
            return;
        }
        heap[position] = input;
        this.update(position);
    }


    /**
     * Check to see if the position is valid
     * 
     * @param position
     *            the checking index
     * @return true if position is invalid
     */
    boolean invalid(int position) {
        return position < 0 || position >= currSize;
    }


    /**
     * Check if the heap is full
     * 
     * @return true if its full
     */
    boolean isFull() {
        return currSize == maxSize;
    }


    /**
     * check to see if the heap is empty
     * 
     * @return true if the heap is empty
     */
    boolean isEmpty() {
        return currSize == 0;
    }


    /**
     * get the current size of the heap
     * 
     * @return the size of the heap
     */
    public int getCurrSize() {
        return currSize;
    }


    /**
     * This method is specifically for replacement selection
     * in external sort. Any elements inside the array but not
     * belong to the heap, the method will insert those element
     * back to the heap
     */
    public void reInsert() {
        for (int i = currSize; i < maxSize; i++) {
            this.insert(heap[i]);
        }
    }


    /**
     * This method is specifically for replacement selection
     * in external sort. If the first item A in the input buffer
     * is bigger than the last item B of output buffer, this method will
     * replace root with B then shift B down to the correct position.
     * Meanwhile, it will store A if the end of the array
     * 
     * @param input
     *            the input
     */
    public void nextHeapRun(Comparable<?> input) {
        heap[0] = input;
        swap(0, currSize - 1);
        currSize--;
        siftDown(0);
    }


    // _____________________ Run Helper______________

    /**
     * runs here is the info of each run inside the heap
     * clear will reset the heap which set the current size
     * to 0 then clear all the run information
     */
    public void clear() {
        currSize = 0;
        runs.clear();
    }


    /**
     * get total number of runs in the array
     * 
     * @return total run number
     */
    public int getTotalRun() {
        return runs.size();
    }


    /**
     * initialize a run inside the array
     * 
     * @param amountItem
     *            the number of item of that run
     */
    public void addRun(int amountItem) {
        runs.add(new RunInfo(currSize, currSize + amountItem, 1));
    }


    /**
     * Simply insert Item into the array
     * 
     * @param item
     *            the item
     */
    public void insertItemToArr(Comparable<?> item) {
        heap[currSize] = item;
        currSize++;
    }


    /**
     * insert item into a specific run
     * 
     * @param item
     *            the item
     * @param runNum
     *            which run to insert
     */
    public void insertItemToRun(Comparable<?> item, int runNum) {
        int checking = runs.get(runNum).getCurrIndex();
        heap[checking] = item;
        runs.get(runNum).incIndex();
    }


    /**
     * change the current index of a run to its starting index
     * 
     * @param runNum
     *            which run to reset
     */
    public void resetRun(int runNum) {
        runs.get(runNum).reset();
    }


    /**
     * remove the item inside a run
     * 
     * @param runNum
     *            which run
     * @return the item of a run which its current index pointing to
     */
    public Comparable<?> removeMinFromRun(int runNum) {

        if (runNum >= runs.size()) {
            System.out.print("removeMinFromRun outbound");
            return null;
        }
        if (runs.get(runNum).isEmpty()) {
            runs.get(runNum).reset();
            return null;
        }

        int temp = runs.get(runNum).getCurrIndex();
        runs.get(runNum).incIndex();
        return heap[temp];
    }


    /**
     * check to see if the run is empty
     * 
     * @param runNum
     *            the run number
     * @return return true if the run is empty
     */
    public boolean isRunEmpty(int runNum) {
        return runs.get(runNum).isEmpty();
    }


    /**
     * get current index of the specific run
     * 
     * @param runNum
     *            the run number
     * @return current index number of that run
     */
    public int getCurrIndex(int runNum) {
        return runs.get(runNum).getCurrIndex();
    }


    /**
     * Get a specific run information
     * 
     * @param runNum
     *            run number
     * @return the run information
     */
    public RunInfo getRun(int runNum) {
        return runs.get(runNum);
    }


    /**
     * set the end index of a specific run
     * 
     * @param runNum
     *            run number
     * @param endIndex
     *            end index
     */
    public void setEndIndex(int runNum, int endIndex) {
        runs.get(runNum).setEndIndex(endIndex);
    }
}
