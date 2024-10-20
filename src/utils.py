from os import getenv
from typing import Optional
from requests import request
from requests.exceptions import RequestException
# from requests.exceptions import JSONDecodeError
from src.exceptions import NetworkErrorException
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
        response = request(method=method, url=url, data=data)
        
        #FIXME super horrible solution, we must fix this problem with the .json() exceptions
        # but works for now
        if response.status_code == 204:
            return {}, 204
        
        return response.json(), response.status_code
    except RequestException as e:
        print(f"Error: {e}")
        raise NetworkErrorException("Error de red")

PORT: int = int(getenv("PORT", 8000))
APPLICATION_ID: str = "es.udc.fic.ipm.acdc.pacientes"
WINDOW_PADDING: int = 24
BLOCK_TIME: int = int(getenv("BLOCK_TIME", 0)) # Used for testing asynchronous functions
ASYNC_CODE: bool = bool(getenv("ASYNC_CODE", True))