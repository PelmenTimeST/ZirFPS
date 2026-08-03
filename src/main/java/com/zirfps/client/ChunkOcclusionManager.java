package com.zirfps.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;

import java.util.HashMap;
import java.util.Map;

public final class ChunkOcclusionManager {
    private ChunkOcclusionManager() {}

    private static final Minecraft MC = Minecraft.getInstance();
    private static final Map<Long, Boolean> VISIBILITY = new HashMap<>();
    private static int lastTick = -1;

    public static boolean isChunkVisible(Entity entity) {
        Boolean v = VISIBILITY.get(ChunkPos.asLong(entity.chunkCoordX, entity.chunkCoordZ));
        return v == null || v;
    }

    public static void update() {
        if (MC.player == null || MC.world == null) return;
        int tick = MC.player.ticksExisted;
        if (tick - lastTick < 20) return;
        lastTick = tick;
        VISIBILITY.clear();

        Vec3d cam = MC.gameRenderer.getActiveRenderInfo().getProjectedView();
        BlockPos pos = MC.player.getPosition();
        int pcx = pos.getX() >> 4;
        int pcz = pos.getZ() >> 4;
        int radius = Math.min(MC.gameSettings.renderDistanceChunks, 6);

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

    private static boolean isOccluded(Vec3d cam, int cx, int cz) {
        Vec3d center = new Vec3d((cx << 4) + 8, cam.y, (cz << 4) + 8);
        double dist = cam.distanceTo(center);
        if (dist < 16.0) return false;

        IBlockReader world = MC.world;
        BlockRayTraceResult res = world.rayTraceBlocks(new RayTraceContext(
            cam, center,
            RayTraceContext.BlockMode.COLLIDER,
            RayTraceContext.FluidMode.NONE,
            MC.player
        ));
        if (res.getType() == RayTraceResult.Type.MISS) return false;
        if (!world.getBlockState(res.getPos()).isOpaqueCube(world, res.getPos())) return false;

        return cam.distanceTo(res.getHitVec()) < dist - 4.0;
    }
}
