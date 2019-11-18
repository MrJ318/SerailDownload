package com.qixin.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.qixin.ui.MainWindows;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * ���Ͷ�ȡ�ļ�����󴮿ڼ���
 * 
 * @author Jevon
 * @date 2019��11��16�� ����4:10:08
 * 
 */
public class ComReceiveEventListener implements SerialPortEventListener {

	private Logger logger = Logger.getLogger(ComReceiveEventListener.class);

	private MainWindows mainWindows;
	private SerialPort serialPort;
	private int flag;

	public ComReceiveEventListener(MainWindows mainWindows, SerialPort serialPort, int flag) {
		this.mainWindows = mainWindows;
		this.serialPort = serialPort;
		this.flag = flag;
	}

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			InputStream in = null;
			byte[] bytes = null;
			try {
				in = serialPort.getInputStream();

				while (in.available() > 0) {

					try {
						Thread.sleep(1700);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// ��ʼ��byte����Ϊbuffer�����ݵĳ���
					bytes = new byte[in.available()];
					in.read(bytes);
				}

				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < bytes.length; i++) {
					buffer.append(String.format("%02X", bytes[i]) + " ");
				}
				logger.debug("�յ�����" + bytes.length + ":" + buffer);

				if (flag == 0) {
					mainWindows.onReceiveCompelet1(bytes);
					flag = 1;
				} else {
					mainWindows.onReceiveCompelet2(bytes);
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			} finally {
				try {
					if (in != null) {
						in.close();
						in = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}
		}
	}
}
