# Image Search API - Vision Transformer Based Product Search

API tìm kiếm sản phẩm dựa trên hình ảnh sử dụng Vision Transformer (ViT) và Redis Vector Search.

## 📋 Tổng quan

Dự án này cung cấp một REST API cho phép:
- **Tìm kiếm sản phẩm tương tự** bằng cách upload hình ảnh
- **Thêm vector embedding** của sản phẩm mới vào database
- **Cập nhật vector embedding** khi thay đổi hình ảnh sản phẩm

API sử dụng mô hình Vision Transformer đã được fine-tune để tạo embedding 128 chiều cho mỗi hình ảnh, sau đó lưu trữ và tìm kiếm vector trong Redis.

## 🏗️ Kiến trúc hệ thống

### Sơ đồ tổng quan

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            FRONTEND (Angular)                           │
│                                                                         │
│  ┌──────────────────┐                                                   │
│  │  Image Upload    │  User uploads product image                       │
│  │  Component       │                                                   │
│  └────────┬─────────┘                                                   │
│           │                                                             │
│           │ HTTP Request (multipart/form-data)                          │
│           ▼                                                             │
└───────────┼─────────────────────────────────────────────────────────────┘
            │
            │
┌───────────▼──────────────────────────────────────────────────────────────┐
│                    SPRING BOOT BACKEND (Java)                            │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────┐         │
│  │  Product Controller                                         │         │
│  │  - Receives image from frontend                             │         │
│  │  - Validates request                                        │         │
│  └────────┬────────────────────────────────────────────────────┘         │
│           │                                                              │
│           │ Server-to-Server                                             │
│           ▼                                                              │
│  ┌─────────────────────────────────────────────────────────────┐         │
│  │  Image Search Service (RestTemplate/WebClient)              │         │ 
│  │  POST http://ai-service:8000/search                         │         │
│  │  - Forwards image to AI service                             │         │
│  └────────┬────────────────────────────────────────────────────┘         │
│           │                                                              │
│           │ Response: List<ProductId + Score>                            │
│           ▼                                                              │
│  ┌─────────────────────────────────────────────────────────────┐         │
│  │  Product Repository (JPA)                                   │         │
│  │  SELECT * FROM products WHERE id IN (...)                   │         │
│  └────────┬────────────────────────────────────────────────────┘         │
│           │                                                              │
└───────────┼──────────────────────────────────────────────────────────────┘
            │
            │ Query by Product IDs
            ▼
┌───────────────────────────────────────────────────────────────────────┐
│                         DATABASE (MySQL/PostgreSQL)                   │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │  Products Table                                         │          │
│  │  - product_id, name, price, description, images, ...    │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘

            │
            │ Product Details
            ▼
┌───────────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT BACKEND (Java)                         │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │  Response Builder                                       │          │
│  │  - Combines AI scores with product data                 │          │
│  │  - Sorts by similarity score                            │          │
│  └────────┬────────────────────────────────────────────────┘          │
│           │                                                           │
│           │ JSON Response                                             │
│           ▼                                                           │
└───────────┼───────────────────────────────────────────────────────────┘
            │
            │
┌───────────▼───────────────────────────────────────────────────────────┐
│                         FRONTEND (Angular)                            │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────┐          │
│  │  Search Results Display                                 │          │
│  │  - Render product cards with similarity scores          │          │
│  │  - Show product images, names, prices                   │          │
│  └─────────────────────────────────────────────────────────┘          │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘


            ┌──────────────────────────────────────────┐
            │  AI SERVICE (FastAPI + PyTorch)          │
            │  - Python ML Service                     │
            └──────────────┬───────────────────────────┘
                           │
                ┌──────────▼──────────┐
                │                     │
    ┌───────────▼──────────┐  ┌──────▼───────────┐
    │  Vision Transformer  │  │  Redis Vector DB │
    │  (ViT Model)         │  │  - Vector Index  │
    │  - Image Embedding   │  │  - KNN Search    │
    │  - 128D Vector       │  │  - Product IDs   │
    └──────────────────────┘  └──────────────────┘
```

### Flow chi tiết

**1. User tìm kiếm bằng hình ảnh:**
```
Frontend → Spring Boot → AI Service (FastAPI) → Redis → AI Service → Spring Boot → Database → Spring Boot → Frontend
```

**2. Xử lý trong AI Service:**
```
Image Upload → ViT Model → 128D Vector → Redis KNN Search → Top K Product IDs → Return to Spring Boot
```

**3. Xử lý trong Spring Boot:**
```
Product IDs → Query Database → Combine with Scores → Sort by Similarity → Return to Frontend
```
## 🏗️ Kiến trúc

```
service_olioli/
├── app/
│   ├── main.py              # FastAPI endpoints
│   ├── config.py            # Cấu hình và kết nối Redis
│   ├── schema.py            # Pydantic models
│   ├── ViT_best_model.pth   # Model weights (không commit)
│   ├── model/
│   │   └── vit_embedding.py # ViT model và embedding
│   └── service/
│       ├── image_search.py  # Logic tìm kiếm
│       └── vector_service.py # Quản lý vector trong Redis
├── requirements.txt
├── Dockerfile
└── .env
```

## 🚀 Cài đặt

### Yêu cầu hệ thống

- Python 3.10+
- Redis với RediSearch module
- CUDA 12.8+ (nếu sử dụng GPU)
- RAM: 8GB+ (16GB khuyến nghị)
- GPU: NVIDIA GPU với 4GB+ VRAM (tùy chọn)

### Cài đặt Local

1. **Clone repository và cài đặt dependencies:**

```bash
pip install -r requirements.txt
```

2. **Cấu hình biến môi trường:**

Tạo file [`.env`](.env) với nội dung:

```env
# Redis
REDIS_HOST=172.19.240.57
REDIS_PORT=6379

# Embedding model
EMBEDDING_MODEL_NAME=google/vit-base-patch16-224-in21k
VECTOR_DIM=128
INDEX_NAME=idx_image_vectors
DOC_PREFIX=product-image-search:

# Server
HOST=0.0.0.0
PORT=8000
```

3. **Đặt model weights:**

Đặt file `ViT_best_model.pth` vào thư mục `app/`

4. **Khởi động server:**

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

### Cài đặt với Docker

1. **Build Docker image:**

```bash
docker build -t image-search-api .
```

2. **Chạy container:**

```bash
docker run -d \
  --name image-search \
  --gpus all \
  -p 8000:8000 \
  -v $(pwd)/app/ViT_best_model.pth:/app/app/ViT_best_model.pth \
  --env-file .env \
  image-search-api
```

## 📚 API Documentation

### 1. Tìm kiếm sản phẩm tương tự

**Endpoint:** `POST /search`

**Parameters:**
- `file` (UploadFile): Hình ảnh cần tìm kiếm
- `top_k` (int, optional): Số lượng kết quả trả về (default: 100)

**Response:**

```json
{
  "status": "success",
  "message": "Tìm thấy 10 sản phẩm tương tự.",
  "total": 10,
  "results": [
    {
      "spu_id": "PROD123",
      "score": 0.95
    }
  ]
}
```

**Example:**

```bash
curl -X POST "http://localhost:8000/search?top_k=10" \
  -F "file=@product_image.jpg"
```

### 2. Thêm vector embedding sản phẩm mới

**Endpoint:** `POST /vector/add`

**Parameters:**
- `imageUrls` (List[str]): Danh sách URL của hình ảnh
- `files` (List[UploadFile]): Danh sách file hình ảnh tương ứng

**Response:**

```json
{
  "status": "success",
  "message": "Đã lưu 3 vector.",
  "results": [
    "product-image-search:PROD123_image1.jpg",
    "product-image-search:PROD123_image2.jpg"
  ],
  "errors": []
}
```

**Example:**

```bash
curl -X POST "http://localhost:8000/vector/add" \
  -F "imageUrls=PROD123_image1.jpg" \
  -F "imageUrls=PROD123_image2.jpg" \
  -F "files=@image1.jpg" \
  -F "files=@image2.jpg"
```

### 3. Cập nhật vector embedding sản phẩm

**Endpoint:** `POST /vector/update`

**Parameters:**
- `productSpuId` (str): ID sản phẩm
- `deletedImages` (List[str], optional): Danh sách URL ảnh cần xóa
- `newImageUrls` (List[str], optional): Danh sách URL ảnh mới
- `newFiles` (List[UploadFile], optional): Danh sách file ảnh mới

**Response:**

```json
{
  "status": "success",
  "message": "Đã xóa 1 vector cũ. Đã thêm 2 vector mới.",
  "saved_keys": ["product-image-search:PROD123_new1.jpg"],
  "deleted_keys": ["product-image-search:PROD123_old.jpg"],
  "errors": []
}
```

## 🔧 Cấu hình

### Redis Index

Redis index được sử dụng để lưu trữ và tìm kiếm vector. Cấu trúc:

- **Index Name:** `idx_image_vectors` (xem [`settings.INDEX_NAME`](app/config.py))
- **Key Prefix:** `product-image-search:` (xem [`settings.DOC_PREFIX`](app/config.py))
- **Vector Field:** `embedding` (FLAT index, 128 dimensions)
- **Metadata:** `product_id`

### Model

- **Base Model:** Vision Transformer ([`google/vit-base-patch16-224-in21k`](app/config.py))
- **Output Dimension:** 128D embedding (xem [`settings.VECTOR_DIM`](app/config.py))
- **Architecture:** Custom [`ViTEmbeddingModel`](app/model/vit_embedding.py) với linear projection layer

## 🛠️ Công nghệ sử dụng

- **Framework:** FastAPI 0.116.1
- **Deep Learning:** PyTorch 2.7.1, Transformers 4.53.2
- **Vector Database:** Redis 5.3.1 với RediSearch
- **Image Processing:** Pillow 10.1.0, torchvision 0.22.1
- **Validation:** Pydantic 2.11.7

## 📊 Performance

- **GPU:** NVIDIA CUDA 12.8 support
- **Inference Time:** ~50-100ms per image (on GPU)
- **Vector Search:** O(n) FLAT index, sub-second for millions of vectors

## 📝 License

Dự án này được phát triển cho mục đích học tập tại Khóa Luận Tốt Nghiệp HK7 HUIT.


---

**Note:** File `app/ViT_best_model.pth` không được commit vào Git (xem [`.gitignore`](.gitignore)). Vui lòng tải model weights riêng.