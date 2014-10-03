package br.com.dgimenes.smashbrostwitterstreamprocessor.util;

public class Utils {

	public static void sleepSeconds(long seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
