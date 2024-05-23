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
    public static final String INVALID_TOKEN_ERROR_MESS = "Mã kích hoạt không chính xác";
    public static final String EMAIL_ERROR_MESS = "Mail kích hoạt không chính xác";
    public static final String REVOKED_TOKEN_ERROR_MESS = "Mã kích hoạt không chính xác";
    public static final String TIME_ERROR_MESS = "Mã kích hoạt đã hết hạn";
    public static final String VERIFIED_MESS = "Tài khoản của bạn đã được kích hoạt trước đó";
    public static final String VALID_MESS = "Kích hoạt thành công";
    public static final String EXCEPTION_ERROR_MESS = "Đã có lỗi xảy ra";

}