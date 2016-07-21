package com.searchengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author ganeshharugeri
 *
 */
public class Query_process {
	static Storetables st = new Storetables();
	private static Scanner in;
	static String score_type = null;

	public static void main(String[] args) throws Exception {
		in = new Scanner(System.in);
		String query = null, querytype = null;
		int limit = 0;
		Stemmer stemObj = new Stemmer();
		// Get from the user the query term from user
		System.out.println("\n Enter the query terms");
		query = in.nextLine();

		// Get from the user the limit on number of docs to be retrieved
		System.out.println("\n Enter number of documents to be retrieved: ");
		limit = in.nextInt();
		if (in.hasNextLine()) {
			in.nextLine();
		}
		// Get from the user how query terms to be handled
		System.out
				.println("Enter the type of query: C: Conjunctive / D: Disjunctive ");
		while (true) {
			String ans = in.nextLine().trim().toLowerCase();
			if (ans.equals("c")) {
				querytype = ans;
				break;
			} else if (ans.equals("d")) {
				querytype = ans;
				break;
			} else {
				System.out.println("Only c/d is allowed");
			}
		}
		// Get from the user the type of scoring model
		System.out
				.println("Which Scoring model do you prefer,\n1:TFIDF \n2:Okapi \n3:Combined score(pagerank+Okapi)");
		while (true) {
			int choice = in.nextInt();
			if (choice == 1) {
				score_type = "tf_idf_score";
				break;
			} else if (choice == 2) {
				score_type = "bm25_score";
				break;
			} else if (choice == 3) {
				score_type = "combined_score";
				break;
			} else {
				System.out.println("Only 1/2/3 are allowed");
			}
		}
		in.close();
		// Collect the query terms
		ArrayList<String> keys = new ArrayList<String>();
		// System.out.println("Keys Size: >>" + keys.size());
		String stemmedkeyword = null;
		String[] array = query.split(" ");
		for (int j = 0; j < array.length; j++) {
			if (array[j].length() != 1) {
				char[] w = array[j].toCharArray();
				stemObj.add(w, array[j].length());
				stemObj.stem();
				stemmedkeyword = stemObj.toString();
				keys.add(stemmedkeyword);
				// System.out.println("test stem:" + stemmedkeyword);
			}
		}

		// Based on users choice call the functions accordingly
		if (querytype.equalsIgnoreCase("d")) {
			disjunctive(keys, limit);
		} else if (querytype.equalsIgnoreCase("c")) {
			conjunctive(keys, limit);
		}
	}

	// Disjunctive query processing
	public static void disjunctive(ArrayList<String> terms, int limit)
			throws ClassNotFoundException, SQLException {
		System.out.println("Disjunctive Query Results");
		Connection conn = st.connects();
		PreparedStatement orps = null;
		ResultSet orrs = null;
		int z = 1;
		try {
			if (conn != null) {
				// Query to get documents containing any of the query terms
				StringBuilder orquery = new StringBuilder(
						"select distinct(url),fs."
								+ score_type
								+ " from documents d,features fs where fs.term in (");
				for (int i = 0; i < terms.size(); i++) {
					if (i > 0) {
						orquery.append(",");
					}
					orquery.append("?");
				}
				orquery.append(")").append(
						" and fs.docid = d.docid order by " + score_type
								+ " desc;");
				orps = conn.prepareStatement(orquery.toString());
				System.out.println("OR query: " + orquery);
				for (int m = 0; m < terms.size(); m++) {
					// System.out.println("term to write in query"+terms.get(m));
					orps.setString(m + 1, terms.get(m));
				}
				orrs = orps.executeQuery();
				while (orrs.next() && z <= limit) {
					System.out.format("\n%2d %20s   %3f", z, orrs.getString(1),
							orrs.getFloat(2));
					// System.out.println("Print results:"+orrs.getString(1)+" tfidf: "+orrs.getFloat(2));
					z++;
				}

			}
		} catch (Exception e) {
			System.err.println(" Exception in disjunctive method" + e);
		} finally {
			if (orps != null)
				orps.close();
			if (orrs != null)
				orrs.close();
			if (conn != null)
				conn.close();
		}

	}

	// Conjunctive query processing
	public static void conjunctive(ArrayList<String> terms, int limit)
			throws ClassNotFoundException, SQLException {
		System.out.println("Conjunctive Query Results");
		Connection conn = st.connects();
		PreparedStatement andps = null;
		ResultSet andrs = null;
		int z = 1;
		try {
			if (conn != null) {
				// Query to get documents containing all of the query terms
				StringBuilder andquery = new StringBuilder(
						"SELECT url, sum("
								+ score_type
								+ ") score FROM documents d JOIN features f USING (docid) WHERE f.term IN (");
				for (int l = 0; l < terms.size(); l++) {
					if (l > 0)
						andquery.append(",");
					andquery.append("?");
				}
				andquery.append(")").append(
						" GROUP BY url HAVING COUNT(*) = "
								+ terms.size() + " order by score desc");

				System.out.println("And query :" + andquery);
				andps = conn.prepareStatement(andquery.toString());
				for (int n = 0; n < terms.size(); n++) {
					andps.setString(n + 1, terms.get(n));
				}
				andrs = andps.executeQuery();
			}

			while (andrs.next() && z <= limit) {
				System.out.format("\n%2d %20s  %3f", z, andrs.getString(1),
						andrs.getFloat(2));
				z++;
			}

		} catch (Exception e) {
			System.err.println(" Exception in conjunctive method" + e);
		} finally {
			if (andps != null)
				andps.close();
			if (andrs != null)
				andrs.close();
			if (conn != null)
				conn.close();
		}
	}
}
