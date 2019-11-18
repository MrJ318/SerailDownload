package com.qixin.model;

/**
 * 总显实体类
 * 
 * @author Jevon
 * @date 2019年11月16日 下午4:10:08
 * 
 */
public class Zongxian {

	private int num;
	private int addr;

	public Zongxian() {
		super();
	}

	public Zongxian(int num, int addr) {
		super();
		this.num = num;
		this.addr = addr;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getAddr() {
		return addr;
	}

	public void setAddr(int addr) {
		this.addr = addr;
	}

}
