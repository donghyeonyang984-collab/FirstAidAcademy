//package com.emergency.exam.dao;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//import org.springframework.stereotype.Repository;
//
//import java.sql.PreparedStatement;
//import java.sql.Statement;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//@RequiredArgsConstructor
//public class ExamDao {
//
//    private final JdbcTemplate jdbc;
//
//    // 자막 합치기
//    public String getCaptionByCourseId(Long courseId) {
//
//        String sql = """
//        SELECT information
//        FROM course_lectures
//        WHERE course_id = ?
//        ORDER BY lecture_no ASC
//    """;
//
//        List<String> list = jdbc.query(sql,
//                (rs, rowNum) -> rs.getString("information"),
//                courseId
//        );
//
//        // Null 제거 + 공백 정리
//        return list.stream()
//                .filter(s -> s != null && !s.isBlank())
//                .reduce("", (a, b) -> a + " " + b);
//    }
//
//    // attempt 생성
//    public Long createAttempt(Long userId, Long examId) {
//
//        String sql = "INSERT INTO exam_attempts (user_id, exam_id, started_at) VALUES (?, ?, NOW())";
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        jdbc.update(con -> {
//            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//            ps.setLong(1, userId);
//            ps.setLong(2, examId);
//            return ps;
//        }, keyHolder);
//
//        return keyHolder.getKey().longValue();
//    }
//
//    // 문제 저장
//    // 시험 문제 + 보기를 DB에 저장하고,
//// 클라이언트로 내려줄 questionId / choiceId 포함한 리스트 리턴
//    public List<Map<String, Object>> saveQuestions(Long attemptId,
//                                                   Long examId,
//                                                   List<Map<String, Object>> questions) {
//
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        String insertQ = """
//        INSERT INTO exam_questions(exam_id, question_no, question_text)
//        VALUES (?, ?, ?)
//    """;
//
//        String insertChoice = """
//        INSERT INTO exam_choices(exam_question_id, choice_no, choice_text, is_correct)
//        VALUES (?, ?, ?, ?)
//    """;
//
//        for (Map<String, Object> q : questions) {
//
//            int questionNo = (Integer) q.get("questionNo");
//            String questionText = (String) q.get("questionText");
//            int correctChoice = (Integer) q.get("correctChoice"); // 1~4
//            List<String> choices = (List<String>) q.get("choices");
//
//            // 1) exam_questions INSERT
//            KeyHolder qKey = new GeneratedKeyHolder();
//            jdbc.update(con -> {
//                PreparedStatement ps = con.prepareStatement(insertQ, Statement.RETURN_GENERATED_KEYS);
//                ps.setLong(1, examId);
//                ps.setInt(2, questionNo);
//                ps.setString(3, questionText);
//                return ps;
//            }, qKey);
//
//            long questionId = qKey.getKey().longValue();
//
//            // 2) exam_choices INSERT 들
//            List<Map<String, Object>> choiceList = new ArrayList<>();
//
//            for (int i = 0; i < choices.size(); i++) {
//                String text = choices.get(i);
//                int choiceNo = i + 1;
//
//                KeyHolder cKey = new GeneratedKeyHolder();
//                jdbc.update(con -> {
//                    PreparedStatement ps = con.prepareStatement(insertChoice, Statement.RETURN_GENERATED_KEYS);
//                    ps.setLong(1, questionId);
//                    ps.setInt(2, choiceNo);
//                    ps.setString(3, text);
//                    ps.setBoolean(4, choiceNo == correctChoice);   // 정답 표시
//                    return ps;
//                }, cKey);
//
//                long choiceId = cKey.getKey().longValue();
//
//                Map<String, Object> choiceMap = new HashMap<>();
//                choiceMap.put("choiceId", choiceId);
//                choiceMap.put("choiceNo", choiceNo);
//                choiceMap.put("choiceText", text);
//                choiceList.add(choiceMap);
//            }
//
//            // 3) 프론트로 내려줄 question 구조 만들기
//            Map<String, Object> qResp = new HashMap<>();
//            qResp.put("questionId", questionId);
//            qResp.put("questionNo", questionNo);
//            qResp.put("questionText", questionText);
//            qResp.put("choices", choiceList);
//
//            result.add(qResp);
//        }
//
//        return result;
//    }
//    // 사용자 답안 저장
//    public void saveAnswer(Long attemptId, Long questionId, Long choiceId) {
//        String sql = """
//        INSERT INTO exam_answers(exam_attempt_id, exam_question_id, selected_exam_choice_id)
//        VALUES (?, ?, ?)
//    """;
//        jdbc.update(sql, attemptId, questionId, choiceId);
//    }
//
//
//    // 해당 choiceId가 정답인지 확인
//    public boolean isCorrectChoice(Long choiceId) {
//        String sql = "SELECT is_correct FROM exam_choices WHERE exam_choice_id = ?";
//        Integer flag = jdbc.queryForObject(sql, Integer.class, choiceId);
//        return flag != null && flag == 1;
//    }
//
//    // 🔥 새로 추가해야 하는 메서드
//    public Long findCorrectChoiceId(Long questionId) {
//        String sql = """
//        SELECT exam_choice_id
//        FROM exam_choices
//        WHERE exam_question_id = ?
//          AND is_correct = 1
//        LIMIT 1
//    """;
//        return jdbc.queryForObject(sql, Long.class, questionId);
//    }
//
//    // attempt 점수 + 결과 저장
//    public void updateAttemptScore(Long attemptId, int score) {
//        String result = (score >= 60) ? "통과" : "실패";
//
//        String sql = """
//        UPDATE exam_attempts
//        SET score = ?, result = ?, ended_at = NOW()
//        WHERE exam_attempt_id = ?
//    """;
//
//        jdbc.update(sql, score, result, attemptId);
//    }
//
//    public Map<String, Object> getAttemptInfo(Long attemptId) {
//        String sql = """
//        SELECT ea.user_id, e.course_id, ea.ended_at
//        FROM exam_attempts ea
//        JOIN exams e ON ea.exam_id = e.exam_id
//        WHERE ea.exam_attempt_id = ?
//    """;
//
//        return jdbc.queryForMap(sql, attemptId);
//    }
//
//    public void updateEnrollmentToPassed(Long userId, Long courseId, LocalDateTime passedAt) {
//        String sql = """
//        UPDATE enrollments
//        SET status = '수료', passed_at = ?
//        WHERE user_id = ? AND course_id = ?
//    """;
//
//        jdbc.update(sql, passedAt, userId, courseId);
//    }
//
//    // exam_id 검색
//    public Long findExamIdByCourseId(Long courseId) {
//        String sql = "SELECT exam_id FROM exams WHERE course_id = ?";
//        return jdbc.queryForObject(sql, Long.class, courseId);
//    }
//}
