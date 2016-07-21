/**
 * 
 */
package com.searchengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author ganeshharugeri
 *
 */
public class MinHashing {

	// hash the shingles and store them in snippets table
	public static void hashShingles() throws SQLException {
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement hashshing_ps = null;

		try {
			conn = st.connects();

			// sensitive query, you need to have created a hex_to_bigint UDF
			// before executing this query
			// fyi hex_to_bigint UDF is created in create_tables class
			String hashShingQuery = "update snippets s set hashShingles = arr.intarr from (select docid,array_agg(hex_to_bigint(hex)) as intarr  from snippets, unnest(shingles) shing, md5(shing) hex group by docid) arr  where arr.docid=s.docid";
			hashshing_ps = conn.prepareStatement(hashShingQuery);
			hashshing_ps.executeUpdate();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (hashshing_ps != null)
				hashshing_ps.close();
			if (conn != null)
				conn.close();
		}
		// call minShingles function to find minValues for each document
		minShingles();
	}

	// get min shingles for n=1,4,16 and 32 values and store them in snippets
	// table
	public static void minShingles() throws SQLException {
		// System.out.println("I am in minShingles function");
		Storetables st = new Storetables();
		Connection conn = null;
		int docCount = 0;
		PreparedStatement docCount_ps = null;
		ResultSet docCount_rs = null;

		try {
			conn = st.connects();
			// get total number of documents
			String docCountQuery = "SELECT COUNT(DISTINCT docid) FROM documents";
			docCount_ps = conn.prepareStatement(docCountQuery);
			docCount_rs = docCount_ps.executeQuery();
			while (docCount_rs.next()) {
				docCount = docCount_rs.getInt(1);
			}

			// Update all min(1,4,16,32) columns
			updateMinColumns("min1", 1, docCount);
			updateMinColumns("min4", 4, docCount);
			updateMinColumns("min16", 16, docCount);
			updateMinColumns("min32", 32, docCount);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (docCount_ps != null)
				docCount_ps.close();
			if (conn != null)
				conn.close();
		}
	}

	public static void updateMinColumns(String columnName, int limit,
			int docCount) throws SQLException {
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement minhash_ps = null;

		String minColUpdateQuery = "update snippets set "
				+ columnName
				+ " = arr.minarr from (select array_agg(shingsarr.shings) minarr from(select shings from snippets,unnest(hashShingles) shings where docid =? order by shings limit "
				+ limit + ") shingsarr)arr where docid =?";
		try {
			conn = st.connects();
			minhash_ps = conn.prepareStatement(minColUpdateQuery);

			for (int i = 0; i <= docCount; i++) {
				minhash_ps.setInt(1, i);
				minhash_ps.setInt(2, i);
				minhash_ps.executeUpdate();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (minhash_ps != null)
				minhash_ps.close();
			if (conn != null)
				conn.close();
		}

	}

	// comapre all min(1,4,16,32) shingles and accordingly update the
	// 'jaccardsim' table
	public static void compareMinShingles() throws SQLException {
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement union_ps = null, intersect_ps = null, minsim_ps = null, docCount_ps = null;
		ResultSet union_rs = null, intersect_rs = null, docCount_rs = null;
		int ucount = 0, icount = 0, docCount = 0;
		// min column names in Jaccardsim table
		String[] min = { "min1", "min4", "min16", "min32" };
		double minsim = 0.0;
		try {
			conn = st.connects();

			// get total number of documents
			String docCountQuery = "SELECT COUNT(DISTINCT docid) FROM documents";
			docCount_ps = conn.prepareStatement(docCountQuery);
			docCount_rs = docCount_ps.executeQuery();
			while (docCount_rs.next()) {
				docCount = docCount_rs.getInt(1);
			}

			// First for loop iterates through each min(1,4,16,32) columns
			for (int m = 0; m < 4; m++) {
				// System.out.println("Min array " + min[m]);
				// get magnitude of union results
				String unionQuery = "SELECT count(elements) FROM (SELECT ARRAY (SELECT unnest("
						+ min[m]
						+ ") FROM snippets WHERE docid=? UNION SELECT unnest("
						+ min[m]
						+ ") FROM snippets WHERE docid=? ) AS resultArray) arr, unnest(arr.resultArray) AS elements";
				// get magnitude of intersect results
				String intersectQuery = "SELECT count(elements) FROM (SELECT ARRAY (SELECT unnest("
						+ min[m]
						+ ") FROM snippets WHERE docid=? INTERSECT SELECT unnest("
						+ min[m]
						+ ") FROM snippets WHERE docid=? ) AS resultArray) arr, unnest(arr.resultArray) AS elements";
				// Store jaccard similarity of documents
				String updateJsim = "update jaccardsim  set " + min[m]
						+ "= ?  where from_docid = ? and to_docid = ?";

				// Second for loop iterates through from_docid of snippets table
				for (int i = 0; i <= docCount; i++) {
					// Third for loop iterates through To_docid of snippets
					// table
					for (int j = i + 1; j <= docCount; j++) {
						// union_ps.setString(1, min[m]);
						// union_ps.setString(3, min[m]);
						union_ps = conn.prepareStatement(unionQuery);
						union_ps.setInt(1, i);
						union_ps.setInt(2, j);

						// intersect_ps.setString(1, min[m]);
						// intersect_ps.setString(3, min[m]);
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
							minsim = ((double) icount) / ucount;
							// System.out.println("Correspong minsim value :"+minsim);
						} else {
							// System.out.println("ucount zero:");
							minsim = 0;
						}
						// minsim_ps.setString(1, min[m]);
						minsim_ps = conn.prepareStatement(updateJsim);
						minsim_ps.setDouble(1, minsim);
						minsim_ps.setInt(2, i);
						minsim_ps.setInt(3, j);
						minsim_ps.executeUpdate();

					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (union_ps != null)
				union_ps.close();
			if (intersect_ps != null)
				intersect_ps.close();
			if (minsim_ps != null)
				minsim_ps.close();
			if (union_rs != null)
				union_rs.close();
			if (intersect_rs != null)
				intersect_rs.close();
			if (conn != null)
				conn.close();
		}
		//call to find absolute errors between jaccard and varying minhash values 
		findAbsError();
	}
	
	
	//Update Absolute errors between jaccard and varying minhash values
	public static void findAbsError() throws SQLException{
	
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement error_ps=null;
		String[] errCol = {"abserrormin1","abserrormin4","abserrormin16","abserrormin32"};
		String[] col ={"min1","min4","min16","min32"};

		try {
			conn =st.connects();
			for(int e=0;e<4;e++){
				String error = "update jaccardsim s set "+errCol[e]+" = inquery.error from (select j1.from_docid fid,j1.to_docid tid, abs(j1.jaccard_sim -j1."+col[e]+") error from jaccardsim j1,jaccardsim j2 where j1.from_docid = j2.from_docid and j1.to_docid = j2.to_docid) inquery where s.from_docid =inquery.fid and s.to_docid = inquery.tid"; 
				error_ps = conn.prepareStatement(error);
    			error_ps.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (error_ps != null)
				error_ps.close();
			if (conn != null)
				conn.close();
		}	
	}

	public static void compareSimResults() throws SQLException {

		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement avg_ps = null, median_ps = null, insert_ps = null;
		ResultSet avg_rs = null, median_rs = null;
		ArrayList<Double> medianvalues = new ArrayList<Double>();

		try {
			conn = st.connects();
			String insertAvg = " insert into simresults(measuretype, jaccardsim,min1sim, min4sim ,min16sim,min32sim) values(?,?,?,?,?,?)";
			insert_ps = conn.prepareStatement(insertAvg);
			String getAVG = "SELECT AVG(jaccard_sim) as Jcoef,AVG(abserrormin1) as Avg_min1,AVG(abserrormin4) as Avg_min4 ,AVG(abserrormin16) as Avg_min16,AVG(abserrormin32) as Avg_min32 FROM jaccardsim;";
			avg_ps = conn.prepareStatement(getAVG);
			avg_rs = avg_ps.executeQuery();

			// Print Avg Values
			// System.out.println("============AVG Values=====================");
			while (avg_rs.next()) {
				System.out.println("Updating Avg values...");
				insert_ps.setString(1, "Average");
				insert_ps.setDouble(2, avg_rs.getDouble(1));
				insert_ps.setDouble(3, avg_rs.getDouble(2));
				insert_ps.setDouble(4, avg_rs.getDouble(3));
				insert_ps.setDouble(5, avg_rs.getDouble(4));
				insert_ps.setDouble(6, avg_rs.getDouble(5));
				insert_ps.executeUpdate();

			}

			String[] quartiles = { "quartile1","median","quartile3" };
			String[] columns = { "jaccard_sim", "abserrormin1", "abserrormin4", "abserrormin16", "abserrormin32" };

			// Median calculations
			for (int j = 1; j < 4; j++) {

				for (int k = 0; k < 5; k++) {
					String getMedian = "SELECT MAX(" + columns[k]
							+ ")  FROM (SELECT " + columns[k]
							+ ",ntile(4) OVER (ORDER BY " + columns[k]
							+ ") AS parts FROM jaccardsim ) as t WHERE parts ="
							+ j + " GROUP BY parts";
					median_ps = conn.prepareStatement(getMedian);
					median_rs = median_ps.executeQuery();
					while (median_rs.next()) {
						medianvalues.add(median_rs.getDouble(1));
					}
				}

				// System.out.println("============Median Values=====================");
				// Update Median values
				System.out.println("Updating median values...");
				insert_ps.setString(1, quartiles[j - 1]);
				insert_ps.setDouble(2, medianvalues.get(0));
				insert_ps.setDouble(3, medianvalues.get(1));
				insert_ps.setDouble(4, medianvalues.get(2));
				insert_ps.setDouble(5, medianvalues.get(3));
				insert_ps.setDouble(6, medianvalues.get(4));
				insert_ps.executeUpdate();
				medianvalues.clear();				
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (avg_ps != null)
				avg_ps.close();
			if (median_ps != null)
				median_ps.close();
			if (insert_ps != null)
				insert_ps.close();
			if (median_rs != null)
				median_rs.close();
			if (avg_rs != null)
				avg_rs.close();
			if (conn != null)
				conn.close();
		}
	}

}
