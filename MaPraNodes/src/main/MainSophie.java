package main;

import io.FileUtilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;

import algorithm.AlgoPheno;
import algorithm.FrequencyConverter;
import algorithm.PValueFolder;
import algorithm.PValueGenerator;

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
	
	private double calculateRankNoPval(String disease_id, LinkedList<String []> pheno_res){
		
		String prev="";
		int count = 1;
		boolean found=false;
		int rank =0;
		
		//score[0]: disease_id, score[1]: score
		for(String [] score: pheno_res){
			if(!score[1].equals(prev)){
				if(found){
					//System.out.println(rank+"\t"+count);
					break;
				}
				rank++;
				prev=score[1];
				count=1;
			}
			else{
				rank++;
				count++;
			}
			if(score[0].equals(disease_id)){
				found=true;
			}
			//System.out.println(score[0]+"\t"+score[1]+"\t"+rank+"\t"+count);
		}
		
		if(!found){
			System.out.println("Error: could not calculate rank");
		}
		
		int sum=0;
		for(int i=0; i<count; i++){
			sum+=rank-i;
		}
		double d = ((double)sum)/count;
		//System.out.println(disease_id+" Rang: "+d);
		return d;
	}
	
	private double [] calculateRankANDPval(String disease_id, LinkedList<String []> pheno_res){
		
		double[] res = new double[2];
		
		String prev="";
		int count = 1;
		boolean found=false;
		int rank =0;
		
		//score[0]: disease_id, score[1]: score, score[2]:pvalue
		for(String [] score: pheno_res){
			if(!(score[2]+"+"+score[1]).equals(prev)){
				if(found){
					//System.out.println(rank+"\t"+count);
					break;
				}
				rank++;
				prev=score[2]+"+"+score[1];
				count=1;
			}
			else{
				rank++;
				count++;
			}
			if(score[0].equals(disease_id)){
				found=true;
				res[1]=Double.valueOf(score[2]);
			}
			//System.out.println(score[0]+"\t"+score[1]+"\t"+rank+"\t"+count);
		}
		
		if(!found){
			System.out.println("Error: could not calculate rank");
		}
		
		int sum=0;
		for(int i=0; i<count; i++){
			sum+=rank-i;
		}
		res[0] = ((double)sum)/count;
		//System.out.println(disease_id+" Rang: "+d);
		return res;
	}
	
	private HashMap<Integer, LinkedList<Integer []>> addWeights (HashMap<Integer, LinkedList<Integer>> ksz){
		
		HashMap<Integer, LinkedList<Integer[]>> res = new HashMap<Integer, LinkedList<Integer []>>(ksz.size()*3);
		for(Integer k: ksz.keySet()){
			LinkedList<Integer []> list = new LinkedList<Integer[]>();
			res.put(k, list);
			for(int i: ksz.get(k)){
				Integer [] symp_and_weight = new Integer [2];
				symp_and_weight[0]=i;
				symp_and_weight[1]=10;
				list.add(symp_and_weight);
			}			
		}
		return res;
	}
	
	private HashMap<Integer, LinkedList<Integer[]>> convertFreqs (HashMap<Integer, LinkedList<String[]>> ksz){
		
		HashMap<Integer, LinkedList<Integer[]>> res = new HashMap<Integer, LinkedList<Integer[]>>(ksz.size()*3);
		for(Integer k: ksz.keySet()){
			LinkedList<Integer []> list = new LinkedList<Integer[]>();
			res.put(k, list);
			for(String [] s: ksz.get(k)){
				Integer [] symp_and_weight = new Integer [2];
				symp_and_weight[0]=Integer.valueOf(s[0]);
				symp_and_weight[1]=FrequencyConverter.convertFrequency(s[1]);
				list.add(symp_and_weight);
			}
		}
		return res;
	}
	
	private void runOMIMVal(int mode, String outfile, String phenofile, String tmfile, String isa, String symptom, String ksz, String folder_pval){
		
		// read symptom queries for omim entries extracted with textmiming
		HashMap<String, LinkedList<Integer>> queries = readQueriesTM(tmfile);
		//read omim ids and corresponding phenodis disease_ids
		LinkedList<String []> disease_pairs = readOMIMPheno(phenofile);
		
		//read input files for phenomizer data structure
		HashMap<Integer, LinkedList<Integer[]>> ksz_struct = null;
		if(mode==NO_WEIGHT_NO_P_VALUE||NO_WEIGHT_P_VALUE==mode){
			ksz_struct = addWeights(FileUtilities.readInKSZ(ksz));
		}
		else{
			ksz_struct = convertFreqs(FileUtilities.readInKSZFrequency(ksz));
		}
		LinkedList<Integer> symptom_struct = FileUtilities.readInSymptoms(symptom);
		int [][] isa_struct = FileUtilities.readInOntology(isa);
		
		
		boolean start = true;
		int count = 1;
		Writer_Output w = new Writer_Output(outfile);
		
		//pair[0]: pheno_dis pair[1]: omim
		for(String[] pair :disease_pairs){
			if(queries.containsKey(pair[1]) && !queries.get(pair[1]).isEmpty()){
				System.out.println(count+" out of "+ disease_pairs.size());
				System.out.println("Calculate pair "+pair[1]+"\t"+pair[0]);
				
				if(start){
						AlgoPheno.setInput(queries.get(pair[1]),
								symptom_struct,ksz_struct,isa_struct);
						if(mode==NO_WEIGHT_P_VALUE|| mode ==WEIGHT_P_VALUE || mode==AS_WEIGHT_P_VALUE){
							PValueFolder.setPvalFoder(folder_pval);
						}
						start = false;
				}
				else{
					AlgoPheno.setQuery(queries.get(pair[1]));
				}
				
				String line_out="";
				if(mode==NO_WEIGHT_P_VALUE|| mode == WEIGHT_P_VALUE || mode==AS_WEIGHT_P_VALUE){
					HashMap<Integer,Double> resPhenomizer = null;
					if(mode==AS_WEIGHT_P_VALUE){
						resPhenomizer = AlgoPheno.runPhenomizerWithPValue(false);
					}
					else{
						resPhenomizer = AlgoPheno.runPhenomizerWithPValue(true);
					}
					LinkedList<String []> res=PValueGenerator.getResultsWithPvaluesForOMIM(resPhenomizer, 8000);
					double [] rank = calculateRankANDPval(pair[0], res);
					line_out=pair[0]+"\t"+pair[1]+"\t"+rank[0]+"\t"+rank[1];
				}
				else{
					LinkedList<String []> res=null;
					if(mode ==AS_WEIGHT_NO_P_VALUE){
						res=AlgoPheno.runPhenomizer(8000,false);
					}
					else{
						res=AlgoPheno.runPhenomizer(8000,true);
					}
					double rank = calculateRankNoPval(pair[0], res);
					line_out=pair[0]+"\t"+pair[1]+"\t"+rank;
				}
				System.out.println(line_out);
				w.writeFileln(line_out);
			}
			count++;
		}
		w.closew();
	}
	
//	private void showResultsFor(String [] pair, int mode, String tmfile, String isa, String symptom, String ksz, String folder_pval ){
//		// read symptom queries for omim entries extracted with textmiming
//		HashMap<String, LinkedList<Integer>> queries = readQueriesTM(tmfile);
//		
//		//print input
//		System.out.println("OMIM: " + pair[1] + "\tPhenoDis: " + pair[0]);
//		
//		//print query
//		System.out.println("Original query");
//		LinkedList<Integer> query = queries.get(pair[1]);
//		for(Integer i: query){
//			System.out.print(i+"\t");
//		}
//		System.out.println();
//		
//		//prepare phenomizer
//		HashMap<Integer, LinkedList<Integer[]>> ksz_struct = null;
//		if(mode==NO_WEIGHT_NO_P_VALUE||NO_WEIGHT_P_VALUE==mode){
//			ksz_struct = addWeights(FileUtilities.readInKSZ(ksz));
//		}
//		else{
//			ksz_struct = convertFreqs(FileUtilities.readInKSZFrequency(ksz));
//		}
//		LinkedList<Integer> symptom_struct = FileUtilities.readInSymptoms(symptom);
//		int [][] isa_struct = FileUtilities.readInOntology(isa);
//		AlgoPheno.setInput(queries.get(pair[1]), symptom_struct,ksz_struct,isa_struct);
//		if(mode==NO_WEIGHT_P_VALUE|| mode ==WEIGHT_P_VALUE){
//			PValueFolder.setPvalFoder(folder_pval);
//		}
//		
//		//get reduced query
//		query = AlgoPheno.queryIds;
//		System.out.println("Shortened query");
//		for(Integer i: query){
//			System.out.print(i+"\t");
//		}
//		System.out.println();
//		
//		// get symptoms for phenodis id
//		System.out.println("annotated Symptoms");
//		LinkedList<Integer[]> symptoms = AlgoPheno.kszD.get(Integer.valueOf(pair[0]));
//		for(Integer[] s : symptoms){
//			System.out.print(s[0]+"\t");
//		}
//		System.out.println();
//		
//		// run Phenomizer
//		System.out.println("Phenomizer results");
//		if(mode==NO_WEIGHT_P_VALUE|| mode == WEIGHT_P_VALUE){
//			HashMap<Integer,Double> resPhenomizer = AlgoPheno.runPhenomizerWithPValue();
//			LinkedList<String []> res=PValueGenerator.getResultsWithPvaluesForOMIM(resPhenomizer, 20);
//			for(String [] entry : res){
//				System.out.println(entry[0]+"\t"+entry[1]+"\t"+entry[2]);
//			}
//			String file = PValueFolder.getPvalFile(query.size());
//			FileInputReader fir = new FileInputReader(file);
//			String line="";
//			while((line=fir.read())!=null){
//				String [] split = line.split("\t");
//				if(split[0].equals(pair[0])){
//					System.out.println(split.length);
//					double pval = (double) /10000;
//					System.out.println(pval);
//					double pval_corrected = pval*AlgoPheno.kszD.size();
//					System.out.println(pval_corrected);
//					break;
//				}
//			}
//			fir.closer();
//			
//		}
//		else{
//			LinkedList<String []> res=AlgoPheno.runPhenomizer(8000);
//			double rank = calculateRankNoPval(pair[0], res);
//		}
//	}
	
	private void extractDiseasesWithFreq(String phenofile, String ksz, String out){
		
		//phenodis to omim
		LinkedList<String []> disease_pairs = readOMIMPheno(phenofile);
		//phenodis to symptom
		HashMap<Integer, LinkedList<Integer[]>> ksz_struct = convertFreqs(FileUtilities.readInKSZFrequency(ksz));
		
		Writer_Output w = new Writer_Output(out);
		int counter =0;
		for(String[] pair :disease_pairs){
			if(ksz_struct.containsKey(Integer.valueOf(pair[0]))){
				boolean freq = false;
				for(Integer[] symp: ksz_struct.get(Integer.valueOf(pair[0]))){
					if(symp[1]!=10){
						freq = true;
						
					}
				}
				if(freq){
					w.writeFileln(pair[0]+"\tunknown\t"+pair[1]);
					counter++;
				}
			}
		}
		System.out.println(counter);
		w.closew();
		
	}
	
	
	
	public static void main(String args[]){
		
		String file="/home/marie-sophie/Uni/mapra/omim/omim_tm_res.txt";//"D:\\transfer\\omim_tm_res.txt"; //;
		String isa="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/isa_HPO_test.csv";//"D:\\transfer\\Datenbank\\isa_HPO_test.csv";//
		String ksz="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/ksz_HPO_frequency.csv";//"D:\\transfer\\Datenbank\\ksz_HPO_frequency.csv";//
		String symptom="/home/marie-sophie/Uni/mapra/phenodis/Datenbank/symptoms_HPO_test.csv";//"D:\\transfer\\Datenbank\\symptoms_HPO_test.csv";//
		String phenofile ="/home/marie-sophie/Uni/mapra/omim/phenodis_omimids.txt";//"D:\\transfer\\phenodis_omimids.txt";//
		String outfile="/home/marie-sophie/Uni/mapra/omim/as_weight_p.txt";//"D:\\transfer\\weight_no_pval.txt";//"/home/marie-sophie/Uni/mapra/omim/res_no_weight_no_p.txt";
		String pval="/home/marie-sophie/Uni/mapra/phenodis/pvalues_big_asweight";
		
		MainSophie ms = new MainSophie();
		ms.runOMIMVal(AS_WEIGHT_P_VALUE,outfile, phenofile,file, isa, symptom, ksz, pval);
		
		//String [] pair = new String []{"598","214800"};
		//ms.showResultsFor(pair, WEIGHT_P_VALUE, file, isa, symptom, ksz, pval);
		
//		LinkedList<String[]> res = new LinkedList<String[]>();
//		res.add(new String[]{"1","4","0"});
//		res.add(new String[]{"2","4","0"});
//		res.add(new String[]{"3","4","0.1"});
//		res.add(new String[]{"4","2","0.1"});
//		res.add(new String[]{"5","3","0.2"});
//		res.add(new String[]{"6","1","0.2"});
//		res.add(new String[]{"7","1","0.2"});
//		res.add(new String[]{"8","1","0.2"});
//		
//		System.out.println(ms.calculateRankPval("1", res));
//		System.out.println(ms.calculateRankPval("2", res));
//		System.out.println(ms.calculateRankPval("3", res));
//		System.out.println(ms.calculateRankPval("4", res));
//		System.out.println(ms.calculateRankPval("5", res));
//		System.out.println(ms.calculateRankPval("6", res));
//		System.out.println(ms.calculateRankPval("7", res));
//		System.out.println(ms.calculateRankPval("8", res));
		
//		ms.extractDiseasesWithFreq(phenofile, ksz, outfile);
//		ms.readOMIMPheno(phenofile);
//		HashMap<String, LinkedList<Integer>> queries = ms.readQueriesTM(file);
//		
//		//176000 (OMIM) - 6496 (PhenoDis)
//		System.out.println(queries.get("176000"));
//		
//		int num=1;
//		LinkedList<String[]> res = ms.executePhenomizer(isa, symptom, ksz, queries.get("176000"));
//		for(String []s: res){
//			System.out.println(num+"\t"+s[0]+"\t"+s[1]);
//			num++;
//		}
//		
//		ms.calculateRank("12036", res);
//		ms.calculateRank("6496", res);
//		ms.calculateRank("10890", res);
//		ms.calculateRank("6495", res);
//		ms.calculateRank("8868", res);
//		ms.calculateRank("11763", res);
//		ms.calculateRank("11762", res);
//		ms.calculateRank("11741", res);
//		ms.calculateRank("11396", res);
//		ms.calculateRank("11337", res);
//		ms.calculateRank("9465", res);
//		ms.calculateRank("8916", res);
//		ms.calculateRank("8772", res);
//		ms.calculateRank("2789", res);
//		ms.calculateRank("12044", res);
//		ms.calculateRank("11854", res);
//		ms.calculateRank("11842", res);
//		ms.calculateRank("11547", res);
//		ms.calculateRank("11399", res);
//		ms.calculateRank("11217", res);
//		ms.calculateRank("11160", res);		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private class FileInputReader {
		
		private BufferedReader reader;
		private String path;
		
		public FileInputReader(String path){
			this.path=path;
			Charset c = Charset.forName("UTF-8");
			try{
				reader = Files.newBufferedReader(Paths.get(path),c);
			}
			catch(IOException e){
				System.out.println("Error while creating the reader for file "+path);
				System.exit(1);
			}
		}
		
		public String read(){

				String res;
				try {
					res = reader.readLine();
					return res;
				}
				catch (IOException e) {
					System.out.println("Error while reading from file "+path);
					System.exit(1);
				}

			return "";
		}
		
		public void closer(){
			try{
				reader.close();
			}
			catch(IOException e){
				System.out.println("Error while closing reader for file "+path);
				System.exit(1);
			}
		}


	}
	
	
	public class  Writer_Output {

		public String path;
		public BufferedWriter w;
		
		public Writer_Output (String path){
			this.path=path;
			initiateWriter(path);
		}
		
		public void initiateWriter (String path){
			try{
				Charset c = Charset.forName("UTF-8");
				w = Files.newBufferedWriter(Paths.get(path), c);
			}
			catch(IOException e){
				System.out.println("Error creating the writer for file "+path);
				System.exit(1);
			}
		}
		
		public void writeFile(String s){
			try{
				w.write(s);
				w.flush();
			}
			catch(IOException e){
				System.out.println("Error while writing to file "+path);
				System.exit(1);
			}
		}
		
		public void writeFileln(String s){
			writeFile(s+"\n");
		}
		
		public void closew(){
			try{
				w.flush();
				w.close();
			}
			catch(IOException e){
				System.out.println("Error closing the writer for file "+path);
			}
		}
		
	}


}
