package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import DAO.DAO_KhachHang;
import model.KhachHang;
import model.ChiTietPhanQuyen; 
import java.util.List;
import java.util.ArrayList;
import java.util.Date; // <<< THÊM IMPORT
import com.toedter.calendar.JDateChooser;

public class QuanLyKhachHang extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTen, txtSdt, txtDiaChi, txtTongTien, txtTimKiem;
    private JDateChooser dateNgaySinh;
    private JComboBox<String> cboStatus, cboGioiTinh;
    private JButton btnThem, btnSua, btnLamMoi, btnTraCuu;
    private DAO_KhachHang khachHangDAO;
    private DecimalFormat df = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private ChiTietPhanQuyen permission;
    
    public QuanLyKhachHang() {
        khachHangDAO = new DAO_KhachHang();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(236, 240, 241));
        
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadData();
        generateNextId();
    }
    
    public QuanLyKhachHang(ChiTietPhanQuyen permission) {
        this(); // Gọi constructor gốc để khởi tạo giao diện
        this.permission = permission;
        applyPermissions(); // Áp dụng quyền
    }
    
    private void applyPermissions() {
        if (permission != null) {
            btnThem.setEnabled(permission.isDuocThem());
            btnSua.setEnabled(permission.isDuocSua());
        } else {
            // Nếu không có quyền (lỗi), vô hiệu hóa hết
            btnThem.setEnabled(false);
            btnSua.setEnabled(false);
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(52, 73, 94));
        panel.add(lblTitle, BorderLayout.CENTER);

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setBackground(new Color(52, 152, 219));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBack.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBack.setBackground(new Color(52, 152, 219));
            }
        });

        btnBack.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame instanceof MAINFRAME) {
                ((MAINFRAME) frame).showMainMenuPanel();
            }
        });

        panel.add(btnBack, BorderLayout.WEST);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(380, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        JLabel lblFormTitle = new JLabel("THÔNG TIN KHÁCH HÀNG");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(46, 204, 113));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblFormTitle, gbc);
        gbc.gridwidth = 1;
        
        // Mã khách hàng
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mã khách hàng:"), gbc);
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        formPanel.add(txtId, gbc);
        
        // Tên khách hàng
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tên khách hàng:"), gbc);
        txtTen = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtTen, gbc);
        
        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày sinh:"), gbc);
        dateNgaySinh = new JDateChooser();
        dateNgaySinh.setDateFormatString("dd/MM/yyyy");
        dateNgaySinh.setPreferredSize(new Dimension(150, 25));
        dateNgaySinh.setMaxSelectableDate(new Date()); // <<< SỬA: Thêm ràng buộc
        gbc.gridx = 1;
        formPanel.add(dateNgaySinh, gbc);
        
        // Giới tính
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        gbc.gridx = 1;
        formPanel.add(cboGioiTinh, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        txtSdt = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtSdt, gbc);
        
        // Địa chỉ
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        txtDiaChi = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtDiaChi, gbc);
        
        // Tổng tiền
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Tổng tiền:"), gbc);
        txtTongTien = new JTextField(15);
        txtTongTien.setEditable(false);
        txtTongTien.setBackground(new Color(240, 240, 240));
        txtTongTien.setText("0");
        gbc.gridx = 1;
        formPanel.add(txtTongTien, gbc);
        
        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        cboStatus = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});
        gbc.gridx = 1;
        formPanel.add(cboStatus, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        btnThem = createStyledButton("Thêm", new Color(46, 204, 113));
        btnSua = createStyledButton("Sửa", new Color(52, 152, 219));
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnLamMoi);
        
        btnThem.addActionListener(e -> themKhachHang());
        btnSua.addActionListener(e -> suaKhachHang());
        btnLamMoi.addActionListener(e -> lamMoi());
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 35));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        // Panel tra cứu
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(236, 240, 241));
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lblSearch);
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtTimKiem);
        
        btnTraCuu = createStyledButton("Tìm", new Color(41, 128, 185));
        btnTraCuu.setPreferredSize(new Dimension(100, 30));
        searchPanel.add(btnTraCuu);
        
        btnTraCuu.addActionListener(e -> traCuuTheoSDT(true));
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    traCuuTheoSDT(true);
                } else {
                    traCuuTheoSDT(false); 
                }
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Bảng dữ liệu
        String[] columns = {"Mã khách hàng", "Tên khách hàng", "Ngày sinh", "Giới tính",
                           "Số điện thoại", "Địa chỉ", "Tổng tiền", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);
        
        // Sự kiện click vào bảng
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    hienThiThongTin(row);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void generateNextId() {
        List<KhachHang> list = khachHangDAO.getAll();
        int maxId = 0;
        
        for (KhachHang kh : list) {
            String id = kh.getIdKH();
            if (id != null && id.startsWith("KH")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        
        txtId.setText("KH" + String.format("%03d", maxId + 1));
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<KhachHang> list = khachHangDAO.getAll();
        
        for (KhachHang kh : list) {
            tableModel.addRow(new Object[]{
                kh.getIdKH(),
                kh.getTenKH(),
                kh.getNgaySinh() != null ? sdf.format(kh.getNgaySinh()) : "",
                kh.getGioiTinh(),
                kh.getSdt(),
                kh.getDiaChi(),
                df.format(kh.getTongTien()) + " đ",
                kh.getStatus()
            });
        }
    }
    
    private void themKhachHang() {
        if (!validateInput()) return;
        
        try {
            KhachHang kh = new KhachHang();
            kh.setIdKH(txtId.getText().trim());
            kh.setTenKH(txtTen.getText().trim());
            
            // Chuyển đổi java.util.Date sang java.sql.Date
            if (dateNgaySinh.getDate() != null) {
                kh.setNgaySinh(new java.sql.Date(dateNgaySinh.getDate().getTime()));
            } else {
                kh.setNgaySinh(null); // Đảm bảo là NULL nếu không chọn
            }
            
            kh.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
            kh.setSdt(txtSdt.getText().trim());
            
            // === ĐÃ SỬA: Xử lý địa chỉ NULL ===
            String diaChi = txtDiaChi.getText().trim();
            kh.setDiaChi(diaChi.isEmpty() ? null : diaChi);
            // ===================================
            
            kh.setTongTien(0);
            kh.setStatus(cboStatus.getSelectedItem().toString());
            
            // Kiểm tra SĐT đã tồn tại chưa
            if (khachHangDAO.getBySDT(kh.getSdt()) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Số điện thoại đã tồn tại trong hệ thống!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (khachHangDAO.insert(kh)) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void suaKhachHang() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            KhachHang kh = new KhachHang();
            kh.setIdKH(txtId.getText().trim());
            kh.setTenKH(txtTen.getText().trim());
            
            if (dateNgaySinh.getDate() != null) {
                kh.setNgaySinh(new java.sql.Date(dateNgaySinh.getDate().getTime()));
            } else {
                kh.setNgaySinh(null); // Đảm bảo là NULL nếu không chọn
            }
            
            kh.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
            kh.setSdt(txtSdt.getText().trim());
            
            // === ĐÃ SỬA: Xử lý địa chỉ NULL ===
            String diaChi = txtDiaChi.getText().trim();
            kh.setDiaChi(diaChi.isEmpty() ? null : diaChi);
            // ===================================
            
            // Giữ nguyên tổng tiền hiện tại
            String tongTienStr = txtTongTien.getText().replace(",", "").replace(" đ", "").trim();
            kh.setTongTien(Float.parseFloat(tongTienStr));
            
            kh.setStatus(cboStatus.getSelectedItem().toString());
            
            if (khachHangDAO.update(kh)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void traCuuTheoSDT(boolean showMessage) {
        String keyword = txtTimKiem.getText().trim();
        
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<KhachHang> allList = khachHangDAO.getAll();
        List<KhachHang> resultList = new ArrayList<>();
        
        String normalizedKeyword = removeAccent(keyword.toLowerCase());
        
        for (KhachHang kh : allList) {
            if (kh.getSdt() != null && kh.getSdt().contains(keyword)) {
                resultList.add(kh);
                continue;
            }
            
            if (kh.getTenKH() != null) {
                String normalizedName = removeAccent(kh.getTenKH().toLowerCase());
                if (normalizedName.contains(normalizedKeyword)) {
                    resultList.add(kh);
                }
            }
        }
        
        for (KhachHang kh : resultList) {
            tableModel.addRow(new Object[]{
                kh.getIdKH(),
                kh.getTenKH(),
                kh.getNgaySinh() != null ? sdf.format(kh.getNgaySinh()) : "",
                kh.getGioiTinh(),
                kh.getSdt(),
                kh.getDiaChi(),
                df.format(kh.getTongTien()) + " đ",
                kh.getStatus()
            });
        }
        
        if (resultList.isEmpty() && showMessage) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy khách hàng nào khớp với từ khóa: " + keyword, 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else if (resultList.size() == 1) {
            KhachHang kh = resultList.get(0);
            txtId.setText(kh.getIdKH());
            txtTen.setText(kh.getTenKH());
            dateNgaySinh.setDate(kh.getNgaySinh());
            cboGioiTinh.setSelectedItem(kh.getGioiTinh());
            txtSdt.setText(kh.getSdt());
            txtDiaChi.setText(kh.getDiaChi());
            txtTongTien.setText(String.valueOf((int)kh.getTongTien()));
            cboStatus.setSelectedItem(kh.getStatus());
            table.setRowSelectionInterval(0, 0);
        }
    }
    
    private String removeAccent(String s) {
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp.replaceAll("đ", "d").replaceAll("Đ", "D");
    }
    
    private void hienThiThongTin(int row) {
        txtId.setText(table.getValueAt(row, 0).toString());
        txtTen.setText(table.getValueAt(row, 1).toString());
        
        // Ngày sinh
        Object ngaySinhObj = table.getValueAt(row, 2);
        if (ngaySinhObj != null && !ngaySinhObj.toString().isEmpty()) {
            try {
                dateNgaySinh.setDate(sdf.parse(ngaySinhObj.toString()));
            } catch (Exception e) {
                dateNgaySinh.setDate(null);
            }
        } else {
            dateNgaySinh.setDate(null);
        }
        
        cboGioiTinh.setSelectedItem(table.getValueAt(row, 3).toString());
        txtSdt.setText(table.getValueAt(row, 4).toString());
        
        // Địa chỉ
        Object diaChiObj = table.getValueAt(row, 5);
        txtDiaChi.setText(diaChiObj != null ? diaChiObj.toString() : "");
        
        String tongTien = table.getValueAt(row, 6).toString();
        tongTien = tongTien.replace(",", "").replace(" đ", "").trim();
        txtTongTien.setText(tongTien);
        
        cboStatus.setSelectedItem(table.getValueAt(row, 7).toString());
        
        btnThem.setEnabled(false);
        if (permission != null) {
            btnSua.setEnabled(permission.isDuocSua());
        }
    }
    
    private void lamMoi() {
        generateNextId();
        txtTen.setText("");
        dateNgaySinh.setDate(null);
        cboGioiTinh.setSelectedIndex(0);
        txtSdt.setText("");
        txtDiaChi.setText("");
        txtTongTien.setText("0");
        txtTimKiem.setText("");
        cboStatus.setSelectedIndex(0);
        table.clearSelection();
        txtTen.requestFocus();
        loadData();
        
        applyPermissions();
    }
    
    private boolean validateInput() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng không được để trống!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }
        
        if (txtSdt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSdt.requestFocus();
            return false;
        }
        
        String sdt = txtSdt.getText().trim();
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, 
                "Số điện thoại phải có 10 chữ số!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSdt.requestFocus();
            return false;
        }
        
        // === ĐÃ XÓA: Khối kiểm tra địa chỉ bắt buộc đã được gỡ bỏ ===
        // if (txtDiaChi.getText().trim().isEmpty()) { ... }
        // =========================================================
        
        return true;
    }
}