package site.nansan.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient userServiceWebClient(WebClient.Builder builder) {

        return builder
                .baseUrl("lb://user-service")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                API Gateway가 UserService에서 받아오는 응답의 크기가 256KB를 넘을 가능성
//                WebClient는 최대 256KB까지만 응답 Body를 메모리에 적재할 수 있다.
//                2 * 1024 * 1024 = 2MB 까지 늘려주는 Code 필요하면 추가
//                .exchangeStrategies(ExchangeStrategies.builder()
//                        .codecs(config -> config
//                                .defaultCodecs()
//                                .maxInMemorySize(2 * 1024 * 1024)) // 필요 시 JSON 최대 크기 조정
//                        .build())
                .build();
    }
}
