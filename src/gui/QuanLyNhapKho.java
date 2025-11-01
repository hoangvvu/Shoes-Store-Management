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

public class QuanLyNhapKho extends JPanel {
    private JTable tableNhapKho, tableChiTiet, tableSanPham;
    private DefaultTableModel modelNhapKho, modelChiTiet, modelSanPham;
    private JTextField txtIdNK, txtIdNV, txtIdNCC, txtTenNCC, txtTimKiem, txtSoLuong;
    private JLabel lblTongTien;
    private JButton btnTaoMoi, btnThemSP, btnXoaSP, btnLuuNK, btnHuyNK, btnTraCuuNCC, btnLamMoi, btnTimKiem, btnXacNhan;
    
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
        
        loadDanhSachNhapKho();
        loadDanhSachSanPham();
    }
    
    public QuanLyNhapKho(NhanVien user) {
        this();
        this.currentUser = user;
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
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
        
        JPanel leftPanel = createNhapKhoPanel();
        leftPanel.setPreferredSize(new Dimension(450, 0));
        
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
        headerPanel.setBackground(new Color(231, 76, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblHeader = new JLabel("TẠO PHIẾU NHẬP KHO");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.WEST);
        
        btnTaoMoi = createStyledButton("Tạo mới", new Color(46, 204, 113));
        btnTaoMoi.setPreferredSize(new Dimension(100, 30));
        btnTaoMoi.addActionListener(e -> taoPhieuNhapMoi());
        headerPanel.add(btnTaoMoi, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin phiếu nhập"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mã phiếu:"), gbc);
        txtIdNK = new JTextField(10);
        txtIdNK.setEditable(false);
        txtIdNK.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtIdNK, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mã NV:"), gbc);
        txtIdNV = new JTextField(10);
        txtIdNV.setEditable(false);
        txtIdNV.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        panel.add(txtIdNV, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Mã NCC:"), gbc);
        
        JPanel nccPanel = new JPanel(new BorderLayout(5, 0));
        nccPanel.setBackground(Color.WHITE);
        txtIdNCC = new JTextField(10);
        btnTraCuuNCC = createStyledButton("Tra cứu", new Color(52, 152, 219));
        btnTraCuuNCC.setPreferredSize(new Dimension(80, 25));
        btnTraCuuNCC.addActionListener(e -> traCuuNhaCungCap());
        nccPanel.add(txtIdNCC, BorderLayout.CENTER);
        nccPanel.add(btnTraCuuNCC, BorderLayout.EAST);
        gbc.gridx = 1;
        panel.add(nccPanel, gbc);
        
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
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết nhập kho"));
        
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
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnXoaSP = createStyledButton("Xóa SP", new Color(231, 76, 60));
        btnXoaSP.addActionListener(e -> xoaSanPhamKhoiPhieu());
        btnPanel.add(btnXoaSP);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSanPhamPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm có sẵn"));
        
        String[] columns = {"Mã", "Tên giày", "Size", "Tồn kho hiện tại"};
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
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(new JLabel("Số lượng nhập:"));
        txtSoLuong = new JTextField(5);
        txtSoLuong.setText("1");
        btnPanel.add(txtSoLuong);
        
        btnPanel.add(new JLabel("Giá nhập:"));
        JTextField txtGiaNhap = new JTextField(8);
        btnPanel.add(txtGiaNhap);
        
        btnThemSP = createStyledButton("Thêm vào phiếu", new Color(46, 204, 113));
        btnThemSP.addActionListener(e -> themSanPhamVaoPhieu(txtGiaNhap));
        btnPanel.add(btnThemSP);
        
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTongTienPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JPanel tongTienPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tongTienPanel.setBackground(Color.WHITE);
        
        JLabel lbl1 = new JLabel("Tổng tiền nhập:");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien = new JLabel("0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTongTien.setForeground(Color.RED);
        
        tongTienPanel.add(lbl1);
        tongTienPanel.add(lblTongTien);
        
        panel.add(tongTienPanel, BorderLayout.NORTH);
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        btnLuuNK = createStyledButton("Lưu phiếu nhập", new Color(46, 204, 113));
        btnLuuNK.setPreferredSize(new Dimension(0, 40));
        btnLuuNK.addActionListener(e -> luuPhieuNhap());
        
        btnHuyNK = createStyledButton("Hủy phiếu", new Color(231, 76, 60));
        btnHuyNK.setPreferredSize(new Dimension(0, 40));
        btnHuyNK.addActionListener(e -> huyPhieuNhap());
        
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
        searchPanel.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        searchPanel.add(txtTimKiem);
        btnTimKiem = createStyledButton("Tìm", new Color(52, 152, 219));
        btnTimKiem.setPreferredSize(new Dimension(80, 28));
        btnTimKiem.addActionListener(e -> timKiemPhieuNhap());
        searchPanel.add(btnTimKiem);
        
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        btnLamMoi.setPreferredSize(new Dimension(80, 28));
        btnLamMoi.addActionListener(e -> loadDanhSachNhapKho());
        searchPanel.add(btnLamMoi);
        
        // *** MỚI: THÊM NÚT XÁC NHẬN ***
        btnXacNhan = createStyledButton("Xác nhận", new Color(46, 204, 113));
        btnXacNhan.setPreferredSize(new Dimension(100, 28));
        btnXacNhan.addActionListener(e -> xacNhanPhieuNhap());
        searchPanel.add(btnXacNhan);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = {"Mã phiếu", "Mã NV", "Mã NCC", "Ngày nhập", "Tổng tiền", "Trạng thái"};
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
        tableNhapKho.getTableHeader().setForeground(Color.BLACK);
        
        tableNhapKho.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableNhapKho.getSelectedRow();
                    if (row != -1) {
                        xemChiTietPhieuNhap(row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableNhapKho);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    // 3. THÊM PHƯƠNG THỨC XÁC NHẬN PHIẾU NHẬP
    private void xacNhanPhieuNhap() {
        int selectedRow = tableNhapKho.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn phiếu nhập cần xác nhận!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String idNK = tableNhapKho.getValueAt(selectedRow, 0).toString();
        String trangThai = tableNhapKho.getValueAt(selectedRow, 5).toString();
        
        // Kiểm tra trạng thái hiện tại
        if (trangThai.equals("Đã hoàn thành")) {
            JOptionPane.showMessageDialog(this, 
                "Phiếu nhập này đã được xác nhận trước đó!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Hiển thị thông tin xác nhận
        String tongTien = tableNhapKho.getValueAt(selectedRow, 4).toString();
        String ngayNhap = tableNhapKho.getValueAt(selectedRow, 3).toString();
        String idNCC = tableNhapKho.getValueAt(selectedRow, 2).toString();
        
        // Lấy chi tiết phiếu nhập
        List<ChiTietNhapKho> listCT = chiTietDAO.getByNhapKho(idNK);
        
        StringBuilder message = new StringBuilder();
        message.append("═══════════════════════════════════════\n");
        message.append("XÁC NHẬN PHIẾU NHẬP KHO\n");
        message.append("═══════════════════════════════════════\n\n");
        message.append("Mã phiếu: ").append(idNK).append("\n");
        message.append("Ngày nhập: ").append(ngayNhap).append("\n");
        message.append("Nhà cung cấp: ").append(idNCC).append("\n");
        message.append("Tổng tiền: ").append(tongTien).append("\n\n");
        message.append("Số lượng sản phẩm: ").append(listCT.size()).append("\n\n");
        message.append("───────────────────────────────────────\n");
        message.append("Bạn có chắc chắn muốn xác nhận phiếu này?\n");
        message.append("Sau khi xác nhận, trạng thái sẽ chuyển thành\n");
        message.append("'Đã hoàn thành' và không thể thay đổi.\n");
        message.append("═══════════════════════════════════════");
        
        // Hiển thị dialog xác nhận
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(255, 255, 220)); // Màu vàng nhạt
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        int choice = JOptionPane.showConfirmDialog(this, scrollPane,
            "Xác nhận phiếu nhập " + idNK,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // CẬP NHẬT TRẠNG THÁI TRONG DATABASE
            NhapKho nk = nhapKhoDAO.getById(idNK);
            if (nk != null) {
                nk.setStatus("Đã hoàn thành");
                boolean success = nhapKhoDAO.update(nk);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "✓ Đã xác nhận phiếu nhập " + idNK + " thành công!\n" +
                        "Trạng thái: Đã hoàn thành",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Reload danh sách
                    loadDanhSachNhapKho();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Lỗi khi cập nhật trạng thái phiếu nhập!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
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
    
    private void taoPhieuNhapMoi() {
        currentNhapKhoId = generateNextNhapKhoId();
        txtIdNK.setText(currentNhapKhoId);
        
        if (currentUser != null) {
            txtIdNV.setText(currentUser.getIdNV());
        } else {
            txtIdNV.setText("NV001");
        }
        
        txtIdNCC.setText("");
        txtTenNCC.setText("");
        modelChiTiet.setRowCount(0);
        tongTienNK = 0;
        updateTongTien();
        
        JOptionPane.showMessageDialog(this, "Đã tạo phiếu nhập mới: " + currentNhapKhoId,
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
        String maNCC = txtIdNCC.getText().trim();
        
        if (maNCC.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã nhà cung cấp!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        NhaCungCap ncc = nhaCungCapDAO.getById(maNCC);
        
        if (ncc != null) {
            txtTenNCC.setText(ncc.getTenNCC());
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhà cung cấp!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            txtTenNCC.setText("");
        }
    }
    
    private void themSanPhamVaoPhieu(JTextField txtGiaNhap) {
        if (currentNhapKhoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo phiếu nhập mới trước!",
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
            
            float giaNhap = Float.parseFloat(txtGiaNhap.getText().trim());
            if (giaNhap <= 0) {
                JOptionPane.showMessageDialog(this, "Giá nhập phải lớn hơn 0!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String maSP = tableSanPham.getValueAt(row, 0).toString();
            String tenSP = tableSanPham.getValueAt(row, 1).toString();
            
            float thanhTien = giaNhap * soLuong;
            
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
            txtGiaNhap.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng hoặc giá nhập không hợp lệ!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaSanPhamKhoiPhieu() {
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
    
    private void luuPhieuNhap() {
        if (currentNhapKhoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tạo phiếu nhập mới!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (txtIdNCC.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp!",
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
            nk.setIdNCC(txtIdNCC.getText());
            nk.setNgayNhap(new Date());
            nk.setTongTien(tongTienNK);
            nk.setStatus("Đã nhập");  // ← ĐỔI: Thành "Đã nhập" thay vì "Đã hoàn thành"
            
            if (nhapKhoDAO.insert(nk)) {
                int ctnkCount = chiTietDAO.getAll().size() + 1;
                
                StringBuilder thongBao = new StringBuilder();
                thongBao.append("Lưu phiếu nhập thành công!\n\n");
                thongBao.append("Mã phiếu: ").append(currentNhapKhoId).append("\n");
                thongBao.append("Tổng tiền: ").append(df.format(tongTienNK)).append(" đ\n");
                thongBao.append("Trạng thái: Đã nhập (Chờ xác nhận)\n\n");
                thongBao.append("═══════════════════════════════════════\n");
                thongBao.append("CẬP NHẬT SỐ LƯỢNG TỒN KHO:\n");
                thongBao.append("═══════════════════════════════════════\n");
                
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
                    
                    String idGiay = ctnk.getIdGiay();
                    int soLuongNhap = ctnk.getSoLuong();
                    Giay giay = giayDAO.getById(idGiay);
                    
                    if (giay != null) {
                        int tonKhoCu = giay.getSoLuong();
                        int tonKhoMoi = tonKhoCu + soLuongNhap;
                        
                        giayDAO.updateSoLuong(idGiay, tonKhoMoi);
                        
                        String tenGiay = modelChiTiet.getValueAt(i, 1).toString();
                        thongBao.append(String.format("• %s\n", tenGiay));
                        thongBao.append(String.format("  Tồn cũ: %d → Nhập: +%d → Tồn mới: %d\n\n", 
                            tonKhoCu, soLuongNhap, tonKhoMoi));
                    } else {
                        thongBao.append(String.format("⚠ CẢNH BÁO: Không tìm thấy sản phẩm %s\n\n", idGiay));
                    }
                }
                
                thongBao.append("═══════════════════════════════════════\n");
                thongBao.append("⚠ Lưu ý: Phiếu cần được XÁC NHẬN để hoàn thành!");
                
                JTextArea textArea = new JTextArea(thongBao.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(450, 400));
                
                JOptionPane.showMessageDialog(this, scrollPane,
                    "Nhập kho thành công", JOptionPane.INFORMATION_MESSAGE);
                
                currentNhapKhoId = "";
                txtIdNK.setText("");
                txtIdNCC.setText("");
                txtTenNCC.setText("");
                modelChiTiet.setRowCount(0);
                tongTienNK = 0;
                updateTongTien();
                
                loadDanhSachNhapKho();
                loadDanhSachSanPham();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu phiếu nhập: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void huyPhieuNhap() {
        if (currentNhapKhoId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có phiếu nhập nào để hủy!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn hủy phiếu nhập này?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            currentNhapKhoId = "";
            txtIdNK.setText("");
            txtIdNCC.setText("");
            txtTenNCC.setText("");
            modelChiTiet.setRowCount(0);
            tongTienNK = 0;
            updateTongTien();
            
            JOptionPane.showMessageDialog(this, "Đã hủy phiếu nhập!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void loadDanhSachNhapKho() {
        modelNhapKho.setRowCount(0);
        List<NhapKho> list = nhapKhoDAO.getAll();
        
        for (NhapKho nk : list) {
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
    
    private void loadDanhSachSanPham() {
        modelSanPham.setRowCount(0);
        List<Giay> list = giayDAO.getAll();
        
        // CHỈ HIỂN THỊ SẢN PHẨM ĐÃ CÓ TRONG HỆ THỐNG
        for (Giay g : list) {
            if (g.getStatus().equals("active")) {
                modelSanPham.addRow(new Object[]{
                    g.getIdGiay(),
                    g.getTenGiay(),
                    g.getSize(),
                    g.getSoLuong()
                });
            }
        }
    }
    
    private void timKiemPhieuNhap() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            loadDanhSachNhapKho();
            return;
        }
        
        modelNhapKho.setRowCount(0);
        List<NhapKho> list = nhapKhoDAO.getAll();
        
        for (NhapKho nk : list) {
            String idNK = nk.getIdNhapKho().toLowerCase();
            String idNCC = nk.getIdNCC().toLowerCase();
            String idNV = nk.getIdNV().toLowerCase();
            String status = nk.getStatus().toLowerCase();
            
            if (idNK.contains(keyword) || idNCC.contains(keyword) || 
                idNV.contains(keyword) || status.contains(keyword)) {
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
        
        if (modelNhapKho.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu nhập nào!",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void xemChiTietPhieuNhap(int row) {
        String idNK = tableNhapKho.getValueAt(row, 0).toString();
        String idNV = tableNhapKho.getValueAt(row, 1).toString();
        String idNCC = tableNhapKho.getValueAt(row, 2).toString();
        String ngayNhap = tableNhapKho.getValueAt(row, 3).toString();
        String tongTien = tableNhapKho.getValueAt(row, 4).toString();
        String trangThai = tableNhapKho.getValueAt(row, 5).toString();
        
        NhanVien nv = nhanVienDAO.getById(idNV);
        String tenNV = (nv != null) ? nv.getTenNV() : "N/A";
        
        NhaCungCap ncc = nhaCungCapDAO.getById(idNCC);
        String tenNCC = (ncc != null) ? ncc.getTenNCC() : "N/A";
        
        List<ChiTietNhapKho> listCT = chiTietDAO.getByNhapKho(idNK);
        StringBuilder chiTiet = new StringBuilder();
        chiTiet.append("═══════════════════════════════════════\n");
        chiTiet.append("         PHIẾU NHẬP KHO\n");
        chiTiet.append("═══════════════════════════════════════\n\n");
        chiTiet.append("Mã phiếu: ").append(idNK).append("\n");
        chiTiet.append("Ngày nhập: ").append(ngayNhap).append("\n");
        chiTiet.append("Nhân viên: ").append(tenNV).append(" (").append(idNV).append(")\n");
        chiTiet.append("Nhà cung cấp: ").append(tenNCC).append("\n");
        chiTiet.append("Trạng thái: ").append(trangThai).append("\n");
        chiTiet.append("\n───────────────────────────────────────\n");
        chiTiet.append("CHI TIẾT SẢN PHẨM:\n");
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
            "Chi tiết phiếu nhập: " + idNK, JOptionPane.INFORMATION_MESSAGE);
    }
    
}