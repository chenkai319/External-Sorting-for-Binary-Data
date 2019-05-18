
import java.io.IOException;
import java.util.Random;

/**
 * Testing all the functionality of MinHeap class
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 */

public class MinHeapTest extends student.TestCase {
    private MinHeap heap;


    /**
     * initialize the test
     */
    public void setUp() {
        heap = new MinHeap();
    }


    /**
     * Test all the functionality oh minimum heap
     */
    public void testHeapFuntionality() {
        Random rand = new Random();

        for (int i = 0; i < 100; i++) {
            heap.insert(rand.nextDouble());
        }

        // The the size of heap after insert
        assertEquals(heap.getCurrSize(), 100);

        // Test remove min
        Comparable firstValue = heap.removeMin();
        Comparable secondValue;
        for (int i = 0; i < 99; i++) {
            secondValue = heap.removeMin();
            assertTrue(firstValue.compareTo(secondValue) <= 0);
            firstValue = secondValue;
        }
        secondValue = heap.removeMin();
        assertEquals(secondValue.compareTo(-1), 0);
        // Test if heap is empty
        assertTrue(heap.isEmpty());

        heap.insert(rand.nextDouble());
        // Test get left and right child of a leaf node
        assertEquals(-1, heap.getLChildPos(0));
        assertEquals(-1, heap.getRChildPos(0));

        // Check to see the position is a leaf
        assertTrue(heap.isLeaf(0));
        assertFalse(heap.isLeaf(10));

        Comparable[] array = new Comparable[100];

        for (int i = 0; i < 100; i++) {
            array[i] = rand.nextDouble();
        }
        // Check insert into a full heap
        MinHeap fullHeap = new MinHeap(array);
        fullHeap.reInsert();
        assertTrue(fullHeap.isFull());
        assertEquals(fullHeap.getCurrSize(), 100);
        fullHeap.insert(rand.nextDouble());
        assertEquals(fullHeap.getCurrSize(), 100);

        // test get the minimum value of heap.
        assertEquals(fullHeap.getMinValue(), fullHeap.removeMin());

        // test clear heap
        heap.clear();
        assertTrue(heap.isEmpty());

        // test get minimum value of an empty heap
        assertEquals(heap.getMinValue(), null);

        // test sift down an none existing index
        heap.siftDown(100);

        // test remove a none existing index
        assertFalse(heap.remove(100));

        // test remove 1 element
        heap.insert(rand.nextDouble());
        assertEquals(heap.getCurrSize(), 1);
        heap.remove(0);
        assertTrue(heap.isEmpty());

        // test modify 1 elemnt and shift down
        fullHeap.modifyHeap(0, Double.MAX_VALUE);
        secondValue = fullHeap.removeMin();
        fullHeap.remove(80);
        assertFalse(fullHeap.isFull());
        // make the heap full again for testing purpose
        heap.clear();
        for (int i = 0; i < 100; i++) {
            heap.insert(rand.nextDouble());
        }
        // test modify an non existing position
        fullHeap.modifyHeap(10000, -100);
        // Test to see heap is not empty
        assertFalse(heap.isEmpty());
        /**
         * RunNode is STRICTLY used for 8 records heap ONLY
         * RunNode is simply a node with 1 extra element pointing
         * which run that node belong to
         * 
         * @author Hung Tran
         * @author Chenkai Ren
         * @version 1.0
         */
        // These test down here specifically for multiway merge
        // helper methods in the heaps only

        assertEquals(heap.getCurrSize(), 100);
        heap.nextHeapRun(-100);
        assertEquals(heap.getCurrSize(), 99);
        // Check for run amount before adding any run
        assertEquals(heap.getTotalRun(), 0);
        // Add 1 run with ending index 100
        heap.addRun(20);
        // Test for total amount of run after added 1 run
        assertEquals(heap.getTotalRun(), 1);

        // Test insert to array inside heap without sorting
        heap.clear();
        heap.addRun(20);
        for (int i = 0; i < 100; i++) {
            heap.insertItemToArr(100 - i);
        }
        secondValue = heap.getMinValue();
        assertNotSame(secondValue.compareTo(99), 0);
        heap.resetRun(0);
        // Test insert 100 to 120 to run 0 only
        for (int i = 100; i <= 120; i++) {
            heap.insertItemToRun(i, 0);
        }

        // Check if a run is empty
        assertTrue(heap.isRunEmpty(0));
        // Remove min value from empty run
        assertEquals(heap.removeMinFromRun(0), null);

        heap.resetRun(0);
        // Test remove min from run
        for (int i = 0; i < 20; i++) {
            secondValue = heap.removeMinFromRun(0);
            assertEquals(secondValue, i + 100);
        }

        // Test get current index of a run
        heap.getRun(0);
        assertEquals(heap.getCurrIndex(0), 20);

        // Change end index of run 0 to 100
        heap.setEndIndex(0, 100);

    }


    /**
     * this is test
     * 
     * @throws IOException
     *             error
     */
    public void testMain() throws IOException {

        Externalsort exSort = new Externalsort();
        String[] args = { "sampleInput16.bin" };
        exSort.main(args);
        heap.clear();
        assertTrue(heap.isEmpty());

    }

// @SuppressWarnings("static-access")
// public void testNormal500() throws IOException {
// try {
// Externalsort exSort = new Externalsort();
// String[]args = {"normal500.bin"};
// exSort.main(args);
// }
// catch (EOFException ex1) {
// System.out.print("error");
// }
// }
}
