package cn.irina.thepitaddon.utils

import net.minecraft.server.v1_8_R3.DamageSource.arrow
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player


object LocationUtil {
    fun getClosestPlayer(size: Int, myself: Entity): Player? {
        val nearbyEntities = myself.getNearbyEntities(size.toDouble(), size.toDouble(), size.toDouble())
        var target: Player? = null
        var distance = Double.MAX_VALUE

        for (entity in nearbyEntities) {
            val closestDistance = entity.location.distance(myself.location)
            if (closestDistance < distance) {
                distance = closestDistance
                if (entity is Player) target = entity
            }
        }

        return target
    }

    fun isInWater(myself: Player): Boolean {
        val block: Block = myself.location.block
        val type: Material = block.type
        return type == Material.WATER || type == Material.STATIONARY_WATER
    }

    fun getNearbyPlayer(myself: Entity, radius: Int): Player? {
        val myselfLoc = myself.location.clone()
        var nearestDistanceSquared = Double.MAX_VALUE
        val radiusSquared = (radius * radius).toDouble()
        var nearestPlayer: Player? = null

        for (entity in myself.getNearbyEntities(radius.toDouble(), radius.toDouble(), radius.toDouble())) {
            if (entity !is Player) continue
            if (entity === myself) continue

            val dx = myselfLoc.x - entity.location.x
            val dy = myselfLoc.y - entity.location.y
            val dz = myselfLoc.z - entity.location.z
            val distanceSquared = dx * dx + dy * dy + dz * dz

            if (distanceSquared < nearestDistanceSquared && distanceSquared <= radiusSquared) {
                nearestDistanceSquared = distanceSquared
                nearestPlayer = entity
            }
        }

        return nearestPlayer
    }
}
