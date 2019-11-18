package com.qixin.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.qixin.listener.MainListener;
import com.qixin.utils.SerialPortManager;

import gnu.io.SerialPort;

/**
 * ���Ͷ�ȡ���������߳�
 * 
 * @author Jevon
 * @date 2019��11��16�� ����4:10:08
 * 
 */
public class ComReceiveThread extends Thread {
	private Logger logger = Logger.getLogger(ComReceiveThread.class);

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

			StringBuffer buffer = new StringBuffer();
			for (int j = 0; j < sendList.get(i).length; j++) {
				buffer.append(String.format("%02X", sendList.get(i)[j]) + " ");
			}
			logger.debug("��������" + i + "/" + sendList.size() + "��" + buffer);

			try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			if (count > 2) {
				flag = true;
				err++;
			}
			if (!flag) {
				i--;
				count++;
				logger.debug("����ʧ�ܣ�����");
			} else {
				count = 0;
			}
		}
		listener.onSendReadCompelet(err);

	}
}
