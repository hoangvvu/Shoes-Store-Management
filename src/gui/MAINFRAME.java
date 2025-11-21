package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import model.NhanVien;
import model.ChiTietPhanQuyen; // Import model quyền
import java.util.List; // Import List
import java.util.HashMap; // Import HashMap
import java.util.Map; // Import Map
import javax.swing.border.Border; // Import để sử dụng Border
import java.net.URL; // Import để lấy tài nguyên hình ảnh
import java.io.File; 

public class MAINFRAME extends JFrame {
    private NhanVien currentUser;
    private List<ChiTietPhanQuyen> permissions; // Lưu danh sách quyền
    private Map<String, ChiTietPhanQuyen> permissionMap; // Map để truy cập quyền nhanh

    private JPanel mainMenuPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel lblWelcome;
    private JMenuBar menuBar;
    
    // Hằng số cho mã chức năng
    private static final String CN_NHANVIEN = "CN001";
    private static final String CN_GIAY = "CN002";
    private static final String CN_HOADON = "CN003";
    private static final String CN_KHO = "CN004";
    private static final String CN_KHACHHANG = "CN005";
    private static final String CN_THONGKE = "CN006";

    // Constructor
    public MAINFRAME(NhanVien user, List<ChiTietPhanQuyen> permissions) {
        this.currentUser = user;
        this.permissions = permissions;
        
        this.permissionMap = new HashMap<>();
        if (permissions != null) {
            for (ChiTietPhanQuyen ct : permissions) {
                this.permissionMap.put(ct.getIdCN(), ct);
            }
        }
        
        initComponents();
        setLocationRelativeTo(null);
    }

    // Hàm helper tra cứu quyền
    private ChiTietPhanQuyen getPermission(String functionId) {
        return this.permissionMap.get(functionId);
    }
    
    // Hàm helper kiểm tra quyền xem
    private boolean canView(String functionId) {
        ChiTietPhanQuyen p = getPermission(functionId);
        return (p != null && p.isDuocXem());
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
        
        boolean isAdmin = "PQ001".equalsIgnoreCase(currentUser.getIdPQ());

        contentPanel.add(new QuanLyNhanVien(getPermission(CN_NHANVIEN), isAdmin), "nhanvien");
        contentPanel.add(new QuanLyKhachHang(getPermission(CN_KHACHHANG)), "khachhang");
        contentPanel.add(new QuanLyGiay(getPermission(CN_GIAY)), "giay");
        contentPanel.add(new QuanLyHoaDon(currentUser), "hoadon");
        contentPanel.add(new QuanLyNhapKho(currentUser), "nhapkho");
        contentPanel.add(new QuanLyThongKe(currentUser), "thongke");

        showMainMenuPanel();
    }

    private JPanel createMainMenuPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241)); 

        // === 1. HEADER PANEL ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94)); 
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ CỬA HÀNG GIÀY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28)); 
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false); 
        
        lblWelcome = new JLabel("Xin chào: " + currentUser.getTenNV());
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(Color.WHITE);
        
        JButton btnLogout = new JButton("Đăng Xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(new Color(231, 76, 60)); 
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> handleLogout());
        
        rightPanel.add(lblWelcome);
        rightPanel.add(btnLogout);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);


        // === 2. CENTER PANEL (CHỨA CÁC CARD) ===
        JPanel centerPanel = new JPanel(new GridLayout(2, 3, 40, 40)); 
        centerPanel.setBackground(new Color(236, 240, 241));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); 

        // Dùng đường dẫn /images/ 
        centerPanel.add(createDashboardCard("Quản Lý Nhân Viên", new Color(52, 152, 219), "nhanvien", "/images/nhanvien.png"));
        centerPanel.add(createDashboardCard("Quản Lý Khách Hàng", new Color(46, 204, 113), "khachhang", "/images/khachhang.png"));
        centerPanel.add(createDashboardCard("Quản Lý Giày", new Color(155, 89, 182), "giay", "/images/giay.png"));
        centerPanel.add(createDashboardCard("Quản Lý Hóa Đơn", new Color(230, 126, 34), "hoadon", "/images/hoadon.png"));
        centerPanel.add(createDashboardCard("Quản Lý Nhập Kho", new Color(231, 76, 60), "nhapkho", "/images/nhapkho.png"));
        centerPanel.add(createDashboardCard("Thống Kê Báo Cáo", new Color(26, 188, 156), "thongke", "/images/thongke.png"));

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // === 3. FOOTER PANEL ===
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(52, 73, 94));
        footerPanel.setPreferredSize(new Dimension(0, 30));
        JLabel lblFooter = new JLabel("© 2025 Shoe Store Management System - Version 1.0");
        lblFooter.setForeground(Color.WHITE);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerPanel.add(lblFooter);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createDashboardCard(String title, Color color, String action, String imagePath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Tạo hiệu ứng nổi (shadow) nhẹ
        Border shadowBorder = BorderFactory.createLineBorder(new Color(220, 220, 220), 2);
        Border marginBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        Border defaultBorder = BorderFactory.createCompoundBorder(marginBorder, shadowBorder);
        
        // Hiệu ứng hover (nâng lên)
        Border hoverBorderCompound = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(3, 3, 3, 3), 
            BorderFactory.createLineBorder(color, 2) 
        );

        card.setBorder(defaultBorder);

        // 1. Panel Icon (TOP)
        JPanel iconPanel = new JPanel(new GridBagLayout()); 
        iconPanel.setBackground(color); 
        iconPanel.setPreferredSize(new Dimension(0, 120)); 
        
        // === SỬA LẠI LOGIC TẢI ẢNH: Dùng đường dẫn tệp tuyệt đối (Absolute File Path) ===
        try {
            // Lấy thư mục gốc của dự án (Project Root)
            String projectRoot = System.getProperty("user.dir");
            
            // Xây dựng đường dẫn tuyệt đối đến tệp ảnh: projectRoot + /images/ + tenfile.png
            // Sử dụng File.separator để tương thích với các hệ điều hành (Windows/Linux/macOS)
            String relativeImagePath = imagePath.substring(1).replace("/", File.separator);
            String absolutePath = projectRoot + File.separator + relativeImagePath;

            ImageIcon icon = new ImageIcon(absolutePath);
            
            // Kiểm tra xem ảnh có được tải thành công không (iconWidth > 0)
            if (icon.getIconWidth() > 0) {
                // Đổi kích thước ảnh về 80x80
                Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH); 
                JLabel lblIcon = new JLabel(new ImageIcon(img));
                iconPanel.add(lblIcon);
            } else {
                // Fallback: Nếu không tìm thấy ảnh, hiển thị chữ cái đầu
                String fallbackText = title.substring(0, 1).toUpperCase(); 
                JLabel lblIcon = new JLabel(fallbackText); 
                lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 52));
                lblIcon.setForeground(Color.WHITE);
                iconPanel.add(lblIcon);
                // In ra lỗi để người dùng dễ dàng kiểm tra
                System.err.println("❌ Lỗi: Không tìm thấy tệp icon tại đường dẫn: " + absolutePath + ". Đã chuyển sang Fallback.");
            }
        } catch (Exception e) {
            JLabel lblIcon = new JLabel("Lỗi!"); 
            lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 30));
            lblIcon.setForeground(Color.WHITE);
            iconPanel.add(lblIcon);
            e.printStackTrace();
        }
        // =========================================================================
        
        card.add(iconPanel, BorderLayout.NORTH);

        // 2. Panel Tiêu đề (CENTER)
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblTitle.setForeground(new Color(52, 73, 94)); 
        titlePanel.add(lblTitle);

        card.add(titlePanel, BorderLayout.CENTER);

        // 3. Mouse Listener 
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(hoverBorderCompound); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(defaultBorder); 
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
                        case "thongke":
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
        JMenuItem itemThongKe = new JMenuItem("Thống Kê Báo Cáo");

        itemNhanVien.addActionListener(e -> showQuanLyNhanVienPanel());
        itemKhachHang.addActionListener(e -> showQuanLyKhachHangPanel());
        itemGiay.addActionListener(e -> showQuanLyGiayPanel());
        itemHoaDon.addActionListener(e -> showQuanLyHoaDonPanel());
        itemNhapKho.addActionListener(e -> showQuanLyNhapKhoPanel());
        itemThongKe.addActionListener(e -> showQuanLyThongKePanel());

        menuManagement.add(itemNhanVien);
        menuManagement.add(itemKhachHang);
        menuManagement.add(itemGiay);
        menuManagement.add(itemHoaDon);
        menuManagement.add(itemNhapKho);
        menuManagement.add(itemThongKe);

        menuBar.add(menuSystem);
        menuBar.add(menuManagement); 
    }

    // ========== ĐÂY LÀ PHẦN ĐƯỢC SỬA ==========
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            this.dispose(); // Đóng MAINFRAME hiện tại
            try {
                // Mở lại form đăng nhập
                // (Giả định class LoginForm() nằm cùng package 'gui')
                new LoginForm().setVisible(true); 
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi mở lại form Đăng nhập: " + ex.getMessage());
            }
        }
    }
    // ===========================================

    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn thoát chương trình?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) System.exit(0);
    }
    private void showNotImplemented(String featureName) {
        JOptionPane.showMessageDialog(this,
                "Chức năng " + featureName + " đang được phát triển!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAccessDeniedWarning(String featureName) {
        JOptionPane.showMessageDialog(this,
                "Bạn không có quyền truy cập chức năng: " + featureName,
                "Lỗi Phân Quyền",
                JOptionPane.WARNING_MESSAGE);
    }
    
    public void showMainMenuPanel() { cardLayout.show(contentPanel, "menu"); }
    
    public void showQuanLyNhanVienPanel() { 
        if (canView(CN_NHANVIEN)) {
            cardLayout.show(contentPanel, "nhanvien");
        } else {
            showAccessDeniedWarning("Quản Lý Nhân Viên");
        }
    }
    
    public void showQuanLyKhachHangPanel() { 
        if (canView(CN_KHACHHANG)) {
            cardLayout.show(contentPanel, "khachhang");
        } else {
            showAccessDeniedWarning("Quản Lý Khách Hàng");
        }
    }
    
    public void showQuanLyGiayPanel() { 
        if (canView(CN_GIAY)) {
            cardLayout.show(contentPanel, "giay");
        } else {
            showAccessDeniedWarning("Quản Lý Giày");
        }
    }
    
    public void showQuanLyHoaDonPanel() { 
        if (canView(CN_HOADON)) {
            cardLayout.show(contentPanel, "hoadon");
        } else {
            showAccessDeniedWarning("Quản Lý Hóa Đơn");
        }
    }
    
    public void showQuanLyNhapKhoPanel() { 
        if (canView(CN_KHO)) {
            cardLayout.show(contentPanel, "nhapkho");
        } else {
            showAccessDeniedWarning("Quản Lý Nhập Kho");
        }
    }
    
    public void showQuanLyThongKePanel() { 
        if (canView(CN_THONGKE)) {
            cardLayout.show(contentPanel, "thongke");
        } else {
            showAccessDeniedWarning("Thống Kê Báo Cáo");
        }
    }
}