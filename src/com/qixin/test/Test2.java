package com.qixin.test;

public class Test2 {
	public static void main(String[] args) {
		String string = "ÄãºÃ";
		byte[] bytes = string.getBytes();
		for (byte b : bytes) {
			System.out.println(String.format("%02X", b));
		}
	}

}
