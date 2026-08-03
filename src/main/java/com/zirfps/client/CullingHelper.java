package com.zirfps.client;

import com.zirfps.config.ZirConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;

public final class CullingHelper {
    private CullingHelper() {}
    private static final Minecraft MC = Minecraft.getInstance();

    public static boolean isVisible(Entity entity) {
        if (MC.player == null || MC.world == null) return true;

        Vec3d cam = MC.gameRenderer.getActiveRenderInfo().getProjectedView();
        AxisAlignedBB box = entity.getBoundingBox();
        if (box == null || box.hasNaN()) return true;

        double distSq = cam.squareDistanceTo(box.getCenter());
        double maxDist = ZirConfig.entityRenderDistance > 0 ? ZirConfig.entityRenderDistance : MC.gameSettings.renderDistanceChunks * 16.0;
        if (distSq > maxDist * maxDist) return false;
        if (distSq > 96.0 * 96.0) return true;
        if (!ChunkOcclusionManager.isChunkVisible(entity)) return false;

        return multiRayVisible(cam, box);
    }

    private static boolean multiRayVisible(Vec3d cam, AxisAlignedBB box) {
        Vec3d min = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d max = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d[] targets = {
            box.getCenter(),
            new Vec3d(min.x, min.y, min.z),
            new Vec3d(max.x, min.y, min.z),
            new Vec3d(min.x, min.y, max.z),
            new Vec3d(max.x, min.y, max.z),
        };
        for (Vec3d t : targets) if (rayVisible(cam, t)) return true;
        return false;
    }

    private static boolean rayVisible(Vec3d start, Vec3d end) {
        IBlockReader world = MC.world;
        if (world == null) return true;
        BlockRayTraceResult res = world.rayTraceBlocks(new RayTraceContext(
            start, end, RayTraceContext.BlockMode.COLLIDER,
            RayTraceContext.FluidMode.NONE, MC.player
        ));
        if (res.getType() == RayTraceResult.Type.MISS) return true;
        BlockState state = world.getBlockState(res.getPos());
        if (!state.isOpaqueCube(world, res.getPos())) return true;
        double hitDist = start.squareDistanceTo(res.getHitVec());
        double endDist = start.squareDistanceTo(end);
        return hitDist >= endDist - 0.5;
    }
}
