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

public class QuanLyThongKe extends JPanel {
    private JTable tableThongKe;
    private DefaultTableModel modelThongKe;
    private JLabel lblTongDoanhThu, lblTongSoLuong, lblTongDonHang;
    private JDateChooser dateFrom, dateTo;
    private JComboBox<String> cboLoaiThongKe;

    private DAO_HoaDon hoaDonDAO;
    private DAO_ChiTietHoaDon chiTietHDDAO;
    private DAO_NhapKho nhapKhoDAO;
    private DAO_ChiTietNhapKho chiTietNKDAO;
    private DAO_Giay giayDAO;
    private DAO_KhachHang khachHangDAO;

    private DecimalFormat df = new DecimalFormat("#,###");
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private NhanVien currentUser;

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
    }

    public QuanLyThongKe(NhanVien user) {
        this();
        this.currentUser = user;
    }

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

        // Các nút menu
        String[] menuItems = {
            "Doanh thu theo ngày",
            "Doanh thu theo tháng",
            "Sản phẩm bán chạy",
            "Nhập hàng",
            "Top khách hàng",
            "Báo cáo tổng hợp"
        };

        Color[] colors = {
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

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        summaryPanel.add(createSummaryCard("Tổng doanh thu", "0 đ", new Color(46, 204, 113), "lblTongDoanhThu"));
        summaryPanel.add(createSummaryCard("Tổng đơn hàng", "0", new Color(52, 152, 219), "lblTongDonHang"));
        summaryPanel.add(createSummaryCard("Sản phẩm bán", "0", new Color(241, 196, 15), "lblTongSoLuong"));

        panel.add(summaryPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"STT", "Thông tin", "Giá trị"};
        modelThongKe = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableThongKe = new JTable(modelThongKe);
        tableThongKe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableThongKe.setRowHeight(30);
        tableThongKe.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableThongKe.getTableHeader().setBackground(new Color(52, 73, 94));
        tableThongKe.getTableHeader().setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tableThongKe);
        panel.add(scrollPane, BorderLayout.CENTER);

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

        // Lưu reference để update sau
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

    private void xuLyChucNang(int chucNang) {
        switch (chucNang) {
            case 0: thongKeDoanhThuTheoNgay(); break;
            case 1: thongKeDoanhThuTheoThang(); break;
            case 2: thongKeSanPhamBanChay(); break;
            case 3: thongKeNhapHang(); break;
            case 4: thongKeTopKhachHang(); break;
            case 5: baoCaoTongHop(); break;
        }
    }

    // helper: convert java.util.Date -> java.sql.Date (or null)
    private java.sql.Date toSqlDate(Date d) {
        return (d == null) ? null : new java.sql.Date(d.getTime());
    }

    // ====================== THỐNG KÊ DOANH THU THEO NGÀY ======================
    private void thongKeDoanhThuTheoNgay() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        if (tuNgay == null || denNgay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoảng thời gian!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) listHD = new ArrayList<>();

        // Group by date
        Map<String, Float> doanhThuTheoNgay = new TreeMap<>();
        Map<String, Integer> soDonTheoNgay = new TreeMap<>();

        for (HoaDon hd : listHD) {
            if (hd == null || hd.getNgayLap() == null) continue;
            String ngay = sdf.format(hd.getNgayLap());
            doanhThuTheoNgay.put(ngay, doanhThuTheoNgay.getOrDefault(ngay, 0f) + hd.getTongTien());
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

            modelThongKe.addRow(new Object[]{
                stt++,
                ngay,
                soDon,
                df.format(doanhThu) + " đ"
            });

            tongDoanhThu += doanhThu;
            tongDonHang += soDon;
        }

        // Update summary
        lblTongDoanhThu.setText(df.format(tongDoanhThu) + " đ");
        lblTongDonHang.setText(String.valueOf(tongDonHang));
        lblTongSoLuong.setText(df.format(tinhTongSoLuongBan(tuNgay, denNgay)));
    }

    // ====================== THỐNG KÊ DOANH THU THEO THÁNG ======================
    private void thongKeDoanhThuTheoThang() {
        modelThongKe.setRowCount(0);

        List<HoaDon> listHD = hoaDonDAO.getAll();
        if (listHD == null) listHD = new ArrayList<>();

        // Group by month
        Map<String, Float> doanhThuTheoThang = new TreeMap<>();
        Map<String, Integer> soDonTheoThang = new TreeMap<>();

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy");

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

        for (Map.Entry<String, Float> entry : doanhThuTheoThang.entrySet()) {
            String thang = entry.getKey();
            float doanhThu = entry.getValue();
            int soDon = soDonTheoThang.getOrDefault(thang, 0);

            modelThongKe.addRow(new Object[]{
                stt++,
                thang,
                soDon,
                df.format(doanhThu) + " đ"
            });

            tongDoanhThu += doanhThu;
            tongDonHang += soDon;
        }

        lblTongDoanhThu.setText(df.format(tongDoanhThu) + " đ");
        lblTongDonHang.setText(String.valueOf(tongDonHang));
        lblTongSoLuong.setText("N/A");
    }

    // ====================== THỐNG KÊ SẢN PHẨM BÁN CHẠY ======================
    private void thongKeSanPhamBanChay() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) listHD = new ArrayList<>();

        // Group by product
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

        // Sort by quantity
        List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(soLuongBan.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "SL bán", "Doanh thu"};
        modelThongKe.setColumnIdentifiers(columns);

        int stt = 1;
        int tongSL = 0;
        float tongDT = 0;

        for (Map.Entry<String, Integer> entry : sortedList) {
            String idGiay = entry.getKey();
            int soLuong = entry.getValue();
            float doanhThu = doanhThuSP.getOrDefault(idGiay, 0f);

            Giay giay = giayDAO.getById(idGiay);
            String tenGiay = (giay != null) ? giay.getTenGiay() : "N/A";

            modelThongKe.addRow(new Object[]{
                stt++,
                idGiay,
                tenGiay,
                soLuong,
                df.format(doanhThu) + " đ"
            });

            tongSL += soLuong;
            tongDT += doanhThu;
        }

        lblTongSoLuong.setText(String.valueOf(tongSL));
        lblTongDoanhThu.setText(df.format(tongDT) + " đ");
        lblTongDonHang.setText(String.valueOf(sortedList.size()));
    }

    // ====================== THỐNG KÊ NHẬP HÀNG ======================
    private void thongKeNhapHang() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        List<NhapKho> listNK = nhapKhoDAO.getByDate(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listNK == null) listNK = new ArrayList<>();

        String[] columns = {"STT", "Mã phiếu", "Ngày nhập", "Nhà cung cấp", "Tổng tiền", "Trạng thái"};
        modelThongKe.setColumnIdentifiers(columns);

        int stt = 1;
        float tongTien = 0;
        int tongSL = 0;

        for (NhapKho nk : listNK) {
            if (nk == null) continue;
            modelThongKe.addRow(new Object[]{
                stt++,
                nk.getIdNhapKho(),
                nk.getNgayNhap() == null ? "N/A" : sdf.format(nk.getNgayNhap()),
                nk.getIdNCC(),
                df.format(nk.getTongTien()) + " đ",
                nk.getStatus()
            });

            tongTien += nk.getTongTien();

            // Tính tổng số lượng nhập
            List<ChiTietNhapKho> listCT = chiTietNKDAO.getByNhapKho(nk.getIdNhapKho());
            if (listCT == null) continue;
            for (ChiTietNhapKho ct : listCT) {
                if (ct == null) continue;
                tongSL += ct.getSoLuong();
            }
        }

        lblTongDoanhThu.setText(df.format(tongTien) + " đ");
        lblTongDonHang.setText(String.valueOf(listNK.size()));
        lblTongSoLuong.setText(String.valueOf(tongSL));
    }

    // ====================== THỐNG KÊ TOP KHÁCH HÀNG ======================
    private void thongKeTopKhachHang() {
        modelThongKe.setRowCount(0);
        Date tuNgay = dateFrom.getDate();
        Date denNgay = dateTo.getDate();

        List<HoaDon> listHD = hoaDonDAO.getByDateRange(toSqlDate(tuNgay), toSqlDate(denNgay));
        if (listHD == null) listHD = new ArrayList<>();

        // Group by customer
        Map<String, Float> tongTienKH = new HashMap<>();
        Map<String, Integer> soDonKH = new HashMap<>();

        for (HoaDon hd : listHD) {
            if (hd == null) continue;
            String idKH = hd.getIdKH();
            tongTienKH.put(idKH, tongTienKH.getOrDefault(idKH, 0f) + hd.getTongTien());
            soDonKH.put(idKH, soDonKH.getOrDefault(idKH, 0) + 1);
        }

        // Sort by total
        List<Map.Entry<String, Float>> sortedList = new ArrayList<>(tongTienKH.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String[] columns = {"STT", "Mã KH", "Tên khách hàng", "Số đơn", "Tổng chi tiêu"};
        modelThongKe.setColumnIdentifiers(columns);

        int stt = 1;
        float tongDoanhThu = 0;

        for (Map.Entry<String, Float> entry : sortedList) {
            String idKH = entry.getKey();
            float tongTien = entry.getValue();
            int soDon = soDonKH.getOrDefault(idKH, 0);

            KhachHang kh = khachHangDAO.getById(idKH);
            String tenKH = (kh != null) ? kh.getTenKH() : "N/A";

            modelThongKe.addRow(new Object[]{
                stt++,
                idKH,
                tenKH,
                soDon,
                df.format(tongTien) + " đ"
            });

            tongDoanhThu += tongTien;
        }

        lblTongDoanhThu.setText(df.format(tongDoanhThu) + " đ");
        lblTongDonHang.setText(String.valueOf(sortedList.size()));
        lblTongSoLuong.setText("N/A");
    }

    // ====================== BÁO CÁO TỔNG HỢP ======================
    private void baoCaoTongHop() {
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

    private void locDuLieu() {
        // Tự động gọi lại chức năng đang hiển thị với bộ lọc mới
        JOptionPane.showMessageDialog(this,
            "Vui lòng chọn lại chức năng thống kê sau khi điều chỉnh bộ lọc!",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void xuatBaoCao() {
        if (modelThongKe.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Không có dữ liệu để xuất!",
                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
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
                    writer.print(modelThongKe.getColumnName(i));
                    if (i < modelThongKe.getColumnCount() - 1) writer.print(",");
                }
                writer.println();

                // Write data
                for (int i = 0; i < modelThongKe.getRowCount(); i++) {
                    for (int j = 0; j < modelThongKe.getColumnCount(); j++) {
                        writer.print(modelThongKe.getValueAt(i, j));
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
