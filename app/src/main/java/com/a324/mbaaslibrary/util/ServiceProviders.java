package com.a324.mbaaslibrary.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ServiceProviders {

    public enum ServiceProvider {VERIZON, ATT, BOOST, SPRINT, TMOBILE}

    public static final String[] VERIZON_CODES =
            {
                    "310004"
                    , "310012"
                    , "311280"
                    , "311281"
                    , "311282"
                    , "311283"
                    , "311284"
                    , "311285"
                    , "311286"
                    , "311287"
                    , "311288"
                    , "311289"
                    , "311480"
                    , "311481"
                    , "311482"
                    , "311483"
                    , "311484"
                    , "311485"
                    , "311486"
                    , "311487"
                    , "311488"
                    , "311489"};

    public static final String[] ATT_CODES =
            {
                    "310070"
                    , "310170"
                    , "310380"
                    , "310410"
                    , "310560"
                    , "310680"
                    , "311180"};

    public static final String[] BOOST_CODES =
            {"311870"};


    public static final String[] SPRINT_CODES =
            {
                    "310120"
                    , "312530"};

    public static final String[] TMOBILE_CODES =
            {
                    "310026"
                    , "310160"
                    , "310200"
                    , "310210"
                    , "310220"
                    , "310230"
                    , "310240"
                    , "310250"
                    , "310260"
                    , "31026"
                    , "310270"
                    , "310300"
                    , "310310"
                    , "310490"
                    , "310530"
                    , "310590"
                    , "310640"
                    , "310660"
                    , "310800"};

    public static final HashSet<String> VERIZON_CODE_SET = new HashSet<>(Arrays.asList(VERIZON_CODES));
    public static final HashSet<String> ATT_CODE_SET = new HashSet<>(Arrays.asList(ATT_CODES));
    public static final HashSet<String> BOOST_CODE_SET = new HashSet<>(Arrays.asList(BOOST_CODES));
    public static final HashSet<String> SPRINT_CODE_SET = new HashSet<>(Arrays.asList(SPRINT_CODES));
    public static final HashSet<String> TMOBILE_CODE_SET = new HashSet<>(Arrays.asList(TMOBILE_CODES));


    public static final Map<ServiceProvider, HashSet<String>> SERVICE_PROVIDER_CODES = new HashMap<>();

    static {
        SERVICE_PROVIDER_CODES.put(ServiceProvider.VERIZON, VERIZON_CODE_SET);
        SERVICE_PROVIDER_CODES.put(ServiceProvider.ATT, ATT_CODE_SET);
        SERVICE_PROVIDER_CODES.put(ServiceProvider.BOOST, BOOST_CODE_SET);
        SERVICE_PROVIDER_CODES.put(ServiceProvider.SPRINT, SPRINT_CODE_SET);
        SERVICE_PROVIDER_CODES.put(ServiceProvider.TMOBILE, TMOBILE_CODE_SET);
    }

    public static final Map<ServiceProvider, String> SERVICE_PROVIDER_EMAIL_TO_SMS_DOMAINS = new HashMap<>();
    static {
        SERVICE_PROVIDER_EMAIL_TO_SMS_DOMAINS.put(ServiceProvider.VERIZON, "vtext.com");
        SERVICE_PROVIDER_EMAIL_TO_SMS_DOMAINS.put(ServiceProvider.ATT, "txt.att.net");
        SERVICE_PROVIDER_EMAIL_TO_SMS_DOMAINS.put(ServiceProvider.BOOST, "myboostmobile.com");
        SERVICE_PROVIDER_EMAIL_TO_SMS_DOMAINS.put(ServiceProvider.SPRINT, "messaging.sprintpcs.com");
        SERVICE_PROVIDER_EMAIL_TO_SMS_DOMAINS.put(ServiceProvider.TMOBILE, "tmomail.net");
    }

    /**
     * Use the MCC+MNC (mobile country code + mobile network code) of the provider to return the
     * carrier name.
     *
     * @param code MCC+MNC (mobile country code + mobile network code). 5 or 6 decimal digits.
     * @return ServiceProvider name, or null if code is invalid or not found.
     */
    public static ServiceProvider getServiceProvider(String code){
        if (code != null
                && (code.length() == 5 || code.length() == 6) ){
            for (ServiceProvider provider : SERVICE_PROVIDER_CODES.keySet()){
                if (SERVICE_PROVIDER_CODES.get(provider).contains(code)){
                    return provider;
                }
            }
        }

        return null;
    }



}
