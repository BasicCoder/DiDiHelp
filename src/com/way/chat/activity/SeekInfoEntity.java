package com.way.chat.activity;

import java.io.Serializable;

public class SeekInfoEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	private int img;
	private String name;
	private String address;
	private String says;
	
	public SeekInfoEntity(){
	}
	
	public SeekInfoEntity(int img, String name, String address, String says){
		this.img = img;
		this.name = name;
		this.address = address;
		this.says = says;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getImg(){
		return img;
	}
	
	public void setImg(int img){
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress(){
		return address;
	} 

	public void setAddress(String address){
		this.address = address;
	}

	public String getSays(){
		return says;
	}

	public void setSays(String says){
		this.says = says;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof SeekInfoEntity) {
			SeekInfoEntity seekinfo = (SeekInfoEntity) o;
			if (seekinfo.getId() == id && seekinfo.getName().equals(name)
					&& seekinfo.getSays().equals(says)) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	public String toString() {
		return "SeekInfoEntity [id=" + id + ", name=" + name 
				+ ", img=" + img + ", address=" + address + ", says=" + says
				+ "]";
	}
}
