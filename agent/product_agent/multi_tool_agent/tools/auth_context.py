from typing import Optional
import threading

class AuthContext:
    """Thread-local storage for authentication token"""
    _instance = None
    _lock = threading.Lock()
    
    def __new__(cls):
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super().__new__(cls)
                    cls._instance._local = threading.local()
        return cls._instance
    
    def set_token(self, token: str):
        """Set authentication token for current thread"""
        self._local.token = token
    
    def get_token(self) -> Optional[str]:
        """Get authentication token for current thread"""
        return getattr(self._local, 'token', None)
    
    def clear_token(self):
        """Clear authentication token for current thread"""
        if hasattr(self._local, 'token'):
            delattr(self._local, 'token')


# Singleton instance
auth_context = AuthContext()
