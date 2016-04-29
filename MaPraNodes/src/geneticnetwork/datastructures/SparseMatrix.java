package geneticnetwork.datastructures;

/** implementation of a sparse matrix structure that allows fast matrix - vector multiplication
 * but the entries of the matrix cannot be changed or queries separately*/
public class SparseMatrix {
	
	/** elements of the matrix in the same order as rows and columns*/
	private double [] entries;
	/** rows of the elements of the array entries -> same order as entries*/
	private int [] rows;
	/** columns of the elements of the array entries -> same order as entries*/
	private int [] columns;
	//position of the element of entries, rows and columns that is free
	/** counter indicating the first free position in the entries, rows and columns array*/
	private int fillPointer;
	/** total number of rows*/
	private int rowCount;
	/** total number of columns*/
	private int colCount;
	
	/**
	 * generates an empty spares matrix (all entries 0) with a fixed number of rows, columns and non-zero entries
	 * @param rowCount number of rows of the matrix
	 * @param colCount number of columns of the matrix
	 * @param nonZeroEntries number of non-zero entries of the matrix
	 */
	public SparseMatrix (int rowCount, int colCount, int nonZeroEntries){
		if(rowCount>0 && colCount > 0 && nonZeroEntries>0){
			this.rowCount = rowCount;
			this.colCount = colCount;
			fillPointer=0;
			entries = new double [nonZeroEntries];
			rows = new int[nonZeroEntries];
			columns = new int[nonZeroEntries];
		}
	}
	
	/**
	 * adds a non-zero entry to the matrix, this is only possible if there are free entries available in the data structure
	 * (pre-defined number of non-zero entries)
	 * @param row 0-based row index (has to be positive and smaller than the total number of rows)
	 * @param column 0-based column index (has to be positive and smaller than the total number of columns)
	 * @param value value at row row and column column that is added to the matrix entries
	 */
	public void addEntry(int row, int column, double value){
		if(fillPointer<entries.length && row >=0 && column >=0 && row <rowCount && column <colCount){
			entries[fillPointer]=value;
			rows[fillPointer]=row;
			columns[fillPointer]=column;
			fillPointer++;
		}
	}
	
	/**
	 * multiplies a vector with this object without changing the input vector vector
	 * @param vector vector that is multiplied
	 * @return a new vector = this*vector
	 */
	public Vector multiply(Vector vector){
		if(vector.getLength()!=colCount){
			return null;
		}
		else{
			double [] result = new double [rowCount];
			for(int i=0; i<fillPointer; i++){
				result[rows[i]]+=entries[i]*vector.getEntry(columns[i]);
			}
			return new Vector(result);
		}
	}
	
	/**
	 * gets a traditional array representation of the matrix stored in this object,
	 * DO NOT CALL THIS FUNCTION ON BIG MATRICES !!!
	 * @return array representing the matrix (including zero entries)
	 */
	public double[][] getData(){
		double [][] data = new double [rowCount][colCount];
		for(int i=0; i<fillPointer; i++){
			data[rows[i]][columns[i]]=entries[i];
		}
		return data;
	}
		


}
