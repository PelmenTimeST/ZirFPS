package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CullingHelper {
    private CullingHelper() {}

    private static final double CLOSE_DISTANCE_SQ = 96.0 * 96.0;
    private static final int REFRESH_INTERVAL_TICKS = 4;
    private static final int STALE_ENTRY_TICKS = 200;

    private static final Map<Integer, CacheEntry> CACHE = new ConcurrentHashMap<>();
    private static int lastPurgeTick = -1;

    private static final class CacheEntry {
        boolean visible;
        int checkedAtTick;
    }

    public static boolean isVisible(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return true;

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        AABB box = entity.getBoundingBox();
        double distSq = cam.distanceToSqr(box.getCenter());

        double maxDist = ZirConfig.entityRenderDistance > 0 ? ZirConfig.entityRenderDistance : mc.options.renderDistance().get() * 16.0;
        if (distSq > maxDist * maxDist) return false;
        if (distSq <= CLOSE_DISTANCE_SQ) return true;
        if (!ChunkOcclusionManager.isChunkVisible(entity)) return false;

        int currentTick = mc.player.tickCount;
        CacheEntry cache = CACHE.get(entity.getId());
        if (cache != null && currentTick - cache.checkedAtTick < REFRESH_INTERVAL_TICKS) {
            return cache.visible;
        }

        boolean visible = multiRayVisible(mc, cam, box);
        if (cache == null) {
            cache = new CacheEntry();
            CACHE.put(entity.getId(), cache);
        }
        cache.visible = visible;
        cache.checkedAtTick = currentTick;
        return visible;
    }

    public static void purgeStale(int currentTick) {
        if (currentTick - lastPurgeTick < STALE_ENTRY_TICKS) return;
        lastPurgeTick = currentTick;
        CACHE.entrySet().removeIf(e -> currentTick - e.getValue().checkedAtTick > STALE_ENTRY_TICKS);
    }

    private static boolean multiRayVisible(Minecraft mc, Vec3 cam, AABB box) {
        Vec3 min = new Vec3(box.minX, box.minY, box.minZ);
        Vec3 max = new Vec3(box.maxX, box.maxY, box.maxZ);
        Vec3[] targets = {
            box.getCenter(),
            new Vec3(min.x, min.y, min.z),
            new Vec3(max.x, min.y, min.z),
            new Vec3(min.x, min.y, max.z),
            new Vec3(max.x, min.y, max.z),
        };
        for (Vec3 t : targets) if (rayVisible(mc, cam, t)) return true;
        return false;
    }

    private static boolean rayVisible(Minecraft mc, Vec3 start, Vec3 end) {
        Level level = mc.level;
        if (level == null) return true;
        BlockHitResult res = level.clip(new ClipContext(
            start, end, ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE, mc.player
        ));
        if (res.getType() == HitResult.Type.MISS) return true;
        if (!level.getBlockState(res.getBlockPos()).isSolidRender(level, res.getBlockPos())) return true;
        double hitDist = start.distanceToSqr(res.getLocation());
        double endDist = start.distanceToSqr(end);
        return hitDist >= endDist - 0.5;
    }
}
