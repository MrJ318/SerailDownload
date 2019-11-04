package com.qixin.service;

import java.io.IOException;
import java.io.InputStream;

import com.qixin.ui.MainWindows;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * @author Mr.J
 * @Date 2019/9/7 - 13:53
 */
public class ReceiveData implements SerialPortEventListener {

	private MainWindows mainWindows;
	private SerialPort serialPort;

	public ReceiveData(MainWindows mainWindows, SerialPort serialPort) {
		this.mainWindows = mainWindows;
		this.serialPort = serialPort;
	}

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
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
				if (bytes.length == 7) {
					mainWindows.onReceive(bytes);
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
		}
	}
}
