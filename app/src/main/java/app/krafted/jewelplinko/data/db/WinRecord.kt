package app.krafted.jewelplinko.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "win_records")
data class WinRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val multiplier: Int,
    val winnings: Int,
    val symbolDrawableRes: Int,
    val timestampMillis: Long,
    val playerName: String = "Player"
)
