package app.krafted.jewelplinko.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = 0,
    val coins: Int,
    val lastDailyBonusClaimMillis: Long?,
    val playerName: String = "Player"
)
