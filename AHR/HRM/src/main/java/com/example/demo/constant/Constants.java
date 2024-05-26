package com.example.demo.constant;


import java.time.LocalTime;

public class Constants {

    //avt
    public static final String DEFAULT_AVATAR = "https://res.cloudinary.com/dqvr7kat6/image/upload/v1716485533/hehavockucnje7csqr30.png";


    //dung gio
    public static final LocalTime TIME_START_WORK = LocalTime.of(8, 0, 0);
    public static final LocalTime TIME_LUNCH = LocalTime.of(12, 0);

    //phan trang
    public static final String PAGE = "1";
    public static final String SIZE = "3";


    //thoi gian het han token
    public static final int TOKEN_EXP = 900000;

    // Mã lỗi
    public static final String ERROR = "error";
    public static final String SUCCESS = "mess";
    public static final String INVALID_TOKEN_ERROR = "errors=invalid";
    public static final String EMAIL_ERROR = "errors=email";
    public static final String REVOKED_TOKEN_ERROR = "errors=revoked";
    public static final String TIME_ERROR = "errors=time";
    public static final String VERIFIED = "verified";
    public static final String VALID = "valid";

    // Thông điệp
    public static final String INVALID_TOKEN_ERROR_MESS = "Invalid activation code";
    public static final String EMAIL_ERROR_MESS = "Invalid activation email";
    public static final String REVOKED_TOKEN_ERROR_MESS = "Invalid activation code";
    public static final String TIME_ERROR_MESS = "The activation code has expired";
    public static final String VERIFIED_MESS = "Your account has already been activated";
    public static final String VALID_MESS = "Activation successful";
    public static final String EXCEPTION_ERROR_MESS = "An error has occurred";


}