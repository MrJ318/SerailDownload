package com.qixin.model;

public class FileHead {

	private byte type;
	private byte count;
	private byte errCount;
	private byte[] date;
	private byte[] xiaoqu;
	private byte[] louhao;
	private byte[] danyuan;
	private byte[] zongxian;

	public FileHead() {
		super();
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getCount() {
		return count;
	}

	public void setCount(byte count) {
		this.count = count;
	}

	public byte getErrCount() {
		return errCount;
	}

	public void setErrCount(byte errCount) {
		this.errCount = errCount;
	}

	public byte[] getDate() {
		return date;
	}

	public void setDate(byte[] date) {
		this.date = date;
	}

	public byte[] getXiaoqu() {
		return xiaoqu;
	}

	public void setXiaoqu(byte[] xiaoqu) {
		this.xiaoqu = xiaoqu;
	}

	public byte[] getLouhao() {
		return louhao;
	}

	public void setLouhao(byte[] louhao) {
		this.louhao = louhao;
	}

	public byte[] getDanyuan() {
		return danyuan;
	}

	public void setDanyuan(byte[] danyuan) {
		this.danyuan = danyuan;
	}

	public byte[] getZongxian() {
		return zongxian;
	}

	public void setZongxian(byte[] zongxian) {
		this.zongxian = zongxian;
	}

}
