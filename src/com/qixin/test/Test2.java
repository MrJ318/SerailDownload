package com.qixin.test;

import org.apache.log4j.Logger;

public class Test2 {
	public static void main(String[] args) {
		new Csq().testMe();
	}

}

class Csq {
	Logger logger = Logger.getLogger(Csq.class);

	public void testMe() {
		logger.debug("123");
		logger.error("123");
		logger.info("123");
	}
}
