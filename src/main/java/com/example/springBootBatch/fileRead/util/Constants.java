package com.example.springBootBatch.fileRead.util;

public class Constants {
	public static final String	INSERT_QUERY_OLD1 = "INSERT INTO TESTVEHICLE(ID,vehiclenumber,brand,country,modelname,modelyear)  VALUES(vehicle_seq.nextval,?,?,?,?,?)";
	public static final String	INSERT_QUERY_OLD = "INSERT INTO TESTVEHICLE(ID,VEHICLENUMBER,BRAND,COUNTRY,MODELNAME,MODELYEAR)  VALUES(vehicle_seq.nextval,?,?,?,?,?)";

}
