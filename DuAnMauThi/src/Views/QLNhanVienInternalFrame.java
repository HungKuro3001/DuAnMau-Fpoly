/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import EduSys.entity.NhanVien;
import EduSys.utils.Auth;
import EduSys.utils.Msgbox;
import EduSysDAO.NhanVienDAO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class QLNhanVienInternalFrame extends javax.swing.JInternalFrame {

    private NhanVienDAO dao = new NhanVienDAO();
    private int row = -1;
    private List<NhanVien> listNhanVien;

    /**
     * Creates new form QLNhanVienInternalFrame
     */
    public QLNhanVienInternalFrame() {
        initComponents();
        if (!Auth.isManager()) {
            btnUpdate.setVisible(false);
            btnDelete.setVisible(false);
        }
        btnKhoiPhuc.setVisible(false);

        fillTable("1");
    }

    public void fillTable(String idlist) {
        DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
        model.setRowCount(0);
        try {
            listNhanVien = dao.selectIdList(idlist);
            for (NhanVien nhanVien : listNhanVien) {
                Object[] row = {
                    nhanVien.getMaNV(), "***", nhanVien.getHoTen(),
                    nhanVien.isVaiTro() ? "Trường phòng" : "Nhân Viên"
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            Msgbox.alert(this, "Lỗi truy vấn");
        }
    }

    public void setForm(NhanVien nv) {
        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        txtMatKhau.setText(nv.getMatKhau());
        rdoTruongPhong.setSelected(nv.isVaiTro());
        rdoNhanVien.setSelected(!nv.isVaiTro());
    }

    public NhanVien getform() {
        NhanVien nv = new NhanVien();
        nv.setMaNV(txtMaNV.getText());
        nv.setHoTen(txtHoTen.getText());
        nv.setMatKhau(txtMatKhau.getText());
        nv.setVaiTro(rdoTruongPhong.isSelected());
        return nv;
    }

    public void clearForm() {
        NhanVien nv = new NhanVien();
        this.setForm(nv);
        txtMaNV.setText("");
        txtMatKhau.setText("");
        txtXacNhanMK.setText("");

        this.row = -1;
        btnInsert.setVisible(true);
        txtMaNV.setEditable(true);
//        this.updateStatus();
    }

    public void edit() {
        String manv = (String) tblNhanVien.getValueAt(this.row, 0);
        NhanVien nv = dao.selectById(manv);
        this.setForm(nv);
        tabs.setSelectedIndex(0);
//        this.updateStatus();
    }

    public void insert() {
        NhanVien nv = getform();
        NhanVien nv2 = dao.selectById(nv.getMaNV() + "");
        if (nv2 != null) {
            if (nv2.isIdList() == false) {
                Msgbox.alert(this, "Mã nhân viên có trong danh sách đã xóa");
                return;
            }
        }

        if (nv2 != null) {
            Msgbox.alert(this, "Trùng mã nhân viên");
            return;
        }

        String mk2 = txtXacNhanMK.getText();
        if (!mk2.equals(nv.getMatKhau())) {
            Msgbox.alert(this, "Xác nhận mật khẩu không đúng");
            return;
        }
        if (nv.isVaiTro() == true && !Auth.isManager() == true) {
            Msgbox.alert(this, "Nhân viên không được thêm người có vai trò trưởng phòng");
            return;
        }

        try {
            dao.insert(nv);
            this.fillTable("1");
            this.clearForm();
            Msgbox.alert(this, "Thêm mới thành công");
        } catch (Exception e) {
            Msgbox.alert(this, "Thêm mới thất bại");
        }

    }

    public void update() {
        NhanVien nv = getform();
        NhanVien nv2 = dao.selectById(nv.getMaNV() + "");
        if (nv2.isVaiTro() == false && !Auth.isManager() == true) {
            Msgbox.alert(this, "Nhân viên không được cập nhật lên trưởng phòng");
            return;
        }
        String mk2 = txtXacNhanMK.getText();
        if (!mk2.equals(nv.getMatKhau())) {
            Msgbox.alert(this, "Xác nhận mật khẩu không đúng");
        } else {
            try {
                dao.update(nv);
                this.fillTable("1");
                Msgbox.alert(this, "Cập nhật thành công");
            } catch (Exception e) {
                Msgbox.alert(this, "Cập nhật thất bại");
            }
        }
    }

    public void deleteIDlist() {
        if (!Auth.isManager()) {
            Msgbox.alert(this, "Đăng nhập với tài khoản Admin để xóa");
        } else {
            String manv = txtMaNV.getText();
            if (manv.equals(Auth.user.getMaNV())) {
                Msgbox.alert(this, "Bạn không được xóa chính bạn");
            } else if (Msgbox.confirm(this, "Bạn có thực sự muốn xóa")) {
                try {
                    dao.xoaTamThoi("0", manv);
                    this.fillTable("1");
                    this.clearForm();
                    Msgbox.alert(this, "Xóa thành công");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void delete() {

        if (!Auth.isManager()) {
            Msgbox.alert(this, "Đăng nhập với tài khoản Admin để xóa");
        } else {
            int dong = tblNhanVien.getSelectedRow();
            NhanVien nhanvien = listNhanVien.get(dong);
            if (nhanvien.getMaNV().equals(Auth.user.getMaNV())) {
                Msgbox.alert(this, "Bạn không được xóa chính bạn");
            } else if (Msgbox.confirm(this, "Bạn có thực sự muốn xóa")) {
                try {
                    dao.delete(nhanvien.getMaNV());
                    this.fillTable("1");
                    this.clearForm();
                    Msgbox.alert(this, "Xóa thành công");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void first() {
        this.row = 0;
        this.edit();
    }

    public void prev() {
        if (this.row > 0) {
            this.row--;
            this.edit();
        }
    }

    public void next() {
        if (this.row < tblNhanVien.getRowCount() - 1) {
            this.row++;
            this.edit();
        }
    }

    public void last() {
        this.row = tblNhanVien.getRowCount() - 1;
        this.edit();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lblTitle = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        pnlEdit = new javax.swing.JPanel();
        lblMaNV = new javax.swing.JLabel();
        txtMaNV = new javax.swing.JTextField();
        lblMatKhau = new javax.swing.JLabel();
        lblXacNhanMK = new javax.swing.JLabel();
        lblHoTen = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        lblVaiTro = new javax.swing.JLabel();
        rdoTruongPhong = new javax.swing.JRadioButton();
        rdoNhanVien = new javax.swing.JRadioButton();
        btnInsert = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        txtMatKhau = new javax.swing.JPasswordField();
        txtXacNhanMK = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        pnlList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNhanVien = new javax.swing.JTable();
        btnNhanVienHT = new javax.swing.JToggleButton();
        btnNhanVienDaXoa1 = new javax.swing.JToggleButton();
        btnKhoiPhuc = new javax.swing.JToggleButton();
        btnDelete1 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(153, 0, 153));
        lblTitle.setText("QUẢN LÍ NHÂN VIÊN QUẢN TRỊ");
        getContentPane().add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 279, 26));

        tabs.setBackground(new java.awt.Color(153, 204, 255));

        pnlEdit.setBackground(new java.awt.Color(54, 33, 89));
        pnlEdit.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblMaNV.setForeground(new java.awt.Color(255, 255, 255));
        lblMaNV.setText("Mã nhân viên");
        pnlEdit.add(lblMaNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        txtMaNV.setBackground(new java.awt.Color(54, 33, 89));
        txtMaNV.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.add(txtMaNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 31, 485, -1));

        lblMatKhau.setForeground(new java.awt.Color(255, 255, 255));
        lblMatKhau.setText("Mật khẩu");
        pnlEdit.add(lblMatKhau, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, -1, -1));

        lblXacNhanMK.setForeground(new java.awt.Color(255, 255, 255));
        lblXacNhanMK.setText("Xác nhận mật khẩu");
        pnlEdit.add(lblXacNhanMK, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 113, -1, -1));

        lblHoTen.setForeground(new java.awt.Color(255, 255, 255));
        lblHoTen.setText("Họ và tên");
        pnlEdit.add(lblHoTen, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 164, -1, -1));

        txtHoTen.setBackground(new java.awt.Color(54, 33, 89));
        txtHoTen.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.add(txtHoTen, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 184, 485, -1));

        lblVaiTro.setForeground(new java.awt.Color(255, 255, 255));
        lblVaiTro.setText("Vai trò");
        pnlEdit.add(lblVaiTro, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 215, -1, -1));

        rdoTruongPhong.setBackground(new java.awt.Color(54, 33, 89));
        buttonGroup1.add(rdoTruongPhong);
        rdoTruongPhong.setForeground(new java.awt.Color(255, 255, 255));
        rdoTruongPhong.setSelected(true);
        rdoTruongPhong.setText("Trưởng Phòng");
        pnlEdit.add(rdoTruongPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 236, -1, -1));

        rdoNhanVien.setBackground(new java.awt.Color(54, 33, 89));
        buttonGroup1.add(rdoNhanVien);
        rdoNhanVien.setForeground(new java.awt.Color(255, 255, 255));
        rdoNhanVien.setText("Nhân viên");
        pnlEdit.add(rdoNhanVien, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 236, 93, -1));

        btnInsert.setText("Thêm");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });
        pnlEdit.add(btnInsert, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 321, -1, -1));

        btnUpdate.setText("Sửa");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        pnlEdit.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(77, 321, -1, -1));

        btnDelete.setText("Xóa");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnlEdit.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(134, 321, -1, -1));

        btnClear.setText("Mới");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        pnlEdit.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(191, 321, -1, -1));

        txtMatKhau.setBackground(new java.awt.Color(54, 33, 89));
        txtMatKhau.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.add(txtMatKhau, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 82, 485, -1));

        txtXacNhanMK.setBackground(new java.awt.Color(54, 33, 89));
        txtXacNhanMK.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.add(txtXacNhanMK, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 133, 485, -1));

        jPanel1.setBackground(new java.awt.Color(54, 33, 89));

        btnFirst.setText("|<");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPrev.setText("<<");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        btnNext.setText(">>");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setText(">|");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(btnFirst)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPrev)
                .addGap(24, 24, 24)
                .addComponent(btnNext)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLast))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFirst)
                    .addComponent(btnPrev)
                    .addComponent(btnNext)
                    .addComponent(btnLast))
                .addGap(35, 35, 35))
        );

        pnlEdit.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 300, -1, -1));

        tabs.addTab("CẬP NHẬT", pnlEdit);

        pnlList.setBackground(new java.awt.Color(54, 33, 89));
        pnlList.setForeground(new java.awt.Color(255, 255, 255));

        tblNhanVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ NV", "MẬT KHẨU", "HỌ VÀ TÊN", "VAI TRÒ"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNhanVienMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNhanVien);

        btnNhanVienHT.setText("Nhân viên hiện tại");
        btnNhanVienHT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNhanVienHTMouseClicked(evt);
            }
        });

        btnNhanVienDaXoa1.setText("Nhân viên đã xóa");
        btnNhanVienDaXoa1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNhanVienDaXoa1MouseClicked(evt);
            }
        });

        btnKhoiPhuc.setText("Khôi phục nhân viên");
        btnKhoiPhuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKhoiPhucActionPerformed(evt);
            }
        });

        btnDelete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trash2.png"))); // NOI18N
        btnDelete1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDelete1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlListLayout = new javax.swing.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlListLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
                    .addGroup(pnlListLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(btnKhoiPhuc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNhanVienHT)
                        .addGap(18, 18, 18)
                        .addComponent(btnNhanVienDaXoa1)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNhanVienDaXoa1)
                        .addComponent(btnNhanVienHT)
                        .addComponent(btnKhoiPhuc))
                    .addComponent(btnDelete1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(20, 20, 20))
        );

        tabs.addTab("DANH SÁCH", pnlList);

        getContentPane().add(tabs, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 43, -1, 390));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/chuyende2.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 4, 810, 450));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
        first();
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        prev();
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        next();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        last();
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        if (txtMaNV.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mã nhân viên");
            return;
        }
        if (txtMatKhau.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mật khẩu");
            return;
        }
        if (txtXacNhanMK.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống xác nhận mật khẩu");
            return;
        }
        if (txtHoTen.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống tên");
        }
        insert();
        txtXacNhanMK.setText("");
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (txtMatKhau.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mật khẩu");
            return;
        }
        if (txtXacNhanMK.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống xác nhận mật khẩu");
            return;
        }
        if (txtHoTen.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống tên");
        }
        update();
        txtXacNhanMK.setText("");
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (!Auth.isManager()) {
            Msgbox.alert(this, "Đăng nhập với tài khoản Admin để xóa");
            return;
        }
        if (txtMaNV.getText().trim().isEmpty()) {
            Msgbox.alert(this, "Không để trống mã");
            return;
        }
        deleteIDlist();
        txtXacNhanMK.setText("");
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void tblNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNhanVienMouseClicked

        row = tblNhanVien.getSelectedRow();
        NhanVien nv = listNhanVien.get(row);
        if (evt.getClickCount() == 2) {
            setForm(nv);
            tabs.setSelectedIndex(0);
        }
        if (!Auth.isManager()) {
            if (nv.getMaNV().equals(Auth.user.getMaNV())) {
                btnUpdate.setVisible(true);
            } else {
                btnUpdate.setVisible(false);
            }
        }
        txtMaNV.setEditable(false);
        btnInsert.setVisible(false);


    }//GEN-LAST:event_tblNhanVienMouseClicked

    private void btnNhanVienHTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNhanVienHTMouseClicked
        fillTable("1");
        btnInsert.setVisible(true);
        btnUpdate.setVisible(true);
        btnDelete.setVisible(true);
        btnKhoiPhuc.setVisible(false);
    }//GEN-LAST:event_btnNhanVienHTMouseClicked

    private void btnNhanVienDaXoa1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNhanVienDaXoa1MouseClicked
        fillTable("0");
        btnInsert.setVisible(false);
        btnUpdate.setVisible(false);
        btnDelete.setVisible(false);
        btnKhoiPhuc.setVisible(true);
        btnClear.setVisible(false);
    }//GEN-LAST:event_btnNhanVienDaXoa1MouseClicked

    private void btnKhoiPhucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhoiPhucActionPerformed
        int dong = tblNhanVien.getSelectedRow();
        NhanVien nv = listNhanVien.get(dong);
        dao.xoaTamThoi("1", nv.getMaNV());
        fillTable("0");
    }//GEN-LAST:event_btnKhoiPhucActionPerformed

    private void btnDelete1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDelete1MouseClicked
        if (tblNhanVien.getSelectedRow() < 0) {
            Msgbox.alert(this, "Chọn chuyên đề cần xóa");
            return;
        }
        delete();
    }//GEN-LAST:event_btnDelete1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JLabel btnDelete1;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnInsert;
    private javax.swing.JToggleButton btnKhoiPhuc;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNext;
    private javax.swing.JToggleButton btnNhanVienDaXoa1;
    private javax.swing.JToggleButton btnNhanVienHT;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHoTen;
    private javax.swing.JLabel lblMaNV;
    private javax.swing.JLabel lblMatKhau;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVaiTro;
    private javax.swing.JLabel lblXacNhanMK;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JPanel pnlList;
    private javax.swing.JRadioButton rdoNhanVien;
    private javax.swing.JRadioButton rdoTruongPhong;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblNhanVien;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtMaNV;
    private javax.swing.JPasswordField txtMatKhau;
    private javax.swing.JPasswordField txtXacNhanMK;
    // End of variables declaration//GEN-END:variables
}
