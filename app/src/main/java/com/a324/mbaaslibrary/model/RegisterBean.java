package com.a324.mbaaslibrary.model;

import android.telephony.PhoneNumberUtils;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class RegisterBean {
    @SerializedName("emailAddress")
    private String email;

    @SerializedName("phoneNumber")
    private String phone; //(xxx)xxx-xxxx

    @SerializedName("latLong")
    private String latLong; //LAT,LONG

    @SerializedName("sessionToken")
    private String sessionToken;

    @SerializedName("guid")
    private String guid;

    @SerializedName("path")
    private String path;

    @SerializedName("eventMessage")
    private String eventMessage = null;

    @SerializedName("appName")
    private String appName = null;

    @SerializedName("gcm")
    private String  gcm;

    @SerializedName("role")
    private ArrayList<String> roles = null;

    private String deviceId = null;

    private ValidationMethod validationMethod = ValidationMethod.EMAIL;

    private String emailToSMSDomain = "";

    public RegisterBean(){}

    public RegisterBean(String email, String phone, double latitude, double longitude, String appName, String deviceId, String gcm ){
        this.email = email;
        this.phone = phone;
        this.latLong = formatLatLong(latitude, longitude);
        this.appName = appName;
        this.deviceId = deviceId;
        this.gcm = gcm;
    }

    public RegisterBean(String email, String phone, double latitude, double longitude, String guid, String appName, String deviceId, String gcm){
        this(email, phone, latitude, longitude, appName, deviceId, gcm);
        this.guid = guid;
    }

    public RegisterBean(String email, String sessionToken, String appName){
        this.email = email;
        this.sessionToken = sessionToken;
        this.appName = appName;
    }

    //GET Methods
    public String getEmail(){
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public String getSessionToken(){
        return sessionToken;
    }

    public String getGuid(){
        return guid;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public String getDeviceId() { return deviceId;}

    public ValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public String getEmailToSMSDomain() {
        return emailToSMSDomain;
    }

    //SET Methods
    public void setEmail(String email){
        this.email = email;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setSessionToken(String sessionToken){
        this.sessionToken = sessionToken;
    }

    public void setGuid(String guid){
        this.guid = guid;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setEventMessage(String eventMessage){this.eventMessage = eventMessage;}

    public void setDeviceId(String deviceId){ this.deviceId = deviceId;}

    public String formatLatLong(Double latitude, Double longitude){

        return (Double.toString(latitude) + "," + Double.toString(longitude));
    }

    public void setLatLong(Double latitude, Double longitude){
        latLong = formatLatLong(latitude, longitude);
    }
    public Double getLatOrLong(String valueToGet){
        double value = 0.0;
        if(latLong.contains(",")) {
            String[] values = latLong.split(",");
            if(values.length == 2){
                if(valueToGet.equals("latitude")){
                    value = Double.parseDouble(values[0]);
                } else {
                    value = Double.parseDouble(values[1]);
                }

            }
        }
        return value;
    }

    public String getGcm() {
        return gcm;
    }

    public void setGcm(String gcm) {
        this.gcm = gcm;
    }

    public static String formatPhoneNum(String phNum){
        phNum = PhoneNumberUtils.stripSeparators(phNum);
        StringBuffer phNumSb = new StringBuffer(phNum);

        String formattedNum = "";

        if(phNum.length() == 10 || phNum.length() == 11){
            if (phNum.length() == 11) {
                phNum = phNum.substring(1);
            }
            String areaCode = phNum.substring(0,3);
            String threeDigit = phNum.substring(3,6);
            String fourDigit = phNum.substring(6);
            formattedNum = areaCode  + threeDigit +  fourDigit;
        } else {
            formattedNum = phNum;
            //return phNum;
        }


        return formattedNum;
    }

    private boolean verifyPhoneNum(String phNum){
        return false;
    }
    @Override
    public String toString(){
        return "RegisterBean [email=" + email + ", phone=" + phone + ", latLong=" + latLong + ", sessionToken=" + sessionToken + ", guid=" +
                guid + ", eventMessage=" + eventMessage + "]";

    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public void setValidationMethod(ValidationMethod validationMethod) {
        this.validationMethod = validationMethod;
    }

    public void setEmailToSMSDomain(String emailToSMSDomain) {
        this.emailToSMSDomain = emailToSMSDomain;
    }
}