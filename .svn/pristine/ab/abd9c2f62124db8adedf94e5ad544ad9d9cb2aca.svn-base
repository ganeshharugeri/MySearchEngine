/**
 * 
 */
package com.searchengine;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.postgresql.util.Base64;

public class StoreAdIndfo {

	public static boolean storeInfo(int custid, float budget, String adinfo, String imgURL, String linkURL, String title,
			String n_gram) throws ClassNotFoundException, SQLException {
		Storetables a = new Storetables();
		Connection conn = null;
		Boolean checkflag = false;
		conn = a.connects();
		PreparedStatement ps = null, ngram_ps = null, img_ps = null, imgdata_ps = null;

		try {
			if (conn != null) {
				String storeIds = "insert into Adregisterform(custid,title,adinfo,url,budget)  values(?,?,?,?,?)";

				String ngram = "update Adregisterform set n_gram='{" + n_gram + "}' where custid=" + custid;

				ps = conn.prepareStatement(storeIds);
				ngram_ps = conn.prepareStatement(ngram);

				ps.setInt(1, custid);

				ps.setString(2, title);

				ps.setString(3, adinfo);

				ps.setString(4, linkURL);

				ps.setFloat(5, budget);
				
				ps.executeUpdate();

				if (imgURL == null)
					checkflag = true;

				if (imgURL != null) {

					System.out.println(" Entering into image URL" + imgURL);
					String img = "update Adregisterform set imageurl='" + imgURL + "' where custid=" + custid;
					img_ps = conn.prepareStatement(img);
					ngram_ps.executeUpdate();

					URL url = new URL(imgURL);
					InputStream in = new BufferedInputStream(url.openStream());
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					byte[] byteArray = new byte[8192];
					int x = 0;

					while (-1 != (x = in.read(byteArray))) {
						outStream.write(byteArray, 0, x);
					}
					byte[] resp = outStream.toByteArray();
					outStream.close();
					in.close();

					img_ps.executeUpdate();
					String imgdata = "update Adregisterform set img_data='" + Base64.encodeBytes(resp)
							+ "' where custid=" + custid;
					imgdata_ps = conn.prepareStatement(imgdata);
					imgdata_ps.executeUpdate();

				}
				checkflag = true;
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			checkflag = true;
		} finally {
			if (ps != null)
				ps.close();
			if (ngram_ps != null)
				ngram_ps.close();
			if (img_ps != null)
				img_ps.close();
			if (imgdata_ps != null)
				imgdata_ps.close();
			if (conn != null)
				conn.close();
		}
		return checkflag;
	}
}
