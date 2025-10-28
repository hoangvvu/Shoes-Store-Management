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
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class QuanLyGiay extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTen, txtSize, txtSoLuong, txtGiaBan, txtTimKiem;
    private JTextArea txtMoTa;
    private JComboBox<String> cboLoaiGiay, cboHangGiay, cboStatus, cboLocHang;
    private JButton btnThem, btnSua, btnLamMoi, btnTimKiem, btnChonAnh;
    private JLabel lblHinhAnh;
    private DAO.DAO_Giay giayDAO;
    private DAO.DAO_LoaiGiay loaiGiayDAO;
    private DAO.DAO_HangGiay hangGiayDAO;
    private DecimalFormat df = new DecimalFormat("#,###");
    private String selectedImagePath = "";
    
    // Khai báo lại Map để hỗ trợ việc hiển thị chỉ tên
    private Map<String, String> loaiGiayMap = new HashMap<>(); // ID -> Tên
    private Map<String, String> loaiGiayIdMap = new HashMap<>(); // Tên -> ID (MỚI)
    private Map<String, String> hangGiayMap = new HashMap<>(); // ID -> Tên
    private Map<String, String> hangGiayIdMap = new HashMap<>(); // Tên -> ID
    
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
        
        loadComboBoxData(); 
        loadData();         
        generateNextId();
        
        // <<< YÊU CẦU 2 (Thiết lập trạng thái ban đầu)
        // Khi mới vào, đang ở chế độ "Thêm"
        btnThem.setEnabled(true);
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
        btnBack.setForeground(Color.RED);
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
        gbc.gridx = 1;
        formPanel.add(txtSoLuong, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Giá bán:"), gbc);
        txtGiaBan = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtGiaBan, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Loại giày:"), gbc);
        cboLoaiGiay = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cboLoaiGiay, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Hãng giày:"), gbc);
        cboHangGiay = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cboHangGiay, gbc);
        
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
        cboStatus = new JComboBox<>(new String[]{"active", "inactive"});
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
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lblSearch);
        txtTimKiem = new JTextField(15);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtTimKiem);
        btnTimKiem = createStyledButton("Tìm", new Color(41, 128, 185));
        btnTimKiem.setPreferredSize(new Dimension(80, 30));
        searchPanel.add(btnTimKiem);
        btnTimKiem.addActionListener(e -> timKiem());
        searchPanel.add(Box.createHorizontalStrut(20));
        JLabel lblLocHang = new JLabel("Lọc hãng:");
        lblLocHang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(lblLocHang);
        cboLocHang = new JComboBox<>();
        cboLocHang.setPreferredSize(new Dimension(150, 30));
        cboLocHang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboLocHang.addActionListener(e -> locTheoHang());
        searchPanel.add(cboLocHang);
        txtTimKiem.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (txtTimKiem.getText().trim().isEmpty()) {
                    loadData();
                }
            }
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiem();
                }
            }
        });
        panel.add(searchPanel, BorderLayout.NORTH);
        String[] columns = {"Hình ảnh", "Mã", "Tên giày", "Size", "Số lượng", "Giá bán", 
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
        table.getTableHeader().setForeground(Color.BLACK);
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
        // SỬA: Load loại giày (Chỉ hiển thị TÊN)
        cboLoaiGiay.removeAllItems();
        loaiGiayMap.clear();
        loaiGiayIdMap.clear(); // Khởi tạo Map Tên -> ID
        List<LoaiGiay> listLoai = loaiGiayDAO.getAll();
        for (LoaiGiay lg : listLoai) {
            cboLoaiGiay.addItem(lg.getTenLoaiGiay()); // CHỈ THÊM TÊN
            loaiGiayMap.put(lg.getIdLoaiGiay(), lg.getTenLoaiGiay());
            loaiGiayIdMap.put(lg.getTenLoaiGiay(), lg.getIdLoaiGiay()); // LƯU MAPPING TÊN -> ID
        }
        
        // SỬA: Load hãng giày (Chỉ hiển thị TÊN)
        cboHangGiay.removeAllItems();
        cboLocHang.removeAllItems();
        cboLocHang.addItem("Tất cả");
        hangGiayMap.clear(); 
        hangGiayIdMap.clear(); 
        
        List<HangGiay> listHang = hangGiayDAO.getAll();
        for (HangGiay hg : listHang) {
            cboHangGiay.addItem(hg.getTenHangGiay()); 
            cboLocHang.addItem(hg.getTenHangGiay());   
            
            hangGiayMap.put(hg.getIdHangGiay(), hg.getTenHangGiay());
            hangGiayIdMap.put(hg.getTenHangGiay(), hg.getIdHangGiay()); 
        }
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
            // <<< YÊU CẦU 3: Kiểm tra trùng tên và size
            String ten = txtTen.getText().trim();
            float size = Float.parseFloat(txtSize.getText().trim()); // Đã validate nên an toàn
            
            List<Giay> allGiay = giayDAO.getAll(); // Lấy tất cả giày
            boolean exists = false;
            for (Giay giay : allGiay) {
                // So sánh không phân biệt hoa thường
                if (giay.getTenGiay().equalsIgnoreCase(ten) && giay.getSize() == size) {
                    exists = true;
                    break;
                }
            }
            
            if (exists) {
                JOptionPane.showMessageDialog(this, "Sản phẩm với tên và size này đã tồn tại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return; // Dừng không thêm nữa
            }
            // --- Kết thúc YÊU CẦU 3 ---
            
            Giay g = new Giay();
            g.setIdGiay(txtId.getText().trim());
            g.setTenGiay(ten); // Dùng biến đã lấy
            g.setSize(size);   // Dùng biến đã lấy
            g.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
            g.setGiaBan(Float.parseFloat(txtGiaBan.getText().trim()));
            g.setMoTa(txtMoTa.getText().trim());
            g.setHinhAnh(selectedImagePath);
            
            // SỬA: Lấy ID Loại giày từ TÊN được chọn
            String tenLoaiGiay = cboLoaiGiay.getSelectedItem().toString();
            String idLoaiGiay = loaiGiayIdMap.get(tenLoaiGiay);
            if (idLoaiGiay == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ID loại giày tương ứng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            g.setIdLoaiGiay(idLoaiGiay);
            
            // Lấy ID Hãng giày từ TÊN được chọn
            String tenHangGiay = cboHangGiay.getSelectedItem().toString();
            String idHangGiay = hangGiayIdMap.get(tenHangGiay);
            if (idHangGiay == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ID hãng giày tương ứng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            g.setIdHangGiay(idHangGiay);
            
            g.setStatus(cboStatus.getSelectedItem().toString());
            
            if (giayDAO.insert(g)) {
                JOptionPane.showMessageDialog(this, "Thêm giày thành công!", 
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
        // <<< YÊU CẦU 1: Kiểm tra xem đã chọn sản phẩm chưa
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa từ bảng!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // --- Kết thúc YÊU CẦU 1 ---
        
        if (!validateInput()) return;
        
        try {
            String currentId = txtId.getText().trim();
            String ten = txtTen.getText().trim();
            float size = Float.parseFloat(txtSize.getText().trim()); // Đã validate
            
            // <<< YÊU CẦU 3 (Mở rộng): Kiểm tra trùng khi SỬA
            // (Đảm bảo tên+size mới không trùng với một sản phẩm *khác*)
            List<Giay> allGiay = giayDAO.getAll();
            boolean exists = false;
            for (Giay giay : allGiay) {
                // Nếu tìm thấy giày khác có cùng tên và size
                if (!giay.getIdGiay().equals(currentId) && 
                    giay.getTenGiay().equalsIgnoreCase(ten) && 
                    giay.getSize() == size) {
                    exists = true;
                    break;
                }
            }
            
            if (exists) {
                JOptionPane.showMessageDialog(this, "Một sản phẩm khác với tên và size này đã tồn tại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // --- Kết thúc YÊU CẦU 3 (Mở rộng) ---
            
            Giay g = new Giay();
            g.setIdGiay(currentId);
            g.setTenGiay(ten);
            g.setSize(size);
            g.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
            g.setGiaBan(Float.parseFloat(txtGiaBan.getText().trim()));
            g.setMoTa(txtMoTa.getText().trim());
            g.setHinhAnh(selectedImagePath);
            
            // SỬA: Lấy ID Loại giày từ TÊN được chọn
            String tenLoaiGiay = cboLoaiGiay.getSelectedItem().toString();
            String idLoaiGiay = loaiGiayIdMap.get(tenLoaiGiay);
            if (idLoaiGiay == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ID loại giày tương ứng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            g.setIdLoaiGiay(idLoaiGiay);
            
            // Lấy ID Hãng giày từ TÊN được chọn
            String tenHangGiay = cboHangGiay.getSelectedItem().toString();
            String idHangGiay = hangGiayIdMap.get(tenHangGiay);
            if (idHangGiay == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ID hãng giày tương ứng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            g.setIdHangGiay(idHangGiay);
            
            g.setStatus(cboStatus.getSelectedItem().toString());
            
            if (giayDAO.update(g)) {
                JOptionPane.showMessageDialog(this, "Cập nhật giày thành công!", 
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
    
    private void timKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        tableModel.setRowCount(0);
        List<Giay> list = giayDAO.getAll();

        for (Giay g : list) {
            String id = g.getIdGiay().toLowerCase();
            String ten = g.getTenGiay().toLowerCase();
            String tenLoai = loaiGiayMap.getOrDefault(g.getIdLoaiGiay(), g.getIdLoaiGiay());
            String tenHang = hangGiayMap.getOrDefault(g.getIdHangGiay(), g.getIdHangGiay());

            if (id.contains(keyword) || ten.contains(keyword) || tenLoai.toLowerCase().contains(keyword) || tenHang.toLowerCase().contains(keyword)) {
                
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

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy giày nào!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void locTheoHang() {
        String selected = (String) cboLocHang.getSelectedItem();
        
        if (selected == null || selected.equals("Tất cả")) {
            loadData();
            return;
        }
        
        String idHang = hangGiayIdMap.get(selected); 
        
        if (idHang == null) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Giay> list = giayDAO.getAll();
        
        for (Giay g : list) {
            if (g.getIdHangGiay().equals(idHang)) {
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
            // SỬA: Set combobox loại giày theo Tên loại
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
        
        // <<< YÊU CẦU 2: Khi click vào sản phẩm (để sửa), vô hiệu hóa nút "Thêm"
        btnThem.setEnabled(false);
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
        cboLocHang.setSelectedIndex(0);
        if (cboLoaiGiay.getItemCount() > 0) cboLoaiGiay.setSelectedIndex(0);
        if (cboHangGiay.getItemCount() > 0) cboHangGiay.setSelectedIndex(0);
        table.clearSelection();
        
        selectedImagePath = "";
        lblHinhAnh.setIcon(null);
        lblHinhAnh.setText("Chưa có hình ảnh");
        
        // <<< YÊU CẦU 2 (Reset): Khi làm mới, kích hoạt lại nút "Thêm"
        btnThem.setEnabled(true);
        
        txtTen.requestFocus();
        loadData();
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
            int soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng không được âm!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtSoLuong.requestFocus();
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