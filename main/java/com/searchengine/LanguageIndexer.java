package com.searchengine;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.*;
import java.sql.Connection;
import java.sql.SQLException;
 
public class LanguageIndexer 
 {
 
	String[] englishStopwordsArray = {"it", "every", "least", "less",
			"many", "now", "ever", "never", "say", "says", "said", "also",
			"get", "go", "goes", "just", "made", "make", "put", "see", "seen",
			"whether", "like", "well", "back", "even", "still", "way", "take",
			"since", "another", "however", "two", "three", "four", "five",
			"first", "second", "new", "old", "high", "long", "again",
			"further", "then", "once", "here", "there", "when", "where", "why",
			"how", "all", "any", "both", "each", "few", "more", "most",
			"other", "some", "such", "no", "nor", "not", "only", "own", "same",
			"so", "than", "too", "very", "and", "but", "if", "or", "because",
			"as", "until", "while", "of", "at", "by", "for", "with", "about",
			"against", "between", "into", "through", "during", "before",
			"after", "above", "below", "to", "from", "up", "down", "in", "out",
			"on", "off", "over", "under", "a", "an", "the", "oughtn't",
			"mightn't", "daren't", "needn't", "let's", "that's", "who's",
			"what's", "here's", "there's", "when's", "where's", "why's",
			"how's", "won't", "wouldn't", "shan't", "shouldn't", "can't",
			"cannot", "couldn't", "mustn't", "isn't", "aren't", "wasn't",
			"weren't", "hasn't", "haven't", "hadn't", "doesn't", "don't",
			"didn't", "i'm", "you're", "he's", "she's", "it's", "we're",
			"they're", "i've", "you've", "we've", "they've", "i'd", "you'd",
			"he'd", "she'd", "we'd", "they'd", "i'll", "you'll", "he'll",
			"she'll", "we'll", "they'll", "ought", "must", "may", "might",
			"could", "can", "should", "shall", "will", "would", "do", "does",
			"did", "doing", "have", "has", "had", "having", "am", "is", "are",
			"was", "were", "be", "been", "being", "what", "which", "who",
			"whom", "this", "that", "these", "those", "they", "them", "their",
			"theirs", "themselves", "i", "me", "my", "the", "myself", "we",
			"us", "usually", "our", "ours", "ourselves", "you", "your",
			"yours", "yourself", "yourselves", "he", "him", "his", "himself",
			"she", "her", "hers", "herself", "one", "its", "itself" };

	 String[] germanStopwordsArray = {"aber", "als", "am", "an", "auch", "auf",
			 "aus", "bei", "bin", "bis", "bist", "da", "dadurch", "daher", "darum",
			 "das", "dass", "dein", "deine", "dem", "den", "der", "des", "dessen",
             "deshalb", "die", "dies", "dieser", "dieses", "doch", "dort", "du", 
             "durch", "ein", "eine", "einem", "einen", "einer", "eines", "er", "es", 
             "euer", "eure", "für", "hatte", "hatten", "hattest", "hattet", "hier", 
              "hinter", "ich", "ihr", "ihre", "im","in", "ist", "ja", "jede", "jedem", 
              "jeden", "jeder", "jedes", "jener", "jenes", "jetzt", "kann", "kannst", 
              "können", "könnt", "machen", "mein", "meine", "mit", "muß", "mußt", "musst",
              "müssen", "müsst", "nach", "nachdem", "nein", "nicht", "nun",
             "oder", "seid", "sein", "seine", "sich", "sie", "sind", "soll", "sollen",
             "sollst", "sollt", "sonst", "soweit", "sowie", "und", "unser", "unsere", "unter",
             "vom", "von", "vor", "wann", "warum", "was", "weiter", "weitere", "wenn", "wer", "werde",
             "werden", "werdet", "weshalb", "wie", "wieder", "wieso", "wir", "wird", "wirst", "wo",
             "woher", "wohin", "zu", "zum", "zur", "über"};
    
	
	// Function to Parse URL Content
public boolean parse(String primURL, int docid) throws IOException, ClassNotFoundException, SQLException 
{
//	ImageSource img_src= new ImageSource();	
//	String image_source=img_src.image_source_retrieval(primURL,docid);
	   
	
	URL url1 = new URL(primURL);
		//Buffered object to stream through the URL content 
		BufferedReader br = new BufferedReader(new InputStreamReader(url1.openStream()));
		String inputline = "";
     	String contents = "";
		String imagecontent="";
		ArrayList<String> image_source = new ArrayList<String>();
		

		
        // Reading the URL contents and storing in contents
		int num=1234;
		String uniquenum="#uniq_id"+ num;
		while ((inputline = br.readLine()) != null) {
			contents = contents + inputline;
			contents.replaceAll("<img src=.*?/>", uniquenum);
            num++;
		}
		
		br.close();
		//Parsing and removing the URL HTML content information orderly 
		
	
		

		
		contents = contents.replaceAll("<script.*?</script>", " ")
				.replaceAll("<style.*?</style>", " ").replaceAll("<.*?>"," ")
				.replaceAll("[^a-zA-Z]", " ").replaceAll("\\s+", " ")
				.toLowerCase().trim();
		
	//		contents = image_source.replaceAll("immmmggggg"," ").toLowerCase().trim();
	
	//	System.out.println("The contents of URL:" + contents);
		

		
		HashMap<String, Integer> englishStopwords = new HashMap<String, Integer>();
		//Storing EnglishStopwords in HashMap
	    for (int i = 0; i < englishStopwordsArray.length; i++) 
		{
			englishStopwords.put(englishStopwordsArray[i], 1);
		}

		HashMap<String, Integer> germanStopwords = new HashMap<String, Integer>();
		//Storing EnglishStopwords in HashMap
	    for (int i = 0; i < germanStopwordsArray.length; i++) 
		{
	    	germanStopwords.put(germanStopwordsArray[i], 1);
		}
		LanguageIndexer call = new LanguageIndexer();
	     String language="";
		if(call.LanguageDetection(contents,englishStopwords))
		   language="english";
		else
			language="german";
		
		System.out.println("Language chosen :" + language);
	

        
      // Splitting each words in content by space and storing into keys
		String[] keys = contents.split(" ");
		// Initialising Stemmer object to use stem function 
		Stemmer stemobj = new Stemmer();
		HashMap<String, Integer> keywords =new HashMap<String, Integer>(); 
        for(int i = 0 ; i < keys.length ; i++)
        {
            boolean stopword = false;
            if(keys[i].length() != 1)                   //avoid storing single letter words
            {
                if (language.contentEquals("english"))
                {
                    if(englishStopwords.containsKey(keys[i]))
                        stopword = true;
                }
                else
                {
                    if(germanStopwords.containsKey(keys[i]))
                        stopword = true;
                }
                if (!stopword)
                {
                    String stemmedKeyword = "";
                    if (language.contentEquals("english"))
                    {
                        char[] w = keys[i].toCharArray();
                        stemobj.add(w, keys[i].length());
                        stemobj.stem();                                               //Stemming
                        stemmedKeyword = stemobj.toString();
                    }
                    else
                        stemmedKeyword = keys[i];
                     
                    if (keywords.containsKey(stemmedKeyword))
                    {
                        keywords.put(stemmedKeyword, keywords.get(stemmedKeyword) + 1);
                    }
                    else
                        keywords.put(stemmedKeyword, 1);
                }
                 
            }
             
        }
		//Inserting value to features table
//		Storetables st = new Storetables();
//		st.storetofeatures(keywords, docid,primURL,language);
//        System.out.println("The size of keywords:"+keywords.size());
//        
//         for (String str:keywords.keySet())
//         {
//        	System.out.println("Term: "+ str +"      count:"+keywords.get(str) + "      language :"+ language); 
//         }
        //returns true if parsed completely.
			
		return true;
			
 }


public boolean LanguageDetection(String metadata,HashMap<String, Integer> englishStopwords)
{
	double count=1;
	System.out.println(metadata);
	String []content=metadata.split(" ");
    System.out.println(content.length);
	System.out.println("Entering the Language Detection Section");
    
    for (int i = 0; i < content.length; i++) 
	{
    	
    	if(englishStopwords.containsKey(content[i]))
    	{
    		count++;
    	}
	}
    double check=(count /content.length);
    
    System.out.println("Count :" + count);
    System.out.println("The Probability Ratio:"+ check);
   double d1=0.15; 
    
  
    if((check)>= d1)
    	return true;
    else
    	return false;
}

	
	
	
    public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException 
    {
    	LanguageIndexer call = new LanguageIndexer();
    	String primURL="http://www.topbestsite.com/bigg-boss-kannada-3-news";
    	int docid=1;
    	if(call.parse(primURL, docid))
    		System.out.println("Image contents parsed Successfully");
    	else
    		System.out.println("Did not parse Successfully");
    }
 
}
