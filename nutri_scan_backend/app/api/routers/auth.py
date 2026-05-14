from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.session import get_db
from app.models.user import User
from app.schemas.auth import (
    LoginOtpRequest,
    MessageResponse,
    RegisterRequest,
    TokenResponse,
    UserResponse,
    VerifyOtpRequest,
)
from app.services import auth_service

router = APIRouter()


@router.post("/register", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
async def register(body: RegisterRequest, db: AsyncSession = Depends(get_db)) -> User:
    result = await auth_service.register_user(
        db,
        name=body.name,
        age=body.age,
        diabetes=body.diabetes,
        phone_no=body.phone_no,
    )
    if result == "duplicate":
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail="Phone number already registered")
    return result


@router.post("/login-otp", response_model=MessageResponse)
async def login_otp(body: LoginOtpRequest, db: AsyncSession = Depends(get_db)) -> MessageResponse:
    outcome = await auth_service.request_login_otp(db, phone_no=body.phone_no)
    if outcome == "user_not_found":
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="User not found")
    return MessageResponse(message="OTP sent (mock SMS printed to server logs)")


@router.post("/verify-otp", response_model=TokenResponse)
async def verify_otp(body: VerifyOtpRequest, db: AsyncSession = Depends(get_db)) -> TokenResponse:
    token = await auth_service.verify_otp_and_login(db, phone_no=body.phone_no, otp=body.otp)
    if token is None:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid or expired OTP")
    return TokenResponse(access_token=token)
