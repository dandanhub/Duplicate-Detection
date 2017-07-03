public class LSH<T> {

	int hashTime;// hash function number = m_numBands*ROWSINBAND;
	int bandsNum; // band number
	int rowsNum; 
	Map<Integer, HashSet<String>> lshBuckets = new HashMap<Integer, HashSet<String>>(); // lsh bucket
	Map<Integer, HashSet<int[]>> lshBucketsForEstimate = new HashMap<Integer, HashSet<int[]>>();
	
	/*int[] minHashArray; 
	int itemIndex;*/

	// first index is Set, second index contains hashValue (so index is hash
	// function)
	public LSH(int hashTimeValue, int rowsNumValue) {
		hashTime = hashTimeValue;
		rowsNum = rowsNumValue;
		bandsNum = hashTime / rowsNum;
		/*minHashArray = minHashArrayValue;
		itemIndex = itemIndexValue;*/
	}
	
	public void LSHProcessing(int[] minHashArray, String id)
	{
		
		//minHashArray.hashCode();
		
		for (int b = 0; b < bandsNum; b++) {
			// combine all 5 MH values and then hash get its hashcode
			// need not be sum
			// 
			int sum = 0;

			for (int i = 0; i < rowsNum; i++) {
				sum += minHashArray[b * rowsNum + i];
				//ls.add(minHashArray[b * rowsNum + i]);
			}

			//sum = ls.hashCode();
			//System.out.println(sum);
			
			if (lshBuckets.containsKey(sum)) // lsh桶
			{
				lshBuckets.get(sum).add(id); // 
			}
			else
			{
				HashSet<String> set = new HashSet<String>();
				set.add(id);
				lshBuckets.put(sum, set);
			}
		}	
	}
	
	public void LSHProcessingForEstimate(int[] minHashArray)
	{
		
		//minHashArray.hashCode();
		
		for (int b = 0; b < bandsNum; b++) {
			// combine all 5 MH values and then hash get its hashcode
			// need not be sum
			// 
			int sum = 0;

			for (int i = 0; i < rowsNum; i++) {
				sum += minHashArray[b * rowsNum + i];
				//ls.add(minHashArray[b * rowsNum + i]);
			}

			//sum = ls.hashCode();
			//System.out.println(sum);
			
			if (lshBucketsForEstimate.containsKey(sum)) // lsh桶
			{
				lshBucketsForEstimate.get(sum).add(minHashArray); // 
			}
			else
			{
				HashSet<int[]> set = new HashSet<int[]>();
				set.add(minHashArray);
				lshBucketsForEstimate.put(sum, set);
			}
		}	
	}
	
	public int arrayLength(int[][] array) {
		int length = 0;

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				length++;
			}
		}
		return length;
	}
	
}
