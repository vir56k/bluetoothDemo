package com.zhangyunfei.bluetirepersuretools.bluetooth;

/**
 * Created by zhangyunfei on 16/9/17.
 */
public final class Commond {
    //关回显
    public static final byte[] CMD_ECHO_OFF = new byte[]{0x41, 0x54, 0x45, 0x30, 0x0D};
    public static final byte[] CMD_010C = new byte[]{0x30, 0x31, 0x30, 0x43, 0x0D};


    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            if (s4.length() == 1) {
                s4 = "0" + s4;
            }
            str = str + s4;
        }
        return str;
    }
}
