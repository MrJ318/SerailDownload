package com.qixin.service;

import java.util.List;

import com.qixin.listener.MainListener;
import com.qixin.utils.SerialPortManager;

import gnu.io.SerialPort;

public class ComReceiveThread extends Thread {

	private MainListener listener;
	private List<byte[]> sendList;
	private SerialPort serialPort;
	public boolean flag = true;

	public ComReceiveThread(MainListener listener, SerialPort serialPort, List<byte[]> list) {
		this.listener = listener;
		this.serialPort = serialPort;
		this.sendList = list;
	}

	@Override
	public void run() {
		int count = 0;
		int err = 0;
		for (int i = 0; i < sendList.size(); i++) {
			flag = false;
			SerialPortManager.sendToPort(serialPort, sendList.get(i));
			System.out.println("∑¢ÀÕ£∫" + i + "/" + sendList.size());
//			System.out.print("∑¢ÀÕ" + i + "--");
			// for (int j = 0; j < sendList.get(i).length; j++) {
			// System.out.print(String.format("%02X", sendList.get(i)[j]) + " ");
			// }
			// System.out.println();
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (count > 2) {
				flag = true;
				err++;
			}
			if (!flag) {
				i--;
				count++;
				System.out.println("÷ÿ ‘");
			} else {
				count = 0;
			}
		}
		listener.onSendReadCompelet(err);

	}
}
