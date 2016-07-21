package com.searchengine;

import java.util.LinkedHashSet;

public class CrawlerThread {


	static LinkedHashSet<String> secQueue = new LinkedHashSet<String>();

	private int id;

	public CrawlerThread(int id) {

		this.id = id;

	}

	public void run() {

		while (true) {

			System.out.println(id);

			try {

				Thread.sleep(400);

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

		}

	}
}
