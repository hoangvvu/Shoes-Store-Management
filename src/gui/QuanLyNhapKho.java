package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.awt.FlowLayout;
import java.util.Date;
import java.util.List;
import DAO.*;
import model.*;

public class QuanLyNhapKho extends JPanel {
    private JTable tableNhapKho, tableChiTiet, tableSanPham;
    private DefaultTableModel modelNhapKho, modelChiTiet, modelSanPham;
    private JTextField txtIdNK, txtIdNV, txtIdNCC, txtTenNCC, txtTimKiem, txtSoLuong, txtGiaNhap;
    private JLabel lblTongTien;
    // === ĐÃ SỬA: Thêm btnXoaNK ===
    private JButton btnTaoMoi, btnThemSP, btnXoaSP, btnLuuNK, btnHuyNK, btnTraCuuNCC, btnLamMoi, btnTimKiem, btnXacNhan, btnXoaNK;
    
    // === ĐÃ THÊM: ComboBox lọc trạng thái ===
    private JComboBox<String> cboLocTrangThai;
    
    private DAO_NhapKho nhapKhoDAO;
    private DAO_ChiTietNhapKho chiTietDAO;
    private DAO_NhaCungCap nhaCungCapDAO;
    private DAO_Giay giayDAO;
    private DAO_NhanVien nhanVienDAO;
    
    private DecimalFormat df = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    private NhanVien currentUser;
    private String currentNhapKhoId = "";
    private float tongTienNK = 0;
    
    public QuanLyNhapKho() {
        nhapKhoDAO = new DAO_NhapKho();
        chiTietDAO = new DAO_ChiTietNhapKho();
        nhaCungCapDAO = new DAO_NhaCungCap();
        giayDAO = new DAO_Giay();
        nhanVienDAO = new DAO_NhanVien();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(236, 240, 241));
        
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        
        loadDanhSachNhapKho();
        loadDanhSachSanPham();
        
        // === MỚI: Khóa form khi khởi tạo ===
        setFormTaoNKEnabled(false);
    }
    
    public QuanLyNhapKho(NhanVien user) {
        this(); 
        this.currentUser = user;
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        }
        
        applyPermissions();
        // === MỚI: Khóa form khi khởi tạo (cũng cho constructor này) ===
        setFormTaoNKEnabled(false);
    }
    
    private void applyPermissions() {
        if (btnXoaNK == null) return; 

        if (currentUser != null) {
            String roleId = "";
            try {
                // GIẢ SỬ PHƯƠNG THỨC NÀY TRẢ VỀ ID QUYỀN (ví dụ: "PQ001")
                roleId = currentUser.getIdPQ(); 
            } catch (Exception e) {
                 // Nếu lỗi, roleId sẽ là "" và nút xóa bị ẩn.
            }
            
            // Dùng ID quyền đã tạo trong SQL (PQ001 là Admin)
            if ("PQ001".equalsIgnoreCase(roleId)) {
                btnXoaNK.setVisible(true); // Hiển thị nút Xóa cho Admin (PQ001)
            } else {
                btnXoaNK.setVisible(false); // Ẩn nút Xóa với nhân viên thường
            }
        } else {
            // Nếu không có thông tin user, ẩn nút
            btnXoaNK.setVisible(false);
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("QUẢN LÝ NHẬP KHO", SwingConstants.CENTER);
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
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        // Left: Tạo phiếu nhập kho
        JPanel leftPanel = createNhapKhoPanel();
        leftPanel.setPreferredSize(new Dimension(450, 0));
        
        // Center: Danh sách nhập kho
        JPanel centerPanel = createDanhSachNhapKhoPanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createNhapKhoPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(39, 174, 96));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblHeader = new JLabel("TẠO PHIẾU NHẬP KHO MỚI");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.WEST);
        
        btnTaoMoi = createStyledButton("Tạo mới", new Color(241, 196, 15));
        btnTaoMoi.setPreferredSize(new Dimension(100, 30));
        btnTaoMoi.addActionListener(e -> taoPhieuNhapKhoMoi());
        headerPanel.add(btnTaoMoi, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBackground(Color.WHITE);
        
        // Thông tin phiếu nhập
        contentPanel.add(createThongTinNKPanel(), BorderLayout.NORTH);
        
        // Danh sách sản phẩm + Sản phẩm có sẵn
        JPanel middlePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(createChiTietNKPanel());
        middlePanel.add(createSanPhamPanel());
        contentPanel.add(middlePanel, BorderLayout.CENTER);
        
        // Tổng tiền và nút
        contentPanel.add(createTongTienPanel(), BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createThongTinNKPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu nhập kho"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Mã NK
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã phiếu NK:"), gbc);
        txtIdNK = new JTextField(10);
        txtIdNK.setEditable(false);
        txtIdNK.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtIdNK, gbc);
        
        // Mã NV
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mã nhân viên:"), gbc);
        txtIdNV = new JTextField(10);
        txtIdNV.setEditable(false);
        txtIdNV.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtIdNV, gbc);
        
        // Mã NCC (Tra cứu theo ID/SĐT)
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("ID/Tên NCC:"), gbc);
        
        JPanel nccPanel = new JPanel(new BorderLayout(5, 0));
        nccPanel.setBackground(Color.WHITE);
        txtIdNCC = new JTextField(10); // Dùng để nhập ID/Tên NCC hoặc SĐT tra cứu
        btnTraCuuNCC = createStyledButton("Tra cứu", new Color(52, 152, 219));
        btnTraCuuNCC.setPreferredSize(new Dimension(80, 25));
        btnTraCuuNCC.addActionListener(e -> traCuuNhaCungCap());
        
        nccPanel.add(txtIdNCC, BorderLayout.CENTER);
        nccPanel.add(btnTraCuuNCC, BorderLayout.EAST);
        gbc.gridx = 1;
        panel.add(nccPanel, gbc);
        
        // Tên NCC
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Tên NCC:"), gbc);
        txtTenNCC = new JTextField(10);
        txtTenNCC.setEditable(false);
        txtTenNCC.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtTenNCC, gbc);
        
        return panel;
    }
    
    private JPanel createChiTietNKPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết phiếu nhập kho"));
        
        String[] columns = {"Mã SP", "Tên sản phẩm", "SL", "Giá nhập", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableChiTiet = new JTable(modelChiTiet);
        tableChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableChiTiet.setRowHeight(25);
        tableChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Nút xóa sản phẩm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnXoaSP = createStyledButton("Xóa SP", new Color(231, 76, 60));
        btnXoaSP.addActionListener(e -> xoaSanPhamKhoiNK());
        btnPanel.add(btnXoaSP);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSanPhamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
        
        // Cấu hình JTable (giữ nguyên)
        String[] columns = {"Mã", "Tên giày", "Size", "Tồn kho"};
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
        
        JScrollPane scrollPane = new JScrollPane(tableSanPham);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // === PHẦN SỬA ĐỔI: CHIA THÀNH 2 DÒNG VÀ CĂN GIỮA ===
        
        // 1. Tạo container chính cho 2 dòng, dùng GridLayout(2, 1)
        JPanel bottomContainer = new JPanel(new GridLayout(2, 1, 5, 5)); 
        bottomContainer.setBackground(Color.WHITE);

        // 2. Dòng 1: Chứa các ô nhập liệu (Số lượng và Giá nhập) - ĐÃ CĂN GIỮA (FlowLayout.CENTER)
        JPanel inputLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); 
        inputLine.setBackground(Color.WHITE);
        
        inputLine.add(new JLabel("Số lượng nhập:"));
        txtSoLuong = new JTextField(5);
        txtSoLuong.setText("1");
        inputLine.add(txtSoLuong);
        
        inputLine.add(new JLabel("Giá nhập:"));
        txtGiaNhap = new JTextField(8);
        txtGiaNhap.setText("0");
        inputLine.add(txtGiaNhap);
        
        // 3. Dòng 2: Chứa nút "Thêm" - Căn giữa
        JPanel buttonLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonLine.setBackground(Color.WHITE);
        
        btnThemSP = createStyledButton("Thêm vào NK", new Color(39, 174, 96));
        btnThemSP.addActionListener(e -> themSanPhamVaoNK());
        buttonLine.add(btnThemSP);
        
        // 4. Thêm 2 dòng vào container chính
        bottomContainer.add(inputLine);
        bottomContainer.add(buttonLine);
        
        // 5. Đặt container chính vào vị trí SOUTH
        panel.add(bottomContainer, BorderLayout.SOUTH);
        
        // ==========================================================
        
        return panel;
    }
    
    private JPanel createTongTienPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Tổng tiền
        JPanel tongTienPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        tongTienPanel.setBackground(Color.WHITE);
        
        JLabel lbl1 = new JLabel("Tổng tiền phiếu nhập:", SwingConstants.RIGHT);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien = new JLabel("0 đ", SwingConstants.LEFT);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien.setForeground(new Color(39, 174, 96));
        
        tongTienPanel.add(lbl1);
        tongTienPanel.add(lblTongTien);
        
        panel.add(tongTienPanel, BorderLayout.NORTH);
        
        // Nút
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        btnLuuNK = createStyledButton("Lưu phiếu tạm", new Color(52, 152, 219));
        btnLuuNK.setPreferredSize(new Dimension(0, 40));
        btnLuuNK.addActionListener(e -> luuPhieuNhapKho());
        
        btnHuyNK = createStyledButton("Hủy phiếu", new Color(231, 76, 60));
        btnHuyNK.setPreferredSize(new Dimension(0, 40));
        btnHuyNK.addActionListener(e -> huyPhieuNhapKho());
        
        btnPanel.add(btnLuuNK);
        btnPanel.add(btnHuyNK);
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDanhSachNhapKhoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblHeader = new JLabel("DANH SÁCH PHIẾU NHẬP KHO");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(lblHeader, BorderLayout.WEST);
        
        // === ĐÃ THÊM: Thêm ComboBox lọc và cập nhật logic tìm kiếm ===
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        // Bộ lọc trạng thái
        searchPanel.add(new JLabel("Trạng thái:"));
        String[] options = {"Tất cả", "Đã xác nhận", "Chờ xác nhận"};
        cboLocTrangThai = new JComboBox<>(options);
        cboLocTrangThai.addActionListener(e -> loadDanhSachNhapKho()); 
        searchPanel.add(cboLocTrangThai);
        
        // Tìm kiếm
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        searchPanel.add(txtTimKiem);
        btnTimKiem = createStyledButton("Tìm", new Color(52, 152, 219));
        btnTimKiem.setPreferredSize(new Dimension(80, 28));
        btnTimKiem.addActionListener(e -> loadDanhSachNhapKho());
        searchPanel.add(btnTimKiem);
        
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        btnLamMoi.setPreferredSize(new Dimension(80, 28));
        btnLamMoi.addActionListener(e -> {
            cboLocTrangThai.setSelectedIndex(0); 
            txtTimKiem.setText(""); 
            loadDanhSachNhapKho(); 
        });
        searchPanel.add(btnLamMoi);
        // ==========================================================
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Bảng
        String[] columns = {"Mã phiếu NK", "Mã nhân viên", "Mã NCC", "Ngày lập", "Tổng tiền", "Trạng thái"};
        modelNhapKho = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableNhapKho = new JTable(modelNhapKho);
        tableNhapKho.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableNhapKho.setRowHeight(30);
        tableNhapKho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableNhapKho.getTableHeader().setBackground(new Color(52, 73, 94));
        tableNhapKho.getTableHeader().setForeground(Color.WHITE);
        
        tableNhapKho.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableNhapKho.getSelectedRow();
                    if (row != -1) {
                        xemChiTietNhapKho(row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableNhapKho);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Nút Xóa và Xác nhận
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        
        // Nút Xóa (chỉ Admin thấy)
        btnXoaNK = createStyledButton("Xóa phiếu", new Color(231, 76, 60));
        btnXoaNK.setPreferredSize(new Dimension(150, 35));
        btnXoaNK.addActionListener(e -> xoaPhieuNhapKho());
        btnXoaNK.setVisible(false); 
        bottomPanel.add(btnXoaNK);
        
        // Nút Xác nhận nhập kho
        btnXacNhan = createStyledButton("Xác nhận nhập kho", new Color(39, 174, 96));
        btnXacNhan.setPreferredSize(new Dimension(180, 35));
        btnXacNhan.addActionListener(e -> xacNhanNhapKho());
        bottomPanel.add(btnXacNhan);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // === MỚI: Phương thức điều khiển trạng thái Form ===
    private void setFormTaoNKEnabled(boolean enabled) {
        // Vô hiệu hóa/Kích hoạt các trường nhập liệu
        txtIdNCC.setEnabled(enabled);
        btnTraCuuNCC.setEnabled(enabled);
        
        // Vô hiệu hóa/Kích hoạt panel sản phẩm
        txtSoLuong.setEnabled(enabled);
        txtGiaNhap.setEnabled(enabled);
        btnThemSP.setEnabled(enabled);
        btnXoaSP.setEnabled(enabled);
        
        // Vô hiệu hóa/Kích hoạt nút lưu/hủy
        btnLuuNK.setEnabled(enabled);
        btnHuyNK.setEnabled(enabled);
        
        // Nút Tạo Mới thì ngược lại
        btnTaoMoi.setEnabled(!enabled);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
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
    
    private void taoPhieuNhapKhoMoi() {
        currentNhapKhoId = generateNextNhapKhoId();
        txtIdNK.setText(currentNhapKhoId);
        
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        } else {
            txtIdNV.setText("NV001"); // Fallback
        }
        
        // Clear form
        txtIdNCC.setText("");
        txtTenNCC.setText("");
        modelChiTiet.setRowCount(0);
        tongTienNK = 0;
        updateTongTien();
        
        // === MỚI: Kích hoạt form ===
        setFormTaoNKEnabled(true);
        txtIdNCC.requestFocus();
        
        JOptionPane.showMessageDialog(this, "Đã tạo phiếu nhập kho mới: " + currentNhapKhoId,
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String generateNextNhapKhoId() {
        List<NhapKho> list = nhapKhoDAO.getAll();
        int maxId = 0;
        
        for (NhapKho nk : list) {
            String id = nk.getIdNhapKho();
            if (id.startsWith("NK")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        
        return "NK" + String.format("%03d", maxId + 1);
    }
    
    private void traCuuNhaCungCap() {
        // === ĐÃ SỬA: Tra cứu bằng ID/Tên NCC ===
        String query = txtIdNCC.getText().trim();
        
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID hoặc Tên Nhà Cung Cấp!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        NhaCungCap ncc = nhaCungCapDAO.getByIdOrName(query); 
        
        if (ncc != null) {
            txtIdNCC.setText(ncc.getIdNCC());
            txtTenNCC.setText(ncc.getTenNCC());
        } else {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy Nhà Cung Cấp nào với từ khóa: " + query,
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            txtTenNCC.setText("N/A");
        }
    }
    
    private void themSanPhamVaoNK() {
        if (currentNhapKhoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo phiếu nhập kho mới trước!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (txtIdNCC.getText().trim().isEmpty() || txtTenNCC.getText().equals("N/A")) {
            JOptionPane.showMessageDialog(this, "Vui lòng tra cứu và chọn Nhà Cung Cấp hợp lệ!",
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
            float giaNhap = Float.parseFloat(txtGiaNhap.getText().trim().replace(",", ""));
            
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (giaNhap <= 0) {
                 JOptionPane.showMessageDialog(this, "Giá nhập phải lớn hơn 0!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String maSP = tableSanPham.getValueAt(row, 0).toString();
            String tenSP = tableSanPham.getValueAt(row, 1).toString();
            
            float thanhTien = giaNhap * soLuong;
            
            // Thêm vào bảng chi tiết
            modelChiTiet.addRow(new Object[]{
                maSP,
                tenSP,
                soLuong,
                df.format(giaNhap) + " đ",
                df.format(thanhTien) + " đ"
            });
            
            tongTienNK += thanhTien;
            updateTongTien();
            
            txtSoLuong.setText("1");
            txtGiaNhap.setText("0");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng hoặc Giá nhập không hợp lệ!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaSanPhamKhoiNK() {
        int row = tableChiTiet.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String thanhTienStr = tableChiTiet.getValueAt(row, 4).toString()
            .replace(",", "").replace(" đ", "");
        float thanhTien = Float.parseFloat(thanhTienStr);
        
        tongTienNK -= thanhTien;
        modelChiTiet.removeRow(row);
        updateTongTien();
    }
    
    private void updateTongTien() {
        lblTongTien.setText(df.format(tongTienNK) + " đ");
    }
    
    // Logic `luuPhieuNhapKho` chỉ lưu tạm, KHÔNG cập nhật tồn kho
    private void luuPhieuNhapKho() {
        if (currentNhapKhoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo phiếu nhập kho mới!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (txtIdNCC.getText().trim().isEmpty() || txtTenNCC.getText().equals("N/A")) {
            JOptionPane.showMessageDialog(this, "Vui lòng tra cứu và chọn Nhà Cung Cấp hợp lệ!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (modelChiTiet.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào phiếu nhập!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Lưu phiếu nhập với trạng thái "Chờ xác nhận"
            NhapKho nk = new NhapKho();
            nk.setIdNhapKho(currentNhapKhoId);
            nk.setIdNV(txtIdNV.getText());
            nk.setIdNCC(txtIdNCC.getText());
            nk.setNgayNhap(new Date());
            nk.setTongTien(tongTienNK);
            nk.setStatus("Chờ xác nhận"); 
            
            if (nhapKhoDAO.insert(nk)) {
                // Lưu chi tiết phiếu nhập
                int ctnkCount = chiTietDAO.getAll().size() + 1;
                
                for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                    ChiTietNhapKho ctnk = new ChiTietNhapKho();
                    ctnk.setIdCTNK("CTNK" + String.format("%03d", ctnkCount++));
                    ctnk.setIdNhapKho(currentNhapKhoId);
                    ctnk.setIdGiay(modelChiTiet.getValueAt(i, 0).toString());
                    ctnk.setSoLuong(Integer.parseInt(modelChiTiet.getValueAt(i, 2).toString()));
                    
                    String giaNhapStr = modelChiTiet.getValueAt(i, 3).toString()
                        .replace(",", "").replace(" đ", "");
                    ctnk.setGiaNhap(Float.parseFloat(giaNhapStr));
                    
                    String thanhTienStr = modelChiTiet.getValueAt(i, 4).toString()
                        .replace(",", "").replace(" đ", "");
                    ctnk.setThanhTien(Float.parseFloat(thanhTienStr));
                    ctnk.setStatus("active");
                    
                    chiTietDAO.insert(ctnk);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Lưu phiếu nhập kho tạm thành công!\nMã phiếu: " + currentNhapKhoId + 
                    "\nTrạng thái: Chờ xác nhận\n(Chưa cập nhật tồn kho)",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Reset form
                currentNhapKhoId = "";
                txtIdNK.setText("");
                txtIdNCC.setText("");
                txtTenNCC.setText("");
                modelChiTiet.setRowCount(0);
                tongTienNK = 0;
                updateTongTien();
                
                // === MỚI: Vô hiệu hóa form sau khi lưu ===
                setFormTaoNKEnabled(false);
                
                loadDanhSachNhapKho();
            } else {
                 JOptionPane.showMessageDialog(this, "Lỗi khi lưu phiếu nhập kho!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu phiếu nhập kho: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void huyPhieuNhapKho() {
        if (currentNhapKhoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có phiếu nhập kho nào để hủy!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn hủy phiếu nhập kho tạm này?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            currentNhapKhoId = "";
            txtIdNK.setText("");
            txtIdNCC.setText("");
            txtTenNCC.setText("");
            modelChiTiet.setRowCount(0);
            tongTienNK = 0;
            updateTongTien();
            
            // === MỚI: Vô hiệu hóa form sau khi hủy ===
            setFormTaoNKEnabled(false);
            
            JOptionPane.showMessageDialog(this, "Đã hủy phiếu nhập kho tạm!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Logic `xacNhanNhapKho` là "Xác nhận" VÀ cập nhật tồn kho
    private void xacNhanNhapKho() {
        int row = tableNhapKho.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập kho cần xác nhận!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idNK = tableNhapKho.getValueAt(row, 0).toString();
        String trangThai = tableNhapKho.getValueAt(row, 5).toString();
        
        if (trangThai.equals("Đã xác nhận")) {
            JOptionPane.showMessageDialog(this, "Phiếu này đã được xác nhận nhập kho trước đó!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Xác nhận nhập kho cho phiếu: " + idNK + "?\n" +
            "Hệ thống sẽ cập nhật tồn kho.",
            "Xác nhận nhập kho", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                List<ChiTietNhapKho> listCT = chiTietDAO.getByNhapKho(idNK);
                if (listCT == null || listCT.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy chi tiết của phiếu này.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 1. Cập nhật tồn kho
                for (ChiTietNhapKho ct : listCT) {
                    Giay giay = giayDAO.getById(ct.getIdGiay());
                    int tonKhoMoi = giay.getSoLuong() + ct.getSoLuong();
                    giayDAO.updateSoLuong(giay.getIdGiay(), tonKhoMoi);
                }
                
                // 2. Cập nhật trạng thái phiếu nhập
                NhapKho nk = nhapKhoDAO.getById(idNK);
                if (nk == null) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy phiếu nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                nk.setStatus("Đã xác nhận");
                if (nhapKhoDAO.update(nk)) {
                    JOptionPane.showMessageDialog(this, 
                        "Xác nhận nhập kho thành công!\n✓ Đã cập nhật tồn kho.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadDanhSachNhapKho(); 
                    loadDanhSachSanPham(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái phiếu nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi nghiêm trọng khi xác nhận nhập kho: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void xoaPhieuNhapKho() {
        int row = tableNhapKho.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập kho cần xóa!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idNK = tableNhapKho.getValueAt(row, 0).toString();
        String trangThai = tableNhapKho.getValueAt(row, 5).toString();
        
        // Chỉ được xóa khi "Chờ xác nhận"
        if (!"Chờ xác nhận".equalsIgnoreCase(trangThai)) {
            JOptionPane.showMessageDialog(this, 
                "Không thể xóa phiếu đã xác nhận nhập kho! \nChỉ được xóa phiếu có trạng thái 'Chờ xác nhận'.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn XÓA VĨNH VIỄN phiếu nhập kho " + idNK + "?\n" +
            "Hành động này không thể hoàn tác!",
            "Xác nhận xóa phiếu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Bước 1: Xóa tất cả ChiTietNhapKho
                boolean chiTietDeleted = chiTietDAO.deleteByNhapKhoId(idNK); 
                
                // Bước 2: Xóa NhapKho
                boolean nhapKhoDeleted = nhapKhoDAO.delete(idNK); 
                
                if (nhapKhoDeleted) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công phiếu nhập kho " + idNK + ".",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDanhSachNhapKho(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa phiếu nhập kho.\nKiểm tra lại DAO_NhapKho.delete()", "Lỗi DAO", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi nghiêm trọng khi xóa: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // === ĐÃ SỬA: Hợp nhất bộ lọc và tìm kiếm vào một hàm ===
    private void loadDanhSachNhapKho() {
        modelNhapKho.setRowCount(0);
        
        // Lấy giá trị từ các bộ lọc
        String statusFilter = "Tất cả";
        if (cboLocTrangThai != null) { 
            statusFilter = cboLocTrangThai.getSelectedItem().toString();
        }
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        
        List<NhapKho> list = nhapKhoDAO.getAll();
        
        for (NhapKho nk : list) {
            // Lọc theo trạng thái
            boolean statusMatch = false;
            if (statusFilter.equals("Tất cả")) {
                statusMatch = true;
            } else {
                statusMatch = nk.getStatus().equalsIgnoreCase(statusFilter);
            }
            
            if (!statusMatch) {
                continue; 
            }
            
            // Lọc theo từ khóa
            boolean keywordMatch = false;
            if (keyword.isEmpty()) {
                keywordMatch = true;
            } else {
                String idNK = nk.getIdNhapKho().toLowerCase();
                String idNCC = nk.getIdNCC().toLowerCase();
                String idNV = nk.getIdNV().toLowerCase();
                
                if (idNK.contains(keyword) || idNCC.contains(keyword) || idNV.contains(keyword)) {
                    keywordMatch = true;
                }
            }
            
            // Chỉ thêm vào bảng nếu khớp cả hai
            if (statusMatch && keywordMatch) {
                modelNhapKho.addRow(new Object[]{
                    nk.getIdNhapKho(),
                    nk.getIdNV(),
                    nk.getIdNCC(),
                    sdf.format(nk.getNgayNhap()),
                    df.format(nk.getTongTien()) + " đ",
                    nk.getStatus()
                });
            }
        }
    }
    
    private void loadDanhSachSanPham() {
        modelSanPham.setRowCount(0);
        List<Giay> list = giayDAO.getAll();
        
        for (Giay g : list) {
            if (g.getStatus().equalsIgnoreCase("active") || g.getStatus().equalsIgnoreCase("Hoạt động")) {
                modelSanPham.addRow(new Object[]{
                    g.getIdGiay(),
                    g.getTenGiay(),
                    g.getSize(),
                    g.getSoLuong()
                });
            }
        }
    }
    
    private void xemChiTietNhapKho(int row) {
        String idNK = tableNhapKho.getValueAt(row, 0).toString();
        String idNV = tableNhapKho.getValueAt(row, 1).toString();
        String idNCC = tableNhapKho.getValueAt(row, 2).toString();
        String ngayLap = tableNhapKho.getValueAt(row, 3).toString();
        String tongTien = tableNhapKho.getValueAt(row, 4).toString();
        String trangThai = tableNhapKho.getValueAt(row, 5).toString();
        
        // Lấy thông tin nhân viên
        NhanVien nv = nhanVienDAO.getById(idNV);
        String tenNV = (nv != null) ? nv.getTenNV() : "N/A";
        
        // Lấy thông tin nhà cung cấp
        NhaCungCap ncc = nhaCungCapDAO.getById(idNCC);
        String tenNCC = (ncc != null) ? ncc.getTenNCC() : "N/A";
        
        // Lấy chi tiết phiếu nhập
        List<ChiTietNhapKho> listCT = chiTietDAO.getByNhapKho(idNK);
        StringBuilder chiTiet = new StringBuilder();
        chiTiet.append("═══════════════════════════════════════\n");
        chiTiet.append("         PHIẾU NHẬP KHO CHI TIẾT\n");
        chiTiet.append("═══════════════════════════════════════\n\n");
        chiTiet.append("Mã phiếu NK: ").append(idNK).append("\n");
        chiTiet.append("Ngày lập: ").append(ngayLap).append("\n");
        chiTiet.append("Nhân viên: ").append(tenNV).append(" (").append(idNV).append(")\n");
        chiTiet.append("Nhà cung cấp: ").append(tenNCC).append("\n");
        chiTiet.append("Trạng thái: ").append(trangThai).append("\n");
        chiTiet.append("\n───────────────────────────────────────\n");
        chiTiet.append("CHI TIẾT SẢN PHẨM NHẬP:\n");
        chiTiet.append("───────────────────────────────────────\n\n");
        
        int stt = 1;
        for (ChiTietNhapKho ct : listCT) {
            Giay giay = giayDAO.getById(ct.getIdGiay());
            String tenGiay = (giay != null) ? giay.getTenGiay() : "N/A";
            
            chiTiet.append(stt++).append(". ").append(tenGiay).append("\n");
            chiTiet.append("   Số lượng: ").append(ct.getSoLuong()).append("\n");
            chiTiet.append("   Giá nhập: ").append(df.format(ct.getGiaNhap())).append(" đ\n");
            chiTiet.append("   Thành tiền: ").append(df.format(ct.getThanhTien())).append(" đ\n\n");
        }
        
        chiTiet.append("═══════════════════════════════════════\n");
        chiTiet.append("TỔNG TIỀN: ").append(tongTien).append("\n");
        chiTiet.append("═══════════════════════════════════════\n");
        
        JTextArea textArea = new JTextArea(chiTiet.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Chi tiết phiếu nhập kho: " + idNK, JOptionPane.INFORMATION_MESSAGE);
    }
}