package com.spring.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidSomething {
    public static boolean isValidEmail(String email) {
        boolean err = false;
        // 첫번째 문자는  소문자 or 숫자 ; 그다음은 -_. 와도 됨 뒤에 com 은   2~7글자까지! 왜냐하면 도메인 끝이 company로 끝나는 도메인이 존재함.
//        String regex = "^[0-9a-zA-Z]([-_.]*?[0-9a-zA-Z])*@[0-9a-zA-Z가-힣]([-_.]?[0-9a-zA-Z가-힣])*.[a-zA-Z가-힣]{2,7}$";
        String regex = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidNumber(String number) {
        boolean err = false;
        String regex = "^[0-9]{10,11}$"; //숫자만
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(number);
        if (m.matches()) {
            err = true;
        }
        return err;
    }

    public static boolean isValidPassword(String password){
        boolean err = false;//                                       `~!@#$%^&*()_+
        String regex = "^.*(?=^.{8,32}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[.,/\\\\!@#$%^*+=-]).*$"; //숫자만
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (m.matches()) {
            err = true;
        }
        return err;
    }
    
    public static boolean isValidName(String name){
        boolean err = false;//
        //String regex = "^.*(?=^.{2,20}$)[가-힣0-9a-zA-Z].*$"; //숫자만\ //(?=.*\d)(?=.*[a-zA-Z])
//        String regex = "^.*(?=^.{2,20}$)([가-힣])*([a-zA-Z])*([0-9]).*$";
        String regex = "^.*(?=^.{2,14}$)([가-힣-a-z-A-Z])+[가-힣a-zA-Z0-9]*$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(name);
        if (m.matches()) {
            err = true;
        }
        return err;
    }
}