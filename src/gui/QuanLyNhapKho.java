package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*; 
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.border.Border; 
import DAO.*;
import model.*;
import javax.swing.border.TitledBorder; // THÊM MỚI

public class QuanLyNhapKho extends JPanel {
    private JTable tableNhapKho, tableChiTiet, tableSanPham;
    private DefaultTableModel modelNhapKho, modelChiTiet, modelSanPham;
    
    private JTextField txtIdNK, txtIdNV, txtTimKiem, txtSoLuong, txtGiaNhap, txtTimKiemSP; 
    private JLabel lblTongTien;
    
    // === SỬA ĐỔI: Thêm btnXemChiTietNK ===
    private JButton btnTaoMoi, btnThemSP, btnXoaSP, btnLuuNK, btnHuyNK, btnLamMoi, btnTimKiem, btnXacNhan, btnXoaNK, btnXemChiTietNK; 
    private JButton btnThemNCC; 
    
    private JComboBox<NhaCungCap> cboNhaCungCap; 
    private JComboBox<String> cboLocTrangThai;
    
    private DAO_NhapKho nhapKhoDAO;
    private DAO_ChiTietNhapKho chiTietDAO;
    private DAO_NhaCungCap nhaCungCapDAO;
    private DAO_Giay giayDAO;
    private DAO_NhanVien nhanVienDAO;
    
    private DecimalFormat df = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
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
        
        loadDanhSachNhapKho(false);
        loadDanhSachSanPham(false);
        loadComboBoxNCC(); 
        
        setFormTaoNKEnabled(false);
        
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
    
    public QuanLyNhapKho(NhanVien user) {
        this(); 
        this.currentUser = user;
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        }
        
        applyPermissions();
        setFormTaoNKEnabled(false);
    }
    
    private void applyPermissions() {
        // ... (Không thay đổi) ...
        if (btnXoaNK == null || btnThemNCC == null) return; 

        if (currentUser != null) {
            String roleId = "";
            try {
                roleId = currentUser.getIdPQ(); 
            } catch (Exception e) {
            }
            
            if ("PQ001".equalsIgnoreCase(roleId)) {
                btnXoaNK.setVisible(true); 
                btnThemNCC.setVisible(true); 
            } else {
                btnXoaNK.setVisible(false); 
                btnThemNCC.setVisible(false); 
            }
        } else {
            btnXoaNK.setVisible(false);
            btnThemNCC.setVisible(false);
        }
    }
    
    private JPanel createTitlePanel() {
        // ... (Không thay đổi) ...
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
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        JPanel leftPanel = createNhapKhoPanel();
        leftPanel.setPreferredSize(new Dimension(450, 0));
        
        JPanel centerPanel = createDanhSachNhapKhoPanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createNhapKhoPanel() {
        // ... (Không thay đổi) ...
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(39, 174, 96));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel lblHeader = new JLabel("TẠO PHIẾU NHẬP KHO MỚI", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(new Color(39, 174, 96));

        btnThemNCC = createStyledButton("Thêm nhà cung cấp", new Color(230, 126, 34)); 
        btnThemNCC.setPreferredSize(new Dimension(160, 30)); 
        btnThemNCC.addActionListener(e -> moDialogThemNCC());
        btnThemNCC.setVisible(false); 
        buttonPanel.add(btnThemNCC);

        btnTaoMoi = createStyledButton("Tạo mới", new Color(241, 196, 15)); 
        btnTaoMoi.setPreferredSize(new Dimension(100, 30));
        btnTaoMoi.addActionListener(e -> taoPhieuNhapKhoMoi());
        buttonPanel.add(btnTaoMoi);

        headerPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(createThongTinNKPanel(), BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        middlePanel.setBackground(Color.WHITE);
        middlePanel.add(createChiTietNKPanel());
        middlePanel.add(createSanPhamPanel());
        contentPanel.add(middlePanel, BorderLayout.CENTER);

        contentPanel.add(createTongTienPanel(), BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    
    private JPanel createThongTinNKPanel() {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu nhập kho"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0; 

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1; 
        panel.add(new JLabel("Mã phiếu nhập kho:"), gbc);
        txtIdNK = new JTextField(10);
        txtIdNK.setEditable(false);
        txtIdNK.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.9; 
        panel.add(txtIdNK, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        panel.add(new JLabel("Mã nhân viên:"), gbc);
        txtIdNV = new JTextField(10);
        txtIdNV.setEditable(false);
        txtIdNV.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.9;
        panel.add(txtIdNV, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        panel.add(new JLabel("Nhà cung cấp:"), gbc);
        
        cboNhaCungCap = new JComboBox<>();
        cboNhaCungCap.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.9;
        panel.add(cboNhaCungCap, gbc);
        
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

        // <<< SỬA: THÊM 3 DÒNG SAU ĐỂ HIỂN THỊ BẢNG >>>
        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.setPreferredSize(new Dimension(0, 150)); // Đặt chiều cao cho bảng
        panel.add(scrollPane, BorderLayout.CENTER); 
        // ===============================================
        
        JPanel btnPanel = new JPanel(new BorderLayout(10, 0)); // 10px Hgap
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Đệm ở trên
        
        // 2. Tạo Panel TÌM KIẾM (Bên trái - WEST)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setBackground(Color.WHITE);

        txtTimKiemSP = new JTextField(10); // Kích thước 10
        txtTimKiemSP.setToolTipText("Nhập tên hoặc mã sản phẩm...");
        
        JButton btnTimSP = createStyledButton("Tìm", new Color(52, 152, 219));
        
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
        btnXoaSP.addActionListener(e -> xoaSanPhamKhoiNK());
        
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
        
        JPanel bottomContainer = new JPanel(new GridLayout(2, 1, 5, 5)); 
        bottomContainer.setBackground(Color.WHITE);

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
        
        JPanel buttonLine = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonLine.setBackground(Color.WHITE);
        
        btnThemSP = createStyledButton("Thêm vào nhập kho", new Color(39, 174, 96));
        btnThemSP.addActionListener(e -> themSanPhamVaoNK());
        buttonLine.add(btnThemSP);
        
        bottomContainer.add(inputLine);
        bottomContainer.add(buttonLine);
        
        panel.add(bottomContainer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTongTienPanel() {
        // ... (Không thay đổi) ...
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
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
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel lblHeader = new JLabel("DANH SÁCH PHIẾU NHẬP KHO");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(lblHeader, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        searchPanel.add(new JLabel("Trạng thái:"));
        String[] options = {"Tất cả", "Đã xác nhận", "Chờ xác nhận"};
        cboLocTrangThai = new JComboBox<>(options);
        cboLocTrangThai.addActionListener(e -> loadDanhSachNhapKho(false)); 
        searchPanel.add(cboLocTrangThai);
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        searchPanel.add(txtTimKiem);
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadDanhSachNhapKho(true); 
                } else {
                    loadDanhSachNhapKho(false); 
                }
            }
        });
        
        btnTimKiem = createStyledButton("Tìm", new Color(52, 152, 219));
        btnTimKiem.setPreferredSize(new Dimension(80, 28));
        btnTimKiem.addActionListener(e -> loadDanhSachNhapKho(true));
        searchPanel.add(btnTimKiem);
        
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        btnLamMoi.setPreferredSize(new Dimension(100, 28));
        btnLamMoi.addActionListener(e -> {
            cboLocTrangThai.setSelectedIndex(0); 
            txtTimKiem.setText(""); 
            loadDanhSachNhapKho(false); 
        });
        searchPanel.add(btnLamMoi);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = {"Mã phiếu nhập kho", "Mã nhân viên", "Mã nhà cung cấp", "Ngày lập", "Tổng tiền", "Trạng thái"};
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
        
        // === SỬA ĐỔI: Bỏ Double-Click, đổi thành Single-Click ===
        tableNhapKho.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableNhapKho.getSelectedRow();
                if (row != -1) {
                    // Kích hoạt các nút khi một hàng được chọn
                    btnXemChiTietNK.setEnabled(true);
                    btnXacNhan.setEnabled(true);
                    if (btnXoaNK.isVisible()) {
                        btnXoaNK.setEnabled(true);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableNhapKho);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // === SỬA ĐỔI: Thêm nút Xem Chi Tiết ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        
        btnXemChiTietNK = createStyledButton("Chi tiết phiếu nhập", new Color(149, 165, 166));
        btnXemChiTietNK.setPreferredSize(new Dimension(150, 35));
        btnXemChiTietNK.addActionListener(e -> moDialogChiTietNK());
        btnXemChiTietNK.setEnabled(false); // Mặc định tắt
        bottomPanel.add(btnXemChiTietNK);
        
        btnXoaNK = createStyledButton("Xóa phiếu", new Color(231, 76, 60));
        btnXoaNK.setPreferredSize(new Dimension(150, 35));
        btnXoaNK.addActionListener(e -> xoaPhieuNhapKho());
        btnXoaNK.setVisible(false); 
        btnXoaNK.setEnabled(false); // Mặc định tắt
        bottomPanel.add(btnXoaNK);
        
        btnXacNhan = createStyledButton("Xác nhận nhập kho", new Color(39, 174, 96));
        btnXacNhan.setPreferredSize(new Dimension(180, 35));
        btnXacNhan.addActionListener(e -> xacNhanNhapKho());
        btnXacNhan.setEnabled(false); // Mặc định tắt
        bottomPanel.add(btnXacNhan);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void setFormTaoNKEnabled(boolean enabled) {
        // ... (Không thay đổi) ...
        cboNhaCungCap.setEnabled(enabled); 
        txtSoLuong.setEnabled(enabled);
        txtGiaNhap.setEnabled(enabled);
        btnThemSP.setEnabled(enabled);
        btnXoaSP.setEnabled(enabled);
        btnLuuNK.setEnabled(enabled);
        btnHuyNK.setEnabled(enabled);
        btnTaoMoi.setEnabled(!enabled);
        
        // === THÊM MỚI: Vô hiệu hóa các nút khi tạo phiếu mới ===
        btnXemChiTietNK.setEnabled(false);
        btnXacNhan.setEnabled(false);
        btnXoaNK.setEnabled(false);
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
    
    private void taoPhieuNhapKhoMoi() {
        // ... (Không thay đổi) ...
        currentNhapKhoId = generateNextNhapKhoId();
        txtIdNK.setText(currentNhapKhoId);
        
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        } else {
            txtIdNV.setText("NV001"); // Fallback
        }
        
        cboNhaCungCap.setSelectedIndex(-1); 
        modelChiTiet.setRowCount(0);
        tongTienNK = 0;
        updateTongTien();
        
        if (txtTimKiemSP != null) {
            txtTimKiemSP.setText(""); 
        }
        
        loadDanhSachSanPham(false); 
        
        setFormTaoNKEnabled(true);
        cboNhaCungCap.requestFocus(); 
        
        JOptionPane.showMessageDialog(this, "Đã tạo phiếu nhập kho mới: " + currentNhapKhoId,
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String generateNextNhapKhoId() {
        // ... (Không thay đổi) ...
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
    
    private void loadComboBoxNCC() {
        // ... (Không thay đổi) ...
        if (nhaCungCapDAO == null) {
            nhaCungCapDAO = new DAO_NhaCungCap();
        }
        if (cboNhaCungCap == null) {
            cboNhaCungCap = new JComboBox<>();
        }
        
        cboNhaCungCap.removeAllItems();
        List<NhaCungCap> list = nhaCungCapDAO.getAllActive(); 
        
        if (list != null) {
            for (NhaCungCap ncc : list) {
                cboNhaCungCap.addItem(ncc); 
            }
        }
        cboNhaCungCap.setSelectedIndex(-1); 
    }

    private void moDialogThemNCC() {
        // ... (Không thay đổi) ...
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(topFrame, "Thêm Nhà Cung Cấp Mới", true);
        dialog.setSize(500, 420); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout()); 
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(46, 204, 113)); 
        headerPanel.setPreferredSize(new Dimension(0, 60)); 
        headerPanel.setLayout(new GridBagLayout()); 
        
        JLabel lblDialogTitle = new JLabel("Thêm Nhà Cung Cấp Mới");
        lblDialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblDialogTitle.setForeground(Color.WHITE);
        headerPanel.add(lblDialogTitle); 
        dialog.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8); 
        gbc.weightx = 1.0;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color borderColor = new Color(189, 195, 199); 
        
        Border textFieldBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1), 
            BorderFactory.createEmptyBorder(5, 8, 5, 8) 
        );

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; 
        JLabel lblTen = new JLabel("Tên nhà cung cấp:");
        lblTen.setFont(labelFont);
        formPanel.add(lblTen, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7; 
        JTextField txtTen = new JTextField(25);
        txtTen.setFont(textFont);
        txtTen.setBorder(textFieldBorder);
        formPanel.add(txtTen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblSDT = new JLabel("Số điện thoại:");
        lblSDT.setFont(labelFont);
        formPanel.add(lblSDT, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        JTextField txtSDT = new JTextField(25);
        txtSDT.setFont(textFont);
        txtSDT.setBorder(textFieldBorder);
        formPanel.add(txtSDT, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        JTextField txtEmail = new JTextField(25);
        txtEmail.setFont(textFont);
        txtEmail.setBorder(textFieldBorder);
        formPanel.add(txtEmail, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(labelFont);
        formPanel.add(lblDiaChi, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        JTextField txtDiaChi = new JTextField(25);
        txtDiaChi.setFont(textFont);
        txtDiaChi.setBorder(textFieldBorder);
        formPanel.add(txtDiaChi, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30)); 
        
        JButton btnHuy = createStyledButton("Hủy", new Color(149, 165, 166)); 
        JButton btnLuu = createStyledButton("Lưu", new Color(46, 204, 113)); 
        
        Dimension btnSize = new Dimension(100, 35); 
        btnHuy.setPreferredSize(btnSize);
        btnLuu.setPreferredSize(btnSize);

        btnHuy.addActionListener(e -> dialog.dispose());
        
// ... trong phương thức private void moDialogThemNCC() ...
        
        btnLuu.addActionListener(e -> {
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String email = txtEmail.getText().trim(); // Lấy giá trị Email
            String diaChi = txtDiaChi.getText().trim();
            
            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tên và Số điện thoại là bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // === THÊM RÀNG BUỘC SỐ ĐIỆN THOẠI ===
            if (!sdt.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(dialog, 
                    "Số điện thoại không hợp lệ! Phải có đúng 10 chữ số.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtSDT.requestFocus();
                return;
            }
            
            // === THÊM RÀNG BUỘC EMAIL (Nếu có nhập) ===
            // Regex cơ bản: chấp nhận chữ/số + @ + chữ/số + . + 2-6 ký tự cuối
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
            if (!email.isEmpty() && !email.matches(emailRegex)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Email không hợp lệ! Vui lòng kiểm tra lại định dạng.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return;
            }
            
            String newId = nhaCungCapDAO.generateNextId(); 
            NhaCungCap newNCC = new NhaCungCap(
                newId, ten, sdt,
                email, // Sử dụng biến email đã trim
                diaChi, // Sử dụng biến diaChi đã trim
                "Hoạt động" 
            );
            
            if (nhaCungCapDAO.insert(newNCC)) {
                JOptionPane.showMessageDialog(dialog, "Thêm NCC thành công: " + newId);
                dialog.dispose();
                
                cboNhaCungCap.addItem(newNCC); 
                cboNhaCungCap.setSelectedItem(newNCC); 
            } else {
                JOptionPane.showMessageDialog(dialog, "Thêm NCC thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(btnHuy);
        buttonPanel.add(btnLuu);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    
    private void themSanPhamVaoNK() {
        // ... (Không thay đổi) ...
        if (currentNhapKhoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo phiếu nhập kho mới trước!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (cboNhaCungCap.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp!",
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
            
            modelChiTiet.addRow(new Object[]{
                maSP, tenSP, soLuong,
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

        try {
            // Lấy thông tin hiện tại của dòng
            int currentSoLuong = Integer.parseInt(modelChiTiet.getValueAt(row, 2).toString());
            String giaNhapStr = modelChiTiet.getValueAt(row, 3).toString().replace(",", "").replace(" đ", "");
            float giaNhap = Float.parseFloat(giaNhapStr);
            String tenSP = modelChiTiet.getValueAt(row, 1).toString();

            // Hỏi người dùng số lượng muốn xóa
            String input = JOptionPane.showInputDialog(this, 
                "Sản phẩm: " + tenSP + "\nHiện có: " + currentSoLuong + "\nNhập số lượng muốn xóa:", 
                currentSoLuong); // Mặc định hiển thị số lượng max

            if (input == null) return; // Người dùng ấn Cancel

            int xoaSoLuong = Integer.parseInt(input.trim());

            // Validate dữ liệu nhập
            if (xoaSoLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng xóa phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (xoaSoLuong > currentSoLuong) {
                JOptionPane.showMessageDialog(this, "Không thể xóa nhiều hơn số lượng hiện có!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tính toán tiền cần trừ
            float tienTru = xoaSoLuong * giaNhap;

            if (xoaSoLuong == currentSoLuong) {
                // Trường hợp 1: Xóa hết -> Xóa dòng
                modelChiTiet.removeRow(row);
            } else {
                // Trường hợp 2: Xóa một phần -> Cập nhật lại dòng
                int soLuongMoi = currentSoLuong - xoaSoLuong;
                float thanhTienMoi = soLuongMoi * giaNhap;
                
                modelChiTiet.setValueAt(soLuongMoi, row, 2); // Cập nhật cột Số lượng
                modelChiTiet.setValueAt(df.format(thanhTienMoi) + " đ", row, 4); // Cập nhật cột Thành tiền
            }

            // Cập nhật tổng tiền phiếu nhập
            tongTienNK -= tienTru;
            updateTongTien();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateTongTien() {
        // ... (Không thay đổi) ...
        lblTongTien.setText(df.format(tongTienNK) + " đ");
    }
    
    private void luuPhieuNhapKho() {
        // ... (Không thay đổi) ...
        if (currentNhapKhoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo phiếu nhập kho mới!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (cboNhaCungCap.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (modelChiTiet.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào phiếu nhập!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            NhapKho nk = new NhapKho();
            nk.setIdNhapKho(currentNhapKhoId);
            nk.setIdNV(txtIdNV.getText());
            
            NhaCungCap selectedNCC = (NhaCungCap) cboNhaCungCap.getSelectedItem();
            nk.setIdNCC(selectedNCC.getIdNCC());
            
            nk.setNgayNhap(new Date());
            nk.setTongTien(tongTienNK);
            nk.setStatus("Chờ xác nhận"); 
            
            if (nhapKhoDAO.insert(nk)) {
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
                    ctnk.setStatus("Hoạt động");
                    
                    chiTietDAO.insert(ctnk);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Lưu phiếu nhập kho tạm thành công!\nMã phiếu: " + currentNhapKhoId + 
                    "\nTrạng thái: Chờ xác nhận",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                currentNhapKhoId = "";
                txtIdNK.setText("");
                cboNhaCungCap.setSelectedIndex(-1); 
                modelChiTiet.setRowCount(0);
                tongTienNK = 0;
                updateTongTien();
                
                setFormTaoNKEnabled(false);
                loadDanhSachNhapKho(false); 
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
        // ... (Không thay đổi) ...
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
            cboNhaCungCap.setSelectedIndex(-1); 
            modelChiTiet.setRowCount(0);
            tongTienNK = 0;
            updateTongTien();
            
            setFormTaoNKEnabled(false);
            
            JOptionPane.showMessageDialog(this, "Đã hủy phiếu nhập kho tạm!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void xacNhanNhapKho() {
        // ... (Không thay đổi) ...
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
                
                for (ChiTietNhapKho ct : listCT) {
                    Giay giay = giayDAO.getById(ct.getIdGiay());
                    if (giay == null) continue; 
                    
                    int tonKhoMoi = giay.getSoLuong() + ct.getSoLuong();
                    giayDAO.updateSoLuong(giay.getIdGiay(), tonKhoMoi);
                }
                
                NhapKho nk = nhapKhoDAO.getById(idNK);
                if (nk == null) {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy phiếu nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                nk.setStatus("Đã xác nhận");
                if (nhapKhoDAO.update(nk)) {
                    JOptionPane.showMessageDialog(this, 
                    "Xác nhận nhập kho thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadDanhSachNhapKho(false); 
                    loadDanhSachSanPham(false); 
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
        // ... (Không thay đổi) ...
        int row = tableNhapKho.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu nhập kho cần xóa!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idNK = tableNhapKho.getValueAt(row, 0).toString();
        String trangThai = tableNhapKho.getValueAt(row, 5).toString();
        
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
                chiTietDAO.deleteByNhapKhoId(idNK); 
                boolean nhapKhoDeleted = nhapKhoDAO.delete(idNK); 
                
                if (nhapKhoDeleted) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công phiếu nhập kho " + idNK + ".",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDanhSachNhapKho(false); 
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi xóa phiếu nhập kho.", "Lỗi DAO", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi nghiêm trọng khi xóa: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void loadDanhSachNhapKho(boolean showMessage) {
        // ... (Không thay đổi) ...
        modelNhapKho.setRowCount(0);
        
        String statusFilter = "Tất cả";
        if (cboLocTrangThai != null) { 
            statusFilter = cboLocTrangThai.getSelectedItem().toString();
        }
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        
        List<NhapKho> list = nhapKhoDAO.getAll();
        int count = 0; 
        
        for (NhapKho nk : list) {
            boolean statusMatch = false;
            if (statusFilter.equals("Tất cả")) {
                statusMatch = true;
            } else {
                statusMatch = nk.getStatus().equalsIgnoreCase(statusFilter);
            }
            
            if (!statusMatch) {
                continue; 
            }
            
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
            
            if (statusMatch && keywordMatch) {
                modelNhapKho.addRow(new Object[]{
                    nk.getIdNhapKho(),
                    nk.getIdNV(),
                    nk.getIdNCC(),
                    sdf.format(nk.getNgayNhap()),
                    df.format(nk.getTongTien()) + " đ",
                    nk.getStatus()
                });
                count++; 
            }
        }
        
        if (count == 0 && showMessage && !keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy phiếu nhập nào khớp với từ khóa: " + txtTimKiem.getText().trim(),
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadDanhSachNhapKho() {
        // ... (Không thay đổi) ...
        loadDanhSachNhapKho(false);
    }
    
    private void loadDanhSachSanPham(boolean showMessage) {
        // ... (Không thay đổi) ...
        modelSanPham.setRowCount(0);
        
        String keyword = "";
        if (txtTimKiemSP != null) { 
            keyword = txtTimKiemSP.getText().trim().toLowerCase();
        }

        List<Giay> list = giayDAO.getAll();
        int count = 0; 
        
        for (Giay g : list) {
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
                        g.getSoLuong() 
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
    private void moDialogChiTietNK() {
        int row = tableNhapKho.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu nhập để xem chi tiết.", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idNK = tableNhapKho.getValueAt(row, 0).toString();
        
        // Tạo và hiển thị dialog
        ChiTietNhapKhoDialog dialog = new ChiTietNhapKhoDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            idNK
        );
        dialog.setVisible(true);
    }
    
    // === THÊM MỚI: Lớp Dialog lồng bên trong ===
    class ChiTietNhapKhoDialog extends JDialog {
        private DefaultTableModel modelChiTietDialog;
        
        public ChiTietNhapKhoDialog(JFrame parent, String idNK) {
            super(parent, "Chi Tiết Phiếu Nhập Kho: " + idNK, true); // true = modal
            
            setSize(750, 600);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout(10, 10));
            
            // Lấy dữ liệu
            NhapKho nk = nhapKhoDAO.getById(idNK);
            if (nk == null) {
                JOptionPane.showMessageDialog(this, "Không thể tìm thấy phiếu nhập " + idNK, "Lỗi", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
            
            NhanVien nv = nhanVienDAO.getById(nk.getIdNV());
            NhaCungCap ncc = nhaCungCapDAO.getById(nk.getIdNCC());
            List<ChiTietNhapKho> listCT = chiTietDAO.getByNhapKho(idNK);

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
            infoPanel.add(new JLabel("Mã phiếu nhập kho:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(createReadOnlyTextField(idNK), gbc);
            
            gbc.gridx = 2;
            infoPanel.add(new JLabel("Ngày lập:"), gbc);
            gbc.gridx = 3;
            infoPanel.add(createReadOnlyTextField(sdf.format(nk.getNgayNhap())), gbc);
            
            // Hàng 2
            gbc.gridx = 0; gbc.gridy = 1;
            infoPanel.add(new JLabel("Nhân viên:"), gbc);
            gbc.gridx = 1;
            infoPanel.add(createReadOnlyTextField(nv != null ? nv.getTenNV() + " (" + nv.getIdNV() + ")" : nk.getIdNV()), gbc);
            
            gbc.gridx = 2;
            infoPanel.add(new JLabel("Trạng thái:"), gbc);
            gbc.gridx = 3;
            infoPanel.add(createReadOnlyTextField(nk.getStatus()), gbc);
            
            // Hàng 3
            gbc.gridx = 0; gbc.gridy = 2;
            infoPanel.add(new JLabel("Nhà cung cấp:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; // Cho NCC chiếm 3 cột
            infoPanel.add(createReadOnlyTextField(ncc != null ? ncc.getTenNCC() + " (" + ncc.getIdNCC() + ")" : nk.getIdNCC()), gbc);

            add(infoPanel, BorderLayout.NORTH);

            // 2. Panel Chi Tiết Sản Phẩm (CENTER)
            String[] columns = {"Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Giá nhập", "Thành tiền"};
            modelChiTietDialog = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            
            JTable tableChiTietDialog = new JTable(modelChiTietDialog);
            tableChiTietDialog.setRowHeight(25);
            tableChiTietDialog.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            // Đổ dữ liệu vào bảng
            if (listCT != null) {
                for (ChiTietNhapKho ct : listCT) {
                    Giay giay = giayDAO.getById(ct.getIdGiay());
                    String tenGiay = (giay != null) ? giay.getTenGiay() : "Sản phẩm không tồn tại";
                    modelChiTietDialog.addRow(new Object[]{
                        ct.getIdGiay(),
                        tenGiay,
                        ct.getSoLuong(),
                        df.format(ct.getGiaNhap()) + " đ",
                        df.format(ct.getThanhTien()) + " đ"
                    });
                }
            }

            JScrollPane scrollPane = new JScrollPane(tableChiTietDialog);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Chi tiết sản phẩm nhập"));
            add(scrollPane, BorderLayout.CENTER);

            // 3. Panel Tổng Tiền và Nút Đóng (SOUTH)
            JPanel southPanel = new JPanel(new BorderLayout());
            southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel lblTong = new JLabel("Tổng Tiền: " + df.format(nk.getTongTien()) + " đ");
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
}