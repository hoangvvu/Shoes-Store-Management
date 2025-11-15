package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import DAO.*;
import model.*;
import com.toedter.calendar.JDateChooser; 
import javax.swing.border.TitledBorder; // THÊM MỚI

public class QuanLyHoaDon extends JPanel {
    private JTable tableHoaDon, tableChiTiet, tableSanPham;
    private DefaultTableModel modelHoaDon, modelChiTiet, modelSanPham;
    private JTextField txtIdHD, txtIdNV, txtIdKH, txtTenKH, txtSDT, txtTimKiem, txtSoLuong,txtTimKiemSP;
    private JLabel lblTongTien, lblThanhToan;
    
    // === SỬA ĐỔI: Thêm btnXemChiTietHD ===
    private JButton btnTaoMoi, btnThemSP, btnXoaSP, btnLuuHD, btnHuyHD, btnTraCuuKH, btnLamMoi, btnTimKiem, btnXuatHD, btnXoaHD, btnXemChiTietHD;
    
    private JComboBox<String> cboLocTrangThai;
    
    private DAO_HoaDon hoaDonDAO;
    private DAO_ChiTietHoaDon chiTietDAO;
    private DAO_KhachHang khachHangDAO;
    private DAO_Giay giayDAO;
    private DAO_NhanVien nhanVienDAO;
    
    private DecimalFormat df = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    private NhanVien currentUser;
    private String currentHoaDonId = "";
    private float tongTienHD = 0;
    
    public QuanLyHoaDon() {
        hoaDonDAO = new DAO_HoaDon();
        chiTietDAO = new DAO_ChiTietHoaDon();
        khachHangDAO = new DAO_KhachHang();
        giayDAO = new DAO_Giay();
        nhanVienDAO = new DAO_NhanVien();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(236, 240, 241));
        
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        
        loadDanhSachHoaDon(false);
        loadDanhSachSanPham();
        
        setFormTaoHoaDonEnabled(false);
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (txtTimKiemSP != null) {
                    txtTimKiemSP.setText(""); 
                }
                loadDanhSachSanPham(false);
            }
        });
    }
    
    public QuanLyHoaDon(NhanVien user) {
        this(); 
        this.currentUser = user;
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        }
        
        applyPermissions();
        setFormTaoHoaDonEnabled(false);
    }
    
    private void applyPermissions() {
        if (btnXoaHD == null) return;

        if (currentUser != null) {
            String roleId = "";
            try {
                roleId = currentUser.getIdPQ(); 
            } catch (Exception e) {
            }
            
            if ("PQ001".equalsIgnoreCase(roleId)) {
                btnXoaHD.setVisible(true); 
            } else {
                btnXoaHD.setVisible(false); 
            }
        } else {
            btnXoaHD.setVisible(false);
        }
    }
    
    private JPanel createTitlePanel() {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("QUẢN LÝ HÓA ĐƠN", SwingConstants.CENTER);
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

        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnBack.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(MouseEvent evt) {
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
    
    private JPanel createMainPanel() {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        // Left: Tạo hóa đơn
        JPanel leftPanel = createHoaDonPanel();
        leftPanel.setPreferredSize(new Dimension(450, 0));
        
        // Center: Danh sách hóa đơn
        JPanel centerPanel = createDanhSachHoaDonPanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createHoaDonPanel() {
        // ... (Không thay đổi) ...
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 126, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblHeader = new JLabel("TẠO HÓA ĐƠN MỚI");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.WEST);
        
        btnTaoMoi = createStyledButton("Tạo mới", new Color(46, 204, 113));
        btnTaoMoi.setPreferredSize(new Dimension(100, 30));
        btnTaoMoi.addActionListener(e -> taoHoaDonMoi());
        headerPanel.add(btnTaoMoi, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBackground(Color.WHITE);
        
        // Thông tin hóa đơn
        contentPanel.add(createThongTinHDPanel(), BorderLayout.NORTH);
        
        // Danh sách sản phẩm + Sản phẩm có sẵn
        JPanel middlePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(createChiTietHDPanel());
        middlePanel.add(createSanPhamPanel());
        contentPanel.add(middlePanel, BorderLayout.CENTER);
        
        // Tổng tiền và nút
        contentPanel.add(createTongTienPanel(), BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createThongTinHDPanel() {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Mã HĐ
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã hóa đơn:"), gbc);
        txtIdHD = new JTextField(10);
        txtIdHD.setEditable(false);
        txtIdHD.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtIdHD, gbc);
        
        // Mã NV
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mã nhân viên:"), gbc);
        txtIdNV = new JTextField(10);
        txtIdNV.setEditable(false);
        txtIdNV.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtIdNV, gbc);
        
        // Mã KH
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("SĐT KH:"), gbc);
        
        JPanel khPanel = new JPanel(new BorderLayout(5, 0));
        khPanel.setBackground(Color.WHITE);
        txtSDT = new JTextField(10);
        btnTraCuuKH = createStyledButton("Tra cứu", new Color(52, 152, 219));
        btnTraCuuKH.setPreferredSize(new Dimension(80, 25));
        btnTraCuuKH.addActionListener(e -> traCuuKhachHang());
        
        khPanel.add(txtSDT, BorderLayout.CENTER);
        khPanel.add(btnTraCuuKH, BorderLayout.EAST);
        gbc.gridx = 1;
        panel.add(khPanel, gbc);
        
        // Tên KH
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Tên KH:"), gbc);
        txtTenKH = new JTextField(10);
        txtTenKH.setEditable(false);
        txtTenKH.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtTenKH, gbc);
        
        // ID KH (ẩn)
        txtIdKH = new JTextField();
        
        return panel;
    }
    
    private JPanel createChiTietHDPanel() {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));
        
        String[] columns = {"Mã SP", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableChiTiet = new JTable(modelChiTiet);
        
        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        
        txtTimKiemSP = new JTextField(12);
        txtTimKiemSP.setToolTipText("Nhập tên hoặc mã sản phẩm...");
        
        JButton btnTimSP = createStyledButton("Tìm sản phẩm", new Color(52, 152, 219));
        
        btnTimSP.addActionListener(e -> {
            loadDanhSachSanPham(true); 
        });

        txtTimKiemSP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadDanhSachSanPham(true); 
                } else {
                    loadDanhSachSanPham(false);
                }
            }
        });
        
        btnXoaSP = createStyledButton("Xóa sản phẩm", new Color(231, 76, 60));
        btnXoaSP.addActionListener(e -> xoaSanPhamKhoiHD());
        
        btnPanel.add(new JLabel("Tìm sản phẩm:"));
        btnPanel.add(txtTimKiemSP);
        btnPanel.add(btnTimSP);
        btnPanel.add(Box.createHorizontalStrut(20));
        btnPanel.add(btnXoaSP);
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    
    private JPanel createSanPhamPanel() {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
        
        String[] columns = {"Mã", "Tên giày", "Size", "Giá bán", "Tồn kho", "Khả dụng"};
        modelSanPham = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableSanPham = new JTable(modelSanPham);
        tableSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableSanPham.setRowHeight(25);
        tableSanPham.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        tableSanPham.getColumnModel().getColumn(4).setMaxWidth(60);
        tableSanPham.getColumnModel().getColumn(5).setMaxWidth(60);
        
        JScrollPane scrollPane = new JScrollPane(tableSanPham);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(new JLabel("Số lượng:"));
        txtSoLuong = new JTextField(5);
        txtSoLuong.setText("1");
        btnPanel.add(txtSoLuong);
        
        btnThemSP = createStyledButton("Thêm vào hóa đơn", new Color(46, 204, 113));
        btnThemSP.addActionListener(e -> themSanPhamVaoHD());
        btnPanel.add(btnThemSP);
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTongTienPanel() {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel tongTienPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        tongTienPanel.setBackground(Color.WHITE);
        
        JLabel lbl1 = new JLabel("Tổng tiền:", SwingConstants.RIGHT);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien = new JLabel("0 đ", SwingConstants.LEFT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien.setForeground(new Color(230, 126, 34));
        
        JLabel lbl2 = new JLabel("Thanh toán:", SwingConstants.RIGHT);
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblThanhToan = new JLabel("0 đ", SwingConstants.LEFT);
        lblThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblThanhToan.setForeground(Color.RED);
        
        tongTienPanel.add(lbl1);
        tongTienPanel.add(lblTongTien);
        tongTienPanel.add(lbl2);
        tongTienPanel.add(lblThanhToan);
        
        panel.add(tongTienPanel, BorderLayout.NORTH);
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        btnLuuHD = createStyledButton("Lưu hóa đơn", new Color(46, 204, 113));
        btnLuuHD.setPreferredSize(new Dimension(0, 40));
        btnLuuHD.addActionListener(e -> luuHoaDon());
        
        btnHuyHD = createStyledButton("Hủy hóa đơn", new Color(231, 76, 60));
        btnHuyHD.setPreferredSize(new Dimension(0, 40));
        btnHuyHD.addActionListener(e -> huyHoaDon());
        
        btnPanel.add(btnLuuHD);
        btnPanel.add(btnHuyHD);
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDanhSachHoaDonPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblHeader = new JLabel("DANH SÁCH HÓA ĐƠN");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(lblHeader, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        searchPanel.add(new JLabel("Trạng thái:"));
        String[] options = {"Tất cả", "Đã thanh toán", "Chờ thanh toán"};
        cboLocTrangThai = new JComboBox<>(options);
        cboLocTrangThai.addActionListener(e -> loadDanhSachHoaDon(false)); 
        searchPanel.add(cboLocTrangThai);
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        searchPanel.add(txtTimKiem);
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadDanhSachHoaDon(true); 
                } else {
                    loadDanhSachHoaDon(false); 
                }
            }
        });
        
        btnTimKiem = createStyledButton("Tìm", new Color(52, 152, 219));
        btnTimKiem.setPreferredSize(new Dimension(80, 28));
        btnTimKiem.addActionListener(e -> loadDanhSachHoaDon(true)); 
        searchPanel.add(btnTimKiem);
        
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        btnLamMoi.setPreferredSize(new Dimension(100, 28)); 
        btnLamMoi.addActionListener(e -> {
            cboLocTrangThai.setSelectedIndex(0); 
            txtTimKiem.setText("");
            loadDanhSachHoaDon(false); 
        });
        searchPanel.add(btnLamMoi);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Bảng
        String[] columns = {"Mã hóa đơn", "Mã nhân viên", "Mã khách hàng", "Ngày lập", "Tổng tiền", "Trạng thái"};
        modelHoaDon = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableHoaDon = new JTable(modelHoaDon);
        tableHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableHoaDon.setRowHeight(30);
        tableHoaDon.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHoaDon.getTableHeader().setBackground(new Color(52, 73, 94));
        tableHoaDon.getTableHeader().setForeground(Color.WHITE);
        
        // === SỬA ĐỔI: Bỏ Double-Click, đổi thành Single-Click để kích hoạt nút ===
        tableHoaDon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableHoaDon.getSelectedRow();
                if (row != -1) {
                    // Kích hoạt các nút khi một hàng được chọn
                    btnXemChiTietHD.setEnabled(true);
                    btnXuatHD.setEnabled(true);
                    if (btnXoaHD.isVisible()) {
                        btnXoaHD.setEnabled(true);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // === SỬA ĐỔI: Thêm nút Xem Chi Tiết ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        
        btnXemChiTietHD = createStyledButton("Chi tiết hóa đơn", new Color(149, 165, 166));
        btnXemChiTietHD.setPreferredSize(new Dimension(150, 35));
        btnXemChiTietHD.addActionListener(e -> moDialogChiTietHD());
        btnXemChiTietHD.setEnabled(false); // Mặc định tắt
        bottomPanel.add(btnXemChiTietHD);
        
        btnXoaHD = createStyledButton("Xóa hóa đơn", new Color(231, 76, 60));
        btnXoaHD.setPreferredSize(new Dimension(150, 35));
        btnXoaHD.addActionListener(e -> xoaHoaDon());
        btnXoaHD.setVisible(false); 
        btnXoaHD.setEnabled(false); // Mặc định tắt
        bottomPanel.add(btnXoaHD);
        
        btnXuatHD = createStyledButton("Xác nhận thanh toán", new Color(41, 128, 185));
        btnXuatHD.setPreferredSize(new Dimension(180, 35));
        btnXuatHD.addActionListener(e -> xuatHoaDon());
        btnXuatHD.setEnabled(false); // Mặc định tắt
        bottomPanel.add(btnXuatHD);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setFormTaoHoaDonEnabled(boolean enabled) {
        // ... (Không thay đổi) ...
        txtSDT.setEnabled(enabled);
        btnTraCuuKH.setEnabled(enabled);
        txtSoLuong.setEnabled(enabled);
        btnThemSP.setEnabled(enabled);
        btnXoaSP.setEnabled(enabled);
        btnLuuHD.setEnabled(enabled);
        btnHuyHD.setEnabled(enabled);
        btnTaoMoi.setEnabled(!enabled);
        
        // === THÊM MỚI: Vô hiệu hóa các nút khi tạo hóa đơn ===
        btnXemChiTietHD.setEnabled(false);
        btnXuatHD.setEnabled(false);
        btnXoaHD.setEnabled(false);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        // ... (Không thay đổi) ...
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
    
    private void taoHoaDonMoi() {
        // ... (Không thay đổi) ...
        currentHoaDonId = generateNextHoaDonId();
        txtIdHD.setText(currentHoaDonId);
        
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        } else {
            txtIdNV.setText("NV001"); // Fallback
        }
        
        txtIdKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        modelChiTiet.setRowCount(0);
        tongTienHD = 0;
        updateTongTien();
        
        if (txtTimKiemSP != null) {
            txtTimKiemSP.setText(""); 
        }
        
        loadDanhSachSanPham(false);
        setFormTaoHoaDonEnabled(true);
        txtSDT.requestFocus();
        
        JOptionPane.showMessageDialog(this, "Đã tạo hóa đơn mới: " + currentHoaDonId,
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String generateNextHoaDonId() {
        // ... (Không thay đổi) ...
        List<HoaDon> list = hoaDonDAO.getAll();
        int maxId = 0;
        
        for (HoaDon hd : list) {
            String id = hd.getIdHD();
            if (id.startsWith("HD")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        
        return "HD" + String.format("%03d", maxId + 1);
    }
    
    private void traCuuKhachHang() {
        // ... (Không thay đổi) ...
        String sdt = txtSDT.getText().trim();
        
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT khách hàng!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, 
                "Số điện thoại phải có 10 chữ số!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return;
        }
        
        KhachHang kh = khachHangDAO.getBySDT(sdt);
        
        if (kh != null) {
            txtIdKH.setText(kh.getIdKH());
            txtTenKH.setText(kh.getTenKH());
        } else {
            int choice = JOptionPane.showConfirmDialog(this,
                "Không tìm thấy khách hàng. Bạn có muốn tạo mới?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                KhachHang newKH = showTaoKhachHangDialog(sdt);
                if (newKH != null) {
                    txtIdKH.setText(newKH.getIdKH());
                    txtTenKH.setText(newKH.getTenKH());
                }
            }
        }
    }
    
    private KhachHang showTaoKhachHangDialog(String sdt) {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Tên khách hàng:"), gbc);
        JTextField txtTen = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(txtTen, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        JTextField txtSdt = new JTextField(sdt);
        txtSdt.setEditable(false);
        txtSdt.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(txtSdt, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Ngày sinh:"), gbc);
        JDateChooser dateNgaySinh = new JDateChooser();
        dateNgaySinh.setDateFormatString("dd/MM/yyyy");
        dateNgaySinh.setPreferredSize(new Dimension(150, 25));
        
        dateNgaySinh.setMaxSelectableDate(new Date());
        
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(dateNgaySinh, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Giới tính:"), gbc);
        JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(cboGioiTinh, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Địa chỉ:"), gbc);
        JTextField txtDiaChi = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(txtDiaChi, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tạo khách hàng mới", 
                                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String ten = txtTen.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            List<KhachHang> list = khachHangDAO.getAll();
            int maxId = 0;
            for (KhachHang kh : list) {
                String id = kh.getIdKH();
                if (id.startsWith("KH")) {
                    try {
                        int num = Integer.parseInt(id.substring(2));
                        if (num > maxId) maxId = num;
                    } catch (NumberFormatException e) {
                    }
                }
            }
            String newId = "KH" + String.format("%03d", maxId + 1);

            KhachHang kh = new KhachHang();
            kh.setIdKH(newId);
            kh.setTenKH(ten);
            kh.setSdt(sdt);
            
            String diaChi = txtDiaChi.getText().trim();
            kh.setDiaChi(diaChi.isEmpty() ? null : diaChi); 
            
            if (dateNgaySinh.getDate() != null) {
                kh.setNgaySinh(new java.sql.Date(dateNgaySinh.getDate().getTime()));
            } else {
                kh.setNgaySinh(null); 
            }
            
            kh.setGioiTinh(cboGioiTinh.getSelectedItem().toString()); 
            kh.setTongTien(0);
            kh.setStatus("Hoạt động"); 

            if (khachHangDAO.insert(kh)) {
                JOptionPane.showMessageDialog(this, "Đã tạo khách hàng mới!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                return kh; 
            } else {
                 JOptionPane.showMessageDialog(this, "Lỗi khi tạo khách hàng!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        
        return null; 
    }
    
    private void themSanPhamVaoHD() {
        // ... (Không thay đổi) ...
        if (currentHoaDonId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo hóa đơn mới trước!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int row = tableSanPham.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String maSP = tableSanPham.getValueAt(row, 0).toString();
            String tenSP = tableSanPham.getValueAt(row, 1).toString();
            String giaStr = tableSanPham.getValueAt(row, 3).toString().replace(",", "").replace(" đ", "");
            float donGia = Float.parseFloat(giaStr);
            
            int soLuongKhaDung = Integer.parseInt(tableSanPham.getValueAt(row, 5).toString());
            
            int soLuongTrongGio = 0;
            for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                if (modelChiTiet.getValueAt(i, 0).toString().equals(maSP)) {
                    soLuongTrongGio += Integer.parseInt(modelChiTiet.getValueAt(i, 2).toString());
                }
            }
            
            if (soLuong + soLuongTrongGio > soLuongKhaDung) {
                JOptionPane.showMessageDialog(this, 
                    "Số lượng vượt quá hàng khả dụng!\n\n" +
                    "Hàng khả dụng: " + soLuongKhaDung + "\n" +
                    "Đã thêm (HĐ này): " + soLuongTrongGio + "\n" +
                    "Không thể thêm: " + soLuong,
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            float thanhTien = donGia * soLuong;
            
            modelChiTiet.addRow(new Object[]{
                maSP,
                tenSP,
                soLuong,
                df.format(donGia) + " đ",
                df.format(thanhTien) + " đ"
            });
            
            tongTienHD += thanhTien;
            updateTongTien();
            
            txtSoLuong.setText("1");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaSanPhamKhoiHD() {
        // ... (Không thay đổi) ...
        int row = tableChiTiet.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String thanhTienStr = tableChiTiet.getValueAt(row, 4).toString()
            .replace(",", "").replace(" đ", "");
        float thanhTien = Float.parseFloat(thanhTienStr);
        
        tongTienHD -= thanhTien;
        modelChiTiet.removeRow(row);
        updateTongTien();
    }
    
    private void updateTongTien() {
        // ... (Không thay đổi) ...
        lblTongTien.setText(df.format(tongTienHD) + " đ");
        lblThanhToan.setText(df.format(tongTienHD) + " đ");
    }
    
    private void luuHoaDon() {
        // ... (Không thay đổi) ...
        if (currentHoaDonId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo hóa đơn mới!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (txtIdKH.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (modelChiTiet.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào hóa đơn!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            HoaDon hd = new HoaDon();
            hd.setIdHD(currentHoaDonId);
            hd.setIdNV(txtIdNV.getText());
            hd.setIdKH(txtIdKH.getText());
            hd.setNgayLap(new Date());
            hd.setTongTien(tongTienHD);
            hd.setStatus("Chờ thanh toán"); 
            
            if (hoaDonDAO.insert(hd)) {
                int cthdCount = chiTietDAO.getAll().size() + 1;
                
                for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                    ChiTietHoaDon cthd = new ChiTietHoaDon();
                    cthd.setIdCTHD("CTHD" + String.format("%03d", cthdCount++));
                    cthd.setIdHD(currentHoaDonId);
                    cthd.setIdGiay(modelChiTiet.getValueAt(i, 0).toString());
                    cthd.setSoLuong(Integer.parseInt(modelChiTiet.getValueAt(i, 2).toString()));
                    
                    String donGiaStr = modelChiTiet.getValueAt(i, 3).toString()
                        .replace(",", "").replace(" đ", "");
                    cthd.setDonGia(Float.parseFloat(donGiaStr));
                    
                    String thanhTienStr = modelChiTiet.getValueAt(i, 4).toString()
                        .replace(",", "").replace(" đ", "");
                    cthd.setThanhTien(Float.parseFloat(thanhTienStr));
                    cthd.setStatus("Hoạt động");
                    
                    chiTietDAO.insert(cthd);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Lưu hóa đơn tạm thành công!\nMã hóa đơn: " + currentHoaDonId + 
                    "\nTrạng thái: Chờ thanh toán",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                currentHoaDonId = "";
                txtIdHD.setText("");
                txtIdKH.setText("");
                txtTenKH.setText("");
                txtSDT.setText("");
                modelChiTiet.setRowCount(0);
                tongTienHD = 0;
                updateTongTien();
                
                setFormTaoHoaDonEnabled(false);
                
                loadDanhSachHoaDon(false);
                loadDanhSachSanPham();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu hóa đơn: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void huyHoaDon() {
        // ... (Không thay đổi) ...
        if (currentHoaDonId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn nào để hủy!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn hủy phiếu tạm này?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            currentHoaDonId = "";
            txtIdHD.setText("");
            txtIdKH.setText("");
            txtTenKH.setText("");
            txtSDT.setText("");
            modelChiTiet.setRowCount(0);
            tongTienHD = 0;
            updateTongTien();
            
            setFormTaoHoaDonEnabled(false);
            
            JOptionPane.showMessageDialog(this, "Đã hủy hóa đơn tạm!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void xuatHoaDon() {
        // ... (Không thay đổi) ...
        int row = tableHoaDon.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần thanh toán!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idHD = tableHoaDon.getValueAt(row, 0).toString();
        String trangThai = tableHoaDon.getValueAt(row, 5).toString();
        
        if (trangThai.equals("Đã thanh toán")) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán trước đó!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Xác nhận thanh toán cho hóa đơn: " + idHD + "?\n" +
            "Hệ thống sẽ cập nhật tồn kho và chi tiêu của khách hàng.",
            "Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            
            try {
                List<ChiTietHoaDon> listCT = chiTietDAO.getByHoaDon(idHD);
                if (listCT == null || listCT.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy chi tiết của hóa đơn này.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                for (ChiTietHoaDon ct : listCT) {
                    Giay giay = giayDAO.getById(ct.getIdGiay());
                    if (giay == null || giay.getSoLuong() < ct.getSoLuong()) {
                        String tenGiay = (giay != null) ? giay.getTenGiay() : ct.getIdGiay();
                        JOptionPane.showMessageDialog(this, 
                            "Lỗi: Sản phẩm [" + tenGiay + "] không đủ tồn kho để xuất!\n" +
                            "Số lượng yêu cầu: " + ct.getSoLuong() + "\n" +
                            "Số lượng tồn kho: " + (giay != null ? giay.getSoLuong() : 0),
                            "Lỗi Tồn Kho", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }
                }

                for (ChiTietHoaDon ct : listCT) {
                    Giay giay = giayDAO.getById(ct.getIdGiay());
                    int tonKhoMoi = giay.getSoLuong() - ct.getSoLuong();
                    giayDAO.updateSoLuong(giay.getIdGiay(), tonKhoMoi); 
                }
                
                HoaDon hd = hoaDonDAO.getById(idHD);
                if (hd == null) {
                     JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy hóa đơn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                khachHangDAO.updateTongTien(hd.getIdKH(), hd.getTongTien());

                hd.setStatus("Đã thanh toán");
                if (hoaDonDAO.update(hd)) {
                    hienThiHoaDonXuat(idHD); 
                    
                    loadDanhSachHoaDon(false); 
                    loadDanhSachSanPham(); 
                    
                    JOptionPane.showMessageDialog(this, 
                        "Xác nhận thanh toán thành công!.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng khi xuất hóa đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    
    private void xoaHoaDon() {
        // ... (Không thay đổi) ...
        int row = tableHoaDon.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idHD = tableHoaDon.getValueAt(row, 0).toString();
        String trangThai = tableHoaDon.getValueAt(row, 5).toString();
        
        if (!"Chờ thanh toán".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(this, 
                "Không thể xóa hóa đơn đã thanh toán! \nChỉ được xóa hóa đơn có trạng thái 'Chờ thanh toán'.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn XÓA VĨNH VIỄN hóa đơn " + idHD + "?\n" +
            "Hành động này không thể hoàn tác!",
            "Xác nhận xóa hóa đơn", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean chiTietDeleted = chiTietDAO.deleteByHoaDonId(idHD); 
                
                if (!chiTietDeleted) {
                }
                
                boolean hoaDonDeleted = hoaDonDAO.delete(idHD); 
                
                if (hoaDonDeleted) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công hóa đơn " + idHD + ".",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDanhSachHoaDon(false); 
                    loadDanhSachSanPham();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa hóa đơn.\nKiểm tra lại DAO_HoaDon.delete()", "Lỗi DAO", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi nghiêm trọng khi xóa: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // === SỬA ĐỔI: Phương thức này không còn được sử dụng để xem chi tiết nữa ===
    // === Nó chỉ được gọi sau khi *xác nhận thanh toán* ===
    private void hienThiHoaDonXuat(String idHD) {
        // ... (Không thay đổi) ...
        HoaDon hd = hoaDonDAO.getById(idHD);
        if (hd == null) return;
        
        KhachHang kh = khachHangDAO.getById(hd.getIdKH());
        String tenKH = (kh != null) ? kh.getTenKH() : "N/A";
        String sdtKH = (kh != null) ? kh.getSdt() : "N/A";
        String diaChiKH = (kh != null) ? kh.getDiaChi() : "N/A";
        
        NhanVien nv = nhanVienDAO.getById(hd.getIdNV());
        String tenNV = (nv != null) ? nv.getTenNV() : "N/A";
        
        List<ChiTietHoaDon> listCT = chiTietDAO.getByHoaDon(idHD);
        
        StringBuilder hoaDon = new StringBuilder();
        hoaDon.append("╔═══════════════════════════════════════════════╗\n");
        hoaDon.append("║          CỬA HÀNG GIÀY THỂ THAO               ║\n");
        hoaDon.append("║              HÓA ĐƠN BÁN HÀNG                 ║\n");
        hoaDon.append("╚═══════════════════════════════════════════════╝\n\n");
        
        hoaDon.append("Mã hóa đơn: ").append(idHD).append("\n");
        hoaDon.append("Ngày xuất: ").append(sdfTime.format(new Date())).append("\n");
        hoaDon.append("Nhân viên: ").append(tenNV).append(" (").append(hd.getIdNV()).append(")\n");
        hoaDon.append("─────────────────────────────────────────────────\n");
        hoaDon.append("THÔNG TIN KHÁCH HÀNG:\n");
        hoaDon.append("  Tên: ").append(tenKH).append("\n");
        hoaDon.append("  SĐT: ").append(sdtKH).append("\n");
        hoaDon.append("  Địa chỉ: ").append(diaChiKH).append("\n");
        hoaDon.append("─────────────────────────────────────────────────\n");
        hoaDon.append("CHI TIẾT SẢN PHẨM:\n");
        hoaDon.append("─────────────────────────────────────────────────\n\n");
        
        int stt = 1;
        for (ChiTietHoaDon ct : listCT) {
            Giay giay = giayDAO.getById(ct.getIdGiay());
            String tenGiay = (giay != null) ? giay.getTenGiay() : "N/A";
            String size = (giay != null) ? String.valueOf(giay.getSize()) : "N/A";
            
            hoaDon.append(String.format("%2d. %-30s\n", stt++, tenGiay));
            hoaDon.append(String.format("    Size: %-8s    SL: %d\n", size, ct.getSoLuong()));
            hoaDon.append(String.format("    Đơn giá: %15s đ\n", df.format(ct.getDonGia())));
            hoaDon.append(String.format("    Thành tiền: %12s đ\n\n", df.format(ct.getThanhTien())));
        }
        
        hoaDon.append("═════════════════════════════════════════════════\n");
        hoaDon.append(String.format("TỔNG TIỀN: %25s đ\n", df.format(hd.getTongTien())));
        hoaDon.append("═════════════════════════════════════════════════\n\n");
        hoaDon.append("           Cảm ơn quý khách đã mua hàng!\n");
        hoaDon.append("              Hẹn gặp lại quý khách!\n");
        
        JTextArea textArea = new JTextArea(hoaDon.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 600));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Hóa đơn xuất: " + idHD, JOptionPane.INFORMATION_MESSAGE);
    }

    
    private Map<String, Integer> getTatCaSoLuongTamGiu() {
        // ... (Không thay đổi) ...
        Map<String, Integer> tamGiuMap = new HashMap<>();
        List<HoaDon> allHoaDon = hoaDonDAO.getAll();
        if (allHoaDon == null) return tamGiuMap;
        for (HoaDon hd : allHoaDon) {
            if (hd != null && "Chờ thanh toán".equalsIgnoreCase(hd.getStatus())) {
                List<ChiTietHoaDon> listCT = chiTietDAO.getByHoaDon(hd.getIdHD());
                if (listCT == null) continue;
                for (ChiTietHoaDon ct : listCT) {
                    if (ct != null) {
                        String idGiay = ct.getIdGiay();
                        int soLuong = ct.getSoLuong();
                        tamGiuMap.put(idGiay, tamGiuMap.getOrDefault(idGiay, 0) + soLuong);
                    }
                }
            }
        }
        return tamGiuMap;
    }
    
    private void loadDanhSachHoaDon(boolean showMessage) {
        // ... (Không thay đổi) ...
        modelHoaDon.setRowCount(0);
        
        String statusFilter = "Tất cả";
        if (cboLocTrangThai != null) { 
            statusFilter = cboLocTrangThai.getSelectedItem().toString();
        }
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        
        List<HoaDon> list = hoaDonDAO.getAll();
        int count = 0; 
        
        for (HoaDon hd : list) {
            boolean statusMatch = false;
            if (statusFilter.equals("Tất cả")) {
                statusMatch = true;
            } else {
                statusMatch = hd.getStatus().equalsIgnoreCase(statusFilter);
            }
            
            if (!statusMatch) {
                continue; 
            }
            
            boolean keywordMatch = false;
            if (keyword.isEmpty()) {
                keywordMatch = true;
            } else {
                String idHD = hd.getIdHD().toLowerCase();
                String idKH = hd.getIdKH().toLowerCase();
                String idNV = hd.getIdNV().toLowerCase();
                
                if (idHD.contains(keyword) || idKH.contains(keyword) || idNV.contains(keyword)) {
                    keywordMatch = true;
                }
            }
            
            if (statusMatch && keywordMatch) {
                modelHoaDon.addRow(new Object[]{
                    hd.getIdHD(),
                    hd.getIdNV(),
                    hd.getIdKH(),
                    sdf.format(hd.getNgayLap()),
                    df.format(hd.getTongTien()) + " đ",
                    hd.getStatus()
                });
                count++; 
            }
        }
        
        if (count == 0 && showMessage && !keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy hóa đơn nào khớp với từ khóa: " + txtTimKiem.getText().trim(),
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadDanhSachSanPham(boolean showMessage) {
        // ... (Không thay đổi) ...
        modelSanPham.setRowCount(0);
        
        String keyword = "";
        if (txtTimKiemSP != null) { 
            keyword = txtTimKiemSP.getText().trim().toLowerCase();
        }

        Map<String, Integer> tamGiuMap = getTatCaSoLuongTamGiu();
        List<Giay> list = giayDAO.getAll();
        int count = 0; 
        
        for (Giay g : list) {
            int tonKho = g.getSoLuong(); 
            int tamGiu = tamGiuMap.getOrDefault(g.getIdGiay(), 0); 
            int khaDung = tonKho - tamGiu; 
            
            if (g.getStatus().equalsIgnoreCase("Hoạt động")) { 
                
                boolean keywordMatch = false;
                if (keyword.isEmpty()) {
                    keywordMatch = true; 
                } else {
                    String ma = g.getIdGiay().toLowerCase();
                    String ten = g.getTenGiay().toLowerCase();
                    if (ma.contains(keyword) || ten.contains(keyword)) {
                        keywordMatch = true;
                    }
                }
                
                if (keywordMatch) {
                    modelSanPham.addRow(new Object[]{
                        g.getIdGiay(),
                        g.getTenGiay(),
                        g.getSize(),
                        df.format(g.getGiaBan()) + " đ",
                        tonKho, 
                        khaDung 
                    });
                    count++; 
                }
            }
        }
        
        if (count == 0 && showMessage && !keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy sản phẩm nào khớp với từ khóa: " + txtTimKiemSP.getText().trim(),
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void loadDanhSachSanPham() {
        // ... (Không thay đổi) ...
        loadDanhSachSanPham(false); 
    }
    
    // === THÊM MỚI: Hàm mở Dialog Chi Tiết ===
    private void moDialogChiTietHD() {
        int row = tableHoaDon.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xem chi tiết.", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idHD = tableHoaDon.getValueAt(row, 0).toString();
        
        // Tạo và hiển thị dialog
        ChiTietHoaDonDialog dialog = new ChiTietHoaDonDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            idHD
        );
        dialog.setVisible(true);
    }
    
    // === THÊM MỚI: Lớp Dialog lồng bên trong ===
    class ChiTietHoaDonDialog extends JDialog {
        private DefaultTableModel modelChiTietDialog;
        
        public ChiTietHoaDonDialog(JFrame parent, String idHD) {
            super(parent, "Chi Tiết Hóa Đơn: " + idHD, true); // true = modal
            
            setSize(750, 600);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout(10, 10));
            
            // Lấy dữ liệu
            HoaDon hd = hoaDonDAO.getById(idHD);
            if (hd == null) {
                JOptionPane.showMessageDialog(this, "Không thể tìm thấy hóa đơn " + idHD, "Lỗi", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
            
            NhanVien nv = nhanVienDAO.getById(hd.getIdNV());
            KhachHang kh = khachHangDAO.getById(hd.getIdKH());
            List<ChiTietHoaDon> listCT = chiTietDAO.getByHoaDon(idHD);

            // 1. Panel Thông Tin Chung (NORTH)
            JPanel infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Thông tin chung"),
                BorderFactory.createEmptyBorder(5, 10, 10, 10)
            ));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(4, 5, 4, 5);
            
            // Hàng 1
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("Mã Hóa Đơn:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(createReadOnlyTextField(idHD), gbc);
            
            gbc.gridx = 2;
            infoPanel.add(new JLabel("Ngày Lập:"), gbc);
            gbc.gridx = 3;
            infoPanel.add(createReadOnlyTextField(sdf.format(hd.getNgayLap())), gbc);
            
            // Hàng 2
            gbc.gridx = 0; gbc.gridy = 1;
            infoPanel.add(new JLabel("Nhân Viên:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(createReadOnlyTextField(nv != null ? nv.getTenNV() + " (" + nv.getIdNV() + ")" : hd.getIdNV()), gbc);
            
            gbc.gridx = 2;
            infoPanel.add(new JLabel("Trạng Thái:"), gbc);
            gbc.gridx = 3;
            infoPanel.add(createReadOnlyTextField(hd.getStatus()), gbc);
            
            // Hàng 3
            gbc.gridx = 0; gbc.gridy = 2;
            infoPanel.add(new JLabel("Khách Hàng:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(createReadOnlyTextField(kh != null ? kh.getTenKH() + " (" + kh.getIdKH() + ")" : hd.getIdKH()), gbc);
            
            gbc.gridx = 2;
            infoPanel.add(new JLabel("SĐT Khách:"), gbc);
            gbc.gridx = 3;
            infoPanel.add(createReadOnlyTextField(kh != null ? kh.getSdt() : "N/A"), gbc);

            add(infoPanel, BorderLayout.NORTH);

            // 2. Panel Chi Tiết Sản Phẩm (CENTER)
            String[] columns = {"Mã SP", "Tên Sản Phẩm", "Số Lượng", "Đơn Giá", "Thành Tiền"};
            modelChiTietDialog = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            
            JTable tableChiTietDialog = new JTable(modelChiTietDialog);
            tableChiTietDialog.setRowHeight(25);
            tableChiTietDialog.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            // Đổ dữ liệu vào bảng
            if (listCT != null) {
                for (ChiTietHoaDon ct : listCT) {
                    Giay giay = giayDAO.getById(ct.getIdGiay());
                    String tenGiay = (giay != null) ? giay.getTenGiay() : "Sản phẩm không tồn tại";
                    modelChiTietDialog.addRow(new Object[]{
                        ct.getIdGiay(),
                        tenGiay,
                        ct.getSoLuong(),
                        df.format(ct.getDonGia()) + " đ",
                        df.format(ct.getThanhTien()) + " đ"
                    });
                }
            }

            JScrollPane scrollPane = new JScrollPane(tableChiTietDialog);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết sản phẩm"));
            add(scrollPane, BorderLayout.CENTER);

            // 3. Panel Tổng Tiền và Nút Đóng (SOUTH)
            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel lblTong = new JLabel("Tổng Tiền: " + df.format(hd.getTongTien()) + " đ");
            lblTong.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTong.setForeground(Color.RED);
            
            JButton btnDong = createStyledButton("Đóng", new Color(149, 165, 166));
            btnDong.setPreferredSize(new Dimension(100, 35));
            btnDong.addActionListener(e -> dispose());
            
            southPanel.add(lblTong, BorderLayout.WEST);
            southPanel.add(btnDong, BorderLayout.EAST);
            
            add(southPanel, BorderLayout.SOUTH);
        }
        
        // Helper
        private JTextField createReadOnlyTextField(String text) {
            JTextField txt = new JTextField(text);
            txt.setEditable(false);
            txt.setBackground(new Color(240, 240, 240));
            txt.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            return txt;
        }
    } // Hết ChiTietHoaDonDialog
    
}