from google.adk.tools.function_tool import FunctionTool
from typing import Dict, Any, Optional
from .base_tool import BaseAPITool
import logging

logger = logging.getLogger(__name__)


class ProductTool(BaseAPITool):
    """
    Tool: Gọi API để lấy danh sách sản phẩm.

    Khi người dùng nhập một câu hỏi hoặc yêu cầu dài, hãy phân tích và trích xuất ra các từ khóa quan trọng liên quan đến sản phẩm (ví dụ: tên sản phẩm, loại, thương hiệu, đặc điểm nổi bật, v.v). 
    Chỉ sử dụng các từ khóa này để gọi API tìm kiếm sản phẩm, không dùng toàn bộ câu hỏi.

    Nếu người dùng cung cấp khoảng giá, hãy lấy thông tin này để lọc sản phẩm theo giá.

    Args: 
        search_term (Optional[str]): Từ khóa tìm kiếm sản phẩm (chỉ lấy các từ khóa quan trọng, không dùng toàn bộ câu hỏi)
        min_price (Optional[float]): Giá tối thiểu
        max_price (Optional[float]): Giá tối đa

    Returns:
        Dict[str, Any]: Danh sách sản phẩm và thông tin phân trang

    CÁCH XỬ LÝ KẾT QUẢ:
    - LUÔN LUÔN TRẢ VỀ DẠNG DANH SÁCH SẢN PHẨM RÕ RÀNG VÀ CÓ ID SẢN PHẨM
    - Nhận dữ liệu sản phẩm từ API (nằm trong result.content)
    - Phân tích và trình bày thông tin sản phẩm một cách rõ ràng
    - Hiển thị giá cả theo định dạng: minPrice - maxPrice VND
    - Nhóm sản phẩm theo danh mục khi có thể
    - Nếu có lỗi, thông báo cho người dùng một cách chuyên nghiệp

    VÍ DỤ TRẢ LỜI:
    "Olioli tìm thấy X sản phẩm cho bạn. Dưới đây là một số sản phẩm nổi bật: </br>
    1. Tên sản phẩm - Giá: XX,XXX - XX,XXX VND - Id: <a href="http://localhost:4202/product/{product_id}">XXXX</a></br>
    2. Tên sản phẩm - Giá: XX,XXX VND - Id: <a href="http://localhost:4202/product/{product_id}">XXXX</a></br>
    "

    Nhớ có thẻ br để xuống dòng cho đẹp nhé
    """

    def fetch_products(
            self,
            search_term: Optional[str] = None,
            min_price: Optional[float] = None,
            max_price: Optional[float] = None
    ) -> Dict[str, Any]:
        params = {
            "page": 0, 
            "size": 10,
        }
        if search_term:
            params["search"] = search_term
        if min_price is not None:
            params["minPrice"] = min_price
        if max_price is not None:
            params["maxPrice"] = max_price

        # Gọi API
        api_result = self.get("/spu", params=params)
        

        if api_result["status"] != "success":
            logger.error(f"API error: {api_result.get('message')}")
            return {"error": api_result["message"]}

        result_data = api_result["result"]
        logger.info(f"Result data type: {type(result_data)}")
        
        # Spring API pagination format: result.content là array
        products = []
        total_elements = 0
        
        if isinstance(result_data, dict) and "content" in result_data:
            products = result_data.get("content", [])
            total_elements = result_data.get("totalElements", 0)
            logger.info(f"Pagination info: page={result_data.get('page')}, totalPages={result_data.get('totalPages')}")
        else:
            logger.warning(f"Unexpected result format: {result_data}")
            products = result_data if isinstance(result_data, list) else []
            total_elements = len(products)

        logger.info(f"Parsed {len(products)} products, total: {total_elements}")
        
        return {
            "status": "success",
            "products": products,
            "total_products": len(products),
            "total_elements": total_elements,
            "filters": {
                "search_term": search_term,
                "min_price": min_price,
                "max_price": max_price,
            },
        }


# =========================================
# 🔹 Tạo FunctionTool để agent có thể gọi được
# =========================================
product_tool_instance = ProductTool()
fetch_products_tool = FunctionTool(product_tool_instance.fetch_products)