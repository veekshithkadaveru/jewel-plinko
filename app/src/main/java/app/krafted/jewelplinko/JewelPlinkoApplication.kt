package app.krafted.jewelplinko

import android.app.Application
import app.krafted.jewelplinko.data.db.AppDatabase

class JewelPlinkoApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
