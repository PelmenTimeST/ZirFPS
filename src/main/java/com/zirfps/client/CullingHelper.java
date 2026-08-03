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

public final class CullingHelper {
    private CullingHelper() {}
    private static final Minecraft MC = Minecraft.getInstance();

    public static boolean isVisible(Entity entity) {
        if (MC.player == null || MC.level == null) return true;

        Vec3 cam = MC.gameRenderer.getMainCamera().getPosition();
        AABB box = entity.getBoundingBox();
        if (box == null || box.minX == 0 && box.maxX == 0) return true;

        double distSq = cam.distanceToSqr(box.getCenter());
        double maxDist = ZirConfig.entityRenderDistance > 0 ? ZirConfig.entityRenderDistance : MC.options.renderDistance().get() * 16.0;
        if (distSq > maxDist * maxDist) return false;
        if (distSq > 96.0 * 96.0) return true;
        if (!ChunkOcclusionManager.isChunkVisible(entity)) return false;

        return multiRayVisible(cam, box);
    }

    private static boolean multiRayVisible(Vec3 cam, AABB box) {
        Vec3 min = new Vec3(box.minX, box.minY, box.minZ);
        Vec3 max = new Vec3(box.maxX, box.maxY, box.maxZ);
        Vec3[] targets = {
            box.getCenter(),
            new Vec3(min.x, min.y, min.z),
            new Vec3(max.x, min.y, min.z),
            new Vec3(min.x, min.y, max.z),
            new Vec3(max.x, min.y, max.z),
        };
        for (Vec3 t : targets) if (rayVisible(cam, t)) return true;
        return false;
    }

    private static boolean rayVisible(Vec3 start, Vec3 end) {
        Level level = MC.level;
        if (level == null) return true;
        BlockHitResult res = level.clip(new ClipContext(
            start, end, ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE, MC.player
        ));
        if (res.getType() == HitResult.Type.MISS) return true;
        if (!level.getBlockState(res.getBlockPos()).isSolidRender(level, res.getBlockPos())) return true;
        double hitDist = start.distanceToSqr(res.getLocation());
        double endDist = start.distanceToSqr(end);
        return hitDist >= endDist - 0.5;
    }
}
