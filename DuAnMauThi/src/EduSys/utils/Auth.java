/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EduSys.utils;

import EduSys.entity.NhanVien;

/**
 *
 * @author Admin
 */
public class Auth {
    public static NhanVien user=null;
    public static void clear(){
        Auth.user=null;
    }
    public static boolean isLogin(){
        return Auth.user!=null;
    }
    public static boolean isManager(){
        return Auth.isLogin() && user.isVaiTro();
    }
}
