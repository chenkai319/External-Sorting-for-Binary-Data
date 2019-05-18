
/**
 * test
 * @author chenkairen
 * @author tran Hung
 * @version 1.1
 * test
 */
public class RunNodeTest extends student.TestCase {

    private RunNode a;


    /**
     * set up only
     */
    public void setUp() {
        a = new RunNode();
    }


    /**
     * this is test case for runNodeClass()
     */
    public void testCases() {

        assertEquals(0, a.getRunNum());

    }

}
