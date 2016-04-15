import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import io.TestFileUtilities;
import io.TestIOReaderWriter;
import metabolites.TestDataTransformerMetabolites;
import metabolites.TestFileUtilitiesMetabolites;
import metabolites.TestMetaboliteScoringInReference;
import metabolites.TestScoreMetabolites;
import phenomizeralgorithm.TestComparatorPheno;
import phenomizeralgorithm.TestDataTransformer;
import phenomizeralgorithm.TestFrequencyConverter;
import phenomizeralgorithm.TestOntology;
import phenomizeralgorithm.TestPValueFolder;
import phenomizeralgorithm.TestPhenomizer;
import phenomizeralgorithm.TestPhenomizerPVal;
import phenomizeralgorithm.TestPvalueCorrection;
import phenomizeralgorithm.TestSampling;
import phenomizeralgorithm.TestSimMatrixCalculator;
import phenomizeralgorithm.validation.TestValidationIO;
import phenomizeralgorithm.validation.TestValidationOMIM;
import phenomizeralgorithm.validation.TestValidationOMIMPreparation;
import phenomizeralgorithm.validation.TestValidationWithFrequentWeights;
import phenomizeralgorithm.validation.TestValidationWithFrequentWeightsPreparation;
import phenomizeralgorithm.validation.TestValidationWithFrequentWeightsRanking;
import phenotogeno.TestCombinationWithPhenomizer;
import phenotogeno.TestPhenoToGeno;
import phenotogeno.TestPhenoToGenoAlgo;
import phenotogeno.TestPhenoToGenoDataStructures;
import phenotogeno.TestPhenoToGenoFileIO;
import phenotogeno.TestScoredGeneComparator;
import phenotogeno.validation.TestIteratorAll;
import phenotogeno.validation.TestRankCalculation;
import phenotogeno.validation.TestSimulator;
import phenotogeno.validation.TestValidationProcedure;

@RunWith(Suite.class)
@SuiteClasses({ TestIOReaderWriter.class, 
				TestFileUtilities.class,
				TestPhenomizer.class,
				TestFrequencyConverter.class,
				TestPValueFolder.class,
				TestPvalueCorrection.class,
				TestDataTransformer.class,
				TestOntology.class,
				TestPhenomizerPVal.class,
				TestComparatorPheno.class,
				TestSampling.class,
				TestSimMatrixCalculator.class,
				TestValidationWithFrequentWeightsPreparation.class,
				TestValidationWithFrequentWeightsRanking.class,
				TestValidationWithFrequentWeights.class,
				TestValidationIO.class,
				TestValidationOMIMPreparation.class,
				TestValidationOMIM.class,
				TestPhenoToGenoFileIO.class,
				TestPhenoToGenoDataStructures.class,
				TestPhenoToGenoAlgo.class,
				TestScoredGeneComparator.class,
				TestPhenoToGeno.class,
				TestCombinationWithPhenomizer.class,
				TestRankCalculation.class,
				TestValidationProcedure.class,
				TestIteratorAll.class,
				TestSimulator.class,
				TestFileUtilitiesMetabolites.class,
				TestDataTransformerMetabolites.class,
				TestMetaboliteScoringInReference.class,
				TestScoreMetabolites.class})
public class AllTests {

}
