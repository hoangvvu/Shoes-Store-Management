package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import DAO.DAO_NhanVien;
import model.NhanVien;
import java.util.List;
import java.util.ArrayList;

public class QuanLyNhanVien extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTen, txtUsername, txtEmail, txtTimKiem;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cboPhanQuyen, cboStatus;
    // Bỏ btnXoa khỏi khai báo
    private JButton btnThem, btnSua, btnLamMoi, btnTimKiem, btnDoiMatKhau; 
    private JCheckBox chkShowPassword;
    private DAO_NhanVien nhanVienDAO;
    
    public QuanLyNhanVien() {
        nhanVienDAO = new DAO_NhanVien();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(236, 240, 241));
        
        // Panel tiêu đề
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Panel form nhập liệu
        add(createFormPanel(), BorderLayout.WEST);
        
        // Panel bảng và tìm kiếm
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Load dữ liệu ban đầu
        loadData();
        
        // Tự động tạo mã nhân viên mới
        generateNewEmployeeId();
    }
    
    private JPanel createTitlePanel() {
        // ... (Không thay đổi)
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        // Tiêu đề ở giữa
        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(52, 73, 94));
        panel.add(lblTitle, BorderLayout.CENTER);

        // Nút quay lại bên trái
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setBackground(new Color(52, 152, 219));
        btnBack.setForeground(Color.RED);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hiệu ứng hover
        btnBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnBack.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(MouseEvent evt) {
                btnBack.setBackground(new Color(52, 152, 219));
            }
        });

        // Sự kiện click: Quay về MainFrame
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
        // ... (Không thay đổi, giữ nguyên layout)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(380, 0));
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
        
        // Tiêu đề form
        JLabel lblFormTitle = new JLabel("THÔNG TIN NHÂN VIÊN");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblFormTitle, gbc);
        gbc.gridwidth = 1;
        
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        
        // Mã nhân viên
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblMaNV = new JLabel("Mã NV:");
        lblMaNV.setFont(labelFont);
        lblMaNV.setForeground(Color.BLACK);
        formPanel.add(lblMaNV, gbc);
        
        txtId = new JTextField(15);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        txtId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1;
        formPanel.add(txtId, gbc);
        
        // Tên nhân viên
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblTenNV = new JLabel("Tên NV:");
        lblTenNV.setFont(labelFont);
        lblTenNV.setForeground(Color.BLACK);
        formPanel.add(lblTenNV, gbc);
        
        txtTen = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtTen, gbc);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(labelFont);
        lblUsername.setForeground(Color.BLACK);
        formPanel.add(lblUsername, gbc);
        
        txtUsername = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(labelFont);
        lblPassword.setForeground(Color.BLACK);
        formPanel.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel lblConfirm = new JLabel("Xác nhận MK:");
        lblConfirm.setFont(labelFont);
        lblConfirm.setForeground(Color.BLACK);
        formPanel.add(lblConfirm, gbc);
        
        txtConfirmPassword = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(txtConfirmPassword, gbc);
        
        // Checkbox hiển thị mật khẩu
        gbc.gridx = 1; gbc.gridy = 6;
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
        
        // Email
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        lblEmail.setForeground(Color.BLACK);
        formPanel.add(lblEmail, gbc);
        
        txtEmail = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);
        
        // Phân quyền
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel lblPhanQuyen = new JLabel("Phân quyền:");
        lblPhanQuyen.setFont(labelFont);
        lblPhanQuyen.setForeground(Color.BLACK);
        formPanel.add(lblPhanQuyen, gbc);
        
        cboPhanQuyen = new JComboBox<>(new String[]{"Admin", "User", "Manager"});
        gbc.gridx = 1;
        formPanel.add(cboPhanQuyen, gbc);
        
        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 9;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(labelFont);
        lblStatus.setForeground(Color.BLACK);
        formPanel.add(lblStatus, gbc);
        
        cboStatus = new JComboBox<>(new String[]{"active", "inactive"});
        gbc.gridx = 1;
        formPanel.add(cboStatus, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createButtonPanel() {
        // Cập nhật: Chỉ còn 4 nút chính
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10)); 
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        btnThem = createStyledButton("Thêm", new Color(46, 204, 113));
        btnSua = createStyledButton("Sửa", new Color(52, 152, 219));
        btnDoiMatKhau = createStyledButton("Đổi MK", new Color(230, 126, 34));
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnDoiMatKhau);
        panel.add(btnLamMoi);
        
        // Thêm sự kiện
        btnThem.addActionListener(e -> themNhanVien());
        btnSua.addActionListener(e -> suaNhanVien());
        btnDoiMatKhau.addActionListener(e -> doiMatKhau());
        btnLamMoi.addActionListener(e -> lamMoi());
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        // ... (Không thay đổi)
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 35));
        
        // Hiệu ứng hover
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
        // ... (Không thay đổi)
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        // Panel tìm kiếm
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
                    timKiem(true); // Nhấn Enter: hiển thị thông báo
                } else {
                    timKiem(false); // Gõ bình thường: KHÔNG hiển thị thông báo
                }
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Bảng dữ liệu
        String[] columns = {"Mã NV", "Tên NV", "Username", "Email", "Phân quyền", "Trạng thái"};
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
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        
        // Sự kiện click vào bảng
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
        // ... (Không thay đổi)
        tableModel.setRowCount(0);
        List<NhanVien> list = nhanVienDAO.getAll();
        
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[]{
                nv.getIdNV(),
                nv.getTenNV(),
                nv.getUsername(),
                nv.getEmail(),
                nv.getPhanQuyen(),
                nv.getStatus()
            });
        }
    }
    
    private void generateNewEmployeeId() {
        // ... (Không thay đổi)
        List<NhanVien> list = nhanVienDAO.getAll();
        
        if (list.isEmpty()) {
            txtId.setText("NV001");
        } else {
            // Tìm mã nhân viên lớn nhất
            int maxId = 0;
            for (NhanVien nv : list) {
                String id = nv.getIdNV();
                if (id.startsWith("NV")) {
                    try {
                        int num = Integer.parseInt(id.substring(2));
                        if (num > maxId) {
                            maxId = num;
                        }
                    } catch (NumberFormatException e) {
                        // Bỏ qua nếu format không đúng
                    }
                }
            }
            
            // Tạo mã mới
            maxId++;
            txtId.setText(String.format("NV%03d", maxId));
        }
    }
    
    private void themNhanVien() {
        // ... (Không thay đổi)
        // Đảm bảo các trường cho phép nhập khi thêm
        txtUsername.setEditable(true);
        txtUsername.setBackground(Color.WHITE);
        txtPassword.setEditable(true);
        txtPassword.setBackground(Color.WHITE);
        txtConfirmPassword.setEditable(true);
        txtConfirmPassword.setBackground(Color.WHITE);
        
        if (!validateInput()) return;
        
        // Kiểm tra password khớp
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra username đã tồn tại
        if (nhanVienDAO.isUsernameExist(txtUsername.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Username đã tồn tại!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            NhanVien nv = new NhanVien();
            nv.setIdNV(txtId.getText().trim());
            nv.setTenNV(txtTen.getText().trim());
            nv.setUsername(txtUsername.getText().trim());
            nv.setPassword(password);
            nv.setEmail(txtEmail.getText().trim());
            nv.setPhanQuyen(cboPhanQuyen.getSelectedItem().toString());
            nv.setStatus(cboStatus.getSelectedItem().toString());
            
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
    
    // Giữ nguyên logic sửa: chỉ sửa 4 trường, vô hiệu hóa nhập mật khẩu
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
            
            // Lấy lại toàn bộ dữ liệu cũ (Username, Password)
            NhanVien oldNv = nhanVienDAO.getById(id);
            
            if (oldNv != null) {
                // 1. Giữ nguyên Username và Password cũ (không cho phép sửa ở đây)
                nv.setUsername(oldNv.getUsername()); 
                nv.setPassword(oldNv.getPassword()); 
            } else {
                 JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên để cập nhật!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // 2. Cập nhật 4 trường được phép
            nv.setTenNV(txtTen.getText().trim()); 
            nv.setEmail(txtEmail.getText().trim()); 
            nv.setPhanQuyen(cboPhanQuyen.getSelectedItem().toString()); 
            nv.setStatus(cboStatus.getSelectedItem().toString()); 
            
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
    
    // Đã xóa hàm xoaNhanVien()
    
    // Giữ nguyên logic đổi mật khẩu
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
            
            // Verify old password
            NhanVien nv = nhanVienDAO.getById(txtId.getText().trim());
            if (nv == null || !nv.getPassword().equals(oldPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu cũ không đúng!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update new password
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
    
    private void timKiem() {
        timKiem(false); // Gọi với showMessage = false (tìm tự động)
    }

    // Phương thức tìm kiếm với tùy chọn hiển thị thông báo
    private void timKiem(boolean showMessage) {
        String keyword = txtTimKiem.getText().trim();
        
        // Nếu không có từ khóa, hiển thị lại toàn bộ dữ liệu
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<NhanVien> allList = nhanVienDAO.getAll();
        List<NhanVien> resultList = new ArrayList<>();
        
        // Chuyển keyword về không dấu, lowercase để so sánh
        String normalizedKeyword = removeAccent(keyword.toLowerCase());
        
        for (NhanVien nv : allList) {
            // Tìm theo mã nhân viên (chính xác, không phân biệt hoa thường)
            if (nv.getIdNV().toLowerCase().contains(keyword.toLowerCase())) {
                resultList.add(nv);
                continue;
            }
            
            // Tìm theo tên (không phân biệt hoa thường, có dấu/không dấu)
            String normalizedName = removeAccent(nv.getTenNV().toLowerCase());
            if (normalizedName.contains(normalizedKeyword)) {
                resultList.add(nv);
            }
        }
        
        // Hiển thị kết quả
        for (NhanVien nv : resultList) {
            tableModel.addRow(new Object[]{
                nv.getIdNV(),
                nv.getTenNV(),
                nv.getUsername(),
                nv.getEmail(),
                nv.getPhanQuyen(),
                nv.getStatus()
            });
        }
        
        // CHỈ hiện thông báo khi showMessage = true (nhấn Enter hoặc nút Tìm)
        if (resultList.isEmpty() && showMessage) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy nhân viên nào khớp với từ khóa: " + keyword, 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Phương thức bỏ dấu tiếng Việt
    private String removeAccent(String s) {
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        return temp.replaceAll("đ", "d").replaceAll("Đ", "D");
    }
    
    // Giữ nguyên logic vô hiệu hóa các trường không cho phép sửa
    private void hienThiThongTin(int row) {
        txtId.setText(table.getValueAt(row, 0).toString());
        txtTen.setText(table.getValueAt(row, 1).toString());
        
        // Vô hiệu hóa Username
        txtUsername.setText(table.getValueAt(row, 2).toString());
        txtUsername.setEditable(false);
        txtUsername.setBackground(new Color(240, 240, 240));
        
        // Vô hiệu hóa Password và Confirm Password
        txtPassword.setText("");
        txtPassword.setEditable(false);
        txtPassword.setBackground(new Color(240, 240, 240));
        
        txtConfirmPassword.setText("");
        txtConfirmPassword.setEditable(false);
        txtConfirmPassword.setBackground(new Color(240, 240, 240));
        
        txtEmail.setText(table.getValueAt(row, 3).toString());
        cboPhanQuyen.setSelectedItem(table.getValueAt(row, 4).toString());
        cboStatus.setSelectedItem(table.getValueAt(row, 5).toString());
    }
    
    // Giữ nguyên logic kích hoạt lại các trường
    private void lamMoi() {
        txtTen.setText("");
        
        // Kích hoạt lại Username
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtUsername.setBackground(Color.WHITE);
        
        // Kích hoạt lại Password
        txtPassword.setText("");
        txtPassword.setEditable(true);
        txtPassword.setBackground(Color.WHITE);
        
        txtConfirmPassword.setText("");
        txtConfirmPassword.setEditable(true);
        txtConfirmPassword.setBackground(Color.WHITE);
        
        txtEmail.setText("");
        txtTimKiem.setText("");
        cboPhanQuyen.setSelectedIndex(0);
        cboStatus.setSelectedIndex(0);
        chkShowPassword.setSelected(false);
        txtPassword.setEchoChar('•');
        txtConfirmPassword.setEchoChar('•');
        table.clearSelection();
        
        // Tự động tạo mã nhân viên mới
        generateNewEmployeeId();
        
        txtTen.requestFocus();
    }
    
    private boolean validateInput() {
        // ... (Không thay đổi)
        if (txtId.getText().trim().isEmpty() || txtTen.getText().trim().isEmpty() ||
            txtUsername.getText().trim().isEmpty() || new String(txtPassword.getPassword()).isEmpty() ||
            new String(txtConfirmPassword.getPassword()).isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String password = new String(txtPassword.getPassword());
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return false;
        }
        
        // Validate email format
        String email = txtEmail.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // Giữ nguyên logic kiểm tra input khi cập nhật
    private boolean validateInputForUpdate() {
        if (txtTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhân viên!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtTen.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }

        // Thêm kiểm tra email hợp lệ
        String email = txtEmail.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        return true;
    }
}