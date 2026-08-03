package com.zirfps.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public final class ChunkOcclusionManager {
    private ChunkOcclusionManager() {}
    private static final Minecraft MC = Minecraft.getInstance();
    private static final Map<Long, Boolean> VISIBILITY = new HashMap<>();
    private static int lastTick = -1;

    public static boolean isChunkVisible(Entity entity) {
        Boolean v = VISIBILITY.get(ChunkPos.asLong(entity.chunkPosition().x, entity.chunkPosition().z));
        return v == null || v;
    }

    public static void update() {
        if (MC.player == null || MC.level == null) return;
        int tick = MC.player.tickCount;
        if (tick - lastTick < 20) return;
        lastTick = tick;
        VISIBILITY.clear();

        Vec3 cam = MC.gameRenderer.getMainCamera().getPosition();
        BlockPos pos = MC.player.blockPosition();
        int pcx = pos.getX() >> 4;
        int pcz = pos.getZ() >> 4;
        int radius = Math.min(MC.options.renderDistance().get(), 6);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int cx = pcx + dx;
                int cz = pcz + dz;
                if (isOccluded(cam, cx, cz)) {
                    VISIBILITY.put(ChunkPos.asLong(cx, cz), false);
                }
            }
        }
    }

    private static boolean isOccluded(Vec3 cam, int cx, int cz) {
        Vec3 center = new Vec3((cx << 4) + 8, cam.y, (cz << 4) + 8);
        double dist = cam.distanceTo(center);
        if (dist < 16.0) return false;

        Level level = MC.level;
        BlockHitResult res = level.clip(new ClipContext(
            cam, center, ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE, MC.player
        ));
        if (res.getType() == HitResult.Type.MISS) return false;
        if (!level.getBlockState(res.getBlockPos()).isSolidRender(level, res.getBlockPos())) return false;

        return cam.distanceToSqr(res.getLocation()) < dist * dist - 16.0;
    }
}
