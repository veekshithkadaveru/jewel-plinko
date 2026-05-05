package app.krafted.jewelplinko.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlinkoDao {

    @Query("SELECT * FROM wallet WHERE id = 0")
    suspend fun getWallet(): WalletEntity?

    @Query("SELECT * FROM wallet WHERE id = 0")
    fun observeWallet(): Flow<WalletEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWallet(wallet: WalletEntity)

    @Insert
    suspend fun insertWin(win: WinRecord): Long

    @Query("SELECT * FROM win_records ORDER BY winnings DESC LIMIT 1")
    suspend fun getBestWin(): WinRecord?

    @Query("SELECT * FROM win_records ORDER BY winnings DESC LIMIT :limit")
    suspend fun getTopWins(limit: Int): List<WinRecord>
}
