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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.block.BlockBorder;
import java.awt.BasicStroke;
import java.awt.Paint;

// <<< SỬA: THÊM CÁC IMPORT ĐỂ ĐỊNH DẠNG SỐ (BỎ E7) >>>
import java.text.NumberFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
// =====================================================

public class QuanLyThongKe extends JPanel {
    private JTable tableThongKe;
    private DefaultTableModel modelThongKe;
    private JLabel lblTongDoanhThu, lblTongSoLuong, lblTongDonHang;
    private JDateChooser dateFrom, dateTo;
    private JPanel pnBoLocNgay;
    
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
    private JPanel pnBaoCaoTongHop; // <<< SỬA: THÊM PANEL CHO BÁO CÁO TỔNG HỢP

    // --- CÁC HẰNG SỐ CHO CARDLAYOUT ---
    private static final String VIEW_DASHBOARD = "DASHBOARD";
    private static final String VIEW_DT_NGAY = "VIEW_DT_NGAY";
    private static final String VIEW_DT_THANG = "VIEW_DT_THANG";
    private static final String VIEW_SP_CHAY = "VIEW_SP_CHAY";
    private static final String VIEW_NHAP_HANG = "VIEW_NHAP_HANG";
    private static final String VIEW_TOP_KH = "VIEW_TOP_KH";
    private static final String VIEW_BAO_CAO_TH = "VIEW_BAO_CAO_TH"; // <<< SỬA: THÊM HẰNG SỐ
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

        // === 1. TẠO PANEL CHỨA CÁC THÀNH PHẦN CHỌN NGÀY & NÚT LỌC ===
        pnBoLocNgay = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnBoLocNgay.setBackground(Color.WHITE);

        // --- Cấu hình Ngày bắt đầu ---
        pnBoLocNgay.add(new JLabel("Từ ngày:"));
        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setPreferredSize(new Dimension(130, 30));
        dateFrom.setMaxSelectableDate(new Date()); // Chặn ngày tương lai
        
        // Mặc định khi mở lên: Lấy từ 30 ngày trước
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        dateFrom.setDate(cal.getTime());
        pnBoLocNgay.add(dateFrom);

        // --- Cấu hình Ngày kết thúc ---
        pnBoLocNgay.add(new JLabel("Đến ngày:"));
        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setPreferredSize(new Dimension(130, 30));
        dateTo.setMaxSelectableDate(new Date()); // Chặn ngày tương lai
        dateTo.setDate(new Date());
        pnBoLocNgay.add(dateTo);

        // --- Nút Lọc dữ liệu ---
        JButton btnLoc = createStyledButton("Lọc dữ liệu", new Color(52, 152, 219));
        btnLoc.addActionListener(e -> locDuLieu());
        pnBoLocNgay.add(btnLoc);

        // Thêm panel con vào panel chính
        panel.add(pnBoLocNgay);

        // === 2. CÁC NÚT CHỨC NĂNG CHUNG ===
        
        // <<< SỬA ĐỔI TẠI ĐÂY: GỌI HÀM RESET KHI ẤN LÀM MỚI >>>
        JButton btnLamMoi = createStyledButton("Làm mới", new Color(149, 165, 166));
        btnLamMoi.addActionListener(e -> {
            resetVaHienThiToanBo(); // Gọi hàm reset ngày và load lại bảng
        });
        panel.add(btnLamMoi);
        // =======================================================

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
        String[] columns = {"Số thứ tự", "Thông tin", "Giá trị"};
        modelThongKe = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableThongKe = new JTable(modelThongKe);
        tableThongKe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableThongKe.setRowHeight(30);
        tableThongKe.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableThongKe.getTableHeader().setBackground(new Color(52, 73, 94));
        tableThongKe.getTableHeader().setForeground(Color.WHITE);
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
        pnBaoCaoTongHop = new JPanel(new BorderLayout(5, 5)); // <<< SỬA: KHỞI TẠO PANEL MỚI

        centerContentPanel.add(pnDetailDoanhThuNgay, VIEW_DT_NGAY);
        centerContentPanel.add(pnDetailDoanhThuThang, VIEW_DT_THANG);
        centerContentPanel.add(pnDetailSanPhamBanChay, VIEW_SP_CHAY);
        centerContentPanel.add(pnDetailNhapHang, VIEW_NHAP_HANG);
        centerContentPanel.add(pnDetailTopKhachHang, VIEW_TOP_KH);
        centerContentPanel.add(pnBaoCaoTongHop, VIEW_BAO_CAO_TH); // <<< SỬA: THÊM PANEL MỚI VÀO CARDLAYOUT

        // 5. Thêm CardLayout panel vào chính giữa
        panel.add(centerContentPanel, BorderLayout.CENTER);
    
        return panel;
    }


    private void xuLyChucNang(int chucNang) {
        // Lưu lại chức năng đang xem
        this.currentViewIndex = chucNang;

        // === LOGIC ẨN HIỆN BỘ LỌC NGÀY ===
        if (chucNang == 6) {
            pnBoLocNgay.setVisible(false); // Ẩn chọn ngày nếu là Báo cáo tổng hợp
        } else {
            pnBoLocNgay.setVisible(true);  // Hiện chọn ngày cho các mục khác
        }
        // =================================

        // Cập nhật switch
        switch (chucNang) {
            case 0: hienThiDashboard(); break;
            case 1: thongKeDoanhThuTheoNgay(); break;
            case 2: thongKeDoanhThuTheoThang(); break;
            case 3: thongKeSanPhamBanChay(); break;
            case 4: thongKeNhapHang(); break;
            case 5: thongKeTopKhachHang(); break;
            case 6: hienThiBaoCaoTongHop(); break;
        }
    }

    // helper: convert java.util.Date -> java.sql.Date (or null)
    private java.sql.Date toSqlDate(Date d) {
        return (d == null) ? null : new java.sql.Date(d.getTime());
    }

    // ==================================================================
    // == CÁC HÀM MỚI CHO DASHBOARD VÀ CARDLAYOUT
    // ==================================================================
    
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
        modelThongKe.setColumnIdentifiers(new String[]{"Số thứ tự", "Thông tin", "Giá trị"});
        
        // Buộc JTable cập nhật lại tiêu đề (chuyển về generic)
        tableThongKe.getTableHeader().revalidate();
        tableThongKe.getTableHeader().repaint();
    }

    private void updateDashboardCharts() {
        dashboardPanel.removeAll();
        dashboardPanel.setLayout(new GridLayout(2, 2, 10, 10)); // Đặt lại layout

        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        // 1. Biểu đồ Doanh thu theo ngày (Line Chart)
        ChartPanel revenueChart = createRevenueByDayChart(tuNgay, denNgay);
        revenueChart.setBorder(BorderFactory.createTitledBorder("Doanh thu theo ngày (Đã thanh toán)"));
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
    
    private void updateSummaryCards(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD_raw = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        List<HoaDon> listHD = filterHoaDon(listHD_raw); // <<< LỌC HÓA ĐƠN
        
        if (listHD == null) listHD = new ArrayList<>();

        float tongDoanhThu = 0;
        for (HoaDon hd : listHD) {
            tongDoanhThu += hd.getTongTien();
        }
        int tongDonHang = listHD.size();
        int tongSL = tinhTongSoLuongBan(tuNgay, denNgay); // (Hàm này cũng đã được lọc)
        
        lblTongDoanhThu.setText(df.format(tongDoanhThu) + " đ");
        lblTongDonHang.setText(String.valueOf(tongDonHang));
        lblTongSoLuong.setText(String.valueOf(tongSL));
    }


    // ==================================================================
    // == CÁC HÀM TẠO BIỂU ĐỒ (CHO DASHBOARD VÀ CHI TIẾT)
    // ==================================================================

 // Trong QuanLyThongKe.java

    private ChartPanel createRevenueByDayChart(Date tuNgay, Date denNgay, String title) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // getDataDoanhThuTheoNgay đã được sửa để lọc
        Map<String, Float> doanhThuTheoNgay = getDataDoanhThuTheoNgay(tuNgay, denNgay);

        for (Map.Entry<String, Float> entry : doanhThuTheoNgay.entrySet()) {
            dataset.addValue(entry.getValue(), "Doanh thu", entry.getKey());
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                title, "Ngày", "Doanh thu (đ)", 
                dataset, PlotOrientation.VERTICAL,
                false, true, false); 
                
            // === ÁP DỤNG THEME HIỆN ĐẠI ===
            applyModernTheme(lineChart); // Hàm này sẽ lấy tiêu đề và định dạng font
            // ==============================

            ChartPanel chartPanel = new ChartPanel(lineChart);
            chartPanel.setPreferredSize(new Dimension(0, 250)); // Set chiều cao
       
            return chartPanel;
    }

    // Cập nhật lại các chỗ gọi trong Dashboard (Giữ nguyên giao diện dashboard)
    private ChartPanel createRevenueByDayChart(Date tuNgay, Date denNgay) {
        // Gọi lại hàm mới với tiêu đề mặc định cho Dashboard
        return createRevenueByDayChart(tuNgay, denNgay, "");
    }
    
    private ChartPanel createTopProductsChart(Date tuNgay, Date denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // getDataSanPhamBanChay đã được sửa để lọc
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
            
        // === ÁP DỤNG THEME HIỆN ĐẠI ===
        applyModernTheme(barChart);
        // ==============================
            
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250)); // Set chiều cao
        return chartPanel;
    }

    private ChartPanel createTopCustomersChart(Date tuNgay, Date denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // getDataTopKhachHang đã được sửa để lọc
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
            
        // === ÁP DỤNG THEME HIỆN ĐẠI ===
        applyModernTheme(barChart);
        // ==============================
            
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250)); // Set chiều cao
        return chartPanel;
    }

    private ChartPanel createRevenueByMonthChart(Date tuNgay, Date denNgay) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<HoaDon> listHD_raw = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        List<HoaDon> listHD = filterHoaDon(listHD_raw); // <<< LỌC HÓA ĐƠN
        
        if (listHD == null) listHD = new ArrayList<>();
        
        Map<String, Float> doanhThuTheoThang = new TreeMap<>();
        
        for (HoaDon hd : listHD) { // Dùng list đã lọc
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
            
        // === ÁP DỤNG THEME HIỆN ĐẠI ===
        applyModernTheme(barChart);
        // ==============================
        
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250)); // Set chiều cao
        return chartPanel;
    }
    
    
    // ==================================================================
    // == CÁC HÀM LẤY DỮ LIỆU (TÁCH RA ĐỂ DÙNG CHUNG)
    // ==================================================================

    private Map<String, Float> getDataDoanhThuTheoNgay(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD_raw = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        List<HoaDon> listHD = filterHoaDon(listHD_raw); // <<< LỌC HÓA ĐƠN
        
        if (listHD == null) listHD = new ArrayList<>();
        
        Map<String, Float> doanhThuTheoNgay = new TreeMap<>();
        for (HoaDon hd : listHD) { // Dùng list đã lọc
            if (hd == null || hd.getNgayLap() == null) continue;
            String ngay = sdf.format(hd.getNgayLap());
            doanhThuTheoNgay.put(ngay, doanhThuTheoNgay.getOrDefault(ngay, 0f) + hd.getTongTien());
        }
        return doanhThuTheoNgay;
    }
    
    private Map.Entry<Map<String, Integer>, Map<String, Float>> getDataSanPhamBanChay(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD_raw = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        List<HoaDon> listHD = filterHoaDon(listHD_raw); // <<< LỌC HÓA ĐƠN
        
        if (listHD == null) listHD = new ArrayList<>();

        Map<String, Integer> soLuongBan = new HashMap<>();
        Map<String, Float> doanhThuSP = new HashMap<>();

        for (HoaDon hd : listHD) { // Dùng list đã lọc
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
        List<HoaDon> listHD_raw = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        List<HoaDon> listHD = filterHoaDon(listHD_raw); // <<< LỌC HÓA ĐƠN
        
        if (listHD == null) listHD = new ArrayList<>();

        Map<String, Float> tongTienKH = new HashMap<>();
        Map<String, Integer> soDonKH = new HashMap<>();

        for (HoaDon hd : listHD) { // Dùng list đã lọc
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

 // Trong QuanLyThongKe.java
    private void thongKeDoanhThuTheoNgay() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        // <<< SỬA: Lấy đến hết ngày (23:59:59) >>>
        Date denNgay = getEndOfDay(dateTo.getDate()); 

        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng thời gian!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy dữ liệu với thời gian đã chỉnh sửa
        Map<String, Float> doanhThuTheoNgay = getDataDoanhThuTheoNgay(tuNgay, denNgay);
        List<HoaDon> listHD_raw = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay)); 
        List<HoaDon> listHD = filterHoaDon(listHD_raw); 
        
        Map<String, Integer> soDonTheoNgay = new TreeMap<>();
        for (HoaDon hd : listHD) { 
            if (hd == null || hd.getNgayLap() == null) continue;
            String ngay = sdf.format(hd.getNgayLap());
            soDonTheoNgay.put(ngay, soDonTheoNgay.getOrDefault(ngay, 0) + 1);
        }

        float tongDoanhThu = 0;
        int tongDonHang = 0;
        int stt = 1;

        String[] columns = {"Số thứ tự", "Ngày", "Số đơn hàng", "Doanh thu"};
        modelThongKe.setColumnIdentifiers(columns);
        
        tableThongKe.getTableHeader().revalidate();
        tableThongKe.getTableHeader().repaint();

        // Đổ dữ liệu vào bảng
        for (Map.Entry<String, Float> entry : doanhThuTheoNgay.entrySet()) {
            String ngay = entry.getKey();
            float doanhThu = entry.getValue();
            int soDon = soDonTheoNgay.getOrDefault(ngay, 0);

            modelThongKe.addRow(new Object[]{ stt++, ngay, soDon, df.format(doanhThu) + " đ" });
            tongDoanhThu += doanhThu;
            tongDonHang += soDon;
        }

        updateSummaryCards(tuNgay, denNgay);
        
        // Vẽ biểu đồ
        ChartPanel chartPanel = createRevenueByDayChart(tuNgay, denNgay, "BIỂU ĐỒ DOANH THU THEO NGÀY");
        
        pnDetailDoanhThuNgay.removeAll(); 
        pnDetailDoanhThuNgay.add(chartPanel, BorderLayout.NORTH); 
        pnDetailDoanhThuNgay.add(tableScrollPane, BorderLayout.CENTER); 
        
        tableScrollPane.getVerticalScrollBar().setValue(0);
        tableThongKe.getTableHeader().repaint();

        pnDetailDoanhThuNgay.revalidate();
        pnDetailDoanhThuNgay.repaint();

        centerCardLayout.show(centerContentPanel, VIEW_DT_NGAY);
    }

    private void thongKeDoanhThuTheoThang() {
        modelThongKe.setRowCount(0);
        
        // 1. Lấy ngày từ bộ lọc và xử lý EndOfDay (Thêm đoạn này)
        Date tuNgay = dateFrom.getDate();
        Date denNgay = getEndOfDay(dateTo.getDate()); 

        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng thời gian!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Sửa getAll() thành getByDateRange(...) (Sửa đoạn này)
        List<HoaDon> listHD_raw = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay)); 
        List<HoaDon> listHD = filterHoaDon(listHD_raw); 
        
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

        String[] columns = {"Số thứ tự", "Tháng/Năm", "Số đơn hàng", "Doanh thu"};
        modelThongKe.setColumnIdentifiers(columns);
        
        tableThongKe.getTableHeader().revalidate();
        tableThongKe.getTableHeader().repaint();

        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset(); 

        for (Map.Entry<String, Float> entry : doanhThuTheoThang.entrySet()) {
            String thang = entry.getKey();
            float doanhThu = entry.getValue();
            int soDon = soDonTheoThang.getOrDefault(thang, 0);

            modelThongKe.addRow(new Object[]{ stt++, thang, soDon, df.format(doanhThu) + " đ" });
            chartDataset.addValue(doanhThu, "Doanh thu", thang); 
            tongDoanhThu += doanhThu;
            tongDonHang += soDon;
        }

        // Cập nhật thẻ tổng quan
        updateSummaryCards(tuNgay, denNgay); // Sửa tham số truyền vào
        lblTongDoanhThu.setText(df.format(tongDoanhThu) + " đ (Tổng)"); 

        JFreeChart barChart = ChartFactory.createBarChart(
            "Biểu đồ doanh thu theo tháng", "Tháng", "Doanh thu",
            chartDataset, PlotOrientation.VERTICAL, false, true, false);
            
        applyModernTheme(barChart);
            
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
        Date denNgay = getEndOfDay(dateTo.getDate());

        // Hàm này đã được lọc
        Map.Entry<Map<String, Integer>, Map<String, Float>> data = getDataSanPhamBanChay(tuNgay, denNgay);
        Map<String, Integer> soLuongBan = data.getKey();
        Map<String, Float> doanhThuSP = data.getValue();

        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(soLuongBan.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String[] columns = {"Số thứ tự", "Mã SP", "Tên sản phẩm", "SL bán", "Doanh thu"};
        modelThongKe.setColumnIdentifiers(columns);
        
        // === SỬA LỖI HIỂN THỊ TIÊU ĐỀ BẢNG ===
        tableThongKe.getTableHeader().revalidate();
        tableThongKe.getTableHeader().repaint();
        // ====================================
        
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

        // Cập nhật summary cards (hàm này đã được lọc)
        updateSummaryCards(tuNgay, denNgay);
        
        // === NÂNG CẤP: TẠO BIỂU ĐỒ VÀ HIỂN THỊ PANEL ===
        JFreeChart barChart = ChartFactory.createBarChart(
            "Biểu đồ Top " + topN + " sản phẩm bán chạy", "Sản phẩm", "Số lượng",
            chartDataset, PlotOrientation.HORIZONTAL, false, true, false);
            
        // === ÁP DỤNG THEME HIỆN ĐẠI ===
        applyModernTheme(barChart);
        // ==============================
            
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
        Date denNgay = getEndOfDay(dateTo.getDate());

        List<NhapKho> listNK_raw = nhapKhoDAO.getByDate(toSqlDate(tuNgay), toSqlDate(denNgay));
        List<NhapKho> listNK = filterNhapKho(listNK_raw); // <<< LỌC PHIẾU NHẬP
        
        if (listNK == null) listNK = new ArrayList<>();

        String[] columns = {"Số thứ tự", "Mã phiếu", "Ngày nhập", "Nhà cung cấp", "Tổng tiền", "Trạng thái"};
        modelThongKe.setColumnIdentifiers(columns);
        
        // === SỬA LỖI HIỂN THỊ TIÊU ĐỀ BẢNG ===
        tableThongKe.getTableHeader().revalidate();
        tableThongKe.getTableHeader().repaint();
        // ====================================

        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();

        int stt = 1;
        float tongTien = 0;
        int tongSL = 0;
        Map<String, Float> chiTheoNgay = new TreeMap<>(); // Dữ liệu cho chart

        for (NhapKho nk : listNK) { // Dùng list đã lọc
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
            
        // === ÁP DỤNG THEME HIỆN ĐẠI ===
        applyModernTheme(lineChart);
        // ==============================
            
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
        Date denNgay = getEndOfDay(dateTo.getDate());

        // Hàm này đã được lọc
        Map.Entry<Map<String, Float>, Map<String, Integer>> data = getDataTopKhachHang(tuNgay, denNgay);
        Map<String, Float> tongTienKH = data.getKey();
        Map<String, Integer> soDonKH = data.getValue();

        List<Map.Entry<String, Float>> sortedList = new ArrayList<>(tongTienKH.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String[] columns = {"Số thứ tự", "Mã KH", "Tên khách hàng", "Số đơn", "Tổng chi tiêu"};
        modelThongKe.setColumnIdentifiers(columns);
        
        // === SỬA LỖI HIỂN THỊ TIÊU ĐỀ BẢNG ===
        tableThongKe.getTableHeader().revalidate();
        tableThongKe.getTableHeader().repaint();
        // ====================================
        
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

        // Cập nhật summary cards (hàm này đã được lọc)
        updateSummaryCards(tuNgay, denNgay);

        // === NÂNG CẤP: TẠO BIỂU ĐỒ VÀ HIỂN THỊ PANEL ===
        JFreeChart barChart = ChartFactory.createBarChart(
            "Biểu đồ Top " + topN + " khách hàng", "Khách hàng", "Tổng chi tiêu",
            chartDataset, PlotOrientation.VERTICAL, false, true, false);
            
        // === ÁP DỤNG THEME HIỆN ĐẠI ===
        applyModernTheme(barChart);
        // ==============================
            
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250));

        pnDetailTopKhachHang.removeAll();
        pnDetailTopKhachHang.add(chartPanel, BorderLayout.NORTH);
        pnDetailTopKhachHang.add(tableScrollPane, BorderLayout.CENTER);
        
        pnDetailTopKhachHang.revalidate();
        pnDetailTopKhachHang.repaint();

        centerCardLayout.show(centerContentPanel, VIEW_TOP_KH);
    }

    // =================================================================
    // === SỬA: HÀM MỚI THAY THẾ HOÀN TOÀN HÀM baoCaoTongHop() CŨ
    // =================================================================
    /**
     * THÊM MỚI: Hiển thị báo cáo tổng hợp (Doanh thu, Chi phí, Lợi nhuận)
     * sử dụng framework Chart + Table có sẵn.
     */
    private void hienThiBaoCaoTongHop() {
        modelThongKe.setRowCount(0);
        
        // === SỬA: LẤY TOÀN BỘ DỮ LIỆU (KHÔNG DÙNG DATE FILTER) ===
        // Vì người dùng không chọn ngày, ta mặc định thống kê toàn bộ thời gian
        
        List<HoaDon> listHD_raw = hoaDonDAO.getAll(); // Dùng getAll()
        List<HoaDon> listHD = filterHoaDon(listHD_raw);
        if (listHD == null) listHD = new ArrayList<>();
        
        List<NhapKho> listNK_raw = nhapKhoDAO.getAll(); // Dùng getAll()
        List<NhapKho> listNK = filterNhapKho(listNK_raw);
        if (listNK == null) listNK = new ArrayList<>();

        // Phần tính toán bên dưới giữ nguyên
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
                if (ct != null) tongSLBan += ct.getSoLuong();
            }
        }

        for (NhapKho nk : listNK) {
            if (nk == null) continue;
            tongNhapHang += nk.getTongTien();
            List<ChiTietNhapKho> listCT = chiTietNKDAO.getByNhapKho(nk.getIdNhapKho());
            if (listCT == null) continue;
            for (ChiTietNhapKho ct : listCT) {
                if (ct != null) tongSLNhap += ct.getSoLuong();
            }
        }

        float loiNhuan = tongDoanhThu - tongNhapHang;

        // Cập nhật bảng
        String[] columns = {"Số thứ tự", "Hạng mục", "Giá trị"};
        modelThongKe.setColumnIdentifiers(columns);
        tableThongKe.getTableHeader().revalidate();
        tableThongKe.getTableHeader().repaint();

        modelThongKe.addRow(new Object[]{1, "Tổng doanh thu (Toàn thời gian)", df.format(tongDoanhThu) + " đ"});
        modelThongKe.addRow(new Object[]{2, "Tổng chi phí nhập (Toàn thời gian)", df.format(tongNhapHang) + " đ"});
        modelThongKe.addRow(new Object[]{3, "Lợi nhuận", df.format(loiNhuan) + " đ"});
        
        String tyLeLN = "N/A";
        if (tongDoanhThu != 0) {
            tyLeLN = String.format("%.2f%%", (loiNhuan / tongDoanhThu) * 100);
        }
        modelThongKe.addRow(new Object[]{4, "Tỷ lệ lợi nhuận", tyLeLN});
        modelThongKe.addRow(new Object[]{5, "Tổng đơn hàng đã bán", listHD.size()});
        modelThongKe.addRow(new Object[]{6, "Tổng sản phẩm đã bán", tongSLBan});
        modelThongKe.addRow(new Object[]{7, "Tổng phiếu nhập", listNK.size()});
        modelThongKe.addRow(new Object[]{8, "Tổng sản phẩm đã nhập", tongSLNhap});

        // Cập nhật Summary Cards (Hiển thị tổng quan toàn bộ)
        lblTongDoanhThu.setText(df.format(tongDoanhThu) + " đ");
        lblTongDonHang.setText(String.valueOf(listHD.size()));
        lblTongSoLuong.setText(String.valueOf(tongSLBan));

        // Vẽ biểu đồ
        DefaultCategoryDataset chartDataset = new DefaultCategoryDataset();
        chartDataset.addValue(tongDoanhThu, "Giá trị", "Doanh thu");
        chartDataset.addValue(tongNhapHang, "Giá trị", "Chi phí nhập");
        
        JFreeChart barChart = ChartFactory.createBarChart(
            "Biểu đồ Tổng hợp (Toàn thời gian)", "Hạng mục", "Số tiền (đ)",
            chartDataset, PlotOrientation.VERTICAL, false, true, false);
            
        applyModernTheme(barChart);
            
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(0, 250));

        pnBaoCaoTongHop.removeAll();
        pnBaoCaoTongHop.add(chartPanel, BorderLayout.NORTH);
        pnBaoCaoTongHop.add(tableScrollPane, BorderLayout.CENTER);
        
        pnBaoCaoTongHop.revalidate();
        pnBaoCaoTongHop.repaint();

        centerCardLayout.show(centerContentPanel, VIEW_BAO_CAO_TH);
    }
    
    // =================================================================
    // === SỬA: XÓA HOÀN TOÀN HÀM baoCaoTongHop() CŨ
    // =================================================================
    /* // HÀM CŨ NÀY ĐÃ ĐƯỢC THAY THẾ BẰNG hienThiBaoCaoTongHop()
    private void baoCaoTongHop() {
        // ... (toàn bộ code cũ dùng JTextArea và JOptionPane đã bị xóa) ...
    }
    */


    // ====================== HÀM HỖ TRỢ ======================
    
    private int tinhTongSoLuongBan(Date tuNgay, Date denNgay) {
        List<HoaDon> listHD_raw = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        List<HoaDon> listHD = filterHoaDon(listHD_raw); // <<< LỌC HÓA ĐƠN
        
        if (listHD == null) return 0;
        int tong = 0;

        for (HoaDon hd : listHD) { // Dùng list đã lọc
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

 // Tìm và thay thế hàm locDuLieu() cũ bằng hàm này
    private void locDuLieu() {
        Date tuNgay = dateFrom.getDate();
        Date denNgay = getEndOfDay(dateTo.getDate()); // 23:59:59 của ngày được chọn

        // === SỬA LỖI TẠI ĐÂY ===
        // Thay vì lấy new Date() (giờ hiện tại), ta lấy "Cuối ngày hôm nay" để so sánh
        // Điều này đảm bảo nếu chọn "Hôm nay" (23:59:59) thì vẫn hợp lệ
        Date cuoiNgayHomNay = getEndOfDay(new Date()); 

        // 1. Kiểm tra chưa chọn ngày
        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn đầy đủ ngày bắt đầu và ngày kết thúc!", 
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Kiểm tra: Từ ngày KHÔNG ĐƯỢC lớn hơn Đến ngày
        if (tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(this, 
                "Ngày bắt đầu không được lớn hơn ngày kết thúc!", 
                "Lỗi thời gian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Kiểm tra tương lai
        // So sánh: Ngày chọn (23:59:59) > Ngày hôm nay (23:59:59) ?
        // Nếu bằng nhau (cùng là hôm nay) thì .after() trả về false -> Hợp lệ.
        if (tuNgay.after(cuoiNgayHomNay) || denNgay.after(cuoiNgayHomNay)) {
            JOptionPane.showMessageDialog(this, 
                "Thời gian chọn không được ở trong tương lai!", 
                "Lỗi thời gian", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Nếu tất cả hợp lệ thì mới chạy chức năng
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
            	writer.print("\uFEFF");
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

    // =================================================================
    // === SỬA: XÓA HÀM xuatFileText() VÌ KHÔNG CÒN SỬ DỤNG
    // =================================================================
    /**
     * THÊM MỚI: Hàm hỗ trợ xuất nội dung text ra file (cho Báo cáo tổng hợp)
     * @param content Nội dung text cần xuất
     * @param suggestedFileName Tên file gợi ý
     */
    /*
    // HÀM NÀY KHÔNG CÒN CẦN THIẾT
    private void xuatFileText(String content, String suggestedFileName) {
        // ... (code đã bị xóa) ...
    }
    */
    
    // ==========================================================
    // == HÀM MỚI: CÁC HÀM LỌC DỮ LIỆU
    // ==========================================================

    private List<HoaDon> filterHoaDon(List<HoaDon> rawList) {
        if (rawList == null) return new ArrayList<>();
        List<HoaDon> filteredList = new ArrayList<>();
        for (HoaDon hd : rawList) {
            if (hd != null && "Đã thanh toán".equalsIgnoreCase(hd.getStatus())) {
                filteredList.add(hd);
            }
        }
        return filteredList;
    }
    
    private List<NhapKho> filterNhapKho(List<NhapKho> rawList) {
        if (rawList == null) return new ArrayList<>();
        List<NhapKho> filteredList = new ArrayList<>();
        for (NhapKho nk : rawList) {
            if (nk != null && "Đã xác nhận".equalsIgnoreCase(nk.getStatus())) {
                filteredList.add(nk);
            }
        }
        return filteredList;
    }

    // ==========================================================
    // == HÀM MỚI: ÁP DỤNG THEME HIỆN ĐẠI CHO BIỂU ĐỒ
    // ==========================================================
    
    public void applyModernTheme(JFreeChart chart) {
        
        // === 1. ĐỊNH NGHĨA BẢNG MÀU TRẺ TRUNG ===
        final Paint[] PALETTE = new Paint[] {
            new Color(26, 188, 156),  // Teal
            new Color(52, 152, 219),  // Blue
            new Color(155, 89, 182),  // Purple
            new Color(241, 196, 15),  // Yellow
            new Color(230, 126, 34),  // Orange
            new Color(231, 76, 60),   // Red
            new Color(46, 204, 113)   // Green
        };

        // === 2. TÙY CHỈNH CHUNG ===
        Font axisFont = new Font("Segoe UI", Font.PLAIN, 12);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
        Font plotFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Tắt viền và nền mặc định
        chart.setBackgroundPaint(Color.WHITE);
        chart.setAntiAlias(true);
        chart.setTextAntiAlias(true);
        
        // Bỏ phần Chú thích (Legend) nếu có (biểu đồ của bạn không cần)
        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(Color.WHITE);
            chart.getLegend().setFrame(BlockBorder.NONE);
        }

        // === 3. TÙY CHỈNH VÙNG VẼ (PLOT) ===
        Plot plot = chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE); // Nền vùng vẽ
        plot.setOutlinePaint(null); // Bỏ đường viền quanh vùng vẽ

        if (plot instanceof CategoryPlot) {
            CategoryPlot categoryPlot = (CategoryPlot) plot;

            // Tùy chỉnh Gridlines (đường lưới)
            categoryPlot.setDomainGridlinesVisible(true);
            categoryPlot.setRangeGridlinesVisible(true);
            categoryPlot.setDomainGridlinePaint(new Color(235, 235, 235)); // Màu xám rất nhạt
            categoryPlot.setRangeGridlinePaint(new Color(220, 220, 220));

            // Tùy chỉnh Axes (trục)
            categoryPlot.getDomainAxis().setLabelFont(axisFont);
            categoryPlot.getDomainAxis().setTickLabelFont(axisFont);
            categoryPlot.getRangeAxis().setLabelFont(axisFont);
            categoryPlot.getRangeAxis().setTickLabelFont(axisFont);
            
            // ==========================================================
            // == <<< SỬA LỖI HIỂN THỊ SỐ (VD: 2E7) >>>
            // ==========================================================
            // Lấy trục Y (RangeAxis), là trục hiển thị số liệu
            org.jfree.chart.axis.NumberAxis rangeAxis = 
                (org.jfree.chart.axis.NumberAxis) categoryPlot.getRangeAxis();
                
            // Sử dụng lớp CustomNumberFormat mới để định dạng (lớp này ở cuối file)
            rangeAxis.setNumberFormatOverride(new CustomNumberFormat()); 
            // ==========================================================
            
            // === 4. TÙY CHỈNH RENDERER (THANH BAR, ĐƯỜNG LINE) ===
            
            if (categoryPlot.getRenderer() instanceof BarRenderer) {
                BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
                
                // **Quan trọng: Bỏ hiệu ứng gradient và đổ bóng**
                renderer.setBarPainter(new StandardBarPainter());
                renderer.setShadowVisible(false);
                
                // Tạo một renderer tùy chỉnh để TÔ MÀU CHO TỪNG CỘT
                class CustomBarRenderer extends BarRenderer {
                    @Override
                    public Paint getItemPaint(int row, int col) {
                        // Lấy màu từ bảng màu, xoay vòng
                        return PALETTE[col % PALETTE.length];
                    }
                }
                
                CustomBarRenderer customRenderer = new CustomBarRenderer();
                customRenderer.setBarPainter(new StandardBarPainter());
                customRenderer.setShadowVisible(false);
                customRenderer.setBaseItemLabelsVisible(false); // Tắt hiện số trên cột (nếu có)
                
                categoryPlot.setRenderer(customRenderer);
                
            } else if (categoryPlot.getRenderer() instanceof LineAndShapeRenderer) {
                LineAndShapeRenderer renderer = (LineAndShapeRenderer) categoryPlot.getRenderer();
                
                // Cho đường line đậm hơn
                renderer.setBaseStroke(new BasicStroke(2.5f));
                // Đặt màu cho series đầu tiên
                renderer.setSeriesPaint(0, PALETTE[1]); // Dùng màu Blue
            }
        }
    }
    
    // ==========================================================
    // == <<< CLASS MỚI: ĐỊNH DẠNG SỐ (BỎ E7, THAY BẰNG Tr/N) >>>
    // ==========================================================
    /**
     * Lớp nội bộ tùy chỉnh để định dạng các số lớn trên trục Y của biểu đồ.
     * Chuyển đổi 1,000,000 thành "1 Tr"
     * Chuyển đổi 100,000 thành "100 N"
     */
    static class CustomNumberFormat extends NumberFormat {
        private DecimalFormat df = new DecimalFormat("#.##"); // Định dạng số thập phân

        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            if (number >= 1_000_000_000) {
                toAppendTo.append(df.format(number / 1_000_000_000)).append(" Tỷ"); // Tỷ
            } else if (number >= 1_000_000) {
                toAppendTo.append(df.format(number / 1_000_000)).append(" Tr"); // Triệu
            } else if (number >= 1000) {
                toAppendTo.append(df.format(number / 1000)).append(" N"); // Nghìn
            } else {
                toAppendTo.append(df.format(number));
            }
            return toAppendTo;
        }

        @Override
        public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
            return format((double) number, toAppendTo, pos);
        }

        @Override
        public Number parse(String source, ParsePosition parsePosition) {
            return null; // Không cần thiết cho việc hiển thị biểu đồ
        }
    }
    private Date getEndOfDay(Date date) {
        if (date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    private void resetVaHienThiToanBo() {
        Calendar cal = Calendar.getInstance();
        cal.set(2020, Calendar.JANUARY, 1); 
        dateFrom.setDate(cal.getTime());
        dateTo.setDate(new Date());
        if (currentViewIndex == 0) {
            hienThiDashboard(); 
        } else if (currentViewIndex == 6) {
            hienThiBaoCaoTongHop();
        } else {
            locDuLieu();
        }
    }
    
}