package DupeDetection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessor {

	private static final String regexLinks = "http:\\/\\/[^\\s]*";
	private static final String regexMention = "@[^\\s]*";
	private static final String regexTopic = "#[\\S\\s]*?#";
	private static final String regexBlanks = "\\s{2,}";
	
	//private Map<String, Integer> linkMap = new HashMap<String, Integer>();

	public String Preprocessing(String s) {
		// create pattern object
		Pattern patternLinks = Pattern.compile(regexLinks);
		Pattern patternMention = Pattern.compile(regexMention);
		Pattern patternTopic = Pattern.compile(regexTopic);
		Pattern patternBlanks = Pattern.compile(regexBlanks);
		
		// create mather object
		Matcher matchLinks = patternLinks.matcher(s);
		s = matchLinks.replaceAll("");
		
		Matcher matchMention = patternMention.matcher(s);
		s = matchMention.replaceAll("");
		
		Matcher matchLTopic = patternTopic.matcher(s);
		s = matchLTopic.replaceAll("");
		
		Matcher matchBlanks = patternBlanks.matcher(s);
		s = matchBlanks.replaceAll(" ");
		
		//System.out.println(s);
		/*String link = null;

		// search the input strings
		while (m.find()) {
			// find links which is in group 0
			link = m.group(0);
			
			// check if hasmap already contains the link or not
			if (linkMap.containsKey(link))
				linkMap.put(link, linkMap.get(link) + 1); // set count +1
			else
				linkMap.put(link, 1); // set count 1
		}*/

		return s;
	}

   /* public static void main(String[] args) {
		Preprocessor p = new Preprocessor();

		String s = "OMG，有点出乎我的意料，  #Dandanshi#  原来这是@邱红英oasis   最迷人的部位，还不赶快来看看你身体哪部分最迷人：http://t.cn/zOh0HSs";
		s = p.Preprocessing(s);
		System.out.println(s);
	}*/
}
