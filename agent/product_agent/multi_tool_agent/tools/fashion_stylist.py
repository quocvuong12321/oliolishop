from google.adk.tools.function_tool import FunctionTool
from typing import Dict, Any, Optional
from google.adk.tools import google_search



class FashionStylistTool:
    """Tool: Stylist ảo – chuyên gia tư vấn thời trang thông minh."""

    def __init__(self):
        super().__init__()


    def suggest_outfit(
        self,
        gender: Optional[str] = None,
        age: Optional[int] = None,
        body_shape: Optional[str] = None,
        style: Optional[str] = None,
        occasion: Optional[str] = None,
        season: Optional[str] = None,
        budget_min: Optional[float] = None,
        budget_max: Optional[float] = None,
    ) -> Dict[str, Any]:
        """
        Tư vấn phong cách thời trang cho khách hàng dựa trên các yếu tố như:
        giới tính, phong cách, hoàn cảnh sử dụng, dáng người và ngân sách.

        Vai trò: bạn là một **stylist chuyên nghiệp**, có gu thẩm mỹ cao và hiểu biết
        về xu hướng thời trang toàn cầu.  
        Mục tiêu là giúp khách hàng:
        - Tìm phong cách phù hợp với ngoại hình & cá tính.
        - Hiểu lý do vì sao lựa chọn đó hợp lý.
        - Gợi ý mix & match (áo, quần, giày, phụ kiện).

        Args:
            gender (str): Giới tính ("male", "female", "unisex")
            age (int): Độ tuổi khách hàng
            body_shape (str): Dáng người ("slim", "curvy", "athletic", "average", ...)
            style (str): Phong cách mong muốn ("casual", "vintage", "minimalist", ...)
            occasion (str): Dịp sử dụng ("work", "party", "travel", ...)
            season (str): Mùa ("spring", "summer", "fall", "winter")
            budget_min (float): Ngân sách thấp nhất
            budget_max (float): Ngân sách cao nhất
        
            
        Returns:
            Dict[str, Any]: Gợi ý phối đồ và lời khuyên stylist.

        
        - Lưu ý các câu trả lời dạng liệt kê như 1. ... 2. ... thì nên thêm thẻ </br> để xuống dòng nha
        """

        # 🔹 Chuẩn bị phần mô tả yêu cầu để AI hiểu ngữ cảnh
        context = (
            f"Giới tính: {gender or 'không xác định'}, "
            f"Phong cách: {style or 'tùy chọn'}, "
            f"Dịp sử dụng: {occasion or 'hằng ngày'}, "
            f"Mùa: {season or 'tất cả'}, "
            f"Dáng người: {body_shape or 'chưa rõ'}, "
            f"Ngân sách: {budget_min or 'không giới hạn'} - {budget_max or 'không giới hạn'} VND."
        )

        advice = (
            "Dựa trên thông tin bạn cung cấp, tôi gợi ý phong cách phù hợp như sau:"
            "- Áo: sơ mi hoặc áo phông tông trung tính (trắng, be, hoặc xám) để dễ phối."
            "- Quần: chọn quần có form vừa vặn, ưu tiên chất liệu thoáng, "
            "có thể phối với quần jeans hoặc quần tây nhẹ."
            "- Giày: sneakers trắng hoặc loafer để tạo điểm nhấn tinh tế."
            "- Phụ kiện: đồng hồ hoặc túi chéo nhỏ để tạo phong cách hiện đại."
            "Nếu bạn muốn tôi tra cứu xu hướng mới nhất hoặc outfit tham khảo theo style này, "
        )

        return {
            "status": "success",
            "context": context,
            "stylist_advice": advice,
        }


# =========================================
# 🔹 Đăng ký FunctionTool cho AI Agent
# =========================================
stylist_tool_instance = FashionStylistTool()

suggest_outfit_tool = FunctionTool(
    stylist_tool_instance.suggest_outfit
)

