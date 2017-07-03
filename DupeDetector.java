package DupeDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Analysis.FailedSinceId;
import Experiment.CollectUserInfo;

import weibo4j.Status;
import weibo4j.WeiboException;

public class DupeDetector {

	protected String outputFileName = null;
	protected String inputDir = null;
	protected String outputDir = null;

	//SimpleDateFormat inputFormat = null;

	int hashTime;
	int ngram;
	int bandsNum;
	int rowsNum;

	MinHash minhash = null;
	LSH lsh = null;
	Preprocessor p = null;
	
	Map<Long, Integer> userDuplicteRecord = new HashMap<Long, Integer>();
	Map<Long, Integer> userWeiboRecord = new HashMap<Long, Integer>();
	
	public DupeDetector(String inputDir, String fileName, int hashTime, int ngram, int bandsNum) {
		this.inputDir = inputDir;
		this.outputFileName = fileName;
		this.hashTime = hashTime;
		this.ngram = ngram;
		this.bandsNum = bandsNum;
		this.rowsNum = hashTime/bandsNum;
	}

	public void run() {
		init();
		genLSHBucket();
		//dupeDetectionByUid();
		//outputDuplicateRate();
	}

	public double estimate() {
		init();
		genLSHBucketForEstimate();
		
		return estimatePrecision();
	}
	
	protected void init() {
		//inputFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		this.outputDir = "." + File.separator + "data" + File.separator
				+ File.separator + outputFileName;
		this.minhash = new MinHash(hashTime, ngram);
		this.lsh = new LSH(hashTime, rowsNum);
		Preprocessor p = new Preprocessor();
		
		
	}

	public void genLSHBucket() {

		System.out.println("Start at :" + new Date().toString());

		File filedir = new File(inputDir);
		String[] fileLevel1List = filedir.list();
		System.out.println(inputDir);

		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;

		String line = null;

		//Set<String> uidSet = new HashSet<String>(); // 保存用戶ID
		List<Status> statusList = new ArrayList<Status>();

		//String uid = null;

		int index = 0;

		try {
			for (String fileLevel1 : fileLevel1List) {
				File f1 = new File(inputDir + File.separator + fileLevel1);
				// date 2011_06_27....
				String[] fileLevel2List = f1.list();
				// System.out.println(fileLevel1);

				for (String fileLevel2 : fileLevel2List) {

					if (!fileLevel2.contains("_status")) {
						continue;
					}

					int line_id = 0;

					int temp = fileLevel2.indexOf('_');
					String file_id = fileLevel2.substring(0, temp);

					File f2 = new File(inputDir + File.separator + fileLevel1
							+ File.separator + fileLevel2);

					// System.out.println(fileLevel2);
					fis = new FileInputStream(inputDir + File.separator
							+ fileLevel1 + File.separator + fileLevel2);
					isr = new InputStreamReader(fis, "GBK");
					br = new BufferedReader(isr);

					while ((line = br.readLine()) != null) {
						if (!line.startsWith("[")) {
							continue;
						}

						line_id++;

						try {
							
							statusList = Status.constructStatuses(line);
							int item_id = 0;
							// System.out.println(statusList.size());
							for (Status status : statusList) {
								item_id++;

								if (status.isRetweet() == false)
								{
									String s = status.getText();
									
									if(!s.startsWith("分享图片") 
										&&!s.startsWith("share Image") && !s.startsWith("转发微博"))
									{
										//String id = file_id + "_" + line_id + "_" + item_id;	
										Long uid = status.getUser().getId();
										//String id = String.valueOf(uid) +"_" + status.getId();
										//String id = String.valueOf(status.getUser().getId()) +"_" + status.getId() + 
										//		 "_" + file_id + "_" + line_id + "_" + item_id ;
										String id = s;
										if (userWeiboRecord.containsKey(uid))
										{
											userWeiboRecord.put(uid, userWeiboRecord.get(uid) + 1); 
										}
										else
										{
											userWeiboRecord.put(uid, 1);
										}
										
										int[] minHashArray = minhash
												.computeMinHashForItem(s);
										lsh.LSHProcessing(minHashArray, id);
										
										index++;
									}
								}
							}
						} catch (WeiboException e) {
							System.out.println(e.getMessage());
						}
					}

					br.close();
					isr.close();
					fis.close();
				} // end of fileLevel2
			} // end of fileLevel1
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		System.out.println(index);
	}
	
	public void genLSHBucketForEstimate() {

		System.out.println("Start at :" + new Date().toString());

		File filedir = new File(inputDir);
		String[] fileLevel1List = filedir.list();
		System.out.println(inputDir);

		FileInputStream fis;
		InputStreamReader isr;
		BufferedReader br;

		String line = null;

		//Set<String> uidSet = new HashSet<String>(); // 保存用戶ID
		List<Status> statusList = new ArrayList<Status>();

		//String uid = null;

		int index = 0;

		try {
			for (String fileLevel1 : fileLevel1List) {
				File f1 = new File(inputDir + File.separator + fileLevel1);
				// date 2011_06_27....
				String[] fileLevel2List = f1.list();
				// System.out.println(fileLevel1);

				for (String fileLevel2 : fileLevel2List) {

					if (!fileLevel2.contains("_status")) {
						continue;
					}

					//int line_id = 0;

					int temp = fileLevel2.indexOf('_');
					//String file_id = fileLevel2.substring(0, temp);

					File f2 = new File(inputDir + File.separator + fileLevel1
							+ File.separator + fileLevel2);

					// System.out.println(fileLevel2);
					fis = new FileInputStream(inputDir + File.separator
							+ fileLevel1 + File.separator + fileLevel2);
					isr = new InputStreamReader(fis, "GBK");
					br = new BufferedReader(isr);

					while ((line = br.readLine()) != null) {
						if (!line.startsWith("[")) {
							continue;
						}

						//line_id++;

						try {
							
							statusList = Status.constructStatuses(line);
							//int item_id = 0;
							// System.out.println(statusList.size());
							for (Status status : statusList) {
								//item_id++;

								if (status.isRetweet() == false)
								{
									String s = status.getText();
									
									if(!s.startsWith("分享图片") 
										&&!s.startsWith("share Image") && !s.startsWith("转发微博"))
									{
										//String id = file_id + "_" + line_id + "_" + item_id;	
										Long uid = status.getUser().getId();
										//String id = String.valueOf(uid) +"_" + status.getId();
										//String id = String.valueOf(status.getUser().getId()) +"_" + status.getId() + 
										//		 "_" + file_id + "_" + line_id + "_" + item_id ;
									    /*	
										if (userWeiboRecord.containsKey(uid))
										{
											userWeiboRecord.put(uid, userWeiboRecord.get(uid) + 1); 
										}
										else
										{
											userWeiboRecord.put(uid, 1);
										}*/
										s = p.Preprocessing(s);
										if(s.length() >= this.ngram)
										{
											int[] minHashArray = minhash
													.computeMinHashForItem(s);
											lsh.LSHProcessingForEstimate(minHashArray);
											
											index++;
										}
									}
								}
							}
						} catch (WeiboException e) {
							System.out.println(e.getMessage());
						}
					}

					br.close();
					isr.close();
					fis.close();
				} // end of fileLevel2
			} // end of fileLevel1
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		System.out.println("微博条数: " + index);
	}

	public void dupeDetectionByUid()
	{
		Set<Long> set = new HashSet<Long>();
		
		Iterator it = lsh.lshBuckets.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			
			HashSet<String> bucket = (HashSet<String>)entry.getValue();
			
			if(bucket.size() > 1)
			{
				for(String s : bucket)
				{
					String[] splits = s.split("_");
					
					if(splits.length >= 2)
					{
						Long uid = Long.parseLong(splits[0]);
						
						if(!set.contains(Long.parseLong(splits[1])))
						{
							set.add(Long.parseLong(splits[1]));
							if (userDuplicteRecord.containsKey(uid)) 
							{
								userDuplicteRecord.put(uid, userDuplicteRecord.get(uid) + 1); 
							}
							else
							{
								userDuplicteRecord.put(uid, 1);
							}
						}
						
					}
				}
			}
		}	
		
	}

	public void outputDuplicateRate()
	{
		//double[] duplicateRate;
		
		Iterator it = this.userDuplicteRecord.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			
			Long uid = (Long)entry.getKey();
			int weibo = this.userWeiboRecord.get(uid);
			int duplicated = (Integer)entry.getValue();
			
			double sim = (double)duplicated/(double)weibo;		
		
			if(sim > 0.5)
			{
				System.out.println("User id " + uid + " duplicated rate: " + sim);
			}
		}
	}
	
	public void outputCollisionItems()
	{
		//double[] duplicateRate;
		
		Iterator it = lsh.lshBuckets.entrySet().iterator();
		
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			
			HashSet<String> bucket = (HashSet<String>)entry.getValue();
			
			if(bucket.size() > 1)
			{
				for(String s : bucket)
				{
					System.out.println(entry.getKey() + " " + s);
					
				}
			}
		}
	}
	
	public double estimatePrecision()
	{	
		Iterator it = lsh.lshBucketsForEstimate.entrySet().iterator();
		
		double precision = 0.0;
		double threshold = 0.8;
		
		
		int collisionBucket = 0;
		
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			
			HashSet<int[]> bucket = (HashSet<int[]>)entry.getValue();			
			
			if(bucket.size() > 1)
			{
				collisionBucket++;

				int similarCount = 0;
				int duplicatedCount = 0;
				
				Object[] bucketItems = bucket.toArray();
				
				int[][] minHashValues = new int[bucketItems.length][this.hashTime];
				for(int index = 0; index < bucketItems.length; index++)
				{
					minHashValues[index] = (int[])bucketItems[index];
				}
				
				for(int i = 0; i < bucket.size(); i++)
				{
					for(int j = i; j < bucket.size(); j++)
					{
						similarCount++;
						double sim = this.minhash.computeSimilarity(minHashValues[i], minHashValues[j]);
						if(sim >= threshold)
						{
							duplicatedCount++;
						}
					}
				}		
							
				precision += (double)duplicatedCount/(double)similarCount;
			}
		}
		
		System.out.println("冲突桶个数: " + collisionBucket);
		precision = precision/(double)collisionBucket;
		
		return precision;
	}
	
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis();
		
		String inputDir = "D:\\Paper\\Experiment\\data1";
		String outputFileName = "duplicated.txt";
		int hashTime = 200;
		int ngram = 3;
		int bandsNum = 5;

		DupeDetector dupeDetector = new DupeDetector(inputDir, outputFileName, hashTime, ngram, bandsNum);
		//System.out.println("Precision: " + dupeDetector.estimate());
		dupeDetector.run();
		dupeDetector.outputCollisionItems();
		
		//Preprocessor p = new Preprocessor();
		//String query = "OMG，有点出乎我的意料，原来这是@邱红英oasis  最迷人的部位，还不赶快来看看你身体哪部分最迷人：http://t.cn/zOh0HSs";
		//query = p.Preprocessing(query);		
		//Set<Weibo> similarSet = dupeDetector.dupeDetection(query);
		//System.out.println("Duplicated set size is " + similarSet.size());

		System.out.println("LSH bucket size: " + dupeDetector.lsh.lshBuckets.size());
		int a = 0;
		Iterator interger = dupeDetector.lsh.lshBuckets.entrySet().iterator();
		while(interger.hasNext())
		{
			Map.Entry entry = (Map.Entry)interger.next();
			HashSet<int[]> set = (HashSet<int[]>)entry.getValue();
			if(set.size() > 1)
			{
				/*System.out.println("Bucket Id: " + entry.getKey().toString());			
				System.out.println("Bucket Size: " + set.size());	*/		
				a++;
			}
		}
		
		System.out.println("LSH collision bucket size: "+ a);
		Iterator it = dupeDetector.userDuplicteRecord.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			System.out.println("User Id: " + entry.getKey().toString());
			System.out.println("重复微博数: : " + entry.getValue().toString());
		}
		/*File filedir = new File(dupeDetector.inputDir);
		System.out.println(dupeDetector.findFileName(dupeDetector.inputDir,"1002998647_status.txt"));*/
		/*int i = 0;
		
		while(iterator.hasNext()) {
			Weibo weibo = (Weibo)iterator.next();
			System.out.println("重复微博为：" + weibo.text); 
			System.out.println("发表时间：" + weibo.createAt);
			}*/

		long end = System.currentTimeMillis();
		System.out.println(end-start);
	}
}
