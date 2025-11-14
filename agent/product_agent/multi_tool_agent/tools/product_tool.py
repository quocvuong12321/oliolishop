from google.adk.tools.function_tool import FunctionTool
from typing import Dict, Any, Optional
from .base_tool import BaseAPITool


class ProductTool(BaseAPITool):
    """Tool: Gọi API để lấy danh sách sản phẩm."""

    def fetch_products(
            self, 
            search_term: Optional[str] = None,
            min_price: Optional[float] = None,
            max_price: Optional[float] = None
                       
                       ) -> Dict[str, Any]:
        """
        Tool: Gọi API để lấy danh sách sản phẩm. Hãy phân tích câu hỏi của người dùng để xác định từ khóa tìm kiếm, khoảng giá (nếu có).
        
        Args: 
            search_term (Optional[str]): Từ khóa tìm kiếm sản phẩm.
            min_price (Optional[float]): Giá tối thiểu.
            max_price (Optional[float]): Giá tối đa.

        Returns:
            Dict[str, Any]: Dữ liệu gồm danh sách sản phẩm và thông tin phân trang.

        CÁCH XỬ LÝ KẾT QUẢ:
        1. Nhận dữ liệu sản phẩm từ API (nằm trong result.content)
        2. Phân tích và trình bày thông tin sản phẩm một cách rõ ràng
        3. Hiển thị giá cả theo định dạng: minPrice - maxPrice VND
        4. Nhóm sản phẩm theo danh mục khi có thể
        5. Nếu có lỗi, thông báo cho người dùng một cách chuyên nghiệp

        VÍ DỤ TRẢ LỜI:
        "Tôi tìm thấy X sản phẩm cho bạn. Dưới đây là một số sản phẩm nổi bật:
        1. Tên sản phẩm - Giá: XX,XXX - XX,XXX VND - Id: XXXXX
        2. Tên sản phẩm - Giá: XX,XXX VND - Id: XXXXX
        """
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


        api_result = self.get("/spu", params=params)

        if api_result["status"] != "success":
            return {"error": api_result["message"]}

        result_data = api_result["result"]
        products = result_data.get("content", [])

        return {
            "status": "success",
            "products": products,
            "total_products": len(products),
            "total_elements": result_data.get("totalElements", 0),
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