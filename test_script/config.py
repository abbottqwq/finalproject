import os
HOST = os.environ.get("HOST", "localhost")
PORT = os.environ.get('PORT', "9000")
URL = "http://" + HOST + ":" + PORT + "/test/"
