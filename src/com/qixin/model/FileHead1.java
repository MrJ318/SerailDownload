package com.qixin.model;

public class FileHead1 {

	private String type;
	private String count;
	private String errCount;
	private String date;
	private String xiaoqu;
	private String louhao;
	private String danyuan;
	private String zongxian;

	public FileHead1() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getErrCount() {
		return errCount;
	}

	public void setErrCount(String errCount) {
		this.errCount = errCount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getXiaoqu() {
		return xiaoqu;
	}

	public void setXiaoqu(String xiaoqu) {
		this.xiaoqu = xiaoqu;
	}

	public String getLouhao() {
		return louhao;
	}

	public void setLouhao(String louhao) {
		this.louhao = louhao;
	}

	public String getDanyuan() {
		return danyuan;
	}

	public void setDanyuan(String danyuan) {
		this.danyuan = danyuan;
	}

	public String getZongxian() {
		return zongxian;
	}

	public void setZongxian(String zongxian) {
		this.zongxian = zongxian;
	}

	@Override
	public String toString() {
		return "FileHead1 [xiaoqu=" + xiaoqu + ", louhao=" + louhao + ", danyuan=" + danyuan + ", zongxian=" + zongxian
				+ "]";
	}
	
	

}
