# Restaurant-Management
The Restaurant Management System is a software application developed to support the digitalization of restaurant operations. The system is designed using Object-Oriented Programming (OOP) principles, ensuring modularity, reusability, and scalability

# BTLJavaFX — Quản lý nhà hàng

Ứng dụng mẫu bằng JavaFX cho bài tập lớn (BTL). Ứng dụng sử dụng Java 23, JavaFX 21 và SQLite để quản lý nhân viên, đăng nhập, và một số chức năng xuất nhật ký/Excel.

## Nội dung
- Mã nguồn chính: `src/main/java/com/example/btljavafx`
- Giao diện (FXML/CSS): `src/main/resources/com/example/btljavafx/view`
- Script khởi tạo cơ sở dữ liệu SQLite: `btljava1.sql`
- File cấu hình Maven: `pom.xml`

## Yêu cầu (Prerequisites)
- Java JDK 23 (hoặc JDK tương ứng được cài theo `pom.xml`)
- Maven (hoặc dùng `mvnw`/`mvnw.cmd` có sẵn)
- (Tuỳ chọn) SQLite CLI nếu muốn chạy script SQL cục bộ

Lưu ý: project dùng Java module (`module-info.java`) và plugin `javafx-maven-plugin` để chạy.

## Cài đặt cơ sở dữ liệu
File kết nối tới cơ sở dữ liệu nằm ở `src/main/java/com/example/btljavafx/utils/DatabaseConnection.java`:

```java
private static final String DB_URL = "jdbc:sqlite:identifier.sqlite";
```

Hướng dẫn nhanh để tạo DB từ script (PowerShell):

1. Mở PowerShell và chuyển đến thư mục `BTLJavaFX`:

```powershell
cd D:\Code\Java\Restaurant-Management\BTLJavaFX
```

2. Nếu cài `sqlite3`, tạo file DB từ script:

```powershell
sqlite3 identifier.sqlite < btljava1.sql
```

Sau đó `identifier.sqlite` sẽ được tạo cùng thư mục. Nếu không muốn dùng tên `identifier.sqlite`, chỉnh hằng số `DB_URL` trong `DatabaseConnection.java` sang tên file DB bạn tạo, ví dụ `jdbc:sqlite:btljava1.sqlite`.

Nếu không có `sqlite3`, bạn có thể tạo file rỗng `identifier.sqlite` bằng cách tạo một file bình thường và import dữ liệu bằng công cụ GUI (DB Browser for SQLite) hoặc chỉnh URL để kết nối đến file DB hiện có.

## Chạy ứng dụng
Bạn có thể dùng Maven wrapper có sẵn trên Windows PowerShell.

1. Vào thư mục `BTLJavaFX`:

```powershell
cd D:\Code\Java\Restaurant-Management\BTLJavaFX
```

2. Chạy bằng Maven wrapper (Windows):

```powershell
.\mvnw.cmd clean javafx:run
```

Hoặc nếu dùng Maven đã cài:

```powershell
mvn clean javafx:run
```

Plugin đã cấu hình `mainClass` theo module: `com.example.btljavafx/com.example.btljavafx.app.Main`, nên `javafx:run` sẽ khởi chạy đúng entrypoint.

## Build / Package
- Đóng gói (sử dụng Maven):

```powershell
.\mvnw.cmd -DskipTests package
```

Lưu ý: vì project sử dụng module system và JavaFX, chạy trực tiếp jar tạo ra có thể cần truyền `--module-path` và `--add-modules` hoặc dùng plugin `javafx-maven-plugin` / jlink để tạo runtime image.

## Cấu trúc chính của project
- `com.example.btljavafx.app.Main` — entrypoint JavaFX
- `com.example.btljavafx.controller` — controller cho FXML (Login, Admin)
- `com.example.btljavafx.model` — model (Nhân viên, LogEntry...)
- `com.example.btljavafx.utils` — DatabaseConnection, ActivityLogger, DAO chung

BTLJavaFX • (Generated README)