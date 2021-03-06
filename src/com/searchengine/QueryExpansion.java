/**
 * 
 */
package com.searchengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * @author ganeshharugeri
 *
 */
public class QueryExpansion {
	static String language = null;
	private static Scanner scanner;

	public static ArrayList<String> tildeOperations(String names)
			throws IOException {
		// System.out.println("Entering tilde..");
		ArrayList<String> tildewords = new ArrayList<String>();
		for (int i = 0; i < names.length(); i++) {
			if (names.charAt(i) == '~') {
				String tildeString = names.substring(i + 1, names.length());
				String word = "";
				if (!(tildeString.indexOf(" ") <= 0))
					word = tildeString.substring(0, tildeString.indexOf(" "));
				else
					word = tildeString.substring(0, tildeString.length());

				tildewords.add(word);
			}
		}
		System.out.println("Tilde words are: "
				+ Arrays.toString(tildewords.toArray()));
		return tildewords;
	}

	public static String trimQuery(String names) {
		names = names.replace("~", "").replaceAll("\\s+", " ");
		// System.out.println("Updated after tilde:" + names);
		return names;

	}

	public static ArrayList<String> getEnglishSynonyms(String tildeWord)
			throws IOException {
		ArrayList<POS> allPOS = new ArrayList<POS>();
		ArrayList<String> synonyms = new ArrayList<String>();
		URL url = null;

		//System.out.println("In English Query Expansion");
		// WordNet-3.0 is stored within project folder as internal file
		url = QueryExpansion.class.getResource("/resource/WordNet-3.0/dict");
		// System.out.println("url>>>" + url);
		if (url != null) {
			/*
			 * Idictionary is an interface to access dictionary files construct
			 * the dictionary object and open it
			 */
			IDictionary dict = new Dictionary(url);
			dict.open();
			WordnetStemmer stemmer = new WordnetStemmer(dict);

			for (POS pos : POS.values()) {
				// Check every stem, because WordNet doesn't have every surface
				// form in its database.
				for (String stem : stemmer.findStems(tildeWord, pos)) {
					IIndexWord iw = dict.getIndexWord(stem, pos);
					if (iw != null) {
						allPOS.add(pos);
					}
				}
			}
			System.out.println("All POS: " + Arrays.toString(allPOS.toArray()));

			for (POS pos : allPOS) {
				System.out.println("POS:>>" + pos);
				//System.out.println("tildeWord:>>" + tildeWord);
				IIndexWord indexWord = dict.getIndexWord(tildeWord, pos);
				// System.out.println("idxWord: "+idxWord);
				if (indexWord != null) {
					IWordID wrdID = indexWord.getWordIDs().get(0);
					IWord wordx = dict.getWord(wrdID);
					ISynset iSynset = wordx.getSynset();
					for (IWord mem : iSynset.getWords()) {
						System.out.println("Synonums for the POS " + pos
								+ " are: " + mem.getLemma());
						synonyms.add(mem.getLemma());
					}
				}
			}
			System.out.println("English Synoyms: "
					+ Arrays.toString(synonyms.toArray()));
		}
		return synonyms;
	}

	public static ArrayList<String> getGermanSynoyms(String tildeWord)
			throws FileNotFoundException {
		ArrayList<String> synonyms = new ArrayList<String>();
		// System.out.println("I am in getGermanSynoyms"+ tildeWord);

		// openthesaurus.txt is stored within project folder as internal file
		String path = QueryExpansion.class.getResource(
				"/resource/openthesaurus.txt").getPath();
		File file = new File(path);
		scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			// System.out.println("nextLine: "+nextLine);
			Boolean found = Arrays.asList(nextLine.split(";")).contains(
					tildeWord);

			if (found) {
				ArrayList<String> temp = new ArrayList<String>(
						Arrays.asList(nextLine.split(";")));
				for (int x = temp.size() - 1; x >= 0; x--) {
					// System.out.println("Before: "+temp.get(x));
					String str = temp.get(x).replaceAll("\\(.*?\\) ?", "");
					// System.out.println("str: "+str);
					temp.set(x, str);
					// System.out.println("After: "+temp.get(x));

					System.out.println();
					if (temp.get(x).trim().split("\\s+").length > 1) {
						temp.remove(x);
					}
				}
				synonyms = temp;
				break;
			}
		}
		System.out.println(" Germnan Synonyms for  " + tildeWord + " are: "
				+ Arrays.toString(synonyms.toArray()));
		scanner.close();
		return synonyms;
	}

	public static String buildTildeQuery(String names, String lang)
			throws IOException {
		language = lang;
		ArrayList<String> namesList = new ArrayList<String>(Arrays.asList(names
				.split(" ")));
		// Building Query to get the snippets containing query terms
		StringBuilder tildeQuery = new StringBuilder(
				"select distinct(url), title");

		for (int i = 0; i < namesList.size(); i++) {

			if (namesList.size() > 1) {
				// System.out.println("\n more than one word loop");
				tildeQuery.append(", ts_headline('" + language
						+ "', pagecontent, to_tsquery");
				if (namesList.get(i).startsWith("~"))
					tildeQuery.append(getSynQuery(namesList.get(i), names));
				else
					tildeQuery.append("(' " + namesList.get(i) + " ')");

				tildeQuery.append(", 'MaxWords="
						+ (int) (32 / namesList.size())
						+ ", MinWords=15, ShortWord=3')");
			} else {
				// System.out.println("\n one word loop");
				tildeQuery.append(",ts_headline('" + language
						+ "', pagecontent, to_tsquery"
						+ getSynQuery(namesList.get(i), names));
				tildeQuery.append(",'MaxWords=32, MinWords=20, ShortWord=3')");

			}
		}

		tildeQuery
				.append(", fs.pr_score pr, ts_rank(vector, query) AS rank from documents d,features fs,snippets i, to_tsquery");
		if (namesList.size() > 1)
			tildeQuery.append("('");

		for (int j = 0; j < namesList.size(); j++) {
			if (j > 0)
				tildeQuery.append(" & ");

			if (namesList.get(j).startsWith("~")) {
				if (namesList.size() > 1)
					tildeQuery.append(getSynQuery(namesList.get(j), names)
							.replaceAll("'", ""));
				else
					tildeQuery.append(getSynQuery(namesList.get(j), names));

			} else
				tildeQuery.append(namesList.get(j));
		}
		if (namesList.size() > 1)
			tildeQuery.append("')");

		tildeQuery
				.append(" query where fs.language='"
						+ language
						+ "' and fs.docid= d.docid and fs.docid=i.docid and pagecontent @@ query order by pr desc, rank desc");

		System.out.println("TILDE QUERY: " + tildeQuery);
		return (tildeQuery.toString());

	}

	public static String getSynQuery(String tildeword, String names)
			throws IOException {
		// System.out.println("\nEntring getSynQuery");
		tildeword = trimQuery(tildeword);
		// System.out.println("Tildeword trimmed: "+tildeword);
		names = trimQuery(names);
		// System.out.println("names trimmed: "+names);

		ArrayList<String> namesList = new ArrayList<String>(Arrays.asList(names
				.split(" ")));
		// System.out.println("NamesList: "+namesList);
		ArrayList<String> synonyms = new ArrayList<String>();
		StringBuilder syngroup = new StringBuilder();
		for (String word : namesList) {
			if (tildeword.equals(word)) {
				//System.out.println("Sending word: " + word);
				if (language.equalsIgnoreCase("english"))
					synonyms = getEnglishSynonyms(word);
				else if (language.equalsIgnoreCase("german"))
					synonyms = getGermanSynoyms(word);
				syngroup.append(" (' " + word);
				for (int j = 0; j < synonyms.size(); j++) {
					syngroup.append(" | " + synonyms.get(j));
				}
				syngroup.append(" ') ");
			}
		}

		return syngroup.toString();
	}

}
