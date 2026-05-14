import uuid

from pydantic import BaseModel, ConfigDict, Field


class RegisterRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)

    name: str = Field(..., min_length=1)
    age: int = Field(..., ge=0, le=150)
    diabetes: bool
    phone_no: str = Field(..., min_length=1)


class LoginOtpRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)

    phone_no: str = Field(..., min_length=1)


class VerifyOtpRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)

    phone_no: str = Field(..., min_length=1)
    otp: str = Field(..., min_length=6, max_length=6, pattern=r"^\d{6}$")


class UserResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: uuid.UUID
    name: str
    age: int
    diabetes: bool
    phone_no: str


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"


class MessageResponse(BaseModel):
    message: str
