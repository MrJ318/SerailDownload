package com.qixin.exception;

/**
 * 自定义异常类
 * 
 * @author Jevon
 * @date 2019年11月16日 下午4:10:08
 * 
 */
public class ReadExcelException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReadExcelException() {
		super();
	}

	public ReadExcelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ReadExcelException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReadExcelException(String message) {
		super(message);
	}

	public ReadExcelException(Throwable cause) {
		super(cause);
	}

}
