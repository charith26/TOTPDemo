package my.rnd.totp.client.totpClient;

import java.util.Collections;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import my.rnd.totp.client.totpClient.dto.ApiResponse;
import my.rnd.totp.client.totpClient.dto.AuthResponse;
import my.rnd.totp.client.totpClient.generator.TOTPGenerator;

@SpringBootApplication
public class TotpClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(TotpClientApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			AuthResponse authR = restTemplate.getForObject(
					"http://localhost:8080/totp/auth", AuthResponse.class);

			TOTPGenerator cg = new TOTPGenerator(authR.getSecret());

			System.out.println("");
			System.out.println("--------------------------------------------");
			System.out.println("Application ID:\t"+authR.getAppID());
			System.out.println("Secret:\t\t"+authR.getSecret());
			System.out.println("");


			//register and start application 1
			new Thread(() -> startApplication(restTemplate, cg, authR,"App1")).start();
			//register and start application 2
			new Thread(() -> startApplication(restTemplate, cg, authR,"App2")).start();
			//register and start application 3
			new Thread(() -> startApplication(restTemplate, cg, authR,"App3")).start();
		};
	}

	/**
	 *
	 * @param restTemplate
	 */
	private void startApplication(RestTemplate restTemplate, TOTPGenerator cg ,AuthResponse authR, String app) {
		while(true) {
			try {
				String totp = cg.getTotp().toString();
				while(totp.equals("null")) {
					totp = cg.getTotp().toString();
					Thread.sleep(200);
				}

				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				headers.add("totp", totp);
				headers.add("appID", authR.getAppID());
				HttpEntity<String> entity = new HttpEntity<>("body", headers);
				ResponseEntity<ApiResponse> response = restTemplate.exchange("http://localhost:8080/totp/api", HttpMethod.GET, entity, ApiResponse.class);
				System.out.println("AppID: "+app+","+"Gateway B ID:"+","+authR.getAppID()+","+"TOTP: "+totp+","+"Response:{message:"+response.getBody().getMessage()+","+"statusCode:"+response.getBody().getStatusCode()+"}");
				Thread.sleep(5000);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
