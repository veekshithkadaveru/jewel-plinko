package app.krafted.jewelplinko.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.krafted.jewelplinko.game.PlinkoBoardView

@Composable
fun GameScreen(onSessionComplete: () -> Unit) {
    val boardRef = remember { arrayOfNulls<PlinkoBoardView>(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0220))
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlinkoBoardView(ctx).also { view ->
                    boardRef[0] = view
                }
            }
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                val v = boardRef[0] ?: return@Button
                v.nudgeAim(-v.width * 0.06f)
            }) { Text("◀ AIM") }

            Button(onClick = {
                boardRef[0]?.dropBall()
            }) { Text("DROP") }

            Button(onClick = {
                val v = boardRef[0] ?: return@Button
                v.nudgeAim(v.width * 0.06f)
            }) { Text("AIM ▶") }
        }
    }
}
