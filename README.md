# Shop Thời Trang Online Tích Hợp AI

Dự án này xây dựng một hệ thống thương mại điện tử thời trang trực tuyến, được tích hợp các công nghệ Trí tuệ Nhân tạo (AI) nhằm nâng cao trải nghiệm người dùng và hiệu quả tìm kiếm sản phẩm.

Hệ thống kết hợp giữa kiến trúc backend truyền thống và các thành phần AI hiện đại như chatbot thông minh và tìm kiếm sản phẩm dựa trên hình ảnh.

---

## 📌 Tổng quan dự án

Trong các nền tảng thương mại điện tử thời trang truyền thống, việc tìm kiếm sản phẩm chủ yếu dựa trên từ khóa văn bản. Tuy nhiên, người dùng thường gặp khó khăn khi không thể mô tả chính xác sản phẩm mong muốn bằng lời nói.

Dự án này giải quyết bài toán đó bằng cách:
- Cho phép tìm kiếm sản phẩm thông qua hình ảnh
- Hỗ trợ người dùng bằng chatbot AI có khả năng hội thoại tự nhiên

Hệ thống được chia thành **3 thành phần chính**:
- Backend Core (Java Spring Boot)
- AI Agent / Chatbot (Python + Google ADK)
- Tìm kiếm hình ảnh (ViT + Redis Vector Search)

---

## 🖥️ Backend Core (Java Spring Boot)

Backend Core chịu trách nhiệm xử lý toàn bộ nghiệp vụ và quản lý dữ liệu của hệ thống thương mại điện tử.

**Chức năng chính:**
- Quản lý người dùng, phân quyền và xác thực
- Quản lý sản phẩm, danh mục, đơn hàng
- Giỏ hàng, voucher và các logic liên quan đến thanh toán
- Cung cấp API cho frontend và các module AI

**Công nghệ sử dụng:**
- Java
- Spring Boot
- RESTful API
- Cơ sở dữ liệu quan hệ (MySQL)
- Xác thực bằng JWT

Backend đóng vai trò trung tâm, đảm bảo tính nhất quán dữ liệu, bảo mật và khả năng mở rộng hệ thống.

---

## 🤖 AI Agent & Chatbot (Python + Google ADK)

AI Agent giúp hỗ trợ người dùng trong quá trình mua sắm thông qua hội thoại tự nhiên.

**Chức năng chính:**
- Tư vấn và gợi ý sản phẩm
- Trả lời câu hỏi liên quan đến đơn hàng, chính sách
- Duy trì ngữ cảnh hội thoại
- Kết nối và truy xuất dữ liệu từ backend theo thời gian thực

**Công nghệ sử dụng:**
- Python
- Google Agent Development Kit (ADK)
- Mô hình ngôn ngữ lớn (LLM)
- Thiết kế prompt và workflow cho agent

Module AI Agent giao tiếp với backend thông qua API để đảm bảo thông tin chính xác và cập nhật.

---

## 🖼️ Tìm kiếm sản phẩm bằng hình ảnh (ViT + Redis)

Module tìm kiếm hình ảnh cho phép người dùng tìm sản phẩm dựa trên hình ảnh thay vì từ khóa.

**Quy trình hoạt động:**
1. Người dùng tải lên một hình ảnh sản phẩm
2. Hình ảnh được mã hóa thành vector đặc trưng bằng mô hình Vision Transformer (ViT)
3. Vector được so sánh với các vector sản phẩm đã lưu trữ
4. Hệ thống trả về các sản phẩm có độ tương đồng cao nhất

**Công nghệ sử dụng:**
- Vision Transformer (ViT)  
  - Mô hình pretrained: `google/vit-base-patch16-224-in21k`
  - Fine-tune nhẹ trên tập dữ liệu sản phẩm thời trang
- Redis Vector Database
- Chỉ mục HNSW với Cosine Similarity

**Ưu điểm:**
- Tốc độ tìm kiếm nhanh (phản hồi dưới 1 giây)
- Độ chính xác cao trong việc so khớp hình ảnh
- Khả năng mở rộng tốt với số lượng sản phẩm lớn

---

## 🚀 Điểm nổi bật của dự án

- Kết hợp hệ thống thương mại điện tử truyền thống với AI hiện đại
- Giải quyết bài toán “khoảng cách thị giác” trong tìm kiếm sản phẩm
- Thiết kế module hóa, dễ bảo trì và mở rộng
- Phù hợp cho triển khai thực tế và nghiên cứu học thuật

---

## 📚 Bối cảnh học thuật

Dự án được thực hiện trong khuôn khổ **khóa luận tốt nghiệp**, tập trung vào các hướng nghiên cứu:
- Ứng dụng AI trong thương mại điện tử
- Tìm kiếm thông tin dựa trên hình ảnh
- Hệ thống hội thoại thông minh

---

## 📂 Cấu trúc thư mục

Để dễ dàng điều hướng và phát triển, dự án được chia thành 3 thư mục chính tương ứng với các module chức năng:

### 1. `agent/`
Đây là project chứa phần **Chatbot AI hỗ trợ bán hàng**.
- **Chức năng:** Xử lý hội thoại với người dùng, sử dụng Google ADK để xây dựng luồng hội thoại và tích hợp LLM để tư vấn sản phẩm tự nhiên.
- **Ngôn ngữ:** Python.

### 2. `oliolishop/`
Đây là folder chứa **Backend Core** của hệ thống thương mại điện tử.
- **Chức năng:** Xử lý logic nghiệp vụ chính như quản lý sản phẩm, đơn hàng, người dùng, xác thực và cung cấp RESTful API cho các client và service khác.
- **Ngôn ngữ:** Java (Spring Boot).

### 3. `service_olioli/`
Đây là module **Tìm kiếm bằng hình ảnh**.
- **Chức năng:** Chứa mã nguồn để trích xuất đặc trưng hình ảnh (feature extraction) sử dụng Vision Transformer và thực hiện tìm kiếm vector tương đồng trên Redis.
- **Ngôn ngữ:** Python.

---

## 📄 Bản quyền

Dự án phục vụ cho mục đích học tập và nghiên cứu.
