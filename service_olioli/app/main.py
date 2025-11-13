from fastapi import FastAPI, UploadFile, File, HTTPException, status, Form
import shutil
import os
from .config import settings
from .model.vit_embedding import load_model
from .service.image_search import search_top_k_spu_redis
from .schema import ImageSearchResponse, ImageSearchResult
from .service.vector_service import VectorService
from typing import List


app = FastAPI(title="Image Search API")

# Load model khi khởi động server
MODEL_PATH = os.path.join(os.path.dirname(__file__), "ViT_best_model.pth")
model = load_model(MODEL_PATH, settings.EMBEDDING_MODEL_NAME, settings.VECTOR_DIM)


@app.get("/")
def root():
    return {"message": "Image Search API is running!"}


@app.post("/search", response_model=ImageSearchResponse)
async def search_image(file: UploadFile = File(...), top_k: int = 100):
    temp_path = f"temp/{file.filename}"
    os.makedirs("temp", exist_ok=True)
    with open(temp_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        results_raw = search_top_k_spu_redis(model, temp_path, top_k=top_k)
        os.remove(temp_path)

        if not results_raw:
            return ImageSearchResponse(
                status="success",
                message="Không tìm thấy sản phẩm phù hợp.",
                total=0,
                results=[]
            )

        results = [
            ImageSearchResult(
                spu_id=item["spu_id"],
                score=item["score"],
                category=item.get("category"),
                key=item.get("key")
            )
            for item in results_raw
        ]

        return ImageSearchResponse(
            status="success",
            message=f"Tìm thấy {len(results)} sản phẩm tương tự.",
            total=len(results),
            results=results
        )

    except Exception as e:
        if os.path.exists(temp_path):
            os.remove(temp_path)
        raise HTTPException(status_code=500, detail=str(e))
    

@app.post("/vector/add")
async def add_product_vectors(imageUrls: List[str] = Form(...), 
                              files: List[UploadFile] = File(...)):
    if not files:
        raise HTTPException(status_code=400, detail="Không có file ảnh nào được gửi.")

    result = VectorService.add_vectors(model, imageUrls, files)

    status = "success" if result["saved_keys"] else "failed"
    message = f"Đã lưu {len(result['saved_keys'])} vector." if result["saved_keys"] else "Không lưu được vector nào."


    return {
        "status": status,
        "message": message,
        "results": result["saved_keys"],
        "errors": result["errors"]
    }

@app.post("/vector/update")
async def update_product_vectors(
    productSpuId: str = Form(...),
    deletedImages: List[str] = Form(None),
    newImageUrls: List[str] = Form(None),
    newFiles: List[UploadFile] = File(None)
):
    # Nếu không có gì để cập nhật
    if not deletedImages and not newFiles:
        raise HTTPException(status_code=400, detail="Không có thay đổi nào để cập nhật vector.")

    result = VectorService.update_vectors(
        model=model,
        product_id=productSpuId,   # ✅ truyền product_id cho Redis
        deleted=deletedImages,
        new_files=newFiles,
        new_image_urls=newImageUrls
    )

    status = "success" if result["saved_keys"] or result["deleted_keys"] else "no_change"
    message_parts = []
    if result["saved_keys"]:
        message_parts.append(f"Đã lưu {len(result['saved_keys'])} vector mới.")
    if result["deleted_keys"]:
        message_parts.append(f"Đã xóa {len(result['deleted_keys'])} vector cũ.")
    if not message_parts:
        message_parts.append("Không có thay đổi vector nào.")

    return {
        "status": status,
        "message": " ".join(message_parts),
        "saved_keys": result["saved_keys"],
        "deleted_keys": result["deleted_keys"],
        "errors": result["errors"]
    }
