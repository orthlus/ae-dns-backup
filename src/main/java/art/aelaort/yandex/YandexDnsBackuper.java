package art.aelaort.yandex;

import art.aelaort.S3Params;
import art.aelaort.yandex.dto.ZoneInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static art.aelaort.S3ClientProvider.client;

@Slf4j
@Component
@RequiredArgsConstructor
public class YandexDnsBackuper implements ApplicationRunner {
	private final YandexDnsService yandexDnsService;
	private final S3Params yandexDnsS3;
	@Value("${yandex.dns.s3.bucket}")
	private String bucket;

	public void backupDns() {
		String now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
		for (ZoneInfo zone : yandexDnsService.getZones()) {
			String zoneRecordsString = yandexDnsService.getZoneRecordsStringByZoneId(zone.id());
			String file = "%s/%s.json".formatted(now, zone.name());
			uploadFileContent(file, zoneRecordsString);
			log.info("yandex dns backup {} complete", file);
		}
	}

	private void uploadFileContent(String fileId, String fileContent) {
		try (S3Client client = client(yandexDnsS3)) {
			client.putObject(builder -> builder
							.key(fileId)
							.bucket(bucket)
							.build(),
					RequestBody.fromString(fileContent));
		}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		backupDns();
	}
}
