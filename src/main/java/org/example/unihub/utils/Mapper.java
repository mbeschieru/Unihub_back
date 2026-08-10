package org.example.unihub.utils;

import lombok.RequiredArgsConstructor;
import org.example.unihub.dto.*;
import org.example.unihub.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class Mapper {
    
    private final ModelMapper modelMapper;
    
    public UserDTO toUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
    
    public User toUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }


    public StudentProfileDTO toStudentProfileDTO(StudentProfile studentProfile) {
        return modelMapper.map(studentProfile, StudentProfileDTO.class);
    }
    
    public StudentProfile toStudentProfile(StudentProfileDTO studentProfileDTO) {
        return modelMapper.map(studentProfileDTO, StudentProfile.class);
    }
    
    public ProfessorProfileDTO toProfessorProfileDTO(ProfessorProfile professorProfile) {
        return modelMapper.map(professorProfile, ProfessorProfileDTO.class);
    }
    
    public ProfessorProfile toProfessorProfile(ProfessorProfileDTO professorProfileDTO) {
        return modelMapper.map(professorProfileDTO, ProfessorProfile.class);
    }

    public ClassDTO toClassDTO(ClassEntity classEntity) {
        return modelMapper.map(classEntity, ClassDTO.class);
    }
    
    public ClassEntity toClassEntity(ClassDTO classDTO) {
        return modelMapper.map(classDTO, ClassEntity.class);
    }

    public LaboratoryDTO toLaboratoryDTO(Laboratory laboratory) {
        return modelMapper.map(laboratory,LaboratoryDTO.class);
    }

    // Quiz mappings
    public QuizDTO toQuizDTO(Quiz quiz) {
        QuizDTO quizDTO = new QuizDTO();
        quizDTO.setId(quiz.getId());
        quizDTO.setTitle(quiz.getTitle());
        quizDTO.setDescription(quiz.getDescription());
        quizDTO.setDueDate(quiz.getDueDate());
        quizDTO.setTotalPoints(quiz.getTotalPoints());
        quizDTO.setLaboratoryId(quiz.getLaboratory() != null ? quiz.getLaboratory().getId() : null);
        return quizDTO;
    }

    public Quiz toQuiz(QuizDTO quizDTO) {
        Quiz quiz = new Quiz();
        quiz.setId(quizDTO.getId());
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        quiz.setDueDate(quizDTO.getDueDate());
        quiz.setTotalPoints(quizDTO.getTotalPoints());
        return quiz;
    }

    public Quiz toQuiz(QuizRequest quizRequest) {
        Quiz quiz = new Quiz();
        quiz.setTitle(quizRequest.getTitle());
        quiz.setDescription(quizRequest.getDescription());
        quiz.setDueDate(quizRequest.getDueDate());
        quiz.setTotalPoints(quizRequest.getTotalPoints());
        return quiz;
    }

    public void toQuiz(QuizRequest quizRequest, Quiz quiz) {
        quiz.setTitle(quizRequest.getTitle());
        quiz.setDescription(quizRequest.getDescription());
        quiz.setDueDate(quizRequest.getDueDate());
        quiz.setTotalPoints(quizRequest.getTotalPoints());
    }

    public QuizQuestionDTO toQuizQuestionDTO(QuizQuestion question) {
        QuizQuestionDTO questionDTO = new QuizQuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setQuestion(question.getQuestion());
        questionDTO.setOptionA(question.getOptionA());
        questionDTO.setOptionB(question.getOptionB());
        questionDTO.setOptionC(question.getOptionC());
        questionDTO.setOptionD(question.getOptionD());
        questionDTO.setCorrectAnswer(question.getCorrectAnswer());
        questionDTO.setPoints(question.getPoints());
        questionDTO.setQuizId(question.getQuiz() != null ? question.getQuiz().getId() : null);
        return questionDTO;
    }

    public QuizQuestion toQuizQuestion(QuizQuestionDTO questionDTO) {
        QuizQuestion question = new QuizQuestion();
        question.setId(questionDTO.getId());
        question.setQuestion(questionDTO.getQuestion());
        question.setOptionA(questionDTO.getOptionA());
        question.setOptionB(questionDTO.getOptionB());
        question.setOptionC(questionDTO.getOptionC());
        question.setOptionD(questionDTO.getOptionD());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        question.setPoints(questionDTO.getPoints());
        return question;
    }

    public QuizQuestion toQuizQuestion(QuizQuestionRequest request) {
        QuizQuestion question = new QuizQuestion();
        question.setQuestion(request.getQuestion());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setPoints(request.getPoints());
        return question;
    }

    public void toQuizQuestion(QuizQuestionRequest request, QuizQuestion question) {
        question.setQuestion(request.getQuestion());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setPoints(request.getPoints());
    }

    public QuizAnswerResponseDTO toQuizAnswerResponseDTO(QuizAnswer answer) {
        QuizAnswerResponseDTO responseDTO = new QuizAnswerResponseDTO();
        responseDTO.setId(answer.getId());
        responseDTO.setAnswer(answer.getAnswer());
        responseDTO.setIsCorrect(answer.getIsCorrect());
        responseDTO.setPointsEarned(answer.getPointsEarned());
        responseDTO.setQuestionId(answer.getQuestion() != null ? answer.getQuestion().getId() : null);
        responseDTO.setStudentId(answer.getStudent() != null ? answer.getStudent().getId() : null);
        return responseDTO;
    }

    public QuizAnswerDTO toQuizAnswerDTO(QuizAnswer answer) {
        QuizAnswerDTO answerDTO = new QuizAnswerDTO();
        answerDTO.setAnswer(answer.getAnswer());
        answerDTO.setIsCorrect(answer.getIsCorrect());
        answerDTO.setPointsEarned(answer.getPointsEarned());
        answerDTO.setQuestionId(answer.getQuestion() != null ? answer.getQuestion().getId() : null);
        answerDTO.setStudentId(answer.getStudent() != null ? answer.getStudent().getId() : null);
        return answerDTO;
    }

    public QuizAnswer toQuizAnswer(QuizAnswerDTO answerDTO) {
        QuizAnswer answer = new QuizAnswer();
        answer.setAnswer(answerDTO.getAnswer());
        answer.setIsCorrect(answerDTO.getIsCorrect());
        answer.setPointsEarned(answerDTO.getPointsEarned());
        return answer;
    }

    public QuizAnswer toQuizAnswer(QuizAnswerRequest answerRequest) {
        QuizAnswer answer = new QuizAnswer();
        answer.setAnswer(answerRequest.getAnswer());
        return answer;
    }

    // Task mappings
    public TaskResponseDTO toTaskResponseDTO(Task task) {
        TaskResponseDTO responseDTO = new TaskResponseDTO();
        responseDTO.setId(task.getId());
        responseDTO.setTitle(task.getTitle());
        responseDTO.setDescription(task.getDescription());
        responseDTO.setDueDate(task.getDueDate());
        responseDTO.setTotalPoints(task.getTotalPoints());
        responseDTO.setLaboratoryId(task.getLaboratory() != null ? task.getLaboratory().getId() : null);
        return responseDTO;
    }

    public TaskDTO toTaskDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setTotalPoints(task.getTotalPoints());
        taskDTO.setLaboratoryId(task.getLaboratory() != null ? task.getLaboratory().getId() : null);
        taskDTO.setInstructions(task.getInstructions());
        return taskDTO;
    }

    public Task toTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setTotalPoints(taskDTO.getTotalPoints());
        task.setInstructions(taskDTO.getInstructions());
        return task;
    }

    public Task toTask(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());
        task.setInstructions(taskRequest.getInstructions());
        task.setTotalPoints(taskRequest.getTotalPoints());
        return task;
    }

    public void toTask(TaskRequest taskRequest, Task task) {
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());
        task.setTotalPoints(taskRequest.getTotalPoints());
    }

    public TaskSubmissionResponseDTO toTaskSubmissionResponseDTO(TaskSubmission submission) {
        TaskSubmissionResponseDTO responseDTO = new TaskSubmissionResponseDTO();
        responseDTO.setId(submission.getId());
        responseDTO.setContent(submission.getContent());
        responseDTO.setGrade(submission.getGrade());
        responseDTO.setFeedback(submission.getFeedback());
        responseDTO.setSubmissionDate(submission.getSubmissionDate());
        responseDTO.setTaskId(submission.getTask() != null ? submission.getTask().getId() : null);
        responseDTO.setStudentId(submission.getStudent() != null ? submission.getStudent().getUser().getId() : null);
        return responseDTO;
    }

    public TaskSubmissionDTO toTaskSubmissionDTO(TaskSubmission submission) {
        TaskSubmissionDTO submissionDTO = new TaskSubmissionDTO();
        submissionDTO.setId(submission.getId());
        submissionDTO.setContent(submission.getContent());
        submissionDTO.setGrade(submission.getGrade());
        submissionDTO.setFeedback(submission.getFeedback());
        submissionDTO.setSubmissionDate(submission.getSubmissionDate());
        submissionDTO.setTaskId(submission.getTask() != null ? submission.getTask().getId() : null);
        submissionDTO.setStudentId(submission.getStudent() != null ? submission.getStudent().getId() : null);
        return submissionDTO;
    }

    public TaskSubmission toTaskSubmission(TaskSubmissionDTO submissionDTO) {
        TaskSubmission submission = new TaskSubmission();
        submission.setContent(submissionDTO.getContent());
        submission.setGrade(submissionDTO.getGrade());
        submission.setFeedback(submissionDTO.getFeedback());
        submission.setSubmissionDate(submissionDTO.getSubmissionDate());
        return submission;
    }

    public TaskSubmission toTaskSubmission(TaskSubmissionRequest request) {
        TaskSubmission submission = new TaskSubmission();
        submission.setContent(request.getContent());
        submission.setSubmissionDate(LocalDateTime.now());
        return submission;
    }

    // Final Test mappings
    public FinalTestDTO toFinalTestDTO(FinalTest test) {
        return modelMapper.map(test, FinalTestDTO.class);
    }

    public FinalTest toFinalTest(FinalTestDTO testDTO) {
        return modelMapper.map(testDTO, FinalTest.class);
    }

    public TestQuestionResponseDTO toTestQuestionResponseDTO(TestQuestion question) {
        TestQuestionResponseDTO responseDTO = new TestQuestionResponseDTO();
        responseDTO.setId(question.getId());
        responseDTO.setQuestion(question.getQuestion());
        responseDTO.setOptionA(question.getOptionA());
        responseDTO.setOptionB(question.getOptionB());
        responseDTO.setOptionC(question.getOptionC());
        responseDTO.setOptionD(question.getOptionD());
        responseDTO.setCorrectAnswer(question.getCorrectAnswer());
        responseDTO.setPoints(question.getPoints());
        responseDTO.setTestId(question.getFinalTest() != null ? question.getFinalTest().getId() : null);
        return responseDTO;
    }

    public TestQuestionDTO toTestQuestionDTO(TestQuestion question) {
        TestQuestionDTO questionDTO = new TestQuestionDTO();
        questionDTO.setQuestion(question.getQuestion());
        questionDTO.setCorrectAnswer(question.getCorrectAnswer());
        questionDTO.setPoints(question.getPoints());
        questionDTO.setTestId(question.getFinalTest() != null ? question.getFinalTest().getId() : null);
        return questionDTO;
    }

    public TestQuestion toTestQuestion(TestQuestionRequest request) {
        TestQuestion question = new TestQuestion();
        question.setQuestion(request.getQuestion());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setPoints(request.getPoints());
        return question;
    }

    public void toTestQuestion(TestQuestionRequest request, TestQuestion question) {
        question.setQuestion(request.getQuestion());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setPoints(request.getPoints());
    }

    public TestAnswerResponseDTO toTestAnswerResponseDTO(TestAnswer answer) {
        TestAnswerResponseDTO responseDTO = new TestAnswerResponseDTO();
        responseDTO.setId(answer.getId());
        responseDTO.setAnswer(answer.getAnswer());
        responseDTO.setIsCorrect(answer.getIsCorrect());
        responseDTO.setPointsEarned(answer.getPointsEarned());
        responseDTO.setQuestionId(answer.getQuestion() != null ? answer.getQuestion().getId() : null);
        responseDTO.setStudentId(answer.getStudent() != null ? answer.getStudent().getId() : null);
        return responseDTO;
    }

    public TestAnswerDTO toTestAnswerDTO(TestAnswer answer) {
        TestAnswerDTO answerDTO = new TestAnswerDTO();
        answerDTO.setAnswer(answer.getAnswer());
        answerDTO.setIsCorrect(answer.getIsCorrect());
        answerDTO.setPointsEarned(answer.getPointsEarned());
        answerDTO.setQuestionId(answer.getQuestion() != null ? answer.getQuestion().getId() : null);
        answerDTO.setStudentId(answer.getStudent() != null ? answer.getStudent().getId() : null);
        return answerDTO;
    }

    public TestAnswer toTestAnswer(TestAnswerDTO answerDTO) {
        TestAnswer answer = new TestAnswer();
        answer.setAnswer(answerDTO.getAnswer());
        answer.setIsCorrect(answerDTO.getIsCorrect());
        answer.setPointsEarned(answerDTO.getPointsEarned());
        return answer;
    }

    public TestAnswer toTestAnswer(TestAnswerRequest answerRequest) {
        TestAnswer answer = new TestAnswer();
        answer.setAnswer(answerRequest.getAnswer());
        return answer;
    }

    public FinalTest toFinalTest(FinalTestRequest testRequest) {
        FinalTest test = new FinalTest();
        test.setTitle(testRequest.getTitle());
        test.setDescription(testRequest.getDescription());
        test.setDueDate(testRequest.getDueDate());
        test.setTotalPoints(testRequest.getTotalPoints());
        return test;
    }

    public void toFinalTest(FinalTestRequest testRequest, FinalTest test) {
        test.setTitle(testRequest.getTitle());
        test.setDescription(testRequest.getDescription());
        test.setDueDate(testRequest.getDueDate());
        test.setTotalPoints(testRequest.getTotalPoints());
    }
} 