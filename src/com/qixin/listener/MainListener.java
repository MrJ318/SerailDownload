package com.qixin.listener;

public interface MainListener {

	/**
	 * �����̻߳ص�
	 * 
	 * @param code ���ͽ�� 0�ɹ� -1ʧ��
	 */
	void onSendCompelet(int code);

	/**
	 * д�����ݺ�ķ�����Ϣ
	 * 
	 * @param bytes
	 */
	void onWriteCompelet(byte[] bytes);

	/**
	 * ��ѯ���ݵķ�����Ϣ
	 * 
	 * @param bytes
	 */
	void onReceiveCompelet(byte[] bytes);
}
