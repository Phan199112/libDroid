package com.a324.mbaaslibrary.model;

import com.google.gson.annotations.SerializedName;


public class XDomainDocumentInfo extends DocumentInfo {

    @SerializedName("qualifier")
    protected String qualifier; //used for subfolder structure


    /**
     * Gets the qualifier value used to determine the subfolder to store the file in.
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Sets the qualifier value used to determine the subfolder to store the file in.
     * @param qualifier The subfolder to store the file(s)
     */
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }
}
