import json
from os import getenv
from typing import Optional
from requests import request
from requests.exceptions import RequestException, JSONDecodeError
from src.exceptions import NetworkErrorException, DataErrorException
import time
from functools import wraps
import threading

# Helper decorator made to slowdown http requests and ensure that concurrency works properly
def block_execution(func):
    def wrapper(*args, **kwargs):
        time.sleep(BLOCK_TIME)
        return func(*args, **kwargs)
    return wrapper

# Helper decorator to pass a function into a thread
def run_async(func):
    def wrapper(*args, **kwargs):
        if ASYNC_CODE:
            def thread_target():
                func(*args, **kwargs)
            threading.Thread(target=thread_target).start()
        else:
            func(*args, **kwargs)
    return wrapper

def request_data(url: str, method: str = "GET", data: Optional[dict] = None) -> tuple[dict | list, int]:
    try: 
        response = request(
            method=method,
            url=url,
            headers={"Content-Type": "application/json"} if method in ["PATCH", "POST"] else None,
            data=json.dumps(data) if data is not None else None 
        )

        try:
            return response.json(), response.status_code
        except JSONDecodeError:
            return {}, 204
        
    except RequestException as e:
        raise NetworkErrorException(e)
    except Exception as e:
        raise DataErrorException(e)


PORT: int = int(getenv("PORT", 8000))
HOST: str = getenv("HOST", "localhost")
APPLICATION_ID: str = "es.udc.fic.ipm.acdc.pacientes"
WINDOW_PADDING: int = 24
BLOCK_TIME: int = int(getenv("BLOCK_TIME", 0)) # Used for testing asynchronous functions
ASYNC_CODE: bool = bool(getenv("ASYNC_CODE", True))
