package com.wildlifesurvivaltest.domain.local.questions

import com.wildlifesurvivaltest.data.room.entity.QuestionsEntity

interface QuestionsRepository {

    suspend fun insertQuestions(questions: List<QuestionsEntity>)

    fun getQuestionById(id: Int): QuestionsEntity

    suspend fun getEntitiesCount(): Int

}