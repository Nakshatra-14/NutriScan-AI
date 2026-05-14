import secrets
from datetime import datetime, timedelta, timezone
from typing import Literal

from sqlalchemy import func, select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.otp import OTP
from app.models.user import User
from app.services.jwt import create_access_token


async def register_user(
    db: AsyncSession,
    *,
    name: str,
    age: int,
    diabetes: bool,
    phone_no: str,
) -> User | Literal["duplicate"]:
    existing = await db.scalar(select(User).where(User.phone_no == phone_no))
    if existing is not None:
        return "duplicate"
    user = User(name=name, age=age, diabetes=diabetes, phone_no=phone_no)
    db.add(user)
    await db.commit()
    await db.refresh(user)
    return user


async def request_login_otp(db: AsyncSession, *, phone_no: str) -> Literal["ok", "user_not_found"]:
    user = await db.scalar(select(User).where(User.phone_no == phone_no))
    if user is None:
        return "user_not_found"
    otp_code = f"{secrets.randbelow(1_000_000):06d}"
    expires_at = datetime.now(timezone.utc) + timedelta(minutes=5)
    otp = OTP(phone_no=phone_no, otp_code=otp_code, expires_at=expires_at, is_used=False)
    db.add(otp)
    await db.commit()
    print(f"[MOCK SMS] To {phone_no}: Your OTP is {otp_code}")
    return "ok"


async def verify_otp_and_login(db: AsyncSession, *, phone_no: str, otp: str) -> str | None:
    stmt = (
        select(OTP)
        .where(
            OTP.phone_no == phone_no,
            OTP.is_used.is_(False),
            OTP.expires_at > func.now(),
        )
        .order_by(OTP.expires_at.desc())
        .limit(1)
    )
    otp_row = await db.scalar(stmt)
    if otp_row is None or otp_row.otp_code != otp:
        return None
    user = await db.scalar(select(User).where(User.phone_no == phone_no))
    if user is None:
        return None
    otp_row.is_used = True
    await db.commit()
    return create_access_token(user.id)
