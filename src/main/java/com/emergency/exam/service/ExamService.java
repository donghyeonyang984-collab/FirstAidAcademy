//package com.emergency.exam.service;
//
//import com.emergency.exam.dao.ExamDao;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Service
//@RequiredArgsConstructor
//public class ExamService {
//
//    private final ExamDao examDao;
//    private final GPTService gptService;
//
//    /** 시험 시작 */
//    public Map<String, Object> startExam(Long courseId, Long userId) {
//
//        String caption = examDao.getCaptionByCourseId(courseId);
//
//        List<Map<String, Object>> rawQuestions = gptService.generateQuestions(caption);
//
//        Long examId = examDao.findExamIdByCourseId(courseId);
//
//        Long attemptId = examDao.createAttempt(userId, examId);
//
//        List<Map<String, Object>> questionsForClient =
//                examDao.saveQuestions(attemptId, examId, rawQuestions);
//
//        Map<String, Object> resp = new HashMap<>();
//        resp.put("attemptId", attemptId);
//        resp.put("questions", questionsForClient);
//        return resp;
//    }
//
//    /** 시험 제출 + 채점 */
//    public Map<String, Object> submitExam(Map<String, Object> body) {
//
//        Long attemptId = Long.valueOf(body.get("attemptId").toString());
//        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");
//
//        int correctCount = 0;
//        int total = answers.size();
//
//        List<Map<String, Object>> detailed = new ArrayList<>();
//
//        for (Map<String, Object> ans : answers) {
//
//            Long questionId = Long.valueOf(ans.get("questionId").toString());
//            Long choiceId = Long.valueOf(ans.get("choiceId").toString());
//
//            examDao.saveAnswer(attemptId, questionId, choiceId);
//
//            Long correctChoiceId = examDao.findCorrectChoiceId(questionId);
//
//            boolean isCorrect = correctChoiceId.equals(choiceId);
//            if (isCorrect) correctCount++;
//
//            Map<String, Object> row = new HashMap<>();
//            row.put("questionId", questionId);
//            row.put("userChoiceId", choiceId);
//            row.put("correctChoiceId", correctChoiceId);
//            row.put("isCorrect", isCorrect);
//
//            detailed.add(row);
//        }
//
//        int score = (int) ((correctCount / (double) total) * 100);
//
//        examDao.updateAttemptScore(attemptId, score);
//
//        //  수료 처리 추가
//        if (score >= 60) {
//            Map<String, Object> info = examDao.getAttemptInfo(attemptId);
//
//            Long userId = (Long) info.get("user_id");
//            Long courseId = (Long) info.get("course_id");
//            LocalDateTime endedAt = (LocalDateTime) info.get("ended_at");
//
//            examDao.updateEnrollmentToPassed(userId, courseId, endedAt);
//        }
//
//
//
//
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("attemptId", attemptId);
//        result.put("correctCount", correctCount);
//        result.put("total", total);
//        result.put("score", score);
//        result.put("pass", score >= 60);
//        result.put("results", detailed);
//
//        return result;
//    }
//
//}
