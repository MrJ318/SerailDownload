package com.qixin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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
 * excel读写工具
 * 
 * @author Jevon
 * @date 2019年11月16日 下午4:10:08
 * 
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
			String tmp = row.getCell(5).getStringCellValue();
			if (tmp == null || tmp.equals("")) {
				continue;
			}
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
			String tmp = row.getCell(5).getStringCellValue();
			if (tmp == null || tmp.equals("")) {
				continue;
			}
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

		}

		List<byte[]> listXiaoqu = new ArrayList<byte[]>();
		List<byte[]> listLou = new ArrayList<byte[]>();
		List<byte[]> listZxian = new ArrayList<byte[]>();

		List<byte[]> fileByteList = new ArrayList<byte[]>();

		int start = 0;
		int start1 = 0;
		int start3 = 260;
		for (Map.Entry<String, Integer> xiaoqu : xiaoqus.entrySet()) {

			Map<String, Integer> lous = new HashMap<String, Integer>();
			for (ExcelFile obj : allItems) {
				if (xiaoqu.getKey().equals(obj.getXiaoqu())) {
					lous.put(obj.getLouhao(), 0);

				}
			}

			listXiaoqu.add(ByteUtil.xiaoquToByte(new Xiaoqu(xiaoqu.getKey(), start, xiaoqus.size())));

			start += lous.size();

			for (Map.Entry<String, Integer> lou : lous.entrySet()) {
				Map<String, Integer> zongxians = new HashMap<String, Integer>();
				for (ExcelFile obj : allItems) {
					if (xiaoqu.getKey().equals(obj.getXiaoqu()) && lou.getKey().equals(obj.getLouhao())) {
						zongxians.put(obj.getZongxian(), 0);

					}
				}

				listLou.add(ByteUtil.louToByte(new Lou(lou.getKey(), start1, zongxians.size())));

				start1 += zongxians.size();

				for (Map.Entry<String, Integer> zongxian : zongxians.entrySet()) {

					int zx = Integer.parseInt(zongxian.getKey());

					listZxian.add(ByteUtil.zxianToByte(new Zongxian(zx, start3)));

					List<ExcelFile> listTable = new ArrayList<ExcelFile>();
					for (ExcelFile obj : allItems) {
						if (xiaoqu.getKey().equals(obj.getXiaoqu()) && lou.getKey().equals(obj.getLouhao())
								&& zongxian.getKey().equals(obj.getZongxian())) {
							listTable.add(obj);
						}
					}

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

	public static void writeExcelFile(TableHead head, List<TableRecoder> recoders, File file)
			throws FileNotFoundException, IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		file = new File(file, sdf.format(new Date()) + ".xlsx");
		XSSFWorkbook excel;
		XSSFSheet sheet;
		Row row;
		XSSFCellStyle style;
		if (!file.exists()) {
			excel = new XSSFWorkbook();
			sheet = excel.createSheet();
			row = sheet.createRow(0);
			style = excel.createCellStyle();
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
			row.createCell(0).setCellValue("厂家");
			row.getCell(0).setCellStyle(style);
			row.createCell(1).setCellValue("类型");
			row.getCell(1).setCellStyle(style);
			row.createCell(2).setCellValue("表码");
			row.getCell(2).setCellStyle(style);
			row.createCell(3).setCellValue("门牌");
			row.getCell(3).setCellStyle(style);
			row.createCell(4).setCellValue("冷量");
			row.getCell(4).setCellStyle(style);
			row.createCell(5).setCellValue("热量");
			row.getCell(5).setCellStyle(style);
			row.createCell(6).setCellValue("功率");
			row.getCell(6).setCellStyle(style);
			row.createCell(7).setCellValue("流速");
			row.getCell(7).setCellStyle(style);
			row.createCell(8).setCellValue("流量");
			row.getCell(8).setCellStyle(style);
			row.createCell(9).setCellValue("入口温度");
			row.getCell(9).setCellStyle(style);
			row.createCell(10).setCellValue("出口温度");
			row.getCell(10).setCellStyle(style);
			row.createCell(11).setCellValue("工作时间");
			row.getCell(11).setCellStyle(style);
			row.createCell(12).setCellValue("状态1");
			row.getCell(12).setCellStyle(style);
			row.createCell(13).setCellValue("状态2");
			row.getCell(13).setCellStyle(style);
			for (int i = 0; i < 14; i++) {
				sheet.setColumnWidth(i, 256 * 12);
			}
			OutputStream os = new FileOutputStream(file);
			excel.write(os);
			os.close();
			excel.close();
		}
		InputStream is = new FileInputStream(file);
		excel = new XSSFWorkbook(is);
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
		buffer.append("  抄表时间：" + head.getDatetime());
		sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		buffer.append("  导出时间：" + sdf.format(new Date()));
		style = excel.createCellStyle();
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		row.getCell(0).setCellValue(buffer.toString());
		row.getCell(0).setCellStyle(style);

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
		OutputStream os = new FileOutputStream(file);
		excel.write(os);
		os.close();
		is.close();
		excel.close();
	}

}
