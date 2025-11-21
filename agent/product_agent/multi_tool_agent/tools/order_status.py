from google.adk.tools.function_tool import FunctionTool
from typing import Dict, Any, Optional
from .base_tool import BaseAPITool
from .auth_context import auth_context
import logging

logger = logging.getLogger(__name__)


class OrderStatusTool(BaseAPITool):
    """Tool: Gọi API để tra cứu đơn hàng (yêu cầu authentication)."""

    def get_order_status(
        self,
        order_id: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Gọi API để lấy trạng thái đơn hàng. Sử dụng khi người dùng cung cấp mã đơn hàng.
        
        Args:
            order_id (str): Mã đơn hàng (UUID format)

        Returns:
            Dict: Thông tin đơn hàng hoặc lỗi


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
        logger.info("=" * 80)
        logger.info("GET_ORDER_STATUS CALLED")
        logger.info(f"Order ID: {order_id}")
        logger.info("=" * 80)
       
        if not order_id:
            return {"error": "Vui lòng cung cấp mã đơn hàng (order_id)."}

        # Lấy token từ auth context
        token = auth_context.get_token()
        logger.info(f"Token available: {bool(token)}")
        
        if not token:
            logger.warning("No token in auth_context")
            return {
                "error": "Không tìm thấy thông tin xác thực. Vui lòng đăng nhập lại."
            }

        # Gọi API
        api_result = self.get(
            "/order/{id}", 
            path_vars={"id": order_id},
            token=token
        )
        
        if api_result["status"] != "success":
            error_msg = api_result.get("message", "Không thể lấy thông tin đơn hàng")
            if "401" in error_msg or "403" in error_msg:
                return {"error": "Phiên đăng nhập đã hết hạn."}
            return {"error": error_msg}

        return {
            "status": "success",
            "order": api_result["result"],
        }


# Tạo tool instance
order_status_tool = OrderStatusTool()
fetch_order_status_tool = FunctionTool(order_status_tool.get_order_status)