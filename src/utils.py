import json
from os import getenv
from typing import Optional
from requests import request
from requests.exceptions import RequestException, JSONDecodeError
from src.exceptions import NetworkErrorException, DataErrorException

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