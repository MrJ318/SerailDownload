package com.qixin.service;

import java.util.List;

import com.qixin.ui.MainWindows;

import gnu.io.SerialPort;

public class SendThread extends Thread {

	private MainWindows mainWindows;
	private List<byte[]> sendList;
	private SerialPort serialPort;
	public boolean flag;

	public SendThread(MainWindows mainWindows, SerialPort serialPort, List<byte[]> list) {
		this.mainWindows = mainWindows;
		this.serialPort = serialPort;
		this.sendList = list;
	}

	@Override
	public void run() {

		int count = 0;
		for (int i = 0; i < sendList.size(); i++) {
			flag = false;
			SerialPortManager.sendToPort(serialPort, sendList.get(i));
			try {
				sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!flag) {
				i--;
				count++;
			} else {
				count = 0;
			}
			if (count > 2) {
				mainWindows.onSendErr(i++);
				return;
			}
		}
		mainWindows.onSendErr(256);
//		for (byte[] bytes : sendList) {
//
//			SerialPortManager.sendToPort(serialPort, bytes);
//			synchronized (this) {
//				try {
//					this.wait();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
}
