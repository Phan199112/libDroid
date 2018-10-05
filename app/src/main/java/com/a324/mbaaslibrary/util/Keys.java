package com.a324.mbaaslibrary.util;

import com.a324.mbaaslibrary.util.PropertiesUtil;

public interface Keys {
        public static final String KEY = PropertiesUtil.getStringProperty("key");
        public static final String KEY_PRE_REGISTER = KEY + PropertiesUtil.getStringProperty("keyPreRegister");
        public static final String KEY_REGISTER = KEY + PropertiesUtil.getStringProperty("keyRegister");
        public static final String KEY_VALIDATE = KEY + PropertiesUtil.getStringProperty("keyValidate");
        public static final String KEY_GET_CONTENT = KEY + PropertiesUtil.getStringProperty("keyGetContent");
        public static final String KEY_UPLOAD_FILE = KEY + PropertiesUtil.getStringProperty("keyUploadFile");
        public static final String KEY_DOWNLOAD_FILE = KEY + PropertiesUtil.getStringProperty("download");
        public static final String KEY_EMAIL = KEY + PropertiesUtil.getStringProperty("keyEmail");
        public static final String KEY_SEARCH  = KEY + PropertiesUtil.getStringProperty("keySearch");
        public static final String KEY_APP_REG  = KEY + PropertiesUtil.getStringProperty("keyAppReg");
        public static final String KEY_APP_USERS  = KEY + PropertiesUtil.getStringProperty("keyAppUsers");
        public static final String KEY_NOTIFY  = KEY + PropertiesUtil.getStringProperty("keyNotify");
        public static final String KEY_GEOFENCE_NOTIFY = KEY + PropertiesUtil.getStringProperty("keyGeofence");

        //User Metadata Calls
        public static final String KEY_UPLOAD_META = KEY + PropertiesUtil.getStringProperty("keyUploadMeta");
        public static final String KEY_GET_META_STATE = KEY + PropertiesUtil.getStringProperty("keyGetMetaState");
        public static final String KEY_DELETE_META = KEY + PropertiesUtil.getStringProperty("keyDeleteMeta");
        public static final String KEY_FITLER_META  = KEY + PropertiesUtil.getStringProperty("keyFilterMeta");
        public static final String KEY_SEARCH_META  = KEY + PropertiesUtil.getStringProperty("keySearchMeta");
}
