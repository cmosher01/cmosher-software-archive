package com.surveysampling.logging;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Logger;

public final class LoggingUtil {
	private LoggingUtil() {
		throw new IllegalStateException();
	}
	public static void dumpLoggersWithParents(Logger l, PrintStream s) {
		while (l != null) {
			dumpLogger(l,s);
			l = l.getParent();
		}
	}
	public static void dumpLogger(Logger l, PrintStream s) {
		s.println("-logger: "+l.toString());
		s.flush();
		Handler[] handlers = l.getHandlers();
		if (handlers != null) {
			for (final Handler handlerx : handlers) {
				s.println("Handler: "+handlerx.toString());
			}
		}
		s.println("-end logger: "+l.toString());
		s.flush();
	}
}
