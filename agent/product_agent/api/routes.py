from fastapi import APIRouter, HTTPException, Depends, Header
from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime
from pathlib import Path
from dotenv import load_dotenv
import jwt
import os
from multi_tool_agent import agent
from google.genai import types
from google.adk.runners import Runner
from google.adk.sessions import DatabaseSessionService

# Load .env
env_path = Path(__file__).parent.parent / '.env'
load_dotenv(dotenv_path=env_path)

# Thêm debug log
import logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Verify GOOGLE_API_KEY
logger.info(f"GOOGLE_API_KEY exists: {bool(os.getenv('GOOGLE_API_KEY'))}")

router = APIRouter()
db_url = os.getenv('DATABASE_URL')
agent_name = os.getenv('AGENT_NAME', 'olioli_fashion_assistant')

session_service = DatabaseSessionService(db_url)

runner = Runner(
    app_name=agent_name,
    agent=agent,
    session_service=session_service
)

# Models
class CreateSessionRequest(BaseModel):
    pass

class ChatRequest(BaseModel):
    session_id: str
    message: str

class ChatResponse(BaseModel):
    session_id: str
    user_message: str
    assistant_message: str
    timestamp: str

class SessionResponse(BaseModel):
    session_id: str
    username: str
    customer_id: str
    history: Optional[List["HistoryResponse"]] = None  # Thêm field history

class HistoryResponse(BaseModel):
    id: str
    session_id: str
    customer_id: str
    username: str
    message: str
    role: str
    created_at: str

# Dependency
async def get_current_user(authorization: Optional[str] = Header(None)):
    """Lấy thông tin user từ JWT token"""
    if not authorization:
        raise HTTPException(status_code=401, detail="Authorization header missing")
    
    try:
        token = authorization.split(" ")[1] if " " in authorization else authorization
        SECRET_KEY = os.getenv('SECRET_KEY', 'c248f3c5b1e84f8d1198d01488ee23a18360ce904c9eae484072de278cf2b6cc')
        payload = jwt.decode(token, SECRET_KEY, algorithms=['HS512'])
        return payload
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail="Token has expired")
    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")
    except Exception as e:
        raise HTTPException(status_code=401, detail=f"Authentication failed: {str(e)}")

@router.get("/endpoint")
async def root():
    """Root endpoint"""
    return {
        "message": "Product Agent Chat API",
        "version": "1.0.0",
        "endpoints": {
            "health": "/api/chat/health",
            "get_or_create_session": "GET /api/chat/session",
            "create_new_session": "POST /api/chat/session/create",
            "new_chat": "POST /api/chat/session/new",
            "chat": "POST /api/chat",
            "history": "GET /api/chat/history/{session_id}",
            "stats": "GET /api/chat/session/{session_id}/stats",
            "delete_session": "DELETE /api/chat/session/{session_id}"
        }
    }

@router.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "message": "Chat API is running"}


@router.post("/session/new", response_model=SessionResponse)
async def start_new_chat(current_user: dict = Depends(get_current_user)):
    """Bắt đầu cuộc hội thoại mới - xóa session cũ và tạo session mới"""
    username = current_user.get('sub')
    customer_id = current_user.get('customerId')
    
    if not username:
        raise HTTPException(status_code=400, detail="Username not found in token")
    
    if not customer_id:
        raise HTTPException(status_code=400, detail="Customer ID not found in token")
    
    try:
        user_id = str(customer_id)
        
        # Lấy danh sách sessions hiện có
        sessions_response = await session_service.list_sessions(
            app_name=agent_name,
            user_id=user_id
        )
        
        # Xóa tất cả sessions cũ
        for old_session in sessions_response.sessions:
            await session_service.delete_session(
                app_name=agent_name,
                user_id=user_id,
                session_id=old_session.id
            )
            logger.info(f"Deleted old session: {old_session.id}")
        
        # Tạo session mới
        new_session = await session_service.create_session(
            app_name=agent_name,
            user_id=user_id,
            state={"username": username, "customer_id": customer_id}
        )
        
        return SessionResponse(
            session_id=new_session.id,
            username=username,
            customer_id=customer_id
        )
    except Exception as e:
        logger.error(f"Error starting new chat: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Error starting new chat: {str(e)}")

@router.get("/session", response_model=SessionResponse)
async def get_or_create_session(
    current_user: dict = Depends(get_current_user),
    include_history: bool = True,  # Thêm param để load history
    history_limit: int = 50
):
    """Lấy session hiện tại của user, nếu chưa có thì tạo mới"""
    username = current_user.get('sub')
    customer_id = current_user.get('customerId')
    
    if not username:
        raise HTTPException(status_code=400, detail="Username not found in token")
    
    if not customer_id:
        raise HTTPException(status_code=400, detail="Customer ID not found in token")
    
    try:
        user_id = str(customer_id)
        
        # Lấy danh sách sessions
        sessions_response = await session_service.list_sessions(
            app_name=agent_name,
            user_id=user_id
        )
        
        session_id = None
        history_data = None
        
        if sessions_response.sessions:
            # Lấy session mới nhất
            latest_session = sessions_response.sessions[0]
            session_id = latest_session.id
            
            # Load history nếu được yêu cầu
            if include_history:
                from google.adk.sessions.base_session_service import GetSessionConfig
                config = GetSessionConfig(num_recent_events=history_limit)
                
                session = await session_service.get_session(
                    app_name=agent_name,
                    user_id=user_id,
                    session_id=session_id,
                    config=config
                )
                
                if session:
                    history_data = []
                    for event in session.events:
                        if event.content and event.content.parts:
                            message = ""
                            for part in event.content.parts:
                                if hasattr(part, 'text') and part.text:
                                    message += part.text
                            
                            history_data.append(HistoryResponse(
                                id=event.id,
                                session_id=session_id,
                                customer_id=customer_id,
                                username=username,
                                message=message,
                                role=event.content.role if hasattr(event.content, 'role') else event.author,
                                created_at=datetime.fromtimestamp(event.timestamp).isoformat()
                            ))
        else:
            # Tạo session mới nếu chưa có
            new_session = await session_service.create_session(
                app_name=agent_name,
                user_id=user_id,
                state={"username": username, "customer_id": customer_id}
            )
            session_id = new_session.id
            history_data = []  # Session mới không có history
        
        return SessionResponse(
            session_id=session_id,
            username=username,
            customer_id=customer_id,
            history=history_data
        )
    except Exception as e:
        logger.error(f"Error getting session: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Error getting session: {str(e)}")

@router.post("", response_model=ChatResponse)
async def chat(request: ChatRequest, current_user: dict = Depends(get_current_user)):
    """Gửi tin nhắn và nhận phản hồi từ agent"""
    username = current_user.get('sub')
    customer_id = current_user.get('customerId')
    
    if not username:
        raise HTTPException(status_code=400, detail="Username not found in token")
    
    if not customer_id:
        raise HTTPException(status_code=400, detail="Customer ID not found in token")
    
    try:
        user_id = str(customer_id)
        
        # Gọi agent sử dụng Runner
        try:
            logger.info(f"Calling agent with message: {request.message}")
            
            # Tạo message content
            new_message = types.Content(
                role='user',
                parts=[types.Part(text=request.message)]
            )
            
            # Collect response từ agent
            assistant_message = ""
            
            async for event in runner.run_async(
                user_id=user_id,
                session_id=request.session_id,
                new_message=new_message
            ):
                logger.info(f"Event author: {event.author}, partial: {event.partial}")
                
                # Parse event content - chỉ lấy final response
                if not event.partial and hasattr(event, 'content') and event.content:
                    if hasattr(event.content, 'parts') and event.content.parts:
                        for part in event.content.parts:
                            if hasattr(part, 'text') and part.text:
                                assistant_message += part.text
                                
            logger.info(f"Final agent response: {assistant_message}")
                
        except Exception as agent_error:
            logger.error(f"Error calling agent: {agent_error}", exc_info=True)
            assistant_message = f"Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn: {str(agent_error)}"
        
        return ChatResponse(
            session_id=request.session_id,
            user_message=request.message,
            assistant_message=assistant_message,
            timestamp=datetime.now().isoformat()
        )
        
    except Exception as e:
        logger.error(f"Error in chat endpoint: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Error processing chat: {str(e)}")

@router.get("/history/{session_id}", response_model=List[HistoryResponse])
async def get_history(
    session_id: str,
    limit: int = 50,
    current_user: dict = Depends(get_current_user)
):
    """Lấy lịch sử chat của session"""
    try:
        customer_id = current_user.get('customerId')
        username = current_user.get('sub')
        user_id = str(customer_id)
        
        # Lấy session với events
        from google.adk.sessions.base_session_service import GetSessionConfig
        config = GetSessionConfig(num_recent_events=limit)
        
        session = await session_service.get_session(
            app_name=agent_name,
            user_id=user_id,
            session_id=session_id,
            config=config
        )
        
        if not session:
            return []
        
        history = []
        for event in session.events:
            if event.content and event.content.parts:
                message = ""
                for part in event.content.parts:
                    if hasattr(part, 'text') and part.text:
                        message += part.text
                
                history.append(HistoryResponse(
                    id=event.id,
                    session_id=session_id,
                    customer_id=customer_id,
                    username=username,
                    message=message,
                    role=event.content.role if hasattr(event.content, 'role') else event.author,
                    created_at=datetime.fromtimestamp(event.timestamp).isoformat()
                ))
        
        return history
        
    except Exception as e:
        logger.error(f"Error fetching history: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Error fetching history: {str(e)}")

@router.delete("/session/{session_id}")
async def delete_session(
    session_id: str,
    current_user: dict = Depends(get_current_user)
):
    """Xóa toàn bộ lịch sử của một session"""
    try:
        customer_id = current_user.get('customerId')
        user_id = str(customer_id)
        
        await session_service.delete_session(
            app_name=agent_name,
            user_id=user_id,
            session_id=session_id
        )
        
        return {"message": f"Session {session_id} deleted successfully"}
    except Exception as e:
        logger.error(f"Error deleting session: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/session/{session_id}/stats")
async def get_session_stats(
    session_id: str,
    current_user: dict = Depends(get_current_user)
):
    """Lấy thống kê về session"""
    try:
        customer_id = current_user.get('customerId')
        user_id = str(customer_id)
        
        session = await session_service.get_session(
            app_name=agent_name,
            user_id=user_id,
            session_id=session_id
        )
        
        if not session:
            raise HTTPException(status_code=404, detail="Session not found")
        
        message_count = len(session.events)
        
        return {
            "session_id": session_id,
            "message_count": message_count,
            "last_update": datetime.fromtimestamp(session.last_update_time).isoformat()
        }
    except Exception as e:
        logger.error(f"Error getting session stats: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))