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
import EduSys.entity.ChuyenDe;

public class ChuyenDeDAO extends EduSysDAO<ChuyenDe, String> {
    String insertSql = "INSERT CHUYENDE(MaCD,TenCD,HocPhi,ThoiLuong,Hinh,MoTa,idlist) VALUES(?,?,?,?,?,?,1)";
    String updateSql = "UPDATE CHUYENDE SET TenCD=?, HocPhi=?,ThoiLuong=?,Hinh=?,MoTa=? where MaCD=?";
    String deleteSql = "DELETE FROM CHUYENDE WHERE MaCD=?";
    String selectAll = "SELECT*FROM CHUYENDE ";
    String selectById = "SELECT*FROM CHUYENDE WHERE MaCD=?";
    String xoaTamThoi = "update ChuyenDe set IDlist=? where MaCD=?";
    String selectIdList = "SELECT*FROM CHUYENDE where idlist=?";
    public void xoaTamThoi(String idlist, String macd) {
        jdbcHelper.Update(xoaTamThoi, idlist, macd);
    }
    public List<ChuyenDe> selectIdList(String id) {
        return this.selectBySql(selectIdList,id);
    }

    @Override
    public void insert(ChuyenDe entity) {
        jdbcHelper.Update(insertSql, entity.getMaCD(),
                entity.getTenCD(), entity.getHocPhi(),
                entity.getThoiLuong(), entity.getHinh(),
                entity.getMoTa());
    }

    @Override
    public void update(ChuyenDe entity) {
        jdbcHelper.Update(updateSql,
                entity.getTenCD(), entity.getHocPhi(),
                entity.getThoiLuong(), entity.getHinh(),
                entity.getMoTa(), entity.getMaCD());
    }

    @Override
    public void delete(String id) {
        jdbcHelper.Update(deleteSql, id);
    }

    @Override
    public List<ChuyenDe> selectAll() {
        return this.selectBySql(selectAll);
    }

    @Override
    public ChuyenDe selectById(String key) {
        List<ChuyenDe> list = this.selectBySql(selectById, key);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<ChuyenDe> selectBySql(String sql, Object... args) {
        List<ChuyenDe> list = new ArrayList<>();
        try {
            ResultSet rs = jdbcHelper.query(sql, args);
            while (rs.next()) {
                ChuyenDe entity = new ChuyenDe();
                entity.setMaCD(rs.getString("MaCD"));
                entity.setTenCD(rs.getString("TenCD"));
                entity.setHocPhi(rs.getBigDecimal("HocPhi"));
                entity.setThoiLuong(rs.getInt("ThoiLuong"));
                entity.setHinh(rs.getString("hinh"));
                entity.setMoTa(rs.getString("MoTa"));
                entity.setIdList(rs.getBoolean("idList"));
                list.add(entity);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
