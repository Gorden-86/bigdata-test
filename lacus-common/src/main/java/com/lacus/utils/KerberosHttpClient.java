package com.lacus.utils;

import com.lacus.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * kerberos http client
 */
@Slf4j
public class KerberosHttpClient {

    private String principal;
    private String keyTabLocation;

    public KerberosHttpClient(String principal, String keyTabLocation) {
        super();
        this.principal = principal;
        this.keyTabLocation = keyTabLocation;
    }

    public KerberosHttpClient(String principal, String keyTabLocation, boolean isDebug) {
        this(principal, keyTabLocation);
        if (isDebug) {
            System.setProperty("sun.security.spnego.debug", "true");
            System.setProperty("sun.security.krb5.debug", "true");
        }
    }

    public KerberosHttpClient(String principal, String keyTabLocation, String krb5Location, boolean isDebug) {
        this(principal, keyTabLocation, isDebug);
        System.setProperty("java.security.krb5.conf", krb5Location);
    }

    private static CloseableHttpClient buildSpengoHttpClient() {
        HttpClientBuilder builder = HttpUtils.getHttpClientBuilder();
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(null, -1, null), new Credentials() {

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }
        });
        builder.setDefaultCredentialsProvider(credentialsProvider);
        return builder.build();
    }

    public String get(final String url, final String userId) {
        log.info("Calling KerberosHttpClient {} {} {}", this.principal, this.keyTabLocation, url);
        Configuration config = new Configuration() {

            @SuppressWarnings("serial")
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                Map<String, Object> options = new HashMap<>(9);
                options.put("useTicketCache", "false");
                options.put("useKeyTab", "true");
                options.put("keyTab", keyTabLocation);
                options.put("refreshKrb5Config", "true");
                options.put("principal", principal);
                options.put("storeKey", "true");
                options.put("doNotPrompt", "true");
                options.put("isInitiator", "true");
                options.put("debug", "true");
                return new AppConfigurationEntry[]{
                        new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule",
                                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options)};
            }
        };
        Set<Principal> princ = new HashSet<>(1);
        princ.add(new KerberosPrincipal(userId));
        Subject sub = new Subject(false, princ, new HashSet<>(), new HashSet<>());

        LoginContext lc;
        try {
            lc = new LoginContext("", sub, null, config);
            lc.login();
            Subject serviceSubject = lc.getSubject();
            return Subject.doAs(serviceSubject, (PrivilegedAction<String>) () -> {
                CloseableHttpClient httpClient = buildSpengoHttpClient();
                HttpGet httpget = new HttpGet(url);
                return HttpUtils.getResponseContentString(httpget, httpClient);
            });
        } catch (LoginException le) {
            log.error("Kerberos authentication failed ", le);
        }
        return null;
    }

    /**
     * get http request content by kerberosClient
     *
     * @param url url
     * @return http get request response content
     */
    public static String get(String url) {

        String responseContent;
        KerberosHttpClient kerberosHttpClient = new KerberosHttpClient(
                CommonPropertyUtils.getString(Constants.LOGIN_USER_KEY_TAB_USERNAME),
                CommonPropertyUtils.getString(Constants.LOGIN_USER_KEY_TAB_PATH),
                CommonPropertyUtils.getString(Constants.JAVA_SECURITY_KRB5_CONF_PATH), true);
        responseContent = kerberosHttpClient.get(url, CommonPropertyUtils.getString(Constants.LOGIN_USER_KEY_TAB_USERNAME));
        return responseContent;

    }
}
