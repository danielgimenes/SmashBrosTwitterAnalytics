package br.com.dgimenes.smashbrostwitterstreamprocessor.util;

import java.util.Calendar;
import java.util.Date;

public class Logger {
	public static void info(String message, Class javaClass) {
		printMessage(message, javaClass, LogLevel.INFO);
	}

	public static void warn(String message, Class javaClass) {
		printMessage(message, javaClass, LogLevel.WARN);
	}

	public static void error(String message, Class javaClass) {
		printMessage(message, javaClass, LogLevel.ERROR);
	}

	public static void debug(String message, Class javaClass) {
		// printMessage(message, javaClass, LogLevel.DEBUG);
	}

	private static void printMessage(String message, Class javaClass, LogLevel logLevel) {
		Date now = Calendar.getInstance().getTime();
		System.out.println(String.format("[%1$tF %1$tT] [%2$s] - %3$s - %4$s", now, logLevel,
				javaClass.getSimpleName(), message));
	}
}
