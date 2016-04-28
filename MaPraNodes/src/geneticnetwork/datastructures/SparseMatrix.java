package geneticnetwork.datastructures;

public class SparseMatrix {
	
	private double [] entries;
	private int [] rows;
	private int [] columns;
	//position of the element of entries, rows and columns that is free
	private int fillPointer;
	private int rowCount;
	private int colCount;
	
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
	
	public void addEntry(int row, int column, double value){
		if(fillPointer<entries.length && row >=0 && column >=0 && row <rowCount && column <colCount){
			entries[fillPointer]=value;
			rows[fillPointer]=row;
			columns[fillPointer]=column;
			fillPointer++;
		}
	}
	
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
	
	//DO NOT call on big matrix !!!!!
	public double[][] getData(){
		double [][] data = new double [rowCount][colCount];
		for(int i=0; i<fillPointer; i++){
			data[rows[i]][columns[i]]=entries[i];
		}
		return data;
	}
		


}
