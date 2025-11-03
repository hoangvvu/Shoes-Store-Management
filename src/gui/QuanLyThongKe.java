package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import DAO.*;
import model.*;
import com.toedter.calendar.JDateChooser;

// Import thư viện JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class QuanLyThongKe extends JPanel {
    private JTable tableThongKe;
    private DefaultTableModel modelThongKe;
    private JLabel lblTongDoanhThu, lblTongSoLuong, lblTongDonHang;
    private JDateChooser dateFrom, dateTo;
    
    // DAO objects
    private DAO_HoaDon hoaDonDAO;
    private DAO_ChiTietHoaDon chiTietHDDAO;
    private DAO_NhapKho nhapKhoDAO;
    private DAO_ChiTietNhapKho chiTietNKDAO;
    private DAO_Giay giayDAO;
    private DAO_KhachHang khachHangDAO;

    private DecimalFormat df = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy");

    private NhanVien currentUser;

    // ==========================================================
    // == CÁC BIẾN MỚI CHO CARDLAYOUT VÀ DASHBOARD
    // ==========================================================
    private JPanel centerContentPanel; // Panel chính ở giữa dùng CardLayout
    private CardLayout centerCardLayout;
    private JScrollPane tableScrollPane; // Bảng chi tiết (dùng chung)
    private JPanel dashboardPanel; // Panel cho 4 biểu đồ tổng quan
    private int currentViewIndex = 0; // 0 = Dashboard, 1 = Doanh thu ngày, ...

    // --- CÁC PANEL CHI TIẾT MỚI (CHỨA BIỂU ĐỒ + BẢNG) ---
    private JPanel pnDetailDoanhThuNgay;
    private JPanel pnDetailDoanhThuThang;
    private JPanel pnDetailSanPhamBanChay;
    private JPanel pnDetailNhapHang;
    private JPanel pnDetailTopKhachHang;

    // --- CÁC HẰNG SỐ CHO CARDLAYOUT ---
    private static final String VIEW_DASHBOARD = "DASHBOARD";
    private static final String VIEW_DT_NGAY = "VIEW_DT_NGAY";
    private static final String VIEW_DT_THANG = "VIEW_DT_THANG";
    private static final String VIEW_SP_CHAY = "VIEW_SP_CHAY";
    private static final String VIEW_NHAP_HANG = "VIEW_NHAP_HANG";
    private static final String VIEW_TOP_KH = "VIEW_TOP_KH";
    // ==========================================================


    public QuanLyThongKe() {
        hoaDonDAO = new DAO_HoaDon();
        chiTietHDDAO = new DAO_ChiTietHoaDon();
        nhapKhoDAO = new DAO_NhapKho();
        chiTietNKDAO = new DAO_ChiTietNhapKho();
        giayDAO = new DAO_Giay();
        khachHangDAO = new DAO_KhachHang();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(236, 240, 241));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);

        // Tải dữ liệu dashboard khi vừa mở
        SwingUtilities.invokeLater(() -> {
            hienThiDashboard();
        });
    }

    public QuanLyThongKe(NhanVien user) {
        this();
        this.currentUser = user;
    }

    // ... (createTitlePanel, createMainPanel, createMenuPanel, createMenuButton, createFilterPanel, createSummaryCard, createStyledButton giữ nguyên) ...
    // ... (Bạn có thể copy các hàm này từ code trước) ...
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));

        JLabel lblTitle = new JLabel("THỐNG KÊ - BÁO CÁO", SwingConstants.CENTER);
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
        panel.add(createMenuPanel(), BorderLayout.WEST);
        panel.add(createContentPanel(), BorderLayout.CENTER);
        return panel;
    }

	private JPanel createMenuPanel() {
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	    panel.setBackground(Color.WHITE);
	    panel.setBorder(BorderFactory.createCompoundBorder(
	        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
	        BorderFactory.createEmptyBorder(15, 15, 15, 15)
	    ));
	    panel.setPreferredSize(new Dimension(250, 0));
	
	    JLabel lblMenu = new JLabel("DANH MỤC THỐNG KÊ");
	    lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 14));
	    lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
	    panel.add(lblMenu);
	    panel.add(Box.createRigidArea(new Dimension(0, 20)));
	
	    String[] menuItems = {
	        "Tổng quan",
	        "Doanh thu theo ngày",
	        "Doanh thu theo tháng",
	        "Sản phẩm bán chạy",
	        "Nhập hàng",
	        "Top khách hàng",
	        "Báo cáo tổng hợp"
	    };
	
	    Color[] colors = {
	        new Color(26, 188, 156),  // Teal
	        new Color(46, 204, 113),  // Green
	        new Color(52, 152, 219),  // Blue
	        new Color(241, 196, 15),  // Yellow
	        new Color(230, 126, 34),  // Orange
	        new Color(155, 89, 182),  // Purple
	        new Color(231, 76, 60)    // Red
	    };
	
	    for (int i = 0; i < menuItems.length; i++) {
	        JButton btn = createMenuButton(menuItems[i], colors[i]);
	        final int index = i;
	        btn.addActionListener(e -> xuLyChucNang(index));
	        panel.add(btn);
	        panel.add(Box.createRigidArea(new Dimension(0, 10)));
	    }
	
	    return panel;
	}

	private JButton createMenuButton(String text, Color bgColor) {
	    JButton btn = new JButton(text);
	    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
	    btn.setBackground(bgColor);
	    btn.setForeground(Color.WHITE);
	    btn.setFocusPainted(false);
	    btn.setBorderPainted(false);
	    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    btn.setMaximumSize(new Dimension(220, 45));
	    btn.setAlignmentX(Component.CENTER_ALIGNMENT);
	
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

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        panel.add(createFilterPanel(), BorderLayout.NORTH);
        panel.add(createStatsPanel(), BorderLayout.CENTER);
        return panel;
    }

	private JPanel createFilterPanel() {
	    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
	    panel.setBackground(Color.WHITE);
	    panel.setBorder(BorderFactory.createTitledBorder("Bộ lọc"));
	
	    panel.add(new JLabel("Từ ngày:"));
	    dateFrom = new JDateChooser();
	    dateFrom.setDateFormatString("dd/MM/yyyy");
	    dateFrom.setPreferredSize(new Dimension(130, 30));
	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DAY_OF_MONTH, -30); // 30 ngày trước
	    dateFrom.setDate(cal.getTime());
	    panel.add(dateFrom);
	
	    panel.add(new JLabel("Đến ngày:"));
	    dateTo = new JDateChooser();
	    dateTo.setDateFormatString("dd/MM/yyyy");
	    dateTo.setPreferredSize(new Dimension(130, 30));
	    dateTo.setDate(new Date());
	    panel.add(dateTo);
	
	    JButton btnLoc = createStyledButton("Lọc dữ liệu", new Color(52, 152, 219));
	    btnLoc.addActionListener(e -> locDuLieu());
	    panel.add(btnLoc);
	
	    JButton btnXuatExcel = createStyledButton("Xuất Excel", new Color(46, 204, 113));
	    btnXuatExcel.addActionListener(e -> xuatBaoCao());
	    panel.add(btnXuatExcel);
	
	    return panel;
	}

	private JPanel createSummaryCard(String title, String value, Color color, String labelName) {
	    JPanel card = new JPanel(new BorderLayout(10, 10));
	    card.setBackground(color);
	    card.setBorder(BorderFactory.createCompoundBorder(
	        BorderFactory.createLineBorder(color.darker(), 2),
	        BorderFactory.createEmptyBorder(15, 15, 15, 15)
	    ));
	
	    JLabel lblTitle = new JLabel(title);
	    lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
	    lblTitle.setForeground(Color.WHITE);
	    card.add(lblTitle, BorderLayout.NORTH);
	
	    JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
	    lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
	    lblValue.setForeground(Color.WHITE);
	    card.add(lblValue, BorderLayout.CENTER);
	
	    if (labelName.equals("lblTongDoanhThu")) lblTongDoanhThu = lblValue;
	    else if (labelName.equals("lblTongDonHang")) lblTongDonHang = lblValue;
	    else if (labelName.equals("lblTongSoLuong")) lblTongSoLuong = lblValue;
	
	    return card;
	}

	private JButton createStyledButton(String text, Color bgColor) {
	    JButton btn = new JButton(text);
	    btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
	    btn.setBackground(bgColor);
	    btn.setForeground(Color.WHITE);
	    btn.setFocusPainted(false);
	    btn.setBorderPainted(false);
	    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
	    btn.setPreferredSize(new Dimension(120, 35));
	
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


    /**
     * NÂNG CẤP LỚN:
     * createStatsPanel giờ đây sẽ tạo ra một CardLayout với nhiều thẻ:
     * 1. Thẻ Dashboard (chứa 4 biểu đồ)
     * 2. Thẻ Chi tiết (5 thẻ, mỗi thẻ chứa 1 biểu đồ + 1 bảng JTable)
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // 1. Summary cards (Giữ nguyên)
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        summaryPanel.add(createSummaryCard("Tổng doanh thu", "0 đ", new Color(46, 204, 113), "lblTongDoanhThu"));
        summaryPanel.add(createSummaryCard("Tổng đơn hàng", "0", new Color(52, 152, 219), "lblTongDonHang"));
        summaryPanel.add(createSummaryCard("Sản phẩm bán", "0", new Color(241, 196, 15), "lblTongSoLuong"));
        panel.add(summaryPanel, BorderLayout.NORTH);

        // 2. Tạo JTable và JScrollPane (sẽ được dùng chung)
        String[] columns = {"STT", "Thông tin", "Giá trị"};
        modelThongKe = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableThongKe = new JTable(modelThongKe);
        tableThongKe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableThongKe.setRowHeight(30);
        tableThongKe.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableThongKe.getTableHeader().setBackground(new Color(52, 73, 94));
        tableThongKe.getTableHeader().setForeground(Color.BLACK);
        tableScrollPane = new JScrollPane(tableThongKe); // Lưu vào biến thành viên

        // 3. Tạo Panel chứa CardLayout
        centerCardLayout = new CardLayout();
        centerContentPanel = new JPanel(centerCardLayout);

        // 4. Tạo các panel "con" và thêm vào CardLayout
        
        // 4a. Dashboard
        dashboardPanel = createDashboardPanel(); 
        centerContentPanel.add(dashboardPanel, VIEW_DASHBOARD);

        // 4b. Các panel chi tiết (mỗi panel có BorderLayout để chứa Chart + Table)
        pnDetailDoanhThuNgay = new JPanel(new BorderLayout(5, 5));
        pnDetailDoanhThuThang = new JPanel(new BorderLayout(5, 5));
        pnDetailSanPhamBanChay = new JPanel(new BorderLayout(5, 5));
        pnDetailNhapHang = new JPanel(new BorderLayout(5, 5));
        pnDetailTopKhachHang = new JPanel(new BorderLayout(5, 5));

        centerContentPanel.add(pnDetailDoanhThuNgay, VIEW_DT_NGAY);
        centerContentPanel.add(pnDetailDoanhThuThang, VIEW_DT_THANG);
        centerContentPanel.add(pnDetailSanPhamBanChay, VIEW_SP_CHAY);
        centerContentPanel.add(pnDetailNhapHang, VIEW_NHAP_HANG);
        centerContentPanel.add(pnDetailTopKhachHang, VIEW_TOP_KH);

        // 5. Thêm CardLayout panel vào chính giữa
        panel.add(centerContentPanel, BorderLayout.CENTER);
    
        return panel;
    }


    private void xuLyChucNang(int chucNang) {
        // Lưu lại chức năng đang xem
        this.currentViewIndex = chucNang;

        // Cập nhật switch
        switch (chucNang) {
            case 0: hienThiDashboard(); break;
            case 1: thongKeDoanhThuTheoNgay(); break;
            case 2: thongKeDoanhThuTheoThang(); break;
            case 3: thongKeSanPhamBanChay(); break;
            case 4: thongKeNhapHang(); break;
            case 5: thongKeTopKhachHang(); break;
            case 6: baoCaoTongHop(); break; // Báo cáo tổng hợp dùng JOptionPane, không cần panel riêng
        }
    }

    // helper: convert java.util.Date -> java.sql.Date (or null)
    private java.sql.Date toSqlDate(Date d) {
        return (d == null) ? null : new java.sql.Date(d.getTime());
    }

    // ==================================================================
    // == CÁC HÀM MỚI CHO DASHBOARD VÀ CARDLAYOUT
    // ==================================================================
    
    /**
     * Tạo panel chứa layout cho dashboard
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10)); // Layout 2x2
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Thêm các ô chờ
        panel.add(new JLabel("Đang tải biểu đồ...", SwingConstants.CENTER));
        panel.add(new JLabel("Đang tải biểu đồ...", SwingConstants.CENTER));
        panel.add(new JLabel("Đang tải biểu đồ...", SwingConstants.CENTER));
        panel.add(new JLabel("Đang tải biểu đồ...", SwingConstants.CENTER));
        return panel;
    }

    /**
     * Chuyển về thẻ dashboard và tải lại dữ liệu biểu đồ
     */
    private void hienThiDashboard() {
        // Quan trọng: Gỡ JScrollPane ra khỏi panel chi tiết (nếu có)
        if (tableScrollPane.getParent() != null) {
            tableScrollPane.getParent().remove(tableScrollPane);
        }

        // Tải/làm mới dữ liệu cho các biểu đồ
        updateDashboardCharts();
        
        // Chuyển sang thẻ dashboard
        centerCardLayout.show(centerContentPanel, VIEW_DASHBOARD);
        
        // Xóa bộ lọc cũ trên bảng (nếu có)
        modelThongKe.setRowCount(0);
        modelThongKe.setColumnIdentifiers(new String[]{"STT", "Thông tin", "Giá trị"});
    }

    /**
     * Lấy dữ liệu và vẽ lại các biểu đồ trên dashboard
     */
    private void updateDashboardCharts() {
        dashboardPanel.removeAll();
        dashboardPanel.setLayout(new GridLayout(2, 2, 10, 10)); // Đặt lại layout

        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        // 1. Biểu đồ Doanh thu theo ngày (Line Chart)
        ChartPanel revenueChart = createRevenueByDayChart(tuNgay, denNgay);
        revenueChart.setBorder(BorderFactory.createTitledBorder("Doanh thu theo ngày"));
        dashboardPanel.add(revenueChart);

        // 2. Biểu đồ Top 5 Sản phẩm (Bar Chart)
        ChartPanel topProductsChart = createTopProductsChart(tuNgay, denNgay);
        topProductsChart.setBorder(BorderFactory.createTitledBorder("Top 5 sản phẩm bán chạy"));
        dashboardPanel.add(topProductsChart);

        // 3. Biểu đồ Top 5 Khách hàng (Bar Chart)
        ChartPanel topCustomersChart = createTopCustomersChart(tuNgay, denNgay);
        topCustomersChart.setBorder(BorderFactory.createTitledBorder("Top 5 khách hàng"));
        dashboardPanel.add(topCustomersChart);
        
        // 4. BIỂU ĐỒ MỚI: Doanh thu theo tháng
        ChartPanel revenueMonthChart = createRevenueByMonthChart(tuNgay, denNgay);
        revenueMonthChart.setBorder(BorderFactory.createTitledBorder("Doanh thu theo tháng (trong kỳ)"));
        dashboardPanel.add(revenueMonthChart);

        dashboardPanel.revalidate();
        dashboardPanel.repaint();
        
        // Cập nhật 3 thẻ summary
        updateSummaryCards(tuNgay, denNgay);
    }
    
    /**
     * Hàm riêng để cập nhật 3 thẻ summary, dùng cho cả dashboard và các mục chi tiết
     */
    private void updateSummaryCards(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) listHD = new ArrayList<>();

        float tongDoanhThu = 0;
        for (HoaDon hd : listHD) {
            tongDoanhThu += hd.getTongTien();
        }
        int tongDonHang = listHD.size();
        int tongSL = tinhTongSoLuongBan(tuNgay, denNgay);
        
        lblTongDoanhThu.setText(df.format(tongDoanhThu) + " đ");
        lblTongDonHang.setText(String.valueOf(tongDonHang));
        lblTongSoLuong.setText(String.valueOf(tongSL));
    }


    // ==================================================================
    // == CÁC HÀM TẠO BIỂU ĐỒ (CHO DASHBOARD VÀ CHI TIẾT)
    // ==================================================================

    /**
     * Tạo biểu đồ đường (Line Chart) cho doanh thu theo ngày
     */
    private ChartPanel createRevenueByDayChart(Date tuNgay, Date denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Float> doanhThuTheoNgay = getDataDoanhThuTheoNgay(tuNgay, denNgay);

        for (Map.Entry<String, Float> entry : doanhThuTheoNgay.entrySet()) {
            dataset.addValue(entry.getValue(), "Doanh thu", entry.getKey());
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
            "", "Ngày", "Doanh thu (đ)",
            dataset, PlotOrientation.VERTICAL,
            false, true, false); 
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(0, 250)); // Set chiều cao
        return chartPanel;
    }

    /**
     * Tạo biểu đồ cột (Bar Chart) cho top 5 sản phẩm
     */
    private ChartPanel createTopProductsChart(Date tuNgay, Date denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Integer> soLuongBan = getDataSanPhamBanChay(tuNgay, denNgay).getKey();
        
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(soLuongBan.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedList) {
            if (count >= 5) break; // Chỉ lấy Top 5
            Giay giay = giayDAO.getById(entry.getKey());
            String tenGiay = (giay != null) ? giay.getTenGiay() : entry.getKey();
            dataset.addValue(entry.getValue(), "Số lượng", tenGiay);
            count++;
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "", "Sản phẩm", "Số lượng bán",
            dataset, PlotOrientation.HORIZONTAL, // Biểu đồ cột ngang
            false, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250)); // Set chiều cao
        return chartPanel;
    }

    /**
     * Tạo biểu đồ cột (Bar Chart) cho top 5 khách hàng
     */
    private ChartPanel createTopCustomersChart(Date tuNgay, Date denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, Float> tongTienKH = getDataTopKhachHang(tuNgay, denNgay).getKey();
        
        List<Map.Entry<String, Float>> sortedList = new ArrayList<>(tongTienKH.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        int count = 0;
        for (Map.Entry<String, Float> entry : sortedList) {
            if (count >= 5) break; // Chỉ lấy Top 5
            KhachHang kh = khachHangDAO.getById(entry.getKey());
            String tenKH = (kh != null) ? kh.getTenKH() : entry.getKey();
            dataset.addValue(entry.getValue(), "Chi tiêu", tenKH);
            count++;
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "", "Khách hàng", "Tổng chi tiêu (đ)",
            dataset, PlotOrientation.VERTICAL,
            false, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250)); // Set chiều cao
        return chartPanel;
    }

    /**
     * HÀM MỚI: Tạo biểu đồ cột cho doanh thu theo tháng (dùng cho Dashboard)
     */
    private ChartPanel createRevenueByMonthChart(Date tuNgay, Date denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) listHD = new ArrayList<>();
        
        Map<String, Float> doanhThuTheoThang = new TreeMap<>();
        
        for (HoaDon hd : listHD) {
            if (hd == null || hd.getNgayLap() == null) continue;
            String thang = monthFormat.format(hd.getNgayLap());
            doanhThuTheoThang.put(thang, doanhThuTheoThang.getOrDefault(thang, 0f) + hd.getTongTien());
        }
        
        for (Map.Entry<String, Float> entry : doanhThuTheoThang.entrySet()) {
            dataset.addValue(entry.getValue(), "Doanh thu", entry.getKey());
        }
        
        JFreeChart barChart = ChartFactory.createBarChart(
            "", "Tháng", "Doanh thu (đ)",
            dataset, PlotOrientation.VERTICAL,
            false, true, false);
        
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250)); // Set chiều cao
        return chartPanel;
    }
    
    
    // ==================================================================
    // == CÁC HÀM LẤY DỮ LIỆU (TÁCH RA ĐỂ DÙNG CHUNG)
    // ==================================================================

    private Map<String, Float> getDataDoanhThuTheoNgay(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) listHD = new ArrayList<>();
        
        Map<String, Float> doanhThuTheoNgay = new TreeMap<>();
        for (HoaDon hd : listHD) {
            if (hd == null || hd.getNgayLap() == null) continue;
            String ngay = sdf.format(hd.getNgayLap());
            doanhThuTheoNgay.put(ngay, doanhThuTheoNgay.getOrDefault(ngay, 0f) + hd.getTongTien());
        }
        return doanhThuTheoNgay;
    }
    
    private Map.Entry<Map<String, Integer>, Map<String, Float>> getDataSanPhamBanChay(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) listHD = new ArrayList<>();

        Map<String, Integer> soLuongBan = new HashMap<>();
        Map<String, Float> doanhThuSP = new HashMap<>();

        for (HoaDon hd : listHD) {
            if (hd == null) continue;
            List<ChiTietHoaDon> listCT = chiTietHDDAO.getByHoaDon(hd.getIdHD());
            if (listCT == null) continue;
            for (ChiTietHoaDon ct : listCT) {
                if (ct == null) continue;
                String idGiay = ct.getIdGiay();
                soLuongBan.put(idGiay, soLuongBan.getOrDefault(idGiay, 0) + ct.getSoLuong());
                doanhThuSP.put(idGiay, doanhThuSP.getOrDefault(idGiay, 0f) + ct.getThanhTien());
            }
        }
        return new AbstractMap.SimpleEntry<>(soLuongBan, doanhThuSP);
    }
    
    private Map.Entry<Map<String, Float>, Map<String, Integer>> getDataTopKhachHang(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) listHD = new ArrayList<>();

        Map<String, Float> tongTienKH = new HashMap<>();
        Map<String, Integer> soDonKH = new HashMap<>();

        for (HoaDon hd : listHD) {
            if (hd == null) continue;
            String idKH = hd.getIdKH();
            tongTienKH.put(idKH, tongTienKH.getOrDefault(idKH, 0f) + hd.getTongTien());
            soDonKH.put(idKH, soDonKH.getOrDefault(idKH, 0) + 1);
        }
        return new AbstractMap.SimpleEntry<>(tongTienKH, soDonKH);
    }

    // ==================================================================
    // == CÁC HÀM THỐNG KÊ (ĐÃ CẬP NHẬT ĐỂ HIỂN THỊ CHART + TABLE)
    // ==================================================================

    private void thongKeDoanhThuTheoNgay() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng thời gian!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Float> doanhThuTheoNgay = getDataDoanhThuTheoNgay(tuNgay, denNgay);
        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay)); // Lấy lại để đếm số đơn
        Map<String, Integer> soDonTheoNgay = new TreeMap<>();
        for (HoaDon hd : listHD) {
            if (hd == null || hd.getNgayLap() == null) continue;
            String ngay = sdf.format(hd.getNgayLap());
            soDonTheoNgay.put(ngay, soDonTheoNgay.getOrDefault(ngay, 0) + 1);
        }

        float tongDoanhThu = 0;
        int tongDonHang = 0;
        int stt = 1;

        String[] columns = {"STT", "Ngày", "Số đơn hàng", "Doanh thu"};
        modelThongKe.setColumnIdentifiers(columns);

        for (Map.Entry<String, Float> entry : doanhThuTheoNgay.entrySet()) {
            String ngay = entry.getKey();
            float doanhThu = entry.getValue();
            int soDon = soDonTheoNgay.getOrDefault(ngay, 0);

            modelThongKe.addRow(new Object[]{ stt++, ngay, soDon, df.format(doanhThu) + " đ" });
            tongDoanhThu += doanhThu;
            tongDonHang += soDon;
        }

        // Cập nhật summary cards
        updateSummaryCards(tuNgay, denNgay);
        
        // === NÂNG CẤP: TẠO BIỂU ĐỒ VÀ HIỂN THỊ PANEL ===
        ChartPanel chartPanel = createRevenueByDayChart(tuNgay, denNgay);
        
        pnDetailDoanhThuNgay.removeAll(); // Xóa chart cũ (nếu có)
        pnDetailDoanhThuNgay.add(chartPanel, BorderLayout.NORTH); // Thêm chart mới
        pnDetailDoanhThuNgay.add(tableScrollPane, BorderLayout.CENTER); // Thêm bảng (di chuyển nó)
        
        pnDetailDoanhThuNgay.revalidate();
        pnDetailDoanhThuNgay.repaint();

        centerCardLayout.show(centerContentPanel, VIEW_DT_NGAY);
    }

    private void thongKeDoanhThuTheoThang() {
        modelThongKe.setRowCount(0);
        // Lưu ý: Thống kê này lấy TẤT CẢ dữ liệu, không theo bộ lọc
        List<HoaDon> listHD = hoaDonDAO.getAll(); 
        if (listHD == null) listHD = new ArrayList<>();

        Map<String, Float> doanhThuTheoThang = new TreeMap<>();
        Map<String, Integer> soDonTheoThang = new TreeMap<>();
        
        for (HoaDon hd : listHD) {
            if (hd == null || hd.getNgayLap() == null) continue;
            String thang = monthFormat.format(hd.getNgayLap());
            doanhThuTheoThang.put(thang, doanhThuTheoThang.getOrDefault(thang, 0f) + hd.getTongTien());
            soDonTheoThang.put(thang, soDonTheoThang.getOrDefault(thang, 0) + 1);
        }

        float tongDoanhThu = 0;
        int tongDonHang = 0;
        int stt = 1;

        String[] columns = {"STT", "Tháng/Năm", "Số đơn hàng", "Doanh thu"};
        modelThongKe.setColumnIdentifiers(columns);
        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset(); // Dataset cho chart

        for (Map.Entry<String, Float> entry : doanhThuTheoThang.entrySet()) {
            String thang = entry.getKey();
            float doanhThu = entry.getValue();
            int soDon = soDonTheoThang.getOrDefault(thang, 0);

            modelThongKe.addRow(new Object[]{ stt++, thang, soDon, df.format(doanhThu) + " đ" });
            chartDataset.addValue(doanhThu, "Doanh thu", thang); // Thêm data cho chart
            tongDoanhThu += doanhThu;
            tongDonHang += soDon;
        }

        // Cập nhật summary (theo bộ lọc, không phải tổng)
        updateSummaryCards(dateFrom.getDate(), dateTo.getDate());
        lblTongDoanhThu.setText(df.format(tongDoanhThu) + " đ (Tổng)"); // Ghi đè tổng doanh thu

        // === NÂNG CẤP: TẠO BIỂU ĐỒ VÀ HIỂN THỊ PANEL ===
        JFreeChart barChart = ChartFactory.createBarChart(
            "Biểu đồ doanh thu theo tháng (Tất cả)", "Tháng", "Doanh thu",
            chartDataset, PlotOrientation.VERTICAL, false, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250));

        pnDetailDoanhThuThang.removeAll();
        pnDetailDoanhThuThang.add(chartPanel, BorderLayout.NORTH);
        pnDetailDoanhThuThang.add(tableScrollPane, BorderLayout.CENTER);
        
        pnDetailDoanhThuThang.revalidate();
        pnDetailDoanhThuThang.repaint();

        centerCardLayout.show(centerContentPanel, VIEW_DT_THANG);
    }

    private void thongKeSanPhamBanChay() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        Map.Entry<Map<String, Integer>, Map<String, Float>> data = getDataSanPhamBanChay(tuNgay, denNgay);
        Map<String, Integer> soLuongBan = data.getKey();
        Map<String, Float> doanhThuSP = data.getValue();

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(soLuongBan.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "SL bán", "Doanh thu"};
        modelThongKe.setColumnIdentifiers(columns);
        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset(); // Dataset cho chart

        int stt = 1;
        int tongSL = 0;
        float tongDT = 0;
        int topN = 10; // Chỉ vẽ biểu đồ cho Top 10

        for (Map.Entry<String, Integer> entry : sortedList) {
            String idGiay = entry.getKey();
            int soLuong = entry.getValue();
            float doanhThu = doanhThuSP.getOrDefault(idGiay, 0f);
            Giay giay = giayDAO.getById(idGiay);
            String tenGiay = (giay != null) ? giay.getTenGiay() : "N/A";

            modelThongKe.addRow(new Object[]{ stt, idGiay, tenGiay, soLuong, df.format(doanhThu) + " đ" });
            
            if (stt <= topN) { // Chỉ thêm Top N vào biểu đồ
                chartDataset.addValue(soLuong, "Số lượng", tenGiay + " (ID: " + idGiay + ")");
            }
            
            stt++;
            tongSL += soLuong;
            tongDT += doanhThu;
        }

        // Cập nhật summary cards
        updateSummaryCards(tuNgay, denNgay);
        
        // === NÂNG CẤP: TẠO BIỂU ĐỒ VÀ HIỂN THỊ PANEL ===
        JFreeChart barChart = ChartFactory.createBarChart(
            "Biểu đồ Top " + topN + " sản phẩm bán chạy", "Sản phẩm", "Số lượng",
            chartDataset, PlotOrientation.HORIZONTAL, false, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 300)); // Cần cao hơn

        pnDetailSanPhamBanChay.removeAll();
        pnDetailSanPhamBanChay.add(chartPanel, BorderLayout.NORTH);
        pnDetailSanPhamBanChay.add(tableScrollPane, BorderLayout.CENTER);
        
        pnDetailSanPhamBanChay.revalidate();
        pnDetailSanPhamBanChay.repaint();

        centerCardLayout.show(centerContentPanel, VIEW_SP_CHAY);
    }

    private void thongKeNhapHang() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        List<NhapKho> listNK = nhapKhoDAO.getByDate(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listNK == null) listNK = new ArrayList<>();

        String[] columns = {"STT", "Mã phiếu", "Ngày nhập", "Nhà cung cấp", "Tổng tiền", "Trạng thái"};
        modelThongKe.setColumnIdentifiers(columns);
        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();

        int stt = 1;
        float tongTien = 0;
        int tongSL = 0;
        Map<String, Float> chiTheoNgay = new TreeMap<>(); // Dữ liệu cho chart

        for (NhapKho nk : listNK) {
            if (nk == null) continue;
            modelThongKe.addRow(new Object[]{
                stt++, nk.getIdNhapKho(), nk.getNgayNhap() == null ? "N/A" : sdf.format(nk.getNgayNhap()),
                nk.getIdNCC(), df.format(nk.getTongTien()) + " đ", nk.getStatus()
            });

            tongTien += nk.getTongTien();
            
            // Tính tổng SL
            List<ChiTietNhapKho> listCT = chiTietNKDAO.getByNhapKho(nk.getIdNhapKho());
            if (listCT != null) {
                for (ChiTietNhapKho ct : listCT) {
                    if (ct != null) tongSL += ct.getSoLuong();
                }
            }
            
            // Gom dữ liệu cho chart
            if (nk.getNgayNhap() != null) {
                String ngay = sdf.format(nk.getNgayNhap());
                chiTheoNgay.put(ngay, chiTheoNgay.getOrDefault(ngay, 0f) + nk.getTongTien());
            }
        }
        
        for (Map.Entry<String, Float> entry : chiTheoNgay.entrySet()) {
            chartDataset.addValue(entry.getValue(), "Chi phí nhập", entry.getKey());
        }

        // Cập nhật summary (hiển thị chi phí nhập thay cho doanh thu)
        lblTongDoanhThu.setText(df.format(tongTien) + " đ");
        lblTongDonHang.setText(String.valueOf(listNK.size()));
        lblTongSoLuong.setText(String.valueOf(tongSL));

        // === NÂNG CẤP: TẠO BIỂU ĐỒ VÀ HIỂN THỊ PANEL ===
        JFreeChart lineChart = ChartFactory.createLineChart(
            "Biểu đồ chi phí nhập hàng theo ngày", "Ngày", "Chi phí",
            chartDataset, PlotOrientation.VERTICAL, false, true, false);
        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(0, 250));

        pnDetailNhapHang.removeAll();
        pnDetailNhapHang.add(chartPanel, BorderLayout.NORTH);
        pnDetailNhapHang.add(tableScrollPane, BorderLayout.CENTER);
        
        pnDetailNhapHang.revalidate();
        pnDetailNhapHang.repaint();

        centerCardLayout.show(centerContentPanel, VIEW_NHAP_HANG);
    }

    private void thongKeTopKhachHang() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        Map.Entry<Map<String, Float>, Map<String, Integer>> data = getDataTopKhachHang(tuNgay, denNgay);
        Map<String, Float> tongTienKH = data.getKey();
        Map<String, Integer> soDonKH = data.getValue();

        List<Map.Entry<String, Float>> sortedList = new ArrayList<>(tongTienKH.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String[] columns = {"STT", "Mã KH", "Tên khách hàng", "Số đơn", "Tổng chi tiêu"};
        modelThongKe.setColumnIdentifiers(columns);
        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();

        int stt = 1;
        float tongDoanhThu = 0;
        int topN = 10; // Chỉ vẽ biểu đồ cho Top 10

        for (Map.Entry<String, Float> entry : sortedList) {
            String idKH = entry.getKey();
            float tongTien = entry.getValue();
            int soDon = soDonKH.getOrDefault(idKH, 0);
            KhachHang kh = khachHangDAO.getById(idKH);
            String tenKH = (kh != null) ? kh.getTenKH() : "N/A";

            modelThongKe.addRow(new Object[]{ stt, idKH, tenKH, soDon, df.format(tongTien) + " đ" });

            if (stt <= topN) { // Chỉ thêm Top N vào biểu đồ
                chartDataset.addValue(tongTien, "Chi tiêu", tenKH + " (ID: " + idKH + ")");
            }

            stt++;
            tongDoanhThu += tongTien;
        }

        // Cập nhật summary cards
        updateSummaryCards(tuNgay, denNgay);

        // === NÂNG CẤP: TẠO BIỂU ĐỒ VÀ HIỂN THỊ PANEL ===
        JFreeChart barChart = ChartFactory.createBarChart(
            "Biểu đồ Top " + topN + " khách hàng", "Khách hàng", "Tổng chi tiêu",
            chartDataset, PlotOrientation.VERTICAL, false, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250));

        pnDetailTopKhachHang.removeAll();
        pnDetailTopKhachHang.add(chartPanel, BorderLayout.NORTH);
        pnDetailTopKhachHang.add(tableScrollPane, BorderLayout.CENTER);
        
        pnDetailTopKhachHang.revalidate();
        pnDetailTopKhachHang.repaint();

        centerCardLayout.show(centerContentPanel, VIEW_TOP_KH);
    }

    private void baoCaoTongHop() {
        // Hàm này không thay đổi vì dùng JOptionPane
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();
        java.sql.Date sqlTuNgay = toSqlDate(tuNgay);
        java.sql.Date sqlDenNgay = toSqlDate(denNgay);

        List<HoaDon> listHD = hoaDonDAO.getByDateRange(sqlTuNgay, sqlDenNgay);
        if (listHD == null) listHD = new ArrayList<>();
        List<NhapKho> listNK = nhapKhoDAO.getByDate(sqlTuNgay, sqlDenNgay);
        if (listNK == null) listNK = new ArrayList<>();

        float tongDoanhThu = 0;
        float tongNhapHang = 0;
        int tongSLBan = 0;
        int tongSLNhap = 0;

        for (HoaDon hd : listHD) {
            if (hd == null) continue;
            tongDoanhThu += hd.getTongTien();
            List<ChiTietHoaDon> listCT = chiTietHDDAO.getByHoaDon(hd.getIdHD());
            if (listCT == null) continue;
            for (ChiTietHoaDon ct : listCT) {
                if (ct == null) continue;
                tongSLBan += ct.getSoLuong();
            }
        }

        for (NhapKho nk : listNK) {
            if (nk == null) continue;
            tongNhapHang += nk.getTongTien();
            List<ChiTietNhapKho> listCT = chiTietNKDAO.getByNhapKho(nk.getIdNhapKho());
            if (listCT == null) continue;
            for (ChiTietNhapKho ct : listCT) {
                if (ct == null) continue;
                tongSLNhap += ct.getSoLuong();
            }
        }

        float loiNhuan = tongDoanhThu - tongNhapHang;

        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════\n");
        report.append("           BÁO CÁO TỔNG HỢP KINH DOANH\n");
        report.append("═══════════════════════════════════════════════════\n\n");
        if (tuNgay != null && denNgay != null) {
            report.append("Thời gian: ").append(sdf.format(tuNgay)).append(" → ").append(sdf.format(denNgay)).append("\n\n");
        } else {
            report.append("Thời gian: N/A\n\n");
        }
        report.append("───────────────────────────────────────────────────\n");
        report.append("I. DOANH THU BÁN HÀNG\n");
        report.append("───────────────────────────────────────────────────\n");
        report.append("• Tổng số đơn hàng: ").append(listHD.size()).append("\n");
        report.append("• Tổng sản phẩm bán: ").append(tongSLBan).append("\n");
        report.append("• Tổng doanh thu: ").append(df.format(tongDoanhThu)).append(" đ\n\n");

        report.append("───────────────────────────────────────────────────\n");
        report.append("II. NHẬP HÀNG\n");
        report.append("───────────────────────────────────────────────────\n");
        report.append("• Tổng phiếu nhập: ").append(listNK.size()).append("\n");
        report.append("• Tổng sản phẩm nhập: ").append(tongSLNhap).append("\n");
        report.append("• Tổng chi phí nhập: ").append(df.format(tongNhapHang)).append(" đ\n\n");

        report.append("───────────────────────────────────────────────────\n");
        report.append("III. LỢI NHUẬN\n");
        report.append("───────────────────────────────────────────────────\n");
        report.append("• Doanh thu: ").append(df.format(tongDoanhThu)).append(" đ\n");
        report.append("• Chi phí nhập: ").append(df.format(tongNhapHang)).append(" đ\n");
        report.append("• Lợi nhuận: ").append(df.format(loiNhuan)).append(" đ\n");
        if (tongDoanhThu != 0) {
            report.append("• Tỷ lệ lợi nhuận: ").append(String.format("%.2f%%", (loiNhuan / tongDoanhThu) * 100)).append("\n\n");
        } else {
            report.append("• Tỷ lệ lợi nhuận: N/A\n\n");
        }
        report.append("═══════════════════════════════════════════════════\n");

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 500));

        JOptionPane.showMessageDialog(this, scrollPane,
            "Báo cáo tổng hợp", JOptionPane.INFORMATION_MESSAGE);
    }

    // ====================== HÀM HỖ TRỢ ======================
    
    private int tinhTongSoLuongBan(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) return 0;
        int tong = 0;

        for (HoaDon hd : listHD) {
            if (hd == null) continue;
            List<ChiTietHoaDon> listCT = chiTietHDDAO.getByHoaDon(hd.getIdHD());
            if (listCT == null) continue;
            for (ChiTietHoaDon ct : listCT) {
                if (ct == null) continue;
                tong += ct.getSoLuong();
            }
        }
        return tong;
    }

    /**
     * Tự động chạy lại chức năng hiện tại với bộ lọc mới.
     */
    private void locDuLieu() {
        xuLyChucNang(this.currentViewIndex);
    }

    private void xuatBaoCao() {
        // Chỉ xuất báo cáo nếu đang ở chế độ xem bảng (bất kỳ panel chi tiết nào)
        if (!tableScrollPane.isShowing()) {
             JOptionPane.showMessageDialog(this,
                "Chức năng xuất Excel chỉ dùng cho các báo cáo chi tiết (dạng bảng).\nVui lòng chọn một báo cáo chi tiết trước khi xuất.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (modelThongKe.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo");
        fileChooser.setSelectedFile(new java.io.File("BaoCao_" +
            new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave, "UTF-8")) {
                // Write headers
                for (int i = 0; i < modelThongKe.getColumnCount(); i++) {
                    writer.print("\"" + modelThongKe.getColumnName(i) + "\"");
                    if (i < modelThongKe.getColumnCount() - 1) writer.print(",");
                }
                writer.println();

                // Write data
                for (int i = 0; i < modelThongKe.getRowCount(); i++) {
                    for (int j = 0; j < modelThongKe.getColumnCount(); j++) {
                        Object cellData = modelThongKe.getValueAt(i, j);
                        writer.print("\"" + (cellData == null ? "" : cellData.toString()) + "\"");
                        if (j < modelThongKe.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(this,
                    "Xuất báo cáo thành công!\nĐường dẫn: " + fileToSave.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất báo cáo: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}