from google.adk.tools.function_tool import FunctionTool
from typing import Dict, Any, Optional
from .base_tool import BaseAPITool


class OrderStatusTool(BaseAPITool):
    """Tool: Gọi API để lấy danh sách sản phẩm."""

    def get_order_status(self, order_id: Optional[str] = None) -> Dict[str, Any]:
        """
        Tool: Gọi API để lấy ra trạng thái đơn hàng hiện tại
        
        Args: 
            order_id (Optional[str]): Mã đơn hàng cần tra cứu

        Returns:
            Dict[str, Any]: Dữ liệu gồm thông tin đơn hàng, danh sách sản phẩm (tên, giá, số lượng), tổng tiền, trạng thái đơn hàng.

        CÁCH XỬ LÝ KẾT QUẢ:
        1. Nhận dữ liệu sản phẩm từ API (nằm trong result)
        2. Phân tích và trình bày thông tin đơn hàng một cách rõ ràng
        3. Nếu có lỗi, thông báo cho người dùng một cách chuyên nghiệp

        VÍ DỤ TRẢ LỜI:
        "Tôi đã tìm thấy đơn {order_id} của bạn. Dưới đây là thông tin chi tiết:
        - Trạng thái đơn hàng: {status}
        - Sản phẩm:
          1. Tên sản phẩm - Giá: XX,XXX VND - Số lượng: X
          2. Tên sản phẩm - Giá: XX,XXX VND - Số lượng: X
        - Tổng tiền: XX,XXX VND
        - Địa chỉ giao hàng: {address}
        """
       
        if not order_id:
            return {"error": "Vui lòng cung cấp mã đơn hàng (order_id)."}

        api_result = self.get("/order/{id}", path_vars={"id": order_id})
        
        if api_result["status"] != "success":
            return {"error": api_result.get("message")}

        result_data = api_result["result"]
        order = result_data

        return {
            "status": "success",
            "order": order,
        }


# =========================================
# 🔹 Tạo FunctionTool để agent có thể gọi được
# =========================================
order_status_tool = OrderStatusTool()
fetch_order_status_tool = FunctionTool(order_status_tool.get_order_status)