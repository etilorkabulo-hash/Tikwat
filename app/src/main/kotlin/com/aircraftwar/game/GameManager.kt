package com.aircraftwar.game

import com.aircraftwar.utils.GameLogger
import com.aircraftwar.world.MapSystem
import com.aircraftwar.world.WeatherSystem
import kotlin.random.Random

class GameManager(val screenWidth: Int = 400, val screenHeight: Int = 800) {
    
    val mapSystem = MapSystem(screenWidth, screenHeight)
    val weatherSystem = WeatherSystem()
    
    val player = Player(screenWidth = screenWidth, screenHeight = screenHeight)
    private val enemies = mutableListOf<Enemy>()
    private val projectiles = mutableListOf<Projectile>()
    private val powerUps = mutableListOf<PowerUp>()
    
    var score = 0
    var wave = 1
    var difficulty = 1.0f
    
    private var waveTimer = 0
    private val waveInterval = 350
    private var enemySpawnCount = 0
    private var enemiesPerWave = 5
    
    var isPaused = false
    var isGameOver = false
    
    private val random = Random(System.currentTimeMillis())
    
    var totalEnemiesDefeated = 0
    var totalProjectilesFired = 0
    var longestCombo = 0
    var currentCombo = 0
    private var comboTimer = 0
    
    fun update() {
        if (isPaused || isGameOver) return
        
        weatherSystem.update()
        mapSystem.update(weatherSystem)
        
        player.update(weatherSystem)
        player.damageMultiplier = when (weatherSystem.currentWeather.ordinal) {
            4 -> 1.2f
            1 -> 0.9f
            else -> 1.0f
        }
        
        enemies.forEach { it.update(weatherSystem) }
        enemies.removeAll { !it.isAlive() || it.isOffScreen() }
        
        projectiles.forEach { it.update(weatherSystem) }
        projectiles.removeAll { it.isOffScreen() }
        
        powerUps.forEach { it.update(weatherSystem) }
        powerUps.removeAll { it.isOffScreen() }
        
        updateWaves()
        checkCollisions()
        
        comboTimer--
        if (comboTimer <= 0) currentCombo = 0
        
        if (!player.isAlive()) isGameOver = true
    }
    
    private fun updateWaves() {
        waveTimer++
        
        if (waveTimer >= waveInterval && enemySpawnCount < enemiesPerWave) {
            spawnEnemy()
            enemySpawnCount++
            waveTimer = 0
        }
        
        if (enemies.isEmpty() && enemySpawnCount >= enemiesPerWave) {
            nextWave()
        }
    }
    
    private fun nextWave() {
        wave++
        difficulty = 1.0f + (wave - 1) * 0.15f
        enemiesPerWave = (5 + wave * 2).coerceAtMost(25)
        enemySpawnCount = 0
        
        if (wave % 5 == 0) spawnBoss()
    }
    
    private fun spawnEnemy() {
        val x = random.nextDouble(30.0, (screenWidth - 30).toDouble())
        val type = when {
            wave < 3 -> EnemyType.BASIC
            wave < 7 -> if (random.nextDouble() < 0.6) EnemyType.BASIC else EnemyType.STRONG
            else -> listOf(EnemyType.BASIC, EnemyType.STRONG, EnemyType.FAST).random()
        }
        enemies.add(Enemy(x, -40.0, type, difficulty))
    }
    
    private fun spawnBoss() {
        enemies.add(Enemy(screenWidth / 2.0 - 20.0, -50.0, EnemyType.BOSS, difficulty * 1.5f))
    }
    
    private fun checkCollisions() {
        enemies.forEach { enemy ->
            if (player.collidesWith(enemy)) {
                player.takeDamage(15)
            }
        }
        
        projectiles.filter { it.isPlayerBullet }.forEach { projectile ->
            enemies.forEach { enemy ->
                if (projectile.collidesWith(enemy)) {
                    enemy.takeDamage(projectile.damage)
                    currentCombo++
                    comboTimer = 120
                    
                    if (currentCombo > longestCombo) longestCombo = currentCombo
                    
                    if (!enemy.isAlive()) {
                        score += (enemy.type.score * (1 + currentCombo * 0.1) * difficulty).toInt()
                        totalEnemiesDefeated++
                        player.gainExperience(enemy.type.score)
                        
                        if (random.nextDouble() < 0.25) {
                            powerUps.add(PowerUp(enemy.x, enemy.y, PowerUpType.values().random()))
                        }
                    }
                    
                    projectiles.remove(projectile)
                }
            }
        }
        
        powerUps.forEach { powerUp ->
            if (player.collidesWith(powerUp)) {
                powerUp.type.effect(player)
                powerUps.remove(powerUp)
            }
        }
    }
    
    fun fireProjectile() {
        if (player.ammo > 0 && !isPaused) {
            projectiles.add(Projectile(player.x + player.width / 2 - 2.5, player.y - 10, true, player.damageMultiplier))
            player.ammo--
            totalProjectilesFired++
        }
    }
    
    fun fireEnemyProjectile(enemy: Enemy) {
        projectiles.add(Projectile(enemy.x + enemy.width / 2 - 2.5, enemy.y + enemy.height, false, 1.0f))
    }
    
    fun enemyShoot() {
        enemies.filter { it.shouldShoot() }.forEach { fireEnemyProjectile(it) }
    }
    
    fun reset() {
        enemies.clear()
        projectiles.clear()
        powerUps.clear()
        
        score = 0
        wave = 1
        difficulty = 1.0f
        
        player.health = player.maxHealth
        player.ammo = player.maxAmmo
        player.level = 1
        player.experience = 0
        
        currentCombo = 0
        comboTimer = 0
        
        isPaused = false
        isGameOver = false
    }
    
    fun pause() {
        isPaused = !isPaused
    }
}
