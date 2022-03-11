import hmac, hashlib, time, requests
from requests.auth import AuthBase

# Before implementation, set environmental variables with the names API_KEY and SECRET
API_URL = 'https://api.coinbase.com'
API_KEY = 'YOUR_ACCESS_KEY'
SECRET = 'YOUR_SECRET'
CB_ACCESS_TIMESTAMP = str(int(time.time()))

requestPath = '/v2/accounts?limit=100'

# Create custom authentication for Coinbase API
class CoinbaseWalletAuth(AuthBase):
    def __init__(self, api_key, secret_key):
        self.api_key = api_key
        self.secret_key = secret_key

    def __call__(self, request):
        message = CB_ACCESS_TIMESTAMP + request.method + request.path_url + (request.body or '')
        signature = hmac.new(bytes(self.secret_key, 'utf-8'), bytes(message, 'utf-8'), hashlib.sha256).hexdigest()

        request.headers.update({
            'CB-ACCESS-SIGN': signature,
            'CB-ACCESS-TIMESTAMP': CB_ACCESS_TIMESTAMP,
            'CB-ACCESS-KEY': self.api_key,
            'CB-VERSION': '2015-07-22'
        })
        return request

auth = CoinbaseWalletAuth(API_KEY, SECRET)

# Get request
r = requests.get(API_URL + requestPath, auth=auth)
if r.status_code == 200:
   accounts = r.json() ['data']
   for account in accounts:
       if float(account['balance']['amount']) > 0:
          print("{}: {} ({})".format(account['currency'], account['balance']['amount'], account['native_balance']['amount']))
