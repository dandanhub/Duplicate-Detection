public class MinHash {

	private int hashTime;
	private int ngram;
	int[] M_MASK;

	public MinHash(int hashTimeValue, int ngramValue) {
		this.hashTime = hashTimeValue;
		this.ngram = ngramValue;
		genHashMASK();
	}

	private void genHashMASK() {
		int bound = 10000000;
		M_MASK = new int[hashTime];
		boolean[] flag = new boolean[bound + 1];
		for(int i = 0; i < bound + 1; i++){
			flag[i] = false;
		}
		int i = 0;
		while(i < this.hashTime) {
			int a = new Random().nextInt(bound);
			if(!flag[a]) {
				M_MASK[i] = a;
				flag[a] = true;
				i++;
			}
		}
	}

	private void initializeMinHashArray(int[] minHashArray) {
		for (int i = 0; i < hashTime; i++) {
			minHashArray[i] = Integer.MAX_VALUE;
		}
	}

	public double computeSimilarity(int[] minHashQuery, int[] minHashCompared) {
		int identicalMinHashes = 0;
		for (int i = 0; i < hashTime; i++) {
			if (minHashQuery[i] == minHashCompared[i]) {
				identicalMinHashes++;
			}
		}
		return (1.0 * identicalMinHashes) / hashTime;
	}

	public int[][] computeMinHashMatrix(HashSet<String> set) {
		int[][] minHashMatrix = new int[set.size()][this.hashTime];
		int i = 0;
		for (String s : set) {
			minHashMatrix[i] = computeMinHashForItem(s);
			i++;
		}
		return minHashMatrix;
	}

	private List<String> processString(String s) {
		List<String> ngrams = new ArrayList<String>();
		for (int i = 0; i < s.length(); i++) {
			if (i <= (s.length() - ngram)) {
				if (ngrams.contains(s.substring(i, ngram + i)) == false) {
					ngrams.add(s.substring(i, ngram + i));
				}
			}
		}
		return ngrams;
	}

	public int[] computeMinHashForItem(String s) {
		List<String> ls = processString(s);
		int[] minHashArray = new int[hashTime];
		for (int i = 0; i < hashTime; i++) {
			minHashArray[i] = Integer.MAX_VALUE;
		}
		for (int i = 0; i < hashTime; i++) {
			for (int j = 0; j < ls.size(); j++) {
				int hashValue = oneByOneHash(ls.get(j), M_MASK[i]); // get the hash
				if (hashValue < minHashArray[i]) {
					// if current hash is smaller than the existing hash in the slot then replace with the smaller hash value
					minHashArray[i] = hashValue;
				}
			}
		}
		return minHashArray;
	}

	public static int oneByOneHash(String key, int M_MASK) {
		int hash, i;
		for (hash = 0, i = 0; i < key.length(); ++i) {
			hash += key.charAt(i);
			hash += (hash << 10);
			hash ^= (hash >> 6);
		}
		hash += (hash << 3);
		hash ^= (hash >> 11);
		hash += (hash << 15);
		return (hash & M_MASK);
	}
}