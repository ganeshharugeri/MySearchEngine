package com.searchengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Scanner;
//import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class Jsonresult extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
//	private static Date date_last_update = new Date();
//	private Map<String, Date> query_period = new ConcurrentHashMap<String, Date>();
	static Storetables st = new Storetables();
//	private static int check_querycnt = 0;

	public Jsonresult() {
		super();

	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

//		Boolean securityflag = false;
		// Call Stemmer function
		Stemmer stemobj = new Stemmer();
		// Initializing JSONArray and JSONObject
		JSONObject root = new JSONObject();
		JSONArray resultListArray = new JSONArray();
		JSONArray statarray = new JSONArray();
		// Printer object to print the JSON results in the web console
		PrintWriter output = response.getWriter();
		String query = null;
		int limit = 0;
		Connection conn = null;
//		Date date_current = new Date();
		// Retreive input from JSON.jsp
		query = request.getParameter("jsonsearch");
		limit = Integer.parseInt(request.getParameter("json_no_of_docs"));
		// Securing our interface with limited users
		/*long time_elapsed = date_current.getTime() - date_last_update.getTime();
		if (time_elapsed <= 1000) {
			// Maximum query count per second
			if (check_querycnt <= 10) {
				securityflag = true;
				check_querycnt++;
			}
		} else {
			date_last_update = date_current;
			securityflag = true;
			check_querycnt = 0;

		}
		// Securing the interface by providing access to single IP
		if (securityflag) {
			if (query_period.containsKey(request.getRemoteAddr())) {
				Date same_ip_timecheck = query_period.get(request
						.getRemoteAddr());
				long timeelapsed_forsingleIP = date_current.getTime()
						- same_ip_timecheck.getTime();
				if (timeelapsed_forsingleIP > 1000) {
					securityflag = true;
					query_period.put(request.getRemoteAddr(), date_current);
				} else {
					System.out
							.println("Same IP Address already in process, Please try after 1 second !!");
					securityflag = false;
				}
			} else {
				query_period.put(request.getRemoteAddr(), date_current);
				securityflag = true;
			}
		}*/
		// If it passes secure interface condition then enter to fetch the JSON
		// result format
		//if (securityflag) {
			try {
				// Store the entered query string one by one in an ArrayList
				ArrayList<String> keys = new ArrayList<String>();
				// System.out.println("Keys Size: >>" + keys.size());
				String stemmedkeyword = null;
				String[] array = query.split(" ");
				for (int j = 0; j < array.length; j++) {
					if (array[j].length() != 1) {
						char[] w = array[j].toCharArray();
						stemobj.add(w, array[j].length());
						stemobj.stem();
						stemmedkeyword = stemobj.toString();
						keys.add(stemmedkeyword);
						// System.out.println("test stem:" + stemmedkeyword);
					}
				}

				// Getting Connection to DB from Storetables Class
				conn = st.connects();

				// Default mode-Disjunctive query
				if (conn != null) {
					// Disjunctive query processing
					StringBuilder orquery = new StringBuilder(
							"select distinct(url),fs.tf_idf_score from documents d,features fs where fs.term = ");
					for (int i = 0; i < keys.size(); i++) {
						if (i > 0) {
							orquery.append(" or fs.term =");
						}
						orquery.append("?");
					}
					orquery.append("and fs.docid= d.docid order by tf_idf_score desc;");
					PreparedStatement orps = conn.prepareStatement(orquery
							.toString());
					System.out.println("OR query: " + orquery);
					for (int m = 0; m < keys.size(); m++) {
						orps.setString(m + 1, keys.get(m));
					}
					ResultSet orrs = orps.executeQuery();
					int z = 1;
					while (orrs.next() && z <= limit) {
						// Storing the Disjunctive query result in JSONObject
						JSONObject obj = new JSONObject();
						String url = orrs.getString(1);
						
						obj.put("rank", z);
						obj.put("url", url);
						obj.put("score", orrs.getFloat(2));
						// Adding the result list to JSONArray object
						resultListArray.add(obj);
						z++;
					}

					// Computing document frequency for each term

					System.out
							.println("Entering to compute Document Frequency:");
					for (int i = 0; i < keys.size(); i++) {
						StringBuilder queryterms = new StringBuilder(
								"select count(*) from features fs where fs.term = ?");
						PreparedStatement termcountps = conn
								.prepareStatement(queryterms.toString());

						termcountps.setString(1, keys.get(i));
						ResultSet crps = termcountps.executeQuery();
						while (crps.next()) {
							// Storing the term and its corresponding document
							// frequency in JSONObject
							JSONObject obj2 = new JSONObject();
							obj2.put("term", keys.get(i));
							obj2.put("df", crps.getInt(1));
							// Adding the result list to JSONArray object
							statarray.add(obj2);
						}
					}

					// Computing collection frequency

					System.out.println("Entering to compute Collection Frequency:");
					
						StringBuilder cw = new StringBuilder(
								"select sum(term_freq) from features ");
						PreparedStatement cwps = conn.prepareStatement(cw
								.toString());

						
						ResultSet cwrs = cwps.executeQuery();


					JSONObject obj4 = new JSONObject();
					obj4.put("k", limit);
					obj4.put("query", query);
					// Adding the result list to JSONArray object
					

					// Inserting all JSONArrayList to JSONObject named root
					root.put("resultList", resultListArray);
					root.put("query", obj4);
					root.put("k", limit);
					root.put("stat", statarray);
					
					while (cwrs.next()) {
						// Storing the collection frequency in JSONObject
						
						root.put("cw", cwrs.getInt(1));

					}
					

					System.out.println("JSON Format :\n");
					System.out.println(root.toJSONString().replaceAll("\\\\/","/"));
					StringWriter outn = new StringWriter();
					
					root.toJSONString().replaceAll("\\\\/","/");
					root.writeJSONString(outn);
					// The printwriter object from response writes the result
					// storing json object to the output stream.
					output.println(outn.toString().replaceAll("\\\\/","/"));
					response.setContentType("application/json");

				}

			} catch (NullPointerException e) {
				System.out.println("Check the connection");
			} catch (Exception e) {
				e.printStackTrace();
			}
//		} else {
//			output.println("Please try again later !! Number of HTTP Request exceeded");
//
//		}

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}

}
