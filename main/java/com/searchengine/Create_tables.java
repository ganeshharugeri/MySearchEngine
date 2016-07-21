package com.searchengine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Create_tables {
	// Function to create tables
	public void create() {
		Connection conn = null;
		Statement stmt = null;

		try {
			// Accessing postgres driver class
			Class.forName("org.postgresql.Driver");
			// JDBC URL for connecting with user credentials
			String dbURL = "jdbc:postgresql://localhost:5432/postgres";
			String user = "postgres";
			String pass = "isproject";
			conn = DriverManager.getConnection(dbURL, user, pass);

			if (conn != null) {
				// Create table if not exists the table
				stmt = conn.createStatement();
				String createfeaturessql = "CREATE TABLE IF NOT EXISTS features(docid integer,term text,language text,abs_term_freq integer,term_freq float,doc_freq integer,idf float,tf_idf_score float,crawled_date date,doclen float,doclen_by_avgdoclen float,bm25_idf float,bm25_score float,pr_score float,normalised_bm25score float, normalised_prscore float,combined_score float)";
				String createdocumentsssql = "CREATE TABLE IF NOT EXISTS documents(docid integer,url text,crawled_date date)";
				String createtemptable = "CREATE TABLE IF NOT EXISTS temptable(docid integer,url text)";
				String linkstable = "CREATE TABLE IF NOT EXISTS links(from_docid integer,to_docid integer)";
				String parsedtable = "CREATE TABLE IF NOT EXISTS parsedtable(docid integer,url text)";
				String snippettable = "CREATE TABLE IF NOT EXISTS snippets(docid integer,title text,pagecontent text, vector tsvector,shingles text[], hashshingles text[], min1 text[],min4 text[],min16 text[],min32 text[] )";
				String jaccardtable = "CREATE TABLE IF NOT EXISTS jaccardsim(from_docid integer,to_docid integer,jaccard_sim float, min1 float,min4 float,min16 float,min32 float)";
				String simresults = "CREATE TABLE IF NOT EXISTS simresults(measuretype text, jaccardsim float,min1sim float, min4sim float,min16sim float,min32sim float ) ";
				// Image tables
				String imagestable = "CREATE TABLE IF NOT EXISTS imagestables(img_id integer,img_data text,img_url text)";
				String imagefeatures = "CREATE TABLE IF NOT EXISTS imagesfeatures(docid integer,img_id integer,terms text,score text)";

				
				//Metasearchtables
				String metasearch = "CREATE TABLE IF NOT EXISTS metasearch (se_id integer,term text,df float,cw float, cf float, avg_cw float,Tval float,Ival float,score float)";
				
				//MetaUrldetails
				String MetaUrldetails = "CREATE TABLE IF NOT EXISTS MetaUrldetails (se_id integer ,term text, url text, urlscore float,riscore float,rmax float, ri1score float,dscore float)";
				
				// User defined functions
				String hex_to_bigint = "CREATE OR REPLACE FUNCTION hex_to_bigint(text) returns bigint as $$ select ('x'||substr(md5($1),1,16))::bit(64)::bigint; $$ language sql";
				String findDuplicateDocs = "CREATE OR REPLACE FUNCTION findDuplicate(src_docid integer,  threshold float) RETURNS TABLE(duplicateDocs integer, JaccardSim float) AS 'select to_docid,jaccard_sim from jaccardsim where (from_docid=src_docid and jaccard_sim >=threshold) union select from_docid,jaccard_sim from jaccardsim where (to_docid = src_docid and jaccard_sim >=threshold);' LANGUAGE SQL VOLATILE RETURNS NULL ON NULL INPUT";
				
				//Ad-registration form
				String Adregister = "CREATE TABLE IF NOT EXISTS Adregisterform(custid integer,title text,n_gram text[],adinfo text,url text,budget float,imageURL text,img_data text)";
				
				// Execute the corresponding the create table query
				stmt.executeUpdate(createfeaturessql);
				stmt.executeUpdate(createdocumentsssql);
				stmt.executeUpdate(createtemptable);
				stmt.executeUpdate(linkstable);
				stmt.executeUpdate(parsedtable);
				stmt.executeUpdate(snippettable);
				stmt.executeUpdate(jaccardtable);
				stmt.executeUpdate(hex_to_bigint);
				stmt.executeUpdate(findDuplicateDocs);
				stmt.executeUpdate(simresults);
				stmt.executeUpdate(imagestable);
				stmt.executeUpdate(imagefeatures);
				stmt.executeUpdate(Adregister);
				stmt.executeUpdate(MetaUrldetails);
				stmt.executeUpdate(metasearch);
				// stmt.executeUpdate(index);

			}
			stmt.close();
			conn.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

}
