from typing import Optional
from requests import request
from requests.exceptions import RequestException

def request_data(url: str, method: str = "GET") -> tuple[Optional[dict | list], int | None]:
    try: 
        response = request(method, url)
        return response.json(), response.status_code
    
    except RequestException as e:
        print(f"An error occurred: {e}")
        return None, None

PORT: int = 8000