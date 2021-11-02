/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import EduSys.entity.ChuyenDe;
import EduSys.utils.Auth;
import EduSys.utils.Msgbox;
import EduSys.utils.XImage;
import EduSysDAO.ChuyenDeDAO;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author 
 */
public class QLChuyenDeInternalFrame extends javax.swing.JInternalFrame {

    private int row = -1;
    private ChuyenDeDAO dao = new ChuyenDeDAO();
    private List<ChuyenDe> listChuyenDe;

    /**
     * Creates new form QLiChuyenDeInternalFrame
     */
    public QLChuyenDeInternalFrame() {
        initComponents();
        fileChooser.setVisible(false);
        btnKhoiPhuc.setVisible(false);
        if (!Auth.isManager()) {
            btnDelete.setVisible(false);
            btnDelete1.setVisible(false);
        }
        fillTable("1");
    }

    public void fillTable(String id) {
        DefaultTableModel model = (DefaultTableModel) tblChuyenDe.getModel();
        model.setRowCount(0);
        try {
            listChuyenDe = dao.selectIdList(id);
            for (ChuyenDe chuyenDe1 : listChuyenDe) {
                Object row[] = {chuyenDe1.getMaCD(), chuyenDe1.getTenCD(),
                    chuyenDe1.getHocPhi(), chuyenDe1.getThoiLuong(),
                    chuyenDe1.getHinh()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            Msgbox.alert(this, "Lỗi truy vấn");

        }
    }

    public void chonAnh() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            XImage.save(file);
            ImageIcon icon = XImage.read(file.getName());
            lblImage.setIcon(icon);
            lblImage.setToolTipText(file.getName());
        }
    }

    public void setForm(ChuyenDe cd) {
        txtMaCD.setText(cd.getMaCD());
        txtTenCD.setText(cd.getTenCD());
        txtThoiLuong.setText(cd.getThoiLuong() + "");
        txtHocPhi.setText(cd.getHocPhi() + "");
        txtMoTa.setText(cd.getMoTa());
        if (cd.getHinh() != null) {
            lblImage.setToolTipText(cd.getHinh());
            lblImage.setIcon(XImage.read(cd.getHinh()));
        }
    }

    public ChuyenDe getForm() {
        ChuyenDe cd = new ChuyenDe();
        cd.setMaCD(txtMaCD.getText());
        cd.setTenCD(txtTenCD.getText());
        double hp = Double.parseDouble(txtHocPhi.getText());
        cd.setHocPhi(BigDecimal.valueOf(hp));
        cd.setMoTa(txtMoTa.getText());
        cd.setThoiLuong(Integer.parseInt(txtThoiLuong.getText()));
        cd.setHinh(lblImage.getToolTipText());
        return cd;
    }

    public void clearForm() {
//        ChuyenDe cd = new ChuyenDe();
//        this.setForm(cd);
        txtMaCD.setText("");
        txtHocPhi.setText("");
        txtMoTa.setText("");
        txtTenCD.setText("");
        txtThoiLuong.setText("");
        lblImage.setToolTipText(null);
        lblImage.setIcon(null);
        this.row = -1;
//        this.updateStatus();
    }

    public void edit() {
        String maCd = (String) tblChuyenDe.getValueAt(this.row, 0);

        ChuyenDe cd = dao.selectById(maCd);
        this.setForm(cd);
//        tabs.setSelectedIndex(0);
//        this.updateStatus();
    }

    public void insert() {
        ChuyenDe cd = getForm();
        ChuyenDe cd2 = dao.selectById(cd.getMaCD() + "");
        if (cd2 != null) {
            Msgbox.alert(this, "Trùng mã chuyên đề");
            return;
        }
        try {
            dao.insert(cd);
            this.fillTable("1");
            this.clearForm();
            Msgbox.alert(this, "Thêm mới thành công");
        } catch (Exception e) {
            Msgbox.alert(this, "Thêm mới thất bại");
            e.printStackTrace();
            return;
        }
    }

    public void update() {
        ChuyenDe cd = getForm();

        try {
            dao.update(cd);
            this.fillTable("1");
            Msgbox.alert(this, "Cập nhật thành công");
        } catch (Exception e) {
            e.printStackTrace();
//            Msgbox.alert(this, "Cập nhật thất bại");
        }

    }

    public void delete() {
        if (!Auth.isManager()) {
            Msgbox.alert(this, "Đăng nhập với tài khoản Admin để xóa");
        } else {
            if (Msgbox.confirm(this, "Bạn xác nhận xóa chuyên đề và toàn bộ thông tin liên quan đến chuyền đề này?")) {
                try {
                    int dong = tblChuyenDe.getSelectedRow();
                    String macd = tblChuyenDe.getValueAt(dong, 0).toString();
                    ChuyenDe cd = dao.selectById(macd);
                    if (cd != null) {
                        dao.delete(cd.getMaCD());
                        this.fillTable("1");
                        this.clearForm();
                        Msgbox.alert(this, "Xóa thành công");
                    } else {
                        Msgbox.alert(this, "Không tồn tại mã này");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteIDlist() {
        if (!Auth.isManager()) {
            Msgbox.alert(this, "Đăng nhập với tài khoản Admin để xóa");
        } else {
            if (Msgbox.confirm(this, "Bạn xác nhận xóa")) {
                try {
                    String macd = txtMaCD.getText();
                    ChuyenDe cd = dao.selectById(macd);
                    if (cd != null) {
                        dao.xoaTamThoi("0",cd.getMaCD());
                        this.fillTable("1");
                        this.clearForm();
                        Msgbox.alert(this, "Xóa thành công");
                    } else {
                        Msgbox.alert(this, "Không tồn tại mã này");
                    }

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
        if (this.row < tblChuyenDe.getRowCount() - 1) {
            this.row++;
            this.edit();
        }
    }

    public void last() {
        this.row = tblChuyenDe.getRowCount() - 1;
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

        jPanel1 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        pnlList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblChuyenDe = new javax.swing.JTable();
        btnDelete1 = new javax.swing.JLabel();
        btnChuyenDeHT = new javax.swing.JToggleButton();
        btnChuyenDeDaXoa1 = new javax.swing.JToggleButton();
        btnKhoiPhuc = new javax.swing.JToggleButton();
        pnlEdit = new javax.swing.JPanel();
        lblMaCD = new javax.swing.JLabel();
        txtMaCD = new javax.swing.JTextField();
        txtTenCD = new javax.swing.JTextField();
        lblTenCD = new javax.swing.JLabel();
        lblThoiLuong = new javax.swing.JLabel();
        txtThoiLuong = new javax.swing.JTextField();
        lblHocPhi = new javax.swing.JLabel();
        txtHocPhi = new javax.swing.JTextField();
        lblMoTa = new javax.swing.JLabel();
        lblHinh = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMoTa = new javax.swing.JTextArea();
        btnInsert = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JLabel();
        btnDelete = new javax.swing.JLabel();
        btnClear = new javax.swing.JLabel();
        btnfisrt = new javax.swing.JLabel();
        btnPrev = new javax.swing.JLabel();
        btnNext = new javax.swing.JLabel();
        BtnLast = new javax.swing.JLabel();
        fileChooser = new javax.swing.JFileChooser();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(54, 33, 89));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 51, 51));
        lblTitle.setText("QUẢN LÍ CHUYÊN ĐỀ");
        jPanel1.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 11, 278, 22));

        tabs.setBackground(new java.awt.Color(54, 33, 89));

        pnlList.setBackground(new java.awt.Color(54, 33, 89));

        tblChuyenDe.setAutoCreateRowSorter(true);
        tblChuyenDe.setBackground(new java.awt.Color(54, 33, 89));
        tblChuyenDe.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(153, 0, 153), new java.awt.Color(102, 0, 102)));
        tblChuyenDe.setForeground(new java.awt.Color(255, 255, 255));
        tblChuyenDe.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ CD", "TÊN CD", "HỌC PHÍ", "THỜI LƯỢNG", "HÌNH"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblChuyenDe.setGridColor(new java.awt.Color(255, 255, 255));
        tblChuyenDe.setSelectionForeground(new java.awt.Color(54, 33, 89));
        tblChuyenDe.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblChuyenDeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblChuyenDe);

        btnDelete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trash2.png"))); // NOI18N
        btnDelete1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDelete1MouseClicked(evt);
            }
        });

        btnChuyenDeHT.setText("Chuyên đề hiện tại");
        btnChuyenDeHT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnChuyenDeHTMouseClicked(evt);
            }
        });

        btnChuyenDeDaXoa1.setText("Chuyên đề đã xóa");
        btnChuyenDeDaXoa1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnChuyenDeDaXoa1MouseClicked(evt);
            }
        });

        btnKhoiPhuc.setText("Khôi phục chuyên đề");
        btnKhoiPhuc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKhoiPhucActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlListLayout = new javax.swing.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnKhoiPhuc)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnChuyenDeHT)
                        .addGap(18, 18, 18)
                        .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createSequentialGroup()
                                .addComponent(btnDelete1)
                                .addGap(26, 26, 26))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createSequentialGroup()
                                .addComponent(btnChuyenDeDaXoa1)
                                .addGap(105, 105, 105))))))
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDelete1)
                    .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnChuyenDeDaXoa1)
                        .addComponent(btnChuyenDeHT)
                        .addComponent(btnKhoiPhuc)))
                .addGap(26, 26, 26))
        );

        tabs.addTab("DANH SÁCH", pnlList);

        pnlEdit.setBackground(new java.awt.Color(54, 33, 89));
        pnlEdit.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblMaCD.setForeground(new java.awt.Color(255, 255, 255));
        lblMaCD.setText("Mã chuyên đề");
        pnlEdit.add(lblMaCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 11, -1, -1));

        txtMaCD.setBackground(new java.awt.Color(54, 33, 89));
        txtMaCD.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.add(txtMaCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 36, 370, -1));

        txtTenCD.setBackground(new java.awt.Color(54, 33, 89));
        txtTenCD.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.add(txtTenCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 99, 370, -1));

        lblTenCD.setForeground(new java.awt.Color(255, 255, 255));
        lblTenCD.setText("Tên chuyên đề");
        pnlEdit.add(lblTenCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, -1, -1));

        lblThoiLuong.setForeground(new java.awt.Color(255, 255, 255));
        lblThoiLuong.setText("Thời lượng (giờ)");
        pnlEdit.add(lblThoiLuong, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 125, -1, -1));

        txtThoiLuong.setBackground(new java.awt.Color(54, 33, 89));
        txtThoiLuong.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.add(txtThoiLuong, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 150, 370, -1));

        lblHocPhi.setBackground(new java.awt.Color(54, 33, 89));
        lblHocPhi.setForeground(new java.awt.Color(255, 255, 255));
        lblHocPhi.setText("Học phí");
        pnlEdit.add(lblHocPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 176, -1, -1));

        txtHocPhi.setBackground(new java.awt.Color(54, 33, 89));
        txtHocPhi.setForeground(new java.awt.Color(255, 255, 255));
        pnlEdit.add(txtHocPhi, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 201, 370, -1));

        lblMoTa.setForeground(new java.awt.Color(255, 255, 255));
        lblMoTa.setText("Mô tả chuyên đề");
        pnlEdit.add(lblMoTa, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, -1, -1));

        lblHinh.setForeground(new java.awt.Color(255, 255, 255));
        lblHinh.setText("Hình logo");
        pnlEdit.add(lblHinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, -1, -1));

        lblImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 204)));
        lblImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImageMouseClicked(evt);
            }
        });
        pnlEdit.add(lblImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 36, 160, 173));

        txtMoTa.setBackground(new java.awt.Color(54, 33, 89));
        txtMoTa.setColumns(20);
        txtMoTa.setForeground(new java.awt.Color(255, 255, 255));
        txtMoTa.setRows(5);
        jScrollPane2.setViewportView(txtMoTa);

        pnlEdit.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 245, 540, 93));

        btnInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/more.png"))); // NOI18N
        btnInsert.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnInsertMouseClicked(evt);
            }
        });
        pnlEdit.add(btnInsert, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 50, 30));

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/system-update.png"))); // NOI18N
        btnUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUpdateMouseClicked(evt);
            }
        });
        pnlEdit.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 360, -1, -1));

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trash2.png"))); // NOI18N
        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteMouseClicked(evt);
            }
        });
        pnlEdit.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 360, -1, -1));

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/new (1).png"))); // NOI18N
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnClearMouseClicked(evt);
            }
        });
        pnlEdit.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 360, 70, 40));

        btnfisrt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/first.png"))); // NOI18N
        btnfisrt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnfisrtMouseClicked(evt);
            }
        });
        pnlEdit.add(btnfisrt, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 360, -1, -1));

        btnPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/prebutton.png"))); // NOI18N
        btnPrev.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPrevMouseClicked(evt);
            }
        });
        pnlEdit.add(btnPrev, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 360, -1, -1));

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/next-button.png"))); // NOI18N
        btnNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNextMouseClicked(evt);
            }
        });
        pnlEdit.add(btnNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 360, -1, -1));

        BtnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/lastButton.png"))); // NOI18N
        BtnLast.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BtnLastMouseClicked(evt);
            }
        });
        pnlEdit.add(BtnLast, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 360, -1, -1));

        tabs.addTab("CẬP NHẬT", pnlEdit);

        jPanel1.add(tabs, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 41, 580, 440));
        jPanel1.add(fileChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 372, 245));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 840, 490));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImageMouseClicked

        fileChooser.setVisible(true);
        chonAnh();
    }//GEN-LAST:event_lblImageMouseClicked

    private void tblChuyenDeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblChuyenDeMouseClicked
        row = tblChuyenDe.getSelectedRow();
        ChuyenDe nv = listChuyenDe.get(row);
        if (evt.getClickCount() == 2) {
            tabs.setSelectedIndex(1);
            setForm(nv);
            txtMaCD.setEditable(false);
            btnInsert.setVisible(false);
        }
    }//GEN-LAST:event_tblChuyenDeMouseClicked

    private void btnInsertMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInsertMouseClicked
        if (txtMaCD.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mã chuyên đề");
            return;
        }
        if (txtTenCD.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống tên chuyên đề");
            return;
        }
        try {
            if (txtThoiLuong.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống thời lượng");
                return;
            }
            int tl = Integer.parseInt(txtThoiLuong.getText());
            if (tl < 0) {
                Msgbox.alert(this, "Thời lượng là một số nguyên dương");
                return;
            }
        } catch (Exception e) {
            Msgbox.alert(this, "Thời lượng là một số nguyên dương");
            return;
        }
        if (lblImage.getToolTipText() == null) {
            Msgbox.alert(this, "Chọn hình cho chuyên đề");
            return;
        }
        try {
            if (txtHocPhi.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống học phí");
                return;
            }
            double x = Double.parseDouble(txtHocPhi.getText());
            if (x <= 0) {
                Msgbox.alert(this, "Học phí >0");
                return;
            }
        } catch (Exception e) {
            Msgbox.alert(this, "Học phí là một số >=0");
            return;
        }
        if (txtMoTa.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mô tae");
            return;
        }

        ChuyenDe cd = dao.selectById(txtMaCD.getText());
        if (cd != null && cd.isIdList() == false) {
            Msgbox.alert(this, "Mã chuyên đề này còn dữ liêu, xóa hết dữ liệu liên quan để thêm mới");
            clearForm();
            return;

        }
        if (cd != null) {
            Msgbox.alert(this, "Mã chuyên đề này đã tồn tại");
            return;
        }
        insert();
        clearForm();
    }//GEN-LAST:event_btnInsertMouseClicked

    private void btnUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateMouseClicked
        if (txtMaCD.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mã chuyên đề");
            return;
        }
        if (txtTenCD.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống tên chuyên đề");
            return;
        }
        try {
            if (txtThoiLuong.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống thời lượng");
                return;
            }
            int tl = Integer.parseInt(txtThoiLuong.getText());
            if (tl <= 0) {
                Msgbox.alert(this, "Thời lượng >0");
                return;
            }
        } catch (Exception e) {
            Msgbox.alert(this, "Thời lượng là một số nguyên dương");
            return;
        }
        if (lblImage.getToolTipText() == null) {
            Msgbox.alert(this, "Chọn hình cho chuyên đề");
            return;
        }
        try {
            if (txtHocPhi.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống học phí");
                return;
            }
            double x = Double.parseDouble(txtHocPhi.getText());
            if (x <= 0) {
                Msgbox.alert(this, "Học phí >0");
                return;
            }
        } catch (Exception e) {
            Msgbox.alert(this, "Học phí là một số >=0");
            return;
        }
        if (txtMoTa.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mô tae");
            return;
        }
        update();
        clearForm();
    }//GEN-LAST:event_btnUpdateMouseClicked

    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if (txtMaCD.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mã chuyên đề");
            return;
        }
        deleteIDlist();
    }//GEN-LAST:event_btnDeleteMouseClicked

    private void btnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnClearMouseClicked
        clearForm();
        txtMaCD.setEditable(true);
        btnInsert.setVisible(true);
    }//GEN-LAST:event_btnClearMouseClicked

    private void btnfisrtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnfisrtMouseClicked
        first();
    }//GEN-LAST:event_btnfisrtMouseClicked

    private void btnPrevMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPrevMouseClicked
        prev();
    }//GEN-LAST:event_btnPrevMouseClicked

    private void btnNextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNextMouseClicked
        next();
    }//GEN-LAST:event_btnNextMouseClicked

    private void BtnLastMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnLastMouseClicked
        last();
    }//GEN-LAST:event_BtnLastMouseClicked

    private void btnDelete1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDelete1MouseClicked
        if (tblChuyenDe.getSelectedRow() < 0) {
            Msgbox.alert(this, "Chọn chuyên đề cần xóa");
            return;
        }
        delete();
    }//GEN-LAST:event_btnDelete1MouseClicked

    private void btnChuyenDeHTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnChuyenDeHTMouseClicked
        fillTable("1");
        btnInsert.setVisible(true);
        btnUpdate.setVisible(true);
        btnDelete.setVisible(true);
        btnKhoiPhuc.setVisible(false);
    }//GEN-LAST:event_btnChuyenDeHTMouseClicked

    private void btnChuyenDeDaXoa1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnChuyenDeDaXoa1MouseClicked
        fillTable("0");
        btnInsert.setVisible(false);
        btnUpdate.setVisible(false);
        btnDelete.setVisible(false);
        btnKhoiPhuc.setVisible(true);
    }//GEN-LAST:event_btnChuyenDeDaXoa1MouseClicked

    private void btnKhoiPhucActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhoiPhucActionPerformed
       int dong = tblChuyenDe.getSelectedRow();
        ChuyenDe cd = listChuyenDe.get(dong);
        dao.xoaTamThoi("1", cd.getMaCD());
        fillTable("0");
    }//GEN-LAST:event_btnKhoiPhucActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BtnLast;
    private javax.swing.JToggleButton btnChuyenDeDaXoa1;
    private javax.swing.JToggleButton btnChuyenDeHT;
    private javax.swing.JLabel btnClear;
    private javax.swing.JLabel btnDelete;
    private javax.swing.JLabel btnDelete1;
    private javax.swing.JLabel btnInsert;
    private javax.swing.JToggleButton btnKhoiPhuc;
    private javax.swing.JLabel btnNext;
    private javax.swing.JLabel btnPrev;
    private javax.swing.JLabel btnUpdate;
    private javax.swing.JLabel btnfisrt;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHinh;
    private javax.swing.JLabel lblHocPhi;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblMaCD;
    private javax.swing.JLabel lblMoTa;
    private javax.swing.JLabel lblTenCD;
    private javax.swing.JLabel lblThoiLuong;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JPanel pnlList;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblChuyenDe;
    private javax.swing.JTextField txtHocPhi;
    private javax.swing.JTextField txtMaCD;
    private javax.swing.JTextArea txtMoTa;
    private javax.swing.JTextField txtTenCD;
    private javax.swing.JTextField txtThoiLuong;
    // End of variables declaration//GEN-END:variables
}
