# Olioli Fashion Agent API

Trợ lý AI thời trang thông minh sử dụng Google Gemini để tư vấn phong cách, tìm kiếm sản phẩm và theo dõi đơn hàng.

## 📖 Giới thiệu

**Olioli Fashion Agent** là một API chatbot AI được xây dựng trên nền tảng FastAPI và Google Gemini, chuyên biệt phục vụ cho cửa hàng thời trang Olioli. Hệ thống giúp khách hàng:

- 🛍️ **Tìm kiếm sản phẩm thông minh**: Tìm sản phẩm theo từ khóa, giá cả, phong cách
- 📦 **Tra cứu đơn hàng**: Kiểm tra trạng thái và thông tin chi tiết đơn hàng
- 👔 **Tư vấn stylist AI**: Gợi ý phối đồ theo dáng người, phong cách và dịp sử dụng
- 💬 **Trò chuyện tự nhiên**: Hiểu ngôn ngữ tự nhiên, phản hồi như chuyên viên tư vấn thật

## ✨ Tính năng chính

### 1. Multi-tool AI Agent
- Tích hợp nhiều công cụ: tìm sản phẩm, tra đơn hàng, tư vấn thời trang
- Tự động chọn công cụ phù hợp dựa trên yêu cầu người dùng
- Xử lý ngữ cảnh và trả lời thông minh

### 2. Fashion Stylist AI
- Tư vấn phong cách theo giới tính, dáng người, độ tuổi
- Gợi ý outfit cho các dịp: đi làm, dự tiệc, đi chơi, hẹn hò
- Phối đồ theo xu hướng và ngân sách

### 3. Session Management
- Lưu trữ lịch sử chat trong MySQL
- Quản lý session theo user
- Tự động dọn dẹp tin nhắn cũ khi đạt ngưỡng

### 4. Authentication & Security
- Xác thực JWT token (tích hợp với Spring Boot)
- Bảo mật thông tin khách hàng
- Phân quyền truy cập API

## 🏗️ Kiến trúc hệ thống

```
┌──────────────────────────────────────────┐
│          Frontend Application            │
│         (Angular/Vue/Mobile App)           │
└─────────────────┬────────────────────────┘
                  │ JWT Token
                  ↓
┌──────────────────────────────────────────┐
│      Olioli Fashion Agent API            │
│           (FastAPI + Gemini)             │
│  ┌────────────────────────────────────┐  │
│  │  AI Agent (Google Gemini 2.0)     │  │
│  │  ┌──────────┬──────────┬────────┐ │  │
│  │  │ Product  │  Order   │Fashion │ │  │
│  │  │  Search  │  Status  │Stylist │ │  │
│  │  └──────────┴──────────┴────────┘ │  │
│  └────────────────────────────────────┘  │
└─────────┬───────────────────┬────────────┘
          │                   │
     ┌────┴────┐         ┌────┴─────┐
     ↓         ↓         ↓          ↓
┌─────────┐ ┌─────────┐ ┌──────────┐
│  MySQL  │ │ Spring  │ │  Google  │
│Database │ │Boot API │ │ Gemini   │
└─────────┘ └─────────┘ └──────────┘
```

## 🎯 Use Cases

### Khách hàng tìm sản phẩm
```
User: "Tôi muốn tìm áo sơ mi trắng giá dưới 500k"
Agent: "Tôi tìm thấy 8 sản phẩm áo sơ mi trắng trong khoảng giá của bạn:
        1. Áo sơ mi Oxford trắng - 350,000 - 450,000 VND
        2. Áo sơ mi cotton cao cấp - 420,000 VND
        ..."
```

### Khách hàng tra đơn hàng
```
User: "Cho tôi xem trạng thái đơn DH12345"
Agent: "Đơn hàng DH12345 của bạn:
        - Trạng thái: Đang giao hàng
        - Dự kiến: 15/01/2024
        - Sản phẩm: Áo sơ mi trắng x1, Quần jean x1
        - Tổng tiền: 850,000 VND"
```

### Khách hàng xin tư vấn
```
User: "Tư vấn outfit đi làm cho nam, phong cách lịch lãm"
Agent: "Tôi gợi ý phong cách smart casual cho bạn:
        - Áo: Sơ mi trắng/xanh nhạt
        - Quần: Quần tây slim fit màu be/xám
        - Giày: Oxford hoặc loafer da
        - Phụ kiện: Đồng hồ và thắt lưng da nâu
        
        Phong cách này vừa chuyên nghiệp vừa thoải mái!"
```

## 🔧 Công nghệ sử dụng

### Backend
- **FastAPI** 0.116.1 - Web framework hiệu năng cao
- **Google Gemini 2.0** - AI model xử lý ngôn ngữ tự nhiên
- **Google ADK** 1.8.0 - AI Development Kit
- **Uvicorn** 0.35.0 - ASGI server

### Database & Storage
- **MySQL** 8.0 - Lưu trữ lịch sử chat và session
- **PyMySQL** 1.1.1 - MySQL connector

### Authentication & Security
- **PyJWT** 2.10.1 - JWT token handling
- **Cryptography** 45.0.5 - Mã hóa dữ liệu

### DevOps
- **Docker** - Container hóa ứng dụng
- **Python** 3.11 - Ngôn ngữ lập trình

## 📊 Thống kê dự án

- **Số lượng tools**: 3 (Product Search, Order Status, Fashion Stylist)
- **Số endpoint API**: 8
- **Mô hình AI**: Google Gemini 2.0 Flash
- **Ngôn ngữ hỗ trợ**: Tiếng Việt
- **Session timeout**: 100 tin nhắn/session

## 🎨 Điểm nổi bật

### 1. Hiểu ngôn ngữ tự nhiên
AI agent có khả năng hiểu ngữ cảnh và ý định người dùng, không cần câu lệnh cứng nhắc.

### 2. Tư vấn như chuyên gia thật
Không chỉ tìm sản phẩm, agent còn giải thích lý do, gợi ý phối đồ và tư vấn phong cách.

### 3. Tích hợp đa nền tảng
Dễ dàng tích hợp vào website, mobile app hoặc chatbot platform qua REST API.

### 4. Quản lý session thông minh
Lưu trữ ngữ cảnh cuộc hội thoại, tự động dọn dẹp để tối ưu hiệu suất.

### 5. Bảo mật cao
Xác thực JWT, phân quyền truy cập, mã hóa dữ liệu nhạy cảm.

## 📞 Liên hệ & Hỗ trợ

- **Email**: support@olioli.com
- **Website**: https://olioli.com
- **Documentation**: `/docs` (Swagger UI)
- **API Reference**: `/redoc`

## 📄 License

MIT License - Tự do sử dụng cho mục đích học tập và thương mại.

---

**Olioli Fashion Agent** - Trợ lý AI thời trang thông minh, mang đến trải nghiệm mua sắm hoàn hảo! 🛍️✨
