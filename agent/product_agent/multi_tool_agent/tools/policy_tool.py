from google.adk.tools.function_tool import FunctionTool
from typing import Dict, Any, Optional
from .base_tool import BaseAPITool
from .auth_context import auth_context
import logging


class PolicyTool(BaseAPITool):
    """
    Tool: Tra cứu chính sách của shop.

    HƯỚNG DẪN CHO AGENT:
    - Nếu người dùng hỏi về chính sách chung hoặc muốn xem các chính sách, hãy gọi API /policy để lấy danh sách và liệt kê cho khách hàng.
    - Nếu người dùng hỏi cụ thể về một chính sách (ví dụ: bảo mật, đổi trả...), hãy truyền item vào API /policy/{item}/pdf để lấy nội dung chính sách và tóm tắt lại cho khách hàng. Không cần trả về file PDF.
    - Nếu khách hàng hỏi thẳng chính sách nào đó mà không rõ item, hãy gọi API /policy để lấy danh sách, phân tích ngữ nghĩa câu hỏi để xác định đúng item phù hợp, sau đó tra cứu chi tiết bằng /policy/{item}/pdf hoặc tóm tắt như dưới nếu không lấy được nội dung.
    - Nếu không lấy được nội dung, hãy trả về mẫu tóm tắt mặc định cho khách hàng.
    """

    def get_policy_pdf(
        self,
        item: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        Tra cứu nội dung chính sách theo item và tóm tắt lại cho khách hàng.
        Không trả về file PDF, chỉ trả về nội dung tóm tắt.
        """
        if not item:
            return {"error": "Vui lòng cung cấp tên chính sách (item)."}

        api_result = self.get(
            "/policy/{item}/pdf",
            path_vars={"item": item}
        )

        # Nếu API trả về bytes PDF hoặc dict chứa text
        summary = None
        if isinstance(api_result, dict) and api_result.get("status") == "success":
            # Nếu API trả về nội dung text của chính sách
            text = api_result.get("result")
            if text:
                # Tóm tắt lại nội dung (hoặc trả về trực tiếp nếu đã là tóm tắt)
                summary = text
        # Nếu không lấy được nội dung, trả về mẫu tóm tắt mặc định
        if not summary:
            summary = (
                "Olioli cam kết bảo vệ thông tin cá nhân của khách hàng, chỉ sử dụng cho mục đích phục vụ và không chia sẻ với bên thứ ba, "
                "áp dụng các biện pháp bảo mật thanh toán. Nếu cần chi tiết hơn, vui lòng liên hệ bộ phận hỗ trợ khách hàng."
            )
        return {
            "status": "success",
            "policy_summary": summary
        }

    def get_all_policies(self) -> Dict[str, Any]:
        """
        Gọi API để lấy danh sách tất cả các chính sách.

        Returns:
            Dict: Danh sách chính sách hoặc lỗi
        """
        # Không cần lấy token
        api_result = self.get(
            "/policy"
        )

        if api_result["status"] != "success":
            error_msg = api_result.get("message", "Không thể lấy danh sách chính sách")
            return {"error": error_msg}

        return {
            "status": "success",
            "policies": api_result.get("result", [])
        }

# Tạo tool instance
policy_tool = PolicyTool()
fetch_policy_pdf_tool = FunctionTool(policy_tool.get_policy_pdf)
fetch_all_policies_tool = FunctionTool(policy_tool.get_all_policies)
