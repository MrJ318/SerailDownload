package com.qixin.exception;

/**
 * �Զ����쳣��
 * 
 * @author Jevon
 * @date 2019��11��16�� ����4:10:08
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
