import jwt
import os
from pathlib import Path
from dotenv import load_dotenv

# Load .env từ thư mục product_agent
env_path = Path(__file__).parent.parent / '.env'
load_dotenv(dotenv_path=env_path)

SECRET_KEY = os.getenv('SECRET_KEY', 'your-secret-key-here')

def decode_token(token: str) -> dict:
    """Giải mã JWT token và lấy thông tin user"""
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=['HS512'])
        return payload
    except jwt.ExpiredSignatureError:
        print("Token has expired")
        return None
    except jwt.InvalidTokenError:
        print("Invalid token")
        return None

def get_username_from_token(token: str) -> str:
    """Lấy username (sub) từ token"""
    payload = decode_token(token)
    if payload:
        return payload.get('sub')
    return None
