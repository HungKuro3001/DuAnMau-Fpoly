/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import EduSys.entity.NguoiHoc;
import EduSys.utils.Auth;
import EduSys.utils.Msgbox;
import EduSys.utils.XDate;
import EduSysDAO.HocVienDAO;
import EduSysDAO.NguoiHocDAO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;

public class QLNguoiHocInternalFrame extends javax.swing.JInternalFrame {

    private List<NguoiHoc> listNguoiHoc;
    private NguoiHocDAO daoNguoiHoc = new NguoiHocDAO();
    private HocVienDAO daoHocVien  = new HocVienDAO();
    private int row = -1;

    /**
     * Creates new form QLNguoiHocInternalFrame
     */
    public QLNguoiHocInternalFrame() {
        initComponents();
        fillTable();
    }

    public void fillTable() {
        DefaultTableModel model = (DefaultTableModel) tblNguoiHoc.getModel();
        model.setRowCount(0);
        try {
            String keyword = txtTimKiem.getText();
            listNguoiHoc = daoNguoiHoc.selectByKeyword(keyword);
            for (NguoiHoc nguoiHoc : listNguoiHoc) {
                Object row[] = {
                    nguoiHoc.getMaNH(), nguoiHoc.getHoTen(), nguoiHoc.isGioiTinh() == true ? "Nam" : "Nữ",
                    nguoiHoc.getNgaySinh(), nguoiHoc.getDienThoai(), nguoiHoc.getEmail(),
                    nguoiHoc.getMaNV(), nguoiHoc.getNgayDK()
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            Msgbox.alert(this, "Lỗi truy vấn");
        }
        if (listNguoiHoc == null) {
            Msgbox.alert(this, "Không có người học có tên: " + txtTimKiem.getText());
        }
    }

    public NguoiHoc getForm() {
//        this.row=tblNguoiHoc.getSelectedRow();
        NguoiHoc nguoiHoc = new NguoiHoc();
        nguoiHoc.setMaNH(txtMaNH.getText());
        nguoiHoc.setHoTen(txtHoTen.getText());
        if (rdoNam.isSelected() == true) {
            nguoiHoc.setGioiTinh(true);
        } else {
            nguoiHoc.setGioiTinh(false);
        }
        try {
            SimpleDateFormat formater = new SimpleDateFormat();
            formater.applyPattern("yyyy-MM-dd");
            Date date = formater.parse(txtNgaySinh.getText());
            nguoiHoc.setNgaySinh(date);
//            XDate.toDate(txtNgaySinh.getText(), "yyyy-MM-dd");
        } catch (Exception e) {
            Msgbox.alert(this, "Ngày theo định dạng:yyyy-MM-dd ");

        }

        nguoiHoc.setDienThoai(txtDienThoai.getText());
        nguoiHoc.setEmail(txtEmail.getText());
        nguoiHoc.setGhiChu(txtGhiChu.getText());
        nguoiHoc.setMaNV(Auth.user.getMaNV());
        return nguoiHoc;
    }

    public void setForm(NguoiHoc nguoiHoc) {
        txtHoTen.setText(nguoiHoc.getHoTen());
        txtMaNH.setText(nguoiHoc.getMaNH());
        txtEmail.setText(nguoiHoc.getEmail());
        txtDienThoai.setText(nguoiHoc.getDienThoai());
        txtGhiChu.setText(nguoiHoc.getGhiChu());
        txtNgaySinh.setText(nguoiHoc.getNgaySinh() + "");
        if (nguoiHoc.isGioiTinh() == true) {
            rdoNam.setSelected(true);
        } else {
            rdoNu.setSelected(true);
        }
    }

    public void clear() {
        
        txtHoTen.setText("");
        txtMaNH.setText("");
        txtEmail.setText("");
        txtDienThoai.setText("");
        txtGhiChu.setText("");
        txtNgaySinh.setText("");
        rdoNam.setSelected(true);
    }

    public void edit() {
//        row = tblNguoiHoc.getSelectedRow();
        NguoiHoc nguoiHoc = listNguoiHoc.get(row);
        setForm(nguoiHoc);
//        row = -1;
    }

    public void insert() {
        try {
            if (txtMaNH.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống mã");
                return;
            }
            NguoiHoc nh = daoNguoiHoc.selectById(txtMaNH.getText());
            if (nh != null) {
                Msgbox.alert(this, "Mã người học đã tồn tại");
                return;
            }
            if (txtHoTen.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống Họ tên");
                return;
            }
            if (txtNgaySinh.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống Ngày sinh");
                return;
            }
            try {
                SimpleDateFormat formater = new SimpleDateFormat();
                formater.applyPattern("yyyy-MM-dd");

                Date date = formater.parse(txtNgaySinh.getText());
                Date ngayht = formater.parse(java.time.LocalDate.now() + "");

                if (!(ngayht.compareTo(date) > 0)) {
                    Msgbox.alert(this, "Ngày sinh không được lớn hơn ngày hiện tại ");
                    return;
                }
            } catch (Exception e) {
                Msgbox.alert(this, "Ngày theo định dạng:yyyy-MM-dd ");
                return;
            }
            if (txtDienThoai.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống Điện thoại");
                return;
            }
            String sdt = "[0]{1}[0-9]{9}";
            if (!txtDienThoai.getText().matches(sdt)) {
                Msgbox.alert(this, "sai định dạng SĐT");
                return;
            }
            if (txtEmail.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống email");
                return;
            }
            String checkEmail = "[0-9a-zA-Z]{5,40}@fpt.edu.vn";
            if (!txtEmail.getText().matches(checkEmail)) {
                Msgbox.alert(this, "Email theo định dạng: Ten_DN@@fpt.edu.vn");
                return;
            }

            if (txtGhiChu.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống Ghi chú");
                return;
            }
            NguoiHoc nguoiHoc = getForm();
            daoNguoiHoc.insert(nguoiHoc);
            clear();
            Msgbox.alert(this, "Thêm thành công");
            this.fillTable();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void update() {
        try {
            if (txtMaNH.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống mã");
                return;
            }
            if (txtHoTen.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống Họ tên");
                return;
            }
            if (txtNgaySinh.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống Ngày sinh");
                return;
            }
            try {
                SimpleDateFormat formater = new SimpleDateFormat();
                formater.applyPattern("yyyy-MM-dd");

                Date date = formater.parse(txtNgaySinh.getText());
                Date ngayht = formater.parse(java.time.LocalDate.now() + "");

                if (!(ngayht.compareTo(date) > 0)) {
                    Msgbox.alert(this, "Ngày sinh không được lớn hơn ngày hiện tại ");
                    return;
                }
            } catch (Exception e) {
                Msgbox.alert(this, "Ngày theo định dạng:yyyy-MM-dd ");
                return;
            }
            if (txtDienThoai.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống Điện thoại");
                return;
            }
            String sdt = "[0]{1}[0-9]{9}";
            if (!txtDienThoai.getText().matches(sdt)) {
                Msgbox.alert(this, "sai định dạng SĐT");
                return;
            }
            if (txtEmail.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống email");
                return;
            }
            String checkEmail = "[0-9a-zA-Z]{5,40}@fpt.edu.vn";
            if (!txtEmail.getText().matches(checkEmail)) {
                Msgbox.alert(this, "Email theo định dạng: Ten_DN@@fpt.edu.vn");
                return;
            }

            if (txtGhiChu.getText().length() == 0) {
                Msgbox.alert(this, "Không để trống Ghi chú");
                return;
            }
            NguoiHoc nguoiHoc = getForm();
            daoNguoiHoc.update(nguoiHoc);
            clear();
            Msgbox.alert(this, "Cập nhật thành công");
            this.fillTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        try {
            daoHocVien.deleteTheoMaNH(txtMaNH.getText());

            daoNguoiHoc.delete(txtMaNH.getText());
            clear();
            Msgbox.alert(this, "Xóa thành công");
            this.fillTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void first() {
        row = 0;
        txtMaNH.setEditable(false);
        this.edit();
    }

    public void prev() {
        txtMaNH.setEditable(false);
        if (this.row > 0) {
            this.row--;
            this.edit();
        }
    }

    public void next() {
        txtMaNH.setEditable(false);
        if (this.row < tblNguoiHoc.getRowCount() - 1) {
            this.row++;
            this.edit();
        }
    }

    public void last() {
        txtMaNH.setEditable(false);
        this.row = tblNguoiHoc.getRowCount() - 1;
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
        jPanel1 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        pnlEdit = new javax.swing.JPanel();
        lblMaNH = new javax.swing.JLabel();
        txtMaNH = new javax.swing.JTextField();
        lblHoTen = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        lblGioiTinh = new javax.swing.JLabel();
        lblDienThoai = new javax.swing.JLabel();
        txtDienThoai = new javax.swing.JTextField();
        lblGhiChu = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtGhiChu = new javax.swing.JTextArea();
        lblNgaySinh = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtNgaySinh = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        rdoNam = new javax.swing.JRadioButton();
        rdoNu = new javax.swing.JRadioButton();
        btnInsert = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JLabel();
        btnDelete = new javax.swing.JLabel();
        btnClear = new javax.swing.JLabel();
        btnfisrt = new javax.swing.JLabel();
        btnPrev = new javax.swing.JLabel();
        btnNext = new javax.swing.JLabel();
        BtnLast = new javax.swing.JLabel();
        pnlList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNguoiHoc = new javax.swing.JTable();
        pnlTimKiem = new javax.swing.JPanel();
        txtTimKiem = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(54, 33, 89));

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(153, 0, 153));
        lblTitle.setText("QUẢN LÍ NGƯỜI HỌC");

        tabs.setBackground(new java.awt.Color(153, 204, 255));

        pnlEdit.setBackground(new java.awt.Color(54, 33, 89));

        lblMaNH.setBackground(new java.awt.Color(54, 33, 89));
        lblMaNH.setForeground(new java.awt.Color(255, 255, 255));
        lblMaNH.setText("Mã người học");

        txtMaNH.setBackground(new java.awt.Color(54, 33, 89));
        txtMaNH.setForeground(new java.awt.Color(255, 255, 255));

        lblHoTen.setBackground(new java.awt.Color(54, 33, 89));
        lblHoTen.setForeground(new java.awt.Color(255, 255, 255));
        lblHoTen.setText("Họ và tên");

        txtHoTen.setBackground(new java.awt.Color(54, 33, 89));
        txtHoTen.setForeground(new java.awt.Color(255, 255, 255));

        lblGioiTinh.setBackground(new java.awt.Color(54, 33, 89));
        lblGioiTinh.setForeground(new java.awt.Color(255, 255, 255));
        lblGioiTinh.setText("Giới tính");

        lblDienThoai.setBackground(new java.awt.Color(54, 33, 89));
        lblDienThoai.setForeground(new java.awt.Color(255, 255, 255));
        lblDienThoai.setText("Điện thoại");

        txtDienThoai.setBackground(new java.awt.Color(54, 33, 89));
        txtDienThoai.setForeground(new java.awt.Color(255, 255, 255));

        lblGhiChu.setBackground(new java.awt.Color(54, 33, 89));
        lblGhiChu.setForeground(new java.awt.Color(255, 255, 255));
        lblGhiChu.setText("Ghi chú");

        txtGhiChu.setBackground(new java.awt.Color(54, 33, 89));
        txtGhiChu.setColumns(20);
        txtGhiChu.setForeground(new java.awt.Color(255, 255, 255));
        txtGhiChu.setRows(5);
        jScrollPane2.setViewportView(txtGhiChu);

        lblNgaySinh.setBackground(new java.awt.Color(54, 33, 89));
        lblNgaySinh.setForeground(new java.awt.Color(255, 255, 255));
        lblNgaySinh.setText("Ngày sinh");

        lblEmail.setBackground(new java.awt.Color(54, 33, 89));
        lblEmail.setForeground(new java.awt.Color(255, 255, 255));
        lblEmail.setText("Địa chỉ email");

        txtNgaySinh.setBackground(new java.awt.Color(54, 33, 89));
        txtNgaySinh.setForeground(new java.awt.Color(255, 255, 255));

        txtEmail.setBackground(new java.awt.Color(54, 33, 89));
        txtEmail.setForeground(new java.awt.Color(255, 255, 255));

        rdoNam.setBackground(new java.awt.Color(54, 33, 89));
        buttonGroup1.add(rdoNam);
        rdoNam.setForeground(new java.awt.Color(255, 255, 255));
        rdoNam.setText("Nam");

        rdoNu.setBackground(new java.awt.Color(54, 33, 89));
        buttonGroup1.add(rdoNu);
        rdoNu.setForeground(new java.awt.Color(255, 255, 255));
        rdoNu.setText("Nữ");

        btnInsert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/more.png"))); // NOI18N
        btnInsert.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnInsertMouseClicked(evt);
            }
        });

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/system-update.png"))); // NOI18N
        btnUpdate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnUpdateMouseClicked(evt);
            }
        });

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trash2.png"))); // NOI18N
        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteMouseClicked(evt);
            }
        });

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/new (1).png"))); // NOI18N
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnClearMouseClicked(evt);
            }
        });

        btnfisrt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/first.png"))); // NOI18N
        btnfisrt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnfisrtMouseClicked(evt);
            }
        });

        btnPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/prebutton.png"))); // NOI18N
        btnPrev.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPrevMouseClicked(evt);
            }
        });

        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/next-button.png"))); // NOI18N
        btnNext.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNextMouseClicked(evt);
            }
        });

        BtnLast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/lastButton.png"))); // NOI18N
        BtnLast.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BtnLastMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlEditLayout = new javax.swing.GroupLayout(pnlEdit);
        pnlEdit.setLayout(pnlEditLayout);
        pnlEditLayout.setHorizontalGroup(
            pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(txtMaNH)
                    .addComponent(txtHoTen)
                    .addGroup(pnlEditLayout.createSequentialGroup()
                        .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblGioiTinh)
                            .addComponent(lblDienThoai)
                            .addComponent(txtDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlEditLayout.createSequentialGroup()
                                .addComponent(rdoNam, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rdoNu, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(38, 38, 38)
                        .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNgaySinh)
                            .addGroup(pnlEditLayout.createSequentialGroup()
                                .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblEmail)
                                    .addComponent(lblNgaySinh))
                                .addGap(0, 226, Short.MAX_VALUE))
                            .addComponent(txtEmail)))
                    .addGroup(pnlEditLayout.createSequentialGroup()
                        .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMaNH)
                            .addComponent(lblHoTen)
                            .addComponent(lblGhiChu))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlEditLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btnUpdate)
                .addGap(28, 28, 28)
                .addComponent(btnDelete)
                .addGap(28, 28, 28)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75)
                .addComponent(btnfisrt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPrev)
                .addGap(41, 41, 41)
                .addComponent(btnNext)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtnLast)
                .addGap(22, 22, 22))
        );
        pnlEditLayout.setVerticalGroup(
            pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEditLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblMaNH)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaNH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblHoTen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGioiTinh)
                    .addComponent(lblNgaySinh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdoNam)
                    .addComponent(rdoNu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDienThoai)
                    .addComponent(lblEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblGhiChu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(pnlEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnInsert, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnfisrt)
                    .addComponent(btnPrev)
                    .addComponent(btnNext)
                    .addComponent(BtnLast))
                .addGap(25, 25, 25))
        );

        tabs.addTab("CẬP NHẬT", pnlEdit);

        pnlList.setBackground(new java.awt.Color(54, 33, 89));

        tblNguoiHoc.setBackground(new java.awt.Color(54, 33, 89));
        tblNguoiHoc.setForeground(new java.awt.Color(255, 255, 255));
        tblNguoiHoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ NH", "HỌ VÀ TÊN", "GIỚI TÍNH", "NGÀY SINH", "ĐIỆN THOẠI", "EMAIL", "MÃ NV", "NGÀY ĐK"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblNguoiHoc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNguoiHocMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNguoiHoc);

        pnlTimKiem.setBackground(new java.awt.Color(54, 33, 89));
        pnlTimKiem.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TÌM KIẾM", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        pnlTimKiem.setForeground(new java.awt.Color(153, 0, 153));

        txtTimKiem.setBackground(new java.awt.Color(54, 33, 89));
        txtTimKiem.setForeground(new java.awt.Color(255, 255, 255));
        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTimKiemKeyReleased(evt);
            }
        });

        btnTimKiem.setText("Tìm");
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlTimKiemLayout = new javax.swing.GroupLayout(pnlTimKiem);
        pnlTimKiem.setLayout(pnlTimKiemLayout);
        pnlTimKiemLayout.setHorizontalGroup(
            pnlTimKiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTimKiemLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTimKiem)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        pnlTimKiemLayout.setVerticalGroup(
            pnlTimKiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTimKiemLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlTimKiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimKiem))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlListLayout = new javax.swing.GroupLayout(pnlList);
        pnlList.setLayout(pnlListLayout);
        pnlListLayout.setHorizontalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addComponent(pnlTimKiem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlListLayout.setVerticalGroup(
            pnlListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlListLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(pnlTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("DANH SÁCH", pnlList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(tabs)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabs))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 540, 480));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/student-with-graduation-cap.png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 120, 190, 240));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        fillTable();
        clear();
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void tblNguoiHocMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNguoiHocMouseClicked
        row = tblNguoiHoc.getSelectedRow();
        NguoiHoc ng = listNguoiHoc.get(row);
        if (evt.getClickCount() == 2) {
            setForm(ng);
            tabs.setSelectedIndex(0);
            txtMaNH.setEditable(false);
            btnInsert.setVisible(false);
        }

    }//GEN-LAST:event_tblNguoiHocMouseClicked

    private void txtTimKiemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTimKiemKeyReleased
        fillTable();
    }//GEN-LAST:event_txtTimKiemKeyReleased

    private void btnInsertMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnInsertMouseClicked

        insert();
    }//GEN-LAST:event_btnInsertMouseClicked

    private void btnUpdateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUpdateMouseClicked

        update();
    }//GEN-LAST:event_btnUpdateMouseClicked

    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if (!Auth.isManager()) {
            Msgbox.alert(this, "Đăng nhập với tài khoản Admin để xóa");
            return;
        }
        if (txtMaNH.getText().length() == 0) {
            Msgbox.alert(this, "Không để trống mã chuyên đề");
            return;
        }
        delete();
    }//GEN-LAST:event_btnDeleteMouseClicked

    private void btnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnClearMouseClicked
        clear();
        txtMaNH.setEditable(true);
        btnInsert.setVisible(true);
    }//GEN-LAST:event_btnClearMouseClicked

    private void btnfisrtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnfisrtMouseClicked
        txtMaNH.setEditable(false);
        btnInsert.setVisible(false);
        first();
    }//GEN-LAST:event_btnfisrtMouseClicked

    private void btnPrevMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnPrevMouseClicked
        txtMaNH.setEditable(false);
        btnInsert.setVisible(false);
        prev();
    }//GEN-LAST:event_btnPrevMouseClicked

    private void btnNextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNextMouseClicked
        txtMaNH.setEditable(false);
        btnInsert.setVisible(false);
        next();
    }//GEN-LAST:event_btnNextMouseClicked

    private void BtnLastMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnLastMouseClicked
        txtMaNH.setEditable(false);
        btnInsert.setVisible(false);
        last();
    }//GEN-LAST:event_BtnLastMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BtnLast;
    private javax.swing.JLabel btnClear;
    private javax.swing.JLabel btnDelete;
    private javax.swing.JLabel btnInsert;
    private javax.swing.JLabel btnNext;
    private javax.swing.JLabel btnPrev;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JLabel btnUpdate;
    private javax.swing.JLabel btnfisrt;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDienThoai;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGhiChu;
    private javax.swing.JLabel lblGioiTinh;
    private javax.swing.JLabel lblHoTen;
    private javax.swing.JLabel lblMaNH;
    private javax.swing.JLabel lblNgaySinh;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlEdit;
    private javax.swing.JPanel pnlList;
    private javax.swing.JPanel pnlTimKiem;
    private javax.swing.JRadioButton rdoNam;
    private javax.swing.JRadioButton rdoNu;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblNguoiHoc;
    private javax.swing.JTextField txtDienThoai;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextArea txtGhiChu;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtMaNH;
    private javax.swing.JTextField txtNgaySinh;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
