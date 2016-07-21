package com.searchengine;

import java.io.IOException;

import java.io.PrintWriter;

import java.sql.SQLException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Adregistration extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public Adregistration() {
		super();
	
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		

		PrintWriter output = response.getWriter();

		output.print("<html><body>");
		String n_gram = request.getParameter("n-gram");
		String adinfo = request.getParameter("adinfo");
		int custid = Integer.parseInt(request.getParameter("custid"));
		String linkURL = request.getParameter("linkURL");
		String imgURL = request.getParameter("imageURL");
		String title = request.getParameter("title");
		Float budget = Float.parseFloat(request.getParameter("Budget"));

		// Printing in console to verify input stored in String and int
		// variables
		System.out.println(" N-gram for which ad needs to be published : " + n_gram);

		System.out.println(" Customer ID : " + custid);

		System.out.println(" Total Budget : " + budget);

		System.out.println(" Advertisement info : " + adinfo);

		System.out.println(" Image URL: " + imgURL);
		System.out.println(" Link URL: " + linkURL);
		System.out.println(" Title: " + title);



		try {
			if(StoreAdIndfo.storeInfo(custid,budget,adinfo,imgURL,linkURL,title,n_gram))
			{
			  output.println("<br><br><br><br>");
			  output.println("<h2 align=centre><p><font color='blue'>Thank you for registering your AD with G&G </font></p></h2>");
			  output.print("</body></html>");
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
		
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		doGet(request, response);
	}

}
