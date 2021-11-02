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
import EduSys.entity.NguoiHoc;


public class NguoiHocDAO extends EduSysDAO<NguoiHoc, String>{
    String insertSql = "INSERT NGUOIHOC(MaNH,HoTen,NgaySinh,GioiTinh,DienThoai,Email,GhiChu,MaNV,NgayDK) VALUES(?,?,?,?,?,?,?,?,getdate())";
    String updateSql = "UPDATE NGUOIHOC SET HoTen=?, NgaySinh=?, GioiTinh=?, DienThoai=?, Email=?, GhiChu=?, MaNV=? where MaNH=?";
    String deleteSql = "DELETE FROM NGUOIHOC WHERE MaNH=?";
    String selectAll = "SELECT*FROM NGUOIHOC";
    String selectById = "SELECT*FROM NGUOIHOC WHERE MaNH=?";
    
    public List<NguoiHoc> selectByKeyword(String keyword){
        String sql="SELECT * FROM NGUOIHOC WHERE HoTen LIKE ?";
        return this.selectBySql(sql, "%"+keyword+"%");
    }
     @Override
    public void insert(NguoiHoc entity) {
        jdbcHelper.Update(insertSql, entity.getMaNH(), entity.getHoTen(), entity.getNgaySinh(),
                entity.isGioiTinh(), entity.getDienThoai(), entity.getEmail(),
                entity.getGhiChu(), entity.getMaNV());
    }

    @Override
    public void update(NguoiHoc entity) {
        jdbcHelper.Update(updateSql, entity.getHoTen(), entity.getNgaySinh(),
                entity.isGioiTinh(), entity.getDienThoai(), entity.getEmail(),
                entity.getGhiChu(), entity.getMaNV(),entity.getMaNH());
    }

    @Override
    public void delete(String id) {
        jdbcHelper.Update(deleteSql, id );
    }

    @Override
    public List<NguoiHoc> selectAll() {
      return this.selectBySql(selectAll);
    }

    @Override
    public NguoiHoc selectById(String key) {
        List<NguoiHoc> list = this.selectBySql(selectById, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<NguoiHoc> selectBySql(String sql, Object... args) {
       List<NguoiHoc> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            while (rs.next()) {
                NguoiHoc entity= new NguoiHoc();
                entity.setMaNH(rs.getString("MaNH").trim());
                entity.setHoTen(rs.getString("HoTen"));
                entity.setNgaySinh(rs.getDate("NgaySinh"));
                entity.setGioiTinh(rs.getBoolean("GioiTinh"));
                entity.setDienThoai(rs.getString("DienThoai"));
                entity.setEmail(rs.getString("Email"));
                entity.setGhiChu(rs.getString("GhiChu"));
                entity.setMaNV(rs.getString("MaNV"));
                entity.setNgayDK(rs.getDate("NgayDK"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<NguoiHoc> selectNotlnCouse(int makh, String keyWord){
        String sql="SELECT * FROM NGUOIHOC WHERE HoTen like ? AND "
                   +"MaNH NOT IN (SELECT MaNH FROM HocVien where makh=?)";
        return this.selectBySql(sql, "%" +keyWord+"%",makh);
    }
    
}
