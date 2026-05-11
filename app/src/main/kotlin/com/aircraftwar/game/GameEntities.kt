package com.aircraftwar.game

import com.aircraftwar.world.WeatherSystem
import kotlin.math.sqrt
import kotlin.random.Random

abstract class GameEntity(
    open var x: Double,
    open var y: Double,
    open val width: Double,
    open val height: Double
) {
    abstract fun update(weatherSystem: WeatherSystem? = null)
    
    open fun getBounds() = Rect(x, y, width, height)
    
    fun collidesWith(other: GameEntity): Boolean {
        val r1 = getBounds()
        val r2 = other.getBounds()
        return !(r1.right < r2.left || r2.right < r1.left || 
                 r1.bottom < r2.top || r2.bottom < r1.top)
    }
}

data class Rect(val x: Double, val y: Double, val width: Double, val height: Double) {
    val left get() = x
    val right get() = x + width
    val top get() = y
    val bottom get() = y + height
}

class Player(
    x: Double = 175.0,
    y: Double = 700.0,
    private val screenWidth: Int = 400,
    private val screenHeight: Int = 800
) : GameEntity(x, y, 40.0, 40.0) {
    
    override var x = x
    override var y = y
    
    var health = 150
    var maxHealth = 150
    var ammo = 200
    var maxAmmo = 200
    var shield = 0
    var maxShield = 100
    
    var combo = 0
    var experience = 0
    var level = 1
    
    var moveUp = false
    var moveDown = false
    var moveLeft = false
    var moveRight = false
    
    var damageMultiplier = 1.0f
    
    private var velocityX = 0.0
    private var velocityY = 0.0
    val baseSpeed = 8.0  // ✅ PUBLIC - CORRIGÉ
    var currentSpeed = baseSpeed
    
    override fun update(weatherSystem: WeatherSystem?) {
        velocityX = 0.0
        velocityY = 0.0
        
        currentSpeed = baseSpeed * (weatherSystem?.visibility ?: 1.0f)
        
        if (moveLeft && x > 0) velocityX = -currentSpeed
        if (moveRight && x < screenWidth - width) velocityX = currentSpeed
        if (moveUp && y > screenHeight / 2) velocityY = -currentSpeed
        if (moveDown && y < screenHeight - height) velocityY = currentSpeed
        
        weatherSystem?.let { x += it.windForce * 0.5 }
        
        x += velocityX
        y += velocityY
        
        x = x.coerceIn(0.0, (screenWidth - width).toDouble())
        y = y.coerceIn((screenHeight / 2).toDouble(), (screenHeight - height).toDouble())
        
        if (shield < maxShield && shield > 0) {
            shield = (shield + 0.5).toInt().coerceAtMost(maxShield)
        }
    }
    
    fun takeDamage(amount: Int) {
        if (shield > 0) {
            val shieldDamage = (amount * 0.7).toInt()
            shield = (shield - shieldDamage).coerceAtLeast(0)
            health -= (amount - shieldDamage)
        } else {
            health -= amount
        }
    }
    
    fun heal(amount: Int) {
        health = (health + amount).coerceAtMost(maxHealth)
    }
    
    fun gainExperience(amount: Int) {
        experience += amount
        if (experience >= 100 * level) {
            level++
            experience = 0
            maxHealth += 20
            maxAmmo += 50
        }
    }
    
    fun isAlive() = health > 0
}

class Enemy(
    x: Double,
    y: Double,
    val type: EnemyType = EnemyType.BASIC,
    difficulty: Float = 1.0f
) : GameEntity(x, y, 30.0, 30.0) {
    
    override var x = x
    override var y = y
    
    var health = (type.health * difficulty).toInt()
    private var velocityX = (Math.random() - 0.5) * 3
    private val velocityY = 2.0 * difficulty
    private var shootTimer = 0
    private val shootInterval = (type.shootInterval / difficulty).toInt()
    
    override fun update(weatherSystem: WeatherSystem?) {
        weatherSystem?.let { velocityX += it.windForce * 0.3f }
        
        x += velocityX
        y += velocityY
        
        shootTimer++
    }
    
    fun shouldShoot(): Boolean {
        if (shootTimer >= shootInterval) {
            shootTimer = 0
            return true
        }
        return false
    }
    
    fun takeDamage(amount: Int) { 
        health -= amount 
    }
    
    fun isAlive() = health > 0
    fun isOffScreen() = y > 850
}

enum class EnemyType(
    val health: Int, 
    val shootInterval: Int, 
    val score: Int, 
    val symbol: String
) {
    BASIC(25, 120, 10, "🔴"),
    STRONG(60, 90, 30, "🔶"),
    FAST(20, 150, 15, "🟠"),
    BOSS(200, 60, 150, "👹")
}

class Projectile(
    x: Double,
    y: Double,
    val isPlayerBullet: Boolean = true,
    var damageMultiplier: Float = 1.0f
) : GameEntity(x, y, 5.0, 12.0) {
    
    override var x = x
    override var y = y
    
    private val baseVelocityY = if (isPlayerBullet) -12.0 else 8.0
    private var velocityY = baseVelocityY
    
    val baseDamage = if (isPlayerBullet) 20 else 12
    val damage get() = (baseDamage * damageMultiplier).toInt()
    
    override fun update(weatherSystem: WeatherSystem?) {
        velocityY += 0.2
        y += velocityY
    }
    
    fun isOffScreen() = y < -20 || y > 850
}

class PowerUp(
    x: Double, 
    y: Double, 
    val type: PowerUpType
) : GameEntity(x, y, 25.0, 25.0) {
    
    override var x = x
    override var y = y
    private val velocityY = 1.5
    
    override fun update(weatherSystem: WeatherSystem?) {
        y += velocityY
    }
    
    fun isOffScreen() = y > 850
}

enum class PowerUpType(
    val symbol: String, 
    val effect: (Player) -> Unit
) {
    HEALTH("❤", { it.heal(40) }),
    AMMO("🔫", { it.ammo = (it.ammo + 60).coerceAtMost(it.maxAmmo) }),
    SHIELD("🛡", { it.shield = it.maxShield }),
    DAMAGE("⚡", { it.damageMultiplier = 1.5f }),
    SPEED("💨", { it.currentSpeed = it.baseSpeed * 1.3f })
}
