import json
from os import getenv
from typing import Optional
from requests import request
from requests.exceptions import RequestException
# from requests.exceptions import JSONDecodeError
from src.exceptions import NetworkErrorException

def request_data(url: str, method: str = "GET", data: Optional[dict] = None) -> tuple[dict | list, int]:
    try: 
        response = request(
            method=method,
            url=url,
            headers={"Content-Type": "application/json"} if method in ["PATCH", "POST"] else None,  # Arreglo de headers
            data=json.dumps(data) if data is not None else None 
        )
        
        #FIXME super horrible solution, we must fix this problem with the .json() exceptions
        # but works for now
        if response.status_code == 204:
            return {}, 204
        
        return response.json(), response.status_code
    except RequestException as e:
        print(f"Error: {e}")
        raise NetworkErrorException("Error de red")

PORT: int = int(getenv("PORT", 8000))
HOST: str = getenv("HOST", "localhost")
APPLICATION_ID: str = "es.udc.fic.ipm.acdc.pacientes"
WINDOW_PADDING: int = 24