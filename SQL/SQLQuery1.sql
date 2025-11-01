-- Tạo CSDL
CREATE DATABASE ShoeStoreDB;
GO

USE ShoeStoreDB;
GO

-- Bảng LOAIGIAY
CREATE TABLE LOAIGIAY (
    idLoaiGiay NVARCHAR(10) PRIMARY KEY,
    tenLoaiGiay NVARCHAR(20),
    moTa NVARCHAR(255),
    status NVARCHAR(20)
);
GO

-- Bảng HANGGIAY
CREATE TABLE HANGGIAY (
    idHangGiay NVARCHAR(10) PRIMARY KEY,
    tenHangGiay NVARCHAR(20),
    moTa NVARCHAR(255),
    status NVARCHAR(20)
);
GO

-- Bảng GIAY
CREATE TABLE GIAY (
    idGiay NVARCHAR(10) PRIMARY KEY,
    tenGiay NVARCHAR(20),
    size FLOAT,
    soLuong INT,
    giaBan FLOAT,
    moTa NVARCHAR(255),
    hinhAnh NVARCHAR(255),
    idLoaiGiay NVARCHAR(10),
    idHangGiay NVARCHAR(10),
    status NVARCHAR(20),
    FOREIGN KEY (idLoaiGiay) REFERENCES LOAIGIAY(idLoaiGiay),
    FOREIGN KEY (idHangGiay) REFERENCES HANGGIAY(idHangGiay)
);
GO

-- Bảng NHANVIEN
CREATE TABLE NHANVIEN (
    idNV NVARCHAR(10) PRIMARY KEY,
    tenNV NVARCHAR(20),
    username NVARCHAR(50),
    password NVARCHAR(100),
    email NVARCHAR(100),
    phanQuyen NVARCHAR(20),
    status NVARCHAR(20)
);
GO

-- Bảng KHACHHANG
CREATE TABLE KHACHHANG (
    idKH NVARCHAR(10) PRIMARY KEY,
    tenKH NVARCHAR(20),
    sdt NVARCHAR(15),
    diaChi NVARCHAR(255),
    tongTien FLOAT,
    status NVARCHAR(20)
);
GO

-- Bảng HOADON
CREATE TABLE HOADON (
    idHD NVARCHAR(10) PRIMARY KEY,
    idNV NVARCHAR(10),
    idKH NVARCHAR(10),
    ngayLap DATE,
    tongTien FLOAT,
    status NVARCHAR(20),
    FOREIGN KEY (idNV) REFERENCES NHANVIEN(idNV),
    FOREIGN KEY (idKH) REFERENCES KHACHHANG(idKH)
);
GO

-- Bảng CHITIETHOADON
CREATE TABLE CHITIETHOADON (
    idCTHD NVARCHAR(10) PRIMARY KEY,
    idHD NVARCHAR(10),
    idGiay NVARCHAR(10),
    soLuong INT,
    donGia FLOAT,
    thanhTien FLOAT,
    status NVARCHAR(20),
    FOREIGN KEY (idHD) REFERENCES HOADON(idHD),
    FOREIGN KEY (idGiay) REFERENCES GIAY(idGiay)
);
GO

-- Bảng NHACUNGCAP
CREATE TABLE NHACUNGCAP (
    idNCC NVARCHAR(10) PRIMARY KEY,
    tenNCC NVARCHAR(28),
    sdt NVARCHAR(15),
    email NVARCHAR(100),
    diaChi NVARCHAR(255),
    status NVARCHAR(20)
);
GO

-- Bảng NHAPKHO
CREATE TABLE NHAPKHO (
    idNhapKho NVARCHAR(10) PRIMARY KEY,
    ngayNhap DATE,
    tongTien FLOAT,
    idNCC NVARCHAR(10),
    idNV NVARCHAR(10),
    status NVARCHAR(20),
    FOREIGN KEY (idNCC) REFERENCES NHACUNGCAP(idNCC),
    FOREIGN KEY (idNV) REFERENCES NHANVIEN(idNV)
);
GO

-- Bảng CHITIETNHAPKHO
CREATE TABLE CHITIETNHAPKHO (
    idCTNK NVARCHAR(10) PRIMARY KEY,
    idNhapKho NVARCHAR(10),
    idGiay NVARCHAR(10),
    soLuong INT,
    giaNhap FLOAT,      
    thanhTien FLOAT,
    status NVARCHAR(20),
    FOREIGN KEY (idNhapKho) REFERENCES NHAPKHO(idNhapKho),
    FOREIGN KEY (idGiay) REFERENCES GIAY(idGiay)
);
GO

DROP TABLE IF EXISTS CHITIETNHAPKHO;
DROP TABLE IF EXISTS NHAPKHO;
DROP TABLE IF EXISTS CHITIETHOADON;
DROP TABLE IF EXISTS HOADON;
DROP TABLE IF EXISTS GIAY;
DROP TABLE IF EXISTS LOAIGIAY;
DROP TABLE IF EXISTS HANGGIAY;
DROP TABLE IF EXISTS NHACUNGCAP;
DROP TABLE IF EXISTS KHACHHANG;
DROP TABLE IF EXISTS NHANVIEN;
GO

INSERT INTO NHANVIEN VALUES 
(N'NV001', N'Vũ Việt Hoàng', 'admin', '123456', 'hoangchebim123@gmail.com', N'Admin', N'active'),
(N'NV002', N'Nguyễn Đức Tài', 'user', '123456', 'taicungchebim123@gmail.com', N'User', N'active'),
(N'NV003', N'Tống Minh Quang', 'user', '123456', 'quangthichcontrai36@gmail.com', N'User', N'active'),
(N'NV004', N'Nguyễn Hữu Anh Khoa', 'admin', '123456', 'khoadz104@gmail.com', N'User', N'active');

-- Thêm Loại Giày
INSERT INTO LOAIGIAY VALUES 
(N'LG001', N'Giày thể thao', N'Giày dành cho chơi thể thao', N'active'),
(N'LG002', N'Giày da', N'Giày da cao cấp', N'active'),
(N'LG003', N'Giày sneaker', N'Giày sneaker thời trang', N'active');

-- Thêm Hãng Giày
INSERT INTO HANGGIAY VALUES 
(N'HG001', N'Nike', N'Thương hiệu thể thao nổi tiếng', N'active'),
(N'HG002', N'Adidas', N'Thương hiệu thể thao Đức', N'active'),
(N'HG003', N'Puma', N'Thương hiệu thể thao quốc tế', N'active'),
(N'HG004', N'Converse', N'Thương hiệu giày sneaker', N'active');

INSERT INTO GIAY VALUES 
(N'G001', N'Nike Air Max 270', 42, 50, 2500000, N'Giày thể thao cao cấp', N'', N'LG001', N'HG001', N'active'),
(N'G002', N'Adidas Ultraboost', 41, 30, 3200000, N'Giày chạy bộ chuyên nghiệp', N'', N'LG001', N'HG002', N'active'),
(N'G003', N'Nike Air Force 1', 40, 45, 2800000, N'Giày sneaker cổ điển', N'', N'LG003', N'HG001', N'active'),
(N'G004', N'Puma Suede Classic', 39, 25, 1800000, N'Giày sneaker phong cách', N'', N'LG003', N'HG003', N'active'),
(N'G005', N'Converse Chuck', 38, 60, 1500000, N'Giày vải cổ điển', N'', N'LG003', N'HG004', N'active');
GO

INSERT INTO KHACHHANG (idKH, tenKH, sdt, diaChi, tongTien, status) VALUES
('KH001', N'Trần Văn An', '0901234567', N'123 Nguyễn Trãi, Q.1, TP.HCM', 15000000, N'Active'),
('KH002', N'Lê Thị Bình', '0987654321', N'456 CMT8, Q.3, TP.HCM', 8500000, N'Active'),
('KH003', N'Phạm Văn Cường', '0345123789', N'Hà Nội', 10000000, N'Active'),
('KH004', N'Đỗ Thị Duyên', '0777888999', N'Khu phố 5, TP. Biên Hòa, Đồng Nai', 22500000, N'Inactive'),
('KH005', N'Hoàng Minh Hải', '0912345678', N'789 Lạc Long Quân, Q. Tây Hồ, Hà Nội', 3000000000, N'Active');
GO

INSERT INTO HOADON (idHD, idNV, idKH, ngayLap, tongTien, status) VALUES
('HD001', 'NV001', 'KH001', '2025-10-25', 5300000.00, N'Đã thanh toán'),
('HD002', 'NV002', 'KH003', '2025-10-26', 2800000.00, N'Chờ thanh toán'),
('HD003', 'NV003', 'KH002', '2025-10-27', 7900000.00, N'Đã thanh toán');
GO

INSERT INTO CHITIETHOADON (idCTHD, idHD, idGiay, soLuong, donGia, thanhTien, status) VALUES
-- HD001
('CT001', 'HD001', 'G001', 1, 2500000.00, 2500000.00, N'Hoàn thành'),
('CT002', 'HD001', 'G003', 1, 2800000.00, 2800000.00, N'Hoàn thành'),
-- HD002
('CT003', 'HD002', 'G003', 1, 2800000.00, 2800000.00, N'Hoàn thành'),
-- HD003
('CT004', 'HD003', 'G002', 1, 3200000.00, 3200000.00, N'Hoàn thành'),
('CT005', 'HD003', 'G004', 1, 1800000.00, 1800000.00, N'Hoàn thành'),
('CT006', 'HD003', 'G005', 1, 1500000.00, 1500000.00, N'Hoàn thành');
GO

-- Thêm Nhà Cung Cấp
INSERT INTO NHACUNGCAP (idNCC, tenNCC, sdt, email, diaChi, status) VALUES
(N'NCC001', N'Công ty TNHH Nike Việt Nam', '02838292888', 'contact.vn@nike.com', N'Tòa nhà Metropolitan, 235 Đồng Khởi, Q.1, TP.HCM', N'active'),
(N'NCC002', N'Tổng kho Sneaker Miền Nam', '0909123456', 'tongkhosneaker@gmail.com', N'Quận 10, TP.HCM', N'active');
GO

-- Thêm Phiếu Nhập Kho
INSERT INTO NHAPKHO (idNhapKho, ngayNhap, tongTien, idNCC, idNV, status) VALUES
(N'NK001', '2025-10-01', 255000000.00, N'NCC001', N'NV001', N'Đã hoàn thành'),
(N'NK002', '2025-10-02', 90000000.00, N'NCC002', N'NV004', N'Đã hoàn thành');
GO

-- Thêm Chi Tiết Nhập Kho (SỬA: donGia → giaNhap)
INSERT INTO CHITIETNHAPKHO (idCTNK, idNhapKho, idGiay, soLuong, giaNhap, thanhTien, status) VALUES
-- Chi tiết cho Phiếu NK001
(N'CTNK01', N'NK001', N'G001', 50, 1800000.00, 90000000.00, N'active'),
(N'CTNK02', N'NK001', N'G002', 30, 2500000.00, 75000000.00, N'active'),
(N'CTNK03', N'NK001', N'G003', 45, 2000000.00, 90000000.00, N'active'),
-- Chi tiết cho Phiếu NK002
(N'CTNK04', N'NK002', N'G004', 25, 1200000.00, 30000000.00, N'active'),
(N'CTNK05', N'NK002', N'G005', 60, 1000000.00, 60000000.00, N'active');
GO