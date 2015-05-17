package com.sxzq.oa.bean;

import java.io.Serializable;
import java.util.List;


public abstract class BaseListModel<T> implements Serializable{

	private static final long serialVersionUID = 1L;

	private int next = -1;
	
	private int pre = -1;
	
	private int total;
	
	private String sparkid;
	
	private List<T> list;

	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public int getPre() {
		return pre;
	}

	public void setPre(int pre) {
		this.pre = pre;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
	
	public List<T> getList(){
		return list;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getSparkid() {
		return sparkid;
	}

	public void setSparkid(String sparkid) {
		this.sparkid = sparkid;
	}


	@Override
	public String toString() {
		return "BaseModel [next=" + next + ", total=" + total + ", sparkid="
				+ sparkid + "]";
	}
	
	
	
}
