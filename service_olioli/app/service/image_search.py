import numpy as np
from redis.commands.search.query import Query
from ..config import r, settings
from ..model.vit_embedding import get_embedding


def search_top_k_spu_redis(model, query_image_path: str, top_k: int = 100):
    query_emb = get_embedding(model, query_image_path)
    if query_emb is None:
        return []

    query_vector_bytes = query_emb.astype(np.float32).tobytes()

    knn_query = f"*=>[KNN {top_k} @embedding $vec AS score]"
    query = (
        Query(knn_query)
        .return_fields("product_id", "category", "score")
        .dialect(2)
        .paging(0, top_k)
        .sort_by("score", asc=True)
    )

    results = r.ft(settings.INDEX_NAME).search(query, query_params={"vec": query_vector_bytes})

    selected = []
    seen_spus = set()
    for doc in results.docs:
        spu_id = doc.product_id.decode() if isinstance(doc.product_id, bytes) else doc.product_id
        if spu_id not in seen_spus:
            selected.append({
                "spu_id": spu_id,
                "score": 1.0 - float(doc.score),
            })
            seen_spus.add(spu_id)
        if len(selected) >= top_k:
            break

    return selected
