package io.ipoli.android

import android.app.Application
import android.content.Context
import com.bluelinelabs.conductor.Router
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.crashlytics.android.Crashlytics
import com.github.moduth.blockcanary.BlockCanary
import com.github.moduth.blockcanary.BlockCanaryContext
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import io.fabric.sdk.android.Fabric
import io.ipoli.android.common.di.*
import io.ipoli.android.quest.data.persistence.CouchbaseQuestRepository
import kotlinx.coroutines.experimental.android.UI
import space.traversal.kapsule.transitive
import timber.log.Timber

/**
 * Created by Venelin Valkov <venelin@ipoli.io>
 * on 7/7/17.
 */

class iPoliApp : Application() {

    companion object {
        lateinit var refWatcher: RefWatcher

        fun module(context: Context, router: Router) = Module(
            androidModule = MainAndroidModule(context, router),
            repositoryModule = CouchbaseRepositoryModule(),
            useCaseModule = MainUseCaseModule(),
            presenterModule = AndroidPresenterModule()
        ).transitive()
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }


        AndroidThreeTen.init(this)

        // Initialize Realm. Should only be done once when the application starts.
//        Realm.init(this)
//        val db = Database()
        Timber.plant(Timber.DebugTree())
//        Logger.addLogAdapter(AndroidLogAdapter())

//        Timber.plant(object : Timber.DebugTree() {
//            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
//                Logger.log(priority, tag, message, t)
//            }
//        })

        if (!BuildConfig.DEBUG) {

            BlockCanary.install(this, object : BlockCanaryContext() {
                override fun provideBlockThreshold(): Int {
                    return 500
                }
            }).start()

            Fabric.with(Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(BuildConfig.DEBUG)
                .build())

            refWatcher = LeakCanary.install(this)
        }

        val repo = CouchbaseQuestRepository(Database("iPoli", DatabaseConfiguration(this)), UI)
//        val q = Quest(
//            name = "Welcome",
//            color = Color.GREEN,
//            category = Category("Wellness", Color.GREEN),
//            plannedSchedule = QuestSchedule(LocalDate.now(), duration = 60, time = Time.at(15, 0)),
//            reminder = Reminder(Random().nextInt().toString(), "Welcome message", Time.at(20, 0), LocalDate.now())
//        )
//
//        repo.save(q)

        val quests = repo.findQuestsToRemind(System.currentTimeMillis())
        quests.forEach {
            Timber.d("AAAA $it")
        }
//        Timber.d("AAAAA $quests")

//        TinyDancer.create().show(this)

//
//        val rewardRepository = RealmRewardRepository()
//        rewardRepository.save(Reward(name = "Reward 1", description = "desc1", price = 100)).subscribe()
//        rewardRepository.save(Reward(name = "Reward 2", description = "desc 2", price = 200)).subscribe()
//        rewardRepository.save(Reward(name = "Reward 3", description = "desc 3", price = 300)).subscribe()
//
//        val questRepository = RealmQuestRepository()
//        val quest = Quest("Today123", LocalDate.now(), Category.PERSONAL)
//        quest.setDuration(30)
//        quest.startMinute = Time.at(14, 0).toMinuteOfDay()
//        quest.completedAtDate = LocalDate.now()
//        quest.completedAtMinute = 380
//        questRepository.save(quest).subscribe()

//        val repeatingQuestRepository = RealmRepeatingQuestRepository()
//
//        val rq = RepeatingQuest("Wakka")
//        rq.name = "Doodle"
//        rq.setDuration(20)
//        rq.recurrence = Recurrence.create()
//        val quest = Quest("Piki")
//        quest.id = UUID.randomUUID().toString()
//        quest.scheduledDate = LocalDate.now().plusDays(1)
////        quest.completedAt = System.currentTimeMillis()
//        rq.quests.add(quest)
//        repeatingQuestRepository.save(rq).subscribe()

//        val challenge = Challenge("Hello")
//        challenge.endDate = LocalDate.now().plusDays(2)
//        challenge.quests.add(Quest("Welcome to China", Category.CHORES))
//
//        val repeatingQuest = RepeatingQuest("Hi")
//        repeatingQuest.quests.add(Quest("Hi", Category.LEARNING))
//        challenge.repeatingQuests.add(repeatingQuest)
//
//        RealmChallengeRepository().save(challenge).subscribe()
    }
}