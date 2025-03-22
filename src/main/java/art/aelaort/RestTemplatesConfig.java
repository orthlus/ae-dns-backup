package art.aelaort;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplatesConfig {
	@Bean
	public RestTemplate yandexDns(RestTemplateBuilder restTemplateBuilder,
								  @Value("${yandex.dns.api.url}") String url) {
		return restTemplateBuilder
				.rootUri(url)
				.build();
	}

	@Bean
	public RestTemplate iamRestTemplate(RestTemplateBuilder restTemplateBuilder,
										@Value("${iam.url}") String url) {
		return restTemplateBuilder.rootUri(url).build();
	}
}
