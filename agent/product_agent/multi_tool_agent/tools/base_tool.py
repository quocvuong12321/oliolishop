import os
import httpx
import logging
from typing import Dict, Any, Optional
from pathlib import Path
from dotenv import load_dotenv

logger = logging.getLogger(__name__)

# Load .env từ thư mục product_agent
env_path = Path(__file__).parent.parent.parent / '.env'
load_dotenv(dotenv_path=env_path)


class BaseAPITool:
    """Base class cho các tool gọi API"""
    
    def __init__(self):
        self.base_url = os.getenv('API_BASE_URL', 'http://localhost:8080/api')
        self.timeout = 30.0
    
    def get(
        self, 
        endpoint: str, 
        params: Optional[Dict[str, Any]] = None,
        token: Optional[str] = None,
        path_vars: Optional[Dict[str, Any]] = None  # Thêm path_vars
    ) -> Dict[str, Any]:
        """GET request với optional token và path variables"""
        headers = {}
        if token:
            headers['Authorization'] = f'Bearer {token}'
        
        # Replace path variables in endpoint
        if path_vars:
            for key, value in path_vars.items():
                endpoint = endpoint.replace(f"{{{key}}}", str(value))
        
        try:
            with httpx.Client(timeout=self.timeout) as client:
                response = client.get(
                    f"{self.base_url}{endpoint}",
                    params=params,
                    headers=headers
                )
                response.raise_for_status()
                
                # Parse Spring API response format
                response_json = response.json()
                logger.info(f"Raw API response: {response_json}")
                
                # Spring API format: {code, message, result, status}
                if "code" in response_json and "result" in response_json:
                    if response_json["code"] == 1000:  # Success code
                        return {
                            "status": "success",
                            "result": response_json["result"]
                        }
                    else:
                        return {
                            "status": "error",
                            "message": response_json.get("message", "Unknown error")
                        }
                
                # Fallback: treat as success if no Spring format
                return {
                    "status": "success",
                    "result": response_json
                }
                
        except httpx.HTTPStatusError as e:
            logger.error(f"HTTP error: {e}")
            logger.error(f"Response body: {e.response.text}")
            return {
                "status": "error",
                "message": f"HTTP {e.response.status_code}: {e.response.text}"
            }
        except Exception as e:
            logger.error(f"API call failed: {str(e)}")
            return {
                "status": "error",
                "message": str(e)
            }
    
    def post(
        self, 
        endpoint: str, 
        json_data: Optional[Dict[str, Any]] = None,
        token: Optional[str] = None
    ) -> Dict[str, Any]:
        """POST request với optional token"""
        headers = {}
        if token:
            headers['Authorization'] = f'Bearer {token}'
        
        try:
            with httpx.Client(timeout=self.timeout) as client:
                response = client.post(
                    f"{self.base_url}{endpoint}",
                    json=json_data,
                    headers=headers
                )
                response.raise_for_status()
                return {
                    "status": "success",
                    "result": response.json()
                }
        except httpx.HTTPStatusError as e:
            logger.error(f"HTTP error: {e}")
            return {
                "status": "error",
                "message": f"HTTP {e.response.status_code}: {e.response.text}"
            }
        except Exception as e:
            logger.error(f"API call failed: {str(e)}")
            return {
                "status": "error",
                "message": str(e)
            }
