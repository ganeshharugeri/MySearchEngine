package com.searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageFormatStoring {

	public static String IMG = "immmmggggg";
	public static int imgid = 0;

	public static void Image_retrieval(String hostURL, int docid)
			throws IOException, ClassNotFoundException, SQLException {
		
//		public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException
//		{
//         int docid =4;
		Storetables st = new Storetables();
		//String hostURL = "http://www.uni-kl.de/startseite/";
		URL url1 = null;

		try {
			url1 = new URL(hostURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		String hoststring = "";
		String inputline = "";
		String contents = "";
		// System.out.println("Host URL: "+hostURL);
		// System.out.println("Indicate "+indicate);
		hoststring = hostURL.substring(0, hostURL.indexOf("de") + 2);
		
//		if (url1.getHost().contains("www"))
//			hoststring = "http://" + url1.getHost();
//		else
//			hoststring = "http://www." + url1.getHost();
//		

//		 System.out.println("HostString "+hoststring);

		// Buffered object to stream through the URL content
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(url1.openStream()));
			while ((inputline = br.readLine()) != null) {
				contents = contents + inputline;
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Store the images in document tables

		Pattern p = Pattern.compile("<img.*?src=\"(.*?)\""); // gets contents
		Matcher m = p.matcher(contents);

		String img = "";
		String s3 = "";

		ArrayList<String> image_source = new ArrayList<String>();

		while (m.find()) {
			img = m.group(1); // Gets the image source either broken or direct
			// URLr
			if (img.contains("http") || img.contains("https")) {
				{
					if (hoststring.endsWith("/"))
						image_source.add(img);

				}
			} else {
				s3 = hoststring + "/" + img;
				image_source.add(s3);
			}
		}
		// System.out.println(image_source);

		// Parsing and removing the URL HTML content information orderly
		contents = contents.replaceAll("<script.*?</script>", " ")
				.replaceAll("<style.*?</style>", " ")
				.replaceAll("(?i)<(?!img|/img).*?>", " ")
				.replaceAll("^(<img.+?src=[\"'](.+?)[\"'].*?>)", " ")
				.replaceAll("&.*?;", " ").replaceAll("\\s+", " ").trim();

		ArrayList<String> alt = new ArrayList<String>();
		Pattern p1 = Pattern.compile("<img.+?src=.*?(alt=\"(.*?)\".*?)/>");
		Matcher m1 = p1.matcher(contents);
		while (m1.find()) {
			String altinfo = m1.group(2);
			alt.add(altinfo);
		}

		// System.out.println(contents);

		String contents1 = contents.replaceAll("<img.+?src=.*?>", IMG)
				.replaceAll("&.*?;", " ").replaceAll("[^a-zA-Z]", " ")
				.replaceAll("\\s+", " ").toLowerCase().trim();

		ArrayList<String> contentList = new ArrayList<String>(
				Arrays.asList(contents1.split(" ")));

		// Store in Image Feature table and Bytearray in Images table

		Map<String, String> map = GetFormattedData(contentList, image_source);

		Iterator<String> iterator = map.keySet().iterator();

		imgid = st.max_imgid();

		while (iterator.hasNext()) {

			imgid++;

			String url = iterator.next().toString();
			String words = map.get(url);

			String[] leftwords = words.split("<>")[0].split(" ");
			String[] rightwords = words.split("<>")[1].split(" ");
			// String[] wordscore="";
			// System.out.println("LEFT WORDS");
			// extract left word and scores
			for (String leftword : leftwords) {
				String[] wordscore = leftword.split("=");
				if (wordscore.length == 2) {
					String wordleft = wordscore[0];
					String score = wordscore[1];
					// Store to database
					if(wordleft.length()!=1)
					st.store_to_imagefeatures(docid, imgid, wordleft, score);

				}
			}

			// System.out.println("RIGHT WORDS");
			// extract right words and scores
			for (String rightword : rightwords) {
				String[] wordscore = rightword.split("=");

				if (wordscore.length == 2) {
					String wordright = wordscore[0];
					String score = wordscore[1];
					// Store to database
					if(wordright.length()!=1)
					st.store_to_imagefeatures(docid, imgid, wordright, score);
					// System.out.println(word + " " + score);

				}
			}



			st.store_to_images(imgid, url);
			//System.out.println("Done");

		}

	}

	public static Map<String, String> GetFormattedData(
			ArrayList<String> contentList, ArrayList<String> image_source) {
		final int LEN = 10;
		Map<String, String> map = new HashMap<String, String>();

		String[] left = new String[LEN];
		String[] right = new String[LEN];
		int leftIndex = 0;
		int rightIndex = 0;
		int urlIndex = 0;

		boolean seenImgTag = false;
		boolean leftWord = true;
		boolean Done = false;

		for (String str : contentList) {
			if (str.equalsIgnoreCase(IMG)) {
				leftWord = false;
				// back to back IMG tag. No right words.
				// flag done processing
				if (seenImgTag) {
					Done = true;
				}
				seenImgTag = !seenImgTag;
				continue;
			}

			seenImgTag = false;

			// check if done processing of IMG tag
			// then we have words in left and right
			if (Done) {
				String leftStr = "";
				for (String l : left) {
					if (l == null)
						break;
					leftStr += l + " ";
				}

				leftStr += "<> ";
				String rightStr = "";
				for (String l : right) {
					if (l == null)
						break;
					rightStr += l + " ";
				}

				map.put(image_source.get(urlIndex++), leftStr + rightStr);

				left = right;
				leftIndex = rightIndex;
				leftWord = true;

				right = new String[LEN];
				rightIndex = 0;
				// reset Done for next iteration
				Done = false;
			}

			if (leftWord) {
				// circular queue of 10.
				if (leftIndex >= LEN) {
					// more words on left side, wrap around
					// capture words closer to IMG tag
					leftIndex = 0;
				}

				// left word = score
				left[leftIndex++] = str + "=" + (float) leftIndex / 100;
			} else {
				// IMG tag not seen so collect words until
				// buffer full.
				if (!Done) {
					if (rightIndex >= LEN) {
						rightIndex = 0;
						Done = true;
					} else {
						// right word = score
						right[rightIndex++] = str + "="
								+ (float) (LEN - rightIndex + 1) / 100;
					}
				}
			}
		}

		return map;
	}
}
