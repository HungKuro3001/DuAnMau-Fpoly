/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import EduSys.entity.ChuyenDe;
import EduSys.entity.HocVien;
import EduSys.entity.KhoaHoc;
import EduSys.entity.NguoiHoc;
import EduSys.utils.Auth;
import EduSys.utils.Msgbox;
import EduSysDAO.ChuyenDeDAO;
import EduSysDAO.HocVienDAO;
import EduSysDAO.KhoaHocDAO;
import EduSysDAO.NguoiHocDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class QLHocVienInternalFrame extends javax.swing.JInternalFrame {

    private ChuyenDeDAO cdDao = new ChuyenDeDAO();
    private KhoaHocDAO khDao = new KhoaHocDAO();
    private NguoiHocDAO nhDao = new NguoiHocDAO();
    private HocVienDAO hvDao = new HocVienDAO();
    private List<ChuyenDe> listChuyenDe = new ArrayList<>();
    private List<KhoaHoc> listKhoaHoc = new ArrayList<>();
    private List<HocVien> listHocVien = new ArrayList<>();
    private List<NguoiHoc> listNguoiHoc = new ArrayList<>();

    /**
     * Creates new form QLHocVienInternalFrame
     */
    public QLHocVienInternalFrame() {
        initComponents();
        fillComboBoxChuyenDe();
        fillComboBoxKhoaHoc();
//        fillTableNguoiHoc();
    }

    public void fillComboBoxChuyenDe() {
        listChuyenDe = cdDao.selectIdList("1");
        for (ChuyenDe chuyenDe : listChuyenDe) {
            cboChuyenDe.addItem(chuyenDe);
        }
    }

    public void fillComboBoxKhoaHoc() {
        cboKhoaHoc.removeAllItems();
        ChuyenDe cd = (ChuyenDe) cboChuyenDe.getSelectedItem();
        listKhoaHoc = khDao.selectByChuyenDe(cd.getMaCD());
        for (KhoaHoc kh : listKhoaHoc) {
            cboKhoaHoc.addItem(kh);
        }
        fillTableHocVien();
        fillTableNguoiHoc();
    }

    public void fillTableHocVien() {
        DefaultTableModel modelTableHocVien = (DefaultTableModel) tblHocVien.getModel();
        modelTableHocVien.setRowCount(0);
        int i = 1;
        try {
            KhoaHoc kh = (KhoaHoc) cboKhoaHoc.getSelectedItem();
            if (kh != null) {
                listHocVien = hvDao.selectByKhoaHoc(kh.getMaKH());
                for (HocVien hv : listHocVien) {
                    String hoTen = nhDao.selectById(hv.getMaNH()).getHoTen();
                    Object row[] = {i++, hv.getMaHV(), hv.getMaNH(), hoTen, hv.getDiem() >= 0 ? hv.getDiem() : "null"
                    };
                    modelTableHocVien.addRow(row);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void fillTableNguoiHoc() {
        DefaultTableModel model = (DefaultTableModel) tblNguoiHoc.getModel();
        model.setRowCount(0);
        try {

            String keyword = txtTimKiem.getText();
            KhoaHoc kh = (KhoaHoc) cboKhoaHoc.getSelectedItem();
            if (kh != null) {
                listNguoiHoc = nhDao.selectNotlnCouse(kh.getMaKH(), keyword);
                for (NguoiHoc nguoiHoc : listNguoiHoc) {
                    Object row[] = {
                        nguoiHoc.getMaNH(), nguoiHoc.getHoTen(), nguoiHoc.isGioiTinh() == true ? "Nam" : "Nữ",
                        nguoiHoc.getNgaySinh(), nguoiHoc.getDienThoai(), nguoiHoc.getEmail(),
                        nguoiHoc.getMaNV(), nguoiHoc.getNgayDK()
                    };
                    model.addRow(row);
                }
            }

        } catch (Exception e) {
//            Msgbox.alert(this, "Lỗi truy vấn");
            e.printStackTrace();
        }

    }

    public void addHocVien() {
        KhoaHoc kh = (KhoaHoc) cboKhoaHoc.getSelectedItem();
        int makh = kh.getMaKH();
        HocVien hv = new HocVien();
        hv.setMaKH(makh);
        hv.setMaNH(tblNguoiHoc.getValueAt(tblNguoiHoc.getSelectedRow(), 0).toString());
        Msgbox.alert(this, "Thêm thành công");
        hvDao.insert(hv);
        fillTableNguoiHoc();
        fillTableHocVien();
    }

    public void updateHocVien() {
        for (int i = 0; i < tblHocVien.getRowCount(); i++) {
            String diem = tblHocVien.getValueAt(i, 4).toString();
            if (diem.equals("null")) {
            } else {
                try {
                    float diemSo = Float.parseFloat(diem);
                    if (diemSo < 0 || diemSo > 10) {
                        Msgbox.alert(this, "Điểm của học hiên thứ: " + (i + 1) + " không hợp lệ");
                        return;
                    }
                } catch (Exception e) {
                    Msgbox.alert(this, "Điểm của học hiên thứ: " + (i + 1) + " không hợp lệ");
                    return;
                }
                int mahv = (Integer) tblHocVien.getValueAt(i, 1);
                HocVien hv = hvDao.selectById(mahv + "");
                hv.setDiem(Float.parseFloat(tblHocVien.getValueAt(i, 4).toString()));
                hvDao.update(hv);
            }

        }
        Msgbox.alert(this, "Cập nhật thành công");
        fillTableHocVien();
    }

    public void deleteHocVien() {
        if (!Auth.isManager()) {
            Msgbox.alert(this, "Đăng nhập với tài khoản Admin để xóa");
            return;
        }
        HocVien hv = listHocVien.get(tblHocVien.getSelectedRow());
        hvDao.delete(hv.getMaHV() + "");
        Msgbox.alert(this, "Xóa thành công");
        fillTableHocVien();
        fillTableNguoiHoc();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlKhoaHoc = new javax.swing.JPanel();
        cboKhoaHoc = new javax.swing.JComboBox<>();
        pnlChuyenDe = new javax.swing.JPanel();
        cboChuyenDe = new javax.swing.JComboBox<>();
        tabs = new javax.swing.JTabbedPane();
        pnlHocVien = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblHocVien = new javax.swing.JTable();
        btnXoa = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        pnlNguoiHoc = new javax.swing.JPanel();
        pnlHVK2 = new javax.swing.JPanel();
        txtTimKiem = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNguoiHoc = new javax.swing.JTable();
        btnThem = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        pnlKhoaHoc.setBackground(new java.awt.Color(255, 255, 255));
        pnlKhoaHoc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "KHÓA HỌC", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(153, 0, 153))); // NOI18N

        cboKhoaHoc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboKhoaHocItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlKhoaHocLayout = new javax.swing.GroupLayout(pnlKhoaHoc);
        pnlKhoaHoc.setLayout(pnlKhoaHocLayout);
        pnlKhoaHocLayout.setHorizontalGroup(
            pnlKhoaHocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlKhoaHocLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboKhoaHoc, 0, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlKhoaHocLayout.setVerticalGroup(
            pnlKhoaHocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlKhoaHocLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboKhoaHoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlChuyenDe.setBackground(new java.awt.Color(255, 255, 255));
        pnlChuyenDe.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CHUYÊN ĐỀ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(153, 0, 153))); // NOI18N
        pnlChuyenDe.setForeground(new java.awt.Color(255, 255, 255));

        cboChuyenDe.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboChuyenDeItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlChuyenDeLayout = new javax.swing.GroupLayout(pnlChuyenDe);
        pnlChuyenDe.setLayout(pnlChuyenDeLayout);
        pnlChuyenDeLayout.setHorizontalGroup(
            pnlChuyenDeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChuyenDeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboChuyenDe, 0, 298, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlChuyenDeLayout.setVerticalGroup(
            pnlChuyenDeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChuyenDeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboChuyenDe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlHocVien.setBackground(new java.awt.Color(255, 255, 255));
        pnlHocVien.setForeground(new java.awt.Color(255, 255, 255));

        tblHocVien.setBackground(new java.awt.Color(54, 33, 89));
        tblHocVien.setForeground(new java.awt.Color(255, 255, 255));
        tblHocVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "TT", "MÃ HV", "MÃ NH", "HỌ TÊN", "ĐIỂM"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblHocVien);

        btnXoa.setText("XÓA KHỎI KHÓA HỌC");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnCapNhat.setText("CẬP NHẬT ĐIỂM");
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlHocVienLayout = new javax.swing.GroupLayout(pnlHocVien);
        pnlHocVien.setLayout(pnlHocVienLayout);
        pnlHocVienLayout.setHorizontalGroup(
            pnlHocVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHocVienLayout.createSequentialGroup()
                .addGroup(pnlHocVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE)
                    .addGroup(pnlHocVienLayout.createSequentialGroup()
                        .addGap(210, 210, 210)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(69, 69, 69)
                        .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlHocVienLayout.setVerticalGroup(
            pnlHocVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHocVienLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnlHocVienLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa)
                    .addComponent(btnCapNhat))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        tabs.addTab("HỌC VIÊN", pnlHocVien);

        pnlNguoiHoc.setBackground(new java.awt.Color(54, 33, 89));

        pnlHVK2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TÌM KIẾM", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTimKiemKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout pnlHVK2Layout = new javax.swing.GroupLayout(pnlHVK2);
        pnlHVK2.setLayout(pnlHVK2Layout);
        pnlHVK2Layout.setHorizontalGroup(
            pnlHVK2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHVK2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtTimKiem)
                .addContainerGap())
        );
        pnlHVK2Layout.setVerticalGroup(
            pnlHVK2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHVK2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tblNguoiHoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ NH", "HỌ VÀ TÊN", "GIỚI TÍNH", "NGÀY SINH", "ĐIỆN THOẠI", "EMAIL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblNguoiHoc);

        btnThem.setText("Thêm vào khóa học");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlNguoiHocLayout = new javax.swing.GroupLayout(pnlNguoiHoc);
        pnlNguoiHoc.setLayout(pnlNguoiHocLayout);
        pnlNguoiHocLayout.setHorizontalGroup(
            pnlNguoiHocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlNguoiHocLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlNguoiHocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 795, Short.MAX_VALUE)
                    .addGroup(pnlNguoiHocLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlHVK2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlNguoiHocLayout.setVerticalGroup(
            pnlNguoiHocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNguoiHocLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlHVK2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnThem)
                .addContainerGap())
        );

        tabs.addTab("NGƯỜI HỌC", pnlNguoiHoc);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlChuyenDe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlKhoaHoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlChuyenDe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlKhoaHoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        txtTimKiem.setText("");
        addHocVien();

    }//GEN-LAST:event_btnThemActionPerformed

    private void cboChuyenDeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboChuyenDeItemStateChanged
        listKhoaHoc.clear();
        fillComboBoxKhoaHoc();
    }//GEN-LAST:event_cboChuyenDeItemStateChanged

    private void cboKhoaHocItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboKhoaHocItemStateChanged
        fillTableHocVien();
        fillTableNguoiHoc();
    }//GEN-LAST:event_cboKhoaHocItemStateChanged

    private void txtTimKiemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemKeyReleased
        fillTableNguoiHoc();
    }//GEN-LAST:event_txtTimKiemKeyReleased

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatActionPerformed
        updateHocVien();
    }//GEN-LAST:event_btnCapNhatActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        deleteHocVien();
    }//GEN-LAST:event_btnXoaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<Object> cboChuyenDe;
    private javax.swing.JComboBox<Object> cboKhoaHoc;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnlChuyenDe;
    private javax.swing.JPanel pnlHVK2;
    private javax.swing.JPanel pnlHocVien;
    private javax.swing.JPanel pnlKhoaHoc;
    private javax.swing.JPanel pnlNguoiHoc;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblHocVien;
    private javax.swing.JTable tblNguoiHoc;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
