package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import DAO.DAO_NhanVien;
import DAO.DAO_ChiTietPhanQuyen;
import model.NhanVien;
import model.ChiTietPhanQuyen;
import java.util.List;

public class LoginForm extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit;
    private DAO_NhanVien nhanVienDAO;
    private DAO_ChiTietPhanQuyen chiTietPQDAO;

    public LoginForm() {
        nhanVienDAO = new DAO_NhanVien();
        chiTietPQDAO = new DAO_ChiTietPhanQuyen();
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Đăng Nhập Hệ Thống");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel);

        // === PANEL TRÁI: ẢNH NỀN + LOGO ===
        JPanel leftPanel = new JPanel() {
            private final Image bgImage = new ImageIcon("images/bb.jpg").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        leftPanel.setLayout(null);

        // LOGO (góc trên trái)
        JLabel logo = new JLabel(new ImageIcon("images"));
        logo.setBounds(25, 25, 120, 60);
        leftPanel.add(logo);
        mainPanel.add(leftPanel);

        // === PANEL PHẢI: FORM ===
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        mainPanel.add(rightPanel);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(360, 340));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel lblTitle = new JLabel("Đăng nhập");
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        lblTitle.setForeground(new Color(34, 139, 34)); // cyan
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Trường nhập
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtUsername.setBorder(BorderFactory.createTitledBorder("Tên đăng nhập"));
        card.add(txtUsername);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtPassword.setBorder(BorderFactory.createTitledBorder("Mật khẩu"));
        card.add(txtPassword);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        // === NÚT CÙNG HÀNG, LUÔN HIỂN THỊ ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        btnLogin = new JButton("Đăng nhập");
        styleButton(btnLogin, new Color(34, 139, 34));
        buttonPanel.add(btnLogin);

        btnExit = new JButton("Thoát");
        styleButton(btnExit, new Color(34, 139, 34));
        buttonPanel.add(btnExit);

        card.add(buttonPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        rightPanel.add(card, gbc);

        // === SỰ KIỆN ===
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    LoginForm.this,
                    "Bạn có chắc chắn muốn thoát?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) System.exit(0);
        });
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        });
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(130, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        try {
            NhanVien nv = nhanVienDAO.login(username, password);
            if (nv != null) {
                if ("Hoạt động".equalsIgnoreCase(nv.getStatus()) || "active".equalsIgnoreCase(nv.getStatus())) {
                    List<ChiTietPhanQuyen> permissions = chiTietPQDAO.getByIdPQ(nv.getIdPQ());
                    JOptionPane.showMessageDialog(this, "Đăng nhập thành công!\nXin chào: " + nv.getTenNV());
                    this.dispose();
                    SwingUtilities.invokeLater(() -> new MAINFRAME(nv, permissions).setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(this, "Tài khoản đã bị khóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtUsername.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database:\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
