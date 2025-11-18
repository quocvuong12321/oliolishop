# Docker Setup Guide

## 📦 Trường hợp 1: Clone code lần đầu

### Bước 1: Clone repository

```bash
git clone <repository-url>
cd multi-agent
```

### Bước 2: Cấu hình môi trường

Tạo file `.env` từ template:

```bash
# Copy file template
cp .env.example product_agent/.env
```

Sửa file `product_agent/.env` với thông tin của bạn:

```properties
# Google API Key
GOOGLE_API_KEY=AIzaSyBMDjSHcHawGy3sj9hUQYVOu07fzptCiyo

# JWT Secret Key (phải giống Spring Boot)
SECRET_KEY=c248f3c5b1e84f8d1198d01488ee23a18360ce904c9eae484072de278cf2b6cc

# Backend API URL - IP/DOMAIN CỦA SPRING BOOT
API_BASE_URL=http://192.168.100.118:8080/oliolishop/api

# MySQL Configuration - THÔNG TIN MYSQL CỦA BẠN
DB_HOST=172.19.240.57
DB_PORT=3306
DB_USER=root
DB_PASSWORD=12345
DB_NAME=olioli
```

### Bước 3: Test cấu hình (Optional nhưng nên làm) tạo 1 file env để test rồi xóa cũng được 
```bash
# Cài đặt dependencies
pip install -r requirements.txt

# Test connection
python test_connection.py
```

Kết quả mong đợi:
```
==================================================
Product Agent Configuration Test
==================================================
Testing MySQL connection...
DB_HOST: 172.19.240.57
DB_PORT: 3306
DB_NAME: olioli
MySQL connection successful!

Testing Spring Boot API connection...
API_BASE_URL: http://192.168.100.118:8080/oliolishop/api
API connection successful! Status: 200

Testing Google API key...
Google API key found!

==================================================
Summary:
Database: ✅ PASS
API: ✅ PASS
Google API: ✅ PASS
==================================================

🎉 All tests passed! Ready to build Docker image.
```

### Bước 4: Build Docker Image

```bash
docker build -t product-agent:latest .
```

### Bước 5: Run Container

```bash
docker run -d \
  --name product-agent \
  -p 8001:8001 \
  product-agent:latest
```

### Bước 6: Kiểm tra Container

```bash
# Xem logs
docker logs -f product-agent

# Test API health check
curl http://localhost:8001/api/chat/health

# Truy cập API Docs
# Mở trình duyệt: http://localhost:8000/docs
```

---

## 🔄 Trường hợp 2: Pull code update từ Git

### Bước 1: Pull code mới

```bash
git pull origin main
```

### Bước 2: Kiểm tra thay đổi

#### 2.1. Kiểm tra requirements.txt có thay đổi không

```bash
git diff HEAD@{1} requirements.txt
```

Nếu có thay đổi, cần rebuild image.

#### 2.2. Kiểm tra Dockerfile có thay đổi không

```bash
git diff HEAD@{1} Dockerfile
```

Nếu có thay đổi, cần rebuild image.

#### 2.3. Kiểm tra code Python có thay đổi không

```bash
git diff HEAD@{1} product_agent/
```

Nếu chỉ code Python thay đổi (không có requirements.txt hay Dockerfile), cần rebuild.

### Bước 3: Rebuild và Deploy

#### Cách 1: Rebuild hoàn toàn (Recommended khi có thay đổi lớn)

```bash
# Stop container cũ
docker stop product-agent
docker rm product-agent

# Xóa image cũ (optional, để đảm bảo build clean)
docker rmi product-agent:latest

# Build image mới
docker build -t product-agent:latest .

# Run container mới
docker run -d \
  --name product-agent \
  -p 8000:8000 \
  product-agent:latest

# Xem logs
docker logs -f product-agent
```

#### Cách 2: Quick rebuild (Khi chỉ có thay đổi nhỏ)

```bash
# Stop và remove container cũ
docker stop product-agent
docker rm product-agent

# Build lại (sử dụng cache)
docker build -t product-agent:latest .

# Run lại
docker run -d --name product-agent -p 8000:8000 product-agent:latest
```

### Bước 4: Kiểm tra sau khi update

```bash
# 1. Kiểm tra container đang chạy
docker ps | grep product-agent

# 2. Xem logs
docker logs --tail 50 product-agent

# 3. Test API
curl http://localhost:8000/api/chat/health

# 4. Test endpoint cụ thể
curl -X GET "http://localhost:8000/" \
  -H "accept: application/json"
```

## Troubleshooting

### Lỗi thường gặp sau khi update

#### 1. Container không start được

```bash
# Xem logs chi tiết
docker logs product-agent

# Xem logs real-time
docker logs -f product-agent
```

#### 2. Import module error

```bash
# Rebuild without cache
docker build --no-cache -t product-agent:latest .
```

#### 3. Database connection error

```bash
# Kiểm tra file .env
cat product_agent/.env

# Test connection
python test_connection.py
```

#### 4. Port đã được sử dụng

```bash
# Tìm process đang dùng port 8000
# Windows:
netstat -ano | findstr :8000

# Linux/Mac:
lsof -i :8000

# Dừng container cũ
docker stop product-agent
docker rm product-agent
```

---

## 📋 Checklist

### Khi clone code lần đầu:
- [ ] Clone repository
- [ ] Copy `.env.example` thành `.env`
- [ ] Cấu hình thông tin trong `.env`
- [ ] Chạy `test_connection.py` để kiểm tra
- [ ] Build Docker image
- [ ] Run container
- [ ] Test API endpoints

### Khi pull code update:
- [ ] Pull code mới từ Git
- [ ] Kiểm tra file nào thay đổi (`git diff`)
- [ ] Stop container cũ
- [ ] Rebuild Docker image
- [ ] Run container mới
- [ ] Kiểm tra logs
- [ ] Test API endpoints
- [ ] Verify chức năng mới (nếu có)

---

## 🚀 Quick Commands

```bash
# Clone lần đầu - Full setup
git clone <repo-url> && cd multi-agent && \
cp .env.example product_agent/.env && \
docker build -t product-agent:latest . && \
docker run -d --name product-agent -p 8000:8000 product-agent:latest

# Update và deploy nhanh
git pull && \
docker stop product-agent && docker rm product-agent && \
docker build -t product-agent:latest . && \
docker run -d --name product-agent -p 8000:8000 product-agent:latest

# Xem logs
docker logs -f product-agent

# Restart container
docker restart product-agent

# Clean up và rebuild hoàn toàn
docker stop product-agent && docker rm product-agent && \
docker rmi product-agent:latest && \
docker build --no-cache -t product-agent:latest . && \
docker run -d --name product-agent -p 8000:8000 product-agent:latest
```
