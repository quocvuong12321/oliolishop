import numpy as np
from ..config import r, settings
from typing import List
from ..model.vit_embedding import get_embedding
import os
import shutil

class VectorService:
    @staticmethod
    def save_vector(image_url: str, product_id: str, embedding: np.ndarray) -> str:
        """
        Lưu 1 vector vào Redis với key = KEY_PREFIX + image_url
        Chỉ lưu field: product_id + embedding
        """
        key = f"{settings.DOC_PREFIX}{image_url}"
        r.hset(key, mapping={
            "product_id": product_id,
            "embedding": embedding.astype(np.float32).tobytes()
        })
        return key


    @staticmethod
    def add_vectors(model, image_urls: List[str], files: List) -> dict:
        """
        Nhận nhiều file + URL tương ứng, tạo embedding bằng model, lưu vào Redis.
        Trả về dict {'saved_keys': [...], 'errors': [...]}
        """
        temp_dir = "temp_upload"
        os.makedirs(temp_dir, exist_ok=True)

        saved_keys = []
        errors = []

        for file, image_url in zip(files, image_urls):
            temp_path = os.path.join(temp_dir, file.filename)
            try:
                # Lưu tạm file
                with open(temp_path, "wb") as buffer:
                    shutil.copyfileobj(file.file, buffer)

                # Tạo embedding bằng model
                embedding = get_embedding(model, temp_path)

                # Lấy product_id từ filename (giả sử dạng: productId_xxx.jpg)
                product_id = os.path.basename(image_url).split("_")[0]

                # Lưu vào Redis
                key = VectorService.save_vector(image_url, product_id, embedding)
                saved_keys.append(key)

            except Exception as e:
                errors.append({"file": file.filename, "error": str(e)})
            finally:
                if os.path.exists(temp_path):
                    os.remove(temp_path)

        return {"saved_keys": saved_keys, "errors": errors}
    
    @staticmethod
    def update_vectors(model, product_id: str,
                    deleted: List[str] | None,
                    new_files: List | None,
                    new_image_urls: List[str] | None) -> dict:
        """
        Cập nhật vector Redis khi update hình sản phẩm.
        - deleted: danh sách URL ảnh bị xóa
        - new_files: danh sách file ảnh mới (UploadFile)
        - new_image_urls: danh sách URL ảnh mới tương ứng với new_files
        """
        saved_keys = []
        deleted_keys = []
        errors = []

        # Đảm bảo các list luôn là list rỗng nếu None
        deleted = deleted or []
        new_files = new_files or []
        new_image_urls = new_image_urls or []

        # 1️⃣ Xóa vector của ảnh bị xóa
        if deleted:
            for image_url in deleted:
                key = f"{settings.DOC_PREFIX}{image_url}"
                try:
                    if r.exists(key):
                        r.delete(key)
                        deleted_keys.append(key)
                except Exception as e:
                    errors.append({"key": key, "error": str(e)})

        # 2️⃣ Thêm vector cho ảnh mới
        if new_files and new_image_urls:
            temp_dir = "temp_upload"
            os.makedirs(temp_dir, exist_ok=True)

            for file, image_url in zip(new_files, new_image_urls):
                temp_path = os.path.join(temp_dir, file.filename)
                try:
                    with open(temp_path, "wb") as buffer:
                        shutil.copyfileobj(file.file, buffer)

                    # Tạo embedding từ ảnh
                    embedding = get_embedding(model, temp_path)
                    key = VectorService.save_vector(image_url, product_id, embedding)
                    saved_keys.append(key)

                except Exception as e:
                    errors.append({"file": file.filename, "error": str(e)})
                finally:
                    if os.path.exists(temp_path):
                        os.remove(temp_path)

        # 3️⃣ Tổng kết kết quả
        result_summary = []
        if deleted_keys:
            result_summary.append(f"Đã xóa {len(deleted_keys)} vector.")
        if saved_keys:
            result_summary.append(f"Đã thêm {len(saved_keys)} vector mới.")
        if not deleted_keys and not saved_keys:
            result_summary.append("Không có thay đổi vector nào.")

        return {
            "status": "success" if saved_keys or deleted_keys else "no_change",
            "message": " ".join(result_summary),
            "saved_keys": saved_keys,
            "deleted_keys": deleted_keys,
            "errors": errors
        }
