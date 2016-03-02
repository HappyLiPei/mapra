import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import io.TestFileUtilities;
import io.TestIOReaderWriter;
import phenomizeralgorithm.TestDataTransformer;
import phenomizeralgorithm.TestFrequencyConverter;
import phenomizeralgorithm.TestPValueFolder;
import phenomizeralgorithm.TestPhenomizer;
import phenomizeralgorithm.TestPvalueCorrection;

@RunWith(Suite.class)
@SuiteClasses({ TestIOReaderWriter.class, 
				TestFileUtilities.class,
				TestPhenomizer.class,
				TestFrequencyConverter.class,
				TestPValueFolder.class,
				TestPvalueCorrection.class,
				TestDataTransformer.class })
public class AllTests {

}
