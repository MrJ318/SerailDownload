package com.qixin.listener;

public interface MainListener {

	/**
	 * 发送线程回调
	 * 
	 * @param code 发送结果 0成功 -1失败
	 */
	void onSendCompelet(int code);

	/**
	 * 写入数据后的返回信息
	 * 
	 * @param bytes
	 */
	void onWriteCompelet(byte[] bytes);

	/**
	 * 查询数据的返回信息
	 * 
	 * @param bytes
	 */
	void onReceiveCompelet(byte[] bytes);
}
