//package com.searchengine;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.SocketTimeoutException;
//import java.sql.SQLException;
//import java.util.LinkedHashSet;
//import java.util.Queue;
//
//public class CrawlerThread {
//
//	static LinkedHashSet<String> secQueue = new LinkedHashSet<String>();
//
//	private int id;
//
//	public CrawlerThread(int id) {
//
//		this.id = id;
//
//	}
//
//	public void run() {
//
//		while (primQueue.is]) {
//				System.out.println("************primQueue is empty*************");
//				while (!secQueue.isEmpty() && depth <= Max_depth
//						&& parsed.size() < Max_Num_Doc) {
//					try{
//					String secURL = secQueue.poll();
//					secURL = trimURL(secURL);
//					int id = st.storetotemp(secURL, docid);
//					//System.out.println("id in else if: "+id);
//					if(id>=docid)
//						docid++;
//					if (index.parse(secURL, id)){
//						System.out.println(secURL+" is Parsed.");
//						String urlDomain = checkDomain(secURL);
//						if (urlDomain.equalsIgnoreCase(hostDomain) || response) {	
//							holder.addAll(getLinksFromSite(secURL));
//							primQueue.addAll(removeRedundant(holder, parsed));
//							for (String str:primQueue){
//								str = trimURL(str);
//								int oid = st.storetotemp(str, docid);
//								//System.out.println("oid in else if: "+oid);
//								if(oid>=docid)
//									docid++;
//								st.storeLinks(id,oid);
//								}
//						}
//						String parsedtest = parsed.toArray()[parsed.size()-1].toString();
//						if(!secURL.equalsIgnoreCase(parsedtest)){		
//							int rid = st.storetotemp(secURL, id);
//							//System.out.println("id in else if: "+rid);
//							if(rid>=docid)
//								docid++;
//							st.storeinparsed(secURL,rid);
//							}
//						parsed.add(secURL);
//					}
//					System.out.println("ALL tempQueue contents: "+primQueue.size() +" \n Parsed size: "+parsed.size());
//					holder.clear();
//					}
//					catch (SocketTimeoutException exception) {
//						continue;
//					}
//					catch (MalformedURLException me) {
//			            System.err.println("MalformedURLException: " + me);
//			        } catch (IOException ioe) {
//			            System.err.println("IOException: " + ioe);
//			        }
//					
//				}
//			}
//
//			try {
//
//				Thread.sleep(400);
//
//			} catch (InterruptedException e) {
//
//				e.printStackTrace();
//
//			}
//
//		}
//
//	}
//}