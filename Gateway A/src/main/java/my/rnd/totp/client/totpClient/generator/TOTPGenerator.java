package my.rnd.totp.client.totpClient.generator;

import java.util.concurrent.atomic.AtomicReference;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

public class TOTPGenerator {

	private DefaultCodeGenerator codeGenerator;
	private Thread thread;
	private AtomicReference<String> totp = new AtomicReference<String>();

	public TOTPGenerator(String secret) {
		try {
			TimeProvider timeProvider = new SystemTimeProvider();
			codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA512);
			DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
			verifier.setTimePeriod(30);
			verifier.setAllowedTimePeriodDiscrepancy(1);

			generateCode(secret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateCode(String secret) {
		thread = new Thread(() -> {
			try {
				while (true) {
					long currentBucket = Math.floorDiv(new SystemTimeProvider().getTime(), 30);
					totp.set(codeGenerator.generate(secret, currentBucket));
					Thread.sleep(28000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.start();
	}

	public AtomicReference<String> getTotp() {
		return totp;
	}
}
