package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.NhanVien;

public class MAINFRAME extends JFrame {
    private NhanVien currentUser;
    private JPanel mainMenuPanel;
    private JPanel contentPanel;
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

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel, BorderLayout.CENTER);

        createMenuBar();

        mainMenuPanel = createMainMenuPanel();
        contentPanel.add(mainMenuPanel, "menu");

        // Thêm các panel quản lý - TRUYỀN currentUser nếu cần
        contentPanel.add(new QuanLyNhanVien(), "nhanvien");
        contentPanel.add(new QuanLyKhachHang(), "khachhang");
        contentPanel.add(new QuanLyGiay(), "giay");
        contentPanel.add(new QuanLyHoaDon(currentUser), "hoadon");
        contentPanel.add(new QuanLyNhapKho(currentUser), "nhapkho");
        contentPanel.add(new QuanLyThongKe(), "thongke");

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

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setBackground(new Color(52, 73, 94));

        lblWelcome = new JLabel("Xin chào: " + currentUser.getTenNV() +
                " (" + currentUser.getPhanQuyen() + ")");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(Color.RED);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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

        centerPanel.add(createDashboardCard("Quản Lý Nhân Viên", new Color(52, 152, 219), "nhanvien"));
        centerPanel.add(createDashboardCard("Quản Lý Khách Hàng", new Color(46, 204, 113), "khachhang"));
        centerPanel.add(createDashboardCard("Quản Lý Giày", new Color(155, 89, 182), "giay"));
        centerPanel.add(createDashboardCard("Quản Lý Hóa Đơn", new Color(230, 126, 34), "hoadon"));
        centerPanel.add(createDashboardCard("Quản Lý Nhập Kho", new Color(231, 76, 60), "nhapkho"));
        centerPanel.add(createDashboardCard("Thống Kê Báo Cáo", new Color(26, 188, 156), "thongke")); // ✅ SỬA LẠI

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
                if (action != null) {
                    switch (action) {
                        case "nhanvien":
                            showQuanLyNhanVienPanel(); break;
                        case "khachhang":
                            showQuanLyKhachHangPanel(); break;
                        case "giay":
                            showQuanLyGiayPanel(); break;
                        case "hoadon":
                            showQuanLyHoaDonPanel(); break;
                        case "nhapkho":
                            showQuanLyNhapKhoPanel(); break;
                        case "thongke": // ✅ THÊM MỚI
                            showQuanLyThongKePanel(); break;
                        default:
                            showNotImplemented(title); break;
                    }
                } else showNotImplemented(title);
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

        JMenuItem itemHome = new JMenuItem("Trang Chủ");
        JMenuItem itemLogout = new JMenuItem("Đăng Xuất");
        JMenuItem itemExit = new JMenuItem("Thoát");

        itemHome.addActionListener(e -> showMainMenuPanel());
        itemLogout.addActionListener(e -> handleLogout());
        itemExit.addActionListener(e -> handleExit());

        menuSystem.add(itemHome);
        menuSystem.addSeparator();
        menuSystem.add(itemLogout);
        menuSystem.add(itemExit);

        JMenu menuManagement = new JMenu("Quản Lý");
        menuManagement.setForeground(Color.WHITE);
        menuManagement.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JMenuItem itemNhanVien = new JMenuItem("Quản Lý Nhân Viên");
        JMenuItem itemKhachHang = new JMenuItem("Quản Lý Khách Hàng");
        JMenuItem itemGiay = new JMenuItem("Quản Lý Giày");
        JMenuItem itemHoaDon = new JMenuItem("Quản Lý Hóa Đơn");
        JMenuItem itemNhapKho = new JMenuItem("Quản Lý Nhập Kho");
        JMenuItem itemThongKe = new JMenuItem("Thống Kê Báo Cáo"); // ✅ THÊM MỚI

        itemNhanVien.addActionListener(e -> showQuanLyNhanVienPanel());
        itemKhachHang.addActionListener(e -> showQuanLyKhachHangPanel());
        itemGiay.addActionListener(e -> showQuanLyGiayPanel());
        itemHoaDon.addActionListener(e -> showQuanLyHoaDonPanel());
        itemNhapKho.addActionListener(e -> showQuanLyNhapKhoPanel());
        itemThongKe.addActionListener(e -> showQuanLyThongKePanel()); // ✅ CẬP NHẬT

        menuManagement.add(itemNhanVien);
        menuManagement.add(itemKhachHang);
        menuManagement.add(itemGiay);
        menuManagement.add(itemHoaDon);
        menuManagement.add(itemNhapKho);
        menuManagement.add(itemThongKe);

        menuBar.add(menuSystem);
        menuBar.add(menuManagement);

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
        if (choice == JOptionPane.YES_OPTION) System.exit(0);
    }

    private void showNotImplemented(String featureName) {
        JOptionPane.showMessageDialog(this,
                "Chức năng " + featureName + " đang được phát triển!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMainMenuPanel() { cardLayout.show(contentPanel, "menu"); }
    public void showQuanLyNhanVienPanel() { cardLayout.show(contentPanel, "nhanvien"); }
    public void showQuanLyKhachHangPanel() { cardLayout.show(contentPanel, "khachhang"); }
    public void showQuanLyGiayPanel() { cardLayout.show(contentPanel, "giay"); }
    public void showQuanLyHoaDonPanel() { cardLayout.show(contentPanel, "hoadon"); }
    public void showQuanLyNhapKhoPanel() { cardLayout.show(contentPanel, "nhapkho"); }
    public void showQuanLyThongKePanel() { cardLayout.show(contentPanel, "thongke"); } // ✅ THÊM MỚI
}
