/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Views;

import EduSys.entity.KhoaHoc;
import EduSys.entity.NguoiHoc;
import EduSys.utils.Auth;
import EduSys.utils.Msgbox;
import EduSysDAO.KhoaHocDAO;
import EduSysDAO.NguoiHocDAO;
import EduSysDAO.ThongKeDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author 
 */
public class THTKInternalFrame extends javax.swing.JInternalFrame {

    private ThongKeDAO daoThongke = new ThongKeDAO();
    private KhoaHocDAO daoKhoaHoc = new KhoaHocDAO();

    /**
     * Creates new form THTKInternalFrame
     */
    public THTKInternalFrame() {

        initComponents();
        if (!Auth.isManager()) {
            tabs.remove(3);
        }
        fillComboBoxKhoaHoc();
        fillComboBoxNam();
        fillTableNguoiHoc();
        fillTableBangDiem();
        fillTongHopDiem();
        fillTableDoanhThu();
    }

    public void fillComboBoxKhoaHoc() {
        List<KhoaHoc> listKH = daoKhoaHoc.selectAll();
        for (KhoaHoc khoaHoc : listKH) {
            cbbKhoaHoc.addItem(khoaHoc);
        }
    }

    public void fillComboBoxNam() {
        List<Integer> listNam = daoKhoaHoc.selectYear();
        for (Integer integer : listNam) {
            cboNam.addItem(integer + "");
        }
    }

    public void fillTableNguoiHoc() {
        List<Object[]> listNH = daoThongke.getLuongNguoiHoc();
        DefaultTableModel model = (DefaultTableModel) tblNguoiHoc.getModel();
        model.setRowCount(0);
        for (Object[] objects : listNH) {
            model.addRow(objects);
        }
    }

    public void fillTableBangDiem() {
        KhoaHoc kh = (KhoaHoc) cbbKhoaHoc.getSelectedItem();
        List<Object[]> listBangDiem = daoThongke.getBangDiem(kh.getMaKH());
        DefaultTableModel modelBangDiem = (DefaultTableModel) tblBangDiem.getModel();
        modelBangDiem.setRowCount(0);
        for (Object[] objects : listBangDiem) {
            modelBangDiem.addRow(objects);
        }
    }

    public void fillTongHopDiem() {
        List<Object[]> listTongHopDiem = daoThongke.thongKeDiem();
        DefaultTableModel modelTongHop = (DefaultTableModel) tblTongHop.getModel();
        modelTongHop.setRowCount(0);
        for (Object[] objects : listTongHopDiem) {
            modelTongHop.addRow(objects);
        }
    }

    public void fillTableDoanhThu() {
        DefaultTableModel modelDoanhThu = (DefaultTableModel) tblDoanhThu.getModel();
        modelDoanhThu.setRowCount(0);
        int nam = Integer.parseInt(cboNam.getSelectedItem().toString());
        List<Object[]> listDoanhThu = daoThongke.getDoanhThu(nam);
        for (Object[] objects : listDoanhThu) {
            modelDoanhThu.addRow(objects);
        }
        listDoanhThu.clear();
    }

    public void SetTab(int i) {
        tabs.setSelectedIndex(i);
    }
    public void xuatNguoiHoc() {
        XSSFWorkbook xs = new XSSFWorkbook();
        XSSFSheet sheet = xs.createSheet("Thống Kê Người Học");
        XSSFRow row = null;
        Cell cell = null;
        row = sheet.createRow(2);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("STT");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Năm");
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Số người học");
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Đầu tiên");
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Sau cùng");
        for (int i = 0; i < tblNguoiHoc.getRowCount(); i++) {
            row = sheet.createRow(3 + i);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(i + 1);
            cell = row.createCell(1, CellType.STRING);
            String nam = tblNguoiHoc.getValueAt(i, 0).toString();
            cell.setCellValue(nam);
            cell = row.createCell(2, CellType.STRING);
            String soNguoiHoc = tblNguoiHoc.getValueAt(i, 1).toString();
            cell.setCellValue(soNguoiHoc);
            cell = row.createCell(3, CellType.STRING);
            String dauTien = tblNguoiHoc.getValueAt(i, 2).toString();
            cell.setCellValue(dauTien);
            cell = row.createCell(4, CellType.STRING);
            String cuoiCung = tblNguoiHoc.getValueAt(i, 3).toString();
            cell.setCellValue(cuoiCung);
        }
        try {
            FormChinhJDialog formchinh = new FormChinhJDialog();
            String time = formchinh.getTime();
            File file = new File("‪‪NguoiHoc " + time + ".xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            xs.write(fos);
            fos.close();
            Msgbox.alert(this, "Xuất file thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
     public void xuatBangDiem() {
        XSSFWorkbook xs = new XSSFWorkbook();
        XSSFSheet sheet = xs.createSheet("Thống Kê Bảng Điểm " + cbbKhoaHoc.getSelectedItem().toString());
        XSSFRow row = null;
        Cell cell = null;
        row = sheet.createRow(2);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("STT");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("MaNH");
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Họ Tên");
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Điểm");
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Xếp loại");
        for (int i = 0; i < tblNguoiHoc.getRowCount(); i++) {
            row = sheet.createRow(3 + i);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(i + 1);
            cell = row.createCell(1, CellType.STRING);
            String MaNH = tblBangDiem.getValueAt(i, 0).toString();
            cell.setCellValue(MaNH);
            cell = row.createCell(2, CellType.STRING);
            String HoTen = tblBangDiem.getValueAt(i, 1).toString();
            cell.setCellValue(HoTen);
            cell = row.createCell(3, CellType.STRING);
            String diem = tblBangDiem.getValueAt(i, 2).toString();
            cell.setCellValue(diem);
            cell = row.createCell(4, CellType.STRING);
            String xepLoai = tblBangDiem.getValueAt(i, 3).toString();
            cell.setCellValue(xepLoai);
        }
        try {
            FormChinhJDialog formchinh = new FormChinhJDialog();
            String time = formchinh.getTime();
//            System.out.println(time);
            File file = new File("‪‪ThongKeBangDiem " + time + ".xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            xs.write(fos);
            fos.close();
            Msgbox.alert(this, "Xuất file thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void tongHopDiem() {
        XSSFWorkbook xs = new XSSFWorkbook();
        XSSFSheet sheet = xs.createSheet("Tổng Bảng Điểm ");
        XSSFRow row = null;
        Cell cell = null;
        row = sheet.createRow(2);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("STT");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Chuyên Đề");
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Tổng Số HV");
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Thấp Nhất");
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Cao Nhất");
        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Điểm Trung Bình");
        for (int i = 0; i < tblTongHop.getRowCount(); i++) {
            row = sheet.createRow(3 + i);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(i + 1);
            cell = row.createCell(1, CellType.STRING);
            String chuyenDe = tblTongHop.getValueAt(i, 0).toString();
            cell.setCellValue(chuyenDe);
            cell = row.createCell(2, CellType.STRING);
            String SoHV = tblTongHop.getValueAt(i, 1).toString();
            cell.setCellValue(SoHV);
            cell = row.createCell(3, CellType.STRING);
            String ThapNhat = tblTongHop.getValueAt(i, 2).toString();
            cell.setCellValue(ThapNhat);
            cell = row.createCell(4, CellType.STRING);
            String caoNhat = tblTongHop.getValueAt(i, 3).toString();
            cell.setCellValue(caoNhat);
            String trungBinh = tblTongHop.getValueAt(i, 4).toString();
            cell.setCellValue(trungBinh);
        }
        try {
            FormChinhJDialog formchinh = new FormChinhJDialog();
            String time = formchinh.getTime();
            File file = new File("‪‪TongHopDiem" + time + ".xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            xs.write(fos);
            fos.close();
            Msgbox.alert(this, "Xuất file thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    public void xuatDoanhThu() {
        XSSFWorkbook xs = new XSSFWorkbook();
        XSSFSheet sheet = xs.createSheet("Doanh Thu");
        XSSFRow row = null;
        Cell cell = null;
        row = sheet.createRow(2);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("STT");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Chuyên Đề");
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Số khóa");
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Số Học Viên");
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Doanh Thu");
        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Học Phí Cao Nhất");
        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Học Phí Thấp Nhất");
        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Học Phí Trung Bình");
        for (int i = 0; i < tblDoanhThu.getRowCount(); i++) {
            row = sheet.createRow(3 + i);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(i + 1);
            cell = row.createCell(1, CellType.STRING);
            String chuyenDe = tblDoanhThu.getValueAt(i, 0).toString();
            cell.setCellValue(chuyenDe);
            cell = row.createCell(2, CellType.STRING);
            String SoKhoa = tblDoanhThu.getValueAt(i, 1).toString();
            cell.setCellValue(SoKhoa);
            cell = row.createCell(3, CellType.STRING);
            String SoHv = tblDoanhThu.getValueAt(i, 2).toString();
            cell.setCellValue(SoHv);
            cell = row.createCell(4, CellType.STRING);
            String DoanhThu = tblDoanhThu.getValueAt(i, 3).toString();
            cell.setCellValue(DoanhThu);
            cell= row.createCell(5,CellType.STRING);
            String HpCaoNhat = tblDoanhThu.getValueAt(i, 4).toString();
            cell.setCellValue(HpCaoNhat);
            cell= row.createCell(6,CellType.STRING);
            String HpThapNhat= tblDoanhThu.getValueAt(i, 5).toString();
            cell.setCellValue(HpThapNhat);
            cell= row.createCell(7,CellType.STRING);
            String hpTrungBinh= tblDoanhThu.getValueAt(i, 6).toString();
            cell.setCellValue(hpTrungBinh);
        }
        try {
            FormChinhJDialog formchinh = new FormChinhJDialog();
            String time = formchinh.getTime();
            File file = new File("DoanhThu" + time + ".xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            xs.write(fos);
            fos.close();
            Msgbox.alert(this, "Xuất file thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        pnlNguoiHoc = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNguoiHoc = new javax.swing.JTable();
        pnlBangDiem = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBangDiem = new javax.swing.JTable();
        cbbKhoaHoc = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        pnlTongHopDiem = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTongHop = new javax.swing.JTable();
        pnlDoanhThu = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDoanhThu = new javax.swing.JTable();
        lblNam = new javax.swing.JLabel();
        cboNam = new javax.swing.JComboBox<>();
        lblTongHop = new javax.swing.JLabel();
        btnPrintExcel = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        tabs.setToolTipText("");

        tblNguoiHoc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NĂM", "SỐ NGƯỜI HỌC", "ĐẦU TIÊN", "SAU CÙNG"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblNguoiHoc);

        javax.swing.GroupLayout pnlNguoiHocLayout = new javax.swing.GroupLayout(pnlNguoiHoc);
        pnlNguoiHoc.setLayout(pnlNguoiHocLayout);
        pnlNguoiHocLayout.setHorizontalGroup(
            pnlNguoiHocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
        );
        pnlNguoiHocLayout.setVerticalGroup(
            pnlNguoiHocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNguoiHocLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("NGƯỜI HỌC", pnlNguoiHoc);

        tblBangDiem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MÃ NH", "HỌ VÀ TÊN", "ĐIỂM", "XẾP LOẠI"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblBangDiem);

        cbbKhoaHoc.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbbKhoaHocItemStateChanged(evt);
            }
        });

        jLabel1.setText("Khóa học");

        javax.swing.GroupLayout pnlBangDiemLayout = new javax.swing.GroupLayout(pnlBangDiem);
        pnlBangDiem.setLayout(pnlBangDiemLayout);
        pnlBangDiemLayout.setHorizontalGroup(
            pnlBangDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBangDiemLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(cbbKhoaHoc, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlBangDiemLayout.setVerticalGroup(
            pnlBangDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBangDiemLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlBangDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbbKhoaHoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabs.addTab("BẢNG ĐIỂM", pnlBangDiem);

        tblTongHop.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CHUYÊN ĐỀ", "TỔNG SỐ HV", "CAO NHẤT", "THẤP NHẤT", "ĐIỂM TB"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblTongHop);

        javax.swing.GroupLayout pnlTongHopDiemLayout = new javax.swing.GroupLayout(pnlTongHopDiem);
        pnlTongHopDiem.setLayout(pnlTongHopDiemLayout);
        pnlTongHopDiemLayout.setHorizontalGroup(
            pnlTongHopDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
        );
        pnlTongHopDiemLayout.setVerticalGroup(
            pnlTongHopDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTongHopDiemLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("TỔNG HỢP ĐIỂM", pnlTongHopDiem);

        tblDoanhThu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CHUYÊN ĐỀ", "SỐ KHÓA", "SỐ HV", "DOANH THU", "HP CAO NHẤT", "HP THẤP NHẤT", "HP TB"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblDoanhThu);

        lblNam.setText("NĂM");

        cboNam.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboNamItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlDoanhThuLayout = new javax.swing.GroupLayout(pnlDoanhThu);
        pnlDoanhThu.setLayout(pnlDoanhThuLayout);
        pnlDoanhThuLayout.setHorizontalGroup(
            pnlDoanhThuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
            .addGroup(pnlDoanhThuLayout.createSequentialGroup()
                .addComponent(lblNam, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboNam, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDoanhThuLayout.setVerticalGroup(
            pnlDoanhThuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDoanhThuLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDoanhThuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboNam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabs.addTab("DOANH THU", pnlDoanhThu);

        lblTongHop.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTongHop.setForeground(new java.awt.Color(0, 0, 204));
        lblTongHop.setText("TỔNG HỢP THỐNG KÊ");

        btnPrintExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Document.png"))); // NOI18N
        btnPrintExcel.setText("IN");
        btnPrintExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabs)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTongHop, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnPrintExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTongHop, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(btnPrintExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbbKhoaHocItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbbKhoaHocItemStateChanged
        fillTableBangDiem();
    }//GEN-LAST:event_cbbKhoaHocItemStateChanged

    private void cboNamItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboNamItemStateChanged
        fillTableDoanhThu();
    }//GEN-LAST:event_cboNamItemStateChanged

    private void btnPrintExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintExcelActionPerformed
        // TODO add your handling code here:
        try {
            switch (tabs.getSelectedIndex()) {
                case 0:
                    xuatNguoiHoc();
                    break;
                case 1:
                    xuatBangDiem();
                    break;
                case 2:
                    tongHopDiem();
                    break;
                case 3:
                    xuatDoanhThu();
                default:
                    break;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnPrintExcelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPrintExcel;
    private javax.swing.JComboBox<Object> cbbKhoaHoc;
    private javax.swing.JComboBox<String> cboNam;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblNam;
    private javax.swing.JLabel lblTongHop;
    private javax.swing.JPanel pnlBangDiem;
    private javax.swing.JPanel pnlDoanhThu;
    private javax.swing.JPanel pnlNguoiHoc;
    private javax.swing.JPanel pnlTongHopDiem;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JTable tblBangDiem;
    private javax.swing.JTable tblDoanhThu;
    private javax.swing.JTable tblNguoiHoc;
    private javax.swing.JTable tblTongHop;
    // End of variables declaration//GEN-END:variables
}
