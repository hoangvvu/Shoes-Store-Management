package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// SỬA ĐỔI: Đảm bảo có KeyAdapter và KeyEvent (mặc dù java.awt.event.* đã bao gồm)
import java.awt.event.*; 
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.border.Border; 
import DAO.*;
import model.*;

public class QuanLyNhapKho extends JPanel {
    private JTable tableNhapKho, tableChiTiet, tableSanPham;
    private DefaultTableModel modelNhapKho, modelChiTiet, modelSanPham;
    
    // SỬA ĐỔI: Thêm txtTimKiemSP
    private JTextField txtIdNK, txtIdNV, txtTimKiem, txtSoLuong, txtGiaNhap, txtTimKiemSP; 
    private JLabel lblTongTien;
    
    // Nút đã sửa đổi và chuyển lên Header
    private JButton btnTaoMoi, btnThemSP, btnXoaSP, btnLuuNK, btnHuyNK, btnLamMoi, btnTimKiem, btnXacNhan, btnXoaNK; 
    private JButton btnThemNCC; // Nút thêm NCC mới
    
    private JComboBox<NhaCungCap> cboNhaCungCap; // ComboBox thay thế TextFields
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
        
        // SỬA ĐỔI: Gọi hàm (boolean) mới
        loadDanhSachNhapKho(false);
        loadDanhSachSanPham(false);
        loadComboBoxNCC(); 
        
        setFormTaoNKEnabled(false);
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Tải lại danh sách sản phẩm để lấy dữ liệu mới nhất
                // (ví dụ: sau khi thêm ở tab QuanLyGiay)
                
                // SỬA ĐỔI: Thêm reset ô tìm kiếm (Giống QuanLyHoaDon)
                if (txtTimKiemSP != null) {
                    txtTimKiemSP.setText("");
                }
                loadDanhSachSanPham(false); // SỬA ĐỔI: Gọi hàm (boolean) mới
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
    
    /**
     * PHÂN QUYỀN: Chỉ Admin (PQ001) được xóa phiếu và thêm NCC
     */
    private void applyPermissions() {
        if (btnXoaNK == null || btnThemNCC == null) return; 

        if (currentUser != null) {
            String roleId = "";
            try {
                roleId = currentUser.getIdPQ(); 
            } catch (Exception e) {
            }
            
            // Dùng ID quyền đã tạo trong SQL (PQ001 là Admin)
            if ("PQ001".equalsIgnoreCase(roleId)) {
                btnXoaNK.setVisible(true); 
                btnThemNCC.setVisible(true); // <<< HIỂN THỊ NÚT THÊM NCC CHO ADMIN
            } else {
                btnXoaNK.setVisible(false); 
                btnThemNCC.setVisible(false); // <<< ẨN NÚT THÊM NCC VỚI USER THƯỜNG
            }
        } else {
            btnXoaNK.setVisible(false);
            btnThemNCC.setVisible(false);
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
        
        JPanel leftPanel = createNhapKhoPanel();
        leftPanel.setPreferredSize(new Dimension(450, 0));
        
        JPanel centerPanel = createDanhSachNhapKhoPanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // === ĐÃ SỬA: Chuyển nút Thêm NCC lên đây ===
    private JPanel createNhapKhoPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // === PHẦN HEADER ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(39, 174, 96));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Dòng chữ tiêu đề (căn giữa)
        JLabel lblHeader = new JLabel("TẠO PHIẾU NHẬP KHO MỚI", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.NORTH);

        // === HÀNG NÚT PHÍA DƯỚI TIÊU ĐỀ ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(new Color(39, 174, 96));

        // Nút Thêm Nhà Cung Cấp
        btnThemNCC = createStyledButton("Thêm nhà cung cấp", new Color(230, 126, 34)); 
        btnThemNCC.setPreferredSize(new Dimension(160, 30)); 
        btnThemNCC.addActionListener(e -> moDialogThemNCC());
        btnThemNCC.setVisible(false); // Mặc định ẩn
        buttonPanel.add(btnThemNCC);

        // Nút Tạo Mới
        btnTaoMoi = createStyledButton("Tạo mới", new Color(241, 196, 15)); 
        btnTaoMoi.setPreferredSize(new Dimension(100, 30));
        btnTaoMoi.addActionListener(e -> taoPhieuNhapKhoMoi());
        buttonPanel.add(btnTaoMoi);

        headerPanel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // === PHẦN NỘI DUNG CHÍNH ===
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

    
    // === ĐÃ SỬA: Chỉ còn ComboBox và không còn nút thêm NCC ở đây ===
    private JPanel createThongTinNKPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu nhập kho"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0; 

        // Mã NK
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1; 
        panel.add(new JLabel("Mã phiếu NK:"), gbc);
        txtIdNK = new JTextField(10);
        txtIdNK.setEditable(false);
        txtIdNK.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.9; 
        panel.add(txtIdNK, gbc);
        
        // Mã NV
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        panel.add(new JLabel("Mã nhân viên:"), gbc);
        txtIdNV = new JTextField(10);
        txtIdNV.setEditable(false);
        txtIdNV.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.9;
        panel.add(txtIdNV, gbc);
        
        // Nhà Cung Cấp (Chỉ còn ComboBox)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1;
        panel.add(new JLabel("Nhà cung cấp:"), gbc);
        
        cboNhaCungCap = new JComboBox<>();
        cboNhaCungCap.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.9;
        panel.add(cboNhaCungCap, gbc);
        
        return panel;
    }
    
    // SỬA ĐỔI: Thêm ô tìm kiếm sản phẩm
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
        
        // === SỬA ĐỔI: Thêm ô tìm kiếm sản phẩm (Giống QuanLyHoaDon) ===
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        
        // Ô nhập tìm kiếm sản phẩm
        txtTimKiemSP = new JTextField(12);
        txtTimKiemSP.setToolTipText("Nhập tên hoặc mã sản phẩm...");
        
        // Nút Tìm kiếm sản phẩm
        JButton btnTimSP = createStyledButton("Tìm sản phẩm", new Color(52, 152, 219));
        
        // SỬA: Cập nhật ActionListener (giống QuanLyHoaDon)
        btnTimSP.addActionListener(e -> {
            loadDanhSachSanPham(true); // true = hiển thị thông báo nếu không tìm thấy
        });

        // SỬA: Thêm KeyAdapter (giống QuanLyHoaDon)
        txtTimKiemSP.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadDanhSachSanPham(true); // Enter: có thông báo
                } else {
                    loadDanhSachSanPham(false); // Gõ: không thông báo
                }
            }
        });
        
        // Nút Xóa SP (cũ)
        btnXoaSP = createStyledButton("Xóa sản phẩm", new Color(231, 76, 60));
        btnXoaSP.addActionListener(e -> xoaSanPhamKhoiNK());
        
        // Thêm vào panel
        btnPanel.add(new JLabel("Tìm sản phẩm:"));
        btnPanel.add(txtTimKiemSP);
        btnPanel.add(btnTimSP);
        btnPanel.add(Box.createHorizontalStrut(20)); // Thêm khoảng cách
        btnPanel.add(btnXoaSP);
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        // === KẾT THÚC SỬA ĐỔI ===
        
        return panel;
    }
    
    private JPanel createSanPhamPanel() {
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
    
    // SỬA ĐỔI: Cập nhật logic tìm kiếm
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
        // SỬA ĐỔI: Gọi hàm (boolean) mới
        cboLocTrangThai.addActionListener(e -> loadDanhSachNhapKho(false)); 
        searchPanel.add(cboLocTrangThai);
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        searchPanel.add(txtTimKiem);
        
        // === THÊM MỚI: KEYLISTENER (Giống QuanLyHoaDon) ===
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadDanhSachNhapKho(true); // Enter: có thông báo
                } else {
                    loadDanhSachNhapKho(false); // Gõ: không thông báo
                }
            }
        });
        // ================================================
        
        btnTimKiem = createStyledButton("Tìm", new Color(52, 152, 219));
        btnTimKiem.setPreferredSize(new Dimension(80, 28));
        // SỬA ĐỔI: Gọi hàm (boolean) mới
        btnTimKiem.addActionListener(e -> loadDanhSachNhapKho(true));
        searchPanel.add(btnTimKiem);
        
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        // SỬA ĐỔI: Cập nhật kích thước nút
        btnLamMoi.setPreferredSize(new Dimension(100, 28));
        btnLamMoi.addActionListener(e -> {
            cboLocTrangThai.setSelectedIndex(0); 
            txtTimKiem.setText(""); 
            loadDanhSachNhapKho(false); // SỬA ĐỔI: Gọi hàm (boolean) mới
        });
        searchPanel.add(btnLamMoi);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
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
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        
        btnXoaNK = createStyledButton("Xóa phiếu", new Color(231, 76, 60));
        btnXoaNK.setPreferredSize(new Dimension(150, 35));
        btnXoaNK.addActionListener(e -> xoaPhieuNhapKho());
        btnXoaNK.setVisible(false); // Sẽ được hiển thị trong applyPermissions nếu là Admin
        bottomPanel.add(btnXoaNK);
        
        btnXacNhan = createStyledButton("Xác nhận nhập kho", new Color(39, 174, 96));
        btnXacNhan.setPreferredSize(new Dimension(180, 35));
        btnXacNhan.addActionListener(e -> xacNhanNhapKho());
        bottomPanel.add(btnXacNhan);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // === ĐÃ SỬA: Loại bỏ logic setEnabled cho btnThemNCC ===
    private void setFormTaoNKEnabled(boolean enabled) {
        // Vô hiệu hóa/Kích hoạt các trường nhập liệu
        cboNhaCungCap.setEnabled(enabled); 
        
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
    
    // SỬA ĐỔI: Thêm reset txtTimKiemSP
    private void taoPhieuNhapKhoMoi() {
        currentNhapKhoId = generateNextNhapKhoId();
        txtIdNK.setText(currentNhapKhoId);
        
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        } else {
            txtIdNV.setText("NV001"); // Fallback
        }
        
        // Clear form
        cboNhaCungCap.setSelectedIndex(-1); 
        modelChiTiet.setRowCount(0);
        tongTienNK = 0;
        updateTongTien();
        
        // THÊM MỚI: Clear bộ lọc SP (Giống QuanLyHoaDon)
        if (txtTimKiemSP != null) {
            txtTimKiemSP.setText(""); 
        }
        
        loadDanhSachSanPham(false); // SỬA ĐỔI: Gọi hàm (boolean) mới
        
        setFormTaoNKEnabled(true);
        cboNhaCungCap.requestFocus(); 
        
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
    
    private void loadComboBoxNCC() {
        if (nhaCungCapDAO == null) {
            nhaCungCapDAO = new DAO_NhaCungCap();
        }
        if (cboNhaCungCap == null) {
            cboNhaCungCap = new JComboBox<>();
        }
        
        cboNhaCungCap.removeAllItems();
        // Lấy tất cả NCC đang "Hoạt động"
        List<NhaCungCap> list = nhaCungCapDAO.getAllActive(); 
        
        if (list != null) {
            for (NhaCungCap ncc : list) {
                cboNhaCungCap.addItem(ncc); 
            }
        }
        cboNhaCungCap.setSelectedIndex(-1); // Bỏ chọn ban đầu
    }

    /**
     * PHƯƠNG THỨC MỚI: Dialog thêm Nhà Cung Cấp (Đã TÂN TRANG UI)
     */
    private void moDialogThemNCC() {
        // 1. Tạo Dialog
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(topFrame, "Thêm Nhà Cung Cấp Mới", true);
        dialog.setSize(500, 420); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout()); 
        dialog.getContentPane().setBackground(Color.WHITE);

        // 2. Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(46, 204, 113)); 
        headerPanel.setPreferredSize(new Dimension(0, 60)); 
        headerPanel.setLayout(new GridBagLayout()); 
        
        JLabel lblDialogTitle = new JLabel("Thêm Nhà Cung Cấp Mới");
        lblDialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblDialogTitle.setForeground(Color.WHITE);
        headerPanel.add(lblDialogTitle); 
        dialog.add(headerPanel, BorderLayout.NORTH);

        // 3. Form Panel (Nội dung chính)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8); 
        gbc.weightx = 1.0;

        // Định nghĩa style
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color borderColor = new Color(189, 195, 199); 
        
        Border textFieldBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1), 
            BorderFactory.createEmptyBorder(5, 8, 5, 8) 
        );

        // Các trường nhập liệu
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; 
        JLabel lblTen = new JLabel("Tên NCC (*):");
        lblTen.setFont(labelFont);
        formPanel.add(lblTen, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7; 
        JTextField txtTen = new JTextField(25);
        txtTen.setFont(textFont);
        txtTen.setBorder(textFieldBorder);
        formPanel.add(txtTen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblSDT = new JLabel("Số điện thoại (*):");
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

        // 4. Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30)); 
        
        JButton btnHuy = createStyledButton("Hủy", new Color(149, 165, 166)); 
        JButton btnLuu = createStyledButton("Lưu", new Color(46, 204, 113)); 
        
        Dimension btnSize = new Dimension(100, 35); 
        btnHuy.setPreferredSize(btnSize);
        btnLuu.setPreferredSize(btnSize);

        btnHuy.addActionListener(e -> dialog.dispose());
        
        btnLuu.addActionListener(e -> {
            String ten = txtTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            
            if (ten.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tên và SĐT là bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String newId = nhaCungCapDAO.generateNextId(); 
            NhaCungCap newNCC = new NhaCungCap(
                newId, ten, sdt,
                txtEmail.getText().trim(),
                txtDiaChi.getText().trim(),
                "Hoạt động" 
            );
            
            if (nhaCungCapDAO.insert(newNCC)) {
                JOptionPane.showMessageDialog(dialog, "Thêm NCC thành công: " + newId);
                dialog.dispose();
                
                // <<< FIX LỖI SYNTAX Ở ĐÂY >>>
                cboNhaCungCap.addItem(newNCC); 
                cboNhaCungCap.setSelectedItem(newNCC); 
            } else {
                JOptionPane.showMessageDialog(dialog, "Thêm NCC thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnLuu);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 5. Hiển thị Dialog
        dialog.setVisible(true);
    }
    
    // === ĐÃ SỬA: Cập nhật logic kiểm tra ===
    private void themSanPhamVaoNK() {
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
    
    // === ĐÃ SỬA: Cập nhật logic lưu và reset ===
    private void luuPhieuNhapKho() {
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
                
                // Reset form
                currentNhapKhoId = "";
                txtIdNK.setText("");
                cboNhaCungCap.setSelectedIndex(-1); 
                modelChiTiet.setRowCount(0);
                tongTienNK = 0;
                updateTongTien();
                
                setFormTaoNKEnabled(false);
                loadDanhSachNhapKho(false); // SỬA ĐỔI
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
    
    // === ĐÃ SỬA: Cập nhật logic reset ===
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
                    if (giay == null) continue; // Bỏ qua nếu giày không tồn tại
                    
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
                    
                    loadDanhSachNhapKho(false); // SỬA ĐỔI
                    loadDanhSachSanPham(false); // SỬA ĐỔI
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
                // Xóa chi tiết trước vì có khóa ngoại
                chiTietDAO.deleteByNhapKhoId(idNK); 
                
                // Sau đó xóa phiếu nhập kho
                boolean nhapKhoDeleted = nhapKhoDAO.delete(idNK); 
                
                if (nhapKhoDeleted) {
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công phiếu nhập kho " + idNK + ".",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadDanhSachNhapKho(false); // SỬA ĐỔI
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
    
    // === SỬA ĐỔI: Thay thế hàm loadDanhSachNhapKho() cũ ===
    
    /**
     * SỬA ĐỔI: Tải danh sách có logic thông báo (Giống QuanLyHoaDon)
     * @param showMessage Hiển thị thông báo nếu không tìm thấy
     */
    private void loadDanhSachNhapKho(boolean showMessage) {
        modelNhapKho.setRowCount(0);
        
        String statusFilter = "Tất cả";
        if (cboLocTrangThai != null) { 
            statusFilter = cboLocTrangThai.getSelectedItem().toString();
        }
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        
        List<NhapKho> list = nhapKhoDAO.getAll();
        int count = 0; // THÊM MỚI: Biến đếm
        
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
                count++; // THÊM MỚI: Tăng biến đếm
            }
        }
        
        // THÊM MỚI: Hiển thị thông báo (Giống QuanLyHoaDon)
        if (count == 0 && showMessage && !keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy phiếu nhập nào khớp với từ khóa: " + txtTimKiem.getText().trim(),
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * THÊM MỚI: Phương thức tải mặc định (không thông báo)
     */
    private void loadDanhSachNhapKho() {
        loadDanhSachNhapKho(false);
    }
    
    // === SỬA ĐỔI: Thay thế hàm loadDanhSachSanPham() cũ ===

    /**
     * SỬA ĐỔI: Tải danh sách sản phẩm có lọc và thông báo
     * (Giống QuanLyHoaDon, nhưng không có logic tồn kho/khả dụng)
     * @param showMessage Hiển thị thông báo nếu không tìm thấy
     */
    private void loadDanhSachSanPham(boolean showMessage) {
        modelSanPham.setRowCount(0);
        
        // Lấy từ khóa từ ô tìm kiếm (biến toàn cục)
        String keyword = "";
        if (txtTimKiemSP != null) { 
            keyword = txtTimKiemSP.getText().trim().toLowerCase();
        }

        List<Giay> list = giayDAO.getAll();
        int count = 0; // Biến đếm kết quả
        
        for (Giay g : list) {
            // Chỉ hiển thị sản phẩm "Hoạt động"
            if (g.getStatus().equalsIgnoreCase("Hoạt động")) { 
                
                // LOGIC LỌC TỪ KHÓA (Giống QuanLyHoaDon)
                boolean keywordMatch = false;
                if (keyword.isEmpty()) {
                    keywordMatch = true; // Rỗng thì luôn khớp
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
                        g.getSoLuong() // Cột Tồn kho
                    });
                    count++; 
                }
            }
        }
        
        // HIỂN THỊ THÔNG BÁO (Giống QuanLyHoaDon)
        if (count == 0 && showMessage && !keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy sản phẩm nào khớp với từ khóa: " + txtTimKiemSP.getText().trim(),
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * THÊM MỚI: Phương thức tải mặc định (không thông báo)
     */
    private void loadDanhSachSanPham() {
        loadDanhSachSanPham(false); // Gọi hàm chính với showMessage = false
    }
    
    private void xemChiTietNhapKho(int row) {
        String idNK = tableNhapKho.getValueAt(row, 0).toString();
        String idNV = tableNhapKho.getValueAt(row, 1).toString();
        String idNCC = tableNhapKho.getValueAt(row, 2).toString();
        String ngayLap = tableNhapKho.getValueAt(row, 3).toString();
        String tongTien = tableNhapKho.getValueAt(row, 4).toString();
        String trangThai = tableNhapKho.getValueAt(row, 5).toString();
        
        NhanVien nv = nhanVienDAO.getById(idNV);
        String tenNV = (nv != null) ? nv.getTenNV() : "N/A";
        
        NhaCungCap ncc = nhaCungCapDAO.getById(idNCC); 
        String tenNCC = (ncc != null) ? ncc.getTenNCC() : "N/A";
        
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