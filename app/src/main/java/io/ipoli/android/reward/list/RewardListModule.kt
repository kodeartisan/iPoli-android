package io.ipoli.android.reward.list

import android.content.Context
import dagger.Module
import dagger.Provides
import io.ipoli.android.common.jobservice.JobQueue
import io.ipoli.android.player.persistence.PlayerRepository
import io.ipoli.android.player.persistence.RealmPlayerRepository
import io.ipoli.android.reward.RealmRewardRepository
import io.ipoli.android.reward.RewardRepository
import io.ipoli.android.reward.list.usecase.DisplayRewardListUseCase
import io.ipoli.android.reward.list.usecase.RemoveRewardFromListUseCase

/**
 * Created by Venelin Valkov <venelin@ipoli.io>
 * on 8/2/17.
 */
@Module
class RewardListModule(private val context: Context) {

    @Provides
    @RewardListScope
    fun provideRewardsRepository(): RewardRepository = RealmRewardRepository()

    @Provides
    @RewardListScope
    fun providePlayerRepository(): PlayerRepository = RealmPlayerRepository()

    @Provides
    @RewardListScope
    fun provideRewardListUseCase(rewardRepository: RewardRepository, playerRepository: PlayerRepository): DisplayRewardListUseCase {
        return DisplayRewardListUseCase(rewardRepository, playerRepository)
    }

    @Provides
    @RewardListScope
    fun provideJobQueue(rewardRepository: RewardRepository): JobQueue =
        JobQueue(rewardRepository)

    @Provides
    @RewardListScope
    fun provideRemoveRewardFromListUseCase(jobQueue: JobQueue): RemoveRewardFromListUseCase =
        RemoveRewardFromListUseCase(jobQueue)
}