/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EduSysDAO;

import EduSys.Helper.jdbcHelper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import EduSys.entity.NhanVien;

public class NhanVienDAO extends EduSysDAO<NhanVien, String> {

    String insertSql = "INSERT NHANVIEN(MaNV,MatKhau,HoTen,VaiTro,IDlist) VALUES(?,?,?,?,1)";
    String updateSql = "UPDATE NHANVIEN SET MatKhau=?, HoTen=?,VaiTro=? where MaNV=?";
    String deleteSql = "DELETE FROM NHANVIEN WHERE MaNV=?";
    String selectAll = "SELECT*FROM NHANVIEN";
    String selectById = "SELECT*FROM NHANVIEN WHERE MaNV=?";
    String xoaTamThoi = "update NHANVIEN set IDlist=? where MaNV=?";
    String selectIdList = "SELECT*FROM NHANVIEN where idlist=?";

    public void xoaTamThoi(String idlist, String macd) {
        jdbcHelper.Update(xoaTamThoi, idlist, macd);
    }

    public List<NhanVien> selectIdList(String id) {
        return this.selectBySql(selectIdList, id);
    }

    @Override
    public void insert(NhanVien entity) {
        jdbcHelper.Update(insertSql, entity.getMaNV(), entity.getMatKhau(), entity.getHoTen(), entity.isVaiTro());
    }

    @Override
    public void update(NhanVien entity) {
        jdbcHelper.Update(updateSql, entity.getMatKhau(), entity.getHoTen(), entity.isVaiTro(), entity.getMaNV());
    }

    @Override
    public void delete(String id) {
        jdbcHelper.Update(deleteSql, id);
    }

    @Override
    public List<NhanVien> selectAll() {
        return this.selectBySql(selectAll);
    }

    @Override
    public NhanVien selectById(String id) {
        List<NhanVien> list = this.selectBySql(selectById, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<NhanVien> selectBySql(String sql, Object... args) {
        List<NhanVien> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            while (rs.next()) {
                NhanVien entity = new NhanVien();
                entity.setMaNV(rs.getString("MaNV"));
                entity.setHoTen(rs.getString("HoTen"));
                entity.setMatKhau(rs.getString("MatKhau"));
                entity.setVaiTro(rs.getBoolean("Vaitro"));
                entity.setIdList(rs.getBoolean("IDlist"));
                list.add(entity);
//                System.out.println(list.size());
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
