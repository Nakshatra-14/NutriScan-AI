import uuid
from datetime import datetime, timedelta, timezone

import jwt

from app.core.config import settings


def create_access_token(
    user_id: uuid.UUID,
    *,
    secret: str | None = None,
    algorithm: str | None = None,
    expires_minutes: int | None = None,
) -> str:
    secret_key = secret if secret is not None else settings.JWT_SECRET
    alg = algorithm if algorithm is not None else settings.JWT_ALGORITHM
    minutes = expires_minutes if expires_minutes is not None else settings.ACCESS_TOKEN_EXPIRE_MINUTES
    expire = datetime.now(timezone.utc) + timedelta(minutes=minutes)
    payload = {"sub": str(user_id), "exp": expire}
    return jwt.encode(payload, secret_key, algorithm=alg)


def decode_access_token(token: str, *, secret: str | None = None, algorithms: list[str] | None = None) -> dict:
    secret_key = secret if secret is not None else settings.JWT_SECRET
    algs = algorithms if algorithms is not None else [settings.JWT_ALGORITHM]
    return jwt.decode(token, secret_key, algorithms=algs)
