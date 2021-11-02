
Create database DuAnMau
go
use DuAnMau
go
CREATE TABLE NhanVien
(
MaNV NVARCHAR(20) NOT NULL,
MatKhau NVARCHAR(50) NOT NULL,
HoTen NVARCHAR(50) NOT NULL,
VaiTro  BIT DEFAULT 0 not null,
IDlist BIT DEFAULT 1,
PRIMARY KEY(MaNV)
)
go
CREATE TABLE ChuyenDe
(
 MaCD NCHAR(5) NOT NULL,
 TenCD NVARCHAR(50) NOT NULL,
 HocPhi FLOAT NOT NULL,
 ThoiLuong INT NOT NULL,
 Hinh NVARCHAR(50) NOT NULL,
 MoTa NVARCHAR(255) NOT NULL,
 IDlist BIT DEFAULT 1,
 PRIMARY KEY(MaCD)
)
go
CREATE TABLE NguoiHoc
(
MaNH NCHAR(7) NOT NULL,
HoTen NVARCHAR(50) NOT NULL,
NgaySinh DATE NOT NULL,
GioiTinh BIT DEFAULT 1 NOT NULL,
DienThoai NVARCHAR(24) NOT NULL,
Email NVARCHAR(50) NOT NULL,
GhiChu NVARCHAR(255) NOT NULL,
MaNV NVARCHAR(20) NOT NULL,
NgayDK DATE DEFAULT getdate() ,
PRIMARY KEY(MaNH),
FOREIGN KEY(MaNV) REFERENCES NhanVien(MaNV)  ON DELETE cascade ON UPDATE CASCADE
)
--delete from NguoiHoc where MaNH='NH02'
go
CREATE TABLE KhoaHoc
(
 MaKH INT IDENTITY(1,1) NOT NULL,
 MaCD NCHAR(5) NOT NULL,
 HocPhi FLOAT NOT NULL,
 ThoiLuong INT NOT NULL,
 NgayKG DATE NOT NULL,
 GhiChu NVARCHAR(255) NULL,
 MaNV NVARCHAR(20) NOT NULL,
 NgayTao DATE DEFAULT GETDATE() NOT NULL,
  IDlist BIT DEFAULT 1,
 PRIMARY KEY(MaKH),
 FOREIGN KEY(MaCD) REFERENCES ChuyenDe(MaCD) ON DELETE cascade ON UPDATE CASCADE,
 FOREIGN KEY(MaNV) REFERENCES NhanVien(MaNV) ON DELETE cascade ON UPDATE CASCADE
)

go
--drop table HocVien
CREATE TABLE HOCVIEN
(
MaHV INT IDENTITY(1,1) NOT NULL,
MaNH NCHAR(7) NOT NULL,
MaKH INT NOT NULL,
Diem Float DEFAULT -1 NOT NULL,
PRIMARY KEY(MaHV),
 FOREIGN KEY(MaNH) REFERENCES NguoiHoc(MaNH) ON DELETE no action ON UPDATE CASCADE ,
 FOREIGN KEY(MaKH) REFERENCES KhoaHoc(MaKH)  ON DELETE  CASCADE
)
--SELECT *FROM ChuyenDe
--DELETE FROM CHUYENDE WHERE MaCD='CD05'
--select * from KhoaHoc
--delete ChuyenDe where MaCD='CD02'
go
insert into NhanVien values (N'NV01','123',N'Đặng Văn Ngữ',0,1)
insert into NhanVien values (N'NV02','123',N'Đỗ Đức Dục',0,1)
insert into NhanVien values (N'TP03','123',N'Hoa Quang Sỹ',1,1)
go
insert into ChuyenDe values ('CD01',N'Photoshop',7000000,6,N'pts.png','A',1);
insert into ChuyenDe values ('CD02',N'Web',8000000,4,N'web.png','B',1)
insert into ChuyenDe values ('CD03',N'UDPM',4000000,5,N'udpm.png','C',1)
insert into ChuyenDe values ('CD04',N'MOB',5600000,3,N'mob.png','D',1)

go
insert into NguoiHoc values ('NH01',N'Đỗ Đức Toàn','01-02-2002',N'1','035743232',N'toandd13@gmail.com',N'Không','NV01','10-10-2010');
insert into NguoiHoc values ('NH02',N'Lan Hương','05-05-2002',N'0','0357343432',N'huongdd18@gmail.com',N'Không','NV02','10-10-2010');
insert into NguoiHoc values ('NH03',N'Hòa QUang Sỹ','07-01-2002',N'1','03223232',N'syhqdd223@gmail.com',N'Không','TP03','10-10-2009');
insert into NguoiHoc values ('NH04',N'Trần Tiến Mạnh','10-11-2002',N'1','01231555',N'manhh1233@gmail.com',N'Không','NV01','10-10-2010');
insert into NguoiHoc values ('NH06',N'Trần Tiến Mạnh','10-11-2002',N'1','01231555',N'manhh1233@gmail.com',N'Không','NV01',getdate());


go
insert into KhoaHoc  values ('CD01',7000000,6,'10-10-2012',N'Không','NV01','10-10-2011',1)
insert into KhoaHoc  values ('CD02',8000000,4,'11-10-2012',N'Không','NV02','10-10-2011',1)
insert into KhoaHoc  values ('CD03',4000000,5,'12-10-2012',N'Không','TP03','10-10-2011',1)
insert into KhoaHoc  values ('CD04',5600000,3,'02-10-2012',N'Không','NV01','10-10-2011',1)
insert into KhoaHoc  values ('CD04',5600000,3,'02-10-2012',N'Không','NV01','10-10-2022',1)
insert into KhoaHoc  values ('CD02',5600000,3,'02-10-2022',N'Không','NV01','10-10-2022',1)
select year(ngaytao) from KhoaHoc  group by(year(ngaytao))
go

insert into HocVien OUTPUT inserted.MaHV values ('NH01','01',8)
insert into HocVien OUTPUT inserted.MaHV values ('NH02','02',9)
insert into HocVien OUTPUT inserted.MaHV values ('NH03','03',8)
insert into HocVien OUTPUT inserted.MaHV values ('NH04','04',8.5)
SELECT *FROM HOCVIEN
select*from NguoiHoc
select*from ChuyenDe
select*from KhoaHoc
delete from KhoaHoc where MaKH=1
select*from NhanVien
delete from HOCVIEN where MaNH='NH01'
go
-- bang diem
CREATE PROC sp_BangDiem(@MaKH INT)
AS
BEGIN
	SELECT
		nh.MaNH,
		nh.HoTen,
		hv.Diem
	FROM HocVien hv
		JOIN NguoiHoc nh ON nh.MaNH=hv.MaNH
	WHERE hv.MaKH= @MaKH
	ORDER BY  hv.Diem DESC
END
GO
-- Doanh thu
--exec sp_ThongKeDoanhThu '2022'
CREATE PROC sp_ThongKeDoanhThu(@Year INT)
AS
BEGIN
SELECT
TenCD ChuyenDe,
COUNT(DISTINCT kh.MaKH) SoKH,
COUNT(hv.MaHV) SoHV,
SUM(kh.HocPhi) DoanhThu,
MIN(kh.HocPhi) ThapNhat,
MAX(kh.HocPhi) CaoNhat,
AVG(kh.HocPhi) TrungBinh
FROM KhoaHoc kh
JOIN HocVien hv ON kh.MaKH=hv.MaKH
JOIN ChuyenDe cd ON cd.MaCD=kh.MaCD
WHERE YEAR(NgayKG)= @Year
GROUP BY TenCD
END
-- luong nguoi hoc
Go
CREATE PROC sp_ThongKeNguoiHoc
AS
BEGIN
SELECT
YEAR(NgayDK) Nam,
COUNT(*) SoLuong,
MIN(NgayDK) DauTien,
MAX(NgayDK) CuoiCung
FROM NguoiHoc
GROUP BY YEAR(NgayDK)
END
-- diem chuyen de
Go
CREATE PROC sp_ThongKeDiem
AS
BEGIN
SELECT
TenCD ChuyenDe,
COUNT(MaHV) SoHV,
MIN(Diem) ThapNhat,
MAX(Diem) CaoNhat,
AVG(Diem) TrungBinh
FROM KhoaHoc kh
JOIN HocVien hv ON kh.MaKH=hv.MaKH
JOIN ChuyenDe cd ON cd.MaCD=kh.MaCD
GROUP
BY TenCD
END