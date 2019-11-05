package com.qixin.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.qixin.service.ExcelManager;

public class Test2 {
	public static void main(String[] args) {
		try {
			List<byte[]> sendList = ExcelManager.getDataBytes(new File("F:\\tt.xlsx"));
			byte[] bytes = sendList.get(0);
			byte b = 0;
			for (int i = 0; i < bytes.length; i++) {
				System.out.print(String.format("%02X", bytes[i]) + " ");
			}
			for (int i = 1; i < bytes.length - 2; i++) {
				b += bytes[i];
			}
			System.out.println(String.format("%02X", b));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
