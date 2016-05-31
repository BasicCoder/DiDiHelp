package com.way.chat.common.bean;

import java.io.Serializable;

public class SeekInfoMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private int isOnline;
	private int img;
	private String ip;
	private int port;
	private int group;
	private String Address;

	private int getId(){
		return id;
	}

	private void setId(int id){
		this.id = id;
	}

	private String getName(){
		return name;
	}

	private void setName(String name){
		this.name = name;
	}

	public int getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	@Override
	public boolean equals(Object o){
		if(o instanceof SeekInfoMessage){
			SeekInfoMessage SeekInfo = (SeekInfoMessage) o;
			if(SeekInfo.getId() == id && SeekInfo.getIp().equals(ip) && SeekInfo.getPort() == port){
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString(){
		return "SeekInfoMessage [id=" + id + ", name=" + name + ", isOnline=" + isOnline
				+ ", img=" + img + ", ip=" + ip + ", port=" + port + ", group="
				+ group + "]";
	}

}
