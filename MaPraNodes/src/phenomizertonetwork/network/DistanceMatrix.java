package phenomizertonetwork.network;

import java.util.HashMap;

public class DistanceMatrix {
	
	/*
	 * this class can represents a symmetric matrix with diagonal elements = 0
	 * but it can be a similarity or a distance matrix
	 */
	
	//stores the scores (double values) internally as integer values -> rounding to three decimal places
	private static final int MULTIPLICATOR=1000;
	private int [][] matrix;
	// maps PhenoDis id to column = row position of matrix array
	private HashMap<String, Integer> IdToPos;
	// array of all PhenoDis ids
	String [] ids;
	
	/**
	 * constructor creates empty matrix with id information
	 * initializes the array of all ids and assigns a position to each ids in the matrix
	 * @param header: array of all PhenoDis ids to store in this object
	 */
	private DistanceMatrix(String [] header){
		matrix = new int [header.length][header.length];
		IdToPos = new HashMap<String, Integer>(header.length*3);
		for(int i = 0; i<header.length; i++){
			IdToPos.put(header[i],i);
		}
		ids=header;
	}
	
	/**
	 * constructor to create a distance matrix from a buffered data table (all against all of PhenoDis diseases)
	 * @param header: column names at corresponding positions -> contains PhenoDis ids
	 * @param rowIds: row ids of all rows -> contains PhenDis ids but may be ordered differently than in header 
	 * @param values: array of all score values from table
	 * 					values [i][j]: similarity/ distance score between diseases header[i] and rowIds[j]
	 */
	public DistanceMatrix(String [] header, String [] rowIds, double [][] values){
		this(header);
		for(int i=0; i<rowIds.length ;i++){
			for(int j=0; j<header.length; j++){
				int entry = (int) Math.round(values[i][j]*MULTIPLICATOR);
				set(rowIds[i],j,entry);
			}
		}
	}
	
	/**
	 * method to fill empty distance matrix with initialized PhenoDis ids
	 * @param i: row index
	 * @param j: column index
	 * @param value: value to place at matrix[i][j]
	 */
	private void set(int i, int j, int value){
		matrix[i][j]=value;
	}
	
	/**
	 * method to fill empty distance matrix with initialized PhenoDis ids from a BufferedDataTable
	 * @param id: PhenoDis id read from a rowId
	 * @param col: column position 
	 * @param value: value read from row with id id and cell at position of column col
	 */
	private void set(String id, int col, int value){
		set(IdToPos.get(id),col,value);		
	}
	
	/**
	 * retrieve matrix value at row i and column j
	 * @param i: row index
	 * @param j: column index
	 * @return: matrix[i][j] as double rounded to three decimal places
	 */
	public double get(int i, int j){
		if(i<matrix.length && j<matrix.length){
			return (double)matrix[i][j]/MULTIPLICATOR;
		}
		else{
			return -1;
		}
	}
	
	/**
	 * Get size of the matrix
	 * @return: number of diseases stored in this data structure
	 */
	public int size(){
		return ids.length;
	}
	
	/**
	 * Get PhenoDis id corresponding to a matrix position
	 * @param pos: position of column= position of row within the matrix
	 * @return: PhenoDis id for which scores are stored at column pos and row pos
	 */
	public String IdAt(int pos){
		if(pos<0 || pos>ids.length-1){
			return "NULL";
		}
		else{
			return ids[pos];
		}
	}
	
	/**
	 * String representation of the matrix, displaying the integer values stored internally
	 */
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

}
