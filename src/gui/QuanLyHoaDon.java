package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import DAO.*;
import model.*;

public class QuanLyHoaDon extends JPanel {
    private JTable tableHoaDon, tableChiTiet, tableSanPham;
    private DefaultTableModel modelHoaDon, modelChiTiet, modelSanPham;
    private JTextField txtIdHD, txtIdNV, txtIdKH, txtTenKH, txtSDT, txtTimKiem, txtSoLuong;
    private JLabel lblTongTien, lblThanhToan;
    private JButton btnTaoMoi, btnThemSP, btnXoaSP, btnLuuHD, btnHuyHD, btnTraCuuKH, btnLamMoi, btnTimKiem, btnXuatHD;
    
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
        
        loadDanhSachHoaDon();
        loadDanhSachSanPham();
    }
    
    // Constructor với tham số NhanVien
    public QuanLyHoaDon(NhanVien user) {
        this();
        this.currentUser = user;
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("QUẢN LÝ HÓA ĐƠN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(52, 73, 94));
        panel.add(lblTitle, BorderLayout.CENTER);

        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setBackground(new Color(52, 152, 219));
        btnBack.setForeground(Color.RED);
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Mã HĐ
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã HĐ:"), gbc);
        txtIdHD = new JTextField(10);
        txtIdHD.setEditable(false);
        txtIdHD.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtIdHD, gbc);
        
        // Mã NV
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mã NV:"), gbc);
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
        btnXoaSP.addActionListener(e -> xoaSanPhamKhoiHD());
        btnPanel.add(btnXoaSP);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSanPhamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));
        
        String[] columns = {"Mã", "Tên giày", "Size", "Giá bán", "Tồn kho"};
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
        
        // Nút thêm sản phẩm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(new JLabel("Số lượng:"));
        txtSoLuong = new JTextField(5);
        txtSoLuong.setText("1");
        btnPanel.add(txtSoLuong);
        
        btnThemSP = createStyledButton("Thêm vào HĐ", new Color(46, 204, 113));
        btnThemSP.addActionListener(e -> themSanPhamVaoHD());
        btnPanel.add(btnThemSP);
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTongTienPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Tổng tiền
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
        
        // Nút
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
        
        // Tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        searchPanel.add(txtTimKiem);
        btnTimKiem = createStyledButton("Tìm", new Color(52, 152, 219));
        btnTimKiem.setPreferredSize(new Dimension(80, 28));
        btnTimKiem.addActionListener(e -> timKiemHoaDon());
        searchPanel.add(btnTimKiem);
        
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        btnLamMoi.setPreferredSize(new Dimension(80, 28));
        btnLamMoi.addActionListener(e -> loadDanhSachHoaDon());
        searchPanel.add(btnLamMoi);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Bảng
        String[] columns = {"Mã HĐ", "Mã NV", "Mã KH", "Ngày lập", "Tổng tiền", "Trạng thái"};
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
        tableHoaDon.getTableHeader().setForeground(Color.BLACK);
        
        tableHoaDon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableHoaDon.getSelectedRow();
                    if (row != -1) {
                        xemChiTietHoaDon(row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableHoaDon);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Nút xuất hóa đơn
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        btnXuatHD = createStyledButton("Xuất hóa đơn", new Color(41, 128, 185));
        btnXuatHD.setPreferredSize(new Dimension(150, 35));
        btnXuatHD.addActionListener(e -> xuatHoaDon());
        bottomPanel.add(btnXuatHD);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
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
    
    private void taoHoaDonMoi() {
        // Generate ID mới
        currentHoaDonId = generateNextHoaDonId();
        txtIdHD.setText(currentHoaDonId);
        
        // Set nhân viên
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        } else {
            txtIdNV.setText("NV001"); // Fallback
        }
        
        // Clear form
        txtIdKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        modelChiTiet.setRowCount(0);
        tongTienHD = 0;
        updateTongTien();
        
        JOptionPane.showMessageDialog(this, "Đã tạo hóa đơn mới: " + currentHoaDonId,
            "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String generateNextHoaDonId() {
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
        String sdt = txtSDT.getText().trim();
        
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT khách hàng!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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
                taoKhachHangMoi(sdt);
            }
        }
    }
    
    private void taoKhachHangMoi(String sdt) {
        String ten = JOptionPane.showInputDialog(this, "Nhập tên khách hàng:");
        if (ten == null || ten.trim().isEmpty()) return;
        
        String diaChi = JOptionPane.showInputDialog(this, "Nhập địa chỉ:");
        if (diaChi == null || diaChi.trim().isEmpty()) return;
        
        // Generate ID mới
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
        kh.setDiaChi(diaChi);
        kh.setTongTien(0);
        kh.setStatus("active");
        
        if (khachHangDAO.insert(kh)) {
            txtIdKH.setText(newId);
            txtTenKH.setText(ten);
            JOptionPane.showMessageDialog(this, "Đã tạo khách hàng mới!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void themSanPhamVaoHD() {
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
            int tonKho = Integer.parseInt(tableSanPham.getValueAt(row, 4).toString());
            
            if (soLuong > tonKho) {
                JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            float thanhTien = donGia * soLuong;
            
            // Thêm vào bảng chi tiết
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
        
        String thanhTienStr = tableChiTiet.getValueAt(row, 4).toString()
            .replace(",", "").replace(" đ", "");
        float thanhTien = Float.parseFloat(thanhTienStr);
        
        tongTienHD -= thanhTien;
        modelChiTiet.removeRow(row);
        updateTongTien();
    }
    
    private void updateTongTien() {
        lblTongTien.setText(df.format(tongTienHD) + " đ");
        lblThanhToan.setText(df.format(tongTienHD) + " đ");
    }
    
    private void luuHoaDon() {
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
            // Lưu hóa đơn với trạng thái "Chờ thanh toán"
            HoaDon hd = new HoaDon();
            hd.setIdHD(currentHoaDonId);
            hd.setIdNV(txtIdNV.getText());
            hd.setIdKH(txtIdKH.getText());
            hd.setNgayLap(new Date());
            hd.setTongTien(tongTienHD);
            hd.setStatus("Chờ thanh toán");
            
            if (hoaDonDAO.insert(hd)) {
                // Lưu chi tiết hóa đơn
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
                    cthd.setStatus("active");
                    
                    chiTietDAO.insert(cthd);
                    
                    // Cập nhật số lượng tồn kho
                    String idGiay = cthd.getIdGiay();
                    int soLuongMua = cthd.getSoLuong();
                    Giay giay = giayDAO.getById(idGiay);
                    if (giay != null) {
                        int tonKhoMoi = giay.getSoLuong() - soLuongMua;
                        giayDAO.updateSoLuong(idGiay, tonKhoMoi);
                    }
                }
                
                // Cập nhật tổng tiền khách hàng
                khachHangDAO.updateTongTien(txtIdKH.getText(), tongTienHD);
                
                JOptionPane.showMessageDialog(this, 
                    "Lưu hóa đơn thành công!\nMã HĐ: " + currentHoaDonId + 
                    "\nTổng tiền: " + df.format(tongTienHD) + " đ" +
                    "\nTrạng thái: Chờ thanh toán",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Reset form
                currentHoaDonId = "";
                txtIdHD.setText("");
                txtIdKH.setText("");
                txtTenKH.setText("");
                txtSDT.setText("");
                modelChiTiet.setRowCount(0);
                tongTienHD = 0;
                updateTongTien();
                
                loadDanhSachHoaDon();
                loadDanhSachSanPham();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu hóa đơn: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void huyHoaDon() {
        if (currentHoaDonId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có hóa đơn nào để hủy!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn hủy hóa đơn này?",
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
            
            JOptionPane.showMessageDialog(this, "Đã hủy hóa đơn!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void xuatHoaDon() {
        int row = tableHoaDon.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xuất!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idHD = tableHoaDon.getValueAt(row, 0).toString();
        String trangThai = tableHoaDon.getValueAt(row, 5).toString();
        
        // Kiểm tra trạng thái
        if (trangThai.equals("Đã thanh toán")) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được xuất trước đó!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Xác nhận xuất hóa đơn: " + idHD + "?",
            "Xác nhận xuất hóa đơn", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            // Cập nhật trạng thái hóa đơn
            HoaDon hd = hoaDonDAO.getById(idHD);
            if (hd != null) {
                hd.setStatus("Đã thanh toán");
                if (hoaDonDAO.update(hd)) {
                    // Hiển thị hóa đơn
                    hienThiHoaDonXuat(idHD);
                    
                    // Reload danh sách
                    loadDanhSachHoaDon();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Xuất hóa đơn thành công!\nHóa đơn " + idHD + " đã được thanh toán.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi cập nhật trạng thái hóa đơn!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void hienThiHoaDonXuat(String idHD) {
        HoaDon hd = hoaDonDAO.getById(idHD);
        if (hd == null) return;
        
        // Lấy thông tin khách hàng
        KhachHang kh = khachHangDAO.getById(hd.getIdKH());
        String tenKH = (kh != null) ? kh.getTenKH() : "N/A";
        String sdtKH = (kh != null) ? kh.getSdt() : "N/A";
        String diaChiKH = (kh != null) ? kh.getDiaChi() : "N/A";
        
        // Lấy thông tin nhân viên
        NhanVien nv = nhanVienDAO.getById(hd.getIdNV());
        String tenNV = (nv != null) ? nv.getTenNV() : "N/A";
        
        // Lấy chi tiết hóa đơn
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
    
    private void loadDanhSachHoaDon() {
        modelHoaDon.setRowCount(0);
        List<HoaDon> list = hoaDonDAO.getAll();
        
        for (HoaDon hd : list) {
            modelHoaDon.addRow(new Object[]{
                hd.getIdHD(),
                hd.getIdNV(),
                hd.getIdKH(),
                sdf.format(hd.getNgayLap()),
                df.format(hd.getTongTien()) + " đ",
                hd.getStatus()
            });
        }
    }
    
    private void loadDanhSachSanPham() {
        modelSanPham.setRowCount(0);
        List<Giay> list = giayDAO.getAll();
        
        for (Giay g : list) {
            if (g.getStatus().equals("active") && g.getSoLuong() > 0) {
                modelSanPham.addRow(new Object[]{
                    g.getIdGiay(),
                    g.getTenGiay(),
                    g.getSize(),
                    df.format(g.getGiaBan()) + " đ",
                    g.getSoLuong()
                });
            }
        }
    }
    
    private void timKiemHoaDon() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            loadDanhSachHoaDon();
            return;
        }
        
        modelHoaDon.setRowCount(0);
        List<HoaDon> list = hoaDonDAO.getAll();
        
        for (HoaDon hd : list) {
            String idHD = hd.getIdHD().toLowerCase();
            String idKH = hd.getIdKH().toLowerCase();
            String idNV = hd.getIdNV().toLowerCase();
            String status = hd.getStatus().toLowerCase();
            
            if (idHD.contains(keyword) || idKH.contains(keyword) || 
                idNV.contains(keyword) || status.contains(keyword)) {
                modelHoaDon.addRow(new Object[]{
                    hd.getIdHD(),
                    hd.getIdNV(),
                    hd.getIdKH(),
                    sdf.format(hd.getNgayLap()),
                    df.format(hd.getTongTien()) + " đ",
                    hd.getStatus()
                });
            }
        }
        
        if (modelHoaDon.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn nào!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void xemChiTietHoaDon(int row) {
        String idHD = tableHoaDon.getValueAt(row, 0).toString();
        String idNV = tableHoaDon.getValueAt(row, 1).toString();
        String idKH = tableHoaDon.getValueAt(row, 2).toString();
        String ngayLap = tableHoaDon.getValueAt(row, 3).toString();
        String tongTien = tableHoaDon.getValueAt(row, 4).toString();
        String trangThai = tableHoaDon.getValueAt(row, 5).toString();
        
        // Lấy thông tin nhân viên
        NhanVien nv = nhanVienDAO.getById(idNV);
        String tenNV = (nv != null) ? nv.getTenNV() : "N/A";
        
        // Lấy thông tin khách hàng
        KhachHang kh = khachHangDAO.getById(idKH);
        String tenKH = (kh != null) ? kh.getTenKH() : "N/A";
        String sdtKH = (kh != null) ? kh.getSdt() : "N/A";
        
        // Lấy chi tiết hóa đơn
        List<ChiTietHoaDon> listCT = chiTietDAO.getByHoaDon(idHD);
        StringBuilder chiTiet = new StringBuilder();
        chiTiet.append("═══════════════════════════════════════\n");
        chiTiet.append("           HÓA ĐƠN BÁN HÀNG\n");
        chiTiet.append("═══════════════════════════════════════\n\n");
        chiTiet.append("Mã hóa đơn: ").append(idHD).append("\n");
        chiTiet.append("Ngày lập: ").append(ngayLap).append("\n");
        chiTiet.append("Nhân viên: ").append(tenNV).append(" (").append(idNV).append(")\n");
        chiTiet.append("Khách hàng: ").append(tenKH).append("\n");
        chiTiet.append("SĐT: ").append(sdtKH).append("\n");
        chiTiet.append("Trạng thái: ").append(trangThai).append("\n");
        chiTiet.append("\n───────────────────────────────────────\n");
        chiTiet.append("CHI TIẾT SẢN PHẨM:\n");
        chiTiet.append("───────────────────────────────────────\n\n");
        
        int stt = 1;
        for (ChiTietHoaDon ct : listCT) {
            Giay giay = giayDAO.getById(ct.getIdGiay());
            String tenGiay = (giay != null) ? giay.getTenGiay() : "N/A";
            
            chiTiet.append(stt++).append(". ").append(tenGiay).append("\n");
            chiTiet.append("   Số lượng: ").append(ct.getSoLuong()).append("\n");
            chiTiet.append("   Đơn giá: ").append(df.format(ct.getDonGia())).append(" đ\n");
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
            "Chi tiết hóa đơn: " + idHD, JOptionPane.INFORMATION_MESSAGE);
    }
}