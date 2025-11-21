USE master;
GO

ALTER DATABASE ShoeStoreDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

DROP DATABASE ShoeStoreDB;
GO

-- Tạo CSDL
CREATE DATABASE ShoeStoreDB;
GO

USE ShoeStoreDB;
GO

-- Xóa các bảng (ĐẢM BẢO THỨ TỰ ĐÚNG ĐỂ TRÁNH LỖI KHÓA NGOẠI)
DROP TABLE IF EXISTS CHITIETPHANQUYEN;
DROP TABLE IF EXISTS PHANQUYEN;
DROP TABLE IF EXISTS CHITIETNHAPKHO;
DROP TABLE IF EXISTS NHAPKHO;
DROP TABLE IF EXISTS NHACUNGCAP;
DROP TABLE IF EXISTS CHITIETHOADON;
DROP TABLE IF EXISTS HOADON;
DROP TABLE IF EXISTS KHACHHANG;
DROP TABLE IF EXISTS NHANVIEN;
DROP TABLE IF EXISTS GIAY;
DROP TABLE IF EXISTS LOAIGIAY;
DROP TABLE IF EXISTS HANGGIAY;
DROP TABLE IF EXISTS CHUCNANG
GO

-- Bảng LOAIGIAY (Không thay đổi)
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

-- Bảng PHANQUYEN 
CREATE TABLE PHANQUYEN (
    idPQ NVARCHAR(10) PRIMARY KEY,
    tenQuyen NVARCHAR(50) UNIQUE,
    moTa NVARCHAR(255),
    status NVARCHAR(20) DEFAULT N'Hoạt động'
);
GO

-- Bảng NHANVIEN 
CREATE TABLE NHANVIEN (
    idNV NVARCHAR(10) PRIMARY KEY,
    tenNV NVARCHAR(100),
    ngaySinh DATE,
    gioiTinh NVARCHAR(10),
    sdt NVARCHAR(15),
    diaChi NVARCHAR(255),
    ngayVaoLam DATE, 
    username NVARCHAR(50),
    password NVARCHAR(50),
    idPQ NVARCHAR(10),
    status NVARCHAR(20) DEFAULT N'Hoạt động',
    FOREIGN KEY (idPQ) REFERENCES PHANQUYEN(idPQ)
);
GO

-- Bảng KHACHHANG (Đã thêm: ngaySinh, gioiTinh)
CREATE TABLE KHACHHANG (
    idKH NVARCHAR(10) PRIMARY KEY,
    tenKH NVARCHAR(100) NOT NULL, 
    ngaySinh DATE NULL,
    gioiTinh NVARCHAR(10) NULL,
    sdt NVARCHAR(15)  NOT NULL,
    diaChi NVARCHAR(255) NULL,
    tongTien FLOAT DEFAULT 0,
    status NVARCHAR(20) DEFAULT N'Hoạt động'
);


-- Bảng HOADON (Không thay đổi)
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

-- Bảng CHITIETHOADON (Không thay đổi)
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

-- Bảng NHACUNGCAP (Không thay đổi)
CREATE TABLE NHACUNGCAP (
    idNCC NVARCHAR(10) PRIMARY KEY,
    tenNCC NVARCHAR(50), -- Tăng độ rộng lên 50
    sdt NVARCHAR(15),
    email NVARCHAR(100),
    diaChi NVARCHAR(255),
    status NVARCHAR(20)
);
GO

-- Bảng NHAPKHO (Không thay đổi)
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

-- Bảng CHITIETNHAPKHO (Không thay đổi)
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

CREATE TABLE CHUCNANG (
    idCN NVARCHAR(10) PRIMARY KEY,
    tenChucNang NVARCHAR(100) UNIQUE,
    moTa NVARCHAR(255),
    allowThem BIT DEFAULT 1,
    allowSua BIT DEFAULT 1,
    status NVARCHAR(20) DEFAULT N'Hoạt động'
);
GO

-- Bảng CHITIETPHANQUYEN (Không thay đổi)
CREATE TABLE CHITIETPHANQUYEN (
    idPQ NVARCHAR(10),
    idCN NVARCHAR(10),
    duocXem BIT DEFAULT 0,
    duocThem BIT DEFAULT 0,
    duocSua BIT DEFAULT 0,
    PRIMARY KEY (idPQ, idCN),
    FOREIGN KEY (idPQ) REFERENCES PHANQUYEN(idPQ) ON DELETE CASCADE,
    FOREIGN KEY (idCN) REFERENCES CHUCNANG(idCN)
);
GO



--- DỮ LIỆU INSERT ĐÃ CHỈNH SỬA ---

INSERT INTO PHANQUYEN VALUES
('PQ001', N'Admin', N'Quản trị toàn bộ hệ thống', N'Hoạt động'),
('PQ002', N'Nhân viên', N'Nhân viên bán hàng cơ bản', N'Hoạt động'),
('PQ003', N'Kho', N'Nhân viên quản lý nhập kho', N'Hoạt động');
GO


INSERT INTO NHANVIEN (idNV, tenNV, ngaySinh, gioiTinh, sdt, diaChi, ngayVaoLam, username, password, idPQ, status) VALUES 
(N'NV001', N'Vũ Việt Hoàng', '1995-05-15', N'Nam', '0901234567', N'Hà Nội', '2022-01-10', 'hoang', '123456', N'PQ002', N'Hoạt động'),
(N'NV002', N'Nguyễn Đức Tài', '2000-11-20', N'Nam', '0987654321', N'TP.HCM', '2023-03-01', 'tai', '123456', N'PQ002', N'Hoạt động'),
(N'NV003', N'Tống Minh Quang', '1998-03-10', N'Nam', '0345123789', N'Đà Nẵng', '2023-06-15', 'quang', '123456', N'PQ002', N'Hoạt động'),
(N'NV004', N'Nguyễn Hữu Anh Khoa', '2002-08-28', N'Nam', '0777888999', N'Cần Thơ', '2024-01-20', 'khoa', '123456', N'PQ001', N'Hoạt động');
GO

-- Thêm Loại Giày (Không thay đổi)
INSERT INTO LOAIGIAY VALUES 
(N'LG001', N'Giày thể thao', N'Giày dành cho chơi thể thao', N'Hoạt động'),
(N'LG002', N'Giày da', N'Giày da cao cấp', N'Hoạt động'),
(N'LG003', N'Giày sneaker', N'Giày sneaker thời trang', N'Hoạt động');
GO

-- Thêm Hãng Giày (Không thay đổi)
INSERT INTO HANGGIAY VALUES 
(N'HG001', N'Nike', N'Thương hiệu thể thao nổi tiếng', N'Hoạt động'),
(N'HG002', N'Adidas', N'Thương hiệu thể thao Đức', N'Hoạt động'),
(N'HG003', N'Puma', N'Thương hiệu thể thao quốc tế', N'Hoạt động'),
(N'HG004', N'Converse', N'Thương hiệu giày sneaker', N'Hoạt động');
GO

INSERT INTO GIAY VALUES 
(N'G001', N'Nike Air Max 270', 42, 50, 2500000, N'Giày thể thao cao cấp', N'', N'LG001', N'HG001', N'Hoạt động'),
(N'G002', N'Adidas Ultraboost', 41, 30, 3200000, N'Giày chạy bộ chuyên nghiệp', N'', N'LG001', N'HG002', N'Hoạt động'),
(N'G003', N'Nike Air Force 1', 40, 45, 2800000, N'Giày sneaker cổ điển', N'', N'LG003', N'HG001', N'Hoạt động'),
(N'G004', N'Puma Suede Classic', 39, 25, 1800000, N'Giày sneaker phong cách', N'', N'LG003', N'HG003', N'Hoạt động'),
(N'G005', N'Converse Chuck', 38, 60, 1500000, N'Giày vải cổ điển', N'', N'LG003', N'HG004', N'Hoạt động');
GO

-- Thêm Nhà Cung Cấp (Tăng độ rộng tên NCC)
INSERT INTO NHACUNGCAP (idNCC, tenNCC, sdt, email, diaChi, status) VALUES
(N'NCC001', N'Công ty TNHH Nike Việt Nam', '02838292888', 'contact.vn@nike.com', N'Tòa nhà Metropolitan, 235 Đồng Khởi, Q.1, TP.HCM', N'Hoạt động'),
(N'NCC002', N'Tổng kho Sneaker Miền Nam', '0909123456', 'tongkhosneaker@gmail.com', N'Quận 10, TP.HCM', N'Hoạt động');
GO

INSERT INTO CHUCNANG (idCN, tenChucNang, moTa, allowThem, allowSua) VALUES
('CN001', N'Quản lý nhân viên', N'Chức năng quản lý nhân viên', 1,1),
('CN002', N'Quản lý giày', N'Chức năng quản lý sản phẩm giày', 1,1),
('CN003', N'Quản lý hóa đơn', N'Xem danh sách và chi tiết hóa đơn', 0,0),
('CN004', N'Quản lý kho', N'Xem và nhập hàng', 0,0),
('CN005', N'Quản lý khách hàng', N'Quản lý thông tin khách hàng', 1,1),
('CN006', N'Báo cáo thống kê', N'Xem thống kê doanh thu, hàng hóa', 0,0);
GO

INSERT INTO CHITIETPHANQUYEN (idPQ, idCN, duocXem, duocThem, duocSua) VALUES
-- PQ001: Admin (toàn quyền)
('PQ001', 'CN001', 1,1,1),
('PQ001', 'CN002', 1,1,1),
('PQ001', 'CN003', 1,1,1),
('PQ001', 'CN004', 1,1,1),
('PQ001', 'CN005', 1,1,1),
('PQ001', 'CN006', 1,1,1),

-- PQ002: Nhân viên bán hàng (chỉ xem/sửa KH + hóa đơn)
('PQ002', 'CN002', 1,0,0),
('PQ002', 'CN003', 1,0,0),
('PQ002', 'CN005', 1,1,1),
('PQ002', 'CN006', 1,0,0),

-- PQ003: Nhân viên kho
('PQ003', 'CN004', 1,1,1),
('PQ003', 'CN006', 1,0,0);
GO

