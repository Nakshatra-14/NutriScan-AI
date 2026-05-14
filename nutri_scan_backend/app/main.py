from contextlib import asynccontextmanager

from fastapi import FastAPI

from app.api.routers import auth
from app.db.session import engine


@asynccontextmanager
async def lifespan(_app: FastAPI):
    yield
    await engine.dispose()


app = FastAPI(lifespan=lifespan)
app.include_router(auth.router, prefix="/auth", tags=["auth"])


@app.get("/health")
async def health() -> dict[str, str]:
    return {"status": "ok"}
