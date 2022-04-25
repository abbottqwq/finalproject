import os
HOST = os.environ.get("HOST", "localhost")
PORT = os.environ.get('PORT', "9999")
PREFIX = os.environ.get('PREFIX', "api")
URL = f"http://{HOST}:{PORT}/{PREFIX}/test/"
