package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.NhanVien;

public class MAINFRAME extends JFrame {
    private NhanVien currentUser;
    private JPanel mainMenuPanel; // menu chính
    private JPanel contentPanel;  // chứa tất cả các panel
    private CardLayout cardLayout;
    private JLabel lblWelcome;
    private JMenuBar menuBar;

    public MAINFRAME(NhanVien user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Hệ Thống Quản Lý Cửa Hàng Giày");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ============= SỬ DỤNG CARDLAYOUT =============
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        // ============= TẠO MENU BAR =============
        createMenuBar();

        // ============= PANEL MENU CHÍNH =============
        mainMenuPanel = createMainMenuPanel();
        contentPanel.add(mainMenuPanel, "menu");

        // ============= PANEL QUẢN LÝ GIÀY =============
        contentPanel.add(new QuanLyGiay(), "giay");

        // Hiển thị menu chính đầu tiên
        showMainMenuPanel();
    }

    private JPanel createMainMenuPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG GIÀY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Bên phải: lời chào + nút đăng xuất
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(new Color(52, 73, 94));

        lblWelcome = new JLabel("Xin chào: " + currentUser.getTenNV() +
                " (" + currentUser.getPhanQuyen() + ")");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(Color.WHITE); // nền trắng
        btnLogout.setForeground(Color.RED);   // chữ đỏ
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // bỏ viền
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> handleLogout());

        rightPanel.add(lblWelcome);
        rightPanel.add(btnLogout);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center
        JPanel centerPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        centerPanel.setBackground(new Color(236, 240, 241));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        centerPanel.add(createDashboardCard("Quản Lý Nhân Viên", new Color(52, 152, 219), null));
        centerPanel.add(createDashboardCard("Quản Lý Khách Hàng", new Color(46, 204, 113), null));
        centerPanel.add(createDashboardCard("Quản Lý Giày", new Color(155, 89, 182), "giay"));
        centerPanel.add(createDashboardCard("Quản Lý Hóa Đơn", new Color(230, 126, 34), null));
        centerPanel.add(createDashboardCard("Quản Lý Nhập Kho", new Color(231, 76, 60), null));
        centerPanel.add(createDashboardCard("Thống Kê Báo Cáo", new Color(26, 188, 156), null));

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        footerPanel.setPreferredSize(new Dimension(0, 30));

        JLabel lblFooter = new JLabel("© 2024 Shoe Store Management System - Version 1.0");
        lblFooter.setForeground(Color.WHITE);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerPanel.add(lblFooter);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createDashboardCard(String title, Color color, String action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(color);

        card.add(lblTitle, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if ("giay".equals(action)) {
                    showQuanLyGiayPanel();
                } else {
                    JOptionPane.showMessageDialog(MAINFRAME.this,
                            "Chức năng " + title + " đang được phát triển!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        return card;
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(44, 62, 80));

        JMenu menuSystem = new JMenu("Hệ Thống");
        menuSystem.setForeground(Color.WHITE);
        menuSystem.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JMenuItem itemLogout = new JMenuItem("Đăng Xuất");
        JMenuItem itemExit = new JMenuItem("Thoát");

        itemLogout.addActionListener(e -> handleLogout());
        itemExit.addActionListener(e -> handleExit());

        menuSystem.add(itemLogout);
        menuSystem.add(itemExit);
        menuBar.add(menuSystem);

        setJMenuBar(menuBar);
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginForm().setVisible(true);
        }
    }

    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn thoát chương trình?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // 👉 Chuyển về menu chính
    public void showMainMenuPanel() {
        cardLayout.show(contentPanel, "menu");
    }

    // 👉 Hiển thị panel Quản lý giày
    public void showQuanLyGiayPanel() {
        cardLayout.show(contentPanel, "giay");
    }
}
