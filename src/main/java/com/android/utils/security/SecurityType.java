package com.android.utils.security;

import com.android.utils.StringUtil;

import java.util.Random;

public enum SecurityType {
    DES, MD5;

    private final char[] multipart_chars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private String encryptKey;

    public String getSecurityString(String key, Object src) throws Exception {
        if (src == null)
            throw new Exception("src can not be null");
        String securityString = null;
        switch (this) {
            case DES:
                if (StringUtil.isEmpty(key))
                    throw new Exception("key can not be null");
                securityString = EncryptUtils.encryptThreeDESECB(src.toString(), key);
                break;
            case MD5:
                securityString = StringUtil.getMD5Str(src.toString());
                break;
        }
        if (StringUtil.isEmpty(securityString))
            throw new Exception("key can not be null");
        return securityString;
    }

    public String getSecurityString(Object src) throws Exception {
        return getSecurityString(getEncryptKey(), src);
    }

    public String getEncryptKey() {
        if (!StringUtil.isEmpty(encryptKey))
            return encryptKey;

        StringBuffer localStringBuffer = new StringBuffer();
        Random localRandom = new Random();
        for (int i = 0; i < 30; i++) {
            localStringBuffer.append(multipart_chars[localRandom.nextInt(multipart_chars.length)]);
        }
        this.encryptKey = localStringBuffer.toString();
        return encryptKey;
    }
}