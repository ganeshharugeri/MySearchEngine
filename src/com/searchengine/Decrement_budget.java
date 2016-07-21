package com.searchengine;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Decrement_budget
 */
@WebServlet("/Decrement_budget")
public class Decrement_budget extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public Decrement_budget() {
        super();
    }

    // Get the URL parameter onclick
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	
		
		   String message= request.getParameter("advertise_url");
		   System.out.println("The URL is "+ message);

		   //Call function to decrement the budget
		   try {
			decrementBudget(message);
		} catch (ClassNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		   //Redirect the response of the clicked URL 
		   response.sendRedirect(response.encodeRedirectURL(request.getParameter("advertise_url")));


	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		 
			doGet(request, response);
	
	}
    //Function to decrement budget based on the URL clicked
	public static void decrementBudget(String url) throws SQLException, ClassNotFoundException
	{
		   Storetables a = new Storetables();
		   Connection conn = null;
		   conn = a.connects();
		   PreparedStatement ps = null,ps1=null;
		   ResultSet rs=null;
		   Float budget=(float) 0.0;
		   
		   try {
				if (conn != null) {
					//Get the budget of the clicked URL
					String get_budget = "select budget from Adregisterform where url=?";

					ps = conn.prepareStatement(get_budget);
					ps.setString(1, url);
					rs=ps.executeQuery();
					float decbudget= 0;
					while(rs.next())
					{
						budget=rs.getFloat(1);
						decbudget= (float) (budget - 0.01);
						System.out.println("Balance of URL : "+url+" before click : "+budget);
						
					}
					
					System.out.println("Balance of URL : "+url+" after click : "+decbudget);
					
					//Decrement the budget of the clicked URL
					String dec_budget = "update Adregisterform set budget='"+decbudget+"'where url=?";
					
					ps1=conn.prepareStatement(dec_budget);
					ps1.setString(1, url);
                    ps1.executeUpdate();
					
				}
	} catch (Exception e) {
		System.err.println(e.getClass().getName() + ": " + e.getMessage());

	} finally {
		if (ps != null)
			ps.close();

		if (ps1 != null)
			ps1.close();
	
		if (conn != null)
			conn.close();
	}
	}
}
