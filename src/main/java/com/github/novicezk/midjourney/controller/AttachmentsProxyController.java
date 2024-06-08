package com.github.novicezk.midjourney.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Slf4j
@Api(tags = "MJ图片链接代理")
@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentsProxyController {
  private final RestTemplate restTemplate;

  @GetMapping("/**")
  public ResponseEntity<byte[]> getImages(HttpServletRequest request) {
    String originUrl = request.getServletPath();
    originUrl = originUrl.replaceFirst("/attachments", "");
    String originQueryString = request.getQueryString();
    String targetDomain = "";

    if (originQueryString.endsWith("=&format=webp&width=700&height=700")) {
      targetDomain = "https://media.discordapp.net";
    } else {
      targetDomain = "https://cdn.discordapp.com";
    }
    String targetUrl = targetDomain + originUrl + "?" + originQueryString;
    log.info("Target URL: {}", targetUrl);

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
    HttpEntity<String> entity = new HttpEntity<>(headers);
    return  this.restTemplate.exchange(targetUrl, HttpMethod.GET, entity, byte[].class);
  }
}
