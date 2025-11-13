from pydantic_settings import BaseSettings
import redis

class Settings(BaseSettings):
    REDIS_HOST: str = "172.19.240.57"
    REDIS_PORT: int = 6379
    EMBEDDING_MODEL_NAME: str = "google/vit-base-patch16-224-in21k"
    VECTOR_DIM: int = 128
    INDEX_NAME: str = "idx_image_vectors"
    DOC_PREFIX: str = "product-image-search:"
    HOST: str = "0.0.0.0"   
    PORT: int = 8000

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = False


settings = Settings()

r = redis.Redis(host=settings.REDIS_HOST, port=settings.REDIS_PORT, decode_responses=False)
