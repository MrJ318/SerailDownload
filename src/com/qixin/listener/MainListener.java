package com.qixin.listener;

/**
 * 主界面回调接口
 * 
 * @author Jevon
 * @date 2019年11月16日 下午4:10:08
 * 
 */
public interface MainListener {

	/**
	 * 发送写入线程回调
	 * 
	 * @param code 发送结果 0成功 -1失败
	 */
	void onSendCompelet(int code);

	/**
	 * 发送读取线程回调
	 * 
	 * @param code 发送结果 失败数量 0成功
	 */
	void onSendReadCompelet(int code);

	/**
	 * 写入数据后的返回信息
	 * 
	 * @param bytes
	 */
	void onWriteCompelet(byte[] bytes);

	/**
	 * 查询总线数据的返回信息
	 * 
	 * @param bytes
	 */
	void onReceiveCompelet1(byte[] bytes);

	/**
	 * 查询文件数据的返回信息
	 * 
	 * @param bytes
	 */
	void onReceiveCompelet2(byte[] bytes);
}
