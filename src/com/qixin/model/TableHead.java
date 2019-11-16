package com.qixin.model;

public class TableHead {

	private String type;
	private String count;
	private String errcnt;
	private String datetime;
	private String xiaoqu;
	private String louhao;
	private String danyuan;
	private String linenum;

	public TableHead() {
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

	public String getErrcnt() {
		return errcnt;
	}

	public void setErrcnt(String errcnt) {
		this.errcnt = errcnt;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
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

	public String getLinenum() {
		return linenum;
	}

	public void setLinenum(String linenum) {
		this.linenum = linenum;
	}

	@Override
	public String toString() {
		return "TableHead [type=" + type + ", count=" + count + ", errcnt=" + errcnt + ", datetime=" + datetime
				+ ", xiaoqu=" + xiaoqu + ", louhao=" + louhao + ", danyuan=" + danyuan + ", linenum=" + linenum + "]";
	}

}
