from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from datetime import datetime
from multi_tool_agent import agent
from google.genai import types
from google.adk.runners import InMemoryRunner
import os

# Thêm debug log
import logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Verify GOOGLE_API_KEY
logger.info(f"GOOGLE_API_KEY exists: {bool(os.getenv('GOOGLE_API_KEY'))}")
logger.info(f"Agent loaded: {agent}")
logger.info(f"Agent type: {type(agent)}")

# Khởi tạo runner 1 lần duy nhất khi module load
runner = InMemoryRunner(agent=agent)
logger.info(f"InMemoryRunner initialized: {runner}")

router = APIRouter()

# Models
class ChatRequest(BaseModel):
    session_id: str
    message: str
    user_id: str

class ChatResponse(BaseModel):
    sessionId: str = Field(..., alias="session_id")
    userMessage: str = Field(..., alias="user_message")
    assistantMessage: str = Field(..., alias="assistant_message")
    timeStamp: str = Field(..., alias="timestamp")
    
    class Config:
        populate_by_name = True
        by_alias = True

@router.get("/endpoint")
async def root():
    """Root endpoint"""
    return {
        "message": "Product Agent Chat API",
        "version": "1.0.0",
        "endpoints": {
            "health": "/api/chat/health",
            "chat": "POST /api/chat"
        }
    }

@router.get("/health")
async def health_check():
    """Health check endpoint"""
    return {"status": "healthy", "message": "Chat API is running"}

@router.post("", response_model=ChatResponse)
async def chat(request: ChatRequest):
    """Gửi tin nhắn và nhận phản hồi từ agent"""
    
    try:
        logger.info(f"Calling agent with message: {request.message}, user_id: {request.user_id}, session_id: {request.session_id}")

        # Kiểm tra và tạo session nếu chưa tồn tại
        session = await runner.session_service.get_session(
            app_name=runner.app_name,
            user_id=request.user_id,
            session_id=request.session_id
        )
        
        if not session:
            logger.info(f"Session not found, creating new session: {request.session_id}")
            try:
                session = await runner.session_service.create_session(
                    app_name=runner.app_name,
                    user_id=request.user_id,
                    session_id=request.session_id
                )
                logger.info(f"Successfully created new session: {session.id}")
            except Exception as create_error:
                logger.error(f"Failed to create session: {create_error}", exc_info=True)
                raise HTTPException(status_code=500, detail=f"Cannot create session: {str(create_error)}")
        else:
            logger.info(f"Using existing session: {session.id}")

        # Tạo message content
        new_message = types.Content(
            role='user',
            parts=[types.Part(text=request.message)]
        )
        
        # Collect response từ agent
        assistant_message = ""
        
        async for event in runner.run_async(
            user_id=request.user_id,
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
        
        # Fallback nếu không có response
        if not assistant_message:
            assistant_message = "Xin lỗi, tôi không nhận được phản hồi. Vui lòng thử lại."
        
        return ChatResponse(
            session_id=request.session_id,
            user_message=request.message,
            assistant_message=assistant_message,
            timestamp=datetime.now().isoformat()
        )
            
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error in chat endpoint: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Error processing chat: {str(e)}")