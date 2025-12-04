//package com.emergency.exam.service;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class GPTService {
//
//    @Value("${openai.api-key}")
//    private String apiKey;
//
//    @Value("${openai.model}")
//    private String model;   // application.properties에 gpt-4.1-mini
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public List<Map<String, Object>> generateQuestions(String caption) {
//
//        try {
//            // 1) 프롬프트
//            String prompt = """
//                너는 응급처치 교육 시험 문제 생성 AI이다.
//
//                아래는 영상 자막 내용이다:
//                ----
//                %s
//                ----
//
//
//
//                위 내용을 기반으로 "객관식 4지선다 문제 5문항"을 JSON 배열로 만들어라.
//                보기 배열은 4개, 정답은 1~4 숫자로 지정해라.
//
//
//
//                예:
//                [
//                  {
//                    "questionNo": 1,
//                    "questionText": "문제 내용",
//                    "choices": ["보기1", "보기2", "보기3", "보기4"],
//                    "correctChoice": 2
//                  }
//                ]
//
//                절대 JSON 외의 어떤 텍스트도 출력하지 마라.
//                 설명 금지, 말풍선 금지, 마크다운 금지.
//                 결과는 반드시 순수 JSON 배열만 반환하라.
//                """.formatted(caption);
//
//            // 2) 요청 Body 구성 (chat completions 엔드포인트)
//            Map<String, Object> body = new HashMap<>();
//            body.put("model", model);
//            body.put("temperature", 0.2);
//
//            // messages 배열
//            body.put("messages", List.of(
//                    Map.of(
//                            "role", "user",
//                            "content", prompt
//                    )
//            ));
//
//            // 3) 헤더 설정
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(apiKey);
//
//            HttpEntity<Map<String, Object>> requestEntity =
//                    new HttpEntity<>(body, headers);
//
//            // 4) OpenAI API 호출
//            String url = "https://api.openai.com/v1/chat/completions";
//
//            ResponseEntity<String> responseEntity =
//                    restTemplate.postForEntity(url, requestEntity, String.class);
//
//            if (responseEntity.getStatusCode() != HttpStatus.OK) {
//                throw new RuntimeException("OpenAI API 호출 실패: " + responseEntity.getStatusCode());
//            }
//
//            String responseJson = responseEntity.getBody();
//            System.out.println("===== GPT FULL RESPONSE =====");
//            System.out.println(responseJson);
//
//            // 5) JSON 파싱해서 choices[0].message.content 꺼내기
//            JsonNode root = objectMapper.readTree(responseJson);
//            JsonNode choicesNode = root.path("choices");
//            if (!choicesNode.isArray() || choicesNode.isEmpty()) {
//                throw new RuntimeException("OpenAI 응답에 choices가 없습니다.");
//            }
//
//            String content = choicesNode.get(0)
//                    .path("message")
//                    .path("content")
//                    .asText();
//            System.out.println("===== GPT content =====");
//            System.out.println(content);
//
//            // 6) content에 있는 JSON 문자열을 List<Map<String,Object>> 로 변환
//            return objectMapper.readValue(
//                    content,
//                    new TypeReference<List<Map<String, Object>>>() {}
//            );
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("GPT 문제 생성 중 오류: " + e.getMessage(), e);
//        }
//    }
//}
