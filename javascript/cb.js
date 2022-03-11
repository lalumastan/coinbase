import fetch from 'node-fetch';
import crypto from 'crypto';

const API_URL = 'https://api.coinbase.com'
const CB_ACCESS_KEY = 'YOUR_ACCESS_KEY';
const SECRET = 'YOUR_SECRET';
const CB_ACCESS_TIMESTAMP = Math.floor(Date.now() / 1000); // in ms

var requestPath = '/v2/accounts?limit=100';
var body = ''
var method = 'GET';

// create the prehash string by concatenating required parts
var message = CB_ACCESS_TIMESTAMP + method + requestPath + body;

// create a sha256 hmac with the secret
var hmac = crypto.createHmac('sha256', SECRET);

// sign the require message with the hmac
// and finally base64 encode the result
var cb_access_sign = hmac.update(message).digest('hex');

const options = {
    method: 'GET',
    headers: {
      Accept: 'application/json',      
      'CB-ACCESS-KEY': CB_ACCESS_KEY,
      'CB-ACCESS-SIGN': cb_access_sign,
      'CB-ACCESS-TIMESTAMP': CB_ACCESS_TIMESTAMP,
      'CB-VERSION':  '2015-07-22',
    }  
  };
   
fetch(API_URL + requestPath, options)  
    .then(response => response.json())  
    .then(response => {
      //console.log(response);
      response.data.forEach(function (account) {
        if (account.balance.amount > 0) {
           console.log(account.currency + ": " + account.balance.amount + " ($" + account.native_balance.amount  + ")");
        }        
      });      
    })
    .catch(err => console.error(err));