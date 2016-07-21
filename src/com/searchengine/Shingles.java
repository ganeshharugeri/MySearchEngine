package com.searchengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author ganeshharugeri
 *
 */
public class Shingles {

	// Create shingles using the page contents
	public static void createShingles(String contents, int docid)
			throws SQLException {
		ArrayList<String> contentsList = new ArrayList<String>(
				Arrays.asList(contents.split(" ")));
		// Hash set to hold unique shingles
		Set<String> shingleSet = new LinkedHashSet<String>();
		// get size of the shingles from user
		//int shing_size = Crawler.getShingle_Size();
		int shing_size = 4;
		
		// generate shingles and add them to a hash set
		for (int i = 0; i < contentsList.size() - shing_size; i++) {
			//Due to problem in virtual machine replaced following code with a for loop
			//String tempShingle = String.join(" ",contentsList.subList(i, i + shing_size));
			String tempShingle = "";
			for (String str : contentsList.subList(i, i + shing_size))
			{
				tempShingle += str + " ";
			}
			shingleSet.add(tempShingle.trim());

		}
		// convert set to string to store it in db as text array
		//String shinglesList = String.join(",", shingleSet);
		String shinglesList = "";
		for (String st : shingleSet)
		{
			shinglesList += st + ",";
		}
		if(shinglesList.length()>0)
		shinglesList = shinglesList.substring(0, shinglesList.length()-1);
		//System.out.println("string after: "+shinglesList);
		storeShingles(shinglesList, docid);
	}

	private static void storeShingles(String shinglesList, int docid)
			throws SQLException {

		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement shing_ps = null;
		try {
			conn = st.connects();
			// replace special characters and the unwanted serial commas to
			// avoid errors
			shinglesList = shinglesList.replaceAll("'", "")
					.replaceAll(",,", "");
			// store shingles in DB
			String shingleQuery = "update snippets set shingles='{"
					+ shinglesList + "}' where docid= ?";
			// System.out.println("shingleQuery: "+shingleQuery);
			shing_ps = conn.prepareStatement(shingleQuery);
			shing_ps.setInt(1, docid);
			shing_ps.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (shing_ps != null)
				shing_ps.close();
			if (conn != null)
				conn.close();
		}

	}

	// Compare documents to find the Jaccard similarity
	public static void compareDocuments() throws SQLException {
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement union_ps = null, intersect_ps = null, jsim_ps = null, docCount_ps = null;
		ResultSet union_rs = null, intersect_rs = null, docCount_rs = null;
		int ucount = 0, icount = 0, docCount = 0;
		double jsim = 0.0;
		try {
			conn = st.connects();
			// get total number of documents
			String docCountQuery = "SELECT COUNT(DISTINCT docid) FROM documents";
			docCount_ps = conn.prepareStatement(docCountQuery);
			docCount_rs = docCount_ps.executeQuery();
			while (docCount_rs.next()) {
				docCount = docCount_rs.getInt(1);
			}

			// get magnitude of union results
			String unionQuery = "SELECT count(elements) FROM (SELECT ARRAY (SELECT unnest(shingles) FROM snippets WHERE docid=? UNION SELECT unnest(shingles) FROM snippets WHERE docid=? ) AS resultArray) arr, unnest(arr.resultArray) AS elements";
			// get magnitude of intersect results
			String intersectQuery = "SELECT count(elements) FROM (SELECT ARRAY (SELECT unnest(shingles) FROM snippets WHERE docid=? INTERSECT SELECT unnest(shingles) FROM snippets WHERE docid=? ) AS resultArray) arr, unnest(arr.resultArray) AS elements";
			// Store jaccard similarity of documents
			String updateJsim = "INSERT INTO jaccardsim (from_docid, to_docid, jaccard_sim) VALUES ( ? , ? , ?)";

			for (int i = 0; i <= docCount; i++) {
				for (int j = i + 1; j <= docCount; j++) {
					union_ps = conn.prepareStatement(unionQuery);
					union_ps.setInt(1, i);
					union_ps.setInt(2, j);

					intersect_ps = conn.prepareStatement(intersectQuery);
					intersect_ps.setInt(1, i);
					intersect_ps.setInt(2, j);

					union_rs = union_ps.executeQuery();
					intersect_rs = intersect_ps.executeQuery();

					while (union_rs.next()) {
						ucount = union_rs.getInt(1);
					}
					while (intersect_rs.next()) {
						icount = intersect_rs.getInt(1);
					}
					// System.out.println("ucount for the documents "+i+" and"+j+": "+ucount);
					// System.out.println("icount for the documents "+i+" and"+j+": "+icount);

					// Jaccard similarity: intersection count/union count
					if (ucount != 0) {
						jsim = ((double) icount) / ucount;
						// System.out.println("Correspong Jsim value :"+jsim);
					} else {
						// System.out.println("ucount zero:");
						jsim = 0;
					}

					jsim_ps = conn.prepareStatement(updateJsim);
					jsim_ps.setInt(1, i);
					jsim_ps.setInt(2, j);
					jsim_ps.setDouble(3, jsim);
					jsim_ps.executeUpdate();

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (union_ps != null)
				union_ps.close();
			if (intersect_ps != null)
				intersect_ps.close();
			if (jsim_ps != null)
				jsim_ps.close();
			if (union_rs != null)
				union_rs.close();
			if (intersect_rs != null)
				intersect_rs.close();
			if (conn != null)
				conn.close();
		}

	}

	public static void main(String[] args) throws Exception {
	createShingles("I'm having trouble with a program I'm working on to create shingle pairs from each sentence in a text file. Right now my code reads in a .txt file in Java and outputs each sentence in order. I want to store each sentence separately then take each sentence and create 2-character shingles of them, which would be stored in an array",
	 4);
	 }

}
