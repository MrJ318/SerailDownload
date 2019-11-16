package com.qixin.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Test2 {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File file = new File("f:\\test.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cell.setCellStyle(style);
		cell.setCellValue(new Random().nextInt());
		workbook.write(new FileOutputStream(file));
		workbook.close();
	}

}

class MyThread extends Thread {

	private Object lock;

	public MyThread(String name, Object lock) {
		super(name);
		this.lock = lock;
	}

	public void run() {
		synchronized (lock) {
			for (int i = 1; i <= 10; i++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + "runing:" + i);
			}
		}
	}
}
