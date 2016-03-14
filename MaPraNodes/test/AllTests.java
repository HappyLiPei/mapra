import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import io.TestFileUtilities;
import io.TestIOReaderWriter;
import phenomizeralgorithm.TestComparatorPheno;
import phenomizeralgorithm.TestDataTransformer;
import phenomizeralgorithm.TestFrequencyConverter;
import phenomizeralgorithm.TestOntology;
import phenomizeralgorithm.TestPValueFolder;
import phenomizeralgorithm.TestPhenomizer;
import phenomizeralgorithm.TestPhenomizerPVal;
import phenomizeralgorithm.TestPvalueCorrection;
import phenomizeralgorithm.TestSampling;
import phenomizeralgorithm.validation.TestValidationWithFrequentWeightsPreparation;
import phenomizeralgorithm.validation.TestValidationWithFrequentWeightsRanking;
import phenotogeno.TestPhenoToGenoDataStructures;
import phenotogeno.TestPhenoToGenoFileIO;

@RunWith(Suite.class)
@SuiteClasses({ TestIOReaderWriter.class, 
				TestFileUtilities.class,
				TestPhenomizer.class,
				TestFrequencyConverter.class,
				TestPValueFolder.class,
				TestPvalueCorrection.class,
				TestDataTransformer.class,
				TestSampling.class,
				TestOntology.class,
				TestPhenomizerPVal.class,
				TestValidationWithFrequentWeightsPreparation.class,
				TestPhenoToGenoFileIO.class,
				TestPhenoToGenoDataStructures.class,
				TestComparatorPheno.class,
				TestValidationWithFrequentWeightsRanking.class })
public class AllTests {

}
