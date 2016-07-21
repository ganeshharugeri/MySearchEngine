package com.searchengine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class N_gram {
	static int maxad=0;

	static ArrayList<String> result = new ArrayList<String>();
	
	//Function to compute Ngram
    public static ArrayList<String> ngrams(String query) throws ClassNotFoundException, SQLException
    	{

    	//Start matching query length and decrement 
        for (int n = query.length(); n >= 1; n--) {
            for (String ngram : computeNgram(query,n))
            {
             //  System.out.println(ngram); //check  for the term is present in Database
               adcheck(ngram);
       
            }

        
        }
        
		return result;
    }
   //Function to split the query terms with respect to ngram factorial
    public static List<String> computeNgram(String Queryterm,int n) 
    {
        List<String> ngrams = new ArrayList<String>();
        String[] query_terms = Queryterm.split(" ");
      
        for (int x = 0; x < query_terms.length - n + 1; x++)
            ngrams.add(concatenate(query_terms, x, x+n));
        return ngrams;
    }

  
    //Function to check if the n_gram is present in AD Registration form
    public static ArrayList<String> adcheck(String ngram) throws ClassNotFoundException, SQLException
    {
    	Storetables a = new Storetables();
		Connection conn = a.connects();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if (conn != null) {
				String max = "select distinct title,adinfo,url,img_data,budget from Adregisterform where '"+ngram +"' = any (n_gram)";
				//System.out.println(ngram);
				ps = conn.prepareStatement(max);
			
				rs = ps.executeQuery();
				while (rs.next())
				{
					
                   if((!result.contains(rs.getString(3)))&& rs.getFloat(5) > 0.01 && maxad < 4)
                   {
                	 
     				  result.add (rs.getString(1));
    				  result.add (rs.getString(2));
    				  result.add (rs.getString(3));
    				  result.add (rs.getString(4));
    				  maxad++;
                   }

			    }
		   }
		} catch (Exception e) {
			// System.err.println(e.getClass().getName() + ": " +
			// e.getMessage());
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
    	
		return result;   	
    }
   //Function to join ngram 
    public static String concatenate(String[] join_ngram, int h, int l) 
    {
        StringBuilder join = new StringBuilder();
        for (int n = h; n < l; n++)
            join.append((n > h ? " " : "") + join_ngram[n]);
        return join.toString();
    }
}
