import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import io.TestFileUtilities;
import io.TestIOReaderWriter;

@RunWith(Suite.class)
@SuiteClasses({ TestIOReaderWriter.class, TestFileUtilities.class })
public class AllTests {

}
