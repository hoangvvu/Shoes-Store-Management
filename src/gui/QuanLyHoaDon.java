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
        
        // === THÊM LẠI BẢNG BỊ THIẾU ===
        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        // === KẾT THÚC THÊM LẠI ===
        

        // === SỬA LẠI LAYOUT NÚT BẤM ===
        
        // 1. Dùng FlowLayout.RIGHT, Gaps = 5
        JPanel btnPanel = new JPanel(new BorderLayout(10, 0)); // 10px Hgap
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Đệm ở trên

        // 2. Tạo Panel TÌM KIẾM (Bên trái - WEST)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setBackground(Color.WHITE);

        txtTimKiemSP = new JTextField(10); // Kích thước 10
        txtTimKiemSP.setToolTipText("Nhập tên hoặc mã sản phẩm...");
        
        JButton btnTimSP = createStyledButton("Tìm kiếm", new Color(52, 152, 219));
        
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
        
        searchPanel.add(new JLabel("Tìm sản phẩm:"));
        searchPanel.add(txtTimKiemSP);
        searchPanel.add(btnTimSP);
        
        // 3. Nút Xóa (Bên phải - EAST)
        btnXoaSP = createStyledButton("Xóa sản phẩm", new Color(231, 76, 60));
        btnXoaSP.addActionListener(e -> xoaSanPhamKhoiHD());

        // 4. Thêm vào panel chính
        btnPanel.add(searchPanel, BorderLayout.WEST);
        btnPanel.add(btnXoaSP, BorderLayout.EAST);
        
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
        int row = tableChiTiet.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Lấy thông tin hiện tại
            int currentSoLuong = Integer.parseInt(modelChiTiet.getValueAt(row, 2).toString());
            String donGiaStr = modelChiTiet.getValueAt(row, 3).toString().replace(",", "").replace(" đ", "");
            float donGia = Float.parseFloat(donGiaStr);
            String tenSP = modelChiTiet.getValueAt(row, 1).toString();

            // Hỏi người dùng
            String input = JOptionPane.showInputDialog(this, 
                "Sản phẩm: " + tenSP + "\nTrong giỏ: " + currentSoLuong + "\nNhập số lượng muốn xóa:", 
                currentSoLuong);

            if (input == null) return;

            int xoaSoLuong = Integer.parseInt(input.trim());

            // Validate
            if (xoaSoLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng xóa phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (xoaSoLuong > currentSoLuong) {
                JOptionPane.showMessageDialog(this, "Không thể xóa nhiều hơn số lượng trong giỏ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tính toán tiền cần trừ
            float tienTru = xoaSoLuong * donGia;

            if (xoaSoLuong == currentSoLuong) {
                // Xóa hết dòng
                modelChiTiet.removeRow(row);
            } else {
                // Cập nhật lại số lượng còn lại trong giỏ
                int soLuongMoi = currentSoLuong - xoaSoLuong;
                float thanhTienMoi = soLuongMoi * donGia;
                
                modelChiTiet.setValueAt(soLuongMoi, row, 2); // Cập nhật cột Số lượng
                modelChiTiet.setValueAt(df.format(thanhTienMoi) + " đ", row, 4); // Cập nhật cột Thành tiền
            }

            // Cập nhật tổng tiền hóa đơn
            tongTienHD -= tienTru;
            updateTongTien();
            
            // Cập nhật lại bảng danh sách sản phẩm (để hiển thị lại số lượng khả dụng đúng)
            // loadDanhSachSanPham(false); // Tùy chọn: Nếu muốn cột "Khả dụng" cập nhật ngay lập tức thì bỏ comment dòng này

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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
    
    private void hienThiHoaDonXuat(String idHD) {
        // Thay vì hiện text area, ta gọi Dialog thiết kế đẹp
        XuatHoaDonDialog dialog = new XuatHoaDonDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            idHD
        );
        dialog.setVisible(true);
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
    }	
 // === CLASS MỚI: Giao diện Xuất Hóa Đơn (Form Thanh Toán) ===
    class XuatHoaDonDialog extends JDialog {
        
        public XuatHoaDonDialog(JFrame parent, String idHD) {
            super(parent, "Hóa Đơn Thanh Toán", true);
            setSize(600, 700); // Kích thước giống tờ hóa đơn dọc
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout(10, 10));
            setBackground(Color.WHITE);

            // Lấy dữ liệu
            HoaDon hd = hoaDonDAO.getById(idHD);
            if (hd == null) {
                dispose();
                return;
            }
            KhachHang kh = khachHangDAO.getById(hd.getIdKH());
            NhanVien nv = nhanVienDAO.getById(hd.getIdNV());
            List<ChiTietHoaDon> listCT = chiTietDAO.getByHoaDon(idHD);

            // --- 1. HEADER (Tên cửa hàng) ---
            JPanel headerPanel = new JPanel(new GridLayout(3, 1));
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel lblStore = new JLabel("CỬA HÀNG GIÀY THỂ THAO", SwingConstants.CENTER);
            lblStore.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblStore.setForeground(new Color(44, 62, 80));

            JLabel lblAddress = new JLabel("12 Nguyễn Văn Bảo, Phường 4, Gò Vấp, TP.HCM", SwingConstants.CENTER);
            lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel lblTitle = new JLabel("HÓA ĐƠN BÁN HÀNG", SwingConstants.CENTER);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            headerPanel.add(lblStore);
            headerPanel.add(lblAddress);
            headerPanel.add(lblTitle);

            add(headerPanel, BorderLayout.NORTH);

            // --- 2. CONTENT (Thông tin + Bảng SP) ---
            JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
            centerPanel.setBackground(Color.WHITE);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

            // A. Thông tin chung
            JPanel infoPanel = new JPanel(new GridBagLayout());
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY)); // Gạch chân
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(3, 5, 3, 5);
            gbc.weightx = 0.5;

            // Dòng 1
            gbc.gridx = 0; gbc.gridy = 0;
            infoPanel.add(new JLabel("Số hóa đơn: " + idHD), gbc);
            
            gbc.gridx = 1;
            infoPanel.add(new JLabel("Ngày: " + sdfTime.format(new Date())), gbc); // Lấy giờ hiện tại

            // Dòng 2
            gbc.gridx = 0; gbc.gridy = 1;
            infoPanel.add(new JLabel("Nhân viên: " + (nv != null ? nv.getTenNV() : hd.getIdNV())), gbc);

            gbc.gridx = 1;
            infoPanel.add(new JLabel(""), gbc); // Empty

            // Dòng 3 (Khách hàng)
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; // Span 2 cột
            infoPanel.add(new JLabel("Khách hàng: " + (kh != null ? kh.getTenKH() : "Khách lẻ")), gbc);
            
            // Dòng 4 (Địa chỉ)
            gbc.gridy = 3;
            infoPanel.add(new JLabel("Địa chỉ: " + ((kh != null && kh.getDiaChi() != null) ? kh.getDiaChi() : "")), gbc);

            // Reset gbc
            gbc.gridwidth = 1;

            centerPanel.add(infoPanel, BorderLayout.NORTH);

            // B. Bảng sản phẩm
            String[] columns = {"Sản phẩm", "Size", "SL", "Đơn giá", "Thành tiền"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            JTable table = new JTable(model);
            table.setRowHeight(25);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            table.setShowVerticalLines(false); // Bỏ kẻ dọc cho giống hóa đơn giấy
            
            // Căn chỉnh cột
            table.getColumnModel().getColumn(0).setPreferredWidth(180); // Tên SP rộng
            table.getColumnModel().getColumn(1).setPreferredWidth(40);  // Size hẹp
            table.getColumnModel().getColumn(2).setPreferredWidth(40);  // SL hẹp

            if (listCT != null) {
                for (ChiTietHoaDon ct : listCT) {
                    Giay giay = giayDAO.getById(ct.getIdGiay());
                    model.addRow(new Object[]{
                        (giay != null ? giay.getTenGiay() : ct.getIdGiay()),
                        (giay != null ? giay.getSize() : ""),
                        ct.getSoLuong(),
                        df.format(ct.getDonGia()),
                        df.format(ct.getThanhTien())
                    });
                }
            }

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.getViewport().setBackground(Color.WHITE);
            scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Bỏ viền scrollpane
            centerPanel.add(scrollPane, BorderLayout.CENTER);

            add(centerPanel, BorderLayout.CENTER);

            // --- 3. FOOTER (Tổng tiền + Lời cảm ơn + Nút) ---
            JPanel footerPanel = new JPanel(new BorderLayout());
            footerPanel.setBackground(Color.WHITE);
            footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

            // Panel tiền
            JPanel moneyPanel = new JPanel(new GridLayout(2, 1));
            moneyPanel.setBackground(Color.WHITE);
            
            JLabel lblLine = new JLabel("________________________________________", SwingConstants.RIGHT);
            
            JPanel totalRow = new JPanel(new BorderLayout());
            totalRow.setBackground(Color.WHITE);
            JLabel lblTextTong = new JLabel("TỔNG CỘNG THANH TOÁN:", SwingConstants.LEFT);
            lblTextTong.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            JLabel lblValTong = new JLabel(df.format(hd.getTongTien()) + " VND", SwingConstants.RIGHT);
            lblValTong.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblValTong.setForeground(Color.RED);
            
            totalRow.add(lblTextTong, BorderLayout.WEST);
            totalRow.add(lblValTong, BorderLayout.EAST);
            
            moneyPanel.add(lblLine);
            moneyPanel.add(totalRow);

            // Panel lời cảm ơn
            JLabel lblThanks = new JLabel("Cảm ơn quý khách & Hẹn gặp lại!", SwingConstants.CENTER);
            lblThanks.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblThanks.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            // Panel Nút
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setBackground(Color.WHITE);

            JButton btnPrint = new JButton("🖨️ In Hóa Đơn");
            btnPrint.setBackground(new Color(46, 204, 113));
            btnPrint.setForeground(Color.WHITE);
            btnPrint.setFocusPainted(false);
            btnPrint.setPreferredSize(new Dimension(120, 35));
            btnPrint.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Đang gửi lệnh in...", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            });

            JButton btnClose = new JButton("Đóng");
            btnClose.setBackground(new Color(149, 165, 166));
            btnClose.setForeground(Color.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.setPreferredSize(new Dimension(100, 35));
            btnClose.addActionListener(e -> dispose());

            btnPanel.add(btnPrint);
            btnPanel.add(btnClose);

            // Lắp ráp footer
            JPanel bottomWrapper = new JPanel(new BorderLayout());
            bottomWrapper.setBackground(Color.WHITE);
            bottomWrapper.add(moneyPanel, BorderLayout.NORTH);
            bottomWrapper.add(lblThanks, BorderLayout.CENTER);
            bottomWrapper.add(btnPanel, BorderLayout.SOUTH);

            footerPanel.add(bottomWrapper, BorderLayout.CENTER);

            add(footerPanel, BorderLayout.SOUTH);
        }
    }
    
}