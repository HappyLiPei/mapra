package main;

import io.FileInputReader;
import io.FileOutputWriter;
import phenomizer.algorithm.AlgoPheno;
import phenomizer.algorithm.FrequencyConverter;
import phenomizer.algorithm.PValueFolder;
import phenomizer.algorithm.PValueGenerator;
import phenomizer.io.FileUtilitiesPhenomizer;

import java.util.HashMap;
import java.util.LinkedList;

public class MainSophie {
	
	private static final int NO_WEIGHT_NO_P_VALUE=1;
	private static final int WEIGHT_NO_P_VALUE=2;
	private static final int NO_WEIGHT_P_VALUE=3;
	private static final int WEIGHT_P_VALUE=4;
	private static final int AS_WEIGHT_NO_P_VALUE=5;
	private static final int AS_WEIGHT_P_VALUE=6;
	
	/*
	 * generates query lists from text mining results
	 * HashMap omim id -> symptom list extracted via text mining 
	 */
	private HashMap<String, LinkedList<Integer>> readQueriesTM(String file){
		
		FileInputReader fir = new FileInputReader(file);
		String line=fir.read();
		
		// process header: extract omim ids
		String [] split=line.split("\t");
		System.out.println(split.length);
		
		HashMap<String, LinkedList<Integer>> queries = new HashMap<String, LinkedList<Integer>>(split.length*3);
		String [] idpos = new String [split.length-1];
		
		for(int i=1; i<split.length; i++){
			String id = extractId(split[i]);
			queries.put(id, new LinkedList<Integer>());
			idpos[i-1]=id;
		}
		
		while((line=fir.read())!=null){
			//read and check one line of text mining table
			String [] row_cells = line.split("\t");
			if(row_cells.length!=idpos.length+1){
				System.out.println("Cannot parse line "+ line);
			}
			int symp =-1;
			
			try{
				symp = Integer.valueOf(row_cells[0]);
			}
			catch(NumberFormatException e){
				System.out.println("Cannot parse symptom id "+row_cells[0]);
			}
			
			if(symp==-1){
				continue;
			}
			
			for(int i=0; i<idpos.length;i++){
				// if: value 1.0 -> add symptom id (column 0) to omim id at idpos[i] (column at position i+1)
				// else: value 0.0: do not add symptom id
				if(!row_cells[i+1].equals("0.0")){
					queries.get(idpos[i]).add(symp);
				}
			}
		}
		fir.closer();
		
		return queries;
	}
	
	// extract omim id from header and checks correct formatting
	private String extractId(String header){
		if(header.startsWith("Max*(")){
			String tmp = header.substring(5);
			if(tmp.matches("[0-9]{6}.*")){
				tmp=tmp.substring(0,6);
				//System.out.println(tmp);
				return tmp;
			}
			else{
				System.out.println("header does not contain six digit omim id");
				return "";
			}
		}
		else{
			System.out.println("header does not start with Max*(");
			return "";
		}
	}
	
	private LinkedList<String []> readOMIMPheno(String infile){
		
		FileInputReader fir = new FileInputReader(infile);
		String line=fir.read();
		
		LinkedList<String []> pheno_to_omim= new LinkedList<String []>();
		
		while((line=fir.read())!=null){
			String [] split = line.split("\t");
			if(split.length!=3){
				System.out.println("Format error");
				continue;
			}
			//pos 0: phenodis id, pos 1: omim id
			String [] id_pair = new String [2];
			
			if(!split[2].contains(";")){
				id_pair[0]=split[0];
				id_pair[1]=split[2];
				pheno_to_omim.add(id_pair);	
			}
		}
		return pheno_to_omim;
	}
	
	//TODO: adapt to new class structure
	private void runOMIMVal(int mode, String outfile, String phenofile, String tmfile, String isa, String symptom, String ksz, String folder_pval){
		
//		// read symptom queries for omim entries extracted with textmiming
//		HashMap<String, LinkedList<Integer>> queries = readQueriesTM(tmfile);
//		//read omim ids and corresponding phenodis disease_ids
//		LinkedList<String []> disease_pairs = readOMIMPheno(phenofile);
//		
//		//read input files for phenomizer data structure
//		HashMap<Integer, LinkedList<Integer[]>> ksz_struct = null;
//		if(mode==NO_WEIGHT_NO_P_VALUE||NO_WEIGHT_P_VALUE==mode){
//			ksz_struct = addWeights(FileUtilities.readInKSZ(ksz));
//		}
//		else{
//			ksz_struct = convertFreqs(FileUtilities.readInKSZFrequency(ksz));
//		}
//		LinkedList<Integer> symptom_struct = FileUtilities.readInSymptoms(symptom);
//		int [][] isa_struct = FileUtilities.readInOntology(isa);
//		
//		
//		boolean start = true;
//		int count = 1;
//		FileOutputWriter w = new FileOutputWriter(outfile);
//		
//		//pair[0]: pheno_dis pair[1]: omim
//		for(String[] pair :disease_pairs){
//			if(queries.containsKey(pair[1]) && !queries.get(pair[1]).isEmpty()){
//				System.out.println(count+" out of "+ disease_pairs.size());
//				System.out.println("Calculate pair "+pair[1]+"\t"+pair[0]);
//				
//				if(start){
//						AlgoPheno.setInput(queries.get(pair[1]),
//								symptom_struct,ksz_struct,isa_struct);
//						if(mode==NO_WEIGHT_P_VALUE|| mode ==WEIGHT_P_VALUE || mode==AS_WEIGHT_P_VALUE){
//							PValueFolder.setPvalFoder(folder_pval);
//						}
//						start = false;
//				}
//				else{
//					AlgoPheno.setQuery(queries.get(pair[1]));
//				}
//				
//				String line_out="";
//				if(mode==NO_WEIGHT_P_VALUE|| mode == WEIGHT_P_VALUE || mode==AS_WEIGHT_P_VALUE){
//					HashMap<Integer,Double> resPhenomizer = null;
//					if(mode==AS_WEIGHT_P_VALUE){
//						resPhenomizer = AlgoPheno.runPhenomizerWithPValue(false);
//					}
//					else{
//						resPhenomizer = AlgoPheno.runPhenomizerWithPValue(true);
//					}
//					LinkedList<String []> res=PValueGenerator.getResultsWithPvaluesForOMIM(resPhenomizer, 8000);
//					double [] rank = calculateRankANDPval(pair[0], res);
//					line_out=pair[0]+"\t"+pair[1]+"\t"+rank[0]+"\t"+rank[1];
//				}
//				else{
//					LinkedList<String []> res=null;
//					if(mode ==AS_WEIGHT_NO_P_VALUE){
//						res=AlgoPheno.runPhenomizer(8000,false);
//					}
//					else{
//						res=AlgoPheno.runPhenomizer(8000,true);
//					}
//					double rank = calculateRankNoPval(pair[0], res);
//					line_out=pair[0]+"\t"+pair[1]+"\t"+rank;
//				}
//				System.out.println(line_out);
//				w.writeFilelnAndFlush(line_out);
//			}
//			count++;
//		}
//		w.closew();
	}
	
	
	public static void main(String args[]){
		
		String file="/home/marie-sophie/Uni/mapra/omim/omim_tm_res.txt";
		String isa="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/isa_HPO_test.csv";
		String ksz="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/ksz_HPO_frequency.csv";
		String symptom="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/symptoms_HPO_test.csv";
		String phenofile ="/home/marie-sophie/Uni/mapra/omim/phenodis_omimids.txt";
		String outfile="/home/marie-sophie/Uni/mapra/omim/as_weight_p.txt";
		String pval="/home/marie-sophie/Uni/mapra/phenodis/pvalues_big_asweight";
		
		MainSophie ms = new MainSophie();
		ms.runOMIMVal(AS_WEIGHT_P_VALUE,outfile, phenofile,file, isa, symptom, ksz, pval);
				
	}
	
}
