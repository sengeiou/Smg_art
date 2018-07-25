/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smg.art.base;

public class Constant {
    public static final int STATE_UNKNOWN = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_EMPTY = 3;
    public static final int STATE_SUCCESS = 4;
//    public  static  final String API_BASE_URL="http://120.78.136.232:9000/";
    public  static  final String API_BASE_URL=" http://192.168.1.56:8080/art-world/";

    /**
     * 会员注册
     */
    public  static  final String MEMBER_REGISTER="member/register";
    /***
     * 获取短信验证码
     */
    public  static  final String MEMBER_GETPHONEVERIFYCODE="member/getPhoneVerifyCode";




    public  static  final String APK_UPDATE="apk_update";
    public  static  final String APK_UPDATE_PATH="apk_path";
    public static String LOCAL_APP_CONFIG_FILE_NAME = "smgconfig";//本地应用配置文件名，存储些全局变量



}
