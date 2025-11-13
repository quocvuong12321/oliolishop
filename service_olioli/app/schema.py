from pydantic import BaseModel
from typing import List, Optional, Literal

class ImageSearchResult(BaseModel):
    spu_id: str
    score: float


class ImageSearchResponse(BaseModel):
    status: Literal["success", "error"]
    message: str
    total: int
    results: List[ImageSearchResult] = []

