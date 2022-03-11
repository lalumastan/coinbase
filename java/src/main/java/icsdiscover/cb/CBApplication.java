package icsdiscover.cb;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CBApplication {	
	
	private static final String API_URL = "https://api.coinbase.com";
	private static final String SECRET = "YOUR_SECRET";
	private static final String CB_ACCESS_KEY = "YOUR_ACCESS_KEY";
	private static final String CB_ACCESS_TIMESTAMP = "" + System.currentTimeMillis() / 1000L;		
	
	public static void main(String[] args) {
		SpringApplication.run(CBApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			HttpMethod httpMethod = HttpMethod.GET;
			String requestPath = "/v2/accounts?limit=100";

			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secretKey);
			String message = CB_ACCESS_TIMESTAMP + httpMethod.name() + requestPath;
			String cbAccessSign = Hex.encodeHexString(sha256_HMAC.doFinal(message.getBytes()));

			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.set("CB-ACCESS-KEY", CB_ACCESS_KEY);
			headers.set("CB-VERSION", "2015-07-22");
			headers.set("CB-ACCESS-SIGN", cbAccessSign);
			headers.set("CB-ACCESS-TIMESTAMP", CB_ACCESS_TIMESTAMP);
			HttpEntity<String> entity = new HttpEntity<String>(headers);		
			
			ResponseEntity<CBAccount> responseEntity = restTemplate.exchange(API_URL + requestPath, httpMethod, entity, CBAccount.class);
			CBAccount accounts = responseEntity.getBody();
			
			System.out.println();
			if (responseEntity.getStatusCode() == HttpStatus.OK) {			   
			   for (Data account: accounts.data()) {
				   Balance balance  = account.balance();
				   if (Float.parseFloat(balance.amount()) > 0) {
					  System.out.println(account.currency() + ": " + balance.amount() + " ($" + account.native_balance().amount() + ")");	   
				   }
			   }
			}
			System.out.println();
		};
	}	
	
}
