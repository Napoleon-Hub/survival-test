package com.wildlifesurvivaltest.domain.local.questions

import com.wildlifesurvivaltest.data.room.dao.QuestionsDao
import com.wildlifesurvivaltest.data.room.entity.QuestionsEntity
import javax.inject.Inject

class QuestionsRepositoryImpl @Inject constructor(private val dao: QuestionsDao) :
    QuestionsRepository {

    override suspend fun insertQuestions(questions: List<QuestionsEntity>) = dao.insertQuestions(questions)

    override fun getQuestionById(id: Int): QuestionsEntity = dao.getQuestionById(id)

    override suspend fun getEntitiesCount(): Int = dao.getEntitiesCount()

}