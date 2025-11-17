import os
import requests
from typing import Dict, Any, Optional
from pathlib import Path
from dotenv import load_dotenv

# Load .env từ thư mục product_agent
env_path = Path(__file__).parent.parent.parent / '.env'
load_dotenv(dotenv_path=env_path)


class BaseAPITool:
    """Lớp cơ sở để gọi API (GET, POST, PUT, DELETE, PATCH) và chuẩn hóa phản hồi."""

    def __init__(self, base_url: Optional[str] = None):
        # Đọc từ environment variable
        self.base_url = base_url or os.getenv("API_BASE_URL", "http://localhost:8080/oliolishop/api")
        print(f"BaseAPITool initialized with base_url: {self.base_url}")

    # =============================
    # 🔹 Generic Request Handler
    # =============================
    def _request(
        self,
        method: str,
        endpoint: str,
        params: Optional[Dict[str, Any]] = None,
        data: Optional[Dict[str, Any]] = None,
        json: Optional[Dict[str, Any]] = None,
        path_vars: Optional[Dict[str, Any]] = None,
    ) -> Dict[str, Any]:
        """Hàm xử lý chung cho tất cả phương thức HTTP."""
        try:
            # Thay thế path variables (VD: /order/{id})
            if path_vars:
                endpoint = endpoint.format(**path_vars)

            url = f"{self.base_url}{endpoint}"

            response = requests.request(
                method=method.upper(),
                url=url,
                params=params,
                data=data,
                json=json,
                timeout=30
            )

            response.raise_for_status()
            data = response.json()

            # Chuẩn hóa phản hồi từ backend
            if data.get("code") != 1000:
                return self.error(data.get("message", "API trả về lỗi."))

            return self.success({
                "result": data.get("result", {}),
                "message": data.get("message", "Thành công."),
                "status_code": data.get("status", response.status_code)
            })

        except requests.Timeout:
            return self.error("Yêu cầu API quá thời gian chờ (timeout).")
        except requests.ConnectionError:
            return self.error("Không thể kết nối tới API (Connection Error).")
        except Exception as e:
            return self.error(f"Lỗi không xác định: {str(e)}")

    # =============================
    # 🔹 Các phương thức cụ thể
    # =============================
    def get(self, endpoint: str, **kwargs) -> Dict[str, Any]:
        return self._request("GET", endpoint, **kwargs)

    def post(self, endpoint: str, **kwargs) -> Dict[str, Any]:
        return self._request("POST", endpoint, **kwargs)

    def put(self, endpoint: str, **kwargs) -> Dict[str, Any]:
        return self._request("PUT", endpoint, **kwargs)

    def patch(self, endpoint: str, **kwargs) -> Dict[str, Any]:
        return self._request("PATCH", endpoint, **kwargs)

    def delete(self, endpoint: str, **kwargs) -> Dict[str, Any]:
        return self._request("DELETE", endpoint, **kwargs)

    # =============================
    # 🔹 Chuẩn hóa phản hồi
    # =============================
    def success(self, data: Dict[str, Any]) -> Dict[str, Any]:
        return {"status": "success", **data}

    def error(self, message: str) -> Dict[str, Any]:
        return {"status": "error", "message": message}
