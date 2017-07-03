package DupeDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import weibo4j.Status;
import weibo4j.WeiboException;

public class PairCompare {
	
	protected String outputFileName = null;
	protected String inputDir = null;
	protected String outputDir = null;
	
	private int ngram;
	
	Preprocessor p = new Preprocessor();
	
	PairCompare(String inputDir, String fileName, int ngram)
	{
		this.ngram = ngram;
		
		this.inputDir = inputDir;
		this.outputFileName = fileName;
		
		this.outputDir = "." + File.separator + "result" 
				+ File.separator + outputFileName;
	}
	
	public int compareOneByOne() {

		System.out.println("Start at :" + new Date().toString());

		List<String> content = new ArrayList<String>();
		
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
										content.add(s);
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
		
		int count = 0;
		for(int i = 0; i < content.size(); i++)
		{
			for(int j = i + 1; j < content.size(); j++)
			{
				double sim = computeSimilarity(content.get(i),content.get(j));
				if(sim > 0.8)
				{
					count++;
				}
			}
		}
		
		return count;
	}
	
	public double computeSimilarity(String query, String compared) {
		
		List<String> one = processString(query);
		List<String> two = processString(compared);
		
		int commonCount = common(one, two);
		int unionCount = union(one, two);
		
		return (double)commonCount / (double)unionCount;
	}
 
	private int common(List<String> one, List<String> two) {
		int res = 0;

		for (int i = 0; i < one.size(); i++) {
			for (int j = 0; j < two.size(); j++) {
				if (one.get(i)
						.equals(two.get(j)))
					res++;
			}
		}

		return res;
	}

	private int union(List<String> one, List<String> two) {
		List<String> t = one;

		for (int i = 0; i < two.size(); i++) {
			int pos = -1;
			boolean found = false;
			for (int j = 0; j < t.size() && !found; j++) {
				if (two.get(i)
						.equals(t.get(j))) {
					found = true;
				}
				pos = j;
			}

			if (!found) {
				String r = two.get(i);
				t.add(r);
			}
		}

		return t.size();
	}

	// 分词，Ngrams
	private List<String> processString(String s) {
		s = p.Preprocessing(s);
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

	public static void main(String[] args) {
	
		int ngram = 3;
		String inputDir = "D:\\Paper\\Experiment\\data1";
		String outputFileName = "duplicated.txt";
		
		/*String s0 = "石丹丹Dan";
		String s1 = "dan石丹丹";*/
		
		PairCompare compare = new PairCompare(inputDir, outputFileName, ngram);
		//double sim = compare.computeSimilarity(s0, s1);
		System.out.println(compare.compareOneByOne());
	}
}
