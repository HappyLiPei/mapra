import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import geneticnetwork.TestEdges;
import geneticnetwork.TestFileParserNetworkScore;
import geneticnetwork.TestGeneticNetworkScore;
import geneticnetwork.TestMatrixVectorBuilder;
import geneticnetwork.TestRandomWalk;
import geneticnetwork.TestScoredGenes;
import geneticnetwork.TestSparseMatrix;
import geneticnetwork.TestVector;
import io.TestFileUtilities;
import io.TestIOReaderWriter;
import metabolites.TestDataTransformerMetabolites;
import metabolites.TestFileUtilitiesMetabolites;
import metabolites.TestMetaboliteScoringInReference;
import metabolites.TestScoreMetabolites;
import metabotogeno.TestMetaboToGenoDataStructures;
import metabotogeno.TestMetaboToGenoIO;
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
import phenotogeno.validation.TestFilter;
import phenotogeno.validation.TestIteratorAll;
import phenotogeno.validation.TestRankCalculation;
import phenotogeno.validation.TestSimulator;
import phenotogeno.validation.TestSimulatorIteratorFromFile;
import phenotogeno.validation.TestValidationProcedure;
import togeno.TestScoredGeneComparator;

@RunWith(Suite.class)
@SuiteClasses({ TestIOReaderWriter.class, 
				//phenomizer
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
				//validation phenomizer
				TestValidationWithFrequentWeightsPreparation.class,
				TestValidationWithFrequentWeightsRanking.class,
				TestValidationWithFrequentWeights.class,
				TestValidationIO.class,
				TestValidationOMIMPreparation.class,
				TestValidationOMIM.class,
				//phenotogeno
				TestPhenoToGenoFileIO.class,
				TestPhenoToGenoDataStructures.class,
				TestPhenoToGenoAlgo.class,
				TestPhenoToGeno.class,
				TestCombinationWithPhenomizer.class,
				//togeno
				TestScoredGeneComparator.class,
				//validation phenotype/phenotogeno
				TestRankCalculation.class,
				TestValidationProcedure.class,
				TestIteratorAll.class,
				TestSimulator.class,
				TestSimulatorIteratorFromFile.class,
				TestFilter.class,
				//metabolite scores
				TestFileUtilitiesMetabolites.class,
				TestDataTransformerMetabolites.class,
				TestMetaboliteScoringInReference.class,
				TestScoreMetabolites.class,
				//network score
				TestFileParserNetworkScore.class,
				TestEdges.class,
				TestScoredGenes.class,
				TestVector.class, 
				TestSparseMatrix.class,
				TestMatrixVectorBuilder.class,
				TestRandomWalk.class,
				TestGeneticNetworkScore.class,
				//metabotogeno
				TestMetaboToGenoIO.class,
				TestMetaboToGenoDataStructures.class })
public class AllTests {

}
