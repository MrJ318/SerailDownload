package com.qixin.model;

public class TableRecoder {

	private String changjia;
	private String type;
	private String addr;
	private String menpai;
	private String cold;
	private String hot;
	private String power;
	private String flux;
	private String vol;
	private String intemp;
	private String outtemp;
	private String worktime;
	private String state1;
	private String state2;

	public String getChangjia() {
		return changjia;
	}

	public void setChangjia(String changjia) {
		this.changjia = changjia;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getMenpai() {
		return menpai;
	}

	public void setMenpai(String menpai) {
		this.menpai = menpai;
	}

	public String getCold() {
		return cold;
	}

	public void setCold(String cold) {
		this.cold = cold;
	}

	public String getHot() {
		return hot;
	}

	public void setHot(String hot) {
		this.hot = hot;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getFlux() {
		return flux;
	}

	public void setFlux(String flux) {
		this.flux = flux;
	}

	public String getVol() {
		return vol;
	}

	public void setVol(String vol) {
		this.vol = vol;
	}

	public String getIntemp() {
		return intemp;
	}

	public void setIntemp(String intemp) {
		this.intemp = intemp;
	}

	public String getOuttemp() {
		return outtemp;
	}

	public void setOuttemp(String outtemp) {
		this.outtemp = outtemp;
	}

	public String getWorktime() {
		return worktime;
	}

	public void setWorktime(String worktime) {
		this.worktime = worktime;
	}

	public String getState1() {
		return state1;
	}

	public void setState1(String state1) {
		this.state1 = state1;
	}

	public String getState2() {
		return state2;
	}

	public void setState2(String state2) {
		this.state2 = state2;
	}

	@Override
	public String toString() {
		return "TableRecoder [changjia=" + changjia + ", type=" + type + ", addr=" + addr + ", menpai=" + menpai
				+ ", cold=" + cold + ", hot=" + hot + ", power=" + power + ", flux=" + flux + ", vol=" + vol
				+ ", intemp=" + intemp + ", outtemp=" + outtemp + ", worktime=" + worktime + ", state1=" + state1
				+ ", state2=" + state2 + "]";
	}

}
