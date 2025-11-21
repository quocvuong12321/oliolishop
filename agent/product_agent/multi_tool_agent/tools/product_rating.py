from google.adk.tools.function_tool import FunctionTool
from typing import Dict, Any, Optional
from .base_tool import BaseAPITool
import logging

logger = logging.getLogger(__name__)


class ProductRatingTool(BaseAPITool):
    """Tool: Gọi API để lấy đánh giá của sản phẩm."""

    def get_product_rating(
            self,
            product_id: str,
            page: int = 0,
            size: int = 30
    ) -> Dict[str, Any]:
        """
        Gọi API để lấy danh sách đánh giá của sản phẩm. 
        Khi người dùng tìm kiếm sản phẩm và muốn biết đánh giá từ sản phẩm đó thì 
        hãy lấy id từ sản phẩm mà bạn vừa tìm được và gọi tool này để lấy đánh giá.
        Args: 
            product_id (str): ID sản phẩm (bắt buộc)
            page (int): Trang hiện tại (mặc định 0)
            size (int): Số đánh giá mỗi trang (mặc định 20)

        Returns:
            Dict[str, Any]: Danh sách đánh giá và tóm tắt

        CÁCH XỬ LÝ KẾT QUẢ:
        1. Đọc và phân tích tất cả comments từ ratings
        2. Tóm tắt nội dung đánh giá:
           - Ưu điểm được nhắc đến nhiều
           - Nhược điểm (nếu có)
           - Nhận xét chung về chất lượng, giá cả, giao hàng

        VÍ DỤ TRẢ LỜI:
        
        Tóm tắt từ khách hàng:
        Ưu điểm:
        - Chất liệu tốt, mát mẻ
        - Đóng gói cẩn thận
        - Giá cả hợp lý
        
        Nhược điểm: (nếu có)
        - Giao hàng hơi chậm
        """
        logger.info("=" * 80)
        logger.info("GET_PRODUCT_RATING CALLED")
        logger.info(f"Product ID: {product_id}, Page: {page}, Size: {size}")
        logger.info("=" * 80)
        
        if not product_id:
            return {"error": "Vui lòng cung cấp ID sản phẩm."}

        params = {
            "page": page,
            "size": size,
        }

        # Gọi API
        api_result = self.get(f"/spu/detail/{product_id}/ratings", params=params)
        
        if api_result["status"] != "success":
            logger.error(f"API error: {api_result.get('message')}")
            return {"error": api_result.get("message", "Không thể lấy đánh giá")}

        ratings = api_result["result"]
        
        if not ratings or not isinstance(ratings, list):
            return {
                "status": "success",
                "total_ratings": 0,
                "average_star": 0,
                "ratings": [],
                "summary": "Chưa có đánh giá nào cho sản phẩm này."
            }

        # Phân tích đánh giá
        total_ratings = len(ratings)
        star_counts = {5: 0, 4: 0, 3: 0, 2: 0, 1: 0}
        total_stars = 0
        comments = []
        
        for rating in ratings:
            star = int(rating.get("star", 0))
            total_stars += star
            if star in star_counts:
                star_counts[star] += 1
            
            comment = rating.get("comment", "").strip()
            if comment:
                comments.append({
                    "star": star,
                    "comment": comment,
                    "customer_name": rating.get("customerName", "Khách hàng"),
                    "like_count": rating.get("likeCount", 0)
                })
        
        average_star = round(total_stars / total_ratings, 1) if total_ratings > 0 else 0
        
        # Tính phần trăm
        star_distribution = {}
        for star, count in star_counts.items():
            percentage = round((count / total_ratings) * 100, 1) if total_ratings > 0 else 0
            star_distribution[star] = {
                "count": count,
                "percentage": percentage
            }
        
        logger.info(f"Analyzed {total_ratings} ratings, average: {average_star}")
        
        return {
            "status": "success",
            "product_id": product_id,
            "total_ratings": total_ratings,
            "average_star": average_star,
            "star_distribution": star_distribution,
            "ratings": ratings[:10],  # Trả về top 10 ratings
            "comments": comments[:5],  # Top 5 comments để AI phân tích
            "page_info": {
                "current_page": page,
                "page_size": size
            }
        }


# Tạo tool instance
product_rating_tool = ProductRatingTool()
get_product_rating_tool = FunctionTool(product_rating_tool.get_product_rating)
