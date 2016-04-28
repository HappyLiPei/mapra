package geneticnetwork.datastructures;

/** vector representing a column vector */
public class Vector {
	
	/** array storing all entries of the vector, array-representation of the vector*/
	private double[] vector_entries;
	
	/**
	 * generates an vector with all entries 0
	 * @param length length of the vector
	 */
	public Vector(int length){
		if(length>0){
			vector_entries = new double[length];
		}
	}
	
	/**
	 * generates a vector from a given array of numbers
	 * @param data array of numbers
	 */
	public Vector(double [] data){
		vector_entries = data;
	}
	
	/**
	 * modifies an entry of the vector at a defined position
	 * @param position position of the entry
	 * @param entryValue value that should be placed at position position
	 */
	public void setEntry(int position, double entryValue){
		if(position>=0 && position < vector_entries.length){
			vector_entries[position]=entryValue;
		}
	}
	
	/**
	 * retrieves the entry of the vector at a defined position
	 * @param position position of the entry
	 * @return values of the entry at position position
	 */
	public double getEntry(int position){
		if(position>=0 && position < vector_entries.length){
			return vector_entries[position];
		}
		else{
			return Double.NaN;
		}
	}
	
	/**
	 * retrieves the length of the vector
	 * @return number of entries in the vector
	 */
	public int getLength(){
		return vector_entries.length;
	}
	
	/**
	 * retrieves a copy of the data stored in this vector
	 * @return array representation of the data stored in this object
	 */
	public double [] getData(){
		double [] copyOfData = new double[vector_entries.length];
		for(int i=0; i<vector_entries.length; i++){
			copyOfData[i]=vector_entries[i];
		}
		return copyOfData;
	}
	
	/**
	 * performs the multiplication scalar * this and stores the result in a new vector object,
	 * this vector remains unchanged
	 * @param scalar scalar that is multiplied
	 * @return vector with the result scalar*this
	 */
	public Vector multiplyScalar(double scalar){
		double[] vector_new = new double [vector_entries.length];
		for(int i=0; i<vector_entries.length; i++){
			vector_new[i] = scalar*vector_entries[i];
		}			
		return new Vector(vector_new);
	}
	
	/**
	 * multiplies this vector with a scalar, the result of the multiplication is directly stored in this object
	 * @param scalar double for multiplication: scalar*this
	 */
	public void multiplyScalarInPlace(double scalar){
		for(int i=0; i<vector_entries.length; i++){
			vector_entries[i]=vector_entries[i]*scalar;
		}
	}
	
	/**
	 * performs addition of this vector and the vector v, both vectors remain unchanged
	 * @param v vector that is added to this
	 * @return a new vector object with new vector = this+v
	 */
	public Vector addVector(Vector v){
		if(v.getLength()==vector_entries.length){
			double [] vector_new = new double [vector_entries.length];
			for(int i=0; i<vector_new.length; i++){
				vector_new[i]=v.getEntry(i)+vector_entries[i];
			}
			return new Vector(vector_new);
		}
		else{
			return null;
		}
	}
	
	/**
	 * adds a vector to this object, the operation modifies this vector itself, but vector v of the argument
	 * remains unchanged
	 * @param v vector that is added to this
	 */
	public void addVectorInPlace(Vector v){
		if(v.getLength()==vector_entries.length){
			for(int i=0; i<vector_entries.length; i++){
				vector_entries[i]+=v.getEntry(i);
			}
		}
	}
}
