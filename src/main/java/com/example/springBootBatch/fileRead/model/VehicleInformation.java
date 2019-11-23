
package com.example.springBootBatch.fileRead.model;
/**
 *
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class VehicleInformation {
	@XmlElement(name="brand")
	private String brand;
	@XmlElement(name="country")
	private String country;
	@XmlElement(name="modelname")
	private String modelname;
	@XmlElement(name="modelyear")
	private String modelyear;
	
	
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getModelname() {
		return modelname;
	}
	public void setModelname(String modelname) {
		this.modelname = modelname;
	}
	public String getModelyear() {
		return modelyear;
	}
	public void setModelyear(String modelyear) {
		this.modelyear = modelyear;
	}
	
	
	
}
