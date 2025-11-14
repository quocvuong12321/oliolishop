from fastapi import APIRouter, HTTPException, Depends, Header
from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime
from core.auth import decode_token
from core.session_manager import SessionManager
from multi_tool_agent import agent
from google.genai import types
import os

# Thêm debug log
import logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Verify GOOGLE_API_KEY
logger.info(f"GOOGLE_API_KEY exists: {bool(os.getenv('GOOGLE_API_KEY'))}")
logger.info(f"Agent loaded: {agent}")
logger.info(f"Agent type: {type(agent)}")

router = APIRouter()

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

class HistoryResponse(BaseModel):
    id: int
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
        payload = decode_token(token)
        
        if not payload:
            raise HTTPException(status_code=401, detail="Invalid or expired token")
        
        return payload
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
        # Xóa session cũ của user (nếu có)
        old_session_id = SessionManager.get_or_create_user_session(username, customer_id)
        if old_session_id:
            SessionManager.delete_session_history(old_session_id)
            logger.info(f"Deleted old session: {old_session_id}")
        
        # Tạo session mới
        new_session_id = SessionManager.create_session(username, customer_id)
        
        return SessionResponse(
            session_id=new_session_id,
            username=username,
            customer_id=customer_id
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error starting new chat: {str(e)}")

@router.get("/session", response_model=SessionResponse)
async def get_or_create_session(current_user: dict = Depends(get_current_user)):
    """Lấy session hiện tại của user, nếu chưa có thì tạo mới"""
    username = current_user.get('sub')
    customer_id = current_user.get('customerId')
    
    if not username:
        raise HTTPException(status_code=400, detail="Username not found in token")
    
    if not customer_id:
        raise HTTPException(status_code=400, detail="Customer ID not found in token")
    
    try:
        session_id = SessionManager.get_or_create_user_session(username, customer_id)
        
        if not session_id:
            raise HTTPException(status_code=500, detail="Failed to get or create session")
        
        return SessionResponse(
            session_id=session_id,
            username=username,
            customer_id=customer_id
        )
    except Exception as e:
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
        # Lưu tin nhắn của user
        saved = SessionManager.save_chat_history(
            session_id=request.session_id,
            customer_id=customer_id,
            username=username,
            message=request.message,
            role='user'
        )
        
        if not saved:
            raise HTTPException(status_code=500, detail="Failed to save user message")
        
        # Gọi agent sử dụng InMemoryRunner từ Google ADK
        try:
            logger.info(f"Calling agent with message: {request.message}")
            
            from google.adk.runners import InMemoryRunner
            
            # Tạo runner với agent
            runner = InMemoryRunner(agent=agent)
            
            # Tạo user_id từ customer_id
            user_id = str(customer_id)
            
            # Tạo hoặc lấy session từ runner's session service
            session = await runner.session_service.get_session(
                app_name=runner.app_name,
                user_id=user_id,
                session_id=request.session_id
            )
            
            if not session:
                # Tạo session mới nếu chưa có
                session = await runner.session_service.create_session(
                    app_name=runner.app_name,
                    user_id=user_id,
                    session_id=request.session_id
                )
                logger.info(f"Created new session: {session.id}")
            
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
            
            # Cleanup runner
            await runner.close()
                
        except Exception as agent_error:
            logger.error(f"Error calling agent: {agent_error}", exc_info=True)
            assistant_message = f"Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn: {str(agent_error)}"
        
        # Lưu phản hồi của assistant
        SessionManager.save_chat_history(
            session_id=request.session_id,
            customer_id=customer_id,
            username=username,
            message=assistant_message,
            role='assistant'
        )
        
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
        history = SessionManager.get_chat_history(session_id, limit)
        
        return [
            HistoryResponse(
                id=item['id'],
                session_id=item['session_id'],
                customer_id=item['customer_id'],
                username=item['username'],
                message=item['message'],
                role=item['role'],
                created_at=item['created_at'].isoformat() if hasattr(item['created_at'], 'isoformat') else str(item['created_at'])
            )
            for item in history
        ]
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching history: {str(e)}")

@router.delete("/session/{session_id}")
async def delete_session(
    session_id: str,
    current_user: dict = Depends(get_current_user)
):
    """Xóa toàn bộ lịch sử của một session"""
    try:
        success = SessionManager.delete_session_history(session_id)
        if success:
            return {"message": f"Session {session_id} deleted successfully"}
        else:
            raise HTTPException(status_code=500, detail="Failed to delete session")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/session/{session_id}/stats")
async def get_session_stats(
    session_id: str,
    current_user: dict = Depends(get_current_user)
):
    """Lấy thống kê về session"""
    try:
        message_count = SessionManager.get_session_message_count(session_id)
        return {
            "session_id": session_id,
            "message_count": message_count,
            "max_messages": SessionManager.MAX_MESSAGES_PER_SESSION,
            "remaining": max(0, SessionManager.MAX_MESSAGES_PER_SESSION - message_count)
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))