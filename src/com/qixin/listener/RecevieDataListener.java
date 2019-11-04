package com.qixin.listener;

public interface RecevieDataListener {

	void onReceive(byte[] bytes);

	void onSendErr(int code);
}
