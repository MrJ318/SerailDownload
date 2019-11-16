package com.qixin.service;

import java.util.List;

import com.qixin.ui.MainWindows;
import com.qixin.utils.SerialPortManager;

import gnu.io.SerialPort;

public class ComReceiveThread extends Thread {

	private MainWindows mainWindows;
	private List<byte[]> sendList;
	private SerialPort serialPort;
	public boolean flag = true;

	public ComReceiveThread(MainWindows mainWindows, SerialPort serialPort, List<byte[]> list) {
		this.mainWindows = mainWindows;
		this.serialPort = serialPort;
		this.sendList = list;
	}

	@Override
	public void run() {
//		byte[] bytes = new byte[] {(byte)0xFE,(byte)0x68,(byte)0x00,(byte)0x02,(byte)0x52,(byte)0x10,(byte)0x60,(byte)0x2C,(byte)0x16};
//		SerialPortManager.sendToPort(serialPort, bytes);
//		try {
//			sleep(160);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		int count = 0;
		for (int i = 0; i < sendList.size(); i++) {
			flag = false;
			SerialPortManager.sendToPort(serialPort, sendList.get(i));
			 System.out.print("·¢ËÍ" + i + "--");
			// for (int j = 0; j < sendList.get(i).length; j++) {
			// System.out.print(String.format("%02X", sendList.get(i)[j]) + " ");
			// }
			// System.out.println();
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			if (!flag) {
//				i--;
//				count++;
//				System.out.println("ÖØÊÔ");
//			} else {
//				count = 0;
//			}
//			if (count > 3) {
//				mainWindows.onSendErr(++i);
//				return;
//			}
		}

//		mainWindows.onSendErr(256);

	}
}
