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
public class ExcelManager {

	public static Vector<Vector<Object>> readExcel(File file) throws InvalidFormatException, IOException {
		OPCPackage pkg = OPCPackage.open(file);
		XSSFWorkbook excel = new XSSFWorkbook(pkg);// ��ȡexcel����������
		XSSFSheet sheet0 = excel.getSheetAt(0);// ��ȡexcel���������(sheet)

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

			// ʱ��
			String tmp = row.getCell(3).getStringCellValue();
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			String[] tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 5] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(3).getStringCellValue());

			// С��
			tmp = row.getCell(4).getStringCellValue();
			tmp = String.format("%40s", tmp);
			tmp = tmp.replaceAll(" ", "0");
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 11] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(4).getStringCellValue());

			// ¥��
			tmp = row.getCell(5).getStringCellValue();
			tmp = String.format("%12s", tmp);
			tmp = tmp.replaceAll(" ", "0");
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 31] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(5).getStringCellValue());

			// ��Ԫ
			tmp = row.getCell(6).getStringCellValue();
			tmp = String.format("%12s", tmp);
			tmp = tmp.replaceAll(" ", "0");
			tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
			tmps = tmp.split(" ");
			for (int j = 0; j < tmps.length; j++) {
				bytes[j + 37] = (byte) (Integer.parseInt(tmps[j]));
			}
			rowData.add(row.getCell(6).getStringCellValue());

			// ���ߺ�
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

	private static List<FileHead1> analyExcel(File file) throws InvalidFormatException, IOException {
		OPCPackage pkg = OPCPackage.open(file);
		XSSFWorkbook excel = new XSSFWorkbook(pkg);// ��ȡexcel����������
		XSSFSheet sheet0 = excel.getSheetAt(0);// ��ȡexcel���������(sheet)

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

			// ʱ��
			head.setDate(row.getCell(3).getStringCellValue());

			// С��
			head.setXiaoqu(row.getCell(4).getStringCellValue());

			// ¥��
			head.setLouhao(row.getCell(5).getStringCellValue());

			// ��Ԫ
			head.setDanyuan(row.getCell(6).getStringCellValue());

			// ���ߺ�
			head.setZongxian(row.getCell(7).getStringCellValue());
			list.add(head);
		}
		excel.close();
		return list;
	}

	/**
	 * ��ȡҪ���͵������ֽ�����
	 * 
	 * @param file Ҫ������excel�ļ�
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public static List<byte[]> getDataBytes(File file) throws InvalidFormatException, IOException {

		List<FileHead1> list = analyExcel(file);

		// ѭ���������ݱ�,��С������洢
		Map<String, Integer> xiaoqus = new HashMap<String, Integer>();
		for (FileHead1 obj : list) {
			String xq = obj.getXiaoqu();
			if (xiaoqus.containsKey(xq)) {
				xiaoqus.put(xq, xiaoqus.get(xq) + 1);
			} else {
				xiaoqus.put(xq, 1);
			}
		}

		List<Byte> byteXaiqu = new ArrayList<Byte>();
		List<Byte> byteLou = new ArrayList<Byte>();
		List<Byte> byteDanyuan = new ArrayList<Byte>();
		List<Byte> byteZongxian = new ArrayList<Byte>();
		List<byte[]> fileByteList = new ArrayList<byte[]>();

		int start = 0;
		int start1 = 0;
		int start2 = 0;
		int start3 = 256;
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
				byteXaiqu.add(recoder[i]);
			}
			start += lous.size();

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
					byteLou.add(recoder[i]);
				}
				start1 += danyuans.size();

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
						byteDanyuan.add(recoder[i]);
					}
					start2 += zongxians.size();

					for (Map.Entry<String, Integer> zongxian : zongxians.entrySet()) {

						int zx = Integer.parseInt(zongxian.getKey());
						recoder = new byte[6];
						recoder[0] = (byte) (zx >> 24);
						recoder[1] = (byte) (zx % 16777216 / 65535);
						recoder[2] = (byte) (zx % 66535 / 256);
						recoder[3] = (byte) (zx % 256);
						recoder[4] = (byte) (start3 >> 8);
						recoder[5] = (byte) (start3 % 256);
						for (int i = 0; i < recoder.length; i++) {
							byteZongxian.add(recoder[i]);
						}

						for (FileHead1 obj : list) {
							if (xiaoqu.getKey().equals(obj.getXiaoqu()) && lou.getKey().equals(obj.getLouhao())
									&& danyuan.getKey().equals(obj.getDanyuan())
									&& zongxian.getKey().equals(obj.getZongxian())) {
								fileByteList.add(getFileHead(obj, recoder, start3));
							}
						}
						start3 += 2;
					}
				}
			}
		}
		fileByteList.add(0, listToArray(byteXaiqu, 256));
		fileByteList.add(1, listToArray(byteLou, 257));
		fileByteList.add(2, listToArray(byteDanyuan, 258));
		fileByteList.add(3, listToArray(byteZongxian, 259));
		return fileByteList;
	}

	/**
	 * ��ȡÿ���ļ�ͷ���ֽ�����
	 * 
	 * @param obj      ��ǰ��ͷ����Ϣ
	 * @param zongxian ���ߺ�
	 * @return �ļ�ͷ�ֽ�����
	 */
	private static byte[] getFileHead(FileHead1 obj, byte[] zongxian, int sect) {
		byte[] head = new byte[54];
		head[0] = (byte) (0xFE);
		head[1] = (byte) (0x68);
		head[2] = (byte) (0x00);
		head[3] = (byte) (0x2D);
		head[4] = (byte) (0x50);
		head[5] = (byte) (sect / 256);
		head[6] = (byte) (sect % 256);
		head[53] = (byte) (0x16);
		head[7] = (byte) (Integer.parseInt(obj.getType()));
		head[8] = (byte) (Integer.parseInt(obj.getCount()));
		head[9] = (byte) (Integer.parseInt(obj.getErrCount()));
		String tmp = obj.getDate();
		tmp = tmp.replaceAll("(.{2})", "$1 ").trim();
		String[] tmps = tmp.split(" ");
		for (int j = 0; j < tmps.length; j++) {
			head[j + 10] = (byte) (Integer.parseInt(tmps[j]));
		}

		tmp = obj.getXiaoqu() + "0";
		byte[] temp = tmp.getBytes();
		int cha = 20 - temp.length;
		for (int i = 0; i < temp.length; i++) {
			head[i + cha + 14] = temp[i];
		}

		tmp = obj.getLouhao();
		temp = tmp.getBytes();
		cha = 6 - temp.length;
		for (int i = 0; i < temp.length; i++) {
			head[i + cha + 34] = temp[i];
		}

		tmp = obj.getDanyuan();
		temp = tmp.getBytes();
		cha = 6 - temp.length;
		for (int i = 0; i < temp.length; i++) {
			head[i + cha + 40] = temp[i];
		}

		for (int i = 0; i < 4; i++) {
			head[i + 46] = zongxian[i];
		}
		for (int i = 1; i < head.length - 2; i++) {
			head[52] += head[i];
//			System.out.println(String.format("%02X", head[i]) + "---" + String.format("%02X", head[50]));
		}
		return head;
	}

	private static byte[] listToArray(List<Byte> list, int sect) {
		int size = list.size();
		byte[] bytes = new byte[size + 9];
		bytes[0] = (byte) 0xFE;
		bytes[1] = (byte) 0x68;
		bytes[2] = (byte) (size / 256);
		bytes[3] = (byte) (size % 256);
		bytes[4] = (byte) 0x50;
		bytes[5] = (byte) (sect / 256);
		bytes[6] = (byte) (sect % 256);
		bytes[size - 1] = (byte) 0x16;
		bytes[size - 2] = (byte) (bytes[1] + bytes[2] + bytes[3] + bytes[4]);
		for (int i = 0; i < size; i++) {
			bytes[i + 7] = list.get(i);
			bytes[size - 2] = list.get(i);
		}
		return bytes;
	}

}
