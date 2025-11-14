import pymysql
import os
from pymysql.err import MySQLError
from pathlib import Path
from dotenv import load_dotenv

# Load .env từ thư mục product_agent
env_path = Path(__file__).parent.parent / '.env'
load_dotenv(dotenv_path=env_path)

def get_db_connection():
    """Tạo kết nối đến MySQL database"""
    try:
        connection = pymysql.connect(
            host=os.getenv('DB_HOST', 'localhost'),
            port=int(os.getenv('DB_PORT', '3306')),
            user=os.getenv('DB_USER', 'root'),
            password=os.getenv('DB_PASSWORD', '12345'),
            database=os.getenv('DB_NAME', 'olioli'),
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor
        )
        return connection
    except MySQLError as e:
        print(f"Error connecting to MySQL: {e}")
        return None

def init_database():
    """Khởi tạo bảng history_chat nếu chưa tồn tại"""
    connection = get_db_connection()
    if not connection:
        return False
    
    try:
        with connection.cursor() as cursor:
            create_table_query = """
            CREATE TABLE IF NOT EXISTS history_chat (
                id INT AUTO_INCREMENT PRIMARY KEY,
                session_id VARCHAR(255) NOT NULL,
                customer_id VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                message TEXT NOT NULL,
                role ENUM('user', 'assistant') NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_session (session_id),
                INDEX idx_customer (customer_id),
                INDEX idx_username (username)
            )
            """
            cursor.execute(create_table_query)
        connection.commit()
        print("Database initialized successfully")
        return True
    except MySQLError as e:
        print(f"Error initializing database: {e}")
        return False
    finally:
        if connection:
            connection.close()
