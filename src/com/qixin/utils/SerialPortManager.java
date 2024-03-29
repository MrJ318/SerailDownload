package com.qixin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.Vector;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * 串口读写工具
 * 
 * @author Jevon
 * @date 2019年11月16日 下午4:10:08
 * 
 */
public class SerialPortManager {

	/**
	 * 查找所有可用端口
	 *
	 * @return 可用端口名称列表
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> findPort() {
		// 获得当前所有可用串口
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		Vector<String> portNameList = new Vector<String>();
		// 将可用串口名添加到List并返回该List
		while (portList.hasMoreElements()) {
			String portName = portList.nextElement().getName();
			portNameList.add(portName);
		}
		return portNameList;
	}

	/**
	 * 打开串口
	 *
	 * @param portName 端口名称
	 * @param baudrate 波特率
	 * @return 串口对象
	 */
	public static SerialPort openPort(String portName, int baudrate)
			throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
		// 通过端口名识别端口
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		// 打开端口，设置端口名与timeout（打开操作的超时时间）
		CommPort commPort = portIdentifier.open(portName, 2000);
		// 判断是不是串口
		if (commPort instanceof SerialPort) {
			SerialPort serialPort = (SerialPort) commPort;
			// 设置串口的波特率等参数
			serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			return serialPort;
		}
		return null;

	}

	/**
	 * 关闭串口
	 *
	 * @param serialPort 要关闭的串口对象
	 */
	public static void closePort(SerialPort serialPort) {
		if (serialPort != null) {
			serialPort.close();
			serialPort = null;
		}
	}

	/**
	 * 向串口发送数据
	 *
	 * @param serialPort 串口对象
	 * @param order      待发送数据
	 */
	public static void sendToPort(SerialPort serialPort, byte[] order) {
		OutputStream out = null;
		try {
			out = serialPort.getOutputStream();
			out.write(order);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
					out = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从串口读取数据
	 *
	 * @param serialPort 当前已建立连接的SerialPort对象
	 * @return 读取到的数据
	 */
	public static byte[] readFromPort(SerialPort serialPort) {
		InputStream in = null;
		byte[] bytes = null;
		try {
			in = serialPort.getInputStream();
			// 获取buffer里的数据长度
			int bufflenth = in.available();
			while (bufflenth != 0) {
				// 初始化byte数组为buffer中数据的长度
				bytes = new byte[bufflenth];
				in.read(bytes);
				bufflenth = in.available();
			}
		} catch (IOException e) {
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
			}
		}
		return bytes;
	}

	/**
	 * 添加监听器
	 *
	 * @param port     串口对象
	 * @param listener 串口监听器
	 */
	public static void addListener(SerialPort port, SerialPortEventListener listener) {
		try {
			// 给串口添加监听器
			port.addEventListener(listener);
			// 设置当有数据到达时唤醒监听接收线程
			port.notifyOnDataAvailable(true);
			// 设置当通信中断时唤醒中断线程
			port.notifyOnBreakInterrupt(true);
		} catch (TooManyListenersException e) {
		}
	}

	/**
	 * 移除监听
	 * 
	 * @param port 串口对象
	 */
	public static void removeListener(SerialPort port) {
		port.removeEventListener();
	}

}
