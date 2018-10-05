package com.a324.mbaaslibrary.model;

import com.google.gson.annotations.SerializedName;


public class DocumentInfo {
    @SerializedName("subCategoryType")
    protected String subCategoryType; //policy, guide

    @SerializedName("qualifiedPath")
    protected String qualifiedPath;

    @SerializedName("contentUploadDate")
    protected String contentUploadDate;

    @SerializedName("contentType")
    protected String contentType; //epub. html. doc, pdf

    @SerializedName("url")
    protected String url;

    @SerializedName("likes")
    protected int numLikes;

    @SerializedName("views")
    protected int numViews;

    @SerializedName("description")
    protected String description;

    @SerializedName("fileName")
    protected String fileName;

    @SerializedName("documentTitle")
    protected String documentTitle;

    @SerializedName("discriminators")
    protected String discriminators;

    @SerializedName("base64encoding")
    protected String base64encoding;

    @SerializedName("info")
    protected String info;


    public void setSubCategoryType(String subCategoryType) {
        this.subCategoryType = subCategoryType;
    }
    protected String title;

    public String getTitle(){
        return title;
    }

    public String getSubCategoryType(){
        return subCategoryType;
    }

    public String getQualifiedPath(){
        return qualifiedPath;
    }

    public String getContentUploadDate(){
        return contentUploadDate;
    }

    public String getContentType(){
        return this.contentType;
    }

    public String getUrl(){
        return this.url;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFileName(){
        return this.fileName;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public void setFileName(String fileName){

        this.fileName = fileName;
    }

    public String getBase64encoding() {
        return base64encoding;
    }

    public void setDescription(String description) { this.description = description; }

    public String getDiscriminators() { return discriminators; }

    public void setDiscriminators(String discriminators) {
        this.discriminators = discriminators;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public void setBase64encoding(String base64encoding) {
        this.base64encoding = base64encoding;
    }

    public void setQualifiedPath(String qualifiedPath) {
        this.qualifiedPath = qualifiedPath;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if(object == null) {
            result = false;
        }
        else {
            DocumentInfo documentInfo = (DocumentInfo) object;
            if(this.fileName.equals(documentInfo.getFileName()) && this.description.equals(documentInfo.getDescription())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.fileName.hashCode();
        hash = 7 * hash + this.description.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "DOCUMENT Info: file name:" + this.fileName + "type: " + this.contentType;
    }
}

