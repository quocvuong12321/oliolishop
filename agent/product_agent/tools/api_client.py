import httpx
from typing import Optional, Dict, Any
import logging

logger = logging.getLogger(__name__)

class AuthenticatedAPIClient:
    """Client để call API với authentication token"""
    
    def __init__(self, base_url: str):
        self.base_url = base_url
        
    async def get(
        self, 
        endpoint: str, 
        token: Optional[str] = None,
        params: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """GET request với token"""
        headers = {}
        if token:
            headers['Authorization'] = f'Bearer {token}'
            
        async with httpx.AsyncClient() as client:
            try:
                response = await client.get(
                    f"{self.base_url}{endpoint}",
                    headers=headers,
                    params=params,
                    timeout=30.0
                )
                response.raise_for_status()
                return response.json()
            except Exception as e:
                logger.error(f"API call failed: {str(e)}")
                raise
                
    async def post(
        self, 
        endpoint: str, 
        token: Optional[str] = None,
        json_data: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """POST request với token"""
        headers = {}
        if token:
            headers['Authorization'] = f'Bearer {token}'
            
        async with httpx.AsyncClient() as client:
            try:
                response = await client.post(
                    f"{self.base_url}{endpoint}",
                    headers=headers,
                    json=json_data,
                    timeout=30.0
                )
                response.raise_for_status()
                return response.json()
            except Exception as e:
                logger.error(f"API call failed: {str(e)}")
                raise
