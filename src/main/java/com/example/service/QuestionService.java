package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.dao.QuestionDao;
import com.example.model.Question;
import com.example.model.QuestionWrapper;
import com.example.model.Response;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuestionService {
    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionDao.findByCategory(category),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<String> addQuestion(Question question) {
        questionDao.save(question);
        return new ResponseEntity<>("success",HttpStatus.CREATED);
    }

	public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numberOfQuestions) {
		List<Integer> questions = questionDao.findShuffledQuestionsByCategory(categoryName, numberOfQuestions);
		return ResponseEntity.ok(questions);
	}

	public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
		List<QuestionWrapper> wrappers = new ArrayList<>();
		List<Question> questions = new ArrayList<>();
		
		for(Integer id: questionIds) {
			questions.add(questionDao.findById(id).get());
		}
		
		for(Question question: questions) {
			QuestionWrapper wrapper = new QuestionWrapper();
			wrapper.setId(question.getId());
			wrapper.setQuestionTitle(question.getQuestionTitle());
			wrapper.setOption1(question.getOption1());
			wrapper.setOption2(question.getOption2());
			wrapper.setOption3(question.getOption3());
			wrapper.setOption4(question.getOption4());
			
			wrappers.add(wrapper);
		}
		return ResponseEntity.ok(wrappers);
	}

	public ResponseEntity<Integer> getScore(List<Response> responses) {
		int score = 0;
		for(Response response: responses) {
			Optional<Question> optionalQuestion = questionDao.findById(response.getId());
			
			if(optionalQuestion.isPresent()) {
			Question questions = questionDao.findById(response.getId()).get();
			if(response.getResponse().equals(questions.getRightAnswer())) 
				score++;
			} else {
				log.info("Question not found for ID: {}", response.getId());
			}
		}
		return ResponseEntity.ok(score);
	}
}
