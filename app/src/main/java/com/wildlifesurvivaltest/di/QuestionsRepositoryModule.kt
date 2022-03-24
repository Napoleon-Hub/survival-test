package com.wildlifesurvivaltest.di

import com.wildlifesurvivaltest.data.room.QuestionsRoomDatabase
import com.wildlifesurvivaltest.domain.local.questions.QuestionsRepository
import com.wildlifesurvivaltest.domain.local.questions.QuestionsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class QuestionsRepositoryModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindRepository(impl: QuestionsRepositoryImpl): QuestionsRepository

    companion object {

        @Provides
        fun provideQuestionsDao(db: QuestionsRoomDatabase) = db.entitiesDao()
    }

}