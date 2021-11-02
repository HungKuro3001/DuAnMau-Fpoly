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
import EduSys.entity.HocVien;
import EduSys.entity.NhanVien;

public class HocVienDAO extends EduSysDAO<HocVien, String> {

    String insertSql = "INSERT HOCVIEN(MaNH,MaKH) VALUES(?,?)";
    String updateSql = "UPDATE HOCVIEN SET Diem=? where MaHV=?";
    String deleteSql = "DELETE FROM HOCVIEN WHERE MaHV=?";
    String selectAll = "SELECT*FROM HOCVIEN";
    String selectById = "SELECT*FROM HOCVIEN WHERE MaHV=?";

    @Override
    public void insert(HocVien entity) {
        jdbcHelper.Update(insertSql, entity.getMaNH(), entity.getMaKH());
    }

    @Override
    public void update(HocVien entity) {
        jdbcHelper.Update(updateSql, entity.getDiem(), entity.getMaHV());
    }

    @Override
    public void delete(String id) {
        jdbcHelper.Update(deleteSql, id);
    }

    @Override
    public List<HocVien> selectAll() {
        return this.selectBySql(selectAll);
    }

    @Override
    public HocVien selectById(String key) {
        List<HocVien> list = this.selectBySql(selectById, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<HocVien> selectBySql(String sql, Object... args) {
        List<HocVien> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            while (rs.next()) {
                HocVien entity = new HocVien();
                entity.setMaHV(rs.getInt("MaHV"));
                entity.setMaKH(rs.getInt("MaKH"));
                entity.setMaNH(rs.getString("MaNH"));
                entity.setDiem(rs.getFloat("Diem"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public List<HocVien> selectByKhoaHoc(int maKH) {
        String sql = "SELECT *FROM HOCVIEN WHERE MAKH=?";
        return this.selectBySql(sql, maKH);
    }
    String deleteTheoMaNh = "delete from HOCVIEN where MaNH=?";

    public void deleteTheoMaNH(String MaNH) {
        jdbcHelper.Update(deleteTheoMaNh, MaNH);
    }

}
