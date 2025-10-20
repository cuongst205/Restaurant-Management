DROP TABLE IF EXIST "Nhân Viên";

CREATE TABLE "Nhân Viên" (
    id_Nhan_Vien_PK INTEGER PRIMARY KEY AUTOINCREMENT,
    "Họ tên" VARCHAR(100) NOT NULL,
    "Chức vụ" VARCHAR(50),
    "Mật khẩu" VARCHAR(100) NOT NULL
);

INSERT INTO "Nhân Viên" ("Họ tên", "Chức vụ", "Mật khẩu") 
VALUES ('Nguyễn Nam', 'Manager', 'admin');

SELECT * FROM "Nhân Viên";
