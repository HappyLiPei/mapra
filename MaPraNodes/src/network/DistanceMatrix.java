package network;

import java.util.HashMap;

import main.FileInputReader;

public class DistanceMatrix {
	
	private static final int MULTIPLICATOR=100;
	private int [][] matrix;
	private HashMap<String, Integer> IdToPos;
	String [] ids;
	
	private DistanceMatrix(String [] header){
		matrix = new int [header.length][header.length];
		IdToPos = new HashMap<String, Integer>(header.length*3);
		for(int i = 0; i<header.length; i++){
			IdToPos.put(header[i],i);
		}
		ids=header;
	}
	
	private DistanceMatrix(){
		matrix=null;
		IdToPos=null;
	}
	
	//TODO:use for buffered data table
	public DistanceMatrix(String [] header, String [] rowIds, int [][] values){
		this(header);
		for(int i=0; i<rowIds.length ;i++){
			for(int j=0; j<header.length; j++){
				set(rowIds[i],j,values[i][j]);
			}
		}
		System.out.println(this);
	}
	
	private void set(int i, int j, int value){
		matrix[i][j]=value;
	}
	
	private void set(String id, int col, int value){
		set(IdToPos.get(id),col,value);		
	}
	
	public double get(int i, int j){
		if(i<matrix.length && j<matrix.length){
			return (double)matrix[i][j]/MULTIPLICATOR;
		}
		else{
			return -1;
		}
	}
	
	public int size(){
		return ids.length;
	}
	
	public String IdAt(int pos){
		if(pos<0 || pos>ids.length-1){
			return "NULL";
		}
		else{
			return ids[pos];
		}
	}
	
	public static DistanceMatrix readDistMatrix(String file){
		
		FileInputReader fir = new FileInputReader(file);
		String line="";
		boolean start=true;
		DistanceMatrix res=new DistanceMatrix();
		
		while((line=fir.read())!=null){
			if(start){
				start = false;
				String [] split= line.split(",");
				String [] header = new String [split.length-1];
				for(int i = 1; i<split.length; i++){
					header[i-1]=split[i];
				}
				res = new DistanceMatrix(header);
			}
			else{
				if(!line.equals("")){
					String [] split = line.split(",");
					for(int i=1; i<split.length; i++){
						int dist = (int) Math.round(Double.valueOf(split[i])*MULTIPLICATOR);
						res.set(split[0], i-1, dist);
					}
				}
			}
		}
		return res;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<matrix.length; i++){
			for(int j=0; j<matrix.length; j++){
				sb.append(matrix[i][j]+"\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
//	public static void main (String args[]){
//		DistanceMatrix m = readDistMatrix("/home/marie-sophie/Uni/mapra/network/allAgainstAll_complete.txt");
//		//SIFWriter.writeSIF("/home/marie-sophie/Uni/mapra/network/comp.sif", m, 4);
//		
//		HashSet<String> hs = new HashSet<String>(20);
//		hs.add("9977");
//		hs.add("9975");
//		hs.add("9976");
//		hs.add("9978");
//		
//		String networkfile ="/home/marie-sophie/Uni/mapra/network/xgmml_comp.xgmml";
//		XGMMLWriter.writeSelectedXGMML(networkfile, m, 4, hs);
//		
//		String outfolder = "/home/marie-sophie/Uni/mapra/network/executor";
//		String script = outfolder+"/script.txt";
//		ScriptWriter.WriteScript(networkfile, script);
//		
//		Executor e = new Executor("/usr/local/Cytoscape_v3.2.1/cytoscape.sh -S "+script, outfolder+"/out.txt", outfolder+"/err.txt");
//		e.runCommand();
//		
//		
//	}

}
