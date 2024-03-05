package ipca.example.topnews.repository.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//
//  TopNews
//
//  Created by Lourenço Gomes on 27/04/2021.
//  Copyright © 2021 Lourenço Gomes. All rights reserved.
//

@Database(entities = [ArticleCache::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleCacheDao(): ArticleCacheDao

}
