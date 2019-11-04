package com.qixin.test;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.ArrayUtil;

import com.qixin.service.ExcelManager;

public class Test {
	public static void main(String[] args) {
		try {
			ExcelManager.getDataBytes(new File("F:\\tt.xlsx"));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
