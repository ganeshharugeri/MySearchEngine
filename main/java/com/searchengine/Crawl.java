package com.searchengine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author ganeshharugeri Reference:
 *         http://introcs.cs.princeton.edu/java/16pagerank/
 *
 */

public class Crawl {
	static Queue<String> primQueue = new LinkedList<String>();
	static Queue<String> secQueue = new LinkedList<String>();
	static Queue<String> holder = new LinkedList<String>();
	static Set<String> parsed = new LinkedHashSet<String>();
	static Indexer index = new Indexer();
	static Storetables st = new Storetables();
	static int docid = 0;
	static int depth = 0;
	static Crawler cr = new Crawler();
	static PageRank pr = new PageRank();
	static int shingle_size = 0;

	public static int startCrawling(String host, int Max_depth,
			int Max_Num_Doc, String ans, int shing_size) throws Exception {

		shingle_size = shing_size;
		setShingle_Size(shing_size);
		primQueue.add(host);
		System.out.println("Host url:" + host);
		System.out.println("Entry level:" + depth);
		// Create necessary tables
		Create_tables cr = new Create_tables();
		cr.create();
		// get the last recent Docid
		if (st.getDocid() > 0)
			docid = st.getDocid() + 1;

		// Get the domain of the host URL
		String hostDomain = Crawler.checkDomain(host);
		// Get a response from the User to allow/deny to leave the domain
		Boolean response;
		while (true) {

			ans = ans.trim().toLowerCase();
			if (ans.equals("y")) {
				response = true;
				break;
			} else if (ans.equals("n")) {
				response = false;
				break;
			} else {
				return -1;
			}
		}
		// Crawler starts using 2 queues
		while (!primQueue.isEmpty() || !secQueue.isEmpty()) {
			// Process primary queue
			if (secQueue.isEmpty()) {
				// CrawlerThread.primQueue =new CrawlerThread(1);
				processPrimQueue(primQueue, depth, hostDomain, response,
						Max_depth, Max_Num_Doc);
				depth++;
				System.out.println("Reached depth" + depth);
			}
			// Process secondary queue
			else if (primQueue.isEmpty()) {
				processSecQueue(secQueue, depth, hostDomain, response,
						Max_depth, Max_Num_Doc);
				depth++;
				System.out.println("Reached depth" + depth);
			}
			// Exit condition for the crawler
			if (depth >= Max_depth || parsed.size() >= Max_Num_Doc) {
				System.out.println("Parsing Completed.\nURLs parsed are: ");
				for (final String str : parsed) {
					System.out.println(str);
					// Store parsed urls in Documents table
					final int id = st.storetotemp(str, docid);
					st.storetodocuments(str, id);
					// docid++;
				}
				System.out.println("\nTotal number of documents parsed are: "
						+ parsed.size());
				// Calculate tf*idf score for all the terms
				System.out.println("\nCalculating TFIDF scores...");
				st.computetf_idf();
				System.out.print("completed.");
				// Calculate Okapi score for all the terms
				System.out.println("\nCalculating okapi scores...");
				st.compute_bm25();
				System.out.print("completed.");
				// Calculate Page rank and combined scores
				System.out
						.println("\nCalculating page rank and combined scores...");
				pr.compute_pagerank();
				System.out.print("completed.");

				System.out.println("\nUpdating snippets table...");
				Snippets.updateSnippetTable();
				System.out.print("completed.");

				System.out
						.println("\nCalculating and updating Jaccard similarity for docs...");
				Shingles.compareDocuments();
				System.out.print("completed.");

				System.out.println("\nHashing shingles ...");
				MinHashing.hashShingles();
				System.out.print("completed.");

				System.out
						.println("\nComparing minhashing shingles and updating similarity results ...");
				MinHashing.compareMinShingles();
				System.out.print("completed.");

				System.out.println("\nUpdating similarity comparisons ...");
				MinHashing.compareSimResults();
				System.out.print("completed.");
				System.out.println("\nEND!");
				break;
			}
		}
		return parsed.size();
	}

	public static void processPrimQueue(Queue<String> primQueue, int depth,
			String hostDomain, Boolean response, int Max_depth, int Max_Num_Doc)
			throws ClassNotFoundException, SQLException, IOException {

		while (!primQueue.isEmpty() && depth <= Max_depth
				&& parsed.size() <= Max_Num_Doc) {
			try {
				final Boolean restart = st.verifyurl();
				String primURL = null;
				// Check to restart if the last execution was aborted
				if (restart && depth == 0) {
					while (restart && depth == 0) {
						LinkedHashSet<String> parsedURLs = new LinkedHashSet<String>();
						final ArrayList<String> temholder = new ArrayList<String>();
						// Get last parsed urls from parsedtable and store them
						// Parsed variable
						parsedURLs = st.getParsedurl();
						parsed.addAll(parsedURLs);
						// Start from the next url
						final String lasturl = parsedURLs.toArray()[parsedURLs
								.size() - 1].toString();
						final int lasturlid = st.storetotemp(lasturl, docid);
						final int fromid = st.getfromid(lasturlid);
						primURL = st.geturl(fromid);
						// Restart crawling
						if (!primURL.isEmpty())
							temholder.addAll(Crawler.getLinksFromSite(primURL));
						for (int m = lasturlid + 3; m < temholder.size(); m++) {
							secQueue.add(temholder.get(m));
						}
						// break and start regular crawling process
						break;
					}
				} else {
					primURL = primQueue.poll();
					primURL = Crawler.trimURL(primURL);
					final int id = st.storetotemp(primURL, docid);
					if (id >= docid)
						docid++;
					if (index.parse(primURL, id)) {
						// System.out.println(primURL + " is Parsed.");
						if (parsed.isEmpty()) {
							parsed.add(primURL);
							st.storeinparsed(primURL, id);
						}

						final String urlDomain = Crawler.checkDomain(primURL);
						if (urlDomain.equalsIgnoreCase(hostDomain) || response) {
							holder.addAll(Crawler.getLinksFromSite(primURL));
							secQueue.addAll(Crawler.removeRedundant(holder,
									parsed));
							for (String str : holder) {
								str = Crawler.trimURL(str);
								// System.out.println("holder:: "+str);
								final int oid = st.storetotemp(str, docid);
								// System.out.println("rid :"+oid);
								if (oid >= docid)
									docid++;
								st.storeLinks(id, oid);
							}
							System.out.println("Yet to parse "
									+ (Max_Num_Doc - parsed.size()) + " URLs");
							holder.clear();
						}
						// to remove cycles in parsed table
						final String parsedtest = parsed.toArray()[parsed
								.size() - 1].toString();
						if (!primURL.equalsIgnoreCase(parsedtest)) {
							final int rid = st.storetotemp(primURL, id);
							// System.out.println("rid :"+rid);
							if (rid >= docid)
								docid++;
							st.storeinparsed(primURL, rid);
						}
						parsed.add(primURL);
					}
				}
			} catch (final SocketTimeoutException exception) {
				continue;
			} catch (final MalformedURLException me) {
				continue;
				// System.err.println("MalformedURLException: " + me);
			} catch (final IOException ioe) {
				continue;
				// System.err.println("IOException: " + ioe);
			}

		}
	}

	public static void processSecQueue(Queue<String> secQueue, int depth,
			String hostDomain, Boolean response, int Max_depth, int Max_Num_Doc)
			throws ClassNotFoundException, SQLException, IOException {

		while (!secQueue.isEmpty() && depth <= Max_depth
				&& parsed.size() < Max_Num_Doc) {
			try {
				String secURL = secQueue.poll();
				secURL = Crawler.trimURL(secURL);
				final int id = st.storetotemp(secURL, docid);
				// System.out.println("id in else if: "+id);
				if (id >= docid)
					docid++;
				if (index.parse(secURL, id)) {
					// System.out.println(secURL + " is Parsed.");

					// Function to store image source and related data
					ImageFormatStoring.Image_retrieval(secURL, id);
					final String urlDomain = Crawler.checkDomain(secURL);
					if (urlDomain.equalsIgnoreCase(hostDomain) || response) {
						holder.addAll(Crawler.getLinksFromSite(secURL));
						primQueue.addAll(Crawler
								.removeRedundant(holder, parsed));
						for (String str : holder) {
							str = Crawler.trimURL(str);
							final int oid = st.storetotemp(str, docid);
							// System.out.println("oid in else if: "+oid);
							if (oid >= docid)
								docid++;
							st.storeLinks(id, oid);
						}
					}
					final String parsedtest = parsed.toArray()[parsed.size() - 1]
							.toString();
					if (!secURL.equalsIgnoreCase(parsedtest)) {
						final int rid = st.storetotemp(secURL, id);
						// System.out.println("id in else if: "+rid);
						if (rid >= docid)
							docid++;
						st.storeinparsed(secURL, rid);
					}
					parsed.add(secURL);
				}
				System.out.println("Yet to parse parse "
						+ (Max_Num_Doc - parsed.size()) + " URLs");
				holder.clear();
			} catch (final SocketTimeoutException exception) {
				continue;
			} catch (final MalformedURLException me) {
				System.err.println("MalformedURLException: " + me);
			} catch (final IOException ioe) {
				System.err.println("MalformedURLException: " + ioe);
			}

		}
	}

	public static void setShingle_Size(int shing_size) {
		Crawler.shingle_size = shing_size;
	}

}
