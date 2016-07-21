package com.searchengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.la4j.Vector;
import org.la4j.matrix.SparseMatrix;
import org.la4j.matrix.sparse.CCSMatrix;
import org.la4j.vector.SparseVector;

/**
 * @author ganeshharugeri Reference:
 *         http://introcs.cs.princeton.edu/java/16pagerank/
 *
 */
public class PageRank {

	Storetables st = new Storetables();
	int size = 0;
	SparseMatrix linkCountMatrix, transMatrix;
	SparseVector pageRank;// SparseVector.zero(size);
	// Convergence Verification matrix
	SparseVector verify;

	public void pr_calculations() throws ClassNotFoundException, SQLException {
		Connection conn = st.connects();
		PreparedStatement ps = null, ps1 = null;
		ResultSet rs = null, rs1 = null;
		try {
			if (conn != null) {
				String maxDocId = "select max(from_docid) from links";
				ps = conn.prepareStatement(maxDocId);
				rs = ps.executeQuery();
				while (rs.next()) {
					size = rs.getInt(1);
				}
				size += 1;
				// System.out.println("Size: " + size);
				// Link count Matrix
				linkCountMatrix = new CCSMatrix(size, size);
				// Transition Matrix
				transMatrix = new CCSMatrix(size, size);

				// Out-degree counts
				int[] outDegree = new int[size];

				// get all the links from DB
				String allLinks = "Select  from_docid,to_docid from links where to_docid in (select distinct From_docid from links)  order by from_docid,to_docid";
				ps1 = conn.prepareStatement(allLinks);
				rs1 = ps1.executeQuery();
				while (rs1.next()) {
					int i = rs1.getInt(1);
					int j = rs1.getInt(2);
					outDegree[i]++;
					linkCountMatrix.set(i, j, linkCountMatrix.get(i, j) + 1);
				}
				// System.out.println(mainMatrix.toCSV());
				double randomJump = 0.10 / size;
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						// forming transition matrix using 90/10 rule.
						double p = 0.90 * linkCountMatrix.get(i, j)
								/ outDegree[i] + randomJump;
						if (Double.isNaN(p))
							transMatrix.set(i, j, randomJump);
						else
							transMatrix.set(i, j, p);
					}
				}

				// Initial Eigen vector
				pageRank = SparseVector.zero(size);
				pageRank.set(0, 1.0);

				// Finding convergence
				for (int i = 1; i < 120; i++) {
					SparseVector pageRankNew;
					pageRankNew = (SparseVector) pageRank.multiply(transMatrix);
					verify = (SparseVector) pageRank.subtract(pageRankNew);
					pageRank = pageRankNew;
					// System.out.println(pageRank);
					if (magnitude(verify) < randomJump) {
						// Convergence reaches when (prNew - pr) < randomJump
						System.out.println("Pagerank converged at iteration:"
								+ i);
						break;
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();
			if (ps1 != null)
				ps1.close();
			if (conn != null)
				conn.close();
		}

	}

	private double magnitude(Vector verify) {
		double res = 0;
		for (int a = 0; a < size; a++) {
			res += Math.pow(verify.get(a), 2);
		}
		// System.out.println("Res:"+Math.sqrt(res));
		return Math.sqrt(res);
	}

	// Calculate page rank and combined scores.
	public void compute_pagerank() throws ClassNotFoundException, SQLException {
		PreparedStatement ps2 = null, ps3 = null, ps4 = null, ps5 = null;
		Connection conn = st.connects();
		try {
			if (conn != null) {
				// Page rank calculations
				pr_calculations();
				// Set page rank value for corresponding pages in db
				for (int m = 0; m < size; m++) {
					String prscore = "update features set pr_score="
							+ pageRank.get(m) + " where docid=" + m;
					ps2 = conn.prepareStatement(prscore);
					ps2.executeUpdate();
				}
				// Normalize Okapi scores
				String normalised_bm25score = "update features f set normalised_bm25score=(f.bm25_score - X.min)/(X.max-X.min)  from (select max(bm25_score) as max, min(bm25_score) as min from features) X, features Z where f.docid=Z.docid";
				// Normalize page rank scores
				String normalised_prscore = "update features f set normalised_prscore=(f.pr_score - X.min)/(X.max-X.min)  from (select max(pr_score) as max, min(pr_score) as min from features) X, features Z where f.docid=Z.docid";
				// Combine Okapi and page rank scores with 60/40 ratio
				double pr_weightage = 0.4;
				String combined_score = "update features set combined_score = (("
						+ (1 - pr_weightage)
						+ "*normalised_bm25score) + ("
						+ pr_weightage + "*normalised_prscore))";
				ps3 = conn.prepareStatement(normalised_bm25score);
				ps3.executeUpdate();
				ps4 = conn.prepareStatement(normalised_prscore);
				ps4.executeUpdate();
				ps5 = conn.prepareStatement(combined_score);
				ps5.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps2 != null)
				ps2.close();
			if (ps3 != null)
				ps3.close();
			if (ps4 != null)
				ps4.close();
			if (ps5 != null)
				ps5.close();
			if (conn != null)
				conn.close();

		}
	}

}
