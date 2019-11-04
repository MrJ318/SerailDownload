package com.qixin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.qixin.model.FileHead1;

/**
 * @author Mr.J
 * @Date 2019/10/9 - 10:22
 */
public class ExcelManager_back1029 {

	public static Vector<Vector<Object>> readExcel(File file) throws InvalidFormatException, IOException {
		OPCPackage pkg = OPCPackage.open(file);
		XSSFWorkbook excel = new XSSFWorkbook(pkg);// 获取excel工作薄对象
		XSSFSheet sheet0 = excel.getSheetAt(0);// 获取excel工作表对象(sheet)

		Vector<Vector<Object>> readData = new Vector<Vector<Object>>();
		for (int i = 1; i <= sheet0.getLastRowNum(); i++) {
			Vector<Object> rowData = new Vector<Object>();
			Row row = sheet0.getRow(i);
			byte[] bytes = new byte[47];
			// flag
			bytes[0] = (byte) 0xEA;
			// type
			bytes[2] = (byte) (Integer.parseInt(row.getCell(0).getStringCellValue()));
			rowData.add(row.getCell(0).getStringCellValue());
			// count
			bytes[3] = (byte) (Integer.parseInt(row.getCell(1).getStringCellValue()));
			rowData.add(row.getCell(1).getStringCellValue());
			// error_count
			bytes[4] = (byte) (Integer.parseInt(row.getCell(2).getStringCellValue()));
			rowData.add(row.getCell(2).getStringCellValue());

			// 时间
			String tmp = row.getCell(3).getStringCellValue();
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			String[] tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 5] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(3).getStringCellValue());

			// 小区
			tmp = row.getCell(4).getStringCellValue();
			tmp = String.format("%40s", tmp);
			tmp = tmp.replaceAll(" ", "0");
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 11] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(4).getStringCellValue());

			// 楼号
			tmp = row.getCell(5).getStringCellValue();
			tmp = String.format("%12s", tmp);
			tmp = tmp.replaceAll(" ", "0");
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 31] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(5).getStringCellValue());

			// 单元
			tmp = row.getCell(6).getStringCellValue();
			tmp = String.format("%12s", tmp);
			tmp = tmp.replaceAll(" ", "0");
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 37] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(6).getStringCellValue());

			// 总线号
			tmp = row.getCell(7).getStringCellValue();
			tmp = String.format("%8s", tmp);
			tmp = tmp.replaceAll(" ", "0");
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 43] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(7).getStringCellValue());

			// crc
			for (int j = 2; j < 47; j++) {
				bytes[1] += bytes[j];
			}
			rowData.add(bytes);
			readData.add(rowData);
		}
		excel.close();
		return readData;
	}

	public static List<FileHead1> analyExcel(File file) throws InvalidFormatException, IOException {
		OPCPackage pkg = OPCPackage.open(file);
		XSSFWorkbook excel = new XSSFWorkbook(pkg);// 获取excel工作薄对象
		XSSFSheet sheet0 = excel.getSheetAt(0);// 获取excel工作表对象(sheet)

		List<FileHead1> list = new ArrayList<FileHead1>();
		for (int i = 1; i <= sheet0.getLastRowNum(); i++) {
			Row row = sheet0.getRow(i);
			FileHead1 head = new FileHead1();
			// type
			head.setType(row.getCell(0).getStringCellValue());
			// count
			head.setCount(row.getCell(1).getStringCellValue());
			// error_count
			head.setErrCount(row.getCell(2).getStringCellValue());

			// 时间
			head.setDate(row.getCell(3).getStringCellValue());

			// 小区
			head.setXiaoqu(row.getCell(4).getStringCellValue());

			// 楼号
			head.setLouhao(row.getCell(5).getStringCellValue());

			// 单元
			head.setDanyuan(row.getCell(6).getStringCellValue());

			// 总线号
			head.setZongxian(row.getCell(7).getStringCellValue());
			list.add(head);
		}
		excel.close();
		return list;
	}

	public static void getDataBytes(File file) throws InvalidFormatException, IOException {

		long starts = System.currentTimeMillis();
		List<FileHead1> list = analyExcel(file);

		Map<String, Integer> xiaoqus = new HashMap<String, Integer>();
		for (FileHead1 obj : list) {
			String xq = obj.getXiaoqu();
			if (xiaoqus.containsKey(xq)) {
				xiaoqus.put(xq, xiaoqus.get(xq) + 1);
			} else {
				xiaoqus.put(xq, 1);
			}
		}
		byte[] byteXaiqu = new byte[7 + 24 * xiaoqus.size()];
		byte[] byteLou = new byte[1024];
		byte[] byteDanyuan = new byte[1024];
		byte[] byteZongxian = new byte[1024];

		int start = 0;
		int start1 = 0;
		int start2 = 0;
		int start3 = 256;
		int flag1 = 0;
		int flag2 = 0;
		int flag3 = 0;
		int flag4 = 0;
		for (Map.Entry<String, Integer> xiaoqu : xiaoqus.entrySet()) {
			Map<String, Integer> lous = new HashMap<String, Integer>();
			for (FileHead1 obj : list) {
				if (xiaoqu.getKey().equals(obj.getXiaoqu())) {
					String key1 = obj.getLouhao();
					if (lous.containsKey(key1)) {
						lous.put(key1, xiaoqu.getValue() + 1);
					} else {
						lous.put(key1, 1);
					}
				}
			}

			String name = xiaoqu.getKey() + "0";
			byte[] temp = name.getBytes();
			byte[] recoder = new byte[24];
			int cha = recoder.length - temp.length - 4;
			for (int i = 0; i < temp.length; i++) {
				recoder[i + cha] = temp[i];
			}
			recoder[20] = (byte) (start / 256);
			recoder[21] = (byte) (start % 256);
			recoder[22] = (byte) (lous.size() / 256);
			recoder[23] = (byte) (lous.size() % 256);
			for (int i = 0; i < recoder.length; i++) {
				byteXaiqu[5 + flag1 * 24 + i] = recoder[i];
			}

			System.out.println(xiaoqu.getKey() + "--" + start + "--" + lous.size());
			start += lous.size();
			flag1++;

			for (Map.Entry<String, Integer> lou : lous.entrySet()) {
				Map<String, Integer> danyuans = new HashMap<String, Integer>();
				for (FileHead1 obj : list) {
					if (xiaoqu.getKey().equals(obj.getXiaoqu()) && lou.getKey().equals(obj.getLouhao())) {
						String key2 = obj.getDanyuan();
						if (danyuans.containsKey(key2)) {
							danyuans.put(key2, xiaoqu.getValue() + 1);
						} else {
							danyuans.put(key2, 1);
						}
					}
				}
				name = lou.getKey();
				temp = name.getBytes();
				recoder = new byte[10];
				cha = recoder.length - temp.length - 4;
				for (int i = 0; i < temp.length; i++) {
					recoder[i + cha] = temp[i];
				}
				recoder[6] = (byte) (start1 / 256);
				recoder[7] = (byte) (start1 % 256);
				recoder[8] = (byte) (danyuans.size() / 256);
				recoder[9] = (byte) (danyuans.size() % 256);
				for (int i = 0; i < recoder.length; i++) {
					byteLou[5 + flag2 * 10 + i] = recoder[i];
				}

				System.out.println("\t" + lou.getKey() + "--" + start1 + "--" + danyuans.size());
				start1 += danyuans.size();
				flag2++;

				for (Map.Entry<String, Integer> danyuan : danyuans.entrySet()) {
					Map<String, Integer> zongxians = new HashMap<String, Integer>();
					for (FileHead1 obj : list) {
						if (xiaoqu.getKey().equals(obj.getXiaoqu()) && lou.getKey().equals(obj.getLouhao())
								&& danyuan.getKey().contentEquals(obj.getDanyuan())) {
							String key3 = obj.getZongxian();
							if (zongxians.containsKey(key3)) {
								zongxians.put(key3, danyuan.getValue() + 1);
							} else {
								zongxians.put(key3, 1);
							}
						}
					}

					name = danyuan.getKey();
					temp = name.getBytes();
					recoder = new byte[10];
					cha = recoder.length - temp.length - 4;
					for (int i = 0; i < temp.length; i++) {
						recoder[i + cha] = temp[i];
					}
					recoder[6] = (byte) (start2 / 256);
					recoder[7] = (byte) (start2 % 256);
					recoder[8] = (byte) (zongxians.size() / 256);
					recoder[9] = (byte) (zongxians.size() % 256);
					for (int i = 0; i < recoder.length; i++) {
						byteDanyuan[5 + flag3 * 10 + i] = recoder[i];
					}

					System.out.println("\t\t" + danyuan.getKey() + "--" + start2 + "--" + zongxians.size());
					start2 += zongxians.size();
					flag3++;

					for (Map.Entry<String, Integer> zongxian : zongxians.entrySet()) {
//						Map<String, Integer> f = new HashMap<String, Integer>();
//						for (FileHead1 obj : list) {
//							if (xiaoqu.getKey().equals(obj.getXiaoqu()) && lou.getKey().equals(obj.getLouhao())
//									&& danyuan.getKey().equals(obj.getDanyuan())
//									&& zongxian.getKey().equals(obj.getZongxian())) {
//								String key4 = obj.getZongxian();
//								if (f.containsKey(key4)) {
//									f.put(key4, zongxian.getValue() + 1);
//								} else {
//									f.put(key4, 1);
//								}
//							}
//						}

						int zx = Integer.parseInt(zongxian.getKey());
						recoder = new byte[6];
						recoder[0] = (byte) (zx >> 24);
						recoder[1] = (byte) (zx % 16777216 / 65535);
						recoder[2] = (byte) (zx % 66535 / 256);
						recoder[3] = (byte) (zx % 256);
						recoder[4] = (byte) (start3 >> 8);
						recoder[5] = (byte) (start3 % 256);
						for (int i = 0; i < recoder.length; i++) {
							byteZongxian[5 + flag4 * 6 + i] = recoder[i];
						}

						System.out.println("\t\t\t" + zongxian.getKey() + "--" + start3);
						start3 += 2;
						flag4++;
					}
				}
			}

		}
		long end = System.currentTimeMillis();
		System.out.println(end - starts);
	}

}
