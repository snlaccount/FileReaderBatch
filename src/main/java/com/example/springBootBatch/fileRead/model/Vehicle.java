package com.example.springBootBatch.fileRead.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="Vehicle")
@XmlAccessorType(XmlAccessType.FIELD)
public class Vehicle {
	@XmlElement(name="vehiclenumber")
	private String vehiclenumber;
	
	@XmlElement(name="VehicleInformation")
	private VehicleInformation vehicleInfo;

	public String getVehiclenumber() {
		return vehiclenumber;
	}

	public void setVehiclenumber(String vehiclenumber) {
		this.vehiclenumber = vehiclenumber;
	}

	public VehicleInformation getVehicleInfo() {
		return vehicleInfo;
	}

	public void setVehicleInfo(VehicleInformation vehicleInfo) {
		this.vehicleInfo = vehicleInfo;
	}

	@Override
	public String toString() {
		return "Vehicle [vehiclenumber=" + vehiclenumber + ", vehicleInfo=" + vehicleInfo + "]";
	}
	
	
}

