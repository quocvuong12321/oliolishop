FROM python:3.10-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    curl \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

WORKDIR /app/product_agent

ENV PYTHONUNBUFFERED=1
ENV PYTHONPATH=/app

# Sửa EXPOSE cho khớp với port chạy
EXPOSE 8001

HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8001/api/chat/health || exit 1

CMD ["uvicorn", "product_agent.main:app", "--host", "0.0.0.0", "--port", "8001"]
