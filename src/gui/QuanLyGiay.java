package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import DAO.DAO_Giay;
import DAO.DAO_LoaiGiay;
import DAO.DAO_HangGiay;
import model.Giay;
import model.LoaiGiay;
import model.HangGiay;
import model.ChiTietPhanQuyen;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class QuanLyGiay extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTen, txtSize, txtSoLuong, txtGiaBan, txtTimKiem;
    private JTextArea txtMoTa;
    
    // SỬA LỖI: Bổ sung khai báo 3 biến này cho Form
    private JComboBox<String> cboLoaiGiay; 
    private JComboBox<String> cboHangGiay;
    private JComboBox<String> cboStatus;
    
    // =================================================================
    // SỬA ĐỔI THEO YÊU CẦU: Dùng 2 cboFilter riêng biệt
    // =================================================================
    private JComboBox<String> cboLocTheoLoai; // Combobox Lọc theo Loại Giày
    private JComboBox<String> cboLocTheoHang; // Combobox Lọc theo Hãng Giày
    // private JComboBox<String> cboFilter; // BỎ
    
    // SỬA ĐỔI: Thêm btnThemLoai
    private JButton btnThem, btnSua, btnLamMoi, btnTimKiem, btnChonAnh, btnThemHang, btnThemLoai; 
    private JLabel lblHinhAnh;
    private DAO.DAO_Giay giayDAO;
    private DAO.DAO_LoaiGiay loaiGiayDAO;
    private DAO.DAO_HangGiay hangGiayDAO;
    private DecimalFormat df = new DecimalFormat("#,###");
    private String selectedImagePath = "";
    private Map<String, String> loaiGiayMap = new HashMap<>();
    private Map<String, String> loaiGiayIdMap = new HashMap<>();
    private Map<String, String> hangGiayMap = new HashMap<>();
    private Map<String, String> hangGiayIdMap = new HashMap<>();
    
    // BỎ: Các biến trạng thái cho cboFilter
    // private boolean isShowingCriteria = true; 
    // private String currentFilterMode = ""; 
    // private ActionListener cboFilterActionListener; 
    
    private ChiTietPhanQuyen permission;
    
    public QuanLyGiay() {
        giayDAO = new DAO_Giay();
        loaiGiayDAO = new DAO_LoaiGiay();
        hangGiayDAO = new DAO_HangGiay();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(236, 240, 241));
        
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadComboBoxData(); // Tải data cho form VÀ khởi tạo bộ lọc
        loadData();         
        generateNextId();
        
        btnThem.setEnabled(true);
        
        txtSoLuong.setToolTipText(
                "<html>Số lượng tự động cập nhật từ:<br>" +
                "- TĂNG: Khi nhập kho (QuanLyNhapKho)<br>" +
                "- GIẢM: Khi bán hàng (QuanLyHoaDon)<br>" +
                "<b>KHÔNG được phép sửa thủ công!</b></html>"
            );
   }
    
    public QuanLyGiay(ChiTietPhanQuyen permission) {
        this(); // Gọi constructor gốc để khởi tạo giao diện
        this.permission = permission;
        applyPermissions(); // Áp dụng quyền
    }
    
    private void applyPermissions() {
        if (permission != null) {
            btnThem.setEnabled(permission.isDuocThem());
            btnSua.setEnabled(permission.isDuocSua());
            
            // Áp dụng quyền cho nút Thêm Hãng
            if (btnThemHang != null) {
                btnThemHang.setEnabled(permission.isDuocThem());
            }
            // SỬA ĐỔI: Áp dụng quyền cho nút Thêm Loại
            if (btnThemLoai != null) {
                btnThemLoai.setEnabled(permission.isDuocThem());
            }
        } else {
            // Nếu không có quyền (lỗi), vô hiệu hóa hết
            btnThem.setEnabled(false);
            btnSua.setEnabled(false);
            
            // Áp dụng quyền cho nút Thêm Hãng
            if (btnThemHang != null) {
                btnThemHang.setEnabled(false);
            }
            // SỬA ĐỔI: Áp dụng quyền cho nút Thêm Loại
            if (btnThemLoai != null) {
                btnThemLoai.setEnabled(false);
            }
        }
    }
    
    // --- Các phương thức createXXXPanel() và createStyledButton() giữ nguyên ---
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        JLabel lblTitle = new JLabel("QUẢN LÝ GIÀY", SwingConstants.CENTER);
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
        mainPanel.setPreferredSize(new Dimension(400, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel lblFormTitle = new JLabel("THÔNG TIN GIÀY");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblFormTitle, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setBackground(Color.WHITE);
        lblHinhAnh = new JLabel("Chưa có hình ảnh", SwingConstants.CENTER);
        lblHinhAnh.setPreferredSize(new Dimension(150, 150));
        lblHinhAnh.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        lblHinhAnh.setBackground(new Color(245, 245, 245));
        lblHinhAnh.setOpaque(true);
        imagePanel.add(lblHinhAnh, BorderLayout.CENTER);
        btnChonAnh = createStyledButton("Chọn ảnh", new Color(52, 152, 219));
        btnChonAnh.setPreferredSize(new Dimension(150, 30));
        btnChonAnh.addActionListener(e -> chonHinhAnh());
        imagePanel.add(btnChonAnh, BorderLayout.SOUTH);
        formPanel.add(imagePanel, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Mã giày:"), gbc);
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        formPanel.add(txtId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tên giày:"), gbc);
        txtTen = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtTen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Size:"), gbc);
        txtSize = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtSize, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Số lượng:"), gbc);
        txtSoLuong = new JTextField(15);
        txtSoLuong.setEditable(false); // VÔ HIỆU HÓA - CHỈ HIỂN THỊ
        txtSoLuong.setBackground(new Color(240, 240, 240));
        txtSoLuong.setToolTipText("Số lượng tự động cập nhật từ phiếu nhập/bán hàng");
        gbc.gridx = 1;
        formPanel.add(txtSoLuong, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Giá bán:"), gbc);
        txtGiaBan = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtGiaBan, gbc);
        
        // --- SỬA ĐỔI: Thêm nút '+' cho Loại Giày ---
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Loại giày:"), gbc);
        
        // SỬA LỖI: Dùng biến thành viên
        cboLoaiGiay = new JComboBox<>(); 
        
        // Gán 'btnThemLoai' cho biến thành viên
        btnThemLoai = new JButton("+");
        btnThemLoai.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemLoai.setPreferredSize(new Dimension(45, (int)cboLoaiGiay.getPreferredSize().getHeight()));
        btnThemLoai.setMargin(new Insets(0, 0, 0, 0));
        btnThemLoai.setToolTipText("Thêm loại giày mới");
        btnThemLoai.addActionListener(e -> themLoaiGiayMoi());

        JPanel loaiPanel = new JPanel(new BorderLayout(5, 0));
        loaiPanel.setBackground(Color.WHITE);
        loaiPanel.add(cboLoaiGiay, BorderLayout.CENTER);
        loaiPanel.add(btnThemLoai, BorderLayout.EAST);
        
        gbc.gridx = 1;
        formPanel.add(loaiPanel, gbc);
        // --- KẾT THÚC SỬA ĐỔI ---
        
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Hãng giày:"), gbc);
        
        // SỬA LỖI: Dùng biến thành viên
        cboHangGiay = new JComboBox<>();
        
        // Gán 'btnThemHang' cho biến thành viên
        btnThemHang = new JButton("+");
        btnThemHang.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemHang.setPreferredSize(new Dimension(45, (int)cboHangGiay.getPreferredSize().getHeight()));
        btnThemHang.setMargin(new Insets(0, 0, 0, 0));
        btnThemHang.setToolTipText("Thêm hãng giày mới");
        btnThemHang.addActionListener(e -> themHangGiayMoi());

        JPanel hangPanel = new JPanel(new BorderLayout(5, 0));
        hangPanel.setBackground(Color.WHITE);
        hangPanel.add(cboHangGiay, BorderLayout.CENTER);
        hangPanel.add(btnThemHang, BorderLayout.EAST);
        
        gbc.gridx = 1;
        formPanel.add(hangPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        
        txtMoTa = new JTextArea(4, 15);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollMoTa, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        gbc.gridx = 0; gbc.gridy = 10;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        
        // SỬA LỖI: Dùng biến thành viên
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
        btnThem = createStyledButton("Tạo", new Color(46, 204, 113));
        btnSua = createStyledButton("Sửa", new Color(52, 152, 219));
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnLamMoi);
        btnThem.addActionListener(e -> themGiay());
        btnSua.addActionListener(e -> suaGiay());
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
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(236, 240, 241));
        
        // --- Thanh Tìm Kiếm ---
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lblSearch);
        txtTimKiem = new JTextField(15);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtTimKiem);
        btnTimKiem = createStyledButton("Tìm", new Color(41, 128, 185));
        btnTimKiem.setPreferredSize(new Dimension(80, 30));
        searchPanel.add(btnTimKiem);
        // Gọi hàm lọc/tìm kiếm GỘP
        btnTimKiem.addActionListener(e -> capNhatDanhSachHienThi(true));
        
        searchPanel.add(Box.createHorizontalStrut(20));
        
        // =================================================================
        // SỬA ĐỔI: Dùng 2 cboFilter riêng biệt
        // =================================================================
        JLabel lblLocLoai = new JLabel("Lọc theo loại:");
        lblLocLoai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lblLocLoai);
        
        cboLocTheoLoai = new JComboBox<>();
        cboLocTheoLoai.setPreferredSize(new Dimension(150, 30));
        cboLocTheoLoai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(cboLocTheoLoai);
        
        searchPanel.add(Box.createHorizontalStrut(10)); 
        
        JLabel lblLocHang = new JLabel("Lọc theo hãng:");
        lblLocHang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lblLocHang);
        
        cboLocTheoHang = new JComboBox<>();
        cboLocTheoHang.setPreferredSize(new Dimension(150, 30));
        cboLocTheoHang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(cboLocTheoHang);

        // --- Thêm sự kiện ---
        cboLocTheoLoai.addActionListener(e -> onFilterLoaiSelect());
        cboLocTheoHang.addActionListener(e -> onFilterHangSelect());
        // KẾT THÚC SỬA ĐỔI
        
        // Gọi hàm lọc/tìm kiếm GỘP
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    capNhatDanhSachHienThi(true); // Enter: có thông báo
                } else {
                    capNhatDanhSachHienThi(false); // Gõ: không thông báo
                }
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // --- Bảng Dữ Liệu ---
        String[] columns = {"Hình ảnh", "Mã giày", "Tên giày", "Size", "Số lượng", "Giá bán", 
                            "Loại giày", "Hãng giày", "Mô tả", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return ImageIcon.class;
                return String.class;
            }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(80); 
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(0).setMaxWidth(100);
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
    
    private ImageIcon loadImageIcon(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return createPlaceholderIcon();
        }
        File imgFile = new File(imagePath);
        if (!imgFile.exists()) {
            imgFile = new File("image/" + imagePath);
            if (!imgFile.exists()) {
                return createPlaceholderIcon();
            }
        }
        try {
            ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return createPlaceholderIcon();
        }
    }
    
    private ImageIcon createPlaceholderIcon() {
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
            70, 70, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(0, 0, 70, 70);
        g2d.setColor(Color.WHITE);
        g2d.drawString("No Image", 10, 35);
        g2d.dispose();
        return new ImageIcon(img);
    }
    
    private void generateNextId() {
        List<Giay> list = giayDAO.getAll();
        int maxId = 0;
        for (Giay g : list) {
            String id = g.getIdGiay();
            if (id.startsWith("G")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        txtId.setText("G" + String.format("%03d", maxId + 1));
    }
    
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Giay> list = giayDAO.getAll();

        for (Giay g : list) {
            String tenLoai = loaiGiayMap.getOrDefault(g.getIdLoaiGiay(), g.getIdLoaiGiay());
            String tenHang = hangGiayMap.getOrDefault(g.getIdHangGiay(), g.getIdHangGiay());

            ImageIcon imageIcon = loadImageIcon(g.getHinhAnh());

            tableModel.addRow(new Object[]{
                imageIcon,         
                g.getIdGiay(),     
                g.getTenGiay(),    
                g.getSize(),       
                g.getSoLuong(),    
                df.format(g.getGiaBan()) + " đ", 
                tenLoai,           
                tenHang,           
                g.getMoTa(),       
                g.getStatus()      
            });
        }
    }
    
    private void loadComboBoxData() {
        // --- Xóa dữ liệu cũ (cho Form) ---
        cboLoaiGiay.removeAllItems(); 
        cboHangGiay.removeAllItems(); 
        
        // SỬA ĐỔI: Xóa dữ liệu cũ (cho Filter)
        if(cboLocTheoLoai != null) cboLocTheoLoai.removeAllItems();
        if(cboLocTheoHang != null) cboLocTheoHang.removeAllItems();
        
        loaiGiayMap.clear();
        loaiGiayIdMap.clear();
        hangGiayMap.clear();
        hangGiayIdMap.clear();

        // SỬA ĐỔI: Thêm mục "Tất cả" cho filter
        if(cboLocTheoLoai != null) cboLocTheoLoai.addItem("Tất cả");
        if(cboLocTheoHang != null) cboLocTheoHang.addItem("Tất cả");

        // --- Load Loại Giày ---
        List<LoaiGiay> listLoai = loaiGiayDAO.getAll();
        for (LoaiGiay lg : listLoai) {
            String tenLoai = lg.getTenLoaiGiay();
            cboLoaiGiay.addItem(tenLoai); // Add to Form
            if(cboLocTheoLoai != null) cboLocTheoLoai.addItem(tenLoai); // SỬA ĐỔI: Add to Filter
            loaiGiayMap.put(lg.getIdLoaiGiay(), tenLoai);
            loaiGiayIdMap.put(tenLoai, lg.getIdLoaiGiay());
        }
        
        // --- Load Hãng Giày ---
        List<HangGiay> listHang = hangGiayDAO.getAll();
        for (HangGiay hg : listHang) {
            String tenHang = hg.getTenHangGiay();
            cboHangGiay.addItem(tenHang); // Add to Form
            if(cboLocTheoHang != null) cboLocTheoHang.addItem(tenHang); // SỬA ĐỔI: Add to Filter
            hangGiayMap.put(hg.getIdHangGiay(), tenHang);
            hangGiayIdMap.put(tenHang, hg.getIdHangGiay());
        }
        
        // SỬA ĐỔI: Bỏ logic cboFilter
        // if (cboFilter != null) {
        //     loadFilterOptions("CRITERIA");
        // }
    }
    
    // =================================================================
    // BỎ: HÀM Tải lại nội dung cho cboFilter
    // =================================================================
    // private void loadFilterOptions(String mode) { ... }
    
    // =================================================================
    // BỎ: HÀM Xử lý sự kiện khi chọn cboFilter
    // =================================================================
    // private void onFilterSelect() { ... }
    
    // =================================================================
    // HÀM MỚI: Xử lý sự kiện khi chọn Lọc Loại
    // =================================================================
    private void onFilterLoaiSelect() {
        // Kiểm tra null
        if (cboLocTheoLoai == null || cboLocTheoHang == null || cboLocTheoLoai.getSelectedItem() == null) {
            return;
        }
        
        String selectedLoai = cboLocTheoLoai.getSelectedItem().toString();
        
        // Nếu chọn một loại cụ thể
        if (!selectedLoai.equals("Tất cả")) {
            // Reset cboLocTheoHang
            cboLocTheoHang.setSelectedItem("Tất cả");
        }
        
        // Cập nhật lại bảng
        capNhatDanhSachHienThi(false);
    }
    
    // =================================================================
    // HÀM MỚI: Xử lý sự kiện khi chọn Lọc Hãng
    // =================================================================
    private void onFilterHangSelect() {
         // Kiểm tra null
        if (cboLocTheoLoai == null || cboLocTheoHang == null || cboLocTheoHang.getSelectedItem() == null) {
            return;
        }
        
        String selectedHang = cboLocTheoHang.getSelectedItem().toString();
        
        // Nếu chọn một hãng cụ thể
        if (!selectedHang.equals("Tất cả")) {
            // Reset cboLocTheoLoai
            cboLocTheoLoai.setSelectedItem("Tất cả");
        }
        
        // Cập nhật lại bảng
        capNhatDanhSachHienThi(false);
    }
    
    private void chonHinhAnh() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Hình ảnh", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            ImageIcon imageIcon = new ImageIcon(selectedImagePath);
            Image image = imageIcon.getImage().getScaledInstance(
                lblHinhAnh.getWidth(), lblHinhAnh.getHeight(), Image.SCALE_SMOOTH);
            lblHinhAnh.setIcon(new ImageIcon(image));
            lblHinhAnh.setText("");
        }
    }
    
    private void themGiay() {
        if (!validateInput()) return;
        
        try {
            // Kiểm tra trùng tên và size
            String ten = txtTen.getText().trim();
            float size = Float.parseFloat(txtSize.getText().trim());
            
            List<Giay> allGiay = giayDAO.getAll();
            boolean exists = false;
            for (Giay giay : allGiay) {
                if (giay.getTenGiay().equalsIgnoreCase(ten) && giay.getSize() == size) {
                    exists = true;
                    break;
                }
            }
            
            if (exists) {
                JOptionPane.showMessageDialog(this, 
                    "Sản phẩm với tên và size này đã tồn tại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Giay g = new Giay();
            g.setIdGiay(txtId.getText().trim());
            g.setTenGiay(ten);
            g.setSize(size);
            
            // *** QUAN TRỌNG: Số lượng ban đầu = 0 ***
            g.setSoLuong(0);
            
            g.setGiaBan(Float.parseFloat(txtGiaBan.getText().trim()));
            g.setMoTa(txtMoTa.getText().trim());
            g.setHinhAnh(selectedImagePath);
            
            String tenLoaiGiay = cboLoaiGiay.getSelectedItem().toString();
            String idLoaiGiay = loaiGiayIdMap.get(tenLoaiGiay);
            if (idLoaiGiay == null) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy ID loại giày tương ứng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            g.setIdLoaiGiay(idLoaiGiay);
            
            String tenHangGiay = cboHangGiay.getSelectedItem().toString();
            String idHangGiay = hangGiayIdMap.get(tenHangGiay);
            if (idHangGiay == null) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy ID hãng giày tương ứng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            g.setIdHangGiay(idHangGiay);
            
            g.setStatus(cboStatus.getSelectedItem().toString());
            
            if (giayDAO.insert(g)) {
                JOptionPane.showMessageDialog(this, 
                    "<html>Thêm giày thành công!<br><br>" +
                    "<b>Lưu ý:</b> Số lượng hiện tại = 0<br>" +
                    "Vui lòng vào <b>Quản lý nhập kho</b> để nhập hàng!</html>", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm giày thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void suaGiay() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm cần sửa từ bảng!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            String currentId = txtId.getText().trim();
            String ten = txtTen.getText().trim();
            float size = Float.parseFloat(txtSize.getText().trim());
            
            // Kiểm tra trùng khi SỬA (với sản phẩm khác)
            List<Giay> allGiay = giayDAO.getAll();
            boolean exists = false;
            for (Giay giay : allGiay) {
                if (!giay.getIdGiay().equals(currentId) && 
                    giay.getTenGiay().equalsIgnoreCase(ten) && 
                    giay.getSize() == size) {
                    exists = true;
                    break;
                }
            }
            
            if (exists) {
                JOptionPane.showMessageDialog(this, 
                    "Một sản phẩm khác với tên và size này đã tồn tại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Lấy thông tin giày hiện tại từ DB
            Giay giayCu = giayDAO.getById(currentId);
            if (giayCu == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Giay g = new Giay();
            g.setIdGiay(currentId);
            g.setTenGiay(ten);
            g.setSize(size);
            
            // *** QUAN TRỌNG: GIỮ NGUYÊN số lượng cũ, KHÔNG cho phép sửa ***
            g.setSoLuong(giayCu.getSoLuong());
            
            g.setGiaBan(Float.parseFloat(txtGiaBan.getText().trim()));
            g.setMoTa(txtMoTa.getText().trim());
            g.setHinhAnh(selectedImagePath);
            
            String tenLoaiGiay = cboLoaiGiay.getSelectedItem().toString();
            String idLoaiGiay = loaiGiayIdMap.get(tenLoaiGiay);
            if (idLoaiGiay == null) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy ID loại giày tương ứng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            g.setIdLoaiGiay(idLoaiGiay);
            
            String tenHangGiay = cboHangGiay.getSelectedItem().toString();
            String idHangGiay = hangGiayIdMap.get(tenHangGiay);
            if (idHangGiay == null) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy ID hãng giày tương ứng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            g.setIdHangGiay(idHangGiay);
            
            g.setStatus(cboStatus.getSelectedItem().toString());
            
            if (giayDAO.update(g)) {
                JOptionPane.showMessageDialog(this, 
                    "<html>Cập nhật giày thành công!<br><br>" +
                    "<b>Lưu ý:</b> Số lượng không thay đổi (vẫn = " + 
                    giayCu.getSoLuong() + ")<br>" +
                    "Số lượng chỉ thay đổi qua Nhập kho/Bán hàng</html>", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật giày thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // =================================================================
    // HÀM MỚI: Thêm loại giày
    // =================================================================
    private void themLoaiGiayMoi() {
        String tenLoaiMoi = JOptionPane.showInputDialog(this, 
                "Nhập tên loại giày mới:", 
                "Thêm loại giày", 
                JOptionPane.PLAIN_MESSAGE);
        
        if (tenLoaiMoi == null || tenLoaiMoi.trim().isEmpty()) {
            return; // Người dùng hủy
        }
        
        tenLoaiMoi = tenLoaiMoi.trim();
        
        // 1. Kiểm tra xem tên loại đã tồn tại chưa
        boolean daTonTai = false;
        for (String ten : loaiGiayIdMap.keySet()) {
            if (ten.equalsIgnoreCase(tenLoaiMoi)) {
                daTonTai = true;
                break;
            }
        }
        
        if (daTonTai) {
            JOptionPane.showMessageDialog(this, "Loại giày này đã tồn tại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Tạo ID mới cho loại
        List<LoaiGiay> listLoai = loaiGiayDAO.getAll();
        int maxId = 0;
        for (LoaiGiay lg : listLoai) {
            String id = lg.getIdLoaiGiay();
            // Giả sử ID loại giày bắt đầu bằng 'L'
            if (id.startsWith("L")) { 
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        String newId = "L" + String.format("%03d", maxId + 1);
        
        // 3. Thêm vào database
        LoaiGiay newLoai = new LoaiGiay();
        newLoai.setIdLoaiGiay(newId);
        newLoai.setTenLoaiGiay(tenLoaiMoi);
        
        try {
            if (loaiGiayDAO.insert(newLoai)) {
                JOptionPane.showMessageDialog(this, "Thêm loại '" + tenLoaiMoi + "' thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // 4. Tải lại dữ liệu combobox (cả form và bộ lọc)
                loadComboBoxData();
                
                // 5. Tự động chọn loại vừa thêm
                cboLoaiGiay.setSelectedItem(tenLoaiMoi);
            } else {
                JOptionPane.showMessageDialog(this, "Thêm loại thất bại!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Lỗi khi thêm loại: " + ex.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // HÀM MỚI: Thêm hãng giày
    private void themHangGiayMoi() {
        String tenHangMoi = JOptionPane.showInputDialog(this, 
                "Nhập tên hãng giày mới:", 
                "Thêm hãng giày", 
                JOptionPane.PLAIN_MESSAGE);
        
        if (tenHangMoi == null || tenHangMoi.trim().isEmpty()) {
            return; // Người dùng hủy
        }
        
        tenHangMoi = tenHangMoi.trim();
        
        // 1. Kiểm tra xem tên hãng đã tồn tại chưa
        boolean daTonTai = false;
        for (String ten : hangGiayIdMap.keySet()) {
            if (ten.equalsIgnoreCase(tenHangMoi)) {
                daTonTai = true;
                break;
            }
        }
        
        if (daTonTai) {
            JOptionPane.showMessageDialog(this, "Hãng giày này đã tồn tại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 2. Tạo ID mới cho hãng
        List<HangGiay> listHang = hangGiayDAO.getAll();
        int maxId = 0;
        for (HangGiay hg : listHang) {
            String id = hg.getIdHangGiay();
            if (id.startsWith("H")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        String newId = "H" + String.format("%03d", maxId + 1);
        
        // 3. Thêm vào database
        HangGiay newHang = new HangGiay();
        newHang.setIdHangGiay(newId);
        newHang.setTenHangGiay(tenHangMoi);
        
        try {
            if (hangGiayDAO.insert(newHang)) {
                JOptionPane.showMessageDialog(this, "Thêm hãng '" + tenHangMoi + "' thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // 4. Tải lại dữ liệu combobox (cả form và bộ lọc)
                loadComboBoxData();
                
                // 5. Tự động chọn hãng vừa thêm
                cboHangGiay.setSelectedItem(tenHangMoi);
            } else {
                JOptionPane.showMessageDialog(this, "Thêm hãng thất bại!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Lỗi khi thêm hãng: " + ex.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // =================================================================
    // SỬA ĐỔI: HÀM LỌC GỘP (Đọc từ 2 cboFilter riêng biệt)
    // =================================================================
    private void capNhatDanhSachHienThi(boolean showMessage) {
        String keyword = txtTimKiem.getText().trim().toLowerCase();

        // SỬA ĐỔI: Lấy giá trị lọc từ 2 combobox mới
        String idLoai = "Tất cả";
        String idHang = "Tất cả";
        
        // Kiểm tra null
        if (cboLocTheoLoai == null || cboLocTheoHang == null || 
            cboLocTheoLoai.getSelectedItem() == null || cboLocTheoHang.getSelectedItem() == null) {
            loadData(); // Tải dữ liệu mặc định nếu combobox chưa sẵn sàng
            return;
        }
        
        String selectedLoai = cboLocTheoLoai.getSelectedItem().toString();
        if (!selectedLoai.equals("Tất cả")) {
            idLoai = loaiGiayIdMap.get(selectedLoai); // Lấy ID
            if (idLoai == null) idLoai = "Tất cả"; // An toàn
        }
        
        String selectedHang = cboLocTheoHang.getSelectedItem().toString();
        if (!selectedHang.equals("Tất cả")) {
            idHang = hangGiayIdMap.get(selectedHang); // Lấy ID
            if (idHang == null) idHang = "Tất cả"; // An toàn
        }

        tableModel.setRowCount(0);
        List<Giay> list = giayDAO.getAll();
        int count = 0;

        for (Giay g : list) {
            // --- Kiểm tra điều kiện lọc ---
            
            // 1. Lọc theo Loại
            boolean locLoaiPhuHop = idLoai.equals("Tất cả") || g.getIdLoaiGiay().equals(idLoai);
            
            // 2. Lọc theo Hãng
            boolean locHangPhuHop = idHang.equals("Tất cả") || g.getIdHangGiay().equals(idHang);
            
            // 3. Lọc theo Tìm kiếm (Keyword)
            boolean timKiemPhuHop = false;
            if (keyword.isEmpty()) {
                timKiemPhuHop = true;
            } else {
                String id = g.getIdGiay().toLowerCase();
                String ten = g.getTenGiay().toLowerCase();
                String tenLoaiDB = loaiGiayMap.getOrDefault(g.getIdLoaiGiay(), g.getIdLoaiGiay()).toLowerCase();
                String tenHangDB = hangGiayMap.getOrDefault(g.getIdHangGiay(), g.getIdHangGiay()).toLowerCase();

                if (id.contains(keyword) || ten.contains(keyword) || 
                    tenLoaiDB.contains(keyword) || tenHangDB.contains(keyword)) {
                    timKiemPhuHop = true;
                }
            }

            // --- Thêm vào bảng nếu thỏa mãn TẤT CẢ ---
            if (locLoaiPhuHop && locHangPhuHop && timKiemPhuHop) {
                String tenLoai = loaiGiayMap.getOrDefault(g.getIdLoaiGiay(), g.getIdLoaiGiay());
                String tenHang = hangGiayMap.getOrDefault(g.getIdHangGiay(), g.getIdHangGiay());
                
                ImageIcon imageIcon = loadImageIcon(g.getHinhAnh());
                
                tableModel.addRow(new Object[]{
                    imageIcon,         
                    g.getIdGiay(),
                    g.getTenGiay(),
                    g.getSize(),
                    g.getSoLuong(),
                    df.format(g.getGiaBan()) + " đ",
                    tenLoai,
                    tenHang,
                    g.getMoTa(),
                    g.getStatus()
                });
                count++;
            }
        }

        // CHỈ hiện thông báo khi showMessage = true (nhấn Enter hoặc nút Tìm)
        // và không tìm thấy kết quả nào
        if (count == 0 && showMessage) {
            if (!keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy giày nào khớp với từ khóa: " + keyword,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy sản phẩm nào khớp với bộ lọc.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void hienThiThongTin(int row) {
        txtId.setText(table.getValueAt(row, 1).toString()); 
        txtTen.setText(table.getValueAt(row, 2).toString()); 
        txtSize.setText(table.getValueAt(row, 3).toString()); 
        txtSoLuong.setText(table.getValueAt(row, 4).toString()); 

        String giaBan = table.getValueAt(row, 5).toString(); 
        giaBan = giaBan.replace(",", "").replace(" đ", "");
        txtGiaBan.setText(giaBan);

        txtMoTa.setText(table.getValueAt(row, 8).toString()); 
        cboStatus.setSelectedItem(table.getValueAt(row, 9).toString()); 

        Giay g = giayDAO.getById(txtId.getText());
        if (g != null) {
            // Set combobox loại giày theo Tên loại
            String tenLoai = loaiGiayMap.getOrDefault(g.getIdLoaiGiay(), g.getIdLoaiGiay());
            cboLoaiGiay.setSelectedItem(tenLoai);

            // Set combobox hãng giày theo Tên hãng
            String tenHang = hangGiayMap.getOrDefault(g.getIdHangGiay(), g.getIdHangGiay());
            cboHangGiay.setSelectedItem(tenHang);

            // Hiển thị hình ảnh
            selectedImagePath = g.getHinhAnh();
            if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                File imgFile = new File(selectedImagePath);
                if (imgFile.exists()) {
                    ImageIcon imageIcon = new ImageIcon(selectedImagePath);
                    Image image = imageIcon.getImage().getScaledInstance(
                        lblHinhAnh.getWidth(), lblHinhAnh.getHeight(), Image.SCALE_SMOOTH);
                    lblHinhAnh.setIcon(new ImageIcon(image));
                    lblHinhAnh.setText("");
                } else {
                    lblHinhAnh.setIcon(null);
                    lblHinhAnh.setText("Không tìm thấy ảnh");
                }
            } else {
                lblHinhAnh.setIcon(null);
                lblHinhAnh.setText("Chưa có hình ảnh");
            }
        }
        
        btnThem.setEnabled(false);
        if (permission != null) {
            btnSua.setEnabled(permission.isDuocSua());
        }
    }
    
    private void lamMoi() {
        generateNextId(); 
        txtTen.setText("");
        txtSize.setText("");
        txtSoLuong.setText("");
        txtGiaBan.setText("");
        txtMoTa.setText("");
        txtTimKiem.setText("");
        cboStatus.setSelectedIndex(0);
        
        // SỬA ĐỔI: Reset 2 combobox lọc về "Tất cả"
        if (cboLocTheoLoai != null) {
            cboLocTheoLoai.setSelectedItem("Tất cả");
        }
        if (cboLocTheoHang != null) {
            cboLocTheoHang.setSelectedItem("Tất cả");
        }
        
        // Vì đã reset bộ lọc, ta phải gọi loadData() để tải lại tất cả
        loadData(); 
        
        if (cboLoaiGiay.getItemCount() > 0) cboLoaiGiay.setSelectedIndex(0);
        if (cboHangGiay.getItemCount() > 0) cboHangGiay.setSelectedIndex(0);
        table.clearSelection();
        
        selectedImagePath = "";
        lblHinhAnh.setIcon(null);
        lblHinhAnh.setText("Chưa có hình ảnh");
        
        applyPermissions();
        
        txtTen.requestFocus();
    }
    
    private boolean validateInput() {
        // ... (Giữ nguyên phần kiểm tra hợp lệ)
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã giày không được để trống!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên giày!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }
        
        try {
            float size = Float.parseFloat(txtSize.getText().trim());
            if (size <= 0) {
                JOptionPane.showMessageDialog(this, "Size phải lớn hơn 0!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Size phải là số!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSize.requestFocus();
            return false;
        }
        
        try {
            float giaBan = Float.parseFloat(txtGiaBan.getText().trim());
            if (giaBan <= 0) {
                JOptionPane.showMessageDialog(this, "Giá bán phải lớn hơn 0!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá bán phải là số!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtGiaBan.requestFocus();
            return false;
        }
        
        if (cboLoaiGiay.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại giày!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (cboHangGiay.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hãng giày!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
}