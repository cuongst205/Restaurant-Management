# Bộ câu hỏi gợi ý khi bảo vệ đồ án + câu trả lời mẫu (Tiếng Việt)

Tài liệu này tổng hợp các câu hỏi thường gặp khi thầy/cô hỏi về dự án Quản lý Nhà hàng (JavaFX, MVC, DAO, JDBC) và cung cấp câu trả lời mẫu ngắn gọn, bám sát mã nguồn hiện có.

Lưu ý:
- Đây là gợi ý để bạn luyện tập. Khi trả lời thực tế, nên chiếu code và mô tả theo luồng thực thi của chính bạn.
- Có thể tùy chỉnh ví dụ, số liệu theo dữ liệu CSDL của nhóm.

---

## 1) Tổng quan dự án
- Hỏi: Dự án của em làm gì, giải quyết vấn đề nào?
  - Đáp: Ứng dụng hỗ trợ quản lý nhà hàng: đăng nhập theo vai trò (admin/nhân viên), thao tác order theo bàn, lưu đơn và thanh toán (tiền mặt/QR), ghi hóa đơn và log, xuất báo cáo Excel. Mục tiêu là tối ưu quy trình bán hàng tại quầy.

- Hỏi: Kiến trúc tổng quan?
  - Đáp: JavaFX + FXML cho giao diện, Controller xử lý sự kiện, tầng DAO truy cập CSDL qua JDBC, Model nắm dữ liệu nghiệp vụ (Order, Payment, NhanVien...). Tách rõ View/Controller/DAO để dễ bảo trì.

- Hỏi: Vì sao chọn JavaFX?
  - Đáp: JavaFX hỗ trợ UI desktop giàu tương tác, FXML tách view và logic, hệ sinh thái Java quen thuộc, dễ tích hợp JDBC, phù hợp bài toán POS nội bộ.

## 2) Điều hướng màn hình & vòng đời ứng dụng
- Hỏi: Ứng dụng khởi động như thế nào?
  - Đáp: Main.java extends Application, trong start() nạp LoginView.fxml, set stylesheet và hiển thị Stage.

- Hỏi: Chuyển scene ra sao?
  - Đáp: Sử dụng Main.switchScene(fxmlPath); hàm này giữ nguyên kích thước cửa sổ hiện tại, nạp FXML và áp CSS.

## 3) Xác thực và phân quyền
- Hỏi: Đăng nhập xử lý thế nào?
  - Đáp: LoginController.handleLogin() lấy username/password, gọi NhanVienDAO.findByCredentials() trả về Optional<NhanVien>. Nếu có, ghi log và điều hướng theo vai trò.

- Hỏi: Phân quyền ở đâu?
  - Đáp: Dựa trên NhanVien.isAdmin(). Admin chuyển sang AdminView.fxml; nhân viên vào food.fxml.

- Hỏi: Lưu thời điểm đăng nhập?
  - Đáp: Với nhân viên, gọi NhanVienDAO.writeTimeLogin(id) sau khi điều hướng.

## 4) Mô hình dữ liệu & DAO
- Hỏi: Các model chính?
  - Đáp: NhanVien, Food, Order, OrderItem, Payment, LogEntry, Bill. Ví dụ Order tính totalPrice từ tổng OrderItem.

- Hỏi: DAO dùng để làm gì?
  - Đáp: Tách riêng truy cập CSDL: NhanVienDAO (auth, thông tin NV), FoodDAO (danh mục món), OrderDAO (lưu đơn), PaymentDAO (thanh toán), ReportDAO/SettingsDAO/Ba nkDAO hỗ trợ cấu hình/báo cáo.

- Hỏi: Kết nối DB triển khai ra sao?
  - Đáp: DatabaseConnection quản lý JDBC (ví dụ SQLite), cung cấp Connection cho các DAO.

## 5) Nghiệp vụ Order/Bán hàng
- Hỏi: Quy trình tạo đơn hàng?
  - Đáp: Tại food.fxml, người dùng chọn bàn, thêm món (Food) vào danh sách; khi lưu, OrderDAO ghi đơn và chi tiết; tổng tiền tính từ OrderItem::getTotalPrice.

- Hỏi: Xóa món, cập nhật số lượng thế nào?
  - Đáp: FoodController cho phép chọn item để xóa/cập nhật, sau đó tổng tiền tính lại dựa trên list hiện tại.

- Hỏi: Quản lý bàn thể hiện thế nào?
  - Đáp: Giao diện sinh các nút/badge đại diện bàn; chọn bàn hiện tại để gắn danh sách món và trạng thái.

## 6) Thanh toán & Hóa đơn
- Hỏi: Các phương thức thanh toán hỗ trợ?
  - Đáp: Tiền mặt và chuyển khoản qua QR. Thanh toán ghi nhận qua PaymentDAO và có thể in/ghi bill text.

- Hỏi: Sinh QR như thế nào?
  - Đáp: Lớp GetVietQR hỗ trợ lấy/nhúng ảnh QR (assets/vietqr.png) hiển thị trong popup thanh toán.

- Hỏi: Hóa đơn lưu ở đâu?
  - Đáp: Bill có thể ghi file text tại resources/view/assets/bills/bill.txt, đồng thời lưu Payment/Order vào DB.

## 7) Ghi log & Báo cáo
- Hỏi: Hệ thống log hoạt động?
  - Đáp: ActivityLogger ghi sự kiện như đăng nhập, thao tác admin. AdminView có thể xem/refresh log và xuất Excel qua ExportExcel.

- Hỏi: Xuất báo cáo Excel dùng gì?
  - Đáp: ExportExcel ghi dữ liệu (log/đơn/bán hàng) ra .xlsx bằng thư viện phù hợp (ví dụ Apache POI nếu áp dụng). File test.xlsx/log.xlsx trong repo minh họa.

## 8) Màn hình Admin
- Hỏi: Admin quản lý gì?
  - Đáp: Quản lý nhân viên (thêm/sửa/xóa), xem log, lưu thiết lập thanh toán/ngân hàng, xuất báo cáo, đăng xuất.

- Hỏi: Điều hướng sau đăng xuất?
  - Đáp: Gọi Main.switchScene() quay lại LoginView và ghi log.

## 9) Bảo mật & chất lượng mã
- Hỏi: Mật khẩu xử lý thế nào?
  - Đáp: Bản cơ sở minh họa xác thực trực tiếp qua DAO. Bản mở rộng khuyến nghị băm mật khẩu (BCrypt) và dùng tham số PreparedStatement để tránh SQL Injection.

- Hỏi: Tránh NullPointer ra sao?
  - Đáp: Dùng Optional trong findByCredentials, kiểm tra null trước khi thao tác, khởi tạo danh sách rỗng hợp lý.

## 10) Thiết kế UI & UX
- Hỏi: Vì sao dùng FXML?
  - Đáp: FXML giúp tách layout khỏi logic, dễ chỉnh giao diện, có thể tái sử dụng stylesheet (style.css) thống nhất.

- Hỏi: Giữ kích thước cửa sổ khi chuyển cảnh?
  - Đáp: Main.switchScene lưu width/height hiện tại và tạo Scene mới với kích thước đó.

## 11) Testing & đảm bảo chất lượng
- Hỏi: Em đã kiểm thử như thế nào?
  - Đáp: Kiểm thử thủ công các luồng chính: đăng nhập hợp lệ/sai, tạo đơn, xóa món, lưu đơn, thanh toán tiền mặt/QR, in bill, xem/refresh log, xuất Excel. Có thể bổ sung unit test cho DAO với DB test.

- Hỏi: Làm sao đảm bảo không vỡ chức năng khi thêm tính năng?
  - Đáp: Tách lớp rõ ràng (Controller/DAO/Model), giữ hợp đồng phương thức ổn định, thêm test hồi quy cơ bản và kiểm tra giao diện thủ công.

## 12) Triển khai & cấu hình
- Hỏi: Cách chạy dự án?
  - Đáp: Yêu cầu JDK phù hợp, Maven; chạy Main.java hoặc mvn javafx:run nếu cấu hình. CSDL dùng SQLite (identifier.sqlite/btljava1.sql) nên không cần dịch vụ ngoài.

- Hỏi: Cấu hình kết nối ở đâu?
  - Đáp: DatabaseConnection chịu trách nhiệm tạo Connection. Có thể chuyển sang file cấu hình/app properties khi triển khai thực tế.

## 13) Khả năng mở rộng & hướng phát triển
- Hỏi: Em sẽ mở rộng gì trong tương lai?
  - Đáp: Phân quyền chi tiết hơn (thu ngân, bếp), đồng bộ bếp/đơn theo thời gian thực, tích hợp cổng thanh toán xác thực tự động, báo cáo BI, in hóa đơn máy nhiệt 80mm, băm mật khẩu và chính sách đổi mật khẩu.

- Hỏi: Làm sao tách lớp để dễ mở rộng?
  - Đáp: Tuân thủ MVC, thêm service layer giữa Controller và DAO để gom nghiệp vụ, tạo interface DAO để dễ thay thế DB.

---

## Câu hỏi nâng cao (thầy hay xoáy sâu)
1) Vì sao tổng tiền Order cập nhật đúng khi sửa danh sách items?
   - Trả lời: Setter setItems() tính lại totalPrice = sum(OrderItem::getTotalPrice). Ngoài ra constructor cũng tính ngay khi khởi tạo.

2) Nếu chuyển scene nhiều lần, có rò rỉ tài nguyên không?
   - Trả lời: switchScene chỉ thay Scene trên cùng Stage, CSS dùng lại; FXML loader tạo scene mới nhưng không giữ reference không cần thiết. Có thể tối ưu thêm bằng preload và DI nếu cần.

3) Làm sao tránh SQL Injection?
   - Trả lời: Dùng PreparedStatement có tham số, không ghép chuỗi SQL trực tiếp; validate input phía UI.

4) Cách tổ chức log để phân trang/tìm kiếm?
   - Trả lời: Lưu log vào DB kèm timestamp, tạo DAO truy vấn theo khoảng thời gian, từ khóa; UI thêm pagination. Hiện tại demo hiển thị bảng đơn giản và export Excel.

5) Đảm bảo tính nhất quán khi lưu Order và Payment?
   - Trả lời: Dùng transaction trong JDBC: tắt autoCommit, lưu Order, lưu các OrderItem, lưu Payment, commit; nếu lỗi thì rollback.

6) QR thanh toán lấy từ đâu?
   - Trả lời: Từ GetVietQR và ảnh assets/vietqr.png. Có thể thay bằng API VietQR/Ngân hàng để tạo QR động theo số tiền.

7) Phục hồi sau lỗi DB?
   - Trả lời: Bọc try-catch ở DAO, log lỗi, hiển thị thông báo thân thiện; có thể thêm retry/backoff và kiểm tra kết nối trước khi thao tác.

---

## Mẹo trả lời khi bảo vệ (ngắn gọn)
- Nhắc đến cấu trúc MVC (View-FXML, Controller-logic, DAO-DB, Model-dữ liệu).
- Trình bày luồng nghiệp vụ end-to-end: đăng nhập → order → lưu đơn → thanh toán → log/bill.
- Nêu rõ thực hành tốt: Optional, PreparedStatement, tách lớp, CSS chung.
- Chỉ ra điểm có thể cải thiện: phân quyền chi tiết, transaction, test tự động, băm mật khẩu.

---

## Phụ lục: Dàn ý trả lời nhanh (template)
- Công nghệ: JavaFX, JDBC, Maven, FXML, CSS.
- Kiến trúc: MVC + DAO; có thể thêm service layer trong tương lai.
- DB: SQLite file, DAO quản lý truy vấn.
- Nghiệp vụ: Quản lý bàn, món, đơn, thanh toán, báo cáo.
- Bảo mật: Đề xuất BCrypt + PreparedStatement.
- Báo cáo: Export Excel; có thể thêm biểu đồ/BI.
- Mở rộng: phân quyền chi tiết, API QR động, in hóa đơn 80mm.
