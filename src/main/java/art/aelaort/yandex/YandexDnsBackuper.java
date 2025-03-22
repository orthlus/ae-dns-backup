package art.aelaort.yandex;

import art.aelaort.S3Params;
import art.aelaort.yandex.dto.ZoneInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import static art.aelaort.S3ClientProvider.client;
import static art.aelaort.Utils.now;

@Slf4j
@Component
@RequiredArgsConstructor
public class YandexDnsBackuper {
	private final YandexDnsService yandexDnsService;
	private final S3Params yandexDnsS3;
	@Value("${yandex.dns.s3.bucket}")
	private String bucket;

	@Scheduled(cron = "${cron.dns.backup}")
	public void backupDns() {
		String now = now();
		for (ZoneInfo zone : yandexDnsService.getZones()) {
			String zoneRecordsString = yandexDnsService.getZoneRecordsStringByZoneId(zone.id());
			String file = "%s/%s.json".formatted(now, zone.name());
			uploadFileContent(file, zoneRecordsString);
			log.info("yandex dns backup {} complete", file);
		}
	}

	public void uploadFileContent(String fileId, String fileContent) {
		try (S3Client client = client(yandexDnsS3)) {
			client.putObject(builder -> builder
							.key(fileId)
							.bucket(bucket)
							.build(),
					RequestBody.fromString(fileContent));
		}
	}
}
