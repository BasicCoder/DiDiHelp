package com.way.chat.activity;

public class SeekInfoEntity {
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
}
