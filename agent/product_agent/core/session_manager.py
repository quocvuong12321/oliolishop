import uuid
from typing import Union
from .database import get_db_connection
from pymysql.err import MySQLError

class SessionManager:
    """Quản lý session và lịch sử chat"""
    
    # Ngưỡng số tin nhắn tối đa cho mỗi session
    MAX_MESSAGES_PER_SESSION = 100
    # Số tin nhắn sẽ xóa khi đạt ngưỡng (xóa 20 tin nhắn cũ nhất)
    MESSAGES_TO_DELETE = 20
    
    @staticmethod
    def create_session(username: str, customer_id: str) -> str:
        """Tạo session mới dựa trên username"""
        session_id = f"{username}_{uuid.uuid4().hex}"
        return session_id
    
    @staticmethod
    def _cleanup_old_messages(session_id: str, connection) -> bool:
        """Xóa tin nhắn cũ nhất nếu session đạt ngưỡng"""
        try:
            with connection.cursor() as cursor:
                # Đếm số tin nhắn trong session
                count_query = """
                SELECT COUNT(*) as count FROM history_chat WHERE session_id = %s
                """
                cursor.execute(count_query, (session_id,))
                result = cursor.fetchone()
                message_count = result['count']
                
                # Nếu đạt ngưỡng, xóa tin nhắn cũ nhất
                if message_count >= SessionManager.MAX_MESSAGES_PER_SESSION:
                    delete_query = """
                    DELETE FROM history_chat 
                    WHERE session_id = %s 
                    ORDER BY created_at ASC 
                    LIMIT %s
                    """
                    cursor.execute(delete_query, (
                        session_id, 
                        SessionManager.MESSAGES_TO_DELETE
                    ))
                    connection.commit()
                    print(f"Cleaned up {SessionManager.MESSAGES_TO_DELETE} old messages from session {session_id}")
                    return True
                    
            return False
            
        except MySQLError as e:
            print(f"Error cleaning up old messages: {e}")
            return False
    
    @staticmethod
    def save_chat_history(session_id: str, customer_id: str, username: str, 
                         message: str, role: str = 'user') -> bool:
        """Lưu lịch sử chat vào database và tự động cleanup nếu cần"""
        connection = get_db_connection()
        if not connection:
            return False
        
        try:
            # Cleanup trước khi insert
            SessionManager._cleanup_old_messages(session_id, connection)
            
            with connection.cursor() as cursor:
                insert_query = """
                INSERT INTO history_chat (session_id, customer_id, username, message, role)
                VALUES (%s, %s, %s, %s, %s)
                """
                cursor.execute(insert_query, (session_id, customer_id, username, message, role))
            connection.commit()
            return True
            
        except MySQLError as e:
            print(f"Error saving chat history: {e}")
            return False
        finally:
            if connection:
                connection.close()
    
    @staticmethod
    def get_chat_history(session_id: str, limit: int = 50):
        """Lấy lịch sử chat theo session_id"""
        connection = get_db_connection()
        if not connection:
            return []
        
        try:
            with connection.cursor() as cursor:
                query = """
                SELECT id, session_id, customer_id, username, message, role, created_at
                FROM history_chat
                WHERE session_id = %s
                ORDER BY created_at ASC
                LIMIT %s
                """
                cursor.execute(query, (session_id, limit))
                results = cursor.fetchall()
                return results
            
        except MySQLError as e:
            print(f"Error fetching chat history: {e}")
            return []
        finally:
            if connection:
                connection.close()
    
    @staticmethod
    def get_session_message_count(session_id: str) -> int:
        """Lấy số lượng tin nhắn trong session"""
        connection = get_db_connection()
        if not connection:
            return 0
        
        try:
            with connection.cursor() as cursor:
                query = "SELECT COUNT(*) as count FROM history_chat WHERE session_id = %s"
                cursor.execute(query, (session_id,))
                result = cursor.fetchone()
                return result['count'] if result else 0
                
        except MySQLError as e:
            print(f"Error getting message count: {e}")
            return 0
        finally:
            if connection:
                connection.close()
    
    @staticmethod
    def delete_session_history(session_id: str) -> bool:
        """Xóa toàn bộ lịch sử của một session (dùng khi user muốn xóa cuộc hội thoại)"""
        connection = get_db_connection()
        if not connection:
            return False
        
        try:
            with connection.cursor() as cursor:
                delete_query = "DELETE FROM history_chat WHERE session_id = %s"
                cursor.execute(delete_query, (session_id,))
            connection.commit()
            return True
            
        except MySQLError as e:
            print(f"Error deleting session history: {e}")
            return False
        finally:
            if connection:
                connection.close()
    
    @staticmethod
    def get_or_create_user_session(username: str, customer_id: str) -> str:
        """Lấy session hiện tại của user, nếu không có thì tạo mới"""
        connection = get_db_connection()
        if not connection:
            return None
        
        try:
            with connection.cursor() as cursor:
                # Tìm session mới nhất của user
                query = """
                SELECT DISTINCT session_id
                FROM history_chat
                WHERE username = %s AND customer_id = %s
                """
                cursor.execute(query, (username, customer_id))
                result = cursor.fetchone()
                print(f"username: {username},customer: {customer_id}")
                if result:
                    print("Existing session found:", result['session_id'])
                    return result['session_id']
                else:
                    # Tạo session mới nếu chưa có
                    return SessionManager.create_session(username, customer_id)
                
        except MySQLError as e:
            print(f"Error getting or creating session: {e}")
            # Fallback: tạo session mới
            return SessionManager.create_session(username, customer_id)
        finally:
            if connection:
                connection.close()
