package com.qixin.listener;

/**
 * ������ص��ӿ�
 * 
 * @author Jevon
 * @date 2019��11��16�� ����4:10:08
 * 
 */
public interface MainListener {

	/**
	 * ����д���̻߳ص�
	 * 
	 * @param code ���ͽ�� 0�ɹ� -1ʧ��
	 */
	void onSendCompelet(int code);

	/**
	 * ���Ͷ�ȡ�̻߳ص�
	 * 
	 * @param code ���ͽ�� ʧ������ 0�ɹ�
	 */
	void onSendReadCompelet(int code);

	/**
	 * д�����ݺ�ķ�����Ϣ
	 * 
	 * @param bytes
	 */
	void onWriteCompelet(byte[] bytes);

	/**
	 * ��ѯ�������ݵķ�����Ϣ
	 * 
	 * @param bytes
	 */
	void onReceiveCompelet1(byte[] bytes);

	/**
	 * ��ѯ�ļ����ݵķ�����Ϣ
	 * 
	 * @param bytes
	 */
	void onReceiveCompelet2(byte[] bytes);
}
