package com.qixin.model;

/**
 * ¥��Ϣʵ����
 * 
 * @author Jevon
 * @date 2019��11��16�� ����4:10:08
 * 
 */
public class Lou {
	private String name;
	private int addr;
	private int len;

	public Lou() {
		super();
	}

	public Lou(String name, int addr, int len) {
		super();
		this.name = name;
		this.addr = addr;
		this.len = len;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAddr() {
		return addr;
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

}
