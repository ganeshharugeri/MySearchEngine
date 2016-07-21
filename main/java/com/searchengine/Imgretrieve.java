package com.searchengine;



import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class Imgretrieve
 */
public class Imgretrieve extends HttpServlet {
	private static final long serialVersionUID = 1L;

 
    public Imgretrieve() {
        super();
 
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		Storetables st = new Storetables();
		ArrayList<String> imgterms = new ArrayList<String>();
		String stemmedkeyword = null;
		
   		Connection conn = null;
        PreparedStatement orps = null;
   		ResultSet rs1 = null;
   		Stemmer stemObj = new Stemmer();
        String image_searchterm=request.getParameter("ImageSearch");
		String[] array = image_searchterm.split(" ");

		for (int j = 0; j < array.length; j++) {

			if (array[j].length() != 1) {
				char[] w = array[j].toCharArray();
				stemObj.add(w, array[j].length());
				stemObj.stem();
				stemmedkeyword = stemObj.toString();
				imgterms.add(stemmedkeyword);
			}
		}
        
   		try {

   			conn = st.connects();
   			response.setContentType("text/html");

   			
   			StringBuilder sqldisplay= new StringBuilder("select distinct img_data,img_url,score from imagesfeatures JOIN imagestables USING (img_id) where terms in (");
   				 for(int i=0 ; i < imgterms.size();i++)
   	              {
   					 if(i > 0 ){
   					     sqldisplay.append(",");
   					 }
   						sqldisplay.append("?");
   	              }
   				        sqldisplay.append(") order by score  desc limit 10");
   				        

   			     orps = conn.prepareStatement(sqldisplay.toString());
   			     

 				// System.out.println("OR query: " + orquery);
 				for (int m = 0; m < imgterms.size(); m++) {
 					// System.out.println("Key values : " + keys.get(m));
 					orps.setString(m + 1, imgterms.get(m));
 					System.out.println(imgterms.get(m));
 				}
 
   			     PrintWriter outs= response.getWriter();
   			     rs1 = orps.executeQuery();
 
       
             outs.println("<html><body>");
  			//ServletOutputStream output = response.getOutputStream();
            outs.println("<div id=header><h2 align=centre> G & G Image Search Results </h2> </div>");

   			 while(rs1.next())
   			 { 
               String checkformat= rs1.getString(2);
               System.out.println(checkformat.length());
               String version= checkformat.substring(checkformat.length()-3,checkformat.length());
               System.out.println("Version is : "+version);
               if(!version.equals("svg"))
               {
   				String bt=rs1.getString(1);
    				outs.println("<a href="+rs1.getString(2) +">"+ "<img src=\"data:image/png;base64,"+bt +"\""
       				 		+ " style=\"width:350px;height:250px;padding:10px\""
       				 		+ "/> </a>");
    				
   			   }
             
   		   }
   		}
   		catch (ClassNotFoundException | SQLException e)
   		{
			
			e.printStackTrace();
		}

	}
      protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		doGet(request, response);
	}

}
