package com.qixin.service;

import java.io.IOException;
import java.io.InputStream;

import com.qixin.listener.MainListener;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * @author Mr.J
 * @Date 2019/9/7 - 13:53
 */
public class ComWriteEventListener implements SerialPortEventListener {

	private MainListener listener;
	private SerialPort serialPort;

	public ComWriteEventListener(MainListener listener, SerialPort serialPort) {
		this.listener = listener;
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
//				int bufflenth = in.available();
//				while (bufflenth != 0) {
//					// 初始化byte数组为buffer中数据的长度
//					bytes = new byte[bufflenth];
//					in.read(bytes);
//					bufflenth = in.available();
//				}

				while (in.available() > 0) {

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 初始化byte数组为buffer中数据的长度
					bytes = new byte[in.available()];
					in.read(bytes);
				}

				System.out.print("收到"+bytes.length+"--:");
				for (int i = 0; i < bytes.length; i++) {
					System.out.print(String.format("%02X", bytes[i])+" ");
				}
				System.out.println();
				listener.onWriteCompelet(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (in != null) {
						in.close();
						in = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
