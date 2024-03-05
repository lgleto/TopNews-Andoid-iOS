package ipca.example.topnews;

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.swagger.client.apis.NewsApi
import ipca.example.topnews.repository.Repository
import ipca.example.topnews.repository.localdb.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi {
        return NewsApi()
    }

    @Provides
    @Singleton
    fun provideRepository(api:NewsApi, db:AppDatabase): Repository {
        return Repository(api,db)
    }

    @Singleton
    @Provides
    fun provideArticlesDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "db_topnews"
    ).fallbackToDestructiveMigration().build()

}
