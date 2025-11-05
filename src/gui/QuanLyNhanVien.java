package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import DAO.DAO_NhanVien;
import DAO.DAO_PhanQuyen;
import DAO.DAO_ChiTietPhanQuyen;
import DAO.DAO_ChucNang;
import model.ChiTietPhanQuyen;
import model.NhanVien;
import model.PhanQuyen;
import model.ChucNang;
import java.util.List;
import java.util.ArrayList;
import com.toedter.calendar.JDateChooser;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.TableModelEvent;

public class QuanLyNhanVien extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTen, txtUsername, txtSdt, txtDiaChi, txtTimKiem;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cboPhanQuyen, cboStatus, cboGioiTinh;
    private JDateChooser dateNgaySinh, dateNgayVaoLam;
    private JButton btnThem, btnSua, btnLamMoi, btnTimKiem, btnDoiMatKhau, btnQuanLyPQ;
    private JCheckBox chkShowPassword;
    private DAO_NhanVien nhanVienDAO;
    private DAO_PhanQuyen phanQuyenDAO;
    private DAO_ChucNang daoChucNang; // DAO mới
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    
    private ChiTietPhanQuyen permission;
    private boolean isAdmin;
    private Map<String, String> phanQuyenMap = new HashMap<>();
    private Map<String, String> phanQuyenMap_IDtoName = new HashMap<>();
    private Map<String, Color> colorMap = new HashMap<>();

    public QuanLyNhanVien() {
        nhanVienDAO = new DAO_NhanVien();
        phanQuyenDAO = new DAO_PhanQuyen();
        daoChucNang = new DAO_ChucNang(); // Khởi tạo DAO mới
        
        // Khởi tạo map màu
        colorMap.put("CN001", new Color(52, 152, 219)); // Nhân viên
        colorMap.put("CN002", new Color(46, 204, 113)); // Giày
        colorMap.put("CN003", new Color(230, 126, 34)); // Hóa đơn
        colorMap.put("CN004", new Color(231, 76, 60)); // Kho
        colorMap.put("CN005", new Color(155, 89, 182)); // Khách hàng
        colorMap.put("CN006", new Color(52, 73, 94)); // Thống kê
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(236, 240, 241));
        
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadPhanQuyen();
        loadData();
        generateNewEmployeeId();
    }
    
    public QuanLyNhanVien(ChiTietPhanQuyen permission, boolean isAdmin) {
        this();
        this.permission = permission;
        this.isAdmin = isAdmin;
        applyPermissions();
    }
    
    private void applyPermissions() {
        if (permission != null) {
            btnThem.setEnabled(permission.isDuocThem());
            btnSua.setEnabled(permission.isDuocSua());
            // Liên kết quyền đổi MK với quyền Sửa
            btnDoiMatKhau.setEnabled(permission.isDuocSua()); 
        } else {
            // Nếu không có quyền (lỗi), vô hiệu hóa hết
            btnThem.setEnabled(false);
            btnSua.setEnabled(false);
            btnDoiMatKhau.setEnabled(false);
        }
        
        // Áp dụng quy tắc đặc biệt: Chỉ Admin được quản lý phân quyền
        btnQuanLyPQ.setEnabled(this.isAdmin);
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
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
    
    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(420, 0));
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
        
        JLabel lblFormTitle = new JLabel("THÔNG TIN NHÂN VIÊN");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblFormTitle, gbc);
        gbc.gridwidth = 1;
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        
        // Mã nhân viên
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblMaNV = new JLabel("Mã nhân viên:");
        lblMaNV.setFont(labelFont);
        formPanel.add(lblMaNV, gbc);
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        formPanel.add(txtId, gbc);
        
        // Tên nhân viên
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblTenNV = new JLabel("Tên nhân viên:");
        lblTenNV.setFont(labelFont);
        formPanel.add(lblTenNV, gbc);
        txtTen = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtTen, gbc);
        
        // Ngày sinh
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblNgaySinh = new JLabel("Ngày sinh:");
        lblNgaySinh.setFont(labelFont);
        formPanel.add(lblNgaySinh, gbc);
        dateNgaySinh = new JDateChooser();
        dateNgaySinh.setDateFormatString("dd/MM/yyyy");
        dateNgaySinh.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1;
        formPanel.add(dateNgaySinh, gbc);
        
        // Giới tính
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(labelFont);
        formPanel.add(lblGioiTinh, gbc);
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        gbc.gridx = 1;
        formPanel.add(cboGioiTinh, gbc);
        
        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblSDT = new JLabel("Số điện thoại:");
        lblSDT.setFont(labelFont);
        formPanel.add(lblSDT, gbc);
        txtSdt = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtSdt, gbc);
        
        // Địa chỉ
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(labelFont);
        formPanel.add(lblDiaChi, gbc);
        txtDiaChi = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtDiaChi, gbc);
        
        // Ngày vào làm
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblNgayVaoLam = new JLabel("Ngày vào làm:");
        lblNgayVaoLam.setFont(labelFont);
        formPanel.add(lblNgayVaoLam, gbc);
        dateNgayVaoLam = new JDateChooser();
        dateNgayVaoLam.setDateFormatString("dd/MM/yyyy");
        dateNgayVaoLam.setPreferredSize(new Dimension(150, 25));
        dateNgayVaoLam.setDate(new Date());
        gbc.gridx = 1;
        formPanel.add(dateNgayVaoLam, gbc);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(labelFont);
        formPanel.add(lblUsername, gbc);
        txtUsername = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(labelFont);
        formPanel.add(lblPassword, gbc);
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 10;
        JLabel lblConfirm = new JLabel("Xác nhận MK:");
        lblConfirm.setFont(labelFont);
        formPanel.add(lblConfirm, gbc);
        txtConfirmPassword = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(txtConfirmPassword, gbc);
        
        // Checkbox hiển thị mật khẩu
        gbc.gridx = 1; gbc.gridy = 11;
        chkShowPassword = new JCheckBox("Hiển thị mật khẩu");
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
                txtConfirmPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
                txtConfirmPassword.setEchoChar('•');
            }
        });
        formPanel.add(chkShowPassword, gbc);
        
        // Phân quyền
        gbc.gridx = 0; gbc.gridy = 12;
        JLabel lblPhanQuyen = new JLabel("Phân quyền:");
        lblPhanQuyen.setFont(labelFont);
        formPanel.add(lblPhanQuyen, gbc);
        cboPhanQuyen = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cboPhanQuyen, gbc);
        
        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 13;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(labelFont);
        formPanel.add(lblStatus, gbc);
        cboStatus = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});
        gbc.gridx = 1;
        formPanel.add(cboStatus, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        btnThem = createStyledButton("Tạo", new Color(46, 204, 113));
        btnSua = createStyledButton("Sửa", new Color(52, 152, 219));
        btnDoiMatKhau = createStyledButton("Đổi MK", new Color(230, 126, 34));
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        btnQuanLyPQ = createStyledButton("Quản lý PQ", new Color(155, 89, 182));
        
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnDoiMatKhau);
        panel.add(btnLamMoi);
        panel.add(btnQuanLyPQ);
        
        btnThem.addActionListener(e -> themNhanVien());
        btnSua.addActionListener(e -> suaNhanVien());
        btnDoiMatKhau.addActionListener(e -> doiMatKhau());
        btnLamMoi.addActionListener(e -> lamMoi());
        btnQuanLyPQ.addActionListener(e -> quanLyPhanQuyen());
        
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
        
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(txtTimKiem);
        
        btnTimKiem = createStyledButton("🔍 Tìm", new Color(41, 128, 185));
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        searchPanel.add(btnTimKiem);
        
        btnTimKiem.addActionListener(e -> timKiem(true));
        
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiem(true);
                } else {
                    timKiem(false);
                }
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        String[] columns = {"Mã nhân viên", "Tên nhân viên", "Ngày sinh", "Giới tính", "SĐT", 
                           "Địa chỉ", "Ngày vào làm", "Username", "Phân quyền", "Trạng thái"};
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
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        
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
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<NhanVien> list = nhanVienDAO.getAll();
        
        for (NhanVien nv : list) {
            // Lấy Tên Quyền từ ID bằng Map tra cứu (Giải quyết lỗi "tenQuyen cannot be resolved")
            String tenQuyen = phanQuyenMap_IDtoName.get(nv.getIdPQ());
            if (tenQuyen == null) tenQuyen = "(Không rõ)"; // Xử lý trường hợp không tìm thấy
            
            tableModel.addRow(new Object[]{
                nv.getIdNV(),
                nv.getTenNV(),
                nv.getNgaySinh() != null ? sdf.format(nv.getNgaySinh()) : "",
                nv.getGioiTinh(),
                nv.getSdt(),
                nv.getDiaChi(),
                nv.getNgayVaoLam() != null ? sdf.format(nv.getNgayVaoLam()) : "",
                nv.getUsername(),
                tenQuyen, // <<== THAY THẾ nv.getPhanQuyen() BẰNG tenQuyen
                nv.getStatus()
            });
        }
    }
    
    private void loadPhanQuyen() {
        cboPhanQuyen.removeAllItems();
        phanQuyenMap.clear();
        phanQuyenMap_IDtoName.clear();
        List<PhanQuyen> list = phanQuyenDAO.getAll();
        for (PhanQuyen pq : list) {
            cboPhanQuyen.addItem(pq.getTenQuyen());
            phanQuyenMap.put(pq.getTenQuyen(), pq.getIdPQ());
            phanQuyenMap_IDtoName.put(pq.getIdPQ(), pq.getTenQuyen());
        }
    }
    
    private void generateNewEmployeeId() {
        List<NhanVien> list = nhanVienDAO.getAll();
        int maxId = 0;
        
        for (NhanVien nv : list) {
            String id = nv.getIdNV();
            if (id != null && id.startsWith("NV")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                }
            }
        }
        
        txtId.setText(String.format("NV%03d", maxId + 1));
    }
    
    private void themNhanVien() {
        txtUsername.setEditable(true);
        txtUsername.setBackground(Color.WHITE);
        txtPassword.setEditable(true);
        txtPassword.setBackground(Color.WHITE);
        txtConfirmPassword.setEditable(true);
        txtConfirmPassword.setBackground(Color.WHITE);
        
        if (!validateInput()) return;
        
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (nhanVienDAO.isUsernameExist(txtUsername.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Username đã tồn tại!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            NhanVien nv = new NhanVien();
            nv.setIdNV(txtId.getText().trim());
            nv.setTenNV(txtTen.getText().trim());
            
            if (dateNgaySinh.getDate() != null) {
                nv.setNgaySinh(new java.sql.Date(dateNgaySinh.getDate().getTime()));
            }
            
            nv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
            nv.setSdt(txtSdt.getText().trim());
            nv.setDiaChi(txtDiaChi.getText().trim());
            
            if (dateNgayVaoLam.getDate() != null) {
                nv.setNgayVaoLam(new java.sql.Date(dateNgayVaoLam.getDate().getTime()));
            }
            
            nv.setUsername(txtUsername.getText().trim());
            nv.setPassword(password);
            nv.setStatus(cboStatus.getSelectedItem().toString());
            
            String tenQuyen = cboPhanQuyen.getSelectedItem().toString();
            String idPQ = phanQuyenMap.get(tenQuyen); // Tra cứu ID

            if (idPQ == null) {
                JOptionPane.showMessageDialog(this, "Lỗi phân quyền. Không tìm thấy ID.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            nv.setIdPQ(idPQ);
           
            
            if (nhanVienDAO.insert(nv)) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void suaNhanVien() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa từ bảng!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputForUpdate()) return;
        
        try {
            NhanVien nv = new NhanVien();
            String id = txtId.getText().trim();
            nv.setIdNV(id);
            
            NhanVien oldNv = nhanVienDAO.getById(id);
            
            if (oldNv != null) {
                nv.setUsername(oldNv.getUsername());
                nv.setPassword(oldNv.getPassword());
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên để cập nhật!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            nv.setTenNV(txtTen.getText().trim());
            
            if (dateNgaySinh.getDate() != null) {
                nv.setNgaySinh(new java.sql.Date(dateNgaySinh.getDate().getTime()));
            }
            
            nv.setGioiTinh(cboGioiTinh.getSelectedItem().toString());
            nv.setSdt(txtSdt.getText().trim());
            nv.setDiaChi(txtDiaChi.getText().trim());
            
            if (dateNgayVaoLam.getDate() != null) {
                nv.setNgayVaoLam(new java.sql.Date(dateNgayVaoLam.getDate().getTime()));
            }
            
            nv.setStatus(cboStatus.getSelectedItem().toString());
            
            String tenQuyen = cboPhanQuyen.getSelectedItem().toString();
            String idPQ = phanQuyenMap.get(tenQuyen); // Tra cứu ID

            if (idPQ == null) {
                JOptionPane.showMessageDialog(this, "Lỗi phân quyền. Không tìm thấy ID.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            nv.setIdPQ(idPQ);
            
            if (nhanVienDAO.update(nv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void doiMatKhau() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần đổi mật khẩu từ bảng!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();
        
        Object[] message = {
            "Mật khẩu cũ:", oldPass,
            "Mật khẩu mới:", newPass,
            "Xác nhận mật khẩu:", confirmPass
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Đổi Mật Khẩu", 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPass.getPassword());
            String newPassword = new String(newPass.getPassword());
            String confirmPassword = new String(confirmPass.getPassword());
            
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            NhanVien nv = nhanVienDAO.getById(txtId.getText().trim());
            if (nv == null || !nv.getPassword().equals(oldPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            nv.setPassword(newPassword);
            if (nhanVienDAO.update(nv)) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // =========================================================================
    // PHẦN QUẢN LÝ PHÂN QUYỀN
    // =========================================================================

    /**
     * Mở dialog quản lý Phân Quyền.
     */
    private void quanLyPhanQuyen() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Quản lý Phân quyền", true);
        dialog.setSize(900, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);
        
        // Tiêu đề
        JLabel lblTitle = new JLabel("DANH SÁCH PHÂN QUYỀN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(155, 89, 182));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);
        
        // Split pane: Left = danh sách PQ, Right = chi tiết quyền
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        
        // LEFT PANEL: Bảng danh sách phân quyền
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Danh sách phân quyền"));
        
        String[] columns = {"Mã phân quyền", "Tên quyền", "Mô tả", "Trạng thái"};
        DefaultTableModel modelPQ = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tablePQ = new JTable(modelPQ);
        tablePQ.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablePQ.setRowHeight(30);
        tablePQ.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablePQ.getTableHeader().setBackground(new Color(155, 89, 182));
        tablePQ.getTableHeader().setForeground(Color.BLACK);
        tablePQ.setSelectionBackground(new Color(155, 89, 182));
        tablePQ.setSelectionForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(tablePQ);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        // RIGHT PANEL: Chi tiết quyền
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết quyền truy cập"));
        
        JLabel lblChiTiet = new JLabel("Chọn một phân quyền để xem chi tiết", SwingConstants.CENTER);
        lblChiTiet.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblChiTiet.setForeground(Color.GRAY);
        
        String[] columnsChiTiet = {"Chức năng", "Xem", "Thêm", "Sửa"};
        DefaultTableModel modelChiTiet = new DefaultTableModel(columnsChiTiet, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tableChiTiet = new JTable(modelChiTiet);
        tableChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableChiTiet.setRowHeight(30);
        tableChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollChiTiet = new JScrollPane(tableChiTiet);
        
        rightPanel.add(lblChiTiet, BorderLayout.NORTH);
        rightPanel.add(scrollChiTiet, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Load dữ liệu (SỬA: Lấy cả quyền bị ngừng để còn kích hoạt lại)
        List<PhanQuyen> list = phanQuyenDAO.getAllWithInactive();
        for (PhanQuyen pq : list) {
            modelPQ.addRow(new Object[]{
                pq.getIdPQ(),
                pq.getTenQuyen(),
                pq.getMoTa(),
                pq.getStatus()
            });
        }
        
        // Sự kiện click vào bảng phân quyền
        tablePQ.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tablePQ.getSelectedRow();
                if (row != -1) {
                    String idPQ = tablePQ.getValueAt(row, 0).toString();
                    String tenQuyen = tablePQ.getValueAt(row, 1).toString();
                    
                    lblChiTiet.setText("Quyền truy cập của: " + tenQuyen);
                    lblChiTiet.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lblChiTiet.setForeground(new Color(155, 89, 182));
                    
                    // Load chi tiết quyền
                    modelChiTiet.setRowCount(0);
                    DAO_ChiTietPhanQuyen ctpqDAO = new DAO_ChiTietPhanQuyen();
                    List<ChiTietPhanQuyen> listCT = ctpqDAO.getByIdPQ(idPQ);
                    
                    for (ChiTietPhanQuyen ct : listCT) {
                        modelChiTiet.addRow(new Object[]{
                            ct.getTenChucNang(),
                            ct.isDuocXem() ? "✓" : "✗",
                            ct.isDuocThem() ? "✓" : "✗",
                            ct.isDuocSua() ? "✓" : "✗"
                        });
                    }
                    
                    if (listCT.isEmpty()) {
                        modelChiTiet.addRow(new Object[]{
                            "Chưa có quyền nào được cấu hình", "", "", ""
                        });
                    }
                }
            }
        });
        
        // Panel nút
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        
        JButton btnThemPQ = createStyledButton("➕ Thêm mới", new Color(46, 204, 113));
        btnThemPQ.setPreferredSize(new Dimension(130, 35));
        
        JButton btnSuaPQ = createStyledButton("✏️ Sửa quyền", new Color(52, 152, 219));
        btnSuaPQ.setPreferredSize(new Dimension(130, 35));
        
        // SỬA: Thay nút Xóa bằng nút Đổi Trạng Thái
        JButton btnDoiTrangThai = createStyledButton("🔄 Đổi TT", new Color(230, 126, 34));
        btnDoiTrangThai.setPreferredSize(new Dimension(130, 35));
        
        JButton btnDong = createStyledButton("✗ Đóng", new Color(149, 165, 166));
        btnDong.setPreferredSize(new Dimension(130, 35));
        
        btnThemPQ.addActionListener(e -> {
            themPhanQuyen(); // Gọi phương thức thêm mới đã sửa
            // Refresh bảng
            modelPQ.setRowCount(0);
            List<PhanQuyen> newList = phanQuyenDAO.getAllWithInactive(); // SỬA: Lấy tất cả
            for (PhanQuyen pq : newList) {
                modelPQ.addRow(new Object[]{
                    pq.getIdPQ(),
                    pq.getTenQuyen(),
                    pq.getMoTa(),
                    pq.getStatus()
                });
            }
            loadPhanQuyen(); // Refresh combo box ở panel chính (chỉ lấy active)
            modelChiTiet.setRowCount(0);
            lblChiTiet.setText("Chọn một phân quyền để xem chi tiết");
            lblChiTiet.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblChiTiet.setForeground(Color.GRAY);
        });
        
        btnSuaPQ.addActionListener(e -> {
            int row = tablePQ.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng chọn phân quyền cần sửa!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String idPQ = tablePQ.getValueAt(row, 0).toString();
            suaPhanQuyen(idPQ); // Gọi phương thức sửa đã sửa
            
            // Refresh
            modelPQ.setRowCount(0);
            List<PhanQuyen> newList = phanQuyenDAO.getAllWithInactive(); // SỬA: Lấy tất cả
            for (PhanQuyen pq : newList) {
                modelPQ.addRow(new Object[]{
                    pq.getIdPQ(),
                    pq.getTenQuyen(),
                    pq.getMoTa(),
                    pq.getStatus()
                });
            }
            loadPhanQuyen();
            modelChiTiet.setRowCount(0); // Xóa chi tiết cũ
            lblChiTiet.setText("Chọn một phân quyền để xem chi tiết"); // Reset
        });
        
        // SỬA: Sự kiện cho nút Đổi Trạng Thái
        btnDoiTrangThai.addActionListener(e -> {
            int row = tablePQ.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng chọn phân quyền cần đổi trạng thái!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String idPQ = tablePQ.getValueAt(row, 0).toString();
            String tenQuyen = tablePQ.getValueAt(row, 1).toString();
            String trangThaiHienTai = tablePQ.getValueAt(row, 3).toString();
            
            String trangThaiMoi;
            if (trangThaiHienTai.equalsIgnoreCase("Hoạt động")) {
                trangThaiMoi = "Ngừng hoạt động";
            } else {
                trangThaiMoi = "Hoạt động";
            }

            int choice = JOptionPane.showConfirmDialog(dialog,
                "Bạn có chắc muốn đổi trạng thái của quyền [" + tenQuyen + "]\n"
                + "từ '" + trangThaiHienTai + "' thành '" + trangThaiMoi + "'?",
                "Xác nhận đổi trạng thái", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                PhanQuyen pq = phanQuyenDAO.getById(idPQ); // Dùng getById để lấy thông tin đầy đủ
                
                if (pq != null) {
                    pq.setStatus(trangThaiMoi);
                    
                    if (phanQuyenDAO.update(pq)) { // Giả sử DAO có hàm update(pq)
                        JOptionPane.showMessageDialog(dialog,
                            "Đổi trạng thái thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Tải lại bảng
                        modelPQ.setRowCount(0);
                        List<PhanQuyen> newList = phanQuyenDAO.getAllWithInactive();
                        for (PhanQuyen p : newList) {
                            modelPQ.addRow(new Object[]{
                                p.getIdPQ(),
                                p.getTenQuyen(),
                                p.getMoTa(),
                                p.getStatus()
                            });
                        }
                        
                        // Tải lại combobox ở panel chính (hàm này chỉ load quyền Active)
                        loadPhanQuyen(); 
                        
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Đổi trạng thái thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                     JOptionPane.showMessageDialog(dialog,
                        "Không tìm thấy phân quyền để cập nhật!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnDong.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(btnThemPQ);
        btnPanel.add(btnSuaPQ);
        btnPanel.add(btnDoiTrangThai); // SỬA: Thêm nút mới
        btnPanel.add(btnDong);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // ====================================================================
    // SỬA: Phương thức Helper để tạo Card Chức Năng
    // ====================================================================
    /**
     * Helper method để tạo 1 card chức năng (dùng cho Thêm/Sửa Phân Quyền)
     * @param cn Chức năng
     * @param checkboxMap Map để lưu trữ checkbox
     * @param cardColor Màu của card
     * @param existingRights Quyền hiện tại (null nếu là Thêm mới, không null nếu là Sửa)
     */
    private JPanel createFunctionCard(ChucNang cn, Map<String, JCheckBox> checkboxMap, Color cardColor, ChiTietPhanQuyen existingRights) {
        JPanel functionCard = new JPanel(new GridBagLayout()); 
        GridBagConstraints gbcFunc = new GridBagConstraints();
        gbcFunc.insets = new Insets(5, 10, 5, 10);
        gbcFunc.anchor = GridBagConstraints.WEST;
        
        functionCard.setBackground(Color.WHITE);
        functionCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cardColor, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        functionCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel lblChucNang = new JLabel(cn.getTenChucNang());
        lblChucNang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblChucNang.setForeground(cardColor);
        
        gbcFunc.gridx = 0;
        gbcFunc.gridy = 0;
        gbcFunc.weightx = 1.0;
        gbcFunc.fill = GridBagConstraints.HORIZONTAL;
        functionCard.add(lblChucNang, gbcFunc);
        
        gbcFunc.weightx = 0;
        gbcFunc.fill = GridBagConstraints.NONE;

        // Quyền XEM (Luôn có)
        JCheckBox chkXem = new JCheckBox("👁️ Xem");
        chkXem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkXem.setBackground(Color.WHITE);
        if (existingRights != null) chkXem.setSelected(existingRights.isDuocXem()); // Set state
        checkboxMap.put(cn.getIdCN() + "_xem", chkXem);
        gbcFunc.gridx = 1;
        functionCard.add(chkXem, gbcFunc);
        
        // Quyền THÊM (Nếu hỗ trợ)
        if (cn.isAllowThem()) {
            JCheckBox chkThem = new JCheckBox("➕ Thêm");
            chkThem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            chkThem.setBackground(Color.WHITE);
            if (existingRights != null) chkThem.setSelected(existingRights.isDuocThem()); // Set state
            checkboxMap.put(cn.getIdCN() + "_them", chkThem);
            gbcFunc.gridx = 2;
            functionCard.add(chkThem, gbcFunc);
        } 
        
        // Quyền SỬA (Nếu hỗ trợ)
        if (cn.isAllowSua()) {
            JCheckBox chkSua = new JCheckBox("✏️ Sửa");
            chkSua.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            chkSua.setBackground(Color.WHITE);
            if (existingRights != null) chkSua.setSelected(existingRights.isDuocSua()); // Set state
            checkboxMap.put(cn.getIdCN() + "_sua", chkSua);
            gbcFunc.gridx = 3;
            functionCard.add(chkSua, gbcFunc);
        }
        
        return functionCard;
    }

    /**
     * Mở dialog Thêm Phân Quyền Mới.
     * SỬA: Bỏ "Chú thích" và chia 2 cột (Full-access vs View-only)
     */
    private void themPhanQuyen() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Thêm phân quyền mới", true);
        dialog.setSize(800, 600); // Tăng chiều rộng cho 2 cột
        dialog.setLocationRelativeTo(this);
        
        // Map lưu trạng thái checkbox, key là idCN (ví dụ: "CN001_xem")
        Map<String, JCheckBox> checkboxMap = new HashMap<>();
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        // ==================== HEADER ====================
        JLabel lblTitle = new JLabel("TẠO PHÂN QUYỀN MỚI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(155, 89, 182));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        // ==================== FORM PANEL ====================
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(Color.WHITE);
        
        // Thông tin cơ bản
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
            "Thông tin phân quyền",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(155, 89, 182)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        JTextField txtIdPQ = new JTextField(20);
        JTextField txtTenQuyen = new JTextField(20);
        JTextArea txtMoTa = new JTextArea(2, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        JComboBox<String> cboStatusPQ = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});
        
        // Auto generate ID
        List<PhanQuyen> list = phanQuyenDAO.getAllWithInactive(); // Lấy tất cả để tìm maxID
        int maxId = 0;
        for (PhanQuyen pq : list) {
            String id = pq.getIdPQ();
            if (id != null && id.startsWith("PQ")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {}
            }
        }
        txtIdPQ.setText(String.format("PQ%03d", maxId + 1));
        txtIdPQ.setEditable(false);
        txtIdPQ.setBackground(new Color(240, 240, 240));
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lbl1 = new JLabel("Mã phân quyền:");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoPanel.add(lbl1, gbc);
        gbc.gridx = 1;
        infoPanel.add(txtIdPQ, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lbl2 = new JLabel("Tên quyền:");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoPanel.add(lbl2, gbc);
        gbc.gridx = 1;
        infoPanel.add(txtTenQuyen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lbl3 = new JLabel("Mô tả:");
        lbl3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoPanel.add(lbl3, gbc);
        gbc.gridx = 1;
        infoPanel.add(scrollMoTa, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lbl4 = new JLabel("Trạng thái:");
        lbl4.setFont(new Font("Segoe UI", Font.BOLD, 13));
        infoPanel.add(lbl4, gbc);
        gbc.gridx = 1;
        infoPanel.add(cboStatusPQ, gbc);
        
        formPanel.add(infoPanel, BorderLayout.NORTH);
        
        // ==================== QUYỀN TRUY CẬP - SỬA LAYOUT 2 CỘT ====================
        JPanel quyenPanel = new JPanel(new BorderLayout(10, 10));
        quyenPanel.setBackground(Color.WHITE);
        quyenPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
            "Cấu hình quyền truy cập",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(155, 89, 182)
        ));
        
        // SỬA: Bỏ Panel Chú thích
        
        // SỬA: Tách dữ liệu ra 2 danh sách
        List<ChucNang> danhSachChucNang = daoChucNang.getAllActive();
        List<ChucNang> fullAccessFunctions = new ArrayList<>();
        List<ChucNang> viewOnlyFunctions = new ArrayList<>();
        
        for (ChucNang cn : danhSachChucNang) {
            if (cn.isAllowThem() || cn.isAllowSua()) {
                fullAccessFunctions.add(cn);
            } else {
                viewOnlyFunctions.add(cn);
            }
        }
        
        // SỬA: Tạo panel 2 cột
        JPanel splitColumnsPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 hàng, 2 cột, 10px hgap
        splitColumnsPanel.setBackground(Color.WHITE);
        
        // --- CỘT TRÁI (Quyền đầy đủ) ---
        JPanel leftColumnWrapper = new JPanel(new BorderLayout());
        leftColumnWrapper.setBackground(Color.WHITE);
        leftColumnWrapper.setBorder(BorderFactory.createTitledBorder(
            null,
            "Quyền đầy đủ (Thêm/Sửa)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(41, 128, 185) // Blue
        ));
        
        JPanel leftFunctionsPanel = new JPanel();
        leftFunctionsPanel.setLayout(new BoxLayout(leftFunctionsPanel, BoxLayout.Y_AXIS));
        leftFunctionsPanel.setBackground(Color.WHITE);
        leftFunctionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- CỘT PHẢI (Chỉ xem) ---
        JPanel rightColumnWrapper = new JPanel(new BorderLayout());
        rightColumnWrapper.setBackground(Color.WHITE);
        rightColumnWrapper.setBorder(BorderFactory.createTitledBorder(
            null,
            "Quyền chỉ xem",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(127, 140, 141) // Gray
        ));
        
        JPanel rightFunctionsPanel = new JPanel();
        rightFunctionsPanel.setLayout(new BoxLayout(rightFunctionsPanel, BoxLayout.Y_AXIS));
        rightFunctionsPanel.setBackground(Color.WHITE);
        rightFunctionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Color defaultColor = new Color(127, 140, 141);

        // Vòng lặp 1: Quyền đầy đủ (Cột trái)
        for (ChucNang cn : fullAccessFunctions) {
            Color cardColor = colorMap.getOrDefault(cn.getIdCN(), defaultColor);
            JPanel functionCard = createFunctionCard(cn, checkboxMap, cardColor, null); // null vì đây là Thêm mới
            leftFunctionsPanel.add(functionCard);
            leftFunctionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        if (fullAccessFunctions.isEmpty()) {
            leftFunctionsPanel.add(new JLabel("  Không có chức năng nào."));
        }

        // Vòng lặp 2: Quyền chỉ xem (Cột phải)
        for (ChucNang cn : viewOnlyFunctions) {
            Color cardColor = colorMap.getOrDefault(cn.getIdCN(), defaultColor);
            JPanel functionCard = createFunctionCard(cn, checkboxMap, cardColor, null); // null vì đây là Thêm mới
            rightFunctionsPanel.add(functionCard);
            rightFunctionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
         if (viewOnlyFunctions.isEmpty()) {
            rightFunctionsPanel.add(new JLabel("  Không có chức năng nào."));
        }
        
        // Thêm JScrollPane cho 2 cột
        JScrollPane scrollLeft = new JScrollPane(leftFunctionsPanel);
        scrollLeft.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLeft.getVerticalScrollBar().setUnitIncrement(16);
        scrollLeft.setBorder(null); // Bỏ border của scrollpane
        
        JScrollPane scrollRight = new JScrollPane(rightFunctionsPanel);
        scrollRight.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollRight.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollRight.getVerticalScrollBar().setUnitIncrement(16);
        scrollRight.setBorder(null); // Bỏ border của scrollpane
        
        leftColumnWrapper.add(scrollLeft, BorderLayout.CENTER);
        rightColumnWrapper.add(scrollRight, BorderLayout.CENTER);
        
        splitColumnsPanel.add(leftColumnWrapper);
        splitColumnsPanel.add(rightColumnWrapper);
        
        // Thêm panel 2 cột vào panel quyền chính
        quyenPanel.add(splitColumnsPanel, BorderLayout.CENTER);
        
        // Buttons (Chọn tất cả / Bỏ chọn)
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        tableButtonPanel.setBackground(Color.WHITE);
        
        JButton btnChonTatCa = createStyledButton("✅ Chọn tất cả", new Color(46, 204, 113));
        btnChonTatCa.setPreferredSize(new Dimension(140, 35));
        btnChonTatCa.addActionListener(e -> {
            for (JCheckBox cb : checkboxMap.values()) {
                cb.setSelected(true);
            }
        });
        
        JButton btnBoChonTatCa = createStyledButton("❌ Bỏ chọn tất cả", new Color(231, 76, 60));
        btnBoChonTatCa.setPreferredSize(new Dimension(140, 35));
        btnBoChonTatCa.addActionListener(e -> {
            for (JCheckBox cb : checkboxMap.values()) {
                cb.setSelected(false);
            }
        });
        
        tableButtonPanel.add(btnChonTatCa);
        tableButtonPanel.add(btnBoChonTatCa);
        quyenPanel.add(tableButtonPanel, BorderLayout.SOUTH);
        
        formPanel.add(quyenPanel, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // ==================== BOTTOM BUTTONS ====================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        
        JButton btnLuu = createStyledButton("💾 Lưu", new Color(46, 204, 113));
        btnLuu.setPreferredSize(new Dimension(120, 40));
        btnLuu.addActionListener(e -> {
            // Validate
            if (txtTenQuyen.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Vui lòng nhập tên quyền!", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // 1. Tạo đối tượng PhanQuyen
                PhanQuyen pq = new PhanQuyen();
                pq.setIdPQ(txtIdPQ.getText().trim());
                pq.setTenQuyen(txtTenQuyen.getText().trim());
                pq.setMoTa(txtMoTa.getText().trim());
                pq.setStatus(cboStatusPQ.getSelectedItem().toString());
                
                // 2. Tạo danh sách ChiTietPhanQuyen
                List<ChiTietPhanQuyen> details = new ArrayList<>();
                
                // Lặp qua danh sách chức năng đã load (danhSachChucNang GỐC)
                for (ChucNang cn : danhSachChucNang) {
                    boolean duocXem = false;
                    boolean duocThem = false;
                    boolean duocSua = false;
                    
                    // Lấy checkbox từ map bằng idCN
                    JCheckBox cbXem = checkboxMap.get(cn.getIdCN() + "_xem");
                    JCheckBox cbThem = checkboxMap.get(cn.getIdCN() + "_them");
                    JCheckBox cbSua = checkboxMap.get(cn.getIdCN() + "_sua");
                    
                    if (cbXem != null) duocXem = cbXem.isSelected();
                    if (cbThem != null) duocThem = cbThem.isSelected();
                    if (cbSua != null) duocSua = cbSua.isSelected();
                    
                    // Chỉ lưu nếu có ít nhất 1 quyền được chọn
                    if (duocXem || duocThem || duocSua) {
                        ChiTietPhanQuyen ctpq = new ChiTietPhanQuyen();
                        ctpq.setIdCN(cn.getIdCN());
                        ctpq.setDuocXem(duocXem);
                        ctpq.setDuocThem(duocThem);
                        ctpq.setDuocSua(duocSua);
                        
                        details.add(ctpq);
                    }
                }
                
                // 3. Gọi DAO để thực hiện transaction
                if (phanQuyenDAO.insertWithDetails(pq, details)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "✅ Thêm phân quyền thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadPhanQuyen(); // Tải lại combobox
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "❌ Thêm phân quyền thất bại! Tên quyền có thể đã tồn tại.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Lỗi: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        JButton btnHuy = createStyledButton("❌ Hủy", new Color(149, 165, 166));
        btnHuy.setPreferredSize(new Dimension(120, 40));
        btnHuy.addActionListener(e -> dialog.dispose());
        
        bottomPanel.add(btnLuu);
        bottomPanel.add(btnHuy);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    /**
     * Mở dialog Sửa Phân Quyền.
     * SỬA: Dùng layout 2 cột (Full-access vs View-only)
     */
    private void suaPhanQuyen(String idPQ) {
        PhanQuyen pq = phanQuyenDAO.getById(idPQ);
        if (pq == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy phân quyền!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Map lưu checkbox để đọc giá trị khi lưu
        Map<String, JCheckBox> checkboxMap = new HashMap<>();
        
        // Lấy tất cả chức năng từ CSDL
        List<ChucNang> danhSachChucNang = daoChucNang.getAllActive();
        
        // Lấy danh sách quyền hiện tại của PQ này
        DAO_ChiTietPhanQuyen ctpqDAO = new DAO_ChiTietPhanQuyen();
        List<ChiTietPhanQuyen> existingRights = ctpqDAO.getByIdPQ(idPQ);
        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Sửa phân quyền", true);
        dialog.setSize(800, 600); // Tăng chiều rộng
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JLabel lblTitle = new JLabel("SỬA PHÂN QUYỀN: " + pq.getTenQuyen(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(52, 152, 219));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(Color.WHITE);
        
        // Thông tin cơ bản (Giữ nguyên)
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Thông tin phân quyền",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        JTextField txtIdPQ = new JTextField(20);
        txtIdPQ.setText(pq.getIdPQ());
        txtIdPQ.setEditable(false);
        txtIdPQ.setBackground(new Color(240, 240, 240));
        
        JTextField txtTenQuyen = new JTextField(20);
        txtTenQuyen.setText(pq.getTenQuyen());
        
        JTextArea txtMoTa = new JTextArea(2, 20);
        txtMoTa.setText(pq.getMoTa());
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        
        JComboBox<String> cboStatusPQ = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});
        cboStatusPQ.setSelectedItem(pq.getStatus());
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Mã phân quyền:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(txtIdPQ, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Tên quyền:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(txtTenQuyen, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(scrollMoTa, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(cboStatusPQ, gbc);
        
        formPanel.add(infoPanel, BorderLayout.NORTH);
        
        // ====================================================================
        // === SỬA: Panel chọn quyền - DÙNG LAYOUT 2 CỘT (GIỐNG THÊM MỚI) ===
        // ====================================================================
        JPanel quyenPanel = new JPanel(new BorderLayout(10, 10));
        quyenPanel.setBackground(Color.WHITE);
        quyenPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            "Cấu hình quyền truy cập",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));

        // SỬA: Tách dữ liệu ra 2 danh sách
        List<ChucNang> fullAccessFunctions = new ArrayList<>();
        List<ChucNang> viewOnlyFunctions = new ArrayList<>();
        
        for (ChucNang cn : danhSachChucNang) {
            if (cn.isAllowThem() || cn.isAllowSua()) {
                fullAccessFunctions.add(cn);
            } else {
                viewOnlyFunctions.add(cn);
            }
        }
        
        // SỬA: Tạo panel 2 cột
        JPanel splitColumnsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        splitColumnsPanel.setBackground(Color.WHITE);
        
        // --- CỘT TRÁI (Quyền đầy đủ) ---
        JPanel leftColumnWrapper = new JPanel(new BorderLayout());
        leftColumnWrapper.setBackground(Color.WHITE);
        leftColumnWrapper.setBorder(BorderFactory.createTitledBorder(
            null,
            "Quyền đầy đủ (Thêm/Sửa)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(41, 128, 185) // Blue
        ));
        
        JPanel leftFunctionsPanel = new JPanel();
        leftFunctionsPanel.setLayout(new BoxLayout(leftFunctionsPanel, BoxLayout.Y_AXIS));
        leftFunctionsPanel.setBackground(Color.WHITE);
        leftFunctionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- CỘT PHẢI (Chỉ xem) ---
        JPanel rightColumnWrapper = new JPanel(new BorderLayout());
        rightColumnWrapper.setBackground(Color.WHITE);
        rightColumnWrapper.setBorder(BorderFactory.createTitledBorder(
            null,
            "Quyền chỉ xem",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(127, 140, 141) // Gray
        ));
        
        JPanel rightFunctionsPanel = new JPanel();
        rightFunctionsPanel.setLayout(new BoxLayout(rightFunctionsPanel, BoxLayout.Y_AXIS));
        rightFunctionsPanel.setBackground(Color.WHITE);
        rightFunctionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Color defaultColor = new Color(127, 140, 141);

        // Vòng lặp 1: Quyền đầy đủ (Cột trái)
        for (ChucNang cn : fullAccessFunctions) {
            Color cardColor = colorMap.getOrDefault(cn.getIdCN(), defaultColor);
            // Tìm quyền hiện có cho chức năng này
            ChiTietPhanQuyen existing = null;
            for (ChiTietPhanQuyen ct : existingRights) {
                if (ct.getIdCN().equals(cn.getIdCN())) {
                    existing = ct;
                    break;
                }
            }
            JPanel functionCard = createFunctionCard(cn, checkboxMap, cardColor, existing); // Pass 'existing'
            leftFunctionsPanel.add(functionCard);
            leftFunctionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        if (fullAccessFunctions.isEmpty()) {
            leftFunctionsPanel.add(new JLabel("  Không có chức năng nào."));
        }

        // Vòng lặp 2: Quyền chỉ xem (Cột phải)
        for (ChucNang cn : viewOnlyFunctions) {
            Color cardColor = colorMap.getOrDefault(cn.getIdCN(), defaultColor);
            // Tìm quyền hiện có cho chức năng này
            ChiTietPhanQuyen existing = null;
            for (ChiTietPhanQuyen ct : existingRights) {
                if (ct.getIdCN().equals(cn.getIdCN())) {
                    existing = ct;
                    break;
                }
            }
            JPanel functionCard = createFunctionCard(cn, checkboxMap, cardColor, existing); // Pass 'existing'
            rightFunctionsPanel.add(functionCard);
            rightFunctionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        if (viewOnlyFunctions.isEmpty()) {
            rightFunctionsPanel.add(new JLabel("  Không có chức năng nào."));
        }
        
        // Thêm JScrollPane cho 2 cột
        JScrollPane scrollLeft = new JScrollPane(leftFunctionsPanel);
        scrollLeft.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollLeft.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLeft.getVerticalScrollBar().setUnitIncrement(16);
        scrollLeft.setBorder(null);
        
        JScrollPane scrollRight = new JScrollPane(rightFunctionsPanel);
        scrollRight.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollRight.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollRight.getVerticalScrollBar().setUnitIncrement(16);
        scrollRight.setBorder(null);
        
        leftColumnWrapper.add(scrollLeft, BorderLayout.CENTER);
        rightColumnWrapper.add(scrollRight, BorderLayout.CENTER);
        
        splitColumnsPanel.add(leftColumnWrapper);
        splitColumnsPanel.add(rightColumnWrapper);
        
        // Thêm panel 2 cột vào panel quyền chính
        quyenPanel.add(splitColumnsPanel, BorderLayout.CENTER);
        
        formPanel.add(quyenPanel, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // ====================================================================
        
        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        
        JButton btnLuu = createStyledButton("💾 Lưu", new Color(46, 204, 113));
        btnLuu.setPreferredSize(new Dimension(120, 35));
        btnLuu.addActionListener(e -> {
            if (txtTenQuyen.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập tên quyền!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // 1. Cập nhật thông tin đối tượng PhanQuyen
                pq.setTenQuyen(txtTenQuyen.getText().trim());
                pq.setMoTa(txtMoTa.getText().trim());
                pq.setStatus(cboStatusPQ.getSelectedItem().toString());
                
                // 2. SỬA: Tạo danh sách ChiTietPhanQuyen MỚI từ checkboxMap
                List<ChiTietPhanQuyen> details = new ArrayList<>();
                
                // Lặp qua danh sách chức năng (danhSachChucNang GỐC)
                for (ChucNang cn : danhSachChucNang) {
                    boolean duocXem = false;
                    boolean duocThem = false;
                    boolean duocSua = false;
                    
                    // Lấy checkbox từ map bằng idCN
                    JCheckBox cbXem = checkboxMap.get(cn.getIdCN() + "_xem");
                    JCheckBox cbThem = checkboxMap.get(cn.getIdCN() + "_them");
                    JCheckBox cbSua = checkboxMap.get(cn.getIdCN() + "_sua");
                    
                    if (cbXem != null) duocXem = cbXem.isSelected();
                    if (cbThem != null) duocThem = cbThem.isSelected();
                    if (cbSua != null) duocSua = cbSua.isSelected();
                    
                    // Chỉ lưu nếu có ít nhất 1 quyền
                    if (duocXem || duocThem || duocSua) {
                        ChiTietPhanQuyen ctpq = new ChiTietPhanQuyen();
                        ctpq.setIdPQ(idPQ); // idPQ đã có
                        ctpq.setIdCN(cn.getIdCN()); // idCN lấy từ map
                        ctpq.setDuocXem(duocXem);
                        ctpq.setDuocThem(duocThem);
                        ctpq.setDuocSua(duocSua);
                        details.add(ctpq);
                    }
                }
                
                // 3. Gọi DAO để thực hiện transaction update (Giữ nguyên)
                if (phanQuyenDAO.updateWithDetails(pq, details)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Cập nhật phân quyền thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Cập nhật phân quyền thất bại! Tên quyền có thể bị trùng.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        JButton btnHuy = createStyledButton("✗ Hủy", new Color(149, 165, 166));
        btnHuy.setPreferredSize(new Dimension(120, 35));
        btnHuy.addActionListener(e -> dialog.dispose());
        
        bottomPanel.add(btnLuu);
        bottomPanel.add(btnHuy);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    // =========================================================================
    // CÁC PHƯƠNG THỨC CÒN LẠI (KHÔNG THAY ĐỔI)
    // =========================================================================

    private void timKiem(boolean showMessage) {
        String keyword = txtTimKiem.getText().trim();
        
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<NhanVien> allList = nhanVienDAO.getAll();
        List<NhanVien> resultList = new ArrayList<>();
        
        String normalizedKeyword = removeAccent(keyword.toLowerCase());
        
        for (NhanVien nv : allList) {
            if (nv.getIdNV() != null && nv.getIdNV().toLowerCase().contains(keyword.toLowerCase())) {
                resultList.add(nv);
                continue;
            }
            
            if (nv.getTenNV() != null) {
                String normalizedName = removeAccent(nv.getTenNV().toLowerCase());
                if (normalizedName.contains(normalizedKeyword)) {
                    resultList.add(nv);
                }
            }
        }
        
        for (NhanVien nv : resultList) {
            tableModel.addRow(new Object[]{
                nv.getIdNV(),
                nv.getTenNV(),
                nv.getNgaySinh() != null ? sdf.format(nv.getNgaySinh()) : "",
                nv.getGioiTinh(),
                nv.getSdt(),
                nv.getDiaChi(),
                nv.getNgayVaoLam() != null ? sdf.format(nv.getNgayVaoLam()) : "",
                nv.getUsername(),
                nv.getPhanQuyen(),
                nv.getStatus()
            });
        }
        
        if (resultList.isEmpty() && showMessage) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy nhân viên nào khớp với từ khóa: " + keyword, 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
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
        String ngaySinhStr = table.getValueAt(row, 2).toString();
        if (!ngaySinhStr.isEmpty()) {
            try {
                dateNgaySinh.setDate(sdf.parse(ngaySinhStr));
            } catch (Exception e) {
                dateNgaySinh.setDate(null);
            }
        } else {
            dateNgaySinh.setDate(null);
        }
        
        cboGioiTinh.setSelectedItem(table.getValueAt(row, 3).toString());
        txtSdt.setText(table.getValueAt(row, 4).toString());
        txtDiaChi.setText(table.getValueAt(row, 5).toString());
        
        // Ngày vào làm
        String ngayVaoLamStr = table.getValueAt(row, 6).toString();
        if (!ngayVaoLamStr.isEmpty()) {
            try {
                dateNgayVaoLam.setDate(sdf.parse(ngayVaoLamStr));
            } catch (Exception e) {
                dateNgayVaoLam.setDate(null);
            }
        } else {
            dateNgayVaoLam.setDate(null);
        }
        
        txtUsername.setText(table.getValueAt(row, 7).toString());
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(240, 240, 240));
        
        txtPassword.setText("");
        txtPassword.setEditable(false);
        txtPassword.setBackground(new Color(240, 240, 240));
        
        txtConfirmPassword.setText("");
        txtConfirmPassword.setEditable(false);
        txtConfirmPassword.setBackground(new Color(240, 240, 240));
        
        cboPhanQuyen.setSelectedItem(table.getValueAt(row, 8).toString());
        cboStatus.setSelectedItem(table.getValueAt(row, 9).toString());
        
        btnThem.setEnabled(false);
        if (permission != null) {
            btnSua.setEnabled(permission.isDuocSua());
            btnDoiMatKhau.setEnabled(permission.isDuocSua());
        }
    }
    
    private void lamMoi() {
        txtTen.setText("");
        dateNgaySinh.setDate(null);
        cboGioiTinh.setSelectedIndex(0);
        txtSdt.setText("");
        txtDiaChi.setText("");
        dateNgayVaoLam.setDate(new Date());
        
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtUsername.setBackground(Color.WHITE);
        
        txtPassword.setText("");
        txtPassword.setEditable(true);
        txtPassword.setBackground(Color.WHITE);
        
        txtConfirmPassword.setText("");
        txtConfirmPassword.setEditable(true);
        txtConfirmPassword.setBackground(Color.WHITE);
        
        txtTimKiem.setText("");
        if (cboPhanQuyen.getItemCount() > 0) {
            cboPhanQuyen.setSelectedIndex(0);
        }
        cboStatus.setSelectedIndex(0);
        chkShowPassword.setSelected(false);
        txtPassword.setEchoChar('•');
        txtConfirmPassword.setEchoChar('•');
        table.clearSelection();
        
        generateNewEmployeeId();
        txtTen.requestFocus();
        
        applyPermissions();
    }
    
    private boolean validateInput() {
        if (txtId.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty() ||
            txtUsername.getText().trim().isEmpty() || new String(txtPassword.getPassword()).isEmpty() ||
            new String(txtConfirmPassword.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (dateNgaySinh.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (dateNgayVaoLam.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày vào làm!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtSdt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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
        
        String password = new String(txtPassword.getPassword());
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return false;
        }
        
        if (cboPhanQuyen.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Chưa có phân quyền nào! Vui lòng thêm phân quyền trước.", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean validateInputForUpdate() {
        if (txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhân viên!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }
        
        if (dateNgaySinh.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (dateNgayVaoLam.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày vào làm!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtSdt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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
        
        if (cboPhanQuyen.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Chưa có phân quyền nào! Vui lòng thêm phân quyền trước.", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
}