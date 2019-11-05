package com.qixin.service;

import java.util.List;

import com.qixin.ui.MainWindows;

import gnu.io.SerialPort;

public class SendThread extends Thread {

	private MainWindows mainWindows;
	private List<byte[]> sendList;
	private SerialPort serialPort;
	public boolean flag = true;

	public SendThread(MainWindows mainWindows, SerialPort serialPort, List<byte[]> list) {
		this.mainWindows = mainWindows;
		this.serialPort = serialPort;
		this.sendList = list;
	}

	@Override
	public void run() {
//		byte[] bytes = { (byte) 0x68, (byte) 0x00, (byte) 0x02, (byte) 0x51, (byte) 0x10, (byte) 0x00, (byte) 0xCB,
//				(byte) 0x16 };
//		for (int i = 0; i < 30; i++) {
//			System.out.println("·¢ËÍ£º" + i);
//			SerialPortManager.sendToPort(serialPort, bytes);
//			try {
//				sleep(160);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
		int count = 0;
		for (int i = 0; i < sendList.size(); i++) {
			flag = false;
			SerialPortManager.sendToPort(serialPort, sendList.get(i));
			System.out.print("·¢ËÍ" + i + "--");
			for (int j = 0; j < sendList.get(i).length; j++) {
				System.out.print(String.format("%02X", sendList.get(i)[j]) + " ");
			}
			System.out.println();
			try {
				sleep(160);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!flag) {
				i--;
				count++;
				System.out.println("ÖØÊÔ");
			} else {
				count = 0;
			}
			if (count > 2) {
				mainWindows.onSendErr(++i);
				return;
			}
		}
		mainWindows.onSendErr(256);

	}
}
