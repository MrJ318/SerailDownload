package com.qixin.utils;

import java.util.ArrayList;
import java.util.List;

import com.qixin.exception.ReadExcelException;
import com.qixin.model.Lou;
import com.qixin.model.TableHead;
import com.qixin.model.TableRecoder;
import com.qixin.model.Xiaoqu;
import com.qixin.model.Zongxian;

public class ByteUtil {

	/**
	 * 字节数组转头文件
	 * 
	 * @param bytes 从设备读取到的数组
	 * @return 头文件
	 */
	public static TableHead byteToHead(byte[] bytes) {

		TableHead head = new TableHead();

		// 类型
		head.setType((bytes[2] & 0xff) + "");
		// 总记录数
		head.setCount((bytes[3] & 0xff) + "");
		// 出错记录数
		head.setErrcnt((bytes[4] & 0xff) + "");
		// 时间
		byte[] tmp = new byte[6];
		System.arraycopy(bytes, 5, tmp, 0, 6);
		head.setDatetime(new String(tmp).trim());
		// 小区
		tmp = new byte[20];
		System.arraycopy(bytes, 11, tmp, 0, 20);
		head.setXiaoqu(new String(tmp).trim());
		// 楼号
		tmp = new byte[6];
		System.arraycopy(bytes, 31, tmp, 0, 6);
		head.setLouhao(new String(tmp).trim());
		// 单元
		tmp = new byte[6];
		System.arraycopy(bytes, 37, tmp, 0, 6);
		head.setDanyuan(new String(tmp).trim());
		// 总线
		int bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[43 + i];
		}
		head.setLinenum(bits + "");
		return head;
	}

	/**
	 * 字节数组转记录文件
	 * 
	 * @param bytes 从设备读取到的数组
	 * @return 记录文件
	 */
	public static TableRecoder byteToRecoder(byte[] bytes) {

		TableRecoder recoder = new TableRecoder();

		// 厂家(直接转换)
		recoder.setChangjia(bytes[2] + "");
		// 类型(直接转换)
		recoder.setType(bytes[3] + "");

		// 表码
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			buffer.append(bytes[4 + i] / 16);
			buffer.append(bytes[4 + i] % 16);
		}
		recoder.setAddr(buffer.toString());
		// 门牌
		byte[] tmp = new byte[6];
		System.arraycopy(bytes, 11, tmp, 0, 6);
		recoder.setMenpai(new String(tmp).trim());
		// 冷量
		int bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[17 + i];
		}
		recoder.setCold(String.format("%.2f", Float.intBitsToFloat(bits)));
		// 热量
		bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[21 + i];
		}
		recoder.setHot(String.format("%.2f", Float.intBitsToFloat(bits)));
		// 功率
		bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[25 + i];
		}
		recoder.setPower(String.format("%.2f", Float.intBitsToFloat(bits)));
		// 流速
		bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[29 + i];
		}
		recoder.setFlux(String.format("%.2f", Float.intBitsToFloat(bits)));
		// 流量
		bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[33 + i];
		}
		recoder.setVol(String.format("%.2f", Float.intBitsToFloat(bits)));
		// 入口温度
		bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[37 + i];
		}
		recoder.setIntemp(String.format("%.2f", Float.intBitsToFloat(bits)));
		// 出口温度
		bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[41 + i];
		}
		recoder.setOuttemp(String.format("%.2f", Float.intBitsToFloat(bits)));
		// 工作时间
		bits = 0;
		for (int i = 0; i < 4; i++) {
			bits = bits << 8;
			bits += bytes[45 + i];
		}
		recoder.setWorktime(bits + "");
		// 状态1
		bits = bytes[49];
		recoder.setState1(String.format("%02X", bits));
		// 状态2
		bits = bytes[50];
		recoder.setState2(String.format("%02X", bits));
		return recoder;
	}

	public static byte[] headToByte(TableHead head, int count) throws ReadExcelException {
		byte[] bytes = new byte[73];
		bytes[0] = (byte) 0xFE;
		bytes[1] = (byte) 0x68;
		bytes[2] = (byte) 0x00;
		bytes[3] = (byte) 0x42;
		bytes[4] = (byte) 0x61;
		bytes[5] = (byte) 0x00;
		bytes[6] = (byte) 0x00;
		bytes[7] = (byte) 0xEA;// 文件头开始
		bytes[8] = (byte) 0x00;// 文件头校验
		bytes[9] = (byte) 0x00;// 文件类型
		bytes[10] = (byte) count;// 记录数
		bytes[11] = (byte) 0x00;// 出错数量
		bytes[12] = (byte) 0x00;// 抄表日期
		bytes[13] = (byte) 0x00;
		bytes[14] = (byte) 0x00;
		bytes[15] = (byte) 0x00;
		bytes[16] = (byte) 0x00;
		bytes[17] = (byte) 0x00;
		String name = head.getXiaoqu().trim();
		if (name.length() > 9) {
			throw new ReadExcelException("小区名过长，请控制在9个字符以内");
		}
		byte[] tmp = name.getBytes();
		for (int i = 0; i < tmp.length; i++) {
			bytes[18 + i] = tmp[i];
		}

		name = head.getLouhao().trim();
		if (name.length() > 9) {
			throw new ReadExcelException("楼号过长，请控制在5位以内");
		}
		tmp = name.getBytes();
		for (int i = 0; i < tmp.length; i++) {
			bytes[38 + i] = tmp[i];
		}

		name = head.getDanyuan().trim();
		if (name.length() > 9) {
			throw new ReadExcelException("单元号过长，请控制在5位以内");
		}
		tmp = name.getBytes();
		for (int i = 0; i < tmp.length; i++) {
			bytes[44 + i] = tmp[i];
		}

		int zx = Integer.parseInt(head.getLinenum());
		bytes[50] = (byte) (zx >> 24);
		bytes[51] = (byte) (zx >> 16);
		bytes[52] = (byte) (zx >> 8);
		bytes[53] = (byte) (zx);
		bytes[71] = (byte) 0x00;
		bytes[72] = (byte) 0x16;
		for (int i = 9; i < 54; i++) {
			bytes[8] += bytes[i];
		}
		for (int i = 1; i < 71; i++) {
			bytes[71] += bytes[i];
		}
		return bytes;
	}

	public static byte[] recoderToByte(TableRecoder recoder, int index) throws ReadExcelException {
		byte[] bytes = new byte[61];
		bytes[0] = (byte) 0xFE;
		bytes[1] = (byte) 0x68;
		bytes[2] = (byte) 0x00;
		bytes[3] = (byte) 0x36;
		bytes[4] = (byte) 0x62;
		bytes[5] = (byte) 0x00;
		bytes[6] = (byte) 0x00;
		bytes[7] = (byte) index;
		bytes[8] = (byte) 0xEA;// 文件头开始
		bytes[9] = (byte) 0x00;// 文件头校验
		int code = Integer.parseInt(recoder.getChangjia());
		bytes[10] = (byte) code;// 厂家
		bytes[11] = (byte) 0x20;// 类型+表码
		String tmp = String.format("%08d", Integer.parseInt(recoder.getAddr().trim()));
		char[] array = tmp.toCharArray();
		for (int j = 0; j < 8; j++) {
			if (j % 2 == 0) {
				bytes[12 + j / 2] = (byte) (Integer.parseInt(array[j] + "") * 16);
			} else {
				bytes[12 + j / 2] += (byte) Integer.parseInt(array[j] + "");
			}
		}

		tmp = recoder.getMenpai().trim();
		if (tmp.length() > 5) {
			throw new ReadExcelException("门牌号过长，请控制在5位以内");
		}
		tmp = String.format("%04d", Integer.parseInt(tmp));
		byte[] tmps = tmp.getBytes();
		for (int i = 0; i < tmps.length; i++) {
			bytes[19 + i] = tmps[i];
		}

		bytes[59] = (byte) 0x00;
		bytes[60] = (byte) 0x16;
		for (int i = 10; i < 59; i++) {
			bytes[9] += bytes[i];
		}
		for (int i = 1; i < bytes.length - 2; i++) {
			bytes[59] += bytes[i];
		}
		return bytes;
	}

	public static byte[] xiaoquToByte(Xiaoqu xiaoqu) {

		byte[] bytes = new byte[24];
		String name = xiaoqu.getName();
		byte[] tmp = name.getBytes();
		for (int i = 0; i < tmp.length; i++) {
			bytes[i] = tmp[i];
		}
		bytes[20] = (byte) (xiaoqu.getAddr() >> 8);
		bytes[21] = (byte) (xiaoqu.getAddr());
		bytes[22] = (byte) (xiaoqu.getLen() >> 8);
		bytes[23] = (byte) (xiaoqu.getLen());
		return bytes;
	}

	public static byte[] louToByte(Lou lou) {
		byte[] bytes = new byte[10];
		byte[] tmp = lou.getName().getBytes();
		for (int i = 0; i < tmp.length; i++) {
			bytes[i] = tmp[i];
		}
		bytes[6] = (byte) (lou.getAddr() >> 8);
		bytes[7] = (byte) (lou.getAddr());
		bytes[8] = (byte) (lou.getLen() >> 8);
		bytes[9] = (byte) (lou.getLen());
		return bytes;
	}

	public static byte[] zxianToByte(Zongxian zongxian) {
		byte[] bytes = new byte[6];
		bytes[0] = (byte) (zongxian.getNum() >> 24);
		bytes[1] = (byte) (zongxian.getNum() >> 16);
		bytes[2] = (byte) (zongxian.getNum() >> 8);
		bytes[3] = (byte) (zongxian.getNum());
		bytes[4] = (byte) (zongxian.getAddr() >> 8);
		bytes[5] = (byte) (zongxian.getAddr());
		return bytes;
	}

	/**
	 * 生成保存命令
	 * 
	 * @param sect 要保存到的扇区
	 * @return 命令
	 */
	public static byte[] getSaveByte(int sect) {
		byte[] bytes = new byte[9];
		bytes[0] = (byte) 0xFE;
		bytes[1] = (byte) 0x68;
		bytes[2] = (byte) 0x00;
		bytes[3] = (byte) 0x02;
		bytes[4] = (byte) 0x63;
		bytes[5] = (byte) (sect >> 8);
		bytes[6] = (byte) sect;
		bytes[7] = (byte) 0x00;
		for (int i = 1; i < 7; i++) {
			bytes[7] += bytes[i];
		}
		bytes[8] = (byte) 0x16;
		return bytes;
	}

	public static List<byte[]> getReadByte(byte[] bytes) {
		int len = bytes[5] << 8;
		len = len + bytes[6];
		len = (len - 2) / 6;
		List<byte[]> list = new ArrayList<byte[]>();
		for (int i = 0; i < len; i++) {
			byte[] b = new byte[9];
			b[0] = (byte) 0xFE;
			b[1] = (byte) 0x68;
			b[2] = 0;
			b[3] = 2;
			b[4] = (byte) 0x51;
			int page = bytes[14 + i * 6] << 8;
			page = page + bytes[15 + i * 6];
			page = page << 4;
			b[5] = (byte) (page >> 8);
			b[6] = (byte) page;
			b[7] = (byte) (((byte) 0xBB) + b[5] + b[6]);
			b[8] = (byte) 0x16;
			list.add(b);
		}
		return list;
	}

}
