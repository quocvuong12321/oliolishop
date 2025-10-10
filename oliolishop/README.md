# 🚀 OLioliShop - Local Setup

## Header 2

### Header 3

**In đậm**
_In nghiên_

[link](http://localhost:8080)

`docker ps -a`
### 1. Pull code từ GitHub
```bash
git clone https://gitlab.com/quocvuong12321-group/oliolishop.git
cd oliolishop/oliolishop
```
### 2. Build Docker image

```bash
docker build -f Dockerfile -t oliolishop:0.0.1 .
```

### 3. Chạy container
#### -  Nếu chạy lần đầu
```bash
docker run -d --name olioli-service -e DB_HOST={ipaddress} -e REDIS_HOST={ipaddress} -p 8080:8080 oliolishop:0.0.1
```

#### - Nếu đã chạy trước đó và vừa pull code mới:
```bash
docker stop olioli-service
docker rm olioli-service
docker build -t oliolishop:0.0.1 .
docker run -d --name olioli-service -e DB_HOST={ipaddress} -e REDIS_HOST={ipaddress} -p 8080:8080 oliolishop:0.0.1

```

### 5. Ghi chú
#### - {ipaddress} là địa chỉ IP của database (MongoDB hoặc MySQL) và Redis container/server.
#### - Image oliolishop:0.0.1 sẽ được ghi đè khi build lại.
#### - Không cần xóa image thủ công — chỉ cần stop và rm container cũ trước khi chạy lại.
#### - Để xem log container:
#### - docker logs -f olioli-service