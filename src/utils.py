from requests import request

def request_data(url: str, method: str = "GET") -> dict:
    response = request(method, url)
    return response.json()

PORT: int = 8000