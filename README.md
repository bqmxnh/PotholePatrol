# Pothole Patrol - Ứng Dụng Phát Hiện và Báo Cáo Ổ Gà
## Giới thiệu 
**Pothole Patrol - Tuần Tra Ổ Gà** là một ứng dụng Android được phát triển nhằm hỗ trợ người dùng điều hướng, phát hiện và báo cáo ổ gà trên đường một cách hiệu quả. Ứng dụng sử dụng dữ liệu cảm biến gia tốc, GPS và bản đồ để thu thập, xử lý và hiển thị thông tin về ổ gà, giúp cơ quan chức năng khắc phục nhanh chóng, đồng thời nâng cao an toàn giao thông.
## Mục tiêu
- Tìm kiếm địa điểm và điều hướng.
- Phát hiện ổ gà tự động thông qua cảm biến gia tốc trên thiết bị di động.
- Thu thập dữ liệu trong quá trình di chuyển và xử lý thời gian thực.
- Ghi lại vị trí chính xác của ổ gà bằng GPS.
- Hiển thị thông tin trực quan trên bản đồ.
- Cho phép người dùng thêm báo cáo thủ công và xem thông tin chi tiết.
## Thiết kế giao diện tổng quan
![Cover](https://github.com/user-attachments/assets/60a1cb9e-829c-4802-bb1f-64bfe4b782e7)

## Công nghệ sử dụng
### Back-end
- **NodeJS:** Xây dựng REST API với framework Express.
- **MongoDB Atlas:** Cơ sở dữ liệu lưu trữ thông tin.
- **AWS EC2:** Dịch vụ VPS để triển khai API.
### Front-end
- **Figma:** Thiết kế giao diện người dùng.
- **Android Studio:** Môi trường phát triển ứng dụng Android.
- **Java:** Ngôn ngữ lập trình chính.
- **Bản đồ:**
  - **osmdroid:** Thư viện hiển thị bản đồ OpenStreetMap.
  - **Nominatim API:** Dịch vụ geocoding của OpenStreetMap để tìm kiếm địa điểm.
  - **OSRM API:** Dịch vụ định tuyến (OpenStreetMap Routing Machine) để dẫn đường.
- **Biểu đồ:** Thư viện MPAndroidChart để tạo biểu đồ dữ liệu.
## Tính năng chính
**1. Xác thực người dùng**
- Hỗ trợ đa ngôn ngữ: Tiếng Anh, Tiếng Việt, Tiếng Hàn, Tiếng Nhật.
- Các màn hình xác thực: Đăng nhập, Đăng ký, Khôi phục mật khẩu.

**2. Dashboard**
- Hiển thị thông tin tổng quan:
  - Số ổ gà đã phát hiện.
  - Quãng đường đã di chuyển.
  - Số lần va chạm.
  - Biểu đồ phân tích dữ liệu.
- Cho phép thêm báo cáo ổ gà mới.
- Xem thông báo (bao gồm thông báo bảo trì và thông báo hệ thống).

**3. Bản đồ và điều hướng**
- Hiển thị vị trí các ổ gà trên bản đồ.
- Tìm kiếm địa điểm và dẫn đường.
- Lọc thông tin hiển thị theo mức độ nghiêm trọng và thời gian.
- Cảnh báo khi người dùng đến gần ổ gà.
- Thông báo khi đã đến đích.
- Tự động phát hiện ổ gà qua cảm biến rung.
- Hỗ trợ zoom in/out và thêm ổ gà thủ công.

**4. Cài đặt**
- Chỉnh sửa thông tin cá nhân.
- Tùy chỉnh mức độ nhạy phát hiện ổ gà.
- Quản lý thông báo (âm thanh, rung).
- Cài đặt quyền riêng tư (chia sẻ dữ liệu).
- Báo cáo sự cố.
- Xem điều khoản sử dụng.
- Đăng xuất tài khoản.
# Giấy phép

Dự án được phát triển trong khuôn khổ môn học **Phát triển ứng dụng trên thiết bị di dộng**, với mục đích học tập và nghiên cứu.
