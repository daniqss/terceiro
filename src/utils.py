from os import getenv
from typing import Optional
from requests import request
from requests.exceptions import RequestException

def request_data(url: str, method: str = "GET", data: Optional[dict] = None) -> tuple[dict | list, int]:
    try: 
        response = request(method=method, url=url, data=data)
        return response.json(), response.status_code
    
    except RequestException as e:
        raise e

PORT: int = int(getenv("PORT", 8000))
APPLICATION_ID: str = "es.udc.fic.ipm.acdc.pacientes"
WINDOW_PADDING: int = 24