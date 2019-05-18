import java.io.*;
import java.util.*;
import java.nio.ByteBuffer;

/**
 * // On my honor:
 * //
 * // - I have not used source code obtained from another student,
 * // or any other unauthorized source, either modified or
 * // unmodified.
 * //
 * // - All source code and documentation used in my program is
 * // either my original work, or was derived by me from the
 * // source code published in the textbook for this course.
 * //
 * // - I have not discussed coding details about this project with
 * // anyone other than my partner (in the case of a joint
 * // submission), instructor, ACM/UPE tutors or the TAs assigned
 * // to this course. I understand that I may discuss the concepts
 * // of this program with other students, and that another student
 * // may help me debug my program so long as neither of us writes
 * // anything during the discussion or modifies any computer file
 * // during the discussion. I have violated neither the spirit nor
 * // letter of this restriction.
 * // Main file
 * 
 * 
 * 
 * 
 * 
 * @author Hung Tran
 * @author Chenkai Ren
 * @version 1.0
 */

public class Externalsort {
    static private int blockSize = 8192; // in bytes 8192
    static private int recSize = 16;
    static private int outBufIndex = 0;
    static private double lastDataOut = -Double.MAX_VALUE;
    static private LinkedList<RunInfo> file1Info = new LinkedList<RunInfo>();
    static private int begin = 0;
    static private int totalSize = -1;
    static private int maxRun = 8;


    /**
     * This is the main class
     * 
     * @param args
     *            args[0] = the file name
     * @throws IOException
     *             if the file is not found
     */

    public static void main(String[] args) throws IOException {
        begin = 0;
        totalSize = -1;
        file1Info.clear();
        outBufIndex = 0;
        lastDataOut = -Double.MAX_VALUE;

        MinHeap heap = new MinHeap();
        String file1Path = args[0];
        String file2Path = "runFile.bin";
        String tempPath = "temp.bin";
        removeFile(file2Path);
        removeFile(tempPath);
        RandomAccessFile raf = new RandomAccessFile(file1Path, "rw");
        RandomAccessFile runFile = new RandomAccessFile(file2Path, "rw");
        RandomAccessFile tempFile = new RandomAccessFile(tempPath, "rw");

        LinkedList<RunInfo> file2Info = new LinkedList<RunInfo>();
        LinkedList<RunInfo> file3Info = new LinkedList<RunInfo>();
        file1Info.clear();
        byte[] inBuf = new byte[blockSize];
        byte[] outBuf = new byte[blockSize];
        int buffIndex = 0;
        replacementSelection(raf, runFile, heap, buffIndex, inBuf, outBuf);
        file2Info = (LinkedList<RunInfo>)file1Info.clone();
        int firstRunSize = file1Info.size();
        LinkedList<RunInfo> currFileInfo;
        LinkedList<RunInfo> nextFileInfo;
        currFileInfo = file2Info;
        nextFileInfo = file3Info;
        boolean file2 = true;

        while (currFileInfo.size() > 1) {
            begin = 0;
            totalSize = -1;
            if (file2) {
                file3Info.clear();
                tempFile.seek(0);
            }
            else {
                file2Info.clear();
                runFile.seek(0);
            }

            while (fullyMerge(currFileInfo, nextFileInfo)) {
                file1Info.clear();
                if (currFileInfo.size() <= maxRun) {
                    file1Info = (LinkedList<RunInfo>)currFileInfo.clone();
                    totalSize = file1Info.get(file1Info.size() - 1)
                        .getEndIndex() - file1Info.get(0).getStartIndex();
                    if (file2) {
                        eightWaysMerge(heap, tempFile, runFile, inBuf, outBuf);
                        updateRunInfo(file3Info);
                    }
                    else {
                        eightWaysMerge(heap, runFile, tempFile, inBuf, outBuf);
                        updateRunInfo(file2Info);
                    }
                    break;
                }
                else {
                    for (int i = 0; i < maxRun; i++) {
                        file1Info.add(currFileInfo.get(0));
                        currFileInfo.removeFirst();
                    }
                    totalSize = file1Info.get(maxRun - 1).getEndIndex()
                        - file1Info.get(0).getStartIndex();

                    if (file2) {
                        eightWaysMerge(heap, tempFile, runFile, inBuf, outBuf);
                        updateRunInfo(file3Info);
                    }
                    else {
                        eightWaysMerge(heap, runFile, tempFile, inBuf, outBuf);
                        updateRunInfo(file2Info);
                    }
                }
            }

            if (file2) {
                currFileInfo = file3Info;
                nextFileInfo = file2Info;
                file2Info.clear();
                file2 = false;
            }
            else {
                currFileInfo = file2Info;
                nextFileInfo = file3Info;
                file3Info.clear();
                file2 = true;
            }
        }

        if (firstRunSize == 1 || firstRunSize > 8 && firstRunSize < 65) {
            printResult(runFile);
        }
        else {
            printResult(tempFile);
        }
    }

    /**
     * This method help remove the file 
     * @param dataPath data path to the file
     * @throws IOException input output exception
     */
    public static void removeFile(String dataPath) throws IOException {
        FileWriter fw = new FileWriter(dataPath, false);
        fw.close();
    }


    /**
     * This method print the result to the file
     * This method so far is used for 8 blocks of file
     * 
     * @param fileResult
     *            the file that we want to write
     * @throws IOException
     *             any error occurs for wrtie to file
     */
    public static void printResult(RandomAccessFile fileResult)
        throws IOException {
        int items = 0;
        for (int i = 0; i < fileResult.length(); i += blockSize) {
            fileResult.seek(i);
            System.out.print(fileResult.readLong() + " ");
            if (items == 4) {
                System.out.print(fileResult.readDouble() + "\n");
                items = -1;
            }
            else {
                System.out.print(fileResult.readDouble() + " ");
            }

            items++;
        }
    }


    /**
     * this method runs the replacement selection sort.
     * Which takes in the original file and put into a heap.
     * When heap is full, take in a block to the input buffer
     * input buffer then sents element to the heap
     * when output buffer is full push to run file.
     * 
     * @param raf
     *            given file
     * @param runFile
     *            file we generate to saved for 8 way merge
     * @param heap
     *            minheap to store 8 blocks
     * @param buffIndex
     *            the index to keep in track of the buffer
     * @param inBuf
     *            input buffer
     * @param outBuf
     *            output buffer
     * @throws IOException
     *             if any error occurs runs to an error
     */
    public static void replacementSelection(
        RandomAccessFile raf,
        RandomAccessFile runFile,
        MinHeap heap,
        int buffIndex,
        byte[] inBuf,
        byte[] outBuf)
        throws IOException {
        boolean lastInBuff = false;
        boolean initHeap = false;

        double totalByte = raf.length();
        for (int i = 0; i < (totalByte / blockSize); i++) {
            raf.seek(buffIndex);
            raf.readFully(inBuf);
            buffIndex = buffIndex + blockSize;
            if (!initHeap) {
                buildHeap(heap, inBuf, blockSize);
                if (heap.isFull()) {
                    initHeap = true;
                    if (i + 1 == totalByte / blockSize) {
                        writeHeapToFile(runFile, heap, outBuf);
                    }
                }
            }
            else {
                if (i == (totalByte / blockSize) - 1) {
                    lastInBuff = true;
                }
                selectionHelper(heap, inBuf, outBuf, blockSize, runFile,
                    lastInBuff);
            }
        }
    }


    /**
     * This function is a special case for 8 blocks of data only
     * When the size of the file is less than or equal the size
     * of the min heap, we dont need multiway merge method
     * 
     * @param runFile
     *            the run file
     * @param heap
     *            minimum heap
     * @param outBuf
     *            output buffer
     * @throws IOException
     *             exception for the file
     */
    static public void writeHeapToFile(
        RandomAccessFile runFile,
        MinHeap heap,
        byte[] outBuf)
        throws IOException {
        while (!heap.isEmpty()) {
            writeToOutBuff(outBuf, (Node<Long, Double>)heap.removeMin(),
                runFile);
            totalSize += recSize;
        }
        updateRunInfo(file1Info);
    }


    /**
     * this method do the fully merge based on the runinfo
     * 
     * @param currRun
     *            current run information
     * @param nextRun
     *            the combined run information
     * @return true if size at current and next is not 0
     */
    public static boolean fullyMerge(
        LinkedList<RunInfo> currRun,
        LinkedList<RunInfo> nextRun) {
        return !(currRun.size() == 0 && nextRun.size() == 0);
    }


    /**
     * this method simply do the eight ways merge.
     * If file is more than 8 we will combined 8 runs into one
     * run and saved in a temporary file and then call eight ways merge
     * sort to combined the runs until there is only one run left which
     * we will stop this method.
     * 
     * @param heap
     *            big heap array to hold the runs
     * @param tempFile
     *            the temporary file to keep in track of combined runs
     * @param runFile
     *            original run file hold all the runs
     * @param inBuf
     *            input buffer
     * @param outBuf
     *            output buffer
     * @throws IOException
     *             any error occured when facing random accessfile method.
     */
    public static void eightWaysMerge(
        MinHeap heap,
        RandomAccessFile tempFile,
        RandomAccessFile runFile,
        byte[] inBuf,
        byte[] outBuf)
        throws IOException {
        heap.clear();
        outBufIndex = 0;
        // the real test is 8
        if (file1Info.size() <= maxRun) {
            int blkAmount = 1;
            for (int runNum = 0; runNum < file1Info.size(); runNum++) {

                int start = file1Info.get(runNum).getStartIndex();
                int end = file1Info.get(runNum).getEndIndex();
                if (end - start < blockSize - 1) {
                    heap.addRun((end - start + 1) / 16 - 1);
                }
                else {
                    heap.addRun((512 - 1));
                }
                addBufToArray(heap, inBuf, runNum, runFile);

            }
            // create a small heap
            int runNumber = 0;
            MinHeap smHeap = new MinHeap(new RunNode[file1Info.size()]);
            // Initialize the heap
            while (!smHeap.isFull()) {
                RunNode rNode = new RunNode(runNumber, heap.removeMinFromRun(
                    runNumber));
                smHeap.insert(rNode);
                runNumber++;
                if (runNumber == heap.getTotalRun()) {
                    runNumber = 0;
                }
            }

            // start to take things out the heap
            while (!smHeap.isEmpty()) {
                // remove first element of the heap
                RunNode rNode = (RunNode)smHeap.removeMin();
                Node minValue = (Node)rNode.getData();
                // put it to output buffer
                writeToOutBuff(outBuf, minValue, tempFile);

                runNumber = rNode.getRunNum();
                // get a new item from big heap of that run then put the to
                // smHeap
                minValue = (Node)heap.removeMinFromRun(runNumber);
                // If the new item from big heap of that run does not exist
                // it means that run in the heap is empty
                if (minValue == null) {
                    // Then check for that run from the Run file
                    // If that run in the Run file is not empty
                    if (!file1Info.get(runNumber).isEmpty()) {
                        // migrade that run from the file to the heap
                        // Check to see if its first run
                        addBufToRun(heap, inBuf, runNumber, blkAmount, runFile);
                        // add that run into the small heap
                        minValue = (Node)heap.removeMinFromRun(runNumber);
                        smHeap.insert(new RunNode(runNumber, minValue));
                    }
                }
                else {
                    // put that new item into the small heap
                    smHeap.insert(new RunNode(runNumber, minValue));
                }
            }
        }

        if (outBufIndex > 0)

        {
            byte[] tempArray = new byte[outBufIndex];
            for (int i = 0; i < outBufIndex; i++) {
                tempArray[i] = outBuf[i];
            }
            tempFile.write(tempArray);
        }
    }


    /**
     * this method help the selection sort method
     * 
     * @param heap
     *            the min heap to help us sort the minium record
     * @param inBuf
     *            input buffer
     * @param outBuf
     *            output buffer
     * @param inBufSize
     *            the size of input buffer
     * @param runFile
     *            the first run file information (
     *            beg index, ending index, current index, run number)
     * @param finalInBuf
     *            this is the last input buffer from the original file(given)
     * @throws IOException
     *             any error occurs when reading data
     */
    public static void selectionHelper(
        MinHeap heap,
        byte[] inBuf,
        byte[] outBuf,
        int inBufSize,
        RandomAccessFile runFile,
        boolean finalInBuf)
        throws IOException {

        double firstDataIn = 0;
        long firstIDIn = 0;
        byte[] data = new byte[8];

        for (int i = 0; i < inBufSize; i += recSize) {

            Node<Long, Double> minValue = (Node<Long, Double>)heap
                .getMinValue();

            totalSize += recSize;

            firstIDIn = bytesToLong(inBuf, i);
            firstDataIn = bytesToDouble(inBuf, i + 8);

            writeToOutBuff(outBuf, minValue, runFile);
            lastDataOut = minValue.getData();

            if (lastDataOut <= firstDataIn) {
                heap.modifyHeap(0, new Node<Long, Double>(firstIDIn,
                    firstDataIn));
            }

            else if (lastDataOut > firstDataIn) {
                heap.nextHeapRun(new Node<Long, Double>(firstIDIn,
                    firstDataIn));
            }

            if (heap.isEmpty()) {
                heap.reInsert();
                writeToFile(runFile, outBuf);
                updateRunInfo(file1Info);
                lastDataOut = -Double.MAX_VALUE;
            }
        }

        if (finalInBuf) {

            if (outBufIndex > 0) {
                writeToFile(runFile, outBuf);
                updateRunInfo(file1Info);
            }

            else if (!heap.isFull()) {
                updateRunInfo(file1Info);
            }

            heap.reInsert();
            while (!heap.isEmpty()) {
                writeToOutBuff(outBuf, (Node<Long, Double>)heap.removeMin(),
                    runFile);
                totalSize += recSize;
                updateRunInfo(file1Info);
            }

        }
    }


    // ____________________ Helper Functions__________________

    /**
     * this update the run info
     * 
     * @param file
     *            file information
     */
    public static void updateRunInfo(LinkedList<RunInfo> file) {
        file.add(new RunInfo(begin, begin + totalSize, blockSize));
        begin += totalSize + 1;
        totalSize = -1;
    }


    /**
     * conversion from bytes to double
     * 
     * @param input
     *            input bytes array
     * @param offSet
     *            the number of offset to seek in the byte array
     * @return the double value of that array
     */
    public static double bytesToDouble(byte[] input, int offSet) {
        byte[] data = new byte[8];
        int index = 0;
        for (int i = offSet; i < offSet + 8; i++) {
            data[index] = input[i];
            index++;
        }
        ByteBuffer result = ByteBuffer.wrap(data);
        return result.getDouble();
    }


    /**
     * this converts bytes to long
     * 
     * @param input
     *            the input buffer
     * @param offSet
     *            the offest
     * @return the long value
     */
    public static long bytesToLong(byte[] input, int offSet) {
        byte[] data = new byte[8];
        int index = 0;
        for (int i = offSet; i < offSet + 8; i++) {
            data[index] = input[i];
            index++;
        }
        ByteBuffer result = ByteBuffer.wrap(data);
        return result.getLong();
    }


    /**
     * this converts double to bytes
     * 
     * @param input
     *            the input buffer
     * @return the byte array
     */
    public static byte[] doubleToBytes(double input) {
        ByteBuffer result = ByteBuffer.allocate(Double.BYTES);
        result.putDouble(input);
        return result.array();
    }


    /**
     * this converts long long to bytes
     * 
     * @param input
     *            the input long
     * @return the byte array
     */
    public static byte[] longToBytes(long input) {
        ByteBuffer result = ByteBuffer.allocate(Long.BYTES);
        result.putLong(input);
        return result.array();
    }


    /**
     * this writes to the file.
     * 
     * @param file
     *            the file to be saved
     * @param outBuf
     *            the output buffer
     * @throws IOException
     *             the error
     */
    public static void writeToFile(RandomAccessFile file, byte[] outBuf)
        throws IOException {
        file.write(outBuf, 0, outBufIndex);
        outBufIndex = 0;
    }


    /**
     * this builds the heap
     * 
     * @param heap
     *            the heap array
     * @param inBuf
     *            the input buffer
     * @param bffLength
     *            the buffer length
     */
    public static void buildHeap(MinHeap heap, byte[] inBuf, int bffLength) {

        long iD = 0;
        double value = 0;

        for (int i = 0; i < bffLength; i += recSize) {
            iD = bytesToLong(inBuf, i);
            value = bytesToDouble(inBuf, i + 8);
            heap.insert(new Node<Long, Double>(iD, value));
        }
    }


    /**
     * the write to output buffer
     * 
     * @param outBuf
     *            the outbuffer
     * @param data
     *            the data node
     * @param file
     *            the randomaccessfile
     * @throws IOException
     *             the error
     */
    public static void writeToOutBuff(
        byte[] outBuf,
        Node<Long, Double> data,
        RandomAccessFile file)
        throws IOException {

        pushEightBytes(outBuf, longToBytes(data.getID()), outBufIndex, 8);
        pushEightBytes(outBuf, doubleToBytes(data.getData()), outBufIndex + 8,
            8);
// System.out.println("Output Buff -> " + data.getData());
        outBufIndex += recSize;

        if (outBufIndex >= blockSize - 1) {
            writeToFile(file, outBuf);
        }
    }


    /**
     * the file push eifht bytes array
     * 
     * @param outBuf
     *            the output buffer
     * @param data
     *            the data array
     * @param offSet
     *            the offset to start with
     * @param length
     *            the length
     */
    public static void pushEightBytes(
        byte[] outBuf,
        byte[] data,
        int offSet,
        int length) {
        int index = 0;
        for (int i = offSet; i < offSet + length; i++) {
            outBuf[i] = data[index];
            index++;
        }
    }


    /**
     * the addbuffer to array
     * 
     * @param heap
     *            the heap
     * @param inBuf
     *            input buffer
     * @param runNum
     *            the number of run in the info size
     * @param runFile
     *            the runfile information
     * @throws IOException
     *             the error
     */
    public static void addBufToArray(
        MinHeap heap,
        byte[] inBuf,
        int runNum,
        RandomAccessFile runFile)
        throws IOException {

        double value;
        long id;

        int start = file1Info.get(runNum).getStartIndex();
        int end = file1Info.get(runNum).getEndIndex();
        int currBSize = end - start;
        runFile.seek(start);

        if (currBSize < blockSize - 1) {
            int temp = 0;
            for (int i = 0; i <= currBSize; i++) {
                // read byte by byte
                inBuf[i] = runFile.readByte();
                if ((i + 1) % 16 == 0) {
                    id = bytesToLong(inBuf, temp);
                    value = bytesToDouble(inBuf, temp + 8);
                    heap.insertItemToArr(new Node<Long, Double>(id, value));
                    temp += recSize;
                }
            }
        }

        else {
            runFile.readFully(inBuf);
            // Read data from that block in then store to the heap array
            for (int i = 0; i < blockSize; i += recSize) {
                id = bytesToLong(inBuf, i);
                value = bytesToDouble(inBuf, i + 8);
                heap.insertItemToArr(new Node<Long, Double>(id, value));
            }
        }

        file1Info.get(runNum).incIndex(); // increase index of 1 block
    }


    /**
     * this methods puts the buffer to a run file
     * 
     * @param heap
     *            the heap array for 8 way merge
     * @param inBuf
     *            the input buffer
     * @param runNum
     *            the run
     * @param blkAmount
     *            the blocks ammount
     * @param runFile
     *            the run file
     * @throws IOException
     *             the error
     */
    public static void addBufToRun(
        MinHeap heap,
        byte[] inBuf,
        int runNum,
        int blkAmount,
        RandomAccessFile runFile)
        throws IOException {

        double value;
        long id;
        int startIndexRF;

        // If there is not full block
        // call this function
        int end = file1Info.get(runNum).getEndIndex();
        int curr = file1Info.get(runNum).getCurrIndex();
        startIndexRF = file1Info.get(runNum).getCurrIndex();

        runFile.seek(startIndexRF);

        int currBSize = end - curr;
        if (currBSize < blockSize - 1) {
            int temp = 0;
            for (int i = 0; i <= currBSize; i++) {
                // read byte by byte
                inBuf[i] = runFile.readByte();
                if ((i + 1) % 16 == 0) {
                    id = bytesToLong(inBuf, temp);
                    value = bytesToDouble(inBuf, temp + 8);
                    heap.insertItemToRun(new Node<Long, Double>(id, value),
                        runNum);
                    temp += recSize;
                }
            }
            heap.setEndIndex(runNum, heap.getRun(runNum).getStartIndex()
                + (currBSize + 1) / recSize - 1);
            // update the ending index of that
        }
        else {

            runFile.readFully(inBuf);

            for (int i = 0; i < blockSize; i += recSize) {
                id = bytesToLong(inBuf, i);
                value = bytesToDouble(inBuf, i + 8);
                heap.insertItemToRun(new Node<Long, Double>(id, value), runNum);
            }
        }
        file1Info.get(runNum).incIndex(); // increase index of 1 block
        heap.resetRun(runNum);
    }

}