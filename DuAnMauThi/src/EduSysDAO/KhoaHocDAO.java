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
import EduSys.entity.KhoaHoc;
import java.util.Date;


public class KhoaHocDAO extends EduSysDAO<KhoaHoc, String> {

    String insertSql = "INSERT KHOAHOC(MaCD,HocPhi,ThoiLuong,NgayKG,GhiChu,MaNV) VALUES(?,?,?,?,?,?)";
    String updateSql = "UPDATE KHOAHOC SET MaCD=?, HocPhi=?, ThoiLuong=?, NgayKG=?, GhiChu=?, MaNV=?, NgayTao=?  where MaKH=?";
    String deleteSql = "DELETE FROM KHOAHOC WHERE MaKH=?";
    String selectAll = "SELECT*FROM KHOAHOC ";
    String selectById = "SELECT*FROM KHOAHOC WHERE MaCD=?";
    String xoaTamThoi = "update KHOAHOC set IDlist=? where Makh=?";
    String selectbyIDList= "SELECT*FROM KHOAHOC WHERE IDLIST=? and MAcd=?";
    
//    public  List<KhoaHoc> selectbyIDList(String id,String macd ){
//        return this.selectBySql(selectbyIDList, id,macd);
//    }
        
    public void xoaTamThoi(String idlist, String Makh) {
        jdbcHelper.Update(xoaTamThoi, idlist, Makh);
    }
    
    public List<KhoaHoc> selectByChuyenDe(String macd){
        String sql="SELECT *FROM KHOAHOC WHERE MACD=?";
        return this.selectBySql(sql, macd);
    }
    public List<KhoaHoc> selectByChuyenDe(String macd,String id){
        String sql="SELECT *FROM KHOAHOC WHERE MACD=? and IDlist=?";
        return this.selectBySql(sql, macd,id);
    }
    @Override
    public void insert(KhoaHoc entity) {
        jdbcHelper.Update(insertSql, entity.getMaCD(), entity.getHocPhi(),
                entity.getThoiLuong(), entity.getNgayKG(), entity.getGhiChu(),
                entity.getMaNV());
    }

    @Override
    public void update(KhoaHoc entity) {
        jdbcHelper.Update(updateSql, entity.getMaCD(), entity.getHocPhi(),
                entity.getThoiLuong(), entity.getNgayKG(), entity.getGhiChu(),
                entity.getMaNV(), entity.getNgayTao(), entity.getMaKH());
    }

    @Override
    public void delete(String id) {
        jdbcHelper.Update(deleteSql, id );
    }

    @Override
    public List<KhoaHoc> selectAll() {
      return this.selectBySql(selectAll);
    }

    @Override
    public KhoaHoc selectById(String key) {
        List<KhoaHoc> list = this.selectBySql(selectById, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<KhoaHoc> selectBySql(String sql, Object... args) {
       List<KhoaHoc> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            while (rs.next()) {
                KhoaHoc entity = new KhoaHoc();
                entity.setMaKH(rs.getInt("MaKH"));
                entity.setMaCD(rs.getString("MaCD"));
                entity.setHocPhi(rs.getFloat("HocPhi"));
                entity.setThoiLuong(rs.getInt("ThoiLuong"));
                entity.setNgayKG(rs.getDate("NgayKG"));
                entity.setGhiChu(rs.getString("GhiChu"));
                entity.setMaNV(rs.getString("MaNV"));
                entity.setNgayTao(rs.getDate("NgayTao"));
                entity.setIdList(rs.getBoolean("IDList"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Integer> selectYear(){
        String sql="select year(NgayKG) from KhoaHoc  group by(year(NgayKG))";
        List<Integer> listNam= new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql);
            while (rs.next()) {                
                listNam.add(rs.getInt(1));
            }
        } catch (Exception e) {
        }
        return listNam;
    }

}
