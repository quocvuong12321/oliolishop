# Base image GPU với CUDA + cuDNN
FROM nvidia/cuda:12.8.0-cudnn-runtime-ubuntu22.04

# Cài đặt python
RUN apt-get update && apt-get install -y python3 python3-pip python3-venv git wget && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy project vào container
COPY . /app

# Upgrade pip
RUN pip3 install --upgrade pip

# Install CPU fallback or GPU specific
RUN pip3 install --no-cache-dir \
    fastapi==0.116.1 \
    uvicorn==0.35.0 \
    pillow==10.1.0 \
    numpy==1.26.0 \
    redis==5.3.1 \
    pydantic==2.11.7 \
    pydantic-settings==2.10.1 \
    transformers==4.53.2


RUN pip3 install --no-cache-dir \
    torch==2.7.1+cu128 \
    torchvision==0.22.1+cu128 \
    torchaudio==2.7.1+cu128 \
    --index-url https://download.pytorch.org/whl/cu128

# Tạo thư mục temp để lưu ảnh tạm
RUN mkdir -p /app/temp

# Copy model vào container
# Giả sử model để trong folder models/ViT_best_model.pth
COPY app /app

# Expose port FastAPI
EXPOSE 8000

# Khởi chạy server FastAPI
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]
