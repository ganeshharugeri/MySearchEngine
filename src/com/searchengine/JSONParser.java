/**
 * 
 */
package com.searchengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * @author ganeshharugeri
 *
 */
public class JSONParser {

	// Form URL to get JSON object from all search engines
	public void formURL(String names, Integer i) throws IOException {
		StringBuilder url = new StringBuilder();
		names = names.trim();
		ArrayList<String> namesList = new ArrayList<String>(Arrays.asList(names
				.split(" ")));
		switch (i) {
		case 1:
			System.out.println("case 1 ");
			System.out.println("No good URL for group 1");
			break;
		case 2:
			System.out.println("case 2 ");
			for (int k = 0; k < namesList.size(); k++) {
				url.append("http://192.168.18.12:9999/project/getjson?query=");
				url.append(namesList.get(k));
				url.append("&score=tfidf&lang=en&k=10");
				parseJSON(url.toString(), i);
				url.setLength(0);
			}
			break;
		case 3:
			System.out.println("case 3 ");
			for (int k = 0; k < namesList.size(); k++) {
				url.append("http://192.168.18.13:8080/IS-Project/json?query=");
				url.append(namesList.get(k));
				parseJSON(url.toString(), i);
				url.setLength(0);
			}
			break;
		case 4:
			System.out.println("case 4 ");
			/*
			 * for (int k = 0; k < namesList.size(); k++) {
			 * url.append("http://192.168.18.25:8080/IS-Project-Servlet/JSON?query="
			 * ); url.append(namesList.get(k)); parseJSON(url.toString(), i);
			 * url.setLength(0); }
			 */
			break;
		case 5:
			System.out.println("case  5");
			for (int k = 0; k < namesList.size(); k++) {
				url.append("http://192.168.18.15:8080/Servlet/JSONData?query=");
				url.append(namesList.get(k));
				url.append("&ressize=20&lang=english");
				parseJSON(url.toString(), i);
				url.setLength(0);
			}
			break;
		case 6:
			System.out.println("case 6 ");
			System.out.println("No URL for group 6");
			break;
		case 7:
			System.out.println("case 7 ");
			// System.out.println("namesList size: " + namesList.size());
			for (int k = 0; k < namesList.size(); k++) {
				url.append("http://192.168.18.17:8080/com.webapp/Jsonresult?jsonsearch=");
				url.append(namesList.get(k));
				url.append("&json_no_of_docs=10");
				parseJSON(url.toString(), i);
				url.setLength(0);
			}
			break;
		case 8:
			System.out.println("case 8");
			// System.out.println("namesList size: " + namesList.size());
			for (int k = 0; k < namesList.size(); k++) {
				url.append("http://192.168.18.18:8080/project08/search?query=");
				url.append(namesList.get(k));
				url.append("&k=20");
				parseJSON(url.toString(), i);
				url.setLength(0);
			}
			break;
		default:
			System.out.println("INAVLID URL");
			break;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void parseJSON(String url, Integer seNum) throws IOException {
		System.out.println("\nURL: " + url + "\n group number: " + seNum);

		List list = new ArrayList();
		String line = "";
		StringBuilder sb = new StringBuilder();
		try {
			// Add search engine number
			list.add(seNum);
			System.setProperty("http.keepAlive", "false");
			InputStream is = new URL(url).openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			// Get the JSON Object from the search engine
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				sb.append(line);
			}
			System.out.println("line: " + sb.toString());
			Object obj = JSONValue.parse(sb.toString());
			JSONObject jsonObject = (JSONObject) obj;
			is.close();
			br.close();

			// get details from the JSON object
			if (jsonObject != null) {
				if (jsonObject.get("cw") != null) {
					Integer cw = (int) (long) jsonObject.get("cw");
					// System.out.println("What's cw?: " + cw);
					list.add(cw);
				} else {
					Integer cw = 0;
					list.add(cw);
				}
				// Get statistics of the term
				// System.out.println("What's Stat?: " +jsonObject.get("stat"));
				if (jsonObject.get("stat") != null) {
					JSONArray stat = (JSONArray) jsonObject.get("stat");
					Iterator statItr = stat.iterator();
					while (statItr.hasNext()) {
						JSONObject innerObj = (JSONObject) statItr.next();
						if (innerObj.get("term") != null
								&& innerObj.get("df") != null) {
							String term = innerObj.get("term").toString();
							Integer df = (int) (long) innerObj.get("df");
							// System.out.println("term: " + term);
							// System.out.println("df:" + df);
							if (!term.isEmpty()) {
								// if (list.size() < 3) {
								list.add(term);
								list.add(df);
								// store informatin in db
								System.out.println("List sending to store: "
										+ list);
								storeMetaDetails(list);
							}

							// store urls for each term in another table
							// System.out.println("What's resultList?: "+jsonObject.get("resultList"));
							if (jsonObject.get("resultList") != null) {
								JSONArray resList = (JSONArray) jsonObject
										.get("resultList");
								Iterator resListItr = resList.iterator();
								// take each value from the json array
								// separately
								while (resListItr.hasNext()) {
									JSONObject innerObject = (JSONObject) resListItr
											.next();
									String resUrl = innerObject.get("url")
											.toString();
									Double resScore = (Double) innerObject
											.get("score");
									// System.out.println("url " + resUrl);
									// System.out.println("score " + resScore);
									StoreMetaUrlDetails(seNum, term, resUrl,
											resScore);
								}
							}

						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Store term specific details in metasearch table
	@SuppressWarnings("rawtypes")
	private static void storeMetaDetails(List list) throws SQLException {
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement meta_ps = null;
		try {
			conn = st.connects();
			String metaInsert = "INSERT INTO metasearch(se_id,cw,term,df) SELECT ?,?,?,? WHERE NOT EXISTS (SELECT term FROM metasearch WHERE se_id=? and term=? )";
			meta_ps = conn.prepareStatement(metaInsert);
			meta_ps.setInt(1, (int) list.get(0));
			meta_ps.setInt(2, (int) list.get(1));
			meta_ps.setString(3, list.get(2).toString());
			meta_ps.setInt(4, (int) list.get(3));
			meta_ps.setInt(5, (int) list.get(0));
			meta_ps.setString(6, list.get(2).toString());
			System.out.println("Meta query: " + meta_ps.toString());
			meta_ps.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (meta_ps != null)
				meta_ps.close();
			if (conn != null)
				conn.close();
		}
	}

	// Store URL,score and other details of each term in another table
	// MetaUrldetails
	private static void StoreMetaUrlDetails(Integer seNum, String term,
			String resUrl, Double resScore) throws SQLException {
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement url_ps = null;
		try {
			conn = st.connects();
			String insUrls = "insert into MetaUrldetails (se_id, term, url, urlscore) SELECT ?,?,?,? WHERE NOT EXISTS (SELECT term FROM MetaUrldetails WHERE term=? and url=?);";
			url_ps = conn.prepareStatement(insUrls);
			url_ps.setInt(1, seNum);
			url_ps.setString(2, term);
			url_ps.setString(3, resUrl);
			url_ps.setDouble(4, resScore);
			url_ps.setString(5, term);
			url_ps.setString(6, resUrl);
			url_ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (url_ps != null)
				url_ps.close();
			if (conn != null)
				conn.close();
		}
	}

	public void calculateMetaInfo() throws SQLException {
		// calculate score in metasearch table
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement cf_ps = null, avg_cw_ps = null, tval_ps = null, ival_ps = null, score_ps = null, riscore_ps = null, ri1score_ps = null, dscore_ps = null;
		String cf = "update metasearch p set cf=cnt from(select  m.term trm,count(*) as cnt from metasearch m,metasearch n where m.term=n.term and m.se_id=n.se_id group by m.term ) q where p.term=q.trm";
		String avg_cw = "update metasearch set avg_cw= b.c from( select avg(cw) c from (select distinct se_id,cw from Metasearch) a) b";
		String tval = "update metasearch set tval = df/(df+50+150*cw/avg_cw)";
		String ival = "update metasearch set ival = log((se+0.5)/cf)/log(se+1.0) from(select count (distinct(se_id)) se from metasearch)k";
		String score = "update metasearch set score =0.4+(1-0.4)*tval*ival";

		// use metasearch table details and calculate scores in metaurldetails
		String riscore = "update metaurldetails m1 set riscore = cs, rmax = 0.4+0.6*i from (select se_id id,term t,score cs,ival i from metasearch) intab where m1.se_id = intab.id and m1.term = intab.t";
		String ri1score = "update metaurldetails set ri1score = ((riscore -0.4)/(rmax-0.4)) , dscore = ((urlscore +0.4*urlscore*ri1score)/1.4)";
		String dscore = "update metaurldetails set dscore = ((urlscore +0.4*urlscore*ri1score)/1.4)";

		try {
			conn = st.connects();
			// update avg_cw
			avg_cw_ps = conn.prepareStatement(avg_cw);
			avg_cw_ps.executeUpdate();
			// update cf
			cf_ps = conn.prepareStatement(cf);
			cf_ps.executeUpdate();
			// update tval
			tval_ps = conn.prepareStatement(tval);
			tval_ps.executeUpdate();
			// update ival
			ival_ps = conn.prepareStatement(ival);
			ival_ps.executeUpdate();
			// update score
			score_ps = conn.prepareStatement(score);
			score_ps.executeUpdate();
			// update riscore
			riscore_ps = conn.prepareStatement(riscore);
			riscore_ps.executeUpdate();
			// update ri1score
			ri1score_ps = conn.prepareStatement(ri1score);
			ri1score_ps.executeUpdate();
			// update dscore
			dscore_ps = conn.prepareStatement(dscore);
			dscore_ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (avg_cw_ps != null)
				avg_cw_ps.close();
			if (cf_ps != null)
				cf_ps.close();
			if (tval_ps != null)
				tval_ps.close();
			if (ival_ps != null)
				ival_ps.close();
			if (score_ps != null)
				score_ps.close();
			if (riscore_ps != null)
				riscore_ps.close();
			if (ri1score_ps != null)
				ri1score_ps.close();
			if (dscore_ps != null)
				dscore_ps.close();
			if (conn != null)
				conn.close();
		}
	}

	// check if stat is available already and send it to those search engines
	public HashSet<Integer> checkStat(String names) throws SQLException {
		// Check if there are already stats available
		Storetables st = new Storetables();
		Connection conn = null;
		PreparedStatement stat_ps = null;
		ResultSet stat_rs = null;
		ArrayList<String> namesList = new ArrayList<String>(Arrays.asList(names
				.split(" ")));
		HashSet<Integer> count = new HashSet<>();
		StringBuilder se_select = new StringBuilder();
		try {
			conn = st.connects();
			se_select
					.append("Select se_id,sum(score) as s from metasearch,to_tsquery('");
			for (int k = 0; k < namesList.size(); k++) {
				if (k > 0)
					se_select.append("|");
				se_select.append(namesList.get(k));
			}
			se_select
					.append("') q where term @@ q group by se_id order by s desc limit 5");

			stat_ps = conn.prepareStatement(se_select.toString());
			System.out.println("Stat_ps query: " + stat_ps.toString());
			stat_rs = stat_ps.executeQuery();
			while (stat_rs.next()) {
				count.add(stat_rs.getInt(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stat_ps != null)
				stat_ps.close();
			if (stat_rs != null)
				stat_rs.close();
			if (conn != null)
				conn.close();
		}
		System.out.println("count in checkStat : " + count);
		return count;
	}
}
