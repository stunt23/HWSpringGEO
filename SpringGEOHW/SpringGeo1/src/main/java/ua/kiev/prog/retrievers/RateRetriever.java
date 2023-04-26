package ua.kiev.prog.retrievers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.kiev.prog.json.Rate;

@Component//  Component Service
public class RateRetriever {
    private static final String URL = "https://api.apilayer.com/fixer/latest?symbols=UAH&base=EUR";
    private static final String KEY = "5uo4bYoYmLA2ua2EGk5hpC47qB6Gl4N4";

    @Autowired
    private CacheManager cacheManager;

    @Cacheable(value = "rates", key = "'rate'")
    public Rate getRate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", KEY);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<Rate> response = restTemplate.exchange(URL, HttpMethod.GET, entity, Rate.class);
        return response.getBody();
    }

    @Scheduled(fixedRate = 60000)
    public void updateRate() {
        cacheManager.getCache("rates").evict("rate");
        getRate();
    }
}
