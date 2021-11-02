/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EduSys.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class XDate {
    public static final SimpleDateFormat formater = new SimpleDateFormat();
    //chuuyển String sang Date
    /*
    @param date truyền vào date kiểu String
    @param pattern truyền vào kiểu
    return trả về date kiểu Date
    */
    public static Date toDate(String date,String pattern){
        try {
           formater.applyPattern(pattern);
           return formater.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex); 
        }
    }
    
    public static String toString(Date date, String pattern){
        try {
            formater.applyPattern(pattern);
           return formater.format(date);
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
    }

    public static Date addDays(Date date, long days){
        date.setTime(date.getTime()+ days*24*60*60*1000);
        return date;
    }
//    public static boolean checkNgayKG(JDateChooser date, StringBuilder sb) {
//        try {
//            if (date.getDate() == null) {
//                sb.append("Khai giảng chưa nhập").append("\n");
//                return false;
//            } else if (date.getDate().before(new Date())) {
//                sb.append("Ngày khai giảng phải sau ngày hôm nay").append("\n");
//                return false;
//            } else {
//                return true;
//            }
//        } catch (Exception e) {
//            sb.append("Ngày khai giảng không hợp lệ").append("\n");
//            return false;
//        }
//    }
//
//    public static boolean checkNgaySinh(JDateChooser date, StringBuilder sb) {
//        try {
//            if (date.getDate() == null) {
//                sb.append("Ngày sinh chưa nhập").append("\n");
//                return false;
//            } else if (date.getDate().after(new Date())) {
//                sb.append("Ngày sinh không hợp lệ").append("\n");
//                return false;
//            } else {
//                return true;
//            }
//        } catch (Exception e) {
//            sb.append("Ngày sinh không hợp lệ").append("\n");
//            return false;
//        }
//    }
}
