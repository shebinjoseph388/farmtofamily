package com.farmtofamily.ecommerce;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;


public class UserDbDTO extends BaseDTO {

    private String userid;
    private String fname;
    private String lname;
    private String email;
    private String phone;
    private String addr1 ;
    private String addr2;
    private String apart;

    public String getApart() {
        return apart;
    }

    public void setApart(String apart) {
        this.apart = apart;
    }

    private String city;
    private String code;



    public UserDbDTO() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static UserDbDTO deserializeJson(String serializedString) {
        Gson gson = new Gson();
        UserDbDTO userDbDTO = null;
        try {
            userDbDTO = gson.fromJson(serializedString, UserDbDTO.class);
        } catch (JsonParseException e) {
            Log.d("Download User Db DTO", "Exception in deserialization" + e.toString());
        }
        return userDbDTO;
    }

}
