package com.aircraftwar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aircraftwar.game.GameManager
import com.aircraftwar.utils.GameLogger
import kotlinx.coroutines.launch

@Composable
fun GameScreen() {
    val gameManager = remember { GameManager(screenWidth = 400, screenHeight = 800) }
    var currentFps by remember { mutableStateOf(0) }
    var gameState by remember { mutableStateOf(GameState.RUNNING) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        GameLogger.info("🎮 Démarrage du jeu Android")
        startGameLoop(gameManager) { fps ->
            currentFps = fps
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(20, 20, 40))
    ) {
        // Zone de jeu
        GameCanvas(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            gameManager = gameManager
        )
        
        // UI en surimpression
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Stats en haut
            GameStats(gameManager, currentFps)
            
            // Boutons de contrôle
            GameControls(gameManager) {
                gameState = it
            }
        }
        
        // Écran de pause
        if (gameManager.isPaused) {
            PauseOverlay(gameManager)
        }
        
        // Écran de fin de partie
        if (gameManager.isGameOver) {
            GameOverOverlay(gameManager)
        }
    }
}

@Composable
fun GameStats(gameManager: GameManager, fps: Int) {
    Column(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.6f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem("Score", "${gameManager.score}", Color(0, 255, 100))
            StatItem("Wave", "${gameManager.wave}", Color(0, 200, 150))
            StatItem("FPS", "$fps", Color(100, 200, 100))
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItem("HP", "${gameManager.player.health}/${gameManager.player.maxHealth}", Color(255, 100, 100))
            StatItem("Ammo", "${gameManager.player.ammo}", Color(255, 200, 50))
            StatItem("Level", "${gameManager.player.level}", Color(150, 100, 255))
        }
        
        // Combo
        if (gameManager.currentCombo > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "🔥 COMBO x${gameManager.currentCombo}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(255, 150, 0)
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun GameControls(gameManager: GameManager, onStateChange: (GameState) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.6f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Dpad pour mouvement
        DPad(gameManager)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Bouton Pause
            IconButton(
                onClick = { 
                    gameManager.pause()
                    onStateChange(if (gameManager.isPaused) GameState.PAUSED else GameState.RUNNING)
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    if (gameManager.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color(255, 200, 100),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // Bouton Tir (prend beaucoup de place)
            Button(
                onClick = { gameManager.fireProjectile() },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(255, 100, 50)
                )
            ) {
                Text("🔫 TIR", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            // Bouton Redémarrer
            IconButton(
                onClick = { gameManager.reset() },
                modifier = Modifier.size(50.dp)
            ) {
                Text("🔄", fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun DPad(gameManager: GameManager) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Haut
        DPadButton("⬆") { gameManager.player.moveUp = true }
        
        // Gauche, Bas, Droite
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            DPadButton("⬅") { gameManager.player.moveLeft = true }
            DPadButton("⬇") { gameManager.player.moveDown = true }
            DPadButton("➡") { gameManager.player.moveRight = true }
        }
    }
}

@Composable
fun DPadButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0, 150, 100)
        )
    ) {
        Text(label, fontSize = 20.sp)
    }
}

@Composable
fun PauseOverlay(gameManager: GameManager) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "⏸ PAUSE",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(255, 200, 100)
            )
            
            Text(
                "Score: ${gameManager.score} | Wave: ${gameManager.wave}",
                color = Color.White,
                fontSize = 18.sp
            )
            
            Button(
                onClick = { gameManager.pause() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0, 200, 100)
                )
            ) {
                Text("▶ CONTINUER", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GameOverOverlay(gameManager: GameManager) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                "☠ GAME OVER ☠",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color(255, 50, 50)
            )
            
            Divider(color = Color(255, 100, 100), thickness = 2.dp)
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatLine("Score Final", "${gameManager.score}", Color(255, 200, 100))
                StatLine("Vagues", "${gameManager.wave}", Color(100, 200, 255))
                StatLine("Ennemis", "${gameManager.totalEnemiesDefeated}", Color(255, 150, 100))
                StatLine("Combo Max", "${gameManager.longestCombo}", Color(255, 150, 0))
                StatLine("Niveau", "${gameManager.player.level}", Color(150, 100, 255))
            }
            
            Divider(color = Color(255, 100, 100), thickness = 2.dp)
            
            Button(
                onClick = { gameManager.reset() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0, 200, 100)
                )
            ) {
                Text("🔄 RECOMMENCER", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatLine(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 16.sp)
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GameCanvas(
    modifier: Modifier = Modifier,
    gameManager: GameManager
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    // Gestion du swipe
                    val dx = dragAmount.x
                    val dy = dragAmount.y
                    
                    gameManager.player.moveUp = dy < -5
                    gameManager.player.moveDown = dy > 5
                    gameManager.player.moveLeft = dx < -5
                    gameManager.player.moveRight = dx > 5
                }
            },
        onDraw = {
            // Fond
            drawRect(Color(20, 20, 40))
            
            // Rendu du jeu (simplifié pour Compose)
            // Dans une vraie impl, utiliser Canvas neatif ou GLSurfaceView
        }
    )
}

enum class GameState {
    RUNNING, PAUSED, GAME_OVER
}

suspend fun startGameLoop(gameManager: GameManager, onFpsUpdate: (Int) -> Unit) {
    var frameCount = 0
    var lastFpsUpdate = System.currentTimeMillis()
    
    while (true) {
        gameManager.update()
        gameManager.enemyShoot()
        
        frameCount++
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastFpsUpdate >= 1000) {
            onFpsUpdate(frameCount)
            frameCount = 0
            lastFpsUpdate = currentTime
        }
        
        kotlinx.coroutines.delay(16) // ~60 FPS
    }
}

@Composable
fun Canvas(
    modifier: Modifier = Modifier,
    onDraw: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier,
        onDraw = onDraw
    )
}
