package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import DAO.DAO_Giay;
import DAO.DAO_LoaiGiay;
import DAO.DAO_HangGiay;
import model.Giay;
import model.LoaiGiay;
import model.HangGiay;
import java.util.List;

public class QuanLyGiay extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtTen, txtSize, txtSoLuong, txtGiaBan, txtMoTa, txtTimKiem;
    private JComboBox<String> cboLoaiGiay, cboHangGiay, cboStatus;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private DAO_Giay giayDAO;
    private DAO_LoaiGiay loaiGiayDAO;
    private DAO_HangGiay hangGiayDAO;
    private DecimalFormat df = new DecimalFormat("#,###");
    
    public QuanLyGiay() {
        giayDAO = new DAO_Giay();
        loaiGiayDAO = new DAO_LoaiGiay();
        hangGiayDAO = new DAO_HangGiay();
        
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
        loadComboBoxData();
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        // Tiêu đề ở giữa
        JLabel lblTitle = new JLabel("QUẢN LÝ GIÀY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(52, 73, 94));
        panel.add(lblTitle, BorderLayout.CENTER);

        // Nút quay lại bên trái
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBack.setBackground(new Color(52, 152, 219));
        btnBack.setForeground(Color.RED);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // bỏ viền
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hiệu ứng hover
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBack.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBack.setBackground(new Color(52, 152, 219));
            }
        });

        // Sự kiện click: Quay về MainFrame mà KHÔNG khởi tạo lại
        btnBack.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame instanceof MAINFRAME) {
                ((MAINFRAME) frame).showMainMenuPanel(); // ✅ Quay về menu gốc mà không khởi tạo lại
            }
        });


        panel.add(btnBack, BorderLayout.WEST);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(350, 0));
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
        JLabel lblFormTitle = new JLabel("THÔNG TIN GIÀY");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblFormTitle, gbc);
        gbc.gridwidth = 1;
        
        // Mã giày
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mã giày:"), gbc);
        txtId = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtId, gbc);
        
        // Tên giày
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tên giày:"), gbc);
        txtTen = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtTen, gbc);
        
        // Size
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Size:"), gbc);
        txtSize = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtSize, gbc);
        
        // Số lượng
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Số lượng:"), gbc);
        txtSoLuong = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtSoLuong, gbc);
        
        // Giá bán
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Giá bán:"), gbc);
        txtGiaBan = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtGiaBan, gbc);
        
        // Loại giày
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Loại giày:"), gbc);
        cboLoaiGiay = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cboLoaiGiay, gbc);
        
        // Hãng giày
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Hãng giày:"), gbc);
        cboHangGiay = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(cboHangGiay, gbc);
        
        // Mô tả
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        txtMoTa = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(txtMoTa, gbc);
        
        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        cboStatus = new JComboBox<>(new String[]{"active", "inactive"});
        gbc.gridx = 1;
        formPanel.add(cboStatus, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        btnThem = createStyledButton("Thêm", new Color(46, 204, 113));
        btnSua = createStyledButton("Sửa", new Color(52, 152, 219));
        btnXoa = createStyledButton("Xóa", new Color(231, 76, 60));
        btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        
        panel.add(btnThem);
        panel.add(btnSua);
        panel.add(btnXoa);
        panel.add(btnLamMoi);
        
        // Thêm sự kiện
        btnThem.addActionListener(e -> themGiay());
        btnSua.addActionListener(e -> suaGiay());
        btnXoa.addActionListener(e -> xoaGiay());
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
        
        btnTimKiem = createStyledButton("Tìm", new Color(41, 128, 185));
        btnTimKiem.setPreferredSize(new Dimension(100, 30));
        searchPanel.add(btnTimKiem);
        
        btnTimKiem.addActionListener(e -> timKiem());
        
        // Nhấn Enter để tìm
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
        
        // Bảng dữ liệu
        String[] columns = {"Mã", "Tên giày", "Size", "Số lượng", "Giá bán", 
                           "Loại", "Hãng", "Mô tả", "Trạng thái"};
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
        table.setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);

        
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
        tableModel.setRowCount(0);
        List<Giay> list = giayDAO.getAll();
        
        for (Giay g : list) {
            tableModel.addRow(new Object[]{
                g.getIdGiay(),
                g.getTenGiay(),
                g.getSize(),
                g.getSoLuong(),
                df.format(g.getGiaBan()) + " đ",
                g.getIdLoaiGiay(),
                g.getIdHangGiay(),
                g.getMoTa(),
                g.getStatus()
            });
        }
    }
    
    private void loadComboBoxData() {
        // Load loại giày
        cboLoaiGiay.removeAllItems();
        List<LoaiGiay> listLoai = loaiGiayDAO.getAll();
        for (LoaiGiay lg : listLoai) {
            cboLoaiGiay.addItem(lg.getIdLoaiGiay() + " - " + lg.getTenLoaiGiay());
        }
        
        // Load hãng giày
        cboHangGiay.removeAllItems();
        List<HangGiay> listHang = hangGiayDAO.getAll();
        for (HangGiay hg : listHang) {
            cboHangGiay.addItem(hg.getIdHangGiay() + " - " + hg.getTenHangGiay());
        }
    }
    
    private void themGiay() {
        if (!validateInput()) return;
        
        try {
            Giay g = new Giay();
            g.setIdGiay(txtId.getText().trim());
            g.setTenGiay(txtTen.getText().trim());
            g.setSize(Float.parseFloat(txtSize.getText().trim()));
            g.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
            g.setGiaBan(Float.parseFloat(txtGiaBan.getText().trim()));
            g.setMoTa(txtMoTa.getText().trim());
            g.setHinhAnh(""); // Có thể thêm chức năng upload ảnh sau
            
            String loaiGiay = cboLoaiGiay.getSelectedItem().toString();
            g.setIdLoaiGiay(loaiGiay.split(" - ")[0]);
            
            String hangGiay = cboHangGiay.getSelectedItem().toString();
            g.setIdHangGiay(hangGiay.split(" - ")[0]);
            
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
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giày cần sửa!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInput()) return;
        
        try {
            Giay g = new Giay();
            g.setIdGiay(txtId.getText().trim());
            g.setTenGiay(txtTen.getText().trim());
            g.setSize(Float.parseFloat(txtSize.getText().trim()));
            g.setSoLuong(Integer.parseInt(txtSoLuong.getText().trim()));
            g.setGiaBan(Float.parseFloat(txtGiaBan.getText().trim()));
            g.setMoTa(txtMoTa.getText().trim());
            g.setHinhAnh("");
            
            String loaiGiay = cboLoaiGiay.getSelectedItem().toString();
            g.setIdLoaiGiay(loaiGiay.split(" - ")[0]);
            
            String hangGiay = cboHangGiay.getSelectedItem().toString();
            g.setIdHangGiay(hangGiay.split(" - ")[0]);
            
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
    
    private void xoaGiay() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giày cần xóa!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa giày này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            String id = table.getValueAt(row, 0).toString();
            
            if (giayDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "Xóa giày thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa giày thất bại!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void timKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();

        // Nếu trống -> load lại toàn bộ
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);

        // Lấy toàn bộ danh sách từ DAO
        List<Giay> list = giayDAO.getAll();

        for (Giay g : list) {
            String id = g.getIdGiay().toLowerCase();
            String ten = g.getTenGiay().toLowerCase();

            // So sánh keyword với id hoặc tên
            if (id.contains(keyword) || ten.contains(keyword)) {
                tableModel.addRow(new Object[]{
                    g.getIdGiay(),
                    g.getTenGiay(),
                    g.getSize(),
                    g.getSoLuong(),
                    df.format(g.getGiaBan()) + " đ",
                    g.getIdLoaiGiay(),
                    g.getIdHangGiay(),
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

    
    private void hienThiThongTin(int row) {
        txtId.setText(table.getValueAt(row, 0).toString());
        txtTen.setText(table.getValueAt(row, 1).toString());
        txtSize.setText(table.getValueAt(row, 2).toString());
        txtSoLuong.setText(table.getValueAt(row, 3).toString());
        
        String giaBan = table.getValueAt(row, 4).toString();
        giaBan = giaBan.replace(",", "").replace(" đ", "");
        txtGiaBan.setText(giaBan);
        
        txtMoTa.setText(table.getValueAt(row, 7).toString());
        cboStatus.setSelectedItem(table.getValueAt(row, 8).toString());
        
        // Set combobox
        String idLoai = table.getValueAt(row, 5).toString();
        for (int i = 0; i < cboLoaiGiay.getItemCount(); i++) {
            if (cboLoaiGiay.getItemAt(i).startsWith(idLoai)) {
                cboLoaiGiay.setSelectedIndex(i);
                break;
            }
        }
        
        String idHang = table.getValueAt(row, 6).toString();
        for (int i = 0; i < cboHangGiay.getItemCount(); i++) {
            if (cboHangGiay.getItemAt(i).startsWith(idHang)) {
                cboHangGiay.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void lamMoi() {
        txtId.setText("");
        txtTen.setText("");
        txtSize.setText("");
        txtSoLuong.setText("");
        txtGiaBan.setText("");
        txtMoTa.setText("");
        txtTimKiem.setText("");
        cboStatus.setSelectedIndex(0);
        if (cboLoaiGiay.getItemCount() > 0) cboLoaiGiay.setSelectedIndex(0);
        if (cboHangGiay.getItemCount() > 0) cboHangGiay.setSelectedIndex(0);
        table.clearSelection();
        txtId.requestFocus();
    }
    
    private boolean validateInput() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã giày!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtId.requestFocus();
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
        
        return true;
    }
}