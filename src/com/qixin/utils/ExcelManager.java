package com.qixin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.qixin.exception.ReadExcelException;
import com.qixin.model.ExcelFile;
import com.qixin.model.Lou;
import com.qixin.model.TableHead;
import com.qixin.model.TableRecoder;
import com.qixin.model.Xiaoqu;
import com.qixin.model.Zongxian;

/**
 * @author Mr.J
 * @Date 2019/10/9 - 10:22
 */
public class ExcelManager {

	public static Vector<Vector<Object>> readExcel(File file) throws InvalidFormatException, IOException {
		OPCPackage pkg = OPCPackage.open(file);
		XSSFWorkbook excel = new XSSFWorkbook(pkg);// 获取excel工作薄对象
		XSSFSheet sheet0 = excel.getSheetAt(0);// 获取excel工作表对象(sheet)
		Vector<Vector<Object>> readData = new Vector<Vector<Object>>();
		for (int i = 1; i <= sheet0.getLastRowNum(); i++) {
			Vector<Object> rowData = new Vector<Object>();
			Row row = sheet0.getRow(i);
			rowData.add(row.getCell(0).getStringCellValue());
			rowData.add(row.getCell(1).getStringCellValue());
			rowData.add(row.getCell(2).getStringCellValue());
			rowData.add(row.getCell(3).getStringCellValue());
			rowData.add(row.getCell(4).getStringCellValue());
			rowData.add(row.getCell(5).getStringCellValue());
			rowData.add(row.getCell(6).getStringCellValue());
			readData.add(rowData);
		}
		excel.close();
		return readData;
	}

//	private static List<ExcelFile> analyExcel(File file) throws InvalidFormatException, IOException {
//		XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(file));
//		XSSFSheet sheet0 = workbook.getSheetAt(0);
//		List<ExcelFile> list = new ArrayList<ExcelFile>();
//		for (int i = 1; i <= sheet0.getLastRowNum(); i++) {
//			Row row = sheet0.getRow(i);
//			ExcelFile excel = new ExcelFile();
//			excel.setXiaoqu(row.getCell(0).getStringCellValue());
//			excel.setLouhao(row.getCell(1).getStringCellValue());
//			excel.setDanyuan(row.getCell(2).getStringCellValue());
//			excel.setZongxian(row.getCell(3).getStringCellValue());
//			excel.setMenpai(row.getCell(4).getStringCellValue());
//			excel.setCode(row.getCell(5).getStringCellValue());
//			excel.setChangjia(row.getCell(6).getStringCellValue());
//			list.add(excel);
//		}
//		workbook.close();
//		return list;
//	}

	/**
	 * 获取要发送的所有字节数字
	 * 
	 * @param file 要解析的excel文件
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws ReadExcelException
	 */
	public static List<byte[]> getDataBytes(File file) throws InvalidFormatException, IOException, ReadExcelException {

		XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(file));
		XSSFSheet sheet0 = workbook.getSheetAt(0);
		List<ExcelFile> allItems = new ArrayList<ExcelFile>();
		for (int i = 1; i <= sheet0.getLastRowNum(); i++) {
			Row row = sheet0.getRow(i);
			ExcelFile excel = new ExcelFile();
			excel.setXiaoqu(row.getCell(0).getStringCellValue());
			excel.setLouhao(row.getCell(1).getStringCellValue());
			excel.setDanyuan(row.getCell(2).getStringCellValue());
			excel.setZongxian(row.getCell(3).getStringCellValue());
			excel.setMenpai(row.getCell(4).getStringCellValue());
			excel.setCode(row.getCell(5).getStringCellValue());
			excel.setChangjia(row.getCell(6).getStringCellValue());
			allItems.add(excel);
		}
		workbook.close();

		// 循环整个数据表,按小区分组存储
		Map<String, Integer> xiaoqus = new HashMap<String, Integer>();
		for (ExcelFile obj : allItems) {
			xiaoqus.put(obj.getXiaoqu(), 0);
			// String xq = obj.getXiaoqu();
//			if (xiaoqus.containsKey(xq)) {
//				xiaoqus.put(xq, xiaoqus.get(xq) + 1);
//			} else {
//				xiaoqus.put(xq, 1);
//			}
		}

		List<byte[]> listXiaoqu = new ArrayList<byte[]>();
		List<byte[]> listLou = new ArrayList<byte[]>();
		List<byte[]> listZxian = new ArrayList<byte[]>();

//		List<Byte> byteXaiqu = new ArrayList<Byte>();
//		List<Byte> byteLou = new ArrayList<Byte>();
//		List<Byte> byteDanyuan = new ArrayList<Byte>();
//		List<Byte> byteZongxian = new ArrayList<Byte>();

		List<byte[]> fileByteList = new ArrayList<byte[]>();
//		List<byte[]> rcdfileByteList = new ArrayList<byte[]>();

		int start = 0;
		int start1 = 0;
		int start3 = 260;
		for (Map.Entry<String, Integer> xiaoqu : xiaoqus.entrySet()) {

			Map<String, Integer> lous = new HashMap<String, Integer>();
			for (ExcelFile obj : allItems) {
				if (xiaoqu.getKey().equals(obj.getXiaoqu())) {
					lous.put(obj.getLouhao(), 0);
//					String key1 = obj.getLouhao();
//					if (lous.containsKey(key1)) {
//						lous.put(key1, xiaoqu.getValue() + 1);
//					} else {
//						lous.put(key1, 1);
//					}
				}
			}

//			String name = xiaoqu.getKey();
//			byte[] temp = name.getBytes();
//			byte[] recoder = new byte[24];
//			for (int i = 0; i < temp.length; i++) {
//				recoder[i] = temp[i];
//			}
//			recoder[20] = (byte) (start / 256);
//			recoder[21] = (byte) (start % 256);
//			recoder[22] = (byte) (lous.size() / 256);
//			recoder[23] = (byte) (lous.size() % 256);
//			for (int i = 0; i < recoder.length; i++) {
//				byteXaiqu.add(recoder[i]);
//			}

			listXiaoqu.add(ByteUtil.xiaoquToByte(new Xiaoqu(xiaoqu.getKey(), start, xiaoqus.size())));

			start += lous.size();

			for (Map.Entry<String, Integer> lou : lous.entrySet()) {
				Map<String, Integer> zongxians = new HashMap<String, Integer>();
				for (ExcelFile obj : allItems) {
					if (xiaoqu.getKey().equals(obj.getXiaoqu()) && lou.getKey().equals(obj.getLouhao())) {
						zongxians.put(obj.getZongxian(), 0);
//						String key3 = obj.getZongxian();
//						if (zongxians.containsKey(key3)) {
//							zongxians.put(key3, lou.getValue() + 1);
//						} else {
//							zongxians.put(key3, 1);
//						}
					}
				}

//				name = lou.getKey();
//				temp = name.getBytes();
//				recoder = new byte[10];
//				for (int i = 0; i < temp.length; i++) {
//					recoder[i] = temp[i];
//				}
//				recoder[6] = (byte) (start1 / 256);
//				recoder[7] = (byte) (start1 % 256);
//				recoder[8] = (byte) (zongxians.size() / 256);
//				recoder[9] = (byte) (zongxians.size() % 256);
//				for (int i = 0; i < recoder.length; i++) {
//					byteLou.add(recoder[i]);
//				}

				listLou.add(ByteUtil.louToByte(new Lou(lou.getKey(), start1, zongxians.size())));

				start1 += zongxians.size();

				for (Map.Entry<String, Integer> zongxian : zongxians.entrySet()) {

					int zx = Integer.parseInt(zongxian.getKey());
//					recoder = new byte[6];
//					recoder[0] = (byte) (zx >> 24);
//					recoder[1] = (byte) (zx % 16777216 / 65535);
//					recoder[2] = (byte) (zx % 66535 / 256);
//					recoder[3] = (byte) (zx % 256);
//					recoder[4] = (byte) (start3 >> 8);
//					recoder[5] = (byte) (start3 % 256);
//					for (int i = 0; i < recoder.length; i++) {
//						byteZongxian.add(recoder[i]);
//					}

					listZxian.add(ByteUtil.zxianToByte(new Zongxian(zx, start3)));

					List<ExcelFile> listTable = new ArrayList<ExcelFile>();
					for (ExcelFile obj : allItems) {
						if (xiaoqu.getKey().equals(obj.getXiaoqu()) && lou.getKey().equals(obj.getLouhao())
								&& zongxian.getKey().equals(obj.getZongxian())) {
							listTable.add(obj);
						}
					}
					// fileByteList.add(getFileHead(zList, recoder, start3));
//					byte[] by = getFileHead(listTable, recoder, start3);
//					fileByteList.add(branchPackage(by));

					TableHead head = new TableHead();
					head.setXiaoqu(xiaoqu.getKey());
					head.setLouhao(lou.getKey());
					head.setDanyuan(listTable.get(0).getDanyuan());
					head.setLinenum(zongxian.getKey());
					head.setCount(listTable.size() + "");
					fileByteList.add(ByteUtil.headToByte(head, listTable.size()));

					for (int i = 0; i < listTable.size(); i++) {
						TableRecoder recoder = new TableRecoder();
						recoder.setChangjia(listTable.get(i).getChangjia());
						recoder.setAddr(listTable.get(i).getCode());
						recoder.setMenpai(listTable.get(i).getMenpai());
						fileByteList.add(ByteUtil.recoderToByte(recoder, i));
					}
					fileByteList.add(ByteUtil.getSaveByte(start3));
					start3 += 2;
				}
			}
		}
		fileByteList.add(0, listToArray(listXiaoqu, 256));
		fileByteList.add(1, listToArray(listLou, 257));
		fileByteList.add(2, listToArray(listZxian, 258));
		return fileByteList;
	}

//	private static byte[] branchPackage(byte[] b) {
//		byte[] bytes = new byte[73];
//		for (int i = 0; i < 73; i++) {
//			bytes[i] = b[i];
//		}
//		bytes[2] = (byte) ((bytes.length - 7) / 256);
//		bytes[3] = (byte) ((bytes.length - 7) % 256);
//		bytes[4] = (byte) (0x61);
//		bytes[71] = 0;
//		for (int i = 1; i < 71; i++) {
//			bytes[71] += bytes[i];
//		}
//		bytes[72] = (byte) 0x16;
//		return bytes;
//	}

//	private static byte[] branchPackage1(byte[] b, int index) {
//		byte[] bytes = new byte[61];
//		bytes[0] = (byte) 0xFE;
//		bytes[1] = (byte) 0x68;
//		bytes[2] = 0;
//		bytes[3] = (byte) 0x36;
//		bytes[4] = (byte) 0x62;
//		bytes[5] = b[5];
//		bytes[6] = b[6];
//		bytes[7] = (byte) index;
//		for (int i = 0; i < 51; i++) {
//			bytes[8 + i] = b[71 + index * 51 + i];
//		}
//		for (int i = 1; i < 59; i++) {
//			bytes[59] += bytes[i];
//		}
//		bytes[60] = (byte) 0x16;
//		return bytes;
//	}

	/**
	 * 获取每个文件头的字节数字
	 * 
	 * @param obj      当前文头件信息
	 * @param zongxian 总线号
	 * @return 文件头字节数组
	 */
//	private static byte[] getFileHead(List<FileHead1> list, byte[] zongxian, int sect) {
//		byte[] head = null;
//		int k = 0;
//		if (list.get(0).getType().equals("0")) {
//			head = new byte[73 + list.size() * 51];
//			k = 51;
//		} else if (list.get(0).getType().equals("1")) {
//			head = new byte[73 + list.size() * 47];
//			k = 47;
//		}
//
//		head[0] = (byte) (0xFE);
//		head[1] = (byte) (0x68);
//		head[2] = (byte) ((head.length - 7) / 256);
//		head[3] = (byte) ((head.length - 7) % 256);
//		head[4] = (byte) (0x51);
//		head[5] = (byte) (sect / 256);
//		head[6] = (byte) (sect % 256);
//		head[7] = (byte) 0xEA;
//		// head[8] = CRC;
//		head[9] = (byte) (Integer.parseInt(list.get(0).getType()));
//		head[10] = (byte) (Integer.parseInt(list.get(0).getCount()));
//		head[11] = 0;
//
//		for (int j = 0; j < 6; j++) {
//			head[j + 12] = 0;
//		}
//
//		String tmp = list.get(0).getXiaoqu();
//		byte[] temp = tmp.getBytes();
//		for (int i = 0; i < temp.length; i++) {
//			head[i + 18] = temp[i];
//		}
//
//		tmp = list.get(0).getLouhao();
//		temp = tmp.getBytes();
//		for (int i = 0; i < temp.length; i++) {
//			head[i + 38] = temp[i];
//		}
//
//		tmp = list.get(0).getDanyuan();
//		temp = tmp.getBytes();
//		for (int i = 0; i < temp.length; i++) {
//			head[i + 44] = temp[i];
//		}
//
//		for (int i = 0; i < 4; i++) {
//			head[i + 50] = zongxian[i];
//		}
//
//		for (int i = 9; i < 56; i++) {
//			head[8] += head[i];
//		}
//
//		for (int i = 0; i < list.size(); i++) {
//
//			head[71 + i * k] = (byte) 0xEA;
//			// head[72 + i * 51] = (byte) CRC;
//			head[73 + i * k] = (byte) Integer.parseInt(list.get(i).getChangjia());
//			head[74 + i * k] = (byte) Integer.parseInt(list.get(i).getType1());
//
//			String codes = String.format("%08d", Integer.parseInt(list.get(i).getCode()));
//			char[] array = codes.toCharArray();
//			for (int j = 0; j < 8; j++) {
//				if (j % 2 == 0) {
//					head[75 + j / 2 + i * k] = (byte) (Integer.parseInt(array[j] + "") * 16);
//				} else {
//					head[75 + j / 2 + i * k] += (byte) Integer.parseInt(array[j] + "");
//				}
//			}
////			int code = Integer.parseInt(list.get(i).getCode());
////			head[75 + i * k] = (byte) (code / 16777216);
////			head[76 + i * k] = (byte) (code % 16777216 / 65536);
////			head[77 + i * k] = (byte) (code % 65536 / 256);
////			head[78 + i * k] = (byte) (code % 256);
//
//			String men = list.get(i).getMenpai();
//			byte[] tmp1 = men.getBytes();
//			for (int j = 0; j < tmp1.length; j++) {
//				head[82 + j + i * k] = tmp1[j];
//			}
//
//			if (i == 0) {
//				for (int j = 0; j < 28; j++) {
//					head[88 + j + i * k] = 65;
//				}
//			}
//
//			for (int j = 73; j < 122; j++) {
//				head[72 + i * k] += head[j + i * 51];
//			}
//		}
//		for (int i = 1; i < head.length - 2; i++) {
//			head[head.length - 2] += head[i];
////			System.out.println(String.format("%02X", head[i]) + "---" + String.format("%02X", head[50]));
//		}
//		head[head.length - 1] = (byte) 0x16;
//		return head;
//	}

	private static byte[] listToArray(List<byte[]> list, int sect) {
		int size = list.size();
		int len = list.get(0).length;
		int bytesLen = size * len + 9;
		byte[] bytes = new byte[bytesLen];
		bytes[0] = (byte) 0xFE;
		bytes[1] = (byte) 0x68;
		bytes[2] = (byte) ((size * len + 2) >> 8);
		bytes[3] = (byte) (size * len + 2);
		bytes[4] = (byte) 0x60;
		bytes[5] = (byte) (sect >> 8);
		bytes[6] = (byte) sect;
		for (int i = 0; i < size; i++) {
			System.arraycopy(list.get(i), 0, bytes, 7 + i * len, len);
		}
		bytes[bytesLen - 1] = (byte) 0x16;
		bytes[bytesLen - 2] = (byte) 0x00;
		for (int i = 1; i < bytes.length - 2; i++) {
			bytes[bytesLen - 2] += bytes[i];
		}
		return bytes;
	}
//	private static byte[] listToArray(List<Byte> list, int sect) {
//		int size = list.size() + 2;
//		int len = size + 7;
//		byte[] bytes = new byte[len];
//		bytes[0] = (byte) 0xFE;
//		bytes[1] = (byte) 0x68;
//		bytes[2] = (byte) (size / 256);
//		bytes[3] = (byte) (size % 256);
//		bytes[4] = (byte) 0x60;
//		bytes[5] = (byte) (sect / 256);
//		bytes[6] = (byte) (sect % 256);
//		bytes[len - 1] = (byte) 0x16;
//		bytes[len - 2] = (byte) (bytes[1] + bytes[2] + bytes[3] + bytes[4] + bytes[5] + bytes[6]);
//		for (int i = 0; i < size - 2; i++) {
//			bytes[i + 7] = list.get(i);
//			bytes[len - 2] += list.get(i);
//		}
//		return bytes;
//	}

	public static void writeExcelFile(TableHead head, List<TableRecoder> recoders) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		File file = null;
		try {
			file = new File("D:\\table\\" + format.format(new Date()) + ".xlsx");
			XSSFWorkbook excel;
			XSSFSheet sheet;
			Row row;
			if (!file.exists()) {
				excel = new XSSFWorkbook();// 获取excel工作薄对象
				sheet = excel.createSheet();// 获取excel工作表对象(sheet)
				row = sheet.createRow(0);
				row.createCell(0).setCellValue("厂家");
				row.createCell(1).setCellValue("类型");
				row.createCell(2).setCellValue("表码");
				row.createCell(3).setCellValue("门牌");
				row.createCell(4).setCellValue("冷量");
				row.createCell(5).setCellValue("热量");
				row.createCell(6).setCellValue("功率");
				row.createCell(7).setCellValue("流速");
				row.createCell(8).setCellValue("流量");
				row.createCell(9).setCellValue("入口温度");
				row.createCell(10).setCellValue("出口温度");
				row.createCell(11).setCellValue("工作时间");
				row.createCell(12).setCellValue("状态1");
				row.createCell(13).setCellValue("状态2");
				for (int i = 0; i < 14; i++) {
					sheet.setColumnWidth(i, 256 * 12);
				}
				excel.write(new FileOutputStream(file));
				excel.close();

			}
			excel = new XSSFWorkbook(new FileInputStream(file));
			sheet = excel.getSheetAt(0);
			int rowid = sheet.getLastRowNum() + 1;
			row = sheet.createRow(rowid);
			for (int i = 0; i < 14; i++) {
				row.createCell(i);
			}
			CellRangeAddress region = new CellRangeAddress(rowid, rowid, 0, 13);
			sheet.addMergedRegion(region);
			StringBuffer buffer = new StringBuffer();
			buffer.append(head.getXiaoqu() + "  ");
			buffer.append(head.getLouhao() + "号楼  ");
			buffer.append(head.getDanyuan() + "单元  --  ");
			buffer.append("总线号：" + head.getLinenum());
			buffer.append("  抄表数量：" + head.getCount());
			buffer.append("  出错数量：" + head.getErrcnt());

			row.getCell(0).setCellValue(buffer.toString());
			for (int i = 0; i < recoders.size(); i++) {
				row = sheet.createRow(rowid + 1 + i);
				row.createCell(0).setCellValue(recoders.get(i).getChangjia());
				row.createCell(1).setCellValue(recoders.get(i).getType());
				row.createCell(2).setCellValue(recoders.get(i).getAddr());
				row.createCell(3).setCellValue(recoders.get(i).getMenpai());
				row.createCell(4).setCellValue(recoders.get(i).getCold());
				row.createCell(5).setCellValue(recoders.get(i).getHot());
				row.createCell(6).setCellValue(recoders.get(i).getPower());
				row.createCell(7).setCellValue(recoders.get(i).getFlux());
				row.createCell(8).setCellValue(recoders.get(i).getVol());
				row.createCell(9).setCellValue(recoders.get(i).getIntemp());
				row.createCell(10).setCellValue(recoders.get(i).getOuttemp());
				row.createCell(11).setCellValue(recoders.get(i).getWorktime());
				row.createCell(12).setCellValue(recoders.get(i).getState1());
				row.createCell(13).setCellValue(recoders.get(i).getState2());
			}
			excel.write(new FileOutputStream(file));
			excel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
